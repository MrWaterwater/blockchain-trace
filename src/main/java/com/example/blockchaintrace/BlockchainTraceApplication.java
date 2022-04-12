package com.example.blockchaintrace;

import com.example.blockchaintrace.pojo.BlockChain;
import com.example.blockchaintrace.pojo.KeyPairs;
import com.example.blockchaintrace.util.RocksDBUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.image.Kernel;

@SpringBootApplication
public class BlockchainTraceApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(BlockchainTraceApplication.class, args);
        BlockChain.getInstance();
        RocksDBUtils.getInstance();
    }
}
