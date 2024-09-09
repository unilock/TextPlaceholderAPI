package eu.pb4.placeholders.impl;

import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.ImmutableList;
import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class GeneralUtils {
    public static final List<Integer> COLOR_CODES = ImmutableList.of(0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 5635925, 5636095, 16733525, 16733695, 16777045, 16777215);

    public static String durationToString(long x) {
        long seconds = x % 60;
        long minutes = (x / 60) % 60;
        long hours = (x / (60 * 60)) % 24;
        long days = x / (60 * 60 * 24);

        if (days > 0) {
            return String.format("%dd%dh%dm%ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh%dm%ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm%ds", minutes, seconds);
        } else if (seconds > 0) {
            return String.format("%ds", seconds);
        } else {
            return "---";
        }
    }

    public static boolean isEmpty(ITextComponent text) {
        return (
                text.getUnformattedComponentText().isEmpty()
                || (text instanceof TextComponentString l && l.getText().isEmpty())
               ) && text.getSiblings().isEmpty();
    }

    public static TextComponentBase toGradient(TextComponentBase base, GradientNode.GradientProvider posToColor) {
        return recursiveGradient(base, posToColor, 0, getGradientLength(base)).text();
    }

    private static int getGradientLength(ITextComponent base) {
        int length = base instanceof TextComponentString l ? l.getText().length() : base.getFormattedText().isEmpty() ? 0 : 1;

        for (var text : base.getSiblings()) {
            length += getGradientLength(text);
        }

        return length;
    }

    private static TextLengthPair recursiveGradient(ITextComponent base, GradientNode.GradientProvider posToColor, int pos, int totalLength) {
        if (base.getStyle().getColor() == null) {
            TextComponentBase out = (TextComponentBase) GeneralUtils.emptyText().setStyle(base.getStyle());
            if (base instanceof TextComponentString literalTextContent) {
                for (String letter : literalTextContent.getText().replaceAll("\\p{So}|.", "$0\0").split("\0+")) {
                    if (!letter.isEmpty()) {
                        out.appendSibling(new TextComponentString(letter).setStyle(emptyStyle().setColor(posToColor.getColorAt(pos++, totalLength))));

                    }
                }
            } else {
                out.appendSibling(base.createCopy().setStyle(emptyStyle().setColor(posToColor.getColorAt(pos++, totalLength))));

            }

            for (ITextComponent sibling : base.getSiblings()) {
                var pair = recursiveGradient(sibling, posToColor, pos, totalLength);
                pos = pair.length;
                out.appendSibling(pair.text);
            }
            return new TextLengthPair(out, pos);
        }
        return new TextLengthPair((TextComponentBase) base.createCopy(), pos + base.getUnformattedText().length());
    }

    public static int hvsToRgb(float hue, float saturation, float value) {
        int h = (int) (hue * 6) % 6;
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        return switch (h) {
            case 0 -> rgbToInt(value, t, p);
            case 1 -> rgbToInt(q, value, p);
            case 2 -> rgbToInt(p, value, t);
            case 3 -> rgbToInt(p, q, value);
            case 4 -> rgbToInt(t, p, value);
            case 5 -> rgbToInt(value, p, q);
            default -> 0;
        };
    }

    public static int rgbToInt(float r, float g, float b) {
        return (((int) (r * 0xff)) & 0xFF) << 16 | (((int) (g * 0xff)) & 0xFF) << 8 | (((int) (b * 0xff) & 0xFF));
    }

    public static HSV rgbToHsv(int rgb) {
        float b = (float) (rgb % 256) / 255;
        rgb = rgb >> 8;
        float g = (float) (rgb % 256) / 255;
        rgb = rgb >> 8;
        float r = (float) (rgb % 256) / 255;

        float cmax = Math.max(r, Math.max(g, b));
        float cmin = Math.min(r, Math.min(g, b));
        float diff = cmax - cmin;
        float h = -1, s = -1;

        if (cmax == cmin) {
            h = 0;
        } else if (cmax == r) {
            h = (0.1666f * ((g - b) / diff) + 1) % 1;
        } else if (cmax == g) {
            h = (0.1666f * ((b - r) / diff) + 0.333f) % 1;
        } else if (cmax == b) {
            h = (0.1666f * ((r - g) / diff) + 0.666f) % 1;
        }
        if (cmax == 0) {
            s = 0;
        } else {
            s = (diff / cmax);
        }

        return new HSV(h, s, cmax);
    }

    public static int textColorToRgb(TextFormatting textColor) {
        return COLOR_CODES.get(textColor.getColorIndex());
    }

    public static TextFormatting rgbToTextColor(int rgb) {
        return TextFormatting.fromColorIndex(COLOR_CODES.indexOf(COLOR_CODES.stream().min(Comparator.comparingInt(i -> Math.abs(i - rgb))).orElse(0)));
    }

    public static ITextComponent removeHoverAndClick(ITextComponent input) {
        var output = cloneText(input);
        removeHoverAndClick(output);
        return output;
    }

    private static void removeHoverAndClick(TextComponentBase input) {
        if (input.getStyle() != null) {
            input.setStyle(input.getStyle().setHoverEvent(null).setClickEvent(null));
        }

        if (input instanceof TextComponentTranslation text) {
            for (int i = 0; i < text.getFormatArgs().length; i++) {
                var arg = text.getFormatArgs()[i];
                if (arg instanceof TextComponentBase argText) {
                    removeHoverAndClick(argText);
                }
            }
        }

        for (var sibling : input.getSiblings()) {
            removeHoverAndClick((TextComponentBase) sibling);
        }

    }

    public static TextComponentBase cloneText(ITextComponent input) {
        TextComponentBase baseText;
        if (input instanceof TextComponentTranslation translatable) {
            var obj = new ArrayList<>();

            for (var arg : translatable.getFormatArgs()) {
                if (arg instanceof ITextComponent argText) {
                    obj.add(cloneText(argText));
                } else {
                    obj.add(arg);
                }
            }

            baseText = new TextComponentTranslation(translatable.getKey(), obj.toArray());
        } else {
            baseText = (TextComponentBase) input.createCopy();
        }

        for (var sibling : input.getSiblings()) {
            baseText.appendSibling(cloneText(sibling));
        }

        baseText.setStyle(input.getStyle());
        return baseText;
    }

    public static ITextComponent getItemText(ItemStack stack) {
        if (!stack.isEmpty()) {
            return stack.getTextComponent();
        }

        return emptyText().appendText(ItemStack.EMPTY.getDisplayName());
    }

    public static ParentNode convertToNodes(ITextComponent input) {
        var list = new ArrayList<TextNode>();

        if (input instanceof TextComponentString content) {
            list.add(new LiteralNode(content.getText()));
        } else if (input instanceof TextComponentTranslation content) {
            var args = new ArrayList<>();
            for (var arg : content.getFormatArgs()) {
                if (arg instanceof ITextComponent text) {
                    args.add(convertToNodes(text));
                } else if (arg instanceof String s) {
                    args.add(new LiteralNode(s));
                } else {
                    args.add(arg);
                }
            }


            list.add(new TranslatedNode(content.getKey(), args.toArray()));
        } else if (input instanceof TextComponentScore content) {
            list.add(new ScoreNode(content.getName(), content.getObjective()));
        } else if (input instanceof TextComponentKeybind content) {
            list.add(new KeybindNode(content.getKeybind()));
        }


        for (var child : input.getSiblings()) {
            list.add(convertToNodes(child));
        }

        if (input.getStyle() == GeneralUtils.emptyStyle()) {
            return new ParentNode(list.toArray(new TextNode[0]));
        } else {
            var style = input.getStyle();
            var hoverValue = style.getHoverEvent() != null && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT
                    ? convertToNodes(style.getHoverEvent().getValue()) : null;

            var clickValue = style.getClickEvent() != null ? new LiteralNode(style.getClickEvent().getValue()) : null;
            var insertion = style.getInsertion() != null ? new LiteralNode(style.getInsertion()) : null;

            return new StyledNode(list.toArray(new TextNode[0]), style, hoverValue, clickValue, insertion);
        }
    }

    public static TextNode removeColors(TextNode node) {
        if (node instanceof ParentTextNode parentNode) {
            var list = new ArrayList<TextNode>();

            for (var child : parentNode.getChildren()) {
                list.add(removeColors(child));
            }

            if (node instanceof ColorNode || node instanceof FormattingNode) {
                return new ParentNode(list.toArray(new TextNode[0]));
            } else if (node instanceof StyledNode styledNode) {
                return new StyledNode(list.toArray(new TextNode[0]), styledNode.rawStyle().setColor((TextFormatting) null), styledNode.hoverValue(), styledNode.clickValue(), styledNode.insertion());
            }

            return parentNode.copyWith(list.toArray(new TextNode[0]));
        } else {
            return node;
        }
    }

    public static TextComponentBase emptyText() {
        return new TextComponentString("");
    }

    public static Style emptyStyle() {
        return emptyText().getStyle();
    }

    @Desugar
    public record HSV(float h, float s, float v) {
    }

    @Desugar
    public record TextLengthPair(TextComponentBase text, int length) {
        public static final TextLengthPair EMPTY = new TextLengthPair(null, 0);
    }

    @Desugar
    public record Pair<L, R>(L left, R right) {
    }
}
