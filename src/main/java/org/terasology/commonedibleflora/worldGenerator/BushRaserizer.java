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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.simpleFarming.events.OnSeedPlanted;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizerPlugin;
import org.terasology.world.generator.plugin.RegisterPlugin;

@RegisterPlugin
public class BushRaserizer implements WorldRasterizerPlugin {
    private FastRandom random = new FastRandom();
    private Block air;
    private Block wildBush;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        air = blockManager.getBlock(BlockManager.AIR_ID);
        wildBush = blockManager.getBlock("CommonEdibleFlora:WildBush");
    }

    /**
     * Called once for each chunk being generated.
     * Places the WildBush randomly throughout the chunk.
     * @param chunk The chunk being generated
     * @param chunkRegion The chunk's region
     */
    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {

        FloraFacet facet = chunkRegion.getFacet(FloraFacet.class);
        facet.getRelativeEntries().keySet().stream().filter(pos -> chunk.getBlock(pos).equals(air)).forEach((BaseVector3i pos) -> {
            if (random.nextFloat() < 0.05) {
                chunk.setBlock(pos, wildBush);
            }
        });
    }
}
