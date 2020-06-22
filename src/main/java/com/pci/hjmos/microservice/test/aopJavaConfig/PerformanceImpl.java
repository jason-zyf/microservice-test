package com.pci.hjmos.microservice.test.aopJavaConfig;

import org.springframework.stereotype.Component;

/**
 * @author zyting
 * @sinne 2020-03-18
 */
@Component
public class PerformanceImpl implements Performance {

    @Override
    public void perform() {
        System.out.println("演出~~~~");
    }

    @Override
    public void intercept() {
        System.out.println("被拦截了~~~~");
    }
}
