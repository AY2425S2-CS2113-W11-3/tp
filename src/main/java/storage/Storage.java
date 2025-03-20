package storage;

import trip.Trip;
import photo.Photo;
import exception.TravelDiaryException;
import java.io.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Storage {
    private static final String FILE_PATH = "./data/TravelDiary.txt";
    private static final Logger logger = Logger.getLogger(Storage.class.getName());

    public static void saveTrips(List<Trip> trips) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Trip trip : trips) {
                writer.write("Trip: " + trip.name + " | " + trip.description + " | " + trip.location);
                writer.newLine();
                for (Photo photo : trip.album.getPhotos()) {
                    writer.write("  Photo: " + photo.getFilePath() + " | " +
                            photo.getPhotoName() + " | " + photo.getCaption() + " | " +
                            photo.getLocation() + " | " + photo.getDatetime());
                    writer.newLine();
                }
                writer.write("---"); // Separator between trips
                writer.newLine();
            }
            logger.info("Trips saved successfully to file.");
        } catch (IOException e) {
            logger.severe("Error saving trips: " + e.getMessage());
        }
    }

    public static List<Trip> loadTrips() {
        List<Trip> trips = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            logger.warning("No existing data file found.");
            return trips;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            Trip currentTrip = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Trip:")) {
                    String[] parts = line.substring(6).split(" \\| ");
                    currentTrip = new Trip(parts[0], parts[1], parts[2]);
                    trips.add(currentTrip);
                } else if (line.startsWith("  Photo:") && currentTrip != null) {
                    String[] parts = line.substring(8).split(" \\| ");
                    // Convert the string to LocalDateTime
                    LocalDateTime datetime = LocalDateTime.parse(parts[4], formatter);
                    currentTrip.album.addPhoto(parts[0], parts[1], parts[2], parts[3], datetime);
                }
            }
            logger.info("Trips loaded successfully from file.");
        } catch (IOException | TravelDiaryException e) {
            logger.severe("Error loading trips: " + e.getMessage());
        }
        return trips;
    }
}
