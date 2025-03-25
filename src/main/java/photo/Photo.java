package photo;

import exception.TravelDiaryException;

import java.time.LocalDateTime;


/**
 * Photo class stores details of a photo.
 */
public class Photo {
    private String filePath;
    private String photoName;
    private String caption;
    private String location;
    private LocalDateTime datetime; // Using LocalDateTime for datetime

    public Photo(String filePath, String photoName, String caption, String location, LocalDateTime datetime)
            throws TravelDiaryException {
        if (filePath == null || photoName == null || caption == null || location == null) {
            throw new TravelDiaryException("Missing required tag(s) for add_photo. Required: f# (filename), n# " +
                    "(photoname), c# (caption), l# (location).");
        }
        this.filePath = filePath;
        this.photoName = photoName;
        this.caption = caption;
        this.location = location;
        this.datetime = datetime;
    }

    public Photo(String filePath, String photoName, String caption, String location) throws TravelDiaryException {
        this(filePath, photoName, caption, location, LocalDateTime.now());
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getPhotoName() {
        return this.photoName;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getLocation() {
        return this.location;
    }

    public LocalDateTime getDatetime() {
        return this.datetime;
    }

    @Override
    public String toString() {
        return photoName;
    }
}
