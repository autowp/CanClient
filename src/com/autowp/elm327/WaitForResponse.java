package com.autowp.elm327;

import com.autowp.elm327.response.Response;

public abstract class WaitForResponse {
    abstract public boolean match(Response response);
    
    abstract public void execute(Response response) throws Elm327Exception;
}
