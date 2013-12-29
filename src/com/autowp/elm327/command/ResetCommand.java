package com.autowp.elm327.command;

public class ResetCommand extends Command {

    public ResetCommand()
    {
        this.name = "Z";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
