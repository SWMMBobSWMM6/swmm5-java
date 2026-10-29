// Generated from EPA SWMM 5.2.4 (snow.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_snow {
    // snow.c:68
    static int snow_readMeltParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        double[] x = new double[7];
        if ((ntoks < 8))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(SNOWMELT, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if ((Snowmelt[j].ID == null))
        {
            Snowmelt[j].ID = project_findID(SNOWMELT, tok[0]);
        }
        k = findmatch(tok[1], SnowmeltWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        n = 7;
        if ((k == SNOW_REMOVAL))
        {
            n = 6;
        }
        if ((ntoks < (n + 2)))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 0; (i < 7); i++)
        {
            x[i] = 0.0;
        }
        for (i = 0; (i < n); i++)
        {
            if (!(getDouble(tok[(i + 2)], new DElem(x, i)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[(i + 2)]);
            }
        }
        if ((k == SNOW_REMOVAL))
        {
            x[6] = (-1.0);
            if ((ntoks >= 9))
            {
                m = project_findObject(SUBCATCH, tok[8]);
                if ((m < 0))
                {
                    return error_setInpError(ERR_NAME, tok[8]);
                }
                x[6] = m;
            }
        }
        setMeltParams(j, k, x);
        return 0;
    }

    // snow.c:126
    static int snow_createSnowpack(int j, int k)
    {
        TSnowpack snowpack = null;
        snowpack = new TSnowpack();
        if (!(snowpack != null))
        {
            return 0;
        }
        Subcatch[j].snowpack = snowpack;
        snowpack.snowmeltIndex = k;
        return 1;
    }

    // snow.c:144
    static void snow_initSnowpack(int j)
    {
        int i = 0;
        int k = 0;
        double f = 0.0;
        double snowDepth = 0.0;
        TSnowpack snowpack = null;
        snowpack = Subcatch[j].snowpack;
        if ((snowpack == null))
        {
            return;
        }
        k = Subcatch[j].snowpack.snowmeltIndex;
        f = Snowmelt[k].snn;
        snowpack.fArea[SNOW_PLOWABLE] = (f * Subcatch[j].fracImperv);
        snowpack.fArea[SNOW_IMPERV] = (((1.0 - f)) * Subcatch[j].fracImperv);
        snowpack.fArea[SNOW_PERV] = (1.0 - Subcatch[j].fracImperv);
        for (i = SNOW_PLOWABLE; (i <= SNOW_PERV); i++)
        {
            if ((snowpack.fArea[i] > 0.0))
            {
                snowpack.wsnow[i] = Snowmelt[k].wsnow[i];
                snowpack.fw[i] = Snowmelt[k].fwnow[i];
            }
            else
            {
                snowpack.wsnow[i] = 0.0;
                snowpack.fw[i] = 0.0;
            }
            snowpack.coldc[i] = 0.0;
            snowpack.ati[i] = Snowmelt[k].tbase[i];
            snowpack.awe[i] = 1.0;
            snowDepth += (snowpack.wsnow[i] * snowpack.fArea[i]);
        }
        Subcatch[j].newSnowDepth = snowDepth;
    }

    // snow.c:193
    static void snow_initSnowmelt(int j)
    {
        int i = 0;
        int k = 0;
        for (i = 0; (i < 3); i++)
        {
            Snowmelt[j].snn = 0.0;
            Snowmelt[j].si[i] = 0.0;
            Snowmelt[j].dhmin[i] = 0.0;
            Snowmelt[j].dhmax[i] = 0.0;
            Snowmelt[j].tbase[i] = 0.0;
            Snowmelt[j].fwfrac[i] = 0.0;
            Snowmelt[j].wsnow[i] = 0.0;
            Snowmelt[j].fwnow[i] = 0.0;
            Snowmelt[j].weplow = 1.0e6;
            for (k = 0; (k < 5); k++)
            {
                Snowmelt[j].sfrac[k] = 0.0;
            }
            Snowmelt[j].toSubcatch = (-1);
        }
    }

    // snow.c:219
    static void snow_validateSnowmelt(int j)
    {
        int k = 0;
        int err = 0;
        double sum = 0.0;
        for (k = SNOW_PLOWABLE; (k <= SNOW_PERV); k++)
        {
            if ((Snowmelt[j].dhmin[k] > Snowmelt[j].dhmax[k]))
            {
                err = 1;
            }
            if (((Snowmelt[j].fwfrac[k] < 0.0) || (Snowmelt[j].fwfrac[k] > 1.0)))
            {
                err = 1;
            }
        }
        if (((Snowmelt[j].snn < 0.0) || (Snowmelt[j].snn > 1.0)))
        {
            err = 1;
        }
        for (k = 0; (k < 5); k++)
        {
            sum += Snowmelt[j].sfrac[k];
        }
        if ((sum > 1.01))
        {
            err = 1;
        }
        if ((err != 0))
        {
            report_writeErrorMsg(ERR_SNOWPACK_PARAMS, Snowmelt[j].ID);
        }
    }

    // snow.c:251
    static void snow_getState(int i, int j, double[] x)
    {
        TSnowpack snowpack = Subcatch[i].snowpack;
        if ((snowpack == null))
        {
            return;
        }
        x[0] = snowpack.wsnow[j];
        x[1] = snowpack.fw[j];
        x[2] = snowpack.coldc[j];
        x[3] = snowpack.ati[j];
        x[4] = snowpack.awe[j];
    }

    // snow.c:270
    static void snow_setState(int i, int j, double[] x)
    {
        TSnowpack snowpack = Subcatch[i].snowpack;
        if ((snowpack == null))
        {
            return;
        }
        snowpack.wsnow[j] = x[0];
        snowpack.fw[j] = x[1];
        snowpack.coldc[j] = x[2];
        snowpack.ati[j] = x[3];
        snowpack.awe[j] = x[4];
    }

    // snow.c:290
    static void setMeltParams(int j, int k, double[] x)
    {
        int i = 0;
        if (((k >= SNOW_PLOWABLE) && (k <= SNOW_PERV)))
        {
            Snowmelt[j].dhmin[k] = ((x[0] * UCF(TEMPERATURE)) / UCF(RAINFALL));
            Snowmelt[j].dhmax[k] = ((x[1] * UCF(TEMPERATURE)) / UCF(RAINFALL));
            Snowmelt[j].tbase[k] = x[2];
            if ((UnitSystem == SI))
            {
                Snowmelt[j].tbase[k] = ((((9.0 / 5.0)) * Snowmelt[j].tbase[k]) + 32.0);
            }
            Snowmelt[j].fwfrac[k] = x[3];
            Snowmelt[j].wsnow[k] = (x[4] / UCF(RAINDEPTH));
            x[5] = (((((x[5]) <= (((x[3] * x[4]))))) ? (x[5]) : (((x[3] * x[4])))));
            Snowmelt[j].fwnow[k] = (x[5] / UCF(RAINDEPTH));
            if ((k == SNOW_PLOWABLE))
            {
                Snowmelt[j].snn = x[6];
            }
            else
            {
                Snowmelt[j].si[k] = (x[6] / UCF(RAINDEPTH));
            }
        }
        else if ((k == SNOW_REMOVAL))
        {
            Snowmelt[j].weplow = (x[0] / UCF(RAINDEPTH));
            for (i = 0; (i <= 4); i++)
            {
                Snowmelt[j].sfrac[i] = x[(i + 1)];
            }
            if ((x[6] >= 0.0))
            {
                Snowmelt[j].toSubcatch = R.D2I(((x[6] + 0.01)));
            }
            else
            {
                Snowmelt[j].toSubcatch = (-1);
            }
        }
    }

    // snow.c:341
    static void snow_setMeltCoeffs(int j, double s)
    {
        int k = 0;
        for (k = SNOW_PLOWABLE; (k <= SNOW_PERV); k++)
        {
            Snowmelt[j].dhm[k] = (0.5 * (((Snowmelt[j].dhmax[k] * ((1.0 + s))) + (Snowmelt[j].dhmin[k] * ((1.0 - s))))));
        }
    }

    // snow.c:360
    static void snow_plowSnow(int j, double tStep)
    {
        int i = 0;
        int k = 0;
        int m = 0;
        DBox rainfall = new DBox(0.0);
        DBox snowfall = new DBox(0.0);
        double exc = 0.0;
        double f = 0.0;
        double sfracTotal = 0.0;
        TSnowpack snowpack = null;
        snowpack = Subcatch[j].snowpack;
        if (!(snowpack != null))
        {
            return;
        }
        gage_getPrecip(Subcatch[j].gage, rainfall, snowfall);
        for (i = SNOW_PLOWABLE; (i <= SNOW_PERV); i++)
        {
            if ((snowpack.fArea[i] > 0.0))
            {
                snowpack.wsnow[i] += (snowfall.v * tStep);
                snowpack.imelt[i] = 0.0;
            }
        }
        if ((snowpack.fArea[SNOW_PLOWABLE] > 0.0))
        {
            k = snowpack.snowmeltIndex;
            if ((snowpack.wsnow[SNOW_PLOWABLE] >= Snowmelt[k].weplow))
            {
                exc = snowpack.wsnow[SNOW_PLOWABLE];
                f = (snowpack.fArea[SNOW_PLOWABLE] * ((Subcatch[j].area - Subcatch[j].lidArea)));
                Snow.removed += ((Snowmelt[k].sfrac[0] * exc) * f);
                sfracTotal = Snowmelt[k].sfrac[0];
                if ((snowpack.fArea[SNOW_IMPERV] > 0.0))
                {
                    f = (snowpack.fArea[SNOW_PLOWABLE] / snowpack.fArea[SNOW_IMPERV]);
                    snowpack.wsnow[SNOW_IMPERV] += ((Snowmelt[k].sfrac[1] * exc) * f);
                    sfracTotal += Snowmelt[k].sfrac[1];
                }
                if ((snowpack.fArea[SNOW_PERV] > 0.0))
                {
                    f = (snowpack.fArea[SNOW_PLOWABLE] / snowpack.fArea[SNOW_PERV]);
                    snowpack.wsnow[SNOW_PERV] += ((Snowmelt[k].sfrac[2] * exc) * f);
                    sfracTotal += Snowmelt[k].sfrac[2];
                }
                snowpack.imelt[SNOW_PLOWABLE] = ((Snowmelt[k].sfrac[3] * exc) / tStep);
                sfracTotal += Snowmelt[k].sfrac[3];
                if ((Snowmelt[k].sfrac[4] > 0.0))
                {
                    m = Snowmelt[k].toSubcatch;
                    if ((Subcatch[m].snowpack != null))
                    {
                        f = Subcatch[m].snowpack.fArea[SNOW_PERV];
                    }
                    else
                    {
                        f = 0.0;
                    }
                    if ((f > 0.0))
                    {
                        f = (snowpack.fArea[SNOW_PLOWABLE] / f);
                        Subcatch[m].snowpack.wsnow[SNOW_PERV] += ((Snowmelt[k].sfrac[4] * exc) * f);
                        sfracTotal += Snowmelt[k].sfrac[4];
                    }
                }
                sfracTotal = (((((sfracTotal) <= (1.0))) ? (sfracTotal) : (1.0)));
                snowpack.wsnow[SNOW_PLOWABLE] = (exc * ((1.0 - sfracTotal)));
            }
        }
    }

    // snow.c:458
    static double snow_getSnowMelt(int j, double rainfall, double snowfall, double tStep, double[] netPrecip)
    {
        int i = 0;
        double rmelt = 0.0;
        double smelt = 0.0;
        double asc = 0.0;
        double snowDepth = 0.0;
        double impervPrecip = 0.0;
        TSnowpack snowpack = null;
        snowpack = Subcatch[j].snowpack;
        rmelt = getRainmelt(rainfall);
        for (i = SNOW_PLOWABLE; (i <= SNOW_PERV); i++)
        {
            if ((snowpack.wsnow[i] <= (0.001 / 12.0)))
            {
                asc = 0.0;
                smelt = 0.0;
                snowpack.imelt[i] += (((snowpack.wsnow[i] + snowpack.fw[i])) / tStep);
                snowpack.wsnow[i] = 0.0;
                snowpack.fw[i] = 0.0;
                snowpack.coldc[i] = 0.0;
            }
            else
            {
                asc = getArealDepletion(snowpack, i, snowfall, tStep);
                smelt = meltSnowpack(snowpack, i, rmelt, asc, snowfall, tStep);
                smelt = routeSnowmelt(snowpack, i, smelt, asc, rainfall, tStep);
            }
            netPrecip[i] = ((smelt + snowpack.imelt[i]) + (rainfall * ((1.0 - asc))));
            snowDepth += (snowpack.wsnow[i] * snowpack.fArea[i]);
        }
        if ((Subcatch[j].fracImperv > 0.0))
        {
            impervPrecip = ((((netPrecip[SNOW_PLOWABLE] * snowpack.fArea[SNOW_PLOWABLE]) + (netPrecip[SNOW_IMPERV] * snowpack.fArea[SNOW_IMPERV]))) / Subcatch[j].fracImperv);
            netPrecip[IMPERV0] = impervPrecip;
            netPrecip[IMPERV1] = impervPrecip;
        }
        return snowDepth;
    }

    // snow.c:532
    static double snow_getSnowCover(int j)
    {
        int i = 0;
        double snowCover = 0.0;
        TSnowpack snowpack = null;
        snowpack = Subcatch[j].snowpack;
        if (!(snowpack != null))
        {
            return 0.0;
        }
        for (i = SNOW_PLOWABLE; (i <= SNOW_PERV); i++)
        {
            snowCover += (((snowpack.wsnow[i] + snowpack.fw[i])) * snowpack.fArea[i]);
        }
        return (snowCover * ((Subcatch[j].area - Subcatch[j].lidArea)));
    }

    // snow.c:555
    static double getArealDepletion(TSnowpack snowpack, int i, double snowfall, double tStep)
    {
        int k = 0;
        double asc = 0.0;
        double si = 0.0;
        double awesi = 0.0;
        double awe = 0.0;
        double sba = 0.0;
        double sbws = 0.0;
        if ((i == SNOW_PLOWABLE))
        {
            return 1.0;
        }
        k = snowpack.snowmeltIndex;
        si = Snowmelt[k].si[i];
        if (((si == 0.0) || (snowpack.wsnow[i] >= si)))
        {
            snowpack.awe[i] = 1.0;
            return 1.0;
        }
        if ((snowpack.wsnow[i] == 0.0))
        {
            snowpack.awe[i] = 1.0;
            return 0.0;
        }
        if ((snowfall > 0.0))
        {
            awe = (((snowpack.wsnow[i] - (snowfall * tStep))) / si);
            awe = (((((awe) >= (0.0))) ? (awe) : (0.0)));
            sba = getArealSnowCover(i, awe);
            sbws = (awe + ((((0.75 * snowfall) * tStep)) / si));
            sbws = (((((sbws) <= (1.0))) ? (sbws) : (1.0)));
            snowpack.awe[i] = awe;
            snowpack.sba[i] = sba;
            snowpack.sbws[i] = sbws;
            return 1.0;
        }
        else
        {
            awe = snowpack.awe[i];
            sba = snowpack.sba[i];
            sbws = snowpack.sbws[i];
            awesi = (snowpack.wsnow[i] / si);
            if ((awesi < snowpack.awe[i]))
            {
                snowpack.awe[i] = 1.0;
                asc = getArealSnowCover(i, awesi);
            }
            else if ((awesi >= snowpack.sbws[i]))
            {
                asc = 1.0;
            }
            else
            {
                asc = (sba + ((((1.0 - sba)) / ((sbws - awe))) * ((awesi - awe))));
            }
            return asc;
        }
    }

    // snow.c:636
    static double getArealSnowCover(int i, double awesi)
    {
        int k = 0;
        int m = 0;
        double asc1 = 0.0;
        double asc2 = 0.0;
        if ((i == SNOW_IMPERV))
        {
            k = 0;
        }
        else if ((i == SNOW_PERV))
        {
            k = 1;
        }
        else
        {
            return 1.0;
        }
        if ((awesi <= 0.0))
        {
            return 0.0;
        }
        if ((awesi >= 0.9999))
        {
            return 1.0;
        }
        m = R.D2I((((awesi * 10.0) + 0.00001)));
        asc1 = Snow.adc[k][m];
        if ((m >= 9))
        {
            asc2 = 1.0;
        }
        else
        {
            asc2 = Snow.adc[k][(m + 1)];
        }
        return (asc1 + ((((asc2 - asc1)) / 0.1) * ((awesi - (0.1 * ((float) (m)))))));
    }

    // snow.c:674
    static double meltSnowpack(TSnowpack snowpack, int i, double rmelt, double asc, double snowfall, double tStep)
    {
        int k = 0;
        double smelt = 0.0;
        double ccFactor = 0.0;
        k = snowpack.snowmeltIndex;
        if ((rmelt > 0.0))
        {
            smelt = rmelt;
        }
        else if ((Temp.ta >= Snowmelt[k].tbase[i]))
        {
            smelt = (Snowmelt[k].dhm[i] * ((Temp.ta - Snowmelt[k].tbase[i])));
        }
        else
        {
            updateColdContent(snowpack, i, asc, snowfall, tStep);
            return 0.0;
        }
        smelt *= asc;
        ccFactor = ((tStep * Snow.rnm) * asc);
        smelt = reduceColdContent(snowpack, i, smelt, ccFactor);
        snowpack.ati[i] = Snowmelt[k].tbase[i];
        return smelt;
    }

    // snow.c:720
    static double getRainmelt(double rainfall)
    {
        double uadj = 0.0;
        double t1 = 0.0;
        double t2 = 0.0;
        double t3 = 0.0;
        double smelt = 0.0;
        rainfall = (rainfall * 43200.0);
        if ((rainfall > 0.02))
        {
            uadj = (0.006 * Wind.ws);
            t1 = (Temp.ta - 32.0);
            t2 = ((7.5 * Temp.gamma) * uadj);
            t3 = ((8.5 * uadj) * ((Temp.ea - 0.18)));
            smelt = ((t1 * (((0.001167 + t2) + (0.007 * rainfall)))) + t3);
            return (smelt / 43200.0);
        }
        else
        {
            return 0.0;
        }
    }

    // snow.c:746
    static void updateColdContent(TSnowpack snowpack, int i, double asc, double snowfall, double tStep)
    {
        int k = 0;
        double ati = 0.0;
        double cc = 0.0;
        double ccMax = 0.0;
        double tipm = 0.0;
        ati = snowpack.ati[i];
        cc = snowpack.coldc[i];
        if (((snowfall * 43200.0) > 0.02))
        {
            ati = Temp.ta;
        }
        else
        {
            tipm = (1.0 - Glibm.pow((1.0 - Snow.tipm), (tStep / ((6.0 * 3600.0)))));
            ati += (tipm * ((Temp.ta - ati)));
        }
        k = snowpack.snowmeltIndex;
        ati = (((((ati) <= (Snowmelt[k].tbase[i]))) ? (ati) : (Snowmelt[k].tbase[i])));
        cc += ((((Snow.rnm * Snowmelt[k].dhm[i]) * ((ati - Temp.ta))) * tStep) * asc);
        cc = (((((cc) >= (0.0))) ? (cc) : (0.0)));
        ccMax = (((snowpack.wsnow[i] * 0.007) / 12.0) * ((Snowmelt[k].tbase[i] - ati)));
        cc = (((((cc) <= (ccMax))) ? (cc) : (ccMax)));
        snowpack.coldc[i] = cc;
        snowpack.ati[i] = ati;
    }

    // snow.c:799
    static double reduceColdContent(TSnowpack snowpack, int i, double smelt, double ccFactor)
    {
        double cc = 0.0;
        cc = snowpack.coldc[i];
        if (((smelt * ccFactor) > cc))
        {
            smelt -= (cc / ccFactor);
            cc = 0.0;
        }
        else
        {
            cc -= (smelt * ccFactor);
            smelt = 0.0;
        }
        snowpack.coldc[i] = cc;
        return smelt;
    }

    // snow.c:828
    static double routeSnowmelt(TSnowpack snowpack, int i, double smelt, double asc, double rainfall, double tStep)
    {
        int k = 0;
        double vmelt = 0.0;
        k = snowpack.snowmeltIndex;
        vmelt = (smelt * tStep);
        vmelt = (((((vmelt) <= (snowpack.wsnow[i]))) ? (vmelt) : (snowpack.wsnow[i])));
        snowpack.wsnow[i] -= vmelt;
        snowpack.fw[i] += (vmelt + ((rainfall * tStep) * asc));
        vmelt = (snowpack.fw[i] - (Snowmelt[k].fwfrac[i] * snowpack.wsnow[i]));
        vmelt = (((((vmelt) >= (0.0))) ? (vmelt) : (0.0)));
        snowpack.fw[i] -= vmelt;
        return (vmelt / tStep);
    }

}
