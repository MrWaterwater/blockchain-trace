package com.example.blockchaintrace.pojo;

import com.example.blockchaintrace.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data

public class MerkleTree implements Serializable {
    private static final long serialVersionUID = 7245772201767335618L;
    private Node root;
    private ArrayList<Info> infos;
    public MerkleTree(ArrayList<Information> informationArrayList){
        constructTree(informationArrayList);
    }

    private void constructTree(ArrayList<Information> informationArrayList){
        this.infos = new ArrayList<>();
        if(informationArrayList == null || informationArrayList.size() < 1){
            throw new RuntimeException("ERROR:Fail to construct merkle tree ! leafHashes data invalid ! ");
        }
        for(Information information:informationArrayList){
            Info info = new Info();
            info.serialNumber = information.getSerialNumber();
            info.setHash(information.calculateHash());
            info.setSignature(information.getSignature());
            this.infos.add(info);
        }
        ArrayList<Node> parents = bottomLevel(this.infos);
        while (parents.size() > 1){
            parents = internalLevel(parents);
        }
        root = parents.get(0);
    }

    private ArrayList<Node> bottomLevel(ArrayList<Info> infos){
        ArrayList<String> hashes = new ArrayList<>();
        for (Info info:infos){
            hashes.add(info.hash);
        }
        ArrayList<Node> parents = new ArrayList<>();
        for(int i = 0; i < hashes.size() - 1; i += 2){
            Node leaf1 = constructLeafNode(hashes.get(i));
            leaf1.orLeafNode = 1;
            leaf1.info = this.infos.get(i);
            Node leaf2 = constructLeafNode(hashes.get(i + 1));
            leaf2.orLeafNode = 1;
            leaf2.info = this.infos.get(i + 1);
            Node parent = constructInternalNode(leaf1, leaf2);
            parents.add(parent);
        }
        if(hashes.size() % 2 != 0){
            Node leaf = constructLeafNode(hashes.get(hashes.size() - 1));
            leaf.orLeafNode = 1;
            leaf.info = this.infos.get(hashes.size() - 1);
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
            for(Info info:this.infos){
                if(info.hash.equals(node.hash)){
                    filters.add(info.serialNumber);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(info.serialNumber);
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

    private static Node constructLeafNode(String hash){
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
    public static class Node implements Serializable{
        private static final long serialVersionUID = -5651944089147718825L;
        private String hash;
       private Node left;
       private Node right;
       private int orLeafNode;
       private BloomFilter bloomFilter;
       private Info info;
       private void addFilter(ArrayList<String> serialNumberArrayList){
           bloomFilter = new BloomFilter(100);
            for(String serialNumber:serialNumberArrayList){
                bloomFilter.add(serialNumber);
            }
       }
    }
    @Data
    public static class Info implements Serializable{
        private static final long serialVersionUID = -4123695294633446258L;
        private String hash;
        private String serialNumber;
        private byte[] signature;
    }
}
