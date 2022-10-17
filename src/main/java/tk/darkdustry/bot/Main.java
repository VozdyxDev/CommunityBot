package tk.darkdustry.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.internal.entities.WebhookImpl;
import tk.darkdustry.bot.components.ConfigUtils;
import tk.darkdustry.bot.components.ResourceUtils;

import static arc.util.Log.err;
import static java.util.Objects.requireNonNull;
import static net.dv8tion.jda.api.entities.WebhookType.INCOMING;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.internal.requests.RestActionImpl.setDefaultFailure;
import static tk.darkdustry.bot.Vars.*;

public class Main {

    public static void main(String[] args) {
        cache.delete();

        dataDirectory.mkdirs();
        cache.mkdirs();
        resources.mkdirs();
        sprites.mkdirs();

        ConfigUtils.init();
        ResourceUtils.init();

        setDefaultFailure(null);

        try {
            jda = JDABuilder.createLight(config.token)
                    .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT)
                    .addEventListeners(new Listener())
                    .build()
                    .awaitReady();

            mapsWebhook = new WebhookImpl(requireNonNull(jda.getTextChannelById(config.mapsChannelId)), config.mapsWebhookId, INCOMING).setToken(config.mapsWebhookToken);
            schematicsWebhook = new WebhookImpl(requireNonNull(jda.getTextChannelById(config.schematicsChannelId)), config.schematicsWebhookId, INCOMING).setToken(config.schematicsWebhookToken);
        } catch (Exception e) {
            err("Failed to launch Community Bot. Make sure the provided token and guild/channel IDs in the configuration are correct.");
            err(e);
        }

        Listener.loadCommands(config.prefix);
    }
}