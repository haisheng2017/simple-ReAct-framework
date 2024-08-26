package hao.simple.ai.tools;

import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by hào on 2024/8/23
 */
public class Lookup {
    public String lookup(String keyword) {
        String q = IOUtils.readLines(IOUtils.toInputStream(keyword, StandardCharsets.UTF_8), StandardCharsets.UTF_8)
                .get(0).toLowerCase();
        // TODO 事先准备好相关词
        if (q.contains("langchain")) {
            return "LangChain is an open source orchestration framework for building applications using large language models (LLMs) like chatbots and virtual agents. It was launched by Harrison Chase in October 2022 and has gained popularity as the fastest-growing open source project on Github in June 2023.";
        }
        return "Sorry, we don't find any relative information.";
    }
}
