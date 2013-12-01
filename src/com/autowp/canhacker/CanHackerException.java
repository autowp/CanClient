package com.autowp.canhacker;

import com.autowp.canclient.CanAdapterException;

@SuppressWarnings("serial")
public class CanHackerException extends CanAdapterException {
    public CanHackerException(String message)
    {
        super(message);
    }

}
