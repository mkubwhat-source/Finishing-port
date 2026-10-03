package net.hecco.bountifulfares.definition.item.custom;

import net.minecraft.world.item.Item;

// `Equipable` (the marker interface for non-armor wearables like elytra) is gone entirely in
// 26.3 - "can this item be worn, and does right-clicking equip it" is now purely the
// `minecraft:equippable` data component (see Equippable.Builder#setEquipOnInteract), set on this
// item's Properties in BFItems rather than through a custom use()/getEquipmentSlot() override here.
public class SunHatItem extends Item {
    public SunHatItem(Properties settings) {
        super(settings);
    }
}
