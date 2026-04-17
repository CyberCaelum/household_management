package org.cybercaelum.household_management.ai.tools;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.RequiredArgsConstructor;
import org.cybercaelum.household_management.pojo.dto.MilvusSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 查询向量数据库工具类
 * @date 2026/4/15 上午9:17
 */
@RequiredArgsConstructor
@Component
public class MilvusSearchTool implements BiFunction<MilvusSearchRequest, ToolContext, String> {

    private static final Logger log = LoggerFactory.getLogger(MilvusSearchTool.class);
    private final VectorStore vectorStore;

    /**
     * @description 通过关键此查询向量数据库信息
     * @author CyberCaelum
     * @date 上午9:28 2026/4/15
     * @param s 关键词
     * @param toolContext 工具方法的参数
     * @return java.lang.String
     **/
    @Override
    public String apply(MilvusSearchRequest s, ToolContext toolContext) {
        log.info("工具被调用了");
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(s.getQuery())
                        .topK(3)
                        .build()
        );

        if (results.isEmpty()) {
            return "未找到相关结果";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("根据关键词【").append(s).append("】找到以下相关文档:\n");
        for (int i = 0; i < results.size(); i++){
            Document doc = results.get(i);
            sb.append(i+1).append(".");
            sb.append(doc.getMetadata().get("answer")).append("\n");
        }
        return sb.toString();
    }
}
