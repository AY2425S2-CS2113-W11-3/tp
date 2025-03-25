package storage;

import trip.Trip;
import photo.Photo;
import exception.TravelDiaryException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Storage {
    private static final String FILE_PATH = "./data/travel_diary.trd";
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());
    private static final String TRIP_MARKER = "T:";
    private static final String PHOTO_MARKER = "P:";
    private static final String SECTION_DELIMITER = "===";
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static void saveTrips(List<Trip> trips) {
        // Create directory
        if (!createDataDirectory()) {
            return;
        }

        // Validate trips collection
        if (trips == null || trips.isEmpty()) {
            LOGGER.info("No trips to save");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Iterate through trips
            for (int i = 0; i < trips.size(); i++) {
                Trip trip = trips.get(i);

                // Skip null trips
                if (trip == null) {
                    continue;
                }

                // Write trip details
                writeTrip(writer, trip);

                // Write trip photos
                writePhotos(writer, trip);

                // Add section delimiter (except for last trip)
                if (i < trips.size() - 1) {
                    writer.write(SECTION_DELIMITER);
                    writer.newLine();
                }
            }

            LOGGER.info("Trips saved successfully");
        } catch (IOException saveError) {
            LOGGER.log(Level.SEVERE, "Trip save failed", saveError);
        }
    }

    private static boolean createDataDirectory() {
        try {
            Files.createDirectories(Paths.get(FILE_PATH).getParent());
            return true;
        } catch (IOException dirError) {
            LOGGER.log(Level.SEVERE, "Cannot create data directory", dirError);
            return false;
        }
    }

    private static void writeTrip(BufferedWriter writer, Trip trip) throws IOException {
        // Write trip header with compact encoding
        writer.write(TRIP_MARKER +
                encodeString(trip.name) + ";" +
                encodeString(trip.description) + ";" +
                encodeString(trip.location)
        );
        writer.newLine();
    }

    private static void writePhotos(BufferedWriter writer, Trip trip) throws IOException {
        // Validate album
        if (trip.album == null) {
            return;
        }

        // Manually iterate through photos
        for (int i = 0; i < trip.album.photos.size(); i++) {
            try {
                trip.album.selectPhoto(i);
                Photo selectedPhoto = trip.album.selectedPhoto;

                // Write photo details
                writer.write(PHOTO_MARKER +
                        encodeString(selectedPhoto.getFilePath()) + ";" +
                        encodeString(selectedPhoto.getPhotoName()) + ";" +
                        encodeString(selectedPhoto.getCaption()) + ";" +
                        encodeString(selectedPhoto.getLocation()) + ";" +
                        formatPhotoDateTime(selectedPhoto)
                );
                writer.newLine();
            } catch (TravelDiaryException photoError) {
                LOGGER.log(Level.WARNING, "Failed to write photo", photoError);
            }
        }
    }

    private static String formatPhotoDateTime(Photo photo) {
        // Handle null datetime
        if (photo.getDatetime() == null) {
            return "";
        }
        return photo.getDatetime().format(DATETIME_FORMAT);
    }

    public static List<Trip> loadTrips() {
        List<Trip> trips = new ArrayList<>();
        File file = new File(FILE_PATH);

        // Check if file exists
        if (!file.exists()) {
            LOGGER.warning("No travel diary file found");
            return trips;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            Trip currentTrip = null;
            String line;

            while ((line = reader.readLine()) != null) {
                // Process trip entries
                if (line.startsWith(TRIP_MARKER)) {
                    currentTrip = processTripEntry(line);
                    if (currentTrip != null) {
                        trips.add(currentTrip);
                    }
                } else if (line.startsWith(PHOTO_MARKER) && currentTrip != null) {
                    processPhotoEntry(currentTrip, line);
                }
            }

            LOGGER.info("Trips loaded successfully");
        } catch (IOException loadError) {
            LOGGER.log(Level.SEVERE, "Trip load failed", loadError);
        }

        return trips;
    }

    private static Trip processTripEntry(String line) {
        String[] tripParts = line.substring(TRIP_MARKER.length()).split(";");

        // Validate trip parts
        if (tripParts.length < 3) {
            LOGGER.warning("Malformed trip entry");
            return null;
        }

        try {
            return new Trip(
                    decodeString(tripParts[0]),
                    decodeString(tripParts[1]),
                    decodeString(tripParts[2])
            );
        } catch (Exception createError) {
            LOGGER.log(Level.WARNING, "Failed to create trip", createError);
            return null;
        }
    }

    private static void processPhotoEntry(Trip currentTrip, String line) {
        String[] photoParts = line.substring(PHOTO_MARKER.length()).split(";");

        // Validate photo parts
        if (photoParts.length < 5) {
            LOGGER.warning("Malformed photo entry");
            return;
        }

        LocalDateTime photoTime = parsePhotoDateTime(photoParts[4]);

        try {
            if (photoTime != null) {
                currentTrip.album.addPhoto(
                        decodeString(photoParts[0]),
                        decodeString(photoParts[1]),
                        decodeString(photoParts[2]),
                        decodeString(photoParts[3]),
                        photoTime
                );
            } else {
                currentTrip.album.addPhoto(
                        decodeString(photoParts[0]),
                        decodeString(photoParts[1]),
                        decodeString(photoParts[2]),
                        decodeString(photoParts[3])
                );
            }
        } catch (TravelDiaryException photoError) {
            LOGGER.log(Level.WARNING, "Photo addition failed", photoError);
        }
    }

    private static LocalDateTime parsePhotoDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeString, DATETIME_FORMAT);
        } catch (Exception timeError) {
            LOGGER.log(Level.WARNING, "Invalid photo datetime", timeError);
            return null;
        }
    }

    // Encoding method to handle special characters
    private static String encodeString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(";", "\\semicolon")
                .replace("=", "\\equals")
                .replace("\n", "\\newline");
    }

    // Decoding method to restore original string
    private static String decodeString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\semicolon", ";")
                .replace("\\equals", "=")
                .replace("\\newline", "\n");
    }
}
