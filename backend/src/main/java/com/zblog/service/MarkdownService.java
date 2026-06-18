package com.zblog.service;

import com.zblog.common.HtmlSanitizer;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.task.list.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MarkdownService {

    private final Parser parser;
    private final HtmlRenderer renderer;
    private final BacklinkService backlinkService;

    public MarkdownService(BacklinkService backlinkService) {
        this.backlinkService = backlinkService;
        List<org.commonmark.Extension> extensions = Arrays.asList(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            TaskListItemsExtension.create()
        );
        this.parser = Parser.builder().extensions(extensions).build();
        this.renderer = HtmlRenderer.builder().extensions(extensions).build();
    }

    public String renderToHtml(String markdown) {
        return renderToHtml(markdown, null);
    }

    public String renderToHtml(String markdown, Long postId) {
        if (markdown == null || markdown.isBlank()) return "";
        String processed = postId != null
            ? backlinkService.resolveWikiLinks(markdown, postId)
            : markdown;
        Node document = parser.parse(processed);
        String html = renderer.render(document);
        return enhanceCodeBlocks(HtmlSanitizer.sanitize(html));
    }

    public String generateSummary(String markdown, int maxLength) {
        if (markdown == null || markdown.isBlank()) return "";
        String text = markdown
            .replaceAll("#+\\s+", "")
            .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
            .replaceAll("`{1,3}[^`]*`{1,3}", "")
            .replaceAll("!\\[.*?\\]\\(.*?\\)", "")
            .replaceAll("\\[([^\\]]+)\\]\\(.*?\\)", "$1")
            .replaceAll("\\n+", " ").trim();
        text = HtmlSanitizer.decodeEntities(text);
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }

    public String generateSummary(String markdown) {
        return generateSummary(markdown, 160);
    }

    private String enhanceCodeBlocks(String html) {
        return html.replaceAll("<pre><code class=\"language-([^\"]+)\">",
            "<pre class=\"code-block\"><code class=\"language-$1\">");
    }
}
