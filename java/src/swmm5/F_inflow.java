// Generated from EPA SWMM 5.2.4 (inflow.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_inflow {
    // inflow.c:41
    static int inflow_readExtInflow(CPtr[] tok, int ntoks)
    {
        int j = 0;
        int param = 0;
        int type = CONCEN_INFLOW;
        int tseries = (-1);
        int basePat = (-1);
        DBox cf = new DBox(1.0);
        DBox sf = new DBox(1.0);
        DBox baseline = new DBox(0.0);
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(NODE, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        param = project_findObject(POLLUT, tok[1]);
        if ((param < 0))
        {
            if ((match(tok[1], L183) != 0))
            {
                param = (-1);
            }
            else
            {
                return error_setInpError(ERR_NAME, tok[1]);
            }
        }
        if ((R.strlen(tok[2]) > 0))
        {
            tseries = project_findObject(TSERIES, tok[2]);
            if ((tseries < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
            Tseries[tseries].refersTo = EXTERNAL_INFLOW;
        }
        if ((param == (-1)))
        {
            type = FLOW_INFLOW;
            cf.v = (1.0 / UCF(FLOW));
        }
        if (((ntoks >= 4) && (param > (-1))))
        {
            if ((match(tok[3], L185) != 0))
            {
                type = CONCEN_INFLOW;
            }
            else if ((match(tok[3], L186) != 0))
            {
                type = MASS_INFLOW;
            }
            else
            {
                return error_setInpError(ERR_KEYWORD, tok[3]);
            }
            if (((ntoks >= 5) && (type == MASS_INFLOW)))
            {
                if (!(getDouble(tok[4], cf) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[4]);
                }
                if ((cf.v <= 0.0))
                {
                    return error_setInpError(ERR_NUMBER, tok[4]);
                }
            }
        }
        if ((ntoks >= 6))
        {
            if (!(getDouble(tok[5], sf) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[5]);
            }
        }
        if ((ntoks >= 7))
        {
            if (!(getDouble(tok[6], baseline) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[6]);
            }
        }
        if ((ntoks >= 8))
        {
            basePat = project_findObject(TIMEPATTERN, tok[7]);
            if ((basePat < 0))
            {
                return error_setInpError(ERR_NAME, tok[7]);
            }
        }
        if ((type == MASS_INFLOW))
        {
            cf.v /= 28.317;
        }
        return (inflow_setExtInflow(j, param, type, tseries, basePat, cf.v, baseline.v, sf.v));
    }

    // inflow.c:138
    static int inflow_setExtInflow(int j, int param, int type, int tseries, int basePat, double cf, double baseline, double sf)
    {
        TExtInflow inflow = null;
        inflow = Node[j].extInflow;
        while ((inflow != null))
        {
            if ((inflow.param == param))
            {
                break;
            }
            inflow = inflow.next;
        }
        if ((inflow == null))
        {
            inflow = new TExtInflow();
            if ((inflow == null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
            inflow.next = Node[j].extInflow;
            Node[j].extInflow = inflow;
        }
        inflow.param = param;
        inflow.type = type;
        inflow.tSeries = tseries;
        inflow.cFactor = cf;
        inflow.sFactor = sf;
        inflow.baseline = baseline;
        inflow.basePat = basePat;
        return 0;
    }

    // inflow.c:187
    static void inflow_deleteExtInflows(int j)
    {
        TExtInflow inflow1 = null;
        TExtInflow inflow2 = null;
        inflow1 = Node[j].extInflow;
        while ((inflow1 != null))
        {
            inflow2 = inflow1.next;
            R.free(inflow1);
            inflow1 = inflow2;
        }
    }

    // inflow.c:207
    static double inflow_getExtInflow(TExtInflow inflow, double aDate)
    {
        int month = 0;
        int day = 0;
        int hour = 0;
        int p = inflow.basePat;
        int k = inflow.tSeries;
        double cf = inflow.cFactor;
        double sf = inflow.sFactor;
        double blv = inflow.baseline;
        double tsv = 0.0;
        if ((p >= 0))
        {
            month = (datetime_monthOfYear(aDate) - 1);
            day = (datetime_dayOfWeek(aDate) - 1);
            hour = datetime_hourOfDay(aDate);
            blv *= (getPatternFactor(p, month, day, hour));
        }
        if ((k >= 0))
        {
            tsv = (table_tseriesLookup(Tseries[k], aDate, 0) * sf);
        }
        return (cf * ((tsv + blv)));
    }

    // inflow.c:237
    static int inflow_readDwfInflow(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int[] pats = new int[4];
        DBox x = new DBox(0.0);
        TDwfInflow inflow = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(NODE, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        k = project_findObject(POLLUT, tok[1]);
        if ((k < 0))
        {
            if ((match(tok[1], L183) != 0))
            {
                k = (-1);
            }
            else
            {
                return error_setInpError(ERR_NAME, tok[1]);
            }
        }
        if (!(getDouble(tok[2], x) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[2]);
        }
        if ((k == (-1)))
        {
            x.v /= (UCF(FLOW));
        }
        for (i = 0; (i < 4); i++)
        {
            pats[i] = (-1);
        }
        for (i = 3; (i < 7); i++)
        {
            if ((i >= ntoks))
            {
                break;
            }
            if ((R.strlen(tok[i]) == 0))
            {
                continue;
            }
            m = project_findObject(TIMEPATTERN, tok[i]);
            if ((m < 0))
            {
                return error_setInpError(ERR_NAME, tok[i]);
            }
            pats[(i - 3)] = m;
        }
        inflow = Node[j].dwfInflow;
        while ((inflow != null))
        {
            if ((inflow.param == k))
            {
                break;
            }
            inflow = inflow.next;
        }
        if ((inflow == null))
        {
            inflow = new TDwfInflow();
            if ((inflow == null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
            inflow.next = Node[j].dwfInflow;
            Node[j].dwfInflow = inflow;
        }
        inflow.param = k;
        inflow.avgValue = x.v;
        for (i = 0; (i < 4); i++)
        {
            inflow.patterns[i] = pats[i];
        }
        return 0;
    }

    // inflow.c:311
    static void inflow_deleteDwfInflows(int j)
    {
        TDwfInflow inflow1 = null;
        TDwfInflow inflow2 = null;
        inflow1 = Node[j].dwfInflow;
        while ((inflow1 != null))
        {
            inflow2 = inflow1.next;
            R.free(inflow1);
            inflow1 = inflow2;
        }
    }

    // inflow.c:331
    static void inflow_initDwfInflow(TDwfInflow inflow)
    {
        int i = 0;
        int p = 0;
        int[] tmpPattern = new int[4];
        for (i = 0; (i < 4); i++)
        {
            tmpPattern[i] = (-1);
        }
        for (i = 0; (i < 4); i++)
        {
            p = inflow.patterns[i];
            if ((p >= 0))
            {
                tmpPattern[Pattern[p].type] = p;
            }
        }
        for (i = 0; (i < 4); i++)
        {
            inflow.patterns[i] = tmpPattern[i];
        }
    }

    // inflow.c:361
    static double inflow_getDwfInflow(TDwfInflow inflow, int month, int day, int hour)
    {
        int p1 = 0;
        int p2 = 0;
        double f = 1.0;
        p1 = inflow.patterns[MONTHLY_PATTERN];
        if ((p1 >= 0))
        {
            f *= (getPatternFactor(p1, month, day, hour));
        }
        p1 = inflow.patterns[DAILY_PATTERN];
        if ((p1 >= 0))
        {
            f *= (getPatternFactor(p1, month, day, hour));
        }
        p1 = inflow.patterns[HOURLY_PATTERN];
        p2 = inflow.patterns[WEEKEND_PATTERN];
        if ((p2 >= 0))
        {
            if (((day == 0) || (day == 6)))
            {
                f *= (getPatternFactor(p2, month, day, hour));
            }
            else if ((p1 >= 0))
            {
                f *= (getPatternFactor(p1, month, day, hour));
            }
        }
        else if ((p1 >= 0))
        {
            f *= (getPatternFactor(p1, month, day, hour));
        }
        return (f * inflow.avgValue);
    }

    // inflow.c:394
    static void inflow_initDwfPattern(int j)
    {
        int i = 0;
        for (i = 0; (i < 24); i++)
        {
            Pattern[j].factor[i] = 1.0;
        }
        Pattern[j].count = 0;
        Pattern[j].type = (-1);
        Pattern[j].ID = null;
    }

    // inflow.c:410
    static int inflow_readDwfPattern(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int n = 1;
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(TIMEPATTERN, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if ((Pattern[j].ID == null))
        {
            Pattern[j].ID = project_findID(TIMEPATTERN, tok[0]);
            k = findmatch(tok[1], PatternTypeWords);
            if ((k < 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[1]);
            }
            Pattern[j].type = k;
            n = 2;
        }
        while (((ntoks > n) && (Pattern[j].count < 24)))
        {
            i = Pattern[j].count;
            if (!(getDouble(tok[n], new DElem(Pattern[j].factor, i)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[n]);
            }
            Pattern[j].count++;
            n++;
        }
        return 0;
    }

    // inflow.c:456
    static double getPatternFactor(int p, int month, int day, int hour)
    {
        switch (Pattern[p].type) {
            case MONTHLY_PATTERN:
                if (((month >= 0) && (month < 12)))
                {
                    return Pattern[p].factor[month];
                }
                break;
            case DAILY_PATTERN:
                if (((day >= 0) && (day < 7)))
                {
                    return Pattern[p].factor[day];
                }
                break;
            case HOURLY_PATTERN:
                if (((hour >= 0) && (hour < 24)))
                {
                    return Pattern[p].factor[hour];
                }
                break;
            case WEEKEND_PATTERN:
                if (((day == 0) || (day == 6)))
                {
                    if (((hour >= 0) && (hour < 24)))
                    {
                        return Pattern[p].factor[hour];
                    }
                }
                break;
        }
        return 1.0;
    }

}
