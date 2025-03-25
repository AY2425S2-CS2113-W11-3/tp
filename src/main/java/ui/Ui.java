package ui;


import java.util.Arrays;
import java.util.Scanner;

import parser.Parser;

public class Ui {
    private static final String SEPARATOR_LINE = "____________________________________________________________\n";
    public static final String[] COMMAND_ARRAY = Parser.COMMAND_ARRAY;
    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public String readInput() {
        return scanner.nextLine();
    }

    public void showWelcome() {
        System.out.println("Welcome to your Travel Diary Management System!\nTo continue, please press one of the " +
                "commands.\n");
        showAvailableCommands();
    }

    public void showLine() {
        System.out.println("----");
    }

    public void showToUser(String message) {
        System.out.println(message);
    }

    public void close() {
        scanner.close();
    }

    public void showAvailableCommands() {
        Arrays.stream(COMMAND_ARRAY).forEach((i) -> System.out.println("    - " + i));
        System.out.println(System.lineSeparator());
    }
}
