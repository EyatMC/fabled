/**
 * Fabled
 * studio.magemonkey.fabled.log.Logger
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package studio.magemonkey.fabled.log;

import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.codex.mccore.config.parse.DataSection;

import java.util.HashMap;

/**
 * Handles logging messages of varying levels
 */
public class Logger {
    private static final HashMap<String, Integer> LEVELS = new HashMap<String, Integer>();

    /**
     * Loads all logging level settings from the config data
     *
     * @param config config data to load from
     */
    public static void loadLevels(DataSection config) {
        for (String key : config.keys())
            setLevel(key, config.getInt(key));
    }

    /**
     * Sets the active logging level for a given category
     *
     * @param key   category
     * @param level logging level
     */
    public static void setLevel(String key, int level) {
        LEVELS.put(key, level);
    }

    /**
     * Logs a message under the given category if it meets the level
     * requirement.
     *
     * @param key     category key
     * @param level   logging level
     * @param message message to send
     */
    public static void log(String key, int level, String message) {
        if (LEVELS.containsKey(key) && LEVELS.get(key) >= level)
            Fabled.inst().getLogger().info(message);
    }

    /**
     * Logs a message under the given category if it meets the level
     * requirement.
     *
     * @param key     category key
     * @param level   logging level
     * @param message message to send
     */
    public static void log(LogType key, int level, String message) {
        log(key.key(), level, message);
    }

    /**
     * Displays an error message for an invalid setting
     *
     * @param message error message
     */
    public static void invalid(String message) {
        Fabled.inst().getLogger().severe(message);
    }

    /**
     * Displays an error message for a bug
     *
     * @param message error message
     */
    public static void bug(String message) {
        Fabled.inst().getLogger().severe(message);
    }

    /**
     * Logs a message, ignoring any active logging levels
     *
     * @param message message to log
     */
    public static void log(String message) {
        Fabled.inst().getLogger().info(message);
    }
}
