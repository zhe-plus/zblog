package com.zblog.common;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {

    private static final Safelist SAFELIST = Safelist.relaxed()
        .addTags("h1", "h2", "h3", "h4", "h5", "h6", "pre", "code", "span", "div", "hr", "br",
                 "table", "thead", "tbody", "tr", "th", "td", "tfoot", "caption", "colgroup", "col",
                 "del", "ins", "mark", "sub", "sup", "dl", "dt", "dd", "abbr", "details", "summary")
        .addAttributes("code", "class")
        .addAttributes("span", "class")
        .addAttributes("pre", "class")
        .addAttributes("a", "rel", "target")
        .addAttributes("img", "alt", "title", "loading", "width", "height")
        .addAttributes("td", "align", "style")
        .addAttributes("th", "align", "style")
        .addAttributes("table", "style")
        .addProtocols("a", "href", "http", "https", "mailto")
        .addProtocols("img", "src", "http", "https", "data");

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) return "";
        return Jsoup.clean(html, SAFELIST);
    }

    public static String decodeEntities(String text) {
        if (text == null) return "";
        return text
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ");
    }
}
