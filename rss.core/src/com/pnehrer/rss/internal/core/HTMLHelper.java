/*
 * Created on Mar 15, 2004
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Hashtable;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
class HTMLHelper {
    
    private static final Hashtable table = new Hashtable();
    
    static {
        // Latin1
        table.put("nbsp", new Integer(160));
        table.put("iexcl", new Integer(161));
        table.put("cent", new Integer(162));
        table.put("pound", new Integer(163));
        table.put("curren", new Integer(164));
        table.put("yen", new Integer(165));
        table.put("brvbar", new Integer(166));
        table.put("sect", new Integer(167));
        table.put("uml", new Integer(168));
        table.put("copy", new Integer(169));
        table.put("ordf", new Integer(170));
        table.put("laquo", new Integer(171));
        table.put("not", new Integer(172));
        table.put("shy", new Integer(173));
        table.put("reg", new Integer(174));
        table.put("macr", new Integer(175));
        table.put("deg", new Integer(176));
        table.put("plusmn", new Integer(177));
        table.put("sup2", new Integer(178));
        table.put("sup3", new Integer(179));
        table.put("acute", new Integer(180));
        table.put("micro", new Integer(181));
        table.put("para", new Integer(182));
        table.put("middot", new Integer(183));
        table.put("cedil", new Integer(184));
        table.put("sup1", new Integer(185));
        table.put("ordm", new Integer(186));
        table.put("raquo", new Integer(187));
        table.put("frac14", new Integer(188));
        table.put("frac12", new Integer(189));
        table.put("frac34", new Integer(190));
        table.put("iquest", new Integer(191));
        table.put("Agrave", new Integer(192));
        table.put("Aacute", new Integer(193));
        table.put("Acirc", new Integer(194));
        table.put("Atilde", new Integer(195));
        table.put("Auml", new Integer(196));
        table.put("Aring", new Integer(197));
        table.put("AElig", new Integer(198));
        table.put("Ccedil", new Integer(199));
        table.put("Egrave", new Integer(200));
        table.put("Eacute", new Integer(201));
        table.put("Ecirc", new Integer(202));
        table.put("Euml", new Integer(203));
        table.put("Igrave", new Integer(204));
        table.put("Iacute", new Integer(205));
        table.put("Icirc", new Integer(206));
        table.put("Iuml", new Integer(207));
        table.put("ETH", new Integer(208));
        table.put("Ntilde", new Integer(209));
        table.put("Ograve", new Integer(210));
        table.put("Oacute", new Integer(211));
        table.put("Ocirc", new Integer(212));
        table.put("Otilde", new Integer(213));
        table.put("Ouml", new Integer(214));
        table.put("times", new Integer(215));
        table.put("Oslash", new Integer(216));
        table.put("Ugrave", new Integer(217));
        table.put("Uacute", new Integer(218));
        table.put("Ucirc", new Integer(219));
        table.put("Uuml", new Integer(220));
        table.put("Yacute", new Integer(221));
        table.put("THORN", new Integer(222));
        table.put("szlig", new Integer(223));
        table.put("agrave", new Integer(224));
        table.put("aacute", new Integer(225));
        table.put("acirc", new Integer(226));
        table.put("atilde", new Integer(227));
        table.put("auml", new Integer(228));
        table.put("aring", new Integer(229));
        table.put("aelig", new Integer(230));
        table.put("ccedil", new Integer(231));
        table.put("egrave", new Integer(232));
        table.put("eacute", new Integer(233));
        table.put("ecirc", new Integer(234));
        table.put("euml", new Integer(235));
        table.put("igrave", new Integer(236));
        table.put("iacute", new Integer(237));
        table.put("icirc", new Integer(238));
        table.put("iuml", new Integer(239));
        table.put("eth", new Integer(240));
        table.put("ntilde", new Integer(241));
        table.put("ograve", new Integer(242));
        table.put("oacute", new Integer(243));
        table.put("ocirc", new Integer(244));
        table.put("otilde", new Integer(245));
        table.put("ouml", new Integer(246));
        table.put("divide", new Integer(247));
        table.put("oslash", new Integer(248));
        table.put("ugrave", new Integer(249));
        table.put("uacute", new Integer(250));
        table.put("ucirc", new Integer(251));
        table.put("uuml", new Integer(252));
        table.put("yacute", new Integer(253));
        table.put("thorn", new Integer(254));
        table.put("yuml", new Integer(255));

        // Symbols
        table.put("fnof", new Integer(402));
        table.put("Alpha", new Integer(913));
        table.put("Beta", new Integer(914));
        table.put("Gamma", new Integer(915));
        table.put("Delta", new Integer(916));
        table.put("Epsilon", new Integer(917));
        table.put("Zeta", new Integer(918));
        table.put("Eta", new Integer(919));
        table.put("Theta", new Integer(920));
        table.put("Iota", new Integer(921));
        table.put("Kappa", new Integer(922));
        table.put("Lambda", new Integer(923));
        table.put("Mu", new Integer(924));
        table.put("Nu", new Integer(925));
        table.put("Xi", new Integer(926));
        table.put("Omicron", new Integer(927));
        table.put("Pi", new Integer(928));
        table.put("Rho", new Integer(929));
        table.put("Sigma", new Integer(931));
        table.put("Tau", new Integer(932));
        table.put("Upsilon", new Integer(933));
        table.put("Phi", new Integer(934));
        table.put("Chi", new Integer(935));
        table.put("Psi", new Integer(936));
        table.put("Omega", new Integer(937));
        table.put("alpha", new Integer(945));
        table.put("beta", new Integer(946));
        table.put("gamma", new Integer(947));
        table.put("delta", new Integer(948));
        table.put("epsilon", new Integer(949));
        table.put("zeta", new Integer(950));
        table.put("eta", new Integer(951));
        table.put("theta", new Integer(952));
        table.put("iota", new Integer(953));
        table.put("kappa", new Integer(954));
        table.put("lambda", new Integer(955));
        table.put("mu", new Integer(956));
        table.put("nu", new Integer(957));
        table.put("xi", new Integer(958));
        table.put("omicron", new Integer(959));
        table.put("pi", new Integer(960));
        table.put("rho", new Integer(961));
        table.put("sigmaf", new Integer(962));
        table.put("sigma", new Integer(963));
        table.put("tau", new Integer(964));
        table.put("upsilon", new Integer(965));
        table.put("phi", new Integer(966));
        table.put("chi", new Integer(967));
        table.put("psi", new Integer(968));
        table.put("omega", new Integer(969));
        table.put("thetasym", new Integer(977));
        table.put("upsih", new Integer(978));
        table.put("piv", new Integer(982));
        table.put("bull", new Integer(8226));
        table.put("hellip", new Integer(8230));
        table.put("prime", new Integer(8242));
        table.put("Prime", new Integer(8243));
        table.put("oline", new Integer(8254));
        table.put("frasl", new Integer(8260));
        table.put("weierp", new Integer(8472));
        table.put("image", new Integer(8465));
        table.put("real", new Integer(8476));
        table.put("trade", new Integer(8482));
        table.put("alefsym", new Integer(8501));
        table.put("larr", new Integer(8592));
        table.put("uarr", new Integer(8593));
        table.put("rarr", new Integer(8594));
        table.put("darr", new Integer(8595));
        table.put("harr", new Integer(8596));
        table.put("crarr", new Integer(8629));
        table.put("lArr", new Integer(8656));
        table.put("uArr", new Integer(8657));
        table.put("rArr", new Integer(8658));
        table.put("dArr", new Integer(8659));
        table.put("hArr", new Integer(8660));
        table.put("forall", new Integer(8704));
        table.put("part", new Integer(8706));
        table.put("exist", new Integer(8707));
        table.put("empty", new Integer(8709));
        table.put("nabla", new Integer(8711));
        table.put("isin", new Integer(8712));
        table.put("notin", new Integer(8713));
        table.put("ni", new Integer(8715));
        table.put("prod", new Integer(8719));
        table.put("sum", new Integer(8721));
        table.put("minus", new Integer(8722));
        table.put("lowast", new Integer(8727));
        table.put("radic", new Integer(8730));
        table.put("prop", new Integer(8733));
        table.put("infin", new Integer(8734));
        table.put("ang", new Integer(8736));
        table.put("and", new Integer(8743));
        table.put("or", new Integer(8744));
        table.put("cap", new Integer(8745));
        table.put("cup", new Integer(8746));
        table.put("int", new Integer(8747));
        table.put("there4", new Integer(8756));
        table.put("sim", new Integer(8764));
        table.put("cong", new Integer(8773));
        table.put("asymp", new Integer(8776));
        table.put("ne", new Integer(8800));
        table.put("equiv", new Integer(8801));
        table.put("le", new Integer(8804));
        table.put("ge", new Integer(8805));
        table.put("sub", new Integer(8834));
        table.put("sup", new Integer(8835));
        table.put("nsub", new Integer(8836));
        table.put("sube", new Integer(8838));
        table.put("supe", new Integer(8839));
        table.put("oplus", new Integer(8853));
        table.put("otimes", new Integer(8855));
        table.put("perp", new Integer(8869));
        table.put("sdot", new Integer(8901));
        table.put("lceil", new Integer(8968));
        table.put("rceil", new Integer(8969));
        table.put("lfloor", new Integer(8970));
        table.put("rfloor", new Integer(8971));
        table.put("lang", new Integer(9001));
        table.put("rang", new Integer(9002));
        table.put("loz", new Integer(9674));
        table.put("spades", new Integer(9824));
        table.put("clubs", new Integer(9827));
        table.put("hearts", new Integer(9829));
        table.put("diams", new Integer(9830));
        
        // Special
        table.put("quot", new Integer(34));
        table.put("amp", new Integer(38));
        table.put("lt", new Integer(60));
        table.put("gt", new Integer(62));
        table.put("OElig", new Integer(338));
        table.put("oelig", new Integer(339));
        table.put("Scaron", new Integer(352));
        table.put("scaron", new Integer(353));
        table.put("Yuml", new Integer(376));
        table.put("circ", new Integer(710));
        table.put("tilde", new Integer(732));
        table.put("ensp", new Integer(8194));
        table.put("emsp", new Integer(8195));
        table.put("thinsp", new Integer(8201));
        table.put("zwnj", new Integer(8204));
        table.put("zwj", new Integer(8205));
        table.put("lrm", new Integer(8206));
        table.put("rlm", new Integer(8207));
        table.put("ndash", new Integer(8211));
        table.put("mdash", new Integer(8212));
        table.put("lsquo", new Integer(8216));
        table.put("rsquo", new Integer(8217));
        table.put("sbquo", new Integer(8218));
        table.put("ldquo", new Integer(8220));
        table.put("rdquo", new Integer(8221));
        table.put("bdquo", new Integer(8222));
        table.put("dagger", new Integer(8224));
        table.put("Dagger", new Integer(8225));
        table.put("permil", new Integer(8240));
        table.put("lsaquo", new Integer(8249));
        table.put("rsaquo", new Integer(8250));
        table.put("euro", new Integer(8364));    
    }

    private HTMLHelper() {
    }
    
    static String stripHTML(String text) throws IOException {
        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(text);
        boolean inTag = false;
        boolean inWhitespace = false;
        int ch;
        while((ch = reader.read()) != -1) {
            if(inTag) {
                if(ch == '>') {
                    inTag = false;
                    ch = ' ';
                }
                else
                    continue;
            }
            else if(ch == '<') {
                inTag = true;
                continue;
            }

            if(ch == '&') {
                StringBuffer buf = new StringBuffer();
                while((ch = reader.read()) != -1) {
                    if(ch == ';')
                        break;
                
                    buf.append((char)ch);
                }
        
                String entity = buf.toString();
                Integer value = (Integer)table.get(entity);
                if(value == null) {
                    if(entity.startsWith("#"))
                        ch = Integer.parseInt(entity.substring(1));
                }
                else
                    ch = value.intValue();
            }

            if(Character.isWhitespace((char)ch)) {
                if(!inWhitespace) {
                    inWhitespace = true;
                    writer.write(' ');
                }
            }
            else {
                inWhitespace = false;
                writer.write(ch);
            }
        }

        reader.close();
        writer.close();
        return writer.toString();
    }
}
