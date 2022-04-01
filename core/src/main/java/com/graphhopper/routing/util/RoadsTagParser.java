package com.graphhopper.routing.util;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.*;
import com.graphhopper.storage.IntsRef;

public class RoadsTagParser extends VehicleTagParser {

    public RoadsTagParser(EncodedValueLookup lookup) {
        this(
                lookup.getBooleanEncodedValue(AccessEV.key("roads")),
                lookup.getDecimalEncodedValue(AverageSpeed.key("roads")),
                lookup.getDecimalEncodedValue(TurnCost.key("roads"))
        );
    }

    public RoadsTagParser(BooleanEncodedValue accessEnc, DecimalEncodedValue speedEnc, DecimalEncodedValue turnCostEnc) {
        super(accessEnc, speedEnc, "roads", null, turnCostEnc, TransportationMode.VEHICLE, speedEnc.getNextStorableValue(254));
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

}
