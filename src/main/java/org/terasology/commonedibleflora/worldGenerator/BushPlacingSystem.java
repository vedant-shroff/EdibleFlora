/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.commonedibleflora.worldGenerator;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.events.DropItemEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.physics.events.ImpulseEvent;
import org.terasology.registry.In;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.block.entity.CreateBlockDropsEvent;

@RegisterSystem(RegisterMode.ALWAYS)
public class BushPlacingSystem extends BaseComponentSystem {
    private Prefab[] bushes = new Prefab[4];
    private FastRandom random = new FastRandom();
    @In
    private PrefabManager prefabManager;
    @In
    private EntityManager entityManager;

    @Override
    public void postBegin() {
        bushes[0] = prefabManager.getPrefab("CommonEdibleFlora:AppleSeed");
        bushes[1] = prefabManager.getPrefab("CommonEdibleFlora:PearSeed");
        bushes[2] = prefabManager.getPrefab("CommonEdibleFlora:Carrot");
        bushes[3] = prefabManager.getPrefab("CommonEdibleFlora:OrangeSeed");
    }

    /**
     * Called when the wild berry bush is destroyed.
     * It handles dropping a random seed from the selected bushes.
     *
     * @param event             The event fired
     * @param entity            The wild bush entity
     * @param flagComponent     The component used to flag the wild berry bush
     * @param locationComponent The location component of the bush
     */
    @ReceiveEvent
    public void onWildBushDestroyed(CreateBlockDropsEvent event, EntityRef entity, WildBushComponent flagComponent, LocationComponent locationComponent) {
        EntityRef seedItem = entityManager.create(bushes[random.nextInt(bushes.length)]);
        seedItem.send(new DropItemEvent(locationComponent.getWorldPosition().add(0, 0.5f, 0)));
        seedItem.send(new ImpulseEvent(random.nextVector3f(22.0f)));

        event.consume();
    }
}
