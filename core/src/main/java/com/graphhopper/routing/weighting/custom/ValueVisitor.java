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
package com.graphhopper.routing.weighting.custom;

import com.graphhopper.routing.ev.DefaultEncodedValueFactory;
import org.codehaus.janino.Java;
import org.codehaus.janino.Visitor;

public class ValueVisitor implements Visitor.AtomVisitor<Boolean, Exception> {
    private final DefaultEncodedValueFactory factory = new DefaultEncodedValueFactory();
    private final ExpressionParser.ParseResult result;
    private final ExpressionParser.NameValidator nameValidator;
    String invalidMessage;

    ValueVisitor(ExpressionParser.ParseResult result, ExpressionParser.NameValidator nameValidator) {
        this.result = result;
        this.nameValidator = nameValidator;
    }

    boolean isValidIdentifier(String identifier) {
        if (nameValidator.isValid(identifier)) {
            if (!Character.isUpperCase(identifier.charAt(0)))
                result.guessedVariables.add(identifier);
            return true;
        }
        return false;
    }

    @Override
    public Boolean visitRvalue(Java.Rvalue rv) throws Exception {
        if (rv instanceof Java.AmbiguousName) {
            Java.AmbiguousName n = (Java.AmbiguousName) rv;
            if (n.identifiers.length == 1) {
                String arg = n.identifiers[0];
                // e.g. like road_class
                if (isValidIdentifier(arg)) return true;
                try {
                    factory.create(arg);
                    invalidMessage = "encoded value '" + arg + "' not available";
                    return false;
                } catch (Exception ex) {
                }
            }
            invalidMessage = "identifier " + n + " invalid";
        } else if (rv instanceof Java.FloatingPointLiteral || rv instanceof Java.IntegerLiteral) {
            return true;
        } else if (rv instanceof Java.BinaryOperation) {
            Java.BinaryOperation binOp = (Java.BinaryOperation) rv;
            return binOp.lhs.accept(this) && binOp.rhs.accept(this);
        }
        return false;
    }

    @Override
    public Boolean visitPackage(Java.Package p) {
        return false;
    }

    @Override
    public Boolean visitType(Java.Type t) {
        return false;
    }

    @Override
    public Boolean visitConstructorInvocation(Java.ConstructorInvocation ci) {
        return false;
    }
}
