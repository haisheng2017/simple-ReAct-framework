package hao.simple.ai.cmd;

import hao.simple.ai.agents.OllamaAgent;
import hao.simple.ai.tools.LocalSearch;
import hao.simple.ai.tools.WikiOpenSearch;
import io.github.ollama4j.exceptions.OllamaBaseException;

import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws OllamaBaseException, IOException, InterruptedException {
        String host = "http://localhost:11434/";
        OllamaAgent agent = new OllamaAgent(host, 60L);
        LocalSearch engine = new LocalSearch();
        agent.addTool("call_google", map -> {
//            try {
            return engine.search(map.get("question").toString());
//            } catch (IOException | InterruptedException e) {
//
//            }
//            return "Not found any related information.";
        });
        Scanner in = new Scanner(System.in);
        System.out.println("Start your chat.");
        while (true) {
            System.out.print("You: ");
            String instruction = in.nextLine();
            if (instruction.equals("q")) {
                System.out.println("Exit.");
                break;
            }
            agent.chat(instruction);
        }
    }
}
