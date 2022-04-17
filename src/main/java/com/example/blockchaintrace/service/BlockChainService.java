package com.example.blockchaintrace.service;

import com.example.blockchaintrace.pojo.Distribute;
import com.example.blockchaintrace.pojo.Product;
import com.example.blockchaintrace.pojo.Retail;
import com.example.blockchaintrace.pojo.Trace;

public interface BlockChainService {
    void addProduct(Product product);
    void addDistribute(Distribute distribute);
    void addRetail(Retail retail) throws Exception;
    void addTrace(Trace trace) throws Exception;
    void addBlock() throws Exception;
    String find(String serialNumber) throws Exception;
}
