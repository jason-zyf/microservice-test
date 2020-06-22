package com.pci.hjmos.microservice.test.controller;

import com.pci.hjmos.microservice.test.aopJavaConfig.Performance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zyting
 * @sinne 2020-03-18
 */
@RestController
public class PerformanceTest {

    @Autowired
    private Performance performance;

    @GetMapping("/indexperform")
    public String index(){
        return "indexperform";
    }

    @GetMapping("/testperform")
    public void testperform(){
        performance.perform();
    }

}
