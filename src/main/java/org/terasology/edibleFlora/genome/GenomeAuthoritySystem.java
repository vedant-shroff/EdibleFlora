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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.edibleFlora.events.OnSpawn;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.genome.component.GenomeComponent;
import org.terasology.logic.characters.CharacterHeldItemComponent;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.Sender;
import org.terasology.network.ClientComponent;
import org.terasology.simpleFarming.components.BushDefinitionComponent;
import org.terasology.simpleFarming.events.ProduceCreated;
import org.terasology.world.block.BlockComponent;

/**
 * System managing genetics of all plants
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class GenomeAuthoritySystem extends BaseComponentSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenomeAuthoritySystem.class);

    @ReceiveEvent
    public void onSpawn(OnSpawn event, EntityRef bush) {
        GenomeComponent genomeComponent = new GenomeComponent();
        //this will be a seed based random value
        genomeComponent.genes = "TTAZ";
        genomeComponent.genomeId = "tester";
        bush.addOrSaveComponent(genomeComponent);
    }

    @ReceiveEvent
    public void onProduceCreated(ProduceCreated event, EntityRef producer) {
        //check if it has genome comp, otherwise give random below
        LOGGER.info("inside");
        GenomeComponent genomeComponent = new GenomeComponent();
        EntityRef produce = event.getProduce();
        if (producer.hasComponent(GenomeComponent.class)) {
            genomeComponent.genomeId = producer.getComponent(GenomeComponent.class).genomeId;
            genomeComponent.genes = producer.getComponent(GenomeComponent.class).genes;
            LOGGER.info(genomeComponent.genes);
            produce.addOrSaveComponent(genomeComponent);
        } else {
            LOGGER.info("Dont have a genome component, giving a new one now");
            genomeComponent.genomeId = "TesterTesting";
            genomeComponent.genes = "TTttZ";
            produce.addOrSaveComponent(genomeComponent);
        }

    }

    @Command(shortDescription = "Prints genome of held item if possible.")
    public String heldGenomeCheck(@Sender EntityRef client) {
        EntityRef character = client.getComponent(ClientComponent.class).character;
        if (character.hasComponent(CharacterHeldItemComponent.class)) {
            EntityRef selectedItem = character.getComponent(CharacterHeldItemComponent.class).selectedItem;
            if (selectedItem.hasComponent(GenomeComponent.class)) {
                return selectedItem.getComponent(GenomeComponent.class).genes;
            } else {
                return "Held item does not have a Genome Component";
            }
        } else {
            return "Command not valid for current conditions.";
        }
    }
}
