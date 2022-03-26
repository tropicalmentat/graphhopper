package com.graphhopper.routing.util;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.*;
import com.graphhopper.storage.IntsRef;

import static com.graphhopper.routing.util.EncodingManager.getKey;

public class RoadsTagParser extends VehicleTagParser {

    public RoadsTagParser() {
        this(
                new SimpleBooleanEncodedValue(getKey("roads", "access")),
                new DecimalEncodedValueImpl(getKey("roads", "average_speed"), 7, 2, true),
                TurnCost.create("roads", 3)
        );
    }

    public RoadsTagParser(EncodedValueLookup lookup) {
        this(
                lookup.getBooleanEncodedValue(EncodingManager.getKey("roads", "access")),
                lookup.getDecimalEncodedValue(EncodingManager.getKey("roads", "average_speed")),
                lookup.getDecimalEncodedValue(TurnCost.key("roads"))
        );
    }

    public RoadsTagParser(BooleanEncodedValue accessEnc, DecimalEncodedValue speedEnc, DecimalEncodedValue turnCostEnc) {
        super(accessEnc, speedEnc, "roads", 7, 2, turnCostEnc);
        maxPossibleSpeed = avgSpeedEnc.getNextStorableValue(254);
    }

    @Override
    public IntsRef handleWayTags(IntsRef edgeFlags, ReaderWay way) {
        // let's make it high and let it be reduced in the custom model
        double speed = maxPossibleSpeed;
        accessEnc.setBool(true, edgeFlags, true);
        accessEnc.setBool(false, edgeFlags, true);
        setSpeed(false, edgeFlags, speed);
        if (avgSpeedEnc.isStoreTwoDirections())
            setSpeed(true, edgeFlags, speed);
        return edgeFlags;
    }

    @Override
    public EncodingManager.Access getAccess(ReaderWay way) {
        if (way.getTag("highway", "").isEmpty())
            return EncodingManager.Access.CAN_SKIP;
        return EncodingManager.Access.WAY;
    }

    @Override
    public TransportationMode getTransportationMode() {
        return TransportationMode.VEHICLE;
    }

}
