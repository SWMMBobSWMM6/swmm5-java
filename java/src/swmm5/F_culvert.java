// Generated from EPA SWMM 5.2.4 (culvert.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_culvert {
    static int MAX_CULVERT_CODE = 57;
    static double[][] Params = new double[][] { new double[] { 0.0, 0.0, 0.0, 0.0, 0.00 }, new double[] { 1.0, 0.0098, 2.00, 0.0398, 0.67 }, new double[] { 1.0, 0.0018, 2.00, 0.0292, 0.74 }, new double[] { 1.0, 0.0045, 2.00, 0.0317, 0.69 }, new double[] { 1.0, 0.0078, 2.00, 0.0379, 0.69 }, new double[] { 1.0, 0.0210, 1.33, 0.0463, 0.75 }, new double[] { 1.0, 0.0340, 1.50, 0.0553, 0.54 }, new double[] { 1.0, 0.0018, 2.50, 0.0300, 0.74 }, new double[] { 1.0, 0.0018, 2.50, 0.0243, 0.83 }, new double[] { 1.0, 0.026, 1.0, 0.0347, 0.81 }, new double[] { 1.0, 0.061, 0.75, 0.0400, 0.80 }, new double[] { 1.0, 0.061, 0.75, 0.0423, 0.82 }, new double[] { 2.0, 0.510, 0.667, 0.0309, 0.80 }, new double[] { 2.0, 0.486, 0.667, 0.0249, 0.83 }, new double[] { 2.0, 0.515, 0.667, 0.0375, 0.79 }, new double[] { 2.0, 0.495, 0.667, 0.0314, 0.82 }, new double[] { 2.0, 0.486, 0.667, 0.0252, 0.865 }, new double[] { 2.0, 0.545, 0.667, 0.04505, 0.73 }, new double[] { 2.0, 0.533, 0.667, 0.0425, 0.705 }, new double[] { 2.0, 0.522, 0.667, 0.0402, 0.68 }, new double[] { 2.0, 0.498, 0.667, 0.0327, 0.75 }, new double[] { 2.0, 0.497, 0.667, 0.0339, 0.803 }, new double[] { 2.0, 0.493, 0.667, 0.0361, 0.806 }, new double[] { 2.0, 0.495, 0.667, 0.0386, 0.71 }, new double[] { 2.0, 0.497, 0.667, 0.0302, 0.835 }, new double[] { 2.0, 0.495, 0.667, 0.0252, 0.881 }, new double[] { 2.0, 0.493, 0.667, 0.0227, 0.887 }, new double[] { 1.0, 0.0083, 2.00, 0.0379, 0.69 }, new double[] { 1.0, 0.0145, 1.75, 0.0419, 0.64 }, new double[] { 1.0, 0.0340, 1.50, 0.0496, 0.57 }, new double[] { 1.0, 0.0100, 2.00, 0.0398, 0.67 }, new double[] { 1.0, 0.0018, 2.50, 0.0292, 0.74 }, new double[] { 1.0, 0.0045, 2.00, 0.0317, 0.69 }, new double[] { 1.0, 0.0100, 2.00, 0.0398, 0.67 }, new double[] { 1.0, 0.0018, 2.50, 0.0292, 0.74 }, new double[] { 1.0, 0.0095, 2.00, 0.0317, 0.69 }, new double[] { 1.0, 0.0083, 2.00, 0.0379, 0.69 }, new double[] { 1.0, 0.0300, 1.00, 0.0463, 0.75 }, new double[] { 1.0, 0.0340, 1.50, 0.0496, 0.57 }, new double[] { 1.0, 0.0300, 1.50, 0.0496, 0.57 }, new double[] { 1.0, 0.0088, 2.00, 0.0368, 0.68 }, new double[] { 1.0, 0.0030, 2.00, 0.0269, 0.77 }, new double[] { 1.0, 0.0300, 1.50, 0.0496, 0.57 }, new double[] { 1.0, 0.0088, 2.00, 0.0368, 0.68 }, new double[] { 1.0, 0.0030, 2.00, 0.0269, 0.77 }, new double[] { 1.0, 0.0083, 2.00, 0.0379, 0.69 }, new double[] { 1.0, 0.0300, 1.00, 0.0473, 0.75 }, new double[] { 1.0, 0.0340, 1.50, 0.0496, 0.57 }, new double[] { 2.0, 0.534, 0.555, 0.0196, 0.90 }, new double[] { 2.0, 0.519, 0.640, 0.0210, 0.90 }, new double[] { 2.0, 0.536, 0.622, 0.0368, 0.83 }, new double[] { 2.0, 0.5035, 0.719, 0.0478, 0.80 }, new double[] { 2.0, 0.547, 0.800, 0.0598, 0.75 }, new double[] { 2.0, 0.475, 0.667, 0.0179, 0.97 }, new double[] { 2.0, 0.560, 0.667, 0.0446, 0.85 }, new double[] { 2.0, 0.560, 0.667, 0.0378, 0.87 }, new double[] { 2.0, 0.500, 0.667, 0.0446, 0.65 }, new double[] { 2.0, 0.500, 0.667, 0.0378, 0.71 } };

    // culvert.c:174
    static double culvert_getInflow(int j, double q0, double h)
    {
        int code = 0;
        int k = 0;
        int condition = 0;
        double y = 0.0;
        double y1 = 0.0;
        double y2 = 0.0;
        double q = 0.0;
        TCulvert culvert = new TCulvert();
        if ((Link[j].type != CONDUIT))
        {
            return q0;
        }
        culvert.xsect = Link[j].xsect;
        code = culvert.xsect.culvertCode;
        if (((code <= 0) || (code > MAX_CULVERT_CODE)))
        {
            return q0;
        }
        k = Link[j].subIndex;
        culvert.yFull = culvert.xsect.yFull;
        culvert.ad = (culvert.xsect.aFull * Math.sqrt(culvert.yFull));
        switch (code) {
            case 5:
            case 37:
            case 46:
                culvert.scf = ((-7.0) * Conduit[k].slope);
                break;
            default:
                culvert.scf = (0.5 * Conduit[k].slope);
        }
        y = (h - ((Node[Link[j].node1].invertElev + Link[j].offset1)));
        y2 = (culvert.yFull * ((((16.0 * Params[code][C]) + Params[code][Y]) - culvert.scf)));
        if ((y >= y2))
        {
            q = getSubmergedFlow(code, y, culvert);
            condition = 2;
        }
        else
        {
            y1 = (0.95 * culvert.yFull);
            if ((y <= y1))
            {
                q = getUnsubmergedFlow(code, y, culvert);
                condition = 1;
            }
            else
            {
                q = getTransitionFlow(code, y, y1, y2, culvert);
                condition = 0;
            }
        }
        if ((q < q0))
        {
            Link[j].inletControl = 1;
            Link[j].dqdh = culvert.dQdH;
            return q;
        }
        else
        {
            return q0;
        }
    }

    // culvert.c:257
    static double getUnsubmergedFlow(int code, double h, TCulvert culvert)
    {
        double arg = 0.0;
        double q = 0.0;
        culvert.kk = Params[code][K];
        culvert.mm = Params[code][M];
        arg = ((h / culvert.yFull) / culvert.kk);
        if ((Params[code][FORM] == 1.0))
        {
            q = getForm1Flow(h, culvert);
        }
        else
        {
            q = (culvert.ad * Glibm.pow(arg, (1.0 / culvert.mm)));
        }
        culvert.dQdH = ((q / h) / culvert.mm);
        return q;
    }

    // culvert.c:288
    static double getSubmergedFlow(int code, double h, TCulvert culvert)
    {
        double cc = Params[code][C];
        double yy = Params[code][Y];
        double arg = (((((h / culvert.yFull) - yy) + culvert.scf)) / cc);
        double q = 0.0;
        if ((arg <= 0.0))
        {
            culvert.dQdH = 0.0;
            return 10000000000.0;
        }
        q = (Math.sqrt(arg) * culvert.ad);
        culvert.dQdH = ((((0.5 * q) / arg) / culvert.yFull) / cc);
        return q;
    }

    // culvert.c:316
    static double getTransitionFlow(int code, double h, double h1, double h2, TCulvert culvert)
    {
        double q1 = getUnsubmergedFlow(code, h1, culvert);
        double q2 = getSubmergedFlow(code, h2, culvert);
        double q = (q1 + ((((q2 - q1)) * ((h - h1))) / ((h2 - h1))));
        culvert.dQdH = (((q2 - q1)) / ((h2 - h1)));
        return q;
    }

    // culvert.c:339
    static double getForm1Flow(double h, TCulvert culvert)
    {
        culvert.hPlus = ((h / culvert.yFull) + culvert.scf);
        findroot_Ridder((0.01 * h), h, 0.001, F_culvert::form1Eqn, culvert);
        return culvert.qc;
    }

    // culvert.c:363
    static double form1Eqn(double yc, Object p)
    {
        double ac = 0.0;
        double wc = 0.0;
        double yh = 0.0;
        TCulvert culvert = ((TCulvert) p);
        ac = xsect_getAofY(culvert.xsect, yc);
        wc = xsect_getWofY(culvert.xsect, yc);
        yh = (ac / wc);
        culvert.qc = (ac * Math.sqrt((32.2 * yh)));
        return (((culvert.hPlus - (yc / culvert.yFull)) - ((yh / 2.0) / culvert.yFull)) - (culvert.kk * Glibm.pow((culvert.qc / culvert.ad), culvert.mm)));
    }

}
