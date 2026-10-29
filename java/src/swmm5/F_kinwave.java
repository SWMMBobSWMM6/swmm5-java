// Generated from EPA SWMM 5.2.4 (kinwave.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_kinwave {
    static double WX = 0.6;
    static double WT = 0.6;
    static double EPSIL = 0.001;
    static double Beta1 = 0.0;
    static double C1 = 0.0;
    static double C2 = 0.0;
    static double Afull = 0.0;
    static double Qfull = 0.0;
    static TXsect pXsect = null;

    // kinwave.c:57
    static int kinwave_execute(int j, DRef qinflow, DRef qoutflow, double tStep)
    {
        int k = 0;
        int result = 1;
        double dxdt = 0.0;
        double dq = 0.0;
        double ain = 0.0;
        DBox aout = new DBox(0.0);
        double qin = 0.0;
        double qout = 0.0;
        double a1 = 0.0;
        double a2 = 0.0;
        double q1 = 0.0;
        double q2 = 0.0;
        double q3 = 0.0;
        qoutflow.set((qinflow.get()));
        if ((Link[j].type != CONDUIT))
        {
            return result;
        }
        if ((Link[j].xsect.type == DUMMY))
        {
            return result;
        }
        pXsect = Link[j].xsect;
        Qfull = Link[j].qFull;
        Afull = Link[j].xsect.aFull;
        k = Link[j].subIndex;
        Beta1 = (Conduit[k].beta / Qfull);
        q1 = (Conduit[k].q1 / Qfull);
        q2 = (Conduit[k].q2 / Qfull);
        qin = (((qinflow.get()) / Conduit[k].barrels) / Qfull);
        q3 = (link_getLossRate(j, KW, (qin * Qfull), tStep) / Qfull);
        a1 = (Conduit[k].a1 / Afull);
        a2 = (Conduit[k].a2 / Afull);
        if ((qin >= 1.0))
        {
            ain = 1.0;
        }
        else
        {
            ain = (xsect_getAofS(pXsect, (qin / Beta1)) / Afull);
        }
        if (((qin <= 1e-06) && (q2 <= 1e-06)))
        {
            qout = 0.0;
            aout.v = 0.0;
        }
        else
        {
            dxdt = (((link_getLength(j) / tStep) * Afull) / Qfull);
            dq = (q2 - q1);
            C1 = ((dxdt * WT) / WX);
            C2 = (((1.0 - WT)) * ((ain - a1)));
            C2 = (C2 - (WT * a2));
            C2 = ((C2 * dxdt) / WX);
            C2 = ((C2 + ((((1.0 - WX)) / WX) * dq)) - qin);
            C2 = (C2 + (q3 / WX));
            aout.v = a2;
            result = solveContinuity(qin, ain, aout);
            if ((result == (-1)))
            {
                report_writeErrorMsg(ERR_KINWAVE, Link[j].ID);
                return 1;
            }
            if ((result <= 0))
            {
                result = 1;
            }
            qout = (Beta1 * xsect_getSofA(pXsect, (aout.v * Afull)));
            if ((qin > 1.0))
            {
                qin = 1.0;
            }
        }
        Conduit[k].q1 = (qin * Qfull);
        Conduit[k].a1 = (ain * Afull);
        Conduit[k].q2 = (qout * Qfull);
        Conduit[k].a2 = (aout.v * Afull);
        Conduit[k].fullState = link_getFullState(Conduit[k].a1, Conduit[k].a2, Afull);
        qinflow.set((Conduit[k].q1 * Conduit[k].barrels));
        qoutflow.set((Conduit[k].q2 * Conduit[k].barrels));
        return result;
    }

    // kinwave.c:170
    static int solveContinuity(double qin, double ain, DRef aout)
    {
        int n = 0;
        double aLo = 0.0;
        double aHi = 0.0;
        double aTmp = 0.0;
        double fLo = 0.0;
        double fHi = 0.0;
        double tol = EPSIL;
        aHi = 1.0;
        fHi = ((1.0 + C1) + C2);
        aLo = (xsect_getAmax(pXsect) / Afull);
        if ((aLo < aHi))
        {
            fLo = ((((Beta1 * pXsect.sMax)) + ((C1 * aLo))) + C2);
        }
        else
        {
            fLo = fHi;
        }
        if (((fHi * fLo) > 0.0))
        {
            aHi = aLo;
            fHi = fLo;
            aLo = 0.0;
            fLo = C2;
        }
        if (((fHi * fLo) <= 0.0))
        {
            if (((aout.get() < aLo) || (aout.get() > aHi)))
            {
                aout.set((0.5 * ((aLo + aHi))));
            }
            if ((fLo > fHi))
            {
                aTmp = aLo;
                aLo = aHi;
                aHi = aTmp;
            }
            n = findroot_Newton(aLo, aHi, aout, tol, F_kinwave::evalContinuity, null);
            if ((n <= 0))
            {
                n = (-1);
            }
        }
        else if ((fLo < 0.0))
        {
            if ((qin > 1.0))
            {
                aout.set(ain);
            }
            else
            {
                aout.set(1.0);
            }
            n = (-2);
        }
        else if ((fLo > 0))
        {
            aout.set(0.0);
            n = (-3);
        }
        else
        {
            n = (-1);
        }
        return n;
    }

    // kinwave.c:261
    static void evalContinuity(double a, DRef f, DRef df, Object p)
    {
        f.set(((((Beta1 * xsect_getSofA(pXsect, (a * Afull)))) + ((C1 * a))) + C2));
        df.set(((((Beta1 * Afull) * xsect_getdSdA(pXsect, (a * Afull)))) + C1));
    }

}
