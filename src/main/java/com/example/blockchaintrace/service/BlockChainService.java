package com.example.blockchaintrace.service;

import com.example.blockchaintrace.pojo.Information;
import com.example.blockchaintrace.pojo.TraceVO;

import java.util.ArrayList;

public interface BlockChainService {
    void addTrace(TraceVO traceVO) throws Exception;
    void addBlock() throws Exception;
    String find(String serialNumber) throws Exception;
}
