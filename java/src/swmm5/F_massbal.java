// Generated from EPA SWMM 5.2.4 (massbal.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_massbal {
    static double MAX_RUNOFF_BALANCE_ERR = 10.0;
    static double MAX_FLOW_BALANCE_ERR = 10.0;
    static TRunoffTotals RunoffTotals = new TRunoffTotals();
    static TLoadingTotals[] LoadingTotals = null;
    static TGwaterTotals GwaterTotals = new TGwaterTotals();
    static TRoutingTotals FlowTotals = new TRoutingTotals();
    static TRoutingTotals[] QualTotals = null;
    static TRoutingTotals StepFlowTotals = new TRoutingTotals();
    static TRoutingTotals OldStepFlowTotals = new TRoutingTotals();
    static TRoutingTotals[] StepQualTotals = null;
    static double[] NodeInflow = null;
    static double[] NodeOutflow = null;
    static double TotalArea = 0.0;

    // massbal.c:100
    static int massbal_open()
    {
        int j = 0;
        int n = 0;
        RunoffError = 0.0;
        GwaterError = 0.0;
        FlowError = 0.0;
        QualError = 0.0;
        RunoffTotals.rainfall = 0.0;
        RunoffTotals.evap = 0.0;
        RunoffTotals.infil = 0.0;
        RunoffTotals.runoff = 0.0;
        RunoffTotals.runon = 0.0;
        RunoffTotals.drains = 0.0;
        RunoffTotals.snowRemoved = 0.0;
        RunoffTotals.initStorage = 0.0;
        RunoffTotals.initSnowCover = 0.0;
        TotalArea = 0.0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            RunoffTotals.initStorage += (subcatch_getStorage(j));
            RunoffTotals.initSnowCover += (snow_getSnowCover(j));
            TotalArea += Subcatch[j].area;
        }
        GwaterTotals.infil = 0.0;
        GwaterTotals.upperEvap = 0.0;
        GwaterTotals.lowerEvap = 0.0;
        GwaterTotals.lowerPerc = 0.0;
        GwaterTotals.gwater = 0.0;
        GwaterTotals.initStorage = 0.0;
        GwaterTotals.finalStorage = 0.0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            GwaterTotals.initStorage += (gwater_getVolume(j) * Subcatch[j].area);
        }
        FlowTotals.dwInflow = 0.0;
        FlowTotals.wwInflow = 0.0;
        FlowTotals.gwInflow = 0.0;
        FlowTotals.iiInflow = 0.0;
        FlowTotals.exInflow = 0.0;
        FlowTotals.flooding = 0.0;
        FlowTotals.outflow = 0.0;
        FlowTotals.evapLoss = 0.0;
        FlowTotals.seepLoss = 0.0;
        FlowTotals.reacted = 0.0;
        FlowTotals.initStorage = 0.0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            FlowTotals.initStorage += Node[j].newVolume;
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            FlowTotals.initStorage += Link[j].newVolume;
        }
        StepFlowTotals = FlowTotals.copy();
        LoadingTotals = (null);
        QualTotals = (null);
        StepQualTotals = (null);
        NodeInflow = (null);
        NodeOutflow = (null);
        n = Nobjects[POLLUT];
        if ((n > 0))
        {
            LoadingTotals = R.fill(new TLoadingTotals[(int) (n)], TLoadingTotals::new);
            if ((LoadingTotals == null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            for (j = 0; (j < n); j++)
            {
                LoadingTotals[j].initLoad = massbal_getBuildup(j);
                LoadingTotals[j].buildup = 0.0;
                LoadingTotals[j].deposition = 0.0;
                LoadingTotals[j].sweeping = 0.0;
                LoadingTotals[j].infil = 0.0;
                LoadingTotals[j].bmpRemoval = 0.0;
                LoadingTotals[j].runoff = 0.0;
                LoadingTotals[j].finalLoad = 0.0;
            }
        }
        if ((n > 0))
        {
            QualTotals = R.fill(new TRoutingTotals[(int) (n)], TRoutingTotals::new);
            StepQualTotals = R.fill(new TRoutingTotals[(int) (n)], TRoutingTotals::new);
            if (((QualTotals == null) || (StepQualTotals == null)))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
        }
        for (j = 0; (j < n); j++)
        {
            QualTotals[j].dwInflow = 0.0;
            QualTotals[j].wwInflow = 0.0;
            QualTotals[j].gwInflow = 0.0;
            QualTotals[j].exInflow = 0.0;
            QualTotals[j].flooding = 0.0;
            QualTotals[j].outflow = 0.0;
            QualTotals[j].evapLoss = 0.0;
            QualTotals[j].seepLoss = 0.0;
            QualTotals[j].reacted = 0.0;
            QualTotals[j].initStorage = massbal_getStoredMass(j);
        }
        massbal_initTimeStepTotals();
        if ((Nobjects[NODE] > 0))
        {
            NodeInflow = new double[(int) (Nobjects[NODE])];
            if ((NodeInflow == null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            NodeOutflow = new double[(int) (Nobjects[NODE])];
            if ((NodeOutflow == null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                NodeInflow[j] = Node[j].newVolume;
            }
        }
        return ErrorCode;
    }

    // massbal.c:246
    static void massbal_close()
    {
        {
            if ((LoadingTotals != null))
            {
                R.free(LoadingTotals);
                LoadingTotals = (null);
            }
        }
        ;
        {
            if ((QualTotals != null))
            {
                R.free(QualTotals);
                QualTotals = (null);
            }
        }
        ;
        {
            if ((StepQualTotals != null))
            {
                R.free(StepQualTotals);
                StepQualTotals = (null);
            }
        }
        ;
        {
            if ((NodeInflow != null))
            {
                R.free(NodeInflow);
                NodeInflow = (null);
            }
        }
        ;
        {
            if ((NodeOutflow != null))
            {
                R.free(NodeOutflow);
                NodeOutflow = (null);
            }
        }
        ;
    }

    // massbal.c:262
    static void massbal_report()
    {
        int j = 0;
        double gwArea = 0.0;
        if ((Nobjects[SUBCATCH] > 0))
        {
            if (((massbal_getRunoffError() > MAX_RUNOFF_BALANCE_ERR) || (RptFlags.continuity == 1)))
            {
                report_writeRunoffError(RunoffTotals, TotalArea);
            }
            if (((Nobjects[POLLUT] > 0) && (!(IgnoreQuality != 0))))
            {
                if (((massbal_getLoadingError() > MAX_RUNOFF_BALANCE_ERR) || (RptFlags.continuity == 1)))
                {
                    report_writeLoadingError(LoadingTotals);
                }
            }
        }
        if (((Nobjects[AQUIFER] > 0) && (!(IgnoreGwater != 0))))
        {
            if (((massbal_getGwaterError() > MAX_RUNOFF_BALANCE_ERR) || (RptFlags.continuity == 1)))
            {
                for (j = 0; (j < Nobjects[SUBCATCH]); j++)
                {
                    if ((Subcatch[j].groundwater != null))
                    {
                        gwArea += Subcatch[j].area;
                    }
                }
                if ((gwArea > 0.0))
                {
                    report_writeGwaterError(GwaterTotals, gwArea);
                }
            }
        }
        if (((Nobjects[NODE] > 0) && (!(IgnoreRouting != 0))))
        {
            if (((massbal_getFlowError() > MAX_FLOW_BALANCE_ERR) || (RptFlags.continuity == 1)))
            {
                report_writeFlowError(FlowTotals);
            }
            if (((Nobjects[POLLUT] > 0) && (!(IgnoreQuality != 0))))
            {
                if (((massbal_getQualError() > MAX_FLOW_BALANCE_ERR) || (RptFlags.continuity == 1)))
                {
                    report_writeQualError(QualTotals);
                }
            }
        }
    }

    // massbal.c:316
    static double massbal_getBuildup(int p)
    {
        int i = 0;
        int j = 0;
        double load = 0.0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            for (i = 0; (i < Nobjects[LANDUSE]); i++)
            {
                load += Subcatch[j].landFactor[i].buildup[p];
            }
            load += (Subcatch[j].pondedQual[p] * Pollut[p].mcf);
        }
        return load;
    }

    // massbal.c:339
    static void massbal_updateRunoffTotals(int flowType, double v)
    {
        switch (flowType) {
            case RUNOFF_RAINFALL:
                RunoffTotals.rainfall += v;
                break;
            case RUNOFF_EVAP:
                RunoffTotals.evap += v;
                break;
            case RUNOFF_INFIL:
                RunoffTotals.infil += v;
                break;
            case RUNOFF_RUNOFF:
                RunoffTotals.runoff += v;
                break;
            case RUNOFF_DRAINS:
                RunoffTotals.drains += v;
                break;
            case RUNOFF_RUNON:
                RunoffTotals.runon += v;
                break;
        }
    }

    // massbal.c:360
    static void massbal_updateGwaterTotals(double vInfil, double vUpperEvap, double vLowerEvap, double vLowerPerc, double vGwater)
    {
        GwaterTotals.infil += vInfil;
        GwaterTotals.upperEvap += vUpperEvap;
        GwaterTotals.lowerEvap += vLowerEvap;
        GwaterTotals.lowerPerc += vLowerPerc;
        GwaterTotals.gwater += vGwater;
    }

    // massbal.c:381
    static void massbal_initTimeStepTotals()
    {
        int j = 0;
        OldStepFlowTotals = StepFlowTotals.copy();
        StepFlowTotals.dwInflow = 0.0;
        StepFlowTotals.wwInflow = 0.0;
        StepFlowTotals.gwInflow = 0.0;
        StepFlowTotals.iiInflow = 0.0;
        StepFlowTotals.exInflow = 0.0;
        StepFlowTotals.flooding = 0.0;
        StepFlowTotals.outflow = 0.0;
        StepFlowTotals.evapLoss = 0.0;
        StepFlowTotals.seepLoss = 0.0;
        StepFlowTotals.reacted = 0.0;
        for (j = 0; (j < Nobjects[POLLUT]); j++)
        {
            StepQualTotals[j].dwInflow = 0.0;
            StepQualTotals[j].wwInflow = 0.0;
            StepQualTotals[j].gwInflow = 0.0;
            StepQualTotals[j].iiInflow = 0.0;
            StepQualTotals[j].exInflow = 0.0;
            StepQualTotals[j].flooding = 0.0;
            StepQualTotals[j].outflow = 0.0;
            StepQualTotals[j].reacted = 0.0;
            StepQualTotals[j].seepLoss = 0.0;
            StepQualTotals[j].initStorage = 0.0;
            StepQualTotals[j].finalStorage = 0.0;
        }
    }

    // massbal.c:418
    static void massbal_addInflowFlow(int type, double q)
    {
        switch (type) {
            case DRY_WEATHER_INFLOW:
                StepFlowTotals.dwInflow += q;
                break;
            case WET_WEATHER_INFLOW:
                StepFlowTotals.wwInflow += q;
                break;
            case GROUNDWATER_INFLOW:
                StepFlowTotals.gwInflow += q;
                break;
            case RDII_INFLOW:
                StepFlowTotals.iiInflow += q;
                break;
            case EXTERNAL_INFLOW:
                StepFlowTotals.exInflow += q;
                break;
        }
    }

    // massbal.c:438
    static void massbal_updateLoadingTotals(int type, int p, double w)
    {
        switch (type) {
            case BUILDUP_LOAD:
                LoadingTotals[p].buildup += w;
                break;
            case DEPOSITION_LOAD:
                LoadingTotals[p].deposition += w;
                break;
            case SWEEPING_LOAD:
                LoadingTotals[p].sweeping += w;
                break;
            case INFIL_LOAD:
                LoadingTotals[p].infil += w;
                break;
            case BMP_REMOVAL_LOAD:
                LoadingTotals[p].bmpRemoval += w;
                break;
            case RUNOFF_LOAD:
                LoadingTotals[p].runoff += w;
                break;
            case FINAL_LOAD:
                LoadingTotals[p].finalLoad += w;
                break;
        }
    }

    // massbal.c:461
    static void massbal_addInflowQual(int type, int p, double w)
    {
        if (((p < 0) || (p >= Nobjects[POLLUT])))
        {
            return;
        }
        switch (type) {
            case DRY_WEATHER_INFLOW:
                StepQualTotals[p].dwInflow += w;
                break;
            case WET_WEATHER_INFLOW:
                StepQualTotals[p].wwInflow += w;
                break;
            case GROUNDWATER_INFLOW:
                StepQualTotals[p].gwInflow += w;
                break;
            case EXTERNAL_INFLOW:
                StepQualTotals[p].exInflow += w;
                break;
            case RDII_INFLOW:
                StepQualTotals[p].iiInflow += w;
                break;
        }
    }

    // massbal.c:483
    static void massbal_addOutflowFlow(double q, int isFlooded)
    {
        if ((isFlooded != 0))
        {
            StepFlowTotals.flooding += q;
        }
        else
        {
            StepFlowTotals.outflow += q;
        }
    }

    // massbal.c:497
    static void massbal_addOutflowQual(int p, double w, int isFlooded)
    {
        if (((p < 0) || (p >= Nobjects[POLLUT])))
        {
            return;
        }
        if ((w >= 0.0))
        {
            if ((isFlooded != 0))
            {
                StepQualTotals[p].flooding += w;
            }
            else
            {
                StepQualTotals[p].outflow += w;
            }
        }
        else
        {
            StepQualTotals[p].exInflow -= w;
        }
    }

    // massbal.c:517
    static void massbal_addReactedMass(int p, double w)
    {
        if (((p < 0) || (p >= Nobjects[POLLUT])))
        {
            return;
        }
        StepQualTotals[p].reacted += w;
    }

    // massbal.c:531
    static void massbal_addSeepageLoss(int p, double w)
    {
        if (((p < 0) || (p >= Nobjects[POLLUT])))
        {
            return;
        }
        StepQualTotals[p].seepLoss += w;
    }

    // massbal.c:545
    static void massbal_addToFinalStorage(int p, double w)
    {
        if (((p < 0) || (p >= Nobjects[POLLUT])))
        {
            return;
        }
        StepQualTotals[p].finalStorage += w;
    }

    // massbal.c:559
    static void massbal_addNodeLosses(double evapLoss, double seepLoss)
    {
        StepFlowTotals.evapLoss += evapLoss;
        StepFlowTotals.seepLoss += seepLoss;
    }

    // massbal.c:573
    static void massbal_addLinkLosses(double evapLoss, double seepLoss)
    {
        StepFlowTotals.evapLoss += evapLoss;
        StepFlowTotals.seepLoss += seepLoss;
    }

    // massbal.c:587
    static void massbal_updateRoutingTotals(double tStep)
    {
        int j = 0;
        FlowTotals.dwInflow += (StepFlowTotals.dwInflow * tStep);
        FlowTotals.wwInflow += (StepFlowTotals.wwInflow * tStep);
        FlowTotals.gwInflow += (StepFlowTotals.gwInflow * tStep);
        FlowTotals.iiInflow += (StepFlowTotals.iiInflow * tStep);
        FlowTotals.exInflow += (StepFlowTotals.exInflow * tStep);
        FlowTotals.flooding += (StepFlowTotals.flooding * tStep);
        FlowTotals.outflow += (StepFlowTotals.outflow * tStep);
        FlowTotals.evapLoss += (StepFlowTotals.evapLoss * tStep);
        FlowTotals.seepLoss += (StepFlowTotals.seepLoss * tStep);
        for (j = 0; (j < Nobjects[POLLUT]); j++)
        {
            QualTotals[j].dwInflow += (StepQualTotals[j].dwInflow * tStep);
            QualTotals[j].wwInflow += (StepQualTotals[j].wwInflow * tStep);
            QualTotals[j].gwInflow += (StepQualTotals[j].gwInflow * tStep);
            QualTotals[j].iiInflow += (StepQualTotals[j].iiInflow * tStep);
            QualTotals[j].exInflow += (StepQualTotals[j].exInflow * tStep);
            QualTotals[j].flooding += (StepQualTotals[j].flooding * tStep);
            QualTotals[j].outflow += (StepQualTotals[j].outflow * tStep);
            QualTotals[j].reacted += (StepQualTotals[j].reacted * tStep);
            QualTotals[j].seepLoss += (StepQualTotals[j].seepLoss * tStep);
            QualTotals[j].finalStorage += StepQualTotals[j].finalStorage;
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            NodeInflow[j] += (Node[j].inflow * tStep);
            if (((Node[j].type == OUTFALL) || (((Node[j].degree == 0) && (Node[j].type != STORAGE)))))
            {
                NodeOutflow[j] += (Node[j].inflow * tStep);
            }
            else
            {
                NodeOutflow[j] += (Node[j].outflow * tStep);
                if ((Node[j].newVolume <= Node[j].fullVolume))
                {
                    NodeOutflow[j] += (Node[j].overflow * tStep);
                }
            }
        }
    }

    // massbal.c:638
    static double massbal_getStorage(int isFinalStorage)
    {
        int j = 0;
        double totalStorage = 0.0;
        double nodeStorage = 0.0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            nodeStorage = Node[j].newVolume;
            if ((isFinalStorage != 0))
            {
                NodeOutflow[j] += nodeStorage;
            }
            totalStorage += nodeStorage;
        }
        if (((isFinalStorage != 0) && (RouteModel == SF)))
        {
            return totalStorage;
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            totalStorage += Link[j].newVolume;
        }
        return totalStorage;
    }

    // massbal.c:670
    static void massbal_getSysFlows(double f, double[] sysFlows)
    {
        double f1 = (1.0 - f);
        sysFlows[SYS_DWFLOW] = ((((f1 * OldStepFlowTotals.dwInflow) + (f * StepFlowTotals.dwInflow))) * UCF(FLOW));
        sysFlows[SYS_GWFLOW] = ((((f1 * OldStepFlowTotals.gwInflow) + (f * StepFlowTotals.gwInflow))) * UCF(FLOW));
        sysFlows[SYS_IIFLOW] = ((((f1 * OldStepFlowTotals.iiInflow) + (f * StepFlowTotals.iiInflow))) * UCF(FLOW));
        sysFlows[SYS_EXFLOW] = ((((f1 * OldStepFlowTotals.exInflow) + (f * StepFlowTotals.exInflow))) * UCF(FLOW));
        sysFlows[SYS_FLOODING] = ((((f1 * OldStepFlowTotals.flooding) + (f * StepFlowTotals.flooding))) * UCF(FLOW));
        sysFlows[SYS_OUTFLOW] = ((((f1 * OldStepFlowTotals.outflow) + (f * StepFlowTotals.outflow))) * UCF(FLOW));
        sysFlows[SYS_STORAGE] = ((((f1 * OldStepFlowTotals.finalStorage) + (f * StepFlowTotals.finalStorage))) * UCF(VOLUME));
    }

    // massbal.c:696
    static double massbal_getRunoffError()
    {
        int j = 0;
        double totalInflow = 0.0;
        double totalOutflow = 0.0;
        RunoffTotals.finalStorage = 0.0;
        RunoffTotals.finalSnowCover = 0.0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            RunoffTotals.finalStorage += (subcatch_getStorage(j));
            RunoffTotals.finalSnowCover += (snow_getSnowCover(j));
        }
        RunoffTotals.snowRemoved = Snow.removed;
        totalInflow = (((RunoffTotals.rainfall + RunoffTotals.runon) + RunoffTotals.initStorage) + RunoffTotals.initSnowCover);
        totalOutflow = ((((((RunoffTotals.evap + RunoffTotals.infil) + RunoffTotals.runoff) + RunoffTotals.drains) + RunoffTotals.snowRemoved) + RunoffTotals.finalStorage) + RunoffTotals.finalSnowCover);
        RunoffTotals.pctError = 0.0;
        if ((Math.abs((totalInflow - totalOutflow)) < 1.0))
        {
            RunoffTotals.pctError = 1e-06;
        }
        else if ((totalInflow > 0.0))
        {
            RunoffTotals.pctError = (100.0 * ((1.0 - (totalOutflow / totalInflow))));
        }
        else if ((totalOutflow > 0.0))
        {
            RunoffTotals.pctError = (100.0 * (((totalInflow / totalOutflow) - 1.0)));
        }
        RunoffError = RunoffTotals.pctError;
        return RunoffTotals.pctError;
    }

    // massbal.c:750
    static double massbal_getLoadingError()
    {
        int j = 0;
        double loadIn = 0.0;
        double loadOut = 0.0;
        double maxError = 0.0;
        for (j = 0; (j < Nobjects[POLLUT]); j++)
        {
            LoadingTotals[j].finalLoad += (massbal_getBuildup(j));
            loadIn = ((LoadingTotals[j].initLoad + LoadingTotals[j].buildup) + LoadingTotals[j].deposition);
            loadOut = ((((LoadingTotals[j].sweeping + LoadingTotals[j].infil) + LoadingTotals[j].bmpRemoval) + LoadingTotals[j].runoff) + LoadingTotals[j].finalLoad);
            LoadingTotals[j].pctError = 0.0;
            if ((Math.abs((loadIn - loadOut)) < 0.001))
            {
                LoadingTotals[j].pctError = 1e-06;
            }
            else if ((loadIn > 0.0))
            {
                LoadingTotals[j].pctError = (100.0 * ((1.0 - (loadOut / loadIn))));
            }
            else if ((loadOut > 0.0))
            {
                LoadingTotals[j].pctError = (100.0 * (((loadIn / loadOut) - 1.0)));
            }
            maxError = (((((maxError) >= (LoadingTotals[j].pctError))) ? (maxError) : (LoadingTotals[j].pctError)));
            if ((Pollut[j].units == COUNT))
            {
                LoadingTotals[j].initLoad = ((((LoadingTotals[j].initLoad) > 0.0) ? Glibm.log10((LoadingTotals[j].initLoad)) : (LoadingTotals[j].initLoad)));
                LoadingTotals[j].buildup = ((((LoadingTotals[j].buildup) > 0.0) ? Glibm.log10((LoadingTotals[j].buildup)) : (LoadingTotals[j].buildup)));
                LoadingTotals[j].deposition = ((((LoadingTotals[j].deposition) > 0.0) ? Glibm.log10((LoadingTotals[j].deposition)) : (LoadingTotals[j].deposition)));
                LoadingTotals[j].sweeping = ((((LoadingTotals[j].sweeping) > 0.0) ? Glibm.log10((LoadingTotals[j].sweeping)) : (LoadingTotals[j].sweeping)));
                LoadingTotals[j].infil = ((((LoadingTotals[j].infil) > 0.0) ? Glibm.log10((LoadingTotals[j].infil)) : (LoadingTotals[j].infil)));
                LoadingTotals[j].bmpRemoval = ((((LoadingTotals[j].bmpRemoval) > 0.0) ? Glibm.log10((LoadingTotals[j].bmpRemoval)) : (LoadingTotals[j].bmpRemoval)));
                LoadingTotals[j].runoff = ((((LoadingTotals[j].runoff) > 0.0) ? Glibm.log10((LoadingTotals[j].runoff)) : (LoadingTotals[j].runoff)));
                LoadingTotals[j].finalLoad = ((((LoadingTotals[j].finalLoad) > 0.0) ? Glibm.log10((LoadingTotals[j].finalLoad)) : (LoadingTotals[j].finalLoad)));
            }
        }
        return maxError;
    }

    // massbal.c:813
    static double massbal_getGwaterError()
    {
        int j = 0;
        double totalInflow = 0.0;
        double totalOutflow = 0.0;
        GwaterTotals.finalStorage = 0.0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            GwaterTotals.finalStorage += (gwater_getVolume(j) * Subcatch[j].area);
        }
        totalInflow = (GwaterTotals.infil + GwaterTotals.initStorage);
        totalOutflow = ((((GwaterTotals.upperEvap + GwaterTotals.lowerEvap) + GwaterTotals.lowerPerc) + GwaterTotals.gwater) + GwaterTotals.finalStorage);
        GwaterTotals.pctError = 0.0;
        if ((Math.abs((totalInflow - totalOutflow)) < 1.0))
        {
            GwaterTotals.pctError = 1e-06;
        }
        else if ((totalInflow > 0.0))
        {
            GwaterTotals.pctError = (100.0 * ((1.0 - (totalOutflow / totalInflow))));
        }
        else if ((totalOutflow > 0.0))
        {
            GwaterTotals.pctError = (100.0 * (((totalInflow / totalOutflow) - 1.0)));
        }
        GwaterError = GwaterTotals.pctError;
        return GwaterTotals.pctError;
    }

    // massbal.c:858
    static double massbal_getFlowError()
    {
        double totalInflow = 0.0;
        double totalOutflow = 0.0;
        FlowTotals.finalStorage = massbal_getStorage(1);
        totalInflow = ((FlowTotals.initStorage + FlowTotals.wwInflow) + FlowTotals.iiInflow);
        totalOutflow = ((((FlowTotals.finalStorage + FlowTotals.flooding) + FlowTotals.evapLoss) + FlowTotals.seepLoss) + FlowTotals.reacted);
        if ((FlowTotals.dwInflow >= 0.0))
        {
            totalInflow += FlowTotals.dwInflow;
        }
        else
        {
            totalOutflow -= FlowTotals.dwInflow;
        }
        if ((FlowTotals.gwInflow >= 0.0))
        {
            totalInflow += FlowTotals.gwInflow;
        }
        else
        {
            totalOutflow -= FlowTotals.gwInflow;
        }
        if ((FlowTotals.exInflow >= 0.0))
        {
            totalInflow += FlowTotals.exInflow;
        }
        else
        {
            totalOutflow -= FlowTotals.exInflow;
        }
        if ((FlowTotals.outflow >= 0.0))
        {
            totalOutflow += FlowTotals.outflow;
        }
        else
        {
            totalInflow -= FlowTotals.outflow;
        }
        FlowTotals.pctError = 0.0;
        if ((Math.abs((totalInflow - totalOutflow)) < 1.0))
        {
            FlowTotals.pctError = 1e-06;
        }
        else if ((Math.abs(totalInflow) > 0.0))
        {
            FlowTotals.pctError = (100.0 * ((1.0 - (totalOutflow / totalInflow))));
        }
        else if ((Math.abs(totalOutflow) > 0.0))
        {
            FlowTotals.pctError = (100.0 * (((totalInflow / totalOutflow) - 1.0)));
        }
        FlowError = FlowTotals.pctError;
        return FlowTotals.pctError;
    }

    // massbal.c:906
    static double massbal_getQualError()
    {
        int p = 0;
        double maxQualError = 0.0;
        double totalInflow = 0.0;
        double totalOutflow = 0.0;
        double cf = 0.0;
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            QualTotals[p].finalStorage += (massbal_getStoredMass(p));
            totalInflow = (((((QualTotals[p].dwInflow + QualTotals[p].wwInflow) + QualTotals[p].gwInflow) + QualTotals[p].iiInflow) + QualTotals[p].exInflow) + QualTotals[p].initStorage);
            totalOutflow = ((((QualTotals[p].flooding + QualTotals[p].outflow) + QualTotals[p].reacted) + QualTotals[p].seepLoss) + QualTotals[p].finalStorage);
            QualTotals[p].pctError = 0.0;
            if ((Math.abs((totalInflow - totalOutflow)) < 0.001))
            {
                QualTotals[p].pctError = 1e-06;
            }
            else if ((totalInflow > 0.0))
            {
                QualTotals[p].pctError = (100.0 * ((1.0 - (totalOutflow / totalInflow))));
            }
            else if ((totalOutflow > 0.0))
            {
                QualTotals[p].pctError = (100.0 * (((totalInflow / totalOutflow) - 1.0)));
            }
            if ((Math.abs(QualTotals[p].pctError) > Math.abs(maxQualError)))
            {
                maxQualError = QualTotals[p].pctError;
            }
            cf = 28.317;
            if ((Pollut[p].units == COUNT))
            {
                QualTotals[p].dwInflow = (((((cf * QualTotals[p].dwInflow)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].dwInflow))) : ((cf * QualTotals[p].dwInflow))));
                QualTotals[p].wwInflow = (((((cf * QualTotals[p].wwInflow)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].wwInflow))) : ((cf * QualTotals[p].wwInflow))));
                QualTotals[p].gwInflow = (((((cf * QualTotals[p].gwInflow)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].gwInflow))) : ((cf * QualTotals[p].gwInflow))));
                QualTotals[p].iiInflow = (((((cf * QualTotals[p].iiInflow)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].iiInflow))) : ((cf * QualTotals[p].iiInflow))));
                QualTotals[p].exInflow = (((((cf * QualTotals[p].exInflow)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].exInflow))) : ((cf * QualTotals[p].exInflow))));
                QualTotals[p].flooding = (((((cf * QualTotals[p].flooding)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].flooding))) : ((cf * QualTotals[p].flooding))));
                QualTotals[p].outflow = (((((cf * QualTotals[p].outflow)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].outflow))) : ((cf * QualTotals[p].outflow))));
                QualTotals[p].reacted = (((((cf * QualTotals[p].reacted)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].reacted))) : ((cf * QualTotals[p].reacted))));
                QualTotals[p].seepLoss = (((((cf * QualTotals[p].seepLoss)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].seepLoss))) : ((cf * QualTotals[p].seepLoss))));
                QualTotals[p].initStorage = (((((cf * QualTotals[p].initStorage)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].initStorage))) : ((cf * QualTotals[p].initStorage))));
                QualTotals[p].finalStorage = (((((cf * QualTotals[p].finalStorage)) > 0.0) ? Glibm.log10(((cf * QualTotals[p].finalStorage))) : ((cf * QualTotals[p].finalStorage))));
            }
            else
            {
                cf = (cf * UCF(MASS));
                if ((Pollut[p].units == UG))
                {
                    cf /= 1000.0;
                }
                QualTotals[p].dwInflow *= cf;
                QualTotals[p].wwInflow *= cf;
                QualTotals[p].gwInflow *= cf;
                QualTotals[p].iiInflow *= cf;
                QualTotals[p].exInflow *= cf;
                QualTotals[p].flooding *= cf;
                QualTotals[p].outflow *= cf;
                QualTotals[p].reacted *= cf;
                QualTotals[p].seepLoss *= cf;
                QualTotals[p].initStorage *= cf;
                QualTotals[p].finalStorage *= cf;
            }
        }
        QualError = maxQualError;
        return maxQualError;
    }

    // massbal.c:995
    static double massbal_getStepFlowError()
    {
        double totalInflow = 0.0;
        double totalOutflow = 0.0;
        totalInflow = ((((StepFlowTotals.dwInflow + StepFlowTotals.wwInflow) + StepFlowTotals.gwInflow) + StepFlowTotals.iiInflow) + StepFlowTotals.exInflow);
        totalOutflow = ((((StepFlowTotals.flooding + StepFlowTotals.outflow) + StepFlowTotals.evapLoss) + StepFlowTotals.seepLoss) + StepFlowTotals.reacted);
        if ((Math.abs(totalInflow) > 0.0))
        {
            return (1.0 - (totalOutflow / totalInflow));
        }
        else if ((Math.abs(totalOutflow) > 0.0))
        {
            return ((totalInflow / totalOutflow) - 1.0);
        }
        else
        {
            return 0.0;
        }
    }

    // massbal.c:1025
    static double massbal_getStoredMass(int p)
    {
        int j = 0;
        double storedMass = 0.0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            storedMass += (Node[j].newVolume * Node[j].newQual[p]);
        }
        if ((RouteModel != SF))
        {
            for (j = 0; (j < Nobjects[LINK]); j++)
            {
                storedMass += (Link[j].newVolume * Link[j].newQual[p]);
            }
        }
        return storedMass;
    }

}
