package parser;

import exception.TravelDiaryException;
import ui.Ui;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Parser {
    public static final String[] COMMAND_ARRAY = {"bye", "add_trip", "add_photo", "delete", "list", "select", "menu"};
    private static final Ui ui = new Ui();

    public static Map<String, String> getCommandDetails() throws TravelDiaryException {
        String input = ui.readInput().trim();
        if (input.isEmpty()) {
            throw new TravelDiaryException("No command provided. Please enter a command.");
        }
        String[] tokens = splitCommandAndArguments(input);
        String command = tokens[0];
        String rest = tokens[1];
        return convertToHashmap(command, rest);
    }

    private static String[] splitCommandAndArguments(String input) {
        String[] tokens = input.split("\\s+", 2);
        String command = tokens[0].toLowerCase();
        String rest = tokens.length > 1 ? tokens[1].trim() : "";
        return new String[] { command, rest };
    }

    public static Map<String, String> convertToHashmap(String command, String rest)
            throws TravelDiaryException {
        switch (command) {
        case "bye":
        case "list":
        case "menu":
            return createSimpleCommandMap(command);
        case "delete":
        case "select":
            return createIndexCommandMap(command, rest);
        case "add_trip":
            return parseAddTrip(rest);
        case "add_photo":
            return parseAddPhoto(rest);
        default:
            // New instruction for unrecognized command
            throw new TravelDiaryException("I'm sorry, I don't understand that command. " +
                    "Please refer to the following instructions:\n"
                    + "Available commands:\n"
                    + "- bye: Exit the application\n"
                    + "- add_trip: Add a new trip (format: add_trip n#<name> d#<description> l#<location>)\n"
                    + "- add_photo: Add a new photo " +
                    "(format: add_photo f#<filepath> n#<photoname> c#<caption> l#<location>)\n"
                    + "- delete: Delete a trip/photo (format: delete <index>)\n"
                    + "- list: List all trips\n"
                    + "- select: Select a trip/photo by index (format: select <index>)\n"
                    + "- menu: Show the main menu");
        }
    }

    private static Map<String, String> createSimpleCommandMap(String command) {
        Map<String, String> map = new HashMap<>();
        map.put("command", command);
        return map;
    }

    private static Map<String, String> createIndexCommandMap(String command, String index) {
        Map<String, String> map = new HashMap<>();
        map.put("command", command);
        map.put("index", index);
        return map;
    }

    private static Map<String, String> parseAddTrip(String rest) throws TravelDiaryException {
        Map<String, String> map = new HashMap<>();
        map.put("command", "add_trip");
        String[] parts = rest.split(" (?=[ndl]#)");
        Set<String> allowedTags = new HashSet<>(Arrays.asList("n#", "d#", "l#"));
        Map<String, String> tagsMap = processTags(parts, allowedTags);
        map.put("name", tagsMap.get("n#"));
        map.put("description", tagsMap.get("d#"));
        map.put("location", tagsMap.get("l#"));
        if (map.get("name") == null || map.get("description") == null || map.get("location") == null) {
            throw new TravelDiaryException("Missing required tag(s) for add_trip. Required: n# (name), " +
                    "d# (description), l# (location).");
        }
        return map;
    }

    private static Map<String, String> parseAddPhoto(String rest) throws TravelDiaryException {
        Map<String, String> map = new HashMap<>();
        map.put("command", "add_photo");
        String[] parts = rest.split(" (?=[ndlcf]#)");
        Set<String> allowedTags = new HashSet<>(Arrays.asList("f#", "n#", "c#", "l#"));
        Map<String, String> tagsMap = processTags(parts, allowedTags);
        map.put("filepath", tagsMap.get("f#"));
        map.put("photoname", tagsMap.get("n#"));
        map.put("caption", tagsMap.get("c#"));
        map.put("location", tagsMap.get("l#"));
        if (map.get("filepath") == null || map.get("photoname") == null ||
                map.get("caption") == null || map.get("location") == null) {
            throw new TravelDiaryException("Missing required tag(s) for add_photo. Required: f# (filename), n# " +
                    "(photoname), c# (caption), l# (location).");
        }
        return map;
    }

    private static Map<String, String> processTags(String[] parts, Set<String> allowedTags)
            throws TravelDiaryException {
        Map<String, String> tagsMap = new HashMap<>();
        Set<String> seenTags = new HashSet<>();
        for (String part : parts) {
            Map.Entry<String, String> entry = processTagPart(part, allowedTags, seenTags);
            tagsMap.put(entry.getKey(), entry.getValue());
        }
        return tagsMap;
    }

    private static Map.Entry<String, String> processTagPart(String part, Set<String> allowedTags,
                                                            Set<String> seenTags) throws TravelDiaryException {
        for (String tag : allowedTags) {
            if (part.startsWith(tag)) {
                if (!seenTags.add(tag)) {
                    throw new TravelDiaryException("Duplicate tag provided for " + tag);
                }
                String value = part.substring(tag.length()).trim();
                if (value.isEmpty()) {
                    throw new TravelDiaryException("Empty value provided for tag " + tag);
                }
                return new AbstractMap.SimpleEntry<>(tag, value);
            }
        }
        throw new TravelDiaryException("Invalid tag in command: " + part);
    }
}
