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

package com.graphhopper.routing.ev;

import com.graphhopper.storage.IntsRef;
import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.DistanceCalcEarth;
import com.graphhopper.util.PointList;

public class Distance {
    public static final String KEY = "distance";

    public static GeometryEncodedValue create() {
        return new DistanceGeometryEncodedValue();
    }

    private static class DistanceGeometryEncodedValue extends UnsignedDecimalEncodedValue implements GeometryEncodedValue {
        private static final DistanceCalc distCalc = DistanceCalcEarth.DIST_EARTH;

        public DistanceGeometryEncodedValue() {
            // todonow: bits, factor etc.
            super(KEY, 32, 1, false);
        }

        @Override
        public double calculateDecimal(boolean reverse, IntsRef ref, PointList geometry) {
            return distCalc.calcDistance(geometry);
        }
    }
}