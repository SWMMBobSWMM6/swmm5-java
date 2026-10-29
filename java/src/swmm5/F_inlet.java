// Generated from EPA SWMM 5.2.4 (inlet.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_inlet {
    static TInletDesign[] InletDesigns = null;
    static int InletDesignCount = 0;
    static int UsesInlets = 0;
    static CPtr[] InletTypeWords = new CPtr[] { L718, L719, L0, L720, L721, L722, L723, null };
    static CPtr[] GrateTypeWords = new CPtr[] { L724, L725, L726, L727, L728, L729, L730, L731, null };
    static CPtr[] ThroatAngleWords = new CPtr[] { L732, L733, L734, null };
    static CPtr[] PlacementTypeWords = new CPtr[] { L735, L736, L737, null };
    static double[][] SplashCoeffs = new double[][] { new double[] { 2.22, 4.03, 0.65, 0.06 }, new double[] { 0.74, 2.44, 0.27, 0.02 }, new double[] { 1.76, 3.12, 0.45, 0.03 }, new double[] { 0.30, 4.85, 1.31, 0.15 }, new double[] { 0.99, 2.64, 0.36, 0.03 }, new double[] { 0.51, 2.34, 0.2, 0.01 }, new double[] { 0.28, 2.28, 0.18, 0.01 } };
    static double[] GrateOpeningRatios = new double[] { 0.90, 0.80, 0.60, 0.35, 0.17, 0.34, 0.80, 1.00 };
    static double Sx = 0.0;
    static double SL = 0.0;
    static double Sw = 0.0;
    static double a = 0.0;
    static double W = 0.0;
    static double T = 0.0;
    static double n = 0.0;
    static int Nsides = 0;
    static double Tcrown = 0.0;
    static double Beta = 0.0;
    static double Qfactor = 0.0;
    static TXsect xsect = null;
    static double[] InletFlow = null;
    static TInlet FirstInlet = null;

    // inlet.c:257
    static int inlet_create(int numInlets)
    {
        int i = 0;
        InletDesigns = (null);
        InletFlow = (null);
        InletDesignCount = 0;
        UsesInlets = 0;
        FirstInlet = (null);
        InletDesigns = R.fill(new TInletDesign[(int) (numInlets)], TInletDesign::new);
        if ((InletDesigns == null))
        {
            return ERR_MEMORY;
        }
        InletDesignCount = numInlets;
        InletFlow = new double[(int) (Nobjects[NODE])];
        if ((InletFlow == null))
        {
            return ERR_MEMORY;
        }
        for (i = 0; (i < InletDesignCount); i++)
        {
            InletDesigns[i].customCurve = (-1);
            InletDesigns[i].curbInlet.length = 0.0;
            InletDesigns[i].grateInlet.length = 0.0;
            InletDesigns[i].slottedInlet.length = 0.0;
            InletDesigns[i].type = CUSTOM_INLET;
        }
        return 0;
    }

    // inlet.c:291
    static void inlet_delete()
    {
        TInlet inlet = FirstInlet;
        TInlet nextInlet = null;
        while ((inlet != null))
        {
            nextInlet = inlet.nextInlet;
            R.free(inlet);
            inlet = nextInlet;
        }
        FirstInlet = (null);
        {
            if ((InletFlow != null))
            {
                R.free(InletFlow);
                InletFlow = (null);
            }
        }
        ;
        {
            if ((InletDesigns != null))
            {
                R.free(InletDesigns);
                InletDesigns = (null);
            }
        }
        ;
    }

    // inlet.c:313
    static int inlet_readDesignParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        i = project_findObject(INLET, tok[0]);
        if ((i < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        InletDesigns[i].ID = project_findID(INLET, tok[0]);
        InletDesigns[i].type = findmatch(tok[1], InletTypeWords);
        switch (InletDesigns[i].type) {
            case GRATE_INLET:
            case DROP_GRATE_INLET:
                return readGrateInletParams(i, tok, ntoks);
            case CURB_INLET:
            case DROP_CURB_INLET:
                return readCurbInletParams(i, tok, ntoks);
            case SLOTTED_INLET:
                return readSlottedInletParams(i, tok, ntoks);
            case CUSTOM_INLET:
                return readCustomInletParams(i, tok, ntoks);
            default:
                return error_setInpError(ERR_KEYWORD, tok[1]);
        }
    }

    // inlet.c:362
    static int inlet_readUsageParams(CPtr[] tok, int ntoks)
    {
        int linkIndex = 0;
        int designIndex = 0;
        int nodeIndex = 0;
        IBox numInlets = new IBox(1);
        int placement = AUTOMATIC;
        DBox flowLimit = new DBox(0.0);
        DBox pctClogged = new DBox(0.0);
        DBox aLocal = new DBox(0.0);
        DBox wLocal = new DBox(0.0);
        TInlet inlet = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        linkIndex = project_findObject(LINK, tok[0]);
        if ((linkIndex < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        designIndex = project_findObject(INLET, tok[1]);
        if ((designIndex < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        nodeIndex = project_findObject(NODE, tok[2]);
        if ((nodeIndex < 0))
        {
            return error_setInpError(ERR_NAME, tok[2]);
        }
        if ((ntoks > 3))
        {
            if (((!(getInt(tok[3], numInlets) != 0)) || (numInlets.v < 1)))
            {
                return error_setInpError(ERR_NUMBER, tok[3]);
            }
        }
        if ((ntoks > 4))
        {
            if ((((!(getDouble(tok[4], pctClogged) != 0)) || (pctClogged.v < 0.0)) || (pctClogged.v > 99.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[4]);
            }
        }
        if ((ntoks > 5))
        {
            if (((!(getDouble(tok[5], flowLimit) != 0)) || (flowLimit.v < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[5]);
            }
        }
        if ((ntoks > 6))
        {
            if (((!(getDouble(tok[6], aLocal) != 0)) || (aLocal.v < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[6]);
            }
        }
        if ((ntoks > 7))
        {
            if (((!(getDouble(tok[7], wLocal) != 0)) || (wLocal.v < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[7]);
            }
        }
        if ((ntoks > 8))
        {
            placement = findmatch(tok[8], PlacementTypeWords);
            if ((placement < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[8]);
            }
        }
        inlet = Link[linkIndex].inlet;
        if ((inlet == null))
        {
            inlet = new TInlet();
            if (!(inlet != null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
            Link[linkIndex].inlet = inlet;
            inlet.nextInlet = FirstInlet;
            FirstInlet = inlet;
        }
        inlet.linkIndex = linkIndex;
        inlet.designIndex = designIndex;
        inlet.nodeIndex = nodeIndex;
        inlet.numInlets = numInlets.v;
        inlet.placement = placement;
        inlet.clogFactor = (1.0 - ((pctClogged.v / 100.0)));
        inlet.flowLimit = (flowLimit.v / UCF(FLOW));
        inlet.localDepress = (aLocal.v / UCF(LENGTH));
        inlet.localWidth = (wLocal.v / UCF(LENGTH));
        inlet.flowFactor = 0.0;
        inlet.backflowRatio = 0.0;
        initInletStats(inlet);
        UsesInlets = 1;
        return 0;
    }

    // inlet.c:464
    static void inlet_validate()
    {
        int i = 0;
        int j = 0;
        int inletType = 0;
        int inletValid = 0;
        TInlet inlet = null;
        TInlet prevInlet = null;
        if (!(UsesInlets != 0))
        {
            return;
        }
        prevInlet = FirstInlet;
        inlet = FirstInlet;
        while ((inlet != null))
        {
            inletValid = 0;
            i = inlet.linkIndex;
            xsect = Link[i].xsect;
            inletType = InletDesigns[inlet.designIndex].type;
            if ((inletType == CUSTOM_INLET))
            {
                j = InletDesigns[inlet.designIndex].customCurve;
                if ((j >= 0))
                {
                    if (((Curve[j].curveType == DIVERSION_CURVE) || (Curve[j].curveType == RATING_CURVE)))
                    {
                        inletValid = 1;
                    }
                }
            }
            else if (((((xsect.type == TRAPEZOIDAL) || (xsect.type == RECT_OPEN))) && (((inletType == DROP_GRATE_INLET) || (inletType == DROP_CURB_INLET)))))
            {
                inletValid = 1;
            }
            else if ((((xsect.type == STREET_XSECT) && (inletType != DROP_GRATE_INLET)) && (inletType != DROP_CURB_INLET)))
            {
                inletValid = 1;
            }
            if ((inletValid != 0))
            {
                Node[Link[i].node2].inlet = BYPASS;
                Node[inlet.nodeIndex].inlet = CAPTURE;
                inlet.backflow = 0.0;
                getConduitGeometry(inlet);
                inlet.flowFactor = ((((0.56 / n)) * Glibm.pow(SL, 0.5)) * Glibm.pow(Sx, 1.67));
                prevInlet = inlet;
                inlet = inlet.nextInlet;
            }
            else
            {
                report_writeWarningMsg(L187, Link[i].ID);
                if ((inlet == FirstInlet))
                {
                    FirstInlet = inlet.nextInlet;
                    prevInlet = FirstInlet;
                    R.free(inlet);
                    inlet = FirstInlet;
                }
                else
                {
                    prevInlet.nextInlet = inlet.nextInlet;
                    R.free(inlet);
                    inlet = prevInlet.nextInlet;
                }
                Link[i].inlet = (null);
            }
        }
        getBackflowRatios();
    }

    // inlet.c:555
    static void inlet_findCapturedFlows(double tStep)
    {
        int i = 0;
        int j = 0;
        int m = 0;
        int placement = 0;
        double q = 0.0;
        TInlet inlet = null;
        if (!(UsesInlets != 0))
        {
            return;
        }
        R.memset(InletFlow, 0, (Nobjects[NODE] * 8L));
        if ((RouteModel != DW))
        {
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                Node[j].inflow = (((((0.0) >= (Node[j].newLatFlow))) ? (0.0) : (Node[j].newLatFlow)));
            }
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                Node[Link[i].node2].inflow += (((((0.0) >= (Link[i].newFlow))) ? (0.0) : (Link[i].newFlow)));
            }
        }
        for (inlet = FirstInlet; (inlet != null); inlet = inlet.nextInlet)
        {
            i = inlet.linkIndex;
            j = Link[i].node2;
            m = inlet.nodeIndex;
            placement = getInletPlacement(inlet, j);
            if ((InletDesigns[inlet.designIndex].type == CUSTOM_INLET))
            {
                q = Math.abs(Link[i].newFlow);
                inlet.flowCapture = getCustomCapturedFlow(inlet, q, Node[j].newDepth);
            }
            else if ((placement == ON_GRADE))
            {
                q = Math.abs(Link[i].newFlow);
                inlet.flowCapture = getOnGradeCapturedFlow(inlet, q, Node[j].newDepth);
            }
            else
            {
                q = Node[j].inflow;
                inlet.flowCapture = getOnSagCapturedFlow(inlet, q, Node[j].newDepth);
            }
            if ((Math.abs(inlet.flowCapture) < 0.0001))
            {
                inlet.flowCapture = 0.0;
            }
            InletFlow[j] += inlet.flowCapture;
            inlet.backflow = (Node[m].overflow * inlet.backflowRatio);
            if ((Math.abs(inlet.backflow) < 0.0001))
            {
                inlet.backflow = 0.0;
            }
        }
        for (inlet = FirstInlet; (inlet != null); inlet = inlet.nextInlet)
        {
            i = inlet.linkIndex;
            j = Link[i].node2;
            m = inlet.nodeIndex;
            if (((RouteModel != DW) && (getInletPlacement(inlet, j) == ON_SAG)))
            {
                q = (Node[j].newVolume / tStep);
                q += (((((Node[j].inflow) >= (0.0))) ? (Node[j].inflow) : (0.0)));
                if ((InletFlow[j] > q))
                {
                    inlet.flowCapture *= (q / InletFlow[j]);
                }
            }
            Node[j].newLatFlow -= ((inlet.flowCapture - inlet.backflow));
            Node[m].newLatFlow += inlet.flowCapture;
            if ((getDateTime(NewRoutingTime) > ReportStart))
            {
                updateInletStats(inlet, Math.abs(Link[i].newFlow));
            }
        }
    }

    // inlet.c:655
    static void inlet_adjustQualInflows()
    {
        int i = 0;
        int j = 0;
        int m = 0;
        int p = 0;
        double qNet = 0.0;
        TInlet inlet = null;
        if (!(UsesInlets != 0))
        {
            return;
        }
        if (((IgnoreQuality != 0) || (Nobjects[POLLUT] == 0)))
        {
            return;
        }
        for (inlet = FirstInlet; (inlet != null); inlet = inlet.nextInlet)
        {
            i = inlet.linkIndex;
            j = Link[i].node2;
            m = inlet.nodeIndex;
            qNet = (inlet.flowCapture - inlet.backflow);
            if ((qNet > 0.0))
            {
                Node[m].qualInflow += qNet;
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    Node[m].newQual[p] += (qNet * Node[j].oldQual[p]);
                }
            }
            else
            {
                qNet = (-qNet);
                Node[j].qualInflow += qNet;
                for (p = 0; (p < Nobjects[POLLUT]); p++)
                {
                    Node[j].newQual[p] += (qNet * Node[m].oldQual[p]);
                }
            }
        }
    }

    // inlet.c:706
    static void inlet_adjustQualOutflows()
    {
        int j = 0;
        int p = 0;
        double q = 0.0;
        double w = 0.0;
        TInlet inlet = null;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if ((Node[j].inlet == CAPTURE))
            {
                q = Node[j].overflow;
                if ((q > 0.0))
                {
                    StepFlowTotals.flooding -= q;
                    if (!(IgnoreQuality != 0))
                    {
                        for (p = 0; (p < Nobjects[POLLUT]); p++)
                        {
                            w = (q * Node[j].newQual[p]);
                            StepQualTotals[p].flooding -= w;
                        }
                    }
                }
            }
        }
        if (((!(IgnoreQuality != 0)) && (Nobjects[POLLUT] > 0)))
        {
            for (inlet = FirstInlet; (inlet != null); inlet = inlet.nextInlet)
            {
                j = Link[inlet.linkIndex].node2;
                q = (inlet.flowCapture - inlet.backflow);
                if (((q > 0.0) && (Node[j].newLatFlow < 0.0)))
                {
                    for (p = 0; (p < Nobjects[POLLUT]); p++)
                    {
                        w = (q * Node[j].newQual[p]);
                        StepQualTotals[p].outflow -= w;
                    }
                }
            }
        }
    }

    // inlet.c:774
    static void inlet_writeStatsReport()
    {
        int j = 0;
        int header = 0;
        if ((Nobjects[STREET] == 0))
        {
            return;
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if ((Link[j].xsect.type == STREET_XSECT))
            {
                if (!(header != 0))
                {
                    writeStreetStatsHeader();
                    header = 1;
                }
                writeStreetStats(j);
            }
        }
        report_writeLine(L0);
    }

    // inlet.c:801
    static double inlet_capturedFlow(int i)
    {
        if ((Link[i].inlet != null))
        {
            return Link[i].inlet.flowCapture;
        }
        return 0.0;
    }

    // inlet.c:814
    static int readGrateInletParams(int i, CPtr[] tok, int ntoks)
    {
        int grateType = 0;
        DBox width = new DBox(0.0);
        DBox length = new DBox(0.0);
        DBox areaRatio = new DBox(0.0);
        DBox vSplash = new DBox(0.0);
        if ((ntoks < 5))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        if (((!(getDouble(tok[2], length) != 0)) || (length.v <= 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[2]);
        }
        if (((!(getDouble(tok[3], width) != 0)) || (width.v <= 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[3]);
        }
        grateType = findmatch(tok[4], GrateTypeWords);
        if ((grateType < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[4]);
        }
        if ((grateType == GENERIC))
        {
            if ((ntoks < 6))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if ((((!(getDouble(tok[5], areaRatio) != 0)) || (areaRatio.v <= 0.0)) || (areaRatio.v > 1.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[5]);
            }
            if ((ntoks > 6))
            {
                if (((!(getDouble(tok[6], vSplash) != 0)) || (vSplash.v < 0.0)))
                {
                    return error_setInpError(ERR_NUMBER, tok[6]);
                }
            }
        }
        InletDesigns[i].grateInlet.length = (length.v / UCF(LENGTH));
        InletDesigns[i].grateInlet.width = (width.v / UCF(LENGTH));
        InletDesigns[i].grateInlet.type = grateType;
        InletDesigns[i].grateInlet.fracOpenArea = areaRatio.v;
        InletDesigns[i].grateInlet.splashVeloc = (vSplash.v / UCF(LENGTH));
        if (((InletDesigns[i].type == GRATE_INLET) && (InletDesigns[i].curbInlet.length > 0.0)))
        {
            InletDesigns[i].type = COMBO_INLET;
        }
        return 0;
    }

    // inlet.c:868
    static int readCurbInletParams(int i, CPtr[] tok, int ntoks)
    {
        int throatAngle = 0;
        DBox height = new DBox(0.0);
        DBox length = new DBox(0.0);
        if ((ntoks < 4))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        if (((!(getDouble(tok[2], length) != 0)) || (length.v <= 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[2]);
        }
        if (((!(getDouble(tok[3], height) != 0)) || (height.v <= 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[3]);
        }
        throatAngle = VERTICAL_THROAT;
        if (((InletDesigns[i].type == CURB_INLET) && (ntoks > 4)))
        {
            throatAngle = findmatch(tok[4], ThroatAngleWords);
            if ((throatAngle < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[4]);
            }
        }
        InletDesigns[i].curbInlet.length = (length.v / UCF(LENGTH));
        InletDesigns[i].curbInlet.height = (height.v / UCF(LENGTH));
        InletDesigns[i].curbInlet.throatAngle = throatAngle;
        if (((InletDesigns[i].type == CURB_INLET) && (InletDesigns[i].grateInlet.length > 0.0)))
        {
            InletDesigns[i].type = COMBO_INLET;
        }
        return 0;
    }

    // inlet.c:911
    static int readSlottedInletParams(int i, CPtr[] tok, int ntoks)
    {
        DBox width = new DBox(0.0);
        DBox length = new DBox(0.0);
        if ((ntoks < 4))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        if (((!(getDouble(tok[2], length) != 0)) || (length.v <= 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[2]);
        }
        if (((!(getDouble(tok[3], width) != 0)) || (width.v <= 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[3]);
        }
        InletDesigns[i].slottedInlet.length = (length.v / UCF(LENGTH));
        InletDesigns[i].slottedInlet.width = (width.v / UCF(LENGTH));
        return 0;
    }

    // inlet.c:939
    static int readCustomInletParams(int i, CPtr[] tok, int ntoks)
    {
        int c = 0;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        else
        {
            c = project_findObject(CURVE, tok[2]);
            if ((c < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
        }
        InletDesigns[i].customCurve = c;
        return 0;
    }

    // inlet.c:962
    static void initInletStats(TInlet inlet)
    {
        if ((inlet != null))
        {
            inlet.flowCapture = 0.0;
            inlet.backflow = 0.0;
            inlet.stats.flowPeriods = 0;
            inlet.stats.capturePeriods = 0;
            inlet.stats.backflowPeriods = 0;
            inlet.stats.peakFlow = 0.0;
            inlet.stats.peakFlowCapture = 0;
            inlet.stats.avgFlowCapture = 0;
            inlet.stats.bypassFreq = 0;
        }
    }

    // inlet.c:985
    static void updateInletStats(TInlet inlet, double q)
    {
        double qCapture = inlet.flowCapture;
        double qBackflow = inlet.backflow;
        double qNet = (qCapture - qBackflow);
        double qBypass = (q - qNet);
        double fCapture = 0.0;
        if (((q < 0.001) && (qBackflow <= 0.0)))
        {
            return;
        }
        inlet.stats.flowPeriods++;
        if ((qNet > 0.0))
        {
            inlet.stats.capturePeriods++;
            fCapture = (qNet / q);
            fCapture = (((((fCapture) <= (1.0))) ? (fCapture) : (1.0)));
            inlet.stats.avgFlowCapture += fCapture;
            if ((qBypass > 0.001))
            {
                inlet.stats.bypassFreq++;
            }
        }
        else
        {
            inlet.stats.backflowPeriods++;
        }
        if ((q > inlet.stats.peakFlow))
        {
            inlet.stats.peakFlow = q;
            inlet.stats.peakFlowCapture = (fCapture * 100.0);
        }
    }

    // inlet.c:1026
    static void writeStreetStatsHeader()
    {
        report_writeLine(L0);
        report_writeLine(L188);
        report_writeLine(L189);
        report_writeLine(L188);
        report_writeLine(L0);
        R.fprintf(Frpt.file, L190);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L191, FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits]);
        }
        else
        {
            R.fprintf(Frpt.file, L192, FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits]);
        }
        R.fprintf(Frpt.file, L193);
    }

    // inlet.c:1055
    static void writeStreetStats(int link)
    {
        int k = 0;
        int t = 0;
        int placement = 0;
        double maxSpread = 0.0;
        double maxDepth = 0.0;
        double maxFlow = 0.0;
        double fp = 0.0;
        double cp = 0.0;
        double afc = 0.0;
        double bpf = 0.0;
        TInlet inlet = null;
        k = Link[link].subIndex;
        t = Link[link].xsect.transect;
        inlet = Link[link].inlet;
        maxFlow = LinkStats[link].maxFlow;
        maxDepth = LinkStats[link].maxDepth;
        maxSpread = (xsect_getWofY(Link[link].xsect, maxDepth) / Street[t].sides);
        maxSpread = (((((maxSpread) <= (Street[t].width))) ? (maxSpread) : (Street[t].width)));
        R.fprintf(Frpt.file, L194, Link[link].ID);
        R.fprintf(Frpt.file, L195, (maxFlow * UCF(FLOW)));
        R.fprintf(Frpt.file, L195, (maxSpread * UCF(LENGTH)));
        R.fprintf(Frpt.file, L195, (maxDepth * UCF(LENGTH)));
        if ((inlet != null))
        {
            R.fprintf(Frpt.file, L196, InletDesigns[inlet.designIndex].ID);
            placement = getInletPlacement(inlet, Link[inlet.linkIndex].node2);
            if ((placement == ON_GRADE))
            {
                R.fprintf(Frpt.file, L197);
            }
            else
            {
                R.fprintf(Frpt.file, L198);
            }
            R.fprintf(Frpt.file, L199, inlet.numInlets);
            fp = (inlet.stats.flowPeriods / 100.0);
            if ((fp > 0.0))
            {
                cp = (inlet.stats.capturePeriods / 100.0);
                R.fprintf(Frpt.file, L200, inlet.stats.peakFlowCapture);
                if ((cp > 0.0))
                {
                    afc = (inlet.stats.avgFlowCapture / cp);
                    bpf = (inlet.stats.bypassFreq / cp);
                }
                R.fprintf(Frpt.file, L200, afc);
                R.fprintf(Frpt.file, L200, bpf);
                R.fprintf(Frpt.file, L200, (inlet.stats.backflowPeriods / fp));
                R.fprintf(Frpt.file, L200, ((((((maxFlow / Street[t].sides)) * UCF(FLOW)) * 0.01) * inlet.stats.peakFlowCapture) / inlet.numInlets));
                R.fprintf(Frpt.file, L200, (((maxFlow * UCF(FLOW)) * 0.01) * ((100.0 - inlet.stats.peakFlowCapture))));
            }
        }
    }

    // inlet.c:1129
    static int getInletPlacement(TInlet inlet, int j)
    {
        if ((inlet.placement == AUTOMATIC))
        {
            if ((Node[j].degree > 0))
            {
                return ON_GRADE;
            }
            else
            {
                return ON_SAG;
            }
        }
        else
        {
            return inlet.placement;
        }
    }

    // inlet.c:1147
    static void getConduitGeometry(TInlet inlet)
    {
        int linkIndex = inlet.linkIndex;
        int t = 0;
        int k = Link[linkIndex].subIndex;
        SL = Conduit[k].slope;
        Beta = Conduit[k].beta;
        xsect = Link[linkIndex].xsect;
        if ((xsect.type == STREET_XSECT))
        {
            t = xsect.transect;
            Sx = Street[t].slope;
            a = Street[t].gutterDepression;
            W = Street[t].gutterWidth;
            n = Street[t].roughness;
            Nsides = Street[t].sides;
            Tcrown = Street[t].width;
            Qfactor = inlet.flowFactor;
            if (((inlet != null) && ((inlet.localDepress * inlet.localWidth) > 0)))
            {
                a += inlet.localDepress;
                W = inlet.localWidth;
            }
            if (((W * a) > 0.0))
            {
                Sw = (Sx + (a / W));
            }
            else
            {
                Sw = Sx;
            }
        }
        else
        {
            a = 0.0;
            W = 0.0;
            n = Conduit[k].roughness;
            Nsides = 1;
            Sx = 0.01;
            Sw = Sx;
        }
    }

    // inlet.c:1200
    static double getFlowSpread(double Q)
    {
        int iter = 0;
        double f = 0.0;
        double f1 = 0.0;
        double Sr = 0.0;
        double Ts1 = 0.0;
        double Ts2 = 0.0;
        double Tw = 0.0;
        double Qs = 0.0;
        double Eo = 0.0;
        f = Qfactor;
        if ((a == 0.0))
        {
            Ts1 = Glibm.pow((Q / f), 0.375);
        }
        else
        {
            f1 = (f * Glibm.pow((((a / W)) / Sx), 1.67));
            Tw = Glibm.pow((Q / f1), 0.375);
            if ((Tw <= W))
            {
                Ts1 = Tw;
            }
            else
            {
                Sr = (((Sx + (a / W))) / Sx);
                iter = 1;
                Ts1 = (Glibm.pow((Q / f), 0.375) - W);
                if ((Ts1 <= 0))
                {
                    Ts1 = (Tw - W);
                }
                while ((iter < 11))
                {
                    Eo = getEo(Sr, Ts1, W);
                    Qs = (((1.0 - Eo)) * Q);
                    Ts2 = Glibm.pow((Qs / f), 0.375);
                    if ((Math.abs((Ts2 - Ts1)) < 0.01))
                    {
                        break;
                    }
                    Ts1 = Ts2;
                    iter++;
                }
                Ts1 = (Ts2 + W);
            }
        }
        return (((((Ts1) <= (Tcrown))) ? (Ts1) : (Tcrown)));
    }

    // inlet.c:1248
    static double getEo(double Sr, double Ts, double w)
    {
        double x = 0.0;
        x = (Sr / ((Ts / w)));
        x = (Glibm.pow(((1.0 + x)), 2.67) - 1.0);
        x = (1.0 + (Sr / x));
        return (1.0 / x);
    }

    // inlet.c:1267
    static double getOnGradeCapturedFlow(TInlet inlet, double q, double d)
    {
        int i = 0;
        int linkIndex = 0;
        double qApproach = 0.0;
        double qc = 0.0;
        double qCaptured = 0.0;
        double qBypassed = 0.0;
        double qMax = 0.0;
        if ((inlet.numInlets == 0))
        {
            return 0.0;
        }
        linkIndex = inlet.linkIndex;
        qApproach = q;
        if ((qApproach < 0.001))
        {
            return 0.0;
        }
        getConduitGeometry(inlet);
        qApproach /= Nsides;
        qBypassed = qApproach;
        qCaptured = 0.0;
        qMax = 10000000000.0;
        if ((inlet.flowLimit > 0.0))
        {
            qMax = inlet.flowLimit;
        }
        for (i = 1; (i <= inlet.numInlets); i++)
        {
            qc = (getOnGradeInletCapture(inlet.designIndex, qBypassed, d) * inlet.clogFactor);
            qc = (((((qc) <= (qMax))) ? (qc) : (qMax)));
            qc = (((((qc) <= (qBypassed))) ? (qc) : (qBypassed)));
            qCaptured += qc;
            qBypassed -= qc;
            if ((qBypassed < 0.001))
            {
                break;
            }
        }
        return (qCaptured *= Nsides);
    }

    // inlet.c:1325
    static double getOnGradeInletCapture(int i, double Q, double d)
    {
        double Q1 = Q;
        double Qc = 0.0;
        double Lsweep = 0.0;
        double Lcurb = 0.0;
        double Lgrate = 0.0;
        if ((InletDesigns[i].type == DROP_CURB_INLET))
        {
            Qc = getOnSagInletCapture(i, d);
            return (((((Qc) <= (Q))) ? (Qc) : (Q)));
        }
        if ((InletDesigns[i].type == DROP_GRATE_INLET))
        {
            Qc = getGrateInletCapture(i, Q);
            return (((((Qc) <= (Q))) ? (Qc) : (Q)));
        }
        T = getFlowSpread(Q);
        if ((InletDesigns[i].type == SLOTTED_INLET))
        {
            Qc = getCurbInletCapture(Q, InletDesigns[i].slottedInlet.length);
            return (((((Qc) <= (Q))) ? (Qc) : (Q)));
        }
        Lcurb = InletDesigns[i].curbInlet.length;
        Lgrate = InletDesigns[i].grateInlet.length;
        if ((Lcurb > 0.0))
        {
            Lsweep = (Lcurb - Lgrate);
            if ((Lsweep > 0.0))
            {
                Qc = getCurbInletCapture(Q1, Lsweep);
                Q1 -= Qc;
            }
        }
        if (((Lgrate > 0.0) && (Q1 > 0.0)))
        {
            if ((Q1 != Q))
            {
                T = getFlowSpread(Q1);
            }
            Qc += (getGrateInletCapture(i, Q1));
        }
        return Qc;
    }

    // inlet.c:1387
    static double getGrateInletCapture(int i, double Q)
    {
        int grateType = 0;
        double Lg = 0.0;
        double Wg = 0.0;
        double A = 0.0;
        double Y = 0.0;
        double Eo = 0.0;
        double V = 0.0;
        double Vo = 0.0;
        double Qo = Q;
        double Rf = 1.0;
        double Rs = 0.0;
        Lg = InletDesigns[i].grateInlet.length;
        Wg = InletDesigns[i].grateInlet.width;
        if (((xsect.type == TRAPEZOIDAL) || (xsect.type == RECT_OPEN)))
        {
            A = xsect_getAofS(xsect, (Q / Beta));
            Y = xsect_getYofA(xsect, A);
            T = xsect_getWofY(xsect, Y);
            Eo = (((Beta * Glibm.pow((Y * Wg), 1.67)) / Glibm.pow((Wg + (2 * Y)), 0.67)) / Q);
            if ((((Wg > (0.99 * xsect.yBot)) && (xsect.type == TRAPEZOIDAL)) && (xsect.sBot > 0.0)))
            {
                Wg = xsect.yBot;
                Sx = (1.0 / xsect.sBot);
            }
        }
        else if ((a == 0.0))
        {
            A = (((T * T) * Sx) / 2.0);
            Eo = getGutterFlowRatio(Wg);
            if ((T >= Tcrown))
            {
                Qo = (Qfactor * Glibm.pow(Tcrown, 2.67));
            }
        }
        else
        {
            if ((T <= W))
            {
                A = (((T * T) * Sw) / 2.0);
            }
            else
            {
                A = (((((T * T) * Sx) + (a * W))) / 2.0);
            }
            Eo = getGutterFlowRatio(W);
            if ((Eo < 1.0))
            {
                if ((T >= Tcrown))
                {
                    Qo = ((Qfactor * Glibm.pow(Tcrown, 2.67)) / ((1.0 - Eo)));
                }
                Eo = (Eo * getGutterAreaRatio(Wg, A));
            }
        }
        V = (Qo / A);
        grateType = InletDesigns[i].grateInlet.type;
        if (((grateType < 0) || (grateType == GENERIC)))
        {
            Vo = InletDesigns[i].grateInlet.splashVeloc;
        }
        else
        {
            Vo = getSplashOverVelocity(grateType, Lg);
        }
        if ((V > Vo))
        {
            Rf = (1.0 - (0.09 * ((V - Vo))));
        }
        if ((Eo < 1.0))
        {
            Rs = (1.0 / ((1.0 + ((((0.15 * Glibm.pow(V, 1.8)) / Sx) / Glibm.pow(Lg, 2.3))))));
        }
        return (Q * (((Rf * Eo) + (Rs * ((1.0 - Eo))))));
    }

    // inlet.c:1477
    static double getCurbInletCapture(double Q, double L)
    {
        double Se = Sx;
        double Lt = 0.0;
        double Sr = 0.0;
        double Eo = 0.0;
        double E = 1.0;
        if ((a > 0.0))
        {
            Sr = (Sw / Sx);
            Eo = getEo(Sr, (T - W), W);
            Se = (Sx + (((a / W)) * Eo));
        }
        Lt = (((0.6 * Glibm.pow(Q, 0.42)) * Glibm.pow(SL, 0.3)) * Glibm.pow((1.0 / ((n * Se))), 0.6));
        if ((L < Lt))
        {
            E = (1.0 - ((L / Lt)));
            E = (1 - Glibm.pow(E, 1.8));
        }
        E = (((((E) <= (1.0))) ? (E) : (1.0)));
        E = (((((E) >= (0.0))) ? (E) : (0.0)));
        return (E * Q);
    }

    // inlet.c:1518
    static double getGutterFlowRatio(double w)
    {
        if ((T <= w))
        {
            return 1.0;
        }
        else if ((a > 0.0))
        {
            return getEo((Sw / Sx), (T - w), w);
        }
        else
        {
            return (1.0 - Glibm.pow(((1.0 - (w / T))), 2.67));
        }
    }

    // inlet.c:1535
    static double getGutterAreaRatio(double Wg, double A)
    {
        double As = 0.0;
        double Ag = 0.0;
        if ((Wg >= W))
        {
            return 1.0;
        }
        if ((T <= Wg))
        {
            return 1.0;
        }
        if ((T <= W))
        {
            return (Wg / T);
        }
        As = ((0.5 * (((((T - W))) * (((T - W)))))) * Sx);
        Ag = (Wg * (((((T * Sx)) + a) - (((Wg * Sw) / 2.0)))));
        return (Ag / ((A - As)));
    }

    // inlet.c:1557
    static double getSplashOverVelocity(int grateType, double L)
    {
        return (((SplashCoeffs[grateType][0] + (SplashCoeffs[grateType][1] * L)) - ((SplashCoeffs[grateType][2] * L) * L)) + (((SplashCoeffs[grateType][3] * L) * L) * L));
    }

    // inlet.c:1574
    static double getOnSagCapturedFlow(TInlet inlet, double q, double d)
    {
        int linkIndex = 0;
        int designIndex = 0;
        int totalInlets = 0;
        double qCaptured = 0.0;
        double qMax = 10000000000.0;
        if ((inlet.numInlets == 0))
        {
            return 0.0;
        }
        totalInlets = (Nsides * inlet.numInlets);
        linkIndex = inlet.linkIndex;
        designIndex = inlet.designIndex;
        getConduitGeometry(inlet);
        if ((inlet.flowLimit > 0.0))
        {
            qMax = inlet.flowLimit;
        }
        qCaptured = getOnSagInletCapture(designIndex, Math.abs(d));
        qCaptured *= inlet.clogFactor;
        qCaptured = (((((qCaptured) <= (qMax))) ? (qCaptured) : (qMax)));
        qCaptured *= ((double) (totalInlets));
        return qCaptured;
    }

    // inlet.c:1610
    static double getOnSagInletCapture(int i, double d)
    {
        double Lsweep = 0.0;
        double Lcurb = 0.0;
        double Lgrate = 0.0;
        DBox Qsw = new DBox(0.0);
        DBox Qso = new DBox(0.0);
        DBox Qgw = new DBox(0.0);
        DBox Qgo = new DBox(0.0);
        DBox Qcw = new DBox(0.0);
        DBox Qco = new DBox(0.0);
        if ((InletDesigns[i].slottedInlet.length > 0.0))
        {
            return getOnSagSlottedFlow(i, d);
        }
        Lgrate = InletDesigns[i].grateInlet.length;
        if ((Lgrate > 0.0))
        {
            findOnSagGrateFlows(i, d, Qgw, Qgo);
        }
        Lcurb = InletDesigns[i].curbInlet.length;
        if ((Lcurb > 0.0))
        {
            Lsweep = (Lcurb - Lgrate);
            if ((Lsweep > 0.0))
            {
                findOnSagCurbFlows(i, d, Lsweep, Qsw, Qso);
            }
            if ((Qgo.v > 0.0))
            {
                findOnSagCurbFlows(i, d, Lgrate, Qcw, Qco);
            }
        }
        return ((((Qgw.v + Qgo.v) + Qsw.v) + Qso.v) + Qco.v);
    }

    // inlet.c:1644
    static void findOnSagGrateFlows(int i, double d, DRef Qw, DRef Qo)
    {
        int grateType = InletDesigns[i].grateInlet.type;
        double Lg = InletDesigns[i].grateInlet.length;
        double Wg = InletDesigns[i].grateInlet.width;
        double P = 0.0;
        double Ao = 0.0;
        double di = 0.0;
        if ((InletDesigns[i].type == DROP_GRATE_INLET))
        {
            di = d;
            P = (2.0 * ((Lg + Wg)));
        }
        else
        {
            if ((d <= (Wg * Sw)))
            {
                Wg = (d / Sw);
            }
            di = (d - (((Wg / 2.0)) * Sw));
            P = (Lg + (2.0 * Wg));
        }
        if ((grateType == GENERIC))
        {
            Ao = ((Lg * Wg) * InletDesigns[i].grateInlet.fracOpenArea);
        }
        else
        {
            Ao = ((Lg * Wg) * GrateOpeningRatios[grateType]);
        }
        if ((d <= ((1.79 * Ao) / P)))
        {
            Qw.set(((3.0 * P) * Glibm.pow(di, 1.5)));
        }
        else
        {
            Qo.set(((0.67 * Ao) * Math.sqrt(((2.0 * 32.16) * di))));
        }
    }

    // inlet.c:1703
    static void findOnSagCurbFlows(int i, double d, double L, DRef Qw, DRef Qo)
    {
        int throatAngle = InletDesigns[i].curbInlet.throatAngle;
        double h = InletDesigns[i].curbInlet.height;
        double Qweir = 0.0;
        double Qorif = 0.0;
        double P = 0.0;
        double dweir = 0.0;
        double dorif = 0.0;
        double r = 0.0;
        if ((L <= 0.0))
        {
            return;
        }
        if ((InletDesigns[i].type == DROP_CURB_INLET))
        {
            L = (L * 4.0);
        }
        dorif = (1.4 * h);
        if ((d > dorif))
        {
            Qo.set(getCurbOrificeFlow(d, h, L, throatAngle));
            return;
        }
        if (((a == 0.0) || (L > 12.0)))
        {
            dweir = h;
            if ((d < dweir))
            {
                Qw.set(((3.0 * L) * Glibm.pow(d, 1.5)));
                return;
            }
            else
            {
                Qweir = ((3.0 * L) * Glibm.pow(dweir, 1.5));
            }
        }
        else
        {
            P = (L + (1.8 * W));
            dweir = (h + a);
            if ((d < dweir))
            {
                Qw.set(((2.3 * P) * Glibm.pow(d, 1.5)));
                return;
            }
            else
            {
                Qweir = ((2.3 * P) * Glibm.pow(dweir, 1.5));
            }
        }
        Qorif = getCurbOrificeFlow(dorif, h, L, throatAngle);
        r = (((d - dweir)) / ((dorif - dweir)));
        Qw.set((((1.0 - r)) * Qweir));
        Qo.set((r * Qorif));
    }

    // inlet.c:1764
    static double getCurbOrificeFlow(double di, double h, double L, int throatAngle)
    {
        double d = di;
        if ((throatAngle == HORIZONTAL_THROAT))
        {
            d = (di - (h / 2.0));
        }
        else if ((throatAngle == INCLINED_THROAT))
        {
            d = (di - (((h / 2.0)) * 0.7071));
        }
        return (((0.67 * h) * L) * Math.sqrt(((2.0 * 32.16) * d)));
    }

    // inlet.c:1785
    static double getOnSagSlottedFlow(int i, double d)
    {
        double L = InletDesigns[i].slottedInlet.length;
        double w = InletDesigns[i].slottedInlet.width;
        if ((d <= (2.587 * w)))
        {
            return ((2.48 * L) * Glibm.pow(d, 1.5));
        }
        else
        {
            return (((0.8 * L) * w) * Math.sqrt((64.32 * d)));
        }
    }

    // inlet.c:1805
    static void getBackflowRatios()
    {
        TInlet inlet = null;
        double area = 0.0;
        double f = 0.0;
        int n = 0;
        TInletNode[] inletNodes = R.fill(new TInletNode[(int) (Nobjects[NODE])], TInletNode::new);
        if ((inletNodes == null))
        {
            return;
        }
        for (inlet = FirstInlet; (inlet != null); inlet = inlet.nextInlet)
        {
            n = inlet.nodeIndex;
            inletNodes[n].numInletLinks++;
            area = getInletArea(inlet);
            if ((area > 0.0))
            {
                inletNodes[n].numStdInletLinks++;
                inletNodes[n].totalInletArea += area;
            }
            else
            {
                inletNodes[n].numCustomInlets += inlet.numInlets;
            }
        }
        for (inlet = FirstInlet; (inlet != null); inlet = inlet.nextInlet)
        {
            n = inlet.nodeIndex;
            f = (((double) (inletNodes[n].numStdInletLinks)) / ((double) (inletNodes[n].numInletLinks)));
            area = getInletArea(inlet);
            if ((area == 0.0))
            {
                inlet.backflowRatio = ((((double) (inlet.numInlets)) / ((double) (inletNodes[n].numCustomInlets))) * ((1.0 - f)));
            }
            else
            {
                inlet.backflowRatio = ((area / inletNodes[n].totalInletArea) * f);
            }
        }
        R.free(inletNodes);
    }

    // inlet.c:1870
    static double getInletArea(TInlet inlet)
    {
        double area = 0.0;
        double curbLength = 0.0;
        int i = inlet.designIndex;
        int grateType = InletDesigns[i].grateInlet.type;
        if ((InletDesigns[i].grateInlet.length > 0.0))
        {
            area = (InletDesigns[i].grateInlet.length * InletDesigns[i].grateInlet.width);
            if ((grateType == GENERIC))
            {
                area *= InletDesigns[i].grateInlet.fracOpenArea;
            }
            else
            {
                area *= GrateOpeningRatios[grateType];
            }
        }
        curbLength = (InletDesigns[i].curbInlet.length - InletDesigns[i].grateInlet.length);
        if ((curbLength > 0.0))
        {
            area += (curbLength * InletDesigns[i].curbInlet.height);
        }
        if ((InletDesigns[i].slottedInlet.length > 0.0))
        {
            area = (InletDesigns[i].slottedInlet.length * InletDesigns[i].slottedInlet.width);
        }
        return ((area * inlet.numInlets) * inlet.clogFactor);
    }

    // inlet.c:1902
    static double getCustomCapturedFlow(TInlet inlet, double q, double d)
    {
        int i = inlet.designIndex;
        int j = 0;
        int sides = 1;
        int c = 0;
        double qApproach = 0.0;
        double qBypassed = 0.0;
        double qCaptured = 0.0;
        double qIncrement = 0.0;
        double qMax = 10000000000.0;
        if ((inlet.numInlets == 0))
        {
            return 0.0;
        }
        qMax = 10000000000.0;
        if ((inlet.flowLimit > 0.0))
        {
            qMax = inlet.flowLimit;
        }
        xsect = Link[inlet.linkIndex].xsect;
        if ((xsect.type == STREET_XSECT))
        {
            sides = Street[xsect.transect].sides;
        }
        qApproach = (q / sides);
        qBypassed = qApproach;
        qCaptured = 0.0;
        c = InletDesigns[i].customCurve;
        if ((c >= 0))
        {
            if ((Curve[c].curveType == DIVERSION_CURVE))
            {
                for (j = 1; (j <= inlet.numInlets); j++)
                {
                    qIncrement = ((inlet.clogFactor * table_lookupEx(Curve[c], (qBypassed * UCF(FLOW)))) / UCF(FLOW));
                    qIncrement = (((((qIncrement) <= (qMax))) ? (qIncrement) : (qMax)));
                    qIncrement = (((((qIncrement) <= (qBypassed))) ? (qIncrement) : (qBypassed)));
                    qCaptured += qIncrement;
                    qBypassed -= qIncrement;
                    if ((qBypassed < 0.001))
                    {
                        break;
                    }
                }
            }
            else if ((Curve[c].curveType == RATING_CURVE))
            {
                qCaptured = (((inlet.numInlets * inlet.clogFactor) * table_lookupEx(Curve[c], (d * UCF(LENGTH)))) / UCF(FLOW));
            }
            qCaptured *= sides;
        }
        return qCaptured;
    }

}
