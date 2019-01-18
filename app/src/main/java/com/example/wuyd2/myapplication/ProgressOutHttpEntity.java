package com.example.wuyd2.myapplication;


import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

public class ProgressOutHttpEntity extends HttpEntityWrapper {
    public ProgressOutHttpEntity(HttpEntity wrapped) {
        super(wrapped);
    }
}
