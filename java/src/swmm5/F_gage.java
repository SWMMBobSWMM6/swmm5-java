// Generated from EPA SWMM 5.2.4 (gage.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_gage {
    static double OneSecond = 1.1574074e-5;

    // gage.c:58
    static int gage_readParams(int j, CPtr[] tok, int ntoks)
    {
        int k = 0;
        int err = 0;
        CPtr id = null;
        CPtr fname = R.buf(260);
        CPtr staID = R.buf(1025);
        double[] x = new double[7];
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        id = project_findID(GAGE, tok[0]);
        if ((id == null))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        x[0] = (-1.0);
        x[1] = 1.0;
        x[2] = 3600.0;
        x[3] = 1.0;
        x[4] = (-693594);
        x[5] = (-693594);
        x[6] = 0.0;
        fname.s(0, 0);
        staID.s(0, 0);
        if ((ntoks < 5))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        k = findmatch(tok[4], GageDataWords);
        if ((k == RAIN_TSERIES))
        {
            err = readGageSeriesFormat(tok, ntoks, x);
        }
        else if ((k == RAIN_FILE))
        {
            if ((ntoks < 8))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            sstrncpy(fname, tok[5], 259);
            sstrncpy(staID, tok[6], 1024);
            err = readGageFileFormat(tok, ntoks, x);
        }
        else
        {
            return error_setInpError(ERR_KEYWORD, tok[4]);
        }
        if ((err > 0))
        {
            return err;
        }
        Gage[j].ID = id;
        Gage[j].tSeries = R.D2I(x[0]);
        Gage[j].rainType = R.D2I(x[1]);
        Gage[j].rainInterval = R.D2I(x[2]);
        Gage[j].snowFactor = x[3];
        Gage[j].rainUnits = R.D2I(x[6]);
        if ((Gage[j].tSeries >= 0))
        {
            Gage[j].dataSource = RAIN_TSERIES;
        }
        else
        {
            Gage[j].dataSource = RAIN_FILE;
        }
        if ((Gage[j].dataSource == RAIN_FILE))
        {
            sstrncpy(Gage[j].fname, addAbsolutePath(fname), 259);
            sstrncpy(Gage[j].staID, staID, 1024);
            Gage[j].startFileDate = x[4];
            Gage[j].endFileDate = x[5];
        }
        Gage[j].unitsFactor = 1.0;
        Gage[j].coGage = (-1);
        Gage[j].isUsed = 0;
        return 0;
    }

    // gage.c:133
    static int readGageSeriesFormat(CPtr[] tok, int ntoks, double[] x)
    {
        int m = 0;
        int ts = 0;
        DBox aTime = new DBox(0.0);
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        m = findmatch(tok[1], RainTypeWords);
        if ((m < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        x[1] = ((double) (m));
        if ((getDouble(tok[2], new DElem(x, 2)) != 0))
        {
            x[2] = Math.floor(((x[2] * 3600) + 0.5));
        }
        else if ((datetime_strToTime(tok[2], aTime) != 0))
        {
            x[2] = Math.floor(((aTime.v * 86400) + 0.5));
        }
        else
        {
            return error_setInpError(ERR_DATETIME, tok[2]);
        }
        if ((x[2] <= 0.0))
        {
            return error_setInpError(ERR_DATETIME, tok[2]);
        }
        if (!(getDouble(tok[3], new DElem(x, 3)) != 0))
        {
            return error_setInpError(ERR_DATETIME, tok[3]);
        }
        ;
        ts = project_findObject(TSERIES, tok[5]);
        if ((ts < 0))
        {
            return error_setInpError(ERR_NAME, tok[5]);
        }
        x[0] = ((double) (ts));
        sstrncpy(tok[2], L0, 0);
        return 0;
    }

    // gage.c:168
    static int readGageFileFormat(CPtr[] tok, int ntoks, double[] x)
    {
        int m = 0;
        int u = 0;
        DBox aDate = new DBox(0.0);
        DBox aTime = new DBox(0.0);
        m = findmatch(tok[1], RainTypeWords);
        if ((m < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        x[1] = ((double) (m));
        if ((getDouble(tok[2], new DElem(x, 2)) != 0))
        {
            x[2] *= 3600;
        }
        else if ((datetime_strToTime(tok[2], aTime) != 0))
        {
            x[2] = Math.floor(((aTime.v * 86400) + 0.5));
        }
        else
        {
            return error_setInpError(ERR_DATETIME, tok[2]);
        }
        if ((x[2] <= 0.0))
        {
            return error_setInpError(ERR_DATETIME, tok[2]);
        }
        if (!(getDouble(tok[3], new DElem(x, 3)) != 0))
        {
            return error_setInpError(ERR_NUMBER, tok[3]);
        }
        u = findmatch(tok[7], RainUnitsWords);
        if ((u < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[7]);
        }
        x[6] = ((double) (u));
        if (((ntoks > 8) && (tok[8].g(0) != 42)))
        {
            if (!(datetime_strToDate(tok[8], aDate) != 0))
            {
                return error_setInpError(ERR_DATETIME, tok[8]);
            }
            x[4] = ((float) (aDate.v));
        }
        return 0;
    }

    // gage.c:209
    static void gage_validate(int j)
    {
        int i = 0;
        int k = 0;
        int gageInterval = 0;
        if ((Gage[j].dataSource == RAIN_TSERIES))
        {
            if (!(Gage[j].isUsed != 0))
            {
                return;
            }
            k = Gage[j].tSeries;
            for (i = 0; (i < j); i++)
            {
                if ((((Gage[i].dataSource == RAIN_TSERIES) && (Gage[i].tSeries == k)) && (Gage[i].isUsed != 0)))
                {
                    Gage[j].coGage = i;
                    if ((Gage[j].rainType != Gage[i].rainType))
                    {
                        report_writeErrorMsg(ERR_RAIN_GAGE_FORMAT, Gage[j].ID);
                    }
                    return;
                }
            }
            if ((Tseries[k].refersTo >= 0))
            {
                report_writeErrorMsg(ERR_RAIN_GAGE_TSERIES, Gage[j].ID);
            }
            gageInterval = R.D2I((Math.floor(((Tseries[k].dxMin * 86400) + 0.5))));
            if (((gageInterval > 0) && (Gage[j].rainInterval > gageInterval)))
            {
                report_writeErrorMsg(ERR_RAIN_GAGE_INTERVAL, Gage[j].ID);
            }
            if ((Gage[j].rainInterval < gageInterval))
            {
                report_writeWarningMsg(L156, Gage[j].ID);
            }
            if ((Gage[j].rainInterval < WetStep))
            {
                report_writeWarningMsg(L157, Gage[j].ID);
                WetStep = Gage[j].rainInterval;
            }
        }
    }

    // gage.c:270
    static void gage_initState(int j)
    {
        Gage[j].rainfall = 0.0;
        Gage[j].apiRainfall = (-10000000000.0);
        Gage[j].reportRainfall = 0.0;
        if ((IgnoreRainfall != 0))
        {
            return;
        }
        if ((Gage[j].dataSource == RAIN_FILE))
        {
            Gage[j].currentFilePos = Gage[j].startFilePos;
            if ((UnitSystem == SI))
            {
                Gage[j].unitsFactor = 25.4;
            }
        }
        if ((getFirstRainfall(j) != 0))
        {
            Gage[j].endDate = datetime_addSeconds(Gage[j].startDate, Gage[j].rainInterval);
            if ((Gage[j].startDate > StartDateTime))
            {
                Gage[j].nextDate = Gage[j].startDate;
                Gage[j].nextRainfall = Gage[j].rainfall;
                Gage[j].startDate = StartDateTime;
                Gage[j].endDate = Gage[j].nextDate;
                Gage[j].rainfall = 0.0;
            }
            else if (!(getNextRainfall(j) != 0))
            {
                Gage[j].nextDate = (-693594);
            }
        }
        else
        {
            Gage[j].startDate = (-693594);
        }
        initPastRain(j);
    }

    // gage.c:323
    static void gage_setState(int j, double t)
    {
        if ((Gage[j].isUsed == 0))
        {
            return;
        }
        if ((IgnoreRainfall != 0))
        {
            Gage[j].rainfall = 0.0;
            return;
        }
        if ((Gage[j].coGage >= 0))
        {
            Gage[j].rainfall = Gage[Gage[j].coGage].rainfall;
            return;
        }
        if ((Gage[j].apiRainfall != (-10000000000.0)))
        {
            Gage[j].rainfall = Gage[j].apiRainfall;
            return;
        }
        t += OneSecond;
        for (; ; )
        {
            if ((Gage[j].startDate == (-693594)))
            {
                Gage[j].rainfall = 0.0;
                return;
            }
            if ((t < Gage[j].startDate))
            {
                Gage[j].rainfall = 0.0;
                return;
            }
            if ((t < Gage[j].endDate))
            {
                return;
            }
            if ((Gage[j].nextDate == (-693594)))
            {
                Gage[j].rainfall = 0.0;
                return;
            }
            if ((t < Gage[j].nextDate))
            {
                Gage[j].rainfall = 0.0;
                return;
            }
            Gage[j].startDate = Gage[j].nextDate;
            Gage[j].endDate = datetime_addSeconds(Gage[j].startDate, Gage[j].rainInterval);
            Gage[j].rainfall = Gage[j].nextRainfall;
            if (!(getNextRainfall(j) != 0))
            {
                Gage[j].nextDate = (-693594);
            }
        }
    }

    // gage.c:406
    static void initPastRain(int j)
    {
        int i = 0;
        for (i = 0; (i <= 48); i++)
        {
            Gage[j].pastRain[i] = 0.0;
        }
        Gage[j].pastInterval = 0;
    }

    // gage.c:417
    static void gage_updatePastRain(int j, int tStep)
    {
        int i = 0;
        int t = 0;
        double r = 0.0;
        r = (Gage[j].rainfall / 3600.0);
        while ((tStep > 0))
        {
            t = (3600 - Gage[j].pastInterval);
            if ((tStep > t))
            {
                Gage[j].pastRain[0] += (t * r);
                for (i = 48; (i > 0); i--)
                {
                    Gage[j].pastRain[i] = Gage[j].pastRain[(i - 1)];
                }
                Gage[j].pastInterval = 0;
                Gage[j].pastRain[0] = 0.0;
                tStep -= t;
            }
            else
            {
                Gage[j].pastRain[0] += (tStep * r);
                Gage[j].pastInterval += tStep;
                tStep = 0;
            }
        }
    }

    // gage.c:468
    static double gage_getPastRain(int j, int n)
    {
        int i = 0;
        double result = 0.0;
        if (((n < 1) || (n > 48)))
        {
            return 0.0;
        }
        for (i = 1; (i <= n); i++)
        {
            result += Gage[j].pastRain[i];
        }
        return result;
    }

    // gage.c:486
    static double gage_getNextRainDate(int j, double aDate)
    {
        if ((Gage[j].isUsed == 0))
        {
            return aDate;
        }
        aDate += OneSecond;
        if ((aDate < Gage[j].startDate))
        {
            return Gage[j].startDate;
        }
        if ((aDate < Gage[j].endDate))
        {
            return Gage[j].endDate;
        }
        return Gage[j].nextDate;
    }

    // gage.c:503
    static double gage_getPrecip(int j, DRef rainfall, DRef snowfall)
    {
        rainfall.set(0.0);
        snowfall.set(0.0);
        if (((!(IgnoreSnowmelt != 0)) && (Temp.ta <= Snow.snotmp)))
        {
            snowfall.set(((Gage[j].rainfall * Gage[j].snowFactor) / UCF(RAINFALL)));
        }
        else
        {
            rainfall.set((Gage[j].rainfall / UCF(RAINFALL)));
        }
        return ((rainfall.get()) + (snowfall.get()));
    }

    // gage.c:524
    static void gage_setReportRainfall(int j, double reportDate)
    {
        double result = 0.0;
        if ((Gage[j].coGage >= 0))
        {
            Gage[j].reportRainfall = Gage[Gage[j].coGage].reportRainfall;
            return;
        }
        if ((Gage[j].apiRainfall != (-10000000000.0)))
        {
            Gage[j].reportRainfall = Gage[j].apiRainfall;
            return;
        }
        reportDate += OneSecond;
        if ((reportDate < Gage[j].endDate))
        {
            result = Gage[j].rainfall;
        }
        else if ((reportDate < Gage[j].nextDate))
        {
            result = 0.0;
        }
        else
        {
            result = Gage[j].nextRainfall;
        }
        Gage[j].reportRainfall = result;
    }

    // gage.c:567
    static int getFirstRainfall(int j)
    {
        int k = 0;
        FBox vFirst = new FBox(0.0f);
        DBox rFirst = new DBox(0.0);
        Gage[j].startDate = (-693594);
        Gage[j].rainfall = 0.0;
        Gage[j].rainAccum = 0;
        if ((Gage[j].dataSource == RAIN_FILE))
        {
            if (((Frain.file != null) && (Gage[j].endFilePos > Gage[j].startFilePos)))
            {
                R.fseek(Frain.file, Gage[j].startFilePos, 0);
                R.fread1(new DField<TGage>(Gage[j], __o -> __o.startDate, (__o, __x) -> __o.startDate = __x), 8, Frain.file);
                R.fread1(vFirst, 4, Frain.file);
                Gage[j].currentFilePos = R.ftell(Frain.file);
                Gage[j].rainfall = convertRainfall(j, vFirst.v);
                return 1;
            }
            return 0;
        }
        else
        {
            k = Gage[j].tSeries;
            if ((k >= 0))
            {
                if ((table_getFirstEntry(Tseries[k], new DField<TGage>(Gage[j], __o -> __o.startDate, (__o, __x) -> __o.startDate = __x), rFirst) != 0))
                {
                    Gage[j].rainfall = convertRainfall(j, rFirst.v);
                    return 1;
                }
            }
            return 0;
        }
    }

    // gage.c:624
    static int getNextRainfall(int j)
    {
        int k = 0;
        FBox vNext = new FBox(0.0f);
        DBox rNext = new DBox(0.0);
        Gage[j].nextRainfall = 0.0;
        do
        {
            if ((Gage[j].dataSource == RAIN_FILE))
            {
                if (((Frain.file != null) && (Gage[j].currentFilePos < Gage[j].endFilePos)))
                {
                    R.fseek(Frain.file, Gage[j].currentFilePos, 0);
                    R.fread1(new DField<TGage>(Gage[j], __o -> __o.nextDate, (__o, __x) -> __o.nextDate = __x), 8, Frain.file);
                    R.fread1(vNext, 4, Frain.file);
                    Gage[j].currentFilePos = R.ftell(Frain.file);
                    rNext.v = convertRainfall(j, vNext.v);
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                k = Gage[j].tSeries;
                if ((k >= 0))
                {
                    if (!(table_getNextEntry(Tseries[k], new DField<TGage>(Gage[j], __o -> __o.nextDate, (__o, __x) -> __o.nextDate = __x), rNext) != 0))
                    {
                        return 0;
                    }
                    rNext.v = convertRainfall(j, rNext.v);
                }
                else
                {
                    return 0;
                }
            }
        }
        while ((rNext.v == 0.0));
        Gage[j].nextRainfall = rNext.v;
        return 1;
    }

    // gage.c:674
    static double convertRainfall(int j, double r)
    {
        double r1 = 0.0;
        switch (Gage[j].rainType) {
            case RAINFALL_INTENSITY:
                r1 = r;
                break;
            case RAINFALL_VOLUME:
                r1 = ((r / Gage[j].rainInterval) * 3600.0);
                break;
            case CUMULATIVE_RAINFALL:
                if ((r < Gage[j].rainAccum))
                {
                    r1 = ((r / Gage[j].rainInterval) * 3600.0);
                }
                else
                {
                    r1 = ((((r - Gage[j].rainAccum)) / Gage[j].rainInterval) * 3600.0);
                }
                Gage[j].rainAccum = r;
                break;
            default:
                r1 = r;
        }
        return ((r1 * Gage[j].unitsFactor) * Adjust.rainFactor);
    }

}
