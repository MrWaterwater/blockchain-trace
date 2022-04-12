package com.example.blockchaintrace.pojo;

import com.example.blockchaintrace.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
public class Information implements Serializable {
    private static final long serialVersionUID = -7849828273896060243L;
    private String serialNumber;
    private String data;
    private byte[] signature;
    private long timeStamp;

    public Information(String serialNumber, String data){
        this.serialNumber = serialNumber;
        this.data = data;
        this.timeStamp = new Date().getTime();
    }
    public Information(String serialNumber){
        this.serialNumber = serialNumber;
        this.data = "default information!";
        this.timeStamp = new Date().getTime();
    }
    public String calculateHash(){
        return StringUtils.applySha256(
                serialNumber + Long.toString(timeStamp)
        );
    }
}
