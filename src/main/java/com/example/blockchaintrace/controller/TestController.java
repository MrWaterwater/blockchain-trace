package com.example.blockchaintrace.controller;

import com.example.blockchaintrace.pojo.TraceVO;
import com.example.blockchaintrace.service.BlockChainService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @Autowired
    private BlockChainService service ;

    @RequestMapping(value = "add-trance", method = RequestMethod.POST,consumes="application/json")
    public String addTrance(@RequestBody TraceVO traceVO) throws Exception {
        service.addTrace(traceVO);
        return new GsonBuilder().setPrettyPrinting().create().toJson(traceVO);
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
