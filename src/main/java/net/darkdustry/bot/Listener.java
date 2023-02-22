package net.darkdustry.bot;

import arc.func.Cons;
import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.util.UnsafeRunnable;
import arc.util.Strings;
import net.darkdustry.bot.components.ContentHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.ZipException;

import static arc.graphics.Color.scarlet;
import static mindustry.graphics.Pal.accent;
import static net.darkdustry.bot.Vars.*;
import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;
import static net.dv8tion.jda.api.utils.FileUpload.fromData;

public class Listener extends ListenerAdapter {
    private static final ArrayList<SlashCommandData> rawCommands = new ArrayList<>();
    private static final ObjectMap<SlashCommandData, Cons<SlashCommandInteractionEvent>> commands = new ObjectMap<>();

    private static void register(SlashCommandData command, Cons<SlashCommandInteractionEvent> func) {
        commands.put(command, func);
    }

    private static void loadCommands(@NotNull Guild guild) {
        commands.forEach(command -> rawCommands.add(command.key));
        guild.updateCommands().addCommands(rawCommands).queue();
    }

    public static void registerCommands() {
        register(
                slash("map", "Отправить информацию о карте в эту публикацию")
                        .addOption(OptionType.ATTACHMENT, "map", "Карта Mindustry", true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_THREADS)),
                event -> {
                    MessageChannelUnion channelUnion = event.getChannel();
                    if (!channelUnion.getType().equals(ChannelType.GUILD_PUBLIC_THREAD) || !channelUnion.asThreadChannel().getParentChannel().getId().equals(mapsChannel.getId())) {
                        reply(event, ":warning: Ошибка", "Эта команда доступна только в публикациях форума "+mapsChannel.getAsMention(), scarlet);
                        return;
                    }

                    ThreadChannel channel = channelUnion.asThreadChannel();

                    if (!channel.getOwnerId().equals(event.getUser().getId())) {
                        reply(event, ":warning: Ошибка", "Вы должны быть создателем текущей этой публикации, чтобы использовать эту команду!", scarlet);
                        return;
                    }
                    var attachment = Objects.requireNonNull(event.getOption("map")).getAsAttachment();

                    if (!Objects.equals(attachment.getFileExtension(), "msav")) {
                        reply(event, ":warning: Ошибка", ":link: Необходимо прикрепить файл с расширением **.msav**", scarlet);
                        return;
                    }

                    attachment
                            .getProxy()
                            .downloadToFile(cache.child(attachment.getFileName()).file())
                            .thenAccept(file ->
                                    tryWorkWithFile(
                                            file,
                                            () ->
                                            {
                                                var map = ContentHandler.parseMap(file);
                                                var image = ContentHandler.parseMapImage(map);

                                                var embed = new EmbedBuilder()
                                                        .setTitle(stripColors(map.name()))
                                                        .setDescription(stripColors(map.description()))
                                                        .setFooter(map.width + "x" + map.height)
                                                        .setColor(accent.argb8888())
                                                        .setImage("attachment://image.png");

                                                channel
                                                        .sendMessageEmbeds(embed.build())
                                                        .addFiles(
                                                                fromData(image, "image.png"),
                                                                fromData(attachment.getProxy().download().get(), attachment.getFileName())
                                                        )
                                                        .queue();

                                                reply(event, ":map: Успешно", "Информация о карте отправлена", accent);
                                            },
                                            t -> {
                                                if (t instanceof ZipException) {
                                                    reply(event, ":warning: Ошибка", "Файл повреждён", scarlet);
                                                }
                                            }
                                    )
                            );
                }
        );

        loadCommands(Vars.guild);
    }

    private static void reply(SlashCommandInteractionEvent event, String title, String description, Color color) {
        event
                .replyEmbeds(new EmbedBuilder()
                        .setTitle(title)
                        .setDescription(description)
                        .setColor(color.argb8888())
                        .build())
                .setEphemeral(true)
                .queue();
    }

    private static void tryWorkWithFile(File file, UnsafeRunnable runnable, Cons<Throwable> error) {
        try {
            runnable.run();
        } catch (Throwable t) {
            error.get(t);
        } finally {
            file.deleteOnExit();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commands.forEach(command ->
        {
            if (command.key.getName().equals(event.getName())) command.value.get(event);
        });
    }

    public static String stripColors(@NotNull String str) {
        return Strings.stripColors(str);
    }

}
