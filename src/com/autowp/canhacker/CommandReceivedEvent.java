package com.autowp.canhacker;

import java.util.EventObject;

public class CommandReceivedEvent extends EventObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected String command;

    public CommandReceivedEvent(Object source, String command) {
        super(source);
        this.command = command;
    }
    
    public String getCommand()
    {
        return command;
    }
}
