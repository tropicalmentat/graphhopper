package com.graphhopper.routing.util;

public class RoadsFlagEncoder extends AbstractFlagEncoder {

    public RoadsFlagEncoder() {
        super("roads", 7, 2, true, 3);
        maxPossibleSpeed = avgSpeedEnc.getNextStorableValue(254);
    }

    @Override
    public boolean isMotorVehicle() {
        return false;
    }

}
