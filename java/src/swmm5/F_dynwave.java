// Generated from EPA SWMM 5.2.4 (dynwave.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_dynwave {
    static double MINTIMESTEP = 0.001;
    static double dynwave_OMEGA = 0.5;
    static double DEFAULT_SURFAREA = 12.566;
    static double DEFAULT_HEADTOL = 0.005;
    static double EXTRAN_CROWN_CUTOFF = 0.96;
    static double SLOT_CROWN_CUTOFF = 0.985257;
    static int DEFAULT_MAXTRIALS = 8;
    static double VariableStep = 0.0;
    static TXnode[] Xnode = null;
    static double Omega = 0.0;
    static int Steps = 0;

    // dynwave.c:117
    static void dynwave_init()
    {
        int i = 0;
        int j = 0;
        double z = 0.0;
        VariableStep = 0.0;
        Xnode = R.fill(new TXnode[(int) (Nobjects[NODE])], TXnode::new);
        if ((Xnode == null))
        {
            report_writeErrorMsg(ERR_MEMORY, L45);
            return;
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            Xnode[i].newSurfArea = 0.0;
            Xnode[i].oldSurfArea = 0.0;
            Node[i].crownElev = Node[i].invertElev;
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            j = Link[i].node1;
            z = ((Node[j].invertElev + Link[i].offset1) + Link[i].xsect.yFull);
            Node[j].crownElev = (((((Node[j].crownElev) >= (z))) ? (Node[j].crownElev) : (z)));
            j = Link[i].node2;
            z = ((Node[j].invertElev + Link[i].offset2) + Link[i].xsect.yFull);
            Node[j].crownElev = (((((Node[j].crownElev) >= (z))) ? (Node[j].crownElev) : (z)));
            Link[i].flowClass = DRY;
            Link[i].dqdh = 0.0;
        }
        if ((SurchargeMethod == SLOT))
        {
            CrownCutoff = SLOT_CROWN_CUTOFF;
        }
        else
        {
            CrownCutoff = EXTRAN_CROWN_CUTOFF;
        }
    }

    // dynwave.c:165
    static void dynwave_close()
    {
        {
            if ((Xnode != null))
            {
                R.free(Xnode);
                Xnode = (null);
            }
        }
        ;
    }

    // dynwave.c:177
    static void dynwave_validate()
    {
        if ((MinRouteStep.v > RouteStep))
        {
            MinRouteStep.v = RouteStep;
        }
        if ((MinRouteStep.v < MINTIMESTEP))
        {
            MinRouteStep.v = MINTIMESTEP;
        }
        if ((MinSurfArea.v == 0.0))
        {
            MinSurfArea.v = DEFAULT_SURFAREA;
        }
        else
        {
            MinSurfArea.v /= (UCF(LENGTH) * UCF(LENGTH));
        }
        if ((HeadTol.v == 0.0))
        {
            HeadTol.v = DEFAULT_HEADTOL;
        }
        else
        {
            HeadTol.v /= (UCF(LENGTH));
        }
        if ((MaxTrials == 0))
        {
            MaxTrials = DEFAULT_MAXTRIALS;
        }
    }

    // dynwave.c:195
    static double dynwave_getRoutingStep(double fixedStep)
    {
        if ((CourantFactor.v == 0.0))
        {
            return fixedStep;
        }
        if ((fixedStep < MINTIMESTEP))
        {
            return fixedStep;
        }
        if ((VariableStep == 0.0))
        {
            VariableStep = MinRouteStep.v;
        }
        else
        {
            VariableStep = getVariableStep(fixedStep);
        }
        VariableStep = (Math.floor((1000.0 * VariableStep)) / 1000.0);
        return VariableStep;
    }

    // dynwave.c:224
    static int dynwave_execute(double tStep)
    {
        int converged = 0;
        if ((ErrorCode != 0))
        {
            return 0;
        }
        Steps = 0;
        converged = 0;
        Omega = dynwave_OMEGA;
        initRoutingStep();
        while ((Steps < MaxTrials))
        {
            initNodeStates();
            findLinkFlows(tStep);
            converged = findNodeDepths(tStep);
            Steps++;
            if ((Steps > 1))
            {
                if ((converged != 0))
                {
                    break;
                }
                findBypassedLinks();
            }
        }
        if (!(converged != 0))
        {
            updateConvergenceStats();
        }
        findLimitedLinks();
        return Steps;
    }

    // dynwave.c:266
    static void updateConvergenceStats()
    {
        int i = 0;
        NonConvergeCount++;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            stats_updateConvergenceStats(i, Xnode[i].converged);
        }
    }

    // dynwave.c:276
    static void initRoutingStep()
    {
        int i = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            Xnode[i].converged = 0;
            Xnode[i].dYdT = 0.0;
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            Link[i].bypassed = 0;
            Link[i].surfArea1 = 0.0;
            Link[i].surfArea2 = 0.0;
        }
        for (i = 0; (i < Nlinks[CONDUIT]); i++)
        {
            Conduit[i].a2 = Conduit[i].a1;
        }
    }

    // dynwave.c:297
    static void initNodeStates()
    {
        int i = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((AllowPonding != 0))
            {
                Xnode[i].newSurfArea = node_getPondedArea(i, Node[i].newDepth);
            }
            else
            {
                Xnode[i].newSurfArea = node_getSurfArea(i, Node[i].newDepth);
            }
            Node[i].inflow = 0.0;
            Node[i].outflow = Node[i].losses;
            if ((Node[i].newLatFlow >= 0.0))
            {
                Node[i].inflow += Node[i].newLatFlow;
            }
            else
            {
                Node[i].outflow -= Node[i].newLatFlow;
            }
            Xnode[i].sumdqdh = 0.0;
        }
    }

    // dynwave.c:335
    static void findBypassedLinks()
    {
        int i = 0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if (((Xnode[Link[i].node1].converged != 0) && (Xnode[Link[i].node2].converged != 0)))
            {
                Link[i].bypassed = 1;
            }
            else
            {
                Link[i].bypassed = 0;
            }
        }
    }

    // dynwave.c:349
    static void findLimitedLinks()
    {
        int j = 0;
        int n1 = 0;
        int n2 = 0;
        int k = 0;
        double h1 = 0.0;
        double h2 = 0.0;
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if (!(isTrueConduit(j) != 0))
            {
                continue;
            }
            k = Link[j].subIndex;
            Conduit[k].capacityLimited = 0;
            if ((Conduit[k].a1 >= Link[j].xsect.aFull))
            {
                n1 = Link[j].node1;
                n2 = Link[j].node2;
                h1 = (Node[n1].newDepth + Node[n1].invertElev);
                h2 = (Node[n2].newDepth + Node[n2].invertElev);
                if ((((h1 - h2)) > (Math.abs(Conduit[k].slope) * Conduit[k].length)))
                {
                    Conduit[k].capacityLimited = 1;
                }
            }
        }
    }

    // dynwave.c:382
    static void findLinkFlows(double dt)
    {
        int i = 0;
        {
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                if (((isTrueConduit(i) != 0) && (!(Link[i].bypassed != 0))))
                {
                    dwflow_findConduitFlow(i, Steps, Omega, dt);
                }
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if ((isTrueConduit(i) != 0))
            {
                updateNodeFlows(i);
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if (!(isTrueConduit(i) != 0))
            {
                if (!(Link[i].bypassed != 0))
                {
                    findNonConduitFlow(i, dt);
                }
                updateNodeFlows(i);
            }
        }
    }

    // dynwave.c:416
    static int isTrueConduit(int j)
    {
        return ((((Link[j].type == CONDUIT) && (Link[j].xsect.type != DUMMY))) ? 1 : 0);
    }

    // dynwave.c:423
    static void findNonConduitFlow(int i, double dt)
    {
        double qLast = 0.0;
        double qNew = 0.0;
        qLast = Link[i].newFlow;
        Link[i].dqdh = 0.0;
        qNew = link_getInflow(i);
        if ((Link[i].type == PUMP))
        {
            qNew = getModPumpFlow(i, qNew, dt);
        }
        findNonConduitSurfArea(i);
        if (((Steps > 0) && (Link[i].type != PUMP)))
        {
            qNew = ((((1.0 - Omega)) * qLast) + (Omega * qNew));
            if (((qNew * qLast) < 0.0))
            {
                qNew = (0.001 * (((((qNew) < 0)) ? ((-1)) : (1))));
            }
        }
        Link[i].newFlow = qNew;
    }

    // dynwave.c:458
    static double getModPumpFlow(int i, double q, double dt)
    {
        int j = Link[i].node1;
        int k = Link[i].subIndex;
        double newNetInflow = 0.0;
        double netFlowVolume = 0.0;
        double y = 0.0;
        if ((q == 0.0))
        {
            return q;
        }
        if ((Node[j].type == STORAGE))
        {
            return node_getMaxOutflow(j, q, dt);
        }
        switch (Pump[k].type) {
            case TYPE1_PUMP:
                return node_getMaxOutflow(j, q, dt);
            case TYPE2_PUMP:
            case TYPE4_PUMP:
            case TYPE3_PUMP:
                newNetInflow = ((Node[j].inflow - Node[j].outflow) - q);
                netFlowVolume = ((0.5 * ((Node[j].oldNetInflow + newNetInflow))) * dt);
                y = (Node[j].oldDepth + (netFlowVolume / Xnode[j].newSurfArea));
                if ((y <= 0.0))
                {
                    return Node[j].inflow;
                }
        }
        return q;
    }

    // dynwave.c:503
    static void findNonConduitSurfArea(int i)
    {
        if ((Link[i].type == ORIFICE))
        {
            Link[i].surfArea1 = (Orifice[Link[i].subIndex].surfArea / 2.0);
        }
        else
        {
            Link[i].surfArea1 = 0.0;
        }
        Link[i].surfArea2 = Link[i].surfArea1;
        if (((Link[i].flowClass == UP_CRITICAL) || (Node[Link[i].node1].type == STORAGE)))
        {
            Link[i].surfArea1 = 0.0;
        }
        if (((Link[i].flowClass == DN_CRITICAL) || (Node[Link[i].node2].type == STORAGE)))
        {
            Link[i].surfArea2 = 0.0;
        }
    }

    // dynwave.c:528
    static void updateNodeFlows(int i)
    {
        int k = 0;
        int barrels = 1;
        int n1 = Link[i].node1;
        int n2 = Link[i].node2;
        double q = Link[i].newFlow;
        double conduitLossRate = 0.0;
        if ((q >= 0.0))
        {
            Node[n1].outflow += q;
            Node[n2].inflow += q;
        }
        else
        {
            Node[n1].inflow -= q;
            Node[n2].outflow -= q;
        }
        if ((Link[i].type == CONDUIT))
        {
            k = Link[i].subIndex;
            barrels = Conduit[k].barrels;
            conduitLossRate = (((Conduit[k].evapLossRate + Conduit[k].seepLossRate)) * barrels);
            if ((conduitLossRate > 0.0))
            {
                if (((Node[n1].type != OUTFALL) && (Node[n2].type != OUTFALL)))
                {
                    conduitLossRate /= 2.0;
                }
                if ((Node[n1].type != OUTFALL))
                {
                    Node[n1].outflow += conduitLossRate;
                }
                if ((Node[n2].type != OUTFALL))
                {
                    Node[n2].outflow += conduitLossRate;
                }
            }
        }
        Xnode[Link[i].node1].newSurfArea += (Link[i].surfArea1 * barrels);
        Xnode[Link[i].node2].newSurfArea += (Link[i].surfArea2 * barrels);
        Xnode[Link[i].node1].sumdqdh += Link[i].dqdh;
        if ((Link[i].type == PUMP))
        {
            k = Link[i].subIndex;
            if ((Pump[k].type != TYPE4_PUMP))
            {
                Xnode[n2].sumdqdh += Link[i].dqdh;
            }
        }
        else
        {
            Xnode[n2].sumdqdh += Link[i].dqdh;
        }
    }

    // dynwave.c:593
    static int findNodeDepths(double dt)
    {
        int i = 0;
        double yOld = 0.0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            link_setOutfallDepth(i);
        }
        {
            for (i = 0; (i < Nobjects[NODE]); i++)
            {
                if ((Node[i].type == OUTFALL))
                {
                    continue;
                }
                yOld = Node[i].newDepth;
                setNodeDepth(i, dt);
                Xnode[i].converged = 1;
                if ((Math.abs((yOld - Node[i].newDepth)) > HeadTol.v))
                {
                    Xnode[i].converged = 0;
                }
            }
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].type == OUTFALL))
            {
                continue;
            }
            if ((Xnode[i].converged == 0))
            {
                return 0;
            }
        }
        return 1;
    }

    // dynwave.c:636
    static void setNodeDepth(int i, double dt)
    {
        int canPond = 0;
        int isPonded = 0;
        int isSurcharged = 0;
        double dQ = 0.0;
        double dV = 0.0;
        double dy = 0.0;
        double yMax = 0.0;
        double yOld = 0.0;
        double yLast = 0.0;
        double yNew = 0.0;
        double yCrown = 0.0;
        double surfArea = 0.0;
        double denom = 0.0;
        double corr = 0.0;
        double f = 0.0;
        canPond = ((((AllowPonding != 0) && (Node[i].pondedArea > 0.0))) ? 1 : 0);
        isPonded = ((((canPond != 0) && (Node[i].newDepth > Node[i].fullDepth))) ? 1 : 0);
        yCrown = (Node[i].crownElev - Node[i].invertElev);
        yOld = Node[i].oldDepth;
        yLast = Node[i].newDepth;
        Node[i].overflow = 0.0;
        surfArea = Xnode[i].newSurfArea;
        surfArea = (((((surfArea) >= (MinSurfArea.v))) ? (surfArea) : (MinSurfArea.v)));
        dQ = (Node[i].inflow - Node[i].outflow);
        dV = ((0.5 * ((Node[i].oldNetInflow + dQ))) * dt);
        if ((SurchargeMethod == EXTRAN))
        {
            if ((isPonded != 0))
            {
                isSurcharged = 0;
            }
            else if ((Node[i].type == STORAGE))
            {
                isSurcharged = ((((Node[i].surDepth > 0.0) && (yLast > Node[i].fullDepth))) ? 1 : 0);
            }
            else
            {
                isSurcharged = ((((yCrown > 0.0) && (yLast > yCrown))) ? 1 : 0);
            }
        }
        if (!(isSurcharged != 0))
        {
            dy = (dV / surfArea);
            yNew = (yOld + dy);
            if (!(isPonded != 0))
            {
                Xnode[i].oldSurfArea = surfArea;
            }
            if ((Steps > 0))
            {
                yNew = ((((1.0 - Omega)) * yLast) + (Omega * yNew));
            }
            if (((isPonded != 0) && (yNew < Node[i].fullDepth)))
            {
                yNew = (Node[i].fullDepth - 0.0001);
            }
        }
        else
        {
            corr = 1.0;
            if ((Node[i].degree < 0))
            {
                corr = 0.6;
            }
            denom = Xnode[i].sumdqdh;
            if ((yLast < (1.25 * yCrown)))
            {
                f = (((yLast - yCrown)) / yCrown);
                denom += ((((Xnode[i].oldSurfArea / dt) - Xnode[i].sumdqdh)) * Glibm.exp(((-15.0) * f)));
            }
            if ((denom == 0.0))
            {
                dy = 0.0;
            }
            else
            {
                dy = ((corr * dQ) / denom);
            }
            yNew = (yLast + dy);
            if ((yNew < yCrown))
            {
                yNew = (yCrown - 0.0001);
            }
            if (((canPond != 0) && (yNew > Node[i].fullDepth)))
            {
                yNew = (Node[i].fullDepth + 0.0001);
            }
        }
        if ((yNew < 0))
        {
            yNew = 0.0;
        }
        yMax = Node[i].fullDepth;
        if ((canPond == 0))
        {
            yMax += Node[i].surDepth;
        }
        if ((yNew > yMax))
        {
            yNew = getFloodedDepth(i, canPond, dV, yNew, yMax, dt);
        }
        else
        {
            Node[i].newVolume = node_getVolume(i, yNew);
        }
        Xnode[i].dYdT = (Math.abs((yNew - yOld)) / dt);
        Node[i].newDepth = yNew;
    }

    // dynwave.c:766
    static double getFloodedDepth(int i, int canPond, double dV, double yNew, double yMax, double dt)
    {
        if ((canPond == 0))
        {
            Node[i].overflow = (dV / dt);
            Node[i].newVolume = Node[i].fullVolume;
            yNew = yMax;
        }
        else
        {
            Node[i].newVolume = (((((((Node[i].oldVolume + dV))) >= (Node[i].fullVolume))) ? (((Node[i].oldVolume + dV))) : (Node[i].fullVolume)));
            Node[i].overflow = (((Node[i].newVolume - (((((Node[i].oldVolume) >= (Node[i].fullVolume))) ? (Node[i].oldVolume) : (Node[i].fullVolume))))) / dt);
        }
        if ((Node[i].overflow < 0.0001))
        {
            Node[i].overflow = 0.0;
        }
        return yNew;
    }

    // dynwave.c:799
    static double getVariableStep(double maxStep)
    {
        IBox minLink = new IBox((-1));
        IBox minNode = new IBox((-1));
        double tMin = 0.0;
        double tMinLink = 0.0;
        double tMinNode = 0.0;
        tMin = maxStep;
        tMinLink = getLinkStep(tMin, minLink);
        tMinNode = getNodeStep(tMinLink, minNode);
        tMin = tMinLink;
        if ((tMinNode < tMin))
        {
            tMin = tMinNode;
            minLink.v = (-1);
        }
        stats_updateCriticalTimeCount(minNode.v, minLink.v);
        if ((tMin < MinRouteStep.v))
        {
            tMin = MinRouteStep.v;
        }
        return tMin;
    }

    // dynwave.c:836
    static double getLinkStep(double tMin, IRef minLink)
    {
        int i = 0;
        int k = 0;
        double q = 0.0;
        double t = 0.0;
        double tLink = tMin;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if ((Link[i].type == CONDUIT))
            {
                k = Link[i].subIndex;
                q = (Math.abs(Link[i].newFlow) / Conduit[k].barrels);
                if ((((q <= 0.0001) || (Conduit[k].a1 <= 0.0001)) || (Link[i].froude <= 0.01)))
                {
                    continue;
                }
                t = ((Link[i].newVolume / Conduit[k].barrels) / q);
                t = ((t * Conduit[k].modLength) / link_getLength(i));
                t = (((t * Link[i].froude) / ((1.0 + Link[i].froude))) * CourantFactor.v);
                if ((t < tLink))
                {
                    tLink = t;
                    minLink.set(i);
                }
            }
        }
        return tLink;
    }

    // dynwave.c:881
    static double getNodeStep(double tMin, IRef minNode)
    {
        int i = 0;
        double maxDepth = 0.0;
        double dYdT = 0.0;
        double t1 = 0.0;
        double tNode = tMin;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].type == OUTFALL))
            {
                continue;
            }
            if ((Node[i].newDepth <= 0.0001))
            {
                continue;
            }
            if (((Node[i].newDepth + 0.0001) >= (Node[i].crownElev - Node[i].invertElev)))
            {
                continue;
            }
            maxDepth = (((Node[i].crownElev - Node[i].invertElev)) * 0.25);
            if ((maxDepth < 0.0001))
            {
                continue;
            }
            dYdT = Xnode[i].dYdT;
            if ((dYdT < 0.0001))
            {
                continue;
            }
            t1 = (maxDepth / dYdT);
            if ((t1 < tNode))
            {
                tNode = t1;
                minNode.set(i);
            }
        }
        return tNode;
    }

}
