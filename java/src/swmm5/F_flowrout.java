// Generated from EPA SWMM 5.2.4 (flowrout.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_flowrout {
    static double flowrout_OMEGA = 0.55;
    static int MAXITER = 10;
    static double STOPTOL = 0.005;

    // flowrout.c:75
    static void flowrout_init(int routingModel)
    {
        if ((routingModel == DW))
        {
            validateGeneralLayout();
            dynwave_init();
            if ((Fhotstart1.mode == NO_FILE))
            {
                initNodeDepths();
                initLinkDepths();
            }
        }
        else
        {
            validateTreeLayout();
        }
        initNodes();
        initLinks(routingModel);
    }

    // flowrout.c:107
    static void flowrout_close(int routingModel)
    {
        if ((routingModel == DW))
        {
            dynwave_close();
        }
    }

    // flowrout.c:119
    static double flowrout_getRoutingStep(int routingModel, double fixedStep)
    {
        if ((routingModel == DW))
        {
            return dynwave_getRoutingStep(fixedStep);
        }
        return fixedStep;
    }

    // flowrout.c:136
    static int flowrout_execute(int[] links, int routingModel, double tStep)
    {
        int i = 0;
        int j = 0;
        int n1 = 0;
        DBox qin = new DBox(0.0);
        DBox qout = new DBox(0.0);
        double steps = 0.0;
        if ((ErrorCode != 0))
        {
            return 0;
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            Node[j].updated = 0;
            Node[j].overflow = 0.0;
            if (((Node[j].type != STORAGE) && (Node[j].newVolume > Node[j].fullVolume)))
            {
                Node[j].overflow = (((Node[j].newVolume - Node[j].fullVolume)) / tStep);
            }
        }
        if ((routingModel == DW))
        {
            return dynwave_execute(tStep);
        }
        steps = 0.0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            j = links[i];
            n1 = Link[j].node1;
            if ((Node[n1].type == STORAGE))
            {
                updateStorageState(n1, i, links, tStep);
            }
            qin.v = getLinkInflow(j, tStep);
            if ((routingModel == SF))
            {
                steps += (steadyflow_execute(j, qin, qout, tStep));
            }
            else
            {
                steps += (kinwave_execute(j, qin, qout, tStep));
            }
            Link[j].newFlow = qout.v;
            Node[Link[j].node1].outflow += qin.v;
            Node[Link[j].node2].inflow += qout.v;
        }
        if ((Nobjects[LINK] > 0))
        {
            steps /= Nobjects[LINK];
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            setNewNodeState(j, tStep);
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            setNewLinkState(j);
        }
        return R.D2I(((steps + 0.5)));
    }

    // flowrout.c:203
    static void validateTreeLayout()
    {
        int j = 0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            switch (Node[j].type) {
                case DIVIDER:
                    if ((Node[j].degree > 2))
                    {
                        report_writeErrorMsg(ERR_DIVIDER, Node[j].ID);
                    }
                    break;
                case OUTFALL:
                    if ((Node[j].degree > 0))
                    {
                        report_writeErrorMsg(ERR_OUTFALL, Node[j].ID);
                    }
                    break;
                case STORAGE:
                    break;
                default:
                    if ((Node[j].degree > 1))
                    {
                        report_writeErrorMsg(ERR_MULTI_OUTLET, Node[j].ID);
                    }
            }
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            switch (Link[j].type) {
                case CONDUIT:
                    if (((Conduit[Link[j].subIndex].slope < 0.0) && (Link[j].xsect.type != DUMMY)))
                    {
                        report_writeErrorMsg(ERR_SLOPE, Link[j].ID);
                    }
                    break;
                case ORIFICE:
                case WEIR:
                case OUTLET:
                    if ((Node[Link[j].node1].type != STORAGE))
                    {
                        report_writeErrorMsg(ERR_REGULATOR, Link[j].ID);
                    }
            }
        }
    }

    // flowrout.c:274
    static void validateGeneralLayout()
    {
        int i = 0;
        int j = 0;
        int outletCount = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            Node[i].inflow = 0.0;
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            i = Link[j].node1;
            if ((Node[i].type != OUTFALL))
            {
                i = Link[j].node2;
            }
            Node[i].inflow += 1.0;
            if (((((Link[j].type == CONDUIT) && (Link[j].xsect.type == DUMMY))) || (((Link[j].type == PUMP) && (Pump[Link[j].subIndex].type == IDEAL_PUMP)))))
            {
                i = Link[j].node1;
                if ((Link[j].direction < 0))
                {
                    i = Link[j].node2;
                }
                if ((Node[i].degree > 1))
                {
                    report_writeErrorMsg(ERR_DUMMY_LINK, Node[i].ID);
                }
            }
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].type == OUTFALL))
            {
                if (((Node[i].degree + R.D2I(Node[i].inflow)) > 1))
                {
                    report_writeErrorMsg(ERR_OUTFALL, Node[i].ID);
                }
                else
                {
                    outletCount++;
                }
            }
        }
        if ((outletCount == 0))
        {
            report_writeErrorMsg(ERR_NO_OUTLETS, L0);
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].inflow == 0.0))
            {
                Node[i].degree = (-Node[i].degree);
            }
            Node[i].inflow = 0.0;
        }
    }

    // flowrout.c:337
    static void initNodeDepths()
    {
        int i = 0;
        int n = 0;
        double y = 0.0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            Node[i].inflow = 0.0;
            Node[i].outflow = 0.0;
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if ((Link[i].newDepth > 0.0001))
            {
                y = (Link[i].newDepth + Link[i].offset1);
            }
            else
            {
                y = 0.0;
            }
            n = Link[i].node1;
            Node[n].inflow += y;
            Node[n].outflow += 1.0;
            n = Link[i].node2;
            Node[n].inflow += y;
            Node[n].outflow += 1.0;
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].type == OUTFALL))
            {
                continue;
            }
            if ((Node[i].type == STORAGE))
            {
                continue;
            }
            if ((Node[i].initDepth > 0.0))
            {
                continue;
            }
            if ((Node[i].outflow > 0.0))
            {
                Node[i].newDepth = (Node[i].inflow / Node[i].outflow);
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            link_setOutfallDepth(i);
        }
    }

    // flowrout.c:389
    static void initLinkDepths()
    {
        int i = 0;
        double y = 0.0;
        double y1 = 0.0;
        double y2 = 0.0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if ((Link[i].type == CONDUIT))
            {
                if ((Link[i].q0 != 0.0))
                {
                    continue;
                }
                y1 = (Node[Link[i].node1].newDepth - Link[i].offset1);
                y1 = (((((y1) >= (0.0))) ? (y1) : (0.0)));
                y1 = (((((y1) <= (Link[i].xsect.yFull))) ? (y1) : (Link[i].xsect.yFull)));
                y2 = (Node[Link[i].node2].newDepth - Link[i].offset2);
                y2 = (((((y2) >= (0.0))) ? (y2) : (0.0)));
                y2 = (((((y2) <= (Link[i].xsect.yFull))) ? (y2) : (Link[i].xsect.yFull)));
                y = (0.5 * ((y1 + y2)));
                y = (((((y) >= (0.0001))) ? (y) : (0.0001)));
                Link[i].newDepth = y;
            }
        }
    }

    // flowrout.c:425
    static void initNodes()
    {
        int i = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            Node[i].inflow = Node[i].newLatFlow;
            Node[i].outflow = 0.0;
            Node[i].newVolume = 0.0;
            if ((((AllowPonding != 0) && (Node[i].pondedArea > 0.0)) && (Node[i].newDepth > Node[i].fullDepth)))
            {
                Node[i].newVolume = (Node[i].fullVolume + (((Node[i].newDepth - Node[i].fullDepth)) * Node[i].pondedArea));
            }
            else
            {
                Node[i].newVolume = node_getVolume(i, Node[i].newDepth);
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if ((Link[i].newFlow >= 0.0))
            {
                Node[Link[i].node1].outflow += Link[i].newFlow;
                Node[Link[i].node2].inflow += Link[i].newFlow;
            }
            else
            {
                Node[Link[i].node1].inflow -= Link[i].newFlow;
                Node[Link[i].node2].outflow -= Link[i].newFlow;
            }
        }
    }

    // flowrout.c:472
    static void initLinks(int routingModel)
    {
        int i = 0;
        int k = 0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if ((routingModel == SF))
            {
                Link[i].newFlow = 0.0;
            }
            else if ((Link[i].type == CONDUIT))
            {
                k = Link[i].subIndex;
                Conduit[k].q1 = (Link[i].newFlow / Conduit[k].barrels);
                Conduit[k].q2 = Conduit[k].q1;
                Conduit[k].a1 = xsect_getAofY(Link[i].xsect, Link[i].newDepth);
                Conduit[k].a2 = Conduit[k].a1;
                {
                    Link[i].newVolume = ((Conduit[k].a1 * link_getLength(i)) * Conduit[k].barrels);
                }
                Link[i].oldVolume = Link[i].newVolume;
            }
        }
    }

    // flowrout.c:511
    static double getLinkInflow(int j, double dt)
    {
        int n1 = Link[j].node1;
        double q = 0.0;
        if ((((Link[j].type == CONDUIT) || (Link[j].type == PUMP)) || (Node[n1].type == STORAGE)))
        {
            q = link_getInflow(j);
        }
        else
        {
            q = 0.0;
        }
        return node_getMaxOutflow(n1, q, dt);
    }

    // flowrout.c:531
    static void updateStorageState(int i, int j, int[] links, double dt)
    {
        int iter = 0;
        int stopped = 0;
        double vFixed = 0.0;
        double v2 = 0.0;
        double d1 = 0.0;
        double d2 = 0.0;
        if ((Node[i].type != STORAGE))
        {
            return;
        }
        if ((Node[i].updated != 0))
        {
            return;
        }
        vFixed = (Node[i].oldVolume + ((0.5 * (((Node[i].oldNetInflow + Node[i].inflow) - Node[i].outflow))) * dt));
        d1 = Node[i].newDepth;
        iter = 1;
        stopped = 0;
        while (((iter < MAXITER) && (!(stopped != 0))))
        {
            v2 = (vFixed - ((0.5 * getStorageOutflow(i, j, links, dt)) * dt));
            v2 = (((((0.0) >= (v2))) ? (0.0) : (v2)));
            Node[i].overflow = 0.0;
            if ((v2 > Node[i].fullVolume))
            {
                Node[i].overflow = (((v2 - (((((Node[i].oldVolume) >= (Node[i].fullVolume))) ? (Node[i].oldVolume) : (Node[i].fullVolume))))) / dt);
                if ((Node[i].overflow < 0.0001))
                {
                    Node[i].overflow = 0.0;
                }
                if (((!(AllowPonding != 0)) || (Node[i].pondedArea == 0.0)))
                {
                    v2 = Node[i].fullVolume;
                }
            }
            Node[i].newVolume = v2;
            d2 = node_getDepth(i, v2);
            Node[i].newDepth = d2;
            d2 = ((((1.0 - flowrout_OMEGA)) * d1) + (flowrout_OMEGA * d2));
            if ((Math.abs((d2 - d1)) <= STOPTOL))
            {
                stopped = 1;
            }
            d1 = d2;
            iter++;
        }
        Node[i].updated = 1;
    }

    // flowrout.c:605
    static double getStorageOutflow(int i, int j, int[] links, double dt)
    {
        int k = 0;
        int m = 0;
        double outflow = 0.0;
        for (k = j; (k < Nobjects[LINK]); k++)
        {
            m = links[k];
            if ((Link[m].node1 != i))
            {
                break;
            }
            outflow += (getLinkInflow(m, dt));
        }
        return outflow;
    }

    // flowrout.c:629
    static void setNewNodeState(int j, double dt)
    {
        int canPond = 0;
        double newNetInflow = 0.0;
        if ((Node[j].type == STORAGE))
        {
            if ((Node[j].updated == 0))
            {
                updateStorageState(j, Nobjects[LINK], (null), dt);
            }
            return;
        }
        newNetInflow = ((Node[j].inflow - Node[j].outflow) - Node[j].losses);
        Node[j].newVolume = (Node[j].oldVolume + (newNetInflow * dt));
        if ((Node[j].newVolume < 0.0001))
        {
            Node[j].newVolume = 0.0;
        }
        Node[j].overflow = 0.0;
        canPond = ((((AllowPonding != 0) && (Node[j].pondedArea > 0.0))) ? 1 : 0);
        if ((Node[j].newVolume > Node[j].fullVolume))
        {
            Node[j].overflow = (((Node[j].newVolume - (((((Node[j].oldVolume) >= (Node[j].fullVolume))) ? (Node[j].oldVolume) : (Node[j].fullVolume))))) / dt);
            if ((Node[j].overflow < 0.0001))
            {
                Node[j].overflow = 0.0;
            }
            if (!(canPond != 0))
            {
                Node[j].newVolume = Node[j].fullVolume;
            }
        }
        Node[j].newDepth = node_getDepth(j, Node[j].newVolume);
    }

    // flowrout.c:673
    static void setNewLinkState(int j)
    {
        int k = 0;
        double a = 0.0;
        double y1 = 0.0;
        double y2 = 0.0;
        Link[j].newDepth = 0.0;
        Link[j].newVolume = 0.0;
        if ((Link[j].type == CONDUIT))
        {
            k = Link[j].subIndex;
            a = (0.5 * ((Conduit[k].a1 + Conduit[k].a2)));
            Link[j].newVolume = ((a * link_getLength(j)) * Conduit[k].barrels);
            y1 = xsect_getYofA(Link[j].xsect, Conduit[k].a1);
            y2 = xsect_getYofA(Link[j].xsect, Conduit[k].a2);
            Link[j].newDepth = (0.5 * ((y1 + y2)));
            updateNodeDepth(Link[j].node1, (y1 + Link[j].offset1));
            updateNodeDepth(Link[j].node2, (y2 + Link[j].offset2));
            if ((Conduit[k].a1 >= Link[j].xsect.aFull))
            {
                Conduit[k].capacityLimited = 1;
                Conduit[k].fullState = ALL_FULL;
            }
            else
            {
                Conduit[k].capacityLimited = 0;
                Conduit[k].fullState = 0;
            }
        }
    }

    // flowrout.c:717
    static void updateNodeDepth(int i, double y)
    {
        if ((Node[i].type == STORAGE))
        {
            return;
        }
        if ((((Node[i].type != OUTFALL) && (Node[i].degree > 0)) && (Node[i].overflow > 0.0)))
        {
            y = Node[i].fullDepth;
        }
        if ((Node[i].newDepth < y))
        {
            Node[i].newDepth = y;
            if (((Node[i].fullDepth > 0.0) && (y > Node[i].fullDepth)))
            {
                Node[i].newDepth = Node[i].fullDepth;
            }
        }
    }

    // flowrout.c:748
    static int steadyflow_execute(int j, DRef qin, DRef qout, double tStep)
    {
        int k = 0;
        double s = 0.0;
        double q = 0.0;
        if ((Link[j].type == CONDUIT))
        {
            k = Link[j].subIndex;
            q = ((qin.get()) / Conduit[k].barrels);
            if ((Link[j].xsect.type == DUMMY))
            {
                Conduit[k].a1 = 0.0;
            }
            else
            {
                q -= (link_getLossRate(j, SF, q, tStep));
                if ((q > Link[j].qFull))
                {
                    q = Link[j].qFull;
                    Conduit[k].a1 = Link[j].xsect.aFull;
                    qin.set((q * Conduit[k].barrels));
                }
                else
                {
                    s = (q / Conduit[k].beta);
                    Conduit[k].a1 = xsect_getAofS(Link[j].xsect, s);
                }
            }
            Conduit[k].a2 = Conduit[k].a1;
            Conduit[k].q1Old = Conduit[k].q1;
            Conduit[k].q2Old = Conduit[k].q2;
            Conduit[k].q1 = q;
            Conduit[k].q2 = q;
            qout.set((q * Conduit[k].barrels));
        }
        else
        {
            qout.set((qin.get()));
        }
        return 1;
    }

}
