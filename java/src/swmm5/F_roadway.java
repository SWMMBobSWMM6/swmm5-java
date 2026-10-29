// Generated from EPA SWMM 5.2.4 (roadway.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_roadway {
    static int N_Cr_Low_Paved = 4;
    static double[][] Cr_Low_Paved = new double[][] { new double[] { 0.0, 2.85 }, new double[] { 0.2, 2.95 }, new double[] { 0.7, 3.03 }, new double[] { 4.0, 3.05 } };
    static int N_Cr_Low_Gravel = 8;
    static double[][] Cr_Low_Gravel = new double[][] { new double[] { 0.0, 2.5 }, new double[] { 0.5, 2.7 }, new double[] { 1.0, 2.8 }, new double[] { 1.5, 2.9 }, new double[] { 2.0, 2.98 }, new double[] { 2.5, 3.02 }, new double[] { 3.0, 3.03 }, new double[] { 4.0, 3.05 } };
    static int N_Cr_High_Paved = 2;
    static double[][] Cr_High_Paved = new double[][] { new double[] { 0.15, 3.05 }, new double[] { 0.25, 3.10 } };
    static int N_Cr_High_Gravel = 2;
    static double[][] Cr_High_Gravel = new double[][] { new double[] { 0.15, 2.95 }, new double[] { 0.30, 3.10 } };
    static int N_Kt_Paved = 9;
    static double[][] Kt_Paved = new double[][] { new double[] { 0.8, 1.0 }, new double[] { 0.85, 0.98 }, new double[] { 0.90, 0.92 }, new double[] { 0.93, 0.85 }, new double[] { 0.95, 0.80 }, new double[] { 0.97, 0.70 }, new double[] { 0.98, 0.60 }, new double[] { 0.99, 0.50 }, new double[] { 1.00, 0.40 } };
    static int N_Kt_Gravel = 12;
    static double[][] Kt_Gravel = new double[][] { new double[] { 0.75, 1.00 }, new double[] { 0.80, 0.985 }, new double[] { 0.83, 0.97 }, new double[] { 0.86, 0.93 }, new double[] { 0.89, 0.90 }, new double[] { 0.90, 0.87 }, new double[] { 0.92, 0.80 }, new double[] { 0.94, 0.70 }, new double[] { 0.96, 0.60 }, new double[] { 0.98, 0.50 }, new double[] { 0.99, 0.40 }, new double[] { 1.00, 0.24 } };

    // roadway.c:85
    static double roadway_getInflow(int j, double dir, double hRoad, double h1, double h2)
    {
        int k = 0;
        int roadSurf = 0;
        int useVariableCd = 0;
        double length = 0.0;
        double roadWidth = 0.0;
        double hWr = 0.0;
        double ht = 0.0;
        double cD = 0.0;
        double q = 0.0;
        double dqdh = 0.0;
        if ((Link[j].type != WEIR))
        {
            return 0.0;
        }
        k = Link[j].subIndex;
        roadWidth = Weir[k].roadWidth;
        roadSurf = Weir[k].roadSurface;
        cD = Weir[k].cDisch1;
        if ((UnitSystem == SI))
        {
            cD = (cD / 0.552);
        }
        useVariableCd = 0;
        if (((roadWidth > 0.0) && (roadSurf >= 1)))
        {
            useVariableCd = 1;
        }
        hWr = (h1 - hRoad);
        ht = (h2 - hRoad);
        if ((hWr > 0.0001))
        {
            if ((useVariableCd != 0))
            {
                cD = getCd(hWr, ht, roadWidth, roadSurf);
            }
            length = Link[j].xsect.wMax;
            q = ((cD * length) * Glibm.pow(hWr, 1.5));
            dqdh = ((1.5 * q) / hWr);
        }
        Link[j].dqdh = dqdh;
        Link[j].newDepth = ((((((h1 - hRoad)) >= (0.0))) ? ((h1 - hRoad)) : (0.0)));
        Link[j].flowClass = SUBCRITICAL;
        if ((hRoad > h2))
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
        return (dir * q);
    }

    // roadway.c:146
    static double getCd(double hWr, double ht, double roadWidth, int roadSurf)
    {
        double kT = 1.0;
        double hL = 0.0;
        double htH = 0.0;
        double cR = 0.0;
        if ((hWr <= 0.0))
        {
            return 0.0;
        }
        hL = (hWr / roadWidth);
        if ((hL <= 0.15))
        {
            if ((roadSurf == PAVED))
            {
                cR = getY(hWr, Cr_Low_Paved, N_Cr_Low_Paved);
            }
            else
            {
                cR = getY(hWr, Cr_Low_Gravel, N_Cr_Low_Gravel);
            }
        }
        else
        {
            if ((roadSurf == PAVED))
            {
                cR = getY(hL, Cr_High_Paved, N_Cr_High_Paved);
            }
            else
            {
                cR = getY(hL, Cr_High_Gravel, N_Cr_High_Gravel);
            }
        }
        if ((ht > 0.0))
        {
            htH = (ht / hWr);
            if ((roadSurf == PAVED))
            {
                kT = getY(htH, Kt_Paved, N_Kt_Paved);
            }
            else
            {
                kT = getY(htH, Kt_Gravel, N_Kt_Gravel);
            }
        }
        return (cR * kT);
    }

    // roadway.c:176
    static double getY(double x, double[][] table, int n)
    {
        int i = 0;
        double x1 = 0.0;
        double y1 = 0.0;
        double dx = 0.0;
        double dy = 0.0;
        if ((x <= table[0][0]))
        {
            return table[0][1];
        }
        if ((x >= (table[(n - 1)])[0]))
        {
            return (table[(n - 1)])[1];
        }
        for (i = 1; (i < n); i++)
        {
            if ((x <= table[i][0]))
            {
                x1 = (table[(i - 1)])[0];
                dx = (table[i][0] - x1);
                y1 = (table[(i - 1)])[1];
                dy = (table[i][1] - y1);
                return (y1 + ((((x - x1)) * dy) / dx));
            }
        }
        return (table[(n - 1)])[1];
    }

}
