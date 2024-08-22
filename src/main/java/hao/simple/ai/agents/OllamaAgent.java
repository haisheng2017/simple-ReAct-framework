package hao.simple.ai.agents;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.ToolRegistry;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.PromptBuilder;

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

    public final ToolRegistry tools = new ToolRegistry();

    public OllamaAgent(String host, long timeoutSec) {
        api = new OllamaAPI(host);
        if (!api.ping()) {
            throw new RuntimeException("Ollama server is not started.");
        }
        api.setRequestTimeoutSeconds(timeoutSec);
        history.add(new OllamaChatMessage(OllamaChatMessageRole.SYSTEM, SystemPrompt.system()));
    }

    public OllamaAgent addTool(String name, ToolFunction function) {
        tools.addFunction(name, function);
        return this;
    }

    public void chat(String question) throws OllamaBaseException, IOException, InterruptedException {
        // 这个builder很多都是浅拷贝
        history.add(new OllamaChatMessage(OllamaChatMessageRole.USER, "User: " + question));
        OllamaChatRequest request = OllamaChatRequestBuilder.getInstance(OllamaModelType.MISTRAL)
                .withMessages(new ArrayList<>(history))
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
        }else {
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

    private static class SystemPrompt {
        public static String system() {
            return new PromptBuilder()
                    .addLine(
                            """
                                    You run in a loop of Thought, Action.
                                    At the end of the loop you output an Answer
                                    Use Thought to describe your thoughts about the question you have been asked.
                                    Use Action to run one of the actions available to you, and user will call you again.

                                    Your available actions are:

                                    call_google:
                                    e.g. call_google: European Union
                                    Returns a summary from searching European Union on google
                                                                        
                                    You should attention that if you know the Answer, don't call any action, just return answer. Once you do an 'Action', you must stop and waiting.
                                    You can't choose any other actions. If you don't know the answer, just say you don't know.
                                    

                                    Example1:

                                    User: What is the capital of Australia?
                                    Thought: I can look up Australia on Google
                                    Action: call_google: Australia

                                    You will be called again with this:

                                    Observation: Australia is a country. The capital is Canberra.

                                    You then output:

                                    Answer: The capital of Australia is Canberra
                                                                        
                                    Example2:

                                    User: How are you?
                                    Thought: Easy to answer.
                                    You then output:
                                    Answer: Good

                                    Example3:

                                    User: What are the constituent countries of the European Union? Try to list all of them.
                                    Thought: I can search the constituent countries in European Union by Google
                                    Action: call_google: constituent countries in European Union

                                    Observation: The European Union is a group of 27 countries in Europe, Today, 27 countries are part of the European Union.
                                    These countries are:

                                    Austria、Belgium、Bulgaria、Croatia、Cyprus、Czechia、Denmark、Estonia、Finland、France、Germany、Greece、Hungary、Ireland、
                                    Italy、Latvia、Lithuania、Luxembourg、Malta、Netherands、Poland、Portugal、Romania、Slovakia、Slovenia、Spain、Sweden

                                    Answer: There are 27 constituent countries in European Union, the name of constituent countries are listed as follows
                                    Austria
                                    Belgium
                                    Bulgaria
                                    Croatia
                                    Cyprus
                                    Czechia
                                    Denmark
                                    Estonia
                                    Finland
                                    France
                                    Germany
                                    Greece
                                    Hungary
                                    Ireland
                                    Italy
                                    Latvia
                                    Lithuania
                                    Luxembourg
                                    Malta
                                    Netherlands
                                    Poland
                                    Portugal
                                    Romania
                                    Slovakia
                                    Slovenia
                                    Spain
                                    Sweden
                                    """.trim()
                    )
                    .build();
        }
    }
}
