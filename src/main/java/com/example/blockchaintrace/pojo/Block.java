package com.example.blockchaintrace.pojo;

import com.example.blockchaintrace.BlockchainTraceApplication;
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
        this.merkleTree = new MerkleTree(this.informationArrayList);
        this.merkleRoot = this.merkleTree.getRoot().getHash();
        this.hash  = calculateHash();
        this.filter = this.merkleTree.getRoot().getBloomFilter();
    }

    public static Block newGenesisBlock(){
        String data = "创世区块信息！";
        Information information = new Information("", data);
        information.setSignature(StringUtils.applyECDSASig(BlockchainTraceApplication.keyPairs.getPrivateKey(), data));
        ArrayList<Information> informations = new ArrayList<>();
        informations.add(information);
        return new Block("0",informations);
    }

//    private void addFilter(ArrayList<Information> informationArrayList){
//        for(Information information:informationArrayList){
//            this.filter.add(information.getSerialNumber());
//        }
//    }

    public String calculateHash(){
        return StringUtils.applySha256(
                previousHash+
                        Long.toString(timeStamp)+
                        merkleRoot);
    }
    @Data
    public class MerkleTree implements Serializable {
        private static final long serialVersionUID = 7245772201767335618L;
        private Node root;
        public MerkleTree(ArrayList<Information> informationArrayList){
            constructTree(informationArrayList);
        }

        private void constructTree(ArrayList<Information> informationArrayList){
            if(informationArrayList == null || informationArrayList.size() < 1){
                throw new RuntimeException("ERROR:Fail to construct merkle tree ! leafHashes data invalid ! ");
            }
            ArrayList<Node> parents = bottomLevel(informationArrayList);
            while (parents.size() > 1){
                parents = internalLevel(parents);
            }
            root = parents.get(0);
        }

        private ArrayList<Node> bottomLevel(ArrayList<Information> informationArrayList){
            ArrayList<Node> parents = new ArrayList<>();
            for(int i = 0; i < informationArrayList.size() - 1; i += 2){
                Node leaf1 = constructLeafNode(informationArrayList.get(i).getHash());
                leaf1.orLeafNode = 1;
                leaf1.information = informationArrayList.get(i);
                leaf1.hash = leaf1.information.getHash();
                Node leaf2 = constructLeafNode(informationArrayList.get(i + 1).getHash());
                leaf2.orLeafNode = 1;
                leaf2.information = informationArrayList.get(i + 1);
                leaf2.hash = leaf2.information.getHash();
                Node parent = constructInternalNode(leaf1, leaf2);
                parents.add(parent);
            }
            if(informationArrayList.size() % 2 != 0){
                Node leaf = constructLeafNode(informationArrayList.get(informationArrayList.size() - 1).getHash());
                leaf.orLeafNode = 1;
                leaf.information = informationArrayList.get(informationArrayList.size() - 1);
                Node parent = constructInternalNode(leaf, leaf);
                parents.add(parent);
            }
            return parents;
        }

        private void findFilter(Node node, ArrayList<String> filters){
            if (node.orLeafNode == 0 ){
                if(node.getRight() != null){
                    findFilter(node.left, filters);
                    findFilter(node.right, filters);
                }else {
                    findFilter(node.left, filters);
                }
            }else {
                for(Information info:informationArrayList){
                    if(info.getHash().equals(node.hash)){
                        filters.add(info.getSerialNumber());
                        ArrayList<String> list = new ArrayList<>();
                        list.add(info.getSerialNumber());
                        node.addFilter(list);
                    }
                }
            }
        }

        private ArrayList<Node> internalLevel(ArrayList<Node> children){
            ArrayList<Node> parents = new ArrayList<>();
            for(int i = 0; i < children.size() - 1; i += 2){
                Node child1 = children.get(i);
                Node child2 = children.get(i + 1);
                Node parent = constructInternalNode(child1, child2);
                parents.add(parent);
            }
            if(children.size() % 2 != 0) {
                Node child = children.get(children.size() -1);
                Node parent = constructInternalNode(child, null);
                parents.add(parent);
            }
            return parents;
        }

        private  Node constructLeafNode(String hash){
            Node leaf = new Node();
            leaf.hash = hash;
            return leaf;
        }

        private Node constructInternalNode(Node leftChild, Node rightChild){
            Node parent = new Node();
            if (rightChild == null){
                parent.hash = leftChild.hash;
            }else {
                parent.hash = internalHash(leftChild.getHash(), rightChild.getHash());
            }
            parent.left = leftChild;
            parent.right = rightChild;
            ArrayList<String> filters = new ArrayList<>();
            findFilter(parent, filters);
            parent.addFilter(filters);
            return parent;
        }

        private String internalHash(String leftChildHash, String rightChildHash){
            return StringUtils.applySha256(leftChildHash + rightChildHash);
        }

        @Data
        public class Node implements Serializable{
            private static final long serialVersionUID = -5651944089147718825L;
            private String hash;
            private Node left;
            private Node right;
            private Information information;
            private int orLeafNode;
            private BloomFilter bloomFilter;
//            public Node(String hash){
//                this.hash = hash;
//            }
//            public Node(){}
            private void addFilter(ArrayList<String> serialNumberArrayList){
                bloomFilter = new BloomFilter(1000);
                for(String serialNumber:serialNumberArrayList){
                    bloomFilter.add(serialNumber);
                }
            }
        }
    }
}
