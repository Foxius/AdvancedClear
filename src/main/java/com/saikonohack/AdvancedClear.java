package com.saikonohack;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdvancedClear extends JavaPlugin {
    private BukkitTask task;
    private boolean cancelCleanup = false;
    private final String logFileName = "plugins/AdvancedClear/cleanup.log";
    private long nextCleanupTime;
    private String lastSkipper = "Никто";
    int pluginId = 22914;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Metrics metrics = new Metrics(this, pluginId);
//        Bukkit.getServer().getConsoleSender().sendMessage("-------> Login <-------");
//        License license = new License(getConfig().getString("license"), "http://154.194.53.46/", this);
//        license.request();
//        Bukkit.getServer().getConsoleSender().sendMessage(" |- Проверка лицензии: "+license.getLicense());
//        if (license.isValid()) {
//            Bukkit.getServer().getConsoleSender().sendMessage("------------------------");
//            Bukkit.getServer().getConsoleSender().sendMessage(" |- Доступ разрешен");
//            Bukkit.getServer().getConsoleSender().sendMessage(" |- Добро пожаловать: "+license.getLicensedTo());
//            Bukkit.getServer().getConsoleSender().sendMessage(" |- Я включаю все возможности для вас");
//            Bukkit.getServer().getConsoleSender().sendMessage("------------------------");
//            Bukkit.getServer().getConsoleSender().sendMessage(" |- Лицензия сгенерирована: "+license.getGeneratedIn());
//            Bukkit.getServer().getConsoleSender().sendMessage(" |- Лицензия сгенерирована пользователем: "+license.getGeneratedBy());
//            Bukkit.getServer().getConsoleSender().sendMessage("------------------------");
//        } else {
//            Bukkit.getServer().getConsoleSender().sendMessage("------------------------");
//            Bukkit.getServer().getConsoleSender().sendMessage(" |- Доступ запрещён");
//            Bukkit.getServer().getConsoleSender().sendMessage(" |- Получена ошибка "+license.getReturn());
//            Bukkit.getServer().getConsoleSender().sendMessage("------------------------");
//
//            Bukkit.getPluginManager().disablePlugin(this);
//            return;
//        }
        FileConfiguration config = getConfig();
        String version = getDescription().getVersion();
        int interval = config.getInt("interval");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AdvancedClearPlaceholders(this).register();
        } else {
            getLogger().warning("PlaceholderAPI не найден! Плейсхолдеры не будут работать.");
        }

        String author = getDescription().getAuthors().get(0);
        String site = getDescription().getWebsite();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.GREEN + "Плагин загружен!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.AQUA + "Версия: " + version);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.AQUA + "Автор: " + author + " ("+ChatColor.UNDERLINE + site + ")");

        task = new BukkitRunnable() {
            @Override
            public void run() {
                cancelCleanup = false;
                nextCleanupTime = System.currentTimeMillis() + interval * 1000L;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    String lang = player.getLocale().startsWith("en") ? "en" : "ru";
                    String message = config.getString("messages." + lang + ".message");
                    String cancelButton = ChatColor.GREEN + "[Нажмите, чтобы отменить]";

                    String jsonMessage = String.format(
                        "[\"\",{\"text\":\"%s[AdvancedClear] %s%s \",\"color\":\"red\"},{\"text\":\"%s\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/cancelcleanup\"}}]",
                        ChatColor.GOLD, ChatColor.RED, message, cancelButton);

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + jsonMessage);
                }

                getServer().getScheduler().runTaskLater(AdvancedClear.this, () -> {
                    if (!cancelCleanup) {
                        Map<Material, Integer> removedItems = new HashMap<>();
                        int totalItemCount = 0;

                        for (Item item : getServer().getWorlds().get(0).getEntitiesByClass(Item.class)) {
                            ItemStack stack = item.getItemStack();
                            Material itemType = stack.getType();
                            int itemCount = stack.getAmount();

                            totalItemCount += itemCount;
                            removedItems.put(itemType, removedItems.getOrDefault(itemType, 0) + itemCount);
                            item.remove();
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String lang = player.getLocale().startsWith("en") ? "en" : "ru";
                            String itemListHeader = config.getString("messages." + lang + ".item_list_header");

                            StringBuilder hoverText = new StringBuilder();
                            hoverText.append(ChatColor.GOLD).append(itemListHeader).append("\\n");

                            for (Map.Entry<Material, Integer> entry : removedItems.entrySet()) {
                                String itemName = getItemLocalizedName(player, entry.getKey());
                                hoverText.append(ChatColor.YELLOW).append(itemName).append(": ").append(entry.getValue()).append("\\n");
                            }

                            String removedMessage = config.getString("messages." + lang + ".removed_message");

                            String jsonMessageWithHover = String.format(
                                "[\"\",{\"text\":\"%s[AdvancedClear] %s%s%d\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"%s\"}}]",
                                ChatColor.GOLD, ChatColor.RED, removedMessage, totalItemCount, hoverText.toString());

                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + jsonMessageWithHover);
                        }

                        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.RED + "Очистка произведена. Удалено предметов: " + totalItemCount);
                        logToFile("Очистка произведена. Удалено предметов: " + totalItemCount);
                    } else {
                        String cancelMessage = config.getString("messages.ru.cancel_message");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String lang = player.getLocale().startsWith("en") ? "en" : "ru";
                            cancelMessage = config.getString("messages." + lang + ".cancel_message");
                            player.sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.RED + cancelMessage);
                        }

                        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.RED + "Очистка пропущена.");
                        logToFile("Очистка пропущена.");
                    }
                }, interval * 20);
            }
        }.runTaskTimer(this, 0, interval * 20);

        getCommand("cancelcleanup").setExecutor((sender, command, label, args) -> {
            if (sender.hasPermission("advancedclear.cancel")) {
                cancelCleanup();
                String cancelMessage = config.getString("messages.ru.cancel_message");
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    lastSkipper = player.getName();
                    String lang = player.getLocale().startsWith("en") ? "en" : "ru";
                    cancelMessage = config.getString("messages." + lang + ".cancel_message");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.RED + "Очистка пропущена игроком: " + player.getName());
                    logToFile("Очистка пропущена игроком: " + player.getName());
                }
                sender.sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.GREEN + cancelMessage);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
                return false;
            }
        });
    }

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[AdvancedClear] " + ChatColor.RED + "Плагин отключен.");
    }

    public void cancelCleanup() {
        this.cancelCleanup = true;
    }

    public long getTimeUntilNextCleanup() {
        return (nextCleanupTime - System.currentTimeMillis()) / 1000;
    }

    public String getLastSkipper() {
        return lastSkipper;
    }

    private void logToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write(new Date() + ": " + message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getItemLocalizedName(Player player, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && meta.hasLocalizedName()) {
            return meta.getLocalizedName();
        }
        return itemStack.getType().toString().toLowerCase().replace('_', ' ');
    }
}

class AdvancedClearPlaceholders extends PlaceholderExpansion {

    private final AdvancedClear plugin;

    public AdvancedClearPlaceholders(AdvancedClear plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "advancedclear";
    }

    @Override
    public @NotNull String getAuthor() {
        return "saikonohack";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.equals("time_until_next_cleanup")) {
            return String.valueOf(plugin.getTimeUntilNextCleanup());
        }

        if (identifier.equals("last_skipper")) {
            return plugin.getLastSkipper();
        }

        return null;
    }
}
