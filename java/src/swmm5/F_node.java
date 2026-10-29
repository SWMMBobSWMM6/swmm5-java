// Generated from EPA SWMM 5.2.4 (node.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_node {
    // node.c:102
    static int node_readParams(int j, int type, int k, CPtr[] tok, int ntoks)
    {
        switch (type) {
            case JUNCTION:
                return junc_readParams(j, k, tok, ntoks);
            case OUTFALL:
                return outfall_readParams(j, k, tok, ntoks);
            case STORAGE:
                return storage_readParams(j, k, tok, ntoks);
            case DIVIDER:
                return divider_readParams(j, k, tok, ntoks);
            default:
                return 0;
        }
    }

    // node.c:125
    static void node_setParams(int j, int type, int k, double[] x)
    {
        Node[j].type = type;
        Node[j].subIndex = k;
        Node[j].invertElev = (x[0] / UCF(LENGTH));
        Node[j].crownElev = Node[j].invertElev;
        Node[j].initDepth = 0.0;
        Node[j].newVolume = 0.0;
        Node[j].fullVolume = 0.0;
        Node[j].fullDepth = 0.0;
        Node[j].surDepth = 0.0;
        Node[j].pondedArea = 0.0;
        Node[j].degree = 0;
        Node[j].inlet = NO_INLET;
        switch (type) {
            case JUNCTION:
                Node[j].fullDepth = (x[1] / UCF(LENGTH));
                Node[j].initDepth = (x[2] / UCF(LENGTH));
                Node[j].surDepth = (x[3] / UCF(LENGTH));
                Node[j].pondedArea = (x[4] / ((UCF(LENGTH) * UCF(LENGTH))));
                break;
            case OUTFALL:
                Outfall[k].type = R.D2I(x[1]);
                Outfall[k].fixedStage = (x[2] / UCF(LENGTH));
                Outfall[k].tideCurve = R.D2I(x[3]);
                Outfall[k].stageSeries = R.D2I(x[4]);
                Outfall[k].hasFlapGate = ((byte) (R.D2I(x[5])));
                Outfall[k].routeTo = R.D2I(x[6]);
                Outfall[k].wRouted = (null);
                if ((Outfall[k].routeTo >= 0))
                {
                    Outfall[k].wRouted = new double[(int) (Nobjects[POLLUT])];
                }
                break;
            case STORAGE:
                Node[j].fullDepth = (x[1] / UCF(LENGTH));
                Node[j].initDepth = (x[2] / UCF(LENGTH));
                Storage[k].shape = R.D2I(x[3]);
                Storage[k].a1 = x[4];
                Storage[k].a2 = x[5];
                Storage[k].a0 = x[6];
                Storage[k].aCurve = R.D2I(x[7]);
                Node[j].surDepth = (x[8] / UCF(LENGTH));
                Storage[k].fEvap = x[9];
                break;
            case DIVIDER:
                Divider[k].link = R.D2I(x[1]);
                Divider[k].type = R.D2I(x[2]);
                Divider[k].flowCurve = R.D2I(x[3]);
                Divider[k].qMin = (x[4] / UCF(FLOW));
                Divider[k].dhMax = x[5];
                Divider[k].cWeir = x[6];
                Node[j].fullDepth = (x[7] / UCF(LENGTH));
                Node[j].initDepth = (x[8] / UCF(LENGTH));
                Node[j].surDepth = (x[9] / UCF(LENGTH));
                Node[j].pondedArea = (x[10] / ((UCF(LENGTH) * UCF(LENGTH))));
                break;
        }
    }

    // node.c:200
    static void node_validate(int j)
    {
        TDwfInflow inflow = null;
        if (((Node[j].fullDepth > Node[j].oldDepth) && (Node[j].oldDepth > 0.0)))
        {
            report_writeWarningMsg(L317, Node[j].ID);
        }
        if ((Node[j].initDepth > (Node[j].fullDepth + Node[j].surDepth)))
        {
            report_writeErrorMsg(ERR_NODE_DEPTH, Node[j].ID);
        }
        if ((Node[j].type == STORAGE))
        {
            if ((node_getVolume(j, Node[j].fullDepth) < 0.0))
            {
                report_writeErrorMsg(ERR_STORAGE_VOLUME, Node[j].ID);
            }
        }
        if ((Node[j].type == DIVIDER))
        {
            divider_validate(j);
        }
        inflow = Node[j].dwfInflow;
        while ((inflow != null))
        {
            inflow_initDwfInflow(inflow);
            inflow = inflow.next;
        }
    }

    // node.c:237
    static void node_initState(int j)
    {
        int p = 0;
        int k = 0;
        Node[j].oldDepth = Node[j].initDepth;
        Node[j].newDepth = Node[j].oldDepth;
        Node[j].crownElev = Node[j].invertElev;
        Node[j].fullVolume = node_getVolume(j, Node[j].fullDepth);
        Node[j].oldVolume = node_getVolume(j, Node[j].oldDepth);
        Node[j].newVolume = Node[j].oldVolume;
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            Node[j].oldQual[p] = 0.0;
            Node[j].newQual[p] = 0.0;
        }
        Node[j].oldLatFlow = 0.0;
        Node[j].newLatFlow = 0.0;
        Node[j].apiExtInflow = 0.0;
        Node[j].losses = 0.0;
        if ((Node[j].type == STORAGE))
        {
            k = Node[j].subIndex;
            Storage[k].hrt = 0.0;
            if ((Storage[k].exfil != null))
            {
                exfil_initState(k);
            }
        }
        if ((Node[j].type == OUTFALL))
        {
            k = Node[j].subIndex;
            if ((Outfall[k].routeTo >= 0))
            {
                Outfall[k].vRouted = 0.0;
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    Outfall[k].wRouted[p] = 0.0;
                }
            }
        }
    }

    // node.c:293
    static void node_setOldHydState(int j)
    {
        Node[j].oldDepth = Node[j].newDepth;
        Node[j].oldVolume = Node[j].newVolume;
        Node[j].oldFlowInflow = Node[j].inflow;
        Node[j].oldNetInflow = (Node[j].inflow - Node[j].outflow);
    }

    // node.c:308
    static void node_setOldQualState(int j)
    {
        int p = 0;
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            Node[j].oldQual[p] = Node[j].newQual[p];
            Node[j].newQual[p] = 0.0;
        }
    }

    // node.c:325
    static void node_initFlows(int j, double tStep)
    {
        Node[j].inflow = Node[j].newLatFlow;
        Node[j].outflow = Node[j].losses;
        if ((Node[j].newVolume > Node[j].fullVolume))
        {
            Node[j].overflow = (((Node[j].newVolume - Node[j].fullVolume)) / tStep);
        }
        else
        {
            Node[j].overflow = 0.0;
        }
    }

    // node.c:345
    static double node_getDepth(int j, double v)
    {
        switch (Node[j].type) {
            case STORAGE:
                return storage_getDepth(j, v);
            default:
                return 0.0;
        }
    }

    // node.c:362
    static double node_getVolume(int j, double d)
    {
        switch (Node[j].type) {
            case STORAGE:
                return storage_getVolume(j, d);
            default:
                if ((Node[j].fullDepth > 0.0))
                {
                    return (Node[j].fullVolume * ((d / Node[j].fullDepth)));
                }
                else
                {
                    return 0.0;
                }
        }
    }

    // node.c:383
    static double node_getSurfArea(int j, double d)
    {
        switch (Node[j].type) {
            case STORAGE:
                return storage_getSurfArea(j, d);
            default:
                return 0.0;
        }
    }

    // node.c:400
    static double node_getOutflow(int j, int k)
    {
        switch (Node[j].type) {
            case DIVIDER:
                return divider_getOutflow(j, k);
            case STORAGE:
                return storage_getOutflow(j, k);
            default:
                return (Node[j].inflow + Node[j].overflow);
        }
    }

    // node.c:418
    static double node_getMaxOutflow(int j, double q, double tStep)
    {
        double qMax = 0.0;
        if ((Node[j].fullVolume > 0.0))
        {
            qMax = (Node[j].inflow + (Node[j].oldVolume / tStep));
            if ((q > qMax))
            {
                q = qMax;
            }
        }
        return (((((0.0) >= (q))) ? (0.0) : (q)));
    }

    // node.c:438
    static double node_getSystemOutflow(int j, IRef isFlooded)
    {
        double outflow = 0.0;
        ;
        isFlooded.set(0);
        if ((Node[j].type == OUTFALL))
        {
            if ((Node[j].outflow == 0.0))
            {
                outflow = Node[j].inflow;
            }
            else
            {
                if ((Node[j].inflow == 0.0))
                {
                    outflow = (-Node[j].outflow);
                    Node[j].inflow = Math.abs(outflow);
                }
            }
            Node[j].overflow = 0.0;
            Node[j].newVolume = 0.0;
        }
        else if ((((RouteModel != DW) && (Node[j].degree == 0)) && (Node[j].type != STORAGE)))
        {
            if ((Node[j].outflow == 0.0))
            {
                outflow = Node[j].inflow;
            }
            Node[j].overflow = 0.0;
            Node[j].newVolume = 0.0;
        }
        else
        {
            if ((Node[j].newVolume <= Node[j].fullVolume))
            {
                outflow = Node[j].overflow;
            }
            if ((outflow > 0.0))
            {
                isFlooded.set(1);
            }
        }
        return outflow;
    }

    // node.c:497
    static void node_getResults(int j, double f, float[] x)
    {
        int p = 0;
        double z = 0.0;
        double f1 = (1.0 - f);
        z = ((((f1 * Node[j].oldDepth) + (f * Node[j].newDepth))) * UCF(LENGTH));
        x[NODE_DEPTH] = ((float) (z));
        z = (Node[j].invertElev * UCF(LENGTH));
        x[NODE_HEAD] = (x[NODE_DEPTH] + ((float) (z)));
        z = ((((f1 * Node[j].oldVolume) + (f * Node[j].newVolume))) * UCF(VOLUME));
        x[NODE_VOLUME] = ((float) (z));
        z = ((((f1 * Node[j].oldLatFlow) + (f * Node[j].newLatFlow))) * UCF(FLOW));
        x[NODE_LATFLOW] = ((float) (z));
        z = ((((f1 * Node[j].oldFlowInflow) + (f * Node[j].inflow))) * UCF(FLOW));
        x[NODE_INFLOW] = ((float) (z));
        z = (Node[j].overflow * UCF(FLOW));
        x[NODE_OVERFLOW] = ((float) (z));
        if (!(IgnoreQuality != 0))
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                z = ((f1 * Node[j].oldQual[p]) + (f * Node[j].newQual[p]));
                x[(NODE_QUAL + p)] = ((float) (z));
            }
        }
    }

    // node.c:532
    static void node_setOutletDepth(int j, double yNorm, double yCrit, double z)
    {
        switch (Node[j].type) {
            case STORAGE:
                return;
            case OUTFALL:
                outfall_setOutletDepth(j, yNorm, yCrit, z);
                break;
            default:
                if ((z > 0.0))
                {
                    Node[j].newDepth = 0.0;
                }
                else
                {
                    Node[j].newDepth = (((((yNorm) <= (yCrit))) ? (yNorm) : (yCrit)));
                }
        }
    }

    // node.c:562
    static double node_getPondedArea(int j, double d)
    {
        double a = 0.0;
        if (((d <= Node[j].fullDepth) || (Node[j].pondedArea == 0.0)))
        {
            return node_getSurfArea(j, d);
        }
        d = (d - Node[j].fullDepth);
        a = Node[j].pondedArea;
        if ((a <= 0.0))
        {
            a = node_getSurfArea(j, Node[j].fullDepth);
        }
        return a;
    }

    // node.c:589
    static double node_getLosses(int j, double tStep)
    {
        if ((Node[j].type == STORAGE))
        {
            return storage_getLosses(j, tStep);
        }
        else
        {
            return 0.0;
        }
    }

    // node.c:606
    static int junc_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int i = 0;
        double[] x = new double[6];
        CPtr id = null;
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(NODE, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        for (i = 1; (i <= 5); i++)
        {
            x[(i - 1)] = 0.0;
            if ((i < ntoks))
            {
                if (!(getDouble(tok[i], new DElem(x, (i - 1))) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[i]);
                }
            }
        }
        for (i = 1; (i <= 4); i++)
        {
            if ((x[i] < 0.0))
            {
                return error_setInpError(ERR_NUMBER, tok[(i + 1)]);
            }
        }
        Node[j].ID = id;
        node_setParams(j, JUNCTION, k, x);
        return 0;
    }

    // node.c:654
    static int storage_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int m = 0;
        int n = 0;
        double[] x = new double[10];
        double[] y = new double[3];
        double A = 0.0;
        double B = 0.0;
        double L = 0.0;
        double W = 0.0;
        double Z = 0.0;
        CPtr id = null;
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(NODE, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        for (i = 1; (i <= 3); i++)
        {
            if (!(getDouble(tok[i], new DElem(x, (i - 1))) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[i]);
            }
        }
        m = findmatch(tok[4], RelationWords);
        if ((m < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[4]);
        }
        x[3] = m;
        x[4] = 0.0;
        x[5] = 0.0;
        x[6] = 0.0;
        x[7] = (-1.0);
        x[8] = 0.0;
        x[9] = 0.0;
        if ((m == TABULAR))
        {
            i = project_findObject(CURVE, tok[5]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[5]);
            }
            x[7] = i;
            n = 6;
        }
        else
        {
            if ((ntoks < 8))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            for (i = 5; (i <= 7); i++)
            {
                if (!(getDouble(tok[i], new DElem(y, (i - 5))) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[i]);
                }
            }
            n = 8;
        }
        switch (m) {
            case FUNCTIONAL:
                if ((y[2] < 0.0))
                {
                    return error_setInpError(ERR_NUMBER, tok[7]);
                }
                break;
            case CYLINDRICAL:
            case CONICAL:
            case PARABOLOID:
            case PYRAMIDAL:
                if ((y[0] <= 0.0))
                {
                    return error_setInpError(ERR_NUMBER, tok[5]);
                }
                if ((y[1] <= 0.0))
                {
                    return error_setInpError(ERR_NUMBER, tok[6]);
                }
                if ((y[2] < 0.0))
                {
                    return error_setInpError(ERR_NUMBER, tok[7]);
                }
                break;
        }
        if (((m == PARABOLOID) && (y[2] == 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[7]);
        }
        switch (m) {
            case FUNCTIONAL:
                x[4] = y[0];
                x[5] = y[1];
                x[6] = y[2];
                break;
            case CYLINDRICAL:
                A = (y[0] / 2.0);
                B = (y[1] / 2.0);
                x[4] = 0.0;
                x[5] = 0.0;
                x[6] = ((3.141592654 * A) * B);
                break;
            case CONICAL:
                A = (y[0] / 2.0);
                B = (y[1] / 2.0);
                Z = y[2];
                x[4] = (((2.0 * 3.141592654) * B) * Z);
                x[5] = ((((3.141592654 * B) / A) * Z) * Z);
                x[6] = ((3.141592654 * A) * B);
                break;
            case PARABOLOID:
                A = (y[0] / 2.0);
                B = (y[1] / 2.0);
                Z = y[2];
                x[4] = (((3.141592654 * A) * B) / Z);
                x[5] = 0.0;
                x[6] = 0.0;
                break;
            case PYRAMIDAL:
                L = y[0];
                W = y[1];
                Z = y[2];
                x[4] = ((2.0 * ((L + W))) * Z);
                x[5] = ((4.0 * Z) * Z);
                x[6] = (L * W);
                break;
        }
        if ((ntoks > n))
        {
            if (!(getDouble(tok[n], new DElem(x, 8)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[n]);
            }
            n++;
        }
        if ((ntoks > n))
        {
            if (!(getDouble(tok[n], new DElem(x, 9)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[n]);
            }
            n++;
        }
        Node[j].ID = id;
        node_setParams(j, STORAGE, k, x);
        if ((ntoks > n))
        {
            return exfil_readStorageParams(k, tok, ntoks, n);
        }
        return 0;
    }

    // node.c:813
    static double storage_getDepth(int j, double v)
    {
        int k = Node[j].subIndex;
        int i = Storage[k].aCurve;
        int shape = Storage[k].shape;
        double a0 = Storage[k].a0;
        double a1 = Storage[k].a1;
        double a2 = Storage[k].a2;
        DBox d = new DBox(0.0);
        double e = 0.0;
        TStorageVol storageVol = new TStorageVol();
        if (((Node[j].fullVolume > 0.0) && (v >= Node[j].fullVolume)))
        {
            return Node[j].fullDepth;
        }
        if ((v == 0.0))
        {
            return 0.0;
        }
        v *= (UCF(VOLUME));
        storageVol.k = k;
        storageVol.v = v;
        switch (shape) {
            case TABULAR:
                i = Storage[k].aCurve;
                if ((i >= 0))
                {
                    d.v = table_getStorageDepth(Curve[i], v);
                }
                else
                {
                    d.v = 0.0;
                }
                break;
            case CYLINDRICAL:
                d.v = (v / a0);
                break;
            case PARABOLOID:
                d.v = Math.sqrt(((2.0 * v) / a1));
                break;
            case FUNCTIONAL:
                if ((a2 == 0.0))
                {
                    d.v = (v / ((a0 + a1)));
                }
                else if ((a0 == 0.0))
                {
                    e = (1.0 / ((a2 + 1.0)));
                    d.v = Glibm.pow((v / ((a1 * e))), e);
                }
                else if (((a2 == 1.0) && (a1 > 0.0)))
                {
                    d.v = (((Math.sqrt(((a0 * a0) + ((2.0 * a1) * v))) - a0)) / a1);
                }
                else
                {
                    d.v = (v / ((a0 + a1)));
                    findroot_Newton(0.0, (Node[j].fullDepth * UCF(LENGTH)), d, 0.001, F_node::storage_getVolDiff, storageVol);
                }
                break;
            case CONICAL:
            case PYRAMIDAL:
                d.v = (v / a0);
                findroot_Newton(0.0, (Node[j].fullDepth * UCF(LENGTH)), d, 0.001, F_node::storage_getVolDiff, storageVol);
                break;
            default:
                d.v = 0.0;
        }
        d.v /= (UCF(LENGTH));
        if ((d.v > Node[j].fullDepth))
        {
            d.v = Node[j].fullDepth;
        }
        return d.v;
    }

    // node.c:906
    static void storage_getVolDiff(double y, DRef f, DRef df, Object p)
    {
        int k = 0;
        TStorageVol storageVol = null;
        storageVol = ((TStorageVol) p);
        k = storageVol.k;
        f.set((storage_getVolume(k, y) - storageVol.v));
        df.set(storage_getSurfArea(k, y));
    }

    // node.c:930
    static double storage_getVolume(int j, double d)
    {
        int k = Node[j].subIndex;
        int i = 0;
        double n = 0.0;
        double v = 0.0;
        if ((d == 0.0))
        {
            return 0.0;
        }
        if (((d >= Node[j].fullDepth) && (Node[j].fullVolume > 0.0)))
        {
            return Node[j].fullVolume;
        }
        switch (Storage[k].shape) {
            case TABULAR:
                i = Storage[k].aCurve;
                if ((i >= 0))
                {
                    return (table_getStorageVolume(Curve[i], (d * UCF(LENGTH))) / UCF(VOLUME));
                }
                else
                {
                    return 0.0;
                }
            case FUNCTIONAL:
                d *= (UCF(LENGTH));
                n = (Storage[k].a2 + 1.0);
                v = (((Storage[k].a0 * d)) + ((Storage[k].a1 / n) * Glibm.pow(d, n)));
                return (v / UCF(VOLUME));
            case CYLINDRICAL:
            case CONICAL:
            case PARABOLOID:
            case PYRAMIDAL:
                d *= (UCF(LENGTH));
                v = (d * ((Storage[k].a0 + (d * (((Storage[k].a1 / 2.0) + ((d * Storage[k].a2) / 3.0)))))));
                return (v / UCF(VOLUME));
            default:
                return 0.0;
        }
    }

    // node.c:979
    static double storage_getSurfArea(int j, double d)
    {
        double area = 0.0;
        int k = Node[j].subIndex;
        int i = 0;
        switch (Storage[k].shape) {
            case TABULAR:
                i = Storage[k].aCurve;
                if ((i >= 0))
                {
                    area = table_lookupEx(Curve[i], (d * UCF(LENGTH)));
                }
                break;
            case FUNCTIONAL:
                area = (Storage[k].a0 + (Storage[k].a1 * Glibm.pow((d * UCF(LENGTH)), Storage[k].a2)));
                break;
            case CYLINDRICAL:
            case CONICAL:
            case PARABOLOID:
            case PYRAMIDAL:
                d *= (UCF(LENGTH));
                area = (Storage[k].a0 + (d * ((Storage[k].a1 + (d * Storage[k].a2)))));
                break;
            default:
                return 0.0;
        }
        return ((area / UCF(LENGTH)) / UCF(LENGTH));
    }

    // node.c:1022
    static double storage_getOutflow(int j, int i)
    {
        int k = 0;
        double a = 0.0;
        double y = 0.0;
        if ((Link[i].type != CONDUIT))
        {
            return 0.0;
        }
        y = (Node[j].newDepth - Link[i].offset1);
        if ((y <= 0.0))
        {
            return 0.0;
        }
        if ((y >= Link[i].xsect.yFull))
        {
            return Link[i].qFull;
        }
        k = Link[i].subIndex;
        a = xsect_getAofY(Link[i].xsect, y);
        return (Conduit[k].beta * xsect_getSofA(Link[i].xsect, a));
    }

    // node.c:1052
    static double storage_getLosses(int j, double tStep)
    {
        int k = 0;
        double depth = 0.0;
        double area = 0.0;
        double evapRate = 0.0;
        double exfilRate = 0.0;
        double totalLoss = 0.0;
        double lossRatio = 0.0;
        TExfil exfil = null;
        k = Node[j].subIndex;
        evapRate = (Evap.rate * Storage[k].fEvap);
        exfil = Storage[k].exfil;
        if (((evapRate > 0.0) || (exfil != null)))
        {
            depth = Node[j].newDepth;
            area = storage_getSurfArea(j, depth);
            if ((Node[j].newVolume > 0.0001))
            {
                evapRate = (area * evapRate);
            }
            if ((exfil != null))
            {
                exfilRate = exfil_getLoss(exfil, tStep, depth, area);
            }
            totalLoss = (((evapRate + exfilRate)) * tStep);
            if ((totalLoss > Node[j].newVolume))
            {
                lossRatio = (Node[j].newVolume / totalLoss);
                evapRate *= lossRatio;
                exfilRate *= lossRatio;
            }
        }
        Storage[Node[j].subIndex].evapLoss = (evapRate * tStep);
        Storage[Node[j].subIndex].exfilLoss = (exfilRate * tStep);
        return (evapRate + exfilRate);
    }

    // node.c:1113
    static int divider_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int m = 0;
        int m1 = 0;
        int m2 = 0;
        int n = 0;
        double[] x = new double[11];
        CPtr id = null;
        if ((ntoks < 4))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(NODE, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if (!(getDouble(tok[1], new DElem(x, 0)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[1]);
        }
        for (i = 1; (i < 11); i++)
        {
            x[i] = 0.0;
        }
        if (((R.strlen(tok[2]) == 0) || (R.strcmp(tok[2], L275) == 0)))
        {
            x[1] = (-1.0);
        }
        else
        {
            m1 = project_findObject(LINK, tok[2]);
            if ((m1 < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
            x[1] = m1;
        }
        n = 4;
        m1 = findmatch(tok[3], DividerTypeWords);
        if ((m1 < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[3]);
        }
        x[2] = m1;
        x[3] = (-1);
        if ((m1 == TABULAR_DIVIDER))
        {
            if ((ntoks < 5))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            m2 = project_findObject(CURVE, tok[4]);
            if ((m2 < 0))
            {
                return error_setInpError(ERR_NAME, tok[4]);
            }
            x[3] = m2;
            n = 5;
        }
        if ((m1 == CUTOFF_DIVIDER))
        {
            if ((ntoks < 5))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if (!(getDouble(tok[4], new DElem(x, 4)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[4]);
            }
            n = 5;
        }
        if ((m1 == WEIR_DIVIDER))
        {
            if ((ntoks < 7))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            for (i = 4; (i < 7); i++)
            {
                if (!(getDouble(tok[i], new DElem(x, i)) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[i]);
                }
            }
            n = 7;
        }
        if ((m1 == OVERFLOW_DIVIDER))
        {
            n = 4;
        }
        m = 7;
        for (i = n; ((i < ntoks) && (m < 11)); i++)
        {
            if (!(getDouble(tok[i], new DElem(x, m)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[i]);
            }
            m++;
        }
        Node[j].ID = id;
        node_setParams(j, DIVIDER, k, x);
        return 0;
    }

    // node.c:1216
    static void divider_validate(int j)
    {
        int i = 0;
        int k = 0;
        k = Node[j].subIndex;
        i = Divider[k].link;
        if (((i < 0) || (((Link[i].node1 != j) && (Link[i].node2 != j)))))
        {
            report_writeErrorMsg(ERR_DIVIDER_LINK, Node[j].ID);
        }
        if ((Divider[k].type == WEIR_DIVIDER))
        {
            if (((Divider[k].dhMax <= 0.0) || (Divider[k].cWeir <= 0.0)))
            {
                report_writeErrorMsg(ERR_WEIR_DIVIDER, Node[j].ID);
            }
            else
            {
                Divider[k].qMax = ((Divider[k].cWeir * Glibm.pow(Divider[k].dhMax, 1.5)) / UCF(FLOW));
                if ((Divider[k].qMin > Divider[k].qMax))
                {
                    report_writeErrorMsg(ERR_WEIR_DIVIDER, Node[j].ID);
                }
            }
        }
    }

    // node.c:1251
    static double divider_getOutflow(int j, int k)
    {
        int i = 0;
        int m = 0;
        double qIn = 0.0;
        double qOut = 0.0;
        double f = 0.0;
        qIn = (Node[j].inflow + Node[j].overflow);
        i = Node[j].subIndex;
        switch (Divider[i].type) {
            case CUTOFF_DIVIDER:
                if ((qIn <= Divider[i].qMin))
                {
                    qOut = 0.0;
                }
                else
                {
                    qOut = (qIn - Divider[i].qMin);
                }
                break;
            case OVERFLOW_DIVIDER:
                if ((k != Divider[i].link))
                {
                    qOut = qIn;
                }
                else
                {
                    qOut = (qIn - Node[j].outflow);
                }
                if ((qOut < 1e-05))
                {
                    qOut = 0.0;
                }
                return qOut;
            case WEIR_DIVIDER:
                if ((qIn <= Divider[i].qMin))
                {
                    qOut = 0.0;
                }
                else
                {
                    f = (((qIn - Divider[i].qMin)) / ((Divider[i].qMax - Divider[i].qMin)));
                    if ((f > 1.0))
                    {
                        qOut = (Divider[i].qMax * Math.sqrt(f));
                    }
                    else
                    {
                        qOut = ((Divider[i].cWeir * Glibm.pow((f * Divider[i].dhMax), 1.5)) / UCF(FLOW));
                    }
                }
                break;
            case TABULAR_DIVIDER:
                m = Divider[i].flowCurve;
                if ((m >= 0))
                {
                    qOut = (table_lookup(Curve[m], (qIn * UCF(FLOW))) / UCF(FLOW));
                }
                else
                {
                    qOut = 0.0;
                }
                break;
            default:
                qOut = 0.0;
        }
        if ((qOut > qIn))
        {
            qOut = qIn;
        }
        if ((k != Divider[i].link))
        {
            qOut = (qIn - qOut);
        }
        return qOut;
    }

    // node.c:1333
    static int outfall_readParams(int j, int k, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int m = 0;
        int n = 0;
        double[] x = new double[7];
        CPtr id = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(NODE, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if (!(getDouble(tok[1], new DElem(x, 0)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[1]);
        }
        i = findmatch(tok[2], OutfallTypeWords);
        if ((i < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[2]);
        }
        x[1] = i;
        x[2] = 0.0;
        x[3] = (-1.0);
        x[4] = (-1.0);
        x[5] = 0.0;
        x[6] = (-1.0);
        n = 4;
        if ((i >= FIXED_OUTFALL))
        {
            if ((ntoks < 4))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            n = 5;
            switch (i) {
                case FIXED_OUTFALL:
                    if (!(getDouble(tok[3], new DElem(x, 2)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[3]);
                    }
                    break;
                case TIDAL_OUTFALL:
                    m = project_findObject(CURVE, tok[3]);
                    if ((m < 0))
                    {
                        return error_setInpError(ERR_NAME, tok[3]);
                    }
                    x[3] = m;
                    break;
                case TIMESERIES_OUTFALL:
                    m = project_findObject(TSERIES, tok[3]);
                    if ((m < 0))
                    {
                        return error_setInpError(ERR_NAME, tok[3]);
                    }
                    x[4] = m;
                    Tseries[m].refersTo = TIMESERIES_OUTFALL;
            }
        }
        if ((ntoks == n))
        {
            m = findmatch(tok[(n - 1)], NoYesWords);
            if ((m < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[(n - 1)]);
            }
            x[5] = m;
        }
        if ((ntoks == (n + 1)))
        {
            m = project_findObject(SUBCATCH, tok[n]);
            if ((m < 0))
            {
                return error_setInpError(ERR_NAME, tok[n]);
            }
            x[6] = m;
        }
        Node[j].ID = id;
        node_setParams(j, OUTFALL, k, x);
        return 0;
    }

    // node.c:1413
    static void outfall_setOutletDepth(int j, double yNorm, double yCrit, double z)
    {
        DBox x = new DBox(0.0);
        DBox y = new DBox(0.0);
        double yNew = 0.0;
        double stage = 0.0;
        int k = 0;
        int i = Node[j].subIndex;
        double currentDate = 0.0;
        switch (Outfall[i].type) {
            case FREE_OUTFALL:
                if ((z > 0.0))
                {
                    Node[j].newDepth = 0.0;
                }
                else
                {
                    Node[j].newDepth = (((((yNorm) <= (yCrit))) ? (yNorm) : (yCrit)));
                }
                return;
            case NORMAL_OUTFALL:
                if ((z > 0.0))
                {
                    Node[j].newDepth = 0.0;
                }
                else
                {
                    Node[j].newDepth = yNorm;
                }
                return;
            case FIXED_OUTFALL:
                stage = Outfall[i].fixedStage;
                break;
            case TIDAL_OUTFALL:
                k = Outfall[i].tideCurve;
                table_getFirstEntry(Curve[k], x, y);
                currentDate = (NewRoutingTime / 86400000.0);
                x.v += (((currentDate - Math.floor(currentDate))) * 24.0);
                stage = (table_lookup(Curve[k], x.v) / UCF(LENGTH));
                break;
            case TIMESERIES_OUTFALL:
                k = Outfall[i].stageSeries;
                currentDate = (StartDateTime + (NewRoutingTime / 86400000.0));
                stage = (table_tseriesLookup(Tseries[k], currentDate, 1) / UCF(LENGTH));
                break;
            default:
                stage = Node[j].invertElev;
        }
        yCrit = (((((yCrit) <= (yNorm))) ? (yCrit) : (yNorm)));
        if ((((yCrit + z) + Node[j].invertElev) < stage))
        {
            yNew = (stage - Node[j].invertElev);
        }
        else if ((z > 0.0))
        {
            if ((stage < (Node[j].invertElev + z)))
            {
                yNew = (((((0.0) >= (((stage - Node[j].invertElev))))) ? (0.0) : (((stage - Node[j].invertElev)))));
            }
            else
            {
                yNew = (z + yCrit);
            }
        }
        else
        {
            yNew = yCrit;
        }
        Node[j].newDepth = yNew;
    }

}
