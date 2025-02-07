/**
 * Fabled
 * studio.magemonkey.fabled.task.GUITask
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.task;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.hook.PlaceholderAPIHook;
import studio.magemonkey.fabled.hook.PluginChecker;
import studio.magemonkey.fabled.language.RPGFilter;
import studio.magemonkey.fabled.log.LogType;
import studio.magemonkey.fabled.log.Logger;
import studio.magemonkey.fabled.manager.ComboManager;
import studio.magemonkey.fabled.thread.RepeatThreadTask;
import studio.magemonkey.codex.mccore.config.FilterType;
import studio.magemonkey.codex.mccore.util.TextFormatter;
import studio.magemonkey.codex.mccore.util.VersionManager;
import studio.magemonkey.codex.util.MsgUT;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * Task that handles updating GUI elements such as level bar,
 * food bar, and action bar according to the config.yml content.
 */
public class GUITask extends RepeatThreadTask {
    private final boolean levelMana;
    private final boolean levelLevel;

    private final boolean foodMana;
    private final boolean foodExp;

    private final boolean forceScaling;
    private final boolean oldHealth;

    private final boolean useAction;
    private final String  actionText;

    /**
     * Sets up the task, running if any of the GUI options are enabled
     *
     * @param api API reference
     */
    public GUITask(Fabled api) {
        super(5, 5);

        String levelBar = Fabled.getSettings().getLevelBar().toLowerCase();
        levelMana = levelBar.equals("mana");
        levelLevel = levelBar.equals("level");

        String foodBar = Fabled.getSettings().getFoodBar().toLowerCase();
        foodMana = foodBar.equals("mana");
        foodExp = foodBar.equals("exp");

        forceScaling = Fabled.getSettings().isForceScaling();
        oldHealth = Fabled.getSettings().isOldHealth();

        useAction = Fabled.getSettings().isUseActionBar();
        actionText = TextFormatter.colorString(Fabled.getSettings().getActionText());

        Logger.log(LogType.GUI,
                1,
                "GUI Settings: " + levelMana + "/" + levelLevel + "/" + foodMana + "/" + foodExp + "/" + useAction + "/"
                        + actionText);

        if (useAction || levelMana || levelLevel || foodMana || foodExp || forceScaling)
            return;

        expired = true;
    }

    /**
     * Runs the tasks, updating GUI elements for players
     */
    @Override
    public void run() {
        Logger.log(LogType.GUI, 1, "Updating GUI (" + VersionManager.getOnlinePlayers().length + " players)...");
        for (Player player : VersionManager.getOnlinePlayers()) {
            if (!Fabled.getSettings().isWorldEnabled(player.getWorld())) continue;
            if (!Fabled.hasPlayerData(player)) continue;

            PlayerData data = Fabled.getPlayerData(player);

            // Health scale
            if (forceScaling) {
                if (oldHealth)
                    player.setHealthScale(20);
                else
                    player.setHealthScale(player.getMaxHealth());
            }

            // Level bar options
            if (levelMana) {
                Logger.log(LogType.GUI, 2, "Updating level bar with mana");
                if (data.getMaxMana() == 0) {
                    player.setLevel(0);
                    player.setExp(0);
                } else {
                    player.setLevel((int) data.getMana());
                    player.setExp(Math.min(0.999f, (float) (0.999 * data.getMana() / data.getMaxMana())));
                }
            } else if (levelLevel) {
                Logger.log(LogType.GUI, 2, "Updating level bar with class level/exp");
                if (!data.hasClass()) {
                    player.setLevel(0);
                    player.setExp(0);
                } else {
                    PlayerClass main = data.getMainClass();
                    player.setLevel(main.getLevel());
                    player.setExp(Math.min(0.999f, (float) main.getExp() / main.getRequiredExp()));
                }
            }

            // Food bar options
            if (foodMana) {
                Logger.log(LogType.GUI, 2, "Updating food bar with mana");
                player.setSaturation(20);
                if (data.getMaxMana() == 0) {
                    player.setFoodLevel(20);
                } else {
                    player.setFoodLevel((int) Math.ceil(20 * data.getMana() / data.getMaxMana()));
                }
            } else if (foodExp) {
                Logger.log(LogType.GUI, 2, "Updating food bar with class level/exp");
                player.setSaturation(20);
                if (!data.hasClass()) {
                    player.setFoodLevel(0);
                } else {
                    PlayerClass main = data.getMainClass();
                    player.setFoodLevel((int) Math.floor(20 * main.getExp() / main.getRequiredExp()));
                }
            }

            // Action bar options
            if (useAction && data.hasClass()) {
                Logger.log(LogType.GUI, 2, "Updating action bar");
                PlayerClass main = data.getMainClass();
                String filtered = (main.getData().hasActionBarText() ? main.getData().getActionBarText() : actionText)
                        .replace("{combo}",
                                Fabled.getLanguage()
                                        .getMessage(ComboManager.DISPLAY_KEY,
                                                true,
                                                FilterType.COLOR,
                                                RPGFilter.COMBO.setReplacement(data.getComboData()
                                                        .getCurrentComboString()))
                                        .get(0))
                        .replace("{class}", main.getData().getPrefix())
                        .replace("{level}", "" + main.getLevel())
                        .replace("{exp}", "" + (int) main.getExp())
                        .replace("{expReq}", "" + main.getRequiredExp())
                        .replace("{expLeft}", "" + (int) Math.ceil(main.getRequiredExp() - main.getExp()))
                        .replace("{mana}", "" + (int) data.getMana())
                        .replace("{maxMana}", "" + (int) data.getMaxMana())
                        .replace("{name}", player.getName())
                        .replace("{health}", "" + (int) player.getHealth())
                        .replace("{maxHealth}", "" + (int) player.getMaxHealth())
                        .replace("{attr}", "" + data.getAttributePoints())
                        .replace("{sp}", "" + main.getPoints());
                while (filtered.contains("{value:")) {
                    int    index = filtered.indexOf("{value:");
                    int    end   = filtered.indexOf('}', index);
                    String key   = filtered.substring(index + 7, end);
                    String value = DynamicSkill.getCastData(player).get(key);
                    filtered = filtered.replace("{value:" + key + "}", (value == null ? "None" : value));
                }

                if (PluginChecker.isPlaceholderAPIActive()) {
                    filtered = PlaceholderAPIHook.format(filtered, player);
                }

                if (VersionManager.isVersionAtLeast(11000)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(filtered));
                } else {
                    MsgUT.sendActionBar(player, filtered);
                }
            }
        }
    }
}
