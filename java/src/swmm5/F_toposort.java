// Generated from EPA SWMM 5.2.4 (toposort.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_toposort {
    static int[] InDegree = null;
    static int[] StartPos = null;
    static int[] AdjList = null;
    static int[] Stack = null;
    static int First = 0;
    static int Last = 0;
    static CPtr Examined = null;
    static CPtr InTree = null;
    static int[] LoopLinks = null;
    static int LoopLinksLast = 0;

    // toposort.c:57
    static void toposort_sortLinks(int[] sortedLinks)
    {
        int i = 0;
        int n = 0;
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            sortedLinks[i] = i;
        }
        if ((RouteModel == DW))
        {
            checkDummyLinks();
            if ((ErrorCode != 0))
            {
                return;
            }
            for (i = 0; (i < Nobjects[NODE]); i++)
            {
                Node[i].degree = 0;
            }
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                n = Link[i].node1;
                if ((Link[i].direction < 0))
                {
                    n = Link[i].node2;
                }
                if ((Node[n].type == OUTFALL))
                {
                    if ((Link[i].direction < 0))
                    {
                        n = Link[i].node1;
                    }
                    else
                    {
                        n = Link[i].node2;
                    }
                    Node[n].degree++;
                }
                else
                {
                    Node[n].degree++;
                }
            }
            return;
        }
        if ((ErrorCode != 0))
        {
            return;
        }
        InDegree = new int[(int) (Nobjects[NODE])];
        StartPos = new int[(int) (Nobjects[NODE])];
        AdjList = new int[(int) (Nobjects[LINK])];
        Stack = new int[(int) (Nobjects[NODE])];
        if (((((InDegree == null) || (StartPos == null)) || (AdjList == null)) || (Stack == null)))
        {
            report_writeErrorMsg(ERR_MEMORY, L0);
        }
        else
        {
            createAdjList(DIRECTED);
            adjustAdjList();
            for (i = 0; (i < Nobjects[NODE]); i++)
            {
                InDegree[i] = 0;
            }
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                InDegree[Link[i].node2]++;
            }
            n = topoSort(sortedLinks);
        }
        {
            if ((InDegree != null))
            {
                R.free(InDegree);
                InDegree = (null);
            }
        }
        ;
        {
            if ((StartPos != null))
            {
                R.free(StartPos);
                StartPos = (null);
            }
        }
        ;
        {
            if ((AdjList != null))
            {
                R.free(AdjList);
                AdjList = (null);
            }
        }
        ;
        {
            if ((Stack != null))
            {
                R.free(Stack);
                Stack = (null);
            }
        }
        ;
        if (((!(ErrorCode != 0)) && (n != Nobjects[LINK])))
        {
            report_writeErrorMsg(ERR_LOOP, L0);
            findCycles();
        }
    }

    // toposort.c:142
    static void createAdjList(int listType)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            Node[i].degree = 0;
        }
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            Node[Link[j].node1].degree++;
            if ((listType == UNDIRECTED))
            {
                Node[Link[j].node2].degree++;
            }
        }
        StartPos[0] = 0;
        for (i = 0; (i < (Nobjects[NODE] - 1)); i++)
        {
            StartPos[(i + 1)] = (StartPos[i] + Node[i].degree);
            Node[i].degree = 0;
        }
        (Node[(Nobjects[NODE] - 1)]).degree = 0;
        for (j = 0; (j < Nobjects[LINK]); j++)
        {
            i = Link[j].node1;
            k = (StartPos[i] + Node[i].degree);
            AdjList[k] = j;
            Node[i].degree++;
            if ((listType == UNDIRECTED))
            {
                i = Link[j].node2;
                k = (StartPos[i] + Node[i].degree);
                AdjList[k] = j;
                Node[i].degree++;
            }
        }
    }

    // toposort.c:193
    static void adjustAdjList()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((Node[i].type != DIVIDER))
            {
                continue;
            }
            if ((Node[i].degree != 2))
            {
                continue;
            }
            k = Node[i].subIndex;
            m = StartPos[i];
            j = AdjList[m];
            if ((j == Divider[k].link))
            {
                AdjList[m] = AdjList[(m + 1)];
                AdjList[(m + 1)] = j;
            }
        }
    }

    // toposort.c:225
    static int topoSort(int[] sortedLinks)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int n = 0;
        int i1 = 0;
        int i2 = 0;
        int k1 = 0;
        int k2 = 0;
        First = 0;
        Last = (-1);
        for (i = 0; (i < Nobjects[NODE]); i++)
        {
            if ((InDegree[i] == 0))
            {
                Last++;
                Stack[Last] = i;
            }
        }
        n = 0;
        while ((First <= Last))
        {
            i1 = Stack[First];
            k1 = StartPos[i1];
            k2 = (k1 + Node[i1].degree);
            for (k = k1; (k < k2); k++)
            {
                j = AdjList[k];
                sortedLinks[n] = j;
                n++;
                i2 = Link[j].node2;
                InDegree[i2]--;
                if ((InDegree[i2] == 0))
                {
                    Last++;
                    Stack[Last] = i2;
                }
            }
            First++;
        }
        return n;
    }

    // toposort.c:284
    static void findCycles()
    {
        int i = 0;
        AdjList = new int[(int) ((2 * Nobjects[LINK]))];
        StartPos = new int[(int) (Nobjects[NODE])];
        Stack = new int[(int) (Nobjects[NODE])];
        Examined = R.buf((int) (Nobjects[NODE]));
        InTree = R.buf((int) (Nobjects[LINK]));
        LoopLinks = new int[(int) (Nobjects[LINK])];
        if (((((((StartPos != null) && (AdjList != null)) && (Stack != null)) && (Examined != null)) && (InTree != null)) && (LoopLinks != null)))
        {
            createAdjList(UNDIRECTED);
            for (i = 0; (i < Nobjects[NODE]); i++)
            {
                Examined.s(i, 0);
            }
            for (i = 0; (i < Nobjects[LINK]); i++)
            {
                InTree.s(i, 0);
            }
            for (i = 0; (i < Nobjects[NODE]); i++)
            {
                if ((Examined.g(i) != 0))
                {
                    continue;
                }
                Last = (-1);
                findSpanningTree(i);
            }
        }
        {
            if ((StartPos != null))
            {
                R.free(StartPos);
                StartPos = (null);
            }
        }
        ;
        {
            if ((AdjList != null))
            {
                R.free(AdjList);
                AdjList = (null);
            }
        }
        ;
        {
            if ((Stack != null))
            {
                R.free(Stack);
                Stack = (null);
            }
        }
        ;
        {
            if ((Examined != null))
            {
                R.free(Examined);
                Examined = null;
            }
        }
        ;
        {
            if ((InTree != null))
            {
                R.free(InTree);
                InTree = null;
            }
        }
        ;
        {
            if ((LoopLinks != null))
            {
                R.free(LoopLinks);
                LoopLinks = (null);
            }
        }
        ;
    }

    // toposort.c:330
    static void findSpanningTree(int startNode)
    {
        int nextNode = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        for (m = StartPos[startNode]; (m < (StartPos[startNode] + Node[startNode].degree)); m++)
        {
            k = AdjList[m];
            if ((Link[k].node1 == startNode))
            {
                j = Link[k].node2;
            }
            else
            {
                j = Link[k].node1;
            }
            if ((InTree.g(k) == 0))
            {
                if ((Examined.g(j) != 0))
                {
                    InTree.s(k, 2);
                    evalLoop(k);
                }
                else
                {
                    Examined.s(j, 1);
                    Last++;
                    Stack[Last] = j;
                    InTree.s(k, 1);
                }
            }
        }
        if ((Last >= 0))
        {
            nextNode = Stack[Last];
            Last--;
            findSpanningTree(nextNode);
        }
    }

    // toposort.c:385
    static void evalLoop(int startLink)
    {
        int i = 0;
        int i1 = 0;
        int i2 = 0;
        int j = 0;
        int kount = 0;
        int isCycle = 0;
        LoopLinksLast = 0;
        LoopLinks[0] = startLink;
        i1 = Link[startLink].node1;
        i2 = Link[startLink].node2;
        if (!(traceLoop(i1, i2, startLink) != 0))
        {
            return;
        }
        isCycle = 1;
        j = LoopLinks[0];
        i2 = Link[j].node2;
        for (i = 1; (i <= LoopLinksLast); i++)
        {
            j = LoopLinks[i];
            i1 = Link[j].node1;
            if ((i1 == i2))
            {
                i2 = Link[j].node2;
            }
            else
            {
                isCycle = 0;
                break;
            }
        }
        if ((isCycle != 0))
        {
            kount = 0;
            for (i = 0; (i <= LoopLinksLast); i++)
            {
                if (((kount % 5) == 0))
                {
                    R.fprintf(Frpt.file, L276);
                }
                kount++;
                R.fprintf(Frpt.file, L651, Link[LoopLinks[i]].ID);
                if ((i < LoopLinksLast))
                {
                    R.fprintf(Frpt.file, L652);
                }
            }
        }
    }

    // toposort.c:440
    static int traceLoop(int i1, int i2, int k1)
    {
        int j = 0;
        int k = 0;
        int m = 0;
        if ((i1 == i2))
        {
            return 1;
        }
        for (m = StartPos[i1]; (m < (StartPos[i1] + Node[i1].degree)); m++)
        {
            k = AdjList[m];
            if (((k == k1) || (InTree.g(k) != 1)))
            {
                continue;
            }
            if ((Link[k].node1 == i1))
            {
                j = Link[k].node2;
            }
            else
            {
                j = Link[k].node1;
            }
            if ((traceLoop(j, i2, k) != 0))
            {
                LoopLinksLast++;
                LoopLinks[LoopLinksLast] = k;
                return 1;
            }
        }
        return 0;
    }

    // toposort.c:482
    static void checkDummyLinks()
    {
        int i = 0;
        int j = 0;
        int[] marked = null;
        marked = new int[(int) (Nobjects[NODE])];
        if ((marked == null))
        {
            report_writeErrorMsg(ERR_MEMORY, L0);
            return;
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            j = Link[i].node2;
            if ((Link[i].direction < 0))
            {
                j = Link[i].node1;
            }
            if (((((Link[i].type == CONDUIT) && (Link[i].xsect.type == DUMMY))) || (((Link[i].type == PUMP) && (Pump[Link[i].subIndex].type == IDEAL_PUMP)))))
            {
                if ((marked[j] == 0))
                {
                    marked[j] = 1;
                }
            }
            else
            {
                marked[j] = (-1);
            }
        }
        for (i = 0; (i < Nobjects[LINK]); i++)
        {
            if (((((Link[i].type == CONDUIT) && (Link[i].xsect.type == DUMMY))) || (((Link[i].type == PUMP) && (Pump[Link[i].subIndex].type == IDEAL_PUMP)))))
            {
                j = Link[i].node1;
                if ((marked[j] > 0))
                {
                    report_writeErrorMsg(ERR_DUMMY_LINK, Node[j].ID);
                }
            }
        }
        {
            if ((marked != null))
            {
                R.free(marked);
                marked = (null);
            }
        }
        ;
    }

}
