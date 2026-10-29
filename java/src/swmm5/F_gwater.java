// Generated from EPA SWMM 5.2.4 (gwater.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_gwater {
    static double GWTOL = 0.0001;
    static double XTOL = 0.001;
    static CPtr[] GWVarWords = new CPtr[] { L707, L708, L709, L710, L711, L712, L713, L714, L715, L716, L717, null };
    static double Area = 0.0;
    static double gwater_Infil = 0.0;
    static double MaxEvap = 0.0;
    static double AvailEvap = 0.0;
    static double UpperEvap = 0.0;
    static double LowerEvap = 0.0;
    static double UpperPerc = 0.0;
    static double LowerLoss = 0.0;
    static double GWFlow = 0.0;
    static double MaxUpperPerc = 0.0;
    static double MaxGWFlowPos = 0.0;
    static double MaxGWFlowNeg = 0.0;
    static double FracPerv = 0.0;
    static double TotalDepth = 0.0;
    static double Theta = 0.0;
    static double HydCon = 0.0;
    static double Hgw = 0.0;
    static double Hstar = 0.0;
    static double Hsw = 0.0;
    static double gwater_Tstep = 0.0;
    static TAquifer A = new TAquifer();
    static TGroundwater GW = null;
    static MathExpr LatFlowExpr = null;
    static MathExpr DeepFlowExpr = null;

    // gwater.c:119
    static int gwater_readAquiferParams(int j, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int p = 0;
        double[] x = new double[12];
        CPtr id = null;
        if ((ntoks < 13))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(AQUIFER, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        for (i = 0; (i < 11); i++)
        {
            x[i] = 0.0;
        }
        for (i = 1; (i < 13); i++)
        {
            if (!(getDouble(tok[i], new DElem(x, (i - 1))) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[i]);
            }
        }
        p = (-1);
        if ((ntoks > 13))
        {
            p = project_findObject(TIMEPATTERN, tok[13]);
            if ((p < 0))
            {
                return error_setInpError(ERR_NAME, tok[13]);
            }
        }
        Aquifer[j].ID = id;
        Aquifer[j].porosity = x[0];
        Aquifer[j].wiltingPoint = x[1];
        Aquifer[j].fieldCapacity = x[2];
        Aquifer[j].conductivity = (x[3] / UCF(RAINFALL));
        Aquifer[j].conductSlope = x[4];
        Aquifer[j].tensionSlope = (x[5] / UCF(LENGTH));
        Aquifer[j].upperEvapFrac = x[6];
        Aquifer[j].lowerEvapDepth = (x[7] / UCF(LENGTH));
        Aquifer[j].lowerLossCoeff = (x[8] / UCF(RAINFALL));
        Aquifer[j].bottomElev = (x[9] / UCF(LENGTH));
        Aquifer[j].waterTableElev = (x[10] / UCF(LENGTH));
        Aquifer[j].upperMoisture = x[11];
        Aquifer[j].upperEvapPat = p;
        return 0;
    }

    // gwater.c:179
    static int gwater_readGroundwaterParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        double[] x = new double[11];
        TGroundwater gw = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(SUBCATCH, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if ((ntoks < 11))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        k = project_findObject(AQUIFER, tok[1]);
        if ((k < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        n = project_findObject(NODE, tok[2]);
        if ((n < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        for (i = 0; (i < 7); i++)
        {
            if (!(getDouble(tok[(i + 3)], new DElem(x, i)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[(i + 3)]);
            }
        }
        for (i = 7; (i < 11); i++)
        {
            x[i] = (-10000000000.0);
            m = (i + 3);
            if (((ntoks > m) && (tok[m].g(0) != 42)))
            {
                if (!(getDouble(tok[m], new DElem(x, i)) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[m]);
                }
                if ((i < 10))
                {
                    x[i] /= (UCF(LENGTH));
                }
            }
        }
        if (!(Subcatch[j].groundwater != null))
        {
            gw = new TGroundwater();
            if (!(gw != null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
            Subcatch[j].groundwater = gw;
        }
        else
        {
            gw = Subcatch[j].groundwater;
        }
        gw.aquifer = k;
        gw.node = n;
        gw.surfElev = (x[0] / UCF(LENGTH));
        gw.a1 = x[1];
        gw.b1 = x[2];
        gw.a2 = x[3];
        gw.b2 = x[4];
        gw.a3 = x[5];
        gw.fixedDepth = (x[6] / UCF(LENGTH));
        gw.nodeElev = x[7];
        gw.bottomElev = x[8];
        gw.waterTableElev = x[9];
        gw.upperMoisture = x[10];
        return 0;
    }

    // gwater.c:258
    static int gwater_readFlowExpression(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        CPtr exprStr = R.buf(1025);
        MathExpr expr = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(SUBCATCH, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        k = 1;
        if ((match(tok[1], L158) != 0))
        {
            k = 1;
        }
        else if ((match(tok[1], L159) != 0))
        {
            k = 2;
        }
        else
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        sstrncpy(exprStr, tok[2], 1024);
        for (i = 3; (i < ntoks); i++)
        {
            sstrcat(exprStr, L33, (1024 + 1));
            sstrcat(exprStr, tok[i], (1024 + 1));
        }
        if ((k == 1))
        {
            mathexpr_delete(Subcatch[j].gwLatFlowExpr);
        }
        else
        {
            mathexpr_delete(Subcatch[j].gwDeepFlowExpr);
        }
        expr = mathexpr_create(exprStr, F_gwater::gwater_getVariableIndex);
        if ((expr == null))
        {
            return error_setInpError(ERR_MATH_EXPR, L0);
        }
        if ((k == 1))
        {
            Subcatch[j].gwLatFlowExpr = expr;
        }
        else
        {
            Subcatch[j].gwDeepFlowExpr = expr;
        }
        return 0;
    }

    // gwater.c:315
    static void gwater_deleteFlowExpression(int j)
    {
        mathexpr_delete(Subcatch[j].gwLatFlowExpr);
        mathexpr_delete(Subcatch[j].gwDeepFlowExpr);
    }

    // gwater.c:328
    static void gwater_validateAquifer(int j)
    {
        int p = 0;
        if ((((((((((((Aquifer[j].porosity <= 0.0) || (Aquifer[j].fieldCapacity >= Aquifer[j].porosity)) || (Aquifer[j].wiltingPoint >= Aquifer[j].fieldCapacity)) || (Aquifer[j].conductivity <= 0.0)) || (Aquifer[j].conductSlope < 0.0)) || (Aquifer[j].tensionSlope < 0.0)) || (Aquifer[j].upperEvapFrac < 0.0)) || (Aquifer[j].lowerEvapDepth < 0.0)) || (Aquifer[j].waterTableElev < Aquifer[j].bottomElev)) || (Aquifer[j].upperMoisture > Aquifer[j].porosity)) || (Aquifer[j].upperMoisture < Aquifer[j].wiltingPoint)))
        {
            report_writeErrorMsg(ERR_AQUIFER_PARAMS, Aquifer[j].ID);
        }
        p = Aquifer[j].upperEvapPat;
        if (((p >= 0) && (Pattern[p].type != MONTHLY_PATTERN)))
        {
            report_writeErrorMsg(ERR_AQUIFER_PARAMS, Aquifer[j].ID);
        }
    }

    // gwater.c:359
    static void gwater_validate(int j)
    {
        TAquifer a = new TAquifer();
        TGroundwater gw = null;
        gw = Subcatch[j].groundwater;
        if ((gw != null))
        {
            a = Aquifer[gw.aquifer].copy();
            if ((gw.bottomElev == (-10000000000.0)))
            {
                gw.bottomElev = a.bottomElev;
            }
            if ((gw.waterTableElev == (-10000000000.0)))
            {
                gw.waterTableElev = a.waterTableElev;
            }
            if ((gw.upperMoisture == (-10000000000.0)))
            {
                gw.upperMoisture = a.upperMoisture;
            }
            if ((gw.surfElev < gw.waterTableElev))
            {
                report_writeErrorMsg(ERR_GROUND_ELEV, Subcatch[j].ID);
            }
        }
    }

    // gwater.c:382
    static void gwater_initState(int j)
    {
        TAquifer a = new TAquifer();
        TGroundwater gw = null;
        gw = Subcatch[j].groundwater;
        if ((gw != null))
        {
            a = Aquifer[gw.aquifer].copy();
            gw.theta = gw.upperMoisture;
            if ((gw.theta >= a.porosity))
            {
                gw.theta = (a.porosity - XTOL);
            }
            gw.lowerDepth = (gw.waterTableElev - gw.bottomElev);
            if ((gw.lowerDepth >= (gw.surfElev - gw.bottomElev)))
            {
                gw.lowerDepth = ((gw.surfElev - gw.bottomElev) - XTOL);
            }
            gw.oldFlow = 0.0;
            gw.newFlow = 0.0;
            gw.evapLoss = 0.0;
            gw.maxInfilVol = ((((gw.surfElev - gw.waterTableElev)) * ((a.porosity - gw.theta))) / subcatch_getFracPerv(j));
        }
    }

    // gwater.c:425
    static void gwater_getState(int j, double[] x)
    {
        TGroundwater gw = Subcatch[j].groundwater;
        x[0] = gw.theta;
        x[1] = (gw.bottomElev + gw.lowerDepth);
        x[2] = gw.newFlow;
        x[3] = gw.maxInfilVol;
    }

    // gwater.c:441
    static void gwater_setState(int j, double[] x)
    {
        TGroundwater gw = Subcatch[j].groundwater;
        if ((gw == null))
        {
            return;
        }
        gw.theta = x[0];
        gw.lowerDepth = (x[1] - gw.bottomElev);
        gw.oldFlow = x[2];
        if ((x[3] != (-10000000000.0)))
        {
            gw.maxInfilVol = x[3];
        }
    }

    // gwater.c:458
    static double gwater_getVolume(int j)
    {
        TAquifer a = new TAquifer();
        TGroundwater gw = null;
        double upperDepth = 0.0;
        gw = Subcatch[j].groundwater;
        if ((gw == null))
        {
            return 0.0;
        }
        a = Aquifer[gw.aquifer].copy();
        upperDepth = ((gw.surfElev - gw.bottomElev) - gw.lowerDepth);
        return (((upperDepth * gw.theta)) + ((gw.lowerDepth * a.porosity)));
    }

    // gwater.c:477
    static void gwater_getGroundwater(int j, double evap, double infil, double tStep)
    {
        int n = 0;
        double[] x = new double[2];
        double vUpper = 0.0;
        double nodeFlow = 0.0;
        GW = Subcatch[j].groundwater;
        if ((GW == null))
        {
            return;
        }
        LatFlowExpr = Subcatch[j].gwLatFlowExpr;
        DeepFlowExpr = Subcatch[j].gwDeepFlowExpr;
        A = Aquifer[GW.aquifer].copy();
        FracPerv = subcatch_getFracPerv(j);
        if ((FracPerv <= 0.0))
        {
            return;
        }
        Area = Subcatch[j].area;
        infil = ((infil / Area) / tStep);
        gwater_Infil = infil;
        gwater_Tstep = tStep;
        evap = ((evap / Area) / tStep);
        MaxEvap = (Evap.rate * FracPerv);
        AvailEvap = (((((((MaxEvap - evap))) >= (0.0))) ? (((MaxEvap - evap))) : (0.0)));
        TotalDepth = (GW.surfElev - GW.bottomElev);
        if ((TotalDepth <= 0.0))
        {
            return;
        }
        n = GW.node;
        if ((GW.nodeElev != (-10000000000.0)))
        {
            Hstar = (GW.nodeElev - GW.bottomElev);
        }
        else
        {
            Hstar = (Node[n].invertElev - GW.bottomElev);
        }
        if ((GW.fixedDepth > 0.0))
        {
            Hsw = ((GW.fixedDepth + Node[n].invertElev) - GW.bottomElev);
        }
        else
        {
            Hsw = ((Node[n].newDepth + Node[n].invertElev) - GW.bottomElev);
        }
        x[THETA] = GW.theta;
        x[LOWERDEPTH] = GW.lowerDepth;
        vUpper = (((TotalDepth - x[LOWERDEPTH])) * ((x[THETA] - A.fieldCapacity)));
        vUpper = (((((0.0) >= (vUpper))) ? (0.0) : (vUpper)));
        MaxUpperPerc = (vUpper / tStep);
        MaxGWFlowPos = ((x[LOWERDEPTH] * A.porosity) / tStep);
        MaxGWFlowNeg = ((((TotalDepth - x[LOWERDEPTH])) * ((A.porosity - x[THETA]))) / tStep);
        nodeFlow = (((Node[n].inflow + (Node[n].newVolume / tStep))) / Area);
        MaxGWFlowNeg = (-(((((MaxGWFlowNeg) <= (nodeFlow))) ? (MaxGWFlowNeg) : (nodeFlow))));
        odesolve_integrate(x, 2, 0, tStep, GWTOL, tStep, F_gwater::getDxDt);
        x[THETA] = (((((x[THETA]) >= (A.wiltingPoint))) ? (x[THETA]) : (A.wiltingPoint)));
        if ((x[THETA] >= A.porosity))
        {
            x[THETA] = (A.porosity - XTOL);
            x[LOWERDEPTH] = (TotalDepth - XTOL);
        }
        x[LOWERDEPTH] = (((((x[LOWERDEPTH]) >= (0.0))) ? (x[LOWERDEPTH]) : (0.0)));
        if ((x[LOWERDEPTH] >= TotalDepth))
        {
            x[LOWERDEPTH] = (TotalDepth - XTOL);
        }
        GW.theta = x[THETA];
        GW.lowerDepth = x[LOWERDEPTH];
        getFluxes(GW.theta, GW.lowerDepth);
        GW.oldFlow = GW.newFlow;
        GW.newFlow = GWFlow;
        GW.evapLoss = (UpperEvap + LowerEvap);
        GW.maxInfilVol = ((((TotalDepth - x[LOWERDEPTH])) * ((A.porosity - x[THETA]))) / FracPerv);
        updateMassBal(Area, tStep);
        stats_updateGwaterStats(j, infil, GW.evapLoss, GWFlow, LowerLoss, GW.theta, (GW.lowerDepth + GW.bottomElev), tStep);
    }

    // gwater.c:605
    static void updateMassBal(double area, double tStep)
    {
        double vInfil = 0.0;
        double vUpperEvap = 0.0;
        double vLowerEvap = 0.0;
        double vLowerPerc = 0.0;
        double vGwater = 0.0;
        double ft2sec = (area * tStep);
        vInfil = (gwater_Infil * ft2sec);
        vUpperEvap = (UpperEvap * ft2sec);
        vLowerEvap = (LowerEvap * ft2sec);
        vLowerPerc = (LowerLoss * ft2sec);
        vGwater = ((0.5 * ((GW.oldFlow + GW.newFlow))) * ft2sec);
        massbal_updateGwaterTotals(vInfil, vUpperEvap, vLowerEvap, vLowerPerc, vGwater);
    }

    // gwater.c:631
    static void getFluxes(double theta, double lowerDepth)
    {
        double upperDepth = 0.0;
        lowerDepth = (((((lowerDepth) >= (0.0))) ? (lowerDepth) : (0.0)));
        lowerDepth = (((((lowerDepth) <= (TotalDepth))) ? (lowerDepth) : (TotalDepth)));
        upperDepth = (TotalDepth - lowerDepth);
        Hgw = lowerDepth;
        Theta = theta;
        gwater_getEvapRates(theta, upperDepth);
        UpperPerc = getUpperPerc(theta, upperDepth);
        UpperPerc = (((((UpperPerc) <= (MaxUpperPerc))) ? (UpperPerc) : (MaxUpperPerc)));
        if ((DeepFlowExpr != null))
        {
            LowerLoss = (mathexpr_eval(DeepFlowExpr, F_gwater::gwater_getVariableValue) / UCF(RAINFALL));
        }
        else
        {
            LowerLoss = ((A.lowerLossCoeff * lowerDepth) / TotalDepth);
        }
        LowerLoss = (((((LowerLoss) <= ((lowerDepth / gwater_Tstep)))) ? (LowerLoss) : ((lowerDepth / gwater_Tstep))));
        GWFlow = getGWFlow(lowerDepth);
        if ((LatFlowExpr != null))
        {
            GWFlow += (mathexpr_eval(LatFlowExpr, F_gwater::gwater_getVariableValue) / UCF(GWFLOW));
        }
        if ((GWFlow >= 0.0))
        {
            GWFlow = (((((GWFlow) <= (MaxGWFlowPos))) ? (GWFlow) : (MaxGWFlowPos)));
        }
        else
        {
            GWFlow = (((((GWFlow) >= (MaxGWFlowNeg))) ? (GWFlow) : (MaxGWFlowNeg)));
        }
    }

    // gwater.c:677
    static void getDxDt(double t, double[] x, double[] dxdt)
    {
        double qUpper = 0.0;
        double qLower = 0.0;
        double denom = 0.0;
        getFluxes(x[THETA], x[LOWERDEPTH]);
        qUpper = ((gwater_Infil - UpperEvap) - UpperPerc);
        qLower = (((UpperPerc - LowerLoss) - LowerEvap) - GWFlow);
        denom = (TotalDepth - x[LOWERDEPTH]);
        if ((denom > 0.0))
        {
            dxdt[THETA] = (qUpper / denom);
        }
        else
        {
            dxdt[THETA] = 0.0;
        }
        denom = (A.porosity - x[THETA]);
        if ((denom > 0.0))
        {
            dxdt[LOWERDEPTH] = (qLower / denom);
        }
        else
        {
            dxdt[LOWERDEPTH] = 0.0;
        }
    }

    // gwater.c:713
    static void gwater_getEvapRates(double theta, double upperDepth)
    {
        int p = 0;
        int month = 0;
        double f = 0.0;
        double lowerFrac = 0.0;
        double upperFrac = 0.0;
        UpperEvap = 0.0;
        LowerEvap = 0.0;
        if ((gwater_Infil > 0.0))
        {
            return;
        }
        upperFrac = A.upperEvapFrac;
        f = 1.0;
        p = A.upperEvapPat;
        if ((p >= 0))
        {
            month = datetime_monthOfYear(getDateTime(NewRunoffTime));
            f = Pattern[p].factor[(month - 1)];
        }
        upperFrac *= f;
        if ((theta > A.wiltingPoint))
        {
            UpperEvap = (upperFrac * MaxEvap);
            UpperEvap = (((((UpperEvap) <= (AvailEvap))) ? (UpperEvap) : (AvailEvap)));
        }
        if ((A.lowerEvapDepth > 0.0))
        {
            lowerFrac = (((A.lowerEvapDepth - upperDepth)) / A.lowerEvapDepth);
            lowerFrac = (((((0.0) >= (lowerFrac))) ? (0.0) : (lowerFrac)));
            lowerFrac = (((((lowerFrac) <= (1.0))) ? (lowerFrac) : (1.0)));
            LowerEvap = ((lowerFrac * ((1.0 - upperFrac))) * MaxEvap);
            LowerEvap = (((((LowerEvap) <= (((AvailEvap - UpperEvap))))) ? (LowerEvap) : (((AvailEvap - UpperEvap)))));
        }
    }

    // gwater.c:769
    static double getUpperPerc(double theta, double upperDepth)
    {
        double delta = 0.0;
        double dhdz = 0.0;
        double hydcon = 0.0;
        if (((upperDepth <= 0.0) || (theta <= A.fieldCapacity)))
        {
            return 0.0;
        }
        delta = (theta - A.porosity);
        hydcon = (A.conductivity * Glibm.exp((delta * A.conductSlope)));
        delta = (theta - A.fieldCapacity);
        dhdz = (1.0 + (((A.tensionSlope * 2.0) * delta) / upperDepth));
        HydCon = hydcon;
        return (hydcon * dhdz);
    }

    // gwater.c:799
    static double getGWFlow(double lowerDepth)
    {
        double q = 0.0;
        double t1 = 0.0;
        double t2 = 0.0;
        double t3 = 0.0;
        if ((lowerDepth <= Hstar))
        {
            return 0.0;
        }
        if ((GW.b1 == 0.0))
        {
            t1 = GW.a1;
        }
        else
        {
            t1 = (GW.a1 * Glibm.pow((((lowerDepth - Hstar)) * UCF(LENGTH)), GW.b1));
        }
        if ((GW.b2 == 0.0))
        {
            t2 = GW.a2;
        }
        else if ((Hsw > Hstar))
        {
            t2 = (GW.a2 * Glibm.pow((((Hsw - Hstar)) * UCF(LENGTH)), GW.b2));
        }
        else
        {
            t2 = 0.0;
        }
        t3 = ((((GW.a3 * lowerDepth) * Hsw) * UCF(LENGTH)) * UCF(LENGTH));
        q = ((((t1 - t2) + t3)) / UCF(GWFLOW));
        if (((q < 0.0) && (GW.a3 != 0.0)))
        {
            q = 0.0;
        }
        return q;
    }

    // gwater.c:834
    static int gwater_getVariableIndex(CPtr s)
    {
        int k = 0;
        k = findmatch(s, GWVarWords);
        if ((k >= 0))
        {
            return k;
        }
        return (-1);
    }

    // gwater.c:850
    static double gwater_getVariableValue(int varIndex)
    {
        switch (varIndex) {
            case gwvHGW:
                return (Hgw * UCF(LENGTH));
            case gwvHSW:
                return (Hsw * UCF(LENGTH));
            case gwvHCB:
                return (Hstar * UCF(LENGTH));
            case gwvHGS:
                return (TotalDepth * UCF(LENGTH));
            case gwvKS:
                return (A.conductivity * UCF(RAINFALL));
            case gwvK:
                return (HydCon * UCF(RAINFALL));
            case gwvTHETA:
                return Theta;
            case gwvPHI:
                return A.porosity;
            case gwvFI:
                return (gwater_Infil * UCF(RAINFALL));
            case gwvFU:
                return (UpperPerc * UCF(RAINFALL));
            case gwvA:
                return (Area * UCF(LANDAREA));
            default:
                return 0.0;
        }
    }

}
