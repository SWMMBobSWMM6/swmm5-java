// Generated from EPA SWMM 5.2.4 (error.c) by tools/c2x/emit_java.py. Do not edit by hand;
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
final class F_error {
    static CPtr ErrString = R.buf(256);

    // error.c:30
    static CPtr error_getMsg(int errCode, CPtr msg)
    {
        switch (errCode) {
            case 101:
                R.strcpy(msg, L46);
                break;
            case 103:
                R.strcpy(msg, L47);
                break;
            case 105:
                R.strcpy(msg, L48);
                break;
            case 107:
                R.strcpy(msg, L49);
                break;
            case 108:
                R.strcpy(msg, L50);
                break;
            case 109:
                R.strcpy(msg, L51);
                break;
            case 110:
                R.strcpy(msg, L52);
                break;
            case 111:
                R.strcpy(msg, L53);
                break;
            case 112:
                R.strcpy(msg, L54);
                break;
            case 113:
                R.strcpy(msg, L55);
                break;
            case 114:
                R.strcpy(msg, L56);
                break;
            case 115:
                R.strcpy(msg, L57);
                break;
            case 117:
                R.strcpy(msg, L58);
                break;
            case 119:
                R.strcpy(msg, L59);
                break;
            case 121:
                R.strcpy(msg, L60);
                break;
            case 122:
                R.strcpy(msg, L61);
                break;
            case 131:
                R.strcpy(msg, L62);
                break;
            case 133:
                R.strcpy(msg, L63);
                break;
            case 134:
                R.strcpy(msg, L64);
                break;
            case 135:
                R.strcpy(msg, L65);
                break;
            case 136:
                R.strcpy(msg, L66);
                break;
            case 137:
                R.strcpy(msg, L67);
                break;
            case 138:
                R.strcpy(msg, L68);
                break;
            case 139:
                R.strcpy(msg, L69);
                break;
            case 140:
                R.strcpy(msg, L70);
                break;
            case 141:
                R.strcpy(msg, L71);
                break;
            case 143:
                R.strcpy(msg, L72);
                break;
            case 145:
                R.strcpy(msg, L73);
                break;
            case 151:
                R.strcpy(msg, L74);
                break;
            case 153:
                R.strcpy(msg, L75);
                break;
            case 155:
                R.strcpy(msg, L76);
                break;
            case 156:
                R.strcpy(msg, L77);
                break;
            case 157:
                R.strcpy(msg, L78);
                break;
            case 158:
                R.strcpy(msg, L79);
                break;
            case 159:
                R.strcpy(msg, L80);
                break;
            case 161:
                R.strcpy(msg, L81);
                break;
            case 171:
                R.strcpy(msg, L82);
                break;
            case 173:
                R.strcpy(msg, L83);
                break;
            case 181:
                R.strcpy(msg, L84);
                break;
            case 182:
                R.strcpy(msg, L85);
                break;
            case 183:
                R.strcpy(msg, L86);
                break;
            case 184:
                R.strcpy(msg, L87);
                break;
            case 185:
                R.strcpy(msg, L88);
                break;
            case 187:
                R.strcpy(msg, L89);
                break;
            case 188:
                R.strcpy(msg, L90);
                break;
            case 191:
                R.strcpy(msg, L91);
                break;
            case 193:
                R.strcpy(msg, L92);
                break;
            case 195:
                R.strcpy(msg, L93);
                break;
            case 200:
                R.strcpy(msg, L94);
                break;
            case 201:
                R.strcpy(msg, L95);
                break;
            case 203:
                R.strcpy(msg, L96);
                break;
            case 205:
                R.strcpy(msg, L97);
                break;
            case 207:
                R.strcpy(msg, L98);
                break;
            case 209:
                R.strcpy(msg, L99);
                break;
            case 211:
                R.strcpy(msg, L100);
                break;
            case 213:
                R.strcpy(msg, L101);
                break;
            case 217:
                R.strcpy(msg, L102);
                break;
            case 219:
                R.strcpy(msg, L103);
                break;
            case 221:
                R.strcpy(msg, L104);
                break;
            case 223:
                R.strcpy(msg, L105);
                break;
            case 225:
                R.strcpy(msg, L106);
                break;
            case 227:
                R.strcpy(msg, L107);
                break;
            case 229:
                R.strcpy(msg, L108);
                break;
            case 231:
                R.strcpy(msg, L109);
                break;
            case 233:
                R.strcpy(msg, L110);
                break;
            case 235:
                R.strcpy(msg, L111);
                break;
            case 301:
                R.strcpy(msg, L112);
                break;
            case 303:
                R.strcpy(msg, L113);
                break;
            case 305:
                R.strcpy(msg, L114);
                break;
            case 307:
                R.strcpy(msg, L115);
                break;
            case 308:
                R.strcpy(msg, L116);
                break;
            case 309:
                R.strcpy(msg, L117);
                break;
            case 311:
                R.strcpy(msg, L118);
                break;
            case 313:
                R.strcpy(msg, L119);
                break;
            case 315:
                R.strcpy(msg, L120);
                break;
            case 317:
                R.strcpy(msg, L121);
                break;
            case 318:
                R.strcpy(msg, L122);
                break;
            case 319:
                R.strcpy(msg, L123);
                break;
            case 320:
                R.strcpy(msg, L124);
                break;
            case 321:
                R.strcpy(msg, L125);
                break;
            case 323:
                R.strcpy(msg, L126);
                break;
            case 325:
                R.strcpy(msg, L127);
                break;
            case 327:
                R.strcpy(msg, L128);
                break;
            case 329:
                R.strcpy(msg, L129);
                break;
            case 331:
                R.strcpy(msg, L130);
                break;
            case 333:
                R.strcpy(msg, L131);
                break;
            case 335:
                R.strcpy(msg, L132);
                break;
            case 336:
                R.strcpy(msg, L133);
                break;
            case 337:
                R.strcpy(msg, L134);
                break;
            case 338:
                R.strcpy(msg, L135);
                break;
            case 339:
                R.strcpy(msg, L136);
                break;
            case 341:
                R.strcpy(msg, L137);
                break;
            case 343:
                R.strcpy(msg, L138);
                break;
            case 345:
                R.strcpy(msg, L139);
                break;
            case 351:
                R.strcpy(msg, L140);
                break;
            case 353:
                R.strcpy(msg, L141);
                break;
            case 355:
                R.strcpy(msg, L142);
                break;
            case 357:
                R.strcpy(msg, L143);
                break;
            case 361:
                R.strcpy(msg, L144);
                break;
            case 363:
                R.strcpy(msg, L145);
                break;
            case 500:
                R.strcpy(msg, L146);
                break;
            case 501:
                R.strcpy(msg, L147);
                break;
            case 502:
                R.strcpy(msg, L148);
                break;
            case 503:
                R.strcpy(msg, L149);
                break;
            case 504:
                R.strcpy(msg, L150);
                break;
            case 505:
                R.strcpy(msg, L151);
                break;
            case 506:
                R.strcpy(msg, L152);
                break;
            case 507:
                R.strcpy(msg, L153);
                break;
            case 508:
                R.strcpy(msg, L154);
                break;
            case 509:
                R.strcpy(msg, L155);
                break;
            default:
                R.strcpy(msg, L0);
        }
        return (msg);
    }

    // error.c:45
    static int error_setInpError(int errcode, CPtr s)
    {
        R.strcpy(ErrString, s);
        return errcode;
    }

}
