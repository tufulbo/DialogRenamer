package com.jcruz.dialogrenamer.command;

import com.jcruz.dialogrenamer.config.MessageManager;
import com.jcruz.dialogrenamer.dialog.LoreDialogFactory;
import com.jcruz.dialogrenamer.service.LoreService;
import io.papermc.paper.dialog.Dialog;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogRenamerCommand implements CommandExecutor, TabCompleter {

    private final MessageManager messageManager;
    private final LoreService loreService;
    private final LoreDialogFactory dialogFactory;

    public DialogRenamerCommand(MessageManager messageManager, LoreService loreService, LoreDialogFactory dialogFactory) {
        this.messageManager = messageManager;
        this.loreService = loreService;
        this.dialogFactory = dialogFactory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dialogrenamer.admin")) {
                messageManager.sendMessage(sender, "admin.no-permission", "<red>You do not have permission to execute this command.</red>");
                return true;
            }
            messageManager.reload();
            messageManager.sendMessage(sender, "admin.reload", "<green>DialogRenamer configuration and messages reloaded successfully!</green>");
            return true;
        }

        if (!(sender instanceof Player player)) {
            messageManager.sendMessage(sender, "command.player-only", "<red>Only players can execute this command.</red>");
            return true;
        }

        boolean canEditName = player.hasPermission("dialogrenamer.edit.name");
        boolean canEditLore = player.hasPermission("dialogrenamer.edit.lore");

        if (!canEditName && !canEditLore) {
            messageManager.sendMessage(player, "admin.no-permission", "<red>You do not have permission to execute this command.</red>");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!loreService.isValidItem(item)) {
            messageManager.sendMessage(player, "command.invalid-item", "<red>You must hold a valid item in your main hand.</red>");
            return true;
        }

        Dialog dialog = dialogFactory.createLoreDialog(player, item);
        player.showDialog(dialog);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("dialogrenamer.admin")) {
                if ("reload".startsWith(args[0].toLowerCase())) {
                    completions.add("reload");
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
