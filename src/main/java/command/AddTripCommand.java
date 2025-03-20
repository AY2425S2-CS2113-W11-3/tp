package command;

import exception.MissingCompulsoryParameter;
import exception.TravelDiaryException;
import trip.TripManager;
import ui.Ui;
import java.util.logging.Logger;

public class AddTripCommand extends Command {
    private static final Logger logger = Logger.getLogger(AddTripCommand.class.getName());

    private String name;
    private String description;
    private String location;

    public AddTripCommand(String name, String description, String location) {
        logger.info("Creating AddTripCommand instance");

        this.name = name;
        this.description = description;
        this.location = location;

        // Assertions to ensure non-null parameters
        assert this.name != null : "Trip name should not be null";
        assert this.description != null : "Trip description should not be null";
        assert this.location != null : "Trip location should not be null";
    }

    @Override
    public void execute(TripManager tripManager, Ui ui, int fsmValue) throws
            TravelDiaryException, MissingCompulsoryParameter {
        logger.info("Executing AddTripCommand");

        if (tripManager == null) {
            logger.severe("TripManager is null");
            throw new TravelDiaryException("TripManager cannot be null");
        }
        if (ui == null) {
            logger.warning("UI instance is null");
        }

        tripManager.addTrip(this.name, this.description, this.location);
        this.fsmValue = fsmValue;

        logger.info("Trip successfully added: " + this.name);
    }
}
