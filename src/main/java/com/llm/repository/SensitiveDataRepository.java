package com.llm.repository;

import com.llm.model.SensitiveDataDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SensitiveDataRepository extends ElasticsearchRepository<SensitiveDataDocument, String> {

   // @Query("{\"knn\": {\"field\": \"contentVector\", \"query_vector\": ?0, \"k\": 10, \"num_candidates\": 100}}")
    @Query("""
{
  "knn": {
    "field": "contentVector",
    "query_vector": ?0,
    "k": 10,
    "num_candidates": 100
  }
}""")
    SearchHits<SensitiveDataDocument> findSimilarByContentVector(float[] vector);
}