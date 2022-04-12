package com.example.blockchaintrace.dao;

import com.example.blockchaintrace.pojo.Trace;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TraceMapper {
    Trace getTrace(String hash);
    void addTrace(Trace trace);
}
