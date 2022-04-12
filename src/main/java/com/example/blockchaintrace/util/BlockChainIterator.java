package com.example.blockchaintrace.util;

import com.example.blockchaintrace.pojo.Block;
import org.apache.commons.lang.StringUtils;

public class BlockChainIterator {
        private String currentBlockHash;
        private Block currentBlock;
        private static BlockChainIterator blockChainIterator;

        public static BlockChainIterator getBlockChainIterator(String lastHash) throws Exception {
        blockChainIterator = new BlockChainIterator(lastHash);
        return blockChainIterator;
    }
        public BlockChainIterator(String currentBlockHash) throws Exception {
            this.currentBlockHash = currentBlockHash;
            this.currentBlock = RocksDBUtils.getInstance().getBlock(currentBlockHash);
        }

        public boolean hashNext() throws Exception {
            if(StringUtils.isBlank(currentBlockHash)){
                return false;
            }
            if(this.currentBlock == null){
                return false;
            }
            if(this.currentBlock.getPreviousHash().equals("0")){
                return false;
            }
            return this.currentBlock.getPreviousHash() != null;
        }

        public Block getCurrentBlock() {
            return currentBlock;
        }
}
