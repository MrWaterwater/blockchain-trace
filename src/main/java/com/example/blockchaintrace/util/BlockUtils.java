package com.example.blockchaintrace.util;

import com.example.blockchaintrace.BlockchainTraceApplication;
import com.example.blockchaintrace.pojo.Block;
import java.util.ArrayList;

public class BlockUtils {
    public static Boolean isChainValid() throws Exception {
        for(int i = BlockchainTraceApplication.blocks.size()-1;i>0;i--){
            Block currentBlock = BlockchainTraceApplication.blocks.get(i);
            Block previousBlock = BlockchainTraceApplication.blocks.get(i-1);
            if(currentBlock.getPreviousHash().equals(previousBlock.getHash())){
                System.out.println("前区块hash匹配！");
            }else {
                System.out.println("前区块hash不匹配！");
            }
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())){
                System.out.println("当前区块hash不匹配！");
            }else {
                System.out.println("当前区块hash匹配");
            }
        }
        return true;
    }
    public static ArrayList<Block> initBlockChain() throws Exception {
        ArrayList<Block> blocks = new ArrayList<>();
        String temp = RocksDBUtils.getInstance().getLastBlockHash();;
        BlockChainIterator blockChainIterator = null;
        for (blockChainIterator = BlockChainIterator.getBlockChainIterator(temp); blockChainIterator.hashNext();blockChainIterator = BlockChainIterator.getBlockChainIterator(temp)){
            Block currentBlock = blockChainIterator.getCurrentBlock();
            Block previousBlock = RocksDBUtils.getInstance().getBlock(currentBlock.getPreviousHash());
            if(currentBlock.getPreviousHash().equals(previousBlock.getHash()) || (currentBlock.getPreviousHash().equals("0"))){
                currentBlock = previousBlock;
                temp = currentBlock.getHash();
            }else {
                System.out.println("前区块hash不匹配！");
            }
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())){
                System.out.println("当前区块hash不匹配！");
            }
            blocks.add(currentBlock);
        }
        return blocks;
    }
}
