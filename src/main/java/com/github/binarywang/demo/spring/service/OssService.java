package com.github.binarywang.demo.spring.service;

import com.github.binarywang.demo.spring.config.OssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by wen on 2017/4/24.
 */
@Service
public class OssService {



    @Autowired
    OssConfig ossConfig;

    @PostConstruct
    public void inti(){

    }

}
