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

public abstract class AbstractFlagEncoder implements FlagEncoder {
    private final String name;
    protected final int speedBits;
    protected final double speedFactor;
    protected final BooleanEncodedValue accessEnc;
    protected final DecimalEncodedValue avgSpeedEnc;
    private final DecimalEncodedValue turnCostEnc;
    // This value determines the maximal possible speed of any road regardless of the maxspeed value
    // lower values allow more compact representation of the routing graph
    protected double maxPossibleSpeed;
    protected EncodedValueLookup encodedValueLookup;

    protected AbstractFlagEncoder(String name, int speedBits, double speedFactor, boolean speedTwoDirections, int maxTurnCosts) {
        this.name = name;
        this.speedBits = speedBits;
        this.speedFactor = speedFactor;

        this.accessEnc = AccessEV.create(name);
        this.avgSpeedEnc = AverageSpeed.create(name, speedBits, speedFactor, speedTwoDirections);
        this.turnCostEnc = maxTurnCosts > 0 ? TurnCost.create(name, maxTurnCosts) : null;
    }

    @Override
    public boolean isRegistered() {
        return encodedValueLookup != null;
    }

    public void createEncodedValues(List<EncodedValue> registerNewEncodedValue) {
        registerNewEncodedValue.add(accessEnc);
        registerNewEncodedValue.add(avgSpeedEnc);
    }

    public void createTurnCostEncodedValues(List<EncodedValue> registerNewTurnCostEncodedValues) {
        if (supportsTurnCosts())
            registerNewTurnCostEncodedValues.add(turnCostEnc);
    }

    @Override
    public double getMaxSpeed() {
        return maxPossibleSpeed;
    }

    public final DecimalEncodedValue getAverageSpeedEnc() {
        if (!isRegistered())
            throw new NullPointerException("FlagEncoder " + getName() + " not yet initialized");
        return avgSpeedEnc;
    }

    public final BooleanEncodedValue getAccessEnc() {
        if (!isRegistered())
            throw new NullPointerException("FlagEncoder " + getName() + " not yet initialized");
        return accessEnc;
    }

    protected String getPropertiesString() {
        return "speed_factor=" + speedFactor + "|speed_bits=" + speedBits + "|turn_costs=" + supportsTurnCosts();
    }

    @Override
    public List<EncodedValue> getEncodedValues() {
        return encodedValueLookup.getEncodedValues();
    }

    @Override
    public <T extends EncodedValue> T getEncodedValue(String key, Class<T> encodedValueType) {
        return encodedValueLookup.getEncodedValue(key, encodedValueType);
    }

    @Override
    public BooleanEncodedValue getBooleanEncodedValue(String key) {
        return encodedValueLookup.getBooleanEncodedValue(key);
    }

    @Override
    public IntEncodedValue getIntEncodedValue(String key) {
        return encodedValueLookup.getIntEncodedValue(key);
    }

    @Override
    public DecimalEncodedValue getDecimalEncodedValue(String key) {
        return encodedValueLookup.getDecimalEncodedValue(key);
    }

    @Override
    public <T extends Enum<?>> EnumEncodedValue<T> getEnumEncodedValue(String key, Class<T> enumType) {
        return encodedValueLookup.getEnumEncodedValue(key, enumType);
    }

    @Override
    public StringEncodedValue getStringEncodedValue(String key) {
        return encodedValueLookup.getStringEncodedValue(key);
    }

    public void setEncodedValueLookup(EncodedValueLookup encodedValueLookup) {
        this.encodedValueLookup = encodedValueLookup;
    }

    @Override
    public boolean supportsTurnCosts() {
        return turnCostEnc != null;
    }

    public DecimalEncodedValue getTurnCostEnc() {
        return turnCostEnc;
    }

    @Override
    public boolean supports(Class<?> feature) {
        return false;
    }

    @Override
    public boolean hasEncodedValue(String key) {
        return encodedValueLookup.hasEncodedValue(key);
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
