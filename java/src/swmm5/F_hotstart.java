// Generated from EPA SWMM 5.2.4 (hotstart.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_hotstart {
    static int fileVersion = 0;

    // hotstart.c:72
    static int hotstart_open()
    {
        if (!(openHotstartFile1() != 0))
        {
            return 0;
        }
        if (!(openHotstartFile2() != 0))
        {
            return 0;
        }
        return 1;
    }

    // hotstart.c:82
    static void hotstart_close()
    {
        if ((Fhotstart2.file != null))
        {
            saveRunoff();
            saveRouting();
            R.fclose(Fhotstart2.file);
        }
    }

    // hotstart.c:94
    static int openHotstartFile1()
    {
        IBox nSubcatch = new IBox(0);
        IBox nNodes = new IBox(0);
        IBox nLinks = new IBox(0);
        IBox nPollut = new IBox(0);
        IBox nLandUses = new IBox(0);
        IBox flowUnits = new IBox(0);
        CPtr fStamp = R.bufFrom(L160, 15);
        CPtr fileStamp = R.bufFrom(L160, 15);
        CPtr fStampx = R.bufFrom(L161, 16);
        CPtr fileStamp2 = R.bufFrom(L162, 16);
        CPtr fileStamp3 = R.bufFrom(L163, 16);
        CPtr fileStamp4 = R.bufFrom(L164, 16);
        if ((Fhotstart1.mode != USE_FILE))
        {
            return 1;
        }
        if ((((Fhotstart1.file = R.fopen(Fhotstart1.name, L165))) == null))
        {
            report_writeErrorMsg(ERR_HOTSTART_FILE_OPEN, Fhotstart1.name);
            return 0;
        }
        R.fread(fStampx, 0, (int) (R.strlen(fileStamp2)), Fhotstart1.file);
        if ((R.strcmp(fStampx, fileStamp4) == 0))
        {
            fileVersion = 4;
        }
        else if ((R.strcmp(fStampx, fileStamp3) == 0))
        {
            fileVersion = 3;
        }
        else if ((R.strcmp(fStampx, fileStamp2) == 0))
        {
            fileVersion = 2;
        }
        else
        {
            R.rewind(Fhotstart1.file);
            R.fread(fStamp, 0, (int) (R.strlen(fileStamp)), Fhotstart1.file);
            if ((R.strcmp(fStamp, fileStamp) != 0))
            {
                report_writeErrorMsg(ERR_HOTSTART_FILE_FORMAT, L0);
                return 0;
            }
            fileVersion = 1;
        }
        nSubcatch.v = (-1);
        nNodes.v = (-1);
        nLinks.v = (-1);
        nPollut.v = (-1);
        nLandUses.v = (-1);
        flowUnits.v = (-1);
        if ((fileVersion >= 2))
        {
            R.fread1(nSubcatch, 4, Fhotstart1.file);
        }
        else
        {
            nSubcatch.v = Nobjects[SUBCATCH];
        }
        if ((fileVersion >= 3))
        {
            R.fread1(nLandUses, 4, Fhotstart1.file);
        }
        else
        {
            nLandUses.v = Nobjects[LANDUSE];
        }
        R.fread1(nNodes, 4, Fhotstart1.file);
        R.fread1(nLinks, 4, Fhotstart1.file);
        R.fread1(nPollut, 4, Fhotstart1.file);
        R.fread1(flowUnits, 4, Fhotstart1.file);
        if (((((((nSubcatch.v != Nobjects[SUBCATCH]) || (nLandUses.v != Nobjects[LANDUSE])) || (nNodes.v != Nobjects[NODE])) || (nLinks.v != Nobjects[LINK])) || (nPollut.v != Nobjects[POLLUT])) || (flowUnits.v != FlowUnits)))
        {
            report_writeErrorMsg(ERR_HOTSTART_FILE_FORMAT, L0);
            return 0;
        }
        if ((fileVersion >= 3))
        {
            readRunoff();
        }
        readRouting();
        R.fclose(Fhotstart1.file);
        if ((ErrorCode != 0))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    // hotstart.c:180
    static int openHotstartFile2()
    {
        IBox nSubcatch = new IBox(0);
        IBox nLandUses = new IBox(0);
        IBox nNodes = new IBox(0);
        IBox nLinks = new IBox(0);
        IBox nPollut = new IBox(0);
        IBox flowUnits = new IBox(0);
        CPtr fileStamp = R.bufFrom(L164, 16);
        if ((Fhotstart2.mode != SAVE_FILE))
        {
            return 1;
        }
        if ((((Fhotstart2.file = R.fopen(Fhotstart2.name, L166))) == null))
        {
            report_writeErrorMsg(ERR_HOTSTART_FILE_OPEN, Fhotstart2.name);
            return 0;
        }
        nSubcatch.v = Nobjects[SUBCATCH];
        nLandUses.v = Nobjects[LANDUSE];
        nNodes.v = Nobjects[NODE];
        nLinks.v = Nobjects[LINK];
        nPollut.v = Nobjects[POLLUT];
        flowUnits.v = FlowUnits;
        R.fwrite(fileStamp, 0, (int) (R.strlen(fileStamp)), Fhotstart2.file);
        R.fwrite1(nSubcatch.v, 4, Fhotstart2.file);
        R.fwrite1(nLandUses.v, 4, Fhotstart2.file);
        R.fwrite1(nNodes.v, 4, Fhotstart2.file);
        R.fwrite1(nLinks.v, 4, Fhotstart2.file);
        R.fwrite1(nPollut.v, 4, Fhotstart2.file);
        R.fwrite1(flowUnits.v, 4, Fhotstart2.file);
        return 1;
    }

    // hotstart.c:222
    static void saveRouting()
    {
        int i = 0;
        int j = 0;
        float[] x = new float[3];
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            x[0] = ((float) (Node[i].newDepth));
            x[1] = ((float) (Node[i].newLatFlow));
            R.fwrite(x, 0, (int) (2), Fhotstart2.file);
            if ((Node[i].type == STORAGE))
            {
                j = Node[i].subIndex;
                x[0] = ((float) (Storage[j].hrt));
                R.fwrite(x, 0, (int) (1), Fhotstart2.file);
            }
            for (j = 0; (j < Nobjects[POLLUT]); j++)
            {
                x[0] = ((float) (Node[i].newQual[j]));
                R.fwrite(x, 0, (int) (1), Fhotstart2.file);
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            x[0] = ((float) (Link[i].newFlow));
            x[1] = ((float) (Link[i].newDepth));
            x[2] = ((float) (Link[i].setting));
            R.fwrite(x, 0, (int) (3), Fhotstart2.file);
            for (j = 0; (j < Nobjects[POLLUT]); j++)
            {
                x[0] = ((float) (Link[i].newQual[j]));
                R.fwrite(x, 0, (int) (1), Fhotstart2.file);
            }
        }
    }

    // hotstart.c:267
    static void readRouting()
    {
        int i = 0;
        int j = 0;
        FBox x = new FBox(0.0f);
        double[] xgw = new double[4];
        CFile f = Fhotstart1.file;
        if ((fileVersion == 2))
        {
            xgw[2] = 0.0;
            xgw[3] = (-10000000000.0);
            for (i = 0; (i < Nobjects[SUBCATCH]); i++)
            {
                if (!(readFloat(x, f) != 0))
                {
                    return;
                }
                xgw[0] = x.v;
                if (!(readFloat(x, f) != 0))
                {
                    return;
                }
                xgw[1] = x.v;
                if ((Subcatch[i].groundwater != null))
                {
                    gwater_setState(i, xgw);
                }
            }
        }
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if (!(readFloat(x, f) != 0))
            {
                return;
            }
            Node[i].newDepth = x.v;
            if (!(readFloat(x, f) != 0))
            {
                return;
            }
            Node[i].newLatFlow = x.v;
            if (((fileVersion >= 4) && (Node[i].type == STORAGE)))
            {
                if (!(readFloat(x, f) != 0))
                {
                    return;
                }
                j = Node[i].subIndex;
                Storage[j].hrt = x.v;
            }
            for (j = 0; (j < Nobjects[POLLUT]); j++)
            {
                if (!(readFloat(x, f) != 0))
                {
                    return;
                }
                Node[i].newQual[j] = x.v;
            }
            if ((fileVersion <= 2))
            {
                for (j = 0; (j < Nobjects[POLLUT]); j++)
                {
                    if (!(readFloat(x, f) != 0))
                    {
                        return;
                    }
                }
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if (!(readFloat(x, f) != 0))
            {
                return;
            }
            Link[i].newFlow = x.v;
            if (!(readFloat(x, f) != 0))
            {
                return;
            }
            Link[i].newDepth = x.v;
            if (!(readFloat(x, f) != 0))
            {
                return;
            }
            Link[i].setting = x.v;
            Link[i].targetSetting = x.v;
            link_setTargetSetting(i);
            link_setSetting(i, 0.0);
            for (j = 0; (j < Nobjects[POLLUT]); j++)
            {
                if (!(readFloat(x, f) != 0))
                {
                    return;
                }
                Link[i].newQual[j] = x.v;
            }
        }
    }

    // hotstart.c:356
    static void saveRunoff()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        double[] x = new double[6];
        CFile f = Fhotstart2.file;
        for (i = 0; (i < Nobjects[SUBCATCH]); i++)
        {
            for (j = 0; (j < 3); j++)
            {
                x[j] = Subcatch[i].subArea[j].depth;
            }
            x[3] = Subcatch[i].newRunoff;
            R.fwrite(x, 0, (int) (4), f);
            for (j = 0; (j < 6); j++)
            {
                x[j] = 0.0;
            }
            infil_getState(i, x);
            R.fwrite(x, 0, (int) (6), f);
            if ((Subcatch[i].groundwater != null))
            {
                gwater_getState(i, x);
                R.fwrite(x, 0, (int) (4), f);
            }
            if ((Subcatch[i].snowpack != null))
            {
                for (j = 0; (j < 3); j++)
                {
                    snow_getState(i, j, x);
                    R.fwrite(x, 0, (int) (5), f);
                }
            }
            if ((Nobjects[POLLUT] > 0))
            {
                for (j = 0; (j < Nobjects[POLLUT]); j++)
                {
                    x[0] = Subcatch[i].newQual[j];
                    R.fwrite(x, 0, (int) (1), f);
                }
                for (j = 0; (j < Nobjects[POLLUT]); j++)
                {
                    x[0] = Subcatch[i].pondedQual[j];
                    R.fwrite(x, 0, (int) (1), f);
                }
                for (k = 0; (k < Nobjects[LANDUSE]); k++)
                {
                    for (j = 0; (j < Nobjects[POLLUT]); j++)
                    {
                        x[0] = Subcatch[i].landFactor[k].buildup[j];
                        R.fwrite(x, 0, (int) (Nobjects[POLLUT]), f);
                    }
                    x[0] = Subcatch[i].landFactor[k].lastSwept;
                    R.fwrite(x, 0, (int) (1), f);
                }
            }
        }
    }

    // hotstart.c:430
    static void readRunoff()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        double[] x = new double[6];
        CFile f = Fhotstart1.file;
        for (i = 0; (i < Nobjects[SUBCATCH]); i++)
        {
            for (j = 0; (j < 3); j++)
            {
                if (!(readDouble(new DField<TSubarea>(Subcatch[i].subArea[j], __o -> __o.depth, (__o, __x) -> __o.depth = __x), f) != 0))
                {
                    return;
                }
            }
            if (!(readDouble(new DField<TSubcatch>(Subcatch[i], __o -> __o.newRunoff, (__o, __x) -> __o.newRunoff = __x), f) != 0))
            {
                return;
            }
            for (j = 0; (j < 6); j++)
            {
                if (!(readDouble(new DElem(x, j), f) != 0))
                {
                    return;
                }
            }
            infil_setState(i, x);
            if ((Subcatch[i].groundwater != null))
            {
                for (j = 0; (j < 4); j++)
                {
                    if (!(readDouble(new DElem(x, j), f) != 0))
                    {
                        return;
                    }
                }
                gwater_setState(i, x);
            }
            if ((Subcatch[i].snowpack != null))
            {
                for (j = 0; (j < 3); j++)
                {
                    for (k = 0; (k < 5); k++)
                    {
                        if (!(readDouble(new DElem(x, k), f) != 0))
                        {
                            return;
                        }
                    }
                    snow_setState(i, j, x);
                }
            }
            if ((Nobjects[POLLUT] > 0))
            {
                for (j = 0; (j < Nobjects[POLLUT]); j++)
                {
                    if (!(readDouble(new DElem(Subcatch[i].newQual, j), f) != 0))
                    {
                        return;
                    }
                }
                for (j = 0; (j < Nobjects[POLLUT]); j++)
                {
                    if (!(readDouble(new DElem(Subcatch[i].pondedQual, j), f) != 0))
                    {
                        return;
                    }
                }
                for (k = 0; (k < Nobjects[LANDUSE]); k++)
                {
                    for (j = 0; (j < Nobjects[POLLUT]); j++)
                    {
                        if (!(readDouble(new DElem(Subcatch[i].landFactor[k].buildup, j), f) != 0))
                        {
                            return;
                        }
                    }
                    if (!(readDouble(new DField<TLandFactor>(Subcatch[i].landFactor[k], __o -> __o.lastSwept, (__o, __x) -> __o.lastSwept = __x), f) != 0))
                    {
                        return;
                    }
                }
            }
        }
    }

    // hotstart.c:499
    static int readFloat(FRef x, CFile f)
    {
        R.fread1(x, 4, f);
        if (((x).get() != (x).get()))
        {
            report_writeErrorMsg(ERR_HOTSTART_FILE_READ, L0);
            (x).set((float) (0.0));
            return 0;
        }
        return 1;
    }

    // hotstart.c:521
    static int readDouble(DRef x, CFile f)
    {
        if ((R.feof(f) != 0))
        {
            (x).set(0.0);
            report_writeErrorMsg(ERR_HOTSTART_FILE_READ, L0);
            return 0;
        }
        R.fread1(x, 8, f);
        if (((x).get() != (x).get()))
        {
            (x).set(0.0);
            return 0;
        }
        return 1;
    }

}
