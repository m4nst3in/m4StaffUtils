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

        // Registrar o listener de interface
        Bukkit.getPluginManager().registerEvents(new InterfaceListener(), this);

        getLogger().info("m4StaffUtils foi ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("m4StaffUtils foi desativado com sucesso!");
    }

    // Método estático para obter a instância da classe
    public static StaffUtils getInstance() {
        return instance;
    }

    // Método para definir o executor de comando
    public void setCommandExecutor(Player player) {
        this.commandExecutor = player;

        // Após definir o executor de comando, criar a interface
        createInterface();
    }

    // Método para obter o executor de comando
    public Player getCommandExecutor() {
        return commandExecutor;
    }

    // Método para acessar o ConfigManager
    public ConfigManager getConfigManager() {
        return configManager;
    }

    // Método para acessar o LuckPerms
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    // Método para criar a interface após definir o executor de comando
    private void createInterface() {
        if (commandExecutor != null) {
            // Criar a interface e passar o executor de comando
            Bukkit.getPluginManager().registerEvents(new SUInterface(configManager, luckPerms, commandExecutor), this);

            // Registrar o comando /sutil com o executor de comando
            getCommand("sutil").setExecutor(new SutilCommand(configManager, luckPerms, commandExecutor));
        }
    }
}
