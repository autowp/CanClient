package com.autowp.canhacker;

import com.autowp.canhacker.command.Command;

public class CommandSendEvent extends java.util.EventObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Command command;

    public CommandSendEvent(Object source, Command command) {
        super(source);
        this.command = command;
    }
    
    public Command getCommand()
    {
        return command;
    }
}