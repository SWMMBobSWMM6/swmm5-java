// Generated from EPA SWMM 5.2.4 (qualrout.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_qualrout {
    static double ZeroVolume = 0.0353147;
    static double ZeroDepth = 0.003281;

    // qualrout.c:63
    static void qualrout_init()
    {
        int i = 0;
        int p = 0;
        int isWet = 0;
        double c = 0.0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            isWet = (((Node[i].newDepth > ZeroDepth)) ? 1 : 0);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                c = 0.0;
                if ((isWet != 0))
                {
                    c = Pollut[p].initConcen;
                }
                Node[i].oldQual[p] = c;
                Node[i].newQual[p] = c;
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            isWet = (((Link[i].newDepth > ZeroDepth)) ? 1 : 0);
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                c = 0.0;
                if ((isWet != 0))
                {
                    c = Pollut[p].initConcen;
                }
                Link[i].oldQual[p] = c;
                Link[i].newQual[p] = c;
            }
        }
    }

    // qualrout.c:100
    static void qualrout_execute(double tStep)
    {
        int i = 0;
        int j = 0;
        double qIn = 0.0;
        double vAvg = 0.0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            findLinkMassFlow(i, tStep);
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            Node[j].qualInflow = Node[j].inflow;
            qIn = Node[j].qualInflow;
            vAvg = (((Node[j].oldVolume + Node[j].newVolume)) / 2.0);
            if ((Node[j].treatment != null))
            {
                if ((qIn < 1e-10))
                {
                    qIn = 0.0;
                }
                treatmnt_setInflow(qIn, Node[j].newQual);
            }
            if (((Node[j].type == STORAGE) || (Node[j].oldVolume > ZeroVolume)))
            {
                findStorageQual(j, tStep);
            }
            else
            {
                findNodeQual(j);
            }
            if ((Node[j].treatment != null))
            {
                treatmnt_treat(j, qIn, vAvg, tStep);
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            findLinkQual(i, tStep);
        }
    }

    // qualrout.c:146
    static double getMixedQual(double c, double v1, double wIn, double qIn, double tStep)
    {
        double vIn = 0.0;
        double cIn = 0.0;
        double cMax = 0.0;
        if ((qIn <= 1e-10))
        {
            return c;
        }
        vIn = (qIn * tStep);
        cIn = ((wIn * tStep) / vIn);
        cMax = (((((c) >= (cIn))) ? (c) : (cIn)));
        c = ((((c * v1) + (wIn * tStep))) / ((v1 + vIn)));
        c = (((((c) <= (cMax))) ? (c) : (cMax)));
        c = (((((c) >= (0.0))) ? (c) : (0.0)));
        return c;
    }

    // qualrout.c:179
    static void findLinkMassFlow(int i, double tStep)
    {
        int j = 0;
        int p = 0;
        double qLink = 0.0;
        double w = 0.0;
        qLink = Link[i].newFlow;
        j = Link[i].node2;
        if ((qLink < 0.0))
        {
            j = Link[i].node1;
        }
        qLink = Math.abs(qLink);
        if ((RouteModel != DW))
        {
            qLink -= (inlet_capturedFlow(i));
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            w = (qLink * Link[i].oldQual[p]);
            Node[j].newQual[p] += w;
            Link[i].totalLoad[p] += (w * tStep);
        }
        Node[j].qualInflow += qLink;
    }

    // qualrout.c:221
    static void findNodeQual(int j)
    {
        int p = 0;
        double qNode = 0.0;
        qNode = Node[j].qualInflow;
        if ((qNode > 1e-10))
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                Node[j].newQual[p] /= qNode;
            }
        }
        else
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                if ((Node[j].newDepth > ZeroDepth))
                {
                    Node[j].newQual[p] = Node[j].oldQual[p];
                }
                else
                {
                    Node[j].newQual[p] = 0.0;
                }
            }
        }
    }

    // qualrout.c:253
    static void findLinkQual(int i, double tStep)
    {
        int j = 0;
        int k = 0;
        int p = 0;
        double wIn = 0.0;
        double qIn = 0.0;
        double qSeep = 0.0;
        double v1 = 0.0;
        double v2 = 0.0;
        double c1 = 0.0;
        double c2 = 0.0;
        double vEvap = 0.0;
        double vLosses = 0.0;
        double fEvap = 0.0;
        double barrels = 0.0;
        j = Link[i].node1;
        if ((Link[i].newFlow < 0.0))
        {
            j = Link[i].node2;
        }
        if (((Link[i].type != CONDUIT) || (Link[i].xsect.type == DUMMY)))
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                Link[i].newQual[p] = Node[j].newQual[p];
            }
            return;
        }
        k = Link[i].subIndex;
        barrels = Conduit[k].barrels;
        qIn = (Math.abs(Conduit[k].q1) * barrels);
        qSeep = (Conduit[k].seepLossRate * barrels);
        vEvap = ((Conduit[k].evapLossRate * barrels) * tStep);
        v1 = Link[i].oldVolume;
        v2 = Link[i].newVolume;
        vLosses = ((qSeep * tStep) + vEvap);
        fEvap = 1.0;
        if (((vEvap > 0.0) && (v1 > ZeroVolume)))
        {
            fEvap += (vEvap / v1);
        }
        if ((RouteModel == SF))
        {
            findSFLinkQual(i, qSeep, fEvap, tStep);
            return;
        }
        if ((RouteModel == DW))
        {
            qIn = (qIn + ((((v2 + vLosses) - v1)) / tStep));
            qIn = (((((qIn) >= (0.0))) ? (qIn) : (0.0)));
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            c1 = Link[i].oldQual[p];
            massbal_addSeepageLoss(p, (qSeep * c1));
            c1 *= fEvap;
            c2 = getReactedQual(p, c1, v1, tStep);
            wIn = (Node[j].newQual[p] * qIn);
            c2 = getMixedQual(c2, v1, wIn, qIn, tStep);
            if (((v2 < ZeroVolume) || (Link[i].newDepth <= ZeroDepth)))
            {
                massbal_addToFinalStorage(p, (c2 * v2));
                c2 = 0.0;
            }
            Link[i].newQual[p] = c2;
        }
    }

    // qualrout.c:357
    static void findSFLinkQual(int i, double qSeep, double fEvap, double tStep)
    {
        int j = Link[i].node1;
        int p = 0;
        double c1 = 0.0;
        double c2 = 0.0;
        double lossRate = 0.0;
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            c1 = Node[j].newQual[p];
            massbal_addSeepageLoss(p, (qSeep * c1));
            c1 *= fEvap;
            c2 = c1;
            if ((Pollut[p].kDecay > 0.0))
            {
                c2 = (c1 * Glibm.exp(((-Pollut[p].kDecay) * tStep)));
                c2 = (((((0.0) >= (c2))) ? (0.0) : (c2)));
                lossRate = (((c1 - c2)) * Link[i].newFlow);
                massbal_addReactedMass(p, lossRate);
            }
            Link[i].newQual[p] = c2;
        }
    }

    // qualrout.c:398
    static void findStorageQual(int j, double tStep)
    {
        int p = 0;
        int k = 0;
        double qIn = 0.0;
        double wIn = 0.0;
        double v1 = 0.0;
        double c1 = 0.0;
        double c2 = 0.0;
        double qExfil = 0.0;
        double vEvap = 0.0;
        double fEvap = 1.0;
        qIn = Node[j].qualInflow;
        v1 = Node[j].oldVolume;
        if ((Node[j].type == STORAGE))
        {
            updateHRT(j, Node[j].oldVolume, qIn, tStep);
            k = Node[j].subIndex;
            qExfil = (Storage[k].exfilLoss / tStep);
            vEvap = Storage[k].evapLoss;
            if (((vEvap > 0.0) && (v1 > ZeroVolume)))
            {
                fEvap += (vEvap / v1);
            }
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            c1 = Node[j].oldQual[p];
            massbal_addSeepageLoss(p, (qExfil * c1));
            c1 *= fEvap;
            if (((Node[j].treatment == null) || (Node[j].treatment[p].equation == null)))
            {
                c1 = getReactedQual(p, c1, v1, tStep);
            }
            wIn = Node[j].newQual[p];
            c2 = getMixedQual(c1, v1, wIn, qIn, tStep);
            if (((((Node[j].newVolume <= ZeroVolume) || (Node[j].newDepth <= ZeroDepth))) && (qIn <= 1e-10)))
            {
                massbal_addToFinalStorage(p, (c2 * Node[j].newVolume));
                c2 = 0.0;
            }
            Node[j].newQual[p] = c2;
        }
    }

    // qualrout.c:478
    static void updateHRT(int j, double v, double q, double tStep)
    {
        int k = Node[j].subIndex;
        double hrt = Storage[k].hrt;
        if ((v < 1e-10))
        {
            hrt = 0.0;
        }
        else
        {
            hrt = ((((hrt + tStep)) * v) / ((v + (q * tStep))));
        }
        Storage[k].hrt = (((((hrt) >= (0.0))) ? (hrt) : (0.0)));
    }

    // qualrout.c:498
    static double getReactedQual(int p, double c, double v1, double tStep)
    {
        double c2 = 0.0;
        double lossRate = 0.0;
        double kDecay = Pollut[p].kDecay;
        if ((kDecay == 0.0))
        {
            return c;
        }
        c2 = (c * ((1.0 - (kDecay * tStep))));
        c2 = (((((0.0) >= (c2))) ? (0.0) : (c2)));
        lossRate = ((((c - c2)) * v1) / tStep);
        massbal_addReactedMass(p, lossRate);
        return c2;
    }

}
