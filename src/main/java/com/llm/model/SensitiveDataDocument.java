package com.llm.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "sensitive-data-index")
public class SensitiveDataDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] contentVector;

    @Field(type = FieldType.Integer)
    private int sensitivityLevel;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Date)
    private Date timestamp = new Date();
}