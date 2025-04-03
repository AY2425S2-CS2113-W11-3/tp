package ui;

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
        System.out.println("Welcome to your Travel Diary Management System!");
        System.out.println("Capture and manage your travel memories with ease!\n");
        System.out.println("Please type \"help\" if you need instructions \n");
    }

    public void showLine() {
        System.out.println(SEPARATOR_LINE);
    }

    public void showToUser(String message) {
        System.out.println(message);
    }

    public void close() {
        scanner.close();
    }
}
