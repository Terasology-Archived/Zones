package org.terasology.common.nui;
/*
 * Copyright 2012 Benjamin Glatzel <benjamin.glatzel@me.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.zone.nui.UIZoneBook;

@RegisterSystem(RegisterMode.AUTHORITY)
public class OpenUiAction implements ComponentSystem {

    @In
    private NUIManager nuiManager;

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {
    }

    private UIZoneBook zoneBook;

    @ReceiveEvent(components = {ItemComponent.class, OpenUiActionComponent.class})
    public void onActivate(ActivateEvent event, EntityRef entity) {
        OpenUiActionComponent uiInfo = entity.getComponent(OpenUiActionComponent.class);
        if (uiInfo != null) {
            // TODO: there's no way to register programmically-created instances of windows yet short of creating asset resolvers and factories.
            // For the moment, hardcode the asset resolution since we'll end up making it into a file-based asset eventually anyway.
            if ("zonebook".equals(uiInfo.uiwindowid)) {
                if (null == zoneBook) {
                    zoneBook = new UIZoneBook();
                }
                nuiManager.pushScreen(zoneBook);
            }
        }
    }

    @Override
    public void preBegin() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postBegin() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void preSave() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postSave() {
        // TODO Auto-generated method stub
        
    }
}
