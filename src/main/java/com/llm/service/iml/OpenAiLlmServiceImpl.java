package com.llm.service.iml;

import com.llm.Dto.CheckedResponse;
import com.llm.Dto.DetectionResult;
import com.llm.service.ConfidentialityDetector;
import com.llm.service.LargeLanguageModelService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiLlmServiceImpl implements LargeLanguageModelService {
    private  RestTemplate restTemplate;
    @Resource
    private  ConfidentialityDetector detector;

    @Value("${spring.ai.openai.api-key}")
    private  String apiKey;
    @Value("${spring.ai.openai.chat.options.model}")
    private  String model;
    @Value("${spring.ai.openai.base-url}")
    private String url;

    public OpenAiLlmServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CheckedResponse getCheckedResponse(String prompt) {
        var messages = new Object[] {
                Map.of("role", "user", "content", prompt)
        };

        var request = Map.of(
                "model", model,
                "messages", messages,
                "temperature", 0.7
        );

        var headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        var entity = new org.springframework.http.HttpEntity<>(request, headers);

        var response = restTemplate.postForObject(url, entity, Map.class);

        @SuppressWarnings("unchecked")
        String content = (String) ((Map<String, Object>) ((Map<String, Object>) ((List<Object>) response.get("choices")).get(0)).get("message")).get("content");

        DetectionResult detectionResult = detector.detect(content);

        CheckedResponse checkedResponse = new CheckedResponse();
        checkedResponse.setContent(content);
        checkedResponse.setDetectionResult(detectionResult);
        checkedResponse.setBlocked(detectionResult.getConfidentialityScore() > 0.8);

        return checkedResponse;
    }
    @Override
    public CheckedResponse checkedDetect(String text) {
        DetectionResult detectionResult = detector.detect(text);

        CheckedResponse checkedResponse = new CheckedResponse();
        checkedResponse.setContent(text);
        checkedResponse.setDetectionResult(detectionResult);
        checkedResponse.setBlocked(detectionResult.getConfidentialityScore() > 0.8);

        return checkedResponse;
    }

}
