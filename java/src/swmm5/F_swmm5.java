// Generated from EPA SWMM 5.2.4 (swmm5.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_swmm5 {
    static TFile Finp = new TFile();
    static TFile Fout = new TFile();
    static TFile Frpt = new TFile();
    static TFile Fclimate = new TFile();
    static TFile Frain = new TFile();
    static TFile Frunoff = new TFile();
    static TFile Frdii = new TFile();
    static TFile Fhotstart1 = new TFile();
    static TFile Fhotstart2 = new TFile();
    static TFile Finflows = new TFile();
    static TFile Foutflows = new TFile();
    static long Nperiods = 0L;
    static long TotalStepCount = 0L;
    static long ReportStepCount = 0L;
    static long NonConvergeCount = 0L;
    static CPtr Msg = R.buf(1025);
    static CPtr ErrorMsg = R.buf(1025);
    static CPtr[] Title = R.bufs(3, 1025);
    static CPtr TempDir = R.buf(260);
    static CPtr InpDir = R.buf(260);
    static TRptFlags RptFlags = new TRptFlags();
    static int[] Nobjects = new int[18];
    static int[] Nnodes = new int[5];
    static int[] Nlinks = new int[5];
    static int UnitSystem = 0;
    static int FlowUnits = 0;
    static int InfilModel = 0;
    static int RouteModel = 0;
    static int ForceMainEqn = 0;
    static int LinkOffsets = 0;
    static int SurchargeMethod = 0;
    static int AllowPonding = 0;
    static int InertDamping = 0;
    static int NormalFlowLtd = 0;
    static int SlopeWeighting = 0;
    static int Compatibility = 0;
    static int SkipSteadyState = 0;
    static int IgnoreRainfall = 0;
    static int IgnoreRDII = 0;
    static int IgnoreSnowmelt = 0;
    static int IgnoreGwater = 0;
    static int IgnoreRouting = 0;
    static int IgnoreQuality = 0;
    static int ErrorCode = 0;
    static int Warnings = 0;
    static int WetStep = 0;
    static int DryStep = 0;
    static int ReportStep = 0;
    static int RuleStep = 0;
    static int SweepStart = 0;
    static int SweepEnd = 0;
    static int MaxTrials = 0;
    static int NumThreads = 0;
    static int NumEvents = 0;
    static double RouteStep = 0.0;
    static DBox MinRouteStep = new DBox(0.0);
    static double LengtheningStep = 0.0;
    static double StartDryDays = 0.0;
    static DBox CourantFactor = new DBox(0.0);
    static DBox MinSurfArea = new DBox(0.0);
    static DBox MinSlope = new DBox(0.0);
    static double RunoffError = 0.0;
    static double GwaterError = 0.0;
    static double FlowError = 0.0;
    static double QualError = 0.0;
    static DBox HeadTol = new DBox(0.0);
    static DBox SysFlowTol = new DBox(0.0);
    static DBox LatFlowTol = new DBox(0.0);
    static double CrownCutoff = 0.0;
    static DBox StartDate = new DBox(0.0);
    static DBox StartTime = new DBox(0.0);
    static double StartDateTime = 0.0;
    static DBox EndDate = new DBox(0.0);
    static DBox EndTime = new DBox(0.0);
    static double EndDateTime = 0.0;
    static DBox ReportStartDate = new DBox(0.0);
    static DBox ReportStartTime = new DBox(0.0);
    static double ReportStart = 0.0;
    static double ReportTime = 0.0;
    static double OldRunoffTime = 0.0;
    static double NewRunoffTime = 0.0;
    static double OldRoutingTime = 0.0;
    static double NewRoutingTime = 0.0;
    static double TotalDuration = 0.0;
    static double ElapsedTime = 0.0;
    static TTemp Temp = new TTemp();
    static TEvap Evap = new TEvap();
    static TWind Wind = new TWind();
    static TSnow Snow = new TSnow();
    static TAdjust Adjust = new TAdjust();
    static TSnowmelt[] Snowmelt = null;
    static TGage[] Gage = null;
    static TSubcatch[] Subcatch = null;
    static TAquifer[] Aquifer = null;
    static TUnitHyd[] UnitHyd = null;
    static TNode[] Node = null;
    static TOutfall[] Outfall = null;
    static TDivider[] Divider = null;
    static TStorage[] Storage = null;
    static TLink[] Link = null;
    static TConduit[] Conduit = null;
    static TPump[] Pump = null;
    static TOrifice[] Orifice = null;
    static TWeir[] Weir = null;
    static TOutlet[] Outlet = null;
    static TPollut[] Pollut = null;
    static TLanduse[] Landuse = null;
    static TPattern[] Pattern = null;
    static TTable[] Curve = null;
    static TTable[] Tseries = null;
    static TTransect[] Transect = null;
    static TStreet[] Street = null;
    static TShape[] Shape = null;
    static TEvent[] Event = null;
    static double[][] Ucf = new double[][] { new double[] { 43200.0, 1097280.0 }, new double[] { 12.0, 304.8 }, new double[] { 1036800.0, 26334720.0 }, new double[] { 1.0, 0.3048 }, new double[] { 2.2956e-5, 0.92903e-5 }, new double[] { 1.0, 0.02832 }, new double[] { 1.0, 1.608 }, new double[] { 1.0, 1.8 }, new double[] { 2.203e-6, 1.0e-6 }, new double[] { 43560.0, 3048.0 } };
    static double[] Qcf = new double[] { 1.0, 448.831, 0.64632, 0.02832, 28.317, 2.4466 };
    static int IsOpenFlag = 0;
    static int IsStartedFlag = 0;
    static int SaveResultsFlag = 0;
    static int ExceptionCount = 0;
    static int DoRunoff = 0;
    static int DoRouting = 0;
    static double RoutingDuration = 0.0;

    // swmm5.c:186
    static int swmm_run(CPtr f1, CPtr f2, CPtr f3)
    {
        long newHour = 0L;
        long oldHour = 0;
        long theDay = 0L;
        long theHour = 0L;
        DBox elapsedTime = new DBox(0.0);
        IsOpenFlag = 0;
        IsStartedFlag = 0;
        SaveResultsFlag = 1;
        ErrorCode = 0;
        writecon(L642);
        swmm_open(f1, f2, f3);
        if (!(ErrorCode != 0))
        {
            swmm_start(1);
            if (!(ErrorCode != 0))
            {
                writecon(L643);
                do
                {
                    swmm_step(elapsedTime);
                    newHour = R.D2L(((elapsedTime.v * 24.0)));
                    if ((newHour > oldHour))
                    {
                        theDay = R.D2L(elapsedTime.v);
                        theHour = R.D2L(((((elapsedTime.v - Math.floor(elapsedTime.v))) * 24.0)));
                        writecon(L644);
                        R.snprintf(Msg, (int) (1024), L645, theDay, theHour);
                        writecon(Msg);
                        oldHour = newHour;
                    }
                }
                while (((elapsedTime.v > 0.0) && (!(ErrorCode != 0))));
                writecon(L646);
                writecon(L647);
            }
            swmm_end();
        }
        if (((!(ErrorCode != 0)) && (Fout.mode == SCRATCH_FILE)))
        {
            writecon(L648);
            swmm_report();
        }
        swmm_close();
        return ErrorCode;
    }

    // swmm5.c:256
    static int swmm_open(CPtr f1, CPtr f2, CPtr f3)
    {
        {
            datetime_setDateFormat(1);
            ErrorCode = 0;
            ErrorMsg.s(0, 0);
            Warnings = 0;
            IsOpenFlag = 0;
            IsStartedFlag = 0;
            ExceptionCount = 0;
            R.strcpy(InpDir, L0);
            project_open(f1, f2, f3);
            getAbsolutePath(f1, InpDir, 260L);
            if ((ErrorCode != 0))
            {
                return ErrorCode;
            }
            IsOpenFlag = 1;
            report_writeLogo();
            project_readInput();
            if ((ErrorCode != 0))
            {
                return ErrorCode;
            }
            report_writeTitle();
            project_validate();
        }
        return ErrorCode;
    }

    // swmm5.c:314
    static int swmm_start(int saveResults)
    {
        if ((ErrorCode != 0))
        {
            return ErrorCode;
        }
        if (!(IsOpenFlag != 0))
        {
            return ((ErrorCode = ERR_API_NOT_OPEN));
        }
        if ((IsStartedFlag != 0))
        {
            return ((ErrorCode = ERR_API_NOT_ENDED));
        }
        if (!(RptFlags.disabled != 0))
        {
            if ((RptFlags.input != 0))
            {
                inputrpt_writeInput();
            }
            report_writeOptions();
        }
        SaveResultsFlag = saveResults;
        ExceptionCount = 0;
        {
            ElapsedTime = 0.0;
            RoutingDuration = TotalDuration;
            NewRunoffTime = 0.0;
            NewRoutingTime = 0.0;
            ReportTime = (1000 * ((double) (ReportStep)));
            TotalStepCount = 0;
            ReportStepCount = 0;
            NonConvergeCount = 0;
            IsStartedFlag = 1;
            RunoffError = 0.0;
            GwaterError = 0.0;
            FlowError = 0.0;
            QualError = 0.0;
            if (!(IgnoreRainfall != 0))
            {
                rain_open();
            }
            if ((ErrorCode != 0))
            {
                return ErrorCode;
            }
            project_init();
            if ((Nobjects[SUBCATCH] > 0))
            {
                DoRunoff = 1;
            }
            else
            {
                DoRunoff = 0;
            }
            if (((Nobjects[NODE] > 0) && (!(IgnoreRouting != 0))))
            {
                DoRouting = 1;
            }
            else
            {
                DoRouting = 0;
            }
            output_open();
            if ((DoRunoff != 0))
            {
                runoff_open();
            }
            if (!(hotstart_open() != 0))
            {
                return ErrorCode;
            }
            if ((DoRouting != 0))
            {
                routing_open();
            }
            massbal_open();
            stats_open();
            if (((!(RptFlags.disabled != 0)) && (RptFlags.controls != 0)))
            {
                report_writeControlActionsHeading();
            }
        }
        return ErrorCode;
    }

    // swmm5.c:410
    static int swmm_step(DRef elapsedTime)
    {
        elapsedTime.set(0.0);
        if ((ErrorCode != 0))
        {
            return ErrorCode;
        }
        if (!(IsOpenFlag != 0))
        {
            return ((ErrorCode = ERR_API_NOT_OPEN));
        }
        if (!(IsStartedFlag != 0))
        {
            return ((ErrorCode = ERR_API_NOT_STARTED));
        }
        {
            if ((NewRoutingTime < RoutingDuration))
            {
                execRouting();
            }
            if ((SaveResultsFlag != 0))
            {
                saveResults();
            }
            if ((NewRoutingTime < RoutingDuration))
            {
                ElapsedTime = (NewRoutingTime / 86400000.0);
            }
            else
            {
                ElapsedTime = 0.0;
            }
            elapsedTime.set(ElapsedTime);
        }
        return ErrorCode;
    }

    // swmm5.c:466
    static int swmm_stride(int strideStep, DRef elapsedTime)
    {
        double realRouteStep = RouteStep;
        elapsedTime.set(0.0);
        if ((ErrorCode != 0))
        {
            return ErrorCode;
        }
        if (!(IsOpenFlag != 0))
        {
            return ((ErrorCode = ERR_API_NOT_OPEN));
        }
        if (!(IsStartedFlag != 0))
        {
            return ((ErrorCode = ERR_API_NOT_STARTED));
        }
        RoutingDuration = (NewRoutingTime + (1000.0 * strideStep));
        RoutingDuration = (((((TotalDuration) <= (RoutingDuration))) ? (TotalDuration) : (RoutingDuration)));
        if ((strideStep < RouteStep))
        {
            RouteStep = strideStep;
        }
        do
        {
            swmm_step(elapsedTime);
        }
        while (((elapsedTime.get() > 0.0) && (!(ErrorCode != 0))));
        RouteStep = realRouteStep;
        RoutingDuration = TotalDuration;
        if ((NewRoutingTime < TotalDuration))
        {
            ElapsedTime = (NewRoutingTime / 86400000.0);
        }
        else
        {
            ElapsedTime = 0.0;
        }
        elapsedTime.set(ElapsedTime);
        return ErrorCode;
    }

    // swmm5.c:514
    static void execRouting()
    {
        double nextRoutingTime = 0.0;
        double routingStep = 0.0;
        {
            TotalStepCount++;
            if (!(DoRouting != 0))
            {
                routingStep = (((((WetStep) <= (ReportStep))) ? (WetStep) : (ReportStep)));
            }
            else
            {
                routingStep = routing_getRoutingStep(RouteModel, RouteStep);
            }
            if ((routingStep <= 0.0))
            {
                ErrorCode = ERR_TIMESTEP;
                return;
            }
            nextRoutingTime = (NewRoutingTime + (1000.0 * routingStep));
            if ((nextRoutingTime > RoutingDuration))
            {
                routingStep = (((RoutingDuration - NewRoutingTime)) / 1000.0);
                routingStep = (((((routingStep) >= ((1.0 / 1000.0)))) ? (routingStep) : ((1.0 / 1000.0))));
                nextRoutingTime = RoutingDuration;
            }
            if ((DoRunoff != 0))
            {
                while ((NewRunoffTime < nextRoutingTime))
                {
                    runoff_execute();
                    if ((ErrorCode != 0))
                    {
                        return;
                    }
                }
            }
            else
            {
                climate_setState(getDateTime(NewRoutingTime));
            }
            if ((DoRouting != 0))
            {
                routing_execute(RouteModel, routingStep);
            }
            else
            {
                NewRoutingTime = nextRoutingTime;
            }
        }
    }

    // swmm5.c:579
    static void saveResults()
    {
        if ((NewRoutingTime >= ReportTime))
        {
            if ((RptFlags.averages != 0))
            {
                if ((NewRoutingTime == ReportTime))
                {
                    output_updateAvgResults();
                }
                output_saveResults(ReportTime);
                if ((NewRoutingTime > ReportTime))
                {
                    output_updateAvgResults();
                }
            }
            else
            {
                output_saveResults(ReportTime);
            }
            ReportTime = (ReportTime + (1000 * ((double) (ReportStep))));
        }
        else if ((RptFlags.averages != 0))
        {
            output_updateAvgResults();
        }
    }

    // swmm5.c:618
    static int swmm_end()
    {
        if (!(IsOpenFlag != 0))
        {
            return ((ErrorCode = ERR_API_NOT_OPEN));
        }
        if ((IsStartedFlag != 0))
        {
            if ((Fout.file != null))
            {
                output_end();
            }
            if (((!(ErrorCode != 0)) && (RptFlags.disabled == 0)))
            {
                massbal_report();
                stats_report();
            }
            stats_close();
            massbal_close();
            if (!(IgnoreRainfall != 0))
            {
                rain_close();
            }
            if ((DoRunoff != 0))
            {
                runoff_close();
            }
            if ((DoRouting != 0))
            {
                routing_close(RouteModel);
            }
            hotstart_close();
            IsStartedFlag = 0;
        }
        return ErrorCode;
    }

    // swmm5.c:655
    static int swmm_report()
    {
        if (!(ErrorCode != 0))
        {
            report_writeReport();
        }
        return ErrorCode;
    }

    // swmm5.c:669
    static void swmm_writeLine(CPtr line)
    {
        if ((IsOpenFlag != 0))
        {
            report_writeLine(line);
        }
    }

    // swmm5.c:682
    static int swmm_close()
    {
        if ((Fout.file != null))
        {
            output_close();
        }
        if ((IsOpenFlag != 0))
        {
            project_close();
        }
        report_writeSysTime();
        if ((Finp.file != null))
        {
            R.fclose(Finp.file);
        }
        if ((Frpt.file != null))
        {
            R.fclose(Frpt.file);
        }
        if ((Fout.file != null))
        {
            R.fclose(Fout.file);
            if ((Fout.mode == SCRATCH_FILE))
            {
                R.remove(Fout.name);
            }
        }
        IsOpenFlag = 0;
        IsStartedFlag = 0;
        return 0;
    }

    // swmm5.c:708
    static int swmm_getMassBalErr(FRef runoffErr, FRef flowErr, FRef qualErr)
    {
        runoffErr.set((float) (0.0));
        flowErr.set((float) (0.0));
        qualErr.set((float) (0.0));
        if (((IsOpenFlag != 0) && (!(IsStartedFlag != 0))))
        {
            runoffErr.set(((float) (RunoffError)));
            flowErr.set(((float) (FlowError)));
            qualErr.set(((float) (QualError)));
        }
        return 0;
    }

    // swmm5.c:734
    static int swmm_getVersion()
    {
        return 52004;
    }

    // swmm5.c:749
    static int swmm_getWarnings()
    {
        return Warnings;
    }

    // swmm5.c:760
    static int swmm_getError(CPtr errMsg, int msgLen)
    {
        if (((ErrorCode > 0) && (R.strlen(ErrorMsg) == 0)))
        {
            error_getMsg(ErrorCode, ErrorMsg);
        }
        sstrncpy(errMsg, ErrorMsg, msgLen);
        if (((msgLen > 0) && (errMsg.g(0) == 10)))
        {
            errMsg.s(0, 32);
        }
        return ErrorCode;
    }

    // swmm5.c:780
    static int swmm_getCount(int objType)
    {
        if (!(IsOpenFlag != 0))
        {
            return 0;
        }
        if (((objType < swmm_GAGE) || (objType > swmm_LINK)))
        {
            return 0;
        }
        return Nobjects[objType];
    }

    // swmm5.c:795
    static void swmm_getName(int objType, int index, CPtr name, int size)
    {
        CPtr idName = null;
        name.s(0, 0);
        if (!(IsOpenFlag != 0))
        {
            return;
        }
        if (((objType < swmm_GAGE) || (objType > swmm_LINK)))
        {
            return;
        }
        if (((index < 0) || (index >= Nobjects[objType])))
        {
            return;
        }
        switch (objType) {
            case GAGE:
                idName = Gage[index].ID;
                break;
            case SUBCATCH:
                idName = Subcatch[index].ID;
                break;
            case NODE:
                idName = Node[index].ID;
                break;
            case LINK:
                idName = Link[index].ID;
                break;
        }
        if ((idName != null))
        {
            sstrncpy(name, idName, size);
        }
    }

    // swmm5.c:826
    static int swmm_getIndex(int objType, CPtr name)
    {
        if (!(IsOpenFlag != 0))
        {
            return (-1);
        }
        if (((objType < swmm_GAGE) || (objType > swmm_LINK)))
        {
            return (-1);
        }
        return project_findObject(objType, name);
    }

    // swmm5.c:842
    static double swmm_getValue(int property, int index)
    {
        if (!(IsOpenFlag != 0))
        {
            return 0;
        }
        if ((property < 100))
        {
            return getSystemValue(property);
        }
        if ((property < 200))
        {
            return getGageValue(property, index);
        }
        if ((property < 300))
        {
            return getSubcatchValue(property, index);
        }
        if ((property < 400))
        {
            return getNodeValue(property, index);
        }
        if ((property < 500))
        {
            return getLinkValue(property, index);
        }
        return 0;
    }

    // swmm5.c:867
    static void swmm_setValue(int property, int index, double value)
    {
        if (!(IsOpenFlag != 0))
        {
            return;
        }
        switch (property) {
            case swmm_GAGE_RAINFALL:
                if (((index < 0) || (index >= Nobjects[GAGE])))
                {
                    return;
                }
                if ((value >= 0.0))
                {
                    Gage[index].apiRainfall = value;
                }
                return;
            case swmm_SUBCATCH_RPTFLAG:
                if ((((!(IsStartedFlag != 0)) && (index >= 0)) && (index < Nobjects[SUBCATCH])))
                {
                    Subcatch[index].rptFlag = (((value > 0.0)) ? 1 : 0);
                }
                return;
            case swmm_NODE_LATFLOW:
                setNodeLatFlow(index, value);
                return;
            case swmm_NODE_HEAD:
                setOutfallStage(index, value);
                return;
            case swmm_NODE_RPTFLAG:
                if ((((!(IsStartedFlag != 0)) && (index >= 0)) && (index < Nobjects[NODE])))
                {
                    Node[index].rptFlag = (((value > 0.0)) ? 1 : 0);
                }
                return;
            case swmm_LINK_SETTING:
                setLinkSetting(index, value);
                return;
            case swmm_LINK_RPTFLAG:
                if ((((!(IsStartedFlag != 0)) && (index >= 0)) && (index < Nobjects[LINK])))
                {
                    Link[index].rptFlag = (((value > 0.0)) ? 1 : 0);
                }
                return;
            case swmm_ROUTESTEP:
                setRoutingStep(value);
                return;
            case swmm_REPORTSTEP:
                if (((!(IsStartedFlag != 0)) && (value > 0)))
                {
                    ReportStep = R.D2I(value);
                }
                return;
            case swmm_NOREPORT:
                if (!(IsStartedFlag != 0))
                {
                    RptFlags.disabled = (((value > 0.0)) ? 1 : 0);
                }
                return;
        }
    }

    // swmm5.c:922
    static double swmm_getSavedValue(int property, int index, int period)
    {
        if (!(IsOpenFlag != 0))
        {
            return 0;
        }
        if ((IsStartedFlag != 0))
        {
            return 0;
        }
        if (((period < 1) || (period > Nperiods)))
        {
            return 0;
        }
        if ((property == swmm_CURRENTDATE))
        {
            return getSavedDate(period);
        }
        if (((property >= 200) && (property < 300)))
        {
            return getSavedSubcatchValue(property, index, period);
        }
        if ((property < 400))
        {
            return getSavedNodeValue(property, index, period);
        }
        if ((property < 500))
        {
            return getSavedLinkValue(property, index, period);
        }
        return 0;
    }

    // swmm5.c:949
    static void swmm_decodeDate(double date, IRef year, IRef month, IRef day, IRef hour, IRef minute, IRef second, IRef dayOfWeek)
    {
        datetime_decodeDate(date, year, month, day);
        datetime_decodeTime(date, hour, minute, second);
        dayOfWeek.set(datetime_dayOfWeek(date));
    }

    // swmm5.c:966
    static double getGageValue(int property, int index)
    {
        if (((index < 0) || (index >= Nobjects[GAGE])))
        {
            return 0;
        }
        if ((property == swmm_GAGE_RAINFALL))
        {
            return Gage[index].reportRainfall;
        }
        return 0;
    }

    // swmm5.c:982
    static double getSubcatchValue(int property, int index)
    {
        TSubcatch subcatch = null;
        if (((index < 0) || (index >= Nobjects[SUBCATCH])))
        {
            return 0;
        }
        subcatch = Subcatch[index];
        switch (property) {
            case swmm_SUBCATCH_AREA:
                return (subcatch.area * UCF(LANDAREA));
            case swmm_SUBCATCH_RAINGAGE:
                return subcatch.gage;
            case swmm_SUBCATCH_RAINFALL:
                if ((subcatch.gage >= 0))
                {
                    return Gage[subcatch.gage].reportRainfall;
                }
                else
                {
                    return 0.0;
                }
            case swmm_SUBCATCH_EVAP:
                return (subcatch.evapLoss * UCF(EVAPRATE));
            case swmm_SUBCATCH_INFIL:
                return (subcatch.infilLoss * UCF(RAINFALL));
            case swmm_SUBCATCH_RUNOFF:
                return (subcatch.newRunoff * UCF(FLOW));
            case swmm_SUBCATCH_RPTFLAG:
                return (((subcatch.rptFlag > 0)) ? 1 : 0);
            default:
                return 0;
        }
    }

    // swmm5.c:1019
    static double getNodeValue(int property, int index)
    {
        TNode node = null;
        if (((index < 0) || (index >= Nobjects[NODE])))
        {
            return 0;
        }
        node = Node[index];
        switch (property) {
            case swmm_NODE_TYPE:
                return node.type;
            case swmm_NODE_ELEV:
                return (node.invertElev * UCF(LENGTH));
            case swmm_NODE_MAXDEPTH:
                return (node.fullDepth * UCF(LENGTH));
            case swmm_NODE_DEPTH:
                return (node.newDepth * UCF(LENGTH));
            case swmm_NODE_HEAD:
                return (((node.newDepth + node.invertElev)) * UCF(LENGTH));
            case swmm_NODE_VOLUME:
                return (node.newVolume * UCF(VOLUME));
            case swmm_NODE_LATFLOW:
                return (node.newLatFlow * UCF(FLOW));
            case swmm_NODE_INFLOW:
                return (node.inflow * UCF(FLOW));
            case swmm_NODE_OVERFLOW:
                return (node.overflow * UCF(FLOW));
            case swmm_NODE_RPTFLAG:
                return (((node.rptFlag > 0)) ? 1 : 0);
            default:
                return 0;
        }
    }

    // swmm5.c:1059
    static double getLinkValue(int property, int index)
    {
        TLink link = null;
        if (((index < 0) || (index >= Nobjects[LINK])))
        {
            return 0;
        }
        link = Link[index];
        switch (property) {
            case swmm_LINK_TYPE:
                return link.type;
            case swmm_LINK_NODE1:
                return link.node1;
            case swmm_LINK_NODE2:
                return link.node2;
            case swmm_LINK_LENGTH:
                if ((link.type == CONDUIT))
                {
                    return (Conduit[link.subIndex].length * UCF(LENGTH));
                }
                else
                {
                    return 0;
                }
            case swmm_LINK_SLOPE:
                if ((link.type == CONDUIT))
                {
                    return Conduit[link.subIndex].slope;
                }
                else
                {
                    return 0;
                }
            case swmm_LINK_FULLDEPTH:
                return (link.xsect.yFull * UCF(LENGTH));
            case swmm_LINK_FULLFLOW:
                return (link.qFull * UCF(FLOW));
            case swmm_LINK_FLOW:
                return ((link.newFlow * UCF(FLOW)) * ((double) (link.direction)));
            case swmm_LINK_VELOCITY:
                return (link_getVelocity(index, Math.abs(link.newFlow), link.newDepth) * UCF(LENGTH));
            case swmm_LINK_DEPTH:
                return (link.newDepth * UCF(LENGTH));
            case swmm_LINK_TOPWIDTH:
                if ((link.type == CONDUIT))
                {
                    return (xsect_getWofY(link.xsect, link.newDepth) * UCF(LENGTH));
                }
                else
                {
                    return 0;
                }
            case swmm_LINK_SETTING:
                return link.setting;
            case swmm_LINK_TIMEOPEN:
                if ((link.setting > 0.0))
                {
                    return (((getDateTime(NewRoutingTime) - link.timeLastSet)) * 24.0);
                }
                else
                {
                    return 0;
                }
            case swmm_LINK_TIMECLOSED:
                if ((link.setting == 0.0))
                {
                    return (((getDateTime(NewRoutingTime) - link.timeLastSet)) * 24.0);
                }
                else
                {
                    return 0;
                }
            case swmm_LINK_RPTFLAG:
                return (((link.rptFlag > 0)) ? 1 : 0);
            default:
                return 0;
        }
    }

    // swmm5.c:1126
    static double getSystemValue(int property)
    {
        switch (property) {
            case swmm_STARTDATE:
                return StartDateTime;
            case swmm_CURRENTDATE:
                return (StartDateTime + ElapsedTime);
            case swmm_ELAPSEDTIME:
                return ElapsedTime;
            case swmm_ROUTESTEP:
                return RouteStep;
            case swmm_MAXROUTESTEP:
                return getMaxRouteStep();
            case swmm_REPORTSTEP:
                return ReportStep;
            case swmm_TOTALSTEPS:
                return Nperiods;
            case swmm_NOREPORT:
                return RptFlags.disabled;
            case swmm_FLOWUNITS:
                return FlowUnits;
            default:
                return 0;
        }
    }

    // swmm5.c:1159
    static void setNodeLatFlow(int index, double value)
    {
        if (((index < 0) || (index >= Nobjects[NODE])))
        {
            return;
        }
        Node[index].apiExtInflow = (value / UCF(FLOW));
    }

    // swmm5.c:1173
    static void setOutfallStage(int index, double value)
    {
        TNode node = null;
        if (((index < 0) || (index >= Nobjects[NODE])))
        {
            return;
        }
        node = Node[index];
        if ((node.type != OUTFALL))
        {
            return;
        }
        Outfall[node.subIndex].fixedStage = (value / UCF(LENGTH));
        Outfall[node.subIndex].type = FIXED_OUTFALL;
    }

    // swmm5.c:1192
    static void setLinkSetting(int index, double value)
    {
        TLink link = null;
        if (((index < 0) || (index >= Nobjects[LINK])))
        {
            return;
        }
        link = Link[index];
        if (((value < 0.0) || (link.type == CONDUIT)))
        {
            return;
        }
        if (((link.type != PUMP) && (value > 1.0)))
        {
            value = 1.0;
        }
        if ((link.targetSetting == value))
        {
            return;
        }
        link.targetSetting = value;
        if (((link.targetSetting * link.setting) == 0.0))
        {
            link.timeLastSet = (StartDateTime + ElapsedTime);
        }
        link_setSetting(index, 0.0);
    }

    // swmm5.c:1217
    static double getSavedDate(int period)
    {
        DBox days = new DBox(0.0);
        output_readDateTime(period, days);
        return days.v;
    }

    // swmm5.c:1230
    static double getSavedSubcatchValue(int property, int index, int period)
    {
        int outIndex = (Subcatch[index].rptFlag - 1);
        if ((outIndex < 0))
        {
            return 0;
        }
        output_readSubcatchResults(period, outIndex);
        switch (property) {
            case swmm_SUBCATCH_RAINFALL:
                return SubcatchResults[SUBCATCH_RAINFALL];
            case swmm_SUBCATCH_EVAP:
                return SubcatchResults[SUBCATCH_EVAP];
            case swmm_SUBCATCH_INFIL:
                return SubcatchResults[SUBCATCH_INFIL];
            case swmm_SUBCATCH_RUNOFF:
                return SubcatchResults[SUBCATCH_RUNOFF];
            default:
                return 0;
        }
    }

    // swmm5.c:1265
    static double getSavedNodeValue(int property, int index, int period)
    {
        int outIndex = (Node[index].rptFlag - 1);
        if ((outIndex < 0))
        {
            return 0;
        }
        output_readNodeResults(period, outIndex);
        switch (property) {
            case swmm_NODE_DEPTH:
                return NodeResults[NODE_DEPTH];
            case swmm_NODE_HEAD:
                return NodeResults[NODE_HEAD];
            case swmm_NODE_VOLUME:
                return NodeResults[NODE_VOLUME];
            case swmm_NODE_LATFLOW:
                return NodeResults[NODE_LATFLOW];
            case swmm_NODE_INFLOW:
                return NodeResults[NODE_INFLOW];
            case swmm_NODE_OVERFLOW:
                return NodeResults[NODE_OVERFLOW];
            default:
                return 0;
        }
    }

    // swmm5.c:1304
    static double getSavedLinkValue(int property, int index, int period)
    {
        double y = 0.0;
        double w = 0.0;
        int outIndex = (Link[index].rptFlag - 1);
        if ((outIndex < 0))
        {
            return 0;
        }
        output_readLinkResults(period, outIndex);
        switch (property) {
            case swmm_LINK_FLOW:
                return LinkResults[LINK_FLOW];
            case swmm_LINK_DEPTH:
                return LinkResults[LINK_DEPTH];
            case swmm_LINK_VELOCITY:
                return LinkResults[LINK_VELOCITY];
            case swmm_LINK_TOPWIDTH:
                y = (LinkResults[LINK_DEPTH] / UCF(LENGTH));
                w = xsect_getWofY(Link[index].xsect, y);
                return (w * UCF(LENGTH));
            case swmm_LINK_SETTING:
                return LinkResults[LINK_CAPACITY];
            default:
                return 0;
        }
    }

    // swmm5.c:1345
    static double getMaxRouteStep()
    {
        double tmpCourantFactor = CourantFactor.v;
        double result = RouteStep;
        if (((!(IsStartedFlag != 0)) || (RouteModel != DW)))
        {
            return result;
        }
        CourantFactor.v = 1.0;
        result = routing_getRoutingStep(RouteModel, MinRouteStep.v);
        CourantFactor.v = tmpCourantFactor;
        return result;
    }

    // swmm5.c:1360
    static void setRoutingStep(double value)
    {
        if ((value <= 0.0))
        {
            return;
        }
        if ((value <= MinRouteStep.v))
        {
            value = MinRouteStep.v;
        }
        CourantFactor.v = 0.0;
        RouteStep = value;
    }

    // swmm5.c:1378
    static double UCF(int u)
    {
        if ((u < FLOW))
        {
            return Ucf[u][UnitSystem];
        }
        else
        {
            return Qcf[FlowUnits];
        }
    }

    // swmm5.c:1453
    static int strcomp(CPtr s1, CPtr s2)
    {
        int i = 0;
        for (i = 0; (((((((s1.g(i)) >= 97) && ((s1.g(i)) <= 122))) ? (((s1.g(i)) & (~32))) : (s1.g(i)))) == ((((((s2.g(i)) >= 97) && ((s2.g(i)) <= 122))) ? (((s2.g(i)) & (~32))) : (s2.g(i))))); i++)
        {
            if (((!(s1.g((i + 1)) != 0)) && (!(s2.g((i + 1)) != 0))))
            {
                return (1);
            }
        }
        return (0);
    }

    // swmm5.c:1518
    static void getElapsedTime(double aDate, IRef days, IRef hrs, IRef mins)
    {
        double x = 0.0;
        IBox secs = new IBox(0);
        x = (aDate - ReportStart);
        if ((x <= 0.0))
        {
            days.set(0);
            hrs.set(0);
            mins.set(0);
        }
        else
        {
            days.set(R.D2I(x));
            datetime_decodeTime(x, hrs, mins, secs);
        }
    }

    // swmm5.c:1543
    static double getDateTime(double elapsedMsec)
    {
        return datetime_addSeconds(StartDateTime, (((elapsedMsec + 1)) / 1000.0));
    }

    // swmm5.c:1556
    static int isRelativePath(CPtr fname)
    {
        if ((R.strchr(fname, 58) != null))
        {
            return 0;
        }
        if ((fname.g(0) == 92))
        {
            return 0;
        }
        if ((fname.g(0) == 47))
        {
            return 0;
        }
        return 1;
    }

    // swmm5.c:1617
    static CPtr addAbsolutePath(CPtr fname)
    {
        long n = 0L;
        CPtr buffer = R.buf(259);
        if ((isRelativePath(fname) != 0))
        {
            n = R.snprintf(buffer, (int) (259), L649, InpDir, fname);
            if ((n > 0))
            {
                sstrncpy(fname, buffer, 259);
            }
        }
        return fname;
    }

    // swmm5.c:1638
    static void writecon(CPtr s)
    {
        R.fprintf(R.stdout, L169, s);
        R.fflush(R.stdout);
    }

}
