package com.example.progress.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("progress_state")
public class ProgressState {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用于区分不同用户的令牌
     */
    private String userToken;

    private String behaviorName;
    /**
     * 每次点击增加的百分比 (0-100)
     */
    private double stepPercent;
    /**
     * 当前进度百分比 (0-100)
     */
    private double progressPercent;
    /**
     * 已完成次数
     */
    private long completedCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public ProgressState() {
    }

    public ProgressState(String behaviorName, double stepPercent) {
        this.behaviorName = behaviorName;
        this.stepPercent = stepPercent;
        this.progressPercent = 0.0;
        this.completedCount = 0;
    }

    public String getBehaviorName() {
        return behaviorName;
    }

    public void setBehaviorName(String behaviorName) {
        this.behaviorName = behaviorName;
    }

    public double getStepPercent() {
        return stepPercent;
    }

    public void setStepPercent(double stepPercent) {
        this.stepPercent = stepPercent;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }
}

