package net.darkdustry.bot;

import arc.util.Log;

import net.darkdustry.bot.components.ConfigUtils;
import net.darkdustry.bot.components.ResourceUtils;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_EMOJIS_AND_STICKERS;
import static net.dv8tion.jda.internal.requests.RestActionImpl.setDefaultFailure;
import static net.darkdustry.bot.Vars.config;

public class Main {

    public static void main(String[] args) {
        Vars.cache.delete();

        Vars.dataDirectory.mkdirs();
        Vars.cache.mkdirs();
        Vars.resources.mkdirs();
        Vars.sprites.mkdirs();

        ConfigUtils.init();
        ResourceUtils.init();

        setDefaultFailure(null);

        try {
            Vars.jda = JDABuilder.createLight(config.token)
                    .enableIntents(GUILD_MEMBERS, MESSAGE_CONTENT, GUILD_EMOJIS_AND_STICKERS)
                    .enableCache(CacheFlag.EMOJI)
                    .addEventListeners(new Listener())
                    .build()
                    .awaitReady();

            Vars.guild = Vars.jda.getGuildById(config.guildId);
            Vars.emojiGuild = Vars.jda.getGuildById(config.emojiGuildId);
            Vars.mapsChannel = Vars.jda.getForumChannelById(config.mapsChannelId);
            Vars.schematicsChannel = Vars.jda.getForumChannelById(config.schematicsChannelId);

        } catch (Exception e) {
            Log.err("Failed to launch Mindustry Content Bot. Make sure the provided token and guild/channel IDs in the configuration are correct.");
            Log.err(e);
        }

        Listener.registerCommands();
    }
}