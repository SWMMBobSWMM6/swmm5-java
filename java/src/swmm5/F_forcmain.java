// Generated from EPA SWMM 5.2.4 (forcmain.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_forcmain {
    static double VISCOS = 1.1E-5;

    // forcmain.c:37
    static double forcemain_getEquivN(int j, int k)
    {
        TXsect xsect = Link[j].xsect.copy();
        double f = 0.0;
        double d = xsect.yFull;
        switch (ForceMainEqn) {
            case H_W:
                return ((1.067 / xsect.rBot) * Glibm.pow((d / Conduit[k].slope), 0.04));
            case D_W:
                f = forcemain_getFricFactor(xsect.rBot, (d / 4.0), 1.0e12);
                return (Math.sqrt((f / 185.0)) * Glibm.pow(d, ((1.0 / 6.0))));
        }
        return Conduit[k].roughness;
    }

    // forcmain.c:64
    static double forcemain_getRoughFactor(int j, double lengthFactor)
    {
        TXsect xsect = Link[j].xsect.copy();
        double r = 0.0;
        switch (ForceMainEqn) {
            case H_W:
                r = ((1.318 * xsect.rBot) * Glibm.pow(lengthFactor, 0.54));
                return (32.2 / Glibm.pow(r, 1.852));
            case D_W:
                return ((1.0 / 8.0) / lengthFactor);
        }
        return 0.0;
    }

    // forcmain.c:88
    static double forcemain_getFricSlope(int j, double v, double hrad)
    {
        double re = 0.0;
        double f = 0.0;
        TXsect xsect = Link[j].xsect.copy();
        switch (ForceMainEqn) {
            case H_W:
                return ((xsect.sBot * Glibm.pow(v, 0.852)) / Glibm.pow(hrad, 1.1667));
            case D_W:
                re = forcemain_getReynolds(v, hrad);
                f = forcemain_getFricFactor(xsect.rBot, hrad, re);
                return (((f * xsect.sBot) * v) / hrad);
        }
        return 0.0;
    }

    // forcmain.c:117
    static double forcemain_getReynolds(double v, double hrad)
    {
        return (((4.0 * hrad) * v) / VISCOS);
    }

    // forcmain.c:130
    static double forcemain_getFricFactor(double e, double hrad, double re)
    {
        double f = 0.0;
        if ((re < 10.0))
        {
            re = 10.0;
        }
        if ((re <= 2000.0))
        {
            f = (64.0 / re);
        }
        else if ((re < 4000.0))
        {
            f = forcemain_getFricFactor(e, hrad, 4000.0);
            f = (0.032 + ((((f - 0.032)) * ((re - 2000.0))) / 2000.0));
        }
        else
        {
            f = ((e / 3.7) / ((4.0 * hrad)));
            if ((re < 1.0e10))
            {
                f += (5.74 / Glibm.pow(re, 0.9));
            }
            f = Glibm.log10(f);
            f = ((0.25 / f) / f);
        }
        return f;
    }

}
