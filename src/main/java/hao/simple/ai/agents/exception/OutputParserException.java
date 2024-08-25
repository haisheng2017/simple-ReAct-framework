package hao.simple.ai.agents.exception;

import lombok.Getter;

@Getter
public class OutputParserException extends RuntimeException {
    private String observation;
    private String llmOutput;
    private boolean sendToLLM;

    public OutputParserException(String message) {
        super(message);
    }

    public OutputParserException(String message, String observation, String llmOutput, boolean sendToLLM) {
        super(message);
        this.observation = observation;
        this.llmOutput = llmOutput;
        this.sendToLLM = sendToLLM;
    }
}
