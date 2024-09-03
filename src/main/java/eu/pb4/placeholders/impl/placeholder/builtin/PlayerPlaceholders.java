package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.time.DurationFormatUtils;


public class PlayerPlaceholders {
    public static void register() {
        Placeholders.register(new ResourceLocation("player", "name"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(new TextComponentString(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "name_visual"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(new TextComponentString(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "name_unformatted"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(new TextComponentString(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "ping"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.valueOf(ctx.player().ping));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "ping_colored"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                int x = ctx.player().ping;
                return PlaceholderResult.value(new TextComponentString(String.valueOf(x)).setStyle(GeneralUtils.emptyStyle().setColor(x < 100 ? TextFormatting.GREEN : x < 200 ? TextFormatting.GOLD : TextFormatting.RED)));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "displayname"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getDisplayName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(new TextComponentString(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "displayname_visual"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.player().getDisplayName()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(new TextComponentString(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "displayname_unformatted"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(new TextComponentString(ctx.player().getDisplayName().getFormattedText()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(new TextComponentString(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "inventory_slot"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    int slot = Integer.parseInt(arg);

                    var inventory = ctx.player().inventory;

                    if (slot >= 0 && slot < inventory.getSizeInventory()) {
                        var stack = inventory.getStackInSlot(slot);

                        return PlaceholderResult.value(GeneralUtils.getItemText(stack));
                    }

                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "equipment_slot"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    var slot = EntityEquipmentSlot.fromString(arg);

                    var stack = ctx.player().getItemStackFromSlot(slot);
                    return PlaceholderResult.value(GeneralUtils.getItemText(stack));
                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "playtime"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                int x = ctx.player().getStatFile().readStat(StatList.PLAY_ONE_MINUTE) * 60;
                return PlaceholderResult.value(arg != null
                        ? DurationFormatUtils.formatDuration((long) x * 50, arg, true)
                        : GeneralUtils.durationToString((long) x / 20)
                );
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "statistic"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    for (StatBase stat : StatList.ALL_STATS) {
                        if (arg.equals(stat.statId)) {
                            int x = ctx.player().getStatFile().readStat(stat);
                            return PlaceholderResult.value(String.valueOf(x));
                        }
                    }
                } catch (Exception e) {
                    /* Into the void you go! */
                }
                return PlaceholderResult.invalid("Invalid statistic!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "pos_x"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.player().posX;
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.parseInt(arg);
                        format = "%." + x + "f";
                    } catch (Exception e) {
                        format = "%.2f";
                    }
                }

                return PlaceholderResult.value(String.format(format, value));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "pos_y"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.player().posY;
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.parseInt(arg);
                        format = "%." + x + "f";
                    } catch (Exception e) {
                        format = "%.2f";
                    }
                }

                return PlaceholderResult.value(String.format(format, value));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "pos_z"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.player().posZ;
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.parseInt(arg);
                        format = "%." + x + "f";
                    } catch (Exception e) {
                        format = "%.2f";
                    }
                }

                return PlaceholderResult.value(String.format(format, value));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "uuid"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getCachedUniqueIdString());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(new TextComponentString("" + ctx.gameProfile().getId()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "max_health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getMaxHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "hunger"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", (float) ctx.player().getFoodStats().getFoodLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new ResourceLocation("player", "saturation"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getFoodStats().getSaturationLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }
}
