/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.edibleFlora.genome;

import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.edibleFlora.events.OnSpawn;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.genome.GenomeDefinition;
import org.terasology.genome.GenomeRegistry;
import org.terasology.genome.breed.BreedingAlgorithm;
import org.terasology.genome.breed.ContinuousBreedingAlgorithm;
import org.terasology.genome.breed.mutator.GeneMutator;
import org.terasology.genome.breed.mutator.VocabularyGeneMutator;
import org.terasology.genome.component.GenomeComponent;
import org.terasology.genome.genomeMap.SeedBasedGenomeMap;
import org.terasology.logic.characters.CharacterHeldItemComponent;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.Sender;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.simpleFarming.components.BushDefinitionComponent;
import org.terasology.simpleFarming.events.BeforePlanted;
import org.terasology.simpleFarming.events.OnSeedPlanted;
import org.terasology.simpleFarming.events.ProduceCreated;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockComponent;

import javax.annotation.Nullable;

/**
 * System managing genetics of all plants
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class GenomeAuthoritySystem extends BaseComponentSystem {
    @In
    private GenomeRegistry genomeRegistry;
    @In
    private WorldProvider worldProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenomeAuthoritySystem.class);

    private static String genomeRegistryPrefix = "EdibleFlora:";

    @ReceiveEvent
    public void onSpawn(OnSpawn event, EntityRef bush) {
        GenomeComponent genomeComponent = new GenomeComponent();
        //this will be a seed based random value
        genomeComponent.genes = "TTAZ";
        genomeComponent.genomeId = "tester";
        bush.addOrSaveComponent(genomeComponent);
    }

    @ReceiveEvent
    public void onProduceCreated(ProduceCreated event, EntityRef creator) {
        //check if it has genome comp, otherwise give random below
        EntityRef producer = event.getCreator();
        GenomeComponent genomeComponent = new GenomeComponent();
        //might have some issues regarding npe if bush is not sustainable
        EntityRef produce = event.getProduce();
        if (producer.hasComponent(GenomeComponent.class)) {
            genomeComponent.genomeId = producer.getComponent(GenomeComponent.class).genomeId;
            genomeComponent.genes = producer.getComponent(GenomeComponent.class).genes;
            LOGGER.info("Has genome component already " + genomeComponent.genes);
        } else {
            FastRandom rand = new FastRandom();
            LOGGER.info("Dont have a genome component, giving a new one now");
            genomeComponent.genomeId = producer.getParentPrefab().getName();
            LOGGER.info("Parent prefab is " + producer.getParentPrefab().getName());
            if (genomeRegistry.getGenomeDefinition(genomeComponent.genomeId) == null) {
                LOGGER.info("Making new genome map for " + genomeComponent.genomeId);
                addPropertyMap(producer, genomeComponent.genomeId);
            }
            //needs to be random based on vocabulary
            genomeComponent.genes =
                    "" + "ABCDEFGHIJK".charAt(rand.nextInt(9)) + "" + "ABCDEFGHIJK".charAt(rand.nextInt(9)) + "" +
                            "ABCDEFGHIJK".charAt(rand.nextInt(9));
//            producer.addOrSaveComponent(genomeComponent);
            if (producer!=null) {
                producer.addOrSaveComponent(genomeComponent);
            }
        }
        produce.addOrSaveComponent(genomeComponent);
    }

    @ReceiveEvent
    public void onBeforePlantedEvent(BeforePlanted event, EntityRef plant) {
        EntityRef seed = event.getSeed();
        if (seed.hasComponent(GenomeComponent.class)) {
            LOGGER.info("Seed had genome comp, giving it to plant" + (seed.getComponent(GenomeComponent.class)).genes);
            plant.addOrSaveComponent(seed.getComponent(GenomeComponent.class));
        }
        else {
            LOGGER.info("seed didnt have genome comp");
        }
    }

    @Command(shortDescription = "Prints genome of held item if possible.")
    public String heldGenomeCheck(@Sender EntityRef client) {
        EntityRef character = client.getComponent(ClientComponent.class).character;
        if (character.hasComponent(CharacterHeldItemComponent.class)) {
            EntityRef selectedItem = character.getComponent(CharacterHeldItemComponent.class).selectedItem;
            if (selectedItem.hasComponent(GenomeComponent.class)) {
                return "Has " + selectedItem.getComponent(GenomeComponent.class).genes;
            } else {
                return "Held item does not have a Genome Component";
            }
        } else {
            return "Command not valid for current conditions.";
        }
    }

    private void addPropertyMap(EntityRef entity, String genomeId) {
        LOGGER.info("Registering new map for " + genomeId);
        SeedBasedGenomeMap genomeMap = new SeedBasedGenomeMap(worldProvider.getSeed().hashCode());
        String geneVocabulary = "ABCDEFGHIJK";
        GeneMutator geneMutator = new VocabularyGeneMutator(geneVocabulary);
        BreedingAlgorithm continuousBreedingAlgorithm = new ContinuousBreedingAlgorithm(0.3f, geneMutator);
        genomeMap.addSeedBasedProperty("filling", 0, 1, 2, Integer.class, continuousBreedingAlgorithm,
                new Function<String, Integer>() {
                    @Nullable
                    @Override
                    public Integer apply(@Nullable String input) {
                        return (input.charAt(0) - 'A' + 5);
                    }
                });
        GenomeDefinition genomeDefinition = new GenomeDefinition(continuousBreedingAlgorithm, genomeMap);
        genomeRegistry.registerType(genomeId, genomeDefinition);
    }
}
