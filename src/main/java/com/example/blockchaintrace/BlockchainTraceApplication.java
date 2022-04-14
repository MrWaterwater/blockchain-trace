package com.example.blockchaintrace;

import com.example.blockchaintrace.pojo.Block;
import com.example.blockchaintrace.pojo.BlockChain;
import com.example.blockchaintrace.pojo.KeyPairs;
import com.example.blockchaintrace.util.BlockUtils;
import com.example.blockchaintrace.util.RocksDBUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
public class BlockchainTraceApplication {
    public static KeyPairs keyPairs = KeyPairs.getInstance();
    public static ArrayList<Block> blocks = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        SpringApplication.run(BlockchainTraceApplication.class, args);
        BlockChain.getInstance();
        ArrayList<Block> temp = BlockUtils.initBlockChain();
        for(int i = temp.size()-1; i>=0; i--){
            blocks.add(temp.get(i));
        }
    }
}
