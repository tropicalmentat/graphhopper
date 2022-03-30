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

import com.graphhopper.util.PMap;

public class CarFlagEncoder extends AbstractFlagEncoder {
    public CarFlagEncoder() {
        this(new PMap());
    }

    public CarFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
        this("car", speedBits, speedFactor, maxTurnCosts);
    }

    public CarFlagEncoder(String name, int speedBits, double speedFactor, int maxTurnCosts) {
        this(name, speedBits, speedFactor, maxTurnCosts, false);
    }

    public CarFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        this("car", speedBits, speedFactor, maxTurnCosts, speedTwoDirections);
    }

    public CarFlagEncoder(String name, int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        this(new PMap().putObject("name", name).putObject("speed_bits", speedBits).putObject("speed_factor", speedFactor).
                putObject("max_turn_costs", maxTurnCosts).putObject("speed_two_directions", speedTwoDirections));
    }

    public CarFlagEncoder(PMap properties) {
        super(properties.getString("name", "car"),
                properties.getInt("speed_bits", 5),
                properties.getDouble("speed_factor", 5),
                properties.getBool("speed_two_directions", false),
                properties.getInt("max_turn_costs", properties.getBool("turn_costs", false) ? 1 : 0));
        maxPossibleSpeed = avgSpeedEnc.getNextStorableValue(properties.getDouble("max_speed", 140));
    }

    @Override
    public TransportationMode getTransportationMode() {
        return TransportationMode.CAR;
    }
}
