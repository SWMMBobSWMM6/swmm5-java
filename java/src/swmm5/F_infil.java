// Generated from EPA SWMM 5.2.4 (infil.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_infil {
    static TInfil[] Infil = null;
    static double Fumax = 0.0;
    static double InfilFactor = 0.0;

    // infil.c:101
    static void infil_create(int n)
    {
        Infil = R.fill(new TInfil[(int) (n)], TInfil::new);
        if ((Infil == null))
        {
            ErrorCode = ERR_MEMORY;
        }
        InfilFactor = 1.0;
        return;
    }

    // infil.c:116
    static void infil_delete()
    {
        {
            if ((Infil != null))
            {
                R.free(Infil);
                Infil = (null);
            }
        }
        ;
    }

    // infil.c:128
    static int infil_readParams(int m, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int j = 0;
        int n = 0;
        int status = 0;
        double[] x = new double[5];
        j = project_findObject(SUBCATCH, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        i = findmatch(tok[(ntoks - 1)], InfilModelWords);
        if ((i >= 0))
        {
            m = i;
            ntoks--;
        }
        if ((m == HORTON))
        {
            n = 5;
        }
        else if ((m == MOD_HORTON))
        {
            n = 5;
        }
        else if ((m == GREEN_AMPT))
        {
            n = 4;
        }
        else if ((m == MOD_GREEN_AMPT))
        {
            n = 4;
        }
        else if ((m == CURVE_NUMBER))
        {
            n = 4;
        }
        else
        {
            return 0;
        }
        if ((ntoks < n))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        for (i = 0; (i < 5); i++)
        {
            x[i] = 0.0;
        }
        for (i = 1; (i < n); i++)
        {
            if (!(getDouble(tok[i], new DElem(x, (i - 1))) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[i]);
            }
        }
        if (((((m == HORTON) || (m == MOD_HORTON))) && (ntoks > n)))
        {
            if (!(getDouble(tok[n], new DElem(x, (n - 1))) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[n]);
            }
        }
        Subcatch[j].infil = j;
        Subcatch[j].infilModel = m;
        switch (m) {
            case HORTON:
            case MOD_HORTON:
                status = horton_setParams(Infil[j].horton, x);
                break;
            case GREEN_AMPT:
            case MOD_GREEN_AMPT:
                status = grnampt_setParams(Infil[j].grnAmpt, x);
                break;
            case CURVE_NUMBER:
                status = curvenum_setParams(Infil[j].curveNum, x);
                break;
            default:
                status = 1;
        }
        if (!(status != 0))
        {
            return error_setInpError(ERR_INFIL_PARAMS, L0);
        }
        return 0;
    }

    // infil.c:201
    static void infil_initState(int j)
    {
        switch (Subcatch[j].infilModel) {
            case HORTON:
            case MOD_HORTON:
                horton_initState(Infil[j].horton);
                break;
            case GREEN_AMPT:
            case MOD_GREEN_AMPT:
                grnampt_initState(Infil[j].grnAmpt);
                break;
            case CURVE_NUMBER:
                curvenum_initState(Infil[j].curveNum);
                break;
        }
    }

    // infil.c:221
    static void infil_getState(int j, double[] x)
    {
        switch (Subcatch[j].infilModel) {
            case HORTON:
            case MOD_HORTON:
                horton_getState(Infil[j].horton, x);
                break;
            case GREEN_AMPT:
            case MOD_GREEN_AMPT:
                grnampt_getState(Infil[j].grnAmpt, x);
                break;
            case CURVE_NUMBER:
                curvenum_getState(Infil[j].curveNum, x);
                break;
        }
    }

    // infil.c:241
    static void infil_setState(int j, double[] x)
    {
        switch (Subcatch[j].infilModel) {
            case HORTON:
            case MOD_HORTON:
                horton_setState(Infil[j].horton, x);
                break;
            case GREEN_AMPT:
            case MOD_GREEN_AMPT:
                grnampt_setState(Infil[j].grnAmpt, x);
                break;
            case CURVE_NUMBER:
                curvenum_setState(Infil[j].curveNum, x);
                break;
        }
    }

    // infil.c:262
    static void infil_setInfilFactor(int j)
    {
        int m = 0;
        int p = 0;
        InfilFactor = Adjust.hydconFactor;
        if ((j >= 0))
        {
            p = Subcatch[j].infilPattern;
            if (((p >= 0) && (Pattern[p].type == MONTHLY_PATTERN)))
            {
                m = (datetime_monthOfYear(getDateTime(OldRunoffTime)) - 1);
                InfilFactor = Pattern[p].factor[m];
            }
        }
    }

    // infil.c:288
    static double infil_getInfil(int j, double tstep, double rainfall, double runon, double depth)
    {
        switch (Subcatch[j].infilModel) {
            case HORTON:
                return horton_getInfil(Infil[j].horton, tstep, (rainfall + runon), depth);
            case MOD_HORTON:
                return modHorton_getInfil(Infil[j].horton, tstep, (rainfall + runon), depth);
            case GREEN_AMPT:
            case MOD_GREEN_AMPT:
                return grnampt_getInfil(Infil[j].grnAmpt, tstep, (rainfall + runon), depth, Subcatch[j].infilModel);
            case CURVE_NUMBER:
                depth += (runon * tstep);
                return curvenum_getInfil(Infil[j].curveNum, tstep, rainfall, depth);
            default:
                return 0.0;
        }
    }

    // infil.c:325
    static int horton_setParams(THorton infil, double[] p)
    {
        int k = 0;
        for (k = 0; (k < 5); k++)
        {
            if ((p[k] < 0.0))
            {
                return 0;
            }
        }
        infil.f0 = (p[0] / UCF(RAINFALL));
        infil.fmin = (p[1] / UCF(RAINFALL));
        infil.decay = (p[2] / 3600.0);
        if ((p[3] == 0.0))
        {
            p[3] = 1e-06;
        }
        infil.regen = (((-(Glibm.log((1.0 - 0.98)))) / p[3]) / 86400);
        infil.Fmax = (p[4] / UCF(RAINDEPTH));
        if ((infil.f0 < infil.fmin))
        {
            return 0;
        }
        return 1;
    }

    // infil.c:357
    static void horton_initState(THorton infil)
    {
        infil.tp = 0.0;
        infil.Fe = 0.0;
    }

    // infil.c:370
    static void horton_getState(THorton infil, double[] x)
    {
        x[0] = infil.tp;
        x[1] = infil.Fe;
    }

    // infil.c:376
    static void horton_setState(THorton infil, double[] x)
    {
        infil.tp = x[0];
        infil.Fe = x[1];
    }

    // infil.c:384
    static double horton_getInfil(THorton infil, double tstep, double irate, double depth)
    {
        int iter = 0;
        double fa = 0.0;
        double fp = 0.0;
        double Fp = 0.0;
        double F1 = 0.0;
        double t1 = 0.0;
        double tlim = 0.0;
        double ex = 0.0;
        double kt = 0.0;
        double FF = 0.0;
        double FF1 = 0.0;
        double r = 0.0;
        double f0 = (infil.f0 * InfilFactor);
        double fmin = (infil.fmin * InfilFactor);
        double Fmax = infil.Fmax;
        double tp = infil.tp;
        double df = (f0 - fmin);
        double kd = infil.decay;
        double kr = (infil.regen * Evap.recoveryFactor);
        if ((((df < 0.0) || (kd < 0.0)) || (kr < 0.0)))
        {
            return 0.0;
        }
        if (((df == 0.0) || (kd == 0.0)))
        {
            fp = f0;
            fa = (irate + (depth / tstep));
            if ((fp > fa))
            {
                fp = fa;
            }
            return (((((0.0) >= (fp))) ? (0.0) : (fp)));
        }
        fa = (irate + (depth / tstep));
        if ((fa > 1e-10))
        {
            t1 = (tp + tstep);
            tlim = (16.0 / kd);
            if ((tp >= tlim))
            {
                Fp = ((fmin * tp) + (df / kd));
                F1 = (Fp + (fmin * tstep));
            }
            else
            {
                Fp = ((fmin * tp) + ((df / kd) * ((1.0 - Glibm.exp(((-kd) * tp))))));
                F1 = ((fmin * t1) + ((df / kd) * ((1.0 - Glibm.exp(((-kd) * t1))))));
            }
            fp = (((F1 - Fp)) / tstep);
            fp = (((((fp) >= (fmin))) ? (fp) : (fmin)));
            if ((fp > fa))
            {
                fp = fa;
            }
            if ((t1 > tlim))
            {
                tp = t1;
            }
            else if ((fp < fa))
            {
                tp = t1;
            }
            else
            {
                F1 = (Fp + (fp * tstep));
                tp = (tp + (tstep / 2.0));
                for (iter = 1; (iter <= 20); iter++)
                {
                    kt = (((((60.0) <= ((kd * tp)))) ? (60.0) : ((kd * tp))));
                    ex = Glibm.exp((-kt));
                    FF = (((fmin * tp) + ((df / kd) * ((1.0 - ex)))) - F1);
                    FF1 = (fmin + (df * ex));
                    r = (FF / FF1);
                    tp = (tp - r);
                    if ((Math.abs(r) <= (0.001 * tstep)))
                    {
                        break;
                    }
                }
            }
            if ((Fmax > 0.0))
            {
                if (((infil.Fe + (fp * tstep)) > Fmax))
                {
                    fp = (((Fmax - infil.Fe)) / tstep);
                }
                fp = (((((fp) >= (0.0))) ? (fp) : (0.0)));
                infil.Fe += (fp * tstep);
            }
        }
        else if ((kr > 0.0))
        {
            r = Glibm.exp(((-kr) * tstep));
            tp = (1.0 - Glibm.exp(((-kd) * tp)));
            tp = ((-(Glibm.log((1.0 - (r * tp))))) / kd);
            if ((Fmax > 0.0))
            {
                infil.Fe = ((fmin * tp) + (((df / kd)) * ((1.0 - Glibm.exp(((-kd) * tp))))));
            }
        }
        infil.tp = tp;
        return fp;
    }

    // infil.c:496
    static double modHorton_getInfil(THorton infil, double tstep, double irate, double depth)
    {
        double f = 0.0;
        double fp = 0.0;
        double fa = 0.0;
        double f0 = (infil.f0 * InfilFactor);
        double fmin = (infil.fmin * InfilFactor);
        double df = (f0 - fmin);
        double kd = infil.decay;
        double kr = (infil.regen * Evap.recoveryFactor);
        if ((((df < 0.0) || (kd < 0.0)) || (kr < 0.0)))
        {
            return 0.0;
        }
        if (((df == 0.0) || (kd == 0.0)))
        {
            fp = f0;
            fa = (irate + (depth / tstep));
            if ((fp > fa))
            {
                fp = fa;
            }
            return (((((0.0) >= (fp))) ? (0.0) : (fp)));
        }
        fa = (irate + (depth / tstep));
        if ((fa > 1e-10))
        {
            if (((infil.Fmax > 0.0) && (infil.Fe >= infil.Fmax)))
            {
                return 0.0;
            }
            fp = (f0 - (kd * infil.Fe));
            fp = (((((fp) >= (fmin))) ? (fp) : (fmin)));
            f = (((((fa) <= (fp))) ? (fa) : (fp)));
            infil.Fe += ((((((((f - fmin))) >= (0.0))) ? (((f - fmin))) : (0.0))) * tstep);
            if ((infil.Fmax > 0.0))
            {
                infil.Fe = (((((infil.Fe) >= (infil.Fmax))) ? (infil.Fe) : (infil.Fmax)));
            }
        }
        else if ((kr > 0.0))
        {
            infil.Fe *= (Glibm.exp(((-kr) * tstep)));
            infil.Fe = (((((infil.Fe) >= (0.0))) ? (infil.Fe) : (0.0)));
        }
        return f;
    }

    // infil.c:559
    static void grnampt_getParams(int j, double[] p)
    {
        p[0] = (Infil[j].grnAmpt.S * UCF(RAINDEPTH));
        p[1] = (Infil[j].grnAmpt.Ks * UCF(RAINFALL));
        p[2] = Infil[j].grnAmpt.IMDmax;
    }

    // infil.c:574
    static int grnampt_setParams(TGrnAmpt infil, double[] p)
    {
        double ksat = 0.0;
        if (((((p[0] < 0.0) || (p[1] <= 0.0)) || (p[2] < 0.0)) || (p[2] > 1.0)))
        {
            return 0;
        }
        infil.S = (p[0] / UCF(RAINDEPTH));
        infil.Ks = (p[1] / UCF(RAINFALL));
        infil.IMDmax = p[2];
        ksat = ((infil.Ks * 12.0) * 3600.0);
        infil.Lu = ((4.0 * Math.sqrt(ksat)) / 12.0);
        return 1;
    }

    // infil.c:597
    static void grnampt_initState(TGrnAmpt infil)
    {
        if ((infil == null))
        {
            return;
        }
        infil.IMD = infil.IMDmax;
        infil.Fu = 0.0;
        infil.F = 0.0;
        infil.Sat = 0;
        infil.T = 0.0;
    }

    // infil.c:612
    static void grnampt_getState(TGrnAmpt infil, double[] x)
    {
        x[0] = infil.IMD;
        x[1] = infil.F;
        x[2] = infil.Fu;
        x[3] = infil.Sat;
        x[4] = infil.T;
    }

    // infil.c:621
    static void grnampt_setState(TGrnAmpt infil, double[] x)
    {
        infil.IMD = x[0];
        infil.F = x[1];
        infil.Fu = x[2];
        infil.Sat = ((byte) (R.D2I(x[3])));
        infil.T = x[4];
    }

    // infil.c:632
    static double grnampt_getInfil(TGrnAmpt infil, double tstep, double irate, double depth, int modelType)
    {
        Fumax = ((infil.IMDmax * infil.Lu) * Math.sqrt(InfilFactor));
        infil.T -= tstep;
        if ((infil.Sat != 0))
        {
            return grnampt_getSatInfil(infil, tstep, irate, depth);
        }
        else
        {
            return grnampt_getUnsatInfil(infil, tstep, irate, depth, modelType);
        }
    }

    // infil.c:660
    static double grnampt_getUnsatInfil(TGrnAmpt infil, double tstep, double irate, double depth, int modelType)
    {
        double ia = 0.0;
        double c1 = 0.0;
        double F2 = 0.0;
        double dF = 0.0;
        double Fs = 0.0;
        double kr = 0.0;
        double ts = 0.0;
        double ks = (infil.Ks * InfilFactor);
        double lu = (infil.Lu * Math.sqrt(InfilFactor));
        ia = (irate + (depth / tstep));
        if ((ia < 1e-10))
        {
            ia = 0.0;
        }
        if ((ia == 0.0))
        {
            if ((infil.Fu <= 0.0))
            {
                return 0.0;
            }
            kr = ((lu / 90000.0) * Evap.recoveryFactor);
            dF = ((kr * Fumax) * tstep);
            infil.F -= dF;
            infil.Fu -= dF;
            if ((infil.Fu <= 0.0))
            {
                infil.Fu = 0.0;
                infil.F = 0.0;
                infil.IMD = infil.IMDmax;
                return 0.0;
            }
            if ((infil.T <= 0.0))
            {
                infil.IMD = (((Fumax - infil.Fu)) / lu);
                infil.F = 0.0;
            }
            return 0.0;
        }
        if ((ia <= ks))
        {
            dF = (ia * tstep);
            infil.F += dF;
            infil.Fu += dF;
            infil.Fu = (((((infil.Fu) <= (Fumax))) ? (infil.Fu) : (Fumax)));
            if (((modelType == GREEN_AMPT) && (infil.T <= 0.0)))
            {
                infil.IMD = (((Fumax - infil.Fu)) / lu);
                infil.F = 0.0;
            }
            return ia;
        }
        infil.T = ((5400.0 / lu) / Evap.recoveryFactor);
        Fs = (((ks * ((infil.S + depth))) * infil.IMD) / ((ia - ks)));
        if ((infil.F > Fs))
        {
            infil.Sat = 1;
            return grnampt_getSatInfil(infil, tstep, irate, depth);
        }
        if (((infil.F + (ia * tstep)) < Fs))
        {
            dF = (ia * tstep);
            infil.F += dF;
            infil.Fu += dF;
            infil.Fu = (((((infil.Fu) <= (Fumax))) ? (infil.Fu) : (Fumax)));
            return ia;
        }
        ts = (tstep - (((Fs - infil.F)) / ia));
        if ((ts <= 0.0))
        {
            ts = 0.0;
        }
        c1 = (((infil.S + depth)) * infil.IMD);
        F2 = grnampt_getF2(Fs, c1, ks, ts);
        if ((F2 > (Fs + (ia * ts))))
        {
            F2 = (Fs + (ia * ts));
        }
        dF = (F2 - infil.F);
        infil.F = F2;
        infil.Fu += dF;
        infil.Fu = (((((infil.Fu) <= (Fumax))) ? (infil.Fu) : (Fumax)));
        infil.Sat = 1;
        return (dF / tstep);
    }

    // infil.c:767
    static double grnampt_getSatInfil(TGrnAmpt infil, double tstep, double irate, double depth)
    {
        double ia = 0.0;
        double c1 = 0.0;
        double dF = 0.0;
        double F2 = 0.0;
        double ks = (infil.Ks * InfilFactor);
        double lu = (infil.Lu * Math.sqrt(InfilFactor));
        ia = (irate + (depth / tstep));
        if ((ia < 1e-10))
        {
            return 0.0;
        }
        infil.T = ((5400.0 / lu) / Evap.recoveryFactor);
        c1 = (((infil.S + depth)) * infil.IMD);
        F2 = grnampt_getF2(infil.F, c1, ks, tstep);
        dF = (F2 - infil.F);
        if ((dF > (ia * tstep)))
        {
            dF = (ia * tstep);
            infil.Sat = 0;
        }
        infil.F += dF;
        infil.Fu += dF;
        infil.Fu = (((((infil.Fu) <= (Fumax))) ? (infil.Fu) : (Fumax)));
        return (dF / tstep);
    }

    // infil.c:813
    static double grnampt_getF2(double f1, double c1, double ks, double ts)
    {
        int i = 0;
        double f2 = f1;
        double f2min = 0.0;
        double df2 = 0.0;
        double c2 = 0.0;
        f2min = (f1 + (ks * ts));
        if ((c1 == 0.0))
        {
            return f2min;
        }
        if (((ts < 10.0) && (f1 > (0.01 * c1))))
        {
            f2 = (f1 + ((ks * ((1.0 + (c1 / f1)))) * ts));
            return (((((f2) >= (f2min))) ? (f2) : (f2min)));
        }
        c2 = ((c1 * Glibm.log((f1 + c1))) - (ks * ts));
        for (i = 1; (i <= 20); i++)
        {
            df2 = (((((f2 - f1) - (c1 * Glibm.log((f2 + c1)))) + c2)) / ((1.0 - (c1 / ((f2 + c1))))));
            if ((Math.abs(df2) < 0.00001))
            {
                return (((((f2) >= (f2min))) ? (f2) : (f2min)));
            }
            f2 -= df2;
        }
        return f2min;
    }

    // infil.c:861
    static int curvenum_setParams(TCurveNum infil, double[] p)
    {
        if ((p[0] < 10.0))
        {
            p[0] = 10.0;
        }
        if ((p[0] > 99.0))
        {
            p[0] = 99.0;
        }
        infil.Smax = ((((1000.0 / p[0]) - 10.0)) / 12.0);
        if ((infil.Smax < 0.0))
        {
            return 0;
        }
        if ((p[2] > 0.0))
        {
            infil.regen = (1.0 / ((p[2] * 86400)));
        }
        else
        {
            return 0;
        }
        infil.Tmax = (0.06 / infil.regen);
        return 1;
    }

    // infil.c:888
    static void curvenum_initState(TCurveNum infil)
    {
        infil.S = infil.Smax;
        infil.P = 0.0;
        infil.F = 0.0;
        infil.T = 0.0;
        infil.Se = infil.Smax;
        infil.f = 0.0;
    }

    // infil.c:903
    static void curvenum_getState(TCurveNum infil, double[] x)
    {
        x[0] = infil.S;
        x[1] = infil.P;
        x[2] = infil.F;
        x[3] = infil.T;
        x[4] = infil.Se;
        x[5] = infil.f;
    }

    // infil.c:913
    static void curvenum_setState(TCurveNum infil, double[] x)
    {
        infil.S = x[0];
        infil.P = x[1];
        infil.F = x[2];
        infil.T = x[3];
        infil.Se = x[4];
        infil.f = x[5];
    }

    // infil.c:925
    static double curvenum_getInfil(TCurveNum infil, double tstep, double irate, double depth)
    {
        double F1 = 0.0;
        double f1 = 0.0;
        double fa = (irate + (depth / tstep));
        if ((irate > 1e-10))
        {
            if ((infil.T >= infil.Tmax))
            {
                infil.P = 0.0;
                infil.F = 0.0;
                infil.f = 0.0;
                infil.Se = infil.S;
            }
            infil.T = 0.0;
            infil.P += (irate * tstep);
            F1 = (infil.P * ((1.0 - (infil.P / ((infil.P + infil.Se))))));
            f1 = (((F1 - infil.F)) / tstep);
            if (((f1 < 0.0) || (infil.S <= 0.0)))
            {
                f1 = 0.0;
            }
        }
        else
        {
            if (((depth > 0.004167) && (infil.S > 0.0)))
            {
                f1 = infil.f;
                if (((f1 * tstep) > infil.S))
                {
                    f1 = (infil.S / tstep);
                }
            }
            else
            {
                infil.T += tstep;
            }
        }
        if ((f1 > 0.0))
        {
            f1 = (((((f1) <= (fa))) ? (f1) : (fa)));
            f1 = (((((f1) >= (0.0))) ? (f1) : (0.0)));
            infil.F += (f1 * tstep);
            if ((infil.regen > 0.0))
            {
                infil.S -= (f1 * tstep);
                if ((infil.S < 0.0))
                {
                    infil.S = 0.0;
                }
            }
        }
        else
        {
            infil.S += (((infil.regen * infil.Smax) * tstep) * Evap.recoveryFactor);
            if ((infil.S > infil.Smax))
            {
                infil.S = infil.Smax;
            }
        }
        infil.f = f1;
        return f1;
    }

}
