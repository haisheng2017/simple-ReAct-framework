package hao.simple.ai.agents.prompt;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PromptTemplate {
    @SneakyThrows
    public static String loadLocal(String fileName) {
        return IOUtils.resourceToString(fileName, StandardCharsets.UTF_8,
                Thread.currentThread().getContextClassLoader());
    }

    public static String render(String template, Map<String, String> args) {
        String varTemplate = "\\{%s\\}";
        for (var e : args.entrySet()) {
            template = template.replaceFirst(String.format(varTemplate, e.getKey()), e.getValue());
        }
        return template;
    }
}
