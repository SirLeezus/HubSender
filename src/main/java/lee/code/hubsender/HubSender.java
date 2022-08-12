package lee.code.hubsender;

import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

@Plugin(id = "hubsender", name = "HubSender", version = "1.0.0", authors = {"Lee"})
public class HubSender {

    @Getter private final ProxyServer proxy;
    @Getter private final Logger logger;

    @Inject
    public HubSender(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = proxy.getCommandManager();
        commandManager.register(createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> helloNode = LiteralArgumentBuilder
                .<CommandSource>literal("hub")
                .executes(context -> {
                    Component message = getHubMessage();
                    context.getSource().sendMessage(message);
                    if (context.getSource() instanceof Player player) {
                        if (proxy.getServer("hub").isPresent()) {
                            player.createConnectionRequest(proxy.getServer("hub").get()).fireAndForget();
                        }
                    }
                    return 1; // indicates success
                })
                .build();
        return new BrigadierCommand(helloNode);
    }

    private Component getHubMessage() {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(serializer.deserialize("&6Connecting you to server &ehub&6..."));
    }
}
