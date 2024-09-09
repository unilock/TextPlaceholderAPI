package eu.pb4.placeholders.impl.textparser;

import com.google.common.collect.Lists;
import eu.pb4.placeholders.api.node.*;
import eu.pb4.placeholders.api.node.parent.*;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

import static eu.pb4.placeholders.impl.textparser.TextParserImpl.*;

//@ApiStatus.Internal
public final class TextTags {
    public static void register() {
        {
            Map<String, List<String>> aliases = new HashMap<>();
            aliases.put("gold", Lists.newArrayList("orange"));
            aliases.put("gray", Lists.newArrayList("grey"));
            aliases.put("light_purple", Lists.newArrayList("pink"));
            aliases.put("dark_gray", Lists.newArrayList("dark_grey"));
            aliases.put("strikethrough", Lists.newArrayList("st"));
            aliases.put("obfuscated", Lists.newArrayList("obf"));
            aliases.put("italic", Lists.newArrayList("i"));
            aliases.put("bold", Lists.newArrayList("b"));
            aliases.put("underline", Lists.newArrayList("underlined"));

            for (TextFormatting formatting : TextFormatting.values()) {
                TextParserV1.registerDefault(
                        TextParserV1.TextTag.of(
                                formatting.getFriendlyName(),
                                aliases.containsKey(formatting.getFriendlyName()) ? aliases.get(formatting.getFriendlyName()) : new ArrayList<>(),
                                formatting.isColor() ? "color" : "formatting",
                                true,
                                (tag, data, input, handlers, endAt) -> {
                                    var out = recursiveParsing(input, handlers, endAt);
                                    return new TextParserV1.TagNodeValue(new FormattingNode(out.nodes(), formatting), out.length());
                                })
                );
            }
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "color",
                            Lists.newArrayList("colour", "c"),
                            "color",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                return new TextParserV1.TagNodeValue(new ColorNode(out.nodes(), TextFormatting.getValueByName(cleanArgument(data))), out.length());
                            }
                    )
            );
        }
