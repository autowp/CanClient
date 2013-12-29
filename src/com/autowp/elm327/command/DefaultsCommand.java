package com.autowp.elm327.command;

public class DefaultsCommand extends Command {

    public DefaultsCommand()
    {
        this.name = "D";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }
}
