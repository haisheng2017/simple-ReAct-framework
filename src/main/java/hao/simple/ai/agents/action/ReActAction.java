package hao.simple.ai.agents.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReActAction implements Action {
    private String tool;
    private String toolInput;
    private String originalResponse;
}
