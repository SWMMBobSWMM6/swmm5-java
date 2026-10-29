// Generated from EPA SWMM 5.2.4 (statsrpt.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_statsrpt {
    static CPtr FlowFmt = R.buf(6);
    static double Vcf = 0.0;

    // statsrpt.c:84
    static void statsrpt_writeReport()
    {
        if (((FlowUnits == MGD) || (FlowUnits == CMS)))
        {
            sstrncpy(FlowFmt, L534, 5);
        }
        else
        {
            sstrncpy(FlowFmt, L535, 5);
        }
        if ((UnitSystem == US))
        {
            Vcf = (7.48 / 1.0e6);
        }
        else
        {
            Vcf = (28.317 / 1.0e6);
        }
        if ((Nobjects[SUBCATCH] > 0))
        {
            if ((((!(IgnoreRainfall != 0)) || (((Nobjects[SNOWMELT] > 0) && (!(IgnoreSnowmelt != 0))))) || (((Nobjects[AQUIFER] > 0) && (!(IgnoreGwater != 0))))))
            {
                writeSubcatchRunoff();
                lid_writeWaterBalance();
                if (!(IgnoreGwater != 0))
                {
                    writeGroundwater();
                }
                if (((Nobjects[POLLUT] > 0) && (!(IgnoreQuality != 0))))
                {
                    writeSubcatchLoads();
                }
            }
        }
        if (((Nobjects[LINK] > 0) && (!(IgnoreRouting != 0))))
        {
            writeNodeDepths();
            writeNodeFlows();
            if ((RouteModel == DW))
            {
                writeNodeSurcharge();
            }
            writeNodeFlooding();
            writeStorageVolumes();
            writeOutfallLoads();
            inlet_writeStatsReport();
            writeLinkFlows();
            writeFlowClass();
            writeLinkSurcharge();
            writePumpFlows();
            if (((Nobjects[POLLUT] > 0) && (!(IgnoreQuality != 0))))
            {
                writeLinkLoads();
            }
        }
    }

    // statsrpt.c:133
    static void writeSubcatchRunoff()
    {
        int j = 0;
        double a = 0.0;
        double x = 0.0;
        double r = 0.0;
        if ((Nobjects[SUBCATCH] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L473));
        report_writeLine((L536));
        report_writeLine((L473));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L537);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L538, VolUnitsWords[UnitSystem], FlowUnitWords[FlowUnits]);
        }
        else
        {
            R.fprintf(Frpt.file, L539, VolUnitsWords[UnitSystem], FlowUnitWords[FlowUnits]);
        }
        R.fprintf(Frpt.file, L540);
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            a = Subcatch[j].area;
            if ((a == 0.0))
            {
                continue;
            }
            R.fprintf(Frpt.file, L541, Subcatch[j].ID);
            x = (SubcatchStats[j].precip * UCF(RAINDEPTH));
            R.fprintf(Frpt.file, L542, (x / a));
            x = (SubcatchStats[j].runon * UCF(RAINDEPTH));
            R.fprintf(Frpt.file, L542, (x / a));
            x = (SubcatchStats[j].evap * UCF(RAINDEPTH));
            R.fprintf(Frpt.file, L542, (x / a));
            x = (SubcatchStats[j].infil * UCF(RAINDEPTH));
            R.fprintf(Frpt.file, L542, (x / a));
            x = (SubcatchStats[j].impervRunoff * UCF(RAINDEPTH));
            R.fprintf(Frpt.file, L542, (x / a));
            x = (SubcatchStats[j].pervRunoff * UCF(RAINDEPTH));
            R.fprintf(Frpt.file, L542, (x / a));
            x = (SubcatchStats[j].runoff * UCF(RAINDEPTH));
            R.fprintf(Frpt.file, L542, (x / a));
            x = (SubcatchStats[j].runoff * Vcf);
            R.fprintf(Frpt.file, L543, x);
            x = (SubcatchStats[j].maxFlow * UCF(FLOW));
            R.fprintf(Frpt.file, L544, x);
            r = (SubcatchStats[j].precip + SubcatchStats[j].runon);
            if ((r > 0.0))
            {
                r = (SubcatchStats[j].runoff / r);
            }
            R.fprintf(Frpt.file, L545, r);
        }
        report_writeLine((L0));
    }

    // statsrpt.c:190
    static void writeGroundwater()
    {
        int i = 0;
        int j = 0;
        int count = 0;
        double totalSeconds = (NewRunoffTime / 1000.0);
        double[] x = new double[9];
        if ((Nobjects[SUBCATCH] == 0))
        {
            return;
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if ((Subcatch[j].groundwater != null))
            {
                count++;
            }
        }
        if ((count == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L188));
        report_writeLine((L546));
        report_writeLine((L188));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L547);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L548, FlowUnitWords[FlowUnits]);
        }
        else
        {
            R.fprintf(Frpt.file, L549, FlowUnitWords[FlowUnits]);
        }
        R.fprintf(Frpt.file, L550);
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if (((Subcatch[j].area == 0.0) || (Subcatch[j].groundwater == null)))
            {
                continue;
            }
            R.fprintf(Frpt.file, L541, Subcatch[j].ID);
            x[0] = (Subcatch[j].groundwater.stats.infil * UCF(RAINDEPTH));
            x[1] = (Subcatch[j].groundwater.stats.evap * UCF(RAINDEPTH));
            x[2] = (Subcatch[j].groundwater.stats.deepFlow * UCF(RAINDEPTH));
            x[3] = (Subcatch[j].groundwater.stats.latFlow * UCF(RAINDEPTH));
            x[4] = ((Subcatch[j].groundwater.stats.maxFlow * UCF(FLOW)) * Subcatch[j].area);
            x[5] = (Subcatch[j].groundwater.stats.avgUpperMoist / totalSeconds);
            x[6] = ((Subcatch[j].groundwater.stats.avgWaterTable * UCF(LENGTH)) / totalSeconds);
            x[7] = Subcatch[j].groundwater.stats.finalUpperMoist;
            x[8] = (Subcatch[j].groundwater.stats.finalWaterTable * UCF(LENGTH));
            for (i = 0; (i < 9); i++)
            {
                R.fprintf(Frpt.file, L544, x[i]);
            }
        }
        report_writeLine((L0));
    }

    // statsrpt.c:245
    static void writeSubcatchLoads()
    {
        int i = 0;
        int j = 0;
        int p = 0;
        double x = 0.0;
        double[] totals = null;
        CPtr units = R.buf(15);
        CPtr subcatchLine = R.bufFrom(L551, 21);
        CPtr pollutLine = R.bufFrom(L552, 15);
        totals = new double[(int) (Nobjects[POLLUT])];
        if ((totals != null))
        {
            report_writeLine((L0));
            report_writeLine((L553));
            report_writeLine((L554));
            report_writeLine((L553));
            report_writeLine((L0));
            R.fprintf(Frpt.file, L356, subcatchLine);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L169, pollutLine);
            }
            R.fprintf(Frpt.file, L555);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L423, Pollut[p].ID);
            }
            R.fprintf(Frpt.file, L556);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                i = UnitSystem;
                if ((Pollut[p].units == COUNT))
                {
                    i = 2;
                }
                sstrncpy(units, LoadUnitsWords[i], 14);
                R.fprintf(Frpt.file, L423, units);
                totals[p] = 0.0;
            }
            R.fprintf(Frpt.file, L356, subcatchLine);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L169, pollutLine);
            }
            for (j = 0; (j < Nobjects[SUBCATCH]); j++)
            {
                R.fprintf(Frpt.file, L541, Subcatch[j].ID);
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    x = Subcatch[j].totalLoad[p];
                    totals[p] += x;
                    if ((Pollut[p].units == COUNT))
                    {
                        x = ((((x) > 0.0) ? Glibm.log10((x)) : (x)));
                    }
                    R.fprintf(Frpt.file, L427, x);
                }
            }
            R.fprintf(Frpt.file, L356, subcatchLine);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L169, pollutLine);
            }
            R.fprintf(Frpt.file, L557);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                x = totals[p];
                if ((Pollut[p].units == COUNT))
                {
                    x = ((((x) > 0.0) ? Glibm.log10((x)) : (x)));
                }
                R.fprintf(Frpt.file, L427, x);
            }
            R.free(totals);
            report_writeLine((L0));
        }
    }

    // statsrpt.c:310
    static void writeNodeDepths()
    {
        int j = 0;
        IBox days = new IBox(0);
        IBox hrs = new IBox(0);
        IBox mins = new IBox(0);
        if ((Nobjects[LINK] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L558));
        report_writeLine((L559));
        report_writeLine((L558));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L560);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L561);
        }
        else
        {
            R.fprintf(Frpt.file, L562);
        }
        R.fprintf(Frpt.file, L563);
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            R.fprintf(Frpt.file, L541, Node[j].ID);
            R.fprintf(Frpt.file, L564, NodeTypeWords[Node[j].type]);
            getElapsedTime(NodeStats[j].maxDepthDate, days, hrs, mins);
            R.fprintf(Frpt.file, L565, ((NodeStats[j].avgDepth / ReportStepCount) * UCF(LENGTH)), (NodeStats[j].maxDepth * UCF(LENGTH)), (((NodeStats[j].maxDepth + Node[j].invertElev)) * UCF(LENGTH)), days.v, hrs.v, mins.v, NodeStats[j].maxRptDepth);
        }
        report_writeLine((L0));
    }

    // statsrpt.c:353
    static void writeNodeFlows()
    {
        int j = 0;
        IBox days1 = new IBox(0);
        IBox hrs1 = new IBox(0);
        IBox mins1 = new IBox(0);
        report_writeLine((L0));
        report_writeLine((L188));
        report_writeLine((L566));
        report_writeLine((L188));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L567, FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits], VolUnitsWords[UnitSystem], VolUnitsWords[UnitSystem]);
        R.fprintf(Frpt.file, L568);
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            R.fprintf(Frpt.file, L541, Node[j].ID);
            R.fprintf(Frpt.file, L569, NodeTypeWords[Node[j].type]);
            getElapsedTime(NodeStats[j].maxInflowDate, days1, hrs1, mins1);
            R.fprintf(Frpt.file, FlowFmt, (NodeStats[j].maxLatFlow * UCF(FLOW)));
            R.fprintf(Frpt.file, FlowFmt, (NodeStats[j].maxInflow * UCF(FLOW)));
            R.fprintf(Frpt.file, L570, days1.v, hrs1.v, mins1.v);
            R.fprintf(Frpt.file, L571, (NodeStats[j].totLatFlow * Vcf));
            R.fprintf(Frpt.file, L571, (NodeInflow[j] * Vcf));
            if ((Math.abs(NodeOutflow[j]) < 1.0))
            {
                R.fprintf(Frpt.file, L572, ((((NodeInflow[j] - NodeOutflow[j])) * Vcf) * 1.0e6), VolUnitsWords2[UnitSystem]);
            }
            else
            {
                R.fprintf(Frpt.file, L573, ((((NodeInflow[j] - NodeOutflow[j])) / NodeOutflow[j]) * 100.0));
            }
        }
        report_writeLine((L0));
    }

    // statsrpt.c:403
    static void writeNodeSurcharge()
    {
        int j = 0;
        int n = 0;
        double t = 0.0;
        double d1 = 0.0;
        double d2 = 0.0;
        report_writeLine((L0));
        report_writeLine((L574));
        report_writeLine((L575));
        report_writeLine((L574));
        report_writeLine((L0));
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if ((Node[j].type == OUTFALL))
            {
                continue;
            }
            if ((NodeStats[j].timeSurcharged == 0.0))
            {
                continue;
            }
            t = (((((0.01) >= (((NodeStats[j].timeSurcharged / 3600.0))))) ? (0.01) : (((NodeStats[j].timeSurcharged / 3600.0)))));
            if ((n == 0))
            {
                report_writeLine((L576));
                R.fprintf(Frpt.file, L577);
                if ((UnitSystem == US))
                {
                    R.fprintf(Frpt.file, L578);
                }
                else
                {
                    R.fprintf(Frpt.file, L579);
                }
                R.fprintf(Frpt.file, L580);
                n = 1;
            }
            R.fprintf(Frpt.file, L541, Node[j].ID);
            R.fprintf(Frpt.file, L569, NodeTypeWords[Node[j].type]);
            d1 = ((NodeStats[j].maxDepth + Node[j].invertElev) - Node[j].crownElev);
            if ((d1 < 0.0))
            {
                d1 = 0.0;
            }
            d2 = (Node[j].fullDepth - NodeStats[j].maxDepth);
            if ((d2 < 0.0))
            {
                d2 = 0.0;
            }
            R.fprintf(Frpt.file, L581, t, (d1 * UCF(LENGTH)), (d2 * UCF(LENGTH)));
        }
        if ((n == 0))
        {
            report_writeLine((L582));
        }
        report_writeLine((L0));
    }

    // statsrpt.c:449
    static void writeNodeFlooding()
    {
        int j = 0;
        int n = 0;
        IBox days = new IBox(0);
        IBox hrs = new IBox(0);
        IBox mins = new IBox(0);
        double t = 0.0;
        report_writeLine((L0));
        report_writeLine((L255));
        report_writeLine((L583));
        report_writeLine((L255));
        report_writeLine((L0));
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if ((Node[j].type == OUTFALL))
            {
                continue;
            }
            if ((NodeStats[j].timeFlooded == 0.0))
            {
                continue;
            }
            t = (((((0.01) >= (((NodeStats[j].timeFlooded / 3600.0))))) ? (0.01) : (((NodeStats[j].timeFlooded / 3600.0)))));
            if ((n == 0))
            {
                report_writeLine((L584));
                R.fprintf(Frpt.file, L585);
                if ((RouteModel == DW))
                {
                    R.fprintf(Frpt.file, L586);
                }
                else
                {
                    R.fprintf(Frpt.file, L587);
                }
                R.fprintf(Frpt.file, L588, FlowUnitWords[FlowUnits], VolUnitsWords[UnitSystem]);
                if ((RouteModel == DW))
                {
                    R.fprintf(Frpt.file, L589, PondingUnitsWords[UnitSystem]);
                }
                else if ((UnitSystem == US))
                {
                    R.fprintf(Frpt.file, L590);
                }
                else
                {
                    R.fprintf(Frpt.file, L591);
                }
                R.fprintf(Frpt.file, L592);
                n = 1;
            }
            R.fprintf(Frpt.file, L541, Node[j].ID);
            R.fprintf(Frpt.file, L593, t);
            R.fprintf(Frpt.file, FlowFmt, (NodeStats[j].maxOverflow * UCF(FLOW)));
            getElapsedTime(NodeStats[j].maxOverflowDate, days, hrs, mins);
            R.fprintf(Frpt.file, L594, days.v, hrs.v, mins.v);
            R.fprintf(Frpt.file, L573, (NodeStats[j].volFlooded * Vcf));
            if ((RouteModel == DW))
            {
                R.fprintf(Frpt.file, L195, (((NodeStats[j].maxDepth - Node[j].fullDepth)) * UCF(LENGTH)));
            }
            else
            {
                R.fprintf(Frpt.file, L195, ((NodeStats[j].maxPondedVol / 1000.0) * UCF(VOLUME)));
            }
        }
        if ((n == 0))
        {
            report_writeLine((L595));
        }
        report_writeLine((L0));
    }

    // statsrpt.c:508
    static void writeStorageVolumes()
    {
        int j = 0;
        int k = 0;
        IBox days = new IBox(0);
        IBox hrs = new IBox(0);
        IBox mins = new IBox(0);
        double avgVol = 0.0;
        double maxVol = 0.0;
        double pctAvgVol = 0.0;
        double pctMaxVol = 0.0;
        double pctEvapLoss = 0.0;
        double pctSeepLoss = 0.0;
        if ((Nnodes[STORAGE] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L574));
            report_writeLine((L596));
            report_writeLine((L574));
            report_writeLine((L0));
            R.fprintf(Frpt.file, L597);
            if ((UnitSystem == US))
            {
                R.fprintf(Frpt.file, L598);
            }
            else
            {
                R.fprintf(Frpt.file, L599);
            }
            R.fprintf(Frpt.file, L600, FlowUnitWords[FlowUnits]);
            R.fprintf(Frpt.file, L601);
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                if ((Node[j].type != STORAGE))
                {
                    continue;
                }
                k = Node[j].subIndex;
                R.fprintf(Frpt.file, L541, Node[j].ID);
                avgVol = (StorageStats[k].avgVol / ((double) (ReportStepCount)));
                maxVol = StorageStats[k].maxVol;
                pctMaxVol = 0.0;
                pctAvgVol = 0.0;
                if ((Node[j].fullVolume > 0.0))
                {
                    pctAvgVol = ((avgVol / Node[j].fullVolume) * 100.0);
                    pctMaxVol = ((maxVol / Node[j].fullVolume) * 100.0);
                }
                pctEvapLoss = 0.0;
                pctSeepLoss = 0.0;
                if ((NodeInflow[j] > 0.0))
                {
                    pctEvapLoss = ((StorageStats[k].evapLosses / NodeInflow[j]) * 100.0);
                    pctSeepLoss = ((StorageStats[k].exfilLosses / NodeInflow[j]) * 100.0);
                }
                R.fprintf(Frpt.file, L602, ((avgVol * UCF(VOLUME)) / 1000.0), pctAvgVol, pctEvapLoss, pctSeepLoss, ((maxVol * UCF(VOLUME)) / 1000.0), pctMaxVol);
                getElapsedTime(StorageStats[k].maxVolDate, days, hrs, mins);
                R.fprintf(Frpt.file, L603, days.v, hrs.v, mins.v);
                R.fprintf(Frpt.file, FlowFmt, (StorageStats[k].maxFlow * UCF(FLOW)));
            }
            report_writeLine((L0));
        }
    }

    // statsrpt.c:575
    static void writeOutfallLoads()
    {
        CPtr units = R.buf(15);
        int i = 0;
        int j = 0;
        int k = 0;
        int p = 0;
        double x = 0.0;
        double outfallCount = 0.0;
        double flowCount = 0.0;
        double flowSum = 0.0;
        double freqSum = 0.0;
        double volSum = 0.0;
        double[] totals = null;
        if ((Nnodes[OUTFALL] > 0))
        {
            totals = new double[(int) (Nobjects[POLLUT])];
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                totals[p] = 0.0;
            }
            flowSum = 0.0;
            freqSum = 0.0;
            volSum = 0.0;
            report_writeLine((L0));
            report_writeLine((L604));
            report_writeLine((L605));
            report_writeLine((L604));
            report_writeLine((L0));
            R.fprintf(Frpt.file, L606);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L552);
            }
            R.fprintf(Frpt.file, L607);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L608);
            }
            R.fprintf(Frpt.file, L609);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L423, Pollut[p].ID);
            }
            R.fprintf(Frpt.file, L610, FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits], VolUnitsWords[UnitSystem]);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                i = UnitSystem;
                if ((Pollut[p].units == COUNT))
                {
                    i = 2;
                }
                sstrncpy(units, LoadUnitsWords[i], 14);
                R.fprintf(Frpt.file, L423, units);
            }
            R.fprintf(Frpt.file, L606);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L552);
            }
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                if ((Node[j].type != OUTFALL))
                {
                    continue;
                }
                k = Node[j].subIndex;
                flowCount = OutfallStats[k].totalPeriods;
                R.fprintf(Frpt.file, L541, Node[j].ID);
                x = ((100.0 * flowCount) / ((double) (ReportStepCount)));
                R.fprintf(Frpt.file, L611, x);
                freqSum += x;
                if ((flowCount > 0))
                {
                    x = ((OutfallStats[k].avgFlow * UCF(FLOW)) / flowCount);
                }
                else
                {
                    x = 0.0;
                }
                flowSum += x;
                R.fprintf(Frpt.file, L33);
                R.fprintf(Frpt.file, FlowFmt, x);
                R.fprintf(Frpt.file, L33);
                R.fprintf(Frpt.file, FlowFmt, (OutfallStats[k].maxFlow * UCF(FLOW)));
                R.fprintf(Frpt.file, L573, (NodeInflow[j] * Vcf));
                volSum += NodeInflow[j];
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    x = ((OutfallStats[k].totalLoad[p] * 28.317) * Pollut[p].mcf);
                    totals[p] += x;
                    if ((Pollut[p].units == COUNT))
                    {
                        x = ((((x) > 0.0) ? Glibm.log10((x)) : (x)));
                    }
                    R.fprintf(Frpt.file, L427, x);
                }
            }
            outfallCount = Nnodes[OUTFALL];
            R.fprintf(Frpt.file, L606);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(Frpt.file, L552);
            }
            R.fprintf(Frpt.file, L612, (freqSum / outfallCount));
            R.fprintf(Frpt.file, FlowFmt, flowSum);
            R.fprintf(Frpt.file, L33);
            R.fprintf(Frpt.file, FlowFmt, (MaxOutfallFlow * UCF(FLOW)));
            R.fprintf(Frpt.file, L573, (volSum * Vcf));
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                x = totals[p];
                if ((Pollut[p].units == COUNT))
                {
                    x = ((((x) > 0.0) ? Glibm.log10((x)) : (x)));
                }
                R.fprintf(Frpt.file, L427, x);
            }
            report_writeLine((L0));
            R.free(totals);
        }
    }

    // statsrpt.c:691
    static void writeLinkFlows()
    {
        int j = 0;
        int k = 0;
        IBox days = new IBox(0);
        IBox hrs = new IBox(0);
        IBox mins = new IBox(0);
        double v = 0.0;
        double fullDepth = 0.0;
        if ((Nobjects[LINK] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L235));
        report_writeLine((L613));
        report_writeLine((L235));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L614);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L615, FlowUnitWords[FlowUnits]);
        }
        else
        {
            R.fprintf(Frpt.file, L616, FlowUnitWords[FlowUnits]);
        }
        R.fprintf(Frpt.file, L617);
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            k = Link[j].subIndex;
            R.fprintf(Frpt.file, L541, Link[j].ID);
            if ((Link[j].xsect.type == DUMMY))
            {
                R.fprintf(Frpt.file, L618);
            }
            else if ((Link[j].xsect.type == IRREGULAR))
            {
                R.fprintf(Frpt.file, L619);
            }
            else
            {
                R.fprintf(Frpt.file, L620, LinkTypeWords[Link[j].type]);
            }
            getElapsedTime(LinkStats[j].maxFlowDate, days, hrs, mins);
            R.fprintf(Frpt.file, FlowFmt, (LinkStats[j].maxFlow * UCF(FLOW)));
            R.fprintf(Frpt.file, L570, days.v, hrs.v, mins.v);
            if (((Link[j].type == PUMP) && (Link[j].qFull > 0.0)))
            {
                R.fprintf(Frpt.file, L621);
                R.fprintf(Frpt.file, L622, (LinkStats[j].maxFlow / Link[j].qFull));
                continue;
            }
            if ((Link[j].xsect.type == DUMMY))
            {
                continue;
            }
            if ((Link[j].type == OUTLET))
            {
                continue;
            }
            if ((Link[j].type == CONDUIT))
            {
                v = (LinkStats[j].maxVeloc * UCF(LENGTH));
                if ((v > 50.0))
                {
                    R.fprintf(Frpt.file, L623);
                }
                else
                {
                    R.fprintf(Frpt.file, L624, v);
                }
                R.fprintf(Frpt.file, L622, ((LinkStats[j].maxFlow / Link[j].qFull) / ((double) (Conduit[k].barrels))));
            }
            else
            {
                R.fprintf(Frpt.file, L625);
            }
            fullDepth = Link[j].xsect.yFull;
            if (((Link[j].type == ORIFICE) && (Orifice[k].type == BOTTOM_ORIFICE)))
            {
                fullDepth = 0.0;
            }
            if ((fullDepth > 0.0))
            {
                R.fprintf(Frpt.file, L622, (LinkStats[j].maxDepth / fullDepth));
            }
            else
            {
                R.fprintf(Frpt.file, L626);
            }
        }
        report_writeLine((L0));
    }

    // statsrpt.c:778
    static void writeFlowClass()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        double totalSeconds = RoutingTimeSpan;
        if ((RouteModel != DW))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L473));
        report_writeLine((L627));
        report_writeLine((L473));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L628);
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if ((Link[j].type != CONDUIT))
            {
                continue;
            }
            if ((Link[j].xsect.type == DUMMY))
            {
                continue;
            }
            k = Link[j].subIndex;
            R.fprintf(Frpt.file, L541, Link[j].ID);
            R.fprintf(Frpt.file, L629, (Conduit[k].modLength / Conduit[k].length));
            for (i = 0; (i < MAX_FLOW_CLASSES); i++)
            {
                R.fprintf(Frpt.file, L630, (LinkStats[j].timeInFlowClass[i] /= totalSeconds));
            }
            R.fprintf(Frpt.file, L630, (LinkStats[j].timeNormalFlow / totalSeconds));
            R.fprintf(Frpt.file, L630, (LinkStats[j].timeInletControl / totalSeconds));
        }
        report_writeLine((L0));
    }

    // statsrpt.c:821
    static void writeLinkSurcharge()
    {
        int i = 0;
        int j = 0;
        int n = 0;
        double[] t = new double[5];
        report_writeLine((L0));
        report_writeLine((L470));
        report_writeLine((L631));
        report_writeLine((L470));
        report_writeLine((L0));
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if (((Link[j].type != CONDUIT) || (Link[j].xsect.type == DUMMY)))
            {
                continue;
            }
            t[0] = (LinkStats[j].timeSurcharged / 3600.0);
            t[1] = (LinkStats[j].timeFullUpstream / 3600.0);
            t[2] = (LinkStats[j].timeFullDnstream / 3600.0);
            t[3] = (LinkStats[j].timeFullFlow / 3600.0);
            if (((((t[0] + t[1]) + t[2]) + t[3]) == 0.0))
            {
                continue;
            }
            t[4] = (LinkStats[j].timeCapacityLimited / 3600.0);
            for (i = 0; (i < 5); i++)
            {
                t[i] = (((((0.01) >= (t[i]))) ? (0.01) : (t[i])));
            }
            if ((n == 0))
            {
                R.fprintf(Frpt.file, L632);
                n = 1;
            }
            R.fprintf(Frpt.file, L541, Link[j].ID);
            R.fprintf(Frpt.file, L633, t[0], t[1], t[2], t[3], t[4]);
        }
        if ((n == 0))
        {
            report_writeLine((L634));
        }
        report_writeLine((L0));
    }

    // statsrpt.c:862
    static void writePumpFlows()
    {
        int j = 0;
        int k = 0;
        double avgFlow = 0.0;
        double pctUtilized = 0.0;
        double pctOffCurve1 = 0.0;
        double pctOffCurve2 = 0.0;
        double totalSeconds = RoutingTimeSpan;
        if ((Nlinks[PUMP] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L221));
        report_writeLine((L635));
        report_writeLine((L221));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L636, FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits], VolUnitsWords[UnitSystem]);
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if ((Link[j].type != PUMP))
            {
                continue;
            }
            k = Link[j].subIndex;
            R.fprintf(Frpt.file, L541, Link[j].ID);
            pctUtilized = ((PumpStats[k].utilized / totalSeconds) * 100.0);
            avgFlow = PumpStats[k].avgFlow;
            if ((PumpStats[k].totalPeriods > 0))
            {
                avgFlow /= PumpStats[k].totalPeriods;
            }
            R.fprintf(Frpt.file, L637, pctUtilized, PumpStats[k].startUps, (PumpStats[k].minFlow * UCF(FLOW)), (avgFlow * UCF(FLOW)), (PumpStats[k].maxFlow * UCF(FLOW)), (PumpStats[k].volume * Vcf), PumpStats[k].energy);
            pctOffCurve1 = PumpStats[k].offCurveLow;
            pctOffCurve2 = PumpStats[k].offCurveHigh;
            if ((PumpStats[k].utilized > 0.0))
            {
                pctOffCurve1 = ((pctOffCurve1 / PumpStats[k].utilized) * 100.0);
                pctOffCurve2 = ((pctOffCurve2 / PumpStats[k].utilized) * 100.0);
            }
            R.fprintf(Frpt.file, L638, pctOffCurve1, pctOffCurve2);
        }
        report_writeLine((L0));
    }

    // statsrpt.c:916
    static void writeLinkLoads()
    {
        int i = 0;
        int j = 0;
        int p = 0;
        double x = 0.0;
        CPtr units = R.buf(15);
        CPtr linkLine = R.bufFrom(L551, 21);
        CPtr pollutLine = R.bufFrom(L552, 15);
        report_writeLine((L0));
        report_writeLine((L473));
        report_writeLine((L639));
        report_writeLine((L473));
        report_writeLine((L0));
        R.fprintf(Frpt.file, L356, linkLine);
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            R.fprintf(Frpt.file, L169, pollutLine);
        }
        R.fprintf(Frpt.file, L555);
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            R.fprintf(Frpt.file, L423, Pollut[p].ID);
        }
        R.fprintf(Frpt.file, L640);
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            i = UnitSystem;
            if ((Pollut[p].units == COUNT))
            {
                i = 2;
            }
            sstrncpy(units, LoadUnitsWords[i], 14);
            R.fprintf(Frpt.file, L423, units);
        }
        R.fprintf(Frpt.file, L356, linkLine);
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            R.fprintf(Frpt.file, L169, pollutLine);
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            R.fprintf(Frpt.file, L541, Link[j].ID);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                x = ((Link[j].totalLoad[p] * 28.317) * Pollut[p].mcf);
                if ((Pollut[p].units == COUNT))
                {
                    x = ((((x) > 0.0) ? Glibm.log10((x)) : (x)));
                }
                if ((x < 10000.0))
                {
                    R.fprintf(Frpt.file, L427, x);
                }
                else
                {
                    R.fprintf(Frpt.file, L641, x);
                }
            }
        }
        report_writeLine((L0));
    }

}
