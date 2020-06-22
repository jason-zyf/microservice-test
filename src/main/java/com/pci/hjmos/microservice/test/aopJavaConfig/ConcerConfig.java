package com.pci.hjmos.microservice.test.aopJavaConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 如果在切面类中加了 @Component 注解，则不用此类也可以
 * @author zyting
 * @sinne 2020-03-18
 */
@Configuration
@EnableAspectJAutoProxy
public class ConcerConfig {

   /* @Bean
    public Audience audience(){
        return new Audience();
    }*/
}
