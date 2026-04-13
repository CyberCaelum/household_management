package org.cybercaelum.household_management.controller.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    //TODO 先进行向量数据库查找，然后把相关信息和设定发给ai，
    // 如果ai判断信息不正确，调用工具查找更多信息，返回ai，最终ai回答
    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var prompt = new Prompt(new UserMessage(message));
        return deepSeekChatModel.stream(prompt);
    }
}
