// Generated from EPA SWMM 5.2.4 (controls.c) by tools/c2x/emit_java.py. Do not edit by hand;
// fix the translator, R.java or Overrides.java instead.
package swmm5;

import static swmm5.Consts.*;
import static swmm5.Overrides.*;
import static swmm5.F_climate.*;
import static swmm5.F_controls.*;
import static swmm5.F_culvert.*;
import static swmm5.F_datetime.*;
import static swmm5.F_dwflow.*;
import static swmm5.F_dynwave.*;
import static swmm5.F_error.*;
import static swmm5.F_exfil.*;
import static swmm5.F_findroot.*;
import static swmm5.F_flowrout.*;
import static swmm5.F_forcmain.*;
import static swmm5.F_gage.*;
import static swmm5.F_gwater.*;
import static swmm5.F_hash.*;
import static swmm5.F_hotstart.*;
import static swmm5.F_iface.*;
import static swmm5.F_infil.*;
import static swmm5.F_inflow.*;
import static swmm5.F_inlet.*;
import static swmm5.F_input.*;
import static swmm5.F_inputrpt.*;
import static swmm5.F_keywords.*;
import static swmm5.F_kinwave.*;
import static swmm5.F_landuse.*;
import static swmm5.F_lid.*;
import static swmm5.F_lidproc.*;
import static swmm5.F_link.*;
import static swmm5.F_massbal.*;
import static swmm5.F_mathexpr.*;
import static swmm5.F_node.*;
import static swmm5.F_output.*;
import static swmm5.F_project.*;
import static swmm5.F_qualrout.*;
import static swmm5.F_rain.*;
import static swmm5.F_rdii.*;
import static swmm5.F_report.*;
import static swmm5.F_roadway.*;
import static swmm5.F_routing.*;
import static swmm5.F_runoff.*;
import static swmm5.F_shape.*;
import static swmm5.F_snow.*;
import static swmm5.F_stats.*;
import static swmm5.F_statsrpt.*;
import static swmm5.F_street.*;
import static swmm5.F_subcatch.*;
import static swmm5.F_surfqual.*;
import static swmm5.F_swmm5.*;
import static swmm5.F_table.*;
import static swmm5.F_toposort.*;
import static swmm5.F_transect.*;
import static swmm5.F_treatmnt.*;
import static swmm5.F_xsect.*;

@SuppressWarnings("all")
final class F_controls {
    static CPtr[] ObjectWords = new CPtr[] { L656, L657, L658, L659, L660, L661, L662, L663, L32, null };
    static CPtr[] AttribWords = new CPtr[] { L664, L665, L314, L666, L667, L183, L668, L669, L670, L671, L672, L673, L674, L675, L676, L677, L21, L678, L679, L680, L681, null };
    static CPtr[] RelOpWords = new CPtr[] { L31, L682, L683, L684, L685, L686, null };
    static CPtr[] StatusWords = new CPtr[] { L687, L688, null };
    static CPtr[] ConduitWords = new CPtr[] { L689, L690, null };
    static CPtr[] SettingTypeWords = new CPtr[] { L691, L692, L693, null };
    static CPtr IntensityWord = L694;
    static TRule[] Rules = null;
    static TActionList ActionList = null;
    static int InputState = 0;
    static int RuleCount = 0;
    static double ControlValue = 0.0;
    static double SetPoint = 0.0;
    static double CurrentDate = 0.0;
    static double CurrentTime = 0.0;
    static int VariableCount = 0;
    static int ExpressionCount = 0;
    static int CurrentVariable = 0;
    static int CurrentExpression = 0;
    static TNamedVariable[] NamedVariable = null;
    static TExpression[] Expression = null;

    // controls.c:226
    static void controls_init()
    {
        Rules = (null);
        NamedVariable = (null);
        Expression = (null);
        RuleCount = 0;
        VariableCount = 0;
        ExpressionCount = 0;
    }

    // controls.c:243
    static void controls_addToCount(CPtr s)
    {
        if ((match(s, L29) != 0))
        {
            VariableCount++;
        }
        else if ((match(s, L30) != 0))
        {
            ExpressionCount++;
        }
    }

    // controls.c:257
    static int controls_create(int n)
    {
        int r = 0;
        ActionList = (null);
        InputState = r_PRIORITY;
        RuleCount = n;
        if ((RuleCount > 0))
        {
            Rules = R.fill(new TRule[(int) (RuleCount)], TRule::new);
            if ((Rules == null))
            {
                return ERR_MEMORY;
            }
            for (r = 0; (r < RuleCount); r++)
            {
                Rules[r].ID = null;
                Rules[r].firstPremise = (null);
                Rules[r].lastPremise = (null);
                Rules[r].thenActions = (null);
                Rules[r].elseActions = (null);
                Rules[r].priority = 0.0;
            }
        }
        CurrentVariable = (-1);
        CurrentExpression = (-1);
        if ((VariableCount > 0))
        {
            NamedVariable = R.fill(new TNamedVariable[(int) (VariableCount)], TNamedVariable::new);
            if ((NamedVariable == null))
            {
                return ERR_MEMORY;
            }
        }
        if ((ExpressionCount > 0))
        {
            Expression = R.fill(new TExpression[(int) (ExpressionCount)], TExpression::new);
            if ((Expression == null))
            {
                return ERR_MEMORY;
            }
        }
        return 0;
    }

