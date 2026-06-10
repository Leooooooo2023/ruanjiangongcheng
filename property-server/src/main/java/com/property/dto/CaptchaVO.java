package com.property.dto;

import lombok.Data;

/**
 * 验证码返回对象
 */
@Data
public class CaptchaVO {

    private String key;
    private String imageBase64;

    public CaptchaVO(String key, String imageBase64) {
        this.key = key;
        this.imageBase64 = imageBase64;
    }
}
