package hao.simple.ai.agents;

import hao.simple.ai.agents.prompt.PromptTemplate;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.ToolRegistry;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.OptionsBuilder;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by hào on 2024/8/21
 */
public class OllamaAgent {
    public final OllamaAPI api;
    private final List<OllamaChatMessage> history = new ArrayList<>();

    public final ToolRegistry toolRef;
    public final Options options = new OptionsBuilder().setStop(
            "\nObservation"
    ).build();

    @SneakyThrows
    public OllamaAgent(String host, long timeoutSec) {
        api = new OllamaAPI(host);
        if (!api.ping()) {
            throw new RuntimeException("Ollama server is not started.");
        }
        api.setRequestTimeoutSeconds(timeoutSec);
        history.add(new OllamaChatMessage(OllamaChatMessageRole.SYSTEM,
                PromptTemplate.loadLocal("ReAct_prompt.txt")));
        toolRef = (ToolRegistry) FieldUtils.getField(OllamaAPI.class, "toolRegistry").get(api);
    }


    public void chat(String question) throws OllamaBaseException, IOException, InterruptedException {
        // 这个builder很多都是浅拷贝
        history.add(new OllamaChatMessage(OllamaChatMessageRole.USER, "User: " + question));
        OllamaChatRequest request = OllamaChatRequestBuilder.getInstance(OllamaModelType.MISTRAL)
                .withMessages(new ArrayList<>(history))
                .withOptions(options)
                .build();
        boolean fin = false;
        do {
            OllamaChatResult chatResult = api.chat(request);
            String resp = chatResult.getResponse();
            // 有些模型可能不能理解这个prompt，会每个都带上action
            if (resp.contains("Action: ") && doAction(resp)) {
                request = OllamaChatRequestBuilder.getInstance(OllamaModelType.MISTRAL)
                        .withMessages(new ArrayList<>(history))
                        .build();
                continue;

            }
            if (resp.contains("Answer: ")) {
                history.add(new OllamaChatMessage(OllamaChatMessageRole.ASSISTANT, resp));
                System.out.println(resp);
                fin = true;
            }
        } while (!fin);
    }

    private boolean doAction(String resp) {
        Pattern actionPattern = Pattern.compile("Action: (\\w+): (.*)(\n|$)");
        var m = actionPattern.matcher(resp);
        Object obs = null;
        if (m.find()) {
            resp = resp.substring(0, resp.indexOf(m.group(0)));
            String toolName = m.group(1);
            String q = m.group(2);
            if (tools.getFunction(toolName) == null) {
                return false;
            }
            obs = tools.getFunction(toolName).apply(Map.of("question", q));
            resp += m.group(0);
        } else {
            System.out.println(resp);
        }
        String observation = (obs == null ? "User: You can use action 'call_google' only. If you already have answer, just say your Answer." : "Observation: " + obs);
        System.out.println(resp);
        System.out.println(observation);
        history.addAll(OllamaChatRequestBuilder.getInstance(OllamaModelType.MISTRAL)
                .withMessage(OllamaChatMessageRole.ASSISTANT, resp)
                .withMessage(OllamaChatMessageRole.USER, observation).build().getMessages());
        return true;
    }
}
