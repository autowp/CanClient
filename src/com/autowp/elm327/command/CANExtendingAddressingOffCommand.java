package com.autowp.elm327.command;

public class CANExtendingAddressingOffCommand extends Command {

    public CANExtendingAddressingOffCommand()
    {
        this.name = "CEA";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
