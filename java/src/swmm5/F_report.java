// Generated from EPA SWMM 5.2.4 (report.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_report {
    static LBox SysTime = new LBox(0L);

    // report.c:93
    static int report_readOptions(CPtr[] tok, int ntoks)
    {
        int k = 0;
        int j = 0;
        int m = 0;
        int t = 0;
        if ((ntoks < 2))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        k = ((byte) (findmatch(tok[0], ReportWords)));
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[0]);
        }
        if (((k < 2) || (k > 4)))
        {
            m = findmatch(tok[1], NoYesWords);
            if (((m < NO) || (m > YES)))
            {
                return error_setInpError(ERR_KEYWORD, tok[1]);
            }
            switch (k) {
                case 0:
                    RptFlags.disabled = m;
                    return 0;
                case 1:
                    RptFlags.input = m;
                    return 0;
                case 5:
                    RptFlags.continuity = m;
                    return 0;
                case 6:
                    RptFlags.flowStats = m;
                    return 0;
                case 7:
                    RptFlags.controls = m;
                    return 0;
                case 8:
                    RptFlags.averages = m;
                    return 0;
                case 9:
                    return 0;
                default:
                    return error_setInpError(ERR_KEYWORD, tok[1]);
            }
        }
        else
        {
            switch (k) {
                case 2:
                    m = SUBCATCH;
                    break;
                case 3:
                    m = NODE;
                    break;
                case 4:
                    m = LINK;
                    break;
            }
        }
        if ((strcomp(tok[1], L355) != 0))
        {
            k = NONE;
        }
        else if ((strcomp(tok[1], L351) != 0))
        {
            k = ALL;
        }
        else
        {
            k = SOME;
            for (t = 1; (t < ntoks); t++)
            {
                j = project_findObject(m, tok[t]);
                if ((j < 0))
                {
                    return error_setInpError(ERR_NAME, tok[t]);
                }
                switch (m) {
                    case SUBCATCH:
                        Subcatch[j].rptFlag = 1;
                        break;
                    case NODE:
                        Node[j].rptFlag = 1;
                        break;
                    case LINK:
                        Link[j].rptFlag = 1;
                        break;
                }
            }
        }
        switch (m) {
            case SUBCATCH:
                RptFlags.subcatchments = k;
                break;
            case NODE:
                RptFlags.nodes = k;
                break;
            case LINK:
                RptFlags.links = k;
                break;
        }
        return 0;
    }

    // report.c:171
    static void report_writeLine(CPtr line)
    {
        if ((Frpt.file != null))
        {
            R.fprintf(Frpt.file, L356, line);
        }
    }

    // report.c:183
    static void report_writeSysTime()
    {
        CPtr theTime = R.buf(9);
        double elapsedTime = 0.0;
        LBox endTime = new LBox(0L);
        if ((Frpt.file != null))
        {
            R.fprintf(Frpt.file, L357, R.ctime(SysTime.v));
            endTime.v = R.time();
            R.fprintf(Frpt.file, L358, R.ctime(endTime.v));
            elapsedTime = ((double) (endTime.v - SysTime.v));
            R.fprintf(Frpt.file, L359);
            if ((elapsedTime < 1.0))
            {
                R.fprintf(Frpt.file, L360);
            }
            else
            {
                elapsedTime /= 86400;
                if ((elapsedTime >= 1.0))
                {
                    R.fprintf(Frpt.file, L361, R.D2I(Math.floor(elapsedTime)));
                    elapsedTime -= (Math.floor(elapsedTime));
                }
                datetime_timeToStr(elapsedTime, theTime);
                R.fprintf(Frpt.file, L169, theTime);
            }
        }
    }

    // report.c:220
    static void report_writeLogo()
    {
        R.fprintf(Frpt.file, L362);
        R.fprintf(Frpt.file, L363);
        R.fprintf(Frpt.file, L276);
        SysTime.v = R.time();
    }

    // report.c:235
    static void report_writeTitle()
    {
        int i = 0;
        int lineCount = 0;
        if ((ErrorCode != 0))
        {
            return;
        }
        for (i = 0; (i < 3); i++)
        {
            if ((R.strlen(Title[i]) > 0))
            {
                report_writeLine((Title[i]));
                lineCount++;
            }
        }
    }

    // report.c:254
    static void report_writeOptions()
    {
        CPtr str = R.buf(80);
        report_writeLine((L0));
        report_writeLine((L227));
        report_writeLine((L364));
        report_writeLine((L227));
        R.fprintf(Frpt.file, L365, FlowUnitWords[FlowUnits]);
        R.fprintf(Frpt.file, L366);
        R.fprintf(Frpt.file, L367);
        if (((IgnoreRainfall != 0) || (Nobjects[GAGE] == 0)))
        {
            R.fprintf(Frpt.file, L4);
        }
        else
        {
            R.fprintf(Frpt.file, L5);
        }
        R.fprintf(Frpt.file, L368);
        if (((IgnoreRDII != 0) || (Nobjects[UNITHYD] == 0)))
        {
            R.fprintf(Frpt.file, L4);
        }
        else
        {
            R.fprintf(Frpt.file, L5);
        }
        R.fprintf(Frpt.file, L369);
        if (((IgnoreSnowmelt != 0) || (Nobjects[SNOWMELT] == 0)))
        {
            R.fprintf(Frpt.file, L4);
        }
        else
        {
            R.fprintf(Frpt.file, L5);
        }
        R.fprintf(Frpt.file, L370);
        if (((IgnoreGwater != 0) || (Nobjects[AQUIFER] == 0)))
        {
            R.fprintf(Frpt.file, L4);
        }
        else
        {
            R.fprintf(Frpt.file, L5);
        }
        R.fprintf(Frpt.file, L371);
        if (((IgnoreRouting != 0) || (Nobjects[LINK] == 0)))
        {
            R.fprintf(Frpt.file, L4);
        }
        else
        {
            R.fprintf(Frpt.file, L5);
            R.fprintf(Frpt.file, L372);
            if ((AllowPonding != 0))
            {
                R.fprintf(Frpt.file, L5);
            }
            else
            {
                R.fprintf(Frpt.file, L4);
            }
        }
        R.fprintf(Frpt.file, L373);
        if (((IgnoreQuality != 0) || (Nobjects[POLLUT] == 0)))
        {
            R.fprintf(Frpt.file, L4);
        }
        else
        {
            R.fprintf(Frpt.file, L5);
        }
        if ((Nobjects[SUBCATCH] > 0))
        {
            R.fprintf(Frpt.file, L374, InfilModelWords[InfilModel]);
        }
        if ((Nobjects[LINK] > 0))
        {
            R.fprintf(Frpt.file, L375, RouteModelWords[RouteModel]);
        }
        if ((RouteModel == DW))
        {
            R.fprintf(Frpt.file, L376, SurchargeWords[SurchargeMethod]);
        }
        datetime_dateToStr(StartDate.v, str);
        R.fprintf(Frpt.file, L377, str);
        datetime_timeToStr(StartTime.v, str);
        R.fprintf(Frpt.file, L378, str);
        datetime_dateToStr(EndDate.v, str);
        R.fprintf(Frpt.file, L379, str);
        datetime_timeToStr(EndTime.v, str);
        R.fprintf(Frpt.file, L378, str);
        R.fprintf(Frpt.file, L380, StartDryDays);
        datetime_timeToStr(datetime_encodeTime(0, 0, ReportStep), str);
        R.fprintf(Frpt.file, L381, str);
        if ((Nobjects[SUBCATCH] > 0))
        {
            datetime_timeToStr(datetime_encodeTime(0, 0, WetStep), str);
            R.fprintf(Frpt.file, L382, str);
            datetime_timeToStr(datetime_encodeTime(0, 0, DryStep), str);
            R.fprintf(Frpt.file, L383, str);
        }
        if ((Nobjects[LINK] > 0))
        {
            R.fprintf(Frpt.file, L384, RouteStep);
            if ((RouteModel == DW))
            {
                R.fprintf(Frpt.file, L385);
                if ((CourantFactor.v > 0.0))
                {
                    R.fprintf(Frpt.file, L5);
                }
                else
                {
                    R.fprintf(Frpt.file, L4);
                }
                R.fprintf(Frpt.file, L386, MaxTrials);
                R.fprintf(Frpt.file, L387, NumThreads);
                R.fprintf(Frpt.file, L388, (HeadTol.v * UCF(LENGTH)));
                if ((UnitSystem == US))
                {
                    R.fprintf(Frpt.file, L389);
                }
                else
                {
                    R.fprintf(Frpt.file, L390);
                }
            }
        }
        report_writeLine((L0));
    }

    // report.c:355
    static void report_writeRainStats(int i, TRainStats r)
    {
        CPtr date1 = R.bufFrom(L391, 12);
        CPtr date2 = R.bufFrom(L391, 12);
        if ((RptFlags.disabled != 0))
        {
            return;
        }
        if ((i < 0))
        {
            report_writeLine((L0));
            report_writeLine((L255));
            report_writeLine((L392));
            report_writeLine((L255));
            R.fprintf(Frpt.file, L393);
            R.fprintf(Frpt.file, L394);
            R.fprintf(Frpt.file, L245);
        }
        else
        {
            if ((r.startDate != (-693594)))
            {
                datetime_dateToStr(r.startDate, date1);
            }
            if ((r.endDate != (-693594)))
            {
                datetime_dateToStr(r.endDate, date2);
            }
            R.fprintf(Frpt.file, L395, Gage[i].staID, date1, date2, (Gage[i].rainInterval / 60), r.periodsRain, r.periodsMissing, r.periodsMalfunc);
        }
        report_writeLine((L0));
    }

    // report.c:396
    static void report_writeRdiiStats(double rainVol, double rdiiVol)
    {
        double ratio = 0.0;
        double ucf1 = 0.0;
        double ucf2 = 0.0;
        if ((RptFlags.disabled != 0))
        {
            return;
        }
        ucf1 = (UCF(LENGTH) * UCF(LANDAREA));
        if ((UnitSystem == US))
        {
            ucf2 = (0.64632 / 86400);
        }
        else
        {
            ucf2 = (2.4466 / 86400);
        }
        report_writeLine((L0));
        R.fprintf(Frpt.file, L396);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L397);
        }
        else
        {
            R.fprintf(Frpt.file, L398);
        }
        R.fprintf(Frpt.file, L399);
        R.fprintf(Frpt.file, L400, (rainVol * ucf1), (rainVol * ucf2));
        R.fprintf(Frpt.file, L401, (rdiiVol * ucf1), (rdiiVol * ucf2));
        if ((rainVol == 0.0))
        {
            ratio = 0.0;
        }
        else
        {
            ratio = (rdiiVol / rainVol);
        }
        R.fprintf(Frpt.file, L402, ratio);
        report_writeLine((L0));
    }

    // report.c:441
    static void report_writeControlActionsHeading()
    {
        report_writeLine((L0));
        report_writeLine((L255));
        report_writeLine((L403));
        report_writeLine((L255));
        R.fprintf(Frpt.file, L276);
    }

    // report.c:452
    static void report_writeControlAction(double aDate, CPtr linkID, double value, CPtr ruleID)
    {
        CPtr theDate = R.buf(12);
        CPtr theTime = R.buf(9);
        if ((RptFlags.disabled != 0))
        {
            return;
        }
        datetime_dateToStr(aDate, theDate);
        datetime_timeToStr(aDate, theTime);
        R.fprintf(Frpt.file, L404, theDate, theTime, linkID, value, ruleID);
    }

    // report.c:480
    static void report_writeRunoffError(TRunoffTotals totals, double totalArea)
    {
        if ((Frunoff.mode == USE_FILE))
        {
            report_writeLine((L0));
            R.fprintf(Frpt.file, L405, Frunoff.name);
            report_writeLine((L0));
            return;
        }
        if ((totalArea == 0.0))
        {
            return;
        }
        report_writeLine((L0));
        R.fprintf(Frpt.file, L406);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L407);
        }
        else
        {
            R.fprintf(Frpt.file, L408);
        }
        R.fprintf(Frpt.file, L409);
        if ((totals.initStorage > 0.0))
        {
            R.fprintf(Frpt.file, L410, ((totals.initStorage * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.initStorage / totalArea) * UCF(RAINDEPTH)));
        }
        if ((Nobjects[SNOWMELT] > 0))
        {
            R.fprintf(Frpt.file, L411, ((totals.initSnowCover * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.initSnowCover / totalArea) * UCF(RAINDEPTH)));
        }
        R.fprintf(Frpt.file, L412, ((totals.rainfall * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.rainfall / totalArea) * UCF(RAINDEPTH)));
        if ((totals.runon > 0.0))
        {
            R.fprintf(Frpt.file, L413, ((totals.runon * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.runon / totalArea) * UCF(RAINDEPTH)));
        }
        R.fprintf(Frpt.file, L414, ((totals.evap * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.evap / totalArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L415, ((totals.infil * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.infil / totalArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L416, ((totals.runoff * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.runoff / totalArea) * UCF(RAINDEPTH)));
        if ((totals.drains > 0.0))
        {
            R.fprintf(Frpt.file, L417, ((totals.drains * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.drains / totalArea) * UCF(RAINDEPTH)));
        }
        if ((Nobjects[SNOWMELT] > 0))
        {
            R.fprintf(Frpt.file, L418, ((totals.snowRemoved * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.snowRemoved / totalArea) * UCF(RAINDEPTH)));
            R.fprintf(Frpt.file, L419, ((totals.finalSnowCover * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.finalSnowCover / totalArea) * UCF(RAINDEPTH)));
        }
        R.fprintf(Frpt.file, L420, ((totals.finalStorage * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.finalStorage / totalArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L421, totals.pctError);
        report_writeLine((L0));
    }

    // report.c:577
    static void report_writeLoadingError(TLoadingTotals[] totals)
    {
        int p1 = 0;
        int p2 = 0;
        p1 = 1;
        p2 = (((((5) <= (Nobjects[POLLUT]))) ? (5) : (Nobjects[POLLUT])));
        while ((p1 <= Nobjects[POLLUT]))
        {
            report_LoadingErrors((p1 - 1), (p2 - 1), totals);
            p1 = (p2 + 1);
            p2 = (p1 + 4);
            p2 = (((((p2) <= (Nobjects[POLLUT]))) ? (p2) : (Nobjects[POLLUT])));
        }
    }

    // report.c:599
    static void report_LoadingErrors(int p1, int p2, TLoadingTotals[] totals)
    {
        int i = 0;
        int p = 0;
        double cf = 1.0;
        CPtr units = R.buf(15);
        report_writeLine((L0));
        R.fprintf(Frpt.file, L422);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L423, Pollut[p].ID);
        }
        R.fprintf(Frpt.file, L424);
        for (p = p1; (p <= p2); p++)
        {
            i = UnitSystem;
            if ((Pollut[p].units == COUNT))
            {
                i = 2;
            }
            sstrncpy(units, LoadUnitsWords[i], 14);
            R.fprintf(Frpt.file, L423, units);
        }
        R.fprintf(Frpt.file, L422);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L425);
        }
        R.fprintf(Frpt.file, L426);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].initLoad * cf));
        }
        R.fprintf(Frpt.file, L428);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].buildup * cf));
        }
        R.fprintf(Frpt.file, L429);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].deposition * cf));
        }
        R.fprintf(Frpt.file, L430);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].sweeping * cf));
        }
        R.fprintf(Frpt.file, L431);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].infil * cf));
        }
        R.fprintf(Frpt.file, L432);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].bmpRemoval * cf));
        }
        R.fprintf(Frpt.file, L433);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].runoff * cf));
        }
        R.fprintf(Frpt.file, L434);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, (totals[p].finalLoad * cf));
        }
        R.fprintf(Frpt.file, L435);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, totals[p].pctError);
        }
        report_writeLine((L0));
    }

    // report.c:685
    static void report_writeGwaterError(TGwaterTotals totals, double gwArea)
    {
        report_writeLine((L0));
        R.fprintf(Frpt.file, L406);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L436);
        }
        else
        {
            R.fprintf(Frpt.file, L437);
        }
        R.fprintf(Frpt.file, L409);
        R.fprintf(Frpt.file, L438, ((totals.initStorage * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.initStorage / gwArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L439, ((totals.infil * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.infil / gwArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L440, ((totals.upperEvap * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.upperEvap / gwArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L441, ((totals.lowerEvap * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.lowerEvap / gwArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L442, ((totals.lowerPerc * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.lowerPerc / gwArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L443, ((totals.gwater * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.gwater / gwArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L420, ((totals.finalStorage * UCF(LENGTH)) * UCF(LANDAREA)), ((totals.finalStorage / gwArea) * UCF(RAINDEPTH)));
        R.fprintf(Frpt.file, L421, totals.pctError);
        report_writeLine((L0));
    }

    // report.c:737
    static void report_writeFlowError(TRoutingTotals totals)
    {
        double ucf1 = 0.0;
        double ucf2 = 0.0;
        ucf1 = (UCF(LENGTH) * UCF(LANDAREA));
        if ((UnitSystem == US))
        {
            ucf2 = (0.64632 / 86400);
        }
        else
        {
            ucf2 = (2.4466 / 86400);
        }
        report_writeLine((L0));
        R.fprintf(Frpt.file, L444);
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L445);
        }
        else
        {
            R.fprintf(Frpt.file, L446);
        }
        R.fprintf(Frpt.file, L447);
        R.fprintf(Frpt.file, L448, (totals.dwInflow * ucf1), (totals.dwInflow * ucf2));
        R.fprintf(Frpt.file, L449, (totals.wwInflow * ucf1), (totals.wwInflow * ucf2));
        R.fprintf(Frpt.file, L450, (totals.gwInflow * ucf1), (totals.gwInflow * ucf2));
        R.fprintf(Frpt.file, L451, (totals.iiInflow * ucf1), (totals.iiInflow * ucf2));
        R.fprintf(Frpt.file, L452, (totals.exInflow * ucf1), (totals.exInflow * ucf2));
        R.fprintf(Frpt.file, L453, (totals.outflow * ucf1), (totals.outflow * ucf2));
        R.fprintf(Frpt.file, L454, (totals.flooding * ucf1), (totals.flooding * ucf2));
        R.fprintf(Frpt.file, L414, (totals.evapLoss * ucf1), (totals.evapLoss * ucf2));
        R.fprintf(Frpt.file, L455, (totals.seepLoss * ucf1), (totals.seepLoss * ucf2));
        R.fprintf(Frpt.file, L456, (totals.initStorage * ucf1), (totals.initStorage * ucf2));
        R.fprintf(Frpt.file, L457, (totals.finalStorage * ucf1), (totals.finalStorage * ucf2));
        R.fprintf(Frpt.file, L421, totals.pctError);
        report_writeLine((L0));
    }

    // report.c:800
    static void report_writeQualError(TRoutingTotals[] QualTotals)
    {
        int p1 = 0;
        int p2 = 0;
        p1 = 1;
        p2 = (((((5) <= (Nobjects[POLLUT]))) ? (5) : (Nobjects[POLLUT])));
        while ((p1 <= Nobjects[POLLUT]))
        {
            report_QualErrors((p1 - 1), (p2 - 1), QualTotals);
            p1 = (p2 + 1);
            p2 = (p1 + 4);
            p2 = (((((p2) <= (Nobjects[POLLUT]))) ? (p2) : (Nobjects[POLLUT])));
        }
    }

    // report.c:821
    static void report_QualErrors(int p1, int p2, TRoutingTotals[] QualTotals)
    {
        int i = 0;
        int p = 0;
        CPtr units = R.buf(15);
        report_writeLine((L0));
        R.fprintf(Frpt.file, L422);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L423, Pollut[p].ID);
        }
        R.fprintf(Frpt.file, L458);
        for (p = p1; (p <= p2); p++)
        {
            i = UnitSystem;
            if ((Pollut[p].units == COUNT))
            {
                i = 2;
            }
            sstrncpy(units, LoadUnitsWords[i], 14);
            R.fprintf(Frpt.file, L423, units);
        }
        R.fprintf(Frpt.file, L422);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L425);
        }
        R.fprintf(Frpt.file, L459);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].dwInflow);
        }
        R.fprintf(Frpt.file, L460);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].wwInflow);
        }
        R.fprintf(Frpt.file, L461);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].gwInflow);
        }
        R.fprintf(Frpt.file, L462);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].iiInflow);
        }
        R.fprintf(Frpt.file, L463);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].exInflow);
        }
        R.fprintf(Frpt.file, L464);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].outflow);
        }
        R.fprintf(Frpt.file, L465);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].flooding);
        }
        R.fprintf(Frpt.file, L466);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].seepLoss);
        }
        R.fprintf(Frpt.file, L467);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].reacted);
        }
        R.fprintf(Frpt.file, L468);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].initStorage);
        }
        R.fprintf(Frpt.file, L469);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].finalStorage);
        }
        R.fprintf(Frpt.file, L435);
        for (p = p1; (p <= p2); p++)
        {
            R.fprintf(Frpt.file, L427, QualTotals[p].pctError);
        }
        report_writeLine((L0));
    }

    // report.c:923
    static void report_writeMaxStats(TMaxStats[] maxMassBalErrs, TMaxStats[] maxCourantCrit, int nMaxStats)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        if (((RouteModel != DW) || (Nobjects[LINK] == 0)))
        {
            return;
        }
        if ((nMaxStats <= 0))
        {
            return;
        }
        if ((maxMassBalErrs[0].index >= 0))
        {
            report_writeLine((L0));
            report_writeLine((L470));
            report_writeLine((L471));
            report_writeLine((L470));
            for (i = 0; (i < nMaxStats); i++)
            {
                j = maxMassBalErrs[i].index;
                if ((j < 0))
                {
                    continue;
                }
                R.fprintf(Frpt.file, L472, Node[j].ID, maxMassBalErrs[i].value);
            }
            report_writeLine((L0));
        }
        if ((CourantFactor.v == 0.0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L473));
        report_writeLine((L474));
        report_writeLine((L473));
        k = 0;
        for (i = 0; (i < nMaxStats); i++)
        {
            j = maxCourantCrit[i].index;
            if ((j < 0))
            {
                continue;
            }
            k++;
            if ((maxCourantCrit[i].objType == NODE))
            {
                R.fprintf(Frpt.file, L475, Node[j].ID);
            }
            else
            {
                R.fprintf(Frpt.file, L476, Link[j].ID);
            }
            R.fprintf(Frpt.file, L477, maxCourantCrit[i].value);
        }
        if ((k == 0))
        {
            R.fprintf(Frpt.file, L478);
        }
        report_writeLine((L0));
    }

    // report.c:977
    static void report_writeMaxFlowTurns(TMaxStats[] flowTurns, int nMaxStats)
    {
        int i = 0;
        int j = 0;
        if ((Nobjects[LINK] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L479));
        report_writeLine((L480));
        report_writeLine((L479));
        if (((nMaxStats <= 0) || (flowTurns[0].index <= 0)))
        {
            R.fprintf(Frpt.file, L481);
        }
        else
        {
            for (i = 0; (i < nMaxStats); i++)
            {
                j = flowTurns[i].index;
                if ((j < 0))
                {
                    continue;
                }
                R.fprintf(Frpt.file, L482, Link[j].ID, flowTurns[i].value);
            }
        }
        report_writeLine((L0));
    }

    // report.c:1011
    static void report_writeNonconvergedStats(TMaxStats[] maxNonconverged, int nMaxStats)
    {
        int i = 0;
        int j = 0;
        if (((Nobjects[NODE] == 0) || (RouteModel != DW)))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L483));
        report_writeLine((L484));
        report_writeLine((L483));
        if ((((nMaxStats <= 0) || (maxNonconverged[0].index <= 0)) || (maxNonconverged[0].value < 0.00005)))
        {
            R.fprintf(Frpt.file, L485);
        }
        else
        {
            for (i = 0; (i < nMaxStats); i++)
            {
                j = maxNonconverged[i].index;
                if (((j < 0) || (maxNonconverged[i].value <= 0.0)))
                {
                    continue;
                }
                R.fprintf(Frpt.file, L472, Node[j].ID, (100.0 * maxNonconverged[i].value));
            }
        }
        report_writeLine((L0));
    }

    // report.c:1037
    static void report_writeTimeStepStats(TTimeStepStats timeStepStats)
    {
        double timeStepCount = timeStepStats.timeStepCount;
        double totalRoutingTime = 0.0;
        double fSteadyState = 0.0;
        if (((Nobjects[LINK] == 0) || (timeStepStats.timeStepCount == 0.0)))
        {
            return;
        }
        totalRoutingTime = (timeStepStats.steadyStateTime + timeStepStats.routingTime);
        if ((totalRoutingTime > 0.0))
        {
            fSteadyState = ((100.0 * timeStepStats.steadyStateTime) / totalRoutingTime);
        }
        report_writeLine((L0));
        report_writeLine((L470));
        report_writeLine((L486));
        report_writeLine((L470));
        R.fprintf(Frpt.file, L487, timeStepStats.minTimeStep);
        R.fprintf(Frpt.file, L488, (timeStepStats.routingTime / timeStepCount));
        R.fprintf(Frpt.file, L489, timeStepStats.maxTimeStep);
        R.fprintf(Frpt.file, L490, (((((fSteadyState) <= (100.0))) ? (fSteadyState) : (100.0))));
        R.fprintf(Frpt.file, L491, (timeStepStats.trialsCount / timeStepCount));
        R.fprintf(Frpt.file, L492, ((100.0 * ((double) (NonConvergeCount))) / timeStepCount));
        if (((RouteModel == DW) && (CourantFactor.v > 0.0)))
        {
            report_RouteStepFreq(timeStepStats);
        }
        report_writeLine((L0));
    }

    // report.c:1085
    static void report_RouteStepFreq(TTimeStepStats timeStepStats)
    {
        double totalSteps = 0.0;
        int i = 0;
        for (i = 1; (i < 6); i++)
        {
            totalSteps += timeStepStats.timeStepCounts[i];
        }
        if ((totalSteps == 0))
        {
            return;
        }
        R.fprintf(Frpt.file, L493);
        for (i = 1; (i < 6); i++)
        {
            R.fprintf(Frpt.file, L494, timeStepStats.timeStepIntervals[(i - 1)], timeStepStats.timeStepIntervals[i], ((100.0 * ((double) ((timeStepStats.timeStepCounts[i])))) / totalSteps));
        }
    }

    // report.c:1113
    static void report_writeReport()
    {
        if ((ErrorCode != 0))
        {
            return;
        }
        if ((Nperiods == 0))
        {
            return;
        }
        if (((RptFlags.subcatchments != NONE) && ((((IgnoreRainfall == 0) || (IgnoreSnowmelt == 0)) || (IgnoreGwater == 0)))))
        {
            report_Subcatchments();
        }
        if (((IgnoreRouting == 1) && (IgnoreQuality == 1)))
        {
            return;
        }
        if ((RptFlags.nodes != NONE))
        {
            report_Nodes();
        }
        if ((RptFlags.links != NONE))
        {
            report_Links();
        }
    }

    // report.c:1135
    static void report_Subcatchments()
    {
        int j = 0;
        int p = 0;
        int k = 0;
        int period = 0;
        DBox days = new DBox(0.0);
        CPtr theDate = R.buf(12);
        CPtr theTime = R.buf(9);
        int hasSnowmelt = ((((Nobjects[SNOWMELT] > 0) && (!(IgnoreSnowmelt != 0)))) ? 1 : 0);
        int hasGwater = ((((Nobjects[AQUIFER] > 0) && (!(IgnoreGwater != 0)))) ? 1 : 0);
        int hasQuality = ((((Nobjects[POLLUT] > 0) && (!(IgnoreQuality != 0)))) ? 1 : 0);
        if ((Nobjects[SUBCATCH] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L479));
        report_writeLine((L495));
        report_writeLine((L479));
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            k = (Subcatch[j].rptFlag - 1);
            if ((k >= 0))
            {
                report_SubcatchHeader(Subcatch[j].ID);
                for (period = 1; (period <= Nperiods); period++)
                {
                    output_readDateTime(period, days);
                    datetime_dateToStr(days.v, theDate);
                    datetime_timeToStr(days.v, theTime);
                    output_readSubcatchResults(period, k);
                    R.fprintf(Frpt.file, L496, theDate, theTime, SubcatchResults[SUBCATCH_RAINFALL], ((SubcatchResults[SUBCATCH_EVAP] / 24.0) + SubcatchResults[SUBCATCH_INFIL]), SubcatchResults[SUBCATCH_RUNOFF]);
                    if ((hasSnowmelt != 0))
                    {
                        R.fprintf(Frpt.file, L497, SubcatchResults[SUBCATCH_SNOWDEPTH]);
                    }
                    if ((hasGwater != 0))
                    {
                        R.fprintf(Frpt.file, L498, SubcatchResults[SUBCATCH_GW_ELEV], SubcatchResults[SUBCATCH_GW_FLOW]);
                    }
                    if ((hasQuality != 0))
                    {
                        for (p = 0; (p < Nobjects[POLLUT]); p++)
                        {
                            R.fprintf(Frpt.file, L499, SubcatchResults[(SUBCATCH_WASHOFF + p)]);
                        }
                    }
                }
                report_writeLine((L0));
            }
        }
    }

    // report.c:1192
    static void report_SubcatchHeader(CPtr id)
    {
        int i = 0;
        int hasSnowmelt = ((((Nobjects[SNOWMELT] > 0) && (!(IgnoreSnowmelt != 0)))) ? 1 : 0);
        int hasGwater = ((((Nobjects[AQUIFER] > 0) && (!(IgnoreGwater != 0)))) ? 1 : 0);
        int hasQuality = ((((Nobjects[POLLUT] > 0) && (!(IgnoreQuality != 0)))) ? 1 : 0);
        report_writeLine((L0));
        R.fprintf(Frpt.file, L500, id);
        report_writeLine((L501));
        if ((hasSnowmelt > 0))
        {
            R.fprintf(Frpt.file, L502);
        }
        if ((hasGwater != 0))
        {
            R.fprintf(Frpt.file, L503);
            R.fprintf(Frpt.file, L503);
        }
        if ((hasQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L503);
            }
        }
        R.fprintf(Frpt.file, L504);
        if ((hasSnowmelt != 0))
        {
            R.fprintf(Frpt.file, L505);
        }
        if ((hasGwater != 0))
        {
            R.fprintf(Frpt.file, L506);
        }
        if ((hasQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L507, Pollut[i].ID);
            }
        }
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L508, FlowUnitWords[FlowUnits]);
        }
        else
        {
            R.fprintf(Frpt.file, L509, FlowUnitWords[FlowUnits]);
        }
        if ((hasSnowmelt != 0))
        {
            if ((UnitSystem == US))
            {
                R.fprintf(Frpt.file, L510);
            }
            else
            {
                R.fprintf(Frpt.file, L511);
            }
        }
        if ((hasGwater != 0))
        {
            if ((UnitSystem == US))
            {
                R.fprintf(Frpt.file, L512, FlowUnitWords[FlowUnits]);
            }
            else
            {
                R.fprintf(Frpt.file, L513, FlowUnitWords[FlowUnits]);
            }
        }
        if ((hasQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L507, QualUnitsWords[Pollut[i].units]);
            }
        }
        report_writeLine((L501));
        if ((hasSnowmelt != 0))
        {
            R.fprintf(Frpt.file, L502);
        }
        if ((hasGwater != 0))
        {
            R.fprintf(Frpt.file, L503);
            R.fprintf(Frpt.file, L503);
        }
        if ((hasQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L503);
            }
        }
    }

    // report.c:1261
    static void report_Nodes()
    {
        int j = 0;
        int p = 0;
        int k = 0;
        int period = 0;
        DBox days = new DBox(0.0);
        CPtr theDate = R.buf(12);
        CPtr theTime = R.buf(9);
        if ((Nobjects[NODE] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L514));
        report_writeLine((L515));
        report_writeLine((L514));
        for (j = 0; (j < Nobjects[NODE]); j++)
        {
            k = (Node[j].rptFlag - 1);
            if ((k >= 0))
            {
                report_NodeHeader(Node[j].ID);
                for (period = 1; (period <= Nperiods); period++)
                {
                    output_readDateTime(period, days);
                    datetime_dateToStr(days.v, theDate);
                    datetime_timeToStr(days.v, theTime);
                    output_readNodeResults(period, k);
                    R.fprintf(Frpt.file, L516, theDate, theTime, NodeResults[NODE_INFLOW], NodeResults[NODE_OVERFLOW], NodeResults[NODE_DEPTH], NodeResults[NODE_HEAD]);
                    if (!(IgnoreQuality != 0))
                    {
                        for (p = 0; (p < Nobjects[POLLUT]); p++)
                        {
                            R.fprintf(Frpt.file, L195, NodeResults[(NODE_QUAL + p)]);
                        }
                    }
                }
                report_writeLine((L0));
            }
        }
    }

    // report.c:1305
    static void report_NodeHeader(CPtr id)
    {
        int i = 0;
        CPtr lengthUnits = R.buf(9);
        report_writeLine((L0));
        R.fprintf(Frpt.file, L517, id);
        report_writeLine((L518));
        for (i = 0; (i < Nobjects[POLLUT]); i++)
        {
            R.fprintf(Frpt.file, L503);
        }
        R.fprintf(Frpt.file, L519);
        if (!(IgnoreQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L507, Pollut[i].ID);
            }
        }
        if ((UnitSystem == US))
        {
            sstrncpy(lengthUnits, L520, 8);
        }
        else
        {
            sstrncpy(lengthUnits, L521, 8);
        }
        R.fprintf(Frpt.file, L522, FlowUnitWords[FlowUnits], FlowUnitWords[FlowUnits], lengthUnits, lengthUnits);
        if (!(IgnoreQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L507, QualUnitsWords[Pollut[i].units]);
            }
        }
        report_writeLine((L518));
        if (!(IgnoreQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L503);
            }
        }
    }

    // report.c:1339
    static void report_Links()
    {
        int j = 0;
        int p = 0;
        int k = 0;
        int period = 0;
        DBox days = new DBox(0.0);
        CPtr theDate = R.buf(12);
        CPtr theTime = R.buf(9);
        if ((Nobjects[LINK] == 0))
        {
            return;
        }
        report_writeLine((L0));
        report_writeLine((L514));
        report_writeLine((L523));
        report_writeLine((L514));
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            k = (Link[j].rptFlag - 1);
            if ((k >= 0))
            {
                report_LinkHeader(Link[j].ID);
                for (period = 1; (period <= Nperiods); period++)
                {
                    output_readDateTime(period, days);
                    datetime_dateToStr(days.v, theDate);
                    datetime_timeToStr(days.v, theTime);
                    output_readLinkResults(period, k);
                    R.fprintf(Frpt.file, L516, theDate, theTime, LinkResults[LINK_FLOW], LinkResults[LINK_VELOCITY], LinkResults[LINK_DEPTH], LinkResults[LINK_CAPACITY]);
                    if (!(IgnoreQuality != 0))
                    {
                        for (p = 0; (p < Nobjects[POLLUT]); p++)
                        {
                            R.fprintf(Frpt.file, L195, LinkResults[(LINK_QUAL + p)]);
                        }
                    }
                }
                report_writeLine((L0));
            }
        }
    }

    // report.c:1383
    static void report_LinkHeader(CPtr id)
    {
        int i = 0;
        report_writeLine((L0));
        R.fprintf(Frpt.file, L524, id);
        report_writeLine((L518));
        for (i = 0; (i < Nobjects[POLLUT]); i++)
        {
            R.fprintf(Frpt.file, L503);
        }
        R.fprintf(Frpt.file, L525);
        if (!(IgnoreQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L507, Pollut[i].ID);
            }
        }
        if ((UnitSystem == US))
        {
            R.fprintf(Frpt.file, L526, FlowUnitWords[FlowUnits]);
        }
        else
        {
            R.fprintf(Frpt.file, L527, FlowUnitWords[FlowUnits]);
        }
        if (!(IgnoreQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L528, QualUnitsWords[Pollut[i].units]);
            }
        }
        report_writeLine((L518));
        if (!(IgnoreQuality != 0))
        {
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                R.fprintf(Frpt.file, L503);
            }
        }
    }

    // report.c:1422
    static void report_writeErrorMsg(int code, CPtr s)
    {
        if ((Frpt.file != null))
        {
            report_writeLine((L0));
            R.fprintf(Frpt.file, error_getMsg(code, Msg), s);
        }
        ErrorCode = code;
        if (((ErrorCode <= ERR_INPUT) || (ErrorCode >= ERR_FILE_NAME)))
        {
            R.snprintf(ErrorMsg, (int) (1024), error_getMsg(ErrorCode, Msg), s);
        }
    }

    // report.c:1446
    static void report_writeErrorCode()
    {
        if ((Frpt.file != null))
        {
            if ((((((ErrorCode >= ERR_MEMORY) && (ErrorCode <= ERR_TIMESTEP))) || (((ErrorCode >= ERR_FILE_NAME) && (ErrorCode <= ERR_OUT_FILE)))) || ((ErrorCode == ERR_SYSTEM))))
            {
                R.fprintf(Frpt.file, L169, error_getMsg(ErrorCode, Msg));
            }
        }
    }

    // report.c:1464
    static void report_writeInputErrorMsg(int k, int sect, CPtr line, long lineCount)
    {
        if ((Frpt.file != null))
        {
            report_writeErrorMsg(k, ErrString);
            if ((sect < 0))
            {
                R.fprintf(Frpt.file, L529, lineCount);
            }
            else
            {
                R.fprintf(Frpt.file, L530, lineCount, SectWords[sect]);
            }
            R.fprintf(Frpt.file, L356, line);
        }
    }

    // report.c:1485
    static void report_writeWarningMsg(CPtr msg, CPtr id)
    {
        R.fprintf(Frpt.file, L531, msg, id);
        Warnings++;
    }

    // report.c:1499
    static void report_writeTseriesErrorMsg(int code, TTable tseries)
    {
        CPtr theDate = R.buf(20);
        CPtr theTime = R.buf(20);
        double x = 0.0;
        if ((code == ERR_CURVE_SEQUENCE))
        {
            x = tseries.x2;
            datetime_dateToStr(x, theDate);
            datetime_timeToStr(x, theTime);
            report_writeErrorMsg(ERR_TIMESERIES_SEQUENCE, tseries.ID);
            R.fprintf(Frpt.file, L532, theDate, theTime);
        }
        else
        {
            report_writeErrorMsg(code, tseries.ID);
        }
    }

}
