package com.llm.service;

public interface EmbeddingModel {
    /**
     * 获取文本的向量表示
     */
    float[] getEmbedding(String text);

    /**
     * 获取模型维度
     */
    int getDimensions();
}
