package command;

import exception.TravelDiaryException;
import trip.TripManager;
import ui.Ui;

public class DeleteCommand extends Command {
    private int index;
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TripManager tripManager, Ui ui, int fsmValue) throws TravelDiaryException {
        if (fsmValue == 0){
            tripManager.deleteTrip(index);
        } else if (fsmValue == 1) {
            tripManager.getSelectedTrip().album.deletePhoto(index);
        }
        this.fsmValue =fsmValue;
    }
}
