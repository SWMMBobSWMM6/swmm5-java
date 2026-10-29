// Generated from EPA SWMM 5.2.4 (rdii.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_rdii {
    static double ZERO_RDII = 0.0001;
    static CPtr FileStamp = R.bufFrom(L352, 11);
    static TUHGroup[] UHGroup = null;
    static IBox RdiiStep = new IBox(0);
    static IBox NumRdiiNodes = new IBox(0);
    static int[] RdiiNodeIndex = null;
    static float[] RdiiNodeFlow = null;
    static int RdiiFlowUnits = 0;
    static DBox RdiiStartDate = new DBox(0.0);
    static double RdiiEndDate = 0.0;
    static double TotalRainVol = 0.0;
    static double TotalRdiiVol = 0.0;
    static int RdiiFileType = 0;

    // rdii.c:152
    static int rdii_readRdiiInflow(CPtr[] tok, int ntoks)
    {
        int j = 0;
        int k = 0;
        DBox a = new DBox(0.0);
        TRdiiInflow inflow = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(NODE, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        k = project_findObject(UNITHYD, tok[1]);
        if ((k < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        if (((!(getDouble(tok[2], a) != 0)) || (a.v < 0.0)))
        {
            return error_setInpError(ERR_NUMBER, tok[2]);
        }
        inflow = Node[j].rdiiInflow;
        if ((inflow == null))
        {
            inflow = new TRdiiInflow();
            if (!(inflow != null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
        }
        inflow.unitHyd = k;
        inflow.area = (a.v / UCF(LANDAREA));
        Node[j].rdiiInflow = inflow;
        return 0;
    }

    // rdii.c:198
    static void rdii_initUnitHyd(int j)
    {
        int i = 0;
        int m = 0;
        for (m = 0; (m < 12); m++)
        {
            for (i = 0; (i < 3); i++)
            {
                UnitHyd[j].iaMax[m][i] = 0.0;
                UnitHyd[j].iaRecov[m][i] = 0.0;
                UnitHyd[j].iaInit[m][i] = 0.0;
                UnitHyd[j].r[m][i] = 0.0;
                UnitHyd[j].tPeak[m][i] = 0;
                UnitHyd[j].tBase[m][i] = 0;
            }
        }
    }

    // rdii.c:224
    static int rdii_readUnitHydParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int g = 0;
        double[] x = new double[6];
        j = project_findObject(UNITHYD, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        if ((UnitHyd[j].ID == null))
        {
            UnitHyd[j].ID = project_findID(UNITHYD, tok[0]);
        }
        if ((ntoks == 2))
        {
            g = project_findObject(GAGE, tok[1]);
            if ((g < 0))
            {
                return error_setInpError(ERR_NAME, tok[1]);
            }
            UnitHyd[j].rainGage = g;
            Gage[g].isUsed = 1;
            return 0;
        }
        else if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        m = datetime_findMonth(tok[1]);
        if ((m == 0))
        {
            if (!(match(tok[1], L351) != 0))
            {
                return error_setInpError(ERR_KEYWORD, tok[1]);
            }
        }
        k = findmatch(tok[2], UHTypeWords);
        if ((k < 0))
        {
            return readOldUHFormat(j, m, tok, ntoks);
        }
        for (i = 0; (i < 3); i++)
        {
            if (!(getDouble(tok[(i + 3)], new DElem(x, i)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[(i + 3)]);
            }
        }
        for (i = 3; (i < 6); i++)
        {
            x[i] = 0.0;
            if ((ntoks > (i + 3)))
            {
                if (!(getDouble(tok[(i + 3)], new DElem(x, i)) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[(i + 2)]);
                }
            }
        }
        setUnitHydParams(j, k, m, x);
        return 0;
    }

    // rdii.c:293
    static int readOldUHFormat(int j, int m, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int k = 0;
        double[] p = new double[9];
        double[] x = new double[6];
        if ((ntoks < 11))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 0; (i < 9); i++)
        {
            if (!(getDouble(tok[(i + 2)], new DElem(p, i)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[(i + 2)]);
            }
        }
        for (i = 0; (i < 3); i++)
        {
            x[(i + 3)] = 0.0;
            if ((ntoks > (i + 11)))
            {
                if (!(getDouble(tok[(i + 11)], new DElem(x, (i + 3))) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[(i + 11)]);
                }
            }
        }
        for (k = 0; (k < 3); k++)
        {
            for (i = 0; (i < 3); i++)
            {
                x[i] = p[((3 * k) + i)];
                setUnitHydParams(j, k, m, x);
            }
        }
        return 0;
    }

    // rdii.c:342
    static void setUnitHydParams(int j, int i, int m, double[] x)
    {
        int m1 = 0;
        int m2 = 0;
        double t = 0.0;
        double k = 0.0;
        double tBase = 0.0;
        if ((m == 0))
        {
            m1 = 0;
            m2 = 11;
        }
        else
        {
            m1 = (m - 1);
            m2 = m1;
        }
        for (m = m1; (m <= m2); m++)
        {
            UnitHyd[j].r[m][i] = x[0];
            t = x[1];
            k = x[2];
            tBase = (t * ((1.0 + k)));
            UnitHyd[j].tPeak[m][i] = R.D2L(((t * 3600.0)));
            UnitHyd[j].tBase[m][i] = R.D2L(((tBase * 3600.0)));
            UnitHyd[j].iaMax[m][i] = x[3];
            UnitHyd[j].iaRecov[m][i] = x[4];
            UnitHyd[j].iaInit[m][i] = x[5];
        }
    }

    // rdii.c:389
    static void rdii_deleteRdiiInflow(int j)
    {
        if ((Node[j].rdiiInflow != null))
        {
            R.free(Node[j].rdiiInflow);
            Node[j].rdiiInflow = (null);
        }
    }

    // rdii.c:408
    static void rdii_openRdii()
    {
        CPtr fStamp = R.bufFrom(L352, 11);
        RdiiNodeIndex = (null);
        RdiiNodeFlow = (null);
        NumRdiiNodes.v = 0;
        RdiiStartDate.v = (-693594);
        if ((IgnoreRDII != 0))
        {
            return;
        }
        if ((Frdii.mode != USE_FILE))
        {
            createRdiiFile();
        }
        if (((Frdii.mode == NO_FILE) || (ErrorCode != 0)))
        {
            return;
        }
        Frdii.file = R.fopen(Frdii.name, L353);
        if ((Frdii.file == null))
        {
            if ((Frdii.mode == SCRATCH_FILE))
            {
                report_writeErrorMsg(ERR_RDII_FILE_SCRATCH, L0);
            }
            else
            {
                report_writeErrorMsg(ERR_RDII_FILE_OPEN, Frdii.name);
            }
            return;
        }
        R.fread(fStamp, 0, (int) (R.strlen(FileStamp)), Frdii.file);
        if ((R.strcmp(fStamp, FileStamp) == 0))
        {
            RdiiFileType = BINARY;
            ErrorCode = readRdiiFileHeader();
        }
        else
        {
            R.fclose(Frdii.file);
            RdiiFileType = TEXT;
            openRdiiTextFile();
        }
        if ((ErrorCode != 0))
        {
            report_writeErrorMsg(ErrorCode, Frdii.name);
        }
        else
        {
            readRdiiFlows();
        }
    }

    // rdii.c:470
    static void openRdiiTextFile()
    {
        Frdii.file = R.fopen(Frdii.name, L13);
        if ((Frdii.file == null))
        {
            if ((Frdii.mode == SCRATCH_FILE))
            {
                report_writeErrorMsg(ERR_RDII_FILE_SCRATCH, L0);
            }
            else
            {
                report_writeErrorMsg(ERR_RDII_FILE_OPEN, Frdii.name);
            }
            return;
        }
        ErrorCode = readRdiiTextFileHeader();
        if ((ErrorCode != 0))
        {
            report_writeErrorMsg(ErrorCode, Frdii.name);
        }
    }

    // rdii.c:497
    static void rdii_closeRdii()
    {
        if ((Frdii.file != null))
        {
            R.fclose(Frdii.file);
        }
        if ((Frdii.mode == SCRATCH_FILE))
        {
            R.remove(Frdii.name);
        }
        {
            if ((RdiiNodeIndex != null))
            {
                R.free(RdiiNodeIndex);
                RdiiNodeIndex = (null);
            }
        }
        ;
        {
            if ((RdiiNodeFlow != null))
            {
                R.free(RdiiNodeFlow);
                RdiiNodeFlow = (null);
            }
        }
        ;
    }

    // rdii.c:512
    static int rdii_getNumRdiiFlows(double aDate)
    {
        if ((NumRdiiNodes.v == 0))
        {
            return 0;
        }
        if (!(Frdii.file != null))
        {
            return 0;
        }
        while (!(R.feof(Frdii.file) != 0))
        {
            if ((RdiiStartDate.v == (-693594)))
            {
                return 0;
            }
            if ((aDate < RdiiStartDate.v))
            {
                return 0;
            }
            if ((aDate < RdiiEndDate))
            {
                return NumRdiiNodes.v;
            }
            else
            {
                readRdiiFlows();
            }
        }
        return 0;
    }

    // rdii.c:542
    static void rdii_getRdiiFlow(int i, IRef j, DRef q)
    {
        if (((i >= 0) && (i < NumRdiiNodes.v)))
        {
            j.set(RdiiNodeIndex[i]);
            q.set(RdiiNodeFlow[i]);
        }
    }

    // rdii.c:560
    static int readRdiiFileHeader()
    {
        int i = 0;
        int j = 0;
        R.fread1(RdiiStep, 4, Frdii.file);
        if ((RdiiStep.v <= 0))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        R.fread1(NumRdiiNodes, 4, Frdii.file);
        if ((NumRdiiNodes.v <= 0))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        RdiiNodeIndex = new int[(int) (NumRdiiNodes.v)];
        if (!(RdiiNodeIndex != null))
        {
            return ERR_MEMORY;
        }
        RdiiNodeFlow = new float[(int) (NumRdiiNodes.v)];
        if (!(RdiiNodeFlow != null))
        {
            return ERR_MEMORY;
        }
        if ((R.feof(Frdii.file) != 0))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        R.fread(RdiiNodeIndex, 0, (int) (NumRdiiNodes.v), Frdii.file);
        for (i = 0; (i < NumRdiiNodes.v); i++)
        {
            j = RdiiNodeIndex[i];
            if ((Node[j].rdiiInflow == null))
            {
                return ERR_RDII_FILE_FORMAT;
            }
        }
        if ((R.feof(Frdii.file) != 0))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        return 0;
    }

    // rdii.c:595
    static int readRdiiTextFileHeader()
    {
        int i = 0;
        int j = 0;
        CPtr line = R.buf(1025);
        CPtr s1 = R.bufFrom(L0, 1025);
        CPtr s2 = R.buf(1025);
        R.fgets(line, (int) (1024), Frdii.file);
        if (((!(R.sscanf(line, L169, s1) != 0)) || (R.strcmp(s1, L181) != 0)))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        R.fgets(line, (int) (1024), Frdii.file);
        RdiiStep.v = 0;
        R.fgets(line, (int) (1024), Frdii.file);
        if (((!(R.sscanf(line, L182, RdiiStep) != 0)) || (RdiiStep.v <= 0)))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        R.fgets(line, (int) (1024), Frdii.file);
        R.fgets(line, (int) (1024), Frdii.file);
        if ((R.sscanf(line, L44, s1, s2) < 2))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        RdiiFlowUnits = findmatch(s2, FlowUnitWords);
        if ((RdiiFlowUnits < 0))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        R.fgets(line, (int) (1024), Frdii.file);
        if (((R.sscanf(line, L182, NumRdiiNodes) < 1) || (NumRdiiNodes.v <= 0)))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        RdiiNodeIndex = new int[(int) (NumRdiiNodes.v)];
        if (!(RdiiNodeIndex != null))
        {
            return ERR_MEMORY;
        }
        RdiiNodeFlow = new float[(int) (NumRdiiNodes.v)];
        if (!(RdiiNodeFlow != null))
        {
            return ERR_MEMORY;
        }
        for (i = 0; (i < NumRdiiNodes.v); i++)
        {
            if ((R.feof(Frdii.file) != 0))
            {
                return ERR_RDII_FILE_FORMAT;
            }
            R.fgets(line, (int) (1024), Frdii.file);
            if (!(R.sscanf(line, L169, s1) != 0))
            {
                return ERR_RDII_FILE_FORMAT;
            }
            j = project_findObject(NODE, s1);
            if ((j < 0))
            {
                return ERR_RDII_FILE_FORMAT;
            }
            RdiiNodeIndex[i] = j;
        }
        if ((R.feof(Frdii.file) != 0))
        {
            return ERR_RDII_FILE_FORMAT;
        }
        R.fgets(line, (int) (1024), Frdii.file);
        return 0;
    }

    // rdii.c:663
    static void readRdiiFlows()
    {
        if ((RdiiFileType == TEXT))
        {
            readRdiiTextFlows();
        }
        else
        {
            RdiiStartDate.v = (-693594);
            RdiiEndDate = (-693594);
            if ((R.feof(Frdii.file) != 0))
            {
                return;
            }
            R.fread1(RdiiStartDate, 8, Frdii.file);
            if ((RdiiStartDate.v == (-693594)))
            {
                return;
            }
            if ((R.fread(RdiiNodeFlow, 0, (int) (NumRdiiNodes.v), Frdii.file) < NumRdiiNodes.v))
            {
                RdiiStartDate.v = (-693594);
            }
            else
            {
                RdiiEndDate = datetime_addSeconds(RdiiStartDate.v, RdiiStep.v);
            }
        }
    }

    // rdii.c:686
    static void readRdiiTextFlows()
    {
        int i = 0;
        int n = 0;
        IBox yr = new IBox(0);
        IBox mon = new IBox(0);
        IBox day = new IBox(0);
        IBox hr = new IBox(0);
        IBox min = new IBox(0);
        IBox sec = new IBox(0);
        DBox x = new DBox(0.0);
        CPtr line = R.buf(1025);
        CPtr s = R.buf(1025);
        RdiiStartDate.v = (-693594);
        for (i = 0; (i < NumRdiiNodes.v); i++)
        {
            if ((R.feof(Frdii.file) != 0))
            {
                return;
            }
            R.fgets(line, (int) (1024), Frdii.file);
            n = R.sscanf(line, L354, s, yr, mon, day, hr, min, sec, x);
            if ((n < 8))
            {
                return;
            }
            RdiiNodeFlow[i] = ((float) (((x.v / Qcf[RdiiFlowUnits]))));
        }
        RdiiStartDate.v = (datetime_encodeDate(yr.v, mon.v, day.v) + datetime_encodeTime(hr.v, min.v, sec.v));
        RdiiEndDate = datetime_addSeconds(RdiiStartDate.v, RdiiStep.v);
    }

    // rdii.c:720
    static void createRdiiFile()
    {
        int hasRdii = 0;
        double elapsedTime = 0.0;
        double duration = 0.0;
        double currentDate = 0.0;
        RdiiStep.v = WetStep;
        NumRdiiNodes.v = getNumRdiiNodes();
        if ((NumRdiiNodes.v == 0))
        {
            Frdii.mode = NO_FILE;
            return;
        }
        else if ((Frdii.mode == NO_FILE))
        {
            Frdii.mode = SCRATCH_FILE;
        }
        validateRdii();
        initGageData();
        if ((ErrorCode != 0))
        {
            return;
        }
        openRdiiProcessor();
        if (!(ErrorCode != 0))
        {
            initUnitHydData();
            duration = (TotalDuration / 1000.0);
            elapsedTime = 0.0;
            while (((elapsedTime <= duration) && (!(ErrorCode != 0))))
            {
                currentDate = (StartDateTime + (elapsedTime / 86400));
                getRainfall(currentDate);
                getUnitHydRdii(currentDate);
                hasRdii = getNodeRdii();
                if ((hasRdii != 0))
                {
                    saveRdiiFlows(currentDate);
                }
                elapsedTime += RdiiStep.v;
            }
        }
        closeRdiiProcessor();
    }

    // rdii.c:793
    static int getNumRdiiNodes()
    {
        int j = 0;
        int n = 0;
        n = 0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if ((Node[j].rdiiInflow != null))
            {
                n++;
            }
        }
        return n;
    }

    // rdii.c:813
    static void validateRdii()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        double rsum = 0.0;
        for (j = 0; (j < Nobjects[UNITHYD]); j++)
        {
            for (m = 0; (m < 12); m++)
            {
                rsum = 0.0;
                for (k = 0; (k < 3); k++)
                {
                    if ((UnitHyd[j].tBase[m][k] == 0))
                    {
                        continue;
                    }
                    if ((UnitHyd[j].tPeak[m][k] < 0.0))
                    {
                        report_writeErrorMsg(ERR_UNITHYD_TIMES, UnitHyd[j].ID);
                    }
                    if ((UnitHyd[j].r[m][k] < 0.0))
                    {
                        report_writeErrorMsg(ERR_UNITHYD_RATIOS, UnitHyd[j].ID);
                    }
                    else
                    {
                        rsum += UnitHyd[j].r[m][k];
                    }
                }
                if ((rsum > 1.01))
                {
                    report_writeErrorMsg(ERR_UNITHYD_RATIOS, UnitHyd[j].ID);
                }
            }
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].rdiiInflow != null))
            {
                if ((Node[i].rdiiInflow.area < 0.0))
                {
                    report_writeErrorMsg(ERR_RDII_AREA, Node[i].ID);
                }
            }
        }
    }

    // rdii.c:877
    static void openRdiiProcessor()
    {
        int j = 0;
        int n = 0;
        UHGroup = (null);
        RdiiNodeIndex = (null);
        RdiiNodeFlow = (null);
        TotalRainVol = 0.0;
        TotalRdiiVol = 0.0;
        if (!(allocRdiiMemory() != 0))
        {
            report_writeErrorMsg(ERR_MEMORY, L0);
            return;
        }
        if (!(openNewRdiiFile() != 0))
        {
            report_writeErrorMsg(ERR_RDII_FILE_SCRATCH, L0);
            return;
        }
        n = 0;
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            if (((Node[j].rdiiInflow != null) && (RdiiNodeIndex != null)))
            {
                RdiiNodeIndex[n] = j;
                n++;
            }
        }
    }

    // rdii.c:922
    static int allocRdiiMemory()
    {
        int i = 0;
        int k = 0;
        int n = 0;
        UHGroup = R.fill(new TUHGroup[(int) (Nobjects[UNITHYD])], TUHGroup::new);
        if (!(UHGroup != null))
        {
            return 0;
        }
        for (i = 0; (i < Nobjects[UNITHYD]); i++)
        {
            UHGroup[i].rainInterval = getRainInterval(i);
            for (k = 0; (k < 3); k++)
            {
                UHGroup[i].uh[k].pastRain = (null);
                UHGroup[i].uh[k].pastMonth = null;
                UHGroup[i].uh[k].maxPeriods = getMaxPeriods(i, k);
                n = UHGroup[i].uh[k].maxPeriods;
                if ((n > 0))
                {
                    UHGroup[i].uh[k].pastRain = new double[(int) (n)];
                    if (!(UHGroup[i].uh[k].pastRain != null))
                    {
                        return 0;
                    }
                    UHGroup[i].uh[k].pastMonth = R.buf((int) (n));
                    if (!(UHGroup[i].uh[k].pastMonth != null))
                    {
                        return 0;
                    }
                }
            }
        }
        RdiiNodeIndex = new int[(int) (NumRdiiNodes.v)];
        if (!(RdiiNodeIndex != null))
        {
            return 0;
        }
        RdiiNodeFlow = new float[(int) (NumRdiiNodes.v)];
        if (!(RdiiNodeFlow != null))
        {
            return 0;
        }
        return 1;
    }

    // rdii.c:970
    static int getRainInterval(int i)
    {
        int ri = 0;
        int tLimb = 0;
        int k = 0;
        int m = 0;
        ri = WetStep;
        for (m = 0; (m < 12); m++)
        {
            for (k = 0; (k < 3); k++)
            {
                if ((UnitHyd[i].tPeak[m][k] > 0))
                {
                    tLimb = (int) (UnitHyd[i].tPeak[m][k]);
                    ri = (((((ri) <= (tLimb))) ? (ri) : (tLimb)));
                    tLimb = (int) ((UnitHyd[i].tBase[m][k] - tLimb));
                    if ((tLimb > 0))
                    {
                        ri = (((((ri) <= (tLimb))) ? (ri) : (tLimb)));
                    }
                }
            }
        }
        return ri;
    }

    // rdii.c:1005
    static int getMaxPeriods(int i, int k)
    {
        int m = 0;
        int n = 0;
        int nMax = 0;
        int rainInterval = 0;
        rainInterval = UHGroup[i].rainInterval;
        nMax = 0;
        for (m = 0; (m < 12); m++)
        {
            n = (int) ((((UnitHyd[i].tBase[m][k] / rainInterval)) + 1));
            nMax = (((((n) >= (nMax))) ? (n) : (nMax)));
        }
        return nMax;
    }

    // rdii.c:1034
    static void initGageData()
    {
        int i = 0;
        int g = 0;
        for (g = 0; (g < Nobjects[GAGE]); g++)
        {
            if ((Gage[g].tSeries >= 0))
            {
                table_tseriesInit(Tseries[Gage[g].tSeries]);
            }
            gage_initState(g);
        }
        for (i = 0; (i < Nobjects[UNITHYD]); i++)
        {
            g = UnitHyd[i].rainGage;
            if ((g >= 0))
            {
                if ((Gage[g].coGage >= 0))
                {
                    UnitHyd[i].rainGage = Gage[g].coGage;
                }
            }
        }
    }

    // rdii.c:1072
    static void initUnitHydData()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int n = 0;
        int month = 0;
        month = (datetime_monthOfYear(StartDateTime) - 1);
        for (i = 0; (i < Nobjects[UNITHYD]); i++)
        {
            for (k = 0; (k < 3); k++)
            {
                UHGroup[i].uh[k].drySeconds = (((UHGroup[i].uh[k].maxPeriods * UHGroup[i].rainInterval)) + 1);
                UHGroup[i].uh[k].period = (UHGroup[i].uh[k].maxPeriods + 1);
                UHGroup[i].uh[k].hasPastRain = 0;
                UHGroup[i].uh[k].iaUsed = UnitHyd[i].iaInit[month][k];
            }
            UHGroup[i].gageDate = StartDateTime;
            UHGroup[i].area = 0.0;
            UHGroup[i].rdii = 0.0;
        }
        for (i = 0; (i < Nobjects[UNITHYD]); i++)
        {
            UHGroup[i].isUsed = 0;
        }
        for (n = 0; (n < NumRdiiNodes.v); n++)
        {
            j = RdiiNodeIndex[n];
            i = Node[j].rdiiInflow.unitHyd;
            UHGroup[i].isUsed = 1;
            UHGroup[i].lastDate = StartDateTime;
            UHGroup[i].area += Node[j].rdiiInflow.area;
        }
    }

    // rdii.c:1128
    static int openNewRdiiFile()
    {
        IBox j = new IBox(0);
        if ((Frdii.mode == SCRATCH_FILE))
        {
            getTempFileName(Frdii.name);
        }
        Frdii.file = R.fopen(Frdii.name, L166);
        if ((Frdii.file == null))
        {
            return 0;
        }
        R.fwrite(FileStamp, 0, (int) (R.strlen(FileStamp)), Frdii.file);
        R.fwrite1(RdiiStep.v, 4, Frdii.file);
        R.fwrite1(NumRdiiNodes.v, 4, Frdii.file);
        for (j.v = 0; (j.v < Nobjects[NODE]); j.v++)
        {
            if ((Node[j.v].rdiiInflow != null))
            {
                R.fwrite1(j.v, 4, Frdii.file);
            }
        }
        return 1;
    }

    // rdii.c:1163
    static void getRainfall(double currentDate)
    {
        int j = 0;
        int k = 0;
        int g = 0;
        int i = 0;
        int month = 0;
        int rainInterval = 0;
        double rainDepth = 0.0;
        double excessDepth = 0.0;
        double gageDate = 0.0;
        month = (datetime_monthOfYear(currentDate) - 1);
        for (g = 0; (g < Nobjects[GAGE]); g++)
        {
            Gage[g].isCurrent = 0;
        }
        for (j = 0; (j < Nobjects[UNITHYD]); j++)
        {
            g = UnitHyd[j].rainGage;
            rainInterval = UHGroup[j].rainInterval;
            while ((UHGroup[j].gageDate < currentDate))
            {
                gageDate = UHGroup[j].gageDate;
                Adjust.rainFactor = Adjust.rain[(datetime_monthOfYear(gageDate) - 1)];
                if (!(Gage[g].isCurrent != 0))
                {
                    gage_setState(g, gageDate);
                    Gage[g].isCurrent = 1;
                }
                rainDepth = ((Gage[g].rainfall * ((double) (rainInterval))) / 3600.0);
                TotalRainVol += ((rainDepth / UCF(RAINDEPTH)) * UHGroup[j].area);
                for (k = 0; (k < 3); k++)
                {
                    excessDepth = applyIA(j, k, gageDate, rainInterval, rainDepth);
                    updateDryPeriod(j, k, excessDepth, rainInterval);
                    i = UHGroup[j].uh[k].period;
                    if ((i >= UHGroup[j].uh[k].maxPeriods))
                    {
                        i = 0;
                    }
                    UHGroup[j].uh[k].pastRain[i] = excessDepth;
                    UHGroup[j].uh[k].pastMonth.s(i, ((byte) (month)));
                    UHGroup[j].uh[k].period = (i + 1);
                }
                UHGroup[j].gageDate = datetime_addSeconds(gageDate, rainInterval);
            }
        }
    }

    // rdii.c:1231
    static double applyIA(int j, int k, double aDate, double dt, double rainDepth)
    {
        int m = 0;
        double ia = 0.0;
        double netRainDepth = 0.0;
        m = (datetime_monthOfYear(aDate) - 1);
        ia = (UnitHyd[j].iaMax[m][k] - UHGroup[j].uh[k].iaUsed);
        ia = (((((ia) >= (0.0))) ? (ia) : (0.0)));
        if ((rainDepth > 0.0))
        {
            netRainDepth = (rainDepth - ia);
            netRainDepth = (((((netRainDepth) >= (0.0))) ? (netRainDepth) : (0.0)));
            ia = (rainDepth - netRainDepth);
            UHGroup[j].uh[k].iaUsed += ia;
        }
        else
        {
            UHGroup[j].uh[k].iaUsed -= ((dt / 86400.0) * UnitHyd[j].iaRecov[m][k]);
            UHGroup[j].uh[k].iaUsed = (((((UHGroup[j].uh[k].iaUsed) >= (0.0))) ? (UHGroup[j].uh[k].iaUsed) : (0.0)));
            netRainDepth = 0.0;
        }
        return netRainDepth;
    }

    // rdii.c:1276
    static void updateDryPeriod(int j, int k, double rainDepth, int rainInterval)
    {
        int i = 0;
        if ((rainDepth > 0.0))
        {
            if ((UHGroup[j].uh[k].drySeconds >= (rainInterval * UHGroup[j].uh[k].maxPeriods)))
            {
                for (i = 0; (i < UHGroup[j].uh[k].maxPeriods); i++)
                {
                    UHGroup[j].uh[k].pastRain[i] = 0.0;
                }
                UHGroup[j].uh[k].period = 0;
            }
            UHGroup[j].uh[k].drySeconds = 0;
            UHGroup[j].uh[k].hasPastRain = 1;
        }
        else
        {
            UHGroup[j].uh[k].drySeconds += rainInterval;
            if ((UHGroup[j].uh[k].drySeconds >= (rainInterval * UHGroup[j].uh[k].maxPeriods)))
            {
                UHGroup[j].uh[k].hasPastRain = 0;
            }
            else
            {
                UHGroup[j].uh[k].hasPastRain = 1;
            }
        }
    }

    // rdii.c:1321
    static void getUnitHydRdii(double currentDate)
    {
        int j = 0;
        int k = 0;
        int rainInterval = 0;
        for (j = 0; (j < Nobjects[UNITHYD]); j++)
        {
            if (!(UHGroup[j].isUsed != 0))
            {
                continue;
            }
            if ((currentDate < UHGroup[j].lastDate))
            {
                continue;
            }
            UHGroup[j].lastDate = UHGroup[j].gageDate;
            rainInterval = UHGroup[j].rainInterval;
            UHGroup[j].rdii = 0.0;
            for (k = 0; (k < 3); k++)
            {
                if ((UHGroup[j].uh[k].hasPastRain != 0))
                {
                    UHGroup[j].rdii += (getUnitHydConvol(j, k, rainInterval));
                }
            }
        }
    }

    // rdii.c:1358
    static double getUnitHydConvol(int j, int k, int rainInterval)
    {
        int i = 0;
        int m = 0;
        int p = 0;
        int pMax = 0;
        double t = 0.0;
        double u = 0.0;
        double v = 0.0;
        double rdii = 0.0;
        TUHData uh = null;
        rdii = 0.0;
        uh = UHGroup[j].uh[k];
        i = (uh.period - 1);
        if ((i < 0))
        {
            i = (uh.maxPeriods - 1);
        }
        pMax = uh.maxPeriods;
        p = 1;
        while ((p < pMax))
        {
            v = uh.pastRain[i];
            m = uh.pastMonth.g(i);
            if ((v > 0.0))
            {
                t = (((((double) ((p))) - 0.5)) * ((double) (rainInterval)));
                u = (getUnitHydOrd(j, m, k, t) * UnitHyd[j].r[m][k]);
                rdii += (u * v);
            }
            p = (p + 1);
            i = (i - 1);
            if ((i < 0))
            {
                i = (uh.maxPeriods - 1);
            }
        }
        return rdii;
    }

    // rdii.c:1411
    static double getUnitHydOrd(int h, int m, int k, double t)
    {
        double qPeak = 0.0;
        double f = 0.0;
        double t1 = 0.0;
        double t2 = 0.0;
        double tBase = 0.0;
        tBase = UnitHyd[h].tBase[m][k];
        if ((t >= tBase))
        {
            return 0.0;
        }
        qPeak = ((2.0 / tBase) * 3600.0);
        t1 = UnitHyd[h].tPeak[m][k];
        t2 = (tBase - t1);
        if ((t <= t1))
        {
            f = (t / t1);
        }
        else
        {
            f = (1.0 - (((t - t1)) / t2));
        }
        return ((((((f) >= (0.0))) ? (f) : (0.0))) * qPeak);
    }

    // rdii.c:1446
    static int getNodeRdii()
    {
        int hasRdii = 0;
        int i = 0;
        int j = 0;
        int n = 0;
        double rdii = 0.0;
        for (n = 0; (n < NumRdiiNodes.v); n++)
        {
            j = RdiiNodeIndex[n];
            i = Node[j].rdiiInflow.unitHyd;
            rdii = ((UHGroup[i].rdii * Node[j].rdiiInflow.area) / UCF(RAINFALL));
            if ((rdii < ZERO_RDII))
            {
                rdii = 0.0;
            }
            else
            {
                hasRdii = 1;
            }
            RdiiNodeFlow[n] = ((float) (rdii));
            if ((rdii > 0.0))
            {
                TotalRdiiVol += (rdii * ((double) (RdiiStep.v)));
            }
        }
        return hasRdii;
    }

    // rdii.c:1483
    static void saveRdiiFlows(double __currentDate)
    {
        DBox currentDate = new DBox(__currentDate);
        R.fwrite1(currentDate.v, 8, Frdii.file);
        R.fwrite(RdiiNodeFlow, 0, (int) (NumRdiiNodes.v), Frdii.file);
    }

    // rdii.c:1496
    static void closeRdiiProcessor()
    {
        if (!(ErrorCode != 0))
        {
            report_writeRdiiStats(TotalRainVol, TotalRdiiVol);
        }
        freeRdiiMemory();
        if ((Frdii.file != null))
        {
            R.fclose(Frdii.file);
        }
    }

    // rdii.c:1516
    static void freeRdiiMemory()
    {
        int i = 0;
        int k = 0;
        if ((UHGroup != null))
        {
            for (i = 0; (i < Nobjects[UNITHYD]); i++)
            {
                for (k = 0; (k < 3); k++)
                {
                    {
                        if ((UHGroup[i].uh[k].pastRain != null))
                        {
                            R.free(UHGroup[i].uh[k].pastRain);
                            UHGroup[i].uh[k].pastRain = (null);
                        }
                    }
                    ;
                    {
                        if ((UHGroup[i].uh[k].pastMonth != null))
                        {
                            R.free(UHGroup[i].uh[k].pastMonth);
                            UHGroup[i].uh[k].pastMonth = null;
                        }
                    }
                    ;
                }
            }
            {
                if ((UHGroup != null))
                {
                    R.free(UHGroup);
                    UHGroup = (null);
                }
            }
            ;
        }
        {
            if ((RdiiNodeIndex != null))
            {
                R.free(RdiiNodeIndex);
                RdiiNodeIndex = (null);
            }
        }
        ;
        {
            if ((RdiiNodeFlow != null))
            {
                R.free(RdiiNodeFlow);
                RdiiNodeFlow = (null);
            }
        }
        ;
    }

}
