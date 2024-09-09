package eu.pb4.placeholders.api;

import com.github.bsideup.jabel.Desugar;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

@Desugar
public record PlaceholderContext(MinecraftServer server,
                                 ICommandSender source,
                                 @Nullable WorldServer world,
                                 @Nullable EntityPlayerMP player,
                                 @Nullable Entity entity,
                                 @Nullable GameProfile gameProfile
) {



    public static ParserContext.Key<PlaceholderContext> KEY = new ParserContext.Key<>("placeholder_context", PlaceholderContext.class);

    public boolean hasWorld() {
        return this.world != null;
    }

    public boolean hasPlayer() {
        return this.player != null;
    }

    public boolean hasGameProfile() {
        return this.gameProfile != null;
    }

    public boolean hasEntity() {
        return this.entity != null;
    }

    public ParserContext asParserContext() {
        return ParserContext.of(KEY, this);
    }

    public static PlaceholderContext of(MinecraftServer server) {
        return new PlaceholderContext(server,  server, null, null, null, null);
    }

    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        var name = profile.getName() != null ? profile.getName() : profile.getId().toString();
        return new PlaceholderContext(server, new ICommandSender() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean canUseCommand(int permLevel, String commandName) {
                return false;
            }

            @Override
            public World getEntityWorld() {
                return server.getWorld(0);
            }

            @Nullable
            @Override
            public MinecraftServer getServer() {
                return server;
            }
        }, null, null, null, profile);
    }

    public static PlaceholderContext of(EntityPlayerMP player) {
        return new PlaceholderContext(player.getServer(), player, player.getServerWorld(), player, player, player.getGameProfile());
    }

    public static PlaceholderContext of(ICommandSender source) {
        if (source.getCommandSenderEntity() instanceof EntityPlayerMP player) {
            return of(player);
        } else {
            return new PlaceholderContext(source.getServer(), source, (WorldServer) source.getEntityWorld(), null, source.getCommandSenderEntity(), null);
        }
    }

    public static PlaceholderContext of(Entity entity) {
        if (entity instanceof EntityPlayerMP player) {
            return of(player);
        } else {
            return new PlaceholderContext(entity.getServer(), entity, (WorldServer) entity.getEntityWorld(), null, entity, null);
        }
    }
}
