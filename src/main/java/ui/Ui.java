package ui;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import parser.Parser;
import storage.Storage;
import trip.Trip;

public class Ui {
    private static final String SEPARATOR_LINE = "____________________________________________________________\n";
    public static final String[] COMMAND_ARRAY = Parser.COMMAND_ARRAY;
    private final Scanner scanner;
    private List<Trip> trips;

    public Ui() {
        this.scanner = new Scanner(System.in);
        this.trips = Storage.loadTrips(); // Load trips once when UI is initialized
    }

    public String readInput() {
        try {
            return scanner.nextLine();
        } catch (java.util.NoSuchElementException e) {
            System.err.println("Warning: No input available.");
            // Either provide a default value or re-throw a more informative exception
            return "bye"; // Return a command to exit gracefully
        }
    }

    public void showWelcome() {
        System.out.println("Welcome to your Travel Diary Management System!\n" +
                "To continue, please press one of the " +
                "commands.\n");
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
        // Check if there are trips
        if (trips.isEmpty()) {
            // Only show commands related to trip management when there are no trips
            System.out.println("Available commands for you:");
            Arrays.stream(new String[]{"menu", "bye", "add_trip", "delete", "list", "select"})
                    .forEach(command -> System.out.println("    - " + command));
        } else {
            // Show full set of commands if there are trips
            System.out.println("Available commands for you:");
            Arrays.stream(COMMAND_ARRAY).forEach(command -> System.out.println("    - " + command));
        }
        System.out.println(System.lineSeparator());
    }

    public void showTripSelectionMessage() {
        System.out.println("Please select a trip first to add photos or perform other actions related to the trip.");
    }
}
