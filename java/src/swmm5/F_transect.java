// Generated from EPA SWMM 5.2.4 (transect.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_transect {
    static int Ntransects = 0;
    static int Nstations = 0;
    static double[] Station = new double[1501];
    static double[] Elev = new double[1501];
    static double Nleft = 0.0;
    static double Nright = 0.0;
    static double Nchannel = 0.0;
    static double Xleftbank = 0.0;
    static double Xrightbank = 0.0;
    static double Xfactor = 0.0;
    static double Yfactor = 0.0;
    static double Lfactor = 0.0;

    // transect.c:70
    static int transect_create(int n)
    {
        Ntransects = n;
        if ((n == 0))
        {
            return 0;
        }
        Transect = R.fill(new TTransect[(int) (Ntransects)], TTransect::new);
        if ((Transect == null))
        {
            return ERR_MEMORY;
        }
        Nchannel = 0.0;
        Nleft = 0.0;
        Nright = 0.0;
        Nstations = 0;
        return 0;
    }

    // transect.c:90
    static void transect_delete()
    {
        if ((Ntransects == 0))
        {
            return;
        }
        {
            if ((Transect != null))
            {
                R.free(Transect);
                Transect = (null);
            }
        }
        ;
        Ntransects = 0;
    }

    // transect.c:104
    static int transect_readParams(IRef count, CPtr[] tok, int ntoks)
    {
        int i = 0;
        int k = 0;
        int index = count.get();
        int errcode = 0;
        double[] x = new double[10];
        CPtr id = null;
        k = findmatch(tok[0], TransectKeyWords);
        if ((k < 0))
        {
            return error_setInpError(ERR_KEYWORD, tok[0]);
        }
        switch (k) {
            case 0:
                transect_validate((index - 1));
                if ((ntoks < 4))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                for (i = 1; (i <= 3); i++)
                {
                    if (!(getDouble(tok[i], new DElem(x, i)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[i]);
                    }
                }
                return setManning(x);
            case 1:
                if ((ntoks < 10))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                id = project_findID(TRANSECT, tok[1]);
                if ((id == null))
                {
                    return error_setInpError(ERR_NAME, tok[1]);
                }
                for (i = 2; (i < 10); i++)
                {
                    if (!(getDouble(tok[i], new DElem(x, i)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[i]);
                    }
                }
                count.set((index + 1));
                return setParams(index, id, x);
            case 2:
                if (((((ntoks - 1)) % 2) > 0))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                i = 1;
                while ((i < ntoks))
                {
                    if (!(getDouble(tok[i], new DElem(x, 1)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[i]);
                    }
                    if (!(getDouble(tok[(i + 1)], new DElem(x, 2)) != 0))
                    {
                        return error_setInpError(ERR_NUMBER, tok[(i + 1)]);
                    }
                    errcode = addStation(x[1], x[2]);
                    if ((errcode != 0))
                    {
                        return errcode;
                    }
                    i += 2;
                }
                return 0;
        }
        return 0;
    }

    // transect.c:194
    static void transect_validate(int j)
    {
        int i = 0;
        double ymin = 0.0;
        double ymax = 0.0;
        double oldNchannel = Nchannel;
        if (((j < 0) || (j >= Ntransects)))
        {
            return;
        }
        if ((Nstations < 2))
        {
            report_writeErrorMsg(ERR_TRANSECT_TOO_FEW, Transect[j].ID);
            return;
        }
        if ((Nstations >= 1500))
        {
            report_writeErrorMsg(ERR_TRANSECT_TOO_MANY, Transect[j].ID);
            return;
        }
        if ((Nchannel <= 0.0))
        {
            report_writeErrorMsg(ERR_TRANSECT_MANNING, Transect[j].ID);
            return;
        }
        if ((Xleftbank > Xrightbank))
        {
            report_writeErrorMsg(ERR_TRANSECT_OVERBANK, Transect[j].ID);
            return;
        }
        Nchannel = (Nchannel * Math.sqrt(Lfactor));
        Transect[j].lengthFactor = Lfactor;
        ymax = Elev[1];
        ymin = Elev[1];
        for (i = 2; (i <= Nstations); i++)
        {
            ymax = (((((Elev[i]) >= (ymax))) ? (Elev[i]) : (ymax)));
            ymin = (((((Elev[i]) <= (ymin))) ? (Elev[i]) : (ymin)));
        }
        if ((ymin >= ymax))
        {
            report_writeErrorMsg(ERR_TRANSECT_NO_DEPTH, Transect[j].ID);
            return;
        }
        Station[0] = Station[1];
        Elev[0] = ymax;
        Nstations++;
        Station[Nstations] = Station[(Nstations - 1)];
        Elev[Nstations] = Elev[0];
        Transect[j].nTbl = 51;
        createTables(Transect[j], ymin, ymax);
        Transect[j].roughness = oldNchannel;
    }

    // transect.c:264
    static void createTables(TTransect transect, double ymin, double ymax)
    {
        int i = 0;
        int nLast = 0;
        double dy = 0.0;
        double y = 0.0;
        transect.yFull = (ymax - ymin);
        transect.wMax = 0.0;
        transect.areaTbl[0] = 0.0;
        transect.hradTbl[0] = 0.0;
        transect.widthTbl[0] = 0.0;
        dy = (((ymax - ymin)) / ((((double) ((transect.nTbl))) - 1)));
        y = ymin;
        for (i = 1; (i < transect.nTbl); i++)
        {
            y += dy;
            transect.areaTbl[i] = 0.0;
            transect.hradTbl[i] = 0.0;
            transect.widthTbl[i] = 0.0;
            getGeometry(transect, i, y);
        }
        setMaxSectionFactor(transect);
        nLast = (transect.nTbl - 1);
        transect.aFull = transect.areaTbl[nLast];
        transect.rFull = transect.hradTbl[nLast];
        transect.wMax = transect.widthTbl[nLast];
        for (i = 1; (i <= nLast); i++)
        {
            transect.areaTbl[i] /= transect.aFull;
            transect.hradTbl[i] /= transect.rFull;
            transect.widthTbl[i] /= transect.wMax;
        }
        transect.widthTbl[0] = transect.widthTbl[1];
    }

    // transect.c:312
    static int setManning(double[] n)
    {
        int i = 0;
        for (i = 1; (i <= 3); i++)
        {
            if ((n[i] < 0.0))
            {
                return ERR_NUMBER;
            }
        }
        if ((n[1] > 0.0))
        {
            Nleft = n[1];
        }
        if ((n[2] > 0.0))
        {
            Nright = n[2];
        }
        if ((n[3] > 0.0))
        {
            Nchannel = n[3];
        }
        if ((Nleft == 0.0))
        {
            Nleft = Nchannel;
        }
        if ((Nright == 0.0))
        {
            Nright = Nchannel;
        }
        return 0;
    }

    // transect.c:334
    static int setParams(int j, CPtr id, double[] x)
    {
        if (((j < 0) || (j >= Ntransects)))
        {
            return ERR_NUMBER;
        }
        Transect[j].ID = id;
        Xleftbank = (x[3] / UCF(LENGTH));
        Xrightbank = (x[4] / UCF(LENGTH));
        Lfactor = x[7];
        if ((Lfactor == 0.0))
        {
            Lfactor = 1.0;
        }
        Xfactor = x[8];
        if ((Xfactor == 0.0))
        {
            Xfactor = 1.0;
        }
        Xleftbank *= Xfactor;
        Xrightbank *= Xfactor;
        Yfactor = (x[9] / UCF(LENGTH));
        Nstations = 0;
        return 0;
    }

    // transect.c:360
    static int addStation(double y, double x)
    {
        if ((Nstations < 0))
        {
            return ERR_TRANSECT_UNKNOWN;
        }
        Nstations++;
        if ((Nstations >= 1500))
        {
            return 0;
        }
        Station[Nstations] = ((x * Xfactor) / UCF(LENGTH));
        Elev[Nstations] = (((y + Yfactor)) / UCF(LENGTH));
        if (((Nstations > 1) && (Station[Nstations] < Station[(Nstations - 1)])))
        {
            return ERR_TRANSECT_SEQUENCE;
        }
        return 0;
    }

    // transect.c:388
    static void getGeometry(TTransect transect, int i, double y)
    {
        int k = 0;
        double ylo = 0.0;
        double yhi = 0.0;
        DBox w = new DBox(0.0);
        DBox wp = new DBox(0.0);
        double wpSum = 0.0;
        DBox a = new DBox(0.0);
        double aSum = 0.0;
        double q = 0.0;
        double qSum = 0.0;
        int findFlow = 0;
        wpSum = 0.0;
        aSum = 0.0;
        qSum = 0.0;
        for (k = 1; (k <= Nstations); k++)
        {
            if ((Elev[(k - 1)] >= Elev[k]))
            {
                yhi = Elev[(k - 1)];
                ylo = Elev[k];
            }
            else
            {
                yhi = Elev[k];
                ylo = Elev[(k - 1)];
            }
            if ((ylo >= y))
            {
                continue;
            }
            getSliceGeom(k, y, ylo, yhi, w, a, wp);
            wpSum += wp.v;
            aSum += a.v;
            transect.areaTbl[i] += a.v;
            transect.widthTbl[i] += w.v;
            if ((Elev[k] >= y))
            {
                findFlow = 1;
            }
            else
            {
                findFlow = 0;
            }
            q = getFlow(k, aSum, wpSum, findFlow);
            if ((q > 0.0))
            {
                qSum += q;
                aSum = 0.0;
                wpSum = 0.0;
            }
        }
        aSum = transect.areaTbl[i];
        if ((aSum == 0.0))
        {
            transect.hradTbl[i] = transect.hradTbl[(i - 1)];
        }
        else
        {
            transect.hradTbl[i] = Glibm.pow((((qSum * Nchannel) / 1.49) / aSum), 1.5);
        }
    }

    // transect.c:468
    static void getSliceGeom(int k, double y, double ylo, double yhi, DRef w, DRef a, DRef wp)
    {
        double width = 0.0;
        double ratio = 0.0;
        width = Math.abs((Station[k] - Station[(k - 1)]));
        w.set(width);
        wp.set(Math.sqrt(((width * width) + (((yhi - ylo)) * ((yhi - ylo))))));
        a.set(0.0);
        if ((y > yhi))
        {
            a.set(((width * ((((y - yhi)) + ((y - ylo))))) / 2.0));
        }
        else if ((yhi > ylo))
        {
            ratio = (((y - ylo)) / ((yhi - ylo)));
            a.set(((((width * ((yhi - ylo))) / 2.0) * ratio) * ratio));
            w.set((w.get() * ratio));
            wp.set((wp.get() * ratio));
        }
    }

    // transect.c:519
    static double getFlow(int k, double a, double wp, int findFlow)
    {
        double n = 0.0;
        if ((findFlow == 0))
        {
            if ((k == (Nstations - 1)))
            {
                findFlow = 1;
            }
            else if ((Station[k] == Xleftbank))
            {
                if (((Nleft != Nchannel) && (Station[k] != Station[(k - 1)])))
                {
                    findFlow = 1;
                }
            }
            else if ((Station[k] == Xrightbank))
            {
                if (((Nright != Nchannel) && (Station[k] != Station[(k + 1)])))
                {
                    findFlow = 1;
                }
            }
        }
        if ((findFlow != 0))
        {
            n = Nchannel;
            if ((Station[(k - 1)] < Xleftbank))
            {
                n = Nleft;
            }
            if ((Station[k] > Xrightbank))
            {
                n = Nright;
            }
            return (((1.486 / n) * a) * Glibm.pow((a / wp), (2.0 / 3.0)));
        }
        return 0.0;
    }

    // transect.c:570
    static void setMaxSectionFactor(TTransect transect)
    {
        int i = 0;
        double sf = 0.0;
        transect.aMax = 0.0;
        transect.sMax = 0.0;
        for (i = 1; (i < transect.nTbl); i++)
        {
            sf = (transect.areaTbl[i] * Glibm.pow(transect.hradTbl[i], (2.0 / 3.0)));
            if ((sf > transect.sMax))
            {
                transect.sMax = sf;
                transect.aMax = transect.areaTbl[i];
            }
        }
    }

    // transect.c:596
    static void transect_createStreetTransect(TStreet street)
    {
        double ymin = 0.0;
        double ymax = 0.0;
        double y1 = 0.0;
        double y3 = 0.0;
        double y4 = 0.0;
        double w1 = 0.0;
        double w2 = 0.0;
        double w3 = 0.0;
        double w4 = 0.0;
        ymin = 0.0;
        w1 = street.backWidth;
        w2 = street.gutterWidth;
        w3 = street.width;
        w4 = (w3 - w2);
        y3 = (street.gutterDepression + (street.slope * w2));
        y1 = (street.curbHeight + street.gutterDepression);
        ymax = ((street.backSlope * street.backWidth) + y1);
        y4 = (y3 + (street.slope * w4));
        ymax = (((((ymax) >= (y4))) ? (ymax) : (y4)));
        Station[0] = 0.0;
        Elev[0] = ymax;
        Station[1] = w1;
        Elev[1] = y1;
        Station[2] = w1;
        Elev[2] = 0.0;
        Station[3] = (w1 + w2);
        Elev[3] = y3;
        Station[4] = (w1 + w3);
        Elev[4] = y4;
        if ((street.sides == 1))
        {
            Station[5] = Station[4];
            Elev[5] = ymax;
            Nstations = 5;
            street.transect.nTbl = 51;
        }
        else
        {
            Station[5] = (Station[4] + w4);
            Elev[5] = y3;
            Station[6] = (Station[5] + w2);
            Elev[6] = 0.0;
            Station[7] = Station[6];
            Elev[7] = y1;
            Station[8] = (Station[7] + w1);
            Elev[8] = ymax;
            Nstations = 8;
            street.transect.nTbl = 51;
        }
        Nchannel = street.roughness;
        if ((street.backWidth == 0.0))
        {
            Nleft = Nchannel;
            Nright = Nchannel;
            Xleftbank = Station[0];
            Xrightbank = Station[Nstations];
        }
        else
        {
            Nleft = street.backRoughness;
            Nright = Nleft;
            Xleftbank = Station[1];
            if ((street.sides == 2))
            {
                Xrightbank = Station[(Nstations - 1)];
            }
            else
            {
                Xrightbank = Station[Nstations];
            }
        }
        createTables(street.transect, ymin, ymax);
        street.transect.roughness = street.roughness;
    }

}
