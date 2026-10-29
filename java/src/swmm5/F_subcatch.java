// Generated from EPA SWMM 5.2.4 (subcatch.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_subcatch {
    static double MCOEFF = 1.49;
    static double MEXP = 1.6666667;
    static double ODETOL = 0.0001;
    static double Vevap = 0.0;
    static double Vpevap = 0.0;
    static double Vinfil = 0.0;
    static double Vinflow = 0.0;
    static double Voutflow = 0.0;
    static double VlidIn = 0.0;
    static double VlidInfil = 0.0;
    static double VlidOut = 0.0;
    static double VlidDrain = 0.0;
    static double VlidReturn = 0.0;
    static TSubarea theSubarea = null;
    static double Dstore = 0.0;
    static double Alpha = 0.0;
    static CPtr[] RunoffRoutingWords = new CPtr[] { L663, L2, L3, null };

    // subcatch.c:119
    static int subcatch_readParams(int j, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int k = 0;
        int m = 0;
        CPtr id = null;
        double[] x = new double[9];
        if ((ntoks < 8))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(SUBCATCH, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        k = project_findObject(GAGE, tok[1]);
        if ((k < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        x[0] = k;
        m = project_findObject(NODE, tok[2]);
        x[1] = m;
        m = project_findObject(SUBCATCH, tok[2]);
        x[2] = m;
        if (((x[1] < 0.0) && (x[2] < 0.0)))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        for (i = 3; (i < 8); i++)
        {
            if (((!(getDouble(tok[i], new DElem(x, i)) != 0)) || (x[i] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[i]);
            }
        }
        x[8] = (-1);
        if ((ntoks > 8))
        {
            k = project_findObject(SNOWMELT, tok[8]);
            if ((k < 0))
            {
                return error_setInpError(ERR_NAME, tok[8]);
            }
            x[8] = k;
        }
        Subcatch[j].ID = id;
        Subcatch[j].gage = R.D2I(x[0]);
        Subcatch[j].outNode = R.D2I(x[1]);
        Subcatch[j].outSubcatch = R.D2I(x[2]);
        Subcatch[j].area = (x[3] / UCF(LANDAREA));
        Subcatch[j].fracImperv = ((((((x[4]) <= (100.0))) ? (x[4]) : (100.0))) / 100.0);
        Subcatch[j].width = (x[5] / UCF(LENGTH));
        Subcatch[j].slope = (x[6] / 100.0);
        Subcatch[j].curbLength = x[7];
        Subcatch[j].nPervPattern = (-1);
        Subcatch[j].dStorePattern = (-1);
        Subcatch[j].infilPattern = (-1);
        if ((x[8] >= 0))
        {
            if (!(snow_createSnowpack(j, R.D2I(x[8])) != 0))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
        }
        return 0;
    }

    // subcatch.c:196
    static int subcatch_readSubareaParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        double[] x = new double[7];
        if ((ntoks < 7))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(SUBCATCH, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        for (i = 0; (i < 5); i++)
        {
            if (((!(getDouble(tok[(i + 1)], new DElem(x, i)) != 0)) || (x[i] < 0.0)))
            {
                return error_setInpError(ERR_NAME, tok[(i + 1)]);
            }
        }
        m = findmatch(tok[6], RunoffRoutingWords);
        if ((m < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[6]);
        }
        x[5] = m;
        x[6] = 1.0;
        if ((ntoks >= 8))
        {
            if ((((!(getDouble(tok[7], new DElem(x, 6)) != 0)) || (x[6] < 0.0)) || (x[6] > 100.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[7]);
            }
            x[6] /= 100.0;
        }
        Subcatch[j].subArea[IMPERV0].N = x[0];
        Subcatch[j].subArea[IMPERV1].N = x[0];
        Subcatch[j].subArea[PERV].N = x[1];
        Subcatch[j].subArea[IMPERV0].dStore = 0.0;
        Subcatch[j].subArea[IMPERV1].dStore = (x[2] / UCF(RAINDEPTH));
        Subcatch[j].subArea[PERV].dStore = (x[3] / UCF(RAINDEPTH));
        Subcatch[j].subArea[IMPERV0].fArea = ((Subcatch[j].fracImperv * x[4]) / 100.0);
        Subcatch[j].subArea[IMPERV1].fArea = (Subcatch[j].fracImperv * ((1.0 - (x[4] / 100.0))));
        Subcatch[j].subArea[PERV].fArea = ((1.0 - Subcatch[j].fracImperv));
        for (i = IMPERV0; (i <= PERV); i++)
        {
            Subcatch[j].subArea[i].routeTo = TO_OUTLET;
            Subcatch[j].subArea[i].fOutlet = 1.0;
        }
        k = R.D2I(x[5]);
        if (((Subcatch[j].fracImperv == 0.0) || (Subcatch[j].fracImperv == 1.0)))
        {
            k = TO_OUTLET;
        }
        if (((k == TO_IMPERV) && (Subcatch[j].fracImperv != 0.0)))
        {
            Subcatch[j].subArea[PERV].routeTo = k;
            Subcatch[j].subArea[PERV].fOutlet = (1.0 - x[6]);
        }
        if ((k == TO_PERV))
        {
            Subcatch[j].subArea[IMPERV0].routeTo = k;
            Subcatch[j].subArea[IMPERV1].routeTo = k;
            Subcatch[j].subArea[IMPERV0].fOutlet = (1.0 - x[6]);
            Subcatch[j].subArea[IMPERV1].fOutlet = (1.0 - x[6]);
        }
        return 0;
    }

    // subcatch.c:284
    static int subcatch_readLanduseParams(CPtr[] tok, int ntoks)
    {
        int j = 0;
        int k = 0;
        int m = 0;
        DBox f = new DBox(0.0);
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(SUBCATCH, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        for (k = 2; (k <= ntoks); k = (k + 2))
        {
            m = project_findObject(LANDUSE, tok[(k - 1)]);
            if ((m < 0))
            {
                return error_setInpError(ERR_NAME, tok[(k - 1)]);
            }
            if (((k + 1) > ntoks))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if (!(getDouble(tok[k], f) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[k]);
            }
            Subcatch[j].landFactor[m].fraction = (f.v / 100.0);
        }
        return 0;
    }

    // subcatch.c:324
    static int subcatch_readInitBuildup(CPtr[] tok, int ntoks)
    {
        int j = 0;
        int k = 0;
        int m = 0;
        DBox x = new DBox(0.0);
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(SUBCATCH, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        for (k = 2; (k <= ntoks); k = (k + 2))
        {
            m = project_findObject(POLLUT, tok[(k - 1)]);
            if ((m < 0))
            {
                return error_setInpError(ERR_NAME, tok[(k - 1)]);
            }
            if (((k + 1) > ntoks))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if (!(getDouble(tok[k], x) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[k]);
            }
            Subcatch[j].initBuildup[m] = x.v;
        }
        return 0;
    }

    // subcatch.c:364
    static void subcatch_validate(int j)
    {
        int i = 0;
        double area = 0.0;
        double nonLidArea = Subcatch[j].area;
        if (((Subcatch[j].outNode >= 0) && (Subcatch[j].outSubcatch >= 0)))
        {
            report_writeErrorMsg(ERR_SUBCATCH_OUTLET, Subcatch[j].ID);
        }
        gwater_validate(j);
        nonLidArea -= Subcatch[j].lidArea;
        for (i = IMPERV0; (i <= PERV); i++)
        {
            if ((i == PERV))
            {
                area = (((1.0 - Subcatch[j].fracImperv)) * nonLidArea);
            }
            else
            {
                area = (Subcatch[j].fracImperv * nonLidArea);
            }
            Subcatch[j].subArea[i].alpha = 0.0;
            if (((area > 0.0) && (Subcatch[j].subArea[i].N > 0.0)))
            {
                Subcatch[j].subArea[i].alpha = ((((MCOEFF * Subcatch[j].width) / area) * Math.sqrt(Subcatch[j].slope)) / Subcatch[j].subArea[i].N);
            }
        }
        i = Subcatch[j].gage;
        if ((i >= 0))
        {
            Gage[i].isUsed = 1;
        }
    }

    // subcatch.c:419
    static void subcatch_initState(int j)
    {
        int i = 0;
        Subcatch[j].rainfall = 0.0;
        Subcatch[j].oldRunoff = 0.0;
        Subcatch[j].newRunoff = 0.0;
        Subcatch[j].oldSnowDepth = 0.0;
        Subcatch[j].newSnowDepth = 0.0;
        Subcatch[j].runon = 0.0;
        Subcatch[j].evapLoss = 0.0;
        Subcatch[j].infilLoss = 0.0;
        if ((Subcatch[j].infil == j))
        {
            infil_initState(j);
        }
        if ((Subcatch[j].groundwater != null))
        {
            gwater_initState(j);
        }
        if ((Subcatch[j].snowpack != null))
        {
            snow_initSnowpack(j);
        }
        for (i = IMPERV0; (i <= PERV); i++)
        {
            Subcatch[j].subArea[i].depth = 0.0;
            Subcatch[j].subArea[i].inflow = 0.0;
            Subcatch[j].subArea[i].runoff = 0.0;
        }
        surfqual_initState(j);
    }

    // subcatch.c:457
    static void subcatch_setOldState(int j)
    {
        int i = 0;
        Subcatch[j].oldRunoff = Subcatch[j].newRunoff;
        Subcatch[j].oldSnowDepth = Subcatch[j].newSnowDepth;
        for (i = IMPERV0; (i <= PERV); i++)
        {
            Subcatch[j].subArea[i].inflow = 0.0;
        }
        for (i = 0; (i < Nobjects[POLLUT]); i++)
        {
            Subcatch[j].oldQual[i] = Subcatch[j].newQual[i];
            Subcatch[j].newQual[i] = 0.0;
        }
        lid_setOldGroupState(j);
    }

    // subcatch.c:481
    static double subcatch_getFracPerv(int j)
    {
        double fracPerv = (1.0 - Subcatch[j].fracImperv);
        if ((Subcatch[j].lidArea > 0.0))
        {
            fracPerv = ((((fracPerv * ((Subcatch[j].area - Subcatch[j].lidArea))) + lid_getPervArea(j))) / Subcatch[j].area);
            fracPerv = (((((fracPerv) <= (1.0))) ? (fracPerv) : (1.0)));
        }
        return fracPerv;
    }

    // subcatch.c:502
    static double subcatch_getStorage(int j)
    {
        int i = 0;
        double v = 0.0;
        for (i = IMPERV0; (i <= PERV); i++)
        {
            v += (Subcatch[j].subArea[i].depth * Subcatch[j].subArea[i].fArea);
        }
        return ((v * ((Subcatch[j].area - Subcatch[j].lidArea))) + lid_getStoredVolume(j));
    }

    // subcatch.c:523
    static void subcatch_getRunon(int j)
    {
        int k = 0;
        int p = 0;
        double q = 0.0;
        double q1 = 0.0;
        double q2 = 0.0;
        double pervArea = 0.0;
        k = Subcatch[j].outSubcatch;
        q = Subcatch[j].oldRunoff;
        if (((k >= 0) && (k != j)))
        {
            subcatch_addRunonFlow(k, q);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                Subcatch[k].newQual[p] += ((q * Subcatch[j].oldQual[p]) * 28.317);
            }
        }
        if ((Subcatch[j].lidArea > 0.0))
        {
            lid_addDrainRunon(j);
        }
        if (((Subcatch[j].fracImperv < 1.0) && (Subcatch[j].subArea[IMPERV0].routeTo == TO_PERV)))
        {
            q1 = (Subcatch[j].subArea[IMPERV0].runoff * Subcatch[j].subArea[IMPERV0].fArea);
            q2 = (Subcatch[j].subArea[IMPERV1].runoff * Subcatch[j].subArea[IMPERV1].fArea);
            q = (q1 + q2);
            Subcatch[j].subArea[PERV].inflow += ((q * ((1.0 - Subcatch[j].subArea[IMPERV0].fOutlet))) / Subcatch[j].subArea[PERV].fArea);
        }
        if ((((Subcatch[j].fracImperv > 0.0) && (Subcatch[j].subArea[PERV].routeTo == TO_IMPERV)) && (Subcatch[j].subArea[IMPERV1].fArea > 0.0)))
        {
            q = Subcatch[j].subArea[PERV].runoff;
            Subcatch[j].subArea[IMPERV1].inflow += (((q * ((1.0 - Subcatch[j].subArea[PERV].fOutlet))) * Subcatch[j].subArea[PERV].fArea) / Subcatch[j].subArea[IMPERV1].fArea);
        }
        if (((Subcatch[j].lidArea > 0.0) && (Subcatch[j].fracImperv < 1.0)))
        {
            pervArea = (Subcatch[j].subArea[PERV].fArea * ((Subcatch[j].area - Subcatch[j].lidArea)));
            q = lid_getFlowToPerv(j);
            if ((pervArea > 0.0))
            {
                Subcatch[j].subArea[PERV].inflow += (q / pervArea);
            }
        }
    }

    // subcatch.c:600
    static void subcatch_addRunonFlow(int k, double q)
    {
        int i = 0;
        double nonLidArea = 0.0;
        if ((Subcatch[k].area <= 0.0))
        {
            return;
        }
        nonLidArea = (Subcatch[k].area - Subcatch[k].lidArea);
        if ((nonLidArea > 0.0))
        {
            q = (q / nonLidArea);
        }
        else
        {
            q = (q / Subcatch[k].area);
        }
        Subcatch[k].runon += q;
        for (i = IMPERV0; (i <= PERV); i++)
        {
            Subcatch[k].subArea[i].inflow += q;
        }
    }

    // subcatch.c:629
    static double subcatch_getRunoff(int j, double tStep)
    {
        int i = 0;
        double nonLidArea = 0.0;
        double area = 0.0;
        double[] netPrecip = new double[3];
        double vRain = 0.0;
        double vRunon = 0.0;
        double vOutflow = 0.0;
        double runoff = 0.0;
        double evapRate = 0.0;
        double subAreaRunoff = 0.0;
        double vImpervRunoff = 0.0;
        double vPervRunoff = 0.0;
        Vevap = 0.0;
        Vpevap = 0.0;
        Vinfil = 0.0;
        Voutflow = 0.0;
        VlidIn = 0.0;
        VlidInfil = 0.0;
        VlidOut = 0.0;
        VlidDrain = 0.0;
        VlidReturn = 0.0;
        nonLidArea = (Subcatch[j].area - Subcatch[j].lidArea);
        vRunon = ((Subcatch[j].runon * tStep) * nonLidArea);
        Vinflow = (vRunon + (subcatch_getDepth(j) * nonLidArea));
        if ((nonLidArea == 0.0))
        {
            vRunon = ((Subcatch[j].runon * tStep) * Subcatch[j].area);
        }
        getNetPrecip(j, netPrecip, tStep);
        if (((Evap.dryOnly != 0) && (Subcatch[j].rainfall > 0.0)))
        {
            evapRate = 0.0;
        }
        else
        {
            evapRate = Evap.rate;
        }
        infil_setInfilFactor(j);
        if ((nonLidArea > 0.0))
        {
            for (i = IMPERV0; (i <= PERV); i++)
            {
                area = (nonLidArea * Subcatch[j].subArea[i].fArea);
                Subcatch[j].subArea[i].runoff = getSubareaRunoff(j, i, area, netPrecip[i], evapRate, tStep);
                subAreaRunoff = (Subcatch[j].subArea[i].runoff * area);
                if ((i == PERV))
                {
                    vPervRunoff = (subAreaRunoff * tStep);
                }
                else
                {
                    vImpervRunoff += (subAreaRunoff * tStep);
                }
                runoff += subAreaRunoff;
            }
        }
        if ((Subcatch[j].lidArea > 0.0))
        {
            lid_getRunoff(j, tStep);
        }
        if (((!(IgnoreGwater != 0)) && (Subcatch[j].groundwater != null)))
        {
            gwater_getGroundwater(j, Vpevap, (Vinfil + VlidInfil), tStep);
        }
        area = Subcatch[j].area;
        Subcatch[j].evapLoss = ((Vevap / tStep) / area);
        Subcatch[j].infilLoss = ((((Vinfil + VlidInfil)) / tStep) / area);
        vOutflow = ((Voutflow - VlidIn) + VlidOut);
        Subcatch[j].newRunoff = (vOutflow / tStep);
        vRain = ((Subcatch[j].rainfall * tStep) * area);
        stats_updateSubcatchStats(j, vRain, vRunon, Vevap, (Vinfil + VlidInfil), vImpervRunoff, vPervRunoff, (vOutflow + VlidDrain), (Subcatch[j].newRunoff + (VlidDrain / tStep)));
        if (((Subcatch[j].outNode == (-1)) && (Subcatch[j].outSubcatch != j)))
        {
            vOutflow = 0.0;
        }
        massbal_updateRunoffTotals(RUNOFF_RAINFALL, vRain);
        massbal_updateRunoffTotals(RUNOFF_EVAP, Vevap);
        massbal_updateRunoffTotals(RUNOFF_INFIL, (Vinfil + VlidInfil));
        massbal_updateRunoffTotals(RUNOFF_RUNOFF, vOutflow);
        return (runoff / area);
    }

    // subcatch.c:758
    static void getNetPrecip(int j, double[] netPrecip, double tStep)
    {
        int i = 0;
        int k = 0;
        DBox rainfall = new DBox(0.0);
        DBox snowfall = new DBox(0.0);
        k = Subcatch[j].gage;
        if ((k >= 0))
        {
            gage_getPrecip(k, rainfall, snowfall);
        }
        Subcatch[j].rainfall = (rainfall.v + snowfall.v);
        if (((Subcatch[j].snowpack != null) && (!(IgnoreSnowmelt != 0))))
        {
            Subcatch[j].newSnowDepth = snow_getSnowMelt(j, rainfall.v, snowfall.v, tStep, netPrecip);
        }
        else
        {
            for (i = IMPERV0; (i <= PERV); i++)
            {
                netPrecip[i] = (rainfall.v + snowfall.v);
            }
        }
    }

    // subcatch.c:798
    static double subcatch_getDepth(int j)
    {
        int i = 0;
        double fArea = 0.0;
        double depth = 0.0;
        for (i = IMPERV0; (i <= PERV); i++)
        {
            fArea = Subcatch[j].subArea[i].fArea;
            if ((fArea > 0.0))
            {
                depth += (Subcatch[j].subArea[i].depth * fArea);
            }
        }
        return depth;
    }

    // subcatch.c:820
    static double subcatch_getWtdOutflow(int j, double f)
    {
        if ((Subcatch[j].area == 0.0))
        {
            return 0.0;
        }
        return ((((1.0 - f)) * Subcatch[j].oldRunoff) + (f * Subcatch[j].newRunoff));
    }

    // subcatch.c:834
    static void subcatch_getResults(int j, double f, float[] x)
    {
        int p = 0;
        int k = 0;
        double f1 = (1.0 - f);
        double z = 0.0;
        double runoff = 0.0;
        TGroundwater gw = null;
        k = Subcatch[j].gage;
        if ((k >= 0))
        {
            x[SUBCATCH_RAINFALL] = ((float) (Gage[k].reportRainfall));
        }
        else
        {
            x[SUBCATCH_RAINFALL] = 0.0f;
        }
        z = ((((f1 * Subcatch[j].oldSnowDepth) + (f * Subcatch[j].newSnowDepth))) * UCF(RAINDEPTH));
        x[SUBCATCH_SNOWDEPTH] = ((float) (z));
        x[SUBCATCH_EVAP] = ((float) (((Subcatch[j].evapLoss * UCF(EVAPRATE)))));
        x[SUBCATCH_INFIL] = ((float) (((Subcatch[j].infilLoss * UCF(RAINFALL)))));
        runoff = ((f1 * Subcatch[j].oldRunoff) + (f * Subcatch[j].newRunoff));
        if ((Subcatch[j].lidArea > 0.0))
        {
            runoff += ((f1 * lid_getDrainFlow(j, PREVIOUS)) + (f * lid_getDrainFlow(j, CURRENT)));
        }
        if ((runoff < (2.31481e-08 * Subcatch[j].area)))
        {
            runoff = 0.0;
        }
        x[SUBCATCH_RUNOFF] = ((float) (((runoff * UCF(FLOW)))));
        gw = Subcatch[j].groundwater;
        if ((gw != null))
        {
            z = (((((f1 * gw.oldFlow) + (f * gw.newFlow))) * Subcatch[j].area) * UCF(FLOW));
            x[SUBCATCH_GW_FLOW] = ((float) (z));
            z = (((gw.bottomElev + gw.lowerDepth)) * UCF(LENGTH));
            x[SUBCATCH_GW_ELEV] = ((float) (z));
            z = gw.theta;
            x[SUBCATCH_SOIL_MOIST] = ((float) (z));
        }
        else
        {
            x[SUBCATCH_GW_FLOW] = 0.0f;
            x[SUBCATCH_GW_ELEV] = 0.0f;
            x[SUBCATCH_SOIL_MOIST] = 0.0f;
        }
        if (!(IgnoreQuality != 0))
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                if ((runoff == 0.0))
                {
                    z = 0.0;
                }
                else
                {
                    z = ((f1 * Subcatch[j].oldQual[p]) + (f * Subcatch[j].newQual[p]));
                }
                x[(SUBCATCH_WASHOFF + p)] = ((float) (z));
            }
        }
    }

    // subcatch.c:907
    static double getSubareaRunoff(int j, int i, double area, double precip, double evap, double tStep)
    {
        DBox tRunoff = new DBox(0.0);
        double surfMoisture = 0.0;
        double surfEvap = 0.0;
        double infil = 0.0;
        double runoff = 0.0;
        TSubarea subarea = null;
        if ((area == 0.0))
        {
            return 0.0;
        }
        subarea = Subcatch[j].subArea[i];
        tRunoff.v = tStep;
        surfMoisture = (subarea.depth / tStep);
        surfEvap = (((((surfMoisture) <= (evap))) ? (surfMoisture) : (evap)));
        if ((i == PERV))
        {
            infil = getSubareaInfil(j, subarea, precip, tStep);
        }
        subarea.inflow += precip;
        surfMoisture += subarea.inflow;
        Vinflow += ((precip * area) * tStep);
        Vevap += ((surfEvap * area) * tStep);
        if ((i == PERV))
        {
            Vpevap += Vevap;
        }
        Vinfil += ((infil * area) * tStep);
        Alpha = subarea.alpha;
        Dstore = subarea.dStore;
        adjustSubareaParams(i, j);
        if (((surfEvap + infil) >= surfMoisture))
        {
            subarea.depth = 0.0;
        }
        else
        {
            subarea.inflow -= (surfEvap + infil);
            updatePondedDepth(subarea, tRunoff);
        }
        runoff = findSubareaRunoff(subarea, tRunoff.v);
        Voutflow += (((subarea.fOutlet * runoff) * area) * tStep);
        return runoff;
    }

    // subcatch.c:985
    static double getSubareaInfil(int j, TSubarea subarea, double precip, double tStep)
    {
        double infil = 0.0;
        infil = infil_getInfil(j, tStep, precip, subarea.inflow, subarea.depth);
        if (((!(IgnoreGwater != 0)) && (Subcatch[j].groundwater != null)))
        {
            infil = (((((infil) <= ((Subcatch[j].groundwater.maxInfilVol / tStep)))) ? (infil) : ((Subcatch[j].groundwater.maxInfilVol / tStep))));
        }
        return infil;
    }

    // subcatch.c:1013
    static double findSubareaRunoff(TSubarea subarea, double tRunoff)
    {
        double xDepth = (subarea.depth - Dstore);
        double runoff = 0.0;
        if ((xDepth > 1e-10))
        {
            if ((subarea.N > 0.0))
            {
                runoff = (Alpha * Glibm.pow(xDepth, MEXP));
            }
            else
            {
                runoff = (xDepth / tRunoff);
                subarea.depth = Dstore;
            }
        }
        else
        {
            runoff = 0.0;
        }
        return runoff;
    }

    // subcatch.c:1048
    static void updatePondedDepth(TSubarea subarea, DRef dt)
    {
        double ix = subarea.inflow;
        double dx = 0.0;
        double tx = dt.get();
        if (((subarea.depth + (ix * tx)) <= Dstore))
        {
            subarea.depth += (ix * tx);
        }
        else
        {
            dx = (Dstore - subarea.depth);
            if (((dx > 0.0) && (ix > 0.0)))
            {
                tx -= (dx / ix);
                subarea.depth = Dstore;
            }
            if (((Alpha > 0.0) && (tx > 0.0)))
            {
                theSubarea = subarea;
                {
                    double[] __a1 = new double[] { subarea.depth };
                    odesolve_integrate(__a1, 1, 0, tx, ODETOL, tx, F_subcatch::getDdDt);
                    subarea.depth = __a1[0];
                }
            }
            else
            {
                if ((tx < 0.0))
                {
                    tx = 0.0;
                }
                subarea.depth += (ix * tx);
            }
        }
        if ((subarea.depth < 0.0))
        {
            subarea.depth = 0.0;
        }
        dt.set(tx);
    }

    // subcatch.c:1101
    static void getDdDt(double t, double[] d, double[] dddt)
    {
        double ix = theSubarea.inflow;
        double rx = (d[0] - Dstore);
        if ((rx < 0.0))
        {
            rx = 0.0;
        }
        else
        {
            rx = (Alpha * Glibm.pow(rx, MEXP));
        }
        dddt[0] = (ix - rx);
    }

    // subcatch.c:1125
    static void adjustSubareaParams(int i, int j)
    {
        int p = 0;
        int m = 0;
        double f = 0.0;
        if ((i == PERV))
        {
            p = Subcatch[j].dStorePattern;
            if (((p >= 0) && (Pattern[p].type == MONTHLY_PATTERN)))
            {
                m = (datetime_monthOfYear(getDateTime(OldRunoffTime)) - 1);
                f = Pattern[p].factor[m];
                if ((f >= 0.0))
                {
                    Dstore *= f;
                }
            }
            p = Subcatch[j].nPervPattern;
            if (((p >= 0) && (Pattern[p].type == MONTHLY_PATTERN)))
            {
                m = (datetime_monthOfYear(getDateTime(OldRunoffTime)) - 1);
                f = Pattern[p].factor[m];
                if ((f <= 0.0))
                {
                    Alpha = 0.0;
                }
                else
                {
                    Alpha /= f;
                }
            }
        }
    }

}
