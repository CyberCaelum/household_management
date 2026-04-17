package org.cybercaelum.household_management.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.service.AiChatService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: ai客服Service
 * @date 2026/4/13 上午10:52
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
    //TODO 存入向量数据库
    private final VectorStore vectorStore;

    @Override
    public void importToMilvus(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        log.info("开始导入文件到 Milvus: {}", filename);
        List<Document> documents = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), new ReadListener<Map<Integer, String>>() {
                @Override
                public void invoke(Map<Integer, String> row, AnalysisContext context) {
                    //判断是否有必要列
                    if (!row.containsKey(0) || !row.containsKey(1)) {
                        log.warn("第 {} 行缺少必要列", context.readRowHolder().getRowIndex() + 1);
                        return;
                    }
                    //获取列
                    String question = row.get(0);   // 第一列：问题
                    String answer = row.get(1);     // 第二列：答案

                    //逃过异常行
                    if (question == null || question.isBlank() || answer == null || answer.isBlank()) {
                        log.warn("第 {} 行问题或答案为空，跳过", context.readRowHolder().getRowIndex() + 1);
                        return;
                    }
                    //加入数据
                    documents.add(new Document(question,Map.of("answer", answer)));
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel 解析完成，共 {} 行", documents.size());
                }
            }).sheet().doRead();
            //存入数据
            vectorStore.add(documents);
        } catch (Exception e) {
            log.error("文件读取失败", e);
            throw new RuntimeException("文件读取失败: " + e.getMessage());
        }
    }
}
