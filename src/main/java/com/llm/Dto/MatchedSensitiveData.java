package com.llm.Dto;

import lombok.Data;

@Data
public class MatchedSensitiveData {
    private String content;
    private double similarity;
    private int sensitivityLevel;
    private String category;
}
