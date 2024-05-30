package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.util.Formatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ServerPlaceholders {
    public static void register() {
        Placeholders.register(new ResourceLocation("server", "tps"), (ctx, arg) -> {
            double tps = 1000f / Math.max(ctx.server().getTickTime(), 50);
            String format = "%.1f";

            if (arg != null) {
                try {
                    int x = Integer.parseInt(arg);
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }

            return PlaceholderResult.value(String.format(format, tps));
        });

        Placeholders.register(new ResourceLocation("server", "tps_colored"), (ctx, arg) -> {
            double tps = 1000f / Math.max(ctx.server().getTickTime(), 50);
            String format = "%.1f";

            if (arg != null) {
                try {
                    int x = Integer.parseInt(arg);
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }
            return PlaceholderResult.value(new TextComponentString(String.format(format, tps)).formatted(tps > 19 ? Formatting.GREEN : tps > 16 ? Formatting.GOLD : Formatting.RED));
        });

        Placeholders.register(new ResourceLocation("server", "mspt"), (ctx, arg) -> PlaceholderResult.value(String.format("%.0f", ctx.server().getTickTime())));

        Placeholders.register(new ResourceLocation("server", "mspt_colored"), (ctx, arg) -> {
            float x = ctx.server().getTickTime();
            return PlaceholderResult.value(new TextComponentString(String.format("%.0f", x)).formatted(x < 45 ? Formatting.GREEN : x < 51 ? Formatting.GOLD : Formatting.RED));
        });


        Placeholders.register(new ResourceLocation("server", "time"), (ctx, arg) -> {
            SimpleDateFormat format = new SimpleDateFormat(arg != null ? arg : "HH:mm:ss");
            return PlaceholderResult.value(format.format(new Date(System.currentTimeMillis())));
        });

        Placeholders.register(new ResourceLocation("server", "version"), (ctx, arg) -> PlaceholderResult.value(ctx.server().getVersion()));

        Placeholders.register(new ResourceLocation("server", "mod_version"), (ctx, arg) -> {
            if (arg != null) {
                var container = Loader.instance().getIndexedModList().get(arg);

                return PlaceholderResult.value(new TextComponentString(container.getDisplayVersion()));
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(new ResourceLocation("server", "mod_name"), (ctx, arg) -> {
            if (arg != null) {
                var container = Loader.instance().getIndexedModList().get(arg);

                return PlaceholderResult.value(new TextComponentString(container.getName()));
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(new ResourceLocation("server", "mod_description"), (ctx, arg) -> {
            if (arg != null) {
                var container = Loader.instance().getIndexedModList().get(arg);

                return PlaceholderResult.value(new TextComponentString(container.getMetadata().description));
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(new ResourceLocation("server", "name"), (ctx, arg) -> PlaceholderResult.value(ctx.server().getName()));

        Placeholders.register(new ResourceLocation("server", "used_ram"), (ctx, arg) -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

            return PlaceholderResult.value(Objects.equals(arg, "gb")
                    ? String.format("%.1f", (float) heapUsage.getUsed() / 1073741824)
                    : String.format("%d", heapUsage.getUsed() / 1048576));
            });

        Placeholders.register(new ResourceLocation("server", "max_ram"), (ctx, arg) -> {
                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

                    return PlaceholderResult.value(Objects.equals(arg, "gb")
                            ? String.format("%.1f", (float) heapUsage.getMax() / 1073741824)
                            : String.format("%d", heapUsage.getMax() / 1048576));
                });

        Placeholders.register(new ResourceLocation("server", "online"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getCurrentPlayerCount())));
        Placeholders.register(new ResourceLocation("server", "max_players"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getMaxPlayers())));
    }
}
