package com.pci.hjmos.microservice.test.aopJavaConfig;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定义一个切面，同事定义了切点和通知
 * @author zyting
 * @sinne 2020-03-18
 */
@Aspect
@Component
public class Audience {

    @Autowired
    private Performance performance;

    @Pointcut("execution(* com.pci.hjmos.microservice.test.aopJavaConfig.Performance.perform(..))")
    public void performance(){}

    @Before("performance()")
    public void silencePhone(){
        System.out.println("手机静音！");
    }

    @Before("performance()")
    public void takeSeats(){
        System.out.println("请坐！");
    }

    @AfterReturning("performance()")
    public void applause(){
        System.out.println("表演结束，鼓掌！！！");
    }

    @AfterThrowing("performance()")
    public void demanRefund(){
        System.out.println("表演失败，退票！！！");
    }

    @Around("performance()")
    public Object around(ProceedingJoinPoint pjp) {
        try {
            boolean flag = false;
            if (flag) {//你的校验成功执行方法,失败方法就不用执行了
                return pjp.proceed();
            } else {
                //可以返回你失败的信息也可以直接抛出校验失败的异常
                performance.intercept();
                return "被拦截了";
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
