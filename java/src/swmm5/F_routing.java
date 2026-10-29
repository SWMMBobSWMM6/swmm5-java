// Generated from EPA SWMM 5.2.4 (routing.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_routing {
    static int[] SortedLinks = null;
    static int NextEvent = 0;
    static int BetweenEvents = 0;
    static double NewRuleTime = 0.0;

    // routing.c:93
    static int routing_open()
    {
        if (!(treatmnt_open() != 0))
        {
            return ErrorCode;
        }
        SortedLinks = (null);
        if ((Nobjects[LINK] > 0))
        {
            SortedLinks = new int[(int) (Nobjects[LINK])];
            if (!(SortedLinks != null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return ErrorCode;
            }
            toposort_sortLinks(SortedLinks);
            if ((ErrorCode != 0))
            {
                return ErrorCode;
            }
        }
        iface_openRoutingFiles();
        flowrout_init(RouteModel);
        if ((Fhotstart1.mode == NO_FILE))
        {
            qualrout_init();
        }
        if ((NumEvents > 0))
        {
            sortEvents();
        }
        NextEvent = 0;
        BetweenEvents = (((NumEvents > 0)) ? 1 : 0);
        NewRuleTime = 0.0;
        return ErrorCode;
    }

    // routing.c:134
    static void routing_close(int routingModel)
    {
        iface_closeRoutingFiles();
        flowrout_close(routingModel);
        treatmnt_close();
        {
            if ((SortedLinks != null))
            {
                R.free(SortedLinks);
                SortedLinks = (null);
            }
        }
        ;
    }

    // routing.c:152
    static double routing_getRoutingStep(int routingModel, double fixedStep)
    {
        double date1 = 0.0;
        double date2 = 0.0;
        double nextTime = 0.0;
        double routingStep = 0.0;
        double nextRuleTime = 0.0;
        double nextRoutingTime = 0.0;
        if ((Nobjects[LINK] == 0))
        {
            return fixedStep;
        }
        if (((NumEvents > 0) && (BetweenEvents != 0)))
        {
            nextTime = (((((NewRunoffTime) <= (ReportTime))) ? (NewRunoffTime) : (ReportTime)));
            date1 = getDateTime(NewRoutingTime);
            date2 = getDateTime(nextTime);
            if (((date2 > date1) && (date2 < Event[NextEvent].start)))
            {
                routingStep = (((nextTime - NewRoutingTime)) / 1000.0);
            }
            else
            {
                date1 = getDateTime((NewRoutingTime + (1000.0 * fixedStep)));
                if ((date1 < Event[NextEvent].start))
                {
                    return fixedStep;
                }
            }
        }
        if ((routingStep == 0.0))
        {
            routingStep = flowrout_getRoutingStep(routingModel, fixedStep);
        }
        if ((RuleStep > 0))
        {
            nextRuleTime = (NewRuleTime + (1000.0 * RuleStep));
            nextRoutingTime = (NewRoutingTime + (1000.0 * routingStep));
            if ((nextRoutingTime >= nextRuleTime))
            {
                routingStep = (((nextRuleTime - NewRoutingTime)) / 1000.0);
            }
        }
        return routingStep;
    }

    // routing.c:203
    static void routing_execute(int routingModel, double routingStep)
    {
        int trialsCount = 1;
        int actionCount = 0;
        int inSteadyState = 1;
        double currentDate = 0.0;
        double stepFlowError = 0.0;
        if ((ErrorCode != 0))
        {
            return;
        }
        massbal_updateRoutingTotals((routingStep / 2.0));
        currentDate = getDateTime(NewRoutingTime);
        actionCount = evaluateControlRules(currentDate, routingStep);
        stepFlowError = massbal_getStepFlowError();
        massbal_initTimeStepTotals();
        initSystemInflows();
        BetweenEvents = isBetweenEvents(currentDate);
        if ((BetweenEvents == 0))
        {
            addSystemInflows(currentDate, routingStep);
            inlet_findCapturedFlows(routingStep);
            inSteadyState = isInSteadyState(actionCount, stepFlowError);
            if ((inSteadyState == 0))
            {
                trialsCount = routeFlow(routingModel, routingStep);
            }
            if (((Nobjects[POLLUT] > 0) && (!(IgnoreQuality != 0))))
            {
                inlet_adjustQualInflows();
                qualrout_execute(routingStep);
            }
            removeSystemOutflows(routingStep);
            inlet_adjustQualOutflows();
            if ((Nobjects[LINK] > 0))
            {
                stats_updateFlowStats(routingStep, getDateTime(NewRoutingTime));
                stats_updateTimeStepStats(routingStep, trialsCount, inSteadyState);
            }
        }
        massbal_updateRoutingTotals((routingStep / 2.0));
    }

    // routing.c:269
    static int evaluateControlRules(double currentDate, double routingStep)
    {
        int j = 0;
        int actionCount = 0;
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            link_setTargetSetting(j);
        }
        if (((RuleStep == 0) || (Math.abs((NewRoutingTime - NewRuleTime)) < 1.0)))
        {
            controls_evaluate(currentDate, (currentDate - StartDateTime), (routingStep / 86400));
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if ((Link[j].targetSetting != Link[j].setting))
            {
                if (((Link[j].targetSetting * Link[j].setting) == 0.0))
                {
                    Link[j].timeLastSet = currentDate;
                }
                link_setSetting(j, routingStep);
                actionCount++;
            }
        }
        OldRoutingTime = NewRoutingTime;
        NewRoutingTime = (NewRoutingTime + (1000.0 * routingStep));
        if ((Math.abs((NewRoutingTime - ((NewRuleTime + (1000.0 * RuleStep))))) < 1))
        {
            NewRuleTime += (1000.0 * RuleStep);
        }
        return actionCount;
    }

    // routing.c:312
    static void initSystemInflows()
    {
        int j = 0;
        if ((Nobjects[POLLUT] > 0))
        {
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                node_setOldQualState(j);
            }
            for (j = 0; (j < Nobjects[LINK]); j++)
            {
                link_setOldQualState(j);
            }
        }
        infil_setInfilFactor((-1));
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            Node[j].oldLatFlow = Node[j].newLatFlow;
            Node[j].newLatFlow = 0.0;
        }
    }

    // routing.c:337
    static int isBetweenEvents(double currentDate)
    {
        if ((NumEvents == 0))
        {
            return 0;
        }
        if ((currentDate > Event[NextEvent].end))
        {
            NextEvent++;
            return 1;
        }
        else if ((currentDate >= Event[NextEvent].start))
        {
            return 0;
        }
        return 1;
    }

    // routing.c:359
    static void addSystemInflows(double currentDate, double routingStep)
    {
        int j = 0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            Node[j].losses = node_getLosses(j, routingStep);
        }
        addExternalInflows(currentDate);
        addDryWeatherInflows(currentDate);
        addWetWeatherInflows(OldRoutingTime);
        addGroundwaterInflows(OldRoutingTime);
        addLidDrainInflows(OldRoutingTime);
        addRdiiInflows(currentDate);
        addIfaceInflows(currentDate);
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            Node[j].qualInflow = (((((0.0) >= (Node[j].newLatFlow))) ? (0.0) : (Node[j].newLatFlow)));
        }
    }

    // routing.c:383
    static int isInSteadyState(int actionCount, double stepFlowError)
    {
        if ((SkipSteadyState != 0))
        {
            if (((((OldRoutingTime == 0.0) || (actionCount > 0)) || (Math.abs(stepFlowError) > SysFlowTol.v)) || (inflowHasChanged() != 0)))
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
        return 0;
    }

    // routing.c:399
    static int routeFlow(int routingModel, double routingStep)
    {
        int j = 0;
        int stepCount = 1;
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            link_setOldHydState(j);
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            node_setOldHydState(j);
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            node_initFlows(j, routingStep);
        }
        if ((Nobjects[LINK] > 0))
        {
            stepCount = flowrout_execute(SortedLinks, routingModel, routingStep);
        }
        return stepCount;
    }

    // routing.c:425
    static void removeSystemOutflows(double routingStep)
    {
        removeStorageLosses(routingStep);
        removeConduitLosses();
        removeOutflows(routingStep);
    }

    // routing.c:435
    static void addExternalInflows(double currentDate)
    {
        int j = 0;
        int p = 0;
        double q = 0.0;
        double w = 0.0;
        TExtInflow inflow = null;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            q = Node[j].apiExtInflow;
            inflow = Node[j].extInflow;
            while ((inflow != null))
            {
                if ((inflow.type == FLOW_INFLOW))
                {
                    q += (inflow_getExtInflow(inflow, currentDate));
                    break;
                }
                else
                {
                    inflow = inflow.next;
                }
            }
            if ((Math.abs(q) < 1e-05))
            {
                q = 0.0;
            }
            Node[j].newLatFlow += q;
            if ((q >= 0.0))
            {
                massbal_addInflowFlow(EXTERNAL_INFLOW, q);
            }
            else
            {
                massbal_addOutflowFlow((-q), 0);
                continue;
            }
            if (((Node[j].type == OUTFALL) && (Node[j].oldNetInflow < 0.0)))
            {
                q = (q - Node[j].oldNetInflow);
            }
            inflow = Node[j].extInflow;
            while ((inflow != null))
            {
                if ((inflow.type != FLOW_INFLOW))
                {
                    p = inflow.param;
                    w = inflow_getExtInflow(inflow, currentDate);
                    if ((inflow.type == CONCEN_INFLOW))
                    {
                        w *= q;
                    }
                    Node[j].newQual[p] += w;
                    massbal_addInflowQual(EXTERNAL_INFLOW, p, w);
                }
                inflow = inflow.next;
            }
        }
    }

    // routing.c:498
    static void addDryWeatherInflows(double currentDate)
    {
        int j = 0;
        int p = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        double q = 0.0;
        double w = 0.0;
        TDwfInflow inflow = null;
        month = (datetime_monthOfYear(currentDate) - 1);
        day = (datetime_dayOfWeek(currentDate) - 1);
        hour = datetime_hourOfDay(currentDate);
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            inflow = Node[j].dwfInflow;
            if (!(inflow != null))
            {
                continue;
            }
            q = 0.0;
            while ((inflow != null))
            {
                if ((inflow.param < 0))
                {
                    q = inflow_getDwfInflow(inflow, month, day, hour);
                    break;
                }
                inflow = inflow.next;
            }
            if ((Math.abs(q) < 1e-05))
            {
                q = 0.0;
            }
            Node[j].newLatFlow += q;
            massbal_addInflowFlow(DRY_WEATHER_INFLOW, q);
            if ((q <= 0.0))
            {
                continue;
            }
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                if ((Pollut[p].dwfConcen > 0.0))
                {
                    w = (q * Pollut[p].dwfConcen);
                    Node[j].newQual[p] += w;
                    massbal_addInflowQual(DRY_WEATHER_INFLOW, p, w);
                }
            }
            inflow = Node[j].dwfInflow;
            while ((inflow != null))
            {
                if ((inflow.param >= 0))
                {
                    p = inflow.param;
                    w = (q * inflow_getDwfInflow(inflow, month, day, hour));
                    Node[j].newQual[p] += w;
                    massbal_addInflowQual(DRY_WEATHER_INFLOW, p, w);
                    if ((Pollut[p].dwfConcen > 0.0))
                    {
                        w = (q * Pollut[p].dwfConcen);
                        Node[j].newQual[p] -= w;
                        massbal_addInflowQual(DRY_WEATHER_INFLOW, p, (-w));
                    }
                }
                inflow = inflow.next;
            }
        }
    }

    // routing.c:579
    static void addWetWeatherInflows(double routingTime)
    {
        int i = 0;
        int j = 0;
        int p = 0;
        double q = 0.0;
        double w = 0.0;
        double f = 0.0;
        if ((Nobjects[SUBCATCH] == 0))
        {
            return;
        }
        f = (((routingTime - OldRunoffTime)) / ((NewRunoffTime - OldRunoffTime)));
        if ((f < 0.0))
        {
            f = 0.0;
        }
        if ((f > 1.0))
        {
            f = 1.0;
        }
        for (i = 0; (i < Nobjects[SUBCATCH]); i++)
        {
            j = Subcatch[i].outNode;
            if ((j >= 0))
            {
                q = subcatch_getWtdOutflow(i, f);
                Node[j].newLatFlow += q;
                massbal_addInflowFlow(WET_WEATHER_INFLOW, q);
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    w = surfqual_getWtdWashoff(i, p, f);
                    Node[j].newQual[p] += w;
                    massbal_addInflowQual(WET_WEATHER_INFLOW, p, w);
                }
            }
        }
    }

    // routing.c:621
    static void addGroundwaterInflows(double routingTime)
    {
        int i = 0;
        int j = 0;
        int p = 0;
        double q = 0.0;
        double w = 0.0;
        double f = 0.0;
        TGroundwater gw = null;
        if ((Nobjects[SUBCATCH] == 0))
        {
            return;
        }
        f = (((routingTime - OldRunoffTime)) / ((NewRunoffTime - OldRunoffTime)));
        if ((f < 0.0))
        {
            f = 0.0;
        }
        if ((f > 1.0))
        {
            f = 1.0;
        }
        for (i = 0; (i < Nobjects[SUBCATCH]); i++)
        {
            gw = Subcatch[i].groundwater;
            if ((gw != null))
            {
                j = gw.node;
                if ((j >= 0))
                {
                    q = ((((((1.0 - f)) * (gw.oldFlow)) + (f * (gw.newFlow)))) * Subcatch[i].area);
                    if ((Math.abs(q) < 1e-05))
                    {
                        continue;
                    }
                    Node[j].newLatFlow += q;
                    massbal_addInflowFlow(GROUNDWATER_INFLOW, q);
                    if ((q > 0.0))
                    {
                        for (p = 0; (p < Nobjects[POLLUT]); p++)
                        {
                            w = (q * Pollut[p].gwConcen);
                            Node[j].newQual[p] += w;
                            massbal_addInflowQual(GROUNDWATER_INFLOW, p, w);
                        }
                    }
                }
            }
        }
    }

    // routing.c:674
    static void addLidDrainInflows(double routingTime)
    {
        int j = 0;
        double f = 0.0;
        if ((Nobjects[SUBCATCH] == 0))
        {
            return;
        }
        f = (((routingTime - OldRunoffTime)) / ((NewRunoffTime - OldRunoffTime)));
        if ((f < 0.0))
        {
            f = 0.0;
        }
        if ((f > 1.0))
        {
            f = 1.0;
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if (((Subcatch[j].area > 0.0) && (Subcatch[j].lidArea > 0.0)))
            {
                lid_addDrainInflow(j, f);
            }
        }
    }

    // routing.c:698
    static void addRdiiInflows(double currentDate)
    {
        int i = 0;
        IBox j = new IBox(0);
        int p = 0;
        DBox q = new DBox(0.0);
        double w = 0.0;
        int numRdiiNodes = 0;
        numRdiiNodes = rdii_getNumRdiiFlows(currentDate);
        for (i = 0; (i < numRdiiNodes); i++)
        {
            rdii_getRdiiFlow(i, j, q);
            if ((j.v < 0))
            {
                continue;
            }
            if ((Math.abs(q.v) < 1e-05))
            {
                continue;
            }
            Node[j.v].newLatFlow += q.v;
            massbal_addInflowFlow(RDII_INFLOW, q.v);
            if ((q.v > 0.0))
            {
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    w = (q.v * Pollut[p].rdiiConcen);
                    Node[j.v].newQual[p] += w;
                    massbal_addInflowQual(RDII_INFLOW, p, w);
                }
            }
        }
    }

    // routing.c:736
    static void addIfaceInflows(double currentDate)
    {
        int i = 0;
        int j = 0;
        int p = 0;
        double q = 0.0;
        double w = 0.0;
        int numIfaceNodes = 0;
        if ((Finflows.mode != USE_FILE))
        {
            return;
        }
        numIfaceNodes = iface_getNumIfaceNodes(currentDate);
        for (i = 0; (i < numIfaceNodes); i++)
        {
            j = iface_getIfaceNode(i);
            if ((j < 0))
            {
                continue;
            }
            q = iface_getIfaceFlow(i);
            if ((Math.abs(q) < 1e-05))
            {
                continue;
            }
            Node[j].newLatFlow += q;
            massbal_addInflowFlow(EXTERNAL_INFLOW, q);
            if ((q > 0.0))
            {
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    w = (q * iface_getIfaceQual(i, p));
                    Node[j].newQual[p] += w;
                    massbal_addInflowQual(EXTERNAL_INFLOW, p, w);
                }
            }
        }
    }

    // routing.c:776
    static int inflowHasChanged()
    {
        int j = 0;
        double diff = 0.0;
        double qOld = 0.0;
        double qNew = 0.0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            qOld = Node[j].oldLatFlow;
            qNew = Node[j].newLatFlow;
            if ((Math.abs(qOld) > 1e-06))
            {
                diff = (((qNew / qOld)) - 1.0);
            }
            else if ((Math.abs(qNew) > 1e-06))
            {
                diff = 1.0;
            }
            else
            {
                diff = 0.0;
            }
            if ((Math.abs(diff) > LatFlowTol.v))
            {
                return 1;
            }
            if (((Node[j].type == OUTFALL) || (Node[j].degree == 0)))
            {
                qOld = Node[j].oldFlowInflow;
                qNew = Node[j].inflow;
                if ((Math.abs(qOld) > 1e-06))
                {
                    diff = (((qNew / qOld)) - 1.0);
                }
                else if ((Math.abs(qNew) > 1e-06))
                {
                    diff = 1.0;
                }
                else
                {
                    diff = 0.0;
                }
                if ((Math.abs(diff) > LatFlowTol.v))
                {
                    return 1;
                }
            }
        }
        return 0;
    }

    // routing.c:812
    static void removeStorageLosses(double tStep)
    {
        int i = 0;
        double evapLoss = 0.0;
        double exfilLoss = 0.0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].type == STORAGE))
            {
                evapLoss += Storage[Node[i].subIndex].evapLoss;
                exfilLoss += Storage[Node[i].subIndex].exfilLoss;
            }
        }
        massbal_addNodeLosses((evapLoss / tStep), (exfilLoss / tStep));
    }

    // routing.c:841
    static void removeConduitLosses()
    {
        int i = 0;
        int k = 0;
        double barrels = 0.0;
        double evapLoss = 0.0;
        double seepLoss = 0.0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if ((Link[i].type == CONDUIT))
            {
                k = Link[i].subIndex;
                barrels = Conduit[k].barrels;
                evapLoss += (Conduit[k].evapLossRate * barrels);
                seepLoss += (Conduit[k].seepLossRate * barrels);
            }
        }
        massbal_addLinkLosses(evapLoss, seepLoss);
    }

    // routing.c:872
    static void removeOutflows(double tStep)
    {
        int i = 0;
        int p = 0;
        int k = 0;
        IBox isFlooded = new IBox(0);
        double q = 0.0;
        double w = 0.0;
        double v = 0.0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if (((Node[i].type == OUTFALL) && (Node[i].inflow > 0.0)))
            {
                k = Node[i].subIndex;
                if ((Outfall[k].routeTo >= 0))
                {
                    v = (Node[i].inflow * tStep);
                    Outfall[k].vRouted += v;
                    for (p = 0; (p < Nobjects[POLLUT]); p++)
                    {
                        Outfall[k].wRouted[p] += (Node[i].newQual[p] * v);
                    }
                }
            }
            q = node_getSystemOutflow(i, isFlooded);
            if ((q > 0.0))
            {
                massbal_addOutflowFlow(q, isFlooded.v);
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    w = (q * Node[i].newQual[p]);
                    massbal_addOutflowQual(p, w, isFlooded.v);
                }
            }
            else
            {
                massbal_addInflowFlow(EXTERNAL_INFLOW, (-q));
            }
            q = Node[i].newLatFlow;
            if ((q < 0.0))
            {
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    w = ((-q) * Node[i].newQual[p]);
                    massbal_addOutflowQual(p, w, 0);
                }
            }
        }
    }

    // routing.c:929
    static void sortEvents()
    {
        int i = 0;
        int j = 0;
        int gap = 0;
        TEvent temp = new TEvent();
        for (gap = (NumEvents / 2); (gap >= 1); gap /= 2)
        {
            for (i = gap; (i < NumEvents); i += gap)
            {
                temp = Event[i].copy();
                j = (i - gap);
                while (((j >= 0) && (Event[j].start > temp.start)))
                {
                    Event[(j + gap)] = Event[j].copy();
                    j -= gap;
                }
                if ((j != (i - gap)))
                {
                    Event[(j + gap)] = temp.copy();
                }
            }
        }
        for (i = 0; (i < (NumEvents - 1)); i++)
        {
            if ((Event[i].end > (Event[(i + 1)]).start))
            {
                Event[i].end = (Event[(i + 1)]).start;
            }
        }
    }

}
