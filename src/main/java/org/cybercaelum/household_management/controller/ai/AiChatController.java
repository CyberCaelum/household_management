package org.cybercaelum.household_management.controller.ai;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.cybercaelum.household_management.ai.tools.MilvusSearchTool;
import org.cybercaelum.household_management.pojo.entity.Result;
import org.cybercaelum.household_management.service.AiChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

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
            
            ## 回答原则
            1. **基于知识作答**：你必须优先使用上下文（Context）中提供的知识库片段回答问题。
            2. **禁止编造**：如果上下文信息不足以回答问题，或信息明显不相关、过期，你**必须**调用 `MilvusSearch` 工具进行二次检索，严禁自行猜测或编造答案。
            3. **工具使用规范**：
                - 调用工具时，传入你认为最关键的核心关键词（例如：入驻条件、佣金比例、服务流程）。
                - 工具返回结果后，仅基于其中的 `answer` 字段内容组织回答，不要输出工具返回的格式性文字（如“根据关键词xxx找到以下相关文档”）。
            4. **边界控制**：
                - 仅回答与家政招募、平台服务、订单、权益相关的问题。
                - 遇到闲聊、政治、编程、医疗等无关话题，统一回复：“抱歉，我是平台业务助手，只能解答家政招募相关问题哦～”
            5. **回答风格**：自然、专业、简洁，必要时可使用项目符号列出关键点，让用户一目了然。
            """;

    /**
     * @description 智能客服，先进行向量数据库查找，然后把相关信息和设定发给ai，如果ai判断信息不正确，
     * 调用工具查找更多信息，返回ai，最终ai回答
     * @author CyberCaelum
     * @date 上午11:07 2026/4/15
     * @param message 信息
     * @return reactor.core.publisher.Flux<java.lang.String>
     **/
    @Operation(summary = "智能客服",description = "智能客服")
    @GetMapping("/ai/generateStream")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .build();
        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt+"第一次知识库查询信息：\n{context}\n");
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(promptTemplate)
                .searchRequest(searchRequest)
                .build();

        ToolCallback toolCallback = FunctionToolCallback
                .builder("MilvusSearch",milvusSearchTool)
                .description("知识库查询工具,使用简短的语言查询")
                .build();
        return ChatClient.builder(deepSeekChatModel)
                .defaultAdvisors(qaAdvisor)  // 自动RAG增强
                .defaultTools(toolCallback)
                .build()
                .prompt()
                .user(message)
                .stream()
                .content();
    }

    @Operation(summary = "客服文档上传",description = "文档上传")
    @PostMapping("/ai/import")
    public Result importToMilvus(MultipartFile file){
        aiChatService.importToMilvus(file);
        return Result.success();
    }
}
