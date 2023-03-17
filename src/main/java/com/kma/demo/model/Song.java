package com.kma.demo.model;

import lombok.Data;

@Data
public class Song {
    private String id;
    private String artist;
    private Boolean featured;
    private int count;
    private String image;
    private Boolean latest;
    private String title;
    private String url;
}
