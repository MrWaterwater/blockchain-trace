package com.example.blockchaintrace.util;

import com.example.blockchaintrace.pojo.Block;
import com.google.gson.GsonBuilder;

public class BlockUtils {
    public static Boolean isChainValid(String lastHash) throws Exception {
        String temp = lastHash;
        BlockChainIterator blockChainIterator = null;
        for (blockChainIterator = BlockChainIterator.getBlockChainIterator(temp); blockChainIterator.hashNext();blockChainIterator = BlockChainIterator.getBlockChainIterator(temp)){
            Block currentBlock = blockChainIterator.getCurrentBlock();
            Block previousBlock = RocksDBUtils.getInstance().getBlock(currentBlock.getPreviousHash());
            String Json = new GsonBuilder().setPrettyPrinting().create().toJson(currentBlock);
            System.out.println(Json);
            if(currentBlock.getPreviousHash().equals(previousBlock.getHash()) || (currentBlock.getPreviousHash().equals("0"))){
                currentBlock = previousBlock;
                temp = currentBlock.getHash();
            }else {
                System.out.println("前区块hash不匹配！");
            }
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())){
                System.out.println("当前区块hash不匹配！");
            }
        }
        return true;
    }
}
