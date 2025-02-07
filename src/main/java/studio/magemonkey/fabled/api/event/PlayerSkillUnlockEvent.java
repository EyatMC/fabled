/**
 * Fabled
 * studio.magemonkey.fabled.api.event.PlayerSkillUnlockEvent
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
package studio.magemonkey.fabled.api.event;

import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.player.PlayerSkill;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player initially unlocks a skill
 */
public class PlayerSkillUnlockEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final        PlayerData  player;
    private final        PlayerSkill unlockedSkill;

    /**
     * Constructor
     *
     * @param playerData    data of the player unlocking the skill
     * @param unlockedSkill the skill that was unlocked
     */
    public PlayerSkillUnlockEvent(PlayerData playerData, PlayerSkill unlockedSkill) {
        this.player = playerData;
        this.unlockedSkill = unlockedSkill;
    }

    /**
     * @return data of the player unlocking the skill
     */
    public PlayerData getPlayerData() {
        return player;
    }

    /**
     * @return skill that was unlocked
     */
    public PlayerSkill getUnlockedSkill() {
        return unlockedSkill;
    }

    /**
     * @return gets the handlers for the event
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
