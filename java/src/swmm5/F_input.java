// Generated from EPA SWMM 5.2.4 (input.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_input {
    static int MAXERRS = 100;
    static CPtr[] Tok = new CPtr[40];
    static int Ntokens = 0;
    static int[] Mobjects = new int[18];
    static int[] Mnodes = new int[5];
    static int[] Mlinks = new int[5];
    static int Mevents = 0;

    // input.c:69
    static int input_countObjects()
    {
        CPtr line = R.buf(1025);
        CPtr wLine = R.buf(1025);
        CPtr tok = null;
        int sect = (-1);
        int newsect = 0;
        int errcode = 0;
        int errsum = 0;
        int i = 0;
        long lineCount = 0;
        if ((ErrorCode != 0))
        {
            return ErrorCode;
        }
        error_setInpError(0, L0);
        for (i = 0; (i < MAX_OBJ_TYPES); i++)
        {
            Nobjects[i] = 0;
        }
        for (i = 0; (i < 5); i++)
        {
            Nnodes[i] = 0;
        }
        for (i = 0; (i < 5); i++)
        {
            Nlinks[i] = 0;
        }
        controls_init();
        while (((R.fgets(line, (int) (1024), Finp.file)) != null))
        {
            lineCount++;
            sstrncpy(wLine, line, 1024);
            tok = R.strtok(wLine, L184);
            if ((tok == null))
            {
                continue;
            }
            if ((tok.g(0) == 59))
            {
                continue;
            }
            if ((tok.g(0) == 91))
            {
                newsect = findmatch(tok, SectWords);
                if ((newsect >= 0))
                {
                    sect = newsect;
                    continue;
                }
                else
                {
                    sect = (-1);
                    errcode = ERR_KEYWORD;
                }
            }
            if ((sect == s_OPTION))
            {
                errcode = readOption(line);
            }
            else if ((sect >= 0))
            {
                errcode = addObject(sect, tok);
            }
            if ((errcode != 0))
            {
                report_writeInputErrorMsg(errcode, sect, line, lineCount);
                errsum++;
                if ((errsum >= MAXERRS))
                {
                    break;
                }
            }
        }
        if ((errsum > 0))
        {
            ErrorCode = ERR_INPUT;
        }
        return ErrorCode;
    }

    // input.c:141
    static int input_readData()
    {
        CPtr line = R.buf(1025);
        CPtr wLine = R.buf(1025);
        CPtr comment = null;
        int sect = 0;
        int newsect = 0;
        int inperr = 0;
        int errsum = 0;
        int lineLength = 0;
        int i = 0;
        long lineCount = 0;
        if ((ErrorCode != 0))
        {
            return ErrorCode;
        }
        error_setInpError(0, L0);
        for (i = 0; (i < MAX_OBJ_TYPES); i++)
        {
            Mobjects[i] = 0;
        }
        for (i = 0; (i < 5); i++)
        {
            Mnodes[i] = 0;
        }
        for (i = 0; (i < 5); i++)
        {
            Mlinks[i] = 0;
        }
        Mevents = 0;
        for (i = 0; (i < Nobjects[TSERIES]); i++)
        {
            Tseries[i].lastDate = (StartDate.v + StartTime.v);
        }
        sect = 0;
        errsum = 0;
        R.rewind(Finp.file);
        while (((R.fgets(line, (int) (1024), Finp.file)) != null))
        {
            lineCount++;
            sstrncpy(wLine, line, 1024);
            Ntokens = getTokens(wLine);
            if ((Ntokens == 0))
            {
                continue;
            }
            if ((Tok[0].g(0) == 59))
            {
                continue;
            }
            lineLength = (int) (R.strlen(line));
            if ((lineLength >= 1024))
            {
                comment = R.strchr(line, 59);
                if ((comment != null))
                {
                    lineLength = (int) ((((long) (comment.o - line.o))));
                }
                if ((lineLength >= 1024))
                {
                    inperr = ERR_LINE_LENGTH;
                    report_writeInputErrorMsg(inperr, sect, line, lineCount);
                    errsum++;
                }
            }
            if ((Tok[0].g(0) == 91))
            {
                newsect = findmatch(Tok[0], SectWords);
                if ((newsect >= 0))
                {
                    if ((sect == s_TRANSECT))
                    {
                        transect_validate((Nobjects[TRANSECT] - 1));
                    }
                    sect = newsect;
                    continue;
                }
                else
                {
                    inperr = error_setInpError(ERR_KEYWORD, Tok[0]);
                    report_writeInputErrorMsg(inperr, sect, line, lineCount);
                    errsum++;
                    break;
                }
            }
            else
            {
                inperr = parseLine(sect, line);
                if ((inperr > 0))
                {
                    errsum++;
                    if ((errsum > MAXERRS))
                    {
                        report_writeLine(L201);
                    }
                    else
                    {
                        report_writeInputErrorMsg(inperr, sect, line, lineCount);
                    }
                }
            }
            if ((errsum > MAXERRS))
            {
                break;
            }
        }
        if ((errsum > 0))
        {
            ErrorCode = ERR_INPUT;
        }
        return ErrorCode;
    }

    // input.c:251
    static int addObject(int objType, CPtr id)
    {
        int errcode = 0;
        switch (objType) {
            case s_RAINGAGE:
                if (!(project_addObject(GAGE, id, Nobjects[GAGE]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[GAGE]++;
                break;
            case s_SUBCATCH:
                if (!(project_addObject(SUBCATCH, id, Nobjects[SUBCATCH]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[SUBCATCH]++;
                break;
            case s_AQUIFER:
                if (!(project_addObject(AQUIFER, id, Nobjects[AQUIFER]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[AQUIFER]++;
                break;
            case s_UNITHYD:
                if ((project_findObject(UNITHYD, id) < 0))
                {
                    if (!(project_addObject(UNITHYD, id, Nobjects[UNITHYD]) != 0))
                    {
                        errcode = error_setInpError(ERR_DUP_NAME, id);
                    }
                    Nobjects[UNITHYD]++;
                }
                break;
            case s_SNOWMELT:
                if ((project_findObject(SNOWMELT, id) < 0))
                {
                    if (!(project_addObject(SNOWMELT, id, Nobjects[SNOWMELT]) != 0))
                    {
                        errcode = error_setInpError(ERR_DUP_NAME, id);
                    }
                    Nobjects[SNOWMELT]++;
                }
                break;
            case s_JUNCTION:
                if (!(project_addObject(NODE, id, Nobjects[NODE]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[NODE]++;
                Nnodes[JUNCTION]++;
                break;
            case s_OUTFALL:
                if (!(project_addObject(NODE, id, Nobjects[NODE]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[NODE]++;
                Nnodes[OUTFALL]++;
                break;
            case s_STORAGE:
                if (!(project_addObject(NODE, id, Nobjects[NODE]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[NODE]++;
                Nnodes[STORAGE]++;
                break;
            case s_DIVIDER:
                if (!(project_addObject(NODE, id, Nobjects[NODE]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[NODE]++;
                Nnodes[DIVIDER]++;
                break;
            case s_CONDUIT:
                if (!(project_addObject(LINK, id, Nobjects[LINK]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[LINK]++;
                Nlinks[CONDUIT]++;
                break;
            case s_PUMP:
                if (!(project_addObject(LINK, id, Nobjects[LINK]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[LINK]++;
                Nlinks[PUMP]++;
                break;
            case s_ORIFICE:
                if (!(project_addObject(LINK, id, Nobjects[LINK]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[LINK]++;
                Nlinks[ORIFICE]++;
                break;
            case s_WEIR:
                if (!(project_addObject(LINK, id, Nobjects[LINK]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[LINK]++;
                Nlinks[WEIR]++;
                break;
            case s_OUTLET:
                if (!(project_addObject(LINK, id, Nobjects[LINK]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[LINK]++;
                Nlinks[OUTLET]++;
                break;
            case s_POLLUTANT:
                if (!(project_addObject(POLLUT, id, Nobjects[POLLUT]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[POLLUT]++;
                break;
            case s_LANDUSE:
                if (!(project_addObject(LANDUSE, id, Nobjects[LANDUSE]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[LANDUSE]++;
                break;
            case s_PATTERN:
                if ((project_findObject(TIMEPATTERN, id) < 0))
                {
                    if (!(project_addObject(TIMEPATTERN, id, Nobjects[TIMEPATTERN]) != 0))
                    {
                        errcode = error_setInpError(ERR_DUP_NAME, id);
                    }
                    Nobjects[TIMEPATTERN]++;
                }
                break;
            case s_CURVE:
                if ((project_findObject(CURVE, id) < 0))
                {
                    if (!(project_addObject(CURVE, id, Nobjects[CURVE]) != 0))
                    {
                        errcode = error_setInpError(ERR_DUP_NAME, id);
                    }
                    Nobjects[CURVE]++;
                    id = R.strtok(null, L184);
                    if ((findmatch(id, CurveTypeWords) == SHAPE_CURVE))
                    {
                        Nobjects[SHAPE]++;
                    }
                }
                break;
            case s_TIMESERIES:
                if ((project_findObject(TSERIES, id) < 0))
                {
                    if (!(project_addObject(TSERIES, id, Nobjects[TSERIES]) != 0))
                    {
                        errcode = error_setInpError(ERR_DUP_NAME, id);
                    }
                    Nobjects[TSERIES]++;
                }
                break;
            case s_CONTROL:
                if ((match(id, L202) != 0))
                {
                    Nobjects[CONTROL]++;
                }
                else
                {
                    controls_addToCount(id);
                }
                break;
            case s_TRANSECT:
                if ((match(id, L203) != 0))
                {
                    id = R.strtok(null, L184);
                    if ((id != null))
                    {
                        if (!(project_addObject(TRANSECT, id, Nobjects[TRANSECT]) != 0))
                        {
                            errcode = error_setInpError(ERR_DUP_NAME, id);
                        }
                        Nobjects[TRANSECT]++;
                    }
                }
                break;
            case s_LID_CONTROL:
                if ((project_findObject(LID, id) < 0))
                {
                    if (!(project_addObject(LID, id, Nobjects[LID]) != 0))
                    {
                        errcode = error_setInpError(ERR_DUP_NAME, id);
                    }
                    Nobjects[LID]++;
                }
                break;
            case s_EVENT:
                NumEvents++;
                break;
            case s_STREET:
                if (!(project_addObject(STREET, id, Nobjects[STREET]) != 0))
                {
                    errcode = error_setInpError(ERR_DUP_NAME, id);
                }
                Nobjects[STREET]++;
                break;
            case s_INLET:
                if ((project_findObject(INLET, id) < 0))
                {
                    if (!(project_addObject(INLET, id, Nobjects[INLET]) != 0))
                    {
                        errcode = error_setInpError(ERR_DUP_NAME, id);
                    }
                    Nobjects[INLET]++;
                }
                break;
        }
        return errcode;
    }

    // input.c:464
    static int parseLine(int sect, CPtr line)
    {
        int j = 0;
        int err = 0;
        switch (sect) {
            case s_TITLE:
                return readTitle(line);
            case s_RAINGAGE:
                j = Mobjects[GAGE];
                err = gage_readParams(j, Tok, Ntokens);
                Mobjects[GAGE]++;
                return err;
            case s_TEMP:
                return climate_readParams(Tok, Ntokens);
            case s_EVAP:
                return climate_readEvapParams(Tok, Ntokens);
            case s_ADJUST:
                return climate_readAdjustments(Tok, Ntokens);
            case s_SUBCATCH:
                j = Mobjects[SUBCATCH];
                err = subcatch_readParams(j, Tok, Ntokens);
                Mobjects[SUBCATCH]++;
                return err;
            case s_SUBAREA:
                return subcatch_readSubareaParams(Tok, Ntokens);
            case s_INFIL:
                return infil_readParams(InfilModel, Tok, Ntokens);
            case s_AQUIFER:
                j = Mobjects[AQUIFER];
                err = gwater_readAquiferParams(j, Tok, Ntokens);
                Mobjects[AQUIFER]++;
                return err;
            case s_GROUNDWATER:
                return gwater_readGroundwaterParams(Tok, Ntokens);
            case s_GWF:
                return gwater_readFlowExpression(Tok, Ntokens);
            case s_SNOWMELT:
                return snow_readMeltParams(Tok, Ntokens);
            case s_JUNCTION:
                return readNode(JUNCTION);
            case s_OUTFALL:
                return readNode(OUTFALL);
            case s_STORAGE:
                return readNode(STORAGE);
            case s_DIVIDER:
                return readNode(DIVIDER);
            case s_CONDUIT:
                return readLink(CONDUIT);
            case s_PUMP:
                return readLink(PUMP);
            case s_ORIFICE:
                return readLink(ORIFICE);
            case s_WEIR:
                return readLink(WEIR);
            case s_OUTLET:
                return readLink(OUTLET);
            case s_XSECTION:
                return link_readXsectParams(Tok, Ntokens);
            case s_TRANSECT:
                return transect_readParams(new IElem(Mobjects, TRANSECT), Tok, Ntokens);
            case s_LOSSES:
                return link_readLossParams(Tok, Ntokens);
            case s_POLLUTANT:
                j = Mobjects[POLLUT];
                err = landuse_readPollutParams(j, Tok, Ntokens);
                Mobjects[POLLUT]++;
                return err;
            case s_LANDUSE:
                j = Mobjects[LANDUSE];
                err = landuse_readParams(j, Tok, Ntokens);
                Mobjects[LANDUSE]++;
                return err;
            case s_BUILDUP:
                return landuse_readBuildupParams(Tok, Ntokens);
            case s_WASHOFF:
                return landuse_readWashoffParams(Tok, Ntokens);
            case s_COVERAGE:
                return subcatch_readLanduseParams(Tok, Ntokens);
            case s_INFLOW:
                return inflow_readExtInflow(Tok, Ntokens);
            case s_DWF:
                return inflow_readDwfInflow(Tok, Ntokens);
            case s_PATTERN:
                return inflow_readDwfPattern(Tok, Ntokens);
            case s_RDII:
                return rdii_readRdiiInflow(Tok, Ntokens);
            case s_UNITHYD:
                return rdii_readUnitHydParams(Tok, Ntokens);
            case s_LOADING:
                return subcatch_readInitBuildup(Tok, Ntokens);
            case s_TREATMENT:
                return treatmnt_readExpression(Tok, Ntokens);
            case s_CURVE:
                return table_readCurve(Tok, Ntokens);
            case s_TIMESERIES:
                return table_readTimeseries(Tok, Ntokens);
            case s_CONTROL:
                return readControl(Tok, Ntokens);
            case s_REPORT:
                return report_readOptions(Tok, Ntokens);
            case s_FILE:
                return iface_readFileParams(Tok, Ntokens);
            case s_LID_CONTROL:
                return lid_readProcParams(Tok, Ntokens);
            case s_LID_USAGE:
                return lid_readGroupParams(Tok, Ntokens);
            case s_EVENT:
                return readEvent(Tok, Ntokens);
            case s_STREET:
                return street_readParams(Tok, Ntokens);
            case s_INLET:
                return inlet_readDesignParams(Tok, Ntokens);
            case s_INLET_USAGE:
                return inlet_readUsageParams(Tok, Ntokens);
            default:
                return 0;
        }
    }

    // input.c:637
    static int readControl(CPtr[] tok, int ntoks)
    {
        int index = 0;
        int keyword = 0;
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        if ((match(tok[0], L29) != 0))
        {
            return controls_addVariable(tok, ntoks);
        }
        if ((match(tok[0], L30) != 0))
        {
            return controls_addExpression(tok, ntoks);
        }
        keyword = findmatch(tok[0], RuleKeyWords);
        if ((keyword < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[0]);
        }
        if ((keyword == 0))
        {
            if (!(project_addObject(CONTROL, tok[1], Mobjects[CONTROL]) != 0))
            {
                return error_setInpError(ERR_DUP_NAME, Tok[1]);
            }
            Mobjects[CONTROL]++;
        }
        index = (Mobjects[CONTROL] - 1);
        if ((index < 0))
        {
            return error_setInpError(ERR_RULE, L0);
        }
        return controls_addRuleClause(index, keyword, Tok, Ntokens);
    }

    // input.c:680
    static int readOption(CPtr line)
    {
        Ntokens = getTokens(line);
        if ((Ntokens < 2))
        {
            return 0;
        }
        return project_readOption(Tok[0], Tok[1]);
    }

    // input.c:694
    static int readTitle(CPtr line)
    {
        int i = 0;
        int n = 0;
        for (i = 0; (i < 3); i++)
        {
            if ((R.strlen(Title[i]) == 0))
            {
                n = (int) (R.strlen(line));
                if ((line.g((n - 1)) == 10))
                {
                    line.s((n - 1), 32);
                }
                sstrncpy(Title[i], line, 1024);
                break;
            }
        }
        return 0;
    }

    // input.c:721
    static int readNode(int type)
    {
        int j = Mobjects[NODE];
        int k = Mnodes[type];
        int err = node_readParams(j, type, k, Tok, Ntokens);
        Mobjects[NODE]++;
        Mnodes[type]++;
        return err;
    }

    // input.c:738
    static int readLink(int type)
    {
        int j = Mobjects[LINK];
        int k = Mlinks[type];
        int err = link_readParams(j, type, k, Tok, Ntokens);
        Mobjects[LINK]++;
        Mlinks[type]++;
        return err;
    }

    // input.c:755
    static int readEvent(CPtr[] tok, int ntoks)
    {
        double[] x = new double[4];
        if ((ntoks < 4))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        if (!(datetime_strToDate(tok[0], new DElem(x, 0)) != 0))
        {
            return error_setInpError(ERR_DATETIME, tok[0]);
        }
        if (!(datetime_strToTime(tok[1], new DElem(x, 1)) != 0))
        {
            return error_setInpError(ERR_DATETIME, tok[1]);
        }
        if (!(datetime_strToDate(tok[2], new DElem(x, 2)) != 0))
        {
            return error_setInpError(ERR_DATETIME, tok[2]);
        }
        if (!(datetime_strToTime(tok[3], new DElem(x, 3)) != 0))
        {
            return error_setInpError(ERR_DATETIME, tok[3]);
        }
        Event[Mevents].start = (x[0] + x[1]);
        Event[Mevents].end = (x[2] + x[3]);
        if ((Event[Mevents].start >= Event[Mevents].end))
        {
            return error_setInpError(ERR_DATETIME, L204);
        }
        Mevents++;
        return 0;
    }

    // input.c:779
    static int findmatch(CPtr s, CPtr[] keyword)
    {
        int i = 0;
        while ((keyword[i] != null))
        {
            if ((match(s, keyword[i]) != 0))
            {
                return (i);
            }
            i++;
        }
        return ((-1));
    }

    // input.c:798
    static int match(CPtr str, CPtr substr)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        if (!(substr.g(0) != 0))
        {
            return (0);
        }
        for (k = 0; (str.g(k) != 0); k++)
        {
            if ((str.g(k) != 32))
            {
                break;
            }
        }
        for (i = k, j = 0; (substr.g(j) != 0); i++, j++)
        {
            if (((!(str.g(i) != 0)) || (((((((str.g(i)) >= 97) && ((str.g(i)) <= 122))) ? (((str.g(i)) & (~32))) : (str.g(i)))) != ((((((substr.g(j)) >= 97) && ((substr.g(j)) <= 122))) ? (((substr.g(j)) & (~32))) : (substr.g(j)))))))
            {
                return (0);
            }
        }
        return (1);
    }

    // input.c:828
    static int getInt(CPtr s, IRef y)
    {
        DBox x = new DBox(0.0);
        if ((getDouble(s, x) != 0))
        {
            if ((x.v < 0.0))
            {
                x.v -= 0.01;
            }
            else
            {
                x.v += 0.01;
            }
            y.set(R.D2I(x.v));
            return 1;
        }
        y.set(0);
        return 0;
    }

    // input.c:850
    static int getFloat(CPtr s, FRef y)
    {
        OBox<CPtr> endptr = new OBox<>(null);
        y.set(((float) (R.strtod(s, endptr))));
        if ((endptr.v.g(0) > 0))
        {
            return (0);
        }
        return (1);
    }

    // input.c:866
    static int getDouble(CPtr s, DRef y)
    {
        OBox<CPtr> endptr = new OBox<>(null);
        y.set(R.strtod(s, endptr));
        if ((endptr.v.g(0) > 0))
        {
            return (0);
        }
        return (1);
    }

    // input.c:882
    static int getTokens(CPtr s)
    {
        int len = 0;
        int n = 0;
        int m = 0;
        CPtr c = null;
        for (n = 0; (n < 40); n++)
        {
            Tok[n] = null;
        }
        n = 0;
        c = R.strchr(s, 59);
        if ((c != null))
        {
            c.s(0, 0);
        }
        len = (int) (R.strlen(s));
        while (((len > 0) && (n < 40)))
        {
            m = (int) (R.strcspn(s, L184));
            if ((m == 0))
            {
                s = s.add(1);
            }
            else
            {
                if ((s.g(0) == 34))
                {
                    s = s.add(1);
                    len--;
                    m = (int) (R.strcspn(s, L205));
                }
                s.s(m, 0);
                Tok[n] = s;
                n++;
                s = s.add((m + 1));
            }
            len -= (m + 1);
        }
        return n;
    }

}
