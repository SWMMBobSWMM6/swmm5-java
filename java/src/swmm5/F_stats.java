// Generated from EPA SWMM 5.2.4 (stats.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_stats {
    static TTimeStepStats TimeStepStats = new TTimeStepStats();
    static TMaxStats[] MaxMassBalErrs = R.fill(new TMaxStats[5], TMaxStats::new);
    static TMaxStats[] MaxCourantCrit = R.fill(new TMaxStats[5], TMaxStats::new);
    static TMaxStats[] MaxFlowTurns = R.fill(new TMaxStats[5], TMaxStats::new);
    static TMaxStats[] MaxNonConverged = R.fill(new TMaxStats[5], TMaxStats::new);
    static double SysOutfallFlow = 0.0;
    static TSubcatchStats[] SubcatchStats = null;
    static TNodeStats[] NodeStats = null;
    static TLinkStats[] LinkStats = null;
    static TStorageStats[] StorageStats = null;
    static TOutfallStats[] OutfallStats = null;
    static TPumpStats[] PumpStats = null;
    static double MaxOutfallFlow = 0.0;
    static double MaxRunoffFlow = 0.0;
    static double RoutingTimeSpan = 0.0;

    // stats.c:102
    static int stats_open()
    {
        int j = 0;
        int k = 0;
        double timeStepDelta = 0.0;
        double logMaxTimeStep = 0.0;
        double logMinTimeStep = 0.0;
        NodeStats = (null);
        LinkStats = (null);
        StorageStats = (null);
        OutfallStats = (null);
        PumpStats = (null);
        SubcatchStats = (null);
        if ((Nobjects[SUBCATCH] > 0))
        {
            SubcatchStats = R.fill(new TSubcatchStats[(int) (Nobjects[SUBCATCH])], TSubcatchStats::new);
            if (!(SubcatchStats != null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            for (j = 0; (j < Nobjects[SUBCATCH]); j++)
            {
                SubcatchStats[j].precip = 0.0;
                SubcatchStats[j].runon = 0.0;
                SubcatchStats[j].evap = 0.0;
                SubcatchStats[j].infil = 0.0;
                SubcatchStats[j].runoff = 0.0;
                SubcatchStats[j].maxFlow = 0.0;
                SubcatchStats[j].impervRunoff = 0.0;
                SubcatchStats[j].pervRunoff = 0.0;
            }
            for (j = 0; (j < Nobjects[SUBCATCH]); j++)
            {
                if ((Subcatch[j].groundwater == null))
                {
                    continue;
                }
                Subcatch[j].groundwater.stats.avgUpperMoist = 0.0;
                Subcatch[j].groundwater.stats.avgWaterTable = 0.0;
                Subcatch[j].groundwater.stats.infil = 0.0;
                Subcatch[j].groundwater.stats.latFlow = 0.0;
                Subcatch[j].groundwater.stats.deepFlow = 0.0;
                Subcatch[j].groundwater.stats.evap = 0.0;
                Subcatch[j].groundwater.stats.maxFlow = 0.0;
                Subcatch[j].groundwater.stats.finalUpperMoist = 0.0;
                Subcatch[j].groundwater.stats.finalWaterTable = 0.0;
            }
        }
        if ((Nobjects[LINK] > 0))
        {
            NodeStats = R.fill(new TNodeStats[(int) (Nobjects[NODE])], TNodeStats::new);
            LinkStats = R.fill(new TLinkStats[(int) (Nobjects[LINK])], TLinkStats::new);
            if (((!(NodeStats != null)) || (!(LinkStats != null))))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
        }
        if ((NodeStats != null))
        {
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                NodeStats[j].avgDepth = 0.0;
                NodeStats[j].maxDepth = 0.0;
                NodeStats[j].maxDepthDate = StartDateTime;
                NodeStats[j].maxRptDepth = 0.0;
                NodeStats[j].volFlooded = 0.0;
                NodeStats[j].timeFlooded = 0.0;
                NodeStats[j].timeSurcharged = 0.0;
                NodeStats[j].timeCourantCritical = 0.0;
                NodeStats[j].totLatFlow = 0.0;
                NodeStats[j].maxLatFlow = 0.0;
                NodeStats[j].maxInflow = 0.0;
                NodeStats[j].maxOverflow = 0.0;
                NodeStats[j].maxPondedVol = 0.0;
                NodeStats[j].nonConvergedCount = 0;
                NodeStats[j].maxInflowDate = StartDateTime;
                NodeStats[j].maxOverflowDate = StartDateTime;
            }
        }
        if ((LinkStats != null))
        {
            for (j = 0; (j < Nobjects[LINK]); j++)
            {
                LinkStats[j].maxFlow = 0.0;
                LinkStats[j].maxVeloc = 0.0;
                LinkStats[j].maxDepth = 0.0;
                LinkStats[j].maxStreetFilled = 0.0;
                LinkStats[j].timeSurcharged = 0.0;
                LinkStats[j].timeFullUpstream = 0.0;
                LinkStats[j].timeFullDnstream = 0.0;
                LinkStats[j].timeFullFlow = 0.0;
                LinkStats[j].timeCapacityLimited = 0.0;
                LinkStats[j].timeCourantCritical = 0.0;
                for (k = 0; (k < MAX_FLOW_CLASSES); k++)
                {
                    LinkStats[j].timeInFlowClass[k] = 0.0;
                }
                LinkStats[j].flowTurns = 0;
                LinkStats[j].flowTurnSign = 0;
            }
        }
        if ((Nnodes[STORAGE] > 0))
        {
            StorageStats = R.fill(new TStorageStats[(int) (Nnodes[STORAGE])], TStorageStats::new);
            if (!(StorageStats != null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            else
            {
                for (k = 0; (k < Nobjects[NODE]); k++)
                {
                    if ((Node[k].type != STORAGE))
                    {
                        continue;
                    }
                    j = Node[k].subIndex;
                    StorageStats[j].initVol = Node[k].newVolume;
                    StorageStats[j].avgVol = 0.0;
                    StorageStats[j].maxVol = 0.0;
                    StorageStats[j].maxFlow = 0.0;
                    StorageStats[j].evapLosses = 0.0;
                    StorageStats[j].exfilLosses = 0.0;
                    StorageStats[j].maxVolDate = StartDateTime;
                }
            }
        }
        if ((Nnodes[OUTFALL] > 0))
        {
            OutfallStats = R.fill(new TOutfallStats[(int) (Nnodes[OUTFALL])], TOutfallStats::new);
            if (!(OutfallStats != null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            else
            {
                for (j = 0; (j < Nnodes[OUTFALL]); j++)
                {
                    OutfallStats[j].avgFlow = 0.0;
                    OutfallStats[j].maxFlow = 0.0;
                    OutfallStats[j].totalPeriods = 0;
                    if ((Nobjects[POLLUT] > 0))
                    {
                        OutfallStats[j].totalLoad = new double[(int) (Nobjects[POLLUT])];
                        if (!(OutfallStats[j].totalLoad != null))
                        {
                            report_writeErrorMsg(ERR_MEMORY, L0);
                            return ErrorCode;
                        }
                        for (k = 0; (k < Nobjects[POLLUT]); k++)
                        {
                            OutfallStats[j].totalLoad[k] = 0.0;
                        }
                    }
                    else
                    {
                        OutfallStats[j].totalLoad = (null);
                    }
                }
            }
        }
        if ((Nlinks[PUMP] > 0))
        {
            PumpStats = R.fill(new TPumpStats[(int) (Nlinks[PUMP])], TPumpStats::new);
            if (!(PumpStats != null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            else
            {
                for (j = 0; (j < Nlinks[PUMP]); j++)
                {
                    PumpStats[j].utilized = 0.0;
                    PumpStats[j].minFlow = 0.0;
                    PumpStats[j].avgFlow = 0.0;
                    PumpStats[j].maxFlow = 0.0;
                    PumpStats[j].volume = 0.0;
                    PumpStats[j].energy = 0.0;
                    PumpStats[j].startUps = 0;
                    PumpStats[j].offCurveLow = 0.0;
                    PumpStats[j].offCurveHigh = 0.0;
                }
            }
        }
        MaxRunoffFlow = 0.0;
        MaxOutfallFlow = 0.0;
        TimeStepStats.maxTimeStep = 0.0;
        TimeStepStats.minTimeStep = RouteStep;
        TimeStepStats.routingTime = 0.0;
        TimeStepStats.trialsCount = 0.0;
        TimeStepStats.steadyStateTime = 0.0;
        TimeStepStats.timeStepCount = 0;
        logMaxTimeStep = Glibm.log10(RouteStep);
        logMinTimeStep = Glibm.log10(MinRouteStep.v);
        timeStepDelta = (((logMaxTimeStep - logMinTimeStep)) / ((double) (((6 - 1)))));
        TimeStepStats.timeStepIntervals[0] = RouteStep;
        for (j = 1; (j < 6); j++)
        {
            TimeStepStats.timeStepIntervals[j] = Glibm.pow(10.0, (logMaxTimeStep - (j * timeStepDelta)));
            TimeStepStats.timeStepCounts[j] = 0;
        }
        TimeStepStats.timeStepIntervals[(6 - 1)] = MinRouteStep.v;
        RoutingTimeSpan = 0.0;
        return 0;
    }

    // stats.c:318
    static void stats_close()
    {
        int j = 0;
        {
            if ((SubcatchStats != null))
            {
                R.free(SubcatchStats);
                SubcatchStats = (null);
            }
        }
        ;
        {
            if ((NodeStats != null))
            {
                R.free(NodeStats);
                NodeStats = (null);
            }
        }
        ;
        {
            if ((LinkStats != null))
            {
                R.free(LinkStats);
                LinkStats = (null);
            }
        }
        ;
        {
            if ((StorageStats != null))
            {
                R.free(StorageStats);
                StorageStats = (null);
            }
        }
        ;
        if ((OutfallStats != null))
        {
            for (j = 0; (j < Nnodes[OUTFALL]); j++)
            {
                if ((OutfallStats[j].totalLoad != null))
                {
                    R.free(OutfallStats[j].totalLoad);
                    OutfallStats[j].totalLoad = (null);
                }
            }
            ;
            {
                if ((OutfallStats != null))
                {
                    R.free(OutfallStats);
                    OutfallStats = (null);
                }
            }
            ;
        }
        {
            if ((PumpStats != null))
            {
                R.free(PumpStats);
                PumpStats = (null);
            }
        }
        ;
    }

    // stats.c:342
    static void stats_report()
    {
        if (((Nobjects[LINK] > 0) && (RouteModel != NO_ROUTING)))
        {
            stats_findMaxStats();
            if (((!(RptFlags.disabled != 0)) && (RptFlags.flowStats != 0)))
            {
                report_writeMaxStats(MaxMassBalErrs, MaxCourantCrit, 5);
                report_writeMaxFlowTurns(MaxFlowTurns, 5);
                report_writeNonconvergedStats(MaxNonConverged, 5);
                report_writeTimeStepStats(TimeStepStats);
            }
        }
        if (!(RptFlags.disabled != 0))
        {
            statsrpt_writeReport();
        }
    }

    // stats.c:369
    static void stats_updateSubcatchStats(int j, double rainVol, double runonVol, double evapVol, double infilVol, double impervVol, double pervVol, double runoffVol, double runoff)
    {
        SubcatchStats[j].precip += rainVol;
        SubcatchStats[j].runon += runonVol;
        SubcatchStats[j].evap += evapVol;
        SubcatchStats[j].infil += infilVol;
        SubcatchStats[j].runoff += runoffVol;
        SubcatchStats[j].maxFlow = (((((SubcatchStats[j].maxFlow) >= (runoff))) ? (SubcatchStats[j].maxFlow) : (runoff)));
        SubcatchStats[j].impervRunoff += impervVol;
        SubcatchStats[j].pervRunoff += pervVol;
    }

    // stats.c:399
    static void stats_updateGwaterStats(int j, double infil, double evap, double latFlow, double deepFlow, double theta, double waterTable, double tStep)
    {
        Subcatch[j].groundwater.stats.infil += (infil * tStep);
        Subcatch[j].groundwater.stats.evap += (evap * tStep);
        Subcatch[j].groundwater.stats.latFlow += (latFlow * tStep);
        Subcatch[j].groundwater.stats.deepFlow += (deepFlow * tStep);
        Subcatch[j].groundwater.stats.avgUpperMoist += (theta * tStep);
        Subcatch[j].groundwater.stats.avgWaterTable += (waterTable * tStep);
        Subcatch[j].groundwater.stats.finalUpperMoist = theta;
        Subcatch[j].groundwater.stats.finalWaterTable = waterTable;
        if ((Math.abs(latFlow) > Math.abs(Subcatch[j].groundwater.stats.maxFlow)))
        {
            Subcatch[j].groundwater.stats.maxFlow = latFlow;
        }
    }

    // stats.c:419
    static void stats_updateMaxRunoff()
    {
        int j = 0;
        double sysRunoff = 0.0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            sysRunoff += Subcatch[j].newRunoff;
        }
        MaxRunoffFlow = (((((MaxRunoffFlow) >= (sysRunoff))) ? (MaxRunoffFlow) : (sysRunoff)));
    }

    // stats.c:435
    static void stats_updateMaxNodeDepth(int j, double depth)
    {
        if ((NodeStats != null))
        {
            NodeStats[j].maxRptDepth = (((((NodeStats[j].maxRptDepth) >= (depth))) ? (NodeStats[j].maxRptDepth) : (depth)));
        }
    }

    // stats.c:449
    static void stats_updateFlowStats(double tStep, double aDate)
    {
        int j = 0;
        if ((aDate < ReportStart))
        {
            return;
        }
        SysOutfallFlow = 0.0;
        {
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                stats_updateNodeStats(j, tStep, aDate);
            }
            for (j = 0; (j < Nobjects[LINK]); j++)
            {
                stats_updateLinkStats(j, tStep, aDate);
            }
        }
        ReportStepCount++;
        RoutingTimeSpan += tStep;
        MaxOutfallFlow = (((((MaxOutfallFlow) >= (SysOutfallFlow))) ? (MaxOutfallFlow) : (SysOutfallFlow)));
    }

    // stats.c:484
    static void stats_updateTimeStepStats(double tStep, int trialsCount, int steadyState)
    {
        int j = 0;
        TimeStepStats.steadyStateTime += (steadyState * tStep);
        if ((steadyState == 0))
        {
            if ((OldRoutingTime > 0))
            {
                TimeStepStats.minTimeStep = (((((TimeStepStats.minTimeStep) <= (tStep))) ? (TimeStepStats.minTimeStep) : (tStep)));
                for (j = 1; (j < 6); j++)
                {
                    if ((tStep >= TimeStepStats.timeStepIntervals[j]))
                    {
                        TimeStepStats.timeStepCounts[j]++;
                        break;
                    }
                }
            }
            TimeStepStats.maxTimeStep = (((((TimeStepStats.maxTimeStep) >= (tStep))) ? (TimeStepStats.maxTimeStep) : (tStep)));
            TimeStepStats.routingTime += tStep;
            TimeStepStats.timeStepCount++;
            TimeStepStats.trialsCount += trialsCount;
        }
    }

    // stats.c:522
    static void stats_updateCriticalTimeCount(int node, int link)
    {
        if ((node >= 0))
        {
            NodeStats[node].timeCourantCritical += 1.0;
        }
        else if ((link >= 0))
        {
            LinkStats[link].timeCourantCritical += 1.0;
        }
    }

    // stats.c:536
    static void stats_updateConvergenceStats(int node, int converged)
    {
        if ((converged == 0))
        {
            NodeStats[node].nonConvergedCount++;
        }
    }

    // stats.c:543
    static void stats_updateNodeStats(int j, double tStep, double aDate)
    {
        int k = 0;
        int p = 0;
        double newVolume = Node[j].newVolume;
        double newDepth = Node[j].newDepth;
        int canPond = ((((AllowPonding != 0) && (Node[j].pondedArea > 0.0))) ? 1 : 0);
        NodeStats[j].avgDepth += newDepth;
        if ((newDepth > NodeStats[j].maxDepth))
        {
            NodeStats[j].maxDepth = newDepth;
            NodeStats[j].maxDepthDate = aDate;
        }
        if ((Node[j].type != OUTFALL))
        {
            if (((newVolume > Node[j].fullVolume) || (Node[j].overflow > 0.0)))
            {
                NodeStats[j].timeFlooded += tStep;
                NodeStats[j].volFlooded += (Node[j].overflow * tStep);
                if ((canPond != 0))
                {
                    NodeStats[j].maxPondedVol = (((((NodeStats[j].maxPondedVol) >= (((newVolume - Node[j].fullVolume))))) ? (NodeStats[j].maxPondedVol) : (((newVolume - Node[j].fullVolume)))));
                }
            }
            if ((RouteModel == DW))
            {
                if (((((Node[j].type != STORAGE) || (Node[j].surDepth > 0.0))) && (((newDepth + Node[j].invertElev) + 0.0001) >= Node[j].crownElev)))
                {
                    NodeStats[j].timeSurcharged += tStep;
                }
            }
        }
        if ((Node[j].type == STORAGE))
        {
            k = Node[j].subIndex;
            StorageStats[k].avgVol += newVolume;
            StorageStats[k].evapLosses += Storage[Node[j].subIndex].evapLoss;
            StorageStats[k].exfilLosses += Storage[Node[j].subIndex].exfilLoss;
            newVolume = (((((newVolume) <= (Node[j].fullVolume))) ? (newVolume) : (Node[j].fullVolume)));
            if ((newVolume > StorageStats[k].maxVol))
            {
                StorageStats[k].maxVol = newVolume;
                StorageStats[k].maxVolDate = aDate;
            }
            StorageStats[k].maxFlow = (((((StorageStats[k].maxFlow) >= (Node[j].outflow))) ? (StorageStats[k].maxFlow) : (Node[j].outflow)));
        }
        if ((Node[j].type == OUTFALL))
        {
            k = Node[j].subIndex;
            if ((Node[j].inflow >= 0.001))
            {
                OutfallStats[k].avgFlow += Node[j].inflow;
                OutfallStats[k].maxFlow = (((((OutfallStats[k].maxFlow) >= (Node[j].inflow))) ? (OutfallStats[k].maxFlow) : (Node[j].inflow)));
                OutfallStats[k].totalPeriods++;
            }
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                OutfallStats[k].totalLoad[p] += ((Node[j].inflow * Node[j].newQual[p]) * tStep);
            }
            SysOutfallFlow += Node[j].inflow;
        }
        NodeStats[j].totLatFlow += (((((Node[j].oldLatFlow + Node[j].newLatFlow)) * 0.5) * tStep));
        if ((Math.abs(Node[j].newLatFlow) > Math.abs(NodeStats[j].maxLatFlow)))
        {
            NodeStats[j].maxLatFlow = Node[j].newLatFlow;
        }
        if ((Node[j].inflow > NodeStats[j].maxInflow))
        {
            NodeStats[j].maxInflow = Node[j].inflow;
            NodeStats[j].maxInflowDate = aDate;
        }
        if ((Node[j].overflow > NodeStats[j].maxOverflow))
        {
            NodeStats[j].maxOverflow = Node[j].overflow;
            NodeStats[j].maxOverflowDate = aDate;
        }
    }

    // stats.c:647
    static void stats_updateLinkStats(int j, double tStep, double aDate)
    {
        int k = 0;
        double q = 0.0;
        double v = 0.0;
        double dq = 0.0;
        dq = (Link[j].newFlow - Link[j].oldFlow);
        q = Math.abs(Link[j].newFlow);
        if ((q > LinkStats[j].maxFlow))
        {
            LinkStats[j].maxFlow = q;
            LinkStats[j].maxFlowDate = aDate;
        }
        v = link_getVelocity(j, q, Link[j].newDepth);
        if ((v > LinkStats[j].maxVeloc))
        {
            LinkStats[j].maxVeloc = v;
        }
        if ((Link[j].newDepth > LinkStats[j].maxDepth))
        {
            LinkStats[j].maxDepth = Link[j].newDepth;
        }
        if ((Link[j].type == PUMP))
        {
            if ((q >= Link[j].qFull))
            {
                LinkStats[j].timeFullFlow += tStep;
            }
            if ((q > 0.001))
            {
                k = Link[j].subIndex;
                PumpStats[k].minFlow = (((((PumpStats[k].minFlow) <= (q))) ? (PumpStats[k].minFlow) : (q)));
                PumpStats[k].maxFlow = LinkStats[j].maxFlow;
                PumpStats[k].avgFlow += q;
                PumpStats[k].volume += (q * tStep);
                PumpStats[k].utilized += tStep;
                PumpStats[k].energy += ((link_getPower(j) * tStep) / 3600.0);
                if ((Link[j].flowClass == DN_DRY))
                {
                    PumpStats[k].offCurveLow += tStep;
                }
                if ((Link[j].flowClass == UP_DRY))
                {
                    PumpStats[k].offCurveHigh += tStep;
                }
                if ((Link[j].oldFlow < 0.001))
                {
                    PumpStats[k].startUps++;
                }
                PumpStats[k].totalPeriods++;
                LinkStats[j].timeSurcharged += tStep;
                LinkStats[j].timeFullUpstream += tStep;
                LinkStats[j].timeFullDnstream += tStep;
            }
        }
        else if ((Link[j].type == CONDUIT))
        {
            if ((Link[j].normalFlow != 0))
            {
                LinkStats[j].timeNormalFlow += tStep;
            }
            if ((Link[j].inletControl != 0))
            {
                LinkStats[j].timeInletControl += tStep;
            }
            k = Link[j].flowClass;
            if (((k >= 0) && (k < MAX_FLOW_CLASSES)))
            {
                LinkStats[j].timeInFlowClass[k] += tStep;
            }
            k = Link[j].subIndex;
            if ((q >= (Link[j].qFull * ((double) (Conduit[k].barrels)))))
            {
                LinkStats[j].timeFullFlow += tStep;
            }
            if ((Conduit[k].capacityLimited != 0))
            {
                LinkStats[j].timeCapacityLimited += tStep;
            }
            switch (Conduit[k].fullState) {
                case ALL_FULL:
                    LinkStats[j].timeSurcharged += tStep;
                    LinkStats[j].timeFullUpstream += tStep;
                    LinkStats[j].timeFullDnstream += tStep;
                    break;
                case UP_FULL:
                    LinkStats[j].timeFullUpstream += tStep;
                    break;
                case DN_FULL:
                    LinkStats[j].timeFullDnstream += tStep;
            }
            if ((Link[j].xsect.type == STREET_XSECT))
            {
                LinkStats[j].maxStreetFilled = (((((LinkStats[j].maxStreetFilled) >= (street_getExtentFilled(j)))) ? (LinkStats[j].maxStreetFilled) : (street_getExtentFilled(j))));
            }
        }
        k = LinkStats[j].flowTurnSign;
        LinkStats[j].flowTurnSign = (((((dq) < 0)) ? ((-1)) : (1)));
        if (((Math.abs(dq) > 0.001) && ((k * LinkStats[j].flowTurnSign) < 0)))
        {
            LinkStats[j].flowTurns++;
        }
    }

    // stats.c:756
    static void stats_findMaxStats()
    {
        int j = 0;
        double x = 0.0;
        double z = 0.0;
        double stepCount = 0.0;
        for (j = 0; (j < 5); j++)
        {
            MaxMassBalErrs[j].objType = NODE;
            MaxMassBalErrs[j].index = (-1);
            MaxMassBalErrs[j].value = (-1.0);
            MaxCourantCrit[j].index = (-1);
            MaxCourantCrit[j].value = (-1.0);
            MaxFlowTurns[j].index = (-1);
            MaxFlowTurns[j].value = (-1.0);
            MaxNonConverged[j].index = (-1);
            MaxNonConverged[j].value = 0.0;
        }
        if ((ReportStepCount > 2))
        {
            stepCount = ReportStepCount;
            z = (100.0 / (((2.0 / 3.0) * ((stepCount - 2.0)))));
            for (j = 0; (j < Nobjects[LINK]); j++)
            {
                x = (LinkStats[j].flowTurns * z);
                stats_updateMaxStats(MaxFlowTurns, LINK, j, x);
            }
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if ((Node[j].degree <= 0))
            {
                continue;
            }
            if ((NodeInflow[j] <= 0.1))
            {
                continue;
            }
            if ((NodeInflow[j] > 0.0))
            {
                x = (1.0 - (NodeOutflow[j] / NodeInflow[j]));
            }
            else if ((NodeOutflow[j] > 0.0))
            {
                x = (-1.0);
            }
            else
            {
                x = 0.0;
            }
            stats_updateMaxStats(MaxMassBalErrs, NODE, j, (100.0 * x));
        }
        stepCount = TimeStepStats.timeStepCount;
        if ((RouteModel == DW))
        {
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                stats_updateMaxStats(MaxNonConverged, NODE, j, (NodeStats[j].nonConvergedCount / stepCount));
            }
        }
        if (((RouteModel != DW) || (CourantFactor.v == 0.0)))
        {
            return;
        }
        if ((stepCount == 0))
        {
            return;
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            x = (NodeStats[j].timeCourantCritical / stepCount);
            stats_updateMaxStats(MaxCourantCrit, NODE, j, (100.0 * x));
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            x = (LinkStats[j].timeCourantCritical / stepCount);
            stats_updateMaxStats(MaxCourantCrit, LINK, j, (100.0 * x));
        }
    }

    // stats.c:841
    static void stats_updateMaxStats(TMaxStats[] maxStats, int i, int j, double x)
    {
        int k = 0;
        TMaxStats maxStats1 = new TMaxStats();
        TMaxStats maxStats2 = new TMaxStats();
        maxStats1.objType = i;
        maxStats1.index = j;
        maxStats1.value = x;
        for (k = 0; (k < 5); k++)
        {
            if ((Math.abs(maxStats1.value) > Math.abs(maxStats[k].value)))
            {
                maxStats2 = maxStats[k].copy();
                maxStats[k] = maxStats1.copy();
                maxStats1 = maxStats2.copy();
            }
        }
    }

}
