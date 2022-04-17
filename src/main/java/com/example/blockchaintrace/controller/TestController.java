package com.example.blockchaintrace.controller;

import com.example.blockchaintrace.pojo.Distribute;
import com.example.blockchaintrace.pojo.Product;
import com.example.blockchaintrace.pojo.Retail;
import com.example.blockchaintrace.pojo.Trace;
import com.example.blockchaintrace.service.BlockChainService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @Autowired
    private BlockChainService service ;

    @RequestMapping(value = "add-product", method = RequestMethod.POST,consumes="application/json")
    public String addProduct(@RequestBody Product product) throws Exception {
        service.addProduct(product);
        return new GsonBuilder().setPrettyPrinting().create().toJson(product);
    }

    @RequestMapping(value = "add-distribute", method = RequestMethod.POST,consumes="application/json")
    public String addTrance(@RequestBody Distribute distribute) throws Exception {
        service.addDistribute(distribute);
        return new GsonBuilder().setPrettyPrinting().create().toJson(distribute);
    }

    @RequestMapping(value = "add-retail", method = RequestMethod.POST,consumes="application/json")
    public String addTrance(@RequestBody Retail retail) throws Exception {
        service.addRetail(retail);
        return new GsonBuilder().setPrettyPrinting().create().toJson(retail);
    }

    @RequestMapping(value = "add-trance", method = RequestMethod.POST,consumes="application/json")
    public String addTrance(@RequestBody Trace trace) throws Exception {
        service.addTrace(trace);
        return new GsonBuilder().setPrettyPrinting().create().toJson(trace);
    }
    @RequestMapping(value = "find/{serialNumber}", method = RequestMethod.GET)
    public String find(@PathVariable String serialNumber) throws Exception {
        return service.find(serialNumber);
    }
    @RequestMapping(value = "add",method = RequestMethod.GET)
        public void addBlock() {
            try {
                service.addBlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
