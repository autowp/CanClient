package com.autowp.elm327.command;

public class CANAutomaticFormattingOffCommand extends Command {

    public CANAutomaticFormattingOffCommand()
    {
        this.name = "CAF0";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
