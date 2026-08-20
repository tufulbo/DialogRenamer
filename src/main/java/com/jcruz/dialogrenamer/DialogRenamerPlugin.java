package com.jcruz.dialogrenamer;

import com.jcruz.dialogrenamer.command.DialogRenamerCommand;
import com.jcruz.dialogrenamer.config.MessageManager;
import com.jcruz.dialogrenamer.dialog.LoreDialogFactory;
import com.jcruz.dialogrenamer.listener.DialogClickListener;
import com.jcruz.dialogrenamer.service.LoreService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class DialogRenamerPlugin extends JavaPlugin {

    private MessageManager messageManager;
    private LoreService loreService;
    private LoreDialogFactory dialogFactory;

    @Override
    public void onEnable() {
        this.messageManager = new MessageManager(this);
        this.loreService = new LoreService();
        this.dialogFactory = new LoreDialogFactory(messageManager, loreService);

        getServer().getPluginManager().registerEvents(new DialogClickListener(messageManager, loreService), this);

        DialogRenamerCommand commandHandler = new DialogRenamerCommand(messageManager, loreService, dialogFactory);
        PluginCommand command = getCommand("de");
        if (command != null) {
            command.setExecutor(commandHandler);
            command.setTabCompleter(commandHandler);
        }
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public LoreService getLoreService() {
        return loreService;
    }

    public LoreDialogFactory getDialogFactory() {
        return dialogFactory;
    }
}
