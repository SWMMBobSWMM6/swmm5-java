// Generated from EPA SWMM 5.2.4 (project.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_project {
    static HTentry[][] Htable = new HTentry[18][];
    static int MemPoolAllocated = 0;

    // project.c:70
    static int omp_get_max_threads()
    {
        return 1;
    }

    // project.c:113
    static void project_open(CPtr f1, CPtr f2, CPtr f3)
    {
        initPointers();
        setDefaults();
        openFiles(f1, f2, f3);
    }

    // project.c:129
    static void project_readInput()
    {
        createHashTables();
        input_countObjects();
        createObjects();
        input_readData();
        if ((ErrorCode != 0))
        {
            return;
        }
        StartDateTime = (StartDate.v + StartTime.v);
        EndDateTime = (EndDate.v + EndTime.v);
        ReportStart = (ReportStartDate.v + ReportStartTime.v);
        ReportStart = (((((ReportStart) >= (StartDateTime))) ? (ReportStart) : (StartDateTime)));
        if ((EndDateTime <= StartDateTime))
        {
            report_writeErrorMsg(ERR_START_DATE, L0);
        }
        else if ((EndDateTime <= ReportStart))
        {
            report_writeErrorMsg(ERR_REPORT_DATE, L0);
        }
        else
        {
            TotalDuration = Math.floor((((EndDateTime - StartDateTime)) * 86400));
            if ((((double) (ReportStep)) > TotalDuration))
            {
                ReportStep = R.D2I((TotalDuration));
            }
            if ((((double) (ReportStep)) < RouteStep))
            {
                report_writeErrorMsg(ERR_REPORT_STEP, L0);
            }
            TotalDuration *= 1000.0;
        }
    }

    // project.c:186
    static void project_validate()
    {
        int i = 0;
        int j = 0;
        int err = 0;
        for (i = 0; (i < Nobjects[CURVE]); i++)
        {
            err = table_validate(Curve[i]);
            if ((err != 0))
            {
                report_writeErrorMsg(ERR_CURVE_SEQUENCE, Curve[i].ID);
            }
        }
        for (i = 0; (i < Nobjects[TSERIES]); i++)
        {
            err = table_validate(Tseries[i]);
            if ((err != 0))
            {
                report_writeTseriesErrorMsg(err, Tseries[i]);
            }
        }
        climate_validate();
        lid_validate();
        if ((Nobjects[SNOWMELT] == 0))
        {
            IgnoreSnowmelt = 1;
        }
        if ((Nobjects[AQUIFER] == 0))
        {
            IgnoreGwater = 1;
        }
        for (i = 0; (i < Nobjects[AQUIFER]); i++)
        {
            gwater_validateAquifer(i);
        }
        for (i = 0; (i < Nobjects[SUBCATCH]); i++)
        {
            subcatch_validate(i);
        }
        for (i = 0; (i < Nobjects[GAGE]); i++)
        {
            gage_validate(i);
        }
        for (i = 0; (i < Nobjects[SNOWMELT]); i++)
        {
            snow_validateSnowmelt(i);
        }
        j = 0;
        for (i = 0; (i < Nobjects[CURVE]); i++)
        {
            if ((Curve[i].curveType == SHAPE_CURVE))
            {
                Curve[i].refersTo = j;
                Shape[j].curve = i;
                if (!(shape_validate(Shape[j], Curve[i]) != 0))
                {
                    report_writeErrorMsg(ERR_CURVE_SEQUENCE, Curve[i].ID);
                }
                j++;
            }
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            Node[i].oldDepth = Node[i].fullDepth;
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            link_validate(i);
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            node_validate(i);
        }
        if ((DryStep < WetStep))
        {
            report_writeWarningMsg(L319, L0);
            DryStep = WetStep;
        }
        if ((RouteStep > ((double) (WetStep))))
        {
            report_writeWarningMsg(L320, L0);
            RouteStep = WetStep;
        }
        if ((RptFlags.subcatchments == ALL))
        {
            for (i = 0; (i < Nobjects[SUBCATCH]); i++)
            {
                Subcatch[i].rptFlag = 1;
            }
        }
        if ((RptFlags.nodes == ALL))
        {
            for (i = 0; (i < Nobjects[NODE]); i++)
            {
                Node[i].rptFlag = 1;
            }
        }
        if ((RptFlags.links == ALL))
        {
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                Link[i].rptFlag = 1;
            }
        }
        if ((RouteModel == DW))
        {
            dynwave_validate();
        }
        inlet_validate();
        if ((NumThreads == 0))
        {
            NumThreads = omp_get_max_threads();
        }
        else
        {
            NumThreads = (((((NumThreads) <= (omp_get_max_threads()))) ? (NumThreads) : (omp_get_max_threads())));
        }
        if ((Nobjects[LINK] < (4 * NumThreads)))
        {
            NumThreads = 1;
        }
    }

    // project.c:274
    static void project_close()
    {
        deleteObjects();
        deleteHashTables();
    }

    // project.c:287
    static int project_init()
    {
        int j = 0;
        int k = 0;
        climate_initState();
        lid_initState();
        for (j = 0; (j < Nobjects[TSERIES]); j++)
        {
            table_tseriesInit(Tseries[j]);
        }
        for (j = 0; (j < Nobjects[GAGE]); j++)
        {
            gage_initState(j);
        }
        k = 1;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            subcatch_initState(j);
            if ((Subcatch[j].rptFlag > 0))
            {
                Subcatch[j].rptFlag = k;
                k++;
            }
        }
        k = 1;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            node_initState(j);
            if ((Node[j].rptFlag > 0))
            {
                Node[j].rptFlag = k;
                k++;
            }
        }
        k = 1;
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            link_initState(j);
            if ((Link[j].rptFlag > 0))
            {
                Link[j].rptFlag = k;
                k++;
            }
        }
        return ErrorCode;
    }

    // project.c:334
    static int project_addObject(int type, CPtr id, int n)
    {
        int result = 0;
        long len = 0L;
        CPtr newID = null;
        if ((project_findObject(type, id) >= 0))
        {
            return 0;
        }
        len = R.strlen(id);
        newID = Alloc((((len + 1)) * 1L));
        sstrncpy(newID, id, len);
        result = HTinsert(Htable[type], newID, n);
        if ((result == 0))
        {
            result = (-1);
        }
        return result;
    }

    // project.c:364
    static int project_findObject(int type, CPtr id)
    {
        return HTfind(Htable[type], id);
    }

    // project.c:377
    static CPtr project_findID(int type, CPtr id)
    {
        return HTfindKey(Htable[type], id);
    }

    // project.c:445
    static int project_readOption(CPtr s1, CPtr s2)
    {
        int k = 0;
        IBox m = new IBox(0);
        IBox h = new IBox(0);
        IBox s = new IBox(0);
        DBox tStep = new DBox(0.0);
        CPtr strDate = R.buf(25);
        DBox aTime = new DBox(0.0);
        DBox aDate = new DBox(0.0);
        k = findmatch(s1, OptionWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, s1);
        }
        switch (k) {
            case FLOW_UNITS:
                m.v = findmatch(s2, FlowUnitWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                FlowUnits = m.v;
                if ((FlowUnits <= MGD))
                {
                    UnitSystem = US;
                }
                else
                {
                    UnitSystem = SI;
                }
                break;
            case INFIL_MODEL:
                m.v = findmatch(s2, InfilModelWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                InfilModel = m.v;
                break;
            case ROUTE_MODEL:
                m.v = findmatch(s2, RouteModelWords);
                if ((m.v < 0))
                {
                    m.v = findmatch(s2, OldRouteModelWords);
                }
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                if ((m.v == NO_ROUTING))
                {
                    IgnoreRouting = 1;
                }
                else
                {
                    RouteModel = m.v;
                }
                if ((RouteModel == EKW))
                {
                    RouteModel = KW;
                }
                break;
            case START_DATE:
                if (!(datetime_strToDate(s2, StartDate) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                break;
            case START_TIME:
                if (!(datetime_strToTime(s2, StartTime) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                break;
            case END_DATE:
                if (!(datetime_strToDate(s2, EndDate) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                break;
            case END_TIME:
                if (!(datetime_strToTime(s2, EndTime) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                break;
            case REPORT_START_DATE:
                if (!(datetime_strToDate(s2, ReportStartDate) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                break;
            case REPORT_START_TIME:
                if (!(datetime_strToTime(s2, ReportStartTime) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                break;
            case SWEEP_START:
            case SWEEP_END:
                sstrncpy(strDate, s2, 24);
                sstrcat(strDate, L35, 25);
                if (!(datetime_strToDate(strDate, aDate) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                m.v = datetime_dayOfYear(aDate.v);
                if ((k == SWEEP_START))
                {
                    SweepStart = m.v;
                }
                else
                {
                    SweepEnd = m.v;
                }
                break;
            case START_DRY_DAYS:
                StartDryDays = R.atof(s2);
                if ((StartDryDays < 0.0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                break;
            case WET_STEP:
            case DRY_STEP:
            case REPORT_STEP:
            case RULE_STEP:
                if (!(datetime_strToTime(s2, aTime) != 0))
                {
                    return error_setInpError(ERR_DATETIME, s2);
                }
                datetime_decodeTime(aTime.v, h, m, s);
                h.v += (24 * R.D2I(aTime.v));
                s.v = ((s.v + (60 * m.v)) + (3600 * h.v));
                if ((k == RULE_STEP))
                {
                    if ((s.v < 0))
                    {
                        return error_setInpError(ERR_NUMBER, s2);
                    }
                }
                else if ((s.v <= 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                switch (k) {
                    case WET_STEP:
                        WetStep = s.v;
                        break;
                    case DRY_STEP:
                        DryStep = s.v;
                        break;
                    case REPORT_STEP:
                        ReportStep = s.v;
                        break;
                    case RULE_STEP:
                        RuleStep = s.v;
                        break;
                }
                break;
            case INERT_DAMPING:
                m.v = findmatch(s2, InertDampingWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                else
                {
                    InertDamping = m.v;
                }
                break;
            case ALLOW_PONDING:
            case SLOPE_WEIGHTING:
            case SKIP_STEADY_STATE:
            case IGNORE_RAINFALL:
            case IGNORE_SNOWMELT:
            case IGNORE_GWATER:
            case IGNORE_ROUTING:
            case IGNORE_QUALITY:
            case IGNORE_RDII:
                m.v = findmatch(s2, NoYesWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                switch (k) {
                    case ALLOW_PONDING:
                        AllowPonding = m.v;
                        break;
                    case SLOPE_WEIGHTING:
                        SlopeWeighting = m.v;
                        break;
                    case SKIP_STEADY_STATE:
                        SkipSteadyState = m.v;
                        break;
                    case IGNORE_RAINFALL:
                        IgnoreRainfall = m.v;
                        break;
                    case IGNORE_SNOWMELT:
                        IgnoreSnowmelt = m.v;
                        break;
                    case IGNORE_GWATER:
                        IgnoreGwater = m.v;
                        break;
                    case IGNORE_ROUTING:
                        IgnoreRouting = m.v;
                        break;
                    case IGNORE_QUALITY:
                        IgnoreQuality = m.v;
                        break;
                    case IGNORE_RDII:
                        IgnoreRDII = m.v;
                        break;
                }
                break;
            case NORMAL_FLOW_LTD:
                m.v = findmatch(s2, NormalFlowWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                NormalFlowLtd = m.v;
                break;
            case FORCE_MAIN_EQN:
                m.v = findmatch(s2, ForceMainEqnWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                ForceMainEqn = m.v;
                break;
            case LINK_OFFSETS:
                m.v = findmatch(s2, LinkOffsetWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                LinkOffsets = m.v;
                break;
            case COMPATIBILITY:
                if ((strcomp(s2, L321) != 0))
                {
                    Compatibility = SWMM3;
                }
                else if ((strcomp(s2, L322) != 0))
                {
                    Compatibility = SWMM4;
                }
                else if ((strcomp(s2, L323) != 0))
                {
                    Compatibility = SWMM5;
                }
                else
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                break;
            case ROUTE_STEP:
            case LENGTHENING_STEP:
                if (!(getDouble(s2, tStep) != 0))
                {
                    if (!(datetime_strToTime(s2, aTime) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, s2);
                    }
                    else
                    {
                        datetime_decodeTime(aTime.v, h, m, s);
                        h.v += (24 * R.D2I(aTime.v));
                        s.v = ((s.v + (60 * m.v)) + (3600 * h.v));
                        tStep.v = s.v;
                    }
                }
                if ((k == ROUTE_STEP))
                {
                    if ((tStep.v <= 0.0))
                    {
                        return error_setInpError(ERR_NUMBER, s2);
                    }
                    RouteStep = tStep.v;
                }
                else
                {
                    LengtheningStep = (((((0.0) >= (tStep.v))) ? (0.0) : (tStep.v)));
                }
                break;
            case MIN_ROUTE_STEP:
                if (((!(getDouble(s2, MinRouteStep) != 0)) || (MinRouteStep.v < 0.0)))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                break;
            case NUM_THREADS:
                m.v = R.atoi(s2);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                NumThreads = m.v;
                break;
            case VARIABLE_STEP:
                if (!(getDouble(s2, CourantFactor) != 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                if (((CourantFactor.v < 0.0) || (CourantFactor.v > 2.0)))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                break;
            case MIN_SURFAREA:
                if (!(getDouble(s2, MinSurfArea) != 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                if ((MinSurfArea.v < 0.0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                break;
            case MIN_SLOPE:
                if (!(getDouble(s2, MinSlope) != 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                if (((MinSlope.v < 0.0) || (MinSlope.v >= 100)))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                MinSlope.v /= 100.0;
                break;
            case MAX_TRIALS:
                m.v = R.atoi(s2);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                MaxTrials = m.v;
                break;
            case HEAD_TOL:
                if (!(getDouble(s2, HeadTol) != 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                break;
            case SYS_FLOW_TOL:
                if (!(getDouble(s2, SysFlowTol) != 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                SysFlowTol.v /= 100.0;
                break;
            case LAT_FLOW_TOL:
                if (!(getDouble(s2, LatFlowTol) != 0))
                {
                    return error_setInpError(ERR_NUMBER, s2);
                }
                LatFlowTol.v /= 100.0;
                break;
            case SURCHARGE_METHOD:
                m.v = findmatch(s2, SurchargeWords);
                if ((m.v < 0))
                {
                    return error_setInpError(ERR_KEYWORD, s2);
                }
                SurchargeMethod = m.v;
                break;
            case TEMPDIR:
                sstrncpy(TempDir, s2, 259);
                break;
        }
        return 0;
    }

    // project.c:773
    static void initPointers()
    {
        Gage = (null);
        Subcatch = (null);
        Node = (null);
        Outfall = (null);
        Divider = (null);
        Storage = (null);
        Link = (null);
        Conduit = (null);
        Pump = (null);
        Orifice = (null);
        Weir = (null);
        Outlet = (null);
        Pollut = (null);
        Landuse = (null);
        Pattern = (null);
        Curve = (null);
        Tseries = (null);
        Transect = (null);
        Shape = (null);
        Aquifer = (null);
        UnitHyd = (null);
        Snowmelt = (null);
        Event = (null);
        MemPoolAllocated = 0;
    }

    // project.c:808
    static void setDefaults()
    {
        int i = 0;
        int j = 0;
        for (i = 0; (i < 3); i++)
        {
            sstrncpy(Title[i], L0, 0);
        }
        sstrncpy(TempDir, L0, 0);
        Frain.mode = SCRATCH_FILE;
        Fclimate.mode = NO_FILE;
        Frunoff.mode = NO_FILE;
        Frdii.mode = NO_FILE;
        Fhotstart1.mode = NO_FILE;
        Fhotstart2.mode = NO_FILE;
        Finflows.mode = NO_FILE;
        Foutflows.mode = NO_FILE;
        Frain.file = (null);
        Fclimate.file = (null);
        Frunoff.file = (null);
        Frdii.file = (null);
        Fhotstart1.file = (null);
        Fhotstart2.file = (null);
        Finflows.file = (null);
        Foutflows.file = (null);
        Fout.file = (null);
        Fout.mode = NO_FILE;
        UnitSystem = US;
        FlowUnits = CFS;
        InfilModel = HORTON;
        RouteModel = DW;
        SurchargeMethod = EXTRAN;
        CrownCutoff = 0.96;
        AllowPonding = 0;
        InertDamping = PARTIAL_DAMPING;
        NormalFlowLtd = BOTH;
        ForceMainEqn = H_W;
        LinkOffsets = DEPTH_OFFSET;
        LengtheningStep = 0;
        CourantFactor.v = 0.75;
        MinSurfArea.v = 0.0;
        MinSlope.v = 0.0;
        SkipSteadyState = 0;
        IgnoreRainfall = 0;
        IgnoreRDII = 0;
        IgnoreSnowmelt = 0;
        IgnoreGwater = 0;
        IgnoreRouting = 0;
        IgnoreQuality = 0;
        WetStep = 300;
        DryStep = 3600;
        RuleStep = 0;
        RouteStep = 20;
        MinRouteStep.v = 0.5;
        ReportStep = 900;
        StartDryDays = 0.0;
        MaxTrials = 0;
        HeadTol.v = 0.0;
        SysFlowTol.v = 0.05;
        LatFlowTol.v = 0.05;
        NumThreads = 1;
        NumEvents = 0;
        SlopeWeighting = 1;
        Compatibility = SWMM4;
        StartDate.v = datetime_encodeDate(2004, 1, 1);
        StartTime.v = datetime_encodeTime(0, 0, 0);
        StartDateTime = (StartDate.v + StartTime.v);
        EndDate.v = StartDate.v;
        EndTime.v = 0.0;
        ReportStartDate.v = (-693594);
        ReportStartTime.v = (-693594);
        SweepStart = 1;
        SweepEnd = 365;
        RptFlags.disabled = 0;
        RptFlags.input = 0;
        RptFlags.continuity = 1;
        RptFlags.flowStats = 1;
        RptFlags.controls = 0;
        RptFlags.subcatchments = 0;
        RptFlags.nodes = 0;
        RptFlags.links = 0;
        RptFlags.averages = 0;
        Temp.dataSource = NO_TEMP;
        Temp.tSeries = (-1);
        Temp.ta = 70.0;
        Temp.elev = 0.0;
        Temp.anglat = 40.0;
        Temp.dtlong = 0.0;
        Temp.tmax = (-10000000000.0);
        Wind.type = MONTHLY_WIND;
        for (i = 0; (i < 12); i++)
        {
            Wind.aws[i] = 0.0;
        }
        Snow.snotmp = 34.0;
        Snow.tipm = 0.5;
        Snow.rnm = 0.6;
        for (i = 0; (i < 2); i++)
        {
            for (j = 0; (j < 10); j++)
            {
                Snow.adc[i][j] = 1.0;
            }
        }
        Evap.type = CONSTANT_EVAP;
        for (i = 0; (i < 12); i++)
        {
            Evap.monthlyEvap[i] = 0.0;
            Evap.panCoeff[i] = 1.0;
        }
        Evap.recoveryPattern = (-1);
        Evap.recoveryFactor = 1.0;
        Evap.tSeries = (-1);
        Evap.dryOnly = 0;
        for (i = 0; (i < 12); i++)
        {
            Adjust.temp[i] = 0.0;
            Adjust.evap[i] = 0.0;
            Adjust.rain[i] = 1.0;
            Adjust.hydcon[i] = 1.0;
        }
        Adjust.rainFactor = 1.0;
        Adjust.hydconFactor = 1.0;
    }

    // project.c:954
    static void openFiles(CPtr f1, CPtr f2, CPtr f3)
    {
        Finp.file = (null);
        Frpt.file = (null);
        Fout.file = (null);
        sstrncpy(Finp.name, f1, 259);
        sstrncpy(Frpt.name, f2, 259);
        sstrncpy(Fout.name, f3, 259);
        if ((((strcomp(f1, f2) != 0) || (strcomp(f1, f3) != 0)) || (strcomp(f2, f3) != 0)))
        {
            writecon(L324);
            ErrorCode = ERR_FILE_NAME;
            return;
        }
        if ((((Finp.file = R.fopen(f1, L13))) == null))
        {
            writecon(L325);
            writecon(f1);
            ErrorCode = ERR_INP_FILE;
            return;
        }
        if ((((Frpt.file = R.fopen(f2, L171))) == null))
        {
            writecon(L326);
            ErrorCode = ERR_RPT_FILE;
            return;
        }
    }

    // project.c:999
    static void createObjects()
    {
        int j = 0;
        int k = 0;
        if ((ErrorCode != 0))
        {
            return;
        }
        Gage = R.fill(new TGage[(int) (Nobjects[GAGE])], TGage::new);
        Subcatch = R.fill(new TSubcatch[(int) (Nobjects[SUBCATCH])], TSubcatch::new);
        Node = R.fill(new TNode[(int) (Nobjects[NODE])], TNode::new);
        Outfall = R.fill(new TOutfall[(int) (Nnodes[OUTFALL])], TOutfall::new);
        Divider = R.fill(new TDivider[(int) (Nnodes[DIVIDER])], TDivider::new);
        Storage = R.fill(new TStorage[(int) (Nnodes[STORAGE])], TStorage::new);
        Link = R.fill(new TLink[(int) (Nobjects[LINK])], TLink::new);
        Conduit = R.fill(new TConduit[(int) (Nlinks[CONDUIT])], TConduit::new);
        Pump = R.fill(new TPump[(int) (Nlinks[PUMP])], TPump::new);
        Orifice = R.fill(new TOrifice[(int) (Nlinks[ORIFICE])], TOrifice::new);
        Weir = R.fill(new TWeir[(int) (Nlinks[WEIR])], TWeir::new);
        Outlet = R.fill(new TOutlet[(int) (Nlinks[OUTLET])], TOutlet::new);
        Pollut = R.fill(new TPollut[(int) (Nobjects[POLLUT])], TPollut::new);
        Landuse = R.fill(new TLanduse[(int) (Nobjects[LANDUSE])], TLanduse::new);
        Pattern = R.fill(new TPattern[(int) (Nobjects[TIMEPATTERN])], TPattern::new);
        Curve = R.fill(new TTable[(int) (Nobjects[CURVE])], TTable::new);
        Tseries = R.fill(new TTable[(int) (Nobjects[TSERIES])], TTable::new);
        Aquifer = R.fill(new TAquifer[(int) (Nobjects[AQUIFER])], TAquifer::new);
        UnitHyd = R.fill(new TUnitHyd[(int) (Nobjects[UNITHYD])], TUnitHyd::new);
        Snowmelt = R.fill(new TSnowmelt[(int) (Nobjects[SNOWMELT])], TSnowmelt::new);
        Shape = R.fill(new TShape[(int) (Nobjects[SHAPE])], TShape::new);
        Event = R.fill(new TEvent[(int) ((NumEvents + 1))], TEvent::new);
        Event[NumEvents].start = 10000000000.0;
        Event[NumEvents].end = (10000000000.0 + 1.0);
        lid_create(Nobjects[LID], Nobjects[SUBCATCH]);
        ErrorCode = controls_create(Nobjects[CONTROL]);
        if ((ErrorCode != 0))
        {
            return;
        }
        ErrorCode = transect_create(Nobjects[TRANSECT]);
        if ((ErrorCode != 0))
        {
            return;
        }
        ErrorCode = street_create(Nobjects[STREET]);
        if ((ErrorCode != 0))
        {
            return;
        }
        ErrorCode = inlet_create(Nobjects[INLET]);
        if ((ErrorCode != 0))
        {
            return;
        }
        infil_create(Nobjects[SUBCATCH]);
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            Subcatch[j].initBuildup = new double[(int) (Nobjects[POLLUT])];
            Subcatch[j].oldQual = new double[(int) (Nobjects[POLLUT])];
            Subcatch[j].newQual = new double[(int) (Nobjects[POLLUT])];
            Subcatch[j].pondedQual = new double[(int) (Nobjects[POLLUT])];
            Subcatch[j].totalLoad = new double[(int) (Nobjects[POLLUT])];
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            Node[j].oldQual = new double[(int) (Nobjects[POLLUT])];
            Node[j].newQual = new double[(int) (Nobjects[POLLUT])];
            Node[j].extInflow = (null);
            Node[j].dwfInflow = (null);
            Node[j].rdiiInflow = (null);
            Node[j].treatment = (null);
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            Link[j].inlet = (null);
            Link[j].oldQual = new double[(int) (Nobjects[POLLUT])];
            Link[j].newQual = new double[(int) (Nobjects[POLLUT])];
            Link[j].totalLoad = new double[(int) (Nobjects[POLLUT])];
        }
        for (j = 0; (j < Nobjects[LANDUSE]); j++)
        {
            Landuse[j].buildupFunc = R.fill(new TBuildup[(int) (Nobjects[POLLUT])], TBuildup::new);
            Landuse[j].washoffFunc = R.fill(new TWashoff[(int) (Nobjects[POLLUT])], TWashoff::new);
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            Subcatch[j].landFactor = R.fill(new TLandFactor[(int) (Nobjects[LANDUSE])], TLandFactor::new);
            for (k = 0; (k < Nobjects[LANDUSE]); k++)
            {
                Subcatch[j].landFactor[k].buildup = new double[(int) (Nobjects[POLLUT])];
            }
        }
        for (j = 0; (j < Nobjects[LANDUSE]); j++)
        {
            for (k = 0; (k < Nobjects[POLLUT]); k++)
            {
                Landuse[j].buildupFunc[k].funcType = NO_BUILDUP;
                Landuse[j].buildupFunc[k].normalizer = PER_AREA;
                Landuse[j].washoffFunc[k].funcType = NO_WASHOFF;
            }
        }
        for (j = 0; (j < Nobjects[GAGE]); j++)
        {
            Gage[j].tSeries = (-1);
            sstrncpy(Gage[j].fname, L0, 0);
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            Subcatch[j].outSubcatch = (-1);
            Subcatch[j].outNode = (-1);
            Subcatch[j].infil = (-1);
            Subcatch[j].groundwater = (null);
            Subcatch[j].gwLatFlowExpr = (null);
            Subcatch[j].gwDeepFlowExpr = (null);
            Subcatch[j].snowpack = (null);
            Subcatch[j].lidArea = 0.0;
            for (k = 0; (k < Nobjects[POLLUT]); k++)
            {
                Subcatch[j].initBuildup[k] = 0.0;
            }
        }
        for (j = 0; (j < Nobjects[UNITHYD]); j++)
        {
            rdii_initUnitHyd(j);
        }
        for (j = 0; (j < Nobjects[SNOWMELT]); j++)
        {
            snow_initSnowmelt(j);
        }
        for (j = 0; (j < Nnodes[STORAGE]); j++)
        {
            Storage[j].exfil = (null);
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            Link[j].xsect.type = (-1);
            Link[j].cLossInlet = 0.0;
            Link[j].cLossOutlet = 0.0;
            Link[j].cLossAvg = 0.0;
            Link[j].hasFlapGate = 0;
        }
        for (j = 0; (j < Nlinks[PUMP]); j++)
        {
            Pump[j].pumpCurve = (-1);
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            Subcatch[j].rptFlag = 0;
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            Node[j].rptFlag = 0;
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            Link[j].rptFlag = 0;
        }
        for (j = 0; (j < Nobjects[CURVE]); j++)
        {
            table_init(Curve[j]);
        }
        for (j = 0; (j < Nobjects[TSERIES]); j++)
        {
            table_init(Tseries[j]);
        }
        for (j = 0; (j < Nobjects[TIMEPATTERN]); j++)
        {
            inflow_initDwfPattern(j);
        }
    }

    // project.c:1176
    static void deleteObjects()
    {
        int j = 0;
        int k = 0;
        if ((Subcatch != null))
        {
            for (j = 0; (j < Nobjects[SUBCATCH]); j++)
            {
                for (k = 0; (k < Nobjects[LANDUSE]); k++)
                {
                    {
                        if ((Subcatch[j].landFactor[k].buildup != null))
                        {
                            R.free(Subcatch[j].landFactor[k].buildup);
                            Subcatch[j].landFactor[k].buildup = (null);
                        }
                    }
                    ;
                }
                {
                    if ((Subcatch[j].landFactor != null))
                    {
                        R.free(Subcatch[j].landFactor);
                        Subcatch[j].landFactor = (null);
                    }
                }
                ;
                {
                    if ((Subcatch[j].groundwater != null))
                    {
                        R.free(Subcatch[j].groundwater);
                        Subcatch[j].groundwater = (null);
                    }
                }
                ;
                gwater_deleteFlowExpression(j);
                {
                    if ((Subcatch[j].snowpack != null))
                    {
                        R.free(Subcatch[j].snowpack);
                        Subcatch[j].snowpack = (null);
                    }
                }
                ;
            }
        }
        if ((Landuse != null))
        {
            for (j = 0; (j < Nobjects[LANDUSE]); j++)
            {
                {
                    if ((Landuse[j].buildupFunc != null))
                    {
                        R.free(Landuse[j].buildupFunc);
                        Landuse[j].buildupFunc = (null);
                    }
                }
                ;
                {
                    if ((Landuse[j].washoffFunc != null))
                    {
                        R.free(Landuse[j].washoffFunc);
                        Landuse[j].washoffFunc = (null);
                    }
                }
            }
        }
        if ((Subcatch != null))
        {
            for (j = 0; (j < Nobjects[SUBCATCH]); j++)
            {
                {
                    if ((Subcatch[j].initBuildup != null))
                    {
                        R.free(Subcatch[j].initBuildup);
                        Subcatch[j].initBuildup = (null);
                    }
                }
                ;
                {
                    if ((Subcatch[j].oldQual != null))
                    {
                        R.free(Subcatch[j].oldQual);
                        Subcatch[j].oldQual = (null);
                    }
                }
                ;
                {
                    if ((Subcatch[j].newQual != null))
                    {
                        R.free(Subcatch[j].newQual);
                        Subcatch[j].newQual = (null);
                    }
                }
                ;
                {
                    if ((Subcatch[j].pondedQual != null))
                    {
                        R.free(Subcatch[j].pondedQual);
                        Subcatch[j].pondedQual = (null);
                    }
                }
                ;
                {
                    if ((Subcatch[j].totalLoad != null))
                    {
                        R.free(Subcatch[j].totalLoad);
                        Subcatch[j].totalLoad = (null);
                    }
                }
                ;
            }
        }
        if ((Node != null))
        {
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                {
                    if ((Node[j].oldQual != null))
                    {
                        R.free(Node[j].oldQual);
                        Node[j].oldQual = (null);
                    }
                }
                ;
                {
                    if ((Node[j].newQual != null))
                    {
                        R.free(Node[j].newQual);
                        Node[j].newQual = (null);
                    }
                }
                ;
            }
        }
        if ((Link != null))
        {
            for (j = 0; (j < Nobjects[LINK]); j++)
            {
                {
                    if ((Link[j].oldQual != null))
                    {
                        R.free(Link[j].oldQual);
                        Link[j].oldQual = (null);
                    }
                }
                ;
                {
                    if ((Link[j].newQual != null))
                    {
                        R.free(Link[j].newQual);
                        Link[j].newQual = (null);
                    }
                }
                ;
                {
                    if ((Link[j].totalLoad != null))
                    {
                        R.free(Link[j].totalLoad);
                        Link[j].totalLoad = (null);
                    }
                }
                ;
            }
        }
        infil_delete();
        if ((Node != null))
        {
            for (j = 0; (j < Nnodes[STORAGE]); j++)
            {
                if ((Storage[j].exfil != null))
                {
                    {
                        if ((Storage[j].exfil.btmExfil != null))
                        {
                            R.free(Storage[j].exfil.btmExfil);
                            Storage[j].exfil.btmExfil = (null);
                        }
                    }
                    ;
                    {
                        if ((Storage[j].exfil.bankExfil != null))
                        {
                            R.free(Storage[j].exfil.bankExfil);
                            Storage[j].exfil.bankExfil = (null);
                        }
                    }
                    ;
                    {
                        if ((Storage[j].exfil != null))
                        {
                            R.free(Storage[j].exfil);
                            Storage[j].exfil = (null);
                        }
                    }
                    ;
                }
            }
        }
        if ((Node != null))
        {
            for (j = 0; (j < Nnodes[OUTFALL]); j++)
            {
                if ((Outfall[j].wRouted != null))
                {
                    R.free(Outfall[j].wRouted);
                    Outfall[j].wRouted = (null);
                }
            }
        }
        ;
        if ((Node != null))
        {
            for (j = 0; (j < Nobjects[NODE]); j++)
            {
                inflow_deleteExtInflows(j);
                inflow_deleteDwfInflows(j);
                rdii_deleteRdiiInflow(j);
                treatmnt_delete(j);
            }
        }
        if ((Tseries != null))
        {
            for (j = 0; (j < Nobjects[TSERIES]); j++)
            {
                table_deleteEntries(Tseries[j]);
            }
        }
        if ((Curve != null))
        {
            for (j = 0; (j < Nobjects[CURVE]); j++)
            {
                table_deleteEntries(Curve[j]);
            }
        }
        transect_delete();
        street_delete();
        inlet_delete();
        controls_delete();
        lid_delete();
        {
            if ((Gage != null))
            {
                R.free(Gage);
                Gage = (null);
            }
        }
        ;
        {
            if ((Subcatch != null))
            {
                R.free(Subcatch);
                Subcatch = (null);
            }
        }
        ;
        {
            if ((Node != null))
            {
                R.free(Node);
                Node = (null);
            }
        }
        ;
        {
            if ((Outfall != null))
            {
                R.free(Outfall);
                Outfall = (null);
            }
        }
        ;
        {
            if ((Divider != null))
            {
                R.free(Divider);
                Divider = (null);
            }
        }
        ;
        {
            if ((Storage != null))
            {
                R.free(Storage);
                Storage = (null);
            }
        }
        ;
        {
            if ((Link != null))
            {
                R.free(Link);
                Link = (null);
            }
        }
        ;
        {
            if ((Conduit != null))
            {
                R.free(Conduit);
                Conduit = (null);
            }
        }
        ;
        {
            if ((Pump != null))
            {
                R.free(Pump);
                Pump = (null);
            }
        }
        ;
        {
            if ((Orifice != null))
            {
                R.free(Orifice);
                Orifice = (null);
            }
        }
        ;
        {
            if ((Weir != null))
            {
                R.free(Weir);
                Weir = (null);
            }
        }
        ;
        {
            if ((Outlet != null))
            {
                R.free(Outlet);
                Outlet = (null);
            }
        }
        ;
        {
            if ((Pollut != null))
            {
                R.free(Pollut);
                Pollut = (null);
            }
        }
        ;
        {
            if ((Landuse != null))
            {
                R.free(Landuse);
                Landuse = (null);
            }
        }
        ;
        {
            if ((Pattern != null))
            {
                R.free(Pattern);
                Pattern = (null);
            }
        }
        ;
        {
            if ((Curve != null))
            {
                R.free(Curve);
                Curve = (null);
            }
        }
        ;
        {
            if ((Tseries != null))
            {
                R.free(Tseries);
                Tseries = (null);
            }
        }
        ;
        {
            if ((Aquifer != null))
            {
                R.free(Aquifer);
                Aquifer = (null);
            }
        }
        ;
        {
            if ((UnitHyd != null))
            {
                R.free(UnitHyd);
                UnitHyd = (null);
            }
        }
        ;
        {
            if ((Snowmelt != null))
            {
                R.free(Snowmelt);
                Snowmelt = (null);
            }
        }
        ;
        {
            if ((Shape != null))
            {
                R.free(Shape);
                Shape = (null);
            }
        }
        ;
        {
            if ((Event != null))
            {
                R.free(Event);
                Event = (null);
            }
        }
        ;
    }

    // project.c:1304
    static void createHashTables()
    {
        int j = 0;
        MemPoolAllocated = 0;
        for (j = 0; (j < MAX_OBJ_TYPES); j++)
        {
            Htable[j] = HTcreate();
            if ((Htable[j] == null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
            }
        }
        if (((AllocInit()) == null))
        {
            report_writeErrorMsg(ERR_MEMORY, L0);
        }
        else
        {
            MemPoolAllocated = 1;
        }
    }

    // project.c:1325
    static void deleteHashTables()
    {
        int j = 0;
        for (j = 0; (j < MAX_OBJ_TYPES); j++)
        {
            if ((Htable[j] != null))
            {
                HTfree(Htable[j]);
            }
        }
        if ((MemPoolAllocated != 0))
        {
            AllocFreePool();
        }
    }

}
