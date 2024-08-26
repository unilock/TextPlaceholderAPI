package eu.pb4.placeholders.api;

import com.github.bsideup.jabel.Desugar;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

@Desugar
public record PlaceholderContext(MinecraftServer server,
                                 Entity source,
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
        return new PlaceholderContext(server,  server.getCommandSenderEntity(), null, null, null, null);
    }

    // TODO: not sure about this one
    public static PlaceholderContext of(GameProfile profile, MinecraftServer server) {
        return new PlaceholderContext(server, server.getCommandSenderEntity(), null, null, null, profile);
    }

    public static PlaceholderContext of(EntityPlayerMP player) {
        return new PlaceholderContext(player.getServer(), player.getCommandSenderEntity(), player.getServerWorld(), player, player, player.getGameProfile());
    }

    public static PlaceholderContext of(Entity entity) {
        if (entity instanceof EntityPlayerMP player) {
            return of(player);
        } else {
            return new PlaceholderContext(entity.getServer(), entity.getCommandSenderEntity(), (WorldServer) entity.getEntityWorld(), null, entity, null);
        }
    }
}
