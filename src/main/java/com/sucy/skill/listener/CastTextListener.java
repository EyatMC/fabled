package com.sucy.skill.listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.event.PlayerSkillUnlockEvent;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.cast.CastMode;
import com.sucy.skill.cast.PlayerTextCastingData;
import com.sucy.skill.gui.handlers.SkillHandler;
import com.sucy.skill.gui.tool.GUITool;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CastTextListener extends SkillAPIListener {
    private final CastMode                   castMode;
    private final HashMap<UUID, ItemStack[]> backup  = new HashMap<>();
    private       boolean                    enabled = true;

    public CastTextListener(CastMode castMode) {
        switch (castMode) {
            case ACTION_BAR, TITLE, SUBTITLE, CHAT -> {
            }
            default -> throw new IllegalArgumentException(castMode.name());
        }
        this.castMode = castMode;
    }

    @Override
    public void init() {
        MainListener.registerJoin(this::init);
        Bukkit.getOnlinePlayers().forEach(this::init);
    }

    @Override
    public void cleanup() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> SkillAPI.getSettings().isWorldEnabled(player.getWorld()))
                .forEach(this::init);
        this.enabled = false;
    }

    private void init(Player player) {
        if (player.getOpenInventory().getTopInventory() != null && player.getOpenInventory()
                .getTopInventory()
                .getHolder() instanceof SkillHandler) player.closeInventory();
        SkillAPI.getPlayerData(player).getTextCastingData().validate();
    }

    public void restore(Player player) {
        ItemStack[] backup = this.backup.remove(player.getUniqueId());
        if (backup == null) return;
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < backup.length; i++) {
            inventory.setItem(i, backup[i]);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = ((Player) event.getPlayer());
        if (!isWorldEnabled(player)) return;

        if (event.getInventory().getHolder() instanceof SkillHandler) {
            PlayerInventory inventory = player.getInventory();
            ItemStack[]     items     = new ItemStack[9];
            for (int i = 0; i < items.length; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    items[i] = item;
                }
            }
            backup.put(player.getUniqueId(), items);

            refresh(player);
        }
    }

    private void refresh(Player player) {
        PlayerData            playerData = SkillAPI.getPlayerData(player);
        PlayerTextCastingData layout     = playerData.getTextCastingData();
        PlayerInventory       inventory  = player.getInventory();
        ItemStack             unassigned = GUITool.markCastItem(SkillAPI.getSettings().getUnassigned());
        for (int i = 0; i < 9; i++) {
            if (i == SkillAPI.getSettings().getCastSlot()) inventory.setItem(i, null);
            else {
                PlayerSkill skill = playerData.getSkill(layout.getSkill(i));
                inventory.setItem(i,
                        skill == null ? unassigned : GUITool.markCastItem(skill.getData().getIndicator(skill, true)));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = ((Player) event.getWhoClicked());
        if (!isWorldEnabled(player)) return;

        InventoryView view             = event.getView();
        Inventory     topInventory     = view.getTopInventory();
        Inventory     clickedInventory = event.getClickedInventory();

        if (topInventory.getHolder() instanceof SkillHandler) {
            if (clickedInventory == topInventory && event.getClick() == ClickType.NUMBER_KEY) {
                Skill skill = ((SkillHandler) topInventory.getHolder()).get(event.getSlot());
                if (skill != null) {
                    SkillAPI.getPlayerData(player)
                            .getTextCastingData()
                            .assign(skill.getName(), event.getHotbarButton());
                    refresh(player);
                }
            } else if (clickedInventory == view.getBottomInventory()
                    && event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                SkillAPI.getPlayerData(player).getTextCastingData().assign(null, event.getSlot());
                refresh(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = ((Player) event.getPlayer());
        if (!isWorldEnabled(player)) return;

        if (event.getInventory().getHolder() instanceof SkillHandler) restore(player);
    }

    private boolean isWorldEnabled(Player player) {
        return SkillAPI.getSettings().isWorldEnabled(player.getWorld());
    }

    /**
     * Clears skill bars upon quitting the game
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()))
            init(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeWorldPre(PlayerChangedWorldEvent event) {
        if (!SkillAPI.getSettings().isWorldEnabled(event.getPlayer().getWorld()) && SkillAPI.getSettings()
                .isWorldEnabled(event.getFrom()))
            init(event.getPlayer());
    }

    @EventHandler
    public void onClassChange(PlayerClassChangeEvent event) {
        event.getPlayerData().getTextCastingData().validate();
    }

    /**
     * Adds unlocked skills to the skill bar if applicable
     *
     * @param event event details
     */
    @EventHandler
    public void onUnlock(PlayerSkillUnlockEvent event) {
        event.getPlayerData().getTextCastingData().onUnlock(event.getUnlockedSkill().getData().getName());
    }

    public class CastTextTask extends BukkitRunnable {
        private final PlayerData playerData;

        public CastTextTask(PlayerData playerData) {
            this.playerData = playerData;
        }

        @Override
        public void run() {
            Player player = playerData.getPlayer();
            if (!enabled || player == null || !player.isOnline() || !isWorldEnabled(player)
                    || !playerData.getTextCastingData().isCasting()) {
                this.cancel();
                return;
            }
            String message = playerData.getTextCastingData().getMessage();
            switch (castMode) {
                case ACTION_BAR -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                case TITLE -> player.sendTitle(message, "", 0, 20, 0);
                case SUBTITLE -> player.sendTitle(" ", message, 0, 20, 0);
            }
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            if (castMode == CastMode.ACTION_BAR) {
                Player player = playerData.getPlayer();
                if (player != null) player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            }
        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
        Player                player     = event.getPlayer();
        PlayerData            playerData = SkillAPI.getPlayerData(player);
        PlayerTextCastingData castData   = playerData.getTextCastingData();
        if (castData.isCasting()) {
            castData.setCasting(false);
        } else if (!castData.isEmpty()) {
            castData.setCasting(true);
            switch (castMode) {
                case ACTION_BAR, TITLE, SUBTITLE -> new CastTextTask(playerData).runTaskTimer(SkillAPI.inst(), 0, 1);
                case CHAT -> player.sendMessage(castData.getMessage());
            }
        }
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        PlayerTextCastingData castData = SkillAPI.getPlayerData(event.getPlayer()).getTextCastingData();
        if (!castData.isCasting()) return;
        event.setCancelled(true);
        castData.cast(event.getNewSlot());
    }
}
