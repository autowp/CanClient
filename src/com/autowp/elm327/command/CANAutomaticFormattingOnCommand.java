package com.autowp.elm327.command;

public class CANAutomaticFormattingOnCommand extends Command {

    public CANAutomaticFormattingOnCommand()
    {
        this.name = "CAF1";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
