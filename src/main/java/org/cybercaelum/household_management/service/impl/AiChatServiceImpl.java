package org.cybercaelum.household_management.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cybercaelum.household_management.service.AiChatService;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

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
    //TODO 查找向量数据库中的相关问题的答案
    //TODO 存入向量数据库
    private final VectorStore vectorStore;
}
