package org.cybercaelum.household_management.controller.ai;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.cybercaelum.household_management.ai.QaPairDocumentRetriever;
import org.cybercaelum.household_management.ai.tools.MilvusSearchTool;
import org.cybercaelum.household_management.pojo.dto.MilvusSearchRequest;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.AiChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: ai客服controller
 * @date 2026/4/13 上午10:52
 */
@RestController
@RequiredArgsConstructor
public class AiChatController {

    private final DeepSeekChatModel deepSeekChatModel;
    private final VectorStore vectorStore;
    private final MilvusSearchTool milvusSearchTool;
    private final AiChatService aiChatService;

    // 构建系统提示词：告诉AI它的角色和可用工具策略
    String systemPrompt = """
            你是智慧家政-家政招募平台的智能客服助手。专注于解答家政招募、服务流程等平台相关问题。
            
            ## 核心行为准则
            1. **系统已预检索**：你收到的用户消息前，已经附带了知识库中相关的 QA 对（标记为【知识条目】）。
            2. **优先使用预检索**：优先基于已提供的【知识条目】回答用户问题。
            3. **自主补充查询（关键）**：如果预检索的内容不足以回答用户问题，或用户问题涉及新的关键词，你必须调用 `searchKnowledgeBase` 工具补充查询。
            4. **禁止编造**：如果预检索和工具查询都没有结果，回答"对不起，我暂时没有找到相关的规则信息"。
            5. **边界控制**：
               - 仅回答与家政招募、平台服务、订单、权益相关的问题。
               - 遇到无关话题，直接回复：“抱歉，我是平台业务助手，只能解答家政招募相关问题哦～”
            ## 工具调用规范
               - 调用 `searchKnowledgeBase` 时，使用简短关键词（如"押金退款"、"订单查询"）
               - 不要告知用户"我正在查询"等中间状态，直接输出工具调用
               - 获得工具返回后，基于新信息组织回答
               - 最多调用两次工具
            
            ## 回答风格
            自然、专业、简洁，基于事实回答,必要时使用项目符号列出关键点。
            """;
    // 1. 定义一个新的、更智能的 PromptTemplate
    String customRagPrompt = """
        Context information is below.
        ---------------------
        {context}
        ---------------------
        
        User Query: {query}
        
        Instructions:
        - If the context above contains sufficient information to answer the query, use it to provide a concise and helpful answer.
        - If the context is **insufficient or irrelevant**, DO NOT say "I don't know" immediately. Instead, you **MUST call the `searchKnowledgeBase` tool** with appropriate keywords to search for more information.
        - Only if the tool also returns no useful information, respond with: "对不起，我暂时没有找到相关的规则信息。"
        - Avoid phrases like "Based on the context...". Answer directly.
        """;
    /**
     * @param message 信息
     * @return reactor.core.publisher.Flux<java.lang.String>
     * @description 智能客服，先进行向量数据库查找，然后把相关信息和设定发给ai，如果ai判断信息不正确，
     * 调用工具查找更多信息，返回ai，最终ai回答
     * @author CyberCaelum
     * @date 上午11:07 2026/4/15
     **/
    @Operation(summary = "智能客服",description = "智能客服")
    @GetMapping("/ai/generateStream")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        //文档检索器
        QaPairDocumentRetriever documentRetriever = QaPairDocumentRetriever
                .builder(vectorStore)
                .similarityThreshold(0.5)
                .topk(3)
                .build();
//        //上下文增强器,允许上下文为空
//        ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
//                .allowEmptyContext(true)
//                .emptyContextPromptTemplate(new PromptTemplate("""
//                        未在知识库中找到与用户问题直接相关的信息。
//                        请调用 `searchKnowledgeBase` 工具查询相关信息后再回答。
//                        """))
//                .build();
        //组装
        Advisor retrievalAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(
                        ContextualQueryAugmenter.builder()
                                .allowEmptyContext(true)
                                .emptyContextPromptTemplate(new PromptTemplate("""
                                未在知识库中找到与用户问题直接相关的信息。
                                请调用 `searchKnowledgeBase` 工具查询相关信息后再回答。
                                """))
                                .promptTemplate(new PromptTemplate(customRagPrompt)) // 关键：覆盖默认模板
                                .build()
                )
                .build();
        //构建工具
        ToolCallback toolCallback = FunctionToolCallback
                .builder("searchKnowledgeBase", milvusSearchTool)
                .description("知识库查询工具,使用简短的语言查询")
                .inputType(MilvusSearchRequest.class)
                .build();
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(),  // 打印完整请求和响应
                         retrievalAdvisor)
                .defaultSystem(systemPrompt)
                .build();


        return chatClient.prompt()
                .toolCallbacks(List.of(toolCallback))
                .user(message)
                .stream()
                .content();
    }

    @Operation(summary = "客服文档上传",description = "文档上传")
    @PostMapping(value = "/ai/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importToMilvus(@RequestParam("file")MultipartFile file){
        aiChatService.importToMilvus(file);
        return Result.success();
    }
}
