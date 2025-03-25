package command;

import exception.TravelDiaryException;

import java.util.Map;

public class CommandFactory {

    public static Command getCommand(Map<String, String> parsedCommand, int fsmValue) throws
            TravelDiaryException, NumberFormatException{
        String cmd = parsedCommand.get("command");

        // Handle commands available in both FSM states.
        if ("bye".equals(cmd)) {
            return new ExitCommand();
        }
        if ("select".equals(cmd)) {
            int index = Integer.parseInt(parsedCommand.get("index"));
            return new SelectCommand(index); // Insert index in, update FSM value
        }
        if ("list".equals(cmd)) {
            return new ListCommand();
        }
        if ("delete".equals(cmd)) {
            int index = Integer.parseInt(parsedCommand.get("index"));
            return new DeleteCommand(index); // Insert index in
        }

        // Delegate state-specific commands.
        if (fsmValue == 0) {
            return handleMenuStateCommand(parsedCommand);
        } else if (fsmValue == 1) {
            return handleTripStateCommand(parsedCommand);
        } else {
            throw new TravelDiaryException("Invalid state.");
        }
    }

    private static Command handleMenuStateCommand(Map<String, String> parsedCommand) throws TravelDiaryException {
        String cmd = parsedCommand.get("command");
        if ("add_trip".equals(cmd)) {
            String name = parsedCommand.get("name");
            String description = parsedCommand.get("description");
            String location = parsedCommand.get("location");
            return new AddTripCommand(name, description, location);
        }
        if ("menu".equals(cmd)) {
            throw new TravelDiaryException("You are already in the menu.");
        }
        throw new TravelDiaryException("Please select a trip first using 'select' [Trip ID] command. To view all " +
                "your trips, use 'list'");
    }

    private static Command handleTripStateCommand(Map<String, String> parsedCommand) throws TravelDiaryException {
        String cmd = parsedCommand.get("command");
        if ("add_photo".equals(cmd)) {
            String filepath = parsedCommand.get("filepath");
            String photoname = parsedCommand.get("photoname");
            String caption = parsedCommand.get("caption");
            String location = parsedCommand.get("location");
            return new AddPhotoCommand(filepath,photoname, caption, location);
        }
        if ("menu".equals(cmd)) {
            // Assuming MenuCommand resets FSM state and performs any required housekeeping.
            return new MenuCommand();
        }
        throw new TravelDiaryException("Please go back to main menu first before adding a trip. Use 'menu' command");
    }
}
