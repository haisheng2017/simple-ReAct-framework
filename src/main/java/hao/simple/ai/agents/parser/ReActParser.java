package hao.simple.ai.agents.parser;

import hao.simple.ai.agents.action.Action;
import hao.simple.ai.agents.action.ReActAction;
import hao.simple.ai.agents.action.ReActFinish;
import hao.simple.ai.agents.exception.OutputParserException;

import java.util.Map;
import java.util.regex.Pattern;

public class ReActParser {

    private static final String FINAL_ANSWER_ACTION = "Final Answer:";
    private static final String MISSING_ACTION_AFTER_THOUGHT_ERROR_MESSAGE =
            "Invalid Format: Missing 'Action:' after 'Thought:";

    private static final String MISSING_ACTION_INPUT_AFTER_ACTION_ERROR_MESSAGE =
            "Invalid Format: Missing 'Action Input:' after 'Action:'";
    private static final String FINAL_ANSWER_AND_PARSABLE_ACTION_ERROR_MESSAGE =
            "Parsing LLM output produced both a final answer and a parse-able action:";

    private final Pattern pattern = Pattern.compile("Action\\s*\\d*\\s*:\\s*(.*?)\\s*Action\\s*\\d*\\s*Input\\s*\\d*\\s*:\\s*(.*)", Pattern.DOTALL);
    private final Pattern e1 = Pattern.compile("Action\\s*\\d*\\s*:\\s*(.*?)", Pattern.DOTALL);
    private final Pattern e2 = Pattern.compile("\\s*Action\\s*\\d*\\s*Input\\s*\\d*\\s*:\\s*(.*)", Pattern.DOTALL);

    public Action parse(String response) {
        boolean hasAnswer = response.contains(FINAL_ANSWER_ACTION);
        var m = pattern.matcher(response);
        if (m.find()) {
            if (hasAnswer) {
                throw new RuntimeException(
                        FINAL_ANSWER_AND_PARSABLE_ACTION_ERROR_MESSAGE + ": " + response
                );
            }
            var tool = m.group(1).strip();
            var input = m.group(2).strip().replaceAll("\"", "");
            return new ReActAction(tool, input, response);
        } else if (hasAnswer) {
            var fs = response.split(FINAL_ANSWER_ACTION);
            return new ReActFinish(Map.of("output", fs[fs.length - 1].strip()), response);
        }
        if (!e1.matcher(response).find()) {
            throw new OutputParserException(
                    "Could not parse LLM output: " + response,
                    MISSING_ACTION_AFTER_THOUGHT_ERROR_MESSAGE,
                    response,
                    true
            );
        } else if (!e2.matcher(response).find()) {
            throw new OutputParserException(
                    "Could not parse LLM output: " + response,
                    MISSING_ACTION_INPUT_AFTER_ACTION_ERROR_MESSAGE,
                    response,
                    true
            );
        }
        throw new OutputParserException("Could not parse LLM output: " + response);
    }
}