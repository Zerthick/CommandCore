package io.github.zerthick.commandcore.antlr;

import io.github.zerthick.commandcore.antlr.scope.Scope;
import io.github.zerthick.commandskript.CommandSkriptBaseVisitor;
import io.github.zerthick.commandskript.CommandSkriptParser;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;

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

        if (ctx.index() != null) {
            CKValue index = visit(ctx.index());
            return resolveIndex(val, index, ctx);
        }
        return val;
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
            }
            return val;
        }

        throw new CKEvalException("Variable " + id + " undefined in current scope", ctx);
    }

    // Variable index?                          #variableExpression
    @Override
    public CKValue visitSpecialExpression(CommandSkriptParser.SpecialExpressionContext ctx) {
        String id = ctx.Special().getText().substring(1);
        Optional<CKValue> valOptional = scope.resolveSpecial(id, commandSource);

        if (valOptional.isPresent()) {
            CKValue val = valOptional.get();

            if (ctx.index() != null) {
                CKValue index = visit(ctx.index());
                return resolveIndex(val, index, ctx);
            }
            return val;
        }

        throw new CKEvalException("Special Variable " + id + " undefined in current scope", ctx);
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
            }
            return val;
        }

        throw new CKEvalException("Constant " + id + " undefined in current scope", ctx);
    }

    // String                                   #stringExpression
    @Override
    public CKValue visitStringExpression(CommandSkriptParser.StringExpressionContext ctx) {

        String text = ctx.getText();

        return new CKValue(text.substring(1, text.length() - 1));
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

    // Print '(' expression ')'      #printFunctionCall
    @Override
    public CKValue visitPrintFunctionCall(CommandSkriptParser.PrintFunctionCallContext ctx) {

        CKValue val = visit(ctx.expression());

        logger.info(val.toString());

        return CKValue.VOID;
    }

    // Check '(' expression ')'      #checkFunctionCall
    @Override
    public CKValue visitCheckFunctionCall(CommandSkriptParser.CheckFunctionCallContext ctx) {

        CKValue val = visit(ctx.expression());

        if (!val.isString()) {
            throw new CKEvalException("Permission to check must resolve to a string", ctx);
        }

        return new CKValue(commandSource.hasPermission(val.asString()));
    }

    // Size '(' expression ')'       #sizeFunctionCall
    @Override
    public CKValue visitSizeFunctionCall(CommandSkriptParser.SizeFunctionCallContext ctx) {

        CKValue val = this.visit(ctx.expression());

        if (val.isString()) {
            return new CKValue(val.asString().length());
        }

        if (val.isList()) {
            return new CKValue(val.asList().size());
        }

        throw new CKEvalException(ctx);
    }

    // Execute   '(' exprList ')'    #executeFuntionCall
    @Override
    public CKValue visitExecuteFuntionCall(CommandSkriptParser.ExecuteFuntionCallContext ctx) {
        for (CommandSkriptParser.ExpressionContext ex : ctx.exprList().expression()) {
            CKValue val = visit(ex);
            if (!val.isString()) {
                throw new CKEvalException("Command to execute must resolve to a string", ctx);
            }
            Sponge.getCommandManager().process(commandSource, val.asString());
        }
        return CKValue.VOID;
    }

    // ConsoleEx '(' exprList ')'    #consoleExFuntionCall
    @Override
    public CKValue visitConsoleExFuntionCall(CommandSkriptParser.ConsoleExFuntionCallContext ctx) {
        for (CommandSkriptParser.ExpressionContext ex : ctx.exprList().expression()) {
            CKValue val = visit(ex);
            if (!val.isString()) {
                throw new CKEvalException("Command to execute must resolve to a string", ctx);
            }
            Sponge.getCommandManager().process(consoleSource, val.asString());
        }
        return CKValue.VOID;
    }

    // Player '(' ')'                #playerFunctionCall
    @Override
    public CKValue visitPlayerFunctionCall(CommandSkriptParser.PlayerFunctionCallContext ctx) {
        return new CKValue(commandSource instanceof Player);
    }

    // Rand '(' exprList ')'         #randFunctionCall
    @Override
    public CKValue visitRandFunctionCall(CommandSkriptParser.RandFunctionCallContext ctx) {

        List<CommandSkriptParser.ExpressionContext> args = ctx.exprList().expression();

        if (args.size() != 2) {
            throw new CKEvalException(ctx);
        }

        CKValue lhs = visit(args.get(0));
        CKValue rhs = visit(args.get(1));

        if (!lhs.isNumber() || !rhs.isNumber()) {
            throw new CKEvalException(ctx);
        }

        double rand = Math.random() * rhs.asDouble() + lhs.asDouble();

        return new CKValue(rand);
    }

    // Round '(' expression ')'      #roundFunctionCall
    @Override
    public CKValue visitRoundFunctionCall(CommandSkriptParser.RoundFunctionCallContext ctx) {

        CKValue val = visit(ctx.expression());

        if (!val.isNumber()) {
            throw new CKEvalException(ctx);
        }

        long round = Math.round(val.asDouble());

        return new CKValue(round);
    }

    // Floor '(' expression ')'      #floorFunctionCall
    @Override
    public CKValue visitFloorFunctionCall(CommandSkriptParser.FloorFunctionCallContext ctx) {

        CKValue val = visit(ctx.expression());

        if (!val.isNumber()) {
            throw new CKEvalException(ctx);
        }

        double round = Math.floor(val.asDouble());

        return new CKValue(round);
    }

    // Ceil '(' expression ')'       #celiFunctionCall
    @Override
    public CKValue visitCeilFunctionCall(CommandSkriptParser.CeilFunctionCallContext ctx) {

        CKValue val = visit(ctx.expression());

        if (!val.isNumber()) {
            throw new CKEvalException(ctx);
        }

        double round = Math.ceil(val.asDouble());

        return new CKValue(round);
    }

    // ifStatement
    //  : ifStat elseIfStat* elseStat? End
    //  ;
    //
    // ifStat
    //  : If expression Do block
    //  ;
    //
    // elseIfStat
    //  : Else If expression Do block
    //  ;
    //
    // elseStat
    //  : Else Do block
    //  ;
    @Override
    public CKValue visitIfStatement(CommandSkriptParser.IfStatementContext ctx) {

        // if ...
        CKValue condition = visit(ctx.ifStat().expression());
        if (!condition.isBoolean()) {
            throw new CKEvalException(ctx);
        }

        if (condition.asBoolean()) {
            return visit(ctx.ifStat().block());
        }

        // else if ...
        for (int i = 0; i < ctx.elseIfStat().size(); i++) {

            condition = visit(ctx.elseIfStat(i).expression());
            if (!condition.isBoolean()) {
                throw new CKEvalException(ctx);
            }

            if (condition.asBoolean()) {
                return visit(ctx.elseIfStat(i).block());
            }
        }

        // else ...
        if (ctx.elseStat() != null) {
            return visit(ctx.elseStat().block());
        }

        return CKValue.VOID;
    }

    // block
    // : statement*
    // ;
    @Override
    public CKValue visitBlock(CommandSkriptParser.BlockContext ctx) {

        scope = new Scope(scope); // create new local scope
        for (CommandSkriptParser.StatementContext sx : ctx.statement()) {
            visit(sx);
        }
        scope = scope.getParent();
        return CKValue.VOID;
    }

    // forStatement
    // : For Variable '=' expression To expression Do block End
    // ;
    @Override
    public CKValue visitForStatement(CommandSkriptParser.ForStatementContext ctx) {

        CKValue startVal = visit(ctx.expression(0));
        CKValue endVal = visit(ctx.expression(1));

        if (!startVal.isNumber() && !endVal.isNumber()) {
            throw new CKEvalException(ctx);
        }

        int start = startVal.asDouble().intValue();
        int stop = endVal.asDouble().intValue();
        for (int i = start; i <= stop; i++) {
            scope.assignVariable(ctx.Variable().getText(), new CKValue(i));
            visit(ctx.block());
        }
        return CKValue.VOID;
    }

    // whileStatement
    // : While expression Do block End
    // ;
    @Override
    public CKValue visitWhileStatement(CommandSkriptParser.WhileStatementContext ctx) {

        CKValue condition = visit(ctx.expression());
        if (!condition.isBoolean()) {
            throw new CKEvalException(ctx);
        }

        while (visit(ctx.expression()).asBoolean()) {
            visit(ctx.block());
        }
        return CKValue.VOID;
    }
}
