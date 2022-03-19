package com.graphhopper.routing.util;

import com.graphhopper.routing.ev.*;

import java.util.List;

public class RoadsFlagEncoder extends BaseDummyFlagEncoder {
    private final BooleanEncodedValue accessEnc;
    private final DecimalEncodedValue avgSpeedEnc;
    private final DecimalEncodedValue turnCostEnc;

    public RoadsFlagEncoder() {
        accessEnc = new SimpleBooleanEncodedValue(EncodingManager.getKey("roads", "access"), true);
        avgSpeedEnc = new DecimalEncodedValueImpl(EncodingManager.getKey("roads", "average_speed"), 7, 2, true);
        final int maxTurnCosts = 3;
        turnCostEnc = TurnCost.create("roads", maxTurnCosts);
    }

    @Override
    public TransportationMode getTransportationMode() {
        return TransportationMode.VEHICLE;
    }

    @Override
    public double getMaxSpeed() {
        return avgSpeedEnc.getMaxDecimal();
    }

    @Override
    public BooleanEncodedValue getAccessEnc() {
        return accessEnc;
    }

    @Override
    public DecimalEncodedValue getAverageSpeedEnc() {
        return avgSpeedEnc;
    }

    @Override
    public boolean supportsTurnCosts() {
        return true;
    }

    @Override
    public void createEncodedValues(List<EncodedValue> encodedValues) {
        encodedValues.add(accessEnc);
        encodedValues.add(avgSpeedEnc);
    }

    @Override
    public void createTurnCostEncodedValues(List<EncodedValue> turnCostEncodedValues) {
        turnCostEncodedValues.add(turnCostEnc);
    }

    @Override
    public String getName() {
        return "roads";
    }

    @Override
    public String toString() {
        return getName();
    }
}
