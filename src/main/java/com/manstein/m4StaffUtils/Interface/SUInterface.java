package com.manstein.m4StaffUtils.Interface;

import com.manstein.m4StaffUtils.Config.ConfigManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SUInterface implements Listener {
    private final Inventory inv;
    private final ConfigManager configManager;
    private final Map<UUID, String> pendingPlayerActions;
    private final LuckPerms luckPerms;
    private final Player commandExecutor;
    private final String inventoryNome;

    public SUInterface(ConfigManager configManager, LuckPerms luckPerms, Player commandExecutor) {
        this.configManager = configManager;
        this.inventoryNome = configManager.getConfig().getString("inventory_nome");
        this.inv = Bukkit.createInventory(null, 27, inventoryNome);
        this.pendingPlayerActions = new HashMap<>();
        this.luckPerms = luckPerms;
        this.commandExecutor = commandExecutor;
        GUIItems(commandExecutor);
    }

    public void openGUI(Player player) {
        player.openInventory(inv);
    }

    private void GUIItems(Player player) {
        String[] itemNames = {
                "Se tornar invisivel a outros jogadores", "Teleportar a um jogador", "Ver o inventário de um jogador", "Seu cargo:", "Sair do menu"
        };

        String[] configKeys = {
                "textura_url", "textura_url2", "textura_url3", "textura_url4", "textura_url5"
        };

        int[] itemSlots = {9, 10, 11, 13, 17};

        for (int i = 0; i < itemNames.length; i++) {
            String url = configManager.getConfig().getString(configKeys[i]);
            if (itemNames[i].equals("Seu cargo:")) {
                ItemStack cargoItem = GUIItem(url, ChatColor.YELLOW + itemNames[i]);
                SkullMeta meta = (SkullMeta) cargoItem.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.GREEN + getLuckPermsGroup(player)));
                cargoItem.setItemMeta(meta);
                inv.setItem(itemSlots[i], cargoItem);
            } else {
                inv.setItem(itemSlots[i], GUIItem(url, ChatColor.YELLOW + itemNames[i]));
            }
        }
    }



    private ItemStack GUIItem(String url, String name) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        if (url.length() < 16) {
            skullMeta.setOwner(url);
            skullMeta.setDisplayName(name);
            skull.setItemMeta(skullMeta);
            return skull;
        }

        StringBuilder s_url = new StringBuilder();
        s_url.append("http://textures.minecraft.net/texture/").append(url);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", getTexture(url)));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skullMeta.setDisplayName(name);
        skull.setItemMeta(skullMeta);

        return skull;
    }

    private String getTexture(String url) {
        return Base64.getEncoder().encodeToString(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url).getBytes());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (itemName.equals("Se tornar invisivel a outros jogadores") && event.getRawSlot() == 9) {
                player.performCommand("vanish");
                event.setCancelled(true);
                player.closeInventory();
            } else if (itemName.equals("Teleportar a um jogador") && event.getRawSlot() == 10) {
                player.sendMessage(ChatColor.YELLOW + "Digite o nome do jogador para onde deseja ser teleportado:");
                pendingPlayerActions.put(player.getUniqueId(), "teleport");
                event.setCancelled(true);
                player.closeInventory();
            } else if (itemName.equals("Ver o inventário de um jogador") && event.getRawSlot() == 11) {
                player.sendMessage(ChatColor.YELLOW + "Digite o nome do jogador cujo inventário deseja ver:");
                pendingPlayerActions.put(player.getUniqueId(), "invsee");
                event.setCancelled(true);
                player.closeInventory();
            }
            else if (itemName.equals("Sair do menu") && event.getRawSlot() == 17) {
                event.setCancelled(true);
                player.closeInventory();
            }
            else if (event.getRawSlot() < inv.getSize()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (pendingPlayerActions.containsKey(playerId)) {
            String playerName = event.getMessage();
            String action = pendingPlayerActions.remove(playerId);
            if (action.equals("teleport")) {
                handleTeleportRequest(player, playerName);
            } else if (action.equals("invsee")) {
                handleInvSeeRequest(player, playerName);
            }
            event.setCancelled(true);
        }
    }

    private void handleTeleportRequest(Player player, String playerName) {
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer != null) {
            player.teleport(targetPlayer.getLocation());
            player.sendMessage(ChatColor.GREEN + "Você foi teleportado para " + targetPlayer.getName());
        } else {
            player.sendMessage(ChatColor.RED + "Jogador não encontrado.");
        }
    }

    private void handleInvSeeRequest(Player player, String playerName) {
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer != null) {
            player.openInventory(targetPlayer.getInventory());
            player.sendMessage(ChatColor.GREEN + "Abrindo inventário de " + targetPlayer.getName());
        } else {
            player.sendMessage(ChatColor.RED + "Jogador não encontrado.");
        }
    }

    private String getLuckPermsGroup(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            List<String> groups = user.getNodes().stream()
                    .filter(node -> node.getKey().startsWith("group."))
                    .map(Node::getKey)
                    .collect(Collectors.toList());

            if (!groups.isEmpty()) {
                return groups.get(0).replace("group.", "");
            }
        }
        return "Sem Cargo";
    }


}
