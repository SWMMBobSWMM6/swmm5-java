// Generated from EPA SWMM 5.2.4 (iface.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_iface {
    static int IfaceFlowUnits = 0;
    static IBox IfaceStep = new IBox(0);
    static IBox NumIfacePolluts = new IBox(0);
    static int[] IfacePolluts = null;
    static IBox NumIfaceNodes = new IBox(0);
    static int[] IfaceNodes = null;
    static double[][] OldIfaceValues = null;
    static double[][] NewIfaceValues = null;
    static double IfaceFrac = 0.0;
    static double OldIfaceDate = 0.0;
    static double NewIfaceDate = 0.0;

    // iface.c:66
    static int iface_readFileParams(CPtr[] tok, int ntoks)
    {
        int k = 0;
        int j = 0;
        CPtr fname = R.buf(260);
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        k = ((byte) (findmatch(tok[0], FileModeWords)));
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[0]);
        }
        j = findmatch(tok[1], FileTypeWords);
        if ((j < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[1]);
        }
        if ((ntoks < 3))
        {
            return 0;
        }
        sstrncpy(fname, tok[2], 259);
        switch (j) {
            case RAINFALL_FILE:
                Frain.mode = k;
                sstrncpy(Frain.name, addAbsolutePath(fname), 259);
                break;
            case RUNOFF_FILE:
                Frunoff.mode = k;
                sstrncpy(Frunoff.name, addAbsolutePath(fname), 259);
                break;
            case HOTSTART_FILE:
                if ((k == USE_FILE))
                {
                    Fhotstart1.mode = k;
                    sstrncpy(Fhotstart1.name, addAbsolutePath(fname), 259);
                }
                else if ((k == SAVE_FILE))
                {
                    Fhotstart2.mode = k;
                    sstrncpy(Fhotstart2.name, addAbsolutePath(fname), 259);
                }
                break;
            case RDII_FILE:
                Frdii.mode = k;
                sstrncpy(Frdii.name, fname, 259);
                break;
            case INFLOWS_FILE:
                if ((k != USE_FILE))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                Finflows.mode = k;
                sstrncpy(Finflows.name, addAbsolutePath(fname), 259);
                break;
            case OUTFLOWS_FILE:
                if ((k != SAVE_FILE))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                Foutflows.mode = k;
                sstrncpy(Foutflows.name, addAbsolutePath(fname), 259);
                break;
        }
        return 0;
    }

    // iface.c:138
    static void iface_openRoutingFiles()
    {
        NumIfacePolluts.v = 0;
        IfacePolluts = (null);
        NumIfaceNodes.v = 0;
        IfaceNodes = (null);
        OldIfaceValues = (null);
        NewIfaceValues = (null);
        if (((Foutflows.mode != NO_FILE) && (Finflows.mode != NO_FILE)))
        {
            if ((strcomp(Foutflows.name, Finflows.name) != 0))
            {
                report_writeErrorMsg(ERR_ROUTING_FILE_NAMES, L0);
                return;
            }
        }
        if ((Foutflows.mode == SAVE_FILE))
        {
            openFileForOutput();
        }
        if ((Finflows.mode == USE_FILE))
        {
            openFileForInput();
        }
    }

    // iface.c:170
    static void iface_closeRoutingFiles()
    {
        {
            if ((IfacePolluts != null))
            {
                R.free(IfacePolluts);
                IfacePolluts = (null);
            }
        }
        ;
        {
            if ((IfaceNodes != null))
            {
                R.free(IfaceNodes);
                IfaceNodes = (null);
            }
        }
        ;
        if ((OldIfaceValues != null))
        {
            project_freeMatrix(OldIfaceValues);
        }
        if ((NewIfaceValues != null))
        {
            project_freeMatrix(NewIfaceValues);
        }
        if ((Finflows.file != null))
        {
            R.fclose(Finflows.file);
        }
        if ((Foutflows.file != null))
        {
            R.fclose(Foutflows.file);
        }
    }

    // iface.c:187
    static int iface_getNumIfaceNodes(double currentDate)
    {
        if ((OldIfaceDate > currentDate))
        {
            return 0;
        }
        while (((NewIfaceDate < currentDate) && (NewIfaceDate != (-693594))))
        {
            setOldIfaceValues();
            readNewIfaceValues();
        }
        if ((NewIfaceDate == (-693594)))
        {
            return 0;
        }
        IfaceFrac = (((currentDate - OldIfaceDate)) / ((NewIfaceDate - OldIfaceDate)));
        IfaceFrac = (((((0.0) >= (IfaceFrac))) ? (0.0) : (IfaceFrac)));
        IfaceFrac = (((((IfaceFrac) <= (1.0))) ? (IfaceFrac) : (1.0)));
        return NumIfaceNodes.v;
    }

    // iface.c:219
    static int iface_getIfaceNode(int index)
    {
        if (((index >= 0) && (index < NumIfaceNodes.v)))
        {
            return IfaceNodes[index];
        }
        else
        {
            return (-1);
        }
    }

    // iface.c:232
    static double iface_getIfaceFlow(int index)
    {
        double q1 = 0.0;
        double q2 = 0.0;
        if (((index >= 0) && (index < NumIfaceNodes.v)))
        {
            q1 = OldIfaceValues[index][0];
            q2 = NewIfaceValues[index][0];
            return ((((1.0 - IfaceFrac)) * q1) + (IfaceFrac * q2));
        }
        else
        {
            return 0.0;
        }
    }

    // iface.c:253
    static double iface_getIfaceQual(int index, int pollut)
    {
        int j = 0;
        double c1 = 0.0;
        double c2 = 0.0;
        if (((index >= 0) && (index < NumIfaceNodes.v)))
        {
            j = IfacePolluts[pollut];
            if ((j < 0))
            {
                return 0.0;
            }
            c1 = OldIfaceValues[index][(j + 1)];
            c2 = NewIfaceValues[index][(j + 1)];
            return ((((1.0 - IfaceFrac)) * c1) + (IfaceFrac * c2));
        }
        else
        {
            return 0.0;
        }
    }

    // iface.c:281
    static void iface_saveOutletResults(double reportDate, CFile file)
    {
        int i = 0;
        int p = 0;
        IBox yr = new IBox(0);
        IBox mon = new IBox(0);
        IBox day = new IBox(0);
        IBox hr = new IBox(0);
        IBox min = new IBox(0);
        IBox sec = new IBox(0);
        CPtr theDate = R.buf(26);
        datetime_decodeDate(reportDate, yr, mon, day);
        datetime_decodeTime(reportDate, hr, min, sec);
        R.snprintf(theDate, (int) (26), L167, yr.v, mon.v, day.v, hr.v, min.v, sec.v);
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if (!(isOutletNode(i) != 0))
            {
                continue;
            }
            R.fprintf(file, L168, Node[i].ID);
            R.fprintf(file, L169, theDate);
            R.fprintf(file, L170, (Node[i].inflow * UCF(FLOW)));
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                R.fprintf(file, L170, Node[i].newQual[p]);
            }
        }
    }

    // iface.c:313
    static void openFileForOutput()
    {
        int i = 0;
        int n = 0;
        Foutflows.file = R.fopen(Foutflows.name, L171);
        if ((Foutflows.file == null))
        {
            report_writeErrorMsg(ERR_ROUTING_FILE_OPEN, Foutflows.name);
            return;
        }
        R.fprintf(Foutflows.file, L172);
        R.fprintf(Foutflows.file, L173, Title[0]);
        R.fprintf(Foutflows.file, L174, ReportStep);
        R.fprintf(Foutflows.file, L175, (Nobjects[POLLUT] + 1));
        R.fprintf(Foutflows.file, L176, FlowUnitWords[FlowUnits]);
        for (i = 0; (i < Nobjects[POLLUT]); i++)
        {
            R.fprintf(Foutflows.file, L177, Pollut[i].ID, QualUnitsWords[Pollut[i].units]);
        }
        n = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((isOutletNode(i) != 0))
            {
                n++;
            }
        }
        R.fprintf(Foutflows.file, L178, n);
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((isOutletNode(i) != 0))
            {
                R.fprintf(Foutflows.file, L173, Node[i].ID);
            }
        }
        R.fprintf(Foutflows.file, L179);
        for (i = 0; (i < Nobjects[POLLUT]); i++)
        {
            R.fprintf(Foutflows.file, L180, Pollut[i].ID);
        }
        if ((ReportStart == StartDateTime))
        {
            iface_saveOutletResults(ReportStart, Foutflows.file);
        }
    }

    // iface.c:377
    static void openFileForInput()
    {
        int err = 0;
        CPtr line = R.buf(1025);
        CPtr s = R.buf(1025);
        Finflows.file = R.fopen(Finflows.name, L13);
        if ((Finflows.file == null))
        {
            report_writeErrorMsg(ERR_ROUTING_FILE_OPEN, Finflows.name);
            return;
        }
        R.fgets(line, (int) (1024), Finflows.file);
        if (((!(R.sscanf(line, L169, s) != 0)) || (!(strcomp(s, L181) != 0))))
        {
            report_writeErrorMsg(ERR_ROUTING_FILE_FORMAT, Finflows.name);
            return;
        }
        R.fgets(line, (int) (1024), Finflows.file);
        IfaceStep.v = 0;
        R.fgets(line, (int) (1024), Finflows.file);
        if (((!(R.sscanf(line, L182, IfaceStep) != 0)) || (IfaceStep.v <= 0)))
        {
            report_writeErrorMsg(ERR_ROUTING_FILE_FORMAT, Finflows.name);
            return;
        }
        err = getIfaceFilePolluts();
        if ((err > 0))
        {
            report_writeErrorMsg(err, Finflows.name);
            return;
        }
        err = getIfaceFileNodes();
        if ((err > 0))
        {
            report_writeErrorMsg(err, Finflows.name);
            return;
        }
        OldIfaceValues = project_createMatrix(NumIfaceNodes.v, (1 + NumIfacePolluts.v));
        NewIfaceValues = project_createMatrix(NumIfaceNodes.v, (1 + NumIfacePolluts.v));
        if (((OldIfaceValues == null) || (NewIfaceValues == null)))
        {
            report_writeErrorMsg(ERR_MEMORY, L0);
            return;
        }
        readNewIfaceValues();
        OldIfaceDate = NewIfaceDate;
    }

    // iface.c:450
    static int getIfaceFilePolluts()
    {
        int i = 0;
        int j = 0;
        CPtr line = R.buf(1025);
        CPtr s1 = R.buf(1025);
        CPtr s2 = R.buf(1025);
        R.fgets(line, (int) (1024), Finflows.file);
        NumIfacePolluts.v = (-1);
        if ((R.sscanf(line, L182, NumIfacePolluts) != 0))
        {
            NumIfacePolluts.v--;
        }
        if ((NumIfacePolluts.v < 0))
        {
            return ERR_ROUTING_FILE_FORMAT;
        }
        R.fgets(line, (int) (1024), Finflows.file);
        if ((R.sscanf(line, L44, s1, s2) < 2))
        {
            return ERR_ROUTING_FILE_FORMAT;
        }
        if (!(strcomp(s1, L183) != 0))
        {
            return ERR_ROUTING_FILE_FORMAT;
        }
        IfaceFlowUnits = findmatch(s2, FlowUnitWords);
        if ((IfaceFlowUnits < 0))
        {
            return ERR_ROUTING_FILE_FORMAT;
        }
        if ((Nobjects[POLLUT] > 0))
        {
            IfacePolluts = new int[(int) (Nobjects[POLLUT])];
            if (!(IfacePolluts != null))
            {
                return ERR_MEMORY;
            }
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                IfacePolluts[i] = (-1);
            }
        }
        if (((NumIfacePolluts.v > 0) && (Nobjects[POLLUT] > 0)))
        {
            for (i = 0; (i < NumIfacePolluts.v); i++)
            {
                if ((R.feof(Finflows.file) != 0))
                {
                    return ERR_ROUTING_FILE_FORMAT;
                }
                R.fgets(line, (int) (1024), Finflows.file);
                if ((R.sscanf(line, L44, s1, s2) < 2))
                {
                    return ERR_ROUTING_FILE_FORMAT;
                }
                j = project_findObject(POLLUT, s1);
                if ((j < 0))
                {
                    continue;
                }
                if (!(strcomp(s2, QualUnitsWords[Pollut[j].units]) != 0))
                {
                    return ERR_ROUTING_FILE_NOMATCH;
                }
                IfacePolluts[j] = i;
            }
        }
        return 0;
    }

    // iface.c:510
    static int getIfaceFileNodes()
    {
        int i = 0;
        CPtr line = R.buf(1025);
        CPtr s = R.buf(1025);
        R.fgets(line, (int) (1024), Finflows.file);
        if (((!(R.sscanf(line, L182, NumIfaceNodes) != 0)) || (NumIfaceNodes.v <= 0)))
        {
            return ERR_ROUTING_FILE_FORMAT;
        }
        IfaceNodes = new int[(int) (NumIfaceNodes.v)];
        if (!(IfaceNodes != null))
        {
            return ERR_MEMORY;
        }
        for (i = 0; (i < NumIfaceNodes.v); i++)
        {
            if ((R.feof(Finflows.file) != 0))
            {
                return ERR_ROUTING_FILE_FORMAT;
            }
            IfaceNodes[i] = 0;
            R.fgets(line, (int) (1024), Finflows.file);
            if (!(R.sscanf(line, L169, s) != 0))
            {
                return ERR_ROUTING_FILE_FORMAT;
            }
            IfaceNodes[i] = project_findObject(NODE, s);
        }
        if ((R.feof(Finflows.file) != 0))
        {
            return ERR_ROUTING_FILE_FORMAT;
        }
        R.fgets(line, (int) (1024), Finflows.file);
        return 0;
    }

    // iface.c:550
    static void readNewIfaceValues()
    {
        int i = 0;
        int j = 0;
        CPtr s = null;
        int yr = 0;
        int mon = 0;
        int day = 0;
        int hr = 0;
        int min = 0;
        int sec = 0;
        CPtr line = R.buf(1025);
        NewIfaceDate = (-693594);
        for (i = 0; (i < NumIfaceNodes.v); i++)
        {
            if ((R.feof(Finflows.file) != 0))
            {
                return;
            }
            R.fgets(line, (int) (1024), Finflows.file);
            if (((R.strtok(line, L184)) == null))
            {
                return;
            }
            s = R.strtok(null, L184);
            if ((s == null))
            {
                return;
            }
            yr = R.atoi(s);
            s = R.strtok(null, L184);
            if ((s == null))
            {
                return;
            }
            mon = R.atoi(s);
            s = R.strtok(null, L184);
            if ((s == null))
            {
                return;
            }
            day = R.atoi(s);
            s = R.strtok(null, L184);
            if ((s == null))
            {
                return;
            }
            hr = R.atoi(s);
            s = R.strtok(null, L184);
            if ((s == null))
            {
                return;
            }
            min = R.atoi(s);
            s = R.strtok(null, L184);
            if ((s == null))
            {
                return;
            }
            sec = R.atoi(s);
            s = R.strtok(null, L184);
            if ((s == null))
            {
                return;
            }
            NewIfaceValues[i][0] = (R.atof(s) / Qcf[IfaceFlowUnits]);
            for (j = 1; (j <= NumIfacePolluts.v); j++)
            {
                s = R.strtok(null, L184);
                if ((s == null))
                {
                    return;
                }
                NewIfaceValues[i][j] = R.atof(s);
            }
        }
        NewIfaceDate = (datetime_encodeDate(yr, mon, day) + datetime_encodeTime(hr, min, sec));
    }

    // iface.c:613
    static void setOldIfaceValues()
    {
        int i = 0;
        int j = 0;
        OldIfaceDate = NewIfaceDate;
        for (i = 0; (i < NumIfaceNodes.v); i++)
        {
            for (j = 0; (j < (NumIfacePolluts.v + 1)); j++)
            {
                OldIfaceValues[i][j] = NewIfaceValues[i][j];
            }
        }
    }

    // iface.c:633
    static int isOutletNode(int i)
    {
        if ((RouteModel == DW))
        {
            return (((Node[i].type == OUTFALL)) ? 1 : 0);
        }
        else
        {
            return (((Node[i].degree == 0)) ? 1 : 0);
        }
    }

}
