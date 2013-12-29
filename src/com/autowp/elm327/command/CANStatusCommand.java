package com.autowp.elm327.command;

public class CANStatusCommand extends Command {

    public CANStatusCommand()
    {
        this.name = "CS";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
