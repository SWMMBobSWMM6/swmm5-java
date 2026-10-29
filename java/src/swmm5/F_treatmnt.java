// Generated from EPA SWMM 5.2.4 (treatmnt.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_treatmnt {
    static int PVMAX = 5;
    static int ErrCode = 0;
    static int J = 0;
    static double Dt = 0.0;
    static double Q = 0.0;
    static double V = 0.0;
    static double[] R_ = null;
    static double[] Cin = null;

    // treatmnt.c:66
    static int treatmnt_open()
    {
        R_ = (null);
        Cin = (null);
        if ((Nobjects[POLLUT] > 0))
        {
            R_ = new double[(int) (Nobjects[POLLUT])];
            Cin = new double[(int) (Nobjects[POLLUT])];
            if (((R_ == null) || (Cin == null)))
            {
                report_writeErrorMsg(ERR_MEMORY, L0);
                return 0;
            }
        }
        return 1;
    }

    // treatmnt.c:90
    static void treatmnt_close()
    {
        {
            if ((R_ != null))
            {
                R.free(R_);
                R_ = (null);
            }
        }
        ;
        {
            if ((Cin != null))
            {
                R.free(Cin);
                Cin = (null);
            }
        }
        ;
    }

    // treatmnt.c:103
    static int treatmnt_readExpression(CPtr[] tok, int ntoks)
    {
        CPtr s = R.buf(1025);
        CPtr expr = null;
        int i = 0;
        int j = 0;
        int k = 0;
        int p = 0;
        MathExpr equation = null;
        if ((ntoks < 3))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        j = project_findObject(NODE, tok[0]);
        if ((j < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        p = project_findObject(POLLUT, tok[1]);
        if ((p < 0))
        {
            return error_setInpError(ERR_NAME, tok[1]);
        }
        sstrncpy(s, tok[2], 1024);
        for (i = 3; (i < ntoks); i++)
        {
            sstrcat(s, L33, 1024);
            sstrcat(s, tok[i], 1024);
        }
        if ((((((((s.g(0)) >= 97) && ((s.g(0)) <= 122))) ? (((s.g(0)) & (~32))) : (s.g(0)))) == 82))
        {
            k = 0;
        }
        else if ((((((((s.g(0)) >= 97) && ((s.g(0)) <= 122))) ? (((s.g(0)) & (~32))) : (s.g(0)))) == 67))
        {
            k = 1;
        }
        else
        {
            return error_setInpError(ERR_KEYWORD, tok[2]);
        }
        expr = R.strchr(s, 61);
        if ((expr == null))
        {
            return error_setInpError(ERR_KEYWORD, L0);
        }
        else
        {
            expr = expr.add(1);
        }
        if ((Node[j].treatment == null))
        {
            if (!(createTreatment(j) != 0))
            {
                return error_setInpError(ERR_MEMORY, L0);
            }
        }
        equation = mathexpr_create(expr, F_treatmnt::treatmnt_getVariableIndex);
        if ((equation == null))
        {
            return error_setInpError(ERR_MATH_EXPR, L0);
        }
        if ((Node[j].treatment != null))
        {
            Node[j].treatment[p].treatType = k;
            Node[j].treatment[p].equation = equation;
        }
        return 0;
    }

    // treatmnt.c:165
    static void treatmnt_delete(int j)
    {
        int p = 0;
        if ((Node[j].treatment != null))
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                mathexpr_delete(Node[j].treatment[p].equation);
            }
            R.free(Node[j].treatment);
        }
        Node[j].treatment = (null);
    }

    // treatmnt.c:184
    static void treatmnt_setInflow(double qIn, double[] wIn)
    {
        int p = 0;
        if ((qIn > 0.0))
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                Cin[p] = (wIn[p] / qIn);
            }
        }
        else
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                Cin[p] = 0.0;
            }
        }
    }

    // treatmnt.c:202
    static void treatmnt_treat(int j, double q, double v, double tStep)
    {
        int p = 0;
        double cOut = 0.0;
        double massLost = 0.0;
        TTreatment treatment = null;
        if ((Node[j].treatment == null))
        {
            return;
        }
        ErrCode = 0;
        J = j;
        Dt = tStep;
        Q = q;
        V = v;
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            R_[p] = (-1.0);
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            treatment = Node[j].treatment[p];
            if ((treatment.equation == null))
            {
                R_[p] = 0.0;
            }
            else if (((treatment.treatType == REMOVAL) && (q <= 1e-10)))
            {
                R_[p] = 0.0;
            }
            else
            {
                getRemoval(p);
            }
        }
        if ((ErrCode == ERR_CYCLIC_TREATMENT))
        {
            report_writeErrorMsg(ERR_CYCLIC_TREATMENT, Node[J].ID);
        }
        else
        {
            for (p = 0; (p < Nobjects[POLLUT]); p++)
            {
                if ((R_[p] == 0.0))
                {
                    continue;
                }
                treatment = Node[j].treatment[p];
                if ((treatment.treatType == REMOVAL))
                {
                    if ((Cin[p] == 0.0))
                    {
                        cOut = Node[j].newQual[p];
                    }
                    else
                    {
                        cOut = (((1.0 - R_[p])) * Cin[p]);
                    }
                    cOut = (((((cOut) <= (Node[j].newQual[p]))) ? (cOut) : (Node[j].newQual[p])));
                }
                else
                {
                    cOut = (((1.0 - R_[p])) * Node[j].newQual[p]);
                }
                massLost = ((((((Cin[p] * q) * tStep) + (Node[j].oldQual[p] * Node[j].oldVolume)) - (cOut * (((q * tStep) + Node[j].oldVolume))))) / tStep);
                massLost = (((((0.0) >= (massLost))) ? (0.0) : (massLost)));
                massbal_addReactedMass(p, massLost);
                Node[j].newQual[p] = cOut;
            }
        }
    }

    // treatmnt.c:288
    static int createTreatment(int j)
    {
        int p = 0;
        Node[j].treatment = R.fill(new TTreatment[(int) (Nobjects[POLLUT])], TTreatment::new);
        if ((Node[j].treatment == null))
        {
            return 0;
        }
        for (p = 0; (p < Nobjects[POLLUT]); p++)
        {
            Node[j].treatment[p].equation = (null);
        }
        return 1;
    }

    // treatmnt.c:311
    static int treatmnt_getVariableIndex(CPtr s)
    {
        int k = 0;
        int m = PVMAX;
        k = findmatch(s, ProcessVarWords);
        if ((k >= 0))
        {
            return k;
        }
        k = project_findObject(POLLUT, s);
        if ((k >= 0))
        {
            return ((k + m));
        }
        if (((((((((s.g(0)) >= 97) && ((s.g(0)) <= 122))) ? (((s.g(0)) & (~32))) : (s.g(0)))) == 82) && (s.g(1) == 95)))
        {
            k = project_findObject(POLLUT, s.add(2));
            if ((k >= 0))
            {
                return (((Nobjects[POLLUT] + k) + m));
            }
        }
        return (-1);
    }

    // treatmnt.c:340
    static double treatmnt_getVariableValue(int varCode)
    {
        int p = 0;
        double a1 = 0.0;
        double a2 = 0.0;
        double y = 0.0;
        TTreatment treatment = null;
        if ((varCode < PVMAX))
        {
            switch (varCode) {
                case pvHRT:
                    if ((Node[J].type == STORAGE))
                    {
                        return (Storage[Node[J].subIndex].hrt / 3600.0);
                    }
                    else
                    {
                        return 0.0;
                    }
                case pvDT:
                    return Dt;
                case pvFLOW:
                    return (Q * UCF(FLOW));
                case pvDEPTH:
                    y = (((Node[J].oldDepth + Node[J].newDepth)) / 2.0);
                    return (y * UCF(LENGTH));
                case pvAREA:
                    a1 = node_getSurfArea(J, Node[J].oldDepth);
                    a2 = node_getSurfArea(J, Node[J].newDepth);
                    return (((((a1 + a2)) / 2.0) * UCF(LENGTH)) * UCF(LENGTH));
                default:
                    return 0.0;
            }
        }
        else if ((varCode < (PVMAX + Nobjects[POLLUT])))
        {
            p = (varCode - PVMAX);
            treatment = Node[J].treatment[p];
            if ((treatment.treatType == REMOVAL))
            {
                return Cin[p];
            }
            return Node[J].newQual[p];
        }
        else
        {
            p = ((varCode - PVMAX) - Nobjects[POLLUT]);
            if ((p >= Nobjects[POLLUT]))
            {
                return 0.0;
            }
            return getRemoval(p);
        }
    }

    // treatmnt.c:404
    static double getRemoval(int p)
    {
        double c0 = Node[J].newQual[p];
        double r = 0.0;
        TTreatment treatment = null;
        if (((R_[p] > 1.0) || (ErrCode != 0)))
        {
            ErrCode = 1;
            return 0.0;
        }
        if (((R_[p] >= 0.0) && (R_[p] <= 1.0)))
        {
            return R_[p];
        }
        R_[p] = 10.0;
        if ((c0 == 0.0))
        {
            R_[p] = 0.0;
            return 0.0;
        }
        treatment = Node[J].treatment[p];
        r = mathexpr_eval(treatment.equation, F_treatmnt::treatmnt_getVariableValue);
        r = (((((0.0) >= (r))) ? (0.0) : (r)));
        if ((treatment.treatType == REMOVAL))
        {
            r = (((((1.0) <= (r))) ? (1.0) : (r)));
            R_[p] = r;
        }
        else
        {
            r = (((((c0) <= (r))) ? (c0) : (r)));
            R_[p] = (1.0 - (r / c0));
        }
        return R_[p];
    }

}
