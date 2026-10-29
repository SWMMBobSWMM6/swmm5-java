// Generated from EPA SWMM 5.2.4 (rain.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_rain {
    static TRainStats RainStats = new TRainStats();
    static int Condition = 0;
    static int TimeOffset = 0;
    static int DataOffset = 0;
    static int ValueOffset = 0;
    static int RainType = 0;
    static int Interval = 0;
    static double UnitsFactor = 0.0;
    static float RainAccum = 0.0f;
    static CPtr StationID = null;
    static double AccumStartDate = 0.0;
    static double PreviousDate = 0.0;
    static int GageIndex = 0;
    static int hasStationName = 0;

    // rain.c:118
    static void rain_open()
    {
        int i = 0;
        int count = 0;
        count = 0;
        for (i = 0; (i < Nobjects[GAGE]); i++)
        {
            if ((Gage[i].dataSource == RAIN_FILE))
            {
                count++;
            }
        }
        Frain.file = (null);
        if ((count == 0))
        {
            Frain.mode = NO_FILE;
        }
        else
        {
            switch (Frain.mode) {
                case SCRATCH_FILE:
                    getTempFileName(Frain.name);
                    if ((((Frain.file = R.fopen(Frain.name, L166))) == null))
                    {
                        report_writeErrorMsg(ERR_RAIN_FILE_SCRATCH, L0);
                        return;
                    }
                    break;
                case USE_FILE:
                    if ((((Frain.file = R.fopen(Frain.name, L165))) == null))
                    {
                        report_writeErrorMsg(ERR_RAIN_FILE_OPEN, Frain.name);
                        return;
                    }
                    break;
                case SAVE_FILE:
                    if ((((Frain.file = R.fopen(Frain.name, L166))) == null))
                    {
                        report_writeErrorMsg(ERR_RAIN_FILE_OPEN, Frain.name);
                        return;
                    }
                    break;
            }
        }
        if (((Frain.mode == SCRATCH_FILE) || (Frain.mode == SAVE_FILE)))
        {
            createRainFile(count);
        }
        if ((Frain.mode != NO_FILE))
        {
            initRainFile();
        }
        rdii_openRdii();
    }

    // rain.c:184
    static void rain_close()
    {
        if ((Frain.file != null))
        {
            R.fclose(Frain.file);
            if ((Frain.mode == SCRATCH_FILE))
            {
                R.remove(Frain.name);
            }
        }
        Frain.file = (null);
        rdii_closeRdii();
    }

    // rain.c:202
    static void createRainFile(int count)
    {
        int i = 0;
        int k = 0;
        IBox kount = new IBox(count);
        int filePos1 = 0;
        IBox filePos2 = new IBox(0);
        IBox filePos3 = new IBox(0);
        IBox interval = new IBox(0);
        IBox dummy = new IBox((-1));
        CPtr staID = R.buf(1025);
        CPtr fileStamp = R.bufFrom(L327, 11);
        if (((ErrorCode != 0) || (!(Frain.file != null))))
        {
            return;
        }
        R.fwrite(fileStamp, 0, (int) (R.strlen(fileStamp)), Frain.file);
        R.fwrite1(kount.v, 4, Frain.file);
        filePos1 = (int) (R.ftell(Frain.file));
        if ((count > 0))
        {
            report_writeRainStats((-1), RainStats);
        }
        R.strcpy(staID, L33);
        for (i = 0; (i < count); i++)
        {
            R.fwrite(staID, 0, (int) ((1024 + 1)), Frain.file);
            for (k = 1; (k <= 3); k++)
            {
                R.fwrite1(dummy.v, 4, Frain.file);
            }
        }
        filePos2.v = (int) (R.ftell(Frain.file));
        for (i = 0; (i < Nobjects[GAGE]); i++)
        {
            if (((ErrorCode != 0) || (Gage[i].dataSource != RAIN_FILE)))
            {
                continue;
            }
            if ((rainFileConflict(i) != 0))
            {
                break;
            }
            R.fseek(Frain.file, filePos2.v, 0);
            if ((addGageToRainFile(i) != 0))
            {
                filePos3.v = (int) (R.ftell(Frain.file));
                R.fseek(Frain.file, filePos1, 0);
                sstrncpy(staID, Gage[i].staID, 1024);
                interval.v = Interval;
                R.fwrite(staID, 0, (int) ((1024 + 1)), Frain.file);
                R.fwrite1(interval.v, 4, Frain.file);
                R.fwrite1(filePos2.v, 4, Frain.file);
                R.fwrite1(filePos3.v, 4, Frain.file);
                filePos1 = (int) (R.ftell(Frain.file));
                filePos2.v = filePos3.v;
                report_writeRainStats(i, RainStats);
            }
        }
        if ((ErrorCode != 0))
        {
            R.fclose(Frain.file);
            Frain.file = (null);
            R.remove(Frain.name);
        }
    }

    // rain.c:278
    static int rainFileConflict(int i)
    {
        int j = 0;
        CPtr staID = Gage[i].staID;
        CPtr fname = Gage[i].fname;
        for (j = 0; (j < i); j++)
        {
            if (((strcomp(Gage[j].staID, staID) != 0) && (!(strcomp(Gage[j].fname, fname) != 0))))
            {
                report_writeErrorMsg(ERR_RAIN_FILE_CONFLICT, Gage[i].ID);
                return 1;
            }
        }
        return 0;
    }

    // rain.c:302
    static int addGageToRainFile(int i)
    {
        CFile f = null;
        int fileFormat = 0;
        IBox hdrLines = new IBox(0);
        StationID = null;
        if ((((f = R.fopen(Gage[i].fname, L13))) == null))
        {
            report_writeErrorMsg(ERR_RAIN_FILE_DATA, Gage[i].fname);
        }
        else
        {
            fileFormat = findFileFormat(f, i, hdrLines);
            if ((fileFormat == UNKNOWN_FORMAT))
            {
                report_writeErrorMsg(ERR_RAIN_FILE_FORMAT, Gage[i].fname);
            }
            else
            {
                GageIndex = i;
                readFile(f, fileFormat, hdrLines.v, Gage[i].startFileDate, Gage[i].endFileDate);
            }
            R.fclose(f);
        }
        if ((ErrorCode != 0))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    // rain.c:341
    static void initRainFile()
    {
        CPtr fileStamp = R.bufFrom(L327, 11);
        CPtr fStamp = R.bufFrom(L327, 11);
        int i = 0;
        IBox kount = new IBox(0);
        long filePos = 0L;
        if (((ErrorCode != 0) || (!(Frain.file != null))))
        {
            return;
        }
        R.rewind(Frain.file);
        R.fread(fStamp, 0, (int) (R.strlen(fileStamp)), Frain.file);
        if ((R.strcmp(fStamp, fileStamp) != 0))
        {
            report_writeErrorMsg(ERR_RAIN_IFACE_FORMAT, L0);
            return;
        }
        R.fread1(kount, 4, Frain.file);
        filePos = R.ftell(Frain.file);
        for (i = 0; (i < Nobjects[GAGE]); i++)
        {
            if (((ErrorCode != 0) || (Gage[i].dataSource != RAIN_FILE)))
            {
                continue;
            }
            R.fseek(Frain.file, filePos, 0);
            if (((!(findGageInFile(i, kount.v) != 0)) || (Gage[i].startFilePos == Gage[i].endFilePos)))
            {
                report_writeErrorMsg(ERR_RAIN_FILE_GAGE, Gage[i].ID);
            }
        }
    }

    // rain.c:385
    static int findGageInFile(int i, int kount)
    {
        int k = 0;
        IBox interval = new IBox(0);
        IBox filePos1 = new IBox(0);
        IBox filePos2 = new IBox(0);
        CPtr staID = R.bufFrom(L0, 1025);
        for (k = 1; (k <= kount); k++)
        {
            R.fread(staID, 0, (int) ((1024 + 1)), Frain.file);
            R.fread1(interval, 4, Frain.file);
            R.fread1(filePos1, 4, Frain.file);
            R.fread1(filePos2, 4, Frain.file);
            if ((R.strcmp(staID, Gage[i].staID) == 0))
            {
                Gage[i].rainType = RAINFALL_VOLUME;
                Gage[i].rainInterval = interval.v;
                Gage[i].startFilePos = filePos1.v;
                Gage[i].endFilePos = filePos2.v;
                Gage[i].currentFilePos = Gage[i].startFilePos;
                return 1;
            }
        }
        return 0;
    }

    // rain.c:420
    static int findFileFormat(CFile f, int i, IRef hdrLines)
    {
        int fileFormat = 0;
        int lineCount = 0;
        int maxCount = 5;
        int n = 0;
        IBox div = new IBox(0);
        LBox sn2 = new LBox(0L);
        CPtr recdType = R.buf(4);
        CPtr elemType = R.buf(5);
        CPtr coopID = R.bufFrom(L0, 6);
        CPtr line = R.buf(1024);
        IBox year = new IBox(0);
        IBox month = new IBox(0);
        IBox day = new IBox(0);
        IBox hour = new IBox(0);
        IBox minute = new IBox(0);
        IBox elem = new IBox(0);
        FBox x = new FBox(0.0f);
        fileFormat = UNKNOWN_FORMAT;
        hasStationName = 0;
        UnitsFactor = 1.0;
        Interval = 0;
        hdrLines.set(0);
        for (lineCount = 1; (lineCount <= maxCount); lineCount++)
        {
            if (((R.fgets(line, (int) (1024), f)) == null))
            {
                return fileFormat;
            }
            n = R.sscanf(line, L328, sn2, div, elemType);
            if ((n == 3))
            {
                Interval = getNWSInterval(elemType);
                TimeOffset = Interval;
                if ((Interval > 0))
                {
                    fileFormat = NWS_SPACE_DELIMITED;
                    break;
                }
            }
            n = R.sscanf(line.add(37), L329, div, elemType, recdType, year);
            if ((n == 4))
            {
                Interval = getNWSInterval(elemType);
                TimeOffset = Interval;
                if ((Interval > 0))
                {
                    fileFormat = NWS_SPACE_DELIMITED;
                    hasStationName = 1;
                    break;
                }
            }
            n = R.sscanf(line, L330, sn2, div, elemType);
            if ((n == 3))
            {
                Interval = getNWSInterval(elemType);
                TimeOffset = Interval;
                if ((Interval > 0))
                {
                    fileFormat = NWS_COMMA_DELIMITED;
                    break;
                }
            }
            n = R.sscanf(line.add(37), L331, div, elemType, recdType, year);
            if ((n == 4))
            {
                Interval = getNWSInterval(elemType);
                TimeOffset = Interval;
                if ((Interval > 0))
                {
                    fileFormat = NWS_COMMA_DELIMITED;
                    hasStationName = 1;
                    break;
                }
            }
            n = R.sscanf(line, L332, recdType, sn2, div, elemType);
            if ((n == 4))
            {
                Interval = getNWSInterval(elemType);
                TimeOffset = Interval;
                if ((Interval > 0))
                {
                    fileFormat = NWS_TAPE;
                    break;
                }
            }
            n = R.sscanf(line, L333, coopID, sn2);
            if (((n == 2) && (R.strcmp(coopID, L334) == 0)))
            {
                fileFormat = findNWSOnlineFormat(f, line);
                break;
            }
            n = R.sscanf(line, L335, sn2, year, month, day, elem);
            if ((n == 5))
            {
                if (((elem.v == 123) && (R.strlen(line) >= 185)))
                {
                    fileFormat = AES_HLY;
                    Interval = 3600;
                    TimeOffset = Interval;
                    UnitsFactor = (1.0 / 25.4);
                    break;
                }
            }
            n = R.sscanf(line, L336, sn2, year, month, day, elem);
            if ((n == 5))
            {
                if (((elem.v == 159) && (R.strlen(line) >= 691)))
                {
                    fileFormat = CMC_FIF;
                    Interval = 900;
                }
                else if (((elem.v == 123) && (R.strlen(line) >= 186)))
                {
                    fileFormat = CMC_HLY;
                    Interval = 3600;
                }
                if (((fileFormat == CMC_FIF) || (fileFormat == CMC_HLY)))
                {
                    TimeOffset = Interval;
                    UnitsFactor = (1.0 / 25.4);
                    break;
                }
            }
            if ((parseStdLine(line, year, month, day, hour, minute, x) != 0))
            {
                fileFormat = STD_SPACE_DELIMITED;
                RainType = Gage[i].rainType;
                Interval = Gage[i].rainInterval;
                if ((Gage[i].rainUnits == SI))
                {
                    UnitsFactor = (1.0 / 25.4);
                }
                TimeOffset = 0;
                StationID = Gage[i].staID;
                break;
            }
            hdrLines.set(hdrLines.get() + 1);
        }
        if ((fileFormat != UNKNOWN_FORMAT))
        {
            Gage[i].rainInterval = Interval;
        }
        return fileFormat;
    }

    // rain.c:584
    static int findNWSOnlineFormat(CFile f, CPtr line)
    {
        int n = 0;
        int fileFormat = UNKNOWN_FORMAT;
        CPtr str = null;
        R.rewind(f);
        R.fgets(line, (int) (1024), f);
        if ((((str = R.strstr(line, L337))) != null))
        {
            Interval = 3600;
            TimeOffset = Interval;
            ValueOffset = (int) ((((long) (str.o - line.o))));
            fileFormat = NWS_ONLINE_60;
        }
        else if ((((str = R.strstr(line, L338))) != null))
        {
            Interval = 900;
            TimeOffset = Interval;
            ValueOffset = (int) ((((long) (str.o - line.o))));
            fileFormat = NWS_ONLINE_15;
        }
        else
        {
            return UNKNOWN_FORMAT;
        }
        for (n = 1; (n <= 5); n++)
        {
            if (((R.fgets(line, (int) (1024), f)) == null))
            {
                return UNKNOWN_FORMAT;
            }
            if (((R.strstr(line, L334)) == null))
            {
                continue;
            }
            str = R.strrchr(line, 58);
            if ((str == null))
            {
                return UNKNOWN_FORMAT;
            }
            n = (int) ((((long) (str.o - line.o))));
            DataOffset = (n - 11);
            return fileFormat;
        }
        return UNKNOWN_FORMAT;
    }

    // rain.c:641
    static int getNWSInterval(CPtr elemType)
    {
        if ((R.strcmp(elemType, L337) == 0))
        {
            return 3600;
        }
        else if ((R.strcmp(elemType, L338) == 0))
        {
            return 900;
        }
        else if ((R.strcmp(elemType, L339) == 0))
        {
            return 900;
        }
        else
        {
            return 0;
        }
    }

    // rain.c:656
    static void readFile(CFile f, int fileFormat, int hdrLines, double day1, double day2)
    {
        CPtr line = R.buf(1024);
        int i = 0;
        int n = 0;
        R.rewind(f);
        RainStats.startDate = (-693594);
        RainStats.endDate = (-693594);
        RainStats.periodsRain = 0;
        RainStats.periodsMissing = 0;
        RainStats.periodsMalfunc = 0;
        RainAccum = (float) (0.0);
        AccumStartDate = (-693594);
        PreviousDate = (-693594);
        for (i = 1; (i <= hdrLines); i++)
        {
            if (((R.fgets(line, (int) (1024), f)) == null))
            {
                return;
            }
        }
        while (((R.fgets(line, (int) (1024), f)) != null))
        {
            switch (fileFormat) {
                case STD_SPACE_DELIMITED:
                    n = readStdLine(line, day1, day2);
                    break;
                case NWS_TAPE:
                case NWS_SPACE_DELIMITED:
                case NWS_COMMA_DELIMITED:
                case NWS_ONLINE_60:
                case NWS_ONLINE_15:
                    n = readNWSLine(line, fileFormat, day1, day2);
                    break;
                case AES_HLY:
                case CMC_FIF:
                case CMC_HLY:
                    n = readCMCLine(line, fileFormat, day1, day2);
                    break;
                default:
                    n = (-1);
                    break;
            }
            if ((n < 0))
            {
                break;
            }
        }
    }

    // rain.c:717
    static int readNWSLine(CPtr line, int fileFormat, double day1, double day2)
    {
        IBox flag1 = new IBox(0);
        IBox flag2 = new IBox(0);
        int isMissing = 0;
        double date1 = 0.0;
        long result = 1;
        int k = 0;
        IBox y = new IBox(0);
        IBox m = new IBox(0);
        IBox d = new IBox(0);
        IBox n = new IBox(0);
        IBox hour = new IBox(0);
        IBox minute = new IBox(0);
        LBox v = new LBox(0L);
        float x = 0.0f;
        int lineLength = ((int) (R.strlen(line)) - 1);
        int nameLength = 0;
        switch (fileFormat) {
            case NWS_TAPE:
                if ((lineLength <= 30))
                {
                    return 0;
                }
                if ((R.sscanf(line.add(17), L340, y, m, d, n) < 4))
                {
                    return 0;
                }
                k = 30;
                break;
            case NWS_SPACE_DELIMITED:
                if ((hasStationName != 0))
                {
                    nameLength = 31;
                }
                if ((lineLength <= (28 + nameLength)))
                {
                    return 0;
                }
                k = (18 + nameLength);
                if ((R.sscanf(line.add(k), L341, y, m, d) < 3))
                {
                    return 0;
                }
                k = (k + 10);
                break;
            case NWS_COMMA_DELIMITED:
                if ((lineLength <= 28))
                {
                    return 0;
                }
                if ((R.sscanf(line.add(18), L342, y, m, d) < 3))
                {
                    return 0;
                }
                k = 28;
                break;
            case NWS_ONLINE_60:
            case NWS_ONLINE_15:
                if ((lineLength <= (DataOffset + 23)))
                {
                    return 0;
                }
                if ((R.sscanf(line.add(DataOffset), L27, y, m, d) < 3))
                {
                    return 0;
                }
                k = (DataOffset + 8);
                break;
            default:
                return 0;
        }
        date1 = datetime_encodeDate(y.v, m.v, d.v);
        if (((day1 != (-693594)) && (date1 < day1)))
        {
            return 0;
        }
        if (((day2 != (-693594)) && (date1 > day2)))
        {
            return (-1);
        }
        while ((k < lineLength))
        {
            flag1.v = 0;
            flag2.v = 0;
            v.v = 99999;
            hour.v = 25;
            minute.v = 0;
            switch (fileFormat) {
                case NWS_TAPE:
                    n.v = R.sscanf(line.add(k), L343, hour, minute, v, flag1, flag2);
                    k += 12;
                    break;
                case NWS_SPACE_DELIMITED:
                    n.v = R.sscanf(line.add(k), L344, hour, minute, v, flag1, flag2);
                    k += 16;
                    break;
                case NWS_COMMA_DELIMITED:
                    n.v = R.sscanf(line.add(k), L345, hour, minute, v, flag1, flag2);
                    k += 16;
                    break;
                case NWS_ONLINE_60:
                case NWS_ONLINE_15:
                    n.v = R.sscanf(line.add(k), L346, hour, minute);
                    {
                        CPtr __c1 = R.buf(2); __c1.s(0, flag1.v);
                        n.v += (readNwsOnlineValue(line.add(ValueOffset), v, __c1));
                        flag1.v = __c1.g(0);
                    }
                    if ((hour.v == 0))
                    {
                        hour.v = 24;
                        date1 -= 1.0;
                    }
                    k += lineLength;
                    break;
                default:
                    n.v = 0;
            }
            if (((n.v < 3) || (hour.v >= 25)))
            {
                break;
            }
            setCondition(flag1.v);
            if ((((Condition == DELETED_PERIOD) || (Condition == MISSING_PERIOD)) || (flag1.v == 77)))
            {
                isMissing = 1;
            }
            else if ((v.v >= 9999))
            {
                isMissing = 1;
            }
            else
            {
                isMissing = 0;
            }
            if ((flag1.v == 97))
            {
                AccumStartDate = (date1 + datetime_encodeTime(hour.v, minute.v, 0));
            }
            else if ((flag1.v == 65))
            {
                saveAccumRainfall(date1, hour.v, minute.v, v.v);
            }
            else
            {
                x = (((float) (v.v)) / 100.0f);
                if (((x > (float) (0)) || (isMissing != 0)))
                {
                    saveRainfall(date1, hour.v, minute.v, x, isMissing);
                }
            }
            if ((((flag1.v == 65) || (flag1.v == 125)) || (flag1.v == 93)))
            {
                Condition = 0;
            }
        }
        return (int) (result);
    }

    // rain.c:862
    static int readNwsOnlineValue(CPtr s, LRef v, CPtr flag)
    {
        int n = 0;
        FBox x = new FBox(99.98999786376953f);
        if ((R.strchr(s, 46) != null))
        {
            n = R.sscanf(s, L347, x, flag);
            v.set(R.D2L((((100.0f * x.v) + 0.5f))));
        }
        else
        {
            n = R.sscanf(s, L348, v, flag);
        }
        return n;
    }

    // rain.c:891
    static void setCondition(int flag)
    {
        switch (flag) {
            case 97:
            case 65:
                Condition = ACCUMULATED_PERIOD;
                break;
            case 123:
            case 125:
                Condition = DELETED_PERIOD;
                break;
            case 91:
            case 93:
                Condition = MISSING_PERIOD;
                break;
            default:
                Condition = NO_CONDITION;
        }
    }

    // rain.c:914
    static int readCMCLine(CPtr line, int fileFormat, double day1, double day2)
    {
        IBox flag = new IBox(0);
        int isMissing = 0;
        double date1 = 0.0;
        LBox sn = new LBox(0L);
        LBox v = new LBox(0L);
        int col = 0;
        int j = 0;
        int jMax = 0;
        IBox elem = new IBox(0);
        IBox y = new IBox(0);
        IBox m = new IBox(0);
        IBox d = new IBox(0);
        int hour = 0;
        int minute = 0;
        float x = 0.0f;
        if ((fileFormat == AES_HLY))
        {
            if ((R.sscanf(line, L335, sn, y, m, d, elem) < 5))
            {
                return 0;
            }
            if ((y.v < 100))
            {
                y.v = (y.v + 2000);
            }
            else
            {
                y.v = (y.v + 1000);
            }
            col = 17;
        }
        else
        {
            if ((R.sscanf(line, L336, sn, y, m, d, elem) < 5))
            {
                return 0;
            }
            col = 18;
        }
        date1 = datetime_encodeDate(y.v, m.v, d.v);
        if (((day1 != (-693594)) && (date1 < day1)))
        {
            return 0;
        }
        if (((day2 != (-693594)) && (date1 > day2)))
        {
            return (-1);
        }
        if (((fileFormat == AES_HLY) && (elem.v != 123)))
        {
            return 0;
        }
        else if (((fileFormat == CMC_FIF) && (elem.v != 159)))
        {
            return 0;
        }
        else if (((fileFormat == CMC_HLY) && (elem.v != 123)))
        {
            return 0;
        }
        hour = 0;
        minute = 0;
        jMax = 24;
        if ((fileFormat == CMC_FIF))
        {
            jMax = 96;
        }
        for (j = 1; (j <= jMax); j++)
        {
            if ((R.sscanf(line.add(col), L349, v, flag) < 2))
            {
                return 0;
            }
            col += 7;
            if ((v.v == (-99999)))
            {
                isMissing = 1;
            }
            else
            {
                isMissing = 0;
            }
            x = ((float) ((((((double) (v.v)) / 10.0) / 25.4))));
            if (((x > (float) (0)) || (isMissing != 0)))
            {
                saveRainfall(date1, hour, minute, x, isMissing);
            }
            if ((fileFormat == CMC_FIF))
            {
                minute += 15;
                if ((minute == 60))
                {
                    minute = 0;
                    hour++;
                }
            }
            else
            {
                hour++;
            }
        }
        return 1;
    }

    // rain.c:994
    static int readStdLine(CPtr line, double day1, double day2)
    {
        double date1 = 0.0;
        double date2 = 0.0;
        IBox year = new IBox(0);
        IBox month = new IBox(0);
        IBox day = new IBox(0);
        IBox hour = new IBox(0);
        IBox minute = new IBox(0);
        FBox x = new FBox(0.0f);
        if (!(parseStdLine(line, year, month, day, hour, minute, x) != 0))
        {
            return 0;
        }
        date1 = datetime_encodeDate(year.v, month.v, day.v);
        if (((day1 != (-693594)) && (date1 < day1)))
        {
            return 0;
        }
        if (((day2 != (-693594)) && (date1 > day2)))
        {
            return (-1);
        }
        date2 = (date1 + datetime_encodeTime(hour.v, minute.v, 0));
        if ((date2 <= PreviousDate))
        {
            report_writeErrorMsg(ERR_RAIN_FILE_SEQUENCE, Gage[GageIndex].fname);
            report_writeLine(line);
            return (-1);
        }
        PreviousDate = date2;
        switch (RainType) {
            case RAINFALL_INTENSITY:
                x.v = ((x.v * (float) (Interval)) / 3600.0f);
                break;
            case CUMULATIVE_RAINFALL:
                if ((x.v >= RainAccum))
                {
                    x.v = (x.v - RainAccum);
                    RainAccum += x.v;
                }
                else
                {
                    RainAccum = x.v;
                }
                break;
        }
        x.v *= ((float) (UnitsFactor));
        saveRainfall(date1, hour.v, minute.v, x.v, 0);
        return 1;
    }

    // rain.c:1052
    static int parseStdLine(CPtr line, IRef year, IRef month, IRef day, IRef hour, IRef minute, FRef value)
    {
        int n = 0;
        CPtr token = R.buf(1024);
        n = R.sscanf(line, L350, token, year, month, day, hour, minute, value);
        if ((n < 7))
        {
            return 0;
        }
        if (((StationID != null) && (!(strcomp(token, StationID) != 0))))
        {
            return 0;
        }
        return 1;
    }

    // rain.c:1078
    static void saveAccumRainfall(double date1, int hour, int minute, long v)
    {
        DBox date2 = new DBox(0.0);
        int n = 0;
        int j = 0;
        FBox x = new FBox(0.0f);
        if ((AccumStartDate == (-693594)))
        {
            return;
        }
        date2.v = (date1 + datetime_encodeTime(hour, minute, 0));
        n = (int) ((((datetime_timeDiff(date2.v, AccumStartDate) / Interval)) + 1));
        if ((v == 99999))
        {
            RainStats.periodsMissing += n;
            return;
        }
        RainStats.periodsRain += n;
        x.v = ((((float) (v)) / ((float) (n))) / 100.0f);
        if ((x.v > 0.0f))
        {
            date2.v = datetime_addSeconds(AccumStartDate, (-TimeOffset));
            if ((RainStats.startDate == (-693594)))
            {
                RainStats.startDate = date2.v;
            }
            for (j = 0; (j < n); j++)
            {
                R.fwrite1(date2.v, 8, Frain.file);
                R.fwrite1(x.v, 4, Frain.file);
                date2.v = datetime_addSeconds(date2.v, Interval);
                RainStats.endDate = date2.v;
            }
        }
        AccumStartDate = (-693594);
    }

    // rain.c:1133
    static void saveRainfall(double date1, int hour, int minute, float __x, int isMissing)
    {
        FBox x = new FBox(__x);
        DBox date2 = new DBox(0.0);
        int seconds = 0;
        if ((isMissing != 0))
        {
            RainStats.periodsMissing++;
        }
        else
        {
            RainStats.periodsRain++;
        }
        if (!(isMissing != 0))
        {
            seconds = (((3600 * hour) + (60 * minute)) - TimeOffset);
            date2.v = datetime_addSeconds(date1, seconds);
            R.fwrite1(date2.v, 8, Frain.file);
            R.fwrite1(x.v, 4, Frain.file);
            if ((RainStats.startDate == (-693594)))
            {
                RainStats.startDate = date2.v;
            }
            RainStats.endDate = date2.v;
        }
    }

}
