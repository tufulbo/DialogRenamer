package com.jcruz.dialogrenamer.listener;

import com.jcruz.dialogrenamer.config.MessageManager;
import com.jcruz.dialogrenamer.dialog.LoreDialogFactory;
import com.jcruz.dialogrenamer.service.LoreService;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DialogClickListener implements Listener {

    private final MessageManager messageManager;
    private final LoreService loreService;

    public DialogClickListener(MessageManager messageManager, LoreService loreService) {
        this.messageManager = messageManager;
        this.loreService = loreService;
    }

    @EventHandler
    public void onCustomClick(PlayerCustomClickEvent event) {
        if (!event.getIdentifier().equals(LoreDialogFactory.CONFIRM_KEY)) {
            return;
        }

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        if (!(event.getCommonConnection() instanceof PlayerGameConnection connection)) {
            return;
        }

        Player player = connection.getPlayer();
        if (player == null) {
            return;
        }

        boolean canEditName = player.hasPermission("dialogrenamer.edit.name");
        boolean canEditLore = player.hasPermission("dialogrenamer.edit.lore");

        if (!canEditName && !canEditLore) {
            messageManager.sendMessage(player, "admin.no-permission", "<red>You do not have permission to execute this command.</red>");
            return;
        }

        String nameText = view.getText(LoreDialogFactory.NAME_INPUT_KEY);
        String loreText = view.getText(LoreDialogFactory.LORE_INPUT_KEY);

        boolean updateName = canEditName && nameText != null;
        boolean updateLore = canEditLore && loreText != null;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!loreService.isValidItem(item)) {
            messageManager.sendMessage(player, "command.invalid-item", "<red>You must hold a valid item in your main hand.</red>");
            return;
        }

        loreService.updateItem(item, nameText, updateName, loreText, updateLore);
        messageManager.sendMessage(player, "success.lore-updated", "<green>Item updated successfully!</green>");
    }
}
