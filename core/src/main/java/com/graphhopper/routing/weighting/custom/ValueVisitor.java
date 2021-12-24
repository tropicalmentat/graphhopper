package com.graphhopper.routing.weighting.custom;

import com.graphhopper.routing.ev.DefaultEncodedValueFactory;
import com.graphhopper.routing.ev.EncodedValueLookup;
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
