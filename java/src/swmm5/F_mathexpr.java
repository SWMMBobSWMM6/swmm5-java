// Generated from EPA SWMM 5.2.4 (mathexpr.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_mathexpr {
    static int Err = 0;
    static int Bc = 0;
    static int PrevLex = 0;
    static int CurLex = 0;
    static int Len = 0;
    static int Pos = 0;
    static CPtr S = null;
    static CPtr Token = R.buf(255);
    static int Ivar = 0;
    static double Fvalue = 0.0;
    static CPtr[] MathFunc = new CPtr[] { L1004, L1005, L1006, L1007, L1008, L1009, L1010, L1011, L739, L1012, L1013, L1014, L1015, L1016, L1017, L1018, L1019, L1020, L1021, null };
    static FP3 mathexpr_getVariableIndex = null;

    // mathexpr.c:109
    static int sametext(CPtr s1, CPtr s2)
    {
        int i = 0;
        for (i = 0; (R.toupper(s1.g(i)) == R.toupper(s2.g(i))); i++)
        {
            if (((!(s1.g((i + 1)) != 0)) && (!(s2.g((i + 1)) != 0))))
            {
                return (1);
            }
        }
        return (0);
    }

    // mathexpr.c:130
    static int isDigit(int c)
    {
        if (((c >= 49) && (c <= 57)))
        {
            return 1;
        }
        if ((c == 48))
        {
            return 1;
        }
        return 0;
    }

    // mathexpr.c:139
    static int isLetter(int c)
    {
        if (((c >= 97) && (c <= 122)))
        {
            return 1;
        }
        if (((c >= 65) && (c <= 90)))
        {
            return 1;
        }
        if ((c == 95))
        {
            return 1;
        }
        return 0;
    }

    // mathexpr.c:149
    static void getToken()
    {
        CPtr c = R.bufFrom(L33, 2);
        Token.s(0, 0);
        while (((Pos <= Len) && (((isLetter(S.g(Pos)) != 0) || (isDigit(S.g(Pos)) != 0)))))
        {
            c.s(0, S.g(Pos));
            R.strcat(Token, c);
            Pos++;
        }
        Pos--;
    }

    // mathexpr.c:165
    static int getMathFunc()
    {
        int i = 0;
        while ((MathFunc[i] != null))
        {
            if ((sametext(MathFunc[i], Token) != 0))
            {
                return (i + 10);
            }
            i++;
        }
        return (0);
    }

    // mathexpr.c:178
    static int getVariable()
    {
        if (!(mathexpr_getVariableIndex != null))
        {
            return 0;
        }
        Ivar = mathexpr_getVariableIndex.call(Token);
        if ((Ivar >= 0))
        {
            return 8;
        }
        return 0;
    }

    // mathexpr.c:188
    static double getNumber()
    {
        CPtr c = R.bufFrom(L33, 2);
        CPtr sNumber = R.buf(255);
        int errflag = 0;
        int decimalCount = 0;
        sNumber.s(0, 0);
        while (((Pos < Len) && (isDigit(S.g(Pos)) != 0)))
        {
            c.s(0, S.g(Pos));
            R.strcat(sNumber, c);
            Pos++;
        }
        if ((Pos < Len))
        {
            if ((S.g(Pos) == 46))
            {
                decimalCount++;
                if ((decimalCount > 1))
                {
                    Err = 1;
                }
                R.strcat(sNumber, L315);
                Pos++;
                while (((Pos < Len) && (isDigit(S.g(Pos)) != 0)))
                {
                    c.s(0, S.g(Pos));
                    R.strcat(sNumber, c);
                    Pos++;
                }
            }
            if (((Pos < Len) && (((S.g(Pos) == 101) || (S.g(Pos) == 69)))))
            {
                R.strcat(sNumber, L316);
                Pos++;
                if ((Pos >= Len))
                {
                    errflag = 1;
                }
                else
                {
                    if (((S.g(Pos) == 45) || (S.g(Pos) == 43)))
                    {
                        c.s(0, S.g(Pos));
                        R.strcat(sNumber, c);
                        Pos++;
                    }
                    if (((Pos >= Len) || (!(isDigit(S.g(Pos)) != 0))))
                    {
                        errflag = 1;
                    }
                    else
                    {
                        while (((Pos < Len) && (isDigit(S.g(Pos)) != 0)))
                        {
                            c.s(0, S.g(Pos));
                            R.strcat(sNumber, c);
                            Pos++;
                        }
                    }
                }
            }
        }
        Pos--;
        if ((errflag != 0))
        {
            return 0;
        }
        else
        {
            return R.atof(sNumber);
        }
    }

    // mathexpr.c:252
    static int getOperand()
    {
        int code = 0;
        switch (S.g(Pos)) {
            case 40:
                code = 1;
                break;
            case 41:
                code = 2;
                break;
            case 43:
                code = 3;
                break;
            case 45:
                code = 4;
                if ((((Pos < (Len - 1)) && (isDigit(S.g((Pos + 1))) != 0)) && (((CurLex <= 6) || (CurLex == 31)))))
                {
                    Pos++;
                    Fvalue = (-(getNumber()));
                    code = 7;
                }
                break;
            case 42:
                code = 5;
                break;
            case 47:
                code = 6;
                break;
            case 94:
                code = 31;
                break;
            default:
                code = 0;
        }
        return code;
    }

    // mathexpr.c:280
    static int getLex()
    {
        int n = 0;
        while (((Pos < Len) && (S.g(Pos) == 32)))
        {
            Pos++;
        }
        if ((Pos >= Len))
        {
            return 0;
        }
        n = getOperand();
        if ((n == 0))
        {
            if ((isLetter(S.g(Pos)) != 0))
            {
                getToken();
                n = getMathFunc();
                if ((n == 0))
                {
                    n = getVariable();
                }
            }
            else if (((S.g(Pos) == 46) || (isDigit(S.g(Pos)) != 0)))
            {
                n = 7;
                Fvalue = getNumber();
            }
        }
        Pos++;
        PrevLex = CurLex;
        CurLex = n;
        return n;
    }

    // mathexpr.c:314
    static ExprTree newNode()
    {
        ExprTree node = null;
        node = new ExprTree();
        if (!(node != null))
        {
            Err = 2;
        }
        else
        {
            node.opcode = 0;
            node.ivar = (-1);
            node.fvalue = 0.0;
            node.left = (null);
            node.right = (null);
        }
        return node;
    }

    // mathexpr.c:332
    static ExprTree getSingleOp(IRef lex)
    {
        int opcode = 0;
        ExprTree left = null;
        ExprTree node = null;
        if ((lex.get() == 1))
        {
            Bc++;
            left = getTree();
        }
        else
        {
            if ((((lex.get() < 7) || (lex.get() == 9)) || (lex.get() > 30)))
            {
                Err = 1;
                return null;
            }
            opcode = lex.get();
            if (((lex.get() == 7) || (lex.get() == 8)))
            {
                left = newNode();
                left.opcode = opcode;
                if ((lex.get() == 7))
                {
                    left.fvalue = Fvalue;
                }
                if ((lex.get() == 8))
                {
                    left.ivar = Ivar;
                }
            }
            else
            {
                lex.set(getLex());
                if ((lex.get() != 1))
                {
                    Err = 1;
                    return null;
                }
                Bc++;
                left = newNode();
                left.left = getTree();
                left.opcode = opcode;
            }
        }
        lex.set(getLex());
        if ((lex.get() == 31))
        {
            node = newNode();
            node.left = left;
            node.opcode = lex.get();
            lex.set(getLex());
            node.right = getSingleOp(lex);
            left = node;
        }
        return left;
    }

    // mathexpr.c:397
    static ExprTree getOp(IRef lex)
    {
        int opcode = 0;
        ExprTree left = null;
        ExprTree right = null;
        ExprTree node = null;
        int neg = 0;
        lex.set(getLex());
        if (((PrevLex == 0) || (PrevLex == 1)))
        {
            if ((lex.get() == 4))
            {
                neg = 1;
                lex.set(getLex());
            }
            else if ((lex.get() == 3))
            {
                lex.set(getLex());
            }
        }
        left = getSingleOp(lex);
        while (((lex.get() == 5) || (lex.get() == 6)))
        {
            opcode = lex.get();
            lex.set(getLex());
            right = getSingleOp(lex);
            node = newNode();
            if ((Err != 0))
            {
                return null;
            }
            node.left = left;
            node.right = right;
            node.opcode = opcode;
            left = node;
        }
        if ((neg != 0))
        {
            node = newNode();
            if ((Err != 0))
            {
                return null;
            }
            node.left = left;
            node.right = (null);
            node.opcode = 9;
            left = node;
        }
        return left;
    }

    // mathexpr.c:442
    static ExprTree getTree()
    {
        IBox lex = new IBox(0);
        int opcode = 0;
        ExprTree left = null;
        ExprTree right = null;
        ExprTree node = null;
        left = getOp(lex);
        for (; ; )
        {
            if (((lex.v == 0) || (lex.v == 2)))
            {
                if ((lex.v == 2))
                {
                    Bc--;
                }
                break;
            }
            if (((lex.v != 3) && (lex.v != 4)))
            {
                Err = 1;
                break;
            }
            opcode = lex.v;
            right = getOp(lex);
            node = newNode();
            if ((Err != 0))
            {
                break;
            }
            node.left = left;
            node.right = right;
            node.opcode = opcode;
            left = node;
        }
        return left;
    }

    // mathexpr.c:479
    static void traverseTree(ExprTree tree, ORef<MathExpr> expr)
    {
        MathExpr node = null;
        if ((tree == null))
        {
            return;
        }
        traverseTree(tree.left, expr);
        traverseTree(tree.right, expr);
        node = new MathExpr();
        if ((node != null))
        {
            node.fvalue = tree.fvalue;
            node.opcode = tree.opcode;
            node.ivar = tree.ivar;
            node.next = (null);
            node.prev = (expr.get());
        }
        if ((expr.get() != null))
        {
            (expr.get()).next = node;
        }
        expr.set(node);
    }

    // mathexpr.c:501
    static void deleteTree(ExprTree tree)
    {
        if ((tree != null))
        {
            if ((tree.left != null))
            {
                deleteTree(tree.left);
            }
            if ((tree.right != null))
            {
                deleteTree(tree.right);
            }
            R.free(tree);
        }
    }

    // mathexpr.c:516
    static double mathexpr_eval(MathExpr expr, FP4 getVariableValue)
    {
        double[] ExprStack = new double[1024];
        MathExpr node = expr;
        double r1 = 0.0;
        double r2 = 0.0;
        int stackindex = 0;
        ExprStack[0] = 0.0;
        while (((node != null) && (stackindex >= 0)))
        {
            switch (node.opcode) {
                case 3:
                    r1 = ExprStack[stackindex];
                    stackindex--;
                    if ((stackindex < 0))
                    {
                        break;
                    }
                    r2 = ExprStack[stackindex];
                    ExprStack[stackindex] = (r2 + r1);
                    break;
                case 4:
                    r1 = ExprStack[stackindex];
                    stackindex--;
                    if ((stackindex < 0))
                    {
                        break;
                    }
                    r2 = ExprStack[stackindex];
                    ExprStack[stackindex] = (r2 - r1);
                    break;
                case 5:
                    r1 = ExprStack[stackindex];
                    stackindex--;
                    if ((stackindex < 0))
                    {
                        break;
                    }
                    r2 = ExprStack[stackindex];
                    ExprStack[stackindex] = (r2 * r1);
                    break;
                case 6:
                    r1 = ExprStack[stackindex];
                    stackindex--;
                    if ((stackindex < 0))
                    {
                        break;
                    }
                    r2 = ExprStack[stackindex];
                    ExprStack[stackindex] = (r2 / r1);
                    break;
                case 7:
                    stackindex++;
                    if ((stackindex >= 1024))
                    {
                        break;
                    }
                    ExprStack[stackindex] = node.fvalue;
                    break;
                case 8:
                    if ((getVariableValue != null))
                    {
                        r1 = getVariableValue.call(node.ivar);
                    }
                    else
                    {
                        r1 = 0.0;
                    }
                    stackindex++;
                    if ((stackindex >= 1024))
                    {
                        break;
                    }
                    ExprStack[stackindex] = r1;
                    break;
                case 9:
                    ExprStack[stackindex] = (-ExprStack[stackindex]);
                    break;
                case 10:
                    r1 = ExprStack[stackindex];
                    r2 = Glibm.cos(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 11:
                    r1 = ExprStack[stackindex];
                    r2 = Glibm.sin(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 12:
                    r1 = ExprStack[stackindex];
                    r2 = Glibm.tan(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 13:
                    r1 = ExprStack[stackindex];
                    if ((r1 == 0.0))
                    {
                        r2 = 0.0;
                    }
                    else
                    {
                        r2 = (1.0 / Glibm.tan(r1));
                    }
                    ExprStack[stackindex] = r2;
                    break;
                case 14:
                    r1 = ExprStack[stackindex];
                    r2 = Math.abs(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 15:
                    r1 = ExprStack[stackindex];
                    if ((r1 < 0.0))
                    {
                        r2 = (-1.0);
                    }
                    else if ((r1 > 0.0))
                    {
                        r2 = 1.0;
                    }
                    else
                    {
                        r2 = 0.0;
                    }
                    ExprStack[stackindex] = r2;
                    break;
                case 16:
                    r1 = ExprStack[stackindex];
                    if ((r1 < 0.0))
                    {
                        r2 = 0.0;
                    }
                    else
                    {
                        r2 = Math.sqrt(r1);
                    }
                    ExprStack[stackindex] = r2;
                    break;
                case 17:
                    r1 = ExprStack[stackindex];
                    if ((r1 <= 0))
                    {
                        r2 = 0.0;
                    }
                    else
                    {
                        r2 = Glibm.log(r1);
                    }
                    ExprStack[stackindex] = r2;
                    break;
                case 18:
                    r1 = ExprStack[stackindex];
                    r2 = Glibm.exp(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 19:
                    r1 = ExprStack[stackindex];
                    r2 = Glibm.asin(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 20:
                    r1 = ExprStack[stackindex];
                    r2 = Glibm.acos(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 21:
                    r1 = ExprStack[stackindex];
                    r2 = Glibm.atan(r1);
                    ExprStack[stackindex] = r2;
                    break;
                case 22:
                    r1 = ExprStack[stackindex];
                    r2 = (1.57079632679489661923 - Glibm.atan(r1));
                    ExprStack[stackindex] = r2;
                    break;
                case 23:
                    r1 = ExprStack[stackindex];
                    r2 = (((Glibm.exp(r1) - Glibm.exp((-r1)))) / 2.0);
                    ExprStack[stackindex] = r2;
                    break;
                case 24:
                    r1 = ExprStack[stackindex];
                    r2 = (((Glibm.exp(r1) + Glibm.exp((-r1)))) / 2.0);
                    ExprStack[stackindex] = r2;
                    break;
                case 25:
                    r1 = ExprStack[stackindex];
                    r2 = (((Glibm.exp(r1) - Glibm.exp((-r1)))) / ((Glibm.exp(r1) + Glibm.exp((-r1)))));
                    ExprStack[stackindex] = r2;
                    break;
                case 26:
                    r1 = ExprStack[stackindex];
                    r2 = (((Glibm.exp(r1) + Glibm.exp((-r1)))) / ((Glibm.exp(r1) - Glibm.exp((-r1)))));
                    ExprStack[stackindex] = r2;
                    break;
                case 27:
                    r1 = ExprStack[stackindex];
                    if ((r1 == 0.0))
                    {
                        r2 = 0.0;
                    }
                    else
                    {
                        r2 = Glibm.log10(r1);
                    }
                    ExprStack[stackindex] = r2;
                    break;
                case 28:
                    r1 = ExprStack[stackindex];
                    if ((r1 <= 0.0))
                    {
                        r2 = 0.0;
                    }
                    else
                    {
                        r2 = 1.0;
                    }
                    ExprStack[stackindex] = r2;
                    break;
                case 31:
                    r1 = ExprStack[stackindex];
                    stackindex--;
                    if ((stackindex < 0))
                    {
                        break;
                    }
                    r2 = ExprStack[stackindex];
                    if ((r2 <= 0.0))
                    {
                        r2 = 0.0;
                    }
                    else
                    {
                        r2 = Glibm.pow(r2, r1);
                    }
                    ExprStack[stackindex] = r2;
                    break;
            }
            node = node.next;
        }
        if ((stackindex >= 0))
        {
            r1 = ExprStack[stackindex];
        }
        else
        {
            r1 = 0.0;
        }
        if ((r1 != r1))
        {
            r1 = 0.0;
        }
        return r1;
    }

    // mathexpr.c:735
    static void mathexpr_delete(MathExpr expr)
    {
        if ((expr != null))
        {
            mathexpr_delete(expr.next);
        }
        R.free(expr);
    }

    // mathexpr.c:743
    static MathExpr mathexpr_create(CPtr formula, FP3 getVar)
    {
        ExprTree tree = null;
        OBox<MathExpr> expr = new OBox<>((null));
        MathExpr result = (null);
        mathexpr_getVariableIndex = getVar;
        Err = 0;
        PrevLex = 0;
        CurLex = 0;
        S = formula;
        Len = (int) (R.strlen(S));
        Pos = 0;
        Bc = 0;
        tree = getTree();
        if (((Bc == 0) && (Err == 0)))
        {
            traverseTree(tree, expr);
            while ((expr.v != null))
            {
                result = expr.v;
                expr.v = expr.v.prev;
            }
        }
        deleteTree(tree);
        return result;
    }

}
