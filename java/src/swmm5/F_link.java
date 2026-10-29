// Generated from EPA SWMM 5.2.4 (link.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_link {
    static double MIN_DELTA_Z = 0.001;

    // link.c:137
    static int link_readParams(int j, int type, int k, CPtr[] tok, int ntoks)
    {
        switch (type) {
            case CONDUIT:
                return conduit_readParams(j, k, tok, ntoks);
            case PUMP:
                return pump_readParams(j, k, tok, ntoks);
            case ORIFICE:
                return orifice_readParams(j, k, tok, ntoks);
            case WEIR:
                return weir_readParams(j, k, tok, ntoks);
            case OUTLET:
                return outlet_readParams(j, k, tok, ntoks);
            default:
                return 0;
        }
    }

    // link.c:162
    static int link_readXsectParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        double[] x = new double[4];
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(LINK, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        k = findmatch(tok[1], XsectTypeWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        if ((Link[j].type == CONDUIT))
        {
            Conduit[Link[j].subIndex].barrels = 1;
        }
        Link[j].xsect.culvertCode = 0;
        if ((k == IRREGULAR))
        {
            i = project_findObject(TRANSECT, tok[2]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
            Link[j].xsect.type = k;
            Link[j].xsect.transect = i;
            return 0;
        }
        else if ((k == STREET_XSECT))
        {
            i = project_findObject(STREET, tok[2]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
            Link[j].xsect.type = k;
            Link[j].xsect.transect = i;
            return 0;
        }
        else
        {
            if ((ntoks < 6))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if ((k == CUSTOM))
            {
                if (((!(getDouble(tok[2], new DElem(x, 0)) != 0)) || (x[0] <= 0.0)))
                {
                    return error_setInpError(ERR_NUMBER, tok[2]);
                }
                i = project_findObject(CURVE, tok[3]);
                if ((i < 0))
                {
                    return error_setInpError(ERR_NAME, tok[3]);
                }
                Link[j].xsect.type = k;
                Link[j].xsect.transect = i;
                Link[j].xsect.yFull = (x[0] / UCF(LENGTH));
            }
            else
            {
                for (i = 2; (i <= 5); i++)
                {
                    if (!(getDouble(tok[i], new DElem(x, (i - 2))) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[i]);
                    }
                }
            }
            if (((Link[j].type != CONDUIT) && (k == RECT_OPEN)))
            {
                x[2] = 0.0;
                x[3] = 0.0;
            }
            if (!(xsect_setParams(Link[j].xsect, k, x, UCF(LENGTH)) != 0))
            {
                return error_setInpError(ERR_NUMBER, L0);
            }
            if (((Link[j].type == CONDUIT) && (ntoks >= 7)))
            {
                i = R.atoi(tok[6]);
                if ((i <= 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[6]);
                }
                else
                {
                    Conduit[Link[j].subIndex].barrels = ((byte) (i));
                }
            }
            if (((Link[j].type == CONDUIT) && (ntoks >= 8)))
            {
                i = R.atoi(tok[7]);
                if ((i < 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[7]);
                }
                else
                {
                    Link[j].xsect.culvertCode = i;
                }
            }
        }
        return 0;
    }

    // link.c:271
    static int link_readLossParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        double[] x = new double[3];
        DBox seepRate = new DBox(0.0);
        if ((ntoks < 4))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(LINK, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        for (i = 1; (i <= 3); i++)
        {
            if (((!(getDouble(tok[i], new DElem(x, (i - 1))) != 0)) || (x[(i - 1)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[i]);
            }
        }
        k = 0;
        if ((ntoks >= 5))
        {
            k = findmatch(tok[4], NoYesWords);
            if ((k < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[4]);
            }
        }
        if ((ntoks >= 6))
        {
            if (!(getDouble(tok[5], seepRate) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[5]);
            }
        }
        Link[j].cLossInlet = x[0];
        Link[j].cLossOutlet = x[1];
        Link[j].cLossAvg = x[2];
        Link[j].hasFlapGate = k;
        Link[j].seepRate = (seepRate.v / UCF(RAINFALL));
        return 0;
    }

    // link.c:315
    static void link_setParams(int j, int type, int n1, int n2, int k, double[] x)
    {
        Link[j].node1 = n1;
        Link[j].node2 = n2;
        Link[j].type = type;
        Link[j].subIndex = k;
        Link[j].offset1 = 0.0;
        Link[j].offset2 = 0.0;
        Link[j].q0 = 0.0;
        Link[j].qFull = 0.0;
        Link[j].setting = 1.0;
        Link[j].targetSetting = 1.0;
        Link[j].hasFlapGate = 0;
        Link[j].qLimit = 0.0;
        Link[j].direction = 1;
        switch (type) {
            case CONDUIT:
                Conduit[k].length = (x[0] / UCF(LENGTH));
                Conduit[k].modLength = Conduit[k].length;
                Conduit[k].roughness = x[1];
                Link[j].offset1 = (x[2] / UCF(LENGTH));
                Link[j].offset2 = (x[3] / UCF(LENGTH));
                Link[j].q0 = (x[4] / UCF(FLOW));
                Link[j].qLimit = (x[5] / UCF(FLOW));
                break;
            case PUMP:
                Pump[k].pumpCurve = R.D2I(x[0]);
                Link[j].hasFlapGate = 0;
                Pump[k].initSetting = x[1];
                Pump[k].yOn = (x[2] / UCF(LENGTH));
                Pump[k].yOff = (x[3] / UCF(LENGTH));
                Pump[k].xMin = 0.0;
                Pump[k].xMax = 0.0;
                break;
            case ORIFICE:
                Orifice[k].type = R.D2I(x[0]);
                Link[j].offset1 = (x[1] / UCF(LENGTH));
                Link[j].offset2 = Link[j].offset1;
                Orifice[k].cDisch = x[2];
                Link[j].hasFlapGate = (((x[3] > 0.0)) ? 1 : 0);
                Orifice[k].orate = (x[4] * 3600.0);
                break;
            case WEIR:
                Weir[k].type = R.D2I(x[0]);
                Link[j].offset1 = (x[1] / UCF(LENGTH));
                Link[j].offset2 = Link[j].offset1;
                Weir[k].cDisch1 = x[2];
                Link[j].hasFlapGate = (((x[3] > 0.0)) ? 1 : 0);
                Weir[k].endCon = x[4];
                Weir[k].cDisch2 = x[5];
                Weir[k].canSurcharge = R.D2I(x[6]);
                Weir[k].roadWidth = (x[7] / UCF(LENGTH));
                Weir[k].roadSurface = R.D2I(x[8]);
                Weir[k].cdCurve = R.D2I(x[9]);
                break;
            case OUTLET:
                Link[j].offset1 = (x[0] / UCF(LENGTH));
                Link[j].offset2 = Link[j].offset1;
                Outlet[k].qCoeff = x[1];
                Outlet[k].qExpon = x[2];
                Outlet[k].qCurve = R.D2I(x[3]);
                Link[j].hasFlapGate = (((x[4] > 0.0)) ? 1 : 0);
                Outlet[k].curveType = R.D2I(x[5]);
                xsect_setParams(Link[j].xsect, DUMMY, (null), 0.0);
                break;
        }
    }

    // link.c:403
    static void link_validate(int j)
    {
        int n = 0;
        if ((LinkOffsets == ELEV_OFFSET))
        {
            link_convertOffsets(j);
        }
        switch (Link[j].type) {
            case CONDUIT:
                conduit_validate(j, Link[j].subIndex);
                break;
            case PUMP:
                pump_validate(j, Link[j].subIndex);
                break;
            case ORIFICE:
                orifice_validate(j, Link[j].subIndex);
                break;
            case WEIR:
                weir_validate(j, Link[j].subIndex);
                break;
        }
        switch (Link[j].type) {
            case ORIFICE:
            case WEIR:
            case OUTLET:
                if (((Node[Link[j].node1].invertElev + Link[j].offset1) < Node[Link[j].node2].invertElev))
                {
                    if ((RouteModel == DW))
                    {
                        Link[j].offset1 = (Node[Link[j].node2].invertElev - Node[Link[j].node1].invertElev);
                        report_writeWarningMsg(L305, Link[j].ID);
                    }
                    else
                    {
                        report_writeWarningMsg(L306, Link[j].ID);
                    }
                }
        }
        if (((Link[j].type == PUMP) || (((Link[j].type == ORIFICE) && (Orifice[Link[j].subIndex].type == BOTTOM_ORIFICE)))))
        {
            return;
        }
        n = Link[j].node1;
        if (((Node[n].type != STORAGE) || (Node[n].surDepth > 0.0)))
        {
            Node[n].fullDepth = (((((Node[n].fullDepth) >= ((Link[j].offset1 + Link[j].xsect.yFull)))) ? (Node[n].fullDepth) : ((Link[j].offset1 + Link[j].xsect.yFull))));
        }
        n = Link[j].node2;
        if (((((Node[n].type != STORAGE) || (Node[n].surDepth > 0.0))) && (Link[j].type == CONDUIT)))
        {
            Node[n].fullDepth = (((((Node[n].fullDepth) >= ((Link[j].offset2 + Link[j].xsect.yFull)))) ? (Node[n].fullDepth) : ((Link[j].offset2 + Link[j].xsect.yFull))));
        }
    }

    // link.c:468
    static void link_convertOffsets(int j)
    {
        double elev = 0.0;
        elev = Node[Link[j].node1].invertElev;
        Link[j].offset1 = link_getOffsetHeight(j, Link[j].offset1, elev);
        if ((Link[j].type == CONDUIT))
        {
            elev = Node[Link[j].node2].invertElev;
            Link[j].offset2 = link_getOffsetHeight(j, Link[j].offset2, elev);
        }
        else
        {
            Link[j].offset2 = Link[j].offset1;
        }
    }

    // link.c:489
    static double link_getOffsetHeight(int j, double offset, double elev)
    {
        if (((offset <= (-10000000000.0)) || (Link[j].type == PUMP)))
        {
            return 0.0;
        }
        offset -= elev;
        if ((offset >= 0.0))
        {
            return offset;
        }
        if ((offset >= (-MIN_DELTA_Z)))
        {
            return 0.0;
        }
        report_writeWarningMsg(L307, Link[j].ID);
        return 0.0;
    }

    // link.c:508
    static void link_initState(int j)
    {
        int p = 0;
        Link[j].oldFlow = Link[j].q0;
        Link[j].newFlow = Link[j].q0;
        Link[j].oldDepth = 0.0;
        Link[j].newDepth = 0.0;
        Link[j].oldVolume = 0.0;
        Link[j].newVolume = 0.0;
        Link[j].setting = 1.0;
        Link[j].targetSetting = 1.0;
        Link[j].timeLastSet = StartDate.v;
        Link[j].inletControl = 0;
        Link[j].normalFlow = 0;
        if ((Link[j].type == CONDUIT))
        {
            conduit_initState(j, Link[j].subIndex);
        }
        if ((Link[j].type == PUMP))
        {
            pump_initState(j, Link[j].subIndex);
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            Link[j].oldQual[p] = 0.0;
            Link[j].newQual[p] = 0.0;
            Link[j].totalLoad[p] = 0.0;
        }
    }

    // link.c:543
    static double link_getInflow(int j)
    {
        if ((Link[j].setting == 0))
        {
            return 0.0;
        }
        switch (Link[j].type) {
            case CONDUIT:
                return conduit_getInflow(j);
            case PUMP:
                return pump_getInflow(j);
            case ORIFICE:
                return orifice_getInflow(j);
            case WEIR:
                return weir_getInflow(j);
            case OUTLET:
                return outlet_getInflow(j);
            default:
                return node_getOutflow(Link[j].node1, j);
        }
    }

    // link.c:564
    static void link_setOldHydState(int j)
    {
        int k = 0;
        Link[j].oldDepth = Link[j].newDepth;
        Link[j].oldFlow = Link[j].newFlow;
        Link[j].oldVolume = Link[j].newVolume;
        if ((Link[j].type == CONDUIT))
        {
            k = Link[j].subIndex;
            Conduit[k].q1Old = Conduit[k].q1;
            Conduit[k].q2Old = Conduit[k].q2;
        }
    }

    // link.c:587
    static void link_setOldQualState(int j)
    {
        int p = 0;
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            Link[j].oldQual[p] = Link[j].newQual[p];
            Link[j].newQual[p] = 0.0;
        }
    }

    // link.c:604
    static void link_setTargetSetting(int j)
    {
        int k = 0;
        int n1 = 0;
        if ((Link[j].type == PUMP))
        {
            k = Link[j].subIndex;
            n1 = Link[j].node1;
            Link[j].targetSetting = Link[j].setting;
            if ((((Pump[k].yOff > 0.0) && (Link[j].setting > 0.0)) && (Node[n1].newDepth < Pump[k].yOff)))
            {
                Link[j].targetSetting = 0.0;
            }
            if ((((Pump[k].yOn > 0.0) && (Link[j].setting == 0.0)) && (Node[n1].newDepth > Pump[k].yOn)))
            {
                Link[j].targetSetting = 1.0;
            }
        }
    }

    // link.c:628
    static void link_setSetting(int j, double tstep)
    {
        if ((Link[j].type == ORIFICE))
        {
            orifice_setSetting(j, tstep);
        }
        else if ((Link[j].type == WEIR))
        {
            weir_setSetting(j);
        }
        else
        {
            Link[j].setting = Link[j].targetSetting;
        }
    }

    // link.c:643
    static int link_setFlapGate(int j, int n1, int n2, double q)
    {
        int n = (-1);
        if ((Link[j].hasFlapGate != 0))
        {
            if (((q * ((double) (Link[j].direction))) < 0.0))
            {
                return 1;
            }
        }
        if ((q < 0.0))
        {
            n = n2;
        }
        if ((q > 0.0))
        {
            n = n1;
        }
        if ((((n >= 0) && (Node[n].type == OUTFALL)) && (Outfall[Node[n].subIndex].hasFlapGate != 0)))
        {
            return 1;
        }
        return 0;
    }

    // link.c:674
    static void link_getResults(int j, double f, float[] x)
    {
        int p = 0;
        double y = 0.0;
        double q = 0.0;
        double u = 0.0;
        double v = 0.0;
        double c = 0.0;
        double f1 = (1.0 - f);
        y = ((f1 * Link[j].oldDepth) + (f * Link[j].newDepth));
        q = ((f1 * Link[j].oldFlow) + (f * Link[j].newFlow));
        v = ((f1 * Link[j].oldVolume) + (f * Link[j].newVolume));
        u = link_getVelocity(j, q, y);
        c = 0.0;
        if ((Link[j].type == CONDUIT))
        {
            if ((Link[j].xsect.type != DUMMY))
            {
                c = (xsect_getAofY(Link[j].xsect, y) / Link[j].xsect.aFull);
            }
        }
        else
        {
            c = Link[j].setting;
        }
        if (((Link[j].type == PUMP) && ((Link[j].oldFlow * Link[j].newFlow) == 0.0)))
        {
            if ((f >= f1))
            {
                q = Link[j].newFlow;
            }
            else
            {
                q = Link[j].oldFlow;
            }
        }
        y *= (UCF(LENGTH));
        v *= (UCF(VOLUME));
        q *= (UCF(FLOW) * ((double) (Link[j].direction)));
        u *= (UCF(LENGTH) * ((double) (Link[j].direction)));
        x[LINK_DEPTH] = ((float) (y));
        x[LINK_FLOW] = ((float) (q));
        x[LINK_VELOCITY] = ((float) (u));
        x[LINK_VOLUME] = ((float) (v));
        x[LINK_CAPACITY] = ((float) (c));
        if (!(IgnoreQuality != 0))
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                c = ((f1 * Link[j].oldQual[p]) + (f * Link[j].newQual[p]));
                x[(LINK_QUAL + p)] = ((float) (c));
            }
        }
    }

    // link.c:728
    static void link_setOutfallDepth(int j)
    {
        int k = 0;
        int n = 0;
        double z = 0.0;
        double q = 0.0;
        double yCrit = 0.0;
        double yNorm = 0.0;
        if ((Node[Link[j].node2].type == OUTFALL))
        {
            n = Link[j].node2;
            z = Link[j].offset2;
        }
        else if ((Node[Link[j].node1].type == OUTFALL))
        {
            n = Link[j].node1;
            z = Link[j].offset1;
        }
        else
        {
            return;
        }
        if ((Link[j].type == CONDUIT))
        {
            k = Link[j].subIndex;
            q = Math.abs((Link[j].newFlow / Conduit[k].barrels));
            yNorm = link_getYnorm(j, q);
            yCrit = link_getYcrit(j, q);
        }
        node_setOutletDepth(n, yNorm, yCrit, z);
    }

    // link.c:770
    static double link_getYcrit(int j, double q)
    {
        return xsect_getYcrit(Link[j].xsect, q);
    }

    // link.c:783
    static double link_getYnorm(int j, double q)
    {
        int k = 0;
        double s = 0.0;
        double a = 0.0;
        double y = 0.0;
        if ((Link[j].type != CONDUIT))
        {
            return 0.0;
        }
        if ((Link[j].xsect.type == DUMMY))
        {
            return 0.0;
        }
        q = Math.abs(q);
        k = Link[j].subIndex;
        if ((q > Conduit[k].qMax))
        {
            q = Conduit[k].qMax;
        }
        if ((q <= 0.0))
        {
            return 0.0;
        }
        s = (q / Conduit[k].beta);
        a = xsect_getAofS(Link[j].xsect, s);
        y = xsect_getYofA(Link[j].xsect, a);
        return y;
    }

    // link.c:808
    static double link_getLength(int j)
    {
        if ((Link[j].type == CONDUIT))
        {
            return conduit_getLength(j);
        }
        return 0.0;
    }

    // link.c:821
    static double link_getVelocity(int j, double flow, double depth)
    {
        double area = 0.0;
        double veloc = 0.0;
        int k = 0;
        if ((depth <= 0.01))
        {
            return 0.0;
        }
        if ((Link[j].type == CONDUIT))
        {
            k = Link[j].subIndex;
            flow /= Conduit[k].barrels;
            area = xsect_getAofY(Link[j].xsect, depth);
            if ((area > 0.0001))
            {
                veloc = (flow / area);
            }
        }
        return veloc;
    }

    // link.c:847
    static double link_getFroude(int j, double v, double y)
    {
        TXsect xsect = Link[j].xsect;
        if ((Link[j].type != CONDUIT))
        {
            return 0.0;
        }
        if ((y <= 0.0001))
        {
            return 0.0;
        }
        if (((!(xsect_isOpen(xsect.type) != 0)) && ((xsect.yFull - y) <= 0.0001)))
        {
            return 0.0;
        }
        y = (xsect_getAofY(xsect, y) / xsect_getWofY(xsect, y));
        return (Math.abs(v) / Math.sqrt((32.2 * y)));
    }

    // link.c:875
    static double link_getPower(int j)
    {
        int n1 = Link[j].node1;
        int n2 = Link[j].node2;
        double dh = (((Node[n1].invertElev + Node[n1].newDepth)) - ((Node[n2].invertElev + Node[n2].newDepth)));
        double q = Math.abs(Link[j].newFlow);
        return (((Math.abs(dh) * q) / 8.814) * 0.7457);
    }

    // link.c:893
    static double link_getLossRate(int j, int routeModel, double q, double tstep)
    {
        if ((Link[j].type == CONDUIT))
        {
            return conduit_getLossRate(j, routeModel, q, tstep);
        }
        else
        {
            return 0.0;
        }
    }

    // link.c:911
    static int link_getFullState(double a1, double a2, double aFull)
    {
        if ((a1 >= aFull))
        {
            if ((a2 >= aFull))
            {
                return ALL_FULL;
            }
            else
            {
                return UP_FULL;
            }
        }
        if ((a2 >= aFull))
        {
            return DN_FULL;
        }
        return 0;
    }

    // link.c:933
    static int conduit_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int n1 = 0;
        int n2 = 0;
        double[] x = new double[6];
        CPtr id = null;
        if ((ntoks < 7))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(LINK, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        n1 = project_findObject(NODE, tok[1]);
        if ((n1 < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        n2 = project_findObject(NODE, tok[2]);
        if ((n2 < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        if (!(getDouble(tok[3], new DElem(x, 0)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[3]);
        }
        if (!(getDouble(tok[4], new DElem(x, 1)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[4]);
        }
        if (((LinkOffsets == ELEV_OFFSET) && (tok[5].g(0) == 42)))
        {
            x[2] = (-10000000000.0);
        }
        else if (!(getDouble(tok[5], new DElem(x, 2)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[5]);
        }
        if (((LinkOffsets == ELEV_OFFSET) && (tok[6].g(0) == 42)))
        {
            x[3] = (-10000000000.0);
        }
        else if (!(getDouble(tok[6], new DElem(x, 3)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[6]);
        }
        x[4] = 0.0;
        if ((ntoks >= 8))
        {
            if (!(getDouble(tok[7], new DElem(x, 4)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[7]);
            }
        }
        x[5] = 0.0;
        if ((ntoks >= 9))
        {
            if (!(getDouble(tok[8], new DElem(x, 5)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[8]);
            }
        }
        Link[j].ID = id;
        link_setParams(j, CONDUIT, n1, n2, k, x);
        return 0;
    }

    // link.c:992
    static void conduit_validate(int j, int k)
    {
        double aa = 0.0;
        double lengthFactor = 0.0;
        double roughness = 0.0;
        double slope = 0.0;
        if (((Link[j].xsect.type == DUMMY) && (RouteModel == DW)))
        {
            if ((Node[Link[j].node1].type == STORAGE))
            {
                report_writeErrorMsg(ERR_DUMMY_LINK, Node[Link[j].node1].ID);
                return;
            }
        }
        if ((Link[j].xsect.type == CUSTOM))
        {
            xsect_setCustomXsectParams(Link[j].xsect);
        }
        if ((Link[j].xsect.type == IRREGULAR))
        {
            xsect_setIrregXsectParams(Link[j].xsect);
            Conduit[k].roughness = Transect[Link[j].xsect.transect].roughness;
        }
        if ((Link[j].xsect.type == STREET_XSECT))
        {
            xsect_setStreetXsectParams(Link[j].xsect);
            Conduit[k].roughness = Street[Link[j].xsect.transect].roughness;
        }
        if ((Link[j].xsect.type == FORCE_MAIN))
        {
            if ((ForceMainEqn == D_W))
            {
                Link[j].xsect.rBot /= (UCF(RAINDEPTH));
            }
            if ((Link[j].xsect.rBot <= 0.0))
            {
                report_writeErrorMsg(ERR_XSECT, Link[j].ID);
            }
        }
        if ((Conduit[k].length <= 0.0))
        {
            report_writeErrorMsg(ERR_LENGTH, Link[j].ID);
        }
        if ((Conduit[k].roughness <= 0.0))
        {
            report_writeErrorMsg(ERR_ROUGHNESS, Link[j].ID);
        }
        if ((Conduit[k].barrels <= 0))
        {
            report_writeErrorMsg(ERR_BARRELS, Link[j].ID);
        }
        if ((Link[j].xsect.type != DUMMY))
        {
            if ((Link[j].xsect.type < 0))
            {
                report_writeErrorMsg(ERR_NO_XSECT, Link[j].ID);
            }
            else if ((Link[j].xsect.aFull <= 0.0))
            {
                report_writeErrorMsg(ERR_XSECT, Link[j].ID);
            }
        }
        if ((ErrorCode != 0))
        {
            return;
        }
        if ((Link[j].offset1 < 0.0))
        {
            report_writeWarningMsg(L307, Link[j].ID);
            Link[j].offset1 = 0.0;
        }
        if ((Link[j].offset2 < 0.0))
        {
            report_writeWarningMsg(L307, Link[j].ID);
            Link[j].offset2 = 0.0;
        }
        if ((Link[j].xsect.type == FILLED_CIRCULAR))
        {
            Link[j].offset1 += Link[j].xsect.yBot;
            Link[j].offset2 += Link[j].xsect.yBot;
        }
        slope = conduit_getSlope(j);
        Conduit[k].slope = slope;
        if ((((RouteModel == DW) && (slope < 0.0)) && (Link[j].xsect.type != DUMMY)))
        {
            conduit_reverse(j, k);
        }
        roughness = Conduit[k].roughness;
        if (((RouteModel == DW) && (Link[j].xsect.type == FORCE_MAIN)))
        {
            roughness = forcemain_getEquivN(j, k);
        }
        if ((Link[j].xsect.type == IRREGULAR))
        {
            lengthFactor = Transect[Link[j].xsect.transect].lengthFactor;
            roughness *= (Math.sqrt(lengthFactor));
        }
        lengthFactor = 1.0;
        if ((((RouteModel == DW) && (LengtheningStep > 0.0)) && (Link[j].xsect.type != DUMMY)))
        {
            lengthFactor = conduit_getLengthFactor(j, k, roughness);
        }
        if ((lengthFactor != 1.0))
        {
            Conduit[k].modLength = (lengthFactor * conduit_getLength(j));
            slope /= lengthFactor;
            roughness = (roughness / Math.sqrt(lengthFactor));
        }
        if (((RouteModel == DW) && (Link[j].xsect.type == FORCE_MAIN)))
        {
            Link[j].xsect.sBot = forcemain_getRoughFactor(j, lengthFactor);
        }
        Conduit[k].roughFactor = (32.2 * ((((roughness / 1.486)) * ((roughness / 1.486)))));
        if ((Link[j].xsect.type == DUMMY))
        {
            Conduit[k].beta = 0.0;
        }
        else
        {
            Conduit[k].beta = ((1.486 * Math.sqrt(Math.abs(slope))) / roughness);
        }
        Link[j].qFull = (Link[j].xsect.sFull * Conduit[k].beta);
        Conduit[k].qMax = (Link[j].xsect.sMax * Conduit[k].beta);
        aa = (((Conduit[k].beta / Math.sqrt(32.2)) * Glibm.pow(Link[j].xsect.yFull, 0.1666667)) * 0.3);
        if ((aa >= 1.0))
        {
            Conduit[k].superCritical = 1;
        }
        else
        {
            Conduit[k].superCritical = 0;
        }
        if ((((Link[j].cLossInlet == 0.0) && (Link[j].cLossOutlet == 0.0)) && (Link[j].cLossAvg == 0.0)))
        {
            Conduit[k].hasLosses = 0;
        }
        else
        {
            Conduit[k].hasLosses = 1;
        }
    }

    // link.c:1158
    static void conduit_reverse(int j, int k)
    {
        int i = 0;
        double z = 0.0;
        double cLoss = 0.0;
        i = Link[j].node1;
        Link[j].node1 = Link[j].node2;
        Link[j].node2 = i;
        z = Link[j].offset1;
        Link[j].offset1 = Link[j].offset2;
        Link[j].offset2 = z;
        cLoss = Link[j].cLossInlet;
        Link[j].cLossInlet = Link[j].cLossOutlet;
        Link[j].cLossOutlet = cLoss;
        Conduit[k].slope = (-Conduit[k].slope);
        Link[j].direction *= ((byte) ((-1)));
        Link[j].q0 = (-Link[j].q0);
    }

    // link.c:1195
    static double conduit_getLength(int j)
    {
        int k = Link[j].subIndex;
        int t = 0;
        if ((Link[j].xsect.type != IRREGULAR))
        {
            return Conduit[k].length;
        }
        t = Link[j].xsect.transect;
        if (((t < 0) || (t >= Nobjects[TRANSECT])))
        {
            return Conduit[k].length;
        }
        return (Conduit[k].length / Transect[t].lengthFactor);
    }

    // link.c:1217
    static double conduit_getLengthFactor(int j, int k, double roughness)
    {
        double ratio = 0.0;
        double yFull = 0.0;
        double vFull = 0.0;
        double tStep = 0.0;
        yFull = Link[j].xsect.yFull;
        if ((xsect_isOpen(Link[j].xsect.type) != 0))
        {
            yFull = (Link[j].xsect.aFull / xsect_getWofY(Link[j].xsect, yFull));
        }
        vFull = ((((1.486 / roughness) * Link[j].xsect.sFull) * Math.sqrt(Math.abs(Conduit[k].slope))) / Link[j].xsect.aFull);
        if ((LengtheningStep == 0.0))
        {
            tStep = RouteStep;
        }
        else
        {
            tStep = (((((RouteStep) <= (LengtheningStep))) ? (RouteStep) : (LengtheningStep)));
        }
        ratio = ((((Math.sqrt((32.2 * yFull)) + vFull)) * tStep) / conduit_getLength(j));
        if ((ratio > 1.0))
        {
            return ratio;
        }
        else
        {
            return 1.0;
        }
    }

    // link.c:1258
    static double conduit_getSlope(int j)
    {
        double elev1 = 0.0;
        double elev2 = 0.0;
        double delta = 0.0;
        double slope = 0.0;
        double length = conduit_getLength(j);
        elev1 = (Link[j].offset1 + Node[Link[j].node1].invertElev);
        elev2 = (Link[j].offset2 + Node[Link[j].node2].invertElev);
        delta = Math.abs((elev1 - elev2));
        if ((delta < MIN_DELTA_Z))
        {
            report_writeWarningMsg(L308, Link[j].ID);
            delta = MIN_DELTA_Z;
        }
        if ((delta >= length))
        {
            report_writeWarningMsg(L309, Link[j].ID);
            slope = (delta / length);
        }
        else
        {
            slope = (delta / Math.sqrt(((((length) * (length))) - (((delta) * (delta))))));
        }
        if (((MinSlope.v > 0.0) && (slope < MinSlope.v)))
        {
            report_writeWarningMsg(L310, Link[j].ID);
            slope = MinSlope.v;
            if (((RouteModel == SF) || (RouteModel == KW)))
            {
                return slope;
            }
        }
        if ((elev1 < elev2))
        {
            slope = (-slope);
        }
        return slope;
    }

    // link.c:1304
    static void conduit_initState(int j, int k)
    {
        Link[j].newDepth = link_getYnorm(j, (Link[j].q0 / Conduit[k].barrels));
        Link[j].oldDepth = Link[j].newDepth;
        Conduit[k].evapLossRate = 0.0;
        Conduit[k].seepLossRate = 0.0;
    }

    // link.c:1320
    static double conduit_getInflow(int j)
    {
        double qIn = node_getOutflow(Link[j].node1, j);
        if ((Link[j].qLimit > 0.0))
        {
            qIn = (((((qIn) <= (Link[j].qLimit))) ? (qIn) : (Link[j].qLimit)));
        }
        return qIn;
    }

    // link.c:1334
    static double conduit_getLossRate(int j, int routeModel, double q, double tstep)
    {
        TXsect xsect = null;
        double depth = (0.5 * ((Link[j].oldDepth + Link[j].newDepth)));
        double length = 0.0;
        double width = 0.0;
        double topWidth = 0.0;
        double evapLossRate = 0.0;
        double seepLossRate = 0.0;
        double totalLossRate = 0.0;
        if ((depth > 0.0001))
        {
            xsect = Link[j].xsect;
            length = conduit_getLength(j);
            if (((xsect_isOpen(xsect.type) != 0) && (Evap.rate > 0.0)))
            {
                topWidth = xsect_getWofY(xsect, depth);
                evapLossRate = ((topWidth * length) * Evap.rate);
            }
            if ((Link[j].seepRate > 0.0))
            {
                if ((xsect.type == RECT_CLOSED))
                {
                    width = xsect.wMax;
                }
                else
                {
                    if ((depth >= xsect.ywMax))
                    {
                        depth = xsect.ywMax;
                    }
                    width = xsect_getWofY(xsect, depth);
                }
                seepLossRate = ((Link[j].seepRate * width) * length);
                seepLossRate *= Adjust.hydconFactor;
            }
            totalLossRate = (evapLossRate + seepLossRate);
            if ((routeModel == DW))
            {
                q = (Link[j].newVolume / tstep);
            }
            else
            {
                q = (((((q) < 0)) ? (-(q)) : (q)));
            }
            if ((totalLossRate > q))
            {
                evapLossRate = ((evapLossRate * q) / totalLossRate);
                seepLossRate = ((seepLossRate * q) / totalLossRate);
                totalLossRate = q;
            }
        }
        Conduit[Link[j].subIndex].evapLossRate = evapLossRate;
        Conduit[Link[j].subIndex].seepLossRate = seepLossRate;
        return totalLossRate;
    }

    // link.c:1406
    static int pump_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int m = 0;
        int n1 = 0;
        int n2 = 0;
        double[] x = new double[4];
        CPtr id = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(LINK, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        n1 = project_findObject(NODE, tok[1]);
        if ((n1 < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        n2 = project_findObject(NODE, tok[2]);
        if ((n2 < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        x[0] = (-1.0);
        if ((ntoks >= 4))
        {
            if (!(strcomp(tok[3], L275) != 0))
            {
                m = project_findObject(CURVE, tok[3]);
                if ((m < 0))
                {
                    return error_setInpError(ERR_NAME, tok[3]);
                }
                x[0] = m;
            }
        }
        x[1] = 1.0;
        if ((ntoks >= 5))
        {
            m = findmatch(tok[4], OffOnWords);
            if ((m < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[4]);
            }
            x[1] = m;
        }
        x[2] = 0.0;
        if ((ntoks >= 6))
        {
            if (((!(getDouble(tok[5], new DElem(x, 2)) != 0)) || (x[2] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[5]);
            }
        }
        x[3] = 0.0;
        if ((ntoks >= 7))
        {
            if (((!(getDouble(tok[6], new DElem(x, 3)) != 0)) || (x[3] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[6]);
            }
        }
        Link[j].ID = id;
        link_setParams(j, PUMP, n1, n2, k, x);
        return 0;
    }

    // link.c:1473
    static void pump_validate(int j, int k)
    {
        int m = 0;
        int n1 = 0;
        DBox x = new DBox(0.0);
        DBox y = new DBox(0.0);
        Link[j].xsect.yFull = 0.0;
        m = Pump[k].pumpCurve;
        if ((m < 0))
        {
            Pump[k].type = IDEAL_PUMP;
        }
        else
        {
            if (((Curve[m].curveType < PUMP1_CURVE) || (Curve[m].curveType > PUMP5_CURVE)))
            {
                report_writeErrorMsg(ERR_NO_CURVE, Link[j].ID);
            }
            else
            {
                Pump[k].type = (Curve[m].curveType - PUMP1_CURVE);
                if ((table_getFirstEntry(Curve[m], x, y) != 0))
                {
                    Link[j].qFull = y.v;
                    Pump[k].xMin = x.v;
                    Pump[k].xMax = x.v;
                    while ((table_getNextEntry(Curve[m], x, y) != 0))
                    {
                        Link[j].qFull = (((((y.v) >= (Link[j].qFull))) ? (y.v) : (Link[j].qFull)));
                        Pump[k].xMax = x.v;
                    }
                }
                Link[j].qFull /= (UCF(FLOW));
            }
        }
        if (((Pump[k].yOn > 0.0) && (Pump[k].yOn <= Pump[k].yOff)))
        {
            report_writeErrorMsg(ERR_PUMP_LIMITS, Link[j].ID);
        }
        if ((Pump[k].type == TYPE1_PUMP))
        {
            n1 = Link[j].node1;
            if ((Node[n1].type != STORAGE))
            {
                Node[n1].fullVolume = (((((Node[n1].fullVolume) >= ((Pump[k].xMax / UCF(VOLUME))))) ? (Node[n1].fullVolume) : ((Pump[k].xMax / UCF(VOLUME)))));
            }
        }
    }

    // link.c:1534
    static void pump_initState(int j, int k)
    {
        Link[j].setting = Pump[k].initSetting;
        Link[j].targetSetting = Pump[k].initSetting;
    }

    // link.c:1548
    static double pump_getInflow(int j)
    {
        int k = 0;
        int m = 0;
        int n1 = 0;
        int n2 = 0;
        double vol = 0.0;
        double depth = 0.0;
        double head = 0.0;
        double qIn = 0.0;
        double qIn1 = 0.0;
        double dh = 0.001;
        double s = 1.0;
        k = Link[j].subIndex;
        m = Pump[k].pumpCurve;
        n1 = Link[j].node1;
        n2 = Link[j].node2;
        Link[j].flowClass = NO;
        Link[j].setting = Link[j].targetSetting;
        if ((Link[j].setting == 0.0))
        {
            return 0.0;
        }
        if ((Pump[k].type == IDEAL_PUMP))
        {
            qIn = (Node[n1].inflow + Node[n1].overflow);
        }
        else
        {
            switch (Curve[m].curveType) {
                case PUMP1_CURVE:
                    vol = (Node[n1].newVolume * UCF(VOLUME));
                    qIn = (table_intervalLookup(Curve[m], vol) / UCF(FLOW));
                    if (((vol < Pump[k].xMin) || (vol > Pump[k].xMax)))
                    {
                        Link[j].flowClass = YES;
                    }
                    break;
                case PUMP2_CURVE:
                    depth = (Node[n1].newDepth * UCF(LENGTH));
                    qIn = (table_intervalLookup(Curve[m], depth) / UCF(FLOW));
                    if (((depth < Pump[k].xMin) || (depth > Pump[k].xMax)))
                    {
                        Link[j].flowClass = YES;
                    }
                    break;
                case PUMP3_CURVE:
                case PUMP5_CURVE:
                    if ((Curve[m].curveType == PUMP5_CURVE))
                    {
                        s = Link[j].setting;
                    }
                    head = ((((((Node[n2].newDepth + Node[n2].invertElev)) - ((Node[n1].newDepth + Node[n1].invertElev)))) / s) / s);
                    head = ((((((head) >= (0.0))) ? (head) : (0.0))) * UCF(LENGTH));
                    qIn = (table_lookup(Curve[m], head) / UCF(FLOW));
                    Link[j].dqdh = ((((-(table_getSlope(Curve[m], head))) * UCF(LENGTH)) / UCF(FLOW)) / s);
                    if (((head < Pump[k].xMin) || (head > Pump[k].xMax)))
                    {
                        Link[j].flowClass = YES;
                    }
                    break;
                case PUMP4_CURVE:
                    depth = Node[n1].newDepth;
                    qIn = (table_lookup(Curve[m], (depth * UCF(LENGTH))) / UCF(FLOW));
                    qIn1 = (table_lookup(Curve[m], (((depth + dh)) * UCF(LENGTH))) / UCF(FLOW));
                    Link[j].dqdh = (((qIn1 - qIn)) / dh);
                    depth *= (UCF(LENGTH));
                    if ((depth < Pump[k].xMin))
                    {
                        Link[j].flowClass = DN_DRY;
                    }
                    if ((depth > Pump[k].xMax))
                    {
                        Link[j].flowClass = UP_DRY;
                    }
                    break;
                default:
                    qIn = 0.0;
            }
        }
        if ((qIn < 0.0))
        {
            qIn = 0.0;
        }
        return (qIn * Link[j].setting);
    }

    // link.c:1641
    static int orifice_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int m = 0;
        int n1 = 0;
        int n2 = 0;
        double[] x = new double[5];
        CPtr id = null;
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(LINK, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        n1 = project_findObject(NODE, tok[1]);
        if ((n1 < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        n2 = project_findObject(NODE, tok[2]);
        if ((n2 < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        m = findmatch(tok[3], OrificeTypeWords);
        if ((m < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[3]);
        }
        x[0] = m;
        if (((LinkOffsets == ELEV_OFFSET) && (tok[4].g(0) == 42)))
        {
            x[1] = (-10000000000.0);
        }
        else if (!(getDouble(tok[4], new DElem(x, 1)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[4]);
        }
        if (((!(getDouble(tok[5], new DElem(x, 2)) != 0)) || (x[2] < 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[5]);
        }
        x[3] = 0.0;
        if ((ntoks >= 7))
        {
            m = findmatch(tok[6], NoYesWords);
            if ((m < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[6]);
            }
            x[3] = m;
        }
        x[4] = 0.0;
        if ((ntoks >= 8))
        {
            if (((!(getDouble(tok[7], new DElem(x, 4)) != 0)) || (x[4] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[7]);
            }
        }
        Link[j].ID = id;
        link_setParams(j, ORIFICE, n1, n2, k, x);
        return 0;
    }

    // link.c:1696
    static void orifice_validate(int j, int k)
    {
        int err = 0;
        if (((Link[j].xsect.type != RECT_CLOSED) && (Link[j].xsect.type != CIRCULAR)))
        {
            err = ERR_REGULATOR_SHAPE;
        }
        if ((err > 0))
        {
            report_writeErrorMsg(err, Link[j].ID);
            return;
        }
        if ((Link[j].offset1 < 0.0))
        {
            Link[j].offset1 = 0.0;
        }
        orifice_setSetting(j, 0.0);
        Orifice[k].length = ((2.0 * RouteStep) * Math.sqrt((32.2 * Link[j].xsect.yFull)));
        Orifice[k].length = (((((200.0) >= (Orifice[k].length))) ? (200.0) : (Orifice[k].length)));
        Orifice[k].surfArea = 0.0;
    }

    // link.c:1729
    static void orifice_setSetting(int j, double tstep)
    {
        int k = Link[j].subIndex;
        double delta = 0.0;
        double step = 0.0;
        double h = 0.0;
        double f = 0.0;
        if (((Orifice[k].orate == 0.0) || (tstep == 0.0)))
        {
            Link[j].setting = Link[j].targetSetting;
        }
        else
        {
            delta = (Link[j].targetSetting - Link[j].setting);
            step = (tstep / Orifice[k].orate);
            if (((step + 0.001) >= Math.abs(delta)))
            {
                Link[j].setting = Link[j].targetSetting;
            }
            else
            {
                Link[j].setting += ((((((delta) < 0)) ? ((-1)) : (1))) * step);
            }
        }
        h = (Link[j].setting * Link[j].xsect.yFull);
        f = (xsect_getAofY(Link[j].xsect, h) * Math.sqrt((2.0 * 32.2)));
        Orifice[k].cOrif = (Orifice[k].cDisch * f);
        Orifice[k].cWeir = (orifice_getWeirCoeff(j, k, h) * f);
    }

    // link.c:1766
    static double orifice_getWeirCoeff(int j, int k, double h)
    {
        double w = 0.0;
        double aOverL = 0.0;
        if ((Orifice[k].type == BOTTOM_ORIFICE))
        {
            if ((Link[j].xsect.type == CIRCULAR))
            {
                aOverL = (h / 4.0);
            }
            else
            {
                w = Link[j].xsect.wMax;
                aOverL = (((h * w)) / ((2.0 * ((h + w)))));
            }
            h = ((Orifice[k].cDisch / 0.414) * aOverL);
            Orifice[k].hCrit = h;
        }
        else
        {
            Orifice[k].hCrit = h;
            h = (h / 2.0);
        }
        return (Orifice[k].cDisch * Math.sqrt(h));
    }

    // link.c:1812
    static double orifice_getInflow(int j)
    {
        int k = 0;
        int n1 = 0;
        int n2 = 0;
        double head = 0.0;
        double h1 = 0.0;
        double h2 = 0.0;
        double y1 = 0.0;
        double dir = 0.0;
        double f = 0.0;
        double hcrest = 0.0;
        double hcrown = 0.0;
        double hmidpt = 0.0;
        double q = 0.0;
        double ratio = 0.0;
        n1 = Link[j].node1;
        n2 = Link[j].node2;
        k = Link[j].subIndex;
        if ((RouteModel == DW))
        {
            h1 = (Node[n1].newDepth + Node[n1].invertElev);
            h2 = (Node[n2].newDepth + Node[n2].invertElev);
        }
        else
        {
            h1 = (Node[n1].newDepth + Node[n1].invertElev);
            h2 = Node[n1].invertElev;
        }
        dir = (((h1 >= h2)) ? 1.0 : (-1.0));
        y1 = Node[n1].newDepth;
        if ((dir < 0.0))
        {
            head = h1;
            h1 = h2;
            h2 = head;
            y1 = Node[n2].newDepth;
        }
        if ((Orifice[k].type == BOTTOM_ORIFICE))
        {
            hcrest = (Node[n1].invertElev + Link[j].offset1);
            if ((h1 < hcrest))
            {
                head = 0.0;
            }
            else if ((h2 > hcrest))
            {
                head = (h1 - h2);
            }
            else
            {
                head = (h1 - hcrest);
            }
            f = (head / Orifice[k].hCrit);
            f = (((((f) <= (1.0))) ? (f) : (1.0)));
        }
        else
        {
            hcrest = (Node[n1].invertElev + Link[j].offset1);
            hcrown = (hcrest + (Link[j].xsect.yFull * Link[j].setting));
            hmidpt = (((hcrest + hcrown)) / 2.0);
            if (((h1 < hcrown) && (hcrown > hcrest)))
            {
                f = (((h1 - hcrest)) / ((hcrown - hcrest)));
            }
            else
            {
                f = 1.0;
            }
            if ((f < 1.0))
            {
                head = (h1 - hcrest);
            }
            else if ((h2 < hmidpt))
            {
                head = (h1 - hmidpt);
            }
            else
            {
                head = (h1 - h2);
            }
        }
        if ((((head <= 0.0001) || (y1 <= 0.0001)) || (link_setFlapGate(j, n1, n2, dir) != 0)))
        {
            Link[j].newDepth = 0.0;
            Link[j].flowClass = DRY;
            Orifice[k].surfArea = (0.0001 * Orifice[k].length);
            Link[j].dqdh = 0.0;
            return 0.0;
        }
        Link[j].flowClass = SUBCRITICAL;
        if ((hcrest > h2))
        {
            if ((dir == 1.0))
            {
                Link[j].flowClass = DN_CRITICAL;
            }
            else
            {
                Link[j].flowClass = UP_CRITICAL;
            }
        }
        y1 = (Link[j].xsect.yFull * Link[j].setting);
        if ((Orifice[k].type == SIDE_ORIFICE))
        {
            Link[j].newDepth = (y1 * f);
            Orifice[k].surfArea = (xsect_getWofY(Link[j].xsect, Link[j].newDepth) * Orifice[k].length);
        }
        else
        {
            Link[j].newDepth = y1;
            Orifice[k].surfArea = xsect_getAofY(Link[j].xsect, y1);
        }
        q = (dir * orifice_getFlow(j, k, head, f, Link[j].hasFlapGate));
        if (((f < 1.0) && (h2 > hcrest)))
        {
            ratio = (((h2 - hcrest)) / ((h1 - hcrest)));
            q *= (Glibm.pow(((1.0 - Glibm.pow(ratio, 1.5))), 0.385));
        }
        return q;
    }

    // link.c:1938
    static double orifice_getFlow(int j, int k, double head, double f, int hasFlapGate)
    {
        double area = 0.0;
        double q = 0.0;
        double veloc = 0.0;
        double hLoss = 0.0;
        if (((head == 0.0) || (f <= 0.0)))
        {
            Link[j].dqdh = 0.0;
            return 0.0;
        }
        else if ((f < 1.0))
        {
            q = (Orifice[k].cWeir * Glibm.pow(f, 1.5));
            Link[j].dqdh = ((1.5 * q) / ((f * Orifice[k].hCrit)));
        }
        else
        {
            q = (Orifice[k].cOrif * Math.sqrt(head));
            Link[j].dqdh = (q / ((2.0 * head)));
        }
        if ((hasFlapGate != 0))
        {
            area = xsect_getAofY(Link[j].xsect, (Link[j].setting * Link[j].xsect.yFull));
            veloc = (q / area);
            hLoss = (((((4.0 / 32.2)) * veloc) * veloc) * Glibm.exp((((-1.15) * veloc) / Math.sqrt(head))));
            if ((f < 1.0))
            {
                f = (f - (hLoss / Orifice[k].hCrit));
                if ((f < 0.0))
                {
                    f = 0.0;
                }
            }
            else
            {
                head = (head - hLoss);
                if ((head < 0.0))
                {
                    head = 0.0;
                }
            }
            q = orifice_getFlow(j, k, head, f, 0);
        }
        return q;
    }

    // link.c:2010
    static int weir_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int m = 0;
        int n1 = 0;
        int n2 = 0;
        double[] x = new double[10];
        CPtr id = null;
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(LINK, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        n1 = project_findObject(NODE, tok[1]);
        if ((n1 < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        n2 = project_findObject(NODE, tok[2]);
        if ((n2 < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        m = findmatch(tok[3], WeirTypeWords);
        if ((m < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[3]);
        }
        x[0] = m;
        if (((LinkOffsets == ELEV_OFFSET) && (tok[4].g(0) == 42)))
        {
            x[1] = (-10000000000.0);
        }
        else if (!(getDouble(tok[4], new DElem(x, 1)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[4]);
        }
        if (((!(getDouble(tok[5], new DElem(x, 2)) != 0)) || (x[2] < 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[5]);
        }
        x[3] = 0.0;
        x[4] = 0.0;
        x[5] = 0.0;
        x[6] = 1.0;
        x[7] = 0.0;
        x[8] = 0.0;
        x[9] = (-1.0);
        if (((ntoks >= 7) && (tok[6].g(0) != 42)))
        {
            m = findmatch(tok[6], NoYesWords);
            if ((m < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[6]);
            }
            x[3] = m;
        }
        if (((ntoks >= 8) && (tok[7].g(0) != 42)))
        {
            if (((!(getDouble(tok[7], new DElem(x, 4)) != 0)) || (x[4] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[7]);
            }
        }
        if (((ntoks >= 9) && (tok[8].g(0) != 42)))
        {
            if (((!(getDouble(tok[8], new DElem(x, 5)) != 0)) || (x[5] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[8]);
            }
        }
        if (((ntoks >= 10) && (tok[9].g(0) != 42)))
        {
            m = findmatch(tok[9], NoYesWords);
            if ((m < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[9]);
            }
            x[6] = m;
        }
        if ((((m = R.D2I(x[0]))) == ROADWAY_WEIR))
        {
            if ((ntoks >= 11))
            {
                if (((!(getDouble(tok[10], new DElem(x, 7)) != 0)) || (x[7] < 0.0)))
                {
                    return error_setInpError(ERR_NUMBER, tok[10]);
                }
            }
            if ((ntoks >= 12))
            {
                if ((strcomp(tok[11], L311) != 0))
                {
                    x[8] = 1.0;
                }
                else if ((strcomp(tok[11], L312) != 0))
                {
                    x[8] = 2.0;
                }
            }
        }
        if (((ntoks >= 13) && (tok[12].g(0) != 42)))
        {
            m = project_findObject(CURVE, tok[12]);
            if ((m < 0))
            {
                return error_setInpError(ERR_NAME, tok[12]);
            }
            x[9] = m;
        }
        Link[j].ID = id;
        link_setParams(j, WEIR, n1, n2, k, x);
        return 0;
    }

    // link.c:2103
    static void weir_validate(int j, int k)
    {
        int err = 0;
        double q = 0.0;
        DBox q1 = new DBox(0.0);
        DBox q2 = new DBox(0.0);
        double head = 0.0;
        switch (Weir[k].type) {
            case TRANSVERSE_WEIR:
            case SIDEFLOW_WEIR:
            case ROADWAY_WEIR:
                if ((Link[j].xsect.type != RECT_OPEN))
                {
                    err = ERR_REGULATOR_SHAPE;
                }
                Weir[k].slope = 0.0;
                break;
            case VNOTCH_WEIR:
                if ((Link[j].xsect.type != TRIANGULAR))
                {
                    err = ERR_REGULATOR_SHAPE;
                }
                else
                {
                    Weir[k].slope = Link[j].xsect.sBot;
                }
                break;
            case TRAPEZOIDAL_WEIR:
                if ((Link[j].xsect.type != TRAPEZOIDAL))
                {
                    err = ERR_REGULATOR_SHAPE;
                }
                else
                {
                    Weir[k].slope = Link[j].xsect.sBot;
                }
                break;
        }
        if ((err > 0))
        {
            report_writeErrorMsg(err, Link[j].ID);
            return;
        }
        if ((Link[j].offset1 < 0.0))
        {
            Link[j].offset1 = 0.0;
        }
        Weir[k].length = ((2.0 * RouteStep) * Math.sqrt((32.2 * Link[j].xsect.yFull)));
        Weir[k].length = (((((200.0) >= (Weir[k].length))) ? (200.0) : (Weir[k].length)));
        Weir[k].surfArea = 0.0;
        head = Link[j].xsect.yFull;
        weir_getFlow(j, k, head, 1.0, 0, q1, q2);
        q = (q1.v + q2.v);
        head = (head / 2.0);
        Weir[k].cSurcharge = (q / Math.sqrt(head));
    }

    // link.c:2166
    static void weir_setSetting(int j)
    {
        int k = Link[j].subIndex;
        double h = 0.0;
        double q = 0.0;
        DBox q1 = new DBox(0.0);
        DBox q2 = new DBox(0.0);
        Link[j].setting = Link[j].targetSetting;
        if (!(Weir[k].canSurcharge != 0))
        {
            return;
        }
        if ((Weir[k].type == ROADWAY_WEIR))
        {
            return;
        }
        if ((Link[j].setting == 0.0))
        {
            Weir[k].cSurcharge = 0.0;
        }
        else
        {
            h = (Link[j].setting * Link[j].xsect.yFull);
            weir_getFlow(j, k, h, 1.0, 0, q1, q2);
            q = (q1.v + q2.v);
            h = (h / 2.0);
            Weir[k].cSurcharge = (q / Math.sqrt(h));
        }
    }

    // link.c:2198
    static double weir_getInflow(int j)
    {
        int n1 = 0;
        int n2 = 0;
        int k = 0;
        DBox q1 = new DBox(0.0);
        DBox q2 = new DBox(0.0);
        double head = 0.0;
        double h1 = 0.0;
        double h2 = 0.0;
        double hcrest = 0.0;
        double hcrown = 0.0;
        double y = 0.0;
        double dir = 0.0;
        double ratio = 0.0;
        double[] weirPower = new double[] { 1.5, (5.0 / 3.0), 2.5, 1.5 };
        n1 = Link[j].node1;
        n2 = Link[j].node2;
        k = Link[j].subIndex;
        if ((RouteModel == DW))
        {
            h1 = (Node[n1].newDepth + Node[n1].invertElev);
            h2 = (Node[n2].newDepth + Node[n2].invertElev);
        }
        else
        {
            h1 = (Node[n1].newDepth + Node[n1].invertElev);
            h2 = Node[n1].invertElev;
        }
        dir = (((h1 > h2)) ? 1.0 : (-1.0));
        if ((dir < 0.0))
        {
            head = h1;
            h1 = h2;
            h2 = head;
        }
        hcrest = (Node[n1].invertElev + Link[j].offset1);
        hcrown = (hcrest + Link[j].xsect.yFull);
        if ((Weir[k].type == ROADWAY_WEIR))
        {
            return roadway_getInflow(j, dir, hcrest, h1, h2);
        }
        hcrest += (((1.0 - Link[j].setting)) * Link[j].xsect.yFull);
        head = (h1 - hcrest);
        Link[j].dqdh = 0.0;
        if ((((head <= 0.0001) || (hcrest >= hcrown)) || (link_setFlapGate(j, n1, n2, dir) != 0)))
        {
            Link[j].newDepth = 0.0;
            Link[j].flowClass = DRY;
            return 0.0;
        }
        Link[j].flowClass = SUBCRITICAL;
        if ((hcrest > h2))
        {
            if ((dir == 1.0))
            {
                Link[j].flowClass = DN_CRITICAL;
            }
            else
            {
                Link[j].flowClass = UP_CRITICAL;
            }
        }
        y = (Link[j].xsect.yFull - ((hcrown - (((((h1) <= (hcrown))) ? (h1) : (hcrown))))));
        Weir[k].surfArea = (xsect_getWofY(Link[j].xsect, y) * Weir[k].length);
        if ((h1 >= hcrown))
        {
            if ((Weir[k].canSurcharge != 0))
            {
                y = (((hcrest + hcrown)) / 2.0);
                if ((h2 < y))
                {
                    head = (h1 - y);
                }
                else
                {
                    head = (h1 - h2);
                }
                y = (hcrown - hcrest);
                q1.v = weir_getOrificeFlow(j, head, y, Weir[k].cSurcharge);
                Link[j].newDepth = y;
                return (dir * q1.v);
            }
            else
            {
                head = (hcrown - hcrest);
            }
        }
        weir_getFlow(j, k, head, dir, Link[j].hasFlapGate, q1, q2);
        if ((h2 > hcrest))
        {
            ratio = (((h2 - hcrest)) / ((h1 - hcrest)));
            q1.v *= (Glibm.pow(((1.0 - Glibm.pow(ratio, weirPower[Weir[k].type]))), 0.385));
            if ((q2.v > 0.0))
            {
                q2.v *= (Glibm.pow(((1.0 - Glibm.pow(ratio, weirPower[VNOTCH_WEIR]))), 0.385));
            }
        }
        Link[j].newDepth = (((((((h1 - hcrest))) <= (Link[j].xsect.yFull))) ? (((h1 - hcrest))) : (Link[j].xsect.yFull)));
        return (dir * ((q1.v + q2.v)));
    }

    // link.c:2321
    static void weir_getFlow(int j, int k, double head, double dir, int hasFlapGate, DRef q1, DRef q2)
    {
        double length = 0.0;
        double h = 0.0;
        double y = 0.0;
        double hLoss = 0.0;
        double area = 0.0;
        double veloc = 0.0;
        int wType = 0;
        int cdCurve = Weir[k].cdCurve;
        double cDisch1 = Weir[k].cDisch1;
        q1.set(0.0);
        q2.set(0.0);
        Link[j].dqdh = 0.0;
        if ((head <= 0.0))
        {
            return;
        }
        length = (Link[j].xsect.wMax * UCF(LENGTH));
        h = (head * UCF(LENGTH));
        if ((cdCurve >= 0))
        {
            cDisch1 = table_lookup(Curve[cdCurve], h);
        }
        wType = Weir[k].type;
        if (((wType == VNOTCH_WEIR) && (Link[j].setting < 1.0)))
        {
            wType = TRAPEZOIDAL_WEIR;
        }
        switch (wType) {
            case TRANSVERSE_WEIR:
                length -= ((0.1 * Weir[k].endCon) * h);
                length = (((((length) >= (0.0))) ? (length) : (0.0)));
                q1.set(((cDisch1 * length) * Glibm.pow(h, 1.5)));
                break;
            case SIDEFLOW_WEIR:
                length -= ((0.1 * Weir[k].endCon) * h);
                length = (((((length) >= (0.0))) ? (length) : (0.0)));
                if ((dir < 0.0))
                {
                    q1.set(((cDisch1 * length) * Glibm.pow(h, 1.5)));
                }
                else
                {
                    q1.set(((cDisch1 * Glibm.pow(length, 0.83)) * Glibm.pow(h, 1.67)));
                }
                break;
            case VNOTCH_WEIR:
                q1.set(((cDisch1 * Weir[k].slope) * Glibm.pow(h, 2.5)));
                break;
            case TRAPEZOIDAL_WEIR:
                y = (((1.0 - Link[j].setting)) * Link[j].xsect.yFull);
                length = (xsect_getWofY(Link[j].xsect, y) * UCF(LENGTH));
                q1.set(((cDisch1 * length) * Glibm.pow(h, 1.5)));
                q2.set(((Weir[k].cDisch2 * Weir[k].slope) * Glibm.pow(h, 2.5)));
        }
        if ((UnitSystem == SI))
        {
            q1.set((q1.get() / 0.028317));
            q2.set((q2.get() / 0.028317));
        }
        if ((hasFlapGate != 0))
        {
            area = weir_getOpenArea(j, head);
            if ((area > 1e-06))
            {
                veloc = (((q1.get() + q2.get())) / area);
                hLoss = (((((4.0 / 32.2)) * veloc) * veloc) * Glibm.exp((((-1.15) * veloc) / Math.sqrt(head))));
                head = (head - hLoss);
                if ((head < 0.0))
                {
                    head = 0.0;
                }
                weir_getFlow(j, k, head, dir, 0, q1, q2);
            }
        }
        Link[j].dqdh = weir_getdqdh(k, dir, head, q1.get(), q2.get());
    }

    // link.c:2432
    static double weir_getOrificeFlow(int j, double head, double y, double cOrif)
    {
        double a = 0.0;
        double q = 0.0;
        double v = 0.0;
        double hloss = 0.0;
        q = (cOrif * Math.sqrt(head));
        if ((Link[j].hasFlapGate != 0))
        {
            a = weir_getOpenArea(j, y);
            if ((a > 0.0))
            {
                v = (q / a);
                hloss = (((((4.0 / 32.2)) * v) * v) * Glibm.exp((((-1.15) * v) / Math.sqrt(y))));
                head -= hloss;
                head = (((((head) >= (0.0))) ? (head) : (0.0)));
                q = (cOrif * Math.sqrt(head));
            }
        }
        if ((head > 0.0))
        {
            Link[j].dqdh = (q / ((2.0 * head)));
        }
        else
        {
            Link[j].dqdh = 0.0;
        }
        return q;
    }

    // link.c:2467
    static double weir_getOpenArea(int j, double y)
    {
        double z = 0.0;
        double zy = 0.0;
        z = (((1.0 - Link[j].setting)) * Link[j].xsect.yFull);
        zy = (z + y);
        zy = (((((zy) <= (Link[j].xsect.yFull))) ? (zy) : (Link[j].xsect.yFull)));
        return (xsect_getAofY(Link[j].xsect, zy) - xsect_getAofY(Link[j].xsect, z));
    }

    // link.c:2492
    static double weir_getdqdh(int k, double dir, double h, double q1, double q2)
    {
        double q1h = 0.0;
        double q2h = 0.0;
        if ((Math.abs(h) < 0.0001))
        {
            return 0.0;
        }
        q1h = Math.abs((q1 / h));
        q2h = Math.abs((q2 / h));
        switch (Weir[k].type) {
            case TRANSVERSE_WEIR:
                return (1.5 * q1h);
            case SIDEFLOW_WEIR:
                if ((dir < 0.0))
                {
                    return (1.5 * q1h);
                }
                else
                {
                    return (1.67 * q1h);
                }
            case VNOTCH_WEIR:
                if ((q2h == 0.0))
                {
                    return (2.5 * q1h);
                }
                else
                {
                    return ((1.5 * q1h) + (2.5 * q2h));
                }
            case TRAPEZOIDAL_WEIR:
                return ((1.5 * q1h) + (2.5 * q2h));
        }
        return 0.0;
    }

    // link.c:2524
    static int outlet_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int m = 0;
        int n = 0;
        int n1 = 0;
        int n2 = 0;
        double[] x = new double[6];
        CPtr id = null;
        CPtr s = null;
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(LINK, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        n1 = project_findObject(NODE, tok[1]);
        if ((n1 < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        n2 = project_findObject(NODE, tok[2]);
        if ((n2 < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        if (((LinkOffsets == ELEV_OFFSET) && (tok[3].g(0) == 42)))
        {
            x[0] = (-10000000000.0);
        }
        else
        {
            if (!(getDouble(tok[3], new DElem(x, 0)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[3]);
            }
            if (((LinkOffsets == DEPTH_OFFSET) && (x[0] < 0.0)))
            {
                x[0] = 0.0;
            }
        }
        m = findmatch(tok[4], RelationWords);
        if ((m < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[4]);
        }
        x[1] = 0.0;
        x[2] = 0.0;
        x[3] = (-1.0);
        x[4] = 0.0;
        x[5] = NODE_DEPTH;
        s = R.strtok(tok[4], L313);
        s = R.strtok(null, L313);
        if ((strcomp(s, L314) != 0))
        {
            x[5] = NODE_HEAD;
        }
        if ((m == FUNCTIONAL))
        {
            if ((ntoks < 7))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if (!(getDouble(tok[5], new DElem(x, 1)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[5]);
            }
            if (!(getDouble(tok[6], new DElem(x, 2)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[6]);
            }
            n = 7;
        }
        else
        {
            i = project_findObject(CURVE, tok[5]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[5]);
            }
            x[3] = i;
            n = 6;
        }
        if ((ntoks > n))
        {
            i = findmatch(tok[n], NoYesWords);
            if ((i < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[n]);
            }
            x[4] = i;
        }
        Link[j].ID = id;
        link_setParams(j, OUTLET, n1, n2, k, x);
        return 0;
    }

    // link.c:2608
    static double outlet_getInflow(int j)
    {
        int k = 0;
        int n1 = 0;
        int n2 = 0;
        double head = 0.0;
        double hcrest = 0.0;
        double h1 = 0.0;
        double h2 = 0.0;
        double y1 = 0.0;
        double dir = 0.0;
        n1 = Link[j].node1;
        n2 = Link[j].node2;
        k = Link[j].subIndex;
        if ((RouteModel == DW))
        {
            h1 = (Node[n1].newDepth + Node[n1].invertElev);
            h2 = (Node[n2].newDepth + Node[n2].invertElev);
        }
        else
        {
            h1 = (Node[n1].newDepth + Node[n1].invertElev);
            h2 = Node[n1].invertElev;
        }
        dir = (((h1 >= h2)) ? 1.0 : (-1.0));
        y1 = Node[n1].newDepth;
        if ((dir < 0.0))
        {
            y1 = h1;
            h1 = h2;
            h2 = y1;
            y1 = Node[n2].newDepth;
        }
        hcrest = (Node[n1].invertElev + Link[j].offset1);
        if (((Outlet[k].curveType == NODE_HEAD) && (RouteModel == DW)))
        {
            head = (h1 - (((((h2) >= (hcrest))) ? (h2) : (hcrest))));
        }
        else
        {
            head = (h1 - hcrest);
        }
        if ((((head <= 0.0001) || (y1 <= 0.0001)) || (link_setFlapGate(j, n1, n2, dir) != 0)))
        {
            Link[j].newDepth = 0.0;
            Link[j].flowClass = DRY;
            return 0.0;
        }
        Link[j].newDepth = head;
        Link[j].flowClass = SUBCRITICAL;
        return ((dir * Link[j].setting) * outlet_getFlow(k, head));
    }

    // link.c:2672
    static double outlet_getFlow(int k, double head)
    {
        int m = 0;
        double h = 0.0;
        h = (head * UCF(LENGTH));
        m = Outlet[k].qCurve;
        if ((m >= 0))
        {
            return (table_lookup(Curve[m], h) / UCF(FLOW));
        }
        else
        {
            return ((Outlet[k].qCoeff * Glibm.pow(h, Outlet[k].qExpon)) / UCF(FLOW));
        }
    }

}
