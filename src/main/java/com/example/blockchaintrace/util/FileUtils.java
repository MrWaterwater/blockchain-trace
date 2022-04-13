package com.example.blockchaintrace.util;

import com.example.blockchaintrace.pojo.KeyPairs;

import java.io.*;

public class FileUtils {
    private String filename;
    public FileUtils(String filename) {
        this.filename = filename;
    }
    public FileUtils(){

    }
    public void saveObject(KeyPairs keyPairs){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(keyPairs);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public KeyPairs getObject(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            KeyPairs keyPairs = (KeyPairs) ois.readObject();
            return keyPairs;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
