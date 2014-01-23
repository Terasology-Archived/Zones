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

import org.terasology.entitySystem.Component;
import org.terasology.math.Region3i;
import org.terasology.math.Vector3i;

public final class ZoneComponent implements Component {

    private Region3i region;

    public String Name;
    public ZoneType zonetype;

    private ZoneComponent() {
    }

    public ZoneComponent(Region3i region) {
        this.region = region;
    }

    public Vector3i getMinBounds() {
        return getBlockSelectionRegion().min();
    }

    public Vector3i getMaxBounds() {
        return getBlockSelectionRegion().max();
    }

    public Region3i getBlockSelectionRegion() {
        return region;
    }

    public Vector3i getStartPosition() {
        return getBlockSelectionRegion().min();
    }

    public int getZoneHeight() {
        return getZoneHeight(getBlockSelectionRegion());
    }

    public int getZoneDepth() {
        return getZoneDepth(getBlockSelectionRegion());
    }

    public int getZoneWidth() {
        return getZoneWidth(getBlockSelectionRegion());
    }

    public static int getZoneHeight(Region3i region3i) {
        return region3i.size().y;
    }

    public static int getZoneDepth(Region3i region3i) {
        return region3i.size().x;
    }

    public static int getZoneWidth(Region3i region3i) {
        return region3i.size().z;
    }
}