package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorldPlaceholders {
    static final int CHUNK_AREA = (int)Math.pow(17.0D, 2.0D);

    public static void register() {
        Placeholders.register(new ResourceLocation("world", "time"), (ctx, arg) -> {
            WorldServer world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = (WorldServer) ctx.server().getEntityWorld();
            }

            long dayTime = (long) (world.getWorldTime() * 3.6 / 60);

            return PlaceholderResult.value(String.format("%02d:%02d", (dayTime / 60 + 6) % 24, dayTime % 60));
        });

        Placeholders.register(new ResourceLocation("world", "time_alt"), (ctx, arg) -> {
            WorldServer world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = (WorldServer) ctx.server().getEntityWorld();
            }

            long dayTime = (long) (world.getWorldTime() * 3.6 / 60);
            long x = (dayTime / 60 + 6) % 24;
            long y = x % 12;
            if (y == 0) {
                y = 12;
            }
            return PlaceholderResult.value(String.format("%02d:%02d %s", y, dayTime % 60, x > 11 ? "PM" : "AM" ));
        });

        Placeholders.register(new ResourceLocation("world", "day"), (ctx, arg) -> {
            WorldServer world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = (WorldServer) ctx.server().getEntityWorld();
            }

            return PlaceholderResult.value("" + world.getTotalWorldTime() / 24000);
        });

        Placeholders.register(new ResourceLocation("world", "id"), (ctx, arg) -> {
            WorldServer world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = (WorldServer) ctx.server().getEntityWorld();
            }

            return PlaceholderResult.value(world.getWorldInfo().getWorldName());
        });

        Placeholders.register(new ResourceLocation("world", "name"), (ctx, arg) -> {
            WorldServer world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = (WorldServer) ctx.server().getEntityWorld();
            }
            List<String> parts = new ArrayList<>();
            {
                String[] words = world.getWorldInfo().getWorldName().split("_");
                for (String word : words) {
                    String[] s = word.split("", 2);
                    s[0] = s[0].toUpperCase(Locale.ROOT);
                    parts.add(String.join("", s));
                }
            }
            return PlaceholderResult.value(String.join(" ", parts));
        });



        Placeholders.register(new ResourceLocation("world", "player_count"), (ctx, arg) -> {
            WorldServer world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = (WorldServer) ctx.server().getEntityWorld();
            }

            return PlaceholderResult.value("" + world.playerEntities.size());
        });

        Placeholders.register(new ResourceLocation("world", "mob_count"), (ctx, arg) -> {
            WorldServer world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = (WorldServer) ctx.server().getEntityWorld();
            }

            // Cannot figure out the SpawnGroup stuff on 1.12.2...
            return PlaceholderResult.value("" + world.loadedEntityList.size());
        });

//        Placeholders.register(new ResourceLocation("world", "mob_cap"), (ctx, arg) -> {
//            WorldServer world;
//            if (ctx.player() != null) {
//                world = ctx.player().getServerWorld();
//            } else {
//                world = (WorldServer) ctx.server().getEntityWorld();
//            }
//
//            SpawnHelper.Info info = world.getChunkManager().getSpawnInfo();
//
//            SpawnGroup spawnGroup = null;
//            if (arg != null) {
//                spawnGroup = SpawnGroup.valueOf(arg.toUpperCase(Locale.ROOT));
//            }
//
//            if (spawnGroup != null) {
//                return PlaceholderResult.value("" + spawnGroup.getCapacity() * info.getSpawningChunkCount() / CHUNK_AREA);
//            } else {
//                int x = 0;
//
//                for (SpawnGroup group : SpawnGroup.values()) {
//                    x += group.getCapacity();
//                }
//                return PlaceholderResult.value("" + x * info.getSpawningChunkCount() / CHUNK_AREA);
//            }
//        });
    }
}
