package hao.simple.ai.cmd;

import io.github.ollama4j.models.generate.OllamaStreamHandler;

public class ColorfulOutput {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static void green(String s) {
        System.out.println(ANSI_GREEN + s + ANSI_RESET);
    }

    public static void yellow(String s) {
        System.out.println(ANSI_YELLOW + s + ANSI_RESET);
    }

    public static void blue(String s) {
        System.out.println(ANSI_BLUE + s + ANSI_RESET);
    }

    public static void white(String s) {
        System.out.println(ANSI_WHITE + s + ANSI_RESET);
    }
}
