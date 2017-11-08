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


import org.terasology.core.world.generator.facets.FloraFacet;
import org.terasology.core.world.generator.rasterizers.FloraType;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.simpleFarming.events.OnSeedPlanted;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizerPlugin;
import org.terasology.world.generator.plugin.RegisterPlugin;

import java.util.Map;

@RegisterPlugin
public class BushRaserizer implements WorldRasterizerPlugin {
    private Block air;
    private Block dirt;
    private Prefab[] bushes = new Prefab[3];
    private FastRandom random = new FastRandom();
    private EntityManager entityManager;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        PrefabManager prefabManager = CoreRegistry.get(PrefabManager.class);
        entityManager = CoreRegistry.get(EntityManager.class);

        /* Prefabs with a DefinitionComponent */
        bushes[0] = prefabManager.getPrefab("CommonEdibleFlora:AppleSeed");
        bushes[1] = prefabManager.getPrefab("CommonEdibleFlora:PearSeed");
        bushes[2] = prefabManager.getPrefab("CommonEdibleFlora:CarrotBush");

        air = blockManager.getBlock(BlockManager.AIR_ID);
        dirt = blockManager.getBlock("core:dirt");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {

        FloraFacet facet = chunkRegion.getFacet(FloraFacet.class);

        Map<BaseVector3i, FloraType> entries = facet.getRelativeEntries();
        // check if some other rasterizer has already placed something here
        entries.keySet().stream().filter(pos -> chunk.getBlock(pos).equals(air)).forEach(pos -> {
            //chunk.setBlock(pos, dirt);

            Prefab prefab = bushes[random.nextInt(3)];
            EntityRef bush = entityManager.create(prefab);

            bush.send(new OnSeedPlanted(chunk.chunkToWorldPosition(pos)));

            /*if (random.nextFloat() < 0.1) {
                EntityRef bush = entityManager.create(bushes[random.nextInt(3)]);
                Vector3f position =
                bush.addOrSaveComponent(new LocationComponent(position));
                ActivateEvent event = new ActivateEvent(bush, bush, position, position, position, position, 0);
                bush.send(event);
            }*/
        });
    }
}
