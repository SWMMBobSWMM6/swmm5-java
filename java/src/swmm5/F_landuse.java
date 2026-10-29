// Generated from EPA SWMM 5.2.4 (landuse.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_landuse {
    // landuse.c:51
    static int landuse_readParams(int j, CPtr[] tok, int ntoks)
    {
        CPtr id = null;
        if ((ntoks < 1))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(LANDUSE, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        Landuse[j].ID = id;
        if ((ntoks > 1))
        {
            if ((ntoks < 4))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if (!(getDouble(tok[1], new DField<TLanduse>(Landuse[j], __o -> __o.sweepInterval, (__o, __x) -> __o.sweepInterval = __x)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[1]);
            }
            if (!(getDouble(tok[2], new DField<TLanduse>(Landuse[j], __o -> __o.sweepRemoval, (__o, __x) -> __o.sweepRemoval = __x)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[2]);
            }
            if (!(getDouble(tok[3], new DField<TLanduse>(Landuse[j], __o -> __o.sweepDays0, (__o, __x) -> __o.sweepDays0 = __x)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[3]);
            }
        }
        else
        {
            Landuse[j].sweepInterval = 0.0;
            Landuse[j].sweepRemoval = 0.0;
            Landuse[j].sweepDays0 = 0.0;
        }
        if (((Landuse[j].sweepRemoval < 0.0) || (Landuse[j].sweepRemoval > 1.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[2]);
        }
        return 0;
    }

    // landuse.c:92
    static int landuse_readPollutParams(int j, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int k = 0;
        int coPollut = 0;
        int snowFlag = 0;
        double[] x = new double[4];
        DBox coFrac = new DBox(0.0);
        DBox cDWF = new DBox(0.0);
        DBox cInit = new DBox(0.0);
        CPtr id = null;
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(POLLUT, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        k = findmatch(tok[1], QualUnitsWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        for (i = 2; (i <= 4); i++)
        {
            if (((!(getDouble(tok[i], new DElem(x, (i - 2))) != 0)) || (x[(i - 2)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[i]);
            }
        }
        if (!(getDouble(tok[5], new DElem(x, 3)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[5]);
        }
        snowFlag = 0;
        coPollut = (-1);
        coFrac.v = 0.0;
        cDWF.v = 0.0;
        cInit.v = 0.0;
        if ((ntoks >= 7))
        {
            snowFlag = findmatch(tok[6], NoYesWords);
            if ((snowFlag < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[6]);
            }
        }
        if ((ntoks >= 9))
        {
            if (!(strcomp(tok[7], L275) != 0))
            {
                coPollut = project_findObject(POLLUT, tok[7]);
                if ((coPollut < 0))
                {
                    return error_setInpError(ERR_NAME, tok[7]);
                }
                if (((!(getDouble(tok[8], coFrac) != 0)) || (coFrac.v < 0.0)))
                {
                    return error_setInpError(ERR_NUMBER, tok[8]);
                }
            }
        }
        if ((ntoks >= 10))
        {
            if (((!(getDouble(tok[9], cDWF) != 0)) || (cDWF.v < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[9]);
            }
        }
        if ((ntoks >= 11))
        {
            if (((!(getDouble(tok[10], cInit) != 0)) || (cInit.v < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[9]);
            }
        }
        Pollut[j].ID = id;
        Pollut[j].units = k;
        if ((Pollut[j].units == MG))
        {
            Pollut[j].mcf = UCF(MASS);
        }
        else if ((Pollut[j].units == UG))
        {
            Pollut[j].mcf = (UCF(MASS) / 1000.0);
        }
        else
        {
            Pollut[j].mcf = 1.0;
        }
        Pollut[j].pptConcen = x[0];
        Pollut[j].gwConcen = x[1];
        Pollut[j].rdiiConcen = x[2];
        Pollut[j].kDecay = (x[3] / 86400);
        Pollut[j].snowOnly = snowFlag;
        Pollut[j].coPollut = coPollut;
        Pollut[j].coFraction = coFrac.v;
        Pollut[j].dwfConcen = cDWF.v;
        Pollut[j].initConcen = cInit.v;
        return 0;
    }

    // landuse.c:190
    static int landuse_readBuildupParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int n = 0;
        int p = 0;
        double[] c = new double[] { 0, 0, 0 };
        double tmax = 0.0;
        if ((ntoks < 3))
        {
            return 0;
        }
        j = project_findObject(LANDUSE, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        p = project_findObject(POLLUT, tok[1]);
        if ((p < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        k = findmatch(tok[2], BuildupTypeWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[2]);
        }
        Landuse[j].buildupFunc[p].funcType = k;
        if ((k > NO_BUILDUP))
        {
            if ((ntoks < 7))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if ((k != EXTERNAL_BUILDUP))
            {
                for (i = 0; (i < 3); i++)
                {
                    if (((!(getDouble(tok[(i + 3)], new DElem(c, i)) != 0)) || (c[i] < 0.0)))
                    {
                        return error_setInpError(ERR_NUMBER, tok[(i + 3)]);
                    }
                }
            }
            n = findmatch(tok[6], NormalizerWords);
            if ((n < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[6]);
            }
            Landuse[j].buildupFunc[p].normalizer = n;
        }
        switch (Landuse[j].buildupFunc[p].funcType) {
            case POWER_BUILDUP:
                if (((c[2] > 0.0) && (((c[2] < 0.01) || (c[2] > 10.0)))))
                {
                    return error_setInpError(ERR_KEYWORD, tok[5]);
                }
                if (((c[1] * c[2]) == 0.0))
                {
                    tmax = 0.0;
                }
                else if (((Glibm.log10(c[0]) / c[2]) > 3.5))
                {
                    tmax = 3650.0;
                }
                else
                {
                    tmax = Glibm.pow((c[0] / c[1]), (1.0 / c[2]));
                }
                break;
            case EXPON_BUILDUP:
                if ((c[1] == 0.0))
                {
                    tmax = 0.0;
                }
                else
                {
                    tmax = ((-(Glibm.log(0.001))) / c[1]);
                }
                break;
            case SATUR_BUILDUP:
                tmax = (1000.0 * c[2]);
                break;
            case EXTERNAL_BUILDUP:
                if (((!(getDouble(tok[3], new DElem(c, 0)) != 0)) || (c[0] < 0.0)))
                {
                    return error_setInpError(ERR_NUMBER, tok[3]);
                }
                if (((!(getDouble(tok[4], new DElem(c, 1)) != 0)) || (c[1] < 0.0)))
                {
                    return error_setInpError(ERR_NUMBER, tok[3]);
                }
                n = project_findObject(TSERIES, tok[5]);
                if ((n < 0))
                {
                    return error_setInpError(ERR_NAME, tok[4]);
                }
                Tseries[n].refersTo = EXTERNAL_BUILDUP;
                c[2] = n;
                tmax = 0.0;
                break;
            default:
                tmax = 0.0;
        }
        Landuse[j].buildupFunc[p].coeff[0] = c[0];
        Landuse[j].buildupFunc[p].coeff[1] = c[1];
        Landuse[j].buildupFunc[p].coeff[2] = c[2];
        Landuse[j].buildupFunc[p].maxDays = tmax;
        return 0;
    }

    // landuse.c:281
    static int landuse_readWashoffParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int p = 0;
        int func = 0;
        double[] x = new double[4];
        if ((ntoks < 3))
        {
            return 0;
        }
        for (i = 0; (i < 4); i++)
        {
            x[i] = 0.0;
        }
        func = NO_WASHOFF;
        j = project_findObject(LANDUSE, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        p = project_findObject(POLLUT, tok[1]);
        if ((p < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        if ((ntoks > 2))
        {
            func = findmatch(tok[2], WashoffTypeWords);
            if ((func < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[2]);
            }
            if ((func != NO_WASHOFF))
            {
                if ((ntoks < 5))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                if (!(getDouble(tok[3], new DElem(x, 0)) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[3]);
                }
                if (!(getDouble(tok[4], new DElem(x, 1)) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[4]);
                }
                if ((ntoks >= 6))
                {
                    if (!(getDouble(tok[5], new DElem(x, 2)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[5]);
                    }
                }
                if ((ntoks >= 7))
                {
                    if (!(getDouble(tok[6], new DElem(x, 3)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[6]);
                    }
                }
            }
        }
        if ((x[0] < 0.0))
        {
            return error_setInpError(ERR_NUMBER, tok[3]);
        }
        if (((x[1] < (-10.0)) || (x[1] > 10.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[4]);
        }
        ;
        if (((x[2] < 0.0) || (x[2] > 100.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[5]);
        }
        if (((x[3] < 0.0) || (x[3] > 100.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[6]);
        }
        if ((func == EXPON_WASHOFF))
        {
            x[0] /= 3600.0;
        }
        if ((func == RATING_WASHOFF))
        {
            x[0] *= (Glibm.pow(UCF(FLOW), x[1]));
        }
        if ((func == EMC_WASHOFF))
        {
            x[0] *= 28.317;
        }
        Landuse[j].washoffFunc[p].funcType = func;
        Landuse[j].washoffFunc[p].coeff = x[0];
        Landuse[j].washoffFunc[p].expon = x[1];
        Landuse[j].washoffFunc[p].sweepEffic = (x[2] / 100.0);
        Landuse[j].washoffFunc[p].bmpEffic = (x[3] / 100.0);
        return 0;
    }

    // landuse.c:355
    static void landuse_getInitBuildup(TLandFactor[] landFactor, double[] initBuildup, double area, double curb)
    {
        int i = 0;
        int p = 0;
        double startDrySeconds = 0.0;
        double f = 0.0;
        double fArea = 0.0;
        double fCurb = 0.0;
        double buildup = 0.0;
        startDrySeconds = (StartDryDays * 86400);
        for (i = 0; (i < Nobjects[LANDUSE]); i++)
        {
            landFactor[i].lastSwept = (StartDateTime - Landuse[i].sweepDays0);
            f = landFactor[i].fraction;
            fArea = ((f * area) * UCF(LANDAREA));
            fCurb = (f * curb);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                buildup = 0.0;
                if ((initBuildup[p] > 0.0))
                {
                    buildup = (initBuildup[p] * fArea);
                }
                else
                {
                    buildup = landuse_getBuildup(i, p, fArea, fCurb, buildup, startDrySeconds);
                }
                landFactor[i].buildup[p] = buildup;
            }
        }
    }

    // landuse.c:411
    static double landuse_getBuildup(int i, int p, double area, double curb, double buildup, double tStep)
    {
        int n = 0;
        double days = 0.0;
        double perUnit = 0.0;
        if (((Landuse[i].buildupFunc[p].funcType == NO_BUILDUP) || (tStep == 0.0)))
        {
            return buildup;
        }
        n = Landuse[i].buildupFunc[p].normalizer;
        perUnit = 1.0;
        if ((n == PER_AREA))
        {
            perUnit = area;
        }
        if ((n == PER_CURB))
        {
            perUnit = curb;
        }
        if ((perUnit == 0.0))
        {
            return 0.0;
        }
        if ((Landuse[i].buildupFunc[p].funcType == EXTERNAL_BUILDUP))
        {
            return (landuse_getExternalBuildup(i, p, (buildup / perUnit), tStep) * perUnit);
        }
        days = landuse_getBuildupDays(i, p, (buildup / perUnit));
        days += (tStep / 86400);
        return (landuse_getBuildupMass(i, p, days) * perUnit);
    }

    // landuse.c:458
    static double landuse_getBuildupDays(int i, int p, double buildup)
    {
        double c0 = Landuse[i].buildupFunc[p].coeff[0];
        double c1 = Landuse[i].buildupFunc[p].coeff[1];
        double c2 = Landuse[i].buildupFunc[p].coeff[2];
        if ((buildup == 0.0))
        {
            return 0.0;
        }
        if ((buildup >= c0))
        {
            return Landuse[i].buildupFunc[p].maxDays;
        }
        switch (Landuse[i].buildupFunc[p].funcType) {
            case POWER_BUILDUP:
                if (((c1 * c2) == 0.0))
                {
                    return 0.0;
                }
                else
                {
                    return Glibm.pow(((buildup / c1)), ((1.0 / c2)));
                }
            case EXPON_BUILDUP:
                if (((c0 * c1) == 0.0))
                {
                    return 0.0;
                }
                else
                {
                    return ((-(Glibm.log((1.0 - (buildup / c0))))) / c1);
                }
            case SATUR_BUILDUP:
                if ((c0 == 0.0))
                {
                    return 0.0;
                }
                else
                {
                    return ((buildup * c2) / ((c0 - buildup)));
                }
            default:
                return 0.0;
        }
    }

    // landuse.c:494
    static double landuse_getBuildupMass(int i, int p, double days)
    {
        double b = 0.0;
        double c0 = Landuse[i].buildupFunc[p].coeff[0];
        double c1 = Landuse[i].buildupFunc[p].coeff[1];
        double c2 = Landuse[i].buildupFunc[p].coeff[2];
        if ((days == 0.0))
        {
            return 0.0;
        }
        if ((days >= Landuse[i].buildupFunc[p].maxDays))
        {
            return c0;
        }
        switch (Landuse[i].buildupFunc[p].funcType) {
            case POWER_BUILDUP:
                b = (c1 * Glibm.pow(days, c2));
                if ((b > c0))
                {
                    b = c0;
                }
                break;
            case EXPON_BUILDUP:
                b = (c0 * ((1.0 - Glibm.exp(((-days) * c1)))));
                break;
            case SATUR_BUILDUP:
                b = ((days * c0) / ((c2 + days)));
                break;
            default:
                b = 0.0;
        }
        return b;
    }

    // landuse.c:532
    static double landuse_getAvgBmpEffic(int j, int p)
    {
        int i = 0;
        double r = 0.0;
        for (i = 0; (i < Nobjects[LANDUSE]); i++)
        {
            r += (Subcatch[j].landFactor[i].fraction * Landuse[i].washoffFunc[p].bmpEffic);
        }
        return r;
    }

    // landuse.c:553
    static double landuse_getWashoffLoad(int i, int p, double area, TLandFactor[] landFactor, double runoff, double vOutflow)
    {
        double landuseArea = 0.0;
        double buildup = 0.0;
        double washoffQual = 0.0;
        double washoffLoad = 0.0;
        double bmpRemoval = 0.0;
        buildup = landFactor[i].buildup[p];
        landuseArea = (landFactor[i].fraction * area);
        washoffQual = landuse_getWashoffQual(i, p, buildup, runoff, landuseArea);
        washoffLoad = ((((washoffQual * vOutflow) * landuseArea) / area) * Pollut[p].mcf);
        if (((Landuse[i].buildupFunc[p].funcType != NO_BUILDUP) || (buildup > washoffLoad)))
        {
            washoffLoad = (((((washoffLoad) <= (buildup))) ? (washoffLoad) : (buildup)));
            buildup -= washoffLoad;
            landFactor[i].buildup[p] = buildup;
        }
        else
        {
            massbal_updateLoadingTotals(BUILDUP_LOAD, p, washoffLoad);
            landFactor[i].buildup[p] = 0.0;
        }
        bmpRemoval = (Landuse[i].washoffFunc[p].bmpEffic * washoffLoad);
        if ((bmpRemoval > 0.0))
        {
            massbal_updateLoadingTotals(BMP_REMOVAL_LOAD, p, bmpRemoval);
            washoffLoad -= bmpRemoval;
        }
        return (washoffLoad / Pollut[p].mcf);
    }

    // landuse.c:612
    static double landuse_getWashoffQual(int i, int p, double buildup, double runoff, double area)
    {
        double cWashoff = 0.0;
        double coeff = Landuse[i].washoffFunc[p].coeff;
        double expon = Landuse[i].washoffFunc[p].expon;
        int func = Landuse[i].washoffFunc[p].funcType;
        if (((func == NO_WASHOFF) || (runoff == 0.0)))
        {
            return 0.0;
        }
        if (((Landuse[i].buildupFunc[p].funcType != NO_BUILDUP) && (buildup == 0.0)))
        {
            return 0.0;
        }
        if ((func == EXPON_WASHOFF))
        {
            cWashoff = (((coeff * Glibm.pow((runoff * UCF(RAINFALL)), expon)) * buildup) / Pollut[p].mcf);
            cWashoff /= (runoff * area);
        }
        else if ((func == RATING_WASHOFF))
        {
            cWashoff = (coeff * Glibm.pow((runoff * area), (expon - 1.0)));
        }
        else if ((func == EMC_WASHOFF))
        {
            cWashoff = coeff;
        }
        return cWashoff;
    }

    // landuse.c:665
    static double landuse_getCoPollutLoad(int p, double[] washoff)
    {
        int k = 0;
        double w = 0.0;
        k = Pollut[p].coPollut;
        if ((k >= 0))
        {
            w = (Pollut[p].coFraction * washoff[k]);
            massbal_updateLoadingTotals(BUILDUP_LOAD, p, (w * Pollut[p].mcf));
            return w;
        }
        return 0.0;
    }

    // landuse.c:693
    static double landuse_getExternalBuildup(int i, int p, double buildup, double tStep)
    {
        double maxBuildup = Landuse[i].buildupFunc[p].coeff[0];
        double sf = Landuse[i].buildupFunc[p].coeff[1];
        int ts = R.D2I(Math.floor(Landuse[i].buildupFunc[p].coeff[2]));
        double rate = 0.0;
        if ((NewRunoffTime == 0.0))
        {
            return 0.0;
        }
        if ((ts >= 0))
        {
            rate = (sf * table_tseriesLookup(Tseries[ts], getDateTime(NewRunoffTime), 0));
        }
        buildup = (buildup + ((rate * tStep) / 86400));
        buildup = (((((buildup) <= (maxBuildup))) ? (buildup) : (maxBuildup)));
        return buildup;
    }

}
