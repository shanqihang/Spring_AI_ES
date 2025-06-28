package com.llm.Dto;

import lombok.Data;

@Data
public class CheckedResponse {
    private String content;
    private DetectionResult detectionResult;
    private boolean blocked;
}