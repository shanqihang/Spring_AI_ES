package com.llm.controller;


import com.llm.Dto.CheckedResponse;
import com.llm.Dto.SensitiveData;
import com.llm.service.LargeLanguageModelService;
import com.llm.service.SensitiveDataService;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class DetectionController {

    @Resource
    SensitiveDataService sensitiveDataService;
    @Resource
    LargeLanguageModelService llmService;


    /**
     * 校验文本敏感信息
     *
     * @param text
     * @return
     */
    @PostMapping("/check")
    public ResponseEntity<CheckedResponse> checkContent(@RequestBody String text) {
        CheckedResponse response = llmService.checkedDetect(text);
        return ResponseEntity.ok(response);
    }

    /**
     * 校验大模型相应内容敏感信息
     *
     * @param prompt
     * @return
     */
    @PostMapping("/llm/query")
    public ResponseEntity<CheckedResponse> queryLlm(@RequestBody String prompt) {
        CheckedResponse response = llmService.getCheckedResponse(prompt);
        return ResponseEntity.ok(response);
    }

    /**
     * 敏感信息数据
     *
     * @param data
     * @return
     */
    @PostMapping("/sensitive-data")
    public ResponseEntity<Void> addSensitiveData(@RequestBody SensitiveData data) {
        sensitiveDataService.addSensitiveData(data.getContent(),
                data.getSensitivityLevel(), data.getCategory());
        return ResponseEntity.ok().build();
    }

    /**
     * 从文本文件导入敏感信息
     *
     * @param textFile
     * @return
     */
    @PostMapping("/import_sensitive")
    public ResponseEntity<Void> importFromTextFile(@RequestParam("file") MultipartFile textFile) {
        sensitiveDataService.importFromTextFile(textFile);
        return ResponseEntity.ok().build();
    }
}
