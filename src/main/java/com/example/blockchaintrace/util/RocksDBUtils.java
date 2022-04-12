package com.example.blockchaintrace.util;

import com.example.blockchaintrace.pojo.Block;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.HashMap;
import java.util.Map;

public class RocksDBUtils {
    private static final String DB_FILE = "blockchain.db";//区块链文件
    private static final String BLOCKS_BUCKET_KEY = "blocks_";//区块前缀
    private static final String LAST_BLOCK_KEY = "1";
    private volatile static RocksDBUtils instance;
    private RocksDB rocksDB;
    private Gson gson;

    public static RocksDBUtils getInstance() {
        if (instance == null) {
            synchronized (RocksDBUtils.class) {
                if (instance == null) {
                    instance = new RocksDBUtils();
                }
            }
        }
        return instance;
    }

    public RocksDB getRocksDB(){
        return rocksDB;
    }

    private Map<String, byte[]> blocksBucket;

    private RocksDBUtils(){
        gson = new GsonBuilder().create();
        openDB();
        initRocksDB();
    }

    /**
     * 打开数据库
     */
    private void openDB(){
        try{
            rocksDB = RocksDB.open(DB_FILE);
        }catch (RocksDBException e){
            throw new RuntimeException("Fail to open db!", e);
        }
    }

    /**
     * 初始化blocks桶
     */
    private void initRocksDB() {
        try {
            byte[] blockBucketKey = SerializeUtils.serialize(BLOCKS_BUCKET_KEY);
            byte[] blockBucketBytes = rocksDB.get(blockBucketKey);
            if(blockBucketBytes != null){
                blocksBucket = (Map) SerializeUtils.deserialize(blockBucketBytes);
            } else {
                blocksBucket = new HashMap<>();
                rocksDB.put(blockBucketKey, SerializeUtils.serialize(blocksBucket));
            }
        } catch (RocksDBException e) {
            throw new RuntimeException("Fail to init block bucket!", e);
        }
    }

    /**
     * 保存最新一个区块的Hash值
     *
     * @param tipBlockHash
     */
    public void putLastBlockHash(String tipBlockHash) throws Exception {
        try{
            blocksBucket.put(LAST_BLOCK_KEY,SerializeUtils.serialize(tipBlockHash));
            rocksDB.put(SerializeUtils.serialize(BLOCKS_BUCKET_KEY),SerializeUtils.serialize(blocksBucket));
        }catch (RocksDBException e){
            throw new RuntimeException("Fail to put last block hash!", e);
        }
    }

    /**
     * 查询最新一个区块的Hash值
     *
     * @return
     */
    public String getLastBlockHash() throws Exception {
        byte[] lastBlockHashBytes = blocksBucket.get(LAST_BLOCK_KEY);
        if (lastBlockHashBytes != null) {
            return (String) SerializeUtils.deserialize(lastBlockHashBytes);
        }
        return "";
    }

    /**
     * 保存区块
     *
     * @param block
     */
    public void putBlock(Block block) throws Exception {
        try{
            String jsonObject = gson.toJson(block);
            blocksBucket.put(block.getHash(),SerializeUtils.serialize(jsonObject));
            rocksDB.put(SerializeUtils.serialize(BLOCKS_BUCKET_KEY),SerializeUtils.serialize(blocksBucket));
        }catch (RocksDBException e){
            throw new RuntimeException("Fail to put block!", e);
        }
    }

    /**
     * 查询区块
     *
     * @param blockHash
     * @return
     */
    public Block getBlock(String blockHash) throws Exception {
        byte[] temp = blocksBucket.get(blockHash);
        String jsonObject = (String) SerializeUtils.deserialize(temp);
        Block block = gson.fromJson(jsonObject,Block.class);
        return block;
    }

    /**
     * 关闭Rocks数据库
     * @return
     */
    public void closeRocksDB(){
        try {
            rocksDB.close();
        } catch (Exception e){
            throw new RuntimeException("Fail to close db!", e);
        }
    }
}
