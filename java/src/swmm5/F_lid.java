// Generated from EPA SWMM 5.2.4 (lid.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_lid {
    static CPtr[] LidLayerWords = new CPtr[] { L991, L992, L742, L993, L994, L995, L996, null };
    static CPtr[] LidTypeWords = new CPtr[] { L997, L998, L953, L999, L1000, L1001, L1002, L1003, null };
    static TLidProc[] LidProcs = null;
    static int LidCount = 0;
    static LidGroup[] LidGroups = null;
    static int GroupCount = 0;
    static double lid_EvapRate = 0.0;
    static double NativeInfil = 0.0;
    static double lid_MaxNativeInfil = 0.0;
    static int initLidRptFile_colCount = 14;
    static CPtr[] initLidRptFile_head1 = new CPtr[] { L1022, L1023, L1024, L1024, L1025, L1026, L1027, L1028, L1025, L1029, L1025, L1026, L1027, L1030, null };
    static CPtr[] initLidRptFile_head2 = new CPtr[] { L1022, L1031, L1032, L1033, L1034, L1035, L1035, L1036, L1037, L1038, L1039, L1039, L1040, L1041, null };
    static CPtr[] initLidRptFile_units1 = new CPtr[] { L1042, L1043, L1044, L1044, L1044, L1044, L1044, L1044, L1044, L1044, L1045, L1045, L1046, L1047, null };
    static CPtr[] initLidRptFile_units2 = new CPtr[] { L1042, L1043, L1048, L1048, L1048, L1048, L1048, L1048, L1048, L1048, L1049, L1049, L1046, L1050, null };
    static CPtr initLidRptFile_line9 = R.bufFrom(L1051, 11);

    // lid.c:237
    static void lid_create(int lidCount, int subcatchCount)
    {
        int j = 0;
        LidProcs = (null);
        LidGroups = (null);
        LidCount = lidCount;
        GroupCount = subcatchCount;
        if ((GroupCount > 0))
        {
            LidGroups = new LidGroup[(int) (GroupCount)];
            if ((LidGroups == null))
            {
                ErrorCode = ERR_MEMORY;
                return;
            }
        }
        for (j = 0; (j < GroupCount); j++)
        {
            LidGroups[j] = (null);
        }
        if ((LidCount == 0))
        {
            return;
        }
        LidProcs = R.fill(new TLidProc[(int) (LidCount)], TLidProc::new);
        if ((LidProcs == null))
        {
            ErrorCode = ERR_MEMORY;
            return;
        }
        for (j = 0; (j < LidCount); j++)
        {
            LidProcs[j].lidType = (-1);
            LidProcs[j].surface.thickness = 0.0;
            LidProcs[j].surface.voidFrac = 1.0;
            LidProcs[j].surface.roughness = 0.0;
            LidProcs[j].surface.surfSlope = 0.0;
            LidProcs[j].pavement.thickness = 0.0;
            LidProcs[j].soil.thickness = 0.0;
            LidProcs[j].storage.thickness = 0.0;
            LidProcs[j].storage.kSat = 0.0;
            LidProcs[j].drain.coeff = 0.0;
            LidProcs[j].drain.offset = 0.0;
            LidProcs[j].drainMat.thickness = 0.0;
            LidProcs[j].drainMat.roughness = 0.0;
            LidProcs[j].drainRmvl = (null);
            LidProcs[j].drainRmvl = new double[(int) (Nobjects[POLLUT])];
            if ((LidProcs[j].drainRmvl == null))
            {
                ErrorCode = ERR_MEMORY;
                return;
            }
        }
    }

    // lid.c:304
    static void lid_delete()
    {
        int j = 0;
        for (j = 0; (j < GroupCount); j++)
        {
            freeLidGroup(j);
        }
        {
            if ((LidGroups != null))
            {
                R.free(LidGroups);
                LidGroups = (null);
            }
        }
        ;
        for (j = 0; (j < LidCount); j++)
        {
            if ((LidProcs[j].drainRmvl != null))
            {
                R.free(LidProcs[j].drainRmvl);
                LidProcs[j].drainRmvl = (null);
            }
        }
        ;
        {
            if ((LidProcs != null))
            {
                R.free(LidProcs);
                LidProcs = (null);
            }
        }
        ;
        GroupCount = 0;
        LidCount = 0;
    }

    // lid.c:322
    static void freeLidGroup(int j)
    {
        LidGroup lidGroup = LidGroups[j];
        TLidList lidList = null;
        TLidUnit lidUnit = null;
        TLidList nextLidUnit = null;
        if ((lidGroup == null))
        {
            return;
        }
        lidList = lidGroup.lidList;
        while ((lidList != null))
        {
            lidUnit = lidList.lidUnit;
            if ((lidUnit.rptFile != null))
            {
                if ((lidUnit.rptFile.file != null))
                {
                    R.fclose(lidUnit.rptFile.file);
                }
                R.free(lidUnit.rptFile);
            }
            nextLidUnit = lidList.nextLidUnit;
            R.free(lidUnit);
            R.free(lidList);
            lidList = nextLidUnit;
        }
        R.free(lidGroup);
        LidGroups[j] = (null);
    }

    // lid.c:355
    static int lid_readProcParams(CPtr[] toks, int ntoks)
    {
        int j = 0;
        int m = 0;
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(LID, toks[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, toks[0]);
        }
        if ((LidProcs[j].ID == null))
        {
            LidProcs[j].ID = project_findID(LID, toks[0]);
        }
        m = findmatch(toks[1], LidTypeWords);
        if ((m >= 0))
        {
            LidProcs[j].lidType = m;
            return 0;
        }
        else
        {
            m = findmatch(toks[1], LidLayerWords);
        }
        switch (m) {
            case SURF:
                return readSurfaceData(j, toks, ntoks);
            case SOIL:
                return readSoilData(j, toks, ntoks);
            case STOR:
                return readStorageData(j, toks, ntoks);
            case PAVE:
                return readPavementData(j, toks, ntoks);
            case 5:
                return readDrainData(j, toks, ntoks);
            case DRAINMAT:
                return readDrainMatData(j, toks, ntoks);
            case REMOVALS:
                return readRemovalsData(j, toks, ntoks);
        }
        return error_setInpError(ERR_KEYWORD, toks[1]);
    }

    // lid.c:415
    static int lid_readGroupParams(CPtr[] toks, int ntoks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int n = 0;
        double[] x = new double[6];
        CPtr fname = null;
        int drainSubcatch = (-1);
        int drainNode = (-1);
        if ((ntoks < 8))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(SUBCATCH, toks[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, toks[0]);
        }
        k = project_findObject(LID, toks[1]);
        if ((k < 0))
        {
            return error_setInpError(ERR_NAME, toks[1]);
        }
        n = R.atoi(toks[2]);
        if ((n < 0))
        {
            return error_setInpError(ERR_NUMBER, toks[2]);
        }
        if ((n == 0))
        {
            return 0;
        }
        for (i = 3; (i <= 7); i++)
        {
            if (((!(getDouble(toks[i], new DElem(x, (i - 3))) != 0)) || (x[(i - 3)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
        }
        for (i = 2; (i <= 3); i++)
        {
            if ((x[i] > 100.0))
            {
                return error_setInpError(ERR_NUMBER, toks[(i + 3)]);
            }
        }
        if (((ntoks >= 9) && (R.strcmp(toks[8], L275) != 0)))
        {
            fname = toks[8];
        }
        if (((ntoks >= 10) && (R.strcmp(toks[9], L275) != 0)))
        {
            drainSubcatch = project_findObject(SUBCATCH, toks[9]);
            if ((drainSubcatch < 0))
            {
                drainNode = project_findObject(NODE, toks[9]);
                if ((drainNode < 0))
                {
                    return error_setInpError(ERR_NAME, toks[9]);
                }
            }
        }
        x[5] = 0.0;
        if ((ntoks >= 11))
        {
            if ((((!(getDouble(toks[10], new DElem(x, 5)) != 0)) || (x[5] < 0.0)) || (x[5] > 100.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[10]);
            }
        }
        return addLidUnit(j, k, n, x, fname, drainSubcatch, drainNode);
    }

    // lid.c:499
    static int addLidUnit(int j, int k, int n, double[] x, CPtr fname, int drainSubcatch, int drainNode)
    {
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        lidGroup = LidGroups[j];
        if (!(lidGroup != null))
        {
            lidGroup = new LidGroup();
            if (!(lidGroup != null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
            lidGroup.lidList = (null);
            LidGroups[j] = lidGroup;
        }
        lidUnit = new TLidUnit();
        if (!(lidUnit != null))
        {
            return error_setInpError(ERR_MEMORY, L0);
        }
        lidUnit.rptFile = (null);
        lidList = new TLidList();
        if (!(lidList != null))
        {
            R.free(lidUnit);
            return error_setInpError(ERR_MEMORY, L0);
        }
        lidList.lidUnit = lidUnit;
        lidList.nextLidUnit = lidGroup.lidList;
        lidGroup.lidList = lidList;
        lidUnit.lidIndex = k;
        lidUnit.number = n;
        lidUnit.area = (x[0] / (((UCF(LENGTH)) * (UCF(LENGTH)))));
        lidUnit.fullWidth = (x[1] / UCF(LENGTH));
        lidUnit.initSat = (x[2] / 100.0);
        lidUnit.fromImperv = (x[3] / 100.0);
        lidUnit.toPerv = (((x[4] > 0.0)) ? 1 : 0);
        lidUnit.fromPerv = (x[5] / 100.0);
        lidUnit.drainSubcatch = drainSubcatch;
        lidUnit.drainNode = drainNode;
        if ((fname != null))
        {
            if (!(createLidRptFile(lidUnit, fname) != 0))
            {
                return error_setInpError(ERR_RPT_FILE, fname);
            }
        }
        return 0;
    }

    // lid.c:567
    static int createLidRptFile(TLidUnit lidUnit, CPtr fname)
    {
        TLidRptFile rptFile = null;
        rptFile = new TLidRptFile();
        if ((rptFile == null))
        {
            return 0;
        }
        lidUnit.rptFile = rptFile;
        rptFile.file = R.fopen(fname, L171);
        if ((rptFile.file == null))
        {
            return 0;
        }
        return 1;
    }

    // lid.c:581
    static int readSurfaceData(int j, CPtr[] toks, int ntoks)
    {
        int i = 0;
        double[] x = new double[5];
        if ((ntoks < 7))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 2; (i < 7); i++)
        {
            if (((!(getDouble(toks[i], new DElem(x, (i - 2))) != 0)) || (x[(i - 2)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
        }
        if ((x[1] >= 1.0))
        {
            return error_setInpError(ERR_NUMBER, toks[3]);
        }
        if ((x[0] == 0.0))
        {
            x[1] = 0.0;
        }
        LidProcs[j].surface.thickness = (x[0] / UCF(RAINDEPTH));
        LidProcs[j].surface.voidFrac = (1.0 - x[1]);
        LidProcs[j].surface.roughness = x[2];
        LidProcs[j].surface.surfSlope = (x[3] / 100.0);
        LidProcs[j].surface.sideSlope = x[4];
        return 0;
    }

    // lid.c:616
    static int readPavementData(int j, CPtr[] toks, int ntoks)
    {
        int i = 0;
        double[] x = new double[7];
        if ((ntoks < 7))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 2; (i < 7); i++)
        {
            if (((!(getDouble(toks[i], new DElem(x, (i - 2))) != 0)) || (x[(i - 2)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
        }
        x[5] = 0.0;
        if ((ntoks > 7))
        {
            if (((!(getDouble(toks[7], new DElem(x, 5)) != 0)) || (x[5] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[7]);
            }
        }
        x[6] = 0.0;
        if ((ntoks > 8))
        {
            if ((((!(getDouble(toks[8], new DElem(x, 6)) != 0)) || (x[6] < 0.0)) || (x[6] > 1.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[8]);
            }
        }
        x[1] = (x[1] / ((x[1] + 1.0)));
        LidProcs[j].pavement.thickness = (x[0] / UCF(RAINDEPTH));
        LidProcs[j].pavement.voidFrac = x[1];
        LidProcs[j].pavement.impervFrac = x[2];
        LidProcs[j].pavement.kSat = (x[3] / UCF(RAINFALL));
        LidProcs[j].pavement.clogFactor = x[4];
        LidProcs[j].pavement.regenDays = x[5];
        LidProcs[j].pavement.regenDegree = x[6];
        return 0;
    }

    // lid.c:669
    static int readSoilData(int j, CPtr[] toks, int ntoks)
    {
        int i = 0;
        double[] x = new double[7];
        if ((ntoks < 9))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 2; (i < 9); i++)
        {
            if (((!(getDouble(toks[i], new DElem(x, (i - 2))) != 0)) || (x[(i - 2)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
        }
        LidProcs[j].soil.thickness = (x[0] / UCF(RAINDEPTH));
        LidProcs[j].soil.porosity = x[1];
        LidProcs[j].soil.fieldCap = x[2];
        LidProcs[j].soil.wiltPoint = x[3];
        LidProcs[j].soil.kSat = (x[4] / UCF(RAINFALL));
        LidProcs[j].soil.kSlope = x[5];
        LidProcs[j].soil.suction = (x[6] / UCF(RAINDEPTH));
        return 0;
    }

    // lid.c:703
    static int readStorageData(int j, CPtr[] toks, int ntoks)
    {
        int i = 0;
        int covered = 0;
        double[] x = new double[6];
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 2; (i < 6); i++)
        {
            if (((!(getDouble(toks[i], new DElem(x, (i - 2))) != 0)) || (x[(i - 2)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
        }
        if ((ntoks > 6))
        {
            if ((match(toks[6], L5) != 0))
            {
                covered = 1;
            }
        }
        x[1] = (x[1] / ((x[1] + 1.0)));
        LidProcs[j].storage.thickness = (x[0] / UCF(RAINDEPTH));
        LidProcs[j].storage.voidFrac = x[1];
        LidProcs[j].storage.kSat = (x[2] / UCF(RAINFALL));
        LidProcs[j].storage.clogFactor = x[3];
        LidProcs[j].storage.covered = covered;
        return 0;
    }

    // lid.c:749
    static int readDrainData(int j, CPtr[] toks, int ntoks)
    {
        int i = 0;
        double[] x = new double[6];
        if ((ntoks < 6))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 0; (i < 6); i++)
        {
            x[i] = 0.0;
        }
        for (i = 2; (i < 8); i++)
        {
            if ((((ntoks > i)) && (((!(getDouble(toks[i], new DElem(x, (i - 2))) != 0)) || (x[(i - 2)] < 0.0)))))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
        }
        i = (-1);
        if ((ntoks >= 9))
        {
            i = project_findObject(CURVE, toks[8]);
            if ((i < 0))
            {
                return error_setInpError(ERR_NAME, toks[8]);
            }
        }
        LidProcs[j].drain.coeff = x[0];
        LidProcs[j].drain.expon = x[1];
        LidProcs[j].drain.offset = (x[2] / UCF(RAINDEPTH));
        LidProcs[j].drain.delay = (x[3] * 3600.0);
        LidProcs[j].drain.hOpen = (x[4] / UCF(RAINDEPTH));
        LidProcs[j].drain.hClose = (x[5] / UCF(RAINDEPTH));
        LidProcs[j].drain.qCurve = i;
        return 0;
    }

    // lid.c:794
    static int readDrainMatData(int j, CPtr[] toks, int ntoks)
    {
        int i = 0;
        double[] x = new double[3];
        if ((ntoks < 5))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        if ((LidProcs[j].lidType != GREEN_ROOF))
        {
            return 0;
        }
        for (i = 2; (i < 5); i++)
        {
            if (((!(getDouble(toks[i], new DElem(x, (i - 2))) != 0)) || (x[(i - 2)] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
        }
        LidProcs[j].drainMat.thickness = (x[0] / UCF(RAINDEPTH));
        ;
        LidProcs[j].drainMat.voidFrac = x[1];
        LidProcs[j].drainMat.roughness = x[2];
        return 0;
    }

    // lid.c:828
    static int readRemovalsData(int j, CPtr[] toks, int ntoks)
    {
        int i = 2;
        int p = 0;
        DBox rmvl = new DBox(0.0);
        if ((ntoks < 4))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        while ((ntoks > i))
        {
            p = project_findObject(POLLUT, toks[i]);
            if ((p < 0))
            {
                return error_setInpError(ERR_NAME, toks[i]);
            }
            i++;
            if ((ntoks == i))
            {
                return error_setInpError(ERR_ITEMS, L0);
            }
            if ((((!(getDouble(toks[i], rmvl) != 0)) || (rmvl.v < 0.0)) || (rmvl.v > 100.0)))
            {
                return error_setInpError(ERR_NUMBER, toks[i]);
            }
            LidProcs[j].drainRmvl[p] = (rmvl.v / 100.0);
            i++;
        }
        return 0;
    }

    // lid.c:869
    static void lid_writeSummary()
    {
        int j = 0;
        int k = 0;
        double pctArea = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        R.fprintf(Frpt.file, L276);
        R.fprintf(Frpt.file, L276);
        R.fprintf(Frpt.file, L277);
        R.fprintf(Frpt.file, L278);
        R.fprintf(Frpt.file, L277);
        R.fprintf(Frpt.file, L279);
        R.fprintf(Frpt.file, L280);
        R.fprintf(Frpt.file, L281);
        for (j = 0; (j < GroupCount); j++)
        {
            lidGroup = LidGroups[j];
            if ((lidGroup == null))
            {
                continue;
            }
            lidList = lidGroup.lidList;
            while ((lidList != null))
            {
                lidUnit = lidList.lidUnit;
                k = lidUnit.lidIndex;
                pctArea = (((lidUnit.area * lidUnit.number) / Subcatch[j].area) * 100.0);
                R.fprintf(Frpt.file, L282, Subcatch[j].ID, LidProcs[k].ID);
                R.fprintf(Frpt.file, L283, lidUnit.number, (lidUnit.area * (((UCF(LENGTH)) * (UCF(LENGTH))))), (lidUnit.fullWidth * UCF(LENGTH)), pctArea, (lidUnit.fromImperv * 100.0), (lidUnit.fromPerv * 100.0));
                lidList = lidList.nextLidUnit;
            }
        }
    }

    // lid.c:918
    static void lid_validate()
    {
        int j = 0;
        for (j = 0; (j < LidCount); j++)
        {
            validateLidProc(j);
        }
        for (j = 0; (j < GroupCount); j++)
        {
            validateLidGroup(j);
        }
    }

    // lid.c:932
    static void validateLidProc(int j)
    {
        int layerMissing = 0;
        if ((LidProcs[j].lidType < 0))
        {
            report_writeErrorMsg(ERR_LID_TYPE, LidProcs[j].ID);
            return;
        }
        switch (LidProcs[j].lidType) {
            case BIO_CELL:
            case RAIN_GARDEN:
                if ((LidProcs[j].soil.thickness <= 0.0))
                {
                    layerMissing = 1;
                }
                break;
            case GREEN_ROOF:
                if ((LidProcs[j].soil.thickness <= 0.0))
                {
                    layerMissing = 1;
                }
                if ((LidProcs[j].drainMat.thickness <= 0.0))
                {
                    layerMissing = 1;
                }
                break;
            case POROUS_PAVEMENT:
                if ((LidProcs[j].pavement.thickness <= 0.0))
                {
                    layerMissing = 1;
                }
                break;
            case INFIL_TRENCH:
                if ((LidProcs[j].storage.thickness <= 0.0))
                {
                    layerMissing = 1;
                }
                break;
        }
        if ((layerMissing != 0))
        {
            report_writeErrorMsg(ERR_LID_LAYER, LidProcs[j].ID);
            return;
        }
        if ((LidProcs[j].lidType == POROUS_PAVEMENT))
        {
            if ((((((LidProcs[j].pavement.thickness <= 0.0) || (LidProcs[j].pavement.kSat <= 0.0)) || (LidProcs[j].pavement.voidFrac <= 0.0)) || (LidProcs[j].pavement.voidFrac > 1.0)) || (LidProcs[j].pavement.impervFrac > 1.0)))
            {
                sstrncpy(Msg, LidProcs[j].ID, 1024);
                sstrcat(Msg, L284, 1024);
                report_writeErrorMsg(ERR_LID_PARAMS, Msg);
            }
        }
        if ((LidProcs[j].soil.thickness > 0.0))
        {
            if ((((((LidProcs[j].soil.porosity <= 0.0) || (LidProcs[j].soil.fieldCap >= LidProcs[j].soil.porosity)) || (LidProcs[j].soil.wiltPoint >= LidProcs[j].soil.fieldCap)) || (LidProcs[j].soil.kSat <= 0.0)) || (LidProcs[j].soil.kSlope < 0.0)))
            {
                sstrncpy(Msg, LidProcs[j].ID, 1024);
                sstrcat(Msg, L285, 1024);
                report_writeErrorMsg(ERR_LID_PARAMS, Msg);
            }
        }
        if ((LidProcs[j].storage.thickness > 0.0))
        {
            if (((LidProcs[j].storage.voidFrac <= 0.0) || (LidProcs[j].storage.voidFrac > 1.0)))
            {
                sstrncpy(Msg, LidProcs[j].ID, 1024);
                sstrcat(Msg, L286, 1024);
                report_writeErrorMsg(ERR_LID_PARAMS, Msg);
            }
        }
        else
        {
            LidProcs[j].storage.voidFrac = 1.0;
            LidProcs[j].drain.offset = 0.0;
        }
        if (((LidProcs[j].drain.hOpen > 0.0) && (LidProcs[j].drain.hOpen <= LidProcs[j].drain.hClose)))
        {
            sstrncpy(Msg, LidProcs[j].ID, 1024);
            sstrcat(Msg, L287, 1024);
            report_writeErrorMsg(ERR_LID_PARAMS, Msg);
        }
        if ((LidProcs[j].lidType == VEG_SWALE))
        {
            if ((((LidProcs[j].surface.roughness * LidProcs[j].surface.surfSlope) <= 0.0) || (LidProcs[j].surface.thickness == 0.0)))
            {
                sstrncpy(Msg, LidProcs[j].ID, 1024);
                sstrcat(Msg, L288, 1024);
                report_writeErrorMsg(ERR_LID_PARAMS, Msg);
            }
            else
            {
                LidProcs[j].surface.alpha = ((1.49 * Math.sqrt(LidProcs[j].surface.surfSlope)) / LidProcs[j].surface.roughness);
            }
        }
        else
        {
            if ((LidProcs[j].surface.roughness > 0.0))
            {
                LidProcs[j].surface.alpha = ((1.49 / LidProcs[j].surface.roughness) * Math.sqrt(LidProcs[j].surface.surfSlope));
            }
            else
            {
                LidProcs[j].surface.alpha = 0.0;
            }
        }
        if ((LidProcs[j].drainMat.roughness > 0.0))
        {
            LidProcs[j].drainMat.alpha = ((1.49 / LidProcs[j].drainMat.roughness) * Math.sqrt(LidProcs[j].surface.surfSlope));
        }
        else
        {
            LidProcs[j].drainMat.alpha = 0.0;
        }
        if ((LidProcs[j].pavement.thickness > 0.0))
        {
            LidProcs[j].pavement.clogFactor *= ((LidProcs[j].pavement.thickness * LidProcs[j].pavement.voidFrac) * ((1.0 - LidProcs[j].pavement.impervFrac)));
        }
        if ((LidProcs[j].storage.thickness > 0.0))
        {
            LidProcs[j].storage.clogFactor *= (LidProcs[j].storage.thickness * LidProcs[j].storage.voidFrac);
        }
        else
        {
            LidProcs[j].storage.clogFactor = 0.0;
        }
        LidProcs[j].surface.canOverflow = 1;
        switch (LidProcs[j].lidType) {
            case ROOF_DISCON:
                LidProcs[j].surface.canOverflow = 0;
                break;
            case INFIL_TRENCH:
            case POROUS_PAVEMENT:
            case BIO_CELL:
            case RAIN_GARDEN:
            case GREEN_ROOF:
                if ((LidProcs[j].surface.alpha > 0.0))
                {
                    LidProcs[j].surface.canOverflow = 0;
                }
        }
        if ((LidProcs[j].lidType == RAIN_BARREL))
        {
            LidProcs[j].storage.voidFrac = 1.0;
            LidProcs[j].storage.kSat = 0.0;
        }
        if ((LidProcs[j].lidType == GREEN_ROOF))
        {
            LidProcs[j].storage.thickness = LidProcs[j].drainMat.thickness;
            LidProcs[j].storage.voidFrac = LidProcs[j].drainMat.voidFrac;
            LidProcs[j].storage.clogFactor = 0.0;
            LidProcs[j].storage.kSat = 0.0;
        }
    }

    // lid.c:1113
    static void validateLidGroup(int j)
    {
        int k = 0;
        double[] p = new double[3];
        double totalArea = Subcatch[j].area;
        double totalLidArea = 0.0;
        double fromImperv = 0.0;
        double fromPerv = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        lidGroup = LidGroups[j];
        if ((lidGroup == null))
        {
            return;
        }
        lidList = lidGroup.lidList;
        while ((lidList != null))
        {
            lidUnit = lidList.lidUnit;
            k = lidUnit.lidIndex;
            totalLidArea += ((lidUnit.area * lidUnit.number));
            fromImperv += lidUnit.fromImperv;
            fromPerv += lidUnit.fromPerv;
            lidUnit.soilInfil.Ks = 0.0;
            if ((LidProcs[k].soil.thickness > 0.0))
            {
                p[0] = (LidProcs[k].soil.suction * UCF(RAINDEPTH));
                p[1] = (LidProcs[k].soil.kSat * UCF(RAINFALL));
                p[2] = (((LidProcs[k].soil.porosity - LidProcs[k].soil.wiltPoint)) * ((1.0 - lidUnit.initSat)));
                if ((grnampt_setParams(lidUnit.soilInfil, p) == 0))
                {
                    sstrncpy(Msg, LidProcs[k].ID, 1024);
                    sstrcat(Msg, L285, 1024);
                    report_writeErrorMsg(ERR_LID_PARAMS, Msg);
                }
            }
            if ((LidProcs[k].lidType == VEG_SWALE))
            {
                if (((Subcatch[j].infilModel == GREEN_AMPT) || (Subcatch[j].infilModel == MOD_GREEN_AMPT)))
                {
                    grnampt_getParams(j, p);
                    if ((grnampt_setParams(lidUnit.soilInfil, p) == 0))
                    {
                        sstrncpy(Msg, LidProcs[k].ID, 1024);
                        sstrcat(Msg, L289, 1024);
                        report_writeErrorMsg(ERR_LID_PARAMS, Msg);
                    }
                }
                if ((lidUnit.fullWidth <= 0.0))
                {
                    sstrncpy(Msg, LidProcs[k].ID, 1024);
                    sstrcat(Msg, L290, 1024);
                    report_writeErrorMsg(ERR_LID_PARAMS, Msg);
                }
            }
            if ((Subcatch[j].fracImperv >= 0.999))
            {
                lidUnit.toPerv = 0;
            }
            if (((lidUnit.drainNode == (-1)) && (lidUnit.drainSubcatch == (-1))))
            {
                lidUnit.drainNode = Subcatch[j].outNode;
                lidUnit.drainSubcatch = Subcatch[j].outSubcatch;
            }
            lidList = lidList.nextLidUnit;
        }
        if ((totalLidArea > (1.001 * totalArea)))
        {
            report_writeErrorMsg(ERR_LID_AREAS, Subcatch[j].ID);
        }
        if (((fromImperv > 1.001) || (fromPerv > 1.001)))
        {
            report_writeErrorMsg(ERR_LID_CAPTURE_AREA, Subcatch[j].ID);
        }
        if ((totalLidArea > (0.999 * totalArea)))
        {
            totalLidArea = totalArea;
        }
        Subcatch[j].lidArea = totalLidArea;
    }

    // lid.c:1211
    static void lid_initState()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        double initVol = 0.0;
        double initDryTime = (StartDryDays * 86400);
        HasWetLids = 0;
        for (j = 0; (j < GroupCount); j++)
        {
            lidGroup = LidGroups[j];
            if ((lidGroup == null))
            {
                continue;
            }
            lidGroup.pervArea = 0.0;
            lidGroup.flowToPerv = 0.0;
            lidGroup.oldDrainFlow = 0.0;
            lidGroup.newDrainFlow = 0.0;
            lidList = lidGroup.lidList;
            while ((lidList != null))
            {
                lidUnit = lidList.lidUnit;
                k = lidUnit.lidIndex;
                lidUnit.surfaceDepth = 0.0;
                lidUnit.storageDepth = 0.0;
                lidUnit.soilMoisture = 0.0;
                lidUnit.paveDepth = 0.0;
                lidUnit.dryTime = initDryTime;
                lidUnit.volTreated = 0.0;
                lidUnit.nextRegenDay = LidProcs[k].pavement.regenDays;
                initVol = 0.0;
                if ((LidProcs[k].soil.thickness > 0.0))
                {
                    lidUnit.soilMoisture = (LidProcs[k].soil.wiltPoint + (lidUnit.initSat * ((LidProcs[k].soil.porosity - LidProcs[k].soil.wiltPoint))));
                    initVol += (lidUnit.soilMoisture * LidProcs[k].soil.thickness);
                }
                if ((LidProcs[k].storage.thickness > 0.0))
                {
                    lidUnit.storageDepth = (lidUnit.initSat * LidProcs[k].storage.thickness);
                    initVol += (lidUnit.storageDepth * LidProcs[k].storage.voidFrac);
                }
                if ((lidUnit.initSat > 0.0))
                {
                    HasWetLids = 1;
                }
                lidproc_initWaterBalance(lidUnit, initVol);
                lidUnit.volTreated = 0.0;
                if ((lidUnit.rptFile != null))
                {
                    initLidRptFile(Title[0], LidProcs[k].ID, Subcatch[j].ID, lidUnit);
                }
                lidUnit.oldDrainFlow = 0.0;
                lidUnit.newDrainFlow = 0.0;
                for (i = 0; (i < 4); i++)
                {
                    lidUnit.oldFluxRates[i] = 0.0;
                }
                if ((lidUnit.soilInfil.Ks > 0.0))
                {
                    grnampt_initState(lidUnit.soilInfil);
                }
                if ((isLidPervious(lidUnit.lidIndex) != 0))
                {
                    lidGroup.pervArea += ((lidUnit.area * lidUnit.number));
                }
                lidList = lidList.nextLidUnit;
            }
        }
    }

    // lid.c:1302
    static void lid_setOldGroupState(int j)
    {
        TLidList lidList = null;
        if ((LidGroups[j] != null))
        {
            LidGroups[j].oldDrainFlow = LidGroups[j].newDrainFlow;
            LidGroups[j].newDrainFlow = 0.0;
            lidList = LidGroups[j].lidList;
            while ((lidList != null))
            {
                lidList.lidUnit.oldDrainFlow = lidList.lidUnit.newDrainFlow;
                lidList.lidUnit.newDrainFlow = 0.0;
                lidList = lidList.nextLidUnit;
            }
        }
    }

    // lid.c:1326
    static int isLidPervious(int k)
    {
        return ((((LidProcs[k].storage.thickness == 0.0) || (LidProcs[k].storage.kSat > 0.0))) ? 1 : 0);
    }

    // lid.c:1339
    static double getSurfaceDepth(int j)
    {
        int k = 0;
        double depth = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        lidGroup = LidGroups[j];
        if ((lidGroup == null))
        {
            return 0.0;
        }
        if ((Subcatch[j].lidArea == 0.0))
        {
            return 0.0;
        }
        lidList = lidGroup.lidList;
        while ((lidList != null))
        {
            lidUnit = lidList.lidUnit;
            k = lidUnit.lidIndex;
            depth += (((lidUnit.surfaceDepth * LidProcs[k].surface.voidFrac) * lidUnit.area) * lidUnit.number);
            lidList = lidList.nextLidUnit;
        }
        return (depth / Subcatch[j].lidArea);
    }

    // lid.c:1370
    static double lid_getPervArea(int j)
    {
        if ((LidGroups[j] != null))
        {
            return LidGroups[j].pervArea;
        }
        else
        {
            return 0.0;
        }
    }

    // lid.c:1383
    static double lid_getFlowToPerv(int j)
    {
        if ((LidGroups[j] != null))
        {
            return LidGroups[j].flowToPerv;
        }
        return 0.0;
    }

    // lid.c:1397
    static double lid_getStoredVolume(int j)
    {
        double total = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        lidGroup = LidGroups[j];
        if (((lidGroup == null) || (Subcatch[j].lidArea == 0.0)))
        {
            return 0.0;
        }
        lidList = lidGroup.lidList;
        while ((lidList != null))
        {
            lidUnit = lidList.lidUnit;
            total += ((lidUnit.waterBalance.finalVol * lidUnit.area) * lidUnit.number);
            lidList = lidList.nextLidUnit;
        }
        return total;
    }

    // lid.c:1424
    static double lid_getDrainFlow(int j, int timePeriod)
    {
        if ((LidGroups[j] != null))
        {
            if ((timePeriod == PREVIOUS))
            {
                return LidGroups[j].oldDrainFlow;
            }
            else
            {
                return LidGroups[j].newDrainFlow;
            }
        }
        return 0.0;
    }

    // lid.c:1442
    static void lid_addDrainLoads(int j, double[] c, double tStep)
    {
        int isRunoffLoad = 0;
        int p = 0;
        double r = 0.0;
        double w = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        lidGroup = LidGroups[j];
        if ((lidGroup != null))
        {
            lidList = lidGroup.lidList;
            while ((lidList != null))
            {
                lidUnit = lidList.lidUnit;
                isRunoffLoad = ((((lidUnit.drainNode >= 0) || (lidUnit.drainSubcatch == j))) ? 1 : 0);
                if (!(lidUnit.toPerv != 0))
                {
                    for (p = 0; (p < Nobjects[POLLUT]); p++)
                    {
                        w = ((((lidUnit.newDrainFlow * c[p]) * tStep) * 28.317) * Pollut[p].mcf);
                        r = LidProcs[lidUnit.lidIndex].drainRmvl[p];
                        massbal_updateLoadingTotals(BMP_REMOVAL_LOAD, p, (r * w));
                        if ((isRunoffLoad != 0))
                        {
                            massbal_updateLoadingTotals(RUNOFF_LOAD, p, (w * ((1.0 - r))));
                        }
                    }
                }
                lidList = lidList.nextLidUnit;
            }
        }
    }

    // lid.c:1497
    static void lid_addDrainRunon(int j)
    {
        int i = 0;
        int k = 0;
        int p = 0;
        double q = 0.0;
        double w = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        lidGroup = LidGroups[j];
        if ((lidGroup != null))
        {
            lidList = lidGroup.lidList;
            while ((lidList != null))
            {
                lidUnit = lidList.lidUnit;
                i = lidUnit.lidIndex;
                k = lidUnit.drainSubcatch;
                if (((k >= 0) && (k != j)))
                {
                    q = lidUnit.oldDrainFlow;
                    subcatch_addRunonFlow(k, q);
                    for (p = 0; (p < Nobjects[POLLUT]); p++)
                    {
                        w = ((q * Subcatch[j].oldQual[p]) * 28.317);
                        w = (w * ((1.0 - LidProcs[i].drainRmvl[p])));
                        Subcatch[k].newQual[p] += w;
                    }
                }
                lidList = lidList.nextLidUnit;
            }
        }
    }

    // lid.c:1549
    static void lid_addDrainInflow(int j, double f)
    {
        int i = 0;
        int k = 0;
        int p = 0;
        double q = 0.0;
        double w = 0.0;
        double w1 = 0.0;
        double w2 = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        lidGroup = LidGroups[j];
        if ((lidGroup != null))
        {
            lidList = lidGroup.lidList;
            while ((lidList != null))
            {
                lidUnit = lidList.lidUnit;
                i = lidUnit.lidIndex;
                k = lidUnit.drainNode;
                if ((k >= 0))
                {
                    q = ((((1.0 - f)) * lidUnit.oldDrainFlow) + (f * lidUnit.newDrainFlow));
                    Node[k].newLatFlow += q;
                    massbal_addInflowFlow(WET_WEATHER_INFLOW, q);
                    for (p = 0; (p < Nobjects[POLLUT]); p++)
                    {
                        w1 = (lidUnit.oldDrainFlow * Subcatch[j].oldQual[p]);
                        w2 = (lidUnit.newDrainFlow * Subcatch[j].newQual[p]);
                        w = ((((1.0 - f)) * w1) + (f * w2));
                        w = (w * ((1.0 - LidProcs[i].drainRmvl[p])));
                        Node[k].newQual[p] += w;
                        massbal_addInflowQual(WET_WEATHER_INFLOW, p, w);
                    }
                }
                lidList = lidList.nextLidUnit;
            }
        }
    }

    // lid.c:1609
    static void lid_getRunoff(int j, double tStep)
    {
        LidGroup theLidGroup = null;
        TLidList lidList = null;
        TLidUnit lidUnit = null;
        double lidArea = 0.0;
        double qImperv = 0.0;
        double qPerv = 0.0;
        double lidInflow = 0.0;
        DBox qRunoff = new DBox(0.0);
        DBox qDrain = new DBox(0.0);
        DBox qReturn = new DBox(0.0);
        theLidGroup = LidGroups[j];
        if (!(theLidGroup != null))
        {
            return;
        }
        lidList = theLidGroup.lidList;
        if (!(lidList != null))
        {
            return;
        }
        lid_EvapRate = Evap.rate;
        if (((Evap.dryOnly != 0) && (Subcatch[j].rainfall > 0.0)))
        {
            lid_EvapRate = 0.0;
        }
        findNativeInfil(j, tStep);
        if ((Subcatch[j].area > Subcatch[j].lidArea))
        {
            qImperv = getImpervAreaRunoff(j);
            qPerv = getPervAreaRunoff(j);
        }
        while ((lidList != null))
        {
            lidUnit = lidList.lidUnit;
            lidArea = (lidUnit.area * lidUnit.number);
            if ((lidArea > 0.0))
            {
                lidInflow = ((((qImperv * lidUnit.fromImperv) + (qPerv * lidUnit.fromPerv))) / lidArea);
                VlidIn += ((lidInflow * lidArea) * tStep);
                lidInflow = (lidInflow + getRainInflow(j, lidUnit));
                if ((Subcatch[j].area == Subcatch[j].lidArea))
                {
                    lidInflow += Subcatch[j].runon;
                }
                evalLidUnit(j, lidUnit, lidArea, lidInflow, tStep, qRunoff, qDrain, qReturn);
            }
            lidList = lidList.nextLidUnit;
        }
        theLidGroup.newDrainFlow = qDrain.v;
        theLidGroup.flowToPerv = qReturn.v;
        VlidOut = (qRunoff.v * tStep);
        VlidDrain = (qDrain.v * tStep);
        VlidReturn = (qReturn.v * tStep);
    }

    // lid.c:1697
    static void findNativeInfil(int j, double tStep)
    {
        double nonLidArea = 0.0;
        nonLidArea = (Subcatch[j].area - Subcatch[j].lidArea);
        if (((nonLidArea > 0.0) && (Subcatch[j].fracImperv < 1.0)))
        {
            NativeInfil = ((Vinfil / nonLidArea) / tStep);
        }
        else
        {
            NativeInfil = infil_getInfil(j, tStep, Subcatch[j].rainfall, Subcatch[j].runon, getSurfaceDepth(j));
        }
        if (((!(IgnoreGwater != 0)) && (Subcatch[j].groundwater != null)))
        {
            lid_MaxNativeInfil = (Subcatch[j].groundwater.maxInfilVol / tStep);
        }
        else
        {
            lid_MaxNativeInfil = 10000000000.0;
        }
    }

    // lid.c:1734
    static double getRainInflow(int j, TLidUnit lidUnit)
    {
        TLidProc lidProc = LidProcs[lidUnit.lidIndex];
        if (((lidProc.lidType == RAIN_BARREL) && (lidProc.storage.covered == 1)))
        {
            return 0.0;
        }
        return Subcatch[j].rainfall;
    }

    // lid.c:1751
    static double getImpervAreaRunoff(int j)
    {
        int i = 0;
        double q = 0.0;
        double nonLidArea = 0.0;
        for (i = IMPERV0; (i <= IMPERV1); i++)
        {
            q += (Subcatch[j].subArea[i].runoff * Subcatch[j].subArea[i].fArea);
        }
        if (((Subcatch[j].subArea[IMPERV0].routeTo == TO_PERV) && (Subcatch[j].fracImperv < 1.0)))
        {
            q *= Subcatch[j].subArea[IMPERV0].fOutlet;
        }
        nonLidArea = (Subcatch[j].area - Subcatch[j].lidArea);
        return (q * nonLidArea);
    }

    // lid.c:1781
    static double getPervAreaRunoff(int j)
    {
        double q = 0.0;
        double nonLidArea = 0.0;
        q = (Subcatch[j].subArea[PERV].runoff * Subcatch[j].subArea[PERV].fArea);
        if (((Subcatch[j].subArea[PERV].routeTo == TO_IMPERV) && (Subcatch[j].fracImperv > 0.0)))
        {
            q *= Subcatch[j].subArea[PERV].fOutlet;
        }
        nonLidArea = (Subcatch[j].area - Subcatch[j].lidArea);
        return (q * nonLidArea);
    }

    // lid.c:1807
    static void evalLidUnit(int j, TLidUnit lidUnit, double lidArea, double lidInflow, double tStep, DRef qRunoff, DRef qDrain, DRef qReturn)
    {
        TLidProc lidProc = null;
        double lidRunoff = 0.0;
        DBox lidEvap = new DBox(0.0);
        DBox lidInfil = new DBox(0.0);
        DBox lidDrain = new DBox(0.0);
        lidProc = LidProcs[lidUnit.lidIndex];
        lidEvap.v = 0.0;
        lidInfil.v = 0.0;
        lidRunoff = (lidproc_getOutflow(lidUnit, lidProc, lidInflow, lid_EvapRate, NativeInfil, lid_MaxNativeInfil, tStep, lidEvap, lidInfil, lidDrain) * lidArea);
        lidDrain.v *= lidArea;
        if (((lidUnit.toPerv != 0) && (Subcatch[j].area > Subcatch[j].lidArea)))
        {
            qReturn.set((qReturn.get() + lidRunoff));
            lidRunoff = 0.0;
            if (((lidUnit.drainNode == Subcatch[j].outNode) && (lidUnit.drainSubcatch == Subcatch[j].outSubcatch)))
            {
                qReturn.set((qReturn.get() + lidDrain.v));
                lidDrain.v = 0.0;
            }
        }
        if ((lidUnit.drainNode >= 0))
        {
            massbal_updateRunoffTotals(RUNOFF_DRAINS, (lidDrain.v * tStep));
        }
        lidUnit.newDrainFlow = lidDrain.v;
        Vevap += ((lidEvap.v * tStep) * lidArea);
        VlidInfil += ((lidInfil.v * tStep) * lidArea);
        if ((isLidPervious(lidUnit.lidIndex) != 0))
        {
            Vpevap += ((lidEvap.v * tStep) * lidArea);
        }
        if ((Subcatch[j].rainfall > 2.31481e-08))
        {
            lidUnit.dryTime = 0.0;
        }
        else
        {
            lidUnit.dryTime += tStep;
        }
        lidproc_saveResults(lidUnit, UCF(RAINFALL), UCF(RAINDEPTH));
        qRunoff.set((qRunoff.get() + lidRunoff));
        qDrain.set((qDrain.get() + lidDrain.v));
    }

    // lid.c:1890
    static void lid_writeWaterBalance()
    {
        int j = 0;
        int k = 0;
        double ucf = UCF(RAINDEPTH);
        double inflow = 0.0;
        double outflow = 0.0;
        double err = 0.0;
        TLidUnit lidUnit = null;
        TLidList lidList = null;
        LidGroup lidGroup = null;
        for (j = 0; (j < GroupCount); j++)
        {
            if ((LidGroups[j] != null))
            {
                k++;
            }
        }
        if ((k == 0))
        {
            return;
        }
        R.fprintf(Frpt.file, L291);
        R.fprintf(Frpt.file, L292);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L293);
        }
        else
        {
            R.fprintf(Frpt.file, L294);
        }
        R.fprintf(Frpt.file, L295);
        for (j = 0; (j < GroupCount); j++)
        {
            lidGroup = LidGroups[j];
            if (((!(lidGroup != null)) || (Subcatch[j].lidArea == 0.0)))
            {
                continue;
            }
            lidList = lidGroup.lidList;
            while ((lidList != null))
            {
                lidUnit = lidList.lidUnit;
                k = lidUnit.lidIndex;
                R.fprintf(Frpt.file, L296, Subcatch[j].ID, LidProcs[k].ID);
                R.fprintf(Frpt.file, L297, (lidUnit.waterBalance.inflow * ucf), (lidUnit.waterBalance.evap * ucf), (lidUnit.waterBalance.infil * ucf), (lidUnit.waterBalance.surfFlow * ucf), (lidUnit.waterBalance.drainFlow * ucf), (lidUnit.waterBalance.initVol * ucf), (lidUnit.waterBalance.finalVol * ucf));
                inflow = (lidUnit.waterBalance.initVol + lidUnit.waterBalance.inflow);
                outflow = ((((lidUnit.waterBalance.finalVol + lidUnit.waterBalance.evap) + lidUnit.waterBalance.infil) + lidUnit.waterBalance.surfFlow) + lidUnit.waterBalance.drainFlow);
                if ((inflow > 0.0))
                {
                    err = (((inflow - outflow)) / inflow);
                }
                else
                {
                    err = 1.0;
                }
                R.fprintf(Frpt.file, L298, (err * 100.0));
                lidList = lidList.nextLidUnit;
            }
        }
    }

    // lid.c:1972
    static void initLidRptFile(CPtr title, CPtr lidID, CPtr subcatchID, TLidUnit lidUnit)
    {
        int i = 0;
        CFile f = lidUnit.rptFile.file;
        if ((f == null))
        {
            return;
        }
        R.fprintf(f, L299);
        R.fprintf(f, L300, title);
        R.fprintf(f, L301, lidID, subcatchID);
        for (i = 0; (i < initLidRptFile_colCount); i++)
        {
            R.fprintf(f, L169, initLidRptFile_head1[i]);
        }
        for (i = 0; (i < initLidRptFile_colCount); i++)
        {
            R.fprintf(f, L169, initLidRptFile_head2[i]);
        }
        if ((UnitSystem == US))
        {
            for (i = 0; (i < initLidRptFile_colCount); i++)
            {
                R.fprintf(f, L169, initLidRptFile_units1[i]);
            }
        }
        else
        {
            for (i = 0; (i < initLidRptFile_colCount); i++)
            {
                R.fprintf(f, L169, initLidRptFile_units2[i]);
            }
        }
        R.fprintf(f, L302);
        for (i = 1; (i < initLidRptFile_colCount); i++)
        {
            R.fprintf(f, L303, initLidRptFile_line9);
        }
        lidUnit.rptFile.wasDry = 1;
        sstrncpy(lidUnit.rptFile.results, L0, 0);
    }

}
