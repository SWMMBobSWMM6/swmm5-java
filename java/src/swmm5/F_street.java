// Generated from EPA SWMM 5.2.4 (street.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_street {
    // street.c:29
    static int street_create(int nStreets)
    {
        Street = (null);
        Nobjects[STREET] = 0;
        Street = R.fill(new TStreet[(int) (nStreets)], TStreet::new);
        if ((Street == null))
        {
            return ERR_MEMORY;
        }
        Nobjects[STREET] = nStreets;
        return 0;
    }

    // street.c:46
    static void street_delete()
    {
        {
            if ((Street != null))
            {
                R.free(Street);
                Street = (null);
            }
        }
    }

    // street.c:58
    static int street_readParams(CPtr[] tok, int ntoks)
    {
        int i = 0;
        int k = 0;
        IBox sides = new IBox(0);
        double[] x = new double[11];
        TStreet street = null;
        if ((ntoks < 5))
        {
            return error_setInpError(ERR_ITEMS, L0);
        }
        i = project_findObject(STREET, tok[0]);
        if ((i < 0))
        {
            return error_setInpError(ERR_NAME, tok[0]);
        }
        Street[i].ID = project_findID(STREET, tok[0]);
        for (k = 0; (k <= 9); k++)
        {
            x[k] = 0.0;
        }
        for (k = 1; (k <= 4); k++)
        {
            if (((!(getDouble(tok[k], new DElem(x, k)) != 0)) || (x[k] <= 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[k]);
            }
        }
        if ((ntoks > 5))
        {
            if (((!(getDouble(tok[5], new DElem(x, 5)) != 0)) || (x[5] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[5]);
            }
        }
        if ((ntoks > 6))
        {
            if (((!(getDouble(tok[6], new DElem(x, 6)) != 0)) || (x[6] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[6]);
            }
        }
        sides.v = 2;
        if ((ntoks > 7))
        {
            if ((((!(getInt(tok[7], sides) != 0)) || (sides.v < 1)) || (sides.v > 2)))
            {
                return error_setInpError(ERR_NUMBER, tok[7]);
            }
        }
        if ((ntoks > 8))
        {
            if (((!(getDouble(tok[8], new DElem(x, 8)) != 0)) || (x[8] < 0.0)))
            {
                return error_setInpError(ERR_NUMBER, tok[8]);
            }
            if ((x[8] > 0.0))
            {
                if ((ntoks < 11))
                {
                    return error_setInpError(ERR_ITEMS, L0);
                }
                for (k = 9; (k <= 10); k++)
                {
                    if (((!(getDouble(tok[k], new DElem(x, k)) != 0)) || (x[k] <= 0.0)))
                    {
                        return error_setInpError(ERR_NUMBER, tok[k]);
                    }
                }
            }
        }
        street = Street[i];
        street.width = (x[1] / UCF(LENGTH));
        street.curbHeight = (x[2] / UCF(LENGTH));
        street.slope = (x[3] / 100.0);
        street.roughness = x[4];
        street.gutterDepression = (x[5] / UCF(LENGTH));
        street.gutterWidth = (x[6] / UCF(LENGTH));
        street.sides = sides.v;
        street.backWidth = (x[8] / UCF(LENGTH));
        street.backSlope = (x[9] / 100.0);
        street.backRoughness = x[10];
        transect_createStreetTransect(street);
        return 0;
    }

    // street.c:141
    static double street_getExtentFilled(int link)
    {
        int k = 0;
        int t = 0;
        double filled = 0.0;
        t = Link[link].xsect.transect;
        if ((t < 0))
        {
            return 0.0;
        }
        if ((RouteModel == DW))
        {
            filled = (((((Node[Link[link].node1].newDepth) >= (Node[Link[link].node2].newDepth))) ? (Node[Link[link].node1].newDepth) : (Node[Link[link].node2].newDepth)));
        }
        else
        {
            k = Link[link].subIndex;
            filled = (((((Conduit[k].a1) >= (Conduit[k].a2))) ? (Conduit[k].a1) : (Conduit[k].a2)));
        }
        return filled;
    }

}
