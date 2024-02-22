package com.manstein.m4StaffUtils.Commands;

import com.manstein.m4StaffUtils.Config.ConfigManager;
import com.manstein.m4StaffUtils.Interface.SUInterface;
import net.luckperms.api.LuckPerms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SutilCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final LuckPerms luckPerms;
    private final Player commandExecutor;

    public SutilCommand(ConfigManager configManager, LuckPerms luckPerms, Player commandExecutor) {
        this.configManager = configManager;
        this.luckPerms = luckPerms;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por um jogador!");
            return true;
        }
        Player player = (Player) sender;
        SUInterface suInterface = new SUInterface(configManager, luckPerms, commandExecutor);
        suInterface.openGUI(player);
        return true;
    }
}
