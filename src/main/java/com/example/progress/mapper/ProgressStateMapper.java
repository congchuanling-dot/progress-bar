package com.example.progress.mapper;

import com.example.progress.model.ProgressState;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProgressStateMapper {

    ProgressState findOne();

    int insert(ProgressState state);

    int update(ProgressState state);
}

