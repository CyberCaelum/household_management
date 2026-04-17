package org.cybercaelum.household_management.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 创建检索器
 * @date 2026/4/17
 */
@Component
@RequiredArgsConstructor
public class QaPairDocumentRetriever implements DocumentRetriever {

    private final VectorStore vectorStore;
    private int topk = 3;
    private double similarityThreshold = 0.5;

    @Override
    public List<Document> retrieve(Query query) {
        SearchRequest request = SearchRequest.builder()
                .query(query.text())
                .topK(topk)
                .similarityThreshold(similarityThreshold)
                .build();
        List<Document> documents = vectorStore.similaritySearch(request);

        return documents.stream().map(document -> {
            String question = document.getText();
            Object answerObj = document.getMetadata().get("answer");
            String answer = answerObj != null ? answerObj.toString() : null;
            String enrichedContent = String.format("""
                    【知识条目】
                    问题：%s
                    答案：%s
                    """, question, answer);
            return new Document(enrichedContent,document.getMetadata());
        }).collect(Collectors.toList());
    }
    // Builder 模式便于配置
    public static Builder builder(VectorStore vectorStore) {
        return new Builder(vectorStore);
    }

    public static class Builder {
        private final QaPairDocumentRetriever retriever;

        private Builder(VectorStore vectorStore) {
            this.retriever = new QaPairDocumentRetriever(vectorStore);
        }

        public Builder similarityThreshold(double threshold) {
            retriever.similarityThreshold = threshold;
            return this;
        }

        public Builder topk(int topK) {
            retriever.topk = topK;
            return this;
        }

        public QaPairDocumentRetriever build() {
            return retriever;
        }
    }
}
