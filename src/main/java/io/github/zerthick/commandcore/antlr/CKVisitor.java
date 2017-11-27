package io.github.zerthick.commandcore.antlr;

import io.github.zerthick.commandskript.CommandSkriptBaseVisitor;
import io.github.zerthick.commandskript.CommandSkriptParser;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CKVisitor extends CommandSkriptBaseVisitor<CKValue> {

    private Scope scope;

    private CommandSource commandSource;
    private ConsoleSource consoleSource;
    private Logger logger;

    public CKVisitor(Scope scope, CommandSource commandSource, ConsoleSource consoleSource, Logger logger) {
        this.scope = scope;
        this.commandSource = commandSource;
        this.consoleSource = consoleSource;
        this.logger = logger;
    }

    // list: '[' exprList? ']'
    @Override
    public CKValue visitList(CommandSkriptParser.ListContext ctx) {
        List<CKValue> list = new ArrayList<>();
        if (ctx.exprList() != null) {
            for(CommandSkriptParser.ExpressionContext ex: ctx.exprList().expression()) {
                list.add(visit(ex));
            }
        }
        return new CKValue(list);
    }

    // '-' expression                           #unaryMinusExpression
    @Override
    public CKValue visitUnaryMinusExpression(CommandSkriptParser.UnaryMinusExpressionContext ctx) {
        CKValue v = visit(ctx.expression());
        if (!v.isNumber()) {
            throw new CKEvalException(ctx);
        }
        return new CKValue(-1 * v.asDouble());
    }

    // '!' expression                           #notExpression
    @Override
    public CKValue visitNotExpression(CommandSkriptParser.NotExpressionContext ctx) {
        CKValue v = visit(ctx.expression());
        if (!v.isBoolean()) {
            throw new CKEvalException(ctx);
        }
        return new CKValue(!v.asBoolean());
    }

    // expression '^' expression                #powerExpression
    @Override
    public CKValue visitPowerExpression(CommandSkriptParser.PowerExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(Math.pow(lhs.asDouble(), rhs.asDouble()));
        }
        throw new CKEvalException(ctx);
    }

    // expression '*' expression                #multiplyExpression
    @Override
    public CKValue visitMultiplyExpression(CommandSkriptParser.MultiplyExpressionContext ctx) {

        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));

        if(lhs == null || rhs == null) {
            throw new CKEvalException(ctx);
        }

        // number * number
        if(lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() * rhs.asDouble());
        }

        // string * number
        if(lhs.isString() && rhs.isNumber()) {
            StringBuilder str = new StringBuilder();
            int limit = rhs.asDouble().intValue();
            for(int i = 0; i < limit; i++) {
                str.append(lhs.asString());
            }
            return new CKValue(str.toString());
        }

        // list * number
        if(lhs.isList() && rhs.isNumber()) {
            List<CKValue> total = new ArrayList<>();
            int limit = rhs.asDouble().intValue();
            for(int i = 0; i < limit; i++) {
                total.addAll(lhs.asList());
            }
            return new CKValue(total);
        }
        throw new CKEvalException(ctx);
    }

    // expression '/' expression                #divideExpression
    @Override
    public CKValue visitDivideExpression(CommandSkriptParser.DivideExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() / rhs.asDouble());
        }
        throw new CKEvalException(ctx);
    }

    // expression '%' expression                #modulusExpression
    @Override
    public CKValue visitModulusExpression(CommandSkriptParser.ModulusExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() % rhs.asDouble());
        }
        throw new CKEvalException(ctx);
    }

    // expression '+' expression                #addExpression
    @Override
    public CKValue visitAddExpression(CommandSkriptParser.AddExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));

        if(lhs == null || rhs == null) {
            throw new CKEvalException(ctx);
        }

        // number + number
        if(lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() + rhs.asDouble());
        }

        // list + any
        if(lhs.isList()) {
            List<CKValue> list = lhs.asList();
            list.add(rhs);
            return new CKValue(list);
        }

        // string + any
        if(lhs.isString()) {
            return new CKValue(lhs.asString() + "" + rhs.toString());
        }

        // any + string
        if(rhs.isString()) {
            return new CKValue(lhs.toString() + "" + rhs.asString());
        }

        return new CKValue(lhs.toString() + rhs.toString());
    }

    // expression '-' expression                #subtractExpression
    @Override
    public CKValue visitSubtractExpression(CommandSkriptParser.SubtractExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() - rhs.asDouble());
        }
        if (lhs.isList()) {
            List<CKValue> list = lhs.asList();
            list.remove(rhs);
            return new CKValue(list);
        }
        throw new CKEvalException(ctx);
    }

    // expression '>=' expression               #gtEqExpression
    @Override
    public CKValue visitGtEqExpression(CommandSkriptParser.GtEqExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() >= rhs.asDouble());
        }
        if(lhs.isString() && rhs.isString()) {
            return new CKValue(lhs.asString().compareTo(rhs.asString()) >= 0);
        }
        throw new CKEvalException(ctx);
    }

    // expression '<=' expression               #ltEqExpression
    @Override
    public CKValue visitLtEqExpression(CommandSkriptParser.LtEqExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() <= rhs.asDouble());
        }
        if(lhs.isString() && rhs.isString()) {
            return new CKValue(lhs.asString().compareTo(rhs.asString()) <= 0);
        }
        throw new CKEvalException(ctx);
    }

    // expression '>' expression                #gtExpression
    @Override
    public CKValue visitGtExpression(CommandSkriptParser.GtExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() > rhs.asDouble());
        }
        if(lhs.isString() && rhs.isString()) {
            return new CKValue(lhs.asString().compareTo(rhs.asString()) > 0);
        }
        throw new CKEvalException(ctx);
    }

    // expression '<' expression                #ltExpression
    @Override
    public CKValue visitLtExpression(CommandSkriptParser.LtExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs.isNumber() && rhs.isNumber()) {
            return new CKValue(lhs.asDouble() < rhs.asDouble());
        }
        if(lhs.isString() && rhs.isString()) {
            return new CKValue(lhs.asString().compareTo(rhs.asString()) < 0);
        }
        throw new CKEvalException(ctx);
    }

    // expression '==' expression               #eqExpression
    @Override
    public CKValue visitEqExpression(CommandSkriptParser.EqExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        if (lhs == null) {
            throw new CKEvalException(ctx);
        }
        return new CKValue(lhs.equals(rhs));
    }

    // expression '!=' expression               #notEqExpression
    @Override
    public CKValue visitNotEqExpression(CommandSkriptParser.NotEqExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));
        return new CKValue(!lhs.equals(rhs));
    }

    // expression '&&' expression               #andExpression
    @Override
    public CKValue visitAndExpression(CommandSkriptParser.AndExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));

        if(!lhs.isBoolean() || !rhs.isBoolean()) {
            throw new CKEvalException(ctx);
        }
        return new CKValue(lhs.asBoolean() && rhs.asBoolean());
    }

    // expression '||' expression               #andExpression
    @Override
    public CKValue visitOrExpression(CommandSkriptParser.OrExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));

        if(!lhs.isBoolean() || !rhs.isBoolean()) {
            throw new CKEvalException(ctx);
        }
        return new CKValue(lhs.asBoolean() || rhs.asBoolean());
    }

    // expression '?' expression ':' expression #ternaryExpression
    @Override
    public CKValue visitTernaryExpression(CommandSkriptParser.TernaryExpressionContext ctx) {
        CKValue condition = visit(ctx.expression(0));

        if(!condition.isBoolean()) {
            throw new CKEvalException(ctx);
        }

        if (condition.asBoolean()) {
            return new CKValue(visit(ctx.expression(1)));
        } else {
            return new CKValue(visit(ctx.expression(2)));
        }
    }

    // expression In expression                 #inExpression
    @Override
    public CKValue visitInExpression(CommandSkriptParser.InExpressionContext ctx) {
        CKValue lhs = visit(ctx.expression(0));
        CKValue rhs = visit(ctx.expression(1));

        if (rhs.isList()) {
            return new CKValue(rhs.asList().contains(lhs));
        }

        throw new CKEvalException(ctx);
    }

    // Number                                   #numberExpression
    @Override
    public CKValue visitNumberExpression(CommandSkriptParser.NumberExpressionContext ctx) {
        return new CKValue(Double.valueOf(ctx.getText()));
    }

    // Bool                                     #boolExpression
    @Override
    public CKValue visitBoolExpression(CommandSkriptParser.BoolExpressionContext ctx) {
        return new CKValue(Boolean.valueOf(ctx.getText()));
    }

    // Null                                     #nullExpression
    @Override
    public CKValue visitNullExpression(CommandSkriptParser.NullExpressionContext ctx) {
        return CKValue.NULL;
    }

    private CKValue resolveIndex(CKValue val, CKValue index, CommandSkriptParser.ExpressionContext ctx) {

        if(!val.isList() || !index.isNumber()) {
            throw new CKEvalException(ctx);
        }

        List<CKValue> list = val.asList();
        int i = index.asDouble().intValue();

        if(i < 0 || i >= list.size()) {
            throw new CKEvalException("Index " + i + " is out of bounds", ctx);
        }

        return list.get(i);
    }

    private void setIndex(CKValue list, CKValue index, CKValue val, CommandSkriptParser.AssignmentContext ctx) {

        if(!list.isList() || !index.isNumber()) {
            throw new CKEvalException(ctx);
        }

        List<CKValue> valList = list.asList();
        int i = index.asDouble().intValue();

        if(i < 0 || i >= valList.size()) {
            throw new CKEvalException("Index " + i + " is out of bounds", ctx);
        }

        valList.set(i, val);
    }

    // list index?                            #listExpression
    @Override
    public CKValue visitListExpression(CommandSkriptParser.ListExpressionContext ctx) {
        CKValue val = visit(ctx.list());
        CKValue index = visit(ctx.index());

        return resolveIndex(val, index, ctx);
    }

    // Variable index?                          #variableExpression
    @Override
    public CKValue visitVariableExpression(CommandSkriptParser.VariableExpressionContext ctx) {
        String id = ctx.Variable().getText();
        Optional<CKValue> valOptional = scope.resolveVariable(id);

        if(valOptional.isPresent()) {
            CKValue val = valOptional.get();

            if(ctx.index() != null) {
                CKValue index = visit(ctx.index());
                return resolveIndex(val, index, ctx);
            } else {
                return val;
            }

        }

        throw new CKEvalException("Variable " + id + " undefined in current scope", ctx);
    }

    // Constant index?                          #constantExpression
    @Override
    public CKValue visitConstantExpression(CommandSkriptParser.ConstantExpressionContext ctx) {
        String id = ctx.Constant().getText();
        Optional<CKValue> valOptional = scope.resolveConstant(id);

        if(valOptional.isPresent()) {
            CKValue val = valOptional.get();

            if(ctx.index() != null) {
                CKValue index = visit(ctx.index());
                return resolveIndex(val, index, ctx);
            } else {
                return val;
            }

        }

        throw new CKEvalException("Constant " + id + " undefined in current scope", ctx);
    }

    // String                                   #stringExpression
    @Override
    public CKValue visitStringExpression(CommandSkriptParser.StringExpressionContext ctx) {
        return new CKValue(ctx.getText());
    }

    // '(' expression ')'                       #expressionExpression
    @Override
    public CKValue visitExpressionExpression(CommandSkriptParser.ExpressionExpressionContext ctx) {
        return visit(ctx.expression());
    }

    // Variable index? '=' expression    #variableAssignment
    @Override
    public CKValue visitVariableAssignment(CommandSkriptParser.VariableAssignmentContext ctx) {
        String id = ctx.Variable().getText();
        CKValue newVal = visit(ctx.expression());

        if (ctx.index() != null) {
            Optional<CKValue> varOptional = scope.resolveVariable(id);
            if (varOptional.isPresent()) {
                CKValue var = varOptional.get();
                CKValue index = visit(ctx.index());
                setIndex(var, index, newVal, ctx);
            } else {
                throw new CKEvalException("Variable " + id + " undefined in current scope", ctx);
            }
        } else {
            scope.assignVariable(id, newVal);
        }

        return CKValue.VOID;
    }

    // Constant '=' expression           #constantAssignment
    @Override
    public CKValue visitConstantAssignment(CommandSkriptParser.ConstantAssignmentContext ctx) {
        String id = ctx.Constant().getText();
        CKValue newVal = visit(ctx.expression());

        if(scope.resolveConstant(id).isPresent()) {
            throw new CKEvalException("Cannot assign a new value of " + newVal + " to constant " + id, ctx);
        } else {
            scope.assignConstant(id, newVal);
        }

        return CKValue.VOID;
    }


}
