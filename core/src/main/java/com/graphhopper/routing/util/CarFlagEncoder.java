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
import com.graphhopper.util.PMap;

import java.util.List;

public class CarFlagEncoder extends BaseDummyFlagEncoder {
    private final int maxTurnCosts;
    private final BooleanEncodedValue accessEnc;
    private final DecimalEncodedValue avgSpeedEnc;

    public CarFlagEncoder() {
        this(new PMap());
    }

    public CarFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
        this(speedBits, speedFactor, maxTurnCosts, false);
    }

    public CarFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        this(new PMap().putObject("speed_bits", speedBits).putObject("speed_factor", speedFactor).
                putObject("max_turn_costs", maxTurnCosts).putObject("speed_two_directions", speedTwoDirections));
    }

    public CarFlagEncoder(PMap properties) {
        boolean speedTwoDirections = properties.getBool("speed_two_directions", false);
        maxTurnCosts = properties.getInt("max_turn_costs", properties.getBool("turn_costs", false) ? 1 : 0);
        accessEnc = new SimpleBooleanEncodedValue(EncodingManager.getKey("car", "access"), true);
        int speedBits = properties.getInt("speed_bits", 5);
        double speedFactor = properties.getInt("speed_factor", 5);
        avgSpeedEnc = new DecimalEncodedValueImpl(EncodingManager.getKey("car", "average_speed"), speedBits, speedFactor, speedTwoDirections);
    }

    @Override
    public TransportationMode getTransportationMode() {
        throw new UnsupportedOperationException();
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
        return maxTurnCosts > 0;
    }

    @Override
    public void createEncodedValues(List<EncodedValue> encodedValues) {
        encodedValues.add(accessEnc);
        encodedValues.add(avgSpeedEnc);
    }

}
