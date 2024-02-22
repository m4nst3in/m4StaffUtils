package com.manstein.m4StaffUtils;

import com.manstein.m4StaffUtils.Config.ConfigManager;
import com.manstein.m4StaffUtils.Interface.SUInterface;
import com.manstein.m4StaffUtils.Commands.SutilCommand;
import com.manstein.m4StaffUtils.Interface.InterfaceListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffUtils extends JavaPlugin {
    private static StaffUtils instance;
    private ConfigManager configManager;
    private LuckPerms luckPerms;
    private Player commandExecutor;
    private InterfaceListener interfaceListener;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        luckPerms = LuckPermsProvider.get();

        Bukkit.getPluginManager().registerEvents(new InterfaceListener(), this);

        getLogger().info("m4StaffUtils foi ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("m4StaffUtils foi desativado com sucesso!");
    }

    public static StaffUtils getInstance() {
        return instance;
    }

    public void setCommandExecutor(Player player) {
        this.commandExecutor = player;

        createInterface();
    }

    public Player getCommandExecutor() {
        return commandExecutor;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    private void createInterface() {
        if (commandExecutor != null) {
            Bukkit.getPluginManager().registerEvents(new SUInterface(configManager, luckPerms, commandExecutor), this);
            getCommand("sutil").setExecutor(new SutilCommand(configManager, luckPerms, commandExecutor));
        }
    }
}
