package com.autowp.peugeot;

import com.autowp.canclient.CanBusSpecs;

public class CanComfortSpecs extends CanBusSpecs {
    public CanComfortSpecs()
    {
        this.speed = CanComfort.SPEED;
        this.multiframeAbitrationID = new int[] {
            CanComfort.ID_TRACK_LIST
        };
    }
}
