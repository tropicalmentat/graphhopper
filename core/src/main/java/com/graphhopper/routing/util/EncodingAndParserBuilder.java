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

import com.graphhopper.routing.ev.EncodedValue;
import com.graphhopper.routing.ev.EncodedValueLookup;
import com.graphhopper.routing.util.parsers.RelationTagParser;
import com.graphhopper.routing.util.parsers.TagParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EncodingAndParserBuilder {
    private final List<FlagEncoder> flagEncoders;
    private final List<EncodedValue> encodedValues;
    private final List<Function<EncodedValueLookup, TagParser>> wayTagParserCreators;
    private final List<BiFunction<EncodedValueLookup, EncodedValue.InitializerConfig, RelationTagParser>> relationTagParserCreators;

    public EncodingAndParserBuilder() {
        flagEncoders = new ArrayList<>();
        encodedValues = new ArrayList<>();
        wayTagParserCreators = new ArrayList<>();
        relationTagParserCreators = new ArrayList<>();
    }

    public boolean hasFlagEncoder(String name) {
        return flagEncoders.stream().anyMatch(f -> f.toString().equals(name));
    }

    public void addFlagEncoder(FlagEncoder flagEncoder) {
        flagEncoders.add(flagEncoder);
    }

    public void addEncodedValue(EncodedValue encodedValue) {
        encodedValues.add(encodedValue);
    }

    public void addWayTagParser(Function<EncodedValueLookup, TagParser> tagParserCreator) {
        wayTagParserCreators.add(tagParserCreator);
    }

    public void addRelationTagParser(BiFunction<EncodedValueLookup, EncodedValue.InitializerConfig, RelationTagParser> tagParserCreator) {
        relationTagParserCreators.add(tagParserCreator);
    }

    public EncodingManager buildEncodingManager() {
        EncodingManager.Builder builder = new EncodingManager.Builder();
        flagEncoders.forEach(builder::add);
        encodedValues.forEach(builder::add);
        return builder.build();
    }

    public TagParserBundle buildTagParserBundle(EncodedValueLookup lookup) {
        TagParserBundle tagParserBundle = new TagParserBundle();
        relationTagParserCreators.forEach(t -> tagParserBundle.addRelationTagParser(relConf -> t.apply(lookup, relConf)));
        wayTagParserCreators.forEach(t -> tagParserBundle.addWayTagParser(t.apply(lookup)));
        return tagParserBundle;
    }
}
