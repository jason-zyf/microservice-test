package com.pci.hjmos.microservice.test.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zyting
 * @sinne 2020-03-18
 */
@Setter
@Getter
@Data
@ToString
public class SendMsgEntity {

    private String content;
}
