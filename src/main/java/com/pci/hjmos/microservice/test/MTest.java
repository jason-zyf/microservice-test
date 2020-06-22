package com.pci.hjmos.microservice.test;

import com.pci.hjmos.framework.core.CoreStartApp;
import com.pci.hjmos.framework.core.common.annotation.StartApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author czchen
 * @version 1.0
 * @date 2020/2/27 11:43
 */
@StartApplication
@ComponentScan(value = "org.apache.rocketmq.broker.*.*")
public class MTest {
    public static void main(String[] args) {
        CoreStartApp.run(args);
    }

}
