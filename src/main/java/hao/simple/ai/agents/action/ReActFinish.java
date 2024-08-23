package hao.simple.ai.agents.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ReActFinish implements Action {
    private Map<String, String> returnValues;
    private String originalResponse;
}
