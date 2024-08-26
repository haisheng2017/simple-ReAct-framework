package hao.simple.ai.cmd;

import hao.simple.ai.agents.OllamaAgent;
import hao.simple.ai.tools.Lookup;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.tools.Tools;

import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws OllamaBaseException, IOException, InterruptedException {
        String host = "http://localhost:11434/";
        OllamaAgent agent = new OllamaAgent(host, 600L);
        Lookup engine = new Lookup();
        agent.addTool(Tools.ToolSpecification.builder()
                .functionDescription("This tool is used for search")
                .functionName("search")
                .toolDefinition(map -> engine.lookup(map.get("args").toString()))
                .build());
        Scanner in = new Scanner(System.in);
        System.out.println("Start your chat.");
        while (true) {
            System.out.print("You: ");
            String instruction = in.nextLine();
            if (instruction.equals("q")) {
                System.out.println("Exit.");
                break;
            }
            agent.invoke(instruction);
        }
    }
}
