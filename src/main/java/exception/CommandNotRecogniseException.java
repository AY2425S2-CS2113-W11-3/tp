package exception;
import ui.Ui;

public class CommandNotRecogniseException extends Exception{
    public CommandNotRecogniseException(String command){
        System.out.println(command + " is not recognise\n");
        System.out.println("here are the available commands:");
        Ui ui = new Ui();
        ui.showAvailableCommands();
    }
}
