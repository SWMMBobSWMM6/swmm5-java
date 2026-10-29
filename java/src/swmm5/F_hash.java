// Generated from EPA SWMM 5.2.4 (hash.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_hash {
    // hash.c:24
    static int samestr(CPtr s1, CPtr s2)
    {
        int i = 0;
        for (i = 0; (((((((s1.g(i)) >= 97) && ((s1.g(i)) <= 122))) ? (((s1.g(i)) & (~32))) : (s1.g(i)))) == ((((((s2.g(i)) >= 97) && ((s2.g(i)) <= 122))) ? (((s2.g(i)) & (~32))) : (s2.g(i))))); i++)
        {
            if (((!(s1.g((i + 1)) != 0)) && (!(s2.g((i + 1)) != 0))))
            {
                return (1);
            }
        }
        return (0);
    }

    // hash.c:33
    static long hash(CPtr str)
    {
        long sum1 = ((0) & 0xFFFFFFFFL);
        long check1 = 0L;
        long sum2 = 0L;
        while ((0 != str.g(0)))
        {
            sum1 = ((sum1 + ((((((((str.g(0)) >= 97) && ((str.g(0)) <= 122))) ? (((str.g(0)) & (~32))) : (str.g(0))))) & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            str = str.add(1);
            if ((((255) & 0xFFFFFFFFL) <= sum1))
            {
                sum1 = ((sum1 - ((255) & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            }
            sum2 += sum1;
        }
        check1 = ((sum2) & 0xFFFFFFFFL);
        check1 = (check1 % ((255) & 0xFFFFFFFFL));
        check1 = ((((255) & 0xFFFFFFFFL) - ((((sum1 + check1) & 0xFFFFFFFFL)) % ((255) & 0xFFFFFFFFL))) & 0xFFFFFFFFL);
        sum1 = ((((255) & 0xFFFFFFFFL) - ((((sum1 + check1) & 0xFFFFFFFFL)) % ((255) & 0xFFFFFFFFL))) & 0xFFFFFFFFL);
        return (((((((check1 << 8) & 0xFFFFFFFFL)) | sum1)) % ((1999) & 0xFFFFFFFFL)));
    }

    // hash.c:51
    static HTentry[] HTcreate()
    {
        int i = 0;
        HTentry[] ht = new HTentry[(int) (1999)];
        if ((ht != null))
        {
            for (i = 0; (i < 1999); i++)
            {
                ht[i] = (null);
            }
        }
        return (ht);
    }

    // hash.c:59
    static int HTinsert(HTentry[] ht, CPtr key, int data)
    {
        long i = hash(key);
        HTentry entry = null;
        if ((i >= ((1999) & 0xFFFFFFFFL)))
        {
            return (0);
        }
        entry = new HTentry();
        if ((entry == null))
        {
            return (0);
        }
        entry.key = key;
        entry.data = data;
        entry.next = ht[(int) (i)];
        ht[(int) (i)] = entry;
        return (1);
    }

    // hash.c:73
    static int HTfind(HTentry[] ht, CPtr key)
    {
        long i = hash(key);
        HTentry entry = null;
        if ((i >= ((1999) & 0xFFFFFFFFL)))
        {
            return ((-1));
        }
        entry = ht[(int) (i)];
        while ((entry != null))
        {
            if ((samestr(entry.key, key) != 0))
            {
                return (entry.data);
            }
            entry = entry.next;
        }
        return ((-1));
    }

    // hash.c:87
    static CPtr HTfindKey(HTentry[] ht, CPtr key)
    {
        long i = hash(key);
        HTentry entry = null;
        if ((i >= ((1999) & 0xFFFFFFFFL)))
        {
            return null;
        }
        entry = ht[(int) (i)];
        while ((entry != null))
        {
            if ((samestr(entry.key, key) != 0))
            {
                return (entry.key);
            }
            entry = entry.next;
        }
        return null;
    }

    // hash.c:101
    static void HTfree(HTentry[] ht)
    {
        HTentry entry = null;
        HTentry nextentry = null;
        int i = 0;
        for (i = 0; (i < 1999); i++)
        {
            entry = ht[i];
            while ((entry != null))
            {
                nextentry = entry.next;
                R.free(entry);
                entry = nextentry;
            }
        }
        R.free(ht);
    }

}
