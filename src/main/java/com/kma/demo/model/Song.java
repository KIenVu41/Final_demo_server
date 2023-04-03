package com.kma.demo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Song implements Serializable {
    private String docId;
    private String artist;
    private Boolean featured;
    private int count;
    private String image;
    private Boolean latest;
    private String title;
    private String url;
}
