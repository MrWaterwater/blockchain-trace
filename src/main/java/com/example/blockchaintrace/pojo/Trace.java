package com.example.blockchaintrace.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trace {
    private String serialNumber;
    private String productName;
    private String productPlace;
    private String productTime;
    private String producer;
    private String distributor;
    private String distributeTime;
    private String retailer;
    private String retailTime;
    private String recordTimeStamp;
}
