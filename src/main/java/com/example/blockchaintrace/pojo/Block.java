package com.example.blockchaintrace.pojo;

import com.example.blockchaintrace.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Data
public class Block implements Serializable {
    private static final long serialVersionUID = -2577054592009947947L;
    private String hash;
    private String previousHash;
    private long timeStamp;
    private String merkleRoot;
    private ArrayList<Information> informationArrayList;
    private BloomFilter filter;
    private MerkleTree merkleTree;

    public Block(String previousHash ,ArrayList<Information> informationArrayList){
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.informationArrayList = informationArrayList;
        this.filter = new BloomFilter(100);
        this.merkleTree = new MerkleTree(this.informationArrayList);
        this.merkleRoot = this.merkleTree.getRoot().getHash();
        this.hash  = calculateHash();
        addFilter(this.informationArrayList);
    }

    public static Block newGenesisBlock(){
        String data = "创世区块信息！";
        Information information = new Information("", data);
        information.setSignature(StringUtils.applyECDSASig(KeyPairs.getInstance().getPrivateKey(), data));
        ArrayList<Information> informations = new ArrayList<>();
        informations.add(information);
        return new Block("0",informations);
    }

    private void addFilter(ArrayList<Information> informationArrayList){
        for(Information information:informationArrayList){
            this.filter.add(information.getSerialNumber());
        }
    }

    public String calculateHash(){
        return StringUtils.applySha256(
                previousHash+
                        Long.toString(timeStamp)+
                        merkleRoot);
    }
}
