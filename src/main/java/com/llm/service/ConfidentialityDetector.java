package com.llm.service;

import com.llm.Dto.DetectionResult;

public interface ConfidentialityDetector {
    DetectionResult detect(String text);
}
