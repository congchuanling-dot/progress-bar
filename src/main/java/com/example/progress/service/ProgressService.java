package com.example.progress.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.progress.mapper.ProgressStateMapper;
import com.example.progress.model.ProgressState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressService {

    private final ProgressStateMapper mapper;

    public ProgressService(ProgressStateMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 注册一个新的令牌，如果已存在则抛异常。
     */
    @Transactional
    public ProgressState registerToken(String token) {
        ProgressState exists = mapper.selectOne(new LambdaQueryWrapper<ProgressState>()
                .eq(ProgressState::getUserToken, token));
        if (exists != null) {
            throw new IllegalArgumentException("令牌已存在");
        }
        ProgressState state = new ProgressState("默认行为", 10.0);
        state.setUserToken(token);
        state.setProgressPercent(0.0);
        state.setCompletedCount(0);
        mapper.insert(state);
        return mapper.selectOne(new LambdaQueryWrapper<ProgressState>()
                .eq(ProgressState::getUserToken, token));
    }

    /**
     * 获取指定令牌的进度状态，如果不存在则按默认配置创建。
     */
    @Transactional
    public ProgressState getCurrentState(String token) {
        ProgressState state = mapper.selectOne(new LambdaQueryWrapper<ProgressState>()
                .eq(ProgressState::getUserToken, token));
        if (state == null) {
            state = new ProgressState("默认行为", 10.0);
            state.setUserToken(token);
            state.setProgressPercent(0.0);
            state.setCompletedCount(0);
            mapper.insert(state);
            state = mapper.selectOne(new LambdaQueryWrapper<ProgressState>()
                    .eq(ProgressState::getUserToken, token));
        }
        return state;
    }

    @Transactional
    public ProgressState updateConfig(String token, String behaviorName, double stepPercent) {
        ProgressState state = getCurrentState(token);
        state.setBehaviorName(behaviorName);
        state.setStepPercent(stepPercent);
        state.setProgressPercent(0.0);
        state.setCompletedCount(0);
        mapper.updateById(state);
        return state;
    }

    @Transactional
    public ProgressState clickOnce(String token) {
        ProgressState state = getCurrentState(token);
        double newProgress = state.getProgressPercent() + state.getStepPercent();
        if (newProgress > 100.0) {
            newProgress = 100.0;
        }
        state.setProgressPercent(newProgress);
        state.setCompletedCount(state.getCompletedCount() + 1);
        mapper.updateById(state);
        return state;
    }
}

