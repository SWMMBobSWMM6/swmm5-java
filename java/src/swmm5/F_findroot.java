// Generated from EPA SWMM 5.2.4 (findroot.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_findroot {
    // findroot.c:20
    static int findroot_Newton(double x1, double x2, DRef rts, double xacc, FP0 func, Object p)
    {
        int j = 0;
        int n = 0;
        DBox df = new DBox(0.0);
        double dx = 0.0;
        double dxold = 0.0;
        DBox f = new DBox(0.0);
        double x = 0.0;
        double temp = 0.0;
        double xhi = 0.0;
        double xlo = 0.0;
        x = rts.get();
        xlo = x1;
        xhi = x2;
        dxold = Math.abs((x2 - x1));
        dx = dxold;
        func.call(x, f, df, p);
        n++;
        for (j = 1; (j <= 60); j++)
        {
            if ((((((((((x - xhi)) * df.v) - f.v)) * (((((x - xlo)) * df.v) - f.v))) >= 0.0) || ((Math.abs((2.0 * f.v)) > Math.abs((dxold * df.v)))))))
            {
                dxold = dx;
                dx = (0.5 * ((xhi - xlo)));
                x = (xlo + dx);
                if ((xlo == x))
                {
                    break;
                }
            }
            else
            {
                dxold = dx;
                dx = (f.v / df.v);
                temp = x;
                x -= dx;
                if ((temp == x))
                {
                    break;
                }
            }
            if ((Math.abs(dx) < xacc))
            {
                break;
            }
            func.call(x, f, df, p);
            n++;
            if ((f.v < 0.0))
            {
                xlo = x;
            }
            else
            {
                xhi = x;
            }
        }
        rts.set(x);
        if ((n <= 60))
        {
            return n;
        }
        else
        {
            return 0;
        }
    }

    // findroot.c:91
    static double findroot_Ridder(double x1, double x2, double xacc, FP1 func, Object p)
    {
        int j = 0;
        double ans = 0.0;
        double fhi = 0.0;
        double flo = 0.0;
        double fm = 0.0;
        double fnew = 0.0;
        double s = 0.0;
        double xhi = 0.0;
        double xlo = 0.0;
        double xm = 0.0;
        double xnew = 0.0;
        flo = func.call(x1, p);
        fhi = func.call(x2, p);
        if ((flo == 0.0))
        {
            return x1;
        }
        if ((fhi == 0.0))
        {
            return x2;
        }
        ans = (0.5 * ((x1 + x2)));
        if (((((flo > 0.0) && (fhi < 0.0))) || (((flo < 0.0) && (fhi > 0.0)))))
        {
            xlo = x1;
            xhi = x2;
            for (j = 1; (j <= 60); j++)
            {
                xm = (0.5 * ((xlo + xhi)));
                fm = func.call(xm, p);
                s = Math.sqrt(((fm * fm) - (flo * fhi)));
                if ((s == 0.0))
                {
                    return ans;
                }
                xnew = (xm + (((xm - xlo)) * ((((((flo >= fhi) ? 1.0 : (-1.0))) * fm) / s))));
                if ((Math.abs((xnew - ans)) <= xacc))
                {
                    break;
                }
                ans = xnew;
                fnew = func.call(ans, p);
                if ((((((fnew) >= 0.0) ? Math.abs(fm) : (-(Math.abs(fm))))) != fm))
                {
                    xlo = xm;
                    flo = fm;
                    xhi = ans;
                    fhi = fnew;
                }
                else if ((((((fnew) >= 0.0) ? Math.abs(flo) : (-(Math.abs(flo))))) != flo))
                {
                    xhi = ans;
                    fhi = fnew;
                }
                else if ((((((fnew) >= 0.0) ? Math.abs(fhi) : (-(Math.abs(fhi))))) != fhi))
                {
                    xlo = ans;
                    flo = fnew;
                }
                else
                {
                    return ans;
                }
                if ((Math.abs((xhi - xlo)) <= xacc))
                {
                    return ans;
                }
            }
            return ans;
        }
        return (-1.0e20);
    }

}
