package com.autowp.elm327.command;

public class EchoOffCommand extends Command {

    public EchoOffCommand()
    {
        this.name = "E0";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
