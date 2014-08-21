package com.autowp.elm327;

public interface ResponseReceivedEventClassListener {
    public void handleResponseReceivedEventClassEvent(ResponseReceivedEvent e) throws Elm327Exception;
}
