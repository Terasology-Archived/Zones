/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.common.nui;

import java.util.ArrayList;

import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.math.Rect2f;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.ControlWidget;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;

/**
 * @author mkienenb
 */
public class MenuHUDElement extends CoreHudWidget implements ControlWidget {

    public static final String TABBED_MENU_WIDGET_ID = "engine:tabbedMenu";

    private UISingleClickList<UIMenuItem> menu;
    private UISingleClickList<UIMenuItem> submenu;
    private UISingleClickList<UIMenuItem> subSubMenu;
    
    @Override
    public void initialise() {
        menu = this.find("awtMenu", UISingleClickList.class);
        submenu = this.find("awtSubMenu", UISingleClickList.class);
        subSubMenu = this.find("awtSubSubMenu", UISingleClickList.class);

        menu.setList(new ArrayList<UIMenuItem>());
        menu.setVisible(true);
        submenu.setVisible(false);
        subSubMenu.setVisible(false);
        
        menu.subscribe(UIMenuItem.getUiMenuItemActivateEventListener());
        submenu.subscribe(UIMenuItem.getUiMenuItemActivateEventListener());
        subSubMenu.subscribe(UIMenuItem.getUiMenuItemActivateEventListener());

        menu.setItemRenderer(UIMenuItem.getUIMenuItemRenderer());
        submenu.setItemRenderer(UIMenuItem.getUIMenuItemRenderer());
        subSubMenu.setItemRenderer(UIMenuItem.getUIMenuItemRenderer());
    }

    public UISingleClickList<UIMenuItem> getMenu() {
        return menu;
    }

    public UISingleClickList<UIMenuItem> getSubmenu() {
        return submenu;
    }

    public UISingleClickList<UIMenuItem> getSubSubMenu() {
        return subSubMenu;
    }

    public static MenuHUDElement getMenuHudElement() {
        NUIManager nuiManager = CoreRegistry.get(NUIManager.class);
        
        // TODO: temporary workaround for bug:
        AssetUri uri = new AssetUri(AssetType.UI_ELEMENT, TABBED_MENU_WIDGET_ID);
        MenuHUDElement awtTabbedMenuHUDElement = nuiManager.getHUD().getHUDElement(uri, MenuHUDElement.class);
        
        if (null == awtTabbedMenuHUDElement) {
            awtTabbedMenuHUDElement = nuiManager.getHUD().addHUDElement(TABBED_MENU_WIDGET_ID, MenuHUDElement.class, Rect2f.createFromMinAndSize(0, 0, 1, 1));
        }
        
        return awtTabbedMenuHUDElement;
    }
}
