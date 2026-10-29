// Generated from EPA SWMM 5.2.4 (exfil.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_exfil {
    // exfil.c:34
    static int exfil_readStorageParams(int k, CPtr[] tok, int ntoks, int n)
    {
        int i = 0;
        double[] x = new double[3];
        if ((ntoks == (n + 1)))
        {
            if (!(getDouble(tok[n], new DElem(x, 1)) != 0))
            {
                return error_setInpError(ERR_NUMBER, tok[n]);
            }
            x[0] = 0.0;
            x[2] = 0.0;
        }
        else if ((ntoks < (n + 3)))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        else
        {
            for (i = 0; (i < 3); i++)
            {
                if (!(getDouble(tok[(n + i)], new DElem(x, i)) != 0))
                {
                    return error_setInpError(ERR_NUMBER, tok[(n + i)]);
                }
            }
        }
        if ((x[1] == 0.0))
        {
            return 0;
        }
        return createStorageExfil(k, x);
    }

    // exfil.c:74
    static void exfil_initState(int k)
    {
        int i = 0;
        DBox a = new DBox(0.0);
        double alast = 0.0;
        DBox d = new DBox(0.0);
        TTable aCurve = null;
        TExfil exfil = Storage[k].exfil;
        if ((exfil != null))
        {
            grnampt_initState(exfil.btmExfil);
            grnampt_initState(exfil.bankExfil);
            switch (Storage[k].shape) {
                case TABULAR:
                    i = Storage[k].aCurve;
                    exfil.btmArea = 0.0;
                    exfil.bankMinDepth = 0.0;
                    exfil.bankMaxDepth = 0.0;
                    exfil.bankMaxArea = 0.0;
                    if ((i >= 0))
                    {
                        aCurve = Curve[i];
                        Storage[k].exfil.btmArea = table_lookupEx(aCurve, 0.0);
                        table_getFirstEntry(aCurve, d, a);
                        alast = a.v;
                        while ((table_getNextEntry(aCurve, d, a) != 0))
                        {
                            if ((a.v < alast))
                            {
                                break;
                            }
                            else if ((a.v > alast))
                            {
                                exfil.bankMaxArea = a.v;
                                exfil.bankMaxDepth = d.v;
                            }
                            else if ((exfil.bankMaxArea == 0.0))
                            {
                                exfil.bankMinDepth = d.v;
                            }
                            else
                            {
                                break;
                            }
                            alast = a.v;
                        }
                        exfil.btmArea /= (UCF(LENGTH) * UCF(LENGTH));
                        exfil.bankMaxArea /= (UCF(LENGTH) * UCF(LENGTH));
                        exfil.bankMinDepth /= (UCF(LENGTH));
                        exfil.bankMaxDepth /= (UCF(LENGTH));
                    }
                    break;
                case FUNCTIONAL:
                    exfil.btmArea = Storage[k].a0;
                    if ((Storage[k].a2 == 0.0))
                    {
                        exfil.btmArea += Storage[k].a1;
                    }
                    exfil.bankMinDepth = 0.0;
                    exfil.bankMaxDepth = 10000000000.0;
                    exfil.bankMaxArea = 10000000000.0;
                    break;
                case CYLINDRICAL:
                case CONICAL:
                case PYRAMIDAL:
                    exfil.btmArea = Storage[k].a0;
                    exfil.bankMinDepth = 0.0;
                    exfil.bankMaxDepth = 10000000000.0;
                    exfil.bankMaxArea = 10000000000.0;
                    break;
            }
        }
    }

    // exfil.c:158
    static double exfil_getLoss(TExfil exfil, double tStep, double depth, double area)
    {
        double exfilRate = 0.0;
        if ((exfil.btmExfil.IMDmax == 0.0))
        {
            exfilRate = (exfil.btmExfil.Ks * Adjust.hydconFactor);
        }
        else
        {
            exfilRate = grnampt_getInfil(exfil.btmExfil, tStep, 0.0, depth, MOD_GREEN_AMPT);
        }
        exfilRate *= exfil.btmArea;
        if ((depth > exfil.bankMinDepth))
        {
            area = ((((((area) <= (exfil.bankMaxArea))) ? (area) : (exfil.bankMaxArea))) - exfil.btmArea);
            if ((area > 0.0))
            {
                if ((exfil.btmExfil.IMDmax == 0.0))
                {
                    exfilRate += ((area * exfil.btmExfil.Ks) * Adjust.hydconFactor);
                }
                else
                {
                    if ((depth > exfil.bankMaxDepth))
                    {
                        depth = ((depth - exfil.bankMaxDepth) + (((exfil.bankMaxDepth - exfil.bankMinDepth)) / 2.0));
                    }
                    else
                    {
                        depth = (((depth - exfil.bankMinDepth)) / 2.0);
                    }
                    exfilRate += (area * grnampt_getInfil(exfil.bankExfil, tStep, 0.0, depth, MOD_GREEN_AMPT));
                }
            }
        }
        return exfilRate;
    }

    // exfil.c:218
    static int createStorageExfil(int k, double[] x)
    {
        TExfil exfil = null;
        exfil = Storage[k].exfil;
        if ((exfil == null))
        {
            exfil = new TExfil();
            if ((exfil == null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
            Storage[k].exfil = exfil;
            exfil.btmExfil = (null);
            exfil.bankExfil = (null);
            exfil.btmExfil = new TGrnAmpt();
            if ((exfil.btmExfil == null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
            exfil.bankExfil = new TGrnAmpt();
            if ((exfil.bankExfil == null))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
        }
        if (!(grnampt_setParams(exfil.btmExfil, x) != 0))
        {
            return error_setInpError(ERR_NUMBER, L0);
        }
        grnampt_setParams(exfil.bankExfil, x);
        return 0;
    }

}
