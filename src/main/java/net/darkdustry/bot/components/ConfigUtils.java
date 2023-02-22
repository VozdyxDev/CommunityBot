package net.darkdustry.bot.components;

import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonValue.PrettyPrintSettings;
import arc.util.serialization.JsonWriter;
import arc.util.serialization.JsonWriter.OutputType;
import net.darkdustry.bot.Vars;

public class ConfigUtils {

    public static void init() {
        Vars.json.setOutputType(JsonWriter.OutputType.json);
        Vars.json.setUsePrototypes(false);

        var file = Fi.get(".").child("config.json");

        if (file.exists()) {
            Vars.config = Vars.json.fromJson(Config.class, file);
            Log.info("Config loaded. (@)", file.absolutePath());
        } else {
            PrettyPrintSettings settings = new PrettyPrintSettings();
            settings.outputType = OutputType.minimal;
            settings.singleLineColumns = 0;
            StringBuilder buffer = new StringBuilder(512);
//            file.writeString(Vars.json.prettyPrint(Vars.config = new Config()));
            file.writeString(new JsonReader().parse(Vars.json.toJson(Vars.config = new Config())).pretty());
            Log.info("Config file generated. (@)", file.absolutePath());
            System.exit(0);
        }
    }

    public static class Config {
        public String token = "token";
        public long guildId = 0L;
        public long emojiGuildId = 0L;
        public long mapsChannelId = 0L;
        public long schematicsChannelId = 0L;

    }
}
