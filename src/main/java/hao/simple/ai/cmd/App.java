package hao.simple.ai.cmd;

import hao.simple.ai.agents.OllamaAgent;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String host = "http://192.168.1.14:11434/";
        OllamaAgent agent=new OllamaAgent(host,30L);
    }
}
