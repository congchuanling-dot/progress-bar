package com.example.progress.service;

import com.example.progress.mapper.ProgressStateMapper;
import com.example.progress.model.ProgressState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressService {

    private static final long DEFAULT_ID = 1L;

    private final ProgressStateMapper mapper;

    public ProgressService(ProgressStateMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 获取当前进度状态，如果数据库还没有记录则创建一条默认的。
     */
    @Transactional
    public ProgressState getCurrentState() {
        ProgressState state = mapper.findOne();
        if (state == null) {
            state = new ProgressState("默认行为", 10.0);
            state.setId(DEFAULT_ID);
            state.setProgressPercent(0.0);
            state.setCompletedCount(0);
            mapper.insert(state);
        }
        return state;
    }

    @Transactional
    public ProgressState updateConfig(String behaviorName, double stepPercent) {
        ProgressState state = getCurrentState();
        state.setBehaviorName(behaviorName);
        state.setStepPercent(stepPercent);
        state.setProgressPercent(0.0);
        state.setCompletedCount(0);
        mapper.update(state);
        return state;
    }

    @Transactional
    public ProgressState clickOnce() {
        ProgressState state = getCurrentState();
        double newProgress = state.getProgressPercent() + state.getStepPercent();
        if (newProgress > 100.0) {
            newProgress = 100.0;
        }
        state.setProgressPercent(newProgress);
        state.setCompletedCount(state.getCompletedCount() + 1);
        mapper.update(state);
        return state;
    }
}

