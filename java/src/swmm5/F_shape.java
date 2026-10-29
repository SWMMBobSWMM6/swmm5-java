// Generated from EPA SWMM 5.2.4 (shape.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_shape {
    static double Atotal = 0.0;
    static double Ptotal = 0.0;

    // shape.c:42
    static int shape_validate(TShape shape, TTable curve)
    {
        if (!(computeShapeTables(shape, curve) != 0))
        {
            return 0;
        }
        if (!(normalizeShapeTables(shape) != 0))
        {
            return 0;
        }
        return 1;
    }

    // shape.c:64
    static int computeShapeTables(TShape shape, TTable curve)
    {
        int i = 0;
        int n = 0;
        double dy = 0.0;
        double y = 0.0;
        DBox y1 = new DBox(0.0);
        DBox y2 = new DBox(0.0);
        double w = 0.0;
        DBox w1 = new DBox(0.0);
        DBox w2 = new DBox(0.0);
        double yLast = 0.0;
        double wLast = 0.0;
        DBox wMax = new DBox(0.0);
        if (!(table_getFirstEntry(curve, y1, w1) != 0))
        {
            return 0;
        }
        if ((((y1.v < 0.0) || (y1.v >= 1.0)) || (w1.v < 0.0)))
        {
            return 0;
        }
        wMax.v = w1.v;
        if ((y1.v != 0.0))
        {
            y2.v = y1.v;
            w2.v = w1.v;
            y1.v = 0.0;
            w1.v = 0.0;
        }
        else
        {
            if (!(table_getNextEntry(curve, y2, w2) != 0))
            {
                return 0;
            }
            if (((y2.v < y1.v) || (w2.v < 0.0)))
            {
                return 0;
            }
            if ((y2.v > 1.0))
            {
                y2.v = 1.0;
            }
            if ((w2.v > wMax.v))
            {
                wMax.v = w2.v;
            }
        }
        shape.nTbl = 51;
        n = (shape.nTbl - 1);
        dy = (1.0 / ((double) ((n))));
        shape.areaTbl[0] = 0.0;
        shape.hradTbl[0] = 0.0;
        shape.widthTbl[0] = w1.v;
        Ptotal = w1.v;
        Atotal = 0.0;
        y = 0.0;
        w = w1.v;
        for (i = 1; (i <= n); i++)
        {
            yLast = y;
            wLast = w;
            y = (y + dy);
            if ((Math.abs((y - 1.0)) < 1e-06))
            {
                y = 1.0;
            }
            if ((y > y2.v))
            {
                if (!(getNextInterval(curve, y, yLast, wLast, y1, y2, w1, w2, wMax) != 0))
                {
                    return 0;
                }
                yLast = y1.v;
                wLast = w1.v;
            }
            w = shape_getWidth(y, y1.v, y2.v, w1.v, w2.v);
            Atotal += (shape_getArea(y, w, yLast, wLast));
            Ptotal += (getPerim(y, w, yLast, wLast));
            if ((y == 1.0))
            {
                Ptotal += w2.v;
            }
            shape.widthTbl[i] = w;
            shape.areaTbl[i] = Atotal;
            if ((Ptotal > 0.0))
            {
                shape.hradTbl[i] = (Atotal / Ptotal);
            }
            else
            {
                shape.hradTbl[i] = 0.0;
            }
        }
        shape.aFull = shape.areaTbl[n];
        shape.rFull = shape.hradTbl[n];
        shape.wMax = wMax.v;
        getSmax(shape);
        return 1;
    }

    // shape.c:189
    static void getSmax(TShape shape)
    {
        int i = 0;
        int n = (shape.nTbl - 1);
        double sf = 0.0;
        shape.sMax = 0.0;
        shape.aMax = 0.0;
        for (i = 1; (i <= n); i++)
        {
            sf = (shape.areaTbl[i] * Glibm.pow(shape.hradTbl[i], (2.0 / 3.0)));
            if ((sf > shape.sMax))
            {
                shape.sMax = sf;
                shape.aMax = shape.areaTbl[i];
            }
        }
    }

    // shape.c:216
    static int normalizeShapeTables(TShape shape)
    {
        int i = 0;
        int n = (shape.nTbl - 1);
        double aFull = shape.aFull;
        double rFull = shape.rFull;
        double wMax = shape.wMax;
        if ((((aFull == 0.0) || (rFull == 0.0)) || (wMax == 0.0)))
        {
            return 0;
        }
        for (i = 0; (i <= n); i++)
        {
            shape.areaTbl[i] /= aFull;
            shape.hradTbl[i] /= rFull;
            shape.widthTbl[i] /= wMax;
        }
        return 1;
    }

    // shape.c:246
    static int getNextInterval(TTable curve, double y, double yLast, double wLast, DRef y1, DRef y2, DRef w1, DRef w2, DRef wMax)
    {
        while ((y > y2.get()))
        {
            if ((y2.get() > yLast))
            {
                Atotal += (shape_getArea(y2.get(), w2.get(), yLast, wLast));
                Ptotal += (getPerim(y2.get(), w2.get(), yLast, wLast));
                yLast = y2.get();
                wLast = w2.get();
            }
            y1.set(y2.get());
            w1.set(w2.get());
            if (!(table_getNextEntry(curve, y2, w2) != 0))
            {
                y2.set(1.0);
                return 1;
            }
            if ((w2.get() > wMax.get()))
            {
                wMax.set(w2.get());
            }
            if (((y2.get() < y1.get()) || (w2.get() < 0.0)))
            {
                return 0;
            }
            if ((y2.get() > 1.0))
            {
                y2.set(1.0);
            }
        }
        return 1;
    }

    // shape.c:307
    static double shape_getWidth(double y, double y1, double y2, double w1, double w2)
    {
        if ((y2 == y1))
        {
            return w2;
        }
        return (w1 + ((((y - y1)) / ((y2 - y1))) * ((w2 - w1))));
    }

    // shape.c:328
    static double shape_getArea(double y, double w, double y1, double w1)
    {
        double wMin = 0.0;
        double wMax = 0.0;
        if ((w > w1))
        {
            wMin = w1;
            wMax = w;
        }
        else
        {
            wMin = w;
            wMax = w1;
        }
        return (((wMin + (((wMax - wMin)) / 2.0))) * ((y - y1)));
    }

    // shape.c:353
    static double getPerim(double y, double w, double y1, double w1)
    {
        double dy = (y - y1);
        double dw = (Math.abs((w - w1)) / 2.0);
        return (2.0 * Math.sqrt(((dy * dy) + (dw * dw))));
    }

}
