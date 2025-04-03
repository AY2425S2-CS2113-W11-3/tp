package command;

import exception.InvalidIndexException;
import exception.MissingCompulsoryParameter;
import exception.TravelDiaryException;
import trip.TripManager;
import ui.Ui;

/**
 * Represents a command to display help information.
 */
public class HelpCommand extends Command {
    private int helpFsmValue;

    /**
     * Creates a new HelpCommand.
     *
     * @param helpFsmValue The FSM value to use for help display. If null, uses current application FSM value.
     */
    public HelpCommand(String helpFsmValue) {
        if (helpFsmValue != null) {
            try {
                this.helpFsmValue = Integer.parseInt(helpFsmValue);
            } catch (NumberFormatException e) {
                this.helpFsmValue = -1; // Invalid FSM value, will use current app FSM value
            }
        } else {
            this.helpFsmValue = -1; // No FSM value provided, will use current app FSM value
        }
    }

    @Override
    public void execute(TripManager tripManager, Ui ui, int fsmValue) throws
            TravelDiaryException, MissingCompulsoryParameter, InvalidIndexException {

        // If a valid FSM value was provided in the command, use it, otherwise use the current FSM value
        int fsmToUse = (helpFsmValue == 0 || helpFsmValue == 1) ? helpFsmValue : fsmValue;

        showHelp(fsmToUse);
    }

    /**
     * Displays help information based on the current state.
     * FSM = 0: General commands and Trip commands
     * FSM = 1: General commands and Photo commands
     *
     * @param fsm The current state of the finite state machine
     */
    private void showHelp(int fsm) {
        System.out.println("============ TRAVEL DIARY HELP ============");

        // General commands - always available
        System.out.println("\n📋 GENERAL COMMANDS:");
        System.out.println("  help              - Display this help information");
        System.out.println("                       Example: help");
        System.out.println("  bye               - Save and exit the application");
        System.out.println("  menu              - Return to the main menu");

        // State-specific commands
        if (fsm == 0) {
            // Trip management commands
            System.out.println("\n🧳 TRIP MANAGEMENT:");
            System.out.println("  list                - List all your saved trips");
            System.out.println("  add_trip n# d#      - Add a new trip to your collection");
            System.out.println("                         n# - Trip name (required)");
            System.out.println("                         d# - Trip description (optional)");
            System.out.println("                         Example: add_trip n#Paris Vacation d#Summer trip to France");
            System.out.println("  select <index>      - Select a trip to view and manage its photos");
            System.out.println("                         Example: select 2");
            System.out.println("  delete <index>      - Delete a trip and all its photos");
            System.out.println("                         Example: delete 3");
        } else if (fsm == 1) {
            // Photo management commands
            System.out.println("\n📸 PHOTO MANAGEMENT:");
            System.out.println("  list                - List all photos in the current trip");
            System.out.println("  add_photo f# n# c#  - Add a new photo to the current trip");
            System.out.println("                         f# - File path (required)");
            System.out.println("                         n# - Photo name (required)");
            System.out.println("                         c# - Photo caption (required)");
            System.out.println("Example: add_photo f#images/eiffel.jpg n#Eiffel Tower c#Evening view");
            System.out.println("  select <index>      - View a photo's details");
            System.out.println("                         Example: select 1");
            System.out.println("  delete <index>      - Remove a photo from the current trip");
            System.out.println("                         Example: delete 2");
        }

        System.out.println("\n💡 TIPS:");
        System.out.println("• Parameters marked with # must include the prefix (n#, d#, f#, etc.)");
        System.out.println("• Use quotation marks for values containing spaces: n#\"My Trip\"");
        System.out.println("• The application automatically extracts date, time, and location from photos if possible");

        if (fsm == 0) {
            System.out.println("  • Select a trip to add and manage photos within that trip");
        } else if (fsm == 1) {
            System.out.println("  • Use 'menu' to return to trip management");
        }

        System.out.println("==========================================");
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
