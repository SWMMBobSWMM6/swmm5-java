// Generated from EPA SWMM 5.2.4 (lidproc.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_lidproc {
    static TLidUnit theLidUnit = null;
    static TLidProc theLidProc = null;
    static double lidproc_Tstep = 0.0;
    static double lidproc_EvapRate = 0.0;
    static double lidproc_MaxNativeInfil = 0.0;
    static double SurfaceInflow = 0.0;
    static double SurfaceInfil = 0.0;
    static double SurfaceEvap = 0.0;
    static double SurfaceOutflow = 0.0;
    static double SurfaceVolume = 0.0;
    static double PaveEvap = 0.0;
    static double PavePerc = 0.0;
    static double PaveVolume = 0.0;
    static double SoilEvap = 0.0;
    static double SoilPerc = 0.0;
    static double SoilVolume = 0.0;
    static double StorageInflow = 0.0;
    static double StorageExfil = 0.0;
    static double StorageEvap = 0.0;
    static double StorageDrain = 0.0;
    static double StorageVolume = 0.0;
    static double[] Xold = new double[4];

    // lidproc.c:178
    static void lidproc_initWaterBalance(TLidUnit lidUnit, double initVol)
    {
        lidUnit.waterBalance.inflow = 0.0;
        lidUnit.waterBalance.evap = 0.0;
        lidUnit.waterBalance.infil = 0.0;
        lidUnit.waterBalance.surfFlow = 0.0;
        lidUnit.waterBalance.drainFlow = 0.0;
        lidUnit.waterBalance.initVol = initVol;
        lidUnit.waterBalance.finalVol = initVol;
    }

    // lidproc.c:197
    static double lidproc_getOutflow(TLidUnit lidUnit, TLidProc lidProc, double inflow, double evap, double infil, double maxInfil, double tStep, DRef lidEvap, DRef lidInfil, DRef lidDrain)
    {
        int i = 0;
        double[] x = new double[4];
        double[] xOld = new double[4];
        double[] xPrev = new double[4];
        double[] xMin = new double[4];
        double[] xMax = new double[4];
        double[] fOld = new double[4];
        double[] f = new double[4];
        double[] xTol = new double[] { 0.00328, 0.00328, 0.00328, 0.00328 };
        double omega = 0.0;
        FP2 fluxRates = (null);
        theLidProc = lidProc;
        theLidUnit = lidUnit;
        lidproc_EvapRate = evap;
        lidproc_MaxNativeInfil = maxInfil;
        lidproc_Tstep = tStep;
        x[SURF] = theLidUnit.surfaceDepth;
        x[SOIL] = theLidUnit.soilMoisture;
        x[STOR] = theLidUnit.storageDepth;
        x[PAVE] = theLidUnit.paveDepth;
        SurfaceVolume = 0.0;
        PaveVolume = 0.0;
        SoilVolume = 0.0;
        StorageVolume = 0.0;
        SurfaceInflow = inflow;
        SurfaceInfil = 0.0;
        SurfaceEvap = 0.0;
        SurfaceOutflow = 0.0;
        PaveEvap = 0.0;
        PavePerc = 0.0;
        SoilEvap = 0.0;
        SoilPerc = 0.0;
        StorageInflow = 0.0;
        StorageExfil = 0.0;
        StorageEvap = 0.0;
        StorageDrain = 0.0;
        for (i = 0; (i < 4); i++)
        {
            f[i] = 0.0;
            fOld[i] = theLidUnit.oldFluxRates[i];
            xMin[i] = 0.0;
            xMax[i] = 10000000000.0;
            Xold[i] = x[i];
        }
        if ((theLidProc.lidType == POROUS_PAVEMENT))
        {
            SurfaceInfil = 0.0;
        }
        else if ((theLidUnit.soilInfil.Ks > 0.0))
        {
            SurfaceInfil = grnampt_getInfil(theLidUnit.soilInfil, lidproc_Tstep, SurfaceInflow, theLidUnit.surfaceDepth, MOD_GREEN_AMPT);
        }
        else
        {
            SurfaceInfil = infil;
        }
        if ((theLidProc.soil.thickness > 0.0))
        {
            xMin[SOIL] = theLidProc.soil.wiltPoint;
            xMax[SOIL] = theLidProc.soil.porosity;
        }
        if ((theLidProc.pavement.thickness > 0.0))
        {
            xMax[PAVE] = theLidProc.pavement.thickness;
        }
        if ((theLidProc.storage.thickness > 0.0))
        {
            xMax[STOR] = theLidProc.storage.thickness;
        }
        if ((theLidProc.lidType == GREEN_ROOF))
        {
            xMax[STOR] = theLidProc.drainMat.thickness;
        }
        switch (theLidProc.lidType) {
            case BIO_CELL:
            case RAIN_GARDEN:
                fluxRates = F_lidproc::biocellFluxRates;
                break;
            case GREEN_ROOF:
                fluxRates = F_lidproc::greenRoofFluxRates;
                break;
            case INFIL_TRENCH:
                fluxRates = F_lidproc::trenchFluxRates;
                break;
            case POROUS_PAVEMENT:
                fluxRates = F_lidproc::pavementFluxRates;
                break;
            case RAIN_BARREL:
                fluxRates = F_lidproc::barrelFluxRates;
                break;
            case ROOF_DISCON:
                fluxRates = F_lidproc::roofFluxRates;
                break;
            case VEG_SWALE:
                fluxRates = F_lidproc::swaleFluxRates;
                omega = 0.5;
                break;
            default:
                return 0.0;
        }
        i = modpuls_solve(4, x, xOld, xPrev, xMin, xMax, xTol, fOld, f, tStep, omega, fluxRates);
        if (((theLidProc.surface.canOverflow != 0) || (theLidUnit.fullWidth == 0.0)))
        {
            SurfaceOutflow += (getSurfaceOverflowRate(new DElem(x, SURF)));
        }
        theLidUnit.surfaceDepth = x[SURF];
        theLidUnit.paveDepth = x[PAVE];
        theLidUnit.soilMoisture = x[SOIL];
        theLidUnit.storageDepth = x[STOR];
        for (i = 0; (i < 4); i++)
        {
            theLidUnit.oldFluxRates[i] = f[i];
        }
        lidEvap.set((((SurfaceEvap + PaveEvap) + SoilEvap) + StorageEvap));
        lidInfil.set(StorageExfil);
        lidDrain.set(StorageDrain);
        return SurfaceOutflow;
    }

    // lidproc.c:360
    static void lidproc_saveResults(TLidUnit lidUnit, double ucfRainfall, double ucfRainDepth)
    {
        double ucf = 0.0;
        double totalEvap = 0.0;
        double totalVolume = 0.0;
        double[] rptVars = new double[12];
        int isDry = 0;
        CPtr timeStamp = R.buf(22);
        double elapsedHrs = 0.0;
        totalEvap = (((SurfaceEvap + PaveEvap) + SoilEvap) + StorageEvap);
        totalVolume = (((SurfaceVolume + PaveVolume) + SoilVolume) + StorageVolume);
        updateWaterBalance(theLidUnit, SurfaceInflow, totalEvap, StorageExfil, SurfaceOutflow, StorageDrain, totalVolume);
        if ((((((SurfaceInflow < 2.3e-08) && (SurfaceOutflow < 2.3e-08)) && (StorageDrain < 2.3e-08)) && (StorageExfil < 2.3e-08)) && (totalEvap < 2.3e-08)))
        {
            isDry = 1;
        }
        if (!(isDry != 0))
        {
            HasWetLids = 1;
        }
        if ((lidUnit.rptFile != null))
        {
            ucf = ucfRainfall;
            rptVars[SURF_INFLOW] = (SurfaceInflow * ucf);
            rptVars[TOTAL_EVAP] = (totalEvap * ucf);
            rptVars[SURF_INFIL] = (SurfaceInfil * ucf);
            rptVars[PAVE_PERC] = (PavePerc * ucf);
            rptVars[SOIL_PERC] = (SoilPerc * ucf);
            rptVars[STOR_EXFIL] = (StorageExfil * ucf);
            rptVars[SURF_OUTFLOW] = (SurfaceOutflow * ucf);
            rptVars[STOR_DRAIN] = (StorageDrain * ucf);
            ucf = ucfRainDepth;
            rptVars[SURF_DEPTH] = (theLidUnit.surfaceDepth * ucf);
            rptVars[PAVE_DEPTH] = (theLidUnit.paveDepth * ucf);
            rptVars[SOIL_MOIST] = theLidUnit.soilMoisture;
            rptVars[STOR_DEPTH] = (theLidUnit.storageDepth * ucf);
            if (((!(isDry != 0)) && (theLidUnit.rptFile.wasDry > 1)))
            {
                R.fprintf(theLidUnit.rptFile.file, L169, theLidUnit.rptFile.results);
            }
            elapsedHrs = ((NewRunoffTime / 1000.0) / 3600.0);
            datetime_getTimeStamp(1, getDateTime(NewRunoffTime), 21, timeStamp);
            R.snprintf(theLidUnit.rptFile.results, (int) (256L), L304, timeStamp, elapsedHrs, rptVars[0], rptVars[1], rptVars[2], rptVars[3], rptVars[4], rptVars[5], rptVars[6], rptVars[7], rptVars[8], rptVars[9], rptVars[10], rptVars[11]);
            if ((isDry != 0))
            {
                if ((theLidUnit.rptFile.wasDry == 0))
                {
                    R.fprintf(theLidUnit.rptFile.file, L169, theLidUnit.rptFile.results);
                }
                theLidUnit.rptFile.wasDry++;
            }
            else
            {
                R.fprintf(theLidUnit.rptFile.file, L169, theLidUnit.rptFile.results);
                theLidUnit.rptFile.wasDry = 0;
            }
        }
    }

    // lidproc.c:469
    static void roofFluxRates(double[] x, double[] f)
    {
        DBox surfaceDepth = new DBox(x[SURF]);
        lidproc_getEvapRates(surfaceDepth.v, 0.0, 0.0, 0.0, 1.0);
        SurfaceVolume = surfaceDepth.v;
        SurfaceInfil = 0.0;
        if ((theLidProc.surface.alpha > 0.0))
        {
            SurfaceOutflow = getSurfaceOutflowRate(surfaceDepth.v);
        }
        else
        {
            getSurfaceOverflowRate(surfaceDepth);
        }
        StorageDrain = ((((((theLidProc.drain.coeff / UCF(RAINFALL))) <= (SurfaceOutflow))) ? ((theLidProc.drain.coeff / UCF(RAINFALL))) : (SurfaceOutflow)));
        SurfaceOutflow -= StorageDrain;
        f[SURF] = ((((SurfaceInflow - SurfaceEvap) - StorageDrain) - SurfaceOutflow));
    }

    // lidproc.c:491
    static void greenRoofFluxRates(double[] x, double[] f)
    {
        double surfaceDepth = 0.0;
        double soilTheta = 0.0;
        double storageDepth = 0.0;
        double availVolume = 0.0;
        double maxRate = 0.0;
        double soilThickness = theLidProc.soil.thickness;
        double storageThickness = theLidProc.storage.thickness;
        double soilPorosity = theLidProc.soil.porosity;
        double storageVoidFrac = theLidProc.storage.voidFrac;
        double soilFieldCap = theLidProc.soil.fieldCap;
        double soilWiltPoint = theLidProc.soil.wiltPoint;
        surfaceDepth = x[SURF];
        soilTheta = x[SOIL];
        storageDepth = x[STOR];
        SurfaceVolume = (surfaceDepth * theLidProc.surface.voidFrac);
        SoilVolume = (soilTheta * soilThickness);
        StorageVolume = (storageDepth * storageVoidFrac);
        availVolume = (SoilVolume - (soilWiltPoint * soilThickness));
        lidproc_getEvapRates(SurfaceVolume, 0.0, availVolume, StorageVolume, 1.0);
        if ((soilTheta >= soilPorosity))
        {
            StorageEvap = 0.0;
        }
        SoilPerc = getSoilPercRate(soilTheta);
        availVolume = (((soilTheta - soilFieldCap)) * soilThickness);
        maxRate = (((((((availVolume) >= (0.0))) ? (availVolume) : (0.0))) / lidproc_Tstep) - SoilEvap);
        SoilPerc = (((((SoilPerc) <= (maxRate))) ? (SoilPerc) : (maxRate)));
        SoilPerc = (((((SoilPerc) >= (0.0))) ? (SoilPerc) : (0.0)));
        StorageExfil = 0.0;
        StorageDrain = getDrainMatOutflow(storageDepth);
        if (((soilTheta >= soilPorosity) && (storageDepth >= storageThickness)))
        {
            maxRate = (((((SoilPerc) <= (StorageDrain))) ? (SoilPerc) : (StorageDrain)));
            SoilPerc = maxRate;
            StorageDrain = maxRate;
            SurfaceInfil = (((((SurfaceInfil) <= (maxRate))) ? (SurfaceInfil) : (maxRate)));
        }
        else
        {
            maxRate = (((storageDepth * storageVoidFrac) / lidproc_Tstep) - StorageEvap);
            if ((storageDepth >= storageThickness))
            {
                maxRate += SoilPerc;
            }
            maxRate = (((((maxRate) >= (0.0))) ? (maxRate) : (0.0)));
            StorageDrain = (((((StorageDrain) <= (maxRate))) ? (StorageDrain) : (maxRate)));
            maxRate = ((((((storageThickness - storageDepth)) * storageVoidFrac) / lidproc_Tstep) + StorageDrain) + StorageEvap);
            SoilPerc = (((((SoilPerc) <= (maxRate))) ? (SoilPerc) : (maxRate)));
            maxRate = ((((((soilPorosity - soilTheta)) * soilThickness) / lidproc_Tstep) + SoilPerc) + SoilEvap);
            SurfaceInfil = (((((SurfaceInfil) <= (maxRate))) ? (SurfaceInfil) : (maxRate)));
        }
        SurfaceOutflow = getSurfaceOutflowRate(surfaceDepth);
        f[SURF] = (((((SurfaceInflow - SurfaceEvap) - SurfaceInfil) - SurfaceOutflow)) / theLidProc.surface.voidFrac);
        f[SOIL] = ((((SurfaceInfil - SoilEvap) - SoilPerc)) / theLidProc.soil.thickness);
        f[STOR] = ((((SoilPerc - StorageEvap) - StorageDrain)) / theLidProc.storage.voidFrac);
    }

    // lidproc.c:589
    static void biocellFluxRates(double[] x, double[] f)
    {
        double surfaceDepth = 0.0;
        double soilTheta = 0.0;
        double storageDepth = 0.0;
        double availVolume = 0.0;
        double maxRate = 0.0;
        double soilThickness = theLidProc.soil.thickness;
        double soilPorosity = theLidProc.soil.porosity;
        double soilFieldCap = theLidProc.soil.fieldCap;
        double soilWiltPoint = theLidProc.soil.wiltPoint;
        double storageThickness = theLidProc.storage.thickness;
        double storageVoidFrac = theLidProc.storage.voidFrac;
        surfaceDepth = x[SURF];
        soilTheta = x[SOIL];
        storageDepth = x[STOR];
        SurfaceVolume = (surfaceDepth * theLidProc.surface.voidFrac);
        SoilVolume = (soilTheta * soilThickness);
        StorageVolume = (storageDepth * storageVoidFrac);
        availVolume = (SoilVolume - (soilWiltPoint * soilThickness));
        lidproc_getEvapRates(SurfaceVolume, 0.0, availVolume, StorageVolume, 1.0);
        if ((soilTheta >= soilPorosity))
        {
            StorageEvap = 0.0;
        }
        SoilPerc = getSoilPercRate(soilTheta);
        availVolume = (((soilTheta - soilFieldCap)) * soilThickness);
        maxRate = (((((((availVolume) >= (0.0))) ? (availVolume) : (0.0))) / lidproc_Tstep) - SoilEvap);
        SoilPerc = (((((SoilPerc) <= (maxRate))) ? (SoilPerc) : (maxRate)));
        SoilPerc = (((((SoilPerc) >= (0.0))) ? (SoilPerc) : (0.0)));
        StorageExfil = getStorageExfilRate();
        StorageDrain = 0.0;
        if ((theLidProc.drain.coeff > 0.0))
        {
            StorageDrain = getStorageDrainRate(storageDepth, soilTheta, 0.0, surfaceDepth);
        }
        if ((storageThickness == 0.0))
        {
            StorageEvap = 0.0;
            maxRate = (((((SoilPerc) <= (StorageExfil))) ? (SoilPerc) : (StorageExfil)));
            SoilPerc = maxRate;
            StorageExfil = maxRate;
            maxRate = ((((((soilPorosity - soilTheta)) * soilThickness) / lidproc_Tstep) + SoilPerc) + SoilEvap);
            SurfaceInfil = (((((SurfaceInfil) <= (maxRate))) ? (SurfaceInfil) : (maxRate)));
        }
        else
        {
            if (((soilTheta >= soilPorosity) && (storageDepth >= storageThickness)))
            {
                maxRate = (StorageExfil + StorageDrain);
                if ((SoilPerc < maxRate))
                {
                    maxRate = SoilPerc;
                    if ((maxRate > StorageExfil))
                    {
                        StorageDrain = (maxRate - StorageExfil);
                    }
                    else
                    {
                        StorageExfil = maxRate;
                        StorageDrain = 0.0;
                    }
                }
                else
                {
                    SoilPerc = maxRate;
                }
                SurfaceInfil = (((((SurfaceInfil) <= (maxRate))) ? (SurfaceInfil) : (maxRate)));
            }
            else
            {
                maxRate = ((SoilPerc - StorageEvap) + ((storageDepth * storageVoidFrac) / lidproc_Tstep));
                StorageExfil = (((((StorageExfil) <= (maxRate))) ? (StorageExfil) : (maxRate)));
                StorageExfil = (((((StorageExfil) >= (0.0))) ? (StorageExfil) : (0.0)));
                if ((StorageDrain > 0.0))
                {
                    maxRate = ((-StorageExfil) - StorageEvap);
                    if ((storageDepth >= storageThickness))
                    {
                        maxRate += SoilPerc;
                    }
                    if ((theLidProc.drain.offset <= storageDepth))
                    {
                        maxRate += ((((storageDepth - theLidProc.drain.offset)) * storageVoidFrac) / lidproc_Tstep);
                    }
                    maxRate = (((((maxRate) >= (0.0))) ? (maxRate) : (0.0)));
                    StorageDrain = (((((StorageDrain) <= (maxRate))) ? (StorageDrain) : (maxRate)));
                }
                maxRate = (((StorageExfil + StorageDrain) + StorageEvap) + ((((storageThickness - storageDepth)) * storageVoidFrac) / lidproc_Tstep));
                SoilPerc = (((((SoilPerc) <= (maxRate))) ? (SoilPerc) : (maxRate)));
                maxRate = ((((((soilPorosity - soilTheta)) * soilThickness) / lidproc_Tstep) + SoilPerc) + SoilEvap);
                SurfaceInfil = (((((SurfaceInfil) <= (maxRate))) ? (SurfaceInfil) : (maxRate)));
            }
        }
        SurfaceOutflow = getSurfaceOutflowRate(surfaceDepth);
        f[SURF] = (((((SurfaceInflow - SurfaceEvap) - SurfaceInfil) - SurfaceOutflow)) / theLidProc.surface.voidFrac);
        f[SOIL] = ((((SurfaceInfil - SoilEvap) - SoilPerc)) / theLidProc.soil.thickness);
        if ((storageThickness == 0.0))
        {
            f[STOR] = 0.0;
        }
        else
        {
            f[STOR] = (((((SoilPerc - StorageEvap) - StorageExfil) - StorageDrain)) / theLidProc.storage.voidFrac);
        }
    }

    // lidproc.c:735
    static void trenchFluxRates(double[] x, double[] f)
    {
        double surfaceDepth = 0.0;
        double storageDepth = 0.0;
        double availVolume = 0.0;
        double maxRate = 0.0;
        double storageThickness = theLidProc.storage.thickness;
        double storageVoidFrac = theLidProc.storage.voidFrac;
        surfaceDepth = x[SURF];
        storageDepth = x[STOR];
        SurfaceVolume = (surfaceDepth * theLidProc.surface.voidFrac);
        SoilVolume = 0.0;
        StorageVolume = (storageDepth * storageVoidFrac);
        availVolume = (((storageThickness - storageDepth)) * storageVoidFrac);
        lidproc_getEvapRates(SurfaceVolume, 0.0, 0.0, StorageVolume, 1.0);
        if ((surfaceDepth > 0.0))
        {
            StorageEvap = 0.0;
        }
        StorageInflow = (SurfaceInflow + (SurfaceVolume / lidproc_Tstep));
        StorageExfil = getStorageExfilRate();
        StorageDrain = 0.0;
        if ((theLidProc.drain.coeff > 0.0))
        {
            StorageDrain = getStorageDrainRate(storageDepth, 0.0, 0.0, surfaceDepth);
        }
        maxRate = ((StorageInflow - StorageEvap) + ((storageDepth * storageVoidFrac) / lidproc_Tstep));
        StorageExfil = (((((StorageExfil) <= (maxRate))) ? (StorageExfil) : (maxRate)));
        StorageExfil = (((((StorageExfil) >= (0.0))) ? (StorageExfil) : (0.0)));
        if ((StorageDrain > 0.0))
        {
            maxRate = ((-StorageExfil) - StorageEvap);
            if ((storageDepth >= storageThickness))
            {
                maxRate += StorageInflow;
            }
            if ((theLidProc.drain.offset <= storageDepth))
            {
                maxRate += ((((storageDepth - theLidProc.drain.offset)) * storageVoidFrac) / lidproc_Tstep);
            }
            maxRate = (((((maxRate) >= (0.0))) ? (maxRate) : (0.0)));
            StorageDrain = (((((StorageDrain) <= (maxRate))) ? (StorageDrain) : (maxRate)));
        }
        maxRate = (((((((storageThickness - storageDepth)) * storageVoidFrac) / lidproc_Tstep) + StorageExfil) + StorageEvap) + StorageDrain);
        StorageInflow = (((((StorageInflow) <= (maxRate))) ? (StorageInflow) : (maxRate)));
        SurfaceInfil = StorageInflow;
        SurfaceOutflow = getSurfaceOutflowRate(surfaceDepth);
        f[SURF] = (((((SurfaceInflow - SurfaceEvap) - StorageInflow) - SurfaceOutflow)) / theLidProc.surface.voidFrac);
        ;
        f[STOR] = (((((StorageInflow - StorageEvap) - StorageExfil) - StorageDrain)) / theLidProc.storage.voidFrac);
        f[SOIL] = 0.0;
    }

    // lidproc.c:823
    static void pavementFluxRates(double[] x, double[] f)
    {
        double surfaceDepth = 0.0;
        double paveDepth = 0.0;
        double soilTheta = 0.0;
        double storageDepth = 0.0;
        double pervFrac = ((1.0 - theLidProc.pavement.impervFrac));
        double storageInflow = 0.0;
        double availVolume = 0.0;
        double maxRate = 0.0;
        double paveVoidFrac = (theLidProc.pavement.voidFrac * pervFrac);
        double paveThickness = theLidProc.pavement.thickness;
        double soilThickness = theLidProc.soil.thickness;
        double soilPorosity = theLidProc.soil.porosity;
        double soilFieldCap = theLidProc.soil.fieldCap;
        double soilWiltPoint = theLidProc.soil.wiltPoint;
        double storageThickness = theLidProc.storage.thickness;
        double storageVoidFrac = theLidProc.storage.voidFrac;
        surfaceDepth = x[SURF];
        paveDepth = x[PAVE];
        soilTheta = x[SOIL];
        storageDepth = x[STOR];
        SurfaceVolume = (surfaceDepth * theLidProc.surface.voidFrac);
        PaveVolume = (paveDepth * paveVoidFrac);
        SoilVolume = (soilTheta * soilThickness);
        StorageVolume = (storageDepth * storageVoidFrac);
        availVolume = (SoilVolume - (soilWiltPoint * soilThickness));
        lidproc_getEvapRates(SurfaceVolume, PaveVolume, availVolume, StorageVolume, pervFrac);
        if (((paveDepth >= paveThickness) || (((soilThickness > 0.0) && (soilTheta >= soilPorosity)))))
        {
            StorageEvap = 0.0;
        }
        SurfaceInfil = (SurfaceInflow + ((SurfaceVolume / lidproc_Tstep)));
        PavePerc = (getPavementPermRate() * pervFrac);
        SurfaceInfil = (((((SurfaceInfil) <= (PavePerc))) ? (SurfaceInfil) : (PavePerc)));
        maxRate = (((PaveVolume / lidproc_Tstep) + SurfaceInfil) - PaveEvap);
        maxRate = (((((maxRate) >= (0.0))) ? (maxRate) : (0.0)));
        PavePerc = (((((PavePerc) <= (maxRate))) ? (PavePerc) : (maxRate)));
        if ((soilThickness > 0.0))
        {
            SoilPerc = getSoilPercRate(soilTheta);
            availVolume = (((soilTheta - soilFieldCap)) * soilThickness);
            maxRate = (((((((availVolume) >= (0.0))) ? (availVolume) : (0.0))) / lidproc_Tstep) - SoilEvap);
            SoilPerc = (((((SoilPerc) <= (maxRate))) ? (SoilPerc) : (maxRate)));
            SoilPerc = (((((SoilPerc) >= (0.0))) ? (SoilPerc) : (0.0)));
        }
        else
        {
            SoilPerc = PavePerc;
        }
        StorageExfil = getStorageExfilRate();
        StorageDrain = 0.0;
        if ((theLidProc.drain.coeff > 0.0))
        {
            StorageDrain = getStorageDrainRate(storageDepth, soilTheta, paveDepth, surfaceDepth);
        }
        if ((((soilThickness == 0.0) && (storageDepth >= storageThickness)) && (paveDepth >= paveThickness)))
        {
            maxRate = ((StorageEvap + StorageDrain) + StorageExfil);
            if ((PavePerc > maxRate))
            {
                PavePerc = maxRate;
            }
            else
            {
                StorageExfil = (((((StorageExfil) <= (PavePerc))) ? (StorageExfil) : (PavePerc)));
                StorageDrain = (PavePerc - StorageExfil);
            }
            SoilPerc = PavePerc;
            SurfaceInfil = (((((SurfaceInfil) <= (PavePerc))) ? (SurfaceInfil) : (PavePerc)));
        }
        else if (((((soilThickness > 0) && (storageDepth >= storageThickness)) && (soilTheta >= soilPorosity)) && (paveDepth >= paveThickness)))
        {
            maxRate = (StorageExfil + StorageDrain);
            if ((SoilPerc < maxRate))
            {
                maxRate = SoilPerc;
            }
            else
            {
                maxRate = (((((maxRate) <= (PavePerc))) ? (maxRate) : (PavePerc)));
            }
            if ((maxRate > StorageExfil))
            {
                StorageDrain = (maxRate - StorageExfil);
            }
            else
            {
                StorageExfil = maxRate;
                StorageDrain = 0.0;
            }
            SoilPerc = maxRate;
            PavePerc = maxRate;
            SurfaceInfil = (((((SurfaceInfil) <= (PavePerc))) ? (SurfaceInfil) : (PavePerc)));
        }
        else if ((((soilThickness > 0.0) && (storageDepth >= storageThickness)) && (soilTheta >= soilPorosity)))
        {
            maxRate = (StorageDrain + StorageExfil);
            if ((SoilPerc > maxRate))
            {
                SoilPerc = maxRate;
            }
            else
            {
                StorageExfil = (((((StorageExfil) <= (SoilPerc))) ? (StorageExfil) : (SoilPerc)));
                StorageDrain = (SoilPerc - StorageExfil);
            }
            PavePerc = (((((PavePerc) <= (SoilPerc))) ? (PavePerc) : (SoilPerc)));
            availVolume = (((paveThickness - paveDepth)) * paveVoidFrac);
            maxRate = (((availVolume / lidproc_Tstep) + PavePerc) + PaveEvap);
            SurfaceInfil = (((((SurfaceInfil) <= (maxRate))) ? (SurfaceInfil) : (maxRate)));
        }
        else if ((((soilThickness > 0.0) && (paveDepth >= paveThickness)) && (soilTheta >= soilPorosity)))
        {
            PavePerc = (((((PavePerc) <= (SoilPerc))) ? (PavePerc) : (SoilPerc)));
            SoilPerc = PavePerc;
            SurfaceInfil = (((((SurfaceInfil) <= (PavePerc))) ? (SurfaceInfil) : (PavePerc)));
            maxRate = ((((((((StorageVolume / lidproc_Tstep) + SoilPerc) - StorageEvap)) >= (0.0))) ? ((((StorageVolume / lidproc_Tstep) + SoilPerc) - StorageEvap)) : (0.0)));
            StorageExfil = (((((StorageExfil) <= (maxRate))) ? (StorageExfil) : (maxRate)));
        }
        else
        {
            maxRate = ((SoilPerc - StorageEvap) + (StorageVolume / lidproc_Tstep));
            maxRate = (((((0.0) >= (maxRate))) ? (0.0) : (maxRate)));
            StorageExfil = (((((StorageExfil) <= (maxRate))) ? (StorageExfil) : (maxRate)));
            if ((StorageDrain > 0.0))
            {
                maxRate = ((-StorageExfil) - StorageEvap);
                if ((storageDepth >= storageThickness))
                {
                    maxRate += SoilPerc;
                }
                if ((theLidProc.drain.offset <= storageDepth))
                {
                    maxRate += ((((storageDepth - theLidProc.drain.offset)) * storageVoidFrac) / lidproc_Tstep);
                }
                maxRate = (((((maxRate) >= (0.0))) ? (maxRate) : (0.0)));
                StorageDrain = (((((StorageDrain) <= (maxRate))) ? (StorageDrain) : (maxRate)));
            }
            availVolume = (((storageThickness - storageDepth)) * storageVoidFrac);
            maxRate = ((((availVolume / lidproc_Tstep) + StorageEvap) + StorageDrain) + StorageExfil);
            maxRate = (((((maxRate) >= (0.0))) ? (maxRate) : (0.0)));
            if ((soilThickness > 0.0))
            {
                SoilPerc = (((((SoilPerc) <= (maxRate))) ? (SoilPerc) : (maxRate)));
                maxRate = (((((soilPorosity - soilTheta)) * soilThickness) / lidproc_Tstep) + SoilPerc);
            }
            PavePerc = (((((PavePerc) <= (maxRate))) ? (PavePerc) : (maxRate)));
            availVolume = (((paveThickness - paveDepth)) * paveVoidFrac);
            maxRate = (((availVolume / lidproc_Tstep) + PavePerc) + PaveEvap);
            SurfaceInfil = (((((SurfaceInfil) <= (maxRate))) ? (SurfaceInfil) : (maxRate)));
        }
        SurfaceOutflow = getSurfaceOutflowRate(surfaceDepth);
        f[SURF] = (((SurfaceInflow - SurfaceEvap) - SurfaceInfil) - SurfaceOutflow);
        f[PAVE] = ((((SurfaceInfil - PaveEvap) - PavePerc)) / paveVoidFrac);
        if ((theLidProc.soil.thickness > 0.0))
        {
            f[SOIL] = ((((PavePerc - SoilEvap) - SoilPerc)) / soilThickness);
            storageInflow = SoilPerc;
        }
        else
        {
            f[SOIL] = 0.0;
            storageInflow = PavePerc;
            SoilPerc = 0.0;
        }
        f[STOR] = (((((storageInflow - StorageEvap) - StorageExfil) - StorageDrain)) / storageVoidFrac);
    }

    // lidproc.c:1061
    static void swaleFluxRates(double[] x, double[] f)
    {
        double depth = 0.0;
        double topWidth = 0.0;
        double botWidth = 0.0;
        double length = 0.0;
        double surfInflow = 0.0;
        double surfWidth = 0.0;
        double surfArea = 0.0;
        double flowArea = 0.0;
        double lidArea = 0.0;
        double hydRadius = 0.0;
        double slope = 0.0;
        double volume = 0.0;
        double dVdT = 0.0;
        double dStore = 0.0;
        double xDepth = 0.0;
        depth = x[SURF];
        depth = (((((depth) <= (theLidProc.surface.thickness))) ? (depth) : (theLidProc.surface.thickness)));
        dStore = 0.0;
        slope = theLidProc.surface.sideSlope;
        topWidth = theLidUnit.fullWidth;
        topWidth = (((((topWidth) >= (0.5))) ? (topWidth) : (0.5)));
        botWidth = (topWidth - ((2.0 * slope) * theLidProc.surface.thickness));
        if ((botWidth < 0.5))
        {
            botWidth = 0.5;
            slope = ((0.5 * ((topWidth - 0.5))) / theLidProc.surface.thickness);
        }
        lidArea = theLidUnit.area;
        length = (lidArea / topWidth);
        surfWidth = (botWidth + ((2.0 * slope) * depth));
        surfArea = (length * surfWidth);
        flowArea = (((depth * ((botWidth + (slope * depth))))) * theLidProc.surface.voidFrac);
        volume = (length * flowArea);
        surfInflow = (SurfaceInflow * lidArea);
        SurfaceEvap = (lidproc_EvapRate * surfArea);
        SurfaceEvap = (((((SurfaceEvap) <= ((volume / lidproc_Tstep)))) ? (SurfaceEvap) : ((volume / lidproc_Tstep))));
        StorageExfil = (SurfaceInfil * surfArea);
        xDepth = (depth - dStore);
        if ((xDepth <= 1e-10))
        {
            SurfaceOutflow = 0.0;
        }
        else
        {
            flowArea -= (((dStore * ((botWidth + (slope * dStore))))) * theLidProc.surface.voidFrac);
            if ((flowArea < 1e-10))
            {
                SurfaceOutflow = 0.0;
            }
            else
            {
                botWidth = (botWidth + ((2.0 * dStore) * slope));
                hydRadius = (botWidth + ((2.0 * xDepth) * Math.sqrt((1.0 + (slope * slope)))));
                hydRadius = (flowArea / hydRadius);
                SurfaceOutflow = ((theLidProc.surface.alpha * flowArea) * Glibm.pow(hydRadius, (2.0 / 3.0)));
            }
        }
        dVdT = (((surfInflow - SurfaceEvap) - StorageExfil) - SurfaceOutflow);
        if (((depth == theLidProc.surface.thickness) && (dVdT > 0.0)))
        {
            SurfaceOutflow += dVdT;
            dVdT = 0.0;
        }
        SurfaceEvap /= lidArea;
        StorageExfil /= lidArea;
        SurfaceOutflow /= lidArea;
        f[SURF] = (dVdT / surfArea);
        f[SOIL] = 0.0;
        f[STOR] = 0.0;
        SurfaceVolume = (volume / lidArea);
        SoilVolume = 0.0;
        StorageVolume = 0.0;
    }

    // lidproc.c:1176
    static void barrelFluxRates(double[] x, double[] f)
    {
        double storageDepth = x[STOR];
        double head = 0.0;
        double maxValue = 0.0;
        SurfaceVolume = 0.0;
        SoilVolume = 0.0;
        StorageVolume = storageDepth;
        SurfaceInfil = 0.0;
        SurfaceOutflow = 0.0;
        StorageDrain = 0.0;
        if (((theLidProc.drain.delay == 0.0) || (theLidUnit.dryTime >= theLidProc.drain.delay)))
        {
            head = (storageDepth - theLidProc.drain.offset);
            if ((head > 0.0))
            {
                StorageDrain = getStorageDrainRate(storageDepth, 0.0, 0.0, 0.0);
                maxValue = ((head / lidproc_Tstep));
                StorageDrain = (((((StorageDrain) <= (maxValue))) ? (StorageDrain) : (maxValue)));
            }
        }
        StorageInflow = SurfaceInflow;
        maxValue = ((((theLidProc.storage.thickness - storageDepth)) / lidproc_Tstep) + StorageDrain);
        StorageInflow = (((((StorageInflow) <= (maxValue))) ? (StorageInflow) : (maxValue)));
        SurfaceInfil = StorageInflow;
        f[SURF] = (SurfaceInflow - StorageInflow);
        f[STOR] = (StorageInflow - StorageDrain);
        f[SOIL] = 0.0;
    }

    // lidproc.c:1226
    static double getSurfaceOutflowRate(double depth)
    {
        double delta = 0.0;
        double outflow = 0.0;
        delta = (depth - theLidProc.surface.thickness);
        if ((delta < 0.0))
        {
            return 0.0;
        }
        outflow = (((theLidProc.surface.alpha * Glibm.pow(delta, (5.0 / 3.0))) * theLidUnit.fullWidth) / theLidUnit.area);
        outflow = (((((outflow) <= ((delta / lidproc_Tstep)))) ? (outflow) : ((delta / lidproc_Tstep))));
        return outflow;
    }

    // lidproc.c:1251
    static double getPavementPermRate()
    {
        double permReduction = 0.0;
        double clogFactor = theLidProc.pavement.clogFactor;
        double regenDays = theLidProc.pavement.regenDays;
        if ((clogFactor > 0.0))
        {
            if ((regenDays > 0.0))
            {
                if ((((OldRunoffTime / 1000.0) / 86400) >= theLidUnit.nextRegenDay))
                {
                    theLidUnit.volTreated *= ((1.0 - theLidProc.pavement.regenDegree));
                    theLidUnit.nextRegenDay += regenDays;
                }
            }
            permReduction = (theLidUnit.volTreated / clogFactor);
            permReduction = (((((permReduction) <= (1.0))) ? (permReduction) : (1.0)));
        }
        return (theLidProc.pavement.kSat * ((1.0 - permReduction)));
    }

    // lidproc.c:1293
    static double getSoilPercRate(double theta)
    {
        double delta = 0.0;
        if ((theta <= theLidProc.soil.fieldCap))
        {
            return 0.0;
        }
        delta = (theLidProc.soil.porosity - theta);
        return (theLidProc.soil.kSat * Glibm.exp(((-delta) * theLidProc.soil.kSlope)));
    }

    // lidproc.c:1313
    static double getStorageExfilRate()
    {
        double infil = 0.0;
        double clogFactor = 0.0;
        if ((theLidProc.storage.kSat == 0.0))
        {
            return 0.0;
        }
        if ((lidproc_MaxNativeInfil == 0.0))
        {
            return 0.0;
        }
        clogFactor = theLidProc.storage.clogFactor;
        if ((clogFactor > 0.0))
        {
            clogFactor = (theLidUnit.waterBalance.inflow / clogFactor);
            clogFactor = (((((clogFactor) <= (1.0))) ? (clogFactor) : (1.0)));
        }
        infil = (theLidProc.storage.kSat * ((1.0 - clogFactor)));
        return (((((infil) <= (lidproc_MaxNativeInfil))) ? (infil) : (lidproc_MaxNativeInfil)));
    }

    // lidproc.c:1344
    static double getStorageDrainRate(double storageDepth, double soilTheta, double paveDepth, double surfaceDepth)
    {
        int curve = theLidProc.drain.qCurve;
        double head = storageDepth;
        double outflow = 0.0;
        double paveThickness = theLidProc.pavement.thickness;
        double soilThickness = theLidProc.soil.thickness;
        double soilPorosity = theLidProc.soil.porosity;
        double soilFieldCap = theLidProc.soil.fieldCap;
        double storageThickness = theLidProc.storage.thickness;
        if ((storageDepth >= storageThickness))
        {
            if ((soilThickness > 0.0))
            {
                if ((soilTheta > soilFieldCap))
                {
                    head += ((((soilTheta - soilFieldCap)) / ((soilPorosity - soilFieldCap))) * soilThickness);
                    if ((soilTheta >= soilPorosity))
                    {
                        if ((paveThickness > 0.0))
                        {
                            head += paveDepth;
                            if ((paveDepth >= paveThickness))
                            {
                                head += surfaceDepth;
                            }
                        }
                        else
                        {
                            head += surfaceDepth;
                        }
                    }
                }
            }
            else if ((paveThickness > 0.0))
            {
                head += paveDepth;
                if ((paveDepth >= paveThickness))
                {
                    head += surfaceDepth;
                }
            }
        }
        if (((theLidUnit.oldDrainFlow == 0.0) && (head <= theLidProc.drain.hOpen)))
        {
            return 0.0;
        }
        if (((theLidUnit.oldDrainFlow > 0.0) && (head <= theLidProc.drain.hClose)))
        {
            return 0.0;
        }
        head -= theLidProc.drain.offset;
        if ((head > 1e-10))
        {
            head *= (UCF(RAINDEPTH));
            outflow = (theLidProc.drain.coeff * Glibm.pow(head, theLidProc.drain.expon));
            if ((curve >= 0))
            {
                outflow *= (table_lookup(Curve[curve], head));
            }
            outflow /= (UCF(RAINFALL));
        }
        return outflow;
    }

    // lidproc.c:1436
    static double getDrainMatOutflow(double depth)
    {
        double result = SoilPerc;
        if ((theLidProc.drainMat.alpha > 0.0))
        {
            result = ((((theLidProc.drainMat.alpha * Glibm.pow(depth, (5.0 / 3.0))) * theLidUnit.fullWidth) / theLidUnit.area) * theLidProc.drainMat.voidFrac);
        }
        return result;
    }

    // lidproc.c:1458
    static void lidproc_getEvapRates(double surfaceVol, double paveVol, double soilVol, double storageVol, double pervFrac)
    {
        double availEvap = 0.0;
        availEvap = lidproc_EvapRate;
        SurfaceEvap = (((((availEvap) <= ((surfaceVol / lidproc_Tstep)))) ? (availEvap) : ((surfaceVol / lidproc_Tstep))));
        SurfaceEvap = (((((0.0) >= (SurfaceEvap))) ? (0.0) : (SurfaceEvap)));
        availEvap = (((((0.0) >= (((availEvap - SurfaceEvap))))) ? (0.0) : (((availEvap - SurfaceEvap)))));
        availEvap *= pervFrac;
        if ((SurfaceInfil > 0.0))
        {
            PaveEvap = 0.0;
            SoilEvap = 0.0;
            StorageEvap = 0.0;
        }
        else
        {
            PaveEvap = (((((availEvap) <= ((paveVol / lidproc_Tstep)))) ? (availEvap) : ((paveVol / lidproc_Tstep))));
            availEvap = (((((0.0) >= (((availEvap - PaveEvap))))) ? (0.0) : (((availEvap - PaveEvap)))));
            SoilEvap = (((((availEvap) <= ((soilVol / lidproc_Tstep)))) ? (availEvap) : ((soilVol / lidproc_Tstep))));
            availEvap = (((((0.0) >= (((availEvap - SoilEvap))))) ? (0.0) : (((availEvap - SoilEvap)))));
            StorageEvap = (((((availEvap) <= ((storageVol / lidproc_Tstep)))) ? (availEvap) : ((storageVol / lidproc_Tstep))));
        }
    }

    // lidproc.c:1503
    static double getSurfaceOverflowRate(DRef surfaceDepth)
    {
        double delta = (surfaceDepth.get() - theLidProc.surface.thickness);
        if ((delta <= 0.0))
        {
            return 0.0;
        }
        surfaceDepth.set(theLidProc.surface.thickness);
        return ((delta * theLidProc.surface.voidFrac) / lidproc_Tstep);
    }

    // lidproc.c:1518
    static void updateWaterBalance(TLidUnit lidUnit, double inflow, double evap, double infil, double surfFlow, double drainFlow, double storage)
    {
        lidUnit.volTreated += (inflow * lidproc_Tstep);
        lidUnit.waterBalance.inflow += (inflow * lidproc_Tstep);
        lidUnit.waterBalance.evap += (evap * lidproc_Tstep);
        lidUnit.waterBalance.infil += (infil * lidproc_Tstep);
        lidUnit.waterBalance.surfFlow += (surfFlow * lidproc_Tstep);
        lidUnit.waterBalance.drainFlow += (drainFlow * lidproc_Tstep);
        lidUnit.waterBalance.finalVol = storage;
    }

    // lidproc.c:1544
    static int modpuls_solve(int n, double[] x, double[] xOld, double[] xPrev, double[] xMin, double[] xMax, double[] xTol, double[] qOld, double[] q, double dt, double omega, FP2 derivs)
    {
        int i = 0;
        int canStop = 0;
        int steps = 1;
        int maxSteps = 20;
        for (i = 0; (i < n); i++)
        {
            xOld[i] = x[i];
            xPrev[i] = x[i];
        }
        while ((steps < maxSteps))
        {
            canStop = 1;
            derivs.call(x, q);
            for (i = 0; (i < n); i++)
            {
                x[i] = (xOld[i] + ((((omega * qOld[i]) + (((1.0 - omega)) * q[i]))) * dt));
                x[i] = (((((x[i]) <= (xMax[i]))) ? (x[i]) : (xMax[i])));
                x[i] = (((((x[i]) >= (xMin[i]))) ? (x[i]) : (xMin[i])));
                if (((omega > 0.0) && (Math.abs((x[i] - xPrev[i])) > xTol[i])))
                {
                    canStop = 0;
                }
                xPrev[i] = x[i];
            }
            if ((canStop != 0))
            {
                return steps;
            }
            steps++;
        }
        return 0;
    }

}