//        {
//            TextParserV1.registerDefault(
//                    TextParserV1.TextTag.of(
//                            "font",
//                            "other_formatting",
//                            false,
//                            (tag, data, input, handlers, endAt) -> {
//                                var out = recursiveParsing(input, handlers, endAt);
//                                return out.value(new FontNode(out.nodes(), Identifier.tryParse(cleanArgument(data))));
//                            }
//                    )
//            );
//        }

        {
            TextParserV1.registerDefault(TextParserV1.TextTag.of(
                    "lang",
                    Lists.newArrayList("translate"),
                    "special",
                    false,
                    (tag, data, input, handlers, endAt) -> {
                        var lines = data.split(":");
                        if (lines.length > 0) {
                            List<TextNode> textList = new ArrayList<>();
                            boolean skipped = false;
                            for (String part : lines) {
                                if (!skipped) {
                                    skipped = true;
                                    continue;
                                }
                                textList.add(new ParentNode(parse(removeEscaping(cleanArgument(part)), handlers)));
                            }

                            var out = new TranslatedNode(removeEscaping(cleanArgument(lines[0])), textList.toArray(new TextNode[0]));
                            return new TextParserV1.TagNodeValue(out, 0);
                        }
                        return TextParserV1.TagNodeValue.EMPTY;
                    }));
        }

        {
            TextParserV1.registerDefault(TextParserV1.TextTag.of("keybind",
                    "special",
                    false,
                    (tag, data, input, handlers, endAt) -> {
                        if (!data.isEmpty()) {
                            return new TextParserV1.TagNodeValue(new KeybindNode(cleanArgument(data)), 0);
                        }
                        return TextParserV1.TagNodeValue.EMPTY;
                    }));
        }

        {
            TextParserV1.registerDefault(TextParserV1.TextTag.of("click", "click_action", false, (tag, data, input, handlers, endAt) -> {
                String[] lines = data.split(":", 2);
                var out = recursiveParsing(input, handlers, endAt);
                if (lines.length > 1) {
                    ClickEvent.Action action = ClickEvent.Action.getValueByCanonicalName(cleanArgument(lines[0]));
                    if (action != null) {
                        return out.value(new ClickActionNode(out.nodes(), action, new LiteralNode(restoreOriginalEscaping(cleanArgument(lines[1])))));
                    }
                }
                return out.value(new ParentNode(out.nodes()));
            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "run_command",
                            Lists.newArrayList("run_cmd"),
                            "click_action",
                            false,
                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.RUN_COMMAND, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "suggest_command",
                            Lists.newArrayList("cmd"),
                            "click_action",
                            false,
                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.SUGGEST_COMMAND, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "open_url",
                            Lists.newArrayList("url"),
                            "click_action",
                            false, (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.OPEN_URL, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }
                    )
            );
        }

//        {
//            TextParserV1.registerDefault(
//                    TextParserV1.TextTag.of(
//                            "copy_to_clipboard",
//                            Lists.newArrayList("copy"),
//                            "click_action",
//                            false,
//                            (tag, data, input, handlers, endAt) -> {
//                                var out = recursiveParsing(input, handlers, endAt);
//                                if (!data.isEmpty()) {
//                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.COPY_TO_CLIPBOARD, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
//                                }
//                                return out.value(new ParentNode(out.nodes()));
//                            }
//                    )
//            );
//        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "change_page",
                            Lists.newArrayList("page"),
                            "click_action",
                            true, (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                if (!data.isEmpty()) {
                                    return out.value(new ClickActionNode(out.nodes(), ClickEvent.Action.CHANGE_PAGE, new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "hover",
                            "hover_event",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] lines = data.split(":", 2);
                                var out = recursiveParsing(input, handlers, endAt);

                                try {
                                    if (lines.length > 1) {
                                        HoverEvent.Action action = HoverEvent.Action.getValueByCanonicalName(cleanArgument(lines[0].toLowerCase(Locale.ROOT)));

                                        if (action == HoverEvent.Action.SHOW_TEXT) {
                                            return out.value(new HoverNode<>(out.nodes(), HoverNode.Action.TEXT, new ParentNode(parse(restoreOriginalEscaping(cleanArgument(lines[1])), handlers))));
                                        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                                            lines = lines[1].split(":", 3);
                                            if (lines.length == 3) {
                                                return out.value(new HoverNode<>(out.nodes(),
                                                        HoverNode.Action.ENTITY,
                                                        new HoverNode.EntityNodeContent(Optional.ofNullable(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(restoreOriginalEscaping(restoreOriginalEscaping(cleanArgument(lines[0])))))).orElse(ForgeRegistries.ENTITIES.getValue(new ResourceLocation("pig"))))
                                                ));
                                            }
                                        } else if (action == HoverEvent.Action.SHOW_ITEM) {
                                            return out.value(new HoverNode<>(out.nodes(),
                                                    HoverNode.Action.ITEM_STACK,
                                                    new HoverNode.ItemStackNodeContent(new ItemStack(JsonToNBT.getTagFromJson(restoreOriginalEscaping(cleanArgument(lines[1])))))
                                            ));
                                        } else {
                                            return out.value(new HoverNode<>(out.nodes(), HoverNode.Action.TEXT, new ParentNode(parse(restoreOriginalEscaping(cleanArgument(data)), handlers))));
                                        }
                                    } else {
                                        return out.value(new HoverNode<>(out.nodes(), HoverNode.Action.TEXT, new ParentNode(parse(restoreOriginalEscaping(cleanArgument(data)), handlers))));
                                    }
                                } catch (Exception e) {
                                    // Shut
                                }
                                return out.value(new ParentNode(out.nodes()));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "insert",
                            "click_action",
                            false,

                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);
                                return out.value(new InsertNode(out.nodes(), new LiteralNode(restoreOriginalEscaping(cleanArgument(data)))));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "clear_color",
                            Lists.newArrayList("uncolor", "colorless"),
                            "special",
                            false,

                            (tag, data, input, handlers, endAt) -> {
                                var out = recursiveParsing(input, handlers, endAt);

                                return out.value(GeneralUtils.removeColors(new ParentNode(out.nodes())));
                            }));
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "rainbow",
                            Lists.newArrayList("rb"),
                            "gradient",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] val = data.split(":");
                                float freq = 1;
                                float saturation = 1;
                                float offset = 0;
                                int overriddenLength = -1;

                                if (val.length >= 1) {
                                    try {
                                        freq = Float.parseFloat(val[0]);
                                    } catch (Exception e) {
                                        // No u
                                    }
                                }
                                if (val.length >= 2) {
                                    try {
                                        saturation = Float.parseFloat(val[1]);
                                    } catch (Exception e) {
                                        // Idc
                                    }
                                }
                                if (val.length >= 3) {
                                    try {
                                        offset = Float.parseFloat(val[2]);
                                    } catch (Exception e) {
                                        // Ok float
                                    }
                                }

                                if (val.length >= 4) {
                                    try {
                                        overriddenLength = Integer.parseInt(val[3]);
                                    } catch (Exception e) {
                                        // Ok float
                                    }
                                }

                                var out = recursiveParsing(input, handlers, endAt);

                                final float finalFreq = freq;
                                final float finalFreqLength = (finalFreq < 0 ? -freq : 0);
                                final float finalOffset = offset;
                                final float finalSaturation = saturation;
                                final int finalOverriddenLength = overriddenLength;

                                return out.value(new GradientNode(out.nodes(), finalOverriddenLength < 0
                                        ? (pos, length) -> GeneralUtils.rgbToTextColor(GeneralUtils.hvsToRgb((((pos * finalFreq) + (finalFreqLength * length)) / (length + 1) + finalOffset) % 1, finalSaturation, 1))
                                        : (pos, length) -> GeneralUtils.rgbToTextColor(GeneralUtils.hvsToRgb((((pos * finalFreq) + (finalFreqLength * length)) / (finalOverriddenLength + 1) + finalOffset) % 1, finalSaturation, 1))

                                ));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "gradient",
                            Lists.newArrayList("gr"),
                            "gradient",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] val = data.split(":");

                                var out = recursiveParsing(input, handlers, endAt);
                                //String flatString = GeneralUtils.textToString(out.text());
                                List<TextFormatting> textColors = new ArrayList<>();
                                for (String string : val) {
                                    TextFormatting color = TextFormatting.getValueByName(string);
                                    if (color != null) {
                                        textColors.add(color);
                                    }
                                }
                                if (textColors.size() == 0) {
                                    textColors.add(TextFormatting.WHITE);
                                    textColors.add(TextFormatting.WHITE);
                                } else if (textColors.size() == 1) {
                                    textColors.add(textColors.get(0));
                                }

                                GeneralUtils.HSV hsv = GeneralUtils.rgbToHsv(GeneralUtils.textColorToRgb(textColors.get(0)));

                                final int colorSize = textColors.size();

                                return out.value(new GradientNode(out.nodes(), (pos, length) -> {
                                    final double step = ((double) colorSize - 1) / length;
                                    final float sectionSize = ((float) length) / (colorSize - 1);
                                    final float progress = (pos % sectionSize) / sectionSize;

                                    GeneralUtils.HSV colorA = GeneralUtils.rgbToHsv(GeneralUtils.textColorToRgb(textColors.get(Math.min((int) (pos / sectionSize), colorSize - 1))));
                                    GeneralUtils.HSV colorB = GeneralUtils.rgbToHsv(GeneralUtils.textColorToRgb(textColors.get(Math.min((int) (pos / sectionSize) + 1, colorSize - 1))));

                                    float hue;
                                    {
                                        float h = colorB.h() - colorA.h();
                                        float delta = (h + ((Math.abs(h) > 0.50001) ? ((h < 0) ? 1 : -1) : 0));

                                        float futureHue = (float) (colorA.h() + delta * step * pos);
                                        if (futureHue < 0) {
                                            futureHue += 1;
                                        } else if (futureHue > 1) {
                                            futureHue -= 1;
                                        }
                                        hue = futureHue;
                                    }

                                    float sat = MathHelper.clamp(colorB.s() * progress + colorA.s() * (1 - progress), 0, 1);

                                    float value = MathHelper.clamp(colorB.v() * progress + colorA.v() * (1 - progress), 0, 1);

                                    return GeneralUtils.rgbToTextColor(GeneralUtils.hvsToRgb(
                                            MathHelper.clamp(hue, 0, 1),
                                            sat,
                                            value));
                                }));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "hard_gradient",
                            Lists.newArrayList("hgr"),
                            "gradient",
                            true,
                            (tag, data, input, handlers, endAt) -> {
                                String[] val = data.split(":");

                                var out = recursiveParsing(input, handlers, endAt);

                                var textColors = new ArrayList<TextFormatting>();

                                for (String string : val) {
                                    TextFormatting color = TextFormatting.getValueByName(string);
                                    if (color != null) {
                                        textColors.add(color);
                                    }
                                }

                                if (textColors.size() == 0) {
                                    textColors.add(TextFormatting.WHITE);
                                    textColors.add(TextFormatting.WHITE);
                                } else if (textColors.size() == 1) {
                                    textColors.add(textColors.get(0));
                                }

                                final int colorSize = textColors.size();

                                return out.value(new GradientNode(out.nodes(), (pos, length) -> {
                                    if (length == 0) {
                                        return textColors.get(0);
                                    }

                                    final float sectionSize = ((float) length) / colorSize;

                                    return textColors.get(Math.min((int) (pos / sectionSize), colorSize - 1));
                                }));
                            }
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "raw_style",
                            "special",
                            false,
                            (tag, data, input, handlers, endAt) -> new TextParserV1.TagNodeValue(new DirectTextNode(ITextComponent.Serializer.fromJsonLenient(restoreOriginalEscaping(cleanArgument(data)))), 0)
                    )
            );
        }

        {
            TextParserV1.registerDefault(
                    TextParserV1.TextTag.of(
                            "score",
                            "special",
                            false, (tag, data, input, handlers, endAt) -> {
                                String[] lines = data.split(":");
                                if (lines.length == 2) {
                                    return new TextParserV1.TagNodeValue(new ScoreNode(restoreOriginalEscaping(cleanArgument(lines[0])), restoreOriginalEscaping(cleanArgument(lines[1]))), 0);
                                }
                                return TextParserV1.TagNodeValue.EMPTY;
                            }
                    )
            );

        }
    }
}