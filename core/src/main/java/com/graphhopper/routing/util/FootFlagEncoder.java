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

import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.DecimalEncodedValueImpl;
import com.graphhopper.routing.ev.EncodedValue;
import com.graphhopper.routing.weighting.PriorityWeighting;
import com.graphhopper.util.PMap;

import java.util.List;

import static com.graphhopper.routing.util.EncodingManager.getKey;
import static com.graphhopper.routing.util.FootTagParser.FERRY_SPEED;

public class FootFlagEncoder extends AbstractFlagEncoder {
    protected final DecimalEncodedValue priorityWayEncoder;

    public FootFlagEncoder() {
        this(4, 1, false);
    }

    public FootFlagEncoder(PMap properties) {
        this(properties.getString("name", "foot"),
                properties.getInt("speed_bits", 4),
                properties.getDouble("speed_factor", 1),
                properties.getBool("speed_two_directions", false));
    }

    protected FootFlagEncoder(int speedBits, double speedFactor, boolean speedTwoDirections) {
        this("foot", speedBits, speedFactor, speedTwoDirections);
    }

    protected FootFlagEncoder(String name, int speedBits, double speedFactor, boolean speedTwoDirections) {
        super(name, speedBits, speedFactor, speedTwoDirections, 0);
        priorityWayEncoder = new DecimalEncodedValueImpl(getKey(name, "priority"), 4, PriorityCode.getFactor(1), false);
        maxPossibleSpeed = avgSpeedEnc.getNextStorableValue(FERRY_SPEED);
    }

    @Override
    public boolean isMotorVehicle() {
        return false;
    }

    @Override
    public void createEncodedValues(List<EncodedValue> registerNewEncodedValue) {
        super.createEncodedValues(registerNewEncodedValue);
        registerNewEncodedValue.add(priorityWayEncoder);
    }

    @Override
    public boolean supports(Class<?> feature) {
        if (super.supports(feature))
            return true;

        return PriorityWeighting.class.isAssignableFrom(feature);
    }
}
