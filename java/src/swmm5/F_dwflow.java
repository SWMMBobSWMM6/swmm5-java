// Generated from EPA SWMM 5.2.4 (dwflow.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_dwflow {
    static double MAXVELOCITY = 50.0;

    // dwflow.c:57
    static void dwflow_findConduitFlow(int j, int steps, double omega, double dt)
    {
        int k = 0;
        int n1 = 0;
        int n2 = 0;
        double z1 = 0.0;
        double z2 = 0.0;
        DBox h1 = new DBox(0.0);
        DBox h2 = new DBox(0.0);
        DBox y1 = new DBox(0.0);
        DBox y2 = new DBox(0.0);
        double a1 = 0.0;
        double a2 = 0.0;
        double r1 = 0.0;
        double yMid = 0.0;
        double rMid = 0.0;
        double aMid = 0.0;
        double aWtd = 0.0;
        double rWtd = 0.0;
        double qLast = 0.0;
        double qOld = 0.0;
        double aOld = 0.0;
        double v = 0.0;
        double rho = 0.0;
        double sigma = 0.0;
        double length = 0.0;
        double wSlot = 0.0;
        double dq1 = 0.0;
        double dq2 = 0.0;
        double dq3 = 0.0;
        double dq4 = 0.0;
        double dq5 = 0.0;
        double dq6 = 0.0;
        double denom = 0.0;
        double q = 0.0;
        double barrels = 0.0;
        TXsect xsect = Link[j].xsect;
        int isFull = 0;
        int isClosed = 0;
        if ((Link[j].setting == 0))
        {
            isClosed = 1;
        }
        k = Link[j].subIndex;
        barrels = Conduit[k].barrels;
        qOld = (Link[j].oldFlow / barrels);
        qLast = Conduit[k].q1;
        Conduit[k].evapLossRate = 0.0;
        Conduit[k].seepLossRate = 0.0;
        n1 = Link[j].node1;
        n2 = Link[j].node2;
        z1 = (Node[n1].invertElev + Link[j].offset1);
        z2 = (Node[n2].invertElev + Link[j].offset2);
        h1.v = (Node[n1].newDepth + Node[n1].invertElev);
        h2.v = (Node[n2].newDepth + Node[n2].invertElev);
        h1.v = (((((h1.v) >= (z1))) ? (h1.v) : (z1)));
        h2.v = (((((h2.v) >= (z2))) ? (h2.v) : (z2)));
        y1.v = (h1.v - z1);
        y2.v = (h2.v - z2);
        y1.v = (((((y1.v) >= (0.0001))) ? (y1.v) : (0.0001)));
        y2.v = (((((y2.v) >= (0.0001))) ? (y2.v) : (0.0001)));
        if ((SurchargeMethod != SLOT))
        {
            y1.v = (((((y1.v) <= (xsect.yFull))) ? (y1.v) : (xsect.yFull)));
            y2.v = (((((y2.v) <= (xsect.yFull))) ? (y2.v) : (xsect.yFull)));
        }
        aOld = Conduit[k].a2;
        aOld = (((((aOld) >= (0.0001))) ? (aOld) : (0.0001)));
        length = Conduit[k].modLength;
        findSurfArea(j, qLast, length, h1, h2, y1, y2);
        wSlot = getSlotWidth(xsect, y1.v);
        a1 = dwflow_getArea(xsect, y1.v, wSlot);
        r1 = getHydRad(xsect, y1.v);
        wSlot = getSlotWidth(xsect, y2.v);
        a2 = dwflow_getArea(xsect, y2.v, wSlot);
        yMid = (0.5 * ((y1.v + y2.v)));
        wSlot = getSlotWidth(xsect, yMid);
        aMid = dwflow_getArea(xsect, yMid, wSlot);
        rMid = getHydRad(xsect, yMid);
        if (((y1.v >= xsect.yFull) && (y2.v >= xsect.yFull)))
        {
            isFull = 1;
        }
        if ((((((Link[j].flowClass == DRY) || (Link[j].flowClass == UP_DRY)) || (Link[j].flowClass == DN_DRY)) || (isClosed != 0)) || (aMid <= 0.0001)))
        {
            Conduit[k].a1 = (0.5 * ((a1 + a2)));
            Conduit[k].q1 = 0.0;
            ;
            Conduit[k].q2 = 0.0;
            Link[j].dqdh = ((((32.2 * dt) * aMid) / length) * barrels);
            Link[j].froude = 0.0;
            Link[j].newDepth = (((((yMid) <= (Link[j].xsect.yFull))) ? (yMid) : (Link[j].xsect.yFull)));
            Link[j].newVolume = ((Conduit[k].a1 * link_getLength(j)) * barrels);
            Link[j].newFlow = 0.0;
            return;
        }
        v = (qLast / aMid);
        if ((Math.abs(v) > MAXVELOCITY))
        {
            v = (MAXVELOCITY * (((((qLast) < 0)) ? ((-1)) : (1))));
        }
        Link[j].froude = link_getFroude(j, v, yMid);
        if (((Link[j].flowClass == SUBCRITICAL) && (Link[j].froude > 1.0)))
        {
            Link[j].flowClass = SUPCRITICAL;
        }
        if ((Link[j].froude <= 0.5))
        {
            sigma = 1.0;
        }
        else if ((Link[j].froude >= 1.0))
        {
            sigma = 0.0;
        }
        else
        {
            sigma = (2.0 * ((1.0 - Link[j].froude)));
        }
        rho = 1.0;
        if ((((!(isFull != 0)) && (qLast > 0.0)) && (h1.v >= h2.v)))
        {
            rho = sigma;
        }
        aWtd = (a1 + (((aMid - a1)) * rho));
        rWtd = (r1 + (((rMid - r1)) * rho));
        if ((InertDamping == NO_DAMPING))
        {
            sigma = 1.0;
        }
        else if ((InertDamping == FULL_DAMPING))
        {
            sigma = 0.0;
        }
        if (((isFull != 0) && (!(xsect_isOpen(xsect.type) != 0))))
        {
            sigma = 0.0;
        }
        if (((xsect.type == FORCE_MAIN) && (isFull != 0)))
        {
            dq1 = (dt * forcemain_getFricSlope(j, Math.abs(v), rMid));
        }
        else
        {
            dq1 = (((dt * Conduit[k].roughFactor) / Glibm.pow(rWtd, 1.33333)) * Math.abs(v));
        }
        dq2 = ((((dt * 32.2) * aWtd) * ((h2.v - h1.v))) / length);
        dq3 = 0.0;
        dq4 = 0.0;
        if ((sigma > 0.0))
        {
            dq3 = (((2.0 * v) * ((aMid - aOld))) * sigma);
            dq4 = (((((dt * v) * v) * ((a2 - a1))) / length) * sigma);
        }
        dq5 = 0.0;
        if ((Conduit[k].hasLosses != 0))
        {
            dq5 = (((findLocalLosses(j, a1, a2, aMid, qLast) / 2.0) / length) * dt);
        }
        dq6 = ((((link_getLossRate(j, DW, qLast, dt) * 2.5) * dt) * v) / link_getLength(j));
        denom = ((1.0 + dq1) + dq5);
        q = ((((((qOld - dq2) + dq3) + dq4) + dq6)) / denom);
        Link[j].dqdh = ((((((1.0 / denom) * 32.2) * dt) * aWtd) / length) * barrels);
        Link[j].inletControl = 0;
        Link[j].normalFlow = 0;
        if ((q > 0.0))
        {
            if (((xsect.culvertCode > 0) && (!(isFull != 0))))
            {
                q = culvert_getInflow(j, q, h1.v);
            }
            else if ((((NormalFlowLtd != NEITHER) && (y1.v < Link[j].xsect.yFull)) && (((Link[j].flowClass == SUBCRITICAL) || (Link[j].flowClass == SUPCRITICAL)))))
            {
                q = checkNormalFlow(j, q, y1.v, y2.v, a1, r1);
            }
        }
        if ((steps > 0))
        {
            q = ((((1.0 - omega)) * qLast) + (omega * q));
            if (((q * qLast) < 0.0))
            {
                q = (0.001 * (((((q) < 0)) ? ((-1)) : (1))));
            }
        }
        if ((Link[j].qLimit > 0.0))
        {
            if ((Math.abs(q) > Link[j].qLimit))
            {
                q = ((((((q) < 0)) ? ((-1)) : (1))) * Link[j].qLimit);
            }
        }
        if ((link_setFlapGate(j, n1, n2, q) != 0))
        {
            q = 0.0;
        }
        if (((q > 0.0001) && (Node[n1].newDepth <= 0.0001)))
        {
            q = 0.0001;
        }
        if (((q < (-0.0001)) && (Node[n2].newDepth <= 0.0001)))
        {
            q = (-0.0001);
        }
        Conduit[k].a1 = aMid;
        Conduit[k].q1 = q;
        Conduit[k].q2 = q;
        Link[j].newDepth = (((((yMid) <= (xsect.yFull))) ? (yMid) : (xsect.yFull)));
        aMid = (((a1 + a2)) / 2.0);
        Conduit[k].fullState = link_getFullState(a1, a2, xsect.aFull);
        Link[j].newVolume = ((aMid * link_getLength(j)) * barrels);
        Link[j].newFlow = (q * barrels);
    }

    // dwflow.c:297
    static int getFlowClass(int j, double q, double h1, double h2, double y1, double y2, DRef yC, DRef yN, DRef fasnh)
    {
        int n1 = 0;
        int n2 = 0;
        int flowClass = 0;
        double ycMin = 0.0;
        double ycMax = 0.0;
        double z1 = 0.0;
        double z2 = 0.0;
        n1 = Link[j].node1;
        n2 = Link[j].node2;
        z1 = Link[j].offset1;
        z2 = Link[j].offset2;
        if ((Node[n1].type == OUTFALL))
        {
            z1 = (((((0.0) >= (((z1 - Node[n1].newDepth))))) ? (0.0) : (((z1 - Node[n1].newDepth)))));
        }
        if ((Node[n2].type == OUTFALL))
        {
            z2 = (((((0.0) >= (((z2 - Node[n2].newDepth))))) ? (0.0) : (((z2 - Node[n2].newDepth)))));
        }
        flowClass = SUBCRITICAL;
        fasnh.set(1.0);
        if (((y1 > 0.0001) && (y2 > 0.0001)))
        {
            if ((q < 0.0))
            {
                if ((z1 > 0.0))
                {
                    yN.set(link_getYnorm(j, Math.abs(q)));
                    yC.set(link_getYcrit(j, Math.abs(q)));
                    ycMin = (((((yN.get()) <= (yC.get()))) ? (yN.get()) : (yC.get())));
                    if ((y1 < ycMin))
                    {
                        flowClass = UP_CRITICAL;
                    }
                }
            }
            else
            {
                if ((z2 > 0.0))
                {
                    yN.set(link_getYnorm(j, Math.abs(q)));
                    yC.set(link_getYcrit(j, Math.abs(q)));
                    ycMin = (((((yN.get()) <= (yC.get()))) ? (yN.get()) : (yC.get())));
                    ycMax = (((((yN.get()) >= (yC.get()))) ? (yN.get()) : (yC.get())));
                    if ((y2 < ycMin))
                    {
                        flowClass = DN_CRITICAL;
                    }
                    else if ((y2 < ycMax))
                    {
                        if (((ycMax - ycMin) < 0.0001))
                        {
                            fasnh.set(0.0);
                        }
                        else
                        {
                            fasnh.set((((ycMax - y2)) / ((ycMax - ycMin))));
                        }
                    }
                }
            }
        }
        else if (((y1 <= 0.0001) && (y2 <= 0.0001)))
        {
            flowClass = DRY;
        }
        else if ((y2 > 0.0001))
        {
            if ((h2 < (Node[n1].invertElev + Link[j].offset1)))
            {
                flowClass = UP_DRY;
            }
            else if ((z1 > 0.0))
            {
                yN.set(link_getYnorm(j, Math.abs(q)));
                yC.set(link_getYcrit(j, Math.abs(q)));
                flowClass = UP_CRITICAL;
            }
        }
        else
        {
            if ((h1 < (Node[n2].invertElev + Link[j].offset2)))
            {
                flowClass = DN_DRY;
            }
            else if ((z2 > 0.0))
            {
                yN.set(link_getYnorm(j, Math.abs(q)));
                yC.set(link_getYcrit(j, Math.abs(q)));
                flowClass = DN_CRITICAL;
            }
        }
        return flowClass;
    }

    // dwflow.c:417
    static void findSurfArea(int j, double q, double length, DRef h1, DRef h2, DRef y1, DRef y2)
    {
        int n1 = 0;
        int n2 = 0;
        double flowDepth1 = 0.0;
        double flowDepth2 = 0.0;
        double flowDepthMid = 0.0;
        double width1 = 0.0;
        double width2 = 0.0;
        double widthMid = 0.0;
        double surfArea1 = 0.0;
        double surfArea2 = 0.0;
        DBox criticalDepth = new DBox(0.0);
        DBox normalDepth = new DBox(0.0);
        double fullDepth = 0.0;
        DBox fasnh = new DBox(1.0);
        TXsect xsect = Link[j].xsect;
        n1 = Link[j].node1;
        n2 = Link[j].node2;
        flowDepth1 = y1.get();
        flowDepth2 = y2.get();
        normalDepth.v = (((flowDepth1 + flowDepth2)) / 2.0);
        criticalDepth.v = normalDepth.v;
        fullDepth = xsect.yFull;
        if (((flowDepth1 >= fullDepth) && (flowDepth2 >= fullDepth)))
        {
            Link[j].flowClass = SUBCRITICAL;
        }
        else
        {
            Link[j].flowClass = getFlowClass(j, q, h1.get(), h2.get(), y1.get(), y2.get(), criticalDepth, normalDepth, fasnh);
        }
        switch (Link[j].flowClass) {
            case SUBCRITICAL:
                flowDepthMid = (0.5 * ((flowDepth1 + flowDepth2)));
                if ((flowDepthMid < 0.0001))
                {
                    flowDepthMid = 0.0001;
                }
                width1 = dwflow_getWidth(xsect, flowDepth1);
                width2 = dwflow_getWidth(xsect, flowDepth2);
                widthMid = dwflow_getWidth(xsect, flowDepthMid);
                surfArea1 = ((((width1 + widthMid)) * length) / 4.0);
                surfArea2 = (((((widthMid + width2)) * length) / 4.0) * fasnh.v);
                break;
            case UP_CRITICAL:
                flowDepth1 = criticalDepth.v;
                if ((normalDepth.v < criticalDepth.v))
                {
                    flowDepth1 = normalDepth.v;
                }
                flowDepth1 = (((((flowDepth1) >= (0.0001))) ? (flowDepth1) : (0.0001)));
                h1.set(((Node[n1].invertElev + Link[j].offset1) + flowDepth1));
                flowDepthMid = (0.5 * ((flowDepth1 + flowDepth2)));
                if ((flowDepthMid < 0.0001))
                {
                    flowDepthMid = 0.0001;
                }
                width2 = dwflow_getWidth(xsect, flowDepth2);
                widthMid = dwflow_getWidth(xsect, flowDepthMid);
                surfArea2 = ((((widthMid + width2)) * length) * 0.5);
                break;
            case DN_CRITICAL:
                flowDepth2 = criticalDepth.v;
                if ((normalDepth.v < criticalDepth.v))
                {
                    flowDepth2 = normalDepth.v;
                }
                flowDepth2 = (((((flowDepth2) >= (0.0001))) ? (flowDepth2) : (0.0001)));
                h2.set(((Node[n2].invertElev + Link[j].offset2) + flowDepth2));
                width1 = dwflow_getWidth(xsect, flowDepth1);
                flowDepthMid = (0.5 * ((flowDepth1 + flowDepth2)));
                if ((flowDepthMid < 0.0001))
                {
                    flowDepthMid = 0.0001;
                }
                widthMid = dwflow_getWidth(xsect, flowDepthMid);
                surfArea1 = ((((width1 + widthMid)) * length) * 0.5);
                break;
            case UP_DRY:
                flowDepth1 = 0.0001;
                flowDepthMid = (0.5 * ((flowDepth1 + flowDepth2)));
                if ((flowDepthMid < 0.0001))
                {
                    flowDepthMid = 0.0001;
                }
                width1 = dwflow_getWidth(xsect, flowDepth1);
                width2 = dwflow_getWidth(xsect, flowDepth2);
                widthMid = dwflow_getWidth(xsect, flowDepthMid);
                surfArea2 = ((((widthMid + width2)) * length) / 4.0);
                if ((Link[j].offset1 <= 0.0))
                {
                    surfArea1 = ((((width1 + widthMid)) * length) / 4.0);
                }
                break;
            case DN_DRY:
                flowDepth2 = 0.0001;
                flowDepthMid = (0.5 * ((flowDepth1 + flowDepth2)));
                if ((flowDepthMid < 0.0001))
                {
                    flowDepthMid = 0.0001;
                }
                width1 = dwflow_getWidth(xsect, flowDepth1);
                width2 = dwflow_getWidth(xsect, flowDepth2);
                widthMid = dwflow_getWidth(xsect, flowDepthMid);
                surfArea1 = ((((widthMid + width1)) * length) / 4.0);
                if ((Link[j].offset2 <= 0.0))
                {
                    surfArea2 = ((((width2 + widthMid)) * length) / 4.0);
                }
                break;
            case DRY:
                surfArea1 = ((0.0001 * length) / 2.0);
                surfArea2 = surfArea1;
                break;
        }
        Link[j].surfArea1 = surfArea1;
        Link[j].surfArea2 = surfArea2;
        y1.set(flowDepth1);
        y2.set(flowDepth2);
    }

    // dwflow.c:554
    static double findLocalLosses(int j, double a1, double a2, double aMid, double q)
    {
        double losses = 0.0;
        q = Math.abs(q);
        if ((a1 > 0.0001))
        {
            losses += (Link[j].cLossInlet * ((q / a1)));
        }
        if ((a2 > 0.0001))
        {
            losses += (Link[j].cLossOutlet * ((q / a2)));
        }
        if ((aMid > 0.0001))
        {
            losses += (Link[j].cLossAvg * ((q / aMid)));
        }
        return losses;
    }

    // dwflow.c:575
    static double getSlotWidth(TXsect xsect, double y)
    {
        double yNorm = (y / xsect.yFull);
        if ((((SurchargeMethod != SLOT) || (xsect_isOpen(xsect.type) != 0)) || (yNorm < CrownCutoff)))
        {
            return 0.0;
        }
        if ((yNorm > 1.78))
        {
            return (0.01 * xsect.wMax);
        }
        return ((xsect.wMax * 0.5423) * Glibm.exp((-(Glibm.pow(yNorm, 2.4)))));
    }

    // dwflow.c:592
    static double dwflow_getWidth(TXsect xsect, double y)
    {
        double wSlot = getSlotWidth(xsect, y);
        if ((wSlot > 0.0))
        {
            return wSlot;
        }
        if ((((y / xsect.yFull) >= CrownCutoff) && (!(xsect_isOpen(xsect.type) != 0))))
        {
            y = (CrownCutoff * xsect.yFull);
        }
        return xsect_getWofY(xsect, y);
    }

    // dwflow.c:609
    static double dwflow_getArea(TXsect xsect, double y, double wSlot)
    {
        if ((y >= xsect.yFull))
        {
            return (xsect.aFull + (((y - xsect.yFull)) * wSlot));
        }
        return xsect_getAofY(xsect, y);
    }

    // dwflow.c:623
    static double getHydRad(TXsect xsect, double y)
    {
        if ((y >= xsect.yFull))
        {
            return xsect.rFull;
        }
        return xsect_getRofY(xsect, y);
    }

    // dwflow.c:637
    static double checkNormalFlow(int j, double q, double y1, double y2, double a1, double r1)
    {
        int check = 0;
        int k = Link[j].subIndex;
        int n1 = Link[j].node1;
        int n2 = Link[j].node2;
        int hasOutfall = ((((Node[n1].type == OUTFALL) || (Node[n2].type == OUTFALL))) ? 1 : 0);
        double qNorm = 0.0;
        double f1 = 0.0;
        if ((((NormalFlowLtd == SLOPE) || (NormalFlowLtd == BOTH)) || (hasOutfall != 0)))
        {
            if ((y1 < y2))
            {
                check = 1;
            }
        }
        if ((((!(check != 0)) && (((NormalFlowLtd == FROUDE) || (NormalFlowLtd == BOTH)))) && (!(hasOutfall != 0))))
        {
            if (((y1 > 0.0001) && (y2 > 0.0001)))
            {
                f1 = link_getFroude(j, (q / a1), y1);
                if ((f1 >= 1.0))
                {
                    check = 1;
                }
            }
        }
        if ((check != 0))
        {
            qNorm = ((Conduit[k].beta * a1) * Glibm.pow(r1, (2.0 / 3.0)));
            if ((qNorm < q))
            {
                Link[j].normalFlow = 1;
                return qNorm;
            }
        }
        return q;
    }

}
