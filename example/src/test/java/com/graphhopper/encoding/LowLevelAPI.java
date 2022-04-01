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

package com.graphhopper.encoding;

import com.graphhopper.reader.osm.OSMReader;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.OSMReaderConfig;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.ev.*;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.util.parsers.OSMMaxWeightParser;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.TurnCostStorage;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LowLevelAPI {
    @Disabled
    @Test
    void lowLevelManualGraphAndWeighting() {
        // define some encoded values
        DecimalEncodedValue maxSpeedEnc = MaxSpeed.create();
        BooleanEncodedValue accessEnc = AccessEV.create("my_name");
        DecimalEncodedValue speedEnc = AverageSpeed.create("my_name", 5, 3, true);
        IntEncodedValue delayEnc = new IntEncodedValueImpl("delay", 5, true);
        DecimalEncodedValue turnCostEnc = TurnCost.create("my_name", 60);

        EncodingManager em = new EncodingManager.Builder()
                .add(maxSpeedEnc)
                .add(accessEnc)
                .add(speedEnc)
                .add(delayEnc)
                // todonow: this should be possible, and maybe rename add -> addEncodedValue?
//                .addTurnCostEncodedValue(turnCostEnc)
                .build();

        BaseGraph graph = new BaseGraph.Builder(em.getIntsForFlags()).create();
        EdgeIteratorState edge1 = graph.edge(0, 1).setDistance(100)
                .set(maxSpeedEnc, 70)
                .set(accessEnc, true, true)
                .set(speedEnc, 50, 50)
                .set(delayEnc, 12);
        EdgeIteratorState edge2 = graph.edge(1, 2).setDistance(100)
                .set(maxSpeedEnc, 50)
                .set(accessEnc, true, true)
                .set(speedEnc, 30, 30)
                .set(delayEnc, 15);

        graph.getTurnCostStorage().set(turnCostEnc, 0, 1, 2, 40);

        MyWeighting weighting = new MyWeighting(maxSpeedEnc, accessEnc, speedEnc, delayEnc, turnCostEnc, graph.getTurnCostStorage());
        weighting.calcEdgeWeight(edge1, false);
        weighting.calcEdgeWeight(edge2, false);

        Dijkstra dijkstra = new Dijkstra(graph, weighting, TraversalMode.EDGE_BASED);
        Path path = dijkstra.calcPath(0, 2);
        assertEquals(200, path.getDistance());
        // todonow: add tests for time+weight
    }

    @Disabled
    @Test
    void lowLevelGraphWithParser() {
        // define some encoded values
        DecimalEncodedValue maxSpeedEnc = MaxSpeed.create();
        BooleanEncodedValue roundaboutEnc = Roundabout.create();
        BooleanEncodedValue accessEnc = AccessEV.create("my_name");
        DecimalEncodedValue speedEnc = AverageSpeed.create("my_name", 5, 3, true);
        IntEncodedValue delayEnc = new IntEncodedValueImpl("delay", 5, true);

        EncodingManager em = new EncodingManager.Builder()
                .add(maxSpeedEnc)
                .add(accessEnc)
                .add(speedEnc)
                .add(delayEnc)
                .build();

        TagParserBundle tagParserBundle = new TagParserBundle()
                .addWayTagParser(new OSMMaxWeightParser(maxSpeedEnc))
                // todonow: should be split into separate access, speed and turn cost parser
                .addWayTagParser(new CarTagParser(accessEnc, speedEnc, null, roundaboutEnc, new PMap("name=my_name"), TransportationMode.CAR, 100))
                .addWayTagParser((edgeFlags, way, relationFlags) -> {
                    delayEnc.setInt(false, edgeFlags, way.getTag("delay", 0));
                    // todonow: why do we have to return edge flags?
                    return edgeFlags;
                });

        BaseGraph graph = new BaseGraph.Builder(em.getIntsForFlags()).create();
        OSMReader reader = new OSMReader(graph, em, tagParserBundle, new OSMReaderConfig());
        reader.setFile(new File("core/files/andorra.osm.pbf"));
        try {
            // todonow: remove exception, it is not even thrown
            reader.readGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // todonow:
    }

    private static class MyWeighting implements Weighting {
        private final DecimalEncodedValue maxSpeedEnc;
        private final BooleanEncodedValue accessEnc;
        private final DecimalEncodedValue speedEnc;
        private final IntEncodedValue delayEnc;
        private final DecimalEncodedValue turnCostEnc;
        private final TurnCostStorage turnCostStorage;

        MyWeighting(DecimalEncodedValue maxSpeedEnc, BooleanEncodedValue accessEnc, DecimalEncodedValue speedEnc,
                    IntEncodedValue customEnc, DecimalEncodedValue turnCostEnc, TurnCostStorage turnCostStorage) {
            this.maxSpeedEnc = maxSpeedEnc;
            this.accessEnc = accessEnc;
            this.speedEnc = speedEnc;
            this.delayEnc = customEnc;
            this.turnCostEnc = turnCostEnc;
            this.turnCostStorage = turnCostStorage;
        }

        @Override
        public double getMinWeight(double distance) {
            // max speed is 100
            return 1000 * Math.floor(distance / 100);
        }

        @Override
        public double calcEdgeWeight(EdgeIteratorState edgeState, boolean reverse) {
            boolean access = reverse ? edgeState.getReverse(accessEnc) : edgeState.get(accessEnc);
            if (!access)
                return Double.POSITIVE_INFINITY;
            double weight = calcEdgeMillis(edgeState, reverse);
            // avoid roads with high max speed
            if (edgeState.get(maxSpeedEnc) > 80)
                weight *= 1.2;
            return weight;
        }

        @Override
        public long calcEdgeMillis(EdgeIteratorState edgeState, boolean reverse) {
            double speed = reverse ? edgeState.getReverse(speedEnc) : edgeState.get(speedEnc);
            double time = Math.round(edgeState.getDistance() / (speed / 3.6)) + edgeState.get(delayEnc);
            return 1000 * Math.round(time);
        }

        @Override
        public double calcTurnWeight(int inEdge, int viaNode, int outEdge) {
            return calcTurnMillis(inEdge, viaNode, outEdge);
        }

        @Override
        public long calcTurnMillis(int inEdge, int viaNode, int outEdge) {
            return 1000 * Math.round(turnCostStorage.get(turnCostEnc, inEdge, viaNode, outEdge));
        }

        @Override
        public boolean hasTurnCosts() {
            // todo: do we really need this method?
            return true;
        }

        @Override
        public FlagEncoder getFlagEncoder() {
            throw new UnsupportedOperationException("This method should not even exist");
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("This method should not even exist");
        }
    }


}
