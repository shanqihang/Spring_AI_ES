package com.llm.service.impl;

import com.llm.Dto.SensitiveData;
import com.llm.model.SensitiveDataDocument;
import com.llm.repository.SensitiveDataRepository;
import com.llm.service.EmbeddingModel;
import com.llm.service.SensitiveDataService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensitiveDataServiceImpl implements SensitiveDataService {
    @Resource
    SensitiveDataRepository repository;
    @Resource(name = "customOpenAiEmbeddingModel")
    EmbeddingModel embeddingModel;

    @Override
    public void addSensitiveData(String content, int sensitivityLevel, String category) {
        SensitiveDataDocument document = new SensitiveDataDocument();
        document.setContent(content);
        document.setContentVector(embeddingModel.getEmbedding(content));
        document.setSensitivityLevel(sensitivityLevel);
        document.setCategory(category);
        repository.save(document);
    }

    @Override
    public void batchImportSensitiveData(List<SensitiveData> dataList) {
        List<SensitiveDataDocument> documents = dataList.stream()
                .map(data -> {
                    SensitiveDataDocument doc = new SensitiveDataDocument();
                    doc.setContent(data.getContent());
                    doc.setContentVector(embeddingModel.getEmbedding(data.getContent()));
                    doc.setSensitivityLevel(data.getSensitivityLevel());
                    doc.setCategory(data.getCategory());
                    return doc;
                })
                .collect(Collectors.toList());

        repository.saveAll(documents);
    }

    @Override
    public void importFromTextFile(Path filePath, int sensitivityLevel, String category) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            List<SensitiveData> dataList = lines.stream()
                    .map(line -> {
                        SensitiveData data = new SensitiveData();
                        data.setContent(line);
                        data.setSensitivityLevel(sensitivityLevel);
                        data.setCategory(category);
                        return data;
                    })
                    .collect(Collectors.toList());

            batchImportSensitiveData(dataList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import from file: " + filePath, e);
        }
    }

    @Override
    public void importFromTextFile(MultipartFile textFile) {
        try {
            // 从MultipartField获取文本内容
            String content = new String(textFile.getBytes());

            String[] lines = content.split("\\r?\\n");

            List<SensitiveData> dataList = Arrays.stream(lines)
                    .filter(line -> !line.trim().isEmpty()) // 过滤空行
                    .map(line -> {
                        String[] parts = line.split(","); // 按逗号分隔
                        if (parts.length < 3) {
                            throw new IllegalArgumentException("Invalid format: " + line);
                        }

                        SensitiveData data = new SensitiveData();
                        data.setContent(parts[0].trim());
                        data.setSensitivityLevel(Integer.parseInt(parts[1].trim()));
                        data.setCategory(parts[2].trim());
                        return data;
                    })
                    .collect(Collectors.toList());

            batchImportSensitiveData(dataList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process text file", e);
        }
    }
}
