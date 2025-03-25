package trip;
import exception.TravelDiaryException;

import java.util.List;
import album.Album;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TripManager {
    private static final Logger logger = Logger.getLogger(TripManager.class.getName());
    private final List<Trip> trips = new ArrayList<>();
    private Trip selectedTrip = null;

    public void addTrip(String name, String description, String location) throws TravelDiaryException {
        logger.info("Adding a new trip: " + name);
        trips.add(new Trip(name, description, location));
        logger.info("Trip added successfully: " + name);
        System.out.println("Trip added successfully.");
    }

    public void setSelectedTrip(Trip selectedTrip) {
        logger.info("Setting selected trip: " + (selectedTrip != null ? selectedTrip.name : "null"));
        this.selectedTrip = selectedTrip;
    }

    public void deleteTrip(int index) {
        logger.info("Attempting to delete trip at index: " + index);
        if (index < 0 || index >= trips.size()) {
            logger.severe("Invalid trip index: " + index);
            System.out.println("Invalid trip index.");
            return;
        }
        logger.info("Trip deleted: " + trips.get(index).name);
        trips.remove(index);
        System.out.println("Trip deleted successfully.");
    }

    public void viewTrips() {
        logger.info("Viewing all trips.");
        if (trips.isEmpty()) {
            logger.warning("No trips available.");
            System.out.println("No trips available.");
        } else {
            for (int i = 0; i < trips.size(); i++) {
                System.out.println(i + ": " + trips.get(i)); // Display index with trip details
            }
        }
    }

    public void selectTrip(int index) throws TravelDiaryException {
        logger.info("Selecting trip at index: " + index);
        if (index < 0 || index >= trips.size()) {
            logger.severe("Invalid trip index: " + index);
            throw new TravelDiaryException("Invalid trip index.");
        }
        selectedTrip = trips.get(index);
        logger.info("Selected trip: " + selectedTrip.name);
        System.out.println("Selected trip: " + selectedTrip.name);
    }

    public Trip getSelectedTrip() {
        logger.info("Retrieving selected trip.");
        assert selectedTrip != null : "Selected trip should not be null";
        return this.selectedTrip;
    }

    public List<Trip> getTrips() {
        return trips;
    }
}
