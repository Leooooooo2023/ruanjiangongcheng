package com.property.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件，返回相对路径
     */
    String upload(MultipartFile file);
}
