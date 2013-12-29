package com.autowp.elm327.command;

public class EchoOnCommand extends Command {

    public EchoOnCommand()
    {
        this.name = "E1";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
