package com.llm.Dto;

import lombok.Data;

import java.util.List;

@Data
public class DetectionResult {
    private double confidentialityScore;
    private boolean containsSensitiveInfo;
    private List<MatchedSensitiveData> matchedData;
}
