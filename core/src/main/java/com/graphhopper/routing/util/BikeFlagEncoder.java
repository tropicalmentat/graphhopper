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

public class BikeFlagEncoder extends BikeCommonFlagEncoder {
    public BikeFlagEncoder() {
        this(4, 2, 0, false);
    }

    public BikeFlagEncoder(PMap properties) {
        this("bike", properties);
    }

    public BikeFlagEncoder(String name, PMap properties) {
        this(name, properties.getInt("speed_bits", 4),
                properties.getInt("speed_factor", 2),
                properties.getBool("turn_costs", false) ? 1 : 0,
                properties.getBool("speed_two_directions", false));
    }

    public BikeFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        super("bike", speedBits, speedFactor, maxTurnCosts, speedTwoDirections);
    }

    public BikeFlagEncoder(String name, int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        super(name, speedBits, speedFactor, maxTurnCosts, speedTwoDirections);
    }
}
