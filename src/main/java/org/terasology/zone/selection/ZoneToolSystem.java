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
package org.terasology.zone.selection;

import java.awt.Color;

import org.terasology.asset.Assets;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.math.Region3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.world.selection.BlockSelectionComponent;
import org.terasology.zone.Constants;

@Share(ZoneToolSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class ZoneToolSystem implements ComponentSystem {

    @In
    private EntityManager entityManager;

    @In
    private InventoryManager inventoryManager;

    private EntityRef currentBlockSelectionDisplayEntity = EntityRef.NULL;

    @Override
    public void initialise() {
        BlockSelectionComponent blockSelectionComponent = new BlockSelectionComponent();
        Color transparentGreen = new Color(0, 255, 0, 100);
        blockSelectionComponent.texture = Assets.get(TextureUtil.getTextureUriForColor(transparentGreen), Texture.class);
        currentBlockSelectionDisplayEntity = entityManager.create(blockSelectionComponent);

    }

    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player, InventoryComponent inventory) {
        inventoryManager.giveItem(player, entityManager.create(Constants.MODULE_NAME + ":" + "zonetool"));
        inventoryManager.giveItem(player, entityManager.create(Constants.MODULE_NAME + ":" + "zonebook"));
    }

    public void setCurrentlySelectedRegion(Region3i currentBlockSelectionRegion) {

        BlockSelectionComponent blockSelectionComponent = currentBlockSelectionDisplayEntity.getComponent(BlockSelectionComponent.class);
        blockSelectionComponent.currentSelection = currentBlockSelectionRegion;
        if (null != currentBlockSelectionRegion) {
            blockSelectionComponent.shouldRender = true;
        } else {
            blockSelectionComponent.shouldRender = false;
        }

        // TODO: it would be better if we didn't persist this entity and block selection component
        // currentBlockSelectionEntity.saveComponent(blockSelectionComponent);
    }

    public Region3i getCurrentlySelectedRegion() {
        if (currentBlockSelectionDisplayEntity == EntityRef.NULL) {
            return null;
        } else {
            BlockSelectionComponent selection = currentBlockSelectionDisplayEntity.getComponent(BlockSelectionComponent.class);
            return selection.currentSelection;
        }
    }

    @Override
    public void shutdown() {
        currentBlockSelectionDisplayEntity.destroy();
        currentBlockSelectionDisplayEntity = EntityRef.NULL;
    }

    @ReceiveEvent
    public void onSelection(ApplyBlockSelectionEvent event, EntityRef entity) {
        setCurrentlySelectedRegion(event.getSelection());
    }
}
