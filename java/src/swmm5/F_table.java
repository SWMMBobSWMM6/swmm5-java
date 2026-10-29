// Generated from EPA SWMM 5.2.4 (table.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_table {
    // table.c:51
    static double table_interpolate(double x, double x1, double y1, double x2, double y2)
    {
        double dx = (x2 - x1);
        if ((Math.abs(dx) < 1.0e-20))
        {
            return (((y1 + y2)) / 2.0);
        }
        return (y1 + ((((x - x1)) * ((y2 - y1))) / dx));
    }

    // table.c:67
    static int table_readCurve(CPtr[] tok, int ntoks)
    {
        int j = 0;
        int m = 0;
        int k = 0;
        int k1 = 1;
        DBox x = new DBox(0.0);
        DBox y = new DBox(0.0);
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(CURVE, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if ((Curve[j].ID == null))
        {
            Curve[j].ID = project_findID(CURVE, tok[0]);
            m = findmatch(tok[1], CurveTypeWords);
            if ((m < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[1]);
            }
            Curve[j].curveType = m;
            if ((ntoks == 2))
            {
                return 0;
            }
            k1 = 2;
        }
        for (k = k1; (k < ntoks); k = (k + 2))
        {
            if (((k + 1) >= ntoks))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if (!(getDouble(tok[k], x) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[k]);
            }
            if (!(getDouble(tok[(k + 1)], y) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[(k + 1)]);
            }
            table_addEntry(Curve[j], x.v, y.v);
        }
        return 0;
    }

    // table.c:113
    static int table_readTimeseries(CPtr[] tok, int ntoks)
    {
        int j = 0;
        int k = 0;
        int state = 0;
        double x = 0.0;
        DBox y = new DBox(0.0);
        DBox d = new DBox(0.0);
        DBox t = new DBox(0.0);
        CPtr fname = R.buf(260);
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(TSERIES, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if ((Tseries[j].ID == null))
        {
            Tseries[j].ID = project_findID(TSERIES, tok[0]);
        }
        if ((strcomp(tok[1], L1) != 0))
        {
            sstrncpy(fname, tok[2], 259);
            sstrncpy(Tseries[j].file.name, addAbsolutePath(fname), 259);
            Tseries[j].file.mode = USE_FILE;
            return 0;
        }
        x = 0.0;
        k = 1;
        state = 1;
        while ((k < ntoks))
        {
            switch (state) {
                case 1:
                    if ((datetime_strToDate(tok[k], d) != 0))
                    {
                        Tseries[j].lastDate = d.v;
                        k++;
                    }
                    state = 2;
                    break;
                case 2:
                    if ((k >= ntoks))
                    {
                        return error_setInpError(ERR_ITEMS, L0);
                    }
                    if ((getDouble(tok[k], t) != 0))
                    {
                        t.v /= 24.0;
                    }
                    else if (!(datetime_strToTime(tok[k], t) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[k]);
                    }
                    x = (Tseries[j].lastDate + t.v);
                    k++;
                    state = 3;
                    break;
                case 3:
                    if ((k >= ntoks))
                    {
                        return error_setInpError(ERR_ITEMS, L0);
                    }
                    if (!(getDouble(tok[k], y) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[k]);
                    }
                    table_addEntry(Tseries[j], x, y.v);
                    k++;
                    state = 1;
                    break;
            }
        }
        return 0;
    }

    // table.c:208
    static int table_addEntry(TTable table, double x, double y)
    {
        TTableEntry entry = null;
        entry = new TTableEntry();
        if (!(entry != null))
        {
            return 0;
        }
        entry.x = x;
        entry.y = y;
        entry.next = (null);
        if ((table.firstEntry == null))
        {
            table.firstEntry = entry;
        }
        else
        {
            table.lastEntry.next = entry;
        }
        table.lastEntry = entry;
        return 1;
    }

    // table.c:231
    static void table_deleteEntries(TTable table)
    {
        TTableEntry entry = null;
        TTableEntry nextEntry = null;
        entry = table.firstEntry;
        while ((entry != null))
        {
            nextEntry = entry.next;
            R.free(entry);
            entry = nextEntry;
        }
        table.firstEntry = (null);
        table.lastEntry = (null);
        table.thisEntry = (null);
        if ((table.file.file != null))
        {
            R.fclose(table.file.file);
            table.file.file = (null);
        }
    }

    // table.c:260
    static void table_init(TTable table)
    {
        table.ID = null;
        table.refersTo = (-1);
        table.firstEntry = (null);
        table.lastEntry = (null);
        table.thisEntry = table.firstEntry;
        table.lastDate = 0.0;
        table.x1 = 0.0;
        table.x2 = 0.0;
        table.y1 = 0.0;
        table.y2 = 0.0;
        table.dxMin = 0.0;
        table.file.mode = NO_FILE;
        table.file.file = (null);
        table.curveType = (-1);
    }

    // table.c:285
    static int table_validate(TTable table)
    {
        int result = 0;
        DBox x1 = new DBox(0.0);
        DBox x2 = new DBox(0.0);
        DBox y1 = new DBox(0.0);
        DBox y2 = new DBox(0.0);
        double dx = 0.0;
        double dxMin = 10000000000.0;
        if ((table.file.mode == USE_FILE))
        {
            table.file.file = R.fopen(table.file.name, L13);
            if ((table.file.file == null))
            {
                return ERR_TABLE_FILE_OPEN;
            }
        }
        result = table_getFirstEntry(table, x1, y1);
        if (((!(result != 0)) && (table.file.mode == USE_FILE)))
        {
            return ERR_TABLE_FILE_READ;
        }
        while ((table_getNextEntry(table, x2, y2) != 0))
        {
            dx = (x2.v - x1.v);
            if ((dx <= 0.0))
            {
                table.x2 = x2.v;
                return ERR_CURVE_SEQUENCE;
            }
            dxMin = (((((dxMin) <= (dx))) ? (dxMin) : (dx)));
            x1.v = x2.v;
        }
        table.dxMin = dxMin;
        if (((table.file.mode == USE_FILE) && (!(R.feof(table.file.file) != 0))))
        {
            return ERR_TABLE_FILE_READ;
        }
        return 0;
    }

    // table.c:331
    static int table_getFirstEntry(TTable table, DRef x, DRef y)
    {
        TTableEntry entry = null;
        x.set(0);
        y.set(0.0);
        if ((table.file.mode == USE_FILE))
        {
            if ((table.file.file == null))
            {
                return 0;
            }
            R.rewind(table.file.file);
            return table_getNextFileEntry(table, x, y);
        }
        entry = table.firstEntry;
        if ((entry != null))
        {
            x.set(entry.x);
            y.set(entry.y);
            table.thisEntry = entry;
            return 1;
        }
        else
        {
            return 0;
        }
    }

    // table.c:366
    static int table_getNextEntry(TTable table, DRef x, DRef y)
    {
        TTableEntry entry = null;
        if ((table.file.mode == USE_FILE))
        {
            return table_getNextFileEntry(table, x, y);
        }
        entry = table.thisEntry.next;
        if ((entry != null))
        {
            x.set(entry.x);
            y.set(entry.y);
            table.thisEntry = entry;
            return 1;
        }
        else
        {
            return 0;
        }
    }

    // table.c:395
    static double table_lookup(TTable table, double x)
    {
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        TTableEntry entry = null;
        entry = table.firstEntry;
        if ((entry == null))
        {
            return 0.0;
        }
        x1 = entry.x;
        y1 = entry.y;
        if ((x <= x1))
        {
            return y1;
        }
        while ((entry.next != null))
        {
            entry = entry.next;
            x2 = entry.x;
            y2 = entry.y;
            if ((x <= x2))
            {
                return table_interpolate(x, x1, y1, x2, y2);
            }
            x1 = x2;
            y1 = y2;
        }
        return y1;
    }

    // table.c:430
    static double table_getSlope(TTable table, double x)
    {
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double dx = 0.0;
        TTableEntry entry = null;
        entry = table.firstEntry;
        if ((entry == null))
        {
            return 0.0;
        }
        x1 = entry.x;
        y1 = entry.y;
        x2 = x1;
        y2 = y1;
        while ((entry.next != null))
        {
            entry = entry.next;
            x2 = entry.x;
            y2 = entry.y;
            if ((x <= x2))
            {
                break;
            }
            x1 = x2;
            y1 = y2;
        }
        dx = (x2 - x1);
        if ((dx == 0.0))
        {
            return 0.0;
        }
        return (((y2 - y1)) / dx);
    }

    // table.c:464
    static double table_lookupEx(TTable table, double x)
    {
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double s = 0.0;
        TTableEntry entry = null;
        entry = table.firstEntry;
        if ((entry == null))
        {
            return 0.0;
        }
        x1 = entry.x;
        y1 = entry.y;
        if ((x <= x1))
        {
            if ((x1 > 0.0))
            {
                return ((x / x1) * y1);
            }
            else
            {
                return y1;
            }
        }
        while ((entry.next != null))
        {
            entry = entry.next;
            x2 = entry.x;
            y2 = entry.y;
            if ((x2 != x1))
            {
                s = (((y2 - y1)) / ((x2 - x1)));
            }
            if ((x <= x2))
            {
                return table_interpolate(x, x1, y1, x2, y2);
            }
            x1 = x2;
            y1 = y2;
        }
        if ((s < 0.0))
        {
            s = 0.0;
        }
        return (y1 + (s * ((x - x1))));
    }

    // table.c:503
    static double table_intervalLookup(TTable table, double x)
    {
        TTableEntry entry = null;
        entry = table.firstEntry;
        if ((entry == null))
        {
            return 0.0;
        }
        if ((x < entry.x))
        {
            return entry.y;
        }
        while ((entry.next != null))
        {
            entry = entry.next;
            if ((x < entry.x))
            {
                return entry.y;
            }
        }
        return entry.y;
    }

    // table.c:527
    static double table_inverseLookup(TTable table, double y)
    {
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        TTableEntry entry = null;
        entry = table.firstEntry;
        if ((entry == null))
        {
            return 0.0;
        }
        x1 = entry.x;
        y1 = entry.y;
        if ((y <= y1))
        {
            return x1;
        }
        while ((entry.next != null))
        {
            entry = entry.next;
            x2 = entry.x;
            y2 = entry.y;
            if ((y <= y2))
            {
                return table_interpolate(y, y1, x1, y2, x2);
            }
            x1 = x2;
            y1 = y2;
        }
        return x1;
    }

    // table.c:562
    static double table_getMaxY(TTable table, double x)
    {
        double ymax = 0.0;
        TTableEntry entry = null;
        entry = table.firstEntry;
        ymax = entry.y;
        while (((x > entry.x) && (entry.next != null)))
        {
            entry = entry.next;
            if ((entry.y < ymax))
            {
                return ymax;
            }
            ymax = entry.y;
        }
        return 0.0;
    }

    // table.c:587
    static double table_getStorageVolume(TTable table, double x)
    {
        double a = 0.0;
        double a1 = 0.0;
        double x1 = 0.0;
        double v = 0.0;
        double dx = 0.0;
        double dy = 0.0;
        double s = 0.0;
        TTableEntry entry = null;
        v = 0.0;
        entry = table.firstEntry;
        if ((entry == null))
        {
            return 0.0;
        }
        x1 = entry.x;
        a1 = entry.y;
        if ((x <= x1))
        {
            if ((x1 < 1.0e-6))
            {
                return 0.0;
            }
            return (((((a1 / x1)) * x) * x) / 2.0);
        }
        while ((entry.next != null))
        {
            entry = entry.next;
            if ((entry.x >= x))
            {
                a = table_interpolate(x, x1, a1, entry.x, entry.y);
                return (v + ((((a1 + a)) / 2.0) * ((x - x1))));
            }
            else
            {
                dx = (entry.x - x1);
                dy = (entry.y - a1);
                v = (v + ((((a1 + entry.y)) / 2.0) * dx));
                x1 = entry.x;
                a1 = entry.y;
            }
        }
        if ((dx > 1.0e-6))
        {
            s = (dy / dx);
            a = (a1 + (s * ((x - x1))));
            if ((a < 0.0))
            {
                v = (v - (((a1 * a1) / s) / 2.0));
            }
            else
            {
                v = (v + ((((a1 + a)) / 2.0) * ((x - x1))));
            }
        }
        return v;
    }

    // table.c:651
    static double table_getStorageDepth(TTable table, double v)
    {
        double a1 = 0.0;
        double a2 = 0.0;
        double d1 = 0.0;
        double d2 = 0.0;
        double dd = 0.0;
        double da = 0.0;
        double v1 = 0.0;
        double v2 = 0.0;
        double s = 0.0;
        TTableEntry entry = null;
        if ((v == 0.0))
        {
            return 0.0;
        }
        entry = table.firstEntry;
        if ((entry == null))
        {
            return 0.0;
        }
        d1 = entry.x;
        a1 = entry.y;
        v1 = ((a1 * d1) / 2.0);
        if ((v <= v1))
        {
            if ((a1 > 0.0))
            {
                return Math.sqrt((((2.0 * v) * d1) / a1));
            }
            else
            {
                return 0.0;
            }
        }
        while ((entry.next != null))
        {
            entry = entry.next;
            d2 = entry.x;
            a2 = entry.y;
            dd = (d2 - d1);
            da = (a2 - a1);
            v2 = (v1 + ((((a1 + a2)) / 2.0) * dd));
            if ((v <= v2))
            {
                if ((dd <= 0.0))
                {
                    return d1;
                }
                if ((da == 0.0))
                {
                    if ((Math.abs((v2 - v1)) < 1.0e-6))
                    {
                        return d1;
                    }
                    else
                    {
                        return (d1 + ((dd * ((v - v1))) / ((v2 - v1))));
                    }
                }
                if ((da < 0.0))
                {
                    d1 = d2;
                    a1 = a2;
                    v1 = v2;
                }
                s = (da / dd);
                return (d1 + (((Math.sqrt(((a1 * a1) + ((2.0 * s) * ((v - v1))))) - a1)) / s));
            }
            d1 = d2;
            a1 = a2;
            v1 = v2;
        }
        if (((dd == 0.0) || (da == 0.0)))
        {
            if ((a1 > 0.0))
            {
                dd = (((v - v1)) / a1);
            }
            else
            {
                dd = 0.0;
            }
        }
        else
        {
            s = (da / dd);
            dd = (((Math.sqrt(((a1 * a1) + ((2.0 * s) * ((v - v1))))) - a1)) / s);
            if ((dd < 0.0))
            {
                dd = 0.0;
            }
        }
        return (d1 + dd);
    }

    // table.c:730
    static void table_tseriesInit(TTable table)
    {
        table_getFirstEntry(table, new DField<TTable>(table, __o -> __o.x1, (__o, __x) -> __o.x1 = __x), new DField<TTable>(table, __o -> __o.y1, (__o, __x) -> __o.y1 = __x));
        table.x2 = table.x1;
        table.y2 = table.y1;
        table_getNextEntry(table, new DField<TTable>(table, __o -> __o.x2, (__o, __x) -> __o.x2 = __x), new DField<TTable>(table, __o -> __o.y2, (__o, __x) -> __o.y2 = __x));
    }

    // table.c:745
    static double table_tseriesLookup(TTable table, double x, int extend)
    {
        if ((((table.x1 <= x) && (table.x2 >= x)) && (table.x1 != table.x2)))
        {
            return table_interpolate(x, table.x1, table.y1, table.x2, table.y2);
        }
        if (((table.file.mode == USE_FILE) && (R.feof(table.file.file) != 0)))
        {
            if ((extend == 1))
            {
                return table.y1;
            }
            else
            {
                return 0;
            }
        }
        if (((table.x1 == table.x2) || (x < table.x1)))
        {
            table_getFirstEntry(table, new DField<TTable>(table, __o -> __o.x1, (__o, __x) -> __o.x1 = __x), new DField<TTable>(table, __o -> __o.y1, (__o, __x) -> __o.y1 = __x));
            if ((x < table.x1))
            {
                if ((extend == 1))
                {
                    return table.y1;
                }
                else
                {
                    return 0;
                }
            }
        }
        table.x1 = table.x2;
        table.y1 = table.y2;
        while ((table_getNextEntry(table, new DField<TTable>(table, __o -> __o.x2, (__o, __x) -> __o.x2 = __x), new DField<TTable>(table, __o -> __o.y2, (__o, __x) -> __o.y2 = __x)) != 0))
        {
            if ((x <= table.x2))
            {
                return table_interpolate(x, table.x1, table.y1, table.x2, table.y2);
            }
            table.x1 = table.x2;
            table.y1 = table.y2;
        }
        if ((extend == 1))
        {
            return table.y1;
        }
        else
        {
            return 0.0;
        }
    }

    // table.c:808
    static int table_getNextFileEntry(TTable table, DRef x, DRef y)
    {
        CPtr line = R.buf(1025);
        int code = 0;
        if ((table.file.file == null))
        {
            return 0;
        }
        while (((!(R.feof(table.file.file) != 0)) && ((R.fgets(line, (int) (1024), table.file.file)) != null)))
        {
            code = table_parseFileLine(line, table, x, y);
            if ((code < 0))
            {
                continue;
            }
            return code;
        }
        return 0;
    }

    // table.c:833
    static int table_parseFileLine(CPtr line, TTable table, DRef x, DRef y)
    {
        int n = 0;
        CPtr s1 = R.buf(50);
        CPtr s2 = R.buf(50);
        CPtr s3 = R.buf(50);
        CPtr tStr = null;
        CPtr yStr = null;
        DBox yy = new DBox(0.0);
        DBox d = new DBox(0.0);
        DBox t = new DBox(0.0);
        n = R.sscanf(line, L650, s1, s2, s3);
        tStr = R.strtok(line, L184);
        if (((tStr == null) || (tStr.g(0) == 59)))
        {
            return (-1);
        }
        if ((n == 2))
        {
            d.v = table.lastDate;
            tStr = s1;
            yStr = s2;
        }
        else if ((n == 3))
        {
            if (!(datetime_strToDate(s1, d) != 0))
            {
                return 0;
            }
            table.lastDate = d.v;
            tStr = s2;
            yStr = s3;
        }
        else
        {
            return 0;
        }
        if ((getDouble(tStr, t) != 0))
        {
            t.v /= 24.0;
        }
        else if (!(datetime_strToTime(tStr, t) != 0))
        {
            return 0;
        }
        if (!(getDouble(yStr, yy) != 0))
        {
            return 0;
        }
        x.set((d.v + t.v));
        y.set(yy.v);
        return 1;
    }

}
