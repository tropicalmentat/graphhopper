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
import com.graphhopper.routing.weighting.CurvatureWeighting;
import com.graphhopper.routing.weighting.PriorityWeighting;
import com.graphhopper.util.PMap;

import java.util.List;

import static com.graphhopper.routing.util.EncodingManager.getKey;

public class MotorcycleFlagEncoder extends CarFlagEncoder {
    private final DecimalEncodedValue priorityWayEncoder;
    private final DecimalEncodedValue curvatureEncoder;

    public MotorcycleFlagEncoder() {
        this(new PMap());
    }

    public MotorcycleFlagEncoder(PMap properties) {
        super("motorcycle", properties.putObject("speed_two_directions", true));
        priorityWayEncoder = new DecimalEncodedValueImpl(getKey("motorcycle", "priority"), 4, PriorityCode.getFactor(1), false);
        curvatureEncoder = new DecimalEncodedValueImpl(getKey("motorcycle", "curvature"), 4, 0.1, false);
    }

    @Override
    public TransportationMode getTransportationMode() {
        return TransportationMode.MOTORCYCLE;
    }

    @Override
    public boolean supports(Class<?> feature) {
        if (super.supports(feature))
            return true;

        if (CurvatureWeighting.class.isAssignableFrom(feature)) {
            return true;
        }

        return PriorityWeighting.class.isAssignableFrom(feature);
    }

    /**
     * Define the place of the speedBits in the edge flags for car.
     */
    @Override
    public void createEncodedValues(List<EncodedValue> encodedValues) {
        super.createEncodedValues(encodedValues);
        encodedValues.add(priorityWayEncoder);
        encodedValues.add(curvatureEncoder);
    }

}
