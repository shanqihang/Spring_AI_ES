package com.llm.service;

import com.llm.Dto.SensitiveData;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface SensitiveDataService {
    /**
     * 添加敏感信息到向量数据库
     */
    void addSensitiveData(String content, int sensitivityLevel, String category);

    /**
     * 批量导入敏感信息
     */
    void batchImportSensitiveData(List<SensitiveData> dataList);

    /**
     * 从文本文件导入敏感信息
     */
    void importFromTextFile(Path filePath, int sensitivityLevel, String category);

    void importFromTextFile(MultipartFile textFile);
}
