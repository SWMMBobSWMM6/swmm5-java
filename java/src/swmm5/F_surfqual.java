// Generated from EPA SWMM 5.2.4 (surfqual.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_surfqual {
    // surfqual.c:65
    static void surfqual_initState(int j)
    {
        int p = 0;
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            Subcatch[j].oldQual[p] = 0.0;
            Subcatch[j].newQual[p] = 0.0;
            Subcatch[j].pondedQual[p] = 0.0;
        }
        landuse_getInitBuildup(Subcatch[j].landFactor, Subcatch[j].initBuildup, Subcatch[j].area, Subcatch[j].curbLength);
    }

    // surfqual.c:89
    static void surfqual_getBuildup(int j, double tStep)
    {
        int i = 0;
        int p = 0;
        double f = 0.0;
        double area = 0.0;
        double curb = 0.0;
        double oldBuildup = 0.0;
        double newBuildup = 0.0;
        for (i = 0; (i < Nobjects[LANDUSE]); i++)
        {
            f = Subcatch[j].landFactor[i].fraction;
            if ((f == 0.0))
            {
                continue;
            }
            area = ((f * Subcatch[j].area) * UCF(LANDAREA));
            curb = (f * Subcatch[j].curbLength);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                if (((Pollut[p].snowOnly != 0) && (Subcatch[j].newSnowDepth < (0.001 / 12.0))))
                {
                    continue;
                }
                oldBuildup = Subcatch[j].landFactor[i].buildup[p];
                newBuildup = landuse_getBuildup(i, p, area, curb, oldBuildup, tStep);
                newBuildup = (((((newBuildup) >= (oldBuildup))) ? (newBuildup) : (oldBuildup)));
                Subcatch[j].landFactor[i].buildup[p] = newBuildup;
                massbal_updateLoadingTotals(BUILDUP_LOAD, p, ((newBuildup - oldBuildup)));
            }
        }
    }

    // surfqual.c:137
    static void surfqual_sweepBuildup(int j, double aDate)
    {
        int i = 0;
        int p = 0;
        double oldBuildup = 0.0;
        double newBuildup = 0.0;
        if (((Subcatch[j].snowpack != null) && (Subcatch[j].snowpack.wsnow[IMPERV0] > 0.004167)))
        {
            return;
        }
        for (i = 0; (i < Nobjects[LANDUSE]); i++)
        {
            if ((Subcatch[j].landFactor[i].fraction == 0.0))
            {
                continue;
            }
            if ((Landuse[i].sweepInterval == 0.0))
            {
                continue;
            }
            if (((aDate - Subcatch[j].landFactor[i].lastSwept) >= Landuse[i].sweepInterval))
            {
                Subcatch[j].landFactor[i].lastSwept = aDate;
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    oldBuildup = Subcatch[j].landFactor[i].buildup[p];
                    newBuildup = (oldBuildup * ((1.0 - (Landuse[i].sweepRemoval * Landuse[i].washoffFunc[p].sweepEffic))));
                    newBuildup = (((((oldBuildup) <= (newBuildup))) ? (oldBuildup) : (newBuildup)));
                    newBuildup = (((((0.0) >= (newBuildup))) ? (0.0) : (newBuildup)));
                    Subcatch[j].landFactor[i].buildup[p] = newBuildup;
                    massbal_updateLoadingTotals(SWEEPING_LOAD, p, (oldBuildup - newBuildup));
                }
            }
        }
    }

    // surfqual.c:192
    static void surfqual_getWashoff(int j, double runoff, double tStep)
    {
        int p = 0;
        int hasOutflow = 0;
        double cOut = 0.0;
        double massLoad = 0.0;
        double vLidRain = 0.0;
        double vLidRunon = 0.0;
        double vSurfOut = 0.0;
        double vOut1 = 0.0;
        double vOut2 = 0.0;
        double area = 0.0;
        area = Subcatch[j].area;
        if (((Nobjects[POLLUT] == 0) || (area == 0.0)))
        {
            return;
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            OutflowLoad[p] = 0.0;
        }
        findWashoffLoads(j, runoff);
        findPondedLoads(j, tStep);
        findLidLoads(j, tStep);
        vLidRain = ((Subcatch[j].rainfall * Subcatch[j].lidArea) * tStep);
        vLidRunon = 0.0;
        if ((area == Subcatch[j].lidArea))
        {
            vLidRunon = ((Subcatch[j].runon * area) * tStep);
        }
        vOut1 = ((Voutflow + vLidRain) + vLidRunon);
        vSurfOut = (Subcatch[j].newRunoff * tStep);
        vOut2 = (vSurfOut + VlidDrain);
        hasOutflow = (((vOut2 > ((2.31481e-08 * area) * tStep))) ? 1 : 0);
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            cOut = 0.0;
            if (((vOut1 > 0.0) && (hasOutflow != 0)))
            {
                cOut = (OutflowLoad[p] / vOut1);
            }
            if ((Subcatch[j].lidArea > 0.0))
            {
                massLoad = ((cOut * ((vOut1 - vOut2))) * Pollut[p].mcf);
                if ((massLoad > 0.0))
                {
                    massbal_updateLoadingTotals(BMP_REMOVAL_LOAD, p, massLoad);
                }
            }
            massLoad = ((cOut * vOut2) * Pollut[p].mcf);
            Subcatch[j].totalLoad[p] += massLoad;
            if ((((Subcatch[j].outNode >= 0) || (Subcatch[j].outSubcatch == j))))
            {
                massLoad = ((cOut * vSurfOut) * Pollut[p].mcf);
                massbal_updateLoadingTotals(RUNOFF_LOAD, p, massLoad);
            }
            Subcatch[j].newQual[p] = (cOut / 28.317);
        }
        if ((Subcatch[j].lidArea > 0.0))
        {
            lid_addDrainLoads(j, Subcatch[j].newQual, tStep);
        }
    }

    // surfqual.c:298
    static double surfqual_getWtdWashoff(int j, int p, double f)
    {
        return (((((1.0 - f)) * Subcatch[j].oldRunoff) * Subcatch[j].oldQual[p]) + ((f * Subcatch[j].newRunoff) * Subcatch[j].newQual[p]));
    }

    // surfqual.c:313
    static void findPondedLoads(int j, double tStep)
    {
        int p = 0;
        double cPonded = 0.0;
        double wPonded = 0.0;
        double bmpRemoval = 0.0;
        double vRain = 0.0;
        double wRain = 0.0;
        double wRunon = 0.0;
        double wInfil = 0.0;
        double wOutflow = 0.0;
        double fullArea = 0.0;
        double nonLidArea = 0.0;
        if ((Subcatch[j].area == Subcatch[j].lidArea))
        {
            return;
        }
        fullArea = Subcatch[j].area;
        nonLidArea = (fullArea - Subcatch[j].lidArea);
        vRain = ((Subcatch[j].rainfall * nonLidArea) * tStep);
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            wRain = ((Pollut[p].pptConcen * 28.317) * vRain);
            massbal_updateLoadingTotals(DEPOSITION_LOAD, p, (wRain * Pollut[p].mcf));
            if ((Vinflow == 0.0))
            {
                massbal_updateLoadingTotals(FINAL_LOAD, p, (Subcatch[j].pondedQual[p] * Pollut[p].mcf));
                Subcatch[j].pondedQual[p] = 0.0;
            }
            else
            {
                wRunon = (Subcatch[j].newQual[p] * tStep);
                wPonded = ((Subcatch[j].pondedQual[p] + wRain) + wRunon);
                cPonded = (wPonded / Vinflow);
                wInfil = (cPonded * Vinfil);
                wInfil = (((((wInfil) <= (wPonded))) ? (wInfil) : (wPonded)));
                massbal_updateLoadingTotals(INFIL_LOAD, p, (wInfil * Pollut[p].mcf));
                wPonded -= wInfil;
                wOutflow = (cPonded * Voutflow);
                wOutflow = (((((wOutflow) <= (wPonded))) ? (wOutflow) : (wPonded)));
                wPonded -= wOutflow;
                bmpRemoval = (landuse_getAvgBmpEffic(j, p) * wOutflow);
                massbal_updateLoadingTotals(BMP_REMOVAL_LOAD, p, (bmpRemoval * Pollut[p].mcf));
                wOutflow -= bmpRemoval;
                Subcatch[j].pondedQual[p] = ((cPonded * subcatch_getDepth(j)) * nonLidArea);
                OutflowLoad[p] += wOutflow;
            }
        }
    }

    // surfqual.c:390
    static void findWashoffLoads(int j, double runoff)
    {
        int i = 0;
        int p = 0;
        int k = 0;
        double w = 0.0;
        double area = Subcatch[j].area;
        if ((runoff < 2.31481e-08))
        {
            return;
        }
        for (i = 0; (i < Nobjects[LANDUSE]); i++)
        {
            if ((Subcatch[j].landFactor[i].fraction > 0.0))
            {
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    OutflowLoad[p] += (landuse_getWashoffLoad(i, p, area, Subcatch[j].landFactor, runoff, Voutflow));
                }
            }
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            k = Pollut[p].coPollut;
            if ((k >= 0))
            {
                w = (Pollut[p].coFraction * OutflowLoad[k]);
                massbal_updateLoadingTotals(BUILDUP_LOAD, p, (w * Pollut[p].mcf));
                OutflowLoad[p] += w;
            }
        }
    }

    // surfqual.c:443
    static void findLidLoads(int j, double tStep)
    {
        int p = 0;
        int useRunon = 0;
        double lidArea = 0.0;
        double vLidRain = 0.0;
        double wLidRain = 0.0;
        double wLidRunon = 0.0;
        lidArea = Subcatch[j].lidArea;
        if ((lidArea == 0.0))
        {
            return;
        }
        vLidRain = ((Subcatch[j].rainfall * lidArea) * tStep);
        useRunon = (((lidArea == Subcatch[j].area)) ? 1 : 0);
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            wLidRain = ((Pollut[p].pptConcen * vLidRain) * 28.317);
            massbal_updateLoadingTotals(DEPOSITION_LOAD, p, (wLidRain * Pollut[p].mcf));
            if ((useRunon != 0))
            {
                wLidRunon = (Subcatch[j].newQual[p] * tStep);
            }
            else
            {
                wLidRunon = 0.0;
            }
            OutflowLoad[p] += (wLidRain + wLidRunon);
        }
    }

}
