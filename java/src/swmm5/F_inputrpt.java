// Generated from EPA SWMM 5.2.4 (inputrpt.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_inputrpt {
    // inputrpt.c:27
    static void inputrpt_writeInput()
    {
        int m = 0;
        int i = 0;
        int k = 0;
        int lidCount = 0;
        if ((ErrorCode != 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L206));
        report_writeLine((L207));
        report_writeLine((L206));
        R.fprintf(Frpt.file, L208, Nobjects[GAGE]);
        R.fprintf(Frpt.file, L209, Nobjects[SUBCATCH]);
        R.fprintf(Frpt.file, L210, Nobjects[NODE]);
        R.fprintf(Frpt.file, L211, Nobjects[LINK]);
        R.fprintf(Frpt.file, L212, Nobjects[POLLUT]);
        R.fprintf(Frpt.file, L213, Nobjects[LANDUSE]);
        if ((Nobjects[POLLUT] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L214));
            report_writeLine((L215));
            report_writeLine((L214));
            R.fprintf(Frpt.file, L216);
            R.fprintf(Frpt.file, L217);
            R.fprintf(Frpt.file, L218);
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L219, Pollut[i].ID, QualUnitsWords[Pollut[i].units], Pollut[i].pptConcen, Pollut[i].gwConcen, (Pollut[i].kDecay * 86400));
                if ((Pollut[i].coPollut >= 0))
                {
                    R.fprintf(Frpt.file, L220, Pollut[Pollut[i].coPollut].ID, Pollut[i].coFraction);
                }
            }
        }
        if ((Nobjects[LANDUSE] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L221));
            report_writeLine((L222));
            report_writeLine((L221));
            R.fprintf(Frpt.file, L223);
            R.fprintf(Frpt.file, L224);
            R.fprintf(Frpt.file, L225);
            for (i = 0; (i < Nobjects[LANDUSE]); i++)
            {
                R.fprintf(Frpt.file, L226, Landuse[i].ID, Landuse[i].sweepInterval, Landuse[i].sweepRemoval, Landuse[i].sweepDays0);
            }
        }
        if ((Nobjects[GAGE] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L227));
            report_writeLine((L228));
            report_writeLine((L227));
            R.fprintf(Frpt.file, L229);
            R.fprintf(Frpt.file, L230);
            R.fprintf(Frpt.file, L231);
            for (i = 0; (i < Nobjects[GAGE]); i++)
            {
                if ((Gage[i].tSeries >= 0))
                {
                    R.fprintf(Frpt.file, L232, Gage[i].ID, Tseries[Gage[i].tSeries].ID);
                    R.fprintf(Frpt.file, L233, RainTypeWords[Gage[i].rainType], ((Gage[i].rainInterval) / 60));
                }
                else
                {
                    R.fprintf(Frpt.file, L234, Gage[i].ID, Gage[i].fname);
                }
            }
        }
        if ((Nobjects[SUBCATCH] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L235));
            report_writeLine((L236));
            report_writeLine((L235));
            R.fprintf(Frpt.file, L237);
            R.fprintf(Frpt.file, L238);
            for (i = 0; (i < Nobjects[SUBCATCH]); i++)
            {
                R.fprintf(Frpt.file, L239, Subcatch[i].ID, (Subcatch[i].area * UCF(LANDAREA)), (Subcatch[i].width * UCF(LENGTH)), (Subcatch[i].fracImperv * 100.0), (Subcatch[i].slope * 100.0), Gage[Subcatch[i].gage].ID);
                if ((Subcatch[i].outNode >= 0))
                {
                    R.fprintf(Frpt.file, L240, Node[Subcatch[i].outNode].ID);
                }
                else if ((Subcatch[i].outSubcatch >= 0))
                {
                    R.fprintf(Frpt.file, L240, Subcatch[Subcatch[i].outSubcatch].ID);
                }
                if ((Subcatch[i].lidArea != 0.0))
                {
                    lidCount++;
                }
            }
        }
        if ((lidCount > 0))
        {
            lid_writeSummary();
        }
        if ((Nobjects[NODE] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L241));
            report_writeLine((L242));
            report_writeLine((L241));
            R.fprintf(Frpt.file, L243);
            R.fprintf(Frpt.file, L244);
            R.fprintf(Frpt.file, L245);
            for (i = 0; (i < Nobjects[NODE]); i++)
            {
                R.fprintf(Frpt.file, L246, Node[i].ID, NodeTypeWords[(Node[i].type - JUNCTION)], (Node[i].invertElev * UCF(LENGTH)), (Node[i].fullDepth * UCF(LENGTH)), ((Node[i].pondedArea * UCF(LENGTH)) * UCF(LENGTH)));
                if ((((Node[i].extInflow != null) || (Node[i].dwfInflow != null)) || (Node[i].rdiiInflow != null)))
                {
                    R.fprintf(Frpt.file, L247);
                }
            }
        }
        if ((Nobjects[LINK] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L241));
            report_writeLine((L248));
            report_writeLine((L241));
            R.fprintf(Frpt.file, L249);
            R.fprintf(Frpt.file, L250);
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                if ((Link[i].direction == 1))
                {
                    R.fprintf(Frpt.file, L251, Link[i].ID, Node[Link[i].node1].ID, Node[Link[i].node2].ID);
                }
                else
                {
                    R.fprintf(Frpt.file, L251, Link[i].ID, Node[Link[i].node2].ID, Node[Link[i].node1].ID);
                }
                if ((Link[i].type == PUMP))
                {
                    k = Link[i].subIndex;
                    R.fprintf(Frpt.file, L252, PumpTypeWords[Pump[k].type]);
                }
                else
                {
                    R.fprintf(Frpt.file, L253, LinkTypeWords[(Link[i].type - CONDUIT)]);
                }
                if ((Link[i].type == CONDUIT))
                {
                    k = Link[i].subIndex;
                    R.fprintf(Frpt.file, L254, (Conduit[k].length * UCF(LENGTH)), ((Conduit[k].slope * 100.0) * Link[i].direction), Conduit[k].roughness);
                }
            }
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L255));
            report_writeLine((L256));
            report_writeLine((L255));
            R.fprintf(Frpt.file, L257);
            R.fprintf(Frpt.file, L258);
            R.fprintf(Frpt.file, L259);
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                if ((Link[i].type == CONDUIT))
                {
                    k = Link[i].subIndex;
                    R.fprintf(Frpt.file, L260, Link[i].ID);
                    if ((Link[i].xsect.type == CUSTOM))
                    {
                        R.fprintf(Frpt.file, L261, Curve[Link[i].xsect.transect].ID);
                    }
                    else if ((Link[i].xsect.type == IRREGULAR))
                    {
                        R.fprintf(Frpt.file, L261, Transect[Link[i].xsect.transect].ID);
                    }
                    else if ((Link[i].xsect.type == STREET_XSECT))
                    {
                        R.fprintf(Frpt.file, L261, Street[Link[i].xsect.transect].ID);
                    }
                    else
                    {
                        R.fprintf(Frpt.file, L261, XsectTypeWords[Link[i].xsect.type]);
                    }
                    R.fprintf(Frpt.file, L262, (Link[i].xsect.yFull * UCF(LENGTH)), ((Link[i].xsect.aFull * UCF(LENGTH)) * UCF(LENGTH)), (Link[i].xsect.rFull * UCF(LENGTH)), (Link[i].xsect.wMax * UCF(LENGTH)), Conduit[k].barrels, (Link[i].qFull * UCF(FLOW)));
                }
            }
        }
        if ((Nobjects[SHAPE] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L206));
            report_writeLine((L263));
            report_writeLine((L206));
            for (i = 0; (i < Nobjects[SHAPE]); i++)
            {
                k = Shape[i].curve;
                R.fprintf(Frpt.file, L264, Curve[k].ID);
                R.fprintf(Frpt.file, L265);
                for (m = 1; (m < 51); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Shape[i].areaTbl[m]);
                }
                R.fprintf(Frpt.file, L268);
                for (m = 1; (m < 51); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Shape[i].hradTbl[m]);
                }
                R.fprintf(Frpt.file, L269);
                for (m = 1; (m < 51); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Shape[i].widthTbl[m]);
                }
            }
        }
        if ((Nobjects[TRANSECT] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L227));
            report_writeLine((L270));
            report_writeLine((L227));
            for (i = 0; (i < Nobjects[TRANSECT]); i++)
            {
                R.fprintf(Frpt.file, L271, Transect[i].ID);
                R.fprintf(Frpt.file, L265);
                for (m = 1; (m < 51); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Transect[i].areaTbl[m]);
                }
                R.fprintf(Frpt.file, L268);
                for (m = 1; (m < 51); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Transect[i].hradTbl[m]);
                }
                R.fprintf(Frpt.file, L269);
                for (m = 1; (m < 51); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Transect[i].widthTbl[m]);
                }
            }
        }
        if ((Nobjects[STREET] > 0))
        {
            report_writeLine((L0));
            report_writeLine((L0));
            report_writeLine((L272));
            report_writeLine((L273));
            report_writeLine((L272));
            for (i = 0; (i < Nobjects[STREET]); i++)
            {
                R.fprintf(Frpt.file, L274, Street[i].ID);
                R.fprintf(Frpt.file, L265);
                for (m = 1; (m < Street[i].transect.nTbl); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Street[i].transect.areaTbl[m]);
                }
                R.fprintf(Frpt.file, L268);
                for (m = 1; (m < Street[i].transect.nTbl); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Street[i].transect.hradTbl[m]);
                }
                R.fprintf(Frpt.file, L269);
                for (m = 1; (m < Street[i].transect.nTbl); m++)
                {
                    if (((m % 5) == 1))
                    {
                        R.fprintf(Frpt.file, L266);
                    }
                    R.fprintf(Frpt.file, L267, Street[i].transect.widthTbl[m]);
                }
            }
        }
        report_writeLine((L0));
    }

}
