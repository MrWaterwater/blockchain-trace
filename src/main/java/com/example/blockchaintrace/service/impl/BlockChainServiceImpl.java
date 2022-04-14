package com.example.blockchaintrace.service.impl;

import com.example.blockchaintrace.BlockchainTraceApplication;
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

    private ArrayList<TraceVO> traceArrayList = new ArrayList<>();
    private ArrayList<Information> informationArrayList = new ArrayList<>();

    @Override
    public void addTrace(TraceVO traceVO) throws Exception {
//        Trace trace = new Trace();
//        BeanUtils.copyProperties(traceVO,trace);
        Information information = new Information(traceVO.getSerialNumber());
//        trace.setHash(information.getHash());
//        trace.setRecordTimeStamp(Long.toString(new Date().getTime()));
//        this.traceArrayList.add(trace);
        traceVO.setRecordTimeStamp(Long.toString(new Date().getTime()));
        String date = new GsonBuilder().setPrettyPrinting().create().toJson(traceVO);
        information.setData(date);
        information.setSignature(StringUtils.applyECDSASig(BlockchainTraceApplication.keyPairs.getPrivateKey(), date));
        informationArrayList.add(information);
        if(informationArrayList.size() >= 4)
            addBlock();
    }

    @Override
    public void addBlock() throws Exception {
        if(BlockUtils.isChainValid()){
            ArrayList<Information> arrayList = new ArrayList<>();
            for(Information information:this.informationArrayList){
                arrayList.add(information);
            }
            Block block = new Block(RocksDBUtils.getInstance().getLastBlockHash(), arrayList);
            RocksDBUtils.getInstance().putBlock(block);
            RocksDBUtils.getInstance().putLastBlockHash(block.getHash());
            BlockchainTraceApplication.blocks.add(block);
            this.informationArrayList.clear();
//            for(Trace trace:this.traceArrayList){
//                mapper.addTrace(trace);
//            }
            this.traceArrayList.clear();
            System.out.println("添加区块成功");
        }else {
            System.out.println("存在非法区块！添加失败！");
        }
    }

    @Override
    public String find(String serialNumber) throws Exception {
        Information info = findHash(serialNumber);
        if(info == null){
            return "获取失败！";
        }
//        Trace trace = mapper.getTrace(info.getHash());
//        TraceVO traceVO = new TraceVO();
//        BeanUtils.copyProperties(trace,traceVO);
//        traceVO.setSerialNumber(serialNumber);
//        String Json = new GsonBuilder().setPrettyPrinting().create().toJson(traceVO);
        String Json = info.getData();
        if (StringUtils.verifyECDSASig(BlockchainTraceApplication.keyPairs.getPublicKey(), Json, info.getSignature())){
            return Json;
        }
        return "数据验证不通过";
    }

    private Information findHash(String serialNumber) throws Exception {
        String lastHash = RocksDBUtils.getInstance().getLastBlockHash();
        for(int i = BlockchainTraceApplication.blocks.size()-1; i>=0 ; i--) {
            Block temp = BlockchainTraceApplication.blocks.get(i);
            if(temp.getFilter().contains(serialNumber)){
                Block.MerkleTree tree = temp.getMerkleTree();
                Block.MerkleTree.Node root = tree.getRoot();
                while (root.getOrLeafNode()!=1){
                    if(root.getLeft().getBloomFilter().contains(serialNumber)){
                        root = root.getLeft();
                        continue;
                    }
                    if(root.getRight().getBloomFilter().contains(serialNumber)){
                        root = root.getRight();
                    }
                }
                return root.getInformation();
            }
        }
//        BlockChainIterator blockChainIterator = null;
//        String previousHash = null;
//        for (blockChainIterator = BlockChainIterator.getBlockChainIterator(lastHash); blockChainIterator.hashNext();blockChainIterator=BlockChainIterator.getBlockChainIterator(previousHash)){
//            Block block = blockChainIterator.getCurrentBlock();
//            previousHash = block.getPreviousHash();
//            if(block.getFilter().contains(serialNumber)){
//                Block.MerkleTree tree = block.getMerkleTree();
//                Block.MerkleTree.Node root = tree.getRoot();
//                while (root.getOrLeafNode() != 1){
//                    if(root.getLeft().getBloomFilter().contains(serialNumber)){
//                        root = root.getLeft();
//                        continue;
//                    }
//                    if (root.getRight().getBloomFilter().contains(serialNumber)){
//                        root = root.getRight();
//                    }
//                }
//                return root.getInformation();
//            }
//        }
        return null;
    }
}
