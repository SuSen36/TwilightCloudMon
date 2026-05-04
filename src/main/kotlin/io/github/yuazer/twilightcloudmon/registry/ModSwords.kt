package io.github.yuazer.twilightcloudmon.registry

import net.minecraft.world.item.Item
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers

object ModSwords {

    val PRIMAL_SWORD: SwordItem = SwordItem(
        Tiers.NETHERITE,
        Item.Properties()
            .fireResistant().stacksTo(1)
            .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4F))
    )

    val BONESWORD: SwordItem = SwordItem(
        Tiers.NETHERITE,
        Item.Properties()
            .stacksTo(1)
            .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 2, -2.4F))
    )

    fun register() {
        RegistryHelper.registerItem("primal_sword", PRIMAL_SWORD)
        RegistryHelper.registerItem("bonesword", BONESWORD)
    }
}