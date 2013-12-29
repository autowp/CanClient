package com.autowp.elm327.command;

public class MonitorAllCommand extends Command {

    public MonitorAllCommand()
    {
        this.name = "MA";
    }
    
    @Override
    public String toString() {
        return "AT " + this.name;
    }

}
