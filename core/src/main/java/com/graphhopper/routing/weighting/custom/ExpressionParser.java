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

import com.graphhopper.json.Statement;
import com.graphhopper.routing.ev.EncodedValueLookup;
import com.graphhopper.util.Helper;
import org.codehaus.janino.Java;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.TokenType;

import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class ExpressionParser {

    static void parseExpressions(StringBuilder expressions, NameValidator nameInConditionValidator, String exceptionInfo,
                                 Set<String> requiredDeclarations, List<Statement> list, EncodedValueLookup lookup, String lastStmt) {

        for (Statement statement : list) {
            ParseResult parseValueResult = parseValue(statement.getValue(), lookup::hasEncodedValue);
            if (!parseValueResult.ok)
                throw new IllegalArgumentException(exceptionInfo + " invalid value \"" + statement.getValue() + "\"" +
                        (parseValueResult.invalidMessage == null ? "" : ": " + parseValueResult.invalidMessage));
            requiredDeclarations.addAll(parseValueResult.guessedVariables);

            if (statement.getKeyword() == Statement.Keyword.ELSE) {
                if (!Helper.isEmpty(statement.getCondition()))
                    throw new IllegalArgumentException("expression must be empty but was " + statement.getCondition());
                expressions.append("else {" + statement.getOperation().build(statement.getValue()) + "; }\n");
            } else if (statement.getKeyword() == Statement.Keyword.ELSEIF || statement.getKeyword() == Statement.Keyword.IF) {
                ParseResult parseResult = parseCondition(statement.getCondition(), nameInConditionValidator, lookup);
                if (!parseResult.ok)
                    throw new IllegalArgumentException(exceptionInfo + " invalid expression \"" + statement.getCondition() + "\"" +
                            (parseResult.invalidMessage == null ? "" : ": " + parseResult.invalidMessage));
                requiredDeclarations.addAll(parseResult.guessedVariables);
                if (statement.getKeyword() == Statement.Keyword.ELSEIF)
                    expressions.append("else ");
                expressions.append("if (" + parseResult.converted + ") {" + statement.getOperation().build(statement.getValue()) + "; }\n");
            } else {
                throw new IllegalArgumentException("The statement must be either 'if', 'else_if' or 'else'");
            }
        }
        expressions.append(lastStmt);
    }

    /**
     * parseExpression is too powerful. So for this method only allow encoded values and a formula like max_speed * 0.9
     */
    static ParseResult parseValue(String value, NameValidator validator) {
        ParseResult result = new ParseResult();
        try {
            Parser parser = new Parser(new Scanner("ignore", new StringReader(value)));
            Java.Rvalue rvalue = parser.parseAssignmentExpression().toRvalueOrCompileException();
            // after parsing the expression the input should end (otherwise it is not "simple")
            if (parser.peek().type == TokenType.END_OF_INPUT) {
                result.guessedVariables = new LinkedHashSet<>();
                ValueVisitor visitor = new ValueVisitor(result, validator);
                result.ok = rvalue.accept(visitor);
                result.invalidMessage = visitor.invalidMessage;
            }
        } catch (Exception ex) {
        }
        return result;
    }

    /**
     * Enforce simple expressions of user input to increase security.
     *
     * @return ParseResult with ok if it is a valid and "simple" expression. It contains all guessed variables and a
     * converted expression that includes class names for constants to avoid conflicts e.g. when doing "toll == Toll.NO"
     * instead of "toll == NO".
     */
    static ParseResult parseCondition(String expression, NameValidator validator, EncodedValueLookup lookup) {
        ParseResult result = new ParseResult();
        try {
            Parser parser = new Parser(new Scanner("ignore", new StringReader(expression)));
            Java.Atom atom = parser.parseConditionalExpression();
            // after parsing the expression the input should end (otherwise it is not "simple")
            if (parser.peek().type == TokenType.END_OF_INPUT) {
                result.guessedVariables = new LinkedHashSet<>();
                ConditionVisitor visitor = new ConditionVisitor(result, validator, lookup);
                result.ok = atom.accept(visitor);
                result.invalidMessage = visitor.invalidMessage;
                if (result.ok) {
                    result.converted = new StringBuilder(expression.length());
                    int start = 0;
                    for (ConditionVisitor.Replacement replace : visitor.replacements.values()) {
                        result.converted.append(expression, start, replace.start).append(replace.newString);
                        start = replace.start + replace.oldLength;
                    }
                    result.converted.append(expression.substring(start));
                }
            }
        } catch (Exception ex) {
        }
        return result;
    }

    static class ParseResult {
        StringBuilder converted;
        boolean ok;
        String invalidMessage;
        Set<String> guessedVariables;
    }

    interface NameValidator {
        boolean isValid(String name);
    }
}
