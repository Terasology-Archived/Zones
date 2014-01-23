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
package org.terasology.zone;

import java.util.List;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;

/**
 * This tracking system idea needs to be completely redone so that it's based off Components.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ZoneTrackingSystem implements ComponentSystem {

    // TODO : a better way to save / load zones, but it does the trick
    private static EntityRef zonelist = EntityRef.NULL;

    @In
    private EntityManager entityManager;

    @Override
    public void initialise() {
        createZoneList();
    }

    /**
     * adds a new zone to the corresponding zone list
     * @param zone
     * 				the zone to be added
     */
    public static void addZone(EntityRef zone) {
        ZoneListComponent zonelistcomp = zonelist.getComponent(ZoneListComponent.class);
        ZoneComponent zoneComponent = zone.getComponent(ZoneComponent.class);
        switch (zoneComponent.zonetype) {
            case Gather: {
                zonelistcomp.Gatherzones.add(zone);
                break;
            }
            case Work: {
                zonelistcomp.Workzones.add(zone);
                break;
            }
            case Terraform: {
                zonelistcomp.Terrazones.add(zone);
                break;
            }
            case Storage: {
                zonelistcomp.Storagezones.add(zone);
                break;
            }
            case OreonFarm: {
                zonelistcomp.OreonFarmzones.add(zone);
                break;
            }
        }
        zonelist.saveComponent(zonelistcomp);
    }

    /**
     * returns a list with all gather zones
     * @return
     * 			a list with all gather zones
     */
    public static List<EntityRef> getGatherZoneList() {
        if (zonelist == EntityRef.NULL) {
            return null;
        }
        return zonelist.getComponent(ZoneListComponent.class).Gatherzones;
    }

    /**
     * returns a list with all work zones
     * @return
     * 			a list with all work zones
     */
    public static List<EntityRef> getWorkZoneList() {
        if (zonelist == EntityRef.NULL) {
            return null;
        }
        return zonelist.getComponent(ZoneListComponent.class).Workzones;
    }

    /**
     * returns a list with all terraform zones
     * @return
     * 			a list with all terraform zones
     */
    public static List<EntityRef> getTerraformZoneList() {
        if (zonelist == EntityRef.NULL) {
            return null;
        }
        return zonelist.getComponent(ZoneListComponent.class).Terrazones;
    }

    /**
     * returns a list with all storage zones
     * @return
     * 			a list with all storage zones
     */
    public static List<EntityRef> getStorageZoneList() {
        if (zonelist == EntityRef.NULL) {
            return null;
        }
        return zonelist.getComponent(ZoneListComponent.class).Storagezones;
    }

    /**
     * returns a list with all Oreon farm zones
     * @return
     * 			a list with all Oreon farm zones
     */
    public static List<EntityRef> getOreonFarmZoneList() {
        if (zonelist == EntityRef.NULL) {
            return null;
        }
        return zonelist.getComponent(ZoneListComponent.class).OreonFarmzones;
    }

    /**
     * creates a zonelist component
     * used to save all zones (persist)
     */
    private static void createZoneList() {
        zonelist = CoreRegistry.get(EntityManager.class).create();
        ZoneListComponent zonecomp = new ZoneListComponent();
        zonelist.addComponent(zonecomp);
        zonelist.saveComponent(zonecomp);
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }
}
