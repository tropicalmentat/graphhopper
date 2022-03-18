/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing.util;

import com.graphhopper.routing.ev.*;

import java.util.List;

import static com.graphhopper.routing.util.EncodingManager.getKey;

abstract public class BikeCommonFlagEncoder extends BaseDummyFlagEncoder {
    private final int maxTurnCosts;
    protected final boolean speedTwoDirections;
    private final BooleanEncodedValue accessEnc;
    private final DecimalEncodedValue avgSpeedEnc;
    private final DecimalEncodedValue priorityEnc;

    protected BikeCommonFlagEncoder(String name, int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        this.speedTwoDirections = speedTwoDirections;
        this.maxTurnCosts = maxTurnCosts;
        accessEnc = new SimpleBooleanEncodedValue(getKey(name, "access"), true);
        avgSpeedEnc = new DecimalEncodedValueImpl(getKey(name, "average_speed"), speedBits, speedFactor, speedTwoDirections);
        priorityEnc = new DecimalEncodedValueImpl(getKey(name, "priority"), 4, PriorityCode.getFactor(1), false);
    }

    @Override
    public TransportationMode getTransportationMode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createEncodedValues(List<EncodedValue> encodedValues) {
        encodedValues.add(accessEnc);
        encodedValues.add(avgSpeedEnc);
        encodedValues.add(priorityEnc);
    }

    @Override
    public boolean supportsTurnCosts() {
        return maxTurnCosts > 0;
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
}
