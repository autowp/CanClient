package com.autowp.elm327;

import com.autowp.elm327.command.Command;

@SuppressWarnings("serial")
public class CommandSendEvent extends java.util.EventObject {
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