package com.llm.Dto;

import lombok.Data;

@Data
public class SensitiveData {
    private String content;
    private int sensitivityLevel;
    private String category;

   /* public SensitiveData(String content, int sensitivityLevel, String category) {
        this.content = content;
        this.sensitivityLevel = sensitivityLevel;
        this.category = category;
    }*/
}
