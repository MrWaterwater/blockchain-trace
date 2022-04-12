package com.example.blockchaintrace.service.impl;

import com.example.blockchaintrace.dao.TraceMapper;
import com.example.blockchaintrace.pojo.*;
import com.example.blockchaintrace.service.BlockChainService;
import com.example.blockchaintrace.util.BlockChainIterator;
import com.example.blockchaintrace.util.BlockUtils;
import com.example.blockchaintrace.util.RocksDBUtils;
import com.example.blockchaintrace.util.StringUtils;
import com.google.gson.GsonBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;

@Service
public class BlockChainServiceImpl implements BlockChainService {

    @Autowired
    private TraceMapper mapper;
    private ArrayList<Trace> traceArrayList = new ArrayList<>();
    private ArrayList<Information> informationArrayList = new ArrayList<>();

    @Override
    public void addTrace(TraceVO traceVO) throws Exception {
        Trace trace = new Trace();
        BeanUtils.copyProperties(traceVO,trace);
        Information information = new Information(traceVO.getSerialNumber());
        trace.setHash(information.calculateHash());
        trace.setRecordTimeStamp(Long.toString(new Date().getTime()));
        this.traceArrayList.add(trace);
        traceVO.setRecordTimeStamp(trace.getRecordTimeStamp());
        String date = new GsonBuilder().setPrettyPrinting().create().toJson(traceVO);
        information.setSignature(StringUtils.applyECDSASig(KeyPairs.getInstance().getPrivateKey(), date));
        informationArrayList.add(information);

        if(informationArrayList.size() >= 4)
            addBlock();
    }

    @Override
    public void addBlock() throws Exception {
        if(BlockUtils.isChainValid(RocksDBUtils.getInstance().getLastBlockHash())){
            ArrayList<Information> arrayList = new ArrayList<>();
            for(Information information:this.informationArrayList){
                arrayList.add(information);
            }
            Block block = new Block(RocksDBUtils.getInstance().getLastBlockHash(), arrayList);
            BlockChain.getInstance().setLastBlockHash(block.getHash());
            RocksDBUtils.getInstance().putBlock(block);
            RocksDBUtils.getInstance().putLastBlockHash(block.getHash());
            this.informationArrayList.clear();
            for(Trace trace:this.traceArrayList){
                mapper.addTrace(trace);
            }
            this.traceArrayList.clear();
            System.out.println("添加区块成功");
        }else {
            System.out.println("存在非法区块！添加失败！");
        }
    }

    @Override
    public String find(String serialNumber) throws Exception {
        MerkleTree.Info info = findHash(serialNumber);
        Trace trace = mapper.getTrace(info.getHash());
        if(trace == null){
            return "获取失败！";
        }
        TraceVO traceVO = new TraceVO();
        BeanUtils.copyProperties(trace,traceVO);
        traceVO.setSerialNumber(serialNumber);
        String Json = new GsonBuilder().setPrettyPrinting().create().toJson(traceVO);
        if (StringUtils.verifyECDSASig(KeyPairs.getInstance().getPublicKey(), Json, info.getSignature())){
            return Json;
        }
        return "数据验证不通过";
    }

    private MerkleTree.Info findHash(String serialNumber) throws Exception {
        String lastHash = RocksDBUtils.getInstance().getLastBlockHash();
        BlockChainIterator blockChainIterator = null;
        String previousHash = null;
        for (blockChainIterator = BlockChainIterator.getBlockChainIterator(lastHash); blockChainIterator.hashNext();blockChainIterator=BlockChainIterator.getBlockChainIterator(previousHash)){
            Block block = blockChainIterator.getCurrentBlock();
            previousHash = block.getPreviousHash();
            if(block.getFilter().contains(serialNumber)){
                MerkleTree tree = block.getMerkleTree();
                MerkleTree.Node root = tree.getRoot();
                while (root.getOrLeafNode() != 1){
                    if(root.getLeft().getBloomFilter().contains(serialNumber)){
                        root = root.getLeft();
                        continue;
                    }
                    if (root.getRight().getBloomFilter().contains(serialNumber)){
                        root = root.getRight();
                    }
                }
                return root.getInfo();
            }
        }
        return null;
    }
}
