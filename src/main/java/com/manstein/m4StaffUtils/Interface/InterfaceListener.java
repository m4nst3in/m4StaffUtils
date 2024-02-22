package com.manstein.m4StaffUtils.Interface;

import com.manstein.m4StaffUtils.StaffUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class InterfaceListener implements Listener {

    @EventHandler
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/sutil")) {
            Player player = event.getPlayer();
            StaffUtils.getInstance().setCommandExecutor(player);
        }
    }
}
