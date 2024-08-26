package hao.simple.ai.agents;

import hao.simple.ai.agents.action.ReActAction;
import hao.simple.ai.agents.action.ReActFinish;
import hao.simple.ai.agents.exception.OutputParserException;
import hao.simple.ai.agents.parser.ReActParser;
import hao.simple.ai.agents.prompt.PromptTemplate;
import hao.simple.ai.tools.ToolsTemplate;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.impl.ConsoleOutputStreamHandler;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.OptionsBuilder;
import io.github.ollama4j.utils.PromptBuilder;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hào on 2024/8/21
 */
public class OllamaAgent {
    public final OllamaAPI api;
    public final Map<String, Tools.ToolSpecification> tools = new HashMap<>();
    public final Options options = new OptionsBuilder()
            .build();

    private final String systemTemplate = PromptTemplate.loadLocal("ReAct_prompt.txt");
    private final ReActParser parser = new ReActParser();

    @SneakyThrows
    public OllamaAgent(String host, long timeoutSec) {
        api = new OllamaAPI(host);
        if (!api.ping()) {
            throw new RuntimeException("Ollama server is not started.");
        }
        api.setRequestTimeoutSeconds(timeoutSec);
        options.getOptionsMap().put("stop", Arrays.asList("\nObservation"));
    }

    public OllamaAgent addTool(Tools.ToolSpecification spec) {
        tools.put(spec.getFunctionName(), spec);
        return this;
    }

    public void invoke(String question) throws OllamaBaseException, IOException, InterruptedException {
        Map<String, String> args = new HashMap<>();
        args.put("input", question);
        StringBuilder scratch = new StringBuilder();
        args.put("agent_scratchpad", scratch.toString());
        args.putAll(ToolsTemplate.render(tools.values()));

        while (true) {
            String prompt = PromptTemplate.render(systemTemplate, args);
            OllamaResult result = api.generate(
                    OllamaModelType.MISTRAL,
                    prompt,
                    true,
                    options
            );
            String resp = result.getResponse();
            var step = new PromptBuilder().add(resp)
                    .add("\n").add("Observation: ");
            try {
                var a = parser.parse(resp);
                if (a instanceof ReActFinish) {
                    System.out.println(((ReActFinish) a).getReturnValues().get("output"));
                    break;
                }
                if (a instanceof ReActAction ra) {
                    step.addLine(invokeTool(ra.getTool(), ra.getToolInput()));
                }
            } catch (OutputParserException ex) {
                if (ex.isSendToLLM()) {
                    step.addLine(ex.getObservation());
                }
            }
            args.put("agent_scratchpad", scratch.append(step.build()).append("\n").toString());
        }
    }

    private String invokeTool(String tool, String input) {
        return tools.get(tool).getToolDefinition().apply(
                Map.of("args", input)
        ).toString();
    }
}
