package com.autowp.elm327.command;

public class CanSilentMonitorCommand extends Command {
    protected boolean enabled;
    
    public CanSilentMonitorCommand(boolean enabled)
    {
        this.name = "CSM";
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "AT " + this.name + (this.enabled ? "1" : "0");
    }
}
