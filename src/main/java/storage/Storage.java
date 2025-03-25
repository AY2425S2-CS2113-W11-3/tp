package storage;

import trip.Trip;
import trip.TripManager;
import photo.Photo;
import exception.TravelDiaryException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import album.Album;

public class Storage {
    private static final String FILE_PATH = "./data/travel_diary.txt";
    private static final String TRIP_MARKER = "T";
    private static final String PHOTO_MARKER = "P";
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void saveTasks(List<Trip> trips) {
        File dataFile = new File(FILE_PATH);
        dataFile.getParentFile().mkdirs(); // Ensure directory exists

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Trip trip : trips) {
                writer.write(formatTripLine(trip));
                writer.newLine();

                if (trip.album != null) {
                    writer.write("A | " + encodeString(trip.name));
                    writer.newLine();
                    System.out.println("Saving album for trip: " + trip.name);  // Log album saving

                    if (trip.album.photos != null) {
                        for (Photo photo : trip.album.photos) {
                            writer.write(formatPhotoLine(photo));
                            writer.newLine();
                            System.out.println("Saving photo: " + photo.getPhotoName());  // Log each photo saving
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


    /**
     * Formats a trip into a line for storage
     */
    private static String formatTripLine(Trip trip) {
        return TRIP_MARKER + " | " +
                encodeString(trip.name) + " | " +
                encodeString(trip.description) + " | " +
                encodeString(trip.location);
    }

    /**
     * Formats a photo into a line for storage
     */
    private static String formatPhotoLine(Photo photo) {
        String dateTimeString = "";
        if (photo.getDatetime() != null) {
            dateTimeString = photo.getDatetime().format(DATETIME_FORMAT);
        }

        return PHOTO_MARKER + " | " +
                encodeString(photo.getFilePath()) + " | " +
                encodeString(photo.getPhotoName()) + " | " +
                encodeString(photo.getCaption()) + " | " +
                dateTimeString;
    }

    public static List<Trip> loadTrips(TripManager tripManager) {
        List<Trip> trips = new ArrayList<>();
        File dataFile = new File(FILE_PATH);

        if (!dataFile.exists()) {
            return trips;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            trips = processFileLines(reader, tripManager);
        } catch (IOException e) {
            System.out.println("Error reading trips from file: " + e.getMessage());
        }

        return trips;
    }

    private static List<Trip> processFileLines(BufferedReader reader, TripManager tripManager) throws IOException {
        List<Trip> trips = new ArrayList<>();
        String line;
        Trip currentTrip = null;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" \\| ");

            if (isNewTripMarker(parts)) {
                currentTrip = handleTripCreation(parts, tripManager, currentTrip, trips);
            } else if (isAlbumMarker(parts, currentTrip)) {
                handleAlbumMarker(parts, currentTrip);
            } else if (isPhotoMarker(parts, currentTrip)) {
                handlePhotoAddition(parts, currentTrip);
            }
        }

        // Add the last trip to the list
        if (currentTrip != null) {
            trips.add(currentTrip);
        }

        return trips;
    }

    private static boolean isNewTripMarker(String[] parts) {
        return parts[0].equals(TRIP_MARKER);
    }

    private static boolean isAlbumMarker(String[] parts, Trip currentTrip) {
        return parts[0].equals("A") && currentTrip != null;
    }

    private static boolean isPhotoMarker(String[] parts, Trip currentTrip) {
        return parts[0].equals(PHOTO_MARKER) && currentTrip != null && currentTrip.album != null;
    }

    private static Trip handleTripCreation(String[] parts, TripManager tripManager, Trip previousTrip, List<Trip> trips) {
        // Add previous trip to the list if it exists
        if (previousTrip != null) {
            trips.add(previousTrip);
        }

        try {
            String name = decodeString(parts[1]);
            String description = decodeString(parts[2]);
            String location = decodeString(parts[3]);

            // Use the existing addTrip method
            tripManager.addTrip(name, description, location);

            // Get the last added trip
            Trip currentTrip = tripManager.getTrips().get(tripManager.getTrips().size() - 1);

            // Ensure the trip has an album
            if (currentTrip.album == null) {
                currentTrip.album = new Album();
            }

            return currentTrip;
        } catch (TravelDiaryException e) {
            System.out.println("Error creating trip: " + e.getMessage());
            return null;
        }
    }

    private static void handleAlbumMarker(String[] parts, Trip currentTrip) {
        String albumName = decodeString(parts[1]);
        System.out.println("Loading album for trip: " + albumName);
    }

    private static void handlePhotoAddition(String[] parts, Trip currentTrip) {
        try {
            LocalDateTime photoTime = extractPhotoTime(parts);

            if (photoTime != null) {
                currentTrip.album.addPhoto(
                        decodeString(parts[1]),   // file path
                        decodeString(parts[2]),   // photo name
                        decodeString(parts[3]),   // caption
                        "",                       // location (seems unused)
                        photoTime
                );
            } else {
                currentTrip.album.addPhoto(
                        decodeString(parts[1]),   // file path
                        decodeString(parts[2]),   // photo name
                        decodeString(parts[3]),   // caption
                        ""                        // location (seems unused)
                );
            }
        } catch (TravelDiaryException e) {
            System.out.println("Error adding photo to album: " + e.getMessage());
        }
    }

    private static LocalDateTime extractPhotoTime(String[] parts) {
        return (parts.length > 4 && !parts[4].isEmpty())
                ? LocalDateTime.parse(parts[4], DATETIME_FORMAT)
                : null;
    }

    /**
     * Encodes a string to handle special characters.
     *
     * @param input String to encode
     * @return Encoded string
     */
    private static String encodeString(String input) {
        if (input == null) {
            return "";
        }

        String result = input;
        result = result.replace("|", "\\pipe");
        result = result.replace("\n", "\\newline");
        return result;
    }

    /**
     * Decodes a string to restore original characters.
     *
     * @param input String to decode
     * @return Decoded string
     */
    private static String decodeString(String input) {
        if (input == null) {
            return "";
        }

        String result = input;
        result = result.replace("\\pipe", "|");
        result = result.replace("\\newline", "\n");
        return result;
    }
}
