package com.example.blockchaintrace.pojo;

import com.example.blockchaintrace.util.RocksDBUtils;
import com.example.blockchaintrace.util.SerializeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.rocksdb.RocksDBException;

import java.io.Serializable;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

@Data
public class KeyPairs implements Serializable {
    private static final long serialVersionUID = -6548326561552618652L;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private volatile static KeyPairs instance;
    private volatile static Gson gson = new GsonBuilder().create();
    private KeyPairs(){
        generateKeyPair();
    }
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
//    public static KeyPairs getInstance(){
//        if(instance == null){
//            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//            instance = new KeyPairs();
//        }
//        System.out.println(instance.toString());
//        return instance;
//    }

    public static KeyPairs getInstance(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        if(instance == null){
            try {
                byte[] keyPairsKey = SerializeUtils.serialize("Keypair");
                byte[] keyPairsBytes = RocksDBUtils.getInstance().getRocksDB().get(keyPairsKey);
                if(keyPairsBytes != null){
                    String Json = (String) SerializeUtils.deserialize(keyPairsBytes);
                    instance = gson.fromJson(Json, KeyPairs.class);
                }else {
                    instance = new KeyPairs();
                    String Json = gson.toJson(instance);
                    RocksDBUtils.getInstance().getRocksDB().put(keyPairsKey, SerializeUtils.serialize(Json));
                }
            }catch (RocksDBException e) {
                throw new RuntimeException("Fail to init KeyPair!", e);
            }
        }
        return instance;
    }
}
