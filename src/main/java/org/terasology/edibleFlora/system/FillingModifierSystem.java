// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.edibleFlora.system;


import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.hunger.component.FoodComponent;
import org.terasology.simpleFarming.events.ModifyFilling;
import org.terasology.utilities.modifiable.ModifiableValue;

@RegisterSystem(RegisterMode.AUTHORITY)
public class FillingModifierSystem extends BaseComponentSystem {

    @ReceiveEvent
    public void onFillingModifiedEvent(ModifyFilling event, EntityRef entity) {
        FoodComponent foodComponent = new FoodComponent();
        foodComponent.filling = new ModifiableValue(event.getNewFilling());
        entity.addOrSaveComponent(foodComponent);
    }
}
