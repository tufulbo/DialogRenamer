package com.jcruz.dialogrenamer.config;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        loadMessages();
    }

    public void loadMessages() {
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        loadMessages();
    }

    public Component getComponent(String path, String fallback) {
        if (messagesConfig.isList(path)) {
            List<String> lines = messagesConfig.getStringList(path);
            if (lines.isEmpty()) {
                return miniMessage.deserialize(fallback);
            }
            Component result = Component.empty();
            for (int i = 0; i < lines.size(); i++) {
                Component lineComponent = miniMessage.deserialize(lines.get(i));
                if (i == 0) {
                    result = lineComponent;
                } else {
                    result = result.append(Component.newline()).append(lineComponent);
                }
            }
            return result;
        }

        String raw = messagesConfig.getString(path, fallback);
        return miniMessage.deserialize(raw);
    }

    public List<Component> getComponentList(String path, List<String> fallback) {
        List<String> rawLines = messagesConfig.isList(path) ? messagesConfig.getStringList(path) : fallback;
        List<Component> components = new ArrayList<>();
        for (String line : rawLines) {
            components.add(miniMessage.deserialize(line));
        }
        return components;
    }

    public String getRaw(String path, String fallback) {
        return messagesConfig.getString(path, fallback);
    }

    public void sendMessage(Audience audience, String path, String fallback) {
        if (messagesConfig.isList(path)) {
            List<String> lines = messagesConfig.getStringList(path);
            for (String line : lines) {
                audience.sendMessage(miniMessage.deserialize(line));
            }
            return;
        }

        String raw = messagesConfig.getString(path, fallback);
        if (raw != null && !raw.isEmpty()) {
            audience.sendMessage(miniMessage.deserialize(raw));
        }
    }
}
