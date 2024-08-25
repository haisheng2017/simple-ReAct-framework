package hao.simple.ai.tools;

import io.github.ollama4j.tools.Tools;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hào on 2024/8/23
 */
public class ToolsTemplate {

    private static final String FORMAT = "%s: %s";

    @SneakyThrows
    public static Map<String, String> render(Collection<Tools.ToolSpecification> tools) {
        return Map.of("tools", tools.stream().map(t -> String.format(FORMAT, t.getFunctionName(), t.getFunctionDescription()))
                        .collect(Collectors.joining("\n")),
                "tool_names", tools.stream().map(Tools.ToolSpecification::getFunctionName)
                        .collect(Collectors.joining(",")));
    }
}
