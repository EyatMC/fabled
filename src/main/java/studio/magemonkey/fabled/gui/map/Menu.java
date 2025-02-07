/**
 * Fabled
 * studio.magemonkey.fabled.gui.map.Menu
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
package studio.magemonkey.fabled.gui.map;

import studio.magemonkey.codex.mccore.gui.MapFont;
import studio.magemonkey.codex.mccore.gui.MapMenuManager;
import studio.magemonkey.codex.mccore.gui.MapScheme;
import studio.magemonkey.fabled.Fabled;

import java.awt.*;
import java.io.File;

/**
 * Manages schemes for the map menus
 */
public class Menu {
    // Menu keys
    public static final String          SKILL_TREE = "sapiSkills";
    // Images
    static final        String          BACKGROUND = "background";
    static final        String          TITLE      = "title";
    static final        String          NAMEPLATE  = "nameplate";
    static final        String          SELECTOR   = "selector";
    static final        String          UP_0       = "up0";
    static final        String          UP_1       = "up1";
    static final        String          DOWN_0     = "down0";
    static final        String          DOWN_1     = "down1";
    static final        String          MORE_0     = "more0";
    static final        String          MORE_1     = "more1";
    static final        String          BACK_0     = "back0";
    static final        String          BACK_1     = "back1";
    // Fonts
    static final        String          LIST       = "list";
    static final        String          DETAIL     = "detail";
    // Colors
    static final        String          FONT       = "font";
    // Menus
    static              SkillListMenu   LIST_MENU;
    static              SkillDetailMenu DETAIL_MENU;

    /**
     * Sets up the schemes for Fabled
     *
     * @param api Fabled reference
     */
    public static void initialize(Fabled api) {
        LIST_MENU = new SkillListMenu(api);
        DETAIL_MENU = new SkillDetailMenu(api);
        DETAIL_MENU.setParent(LIST_MENU);
        MapMenuManager.registerMenu(SKILL_TREE, LIST_MENU);

        MapScheme scheme = MapScheme.create(api, new File(api.getDataFolder(), "img"));

        // Define images
        scheme.defineImg(BACKGROUND, BACKGROUND);
        scheme.defineImg(TITLE, TITLE);
        scheme.defineImg(NAMEPLATE, NAMEPLATE);
        scheme.defineImg(SELECTOR, SELECTOR);
        scheme.defineImg(UP_0, UP_0);
        scheme.defineImg(UP_1, UP_1);
        scheme.defineImg(DOWN_0, DOWN_0);
        scheme.defineImg(DOWN_1, DOWN_1);
        scheme.defineImg(MORE_0, MORE_0);
        scheme.defineImg(MORE_1, MORE_1);
        scheme.defineImg(BACK_0, BACK_0);
        scheme.defineImg(BACK_1, BACK_1);

        // Define fonts
        scheme.defineFont(LIST, new MapFont(new Font("Tahoma", Font.BOLD, 12), 2));
        scheme.defineFont(DETAIL, new MapFont(new Font("Tahoma", Font.PLAIN, 9), 1));

        // Define colors
        scheme.defineColor(FONT, "FFFFFF");

        scheme.finalize();
    }
}