    // controls.c:302
    static void controls_delete()
    {
        int i = 0;
        for (i = 0; (i < ExpressionCount); i++)
        {
            mathexpr_delete(Expression[i].expression);
            Expression[i].expression = (null);
        }
        {
            if ((Expression != null))
            {
                R.free(Expression);
                Expression = (null);
            }
        }
        ;
        {
            if ((NamedVariable != null))
            {
                R.free(NamedVariable);
                NamedVariable = (null);
            }
        }
        ;
        if ((RuleCount == 0))
        {
            return;
        }
        deleteActionList();
        deleteRules();
    }

    // controls.c:326
    static int controls_addVariable(CPtr[] tok, int nToks)
    {
        TVariable v1 = new TVariable();
        IBox k = new IBox(0);
        int err = 0;
        CurrentVariable++;
        if ((nToks < 5))
        {
            return ERR_ITEMS;
        }
        if ((findExactMatch(tok[1], AttribWords) >= 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        if (!(match(tok[2], L31) != 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[2]);
        }
        if (((!(match(tok[3], L32) != 0)) && (nToks < 6)))
        {
            return ERR_ITEMS;
        }
        k.v = 3;
        err = getPremiseVariable(tok, nToks, k, v1);
        if ((err > 0))
        {
            return err;
        }
        k.v = CurrentVariable;
        NamedVariable[k.v].variable = v1.copy();
        sstrncpy(NamedVariable[k.v].name, tok[1], 32);
        return 0;
    }

    // controls.c:357
    static int controls_addExpression(CPtr[] tok, int nToks)
    {
        int i = 0;
        int k = 0;
        CPtr s = R.buf(1025);
        MathExpr expr = null;
        CurrentExpression++;
        if ((nToks < 4))
        {
            return ERR_ITEMS;
        }
        k = CurrentExpression;
        Expression[k].expression = (null);
        sstrncpy(Expression[k].name, tok[1], 32);
        sstrncpy(s, tok[3], 1024);
        for (i = 4; (i < nToks); i++)
        {
            sstrcat(s, L33, 1024);
            sstrcat(s, tok[i], 1024);
        }
        expr = mathexpr_create(s, F_controls::getVariableIndex);
        if ((expr == null))
        {
            return error_setInpError(ERR_MATH_EXPR, L0);
        }
        Expression[k].expression = expr;
        return 0;
    }

    // controls.c:393
    static int getVariableIndex(CPtr varName)
    {
        int i = 0;
        for (i = 0; (i < VariableCount); i++)
        {
            if ((match(varName, NamedVariable[i].name) != 0))
            {
                return i;
            }
        }
        return (-1);
    }

    // controls.c:410
    static double getNamedVariableValue(int varIndex)
    {
        return getVariableValue(NamedVariable[varIndex].variable);
    }

    // controls.c:422
    static int getExpressionIndex(CPtr exprName)
    {
        int i = 0;
        for (i = 0; (i < ExpressionCount); i++)
        {
            if ((match(exprName, Expression[i].name) != 0))
            {
                return i;
            }
        }
        return (-1);
    }

    // controls.c:439
    static int controls_addRuleClause(int r, int keyword, CPtr[] tok, int nToks)
    {
        switch (keyword) {
            case r_RULE:
                if ((Rules[r].ID == null))
                {
                    Rules[r].ID = project_findID(CONTROL, tok[1]);
                }
                InputState = r_RULE;
                if ((nToks > 2))
                {
                    return ERR_RULE;
                }
                return 0;
            case r_IF:
                if ((InputState != r_RULE))
                {
                    return ERR_RULE;
                }
                InputState = r_IF;
                return addPremise(r, r_AND, tok, nToks);
            case r_AND:
                if ((InputState == r_IF))
                {
                    return addPremise(r, r_AND, tok, nToks);
                }
                else if (((InputState == r_THEN) || (InputState == r_ELSE)))
                {
                    return addAction(r, tok, nToks);
                }
                else
                {
                    return ERR_RULE;
                }
            case r_OR:
                if ((InputState != r_IF))
                {
                    return ERR_RULE;
                }
                return addPremise(r, r_OR, tok, nToks);
            case r_THEN:
                if ((InputState != r_IF))
                {
                    return ERR_RULE;
                }
                InputState = r_THEN;
                return addAction(r, tok, nToks);
            case r_ELSE:
                if ((InputState != r_THEN))
                {
                    return ERR_RULE;
                }
                InputState = r_ELSE;
                return addAction(r, tok, nToks);
            case r_PRIORITY:
                if (((InputState != r_THEN) && (InputState != r_ELSE)))
                {
                    return ERR_RULE;
                }
                InputState = r_PRIORITY;
                if (!(getDouble(tok[1], new DField<TRule>(Rules[r], __o -> __o.priority, (__o, __x) -> __o.priority = __x)) != 0))
                {
                    return ERR_NUMBER;
                }
                if ((nToks > 2))
                {
                    return ERR_RULE;
                }
                return 0;
        }
        return 0;
    }

    // controls.c:495
    static int controls_evaluate(double currentTime, double elapsedTime, double tStep)
    {
        int r = 0;
        int result = 0;
        TPremise p = null;
        TAction a = null;
        CurrentDate = Math.floor(currentTime);
        CurrentTime = (currentTime - Math.floor(currentTime));
        ElapsedTime = elapsedTime;
        if ((RuleCount == 0))
        {
            return 0;
        }
        clearActionList();
        for (r = 0; (r < RuleCount); r++)
        {
            result = 1;
            p = Rules[r].firstPremise;
            while ((p != null))
            {
                if ((p.type == r_OR))
                {
                    if ((result == 0))
                    {
                        result = evaluatePremise(p, tStep);
                    }
                }
                else
                {
                    if ((result == 0))
                    {
                        break;
                    }
                    result = evaluatePremise(p, tStep);
                }
                p = p.next;
            }
            if ((result == 1))
            {
                a = Rules[r].thenActions;
            }
            else
            {
                a = Rules[r].elseActions;
            }
            while ((a != null))
            {
                updateActionValue(a, currentTime, tStep);
                updateActionList(a);
                a = a.next;
            }
        }
        if ((ActionList != null))
        {
            return executeActionList(currentTime);
        }
        else
        {
            return 0;
        }
    }

    // controls.c:556
    static int addPremise(int r, int type, CPtr[] tok, int nToks)
    {
        int relation = 0;
        IBox n = new IBox(0);
        int err = 0;
        DBox value = new DBox((-10000000000.0));
        TPremise p = null;
        TVariable v1 = new TVariable();
        TVariable v2 = new TVariable();
        int obj = 0;
        int exprIndex = 0;
        int varIndex = (-1);
        if ((nToks < 4))
        {
            return ERR_ITEMS;
        }
        v1.attribute = (-1);
        v1.object = (-1);
        v1.index = (-1);
        n.v = 1;
        exprIndex = getExpressionIndex(tok[1]);
        if ((exprIndex < 0))
        {
            varIndex = getVariableIndex(tok[n.v]);
            if ((varIndex >= 0))
            {
                v1 = NamedVariable[varIndex].variable.copy();
            }
            else
            {
                err = getPremiseVariable(tok, nToks, n, v1);
                if ((err > 0))
                {
                    return err;
                }
            }
        }
        n.v++;
        if ((n.v >= nToks))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        relation = findExactMatch(tok[n.v], RelOpWords);
        if ((relation < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[n.v]);
        }
        v2.attribute = (-1);
        v2.object = (-1);
        v2.index = (-1);
        n.v++;
        if ((n.v >= nToks))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        varIndex = getVariableIndex(tok[n.v]);
        if ((varIndex >= 0))
        {
            v2 = NamedVariable[varIndex].variable.copy();
        }
        else
        {
            obj = findmatch(tok[n.v], ObjectWords);
            if ((obj >= 0))
            {
                err = getPremiseVariable(tok, nToks, n, v2);
                if ((err > 0))
                {
                    return ERR_RULE;
                }
                if (((exprIndex < 0) && (v1.attribute != v2.attribute)))
                {
                    report_writeWarningMsg(L34, Rules[r].ID);
                }
            }
            else
            {
                err = getPremiseValue(tok[n.v], v1.attribute, value);
                if ((err > 0))
                {
                    return err;
                }
            }
        }
        n.v++;
        if (((n.v < nToks) && (findmatch(tok[n.v], RuleKeyWords) >= 0)))
        {
            return ERR_RULE;
        }
        p = new TPremise();
        if (!(p != null))
        {
            return ERR_MEMORY;
        }
        p.type = type;
        p.exprIndex = exprIndex;
        p.lhsVar = v1.copy();
        p.rhsVar = v2.copy();
        p.relation = relation;
        p.value = value.v;
        p.next = (null);
        if ((Rules[r].firstPremise == null))
        {
            Rules[r].firstPremise = p;
        }
        else
        {
            Rules[r].lastPremise.next = p;
        }
        Rules[r].lastPremise = p;
        return 0;
    }

    // controls.c:668
    static int getPremiseVariable(CPtr[] tok, int nToks, IRef k, TVariable v)
    {
        int n = k.get();
        int object = (-1);
        int index = (-1);
        int obj = 0;
        int attrib = 0;
        obj = findmatch(tok[n], ObjectWords);
        if ((obj < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[n]);
        }
        n++;
        if ((n >= nToks))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        switch (obj) {
            case r_GAGE:
                index = project_findObject(GAGE, tok[n]);
                if ((index < 0))
                {
                    return error_setInpError(ERR_NAME, tok[n]);
                }
                object = r_GAGE;
                break;
            case r_NODE:
                index = project_findObject(NODE, tok[n]);
                if ((index < 0))
                {
                    return error_setInpError(ERR_NAME, tok[n]);
                }
                object = r_NODE;
                break;
            case r_LINK:
            case r_CONDUIT:
            case r_PUMP:
            case r_ORIFICE:
            case r_WEIR:
            case r_OUTLET:
                index = project_findObject(LINK, tok[n]);
                if ((index < 0))
                {
                    return error_setInpError(ERR_NAME, tok[n]);
                }
                object = r_LINK;
                break;
            default:
                n--;
        }
        n++;
        if ((n >= nToks))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        if ((object == r_GAGE))
        {
            attrib = getGageAttrib(tok[n]);
        }
        else
        {
            attrib = findmatch(tok[n], AttribWords);
        }
        if ((attrib < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[n]);
        }
        if ((obj == r_GAGE))
        {
        }
        else if ((obj == r_NODE))
        {
            switch (attrib) {
                case r_DEPTH:
                case r_MAXDEPTH:
                case r_HEAD:
                case r_VOLUME:
                case r_INFLOW:
                    break;
                default:
                    return error_setInpError(ERR_KEYWORD, tok[n]);
            }
        }
        else if ((((object == r_LINK) && (index >= 0)) && ((((attrib == r_TIMEOPEN) || (attrib == r_TIMECLOSED))))))
        {
        }
        else if (((obj == r_LINK) || (obj == r_CONDUIT)))
        {
            switch (attrib) {
                case r_STATUS:
                case r_DEPTH:
                case r_FULLFLOW:
                case r_FULLDEPTH:
                case r_FLOW:
                case r_LENGTH:
                case r_SLOPE:
                case r_VELOCITY:
                    break;
                default:
                    return error_setInpError(ERR_KEYWORD, tok[n]);
            }
        }
        else if ((obj == r_PUMP))
        {
            switch (attrib) {
                case r_FLOW:
                case r_SETTING:
                case r_STATUS:
                    break;
                default:
                    return error_setInpError(ERR_KEYWORD, tok[n]);
            }
        }
        else if ((((obj == r_ORIFICE) || (obj == r_WEIR)) || (obj == r_OUTLET)))
        {
            switch (attrib) {
                case r_FLOW:
                case r_SETTING:
                    break;
                default:
                    return error_setInpError(ERR_KEYWORD, tok[n]);
            }
        }
        else
        {
            switch (attrib) {
                case r_TIME:
                case r_DATE:
                case r_CLOCKTIME:
                case r_DAY:
                case r_MONTH:
                case r_DAYOFYEAR:
                    break;
                default:
                    return error_setInpError(ERR_KEYWORD, tok[n]);
            }
        }
        v.object = object;
        v.index = index;
        v.attribute = attrib;
        k.set(n);
        return 0;
    }

    // controls.c:797
    static int getGageAttrib(CPtr token)
    {
        int attrib = 0;
        if ((match(token, IntensityWord) != 0))
        {
            return 0;
        }
        attrib = R.atoi(token);
        if (((attrib < 1) || (attrib > 48)))
        {
            return (-1);
        }
        return attrib;
    }

    // controls.c:824
    static int getPremiseValue(CPtr token, int attrib, DRef value)
    {
        CPtr strDate = R.buf(25);
        switch (attrib) {
            case r_STATUS:
                value.set(findmatch(token, StatusWords));
                if ((value.get() < 0.0))
                {
                    value.set(findmatch(token, ConduitWords));
                }
                if ((value.get() < 0.0))
                {
                    return error_setInpError(ERR_KEYWORD, token);
                }
                break;
            case r_TIME:
            case r_CLOCKTIME:
            case r_TIMEOPEN:
            case r_TIMECLOSED:
                if (!(datetime_strToTime(token, value) != 0))
                {
                    return error_setInpError(ERR_DATETIME, token);
                }
                break;
            case r_DATE:
                if (!(datetime_strToDate(token, value) != 0))
                {
                    return error_setInpError(ERR_DATETIME, token);
                }
                break;
            case r_DAY:
                if (!(getDouble(token, value) != 0))
                {
                    return error_setInpError(ERR_NUMBER, token);
                }
                if (((value.get() < 1.0) || (value.get() > 7.0)))
                {
                    return error_setInpError(ERR_DATETIME, token);
                }
                break;
            case r_MONTH:
                if (!(getDouble(token, value) != 0))
                {
                    return error_setInpError(ERR_NUMBER, token);
                }
                if (((value.get() < 1.0) || (value.get() > 12.0)))
                {
                    return error_setInpError(ERR_DATETIME, token);
                }
                break;
            case r_DAYOFYEAR:
                sstrncpy(strDate, token, 6);
                sstrcat(strDate, L35, 25);
                if ((datetime_strToDate(strDate, value) != 0))
                {
                    value.set(datetime_dayOfYear(value.get()));
                }
                else if ((((!(getDouble(token, value) != 0)) || (value.get() < 1)) || (value.get() > 365)))
                {
                    return error_setInpError(ERR_DATETIME, token);
                }
                break;
            default:
                if (!(getDouble(token, value) != 0))
                {
                    return error_setInpError(ERR_NUMBER, token);
                }
        }
        return 0;
    }

    // controls.c:889
    static int addAction(int r, CPtr[] tok, int nToks)
    {
        int obj = 0;
        int link = 0;
        IBox attrib = new IBox(0);
        IBox curve = new IBox((-1));
        IBox tseries = new IBox((-1));
        int n = 0;
        int err = 0;
        double[] values = new double[] { 1.0, 0.0, 0.0 };
        TAction a = null;
        if ((nToks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        obj = findmatch(tok[1], ObjectWords);
        if (((((((obj != r_LINK) && (obj != r_CONDUIT)) && (obj != r_PUMP)) && (obj != r_ORIFICE)) && (obj != r_WEIR)) && (obj != r_OUTLET)))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        link = project_findObject(LINK, tok[2]);
        if ((link < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        switch (obj) {
            case r_CONDUIT:
                if ((Link[link].type != CONDUIT))
                {
                    return error_setInpError(ERR_NAME, tok[2]);
                }
                break;
            case r_PUMP:
                if ((Link[link].type != PUMP))
                {
                    return error_setInpError(ERR_NAME, tok[2]);
                }
                break;
            case r_ORIFICE:
                if ((Link[link].type != ORIFICE))
                {
                    return error_setInpError(ERR_NAME, tok[2]);
                }
                break;
            case r_WEIR:
                if ((Link[link].type != WEIR))
                {
                    return error_setInpError(ERR_NAME, tok[2]);
                }
                break;
            case r_OUTLET:
                if ((Link[link].type != OUTLET))
                {
                    return error_setInpError(ERR_NAME, tok[2]);
                }
                break;
        }
        attrib.v = findmatch(tok[3], AttribWords);
        if ((attrib.v < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[3]);
        }
        if ((obj == r_CONDUIT))
        {
            if ((attrib.v == r_STATUS))
            {
                values[0] = findmatch(tok[5], ConduitWords);
                if ((values[0] < 0.0))
                {
                    return error_setInpError(ERR_KEYWORD, tok[5]);
                }
            }
            else
            {
                return error_setInpError(ERR_KEYWORD, tok[3]);
            }
        }
        else if ((obj == r_PUMP))
        {
            if ((attrib.v == r_STATUS))
            {
                values[0] = findmatch(tok[5], StatusWords);
                if ((values[0] < 0.0))
                {
                    return error_setInpError(ERR_KEYWORD, tok[5]);
                }
            }
            else if ((attrib.v == r_SETTING))
            {
                err = setActionSetting(tok, nToks, curve, tseries, attrib, values);
                if ((err > 0))
                {
                    return err;
                }
            }
            else
            {
                return error_setInpError(ERR_KEYWORD, tok[3]);
            }
        }
        else if ((((obj == r_ORIFICE) || (obj == r_WEIR)) || (obj == r_OUTLET)))
        {
            if ((attrib.v == r_SETTING))
            {
                err = setActionSetting(tok, nToks, curve, tseries, attrib, values);
                if ((err > 0))
                {
                    return err;
                }
                if (((attrib.v == r_SETTING) && (((values[0] < 0.0) || (values[0] > 1.0)))))
                {
                    return error_setInpError(ERR_NUMBER, tok[5]);
                }
            }
            else
            {
                return error_setInpError(ERR_KEYWORD, tok[3]);
            }
        }
        else
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        n = 6;
        if (((curve.v >= 0) || (tseries.v >= 0)))
        {
            n = 7;
        }
        if ((attrib.v == r_PID))
        {
            n = 9;
        }
        if (((n < nToks) && (findmatch(tok[n], RuleKeyWords) >= 0)))
        {
            return ERR_RULE;
        }
        a = new TAction();
        if (!(a != null))
        {
            return ERR_MEMORY;
        }
        a.rule = r;
        a.link = link;
        a.attribute = attrib.v;
        a.curve = curve.v;
        a.tseries = tseries.v;
        a.value = values[0];
        if ((attrib.v == r_PID))
        {
            a.kp = values[0];
            a.ki = values[1];
            a.kd = values[2];
            a.e1 = 0.0;
            a.e2 = 0.0;
        }
        if ((InputState == r_THEN))
        {
            a.next = Rules[r].thenActions;
            Rules[r].thenActions = a;
        }
        else
        {
            a.next = Rules[r].elseActions;
            Rules[r].elseActions = a;
        }
        return 0;
    }

    // controls.c:1028
    static int setActionSetting(CPtr[] tok, int nToks, IRef curve, IRef tseries, IRef attrib, double[] values)
    {
        int k = 0;
        int m = 0;
        if ((nToks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        k = findmatch(tok[5], SettingTypeWords);
        if (((k >= 0) && (nToks < 7)))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        switch (k) {
            case r_CURVE:
                m = project_findObject(CURVE, tok[6]);
                if ((m < 0))
                {
                    return error_setInpError(ERR_NAME, tok[6]);
                }
                curve.set(m);
                break;
            case r_TIMESERIES:
                m = project_findObject(TSERIES, tok[6]);
                if ((m < 0))
                {
                    return error_setInpError(ERR_NAME, tok[6]);
                }
                tseries.set(m);
                Tseries[m].refersTo = CONTROL;
                break;
            case r_PID:
                if ((nToks < 9))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                for (m = 6; (m <= 8); m++)
                {
                    if (!(getDouble(tok[m], new DElem(values, (m - 6))) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[m]);
                    }
                }
                attrib.set(r_PID);
                break;
            default:
                if (!(getDouble(tok[5], new DElem(values, 0)) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[5]);
                }
        }
        return 0;
    }

    // controls.c:1086
    static void updateActionValue(TAction a, double currentTime, double dt)
    {
        if ((a.curve >= 0))
        {
            a.value = table_lookup(Curve[a.curve], ControlValue);
        }
        else if ((a.tseries >= 0))
        {
            a.value = table_tseriesLookup(Tseries[a.tseries], currentTime, 1);
        }
        else if ((a.attribute == r_PID))
        {
            a.value = getPIDSetting(a, dt);
        }
    }

    // controls.c:1111
    static double getPIDSetting(TAction a, double dt)
    {
        double e0 = 0.0;
        double setting = 0.0;
        double p = 0.0;
        double i = 0.0;
        double d = 0.0;
        double update = 0.0;
        double tolerance = 0.0001;
        dt *= 1440.0;
        e0 = (SetPoint - ControlValue);
        if ((Math.abs(e0) > 1e-06))
        {
            if ((SetPoint != 0.0))
            {
                e0 = (e0 / SetPoint);
            }
            else
            {
                e0 = (e0 / ControlValue);
            }
        }
        if ((Math.abs((e0 - a.e1)) < tolerance))
        {
            a.e2 = 0.0;
            a.e1 = 0.0;
        }
        p = ((e0 - a.e1));
        if ((a.ki == 0.0))
        {
            i = 0.0;
        }
        else
        {
            i = ((e0 * dt) / a.ki);
        }
        d = ((a.kd * (((e0 - (2.0 * a.e1)) + a.e2))) / dt);
        update = (a.kp * (((p + i) + d)));
        if ((Math.abs(update) < tolerance))
        {
            update = 0.0;
        }
        setting = (Link[a.link].targetSetting + update);
        a.e2 = a.e1;
        a.e1 = e0;
        if ((setting < 0.0))
        {
            setting = 0.0;
        }
        if (((Link[a.link].type != PUMP) && (setting > 1.0)))
        {
            setting = 1.0;
        }
        return setting;
    }

    // controls.c:1168
    static void updateActionList(TAction a)
    {
        TActionList listItem = null;
        TAction a1 = null;
        double priority = Rules[a.rule].priority;
        listItem = ActionList;
        while ((listItem != null))
        {
            a1 = listItem.action;
            if (!(a1 != null))
            {
                break;
            }
            if ((a1.link == a.link))
            {
                if ((priority > Rules[a1.rule].priority))
                {
                    listItem.action = a;
                }
                return;
            }
            listItem = listItem.next;
        }
        if (!(listItem != null))
        {
            listItem = new TActionList();
            listItem.next = ActionList;
            ActionList = listItem;
        }
        listItem.action = a;
    }

    // controls.c:1206
    static int executeActionList(double currentTime)
    {
        TActionList listItem = null;
        TActionList nextItem = null;
        TAction a1 = null;
        int count = 0;
        listItem = ActionList;
        while ((listItem != null))
        {
            a1 = listItem.action;
            if (!(a1 != null))
            {
                break;
            }
            if ((a1.link >= 0))
            {
                if ((Link[a1.link].targetSetting != a1.value))
                {
                    Link[a1.link].targetSetting = a1.value;
                    if (((((RptFlags.controls != 0) && (a1.curve < 0)) && (a1.tseries < 0)) && (a1.attribute != r_PID)))
                    {
                        report_writeControlAction(currentTime, Link[a1.link].ID, a1.value, Rules[a1.rule].ID);
                    }
                    count++;
                }
            }
            nextItem = listItem.next;
            listItem = nextItem;
        }
        return count;
    }

    // controls.c:1243
    static int evaluatePremise(TPremise p, double tStep)
    {
        double lhsValue = 0.0;
        double rhsValue = 0.0;
        int result = 0;
        if ((p.exprIndex >= 0))
        {
            lhsValue = mathexpr_eval(Expression[p.exprIndex].expression, F_controls::getNamedVariableValue);
        }
        else
        {
            lhsValue = getVariableValue(p.lhsVar);
        }
        if ((p.value == (-10000000000.0)))
        {
            rhsValue = getVariableValue(p.rhsVar);
        }
        else
        {
            rhsValue = p.value;
        }
        if (((lhsValue == (-10000000000.0)) || (rhsValue == (-10000000000.0))))
        {
            return 0;
        }
        switch (p.lhsVar.attribute) {
            case r_TIME:
            case r_CLOCKTIME:
                return compareTimes(lhsValue, p.relation, rhsValue, (tStep / 2.0));
            case r_TIMEOPEN:
            case r_TIMECLOSED:
                result = compareTimes(lhsValue, p.relation, rhsValue, (tStep / 2.0));
                ControlValue = (lhsValue * 24.0);
                return result;
            default:
                return compareValues(lhsValue, p.relation, rhsValue);
        }
    }

    // controls.c:1286
    static double getVariableValue(TVariable v)
    {
        int i = (-1);
        int j = (-1);
        if ((v.object == r_GAGE))
        {
            return getRainValue(v);
        }
        if ((v.object == r_NODE))
        {
            i = v.index;
        }
        if ((v.object == r_LINK))
        {
            j = v.index;
        }
        switch (v.attribute) {
            case r_TIME:
                return ElapsedTime;
            case r_DATE:
                return CurrentDate;
            case r_CLOCKTIME:
                return CurrentTime;
            case r_DAY:
                return datetime_dayOfWeek(CurrentDate);
            case r_MONTH:
                return datetime_monthOfYear(CurrentDate);
            case r_DAYOFYEAR:
                return datetime_dayOfYear(CurrentDate);
            case r_STATUS:
                if (((j < 0) || (((Link[j].type != CONDUIT) && (Link[j].type != PUMP)))))
                {
                    return (-10000000000.0);
                }
                else
                {
                    return Link[j].setting;
                }
            case r_SETTING:
                if (((j < 0) || ((((Link[j].type != PUMP) && (Link[j].type != ORIFICE)) && (Link[j].type != WEIR)))))
                {
                    return (-10000000000.0);
                }
                else
                {
                    return Link[j].setting;
                }
            case r_FLOW:
                if ((j < 0))
                {
                    return (-10000000000.0);
                }
                else
                {
                    return ((Link[j].direction * Link[j].newFlow) * UCF(FLOW));
                }
            case r_FULLFLOW:
            case r_FULLDEPTH:
            case r_VELOCITY:
            case r_LENGTH:
            case r_SLOPE:
                if ((j < 0))
                {
                    return (-10000000000.0);
                }
                else if ((Link[j].type != CONDUIT))
                {
                    return (-10000000000.0);
                }
                switch (v.attribute) {
                    case r_FULLFLOW:
                        return (Link[j].qFull * UCF(FLOW));
                    case r_FULLDEPTH:
                        return (Link[j].xsect.yFull * UCF(LENGTH));
                    case r_VELOCITY:
                        return (link_getVelocity(j, Link[j].newFlow, Link[j].newDepth) * UCF(LENGTH));
                    case r_LENGTH:
                        return (Conduit[Link[j].subIndex].length * UCF(LENGTH));
                    case r_SLOPE:
                        return Conduit[Link[j].subIndex].slope;
                    default:
                        return (-10000000000.0);
                }
            case r_DEPTH:
                if ((j >= 0))
                {
                    return (Link[j].newDepth * UCF(LENGTH));
                }
                else if ((i >= 0))
                {
                    return (Node[i].newDepth * UCF(LENGTH));
                }
                else
                {
                    return (-10000000000.0);
                }
            case r_MAXDEPTH:
                if ((i >= 0))
                {
                    return (Node[i].fullDepth * UCF(LENGTH));
                }
                else
                {
                    return (-10000000000.0);
                }
            case r_HEAD:
                if ((i < 0))
                {
                    return (-10000000000.0);
                }
                return (((Node[i].newDepth + Node[i].invertElev)) * UCF(LENGTH));
            case r_VOLUME:
                if ((i < 0))
                {
                    return (-10000000000.0);
                }
                return ((Node[i].newVolume * UCF(VOLUME)));
            case r_INFLOW:
                if ((i < 0))
                {
                    return (-10000000000.0);
                }
                else
                {
                    return (Node[i].newLatFlow * UCF(FLOW));
                }
            case r_TIMEOPEN:
                if ((j < 0))
                {
                    return (-10000000000.0);
                }
                if ((Link[j].setting <= 0.0))
                {
                    return (-10000000000.0);
                }
                return ((CurrentDate + CurrentTime) - Link[j].timeLastSet);
            case r_TIMECLOSED:
                if ((j < 0))
                {
                    return (-10000000000.0);
                }
                if ((Link[j].setting > 0.0))
                {
                    return (-10000000000.0);
                }
                return ((CurrentDate + CurrentTime) - Link[j].timeLastSet);
            default:
                return (-10000000000.0);
        }
    }

    // controls.c:1388
    static double getRainValue(TVariable v)
    {
        if ((v.index < 0))
        {
            return (-10000000000.0);
        }
        else if ((Gage[v.index].isUsed == 0))
        {
            return 0.0;
        }
        else if ((v.attribute == 0))
        {
            return Gage[v.index].rainfall;
        }
        else
        {
            return gage_getPastRain(v.index, v.attribute);
        }
    }

    // controls.c:1405
    static int compareTimes(double lhsValue, int relation, double rhsValue, double halfStep)
    {
        if ((relation == EQ))
        {
            if (((lhsValue >= (rhsValue - halfStep)) && (lhsValue < (rhsValue + halfStep))))
            {
                return 1;
            }
            return 0;
        }
        else if ((relation == NE))
        {
            if (((lhsValue < (rhsValue - halfStep)) || (lhsValue >= (rhsValue + halfStep))))
            {
                return 1;
            }
            return 0;
        }
        else
        {
            return compareValues(lhsValue, relation, rhsValue);
        }
    }

    // controls.c:1432
    static int compareValues(double lhsValue, int relation, double rhsValue)
    {
        SetPoint = rhsValue;
        ControlValue = lhsValue;
        switch (relation) {
            case EQ:
                if ((lhsValue == rhsValue))
                {
                    return 1;
                }
                break;
            case NE:
                if ((lhsValue != rhsValue))
                {
                    return 1;
                }
                break;
            case LT:
                if ((lhsValue < rhsValue))
                {
                    return 1;
                }
                break;
            case LE:
                if ((lhsValue <= rhsValue))
                {
                    return 1;
                }
                break;
            case GT:
                if ((lhsValue > rhsValue))
                {
                    return 1;
                }
                break;
            case GE:
                if ((lhsValue >= rhsValue))
                {
                    return 1;
                }
                break;
        }
        return 0;
    }

    // controls.c:1455
    static void clearActionList()
    {
        TActionList listItem = null;
        listItem = ActionList;
        while ((listItem != null))
        {
            listItem.action = (null);
            listItem = listItem.next;
        }
    }

    // controls.c:1473
    static void deleteActionList()
    {
        TActionList listItem = null;
        TActionList nextItem = null;
        listItem = ActionList;
        while ((listItem != null))
        {
            nextItem = listItem.next;
            R.free(listItem);
            listItem = nextItem;
        }
        ActionList = (null);
    }

    // controls.c:1494
    static void deleteRules()
    {
        TPremise p = null;
        TPremise pnext = null;
        TAction a = null;
        TAction anext = null;
        int r = 0;
        for (r = 0; (r < RuleCount); r++)
        {
            p = Rules[r].firstPremise;
            while ((p != null))
            {
                pnext = p.next;
                R.free(p);
                p = pnext;
            }
            a = Rules[r].thenActions;
            while ((a != null))
            {
                anext = a.next;
                R.free(a);
                a = anext;
            }
            a = Rules[r].elseActions;
            while ((a != null))
            {
                anext = a.next;
                R.free(a);
                a = anext;
            }
        }
        {
            if ((Rules != null))
            {
                R.free(Rules);
                Rules = (null);
            }
        }
        ;
        RuleCount = 0;
    }

    // controls.c:1536
    static int findExactMatch(CPtr s, CPtr[] keyword)
    {
        int i = 0;
        while ((keyword[i] != null))
        {
            if ((strcomp(s, keyword[i]) != 0))
            {
                return (i);
            }
            i++;
        }
        return ((-1));
    }

}
