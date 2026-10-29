// Generated from EPA SWMM 5.2.4 (climate.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_climate {
    static int MAXCLIMATEVARS = 4;
    static int MAXDAYSPERMONTH = 32;
    static CPtr[] ClimateVarWords = new CPtr[] { L22, L23, L7, L24, L25, null };
    static CPtr[] TempUnitsWords = new CPtr[] { L653, L654, L655, null };
    static double Tmin = 0.0;
    static double Tmax = 0.0;
    static double Trng = 0.0;
    static double Trng1 = 0.0;
    static double Tave = 0.0;
    static double Hrsr = 0.0;
    static double Hrss = 0.0;
    static double Hrday = 0.0;
    static double Dhrdy = 0.0;
    static double Dydif = 0.0;
    static double LastDay = 0.0;
    static TMovAve Tma = new TMovAve();
    static DBox NextEvapDate = new DBox(0.0);
    static DBox NextEvapRate = new DBox(0.0);
    static int FileFormat = 0;
    static IBox FileYear = new IBox(0);
    static IBox FileMonth = new IBox(0);
    static IBox FileDay = new IBox(0);
    static int FileLastDay = 0;
    static int FileElapsedDays = 0;
    static double[] FileValue = new double[4];
    static double[][] FileData = new double[4][32];
    static CPtr FileLine = R.buf(1025);
    static int[] FileFieldPos = new int[4];
    static int FileDateFieldPos = 0;
    static int FileWindType = 0;
    static int FileTempUnits = 0;

    // climate.c:153
    static int climate_readParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        double[] x = new double[6];
        DBox y = new DBox(0.0);
        DBox aDate = new DBox(0.0);
        CPtr fname = R.buf(260);
        k = findmatch(tok[0], TempKeyWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[0]);
        }
        switch (k) {
            case 0:
                if ((ntoks < 2))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                i = project_findObject(TSERIES, tok[1]);
                if ((i < 0))
                {
                    return error_setInpError(ERR_NAME, tok[1]);
                }
                Temp.dataSource = TSERIES_TEMP;
                Temp.tSeries = i;
                Tseries[i].refersTo = TSERIES_TEMP;
                break;
            case 1:
                if ((ntoks < 2))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                Temp.dataSource = FILE_TEMP;
                Fclimate.mode = USE_FILE;
                sstrncpy(fname, tok[1], 259);
                sstrncpy(Fclimate.name, addAbsolutePath(fname), 259);
                Temp.fileStartDate = (-693594);
                if ((ntoks > 2))
                {
                    if ((tok[2].g(0) != 42))
                    {
                        if (!(datetime_strToDate(tok[2], aDate) != 0))
                        {
                            return error_setInpError(ERR_DATETIME, tok[2]);
                        }
                        Temp.fileStartDate = aDate.v;
                    }
                }
                FileTempUnits = DEG_F;
                if ((UnitSystem == SI))
                {
                    FileTempUnits = DEG_C;
                }
                if ((ntoks > 3))
                {
                    i = findmatch(tok[3], TempUnitsWords);
                    if ((i < 0))
                    {
                        return error_setInpError(ERR_KEYWORD, tok[3]);
                    }
                    FileTempUnits = i;
                }
                break;
            case 2:
                if ((strcomp(tok[1], L1) != 0))
                {
                    Wind.type = FILE_WIND;
                }
                else
                {
                    if ((ntoks < 14))
                    {
                        return error_setInpError(ERR_ITEMS, L0);
                    }
                    Wind.type = MONTHLY_WIND;
                    for (i = 0; (i < 12); i++)
                    {
                        if (!(getDouble(tok[(i + 2)], y) != 0))
                        {
                            return error_setInpError(ERR_NUMBER, tok[(i + 2)]);
                        }
                        Wind.aws[i] = y.v;
                    }
                }
                break;
            case 3:
                if ((ntoks < 7))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                for (i = 1; (i < 7); i++)
                {
                    if (!(getDouble(tok[i], new DElem(x, (i - 1))) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[i]);
                    }
                }
                if ((UnitSystem == SI))
                {
                    x[0] = (((9.0 / 5.0) * x[0]) + 32.0);
                }
                Snow.snotmp = x[0];
                Snow.tipm = x[1];
                Snow.rnm = x[2];
                Temp.elev = (x[3] / UCF(LENGTH));
                Temp.anglat = x[4];
                Temp.dtlong = (x[5] / 60.0);
                break;
            case 4:
                if ((ntoks < 12))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                if ((match(tok[1], L2) != 0))
                {
                    i = 0;
                }
                else if ((match(tok[1], L3) != 0))
                {
                    i = 1;
                }
                else
                {
                    return error_setInpError(ERR_KEYWORD, tok[1]);
                }
                for (j = 0; (j < 10); j++)
                {
                    if ((((!(getDouble(tok[(j + 2)], y) != 0)) || (y.v < 0.0)) || (y.v > 1.0)))
                    {
                        return error_setInpError(ERR_NUMBER, tok[(j + 2)]);
                    }
                    Snow.adc[i][j] = y.v;
                }
                break;
        }
        return 0;
    }

    // climate.c:285
    static int climate_readEvapParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int k = 0;
        DBox x = new DBox(0.0);
        k = findmatch(tok[0], EvapTypeWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[0]);
        }
        if ((k == RECOVERY))
        {
            if ((ntoks < 2))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            i = project_findObject(TIMEPATTERN, tok[1]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[1]);
            }
            Evap.recoveryPattern = i;
            return 0;
        }
        if ((k == DRYONLY))
        {
            if ((ntoks < 2))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if ((strcomp(tok[1], L4) != 0))
            {
                Evap.dryOnly = 0;
            }
            else if ((strcomp(tok[1], L5) != 0))
            {
                Evap.dryOnly = 1;
            }
            else
            {
                return error_setInpError(ERR_KEYWORD, tok[1]);
            }
            return 0;
        }
        Evap.type = k;
        if (((k != TEMPERATURE_EVAP) && (ntoks < 2)))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        switch (k) {
            case CONSTANT_EVAP:
                if (!(getDouble(tok[1], x) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[1]);
                }
                for (i = 0; (i < 12); i++)
                {
                    Evap.monthlyEvap[i] = x.v;
                }
                break;
            case MONTHLY_EVAP:
                if ((ntoks < 13))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                for (i = 0; (i < 12); i++)
                {
                    if (!(getDouble(tok[(i + 1)], new DElem(Evap.monthlyEvap, i)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[(i + 1)]);
                    }
                }
                break;
            case TIMESERIES_EVAP:
                i = project_findObject(TSERIES, tok[1]);
                if ((i < 0))
                {
                    return error_setInpError(ERR_NAME, tok[1]);
                }
                Evap.tSeries = i;
                Tseries[i].refersTo = TIMESERIES_EVAP;
                break;
            case FILE_EVAP:
                if ((ntoks > 1))
                {
                    if ((ntoks < 13))
                    {
                        return error_setInpError(ERR_ITEMS, L0);
                    }
                    for (i = 0; (i < 12); i++)
                    {
                        if (!(getDouble(tok[(i + 1)], new DElem(Evap.panCoeff, i)) != 0))
                        {
                            return error_setInpError(ERR_NUMBER, tok[(i + 1)]);
                        }
                    }
                }
                break;
        }
        return 0;
    }

    // climate.c:377
    static int climate_readAdjustments(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        if ((ntoks == 1))
        {
            return 0;
        }
        if ((match(tok[0], L6) != 0))
        {
            if ((ntoks < 13))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            for (i = 1; (i < 13); i++)
            {
                if (!(getDouble(tok[i], new DElem(Adjust.temp, (i - 1))) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[i]);
                }
            }
            return 0;
        }
        if ((match(tok[0], L7) != 0))
        {
            if ((ntoks < 13))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            for (i = 1; (i < 13); i++)
            {
                if (!(getDouble(tok[i], new DElem(Adjust.evap, (i - 1))) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[i]);
                }
            }
            return 0;
        }
        if ((match(tok[0], L8) != 0))
        {
            if ((ntoks < 13))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            for (i = 1; (i < 13); i++)
            {
                if (!(getDouble(tok[i], new DElem(Adjust.rain, (i - 1))) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[i]);
                }
            }
            return 0;
        }
        if ((match(tok[0], L9) != 0))
        {
            if ((ntoks < 13))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            for (i = 1; (i < 13); i++)
            {
                if (!(getDouble(tok[i], new DElem(Adjust.hydcon, (i - 1))) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[i]);
                }
                if ((Adjust.hydcon[(i - 1)] <= 0.0))
                {
                    Adjust.hydcon[(i - 1)] = 1.0;
                }
            }
            return 0;
        }
        if ((match(tok[0], L10) != 0))
        {
            if ((ntoks < 3))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            i = project_findObject(SUBCATCH, tok[1]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[1]);
            }
            j = project_findObject(TIMEPATTERN, tok[2]);
            if ((j < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
            Subcatch[i].nPervPattern = j;
            return 0;
        }
        if ((match(tok[0], L11) != 0))
        {
            if ((ntoks < 3))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            i = project_findObject(SUBCATCH, tok[1]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[1]);
            }
            j = project_findObject(TIMEPATTERN, tok[2]);
            if ((j < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
            Subcatch[i].dStorePattern = j;
            return 0;
        }
        if ((match(tok[0], L12) != 0))
        {
            if ((ntoks < 3))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            i = project_findObject(SUBCATCH, tok[1]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, tok[1]);
            }
            j = project_findObject(TIMEPATTERN, tok[2]);
            if ((j < 0))
            {
                return error_setInpError(ERR_NAME, tok[2]);
            }
            Subcatch[i].infilPattern = j;
            return 0;
        }
        return error_setInpError(ERR_KEYWORD, tok[0]);
    }

    // climate.c:480
    static void climate_validate()
    {
        int i = 0;
        double a = 0.0;
        double z = 0.0;
        double pa = 0.0;
        if ((((Wind.type == FILE_WIND) || (Evap.type == FILE_EVAP)) || (Evap.type == TEMPERATURE_EVAP)))
        {
            if ((Fclimate.mode == NO_FILE))
            {
                report_writeErrorMsg(ERR_NO_CLIMATE_FILE, L0);
            }
        }
        if ((Fclimate.mode == USE_FILE))
        {
            climate_openFile();
        }
        if (((((Snow.tipm < 0.0) || (Snow.tipm > 1.0)) || (Snow.rnm < 0.0)) || (Snow.rnm > 1.0)))
        {
            report_writeErrorMsg(ERR_SNOWMELT_PARAMS, L0);
        }
        a = Temp.anglat;
        if (((a <= (-89.99)) || (a >= 89.99)))
        {
            report_writeErrorMsg(ERR_SNOWMELT_PARAMS, L0);
        }
        else
        {
            Temp.tanAnglat = Glibm.tan(((a * 3.141592654) / 180.0));
        }
        z = (Temp.elev / 1000.0);
        if ((z <= 0.0))
        {
            pa = 29.9;
        }
        else
        {
            pa = ((29.9 - (1.02 * z)) + (0.0032 * Glibm.pow(z, 2.4)));
        }
        Temp.gamma = (0.000359 * pa);
        for (i = 0; (i < 12); i++)
        {
            if ((UnitSystem == SI))
            {
                Adjust.temp[i] *= (9.0 / 5.0);
            }
            Adjust.evap[i] /= (UCF(EVAPRATE));
        }
    }

    // climate.c:531
    static void climate_openFile()
    {
        int i = 0;
        IBox m = new IBox(0);
        IBox y = new IBox(0);
        if ((((Fclimate.file = R.fopen(Fclimate.name, L13))) == null))
        {
            report_writeErrorMsg(ERR_CLIMATE_FILE_OPEN, Fclimate.name);
            return;
        }
        FileValue[TMIN] = Temp.ta;
        FileValue[TMAX] = Temp.ta;
        FileValue[EVAP] = 0.0;
        FileValue[WIND] = 0.0;
        FileFormat = getFileFormat();
        if ((FileFormat == UNKNOWN_FORMAT))
        {
            report_writeErrorMsg(ERR_CLIMATE_FILE_READ, Fclimate.name);
            return;
        }
        R.rewind(Fclimate.file);
        sstrncpy(FileLine, L0, 0);
        if ((Temp.fileStartDate == (-693594)))
        {
            datetime_decodeDate(StartDate.v, FileYear, FileMonth, FileDay);
        }
        else
        {
            datetime_decodeDate(Temp.fileStartDate, FileYear, FileMonth, FileDay);
        }
        while (!(R.feof(Fclimate.file) != 0))
        {
            sstrncpy(FileLine, L0, 0);
            readFileLine(y, m);
            if (((y.v == FileYear.v) && (m.v == FileMonth.v)))
            {
                break;
            }
        }
        if ((R.feof(Fclimate.file) != 0))
        {
            report_writeErrorMsg(ERR_CLIMATE_END_OF_FILE, Fclimate.name);
            return;
        }
        if (!(ErrorCode != 0))
        {
            FileElapsedDays = 0;
            FileLastDay = datetime_daysPerMonth(FileYear.v, FileMonth.v);
            readFileValues();
            for (i = TMIN; (i <= WIND); i++)
            {
                if ((FileData[i][FileDay.v] == (-10000000000.0)))
                {
                    continue;
                }
                FileValue[i] = FileData[i][FileDay.v];
            }
        }
    }

    // climate.c:598
    static void climate_initState()
    {
        LastDay = (-693594);
        Temp.tmax = (-10000000000.0);
        Snow.removed = 0.0;
        NextEvapDate.v = StartDate.v;
        NextEvapRate.v = 0.0;
        if (((Evap.type == TIMESERIES_EVAP) && (Evap.tSeries >= 0)))
        {
            table_getFirstEntry(Tseries[Evap.tSeries], NextEvapDate, NextEvapRate);
            if ((NextEvapDate.v < StartDate.v))
            {
                setNextEvapDate(StartDate.v);
            }
            Evap.rate = (NextEvapRate.v / UCF(EVAPRATE));
            setNextEvapDate(NextEvapDate.v);
        }
        if ((Evap.type == TEMPERATURE_EVAP))
        {
            Tma.maxCount = (int) ((56L / 8L));
            Tma.count = 0;
            Tma.front = 0;
            Tma.tAve = 0.0;
            Tma.tRng = 0.0;
        }
    }

    // climate.c:641
    static void climate_setState(double theDate)
    {
        if ((Fclimate.mode == USE_FILE))
        {
            updateFileValues(theDate);
        }
        if ((Temp.dataSource != NO_TEMP))
        {
            setTemp(theDate);
        }
        setEvap(theDate);
        setWind(theDate);
        Adjust.rainFactor = Adjust.rain[(datetime_monthOfYear(theDate) - 1)];
        Adjust.hydconFactor = Adjust.hydcon[(datetime_monthOfYear(theDate) - 1)];
        setNextEvapDate(theDate);
    }

    // climate.c:659
    static double climate_getNextEvapDate()
    {
        return NextEvapDate.v;
    }

    // climate.c:671
    static void setNextEvapDate(double theDate)
    {
        IBox yr = new IBox(0);
        IBox mon = new IBox(0);
        IBox day = new IBox(0);
        int k = 0;
        DBox d = new DBox(0.0);
        DBox e = new DBox(0.0);
        if ((NextEvapDate.v > theDate))
        {
            return;
        }
        switch (Evap.type) {
            case CONSTANT_EVAP:
                NextEvapDate.v = (theDate + 365.0);
                break;
            case MONTHLY_EVAP:
                datetime_decodeDate(theDate, yr, mon, day);
                if ((mon.v == 12))
                {
                    mon.v = 1;
                    yr.v++;
                }
                else
                {
                    mon.v++;
                }
                NextEvapDate.v = datetime_encodeDate(yr.v, mon.v, 1);
                break;
            case TIMESERIES_EVAP:
                k = Evap.tSeries;
                if ((k >= 0))
                {
                    NextEvapDate.v = (theDate + 365.0);
                    while (((table_getNextEntry(Tseries[k], d, e) != 0) && (d.v <= EndDateTime)))
                    {
                        if ((d.v >= theDate))
                        {
                            NextEvapDate.v = d.v;
                            NextEvapRate.v = e.v;
                            break;
                        }
                    }
                }
                break;
            case FILE_EVAP:
                NextEvapDate.v = (Math.floor(theDate) + 1.0);
                break;
            default:
                NextEvapDate.v = (theDate + 365.0);
        }
    }

    // climate.c:734
    static void updateFileValues(double theDate)
    {
        int i = 0;
        int deltaDays = 0;
        deltaDays = R.D2I(((Math.floor(theDate) - Math.floor(StartDateTime))));
        if ((deltaDays > FileElapsedDays))
        {
            FileElapsedDays++;
            FileDay.v++;
            if ((FileDay.v > FileLastDay))
            {
                FileMonth.v++;
                if ((FileMonth.v > 12))
                {
                    FileMonth.v = 1;
                    FileYear.v++;
                }
                readFileValues();
                FileDay.v = 1;
                FileLastDay = datetime_daysPerMonth(FileYear.v, FileMonth.v);
            }
            for (i = TMIN; (i <= WIND); i++)
            {
                if ((FileData[i][FileDay.v] == (-10000000000.0)))
                {
                    continue;
                }
                FileValue[i] = FileData[i][FileDay.v];
            }
        }
    }

    // climate.c:782
    static void setTemp(double theDate)
    {
        int j = 0;
        int k = 0;
        int mon = 0;
        int day = 0;
        double theDay = 0.0;
        double hour = 0.0;
        double tmp = 0.0;
        mon = datetime_monthOfYear(theDate);
        theDay = Math.floor(theDate);
        if ((theDay > LastDay))
        {
            day = datetime_dayOfYear(theDate);
            if ((Temp.dataSource == FILE_TEMP))
            {
                Tmin = (FileValue[TMIN] + Adjust.temp[(mon - 1)]);
                Tmax = (FileValue[TMAX] + Adjust.temp[(mon - 1)]);
                if ((Tmin > Tmax))
                {
                    tmp = Tmin;
                    Tmin = Tmax;
                    Tmax = tmp;
                }
                updateTempTimes(day);
                if ((Evap.type == TEMPERATURE_EVAP))
                {
                    updateTempMoveAve(Tmin, Tmax);
                    FileValue[EVAP] = getTempEvap(day, Tma.tAve, Tma.tRng);
                }
            }
            Snow.season = Glibm.sin((0.0172615 * ((day - 81.0))));
            for (j = 0; (j < Nobjects[SNOWMELT]); j++)
            {
                snow_setMeltCoeffs(j, Snow.season);
            }
            LastDay = theDate;
        }
        if ((Temp.dataSource == FILE_TEMP))
        {
            hour = (((theDate - theDay)) * 24.0);
            if ((hour < Hrsr))
            {
                Temp.ta = (Tmin + ((Trng1 / 2.0) * Glibm.sin(((3.141592654 / Dydif) * ((Hrsr - hour))))));
            }
            else if (((hour >= Hrsr) && (hour <= Hrss)))
            {
                Temp.ta = (Tave + (Trng * Glibm.sin(((3.141592654 / Dhrdy) * ((Hrday - hour))))));
            }
            else
            {
                Temp.ta = (Tmax - (Trng * Glibm.sin(((3.141592654 / Dydif) * ((hour - Hrss))))));
            }
        }
        if ((Temp.dataSource == TSERIES_TEMP))
        {
            k = Temp.tSeries;
            if ((k >= 0))
            {
                Temp.ta = table_tseriesLookup(Tseries[k], theDate, 1);
                if ((UnitSystem == SI))
                {
                    Temp.ta = ((((9.0 / 5.0)) * Temp.ta) + 32.0);
                }
                Temp.ta += (Adjust.temp[(mon - 1)]);
            }
        }
        Temp.ea = (8.1175e6 * Glibm.exp(((-7701.544) / ((Temp.ta + 405.0265)))));
    }

    // climate.c:872
    static void setEvap(double theDate)
    {
        int k = 0;
        int mon = datetime_monthOfYear(theDate);
        switch (Evap.type) {
            case CONSTANT_EVAP:
                Evap.rate = (Evap.monthlyEvap[0] / UCF(EVAPRATE));
                break;
            case MONTHLY_EVAP:
                Evap.rate = (Evap.monthlyEvap[(mon - 1)] / UCF(EVAPRATE));
                break;
            case TIMESERIES_EVAP:
                if ((theDate >= NextEvapDate.v))
                {
                    Evap.rate = (NextEvapRate.v / UCF(EVAPRATE));
                }
                break;
            case FILE_EVAP:
                Evap.rate = (FileValue[EVAP] / UCF(EVAPRATE));
                Evap.rate *= (Evap.panCoeff[(mon - 1)]);
                break;
            case TEMPERATURE_EVAP:
                Evap.rate = (FileValue[EVAP] / UCF(EVAPRATE));
                break;
            default:
                Evap.rate = 0.0;
        }
        Evap.rate += (Adjust.evap[(mon - 1)]);
        Evap.recoveryFactor = 1.0;
        k = Evap.recoveryPattern;
        if (((k >= 0) && (Pattern[k].type == MONTHLY_PATTERN)))
        {
            Evap.recoveryFactor = Pattern[k].factor[(mon - 1)];
        }
    }

    // climate.c:923
    static void setWind(double theDate)
    {
        IBox yr = new IBox(0);
        IBox mon = new IBox(0);
        IBox day = new IBox(0);
        switch (Wind.type) {
            case MONTHLY_WIND:
                datetime_decodeDate(theDate, yr, mon, day);
                Wind.ws = (Wind.aws[(mon.v - 1)] / UCF(WINDSPEED));
                break;
            case FILE_WIND:
                Wind.ws = FileValue[WIND];
                break;
            default:
                Wind.ws = 0.0;
        }
    }

    // climate.c:949
    static void updateTempTimes(int day)
    {
        double decl = 0.0;
        double hrang = 0.0;
        double arg = 0.0;
        decl = (0.40928 * Glibm.cos((0.017202 * ((172.0 - day)))));
        arg = ((-(Glibm.tan(decl))) * Temp.tanAnglat);
        if ((arg <= (-1.0)))
        {
            arg = 3.141592654;
        }
        else if ((arg >= 1.0))
        {
            arg = 0.0;
        }
        else
        {
            arg = Glibm.acos(arg);
        }
        hrang = (3.8197 * arg);
        Hrsr = ((12.0 - hrang) + Temp.dtlong);
        Hrss = (((12.0 + hrang) + Temp.dtlong) - 3.0);
        Dhrdy = (Hrsr - Hrss);
        Dydif = ((24.0 + Hrsr) - Hrss);
        Hrday = (((Hrsr + Hrss)) / 2.0);
        Tave = (((Tmin + Tmax)) / 2.0);
        Trng = (((Tmax - Tmin)) / 2.0);
        if ((Temp.tmax == (-10000000000.0)))
        {
            Trng1 = (Tmax - Tmin);
        }
        else
        {
            Trng1 = (Temp.tmax - Tmin);
        }
        Temp.tmax = Tmax;
    }

    // climate.c:981
    static double getTempEvap(int day, double tave, double trng)
    {
        double a = ((2.0 * 3.141592654) / 365.0);
        double ta = ((((tave - 32.0)) * 5.0) / 9.0);
        double tr = ((trng * 5.0) / 9.0);
        double lamda = (2.50 - (0.002361 * ta));
        double dr = (1.0 + (0.033 * Glibm.cos((a * day))));
        double phi = (((Temp.anglat * 2.0) * 3.141592654) / 360.0);
        double del = (0.4093 * Glibm.sin((a * ((284.0 + ((double) (day)))))));
        double omega = Glibm.acos(((-(Glibm.tan(phi))) * Glibm.tan(del)));
        double ra = ((37.6 * dr) * ((((omega * Glibm.sin(phi)) * Glibm.sin(del)) + ((Glibm.cos(phi) * Glibm.cos(del)) * Glibm.sin(omega)))));
        double e = ((((0.0023 * ra) / lamda) * Math.sqrt(tr)) * ((ta + 17.8)));
        if ((e < 0.0))
        {
            e = 0.0;
        }
        if ((UnitSystem == US))
        {
            e /= 25.4;
        }
        return e;
    }

    // climate.c:1010
    static int getFileFormat()
    {
        CPtr recdType = R.bufFrom(L0, 4);
        CPtr elemType = R.bufFrom(L0, 4);
        CPtr filler = R.bufFrom(L0, 5);
        CPtr staID = R.buf(80);
        CPtr s = R.buf(80);
        CPtr line = R.buf(1024);
        IBox y = new IBox(0);
        IBox m = new IBox(0);
        IBox d = new IBox(0);
        int n = 0;
        if (((R.fgets(line, (int) (1024), Fclimate.file)) == null))
        {
            return UNKNOWN_FORMAT;
        }
        sstrncpy(recdType, line, 3);
        sstrncpy(filler, line.add(23), 4);
        if (((R.strcmp(recdType, L14) == 0) && (R.strcmp(filler, L15) == 0)))
        {
            return TD3200;
        }
        if ((R.strlen(line) >= 233))
        {
            sstrncpy(elemType, line.add(13), 3);
            n = R.atoi(elemType);
            if ((((n == 1) || (n == 2)) || (n == 151)))
            {
                return DLY0204;
            }
        }
        n = R.sscanf(line, L16, staID, y, m, d, s);
        if ((n == 5))
        {
            return USER_PREPARED;
        }
        if ((isGhcndFormat(line) != 0))
        {
            return GHCND;
        }
        return UNKNOWN_FORMAT;
    }

    // climate.c:1055
    static void readFileLine(IRef y, IRef m)
    {
        while ((R.strlen(FileLine) == 0))
        {
            if (((R.fgets(FileLine, (int) (1024), Fclimate.file)) == null))
            {
                return;
            }
            if ((FileLine.g(0) == 10))
            {
                FileLine.s(0, 0);
            }
        }
        switch (FileFormat) {
            case USER_PREPARED:
                readUserFileLine(y, m);
                break;
            case TD3200:
                readTD3200FileLine(y, m);
                break;
            case DLY0204:
                readDLY0204FileLine(y, m);
                break;
            case GHCND:
                readGhcndFileLine(y, m);
                break;
        }
    }

    // climate.c:1082
    static void readUserFileLine(IRef y, IRef m)
    {
        int n = 0;
        CPtr staID = R.buf(80);
        n = R.sscanf(FileLine, L17, staID, y, m);
        if ((n < 3))
        {
            report_writeErrorMsg(ERR_CLIMATE_FILE_READ, Fclimate.name);
        }
    }

    // climate.c:1101
    static void readTD3200FileLine(IRef y, IRef m)
    {
        CPtr recdType = R.bufFrom(L0, 4);
        CPtr year = R.bufFrom(L0, 5);
        CPtr month = R.bufFrom(L0, 3);
        if ((R.strlen(FileLine) < 30))
        {
            report_writeErrorMsg(ERR_CLIMATE_FILE_READ, Fclimate.name);
            return;
        }
        sstrncpy(recdType, FileLine, 3);
        if ((R.strcmp(recdType, L14) != 0))
        {
            report_writeErrorMsg(ERR_CLIMATE_FILE_READ, Fclimate.name);
            return;
        }
        sstrncpy(year, FileLine.add(17), 4);
        sstrncpy(month, FileLine.add(21), 2);
        y.set(R.atoi(year));
        m.set(R.atoi(month));
    }

    // climate.c:1137
    static void readDLY0204FileLine(IRef y, IRef m)
    {
        CPtr year = R.bufFrom(L0, 5);
        CPtr month = R.bufFrom(L0, 3);
        if ((R.strlen(FileLine) < 16))
        {
            report_writeErrorMsg(ERR_CLIMATE_FILE_READ, Fclimate.name);
            return;
        }
        sstrncpy(year, FileLine.add(7), 4);
        sstrncpy(month, FileLine.add(11), 2);
        y.set(R.atoi(year));
        m.set(R.atoi(month));
    }

    // climate.c:1164
    static void readFileValues()
    {
        int i = 0;
        int j = 0;
        IBox y = new IBox(0);
        IBox m = new IBox(0);
        for (i = 0; (i < MAXCLIMATEVARS); i++)
        {
            for (j = 0; (j < MAXDAYSPERMONTH); j++)
            {
                FileData[i][j] = (-10000000000.0);
            }
        }
        while (!(ErrorCode != 0))
        {
            if ((R.feof(Fclimate.file) != 0))
            {
                return;
            }
            readFileLine(y, m);
            if (((y.v > FileYear.v) || (m.v > FileMonth.v)))
            {
                return;
            }
            switch (FileFormat) {
                case USER_PREPARED:
                    parseUserFileLine();
                    break;
                case TD3200:
                    parseTD3200FileLine();
                    break;
                case DLY0204:
                    parseDLY0204FileLine();
                    break;
                case GHCND:
                    parseGhcndFileLine();
                    break;
            }
            sstrncpy(FileLine, L0, 0);
        }
    }

    // climate.c:1201
    static void parseUserFileLine()
    {
        int n = 0;
        IBox y = new IBox(0);
        IBox m = new IBox(0);
        IBox d = new IBox(0);
        CPtr staID = R.buf(80);
        CPtr s0 = R.bufFrom(L0, 80);
        CPtr s1 = R.bufFrom(L0, 80);
        CPtr s2 = R.bufFrom(L0, 80);
        CPtr s3 = R.bufFrom(L0, 80);
        double x = 0.0;
        n = R.sscanf(FileLine, L18, staID, y, m, d, s0, s1, s2, s3);
        if ((n < 4))
        {
            return;
        }
        if (((d.v < 1) || (d.v > 31)))
        {
            return;
        }
        if (((R.strlen(s0) > 0) && (s0.g(0) != 42)))
        {
            x = R.atof(s0);
            if ((UnitSystem == SI))
            {
                x = (((9.0 / 5.0) * x) + 32.0);
            }
            FileData[TMAX][d.v] = x;
        }
        if (((R.strlen(s1) > 0) && (s1.g(0) != 42)))
        {
            x = R.atof(s1);
            if ((UnitSystem == SI))
            {
                x = (((9.0 / 5.0) * x) + 32.0);
            }
            FileData[TMIN][d.v] = x;
        }
        if (((R.strlen(s2) > 0) && (s2.g(0) != 42)))
        {
            FileData[EVAP][d.v] = R.atof(s2);
        }
        if (((R.strlen(s3) > 0) && (s3.g(0) != 42)))
        {
            FileData[WIND][d.v] = R.atof(s3);
        }
    }

    // climate.c:1249
    static void parseTD3200FileLine()
    {
        int i = 0;
        CPtr param = R.bufFrom(L0, 5);
        sstrncpy(param, FileLine.add(11), 4);
        for (i = 0; (i < MAXCLIMATEVARS); i++)
        {
            if ((R.strcmp(param, ClimateVarWords[i]) == 0))
            {
                setTD3200FileValues(i);
            }
        }
    }

    // climate.c:1271
    static void setTD3200FileValues(int i)
    {
        CPtr valCount = R.bufFrom(L0, 4);
        CPtr day = R.bufFrom(L0, 3);
        CPtr sign = R.bufFrom(L0, 2);
        CPtr value = R.bufFrom(L0, 6);
        CPtr flag2 = R.bufFrom(L0, 2);
        double x = 0.0;
        int nValues = 0;
        int j = 0;
        int k = 0;
        int d = 0;
        sstrncpy(valCount, FileLine.add(27), 3);
        nValues = R.atoi(valCount);
        if (((int) (R.strlen(FileLine)) >= ((12 * nValues) + 30)))
        {
            for (j = 0; (j < nValues); j++)
            {
                k = (30 + (j * 12));
                sstrncpy(day, FileLine.add(k), 2);
                sstrncpy(sign, FileLine.add((k + 4)), 1);
                sstrncpy(value, FileLine.add((k + 5)), 5);
                sstrncpy(flag2, FileLine.add((k + 11)), 1);
                d = R.atoi(day);
                if (((((R.strcmp(value, L19) != 0) && (((flag2.g(0) == 48) || (flag2.g(0) == 49)))) && (d > 0)) && (d <= 31)))
                {
                    x = R.atof(value);
                    if ((sign.g(0) == 45))
                    {
                        x = (-x);
                    }
                    if ((i == EVAP))
                    {
                        x /= 100.0;
                        if ((UnitSystem == SI))
                        {
                            x *= 25.4;
                        }
                    }
                    if ((i == WIND))
                    {
                        x /= 24.0;
                    }
                    FileData[i][d] = x;
                }
            }
        }
    }

    // climate.c:1336
    static void parseDLY0204FileLine()
    {
        int j = 0;
        int k = 0;
        int p = 0;
        CPtr param = R.bufFrom(L0, 4);
        CPtr sign = R.bufFrom(L0, 2);
        CPtr value = R.bufFrom(L0, 6);
        CPtr code = R.bufFrom(L0, 2);
        double x = 0.0;
        sstrncpy(param, FileLine.add(13), 3);
        p = R.atoi(param);
        if ((p == 1))
        {
            p = TMAX;
        }
        else if ((p == 2))
        {
            p = TMIN;
        }
        else if ((p == 151))
        {
            p = EVAP;
        }
        else
        {
            return;
        }
        if ((R.strlen(FileLine) < 233))
        {
            return;
        }
        k = 16;
        for (j = 1; (j <= 31); j++)
        {
            sstrncpy(sign, FileLine.add(k), 1);
            sstrncpy(value, FileLine.add((k + 1)), 5);
            sstrncpy(code, FileLine.add((k + 6)), 1);
            k += 7;
            if (((R.strcmp(value, L19) != 0) && (R.strcmp(value, L20) != 0)))
            {
                switch (p) {
                    case TMAX:
                    case TMIN:
                        x = (R.atof(value) / 10.0);
                        if ((sign.g(0) == 45))
                        {
                            x = (-x);
                        }
                        x = (((9.0 / 5.0) * x) + 32.0);
                        break;
                    case EVAP:
                        x = (R.atof(value) / 10.0);
                        if ((UnitSystem == US))
                        {
                            x /= 25.4;
                        }
                        break;
                    default:
                        return;
                }
                FileData[p][j] = x;
            }
        }
    }

    // climate.c:1401
    static int isGhcndFormat(CPtr line)
    {
        int i = 0;
        CPtr ptr = null;
        ptr = R.strstr(line, L21);
        if ((ptr == null))
        {
            return 0;
        }
        FileDateFieldPos = (int) ((((long) (ptr.o - line.o))));
        for (i = TMIN; (i <= WIND); i++)
        {
            FileFieldPos[i] = (-1);
        }
        ptr = R.strstr(line, L22);
        if ((ptr != null))
        {
            FileFieldPos[TMIN] = (int) ((((long) (ptr.o - line.o))));
        }
        ptr = R.strstr(line, L23);
        if ((ptr != null))
        {
            FileFieldPos[TMAX] = (int) ((((long) (ptr.o - line.o))));
        }
        ptr = R.strstr(line, L7);
        if ((ptr != null))
        {
            FileFieldPos[EVAP] = (int) ((((long) (ptr.o - line.o))));
        }
        FileWindType = WDMV;
        ptr = R.strstr(line, L24);
        if ((ptr == null))
        {
            FileWindType = AWND;
            ptr = R.strstr(line, L25);
        }
        if ((ptr != null))
        {
            FileFieldPos[WIND] = (int) ((((long) (ptr.o - line.o))));
        }
        for (i = TMIN; (i <= WIND); i++)
        {
            if ((FileFieldPos[i] >= 0))
            {
                return 1;
            }
        }
        return 0;
    }

    // climate.c:1445
    static void readGhcndFileLine(IRef y, IRef m)
    {
        int n = R.sscanf(FileLine.add(FileDateFieldPos), L26, y, m);
        if ((n != 2))
        {
            y.set((-99999));
            m.set((-99999));
        }
    }

    // climate.c:1463
    static void parseGhcndFileLine()
    {
        IBox y = new IBox(0);
        IBox m = new IBox(0);
        IBox d = new IBox(0);
        int n = 0;
        int i = 0;
        DBox v = new DBox(0.0);
        n = R.sscanf(FileLine.add(FileDateFieldPos), L27, y, m, d);
        if ((n < 3))
        {
            return;
        }
        if (((d.v < 1) || (d.v > 31)))
        {
            return;
        }
        for (i = TMIN; (i <= WIND); i++)
        {
            if ((FileFieldPos[i] >= 0))
            {
                if ((R.sscanf(FileLine.add(FileFieldPos[i]), L28, v) > 0))
                {
                    if ((Math.abs(v.v) < 9999.0))
                    {
                        FileData[i][d.v] = convertGhcndValue(i, v.v);
                    }
                }
            }
        }
    }

    // climate.c:1496
    static double convertGhcndValue(int var_, double v)
    {
        switch (var_) {
            case TMIN:
            case TMAX:
                switch (FileTempUnits) {
                    case DEG_C10:
                        return ((((v / 10.0) * 9.0) / 5.0) + 32.0);
                    case DEG_C:
                        return (((v * 9.0) / 5.0) + 32.0);
                    default:
                        return v;
                }
            case EVAP:
                switch (FileTempUnits) {
                    case DEG_C10:
                        v /= 10.0;
                        if ((UnitSystem == US))
                        {
                            v /= 25.4;
                        }
                        return v;
                    case DEG_C:
                        if ((UnitSystem == US))
                        {
                            v /= 25.4;
                        }
                        return v;
                    default:
                        if ((UnitSystem == SI))
                        {
                            v *= 25.4;
                        }
                        return v;
                }
            case WIND:
                switch (FileTempUnits) {
                    case DEG_C10:
                        if ((FileWindType == WDMV))
                        {
                            return ((v * 0.62137) / 24.0);
                        }
                        else
                        {
                            return ((((v / 10.0) / 1000.0) * 0.62137) * 3600.0);
                        }
                    case DEG_C:
                        if ((FileWindType == WDMV))
                        {
                            return ((v * 0.62137) / 24.0);
                        }
                        else
                        {
                            return (((v / 1000.0) * 0.62137) * 3600.0);
                        }
                    default:
                        if ((FileWindType == WDMV))
                        {
                            return (v / 24.0);
                        }
                        else
                        {
                            return v;
                        }
                }
            default:
                return v;
        }
    }

    // climate.c:1569
    static void updateTempMoveAve(double tmin, double tmax)
    {
        double ta = 0.0;
        double tr = 0.0;
        int kount = Tma.count;
        double count = kount;
        ta = (((tmin + tmax)) / 2.0);
        tr = Math.abs((tmax - tmin));
        if ((kount == Tma.maxCount))
        {
            Tma.tAve = (((((Tma.tAve * count) + ta) - Tma.ta[Tma.front])) / count);
            Tma.tRng = (((((Tma.tRng * count) + tr) - Tma.tr[Tma.front])) / count);
            Tma.ta[Tma.front] = ta;
            Tma.tr[Tma.front] = tr;
            Tma.front++;
            if ((Tma.front == count))
            {
                Tma.front = 0;
            }
        }
        else
        {
            Tma.tAve = ((((Tma.tAve * count) + ta)) / ((count + 1)));
            Tma.tRng = ((((Tma.tRng * count) + tr)) / ((count + 1)));
            Tma.ta[Tma.front] = ta;
            Tma.tr[Tma.front] = tr;
            Tma.count++;
            Tma.front++;
            if ((Tma.count == Tma.maxCount))
            {
                Tma.front = 0;
            }
        }
    }

}
