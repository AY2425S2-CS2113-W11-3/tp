package storage;

import com.drew.imaging.ImageProcessingException;
import trip.Trip;
import trip.TripManager;
import photo.Photo;
import exception.FileReadException;
import exception.FileFormatException;
import exception.FileWriteException;
import exception.PhotoLoadException;
import exception.PhotoSaveException;
import exception.TripLoadException;
import exception.TravelDiaryException;
import album.Album;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final String TRIP_MARKER = "T";
    private static final String PHOTO_MARKER = "P";
    private static final String ALBUM_MARKER = "A";
    private static final String DELIMITER = " | ";
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Saves trips to a file
     */
    public static void saveTasks(List<Trip> trips, String filePath) throws FileWriteException {
        File dataFile = new File(filePath);
        // Create parent directory if it doesn't exist
        if (dataFile.getParentFile() != null) {
            dataFile.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Trip trip : trips) {
                writeTrip(writer, trip, filePath);
            }
        } catch (IOException e) {
            throw new FileWriteException(filePath, e);
        }
    }

    /**
     * Writes a single trip and its photos to the file
     */
    private static void writeTrip(BufferedWriter writer, Trip trip, String filePath)
            throws IOException, PhotoSaveException {
        // Write trip data
        writer.write(formatTripLine(trip));
        writer.newLine();

        // Skip if no album
        if (trip.album == null) {
            return;
        }

        // Write album marker with proper escaping for trip name
        writer.write(ALBUM_MARKER + DELIMITER + encodeString(trip.name));
        writer.newLine();

        // Skip if no photos
        if (trip.album.photos == null || trip.album.photos.isEmpty()) {
            return;
        }

        // Write each photo
        for (Photo photo : trip.album.photos) {
            try {
                writer.write(formatPhotoLine(photo));
                writer.newLine();
            } catch (Exception e) {
                throw new PhotoSaveException(photo.getPhotoName(), filePath, e);
            }
        }
    }

    /**
     * Formats a trip into a line for storage
     */
    private static String formatTripLine(Trip trip) {
        return TRIP_MARKER + DELIMITER +
                encodeString(trip.name) + DELIMITER +
                encodeString(trip.description == null ? "" : trip.description);
    }

    /**
     * Formats a photo into a line for storage
     */
    private static String formatPhotoLine(Photo photo) {
        String dateTimeString = "";
        if (photo.getDatetime() != null) {
            dateTimeString = photo.getDatetime().format(DATETIME_FORMAT);
        }

        return PHOTO_MARKER + DELIMITER +
                encodeString(photo.getFilePath()) + DELIMITER +
                encodeString(photo.getPhotoName()) + DELIMITER +
                encodeString(photo.getCaption() == null ? "" : photo.getCaption()) + DELIMITER +
                dateTimeString + DELIMITER +
                encodeString(photo.getLocation() == null ? "" : photo.getLocation()) + DELIMITER +
                photo.getLatitude() + DELIMITER +
                photo.getLongitude();
    }

    /**
     * Loads trips from a file
     */
    public static List<Trip> loadTrips(TripManager tripManager, String filePath)
            throws FileReadException, FileFormatException {
        List<Trip> trips = new ArrayList<>();
        File dataFile = new File(filePath);

        if (!dataFile.exists()) {
            return trips;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            return processFileLines(reader, tripManager, filePath);
        } catch (IOException e) {
            throw new FileReadException(filePath, e);
        }
    }

    /**
     * Process all lines in the file
     */
    private static List<Trip> processFileLines(BufferedReader reader, TripManager tripManager, String filePath)
            throws IOException, FileFormatException {
        List<Trip> trips = new ArrayList<>();
        String line;
        Trip currentTrip = null;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            try {
                // Improved splitting logic to handle DELIMITER correctly
                String[] parts = splitByDelimiter(line, DELIMITER);

                if (parts.length == 0) {
                    throw new FileFormatException(filePath, line);
                }

                String marker = parts[0];
                switch (marker) {
                case TRIP_MARKER:
                    // Add previous trip to list before creating a new one
                    if (currentTrip != null) {
                        trips.add(currentTrip);
                    }
                    currentTrip = createTrip(parts, tripManager, filePath);
                    break;
                case ALBUM_MARKER:
                    if (currentTrip == null) {
                        throw new FileFormatException(filePath, "Album marker found without a trip");
                    }
                    validateAlbumMarker(parts, filePath);
                    break;
                case PHOTO_MARKER:
                    if (currentTrip == null || currentTrip.album == null) {
                        throw new FileFormatException(filePath, "Photo marker found without a trip or album");
                    }
                    addPhotoToTrip(parts, currentTrip, filePath, lineNumber);
                    break;
                default:
                    throw new FileFormatException(filePath, "Unknown marker: " + marker);
                }
            } catch (TripLoadException | PhotoLoadException e) {
                // Propagate these exceptions without wrapping
                throw e;
            } catch (Exception e) {
                // Wrap any other exceptions
                if (!(e instanceof FileFormatException)) {
                    throw new FileFormatException(filePath, lineNumber, e);
                } else {
                    throw e;
                }
            }
        }

        // Add the last trip to the list
        if (currentTrip != null) {
            trips.add(currentTrip);
        }

        return trips;
    }

    /**
     * Splits a string by delimiter, respecting escaped delimiters
     */
    private static String[] splitByDelimiter(String line, String delimiter) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (escaped) {
                currentPart.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            // Check if we're at the start of a delimiter
            if (i <= line.length() - delimiter.length() &&
                    line.substring(i, i + delimiter.length()).equals(delimiter)) {
                parts.add(currentPart.toString());
                currentPart = new StringBuilder();
                i += delimiter.length() - 1; // Skip the delimiter
                continue;
            }

            currentPart.append(c);
        }

        // Add the last part
        parts.add(currentPart.toString());

        return parts.toArray(new String[0]);
    }

    /**
     * Creates a trip from a line
     */
    private static Trip createTrip(String[] parts, TripManager tripManager, String filePath)
            throws TripLoadException, FileFormatException {
        if (parts.length < 3) {
            throw new FileFormatException(filePath, String.join(DELIMITER, parts));
        }

        try {
            String name = decodeString(parts[1]);
            String description = decodeString(parts[2]);

            // Use the existing addTrip method
            tripManager.addTrip(name, description);

            // Get the last added trip
            Trip currentTrip = tripManager.getTrips().get(tripManager.getTrips().size() - 1);

            // Ensure the trip has an album
            if (currentTrip.album == null) {
                currentTrip.album = new Album();
            }

            return currentTrip;
        } catch (TravelDiaryException e) {
            throw new TripLoadException(parts.length > 1 ? decodeString(parts[1]) : "unknown", e);
        }
    }

    /**
     * Validates album marker format
     */
    private static void validateAlbumMarker(String[] parts, String filePath) throws FileFormatException {
        if (parts.length <= 1) {
            throw new FileFormatException(filePath, String.join(DELIMITER, parts));
        }
        // Currently, the album name (parts[1]) is not used further
    }

    /**
     * Adds a photo to a trip from a line
     */
    private static void addPhotoToTrip(String[] parts, Trip currentTrip, String filePath, int lineNumber)
            throws PhotoLoadException, FileFormatException {
        if (parts.length < 4) {
            throw new FileFormatException(filePath, String.join(DELIMITER, parts));
        }

        try {
            String photoPath = decodeString(parts[1]);
            String photoName = decodeString(parts[2]);
            String caption = decodeString(parts[3]);

            // Extract photo timestamp and coordinates
            LocalDateTime photoTime = extractPhotoTime(parts);

            // Create the photo with all available data
            if (photoTime != null) {
                currentTrip.album.addPhoto(photoPath, photoName, caption, photoTime);
            } else {
                currentTrip.album.addPhoto(photoPath, photoName, caption);
            }

            // Set location data if available in the file
            if (parts.length > 5) {
                // Update location if it exists in the file
                String location = decodeString(parts[5]);
                if (location != null && !location.isEmpty()) {
                    // Note: We can't directly set the location as it's a private field
                    // This would require modifying the Photo class to support this
                    // For now, we rely on the PhotoMetadataExtractor in the Photo constructor
                }

                // Update coordinates if they exist in the file
                if (parts.length > 7) {
                    try {
                        double latitude = Double.parseDouble(parts[6]);
                        double longitude = Double.parseDouble(parts[7]);
                        // Note: Similarly, we can't directly set these values
                        // This would require modifying the Photo class to support this
                    } catch (NumberFormatException e) {
                        // Ignore parsing errors for coordinates
                    }
                }
            }

        } catch (DateTimeParseException e) {
            throw new FileFormatException(filePath, lineNumber, e);
        } catch (TravelDiaryException | ImageProcessingException | IOException e) {
            throw new PhotoLoadException(
                    parts.length > 2 ? decodeString(parts[2]) : "unknown",
                    parts.length > 1 ? decodeString(parts[1]) : "unknown",
                    e
            );
        }
    }

    /**
     * Extracts photo timestamp from parts
     */
    private static LocalDateTime extractPhotoTime(String[] parts) throws DateTimeParseException {
        if (parts.length <= 4 || parts[4] == null || parts[4].isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(parts[4], DATETIME_FORMAT);
    }

    /**
     * Encodes a string to handle special characters.
     */
    private static String encodeString(String input) {
        if (input == null) {
            return "";
        }
        String result = input;
        // First escape backslashes to avoid double escaping
        result = result.replace("\\", "\\\\");
        // Then escape the delimiter characters
        result = result.replace("|", "\\|");
        result = result.replace("\n", "\\n");
        return result;
    }

    /**
     * Decodes a string to restore original characters.
     */
    private static String decodeString(String input) {
        if (input == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escaped) {
                if (c == '|') {
                    result.append('|');
                } else if (c == 'n') {
                    result.append('\n');
                } else if (c == '\\') {
                    result.append('\\');
                } else {
                    // If not a special character, keep the backslash and the character
                    result.append('\\').append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }

        // If there's a trailing backslash, add it
        if (escaped) {
            result.append('\\');
        }

        return result.toString();
    }
}
