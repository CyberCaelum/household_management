package org.cybercaelum.household_management;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
class HouseholdManagementApplicationTests {

    @Autowired
    private DeepSeekChatModel chatModel;

    @Autowired
    private VectorStore vectorStore;

    @Test
    void testApiConnection() {
        // 测试1：简单调用
        try {
            String response = chatModel.call("Hello, test message");
            System.out.println("✅ API 正常，返回: " + response);
        } catch (Exception e) {
            System.err.println("❌ 简单调用失败: " + e.getMessage());
            printDetailedError(e);
        }
    }

    @Test
    void testStreamApi() {
        // 测试2：流式调用（你正在用的功能）
        try {
            Flux<ChatResponse> stream = chatModel.stream(new Prompt("测试流式响应"));
            stream.collectList()
                    .doOnNext(list -> System.out.println("✅ 流式 API 正常，返回 " + list.size() + " 个 chunk"))
                    .doOnError(this::printDetailedError)
                    .block(); // 阻塞等待结果
        } catch (Exception e) {
            printDetailedError(e);
        }
    }

    @Test
    void testVectorStoreSearch() {
        // 测试3：向量数据库查询
        try {
            String query = "如何查询订单状态"; // 可以改成你要测试的关键词
            List<Document> results = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(3)
                            .build()
            );
            System.out.println("✅ 向量查询成功，关键词: " + query + ", 返回 " + results.size() + " 条结果");
            for (int i = 0; i < results.size(); i++) {
                Document doc = results.get(i);
                System.out.println("--- 结果 " + (i + 1) + " ---");
                System.out.println("content: " + doc.getText());
                System.out.println("metadata: " + doc.getMetadata());
            }
        } catch (Exception e) {
            System.err.println("❌ 向量查询失败: " + e.getMessage());
            printDetailedError(e);
        }
    }

    private void printDetailedError(Throwable error) {
        // 关键：提取 DeepSeek 返回的详细错误信息
        if (error instanceof WebClientResponseException) {
            WebClientResponseException webError = (WebClientResponseException) error;
            System.err.println("状态码: " + webError.getStatusCode());
            System.err.println("响应体: " + webError.getResponseBodyAsString());
            System.err.println("请求头: " + webError.getHeaders());
        } else {
            error.printStackTrace();
        }
    }
}
