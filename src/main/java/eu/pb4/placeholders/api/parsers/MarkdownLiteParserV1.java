package eu.pb4.placeholders.api.parsers;

import com.github.bsideup.jabel.Desugar;
import com.mojang.brigadier.StringReader;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.TranslatedNode;
import eu.pb4.placeholders.api.node.parent.FormattingNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.node.parent.StyledNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MarkdownLiteParserV1 implements NodeParser {
    public static NodeParser ALL = new MarkdownLiteParserV1(MarkdownFormat.values());
    private final EnumSet<MarkdownFormat> allowedFormatting = EnumSet.noneOf(MarkdownFormat.class);
    private final Function<TextNode[], TextNode> spoilerFormatting;
    private final Function<TextNode[], TextNode> backtickFormatting;

    public MarkdownLiteParserV1(MarkdownFormat... formatting) {
        this(MarkdownLiteParserV1::defaultSpoilerFormatting, MarkdownLiteParserV1::defaultQuoteFormatting, formatting);
    }

    public MarkdownLiteParserV1(
            Function<TextNode[], TextNode> spoilerFormatting,
            Function<TextNode[], TextNode> quoteFormatting,
            MarkdownFormat... formatting
    ) {
        for (var form : formatting) {
            this.allowedFormatting.add(form);
        }
        this.spoilerFormatting = spoilerFormatting;
        this.backtickFormatting = quoteFormatting;
    }

    public static TextNode defaultSpoilerFormatting(TextNode[] textNodes) {
        return new StyledNode(new TextNode[]{TextNode.of("["), new TranslatedNode("options.hidden"), TextNode.of("]")},
                GeneralUtils.emptyStyle().setColor(TextFormatting.GRAY).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, GeneralUtils.emptyText())).setItalic(true),
                new ParentNode(textNodes), null, null);

    }

    public static TextNode defaultQuoteFormatting(TextNode[] textNodes) {
        return new StyledNode(textNodes, GeneralUtils.emptyStyle().setColor(TextFormatting.GRAY).setItalic(true), null, null, null);
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        if (input instanceof LiteralNode literalNode) {
            var list = new ArrayList<SubNode<?>>();
            parseLiteral(literalNode, list::add);
            return parseSubNodes(list.listIterator(), null, -1);
        } else if (input instanceof TranslatedNode translatedNode) {
            var list = new ArrayList<>();
            for (var arg : translatedNode.args()) {
                if (arg instanceof TextNode textNode) {
                    list.add(TextNode.asSingle(this.parseNodes(textNode)));
                } else {
                    list.add(arg);
                }
            }
            return new TextNode[]{new TranslatedNode(translatedNode.key(), list.toArray())};
        } else if (input instanceof ParentTextNode parentTextNode) {
            var list = new ArrayList<SubNode<?>>();
            for (var children : parentTextNode.getChildren()) {
                if (children instanceof LiteralNode literalNode) {
                    parseLiteral(literalNode, list::add);
                } else {
                    list.add(new SubNode<>(SubNodeType.TEXT_NODE, TextNode.asSingle(parseNodes(children))));
                }
            }
            return new TextNode[]{parentTextNode.copyWith(parseSubNodes(list.listIterator(), null, -1), this)};
        } else {
            return new TextNode[]{input};
        }
    }

    private void parseLiteral(LiteralNode literalNode, Consumer<SubNode<?>> consumer) {
        var reader = new StringReader(literalNode.value());
        var builder = new StringBuilder();

        while (reader.canRead()) {
            var i = reader.read();
            if (i == '\\' && reader.canRead()) {
                var next = reader.read();
                if (next != '~' && next != '`' && next != '_' && next != '*' && next != '|') {
                    builder.append(i);
                }
                builder.append(next);
                continue;
            }
            SubNodeType<String> type = null;

            if (reader.canRead()) {
                var i2 = reader.read();

                if (i2 == i) {
                    type = switch (i) {
                        case '~' -> SubNodeType.DOUBLE_WAVY_LINE;
                        case '|' -> SubNodeType.SPOILER_LINE;
                        default -> null;
                    };
                }

                if (type == null) {
                    reader.setCursor(reader.getCursor() - 1);
                }
            }

            if (type == null) {
                type = switch (i) {
                    case '`' -> SubNodeType.BACK_TICK;
                    case '*' -> SubNodeType.STAR;
                    case '_' -> SubNodeType.FLOOR;
                    default -> null;
                };
            }

            if (type != null) {
                if (builder.length() == 0) {
                    consumer.accept(new SubNode<>(SubNodeType.STRING, builder.toString()));
                    builder = new StringBuilder();
                }
                consumer.accept(new SubNode<>(type, type.selfValue));
            } else {
                builder.append(i);
            }
        }


        if (builder.length() != 0) {
            consumer.accept(new SubNode<>(SubNodeType.STRING, builder.toString()));
        }
    }

    private TextNode[] parseSubNodes(ListIterator<SubNode<?>> nodes, @Nullable SubNodeType endAt, int count) {
        var out = new ArrayList<TextNode>();
        int startIndex = nodes.nextIndex();
        var builder = new StringBuilder();
        while (nodes.hasNext()) {
            var next = nodes.next();

            if (next.type == endAt) {
                int foundCount = 1;

                if (foundCount == count) {
                    if (builder.length() != 0) {
                        out.add(new LiteralNode(builder.toString()));
                    }
                    return out.toArray(new TextNode[0]);
                }

                var xStart = nodes.nextIndex();

                while (nodes.hasNext()) {
                    if (nodes.next().type == endAt) {
                        if ((++foundCount) == count) {
                            if (builder.length() != 0) {
                                out.add(new LiteralNode(builder.toString()));
                            }
                            return out.toArray(new TextNode[0]);
                        }
                    } else {
                        break;
                    }
                }

                while (xStart != nodes.nextIndex()) {
                    nodes.previous();
                }
            }

            if (next.type == SubNodeType.TEXT_NODE) {
                if (builder.length() != 0) {
                    out.add(new LiteralNode(builder.toString()));
                    builder = new StringBuilder();
                }
                out.add((TextNode) next.value);
                continue;
            } else if (next.type == SubNodeType.STRING) {
                builder.append((String) next.value);
                continue;
            } else if (next.type == SubNodeType.BACK_TICK && this.allowedFormatting.contains(MarkdownFormat.QUOTE)) {
                var value = parseSubNodes(nodes, next.type, 1);

                if (value != null) {
                    if (builder.length() != 0) {
                        out.add(new LiteralNode(builder.toString()));
                        builder = new StringBuilder();
                    }
                    out.add(this.backtickFormatting.apply(value));
                    continue;
                }
            } else if (next.type == SubNodeType.SPOILER_LINE && this.allowedFormatting.contains(MarkdownFormat.SPOILER)) {
                var value = parseSubNodes(nodes, next.type, 1);

                if (value != null) {
                    if (builder.length() != 0) {
                        out.add(new LiteralNode(builder.toString()));
                        builder = new StringBuilder();
                    }
                    out.add(this.spoilerFormatting.apply(value));
                    continue;
                }
            } else if (next.type == SubNodeType.DOUBLE_WAVY_LINE && this.allowedFormatting.contains(MarkdownFormat.STRIKETHROUGH)) {
                var value = parseSubNodes(nodes, next.type, 1);

                if (value != null) {
                    if (builder.length() != 0) {
                        out.add(new LiteralNode(builder.toString()));
                        builder = new StringBuilder();
                    }
                    out.add(new FormattingNode(value, TextFormatting.STRIKETHROUGH));
                    continue;
                }
            } else if (next.type == SubNodeType.STAR || next.type == SubNodeType.FLOOR) {
                boolean two = false;
                if (nodes.hasNext()) {
                    if ((next.type == SubNodeType.STAR && this.allowedFormatting.contains(MarkdownFormat.BOLD))
                            || (next.type == SubNodeType.FLOOR && this.allowedFormatting.contains(MarkdownFormat.UNDERLINE))
                    ) {
                        var nexter = nodes.next();
                        if (nexter.type == next.type) {
                            two = true;
                            var i = nodes.nextIndex();
                            var value = parseSubNodes(nodes, next.type, 2);

                            if (value != null) {
                                if (builder.length() != 0) {
                                    out.add(new LiteralNode(builder.toString()));
                                    builder = new StringBuilder();
                                }
                                out.add(new FormattingNode(value, next.type == SubNodeType.STAR ? TextFormatting.BOLD : TextFormatting.UNDERLINE));
                                continue;
                            }
                        }
                        nodes.previous();
                    }
                }

                if (!two && this.allowedFormatting.contains(MarkdownFormat.ITALIC)) {
                    var value = parseSubNodes(nodes, next.type, 1);

                    if (value != null) {
                        if (builder.length() != 0) {
                            out.add(new LiteralNode(builder.toString()));
                            builder = new StringBuilder();
                        }
                        out.add(new FormattingNode(value, TextFormatting.ITALIC));
                        continue;
                    }
                }
            }

            builder.append((String) next.value);
        }

        if (endAt == null) {
            if (builder.length() != 0) {
                out.add(new LiteralNode(builder.toString()));
            }
            return out.toArray(new TextNode[0]);
        } else {
            while (startIndex != nodes.nextIndex()) {
                nodes.previous();
            }

            return null;
        }
    }

    public enum MarkdownFormat {
        BOLD,
        ITALIC,
        UNDERLINE,
        STRIKETHROUGH,
        QUOTE,
        SPOILER
    }

    @Desugar
    private record SubNodeType<T>(T selfValue) {
        public static final SubNodeType<TextNode> TEXT_NODE = new SubNodeType<>(null);
        public static final SubNodeType<String> STRING = new SubNodeType<>(null);

        public static final SubNodeType<String> STAR = new SubNodeType<>("*");
        public static final SubNodeType<String> FLOOR = new SubNodeType<>("_");
        public static final SubNodeType<String> DOUBLE_WAVY_LINE = new SubNodeType<>("~~");
        public static final SubNodeType<String> BACK_TICK = new SubNodeType<>("`");
        public static final SubNodeType<String> SPOILER_LINE = new SubNodeType<>("||");
    }

    @Desugar
    private record SubNode<T>(SubNodeType<T> type, T value) {
    }

}
