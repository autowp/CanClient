package com.autowp.elm327;

import java.util.EventObject;

import com.autowp.elm327.response.Response;

@SuppressWarnings("serial")
public class ResponseReceivedEvent extends EventObject {
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
