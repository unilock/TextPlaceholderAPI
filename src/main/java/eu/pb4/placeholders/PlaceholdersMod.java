package eu.pb4.placeholders;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class PlaceholdersMod {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandBase() {
            @Override
            public String getName() {
                return "parse";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return "parse <string>";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
                String text = String.join(" ", args);
                sender.sendMessage(new TextComponentString("Placeholders"));
                sender.sendMessage(Placeholders.parseText(TextParserUtils.formatText(text), PlaceholderContext.of(sender)));
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 2;
            }
        });
    }
}
