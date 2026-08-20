package com.jcruz.dialogrenamer.dialog;

import com.jcruz.dialogrenamer.config.MessageManager;
import com.jcruz.dialogrenamer.service.LoreService;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoreDialogFactory {

    public static final Key CONFIRM_KEY = Key.key("dialogrenamer:confirm");
    public static final String NAME_INPUT_KEY = "name_text";
    public static final String LORE_INPUT_KEY = "lore_text";

    private final MessageManager messageManager;
    private final LoreService loreService;

    public LoreDialogFactory(MessageManager messageManager, LoreService loreService) {
        this.messageManager = messageManager;
        this.loreService = loreService;
    }

    public Dialog createLoreDialog(Player player, ItemStack item) {
        boolean canEditName = player.hasPermission("dialogrenamer.edit.name");
        boolean canEditLore = player.hasPermission("dialogrenamer.edit.lore");

        Component title = messageManager.getComponent("dialog.title", "<gold>Edit Item</gold>");
        Component instructions = messageManager.getComponent(
                "dialog.instructions",
                "<gray>Edit item name and lore.\nSupports & legacy and &#rrggbb hex color codes.</gray>"
        );
        Component nameLabel = messageManager.getComponent("dialog.name-label", "Name");
        Component loreLabel = messageManager.getComponent("dialog.input-label", "Lore (one line per row)");
        Component btnConfirm = messageManager.getComponent("dialog.btn-confirm", "<green>OK</green>");
        Component btnConfirmTooltip = messageManager.getComponent("dialog.btn-confirm-tooltip", "Confirm edits");
        Component btnCancel = messageManager.getComponent("dialog.btn-cancel", "<red>Cancel</red>");
        Component btnCancelTooltip = messageManager.getComponent("dialog.btn-cancel-tooltip", "Close without saving");

        List<DialogInput> inputs = new ArrayList<>();

        if (canEditName) {
            String initialName = loreService.getNameAsPlainText(item);
            inputs.add(DialogInput.text(NAME_INPUT_KEY, nameLabel)
                    .initial(initialName)
                    .maxLength(256)
                    .width(320)
                    .build());
        }

        if (canEditLore) {
            String initialLore = loreService.getLoreAsPlainText(item);
            inputs.add(DialogInput.text(LORE_INPUT_KEY, loreLabel)
                    .initial(initialLore)
                    .maxLength(2048)
                    .width(320)
                    .multiline(TextDialogInput.MultilineOptions.create(32, 250))
                    .build());
        }

        return Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(title)
                        .canCloseWithEscape(true)
                        .body(List.of(
                                DialogBody.plainMessage(instructions),
                                DialogBody.item(item).build()
                        ))
                        .inputs(inputs)
                        .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.builder(btnConfirm)
                                .tooltip(btnConfirmTooltip)
                                .action(DialogAction.customClick(CONFIRM_KEY, null))
                                .build(),
                        ActionButton.builder(btnCancel)
                                .tooltip(btnCancelTooltip)
                                .action(null)
                                .build()
                ))
        );
    }
}
