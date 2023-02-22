package net.darkdustry.bot;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import arc.util.serialization.Json;
import mindustry.type.Item;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.darkdustry.bot.components.ConfigUtils.Config;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;

public class Vars {
    public static final Json json = new Json();
    public static final CommandHandler handler = new CommandHandler("");

    public static final Fi dataDirectory = Fi.get(".content");
    public static final Fi cache = dataDirectory.child("cache");
    public static final Fi resources = dataDirectory.child("resources");
    public static final Fi sprites = dataDirectory.child("sprites");

    public static final ObjectMap<String, BufferedImage> regions = new ObjectMap<>();
    public static final ObjectMap<Item, Long> emojis = new ObjectMap<>();

    public static Config config;
    public static JDA jda;
    public static ForumChannel mapsChannel, schematicsChannel;
    public static Guild guild, emojiGuild;
}
