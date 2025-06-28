package com.llm.service.iml;

import com.llm.service.EmbeddingModel;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.embeddings.CreateEmbeddingResponse;
import com.openai.models.embeddings.EmbeddingCreateParams;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service("customOpenAiEmbeddingModel")
public class OpenAiEmbeddingModel implements EmbeddingModel {

    RestTemplate restTemplate;
    @Value("${spring.ai.openai.embedding.api-key}")
    private  String apiKey;
    @Value("${spring.ai.openai.embedding.options.model}")
    private  String model;
    @Value("${spring.ai.openai.embedding.base-url}")
    private  String url;

    public OpenAiEmbeddingModel() {
        this.restTemplate = new RestTemplate();
    }


    public float[] getEmbedding(String text) {
        // 创建客户端，使用环境变量中的API密钥
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(url)
                .build();

        // 创建向量化请求参数
        EmbeddingCreateParams params = EmbeddingCreateParams.builder()
                .model(model)
                .input(text)
                // 指定向量维度（仅 text-embedding-v3及 text-embedding-v4支持该参数）
                .dimensions(1536)
                .build();

        try {
            // 发送请求并获取响应
            CreateEmbeddingResponse response = client.embeddings().create(params);
            List<Double> embedding =  response.data().get(0).embedding();
            float[] result = new float[embedding.size()];
            IntStream.range(0, embedding.size()).forEach(i -> result[i] = embedding.get(i).floatValue());

            return result;
        } catch (Exception e) {
            System.err.println("请求出错，请查看错误码对照网页：");
            System.err.println("https://help.aliyun.com/zh/model-studio/faq-about-alibaba-cloud-model-studio?spm=a2c4g.11186623.help-menu-2400256.d_0_17_0.18733a66lTrcHv#1c38f58abfcml");
            System.err.println("错误详情：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getDimensions() {
        return 1536; // text-embedding-ada-002的维度
    }
}
