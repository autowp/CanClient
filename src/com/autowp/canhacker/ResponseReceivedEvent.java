package com.autowp.canhacker;

import java.util.EventObject;

import com.autowp.canhacker.response.Response;

public class ResponseReceivedEvent extends EventObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
     
    protected Response command;

    public ResponseReceivedEvent(Object source, Response command) {
        super(source);
        this.command = command;
    }
    
    public Response getCommand()
    {
        return command;
    }
}
