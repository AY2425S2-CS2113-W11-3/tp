package command;

import exception.MissingCompulsoryParameter;
import exception.TravelDiaryException;
import trip.TripManager;
import ui.Ui;

public class AddPhotoCommand extends Command {
    private String filepath;
    private String photoname;
    private String caption;
    private String location;

    public AddPhotoCommand(String filepath, String photoname, String caption, String location) {
        this.filepath = filepath;
        this.photoname = photoname;
        this.caption = caption;
        this.location = location;
    }

    @Override
    public void execute(TripManager tripManager, Ui ui, int fsmValue) throws
            TravelDiaryException, MissingCompulsoryParameter {
        tripManager.getSelectedTrip().album.addPhoto(this.filepath, this.photoname, this.caption, this.location);
        this.fsmValue =fsmValue;
    }


}
