// Generated from EPA SWMM 5.2.4 (runoff.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_runoff {
    static int IsRaining = 0;
    static int HasRunoff = 0;
    static int HasSnow = 0;
    static IBox runoff_Nsteps = new IBox(0);
    static IBox MaxSteps = new IBox(0);
    static long MaxStepsPos = 0L;
    static int HasWetLids = 0;
    static double[] OutflowLoad = null;

    // runoff.c:82
    static int runoff_open()
    {
        IsRaining = 0;
        HasRunoff = 0;
        HasSnow = 0;
        runoff_Nsteps.v = 0;
        if (!(odesolve_open(4) != 0))
        {
            report_writeErrorMsg(ERR_ODE_SOLVER, L0);
        }
        OutflowLoad = (null);
        if ((Nobjects[POLLUT] > 0))
        {
            OutflowLoad = new double[(int) (Nobjects[POLLUT])];
            if (!(OutflowLoad != null))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
            }
        }
        switch (Frunoff.mode) {
            case USE_FILE:
                if ((((Frunoff.file = R.fopen(Frunoff.name, L165))) == null))
                {
                    report_writeErrorMsg(ERR_RUNOFF_FILE_OPEN, Frunoff.name);
                }
                else
                {
                    runoff_initFile();
                }
                break;
            case SAVE_FILE:
                if ((((Frunoff.file = R.fopen(Frunoff.name, L166))) == null))
                {
                    report_writeErrorMsg(ERR_RUNOFF_FILE_OPEN, Frunoff.name);
                }
                else
                {
                    runoff_initFile();
                }
                break;
        }
        return ErrorCode;
    }

    // runoff.c:124
    static void runoff_close()
    {
        odesolve_close();
        {
            if ((OutflowLoad != null))
            {
                R.free(OutflowLoad);
                OutflowLoad = (null);
            }
        }
        ;
        if ((Frunoff.file != null))
        {
            if ((Frunoff.mode == SAVE_FILE))
            {
                R.fseek(Frunoff.file, MaxStepsPos, 0);
                R.fwrite1(runoff_Nsteps.v, 4, Frunoff.file);
            }
            R.fclose(Frunoff.file);
        }
        if ((Fclimate.file != null))
        {
            R.fclose(Fclimate.file);
        }
    }

    // runoff.c:155
    static void runoff_execute()
    {
        int j = 0;
        int day = 0;
        double runoffStep = 0.0;
        double oldRunoffStep = 0.0;
        double runoff = 0.0;
        double currentDate = 0.0;
        int canSweep = 0;
        if ((ErrorCode != 0))
        {
            return;
        }
        oldRunoffStep = (((NewRunoffTime - OldRunoffTime)) / 1000.0);
        currentDate = getDateTime(NewRunoffTime);
        climate_setState(currentDate);
        if ((Nobjects[SUBCATCH] == 0))
        {
            OldRunoffTime = NewRunoffTime;
            NewRunoffTime += (((double) ((DryStep))) * 1000.0);
            NewRunoffTime = (((((NewRunoffTime) <= (TotalDuration))) ? (NewRunoffTime) : (TotalDuration)));
            return;
        }
        IsRaining = 0;
        for (j = 0; (j < Nobjects[GAGE]); j++)
        {
            gage_setState(j, currentDate);
            if ((Gage[j].rainfall > 0.0))
            {
                IsRaining = 1;
            }
        }
        if ((Frunoff.mode == USE_FILE))
        {
            runoff_readFromFile();
            return;
        }
        day = datetime_dayOfYear(currentDate);
        canSweep = 0;
        if ((SweepStart <= SweepEnd))
        {
            if (((day >= SweepStart) && (day <= SweepEnd)))
            {
                canSweep = 1;
            }
        }
        else if (((day <= SweepEnd) || (day >= SweepStart)))
        {
            canSweep = 1;
        }
        runoffStep = runoff_getTimeStep(currentDate);
        if ((runoffStep <= 0.0))
        {
            ErrorCode = ERR_TIMESTEP;
            return;
        }
        OldRunoffTime = NewRunoffTime;
        NewRunoffTime += ((1000 * runoffStep));
        if ((NewRunoffTime > TotalDuration))
        {
            runoffStep = (((TotalDuration - OldRunoffTime)) / 1000.0);
            NewRunoffTime = TotalDuration;
        }
        for (j = 0; (j < Nobjects[GAGE]); j++)
        {
            gage_updatePastRain(j, R.D2I(runoffStep));
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            subcatch_setOldState(j);
        }
        if ((oldRunoffStep > 0.0))
        {
            runoff_getOutfallRunon(oldRunoffStep);
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if ((Subcatch[j].area == 0.0))
            {
                continue;
            }
            subcatch_getRunon(j);
            if (!(IgnoreSnowmelt != 0))
            {
                snow_plowSnow(j, runoffStep);
            }
        }
        HasSnow = 0;
        HasRunoff = 0;
        HasWetLids = 0;
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            if ((Subcatch[j].area == 0.0))
            {
                continue;
            }
            runoff = subcatch_getRunoff(j, runoffStep);
            if ((runoff > 0.0))
            {
                HasRunoff = 1;
            }
            if ((Subcatch[j].newSnowDepth > 0.0))
            {
                HasSnow = 1;
            }
            if ((IgnoreQuality != 0))
            {
                continue;
            }
            if ((runoff < 2.31481e-08))
            {
                surfqual_getBuildup(j, runoffStep);
            }
            if (((canSweep != 0) && (Subcatch[j].rainfall <= 2.31481e-08)))
            {
                surfqual_sweepBuildup(j, currentDate);
            }
            surfqual_getWashoff(j, runoff, runoffStep);
        }
        stats_updateMaxRunoff();
        runoff_Nsteps.v++;
        if ((Frunoff.mode == SAVE_FILE))
        {
            runoff_saveToFile(((float) (runoffStep)));
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            Subcatch[j].runon = 0.0;
        }
    }

    // runoff.c:299
    static double runoff_getTimeStep(double currentDate)
    {
        int j = 0;
        long timeStep = 0L;
        long maxStep = DryStep;
        timeStep = datetime_timeDiff(climate_getNextEvapDate(), currentDate);
        if (((timeStep > 0.0) && (timeStep < maxStep)))
        {
            maxStep = timeStep;
        }
        for (j = 0; (j < Nobjects[GAGE]); j++)
        {
            timeStep = datetime_timeDiff(gage_getNextRainDate(j, currentDate), currentDate);
            if (((timeStep > 0) && (timeStep < maxStep)))
            {
                maxStep = timeStep;
            }
        }
        if (((((IsRaining != 0) || (HasSnow != 0)) || (HasRunoff != 0)) || (HasWetLids != 0)))
        {
            timeStep = WetStep;
        }
        else
        {
            timeStep = DryStep;
        }
        if ((timeStep > maxStep))
        {
            timeStep = maxStep;
        }
        return ((double) (timeStep));
    }

    // runoff.c:335
    static void runoff_initFile()
    {
        IBox nSubcatch = new IBox(0);
        IBox nPollut = new IBox(0);
        IBox flowUnits = new IBox(0);
        CPtr fileStamp = R.bufFrom(L533, 13);
        CPtr fStamp = R.bufFrom(L533, 13);
        MaxSteps.v = 0;
        if ((Frunoff.mode == SAVE_FILE))
        {
            nSubcatch.v = Nobjects[SUBCATCH];
            nPollut.v = Nobjects[POLLUT];
            flowUnits.v = FlowUnits;
            R.fwrite(fileStamp, 0, (int) (R.strlen(fileStamp)), Frunoff.file);
            R.fwrite1(nSubcatch.v, 4, Frunoff.file);
            R.fwrite1(nPollut.v, 4, Frunoff.file);
            R.fwrite1(flowUnits.v, 4, Frunoff.file);
            MaxStepsPos = R.ftell(Frunoff.file);
            R.fwrite1(MaxSteps.v, 4, Frunoff.file);
        }
        if ((Frunoff.mode == USE_FILE))
        {
            R.fread(fStamp, 0, (int) (R.strlen(fileStamp)), Frunoff.file);
            if ((R.strcmp(fStamp, fileStamp) != 0))
            {
                report_writeErrorMsg(ERR_RUNOFF_FILE_FORMAT, L0);
                return;
            }
            nSubcatch.v = (-1);
            nPollut.v = (-1);
            flowUnits.v = (-1);
            R.fread1(nSubcatch, 4, Frunoff.file);
            R.fread1(nPollut, 4, Frunoff.file);
            R.fread1(flowUnits, 4, Frunoff.file);
            R.fread1(MaxSteps, 4, Frunoff.file);
            if (((((nSubcatch.v != Nobjects[SUBCATCH]) || (nPollut.v != Nobjects[POLLUT])) || (flowUnits.v != FlowUnits)) || (MaxSteps.v <= 0)))
            {
                report_writeErrorMsg(ERR_RUNOFF_FILE_FORMAT, L0);
            }
        }
    }

    // runoff.c:391
    static void runoff_saveToFile(float __tStep)
    {
        FBox tStep = new FBox(__tStep);
        int j = 0;
        int n = ((9 + Nobjects[POLLUT]) - 1);
        R.fwrite1(tStep.v, 4, Frunoff.file);
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            subcatch_getResults(j, 1.0, SubcatchResults);
            R.fwrite(SubcatchResults, 0, (int) (n), Frunoff.file);
        }
    }

    // runoff.c:412
    static void runoff_readFromFile()
    {
        int i = 0;
        int j = 0;
        int nResults = 0;
        int kount = 0;
        FBox tStep = new FBox(0.0f);
        TGroundwater gw = null;
        if ((runoff_Nsteps.v > MaxSteps.v))
        {
            report_writeErrorMsg(ERR_RUNOFF_FILE_END, L0);
            return;
        }
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            subcatch_setOldState(j);
        }
        kount = 0;
        kount += ((int) (R.fread1(tStep, 4, Frunoff.file)));
        nResults = ((9 + Nobjects[POLLUT]) - 1);
        for (j = 0; (j < Nobjects[SUBCATCH]); j++)
        {
            kount += ((int) (R.fread(SubcatchResults, 0, (int) (nResults), Frunoff.file)));
            Subcatch[j].newSnowDepth = (SubcatchResults[SUBCATCH_SNOWDEPTH] / UCF(RAINDEPTH));
            Subcatch[j].evapLoss = (SubcatchResults[SUBCATCH_EVAP] / UCF(RAINFALL));
            Subcatch[j].infilLoss = (SubcatchResults[SUBCATCH_INFIL] / UCF(RAINFALL));
            Subcatch[j].newRunoff = (SubcatchResults[SUBCATCH_RUNOFF] / UCF(FLOW));
            gw = Subcatch[j].groundwater;
            if ((gw != null))
            {
                gw.newFlow = (SubcatchResults[SUBCATCH_GW_FLOW] / UCF(FLOW));
                gw.lowerDepth = (Aquifer[gw.aquifer].bottomElev - ((SubcatchResults[SUBCATCH_GW_ELEV] / UCF(LENGTH))));
                gw.theta = SubcatchResults[SUBCATCH_SOIL_MOIST];
            }
            for (i = 0; (i < Nobjects[POLLUT]); i++)
            {
                Subcatch[j].newQual[i] = SubcatchResults[(SUBCATCH_WASHOFF + i)];
            }
        }
        if ((kount < (1 + (Nobjects[SUBCATCH] * nResults))))
        {
            report_writeErrorMsg(ERR_RUNOFF_FILE_READ, L0);
            return;
        }
        OldRunoffTime = NewRunoffTime;
        NewRunoffTime = (OldRunoffTime + ((tStep.v) * 1000.0));
        NewRunoffTime = (((((NewRunoffTime) <= (TotalDuration))) ? (NewRunoffTime) : (TotalDuration)));
        runoff_Nsteps.v++;
    }

    // runoff.c:491
    static void runoff_getOutfallRunon(double tStep)
    {
        int i = 0;
        int k = 0;
        int p = 0;
        double w = 0.0;
        for (i = 0; (i < Nnodes[OUTFALL]); i++)
        {
            k = Outfall[i].routeTo;
            if ((k < 0))
            {
                continue;
            }
            if ((Subcatch[k].area == 0.0))
            {
                continue;
            }
            subcatch_addRunonFlow(k, (Outfall[i].vRouted / tStep));
            massbal_updateRunoffTotals(RUNOFF_RUNON, Outfall[i].vRouted);
            Outfall[i].vRouted = 0.0;
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                w = (Outfall[i].wRouted[p] * 28.317);
                massbal_updateLoadingTotals(DEPOSITION_LOAD, p, (w * Pollut[p].mcf));
                Subcatch[k].newQual[p] += (w / tStep);
                Outfall[i].wRouted[p] = 0.0;
            }
        }
    }

}
