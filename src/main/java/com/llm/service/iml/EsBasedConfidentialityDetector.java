package com.llm.service.iml;

import com.llm.Dto.DetectionResult;
import com.llm.Dto.MatchedSensitiveData;
import com.llm.model.SensitiveDataDocument;
import com.llm.repository.SensitiveDataRepository;
import com.llm.service.ConfidentialityDetector;
import com.llm.service.EmbeddingModel;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsBasedConfidentialityDetector implements ConfidentialityDetector {
    @Resource
    SensitiveDataRepository repository;
    @Resource
    EmbeddingModel embeddingModel;
    @Value("${confidentiality.threshold}")
    private  float threshold;


    @Override
    public DetectionResult detect(String text) {
        float[] vector = embeddingModel.getEmbedding(text);
     //   SearchHits<SensitiveDataDocument> hits = repository.findSimilarByContentVector(vector);
        SearchHits<SensitiveDataDocument> hits = findSimilarByContentVector(vector);


        List<MatchedSensitiveData> matches = hits.getSearchHits().stream()
                .filter(hit -> hit.getScore() >= threshold)
                .map(this::convertToMatchedData)
                .collect(Collectors.toList());

        DetectionResult result = new DetectionResult();
        result.setMatchedData(matches);
        result.setContainsSensitiveInfo(!matches.isEmpty());
        result.setConfidentialityScore(calculateScore(matches));

        return result;
    }


    @Resource
    ElasticsearchOperations elasticsearchOperations;
    SearchHits<SensitiveDataDocument> findSimilarByContentVector(float[] vector) {
        List<Float> vectorList = new ArrayList<>(vector.length);
        for (float f : vector) {
            vectorList.add(f);
        }
        Query query = new NativeQueryBuilder()
                .withKnnSearches(builder -> builder
                        .k(10)
                        .numCandidates(100)
                        .queryVector(vectorList)
                     //   .similarity(threshold)
                        .field("contentVector"))
                .build();
        return elasticsearchOperations.search(query, SensitiveDataDocument.class);
    }

    private MatchedSensitiveData convertToMatchedData(SearchHit<SensitiveDataDocument> hit) {
        SensitiveDataDocument doc = hit.getContent();
        MatchedSensitiveData data = new MatchedSensitiveData();
        data.setContent(doc.getContent());
        data.setSimilarity(hit.getScore());
        data.setSensitivityLevel(doc.getSensitivityLevel());
        data.setCategory(doc.getCategory());
        return data;
    }

    private double calculateScore(List<MatchedSensitiveData> matches) {
        if (matches.isEmpty()) {
            return 0.0;
        }

        double maxSimilarity = matches.stream()
                .mapToDouble(MatchedSensitiveData::getSimilarity)
                .max()
                .orElse(0);

        double levelWeighted = matches.stream()
                .mapToDouble(m -> m.getSimilarity() * m.getSensitivityLevel())
                .average()
                .orElse(0);

        return 0.6 * maxSimilarity + 0.4 * levelWeighted / 5.0;
    }
}
