// Generated from EPA SWMM 5.2.4 (output.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_output {
    static LBox IDStartPos = new LBox(0L);
    static LBox InputStartPos = new LBox(0L);
    static LBox OutputStartPos = new LBox(0L);
    static long BytesPerPeriod = 0L;
    static int NumSubcatchVars = 0;
    static int NumNodeVars = 0;
    static int NumLinkVars = 0;
    static int NumSubcatch = 0;
    static int NumNodes = 0;
    static int NumLinks = 0;
    static int NumPolluts = 0;
    static float[] SysResults = new float[15];
    static TAvgResults[] AvgLinkResults = null;
    static TAvgResults[] AvgNodeResults = null;
    static int output_Nsteps = 0;
    static float[] SubcatchResults = null;
    static float[] NodeResults = null;
    static float[] LinkResults = null;

    // output.c:121
    static int output_open()
    {
        int j = 0;
        int m = 0;
        IBox k = new IBox(0);
        float x = 0.0f;
        DBox z = new DBox(0.0);
        long numResults = 0L;
        output_openOutFile();
        if ((ErrorCode != 0))
        {
            return ErrorCode;
        }
        if ((IgnoreQuality != 0))
        {
            NumPolluts = 0;
        }
        else
        {
            NumPolluts = Nobjects[POLLUT];
        }
        NumSubcatchVars = ((9 - 1) + NumPolluts);
        NumNodeVars = ((7 - 1) + NumPolluts);
        NumLinkVars = ((6 - 1) + NumPolluts);
        NumSubcatch = 0;
        NumNodes = 0;
        NumLinks = 0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if ((Subcatch[j].rptFlag != 0))
            {
                NumSubcatch++;
            }
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if ((Node[j].rptFlag != 0))
            {
                NumNodes++;
            }
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if ((Link[j].rptFlag != 0))
            {
                NumLinks++;
            }
        }
        numResults = (((((NumSubcatch * NumSubcatchVars)) + ((NumNodes * NumNodeVars))) + ((NumLinks * NumLinkVars))) + 15);
        BytesPerPeriod = (8L + ((numResults * 4L)));
        Nperiods = 0;
        SubcatchResults = (null);
        NodeResults = (null);
        LinkResults = (null);
        SubcatchResults = new float[(int) (NumSubcatchVars)];
        NodeResults = new float[(int) (NumNodeVars)];
        LinkResults = new float[(int) (NumLinkVars)];
        if ((((!(SubcatchResults != null)) || (!(NodeResults != null))) || (!(LinkResults != null))))
        {
            report_writeErrorMsg(ERR_MEMORY, L0);
            return ErrorCode;
        }
        AvgNodeResults = (null);
        AvgLinkResults = (null);
        if (((RptFlags.averages != 0) && (!(output_openAvgResults() != 0))))
        {
            report_writeErrorMsg(ERR_MEMORY, L0);
            return ErrorCode;
        }
        R.fseek(Fout.file, 0, 0);
        k.v = 516114522;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = 52004;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = FlowUnits;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NumSubcatch;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NumNodes;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NumLinks;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NumPolluts;
        R.fwrite1(k.v, 4, Fout.file);
        IDStartPos.v = R.ftell(Fout.file);
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if ((Subcatch[j].rptFlag != 0))
            {
                output_saveID(Subcatch[j].ID, Fout.file);
            }
        }
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if ((Node[j].rptFlag != 0))
            {
                output_saveID(Node[j].ID, Fout.file);
            }
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if ((Link[j].rptFlag != 0))
            {
                output_saveID(Link[j].ID, Fout.file);
            }
        }
        for (j = 0; (j < NumPolluts); j++)
        {
            output_saveID(Pollut[j].ID, Fout.file);
        }
        for (j = 0; (j < NumPolluts); j++)
        {
            k.v = Pollut[j].units;
            R.fwrite1(k.v, 4, Fout.file);
        }
        InputStartPos.v = R.ftell(Fout.file);
        k.v = 1;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_AREA;
        R.fwrite1(k.v, 4, Fout.file);
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if (!(Subcatch[j].rptFlag != 0))
            {
                continue;
            }
            SubcatchResults[0] = ((float) (((Subcatch[j].area * UCF(LANDAREA)))));
            R.fwrite(SubcatchResults, 0, (int) (1), Fout.file);
        }
        k.v = 3;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_TYPE_CODE;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_INVERT;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_MAX_DEPTH;
        R.fwrite1(k.v, 4, Fout.file);
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if (!(Node[j].rptFlag != 0))
            {
                continue;
            }
            k.v = Node[j].type;
            NodeResults[0] = ((float) (((Node[j].invertElev * UCF(LENGTH)))));
            NodeResults[1] = ((float) (((Node[j].fullDepth * UCF(LENGTH)))));
            R.fwrite1(k.v, 4, Fout.file);
            R.fwrite(NodeResults, 0, (int) (2), Fout.file);
        }
        k.v = 5;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_TYPE_CODE;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_OFFSET;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_OFFSET;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_MAX_DEPTH;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = INPUT_LENGTH;
        R.fwrite1(k.v, 4, Fout.file);
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if (!(Link[j].rptFlag != 0))
            {
                continue;
            }
            k.v = Link[j].type;
            if ((k.v == PUMP))
            {
                for (m = 0; (m < 4); m++)
                {
                    LinkResults[m] = 0.0f;
                }
            }
            else
            {
                LinkResults[0] = ((float) (((Link[j].offset1 * UCF(LENGTH)))));
                LinkResults[1] = ((float) (((Link[j].offset2 * UCF(LENGTH)))));
                if ((Link[j].direction < 0))
                {
                    x = LinkResults[0];
                    LinkResults[0] = LinkResults[1];
                    LinkResults[1] = x;
                }
                if ((k.v == OUTLET))
                {
                    LinkResults[2] = 0.0f;
                }
                else
                {
                    LinkResults[2] = ((float) (((Link[j].xsect.yFull * UCF(LENGTH)))));
                }
                if ((k.v == CONDUIT))
                {
                    m = Link[j].subIndex;
                    LinkResults[3] = ((float) (((Conduit[m].length * UCF(LENGTH)))));
                }
                else
                {
                    LinkResults[3] = 0.0f;
                }
            }
            R.fwrite1(k.v, 4, Fout.file);
            R.fwrite(LinkResults, 0, (int) (4), Fout.file);
        }
        k.v = NumSubcatchVars;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_RAINFALL;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_SNOWDEPTH;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_EVAP;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_INFIL;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_RUNOFF;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_GW_FLOW;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_GW_ELEV;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = SUBCATCH_SOIL_MOIST;
        R.fwrite1(k.v, 4, Fout.file);
        for (j = 0; (j < NumPolluts); j++)
        {
            k.v = (SUBCATCH_WASHOFF + j);
            R.fwrite1(k.v, 4, Fout.file);
        }
        k.v = NumNodeVars;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NODE_DEPTH;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NODE_HEAD;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NODE_VOLUME;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NODE_LATFLOW;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NODE_INFLOW;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = NODE_OVERFLOW;
        R.fwrite1(k.v, 4, Fout.file);
        for (j = 0; (j < NumPolluts); j++)
        {
            k.v = (NODE_QUAL + j);
            R.fwrite1(k.v, 4, Fout.file);
        }
        k.v = NumLinkVars;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = LINK_FLOW;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = LINK_DEPTH;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = LINK_VELOCITY;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = LINK_VOLUME;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = LINK_CAPACITY;
        R.fwrite1(k.v, 4, Fout.file);
        for (j = 0; (j < NumPolluts); j++)
        {
            k.v = (LINK_QUAL + j);
            R.fwrite1(k.v, 4, Fout.file);
        }
        k.v = 15;
        R.fwrite1(k.v, 4, Fout.file);
        for (k.v = 0; (k.v < 15); k.v++)
        {
            R.fwrite1(k.v, 4, Fout.file);
        }
        z.v = (((double) (ReportStep)) / 86400.0);
        if (((StartDateTime + z.v) > ReportStart))
        {
            z.v = StartDateTime;
        }
        else
        {
            z.v = (Math.floor((((ReportStart - StartDateTime)) / z.v)) - 1.0);
            z.v = (StartDateTime + ((z.v * ((double) (ReportStep))) / 86400.0));
        }
        R.fwrite1(z.v, 8, Fout.file);
        k.v = ReportStep;
        if ((R.fwrite1(k.v, 4, Fout.file) < 1))
        {
            report_writeErrorMsg(ERR_OUT_WRITE, L0);
            return ErrorCode;
        }
        OutputStartPos.v = R.ftell(Fout.file);
        return ErrorCode;
    }

    // output.c:427
    static void output_openOutFile()
    {
        if ((Fout.file != null))
        {
            R.fclose(Fout.file);
        }
        else if ((R.strlen(Fout.name) != 0))
        {
            Fout.mode = SAVE_FILE;
        }
        else
        {
            Fout.mode = SCRATCH_FILE;
            getTempFileName(Fout.name);
        }
        if ((((Fout.file = R.fopen(Fout.name, L166))) == null))
        {
            writecon(L318);
            ErrorCode = ERR_OUT_FILE;
        }
    }

    // output.c:457
    static void output_saveResults(double reportTime)
    {
        int i = 0;
        double reportDate = getDateTime(reportTime);
        DBox date = new DBox(0.0);
        if ((reportDate < ReportStart))
        {
            return;
        }
        for (i = 0; (i < 15); i++)
        {
            SysResults[i] = 0.0f;
        }
        date.v = reportDate;
        R.fwrite1(date.v, 8, Fout.file);
        if ((Nobjects[SUBCATCH] > 0))
        {
            output_saveSubcatchResults(reportTime, Fout.file);
        }
        if ((RptFlags.averages != 0))
        {
            output_saveAvgResults(Fout.file);
        }
        else
        {
            if ((Nobjects[NODE] > 0))
            {
                output_saveNodeResults(reportTime, Fout.file);
            }
            if ((Nobjects[LINK] > 0))
            {
                output_saveLinkResults(reportTime, Fout.file);
            }
        }
        SysResults[SYS_FLOODING] = ((float) (((StepFlowTotals.flooding * UCF(FLOW)))));
        SysResults[SYS_OUTFLOW] = ((float) (((StepFlowTotals.outflow * UCF(FLOW)))));
        SysResults[SYS_DWFLOW] = ((float) (((StepFlowTotals.dwInflow * UCF(FLOW)))));
        SysResults[SYS_GWFLOW] = ((float) (((StepFlowTotals.gwInflow * UCF(FLOW)))));
        SysResults[SYS_IIFLOW] = ((float) (((StepFlowTotals.iiInflow * UCF(FLOW)))));
        SysResults[SYS_EXFLOW] = ((float) (((StepFlowTotals.exInflow * UCF(FLOW)))));
        SysResults[SYS_INFLOW] = ((((SysResults[SYS_RUNOFF] + SysResults[SYS_DWFLOW]) + SysResults[SYS_GWFLOW]) + SysResults[SYS_IIFLOW]) + SysResults[SYS_EXFLOW]);
        R.fwrite(SysResults, 0, (int) (15), Fout.file);
        if (((Foutflows.mode == SAVE_FILE) && (!(IgnoreRouting != 0))))
        {
            iface_saveOutletResults(reportDate, Foutflows.file);
        }
        Nperiods++;
    }

    // output.c:515
    static void output_end()
    {
        IBox k = new IBox(0);
        R.fwrite1(IDStartPos.v, 4, Fout.file);
        R.fwrite1(InputStartPos.v, 4, Fout.file);
        R.fwrite1(OutputStartPos.v, 4, Fout.file);
        k.v = (int) (Nperiods);
        R.fwrite1(k.v, 4, Fout.file);
        k.v = ErrorCode;
        R.fwrite1(k.v, 4, Fout.file);
        k.v = 516114522;
        if ((R.fwrite1(k.v, 4, Fout.file) < 1))
        {
            report_writeErrorMsg(ERR_OUT_WRITE, L0);
        }
    }

    // output.c:539
    static void output_close()
    {
        {
            if ((SubcatchResults != null))
            {
                R.free(SubcatchResults);
                SubcatchResults = (null);
            }
        }
        ;
        {
            if ((NodeResults != null))
            {
                R.free(NodeResults);
                NodeResults = (null);
            }
        }
        ;
        {
            if ((LinkResults != null))
            {
                R.free(LinkResults);
                LinkResults = (null);
            }
        }
        ;
        output_closeAvgResults();
    }

    // output.c:554
    static void output_saveID(CPtr id, CFile file)
    {
        IBox n = new IBox((int) (R.strlen(id)));
        R.fwrite1(n.v, 4, file);
        R.fwrite(id, 0, (int) (n.v), file);
    }

    // output.c:569
    static void output_saveSubcatchResults(double reportTime, CFile file)
    {
        int j = 0;
        double f = 0.0;
        double area = 0.0;
        float totalArea = 0.0f;
        double reportDate = getDateTime(reportTime);
        for (j = 0; (j < Nobjects[GAGE]); j++)
        {
            gage_setReportRainfall(j, reportDate);
        }
        f = (((reportTime - OldRunoffTime)) / ((NewRunoffTime - OldRunoffTime)));
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            subcatch_getResults(j, f, SubcatchResults);
            if ((Subcatch[j].rptFlag != 0))
            {
                R.fwrite(SubcatchResults, 0, (int) (NumSubcatchVars), file);
            }
            area = (Subcatch[j].area * UCF(LANDAREA));
            totalArea += ((float) (area));
            SysResults[SYS_RAINFALL] += ((float) (((SubcatchResults[SUBCATCH_RAINFALL] * area))));
            SysResults[SYS_SNOWDEPTH] += ((float) (((SubcatchResults[SUBCATCH_SNOWDEPTH] * area))));
            SysResults[SYS_EVAP] += ((float) (((SubcatchResults[SUBCATCH_EVAP] * area))));
            if ((Subcatch[j].groundwater != null))
            {
                SysResults[SYS_EVAP] += ((float) ((((Subcatch[j].groundwater.evapLoss * UCF(EVAPRATE)) * area))));
            }
            SysResults[SYS_INFIL] += ((float) (((SubcatchResults[SUBCATCH_INFIL] * area))));
            SysResults[SYS_RUNOFF] += SubcatchResults[SUBCATCH_RUNOFF];
        }
        if ((totalArea > 0.0))
        {
            SysResults[SYS_EVAP] /= totalArea;
            SysResults[SYS_RAINFALL] /= totalArea;
            SysResults[SYS_SNOWDEPTH] /= totalArea;
            SysResults[SYS_INFIL] /= totalArea;
        }
        if ((UnitSystem == SI))
        {
            f = (((5.0 / 9.0)) * ((Temp.ta - 32.0)));
        }
        else
        {
            f = Temp.ta;
        }
        SysResults[SYS_TEMPERATURE] = ((float) (f));
        f = (Evap.rate * UCF(EVAPRATE));
        SysResults[SYS_PET] = ((float) (f));
    }

    // output.c:636
    static void output_saveNodeResults(double reportTime, CFile file)
    {
        int j = 0;
        double f = (((reportTime - OldRoutingTime)) / ((NewRoutingTime - OldRoutingTime)));
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            node_getResults(j, f, NodeResults);
            if ((Node[j].rptFlag != 0))
            {
                R.fwrite(NodeResults, 0, (int) (NumNodeVars), file);
            }
            stats_updateMaxNodeDepth(j, NodeResults[NODE_DEPTH]);
            SysResults[SYS_STORAGE] += NodeResults[NODE_VOLUME];
        }
    }

    // output.c:666
    static void output_saveLinkResults(double reportTime, CFile file)
    {
        int j = 0;
        double f = 0.0;
        double z = 0.0;
        f = (((reportTime - OldRoutingTime)) / ((NewRoutingTime - OldRoutingTime)));
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            if ((Link[j].rptFlag != 0))
            {
                link_getResults(j, f, LinkResults);
                R.fwrite(LinkResults, 0, (int) (NumLinkVars), file);
            }
            z = ((((((1.0 - f)) * Link[j].oldVolume) + (f * Link[j].newVolume))) * UCF(VOLUME));
            SysResults[SYS_STORAGE] += ((float) (z));
        }
    }

    // output.c:699
    static void output_readDateTime(long period, DRef days)
    {
        long p = period;
        long bytePos = (OutputStartPos.v + (((p - 1)) * BytesPerPeriod));
        R.fseek(Fout.file, bytePos, 0);
        days.set((-693594));
        R.fread1(days, 8, Fout.file);
    }

    // output.c:716
    static void output_readSubcatchResults(long period, int index)
    {
        long offset = (index * NumSubcatchVars);
        long p = period;
        long bytePos = (((OutputStartPos.v + (((p - 1)) * BytesPerPeriod)) + 8L) + (offset * 4L));
        R.fseek(Fout.file, bytePos, 0);
        R.fread(SubcatchResults, 0, (int) (NumSubcatchVars), Fout.file);
    }

    // output.c:735
    static void output_readNodeResults(long period, int index)
    {
        long offset = ((NumSubcatch * NumSubcatchVars) + (index * NumNodeVars));
        long p = period;
        long bytePos = (((OutputStartPos.v + (((p - 1)) * BytesPerPeriod)) + 8L) + (offset * 4L));
        R.fseek(Fout.file, bytePos, 0);
        R.fread(NodeResults, 0, (int) (NumNodeVars), Fout.file);
    }

    // output.c:753
    static void output_readLinkResults(long period, int index)
    {
        long offset = ((((NumSubcatch * NumSubcatchVars) + (NumNodes * NumNodeVars)) + (index * NumLinkVars)));
        long p = period;
        long bytePos = (((OutputStartPos.v + (((p - 1)) * BytesPerPeriod)) + 8L) + (offset * 4L));
        R.fseek(Fout.file, bytePos, 0);
        R.fread(LinkResults, 0, (int) (NumLinkVars), Fout.file);
        R.fread(SysResults, 0, (int) (15), Fout.file);
    }

    // output.c:774
    static int output_openAvgResults()
    {
        int i = 0;
        AvgNodeResults = R.fill(new TAvgResults[(int) (NumNodes)], TAvgResults::new);
        if ((AvgNodeResults == null))
        {
            return 0;
        }
        for (i = 0; (i < NumNodes); i++)
        {
            AvgNodeResults[i].xAvg = (null);
        }
        AvgLinkResults = R.fill(new TAvgResults[(int) (NumLinks)], TAvgResults::new);
        if ((AvgLinkResults == null))
        {
            output_closeAvgResults();
            return 0;
        }
        for (i = 0; (i < NumLinks); i++)
        {
            AvgLinkResults[i].xAvg = (null);
        }
        for (i = 0; (i < NumNodes); i++)
        {
            AvgNodeResults[i].xAvg = new float[(int) (NumNodeVars)];
            if ((AvgNodeResults[i].xAvg == null))
            {
                output_closeAvgResults();
                return 0;
            }
        }
        for (i = 0; (i < NumLinks); i++)
        {
            AvgLinkResults[i].xAvg = new float[(int) (NumLinkVars)];
            if ((AvgLinkResults[i].xAvg == null))
            {
                output_closeAvgResults();
                return 0;
            }
        }
        return 1;
    }

    // output.c:820
    static void output_closeAvgResults()
    {
        int i = 0;
        if ((AvgNodeResults != null))
        {
            for (i = 0; (i < NumNodes); i++)
            {
                if ((AvgNodeResults[i].xAvg != null))
                {
                    R.free(AvgNodeResults[i].xAvg);
                    AvgNodeResults[i].xAvg = (null);
                }
            }
            ;
            {
                if ((AvgNodeResults != null))
                {
                    R.free(AvgNodeResults);
                    AvgNodeResults = (null);
                }
            }
            ;
        }
        if ((AvgLinkResults != null))
        {
            for (i = 0; (i < NumLinks); i++)
            {
                if ((AvgLinkResults[i].xAvg != null))
                {
                    R.free(AvgLinkResults[i].xAvg);
                    AvgLinkResults[i].xAvg = (null);
                }
            }
            ;
            {
                if ((AvgLinkResults != null))
                {
                    R.free(AvgLinkResults);
                    AvgLinkResults = (null);
                }
            }
            ;
        }
    }

    // output.c:839
    static void output_initAvgResults()
    {
        int i = 0;
        int j = 0;
        output_Nsteps = 0;
        for (i = 0; (i < NumNodes); i++)
        {
            for (j = 0; (j < NumNodeVars); j++)
            {
                AvgNodeResults[i].xAvg[j] = (float) (0.0);
            }
        }
        for (i = 0; (i < NumLinks); i++)
        {
            for (j = 0; (j < NumLinkVars); j++)
            {
                AvgLinkResults[i].xAvg[j] = (float) (0.0);
            }
        }
    }

    // output.c:857
    static void output_updateAvgResults()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int sign = 0;
        k = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if (!(Node[i].rptFlag != 0))
            {
                continue;
            }
            node_getResults(i, 1.0, NodeResults);
            for (j = 0; (j < NumNodeVars); j++)
            {
                AvgNodeResults[k].xAvg[j] += NodeResults[j];
            }
            k++;
        }
        k = 0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if (!(Link[i].rptFlag != 0))
            {
                continue;
            }
            link_getResults(i, 1.0, LinkResults);
            sign = (((((LinkResults[LINK_FLOW]) < (float) (0))) ? ((-1)) : (1)));
            for (j = 0; (j < NumLinkVars); j++)
            {
                if ((j == LINK_CAPACITY))
                {
                    if ((Link[i].type == CONDUIT))
                    {
                        AvgLinkResults[k].xAvg[j] += LinkResults[j];
                    }
                    else
                    {
                        AvgLinkResults[k].xAvg[j] = (LinkResults[j] * (float) (((output_Nsteps + 1))));
                    }
                }
                else
                {
                    AvgLinkResults[k].xAvg[j] += LinkResults[j];
                }
            }
            k++;
        }
        output_Nsteps++;
    }

    // output.c:911
    static void output_saveAvgResults(CFile file)
    {
        int i = 0;
        int j = 0;
        for (i = 0; (i < NumNodes); i++)
        {
            for (j = 0; (j < NumNodeVars); j++)
            {
                NodeResults[j] = (AvgNodeResults[i].xAvg[j] / (float) (output_Nsteps));
            }
            R.fwrite(NodeResults, 0, (int) (NumNodeVars), file);
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            stats_updateMaxNodeDepth(i, (Node[i].newDepth * UCF(LENGTH)));
            SysResults[SYS_STORAGE] += ((float) (((Node[i].newVolume * UCF(VOLUME)))));
        }
        for (i = 0; (i < NumLinks); i++)
        {
            for (j = 0; (j < NumLinkVars); j++)
            {
                LinkResults[j] = (AvgLinkResults[i].xAvg[j] / (float) (output_Nsteps));
            }
            R.fwrite(LinkResults, 0, (int) (NumLinkVars), file);
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            SysResults[SYS_STORAGE] += ((float) (((Link[i].newVolume * UCF(VOLUME)))));
        }
        output_initAvgResults();
    }

}
