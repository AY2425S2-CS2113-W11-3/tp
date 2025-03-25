package command;

import exception.TravelDiaryException;
import trip.TripManager;
import ui.Ui;

public class SelectCommand extends Command {
    private int index;

    public SelectCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TripManager tripManager, Ui ui, int fsmValue) throws TravelDiaryException {
        if (fsmValue == 0){
            tripManager.selectTrip(index);
        } else if (fsmValue == 1) {
            tripManager.getSelectedTrip().album.selectPhoto(index);
        }
        this.fsmValue = 1;

    }
}
