package com.example.blockchaintrace.pojo;

import com.example.blockchaintrace.util.FileUtils;
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
    public static KeyPairs getInstance(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        FileUtils fileUtils = new FileUtils("key.txt");
        if(fileUtils.getObject() != null){
            instance = fileUtils.getObject();
            return instance;
        }else{
            instance = new KeyPairs();
            fileUtils.saveObject(instance);
            return instance;
        }
    }
}
