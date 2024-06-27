package com.vishal;

import com.vishal.myscale.MyScaleDataLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VectorX {

    public static void main(String[] args) {
        MyScaleDataLoader dataLoader = new MyScaleDataLoader();
        dataLoader.createTable();
        dataLoader.processRecords();
        dataLoader.createIndex();
        SpringApplication.run(VectorX.class, args);
    }

}