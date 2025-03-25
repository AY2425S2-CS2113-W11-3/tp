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
        System.out.println("Welcome to your Travel Diary Management System!");
        System.out.println("Capture and manage your travel memories with ease!\n");
        showAvailableCommands();
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

    public void showAvailableCommands() {
        System.out.println("Available Commands:");
        System.out.println("-------------------");

        System.out.println("1. add_trip - Create a new trip");
        System.out.println("   Example: add_trip n#Paris Adventure d#Exploring Europe l#Paris, France\n");

        System.out.println("2. add_photo - Add a photo to a trip");
        System.out.println("   Example: add_photo f#/path/to/photo.jpg n#Eiffel Tower c#Sunset view l#Paris\n");

        System.out.println("3. list - View all trips");
        System.out.println("   Example: list\n");

        System.out.println("4. select - Choose a specific trip");
        System.out.println("   Example: select 1 (selects the first trip in the list)\n");

        System.out.println("5. delete - Remove a trip or photo");
        System.out.println("   Example: delete 2 (deletes the second trip)\n");

        System.out.println("6. menu - Show this help menu");
        System.out.println("   Example: menu\n");

        System.out.println("7. bye - Exit the Travel Diary");
        System.out.println("   Example: bye\n");

        System.out.println("Pro Tips:");
        System.out.println("- Use '#' to separate tag types (n#, d#, l#, f#, c#)");
        System.out.println("- Always provide all required tags when adding trips or photos");
        System.out.println("- Refer to previous examples if you're unsure about command syntax\n");
    }
}