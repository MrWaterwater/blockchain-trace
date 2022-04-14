package com.example.blockchaintrace.pojo;

import com.example.blockchaintrace.util.RocksDBUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;


public class BlockChain implements Serializable {
    private static final long serialVersionUID = 5362352997264825491L;
    private String lastBlockHash;
    private static BlockChain instance;

    private BlockChain(String lastBlockHash){
        this.lastBlockHash = lastBlockHash;
    }

    public static BlockChain getInstance() throws Exception {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            Block block = Block.newGenesisBlock();
            lastBlockHash = block.getHash();
            RocksDBUtils.getInstance().putBlock(block);
            RocksDBUtils.getInstance().putLastBlockHash(lastBlockHash);
            instance = new BlockChain(lastBlockHash);
        }
        return instance = new BlockChain(lastBlockHash);
    }


}
