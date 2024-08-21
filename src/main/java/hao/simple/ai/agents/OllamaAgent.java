package hao.simple.ai.agents;

import io.github.ollama4j.OllamaAPI;

/**
 * Created by hào on 2024/8/21
 */
public class OllamaAgent {
    public final OllamaAPI api;

    public OllamaAgent(String host, long timeoutSec) {
        api = new OllamaAPI(host);
        if (!api.ping()) {
            throw new RuntimeException("Ollama server is not started.");
        }
        api.setRequestTimeoutSeconds(timeoutSec);
    }
}
