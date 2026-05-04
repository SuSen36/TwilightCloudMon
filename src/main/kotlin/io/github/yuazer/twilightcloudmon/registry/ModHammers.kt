package io.github.yuazer.twilightcloudmon.registry

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Item
import net.minecraft.world.item.MaceItem
import net.minecraft.world.item.Rarity

object ModHammers {

    val PRIMAL_HAMMER: MaceItem = MaceItem(
        Item.Properties()
            .rarity(Rarity.EPIC).fireResistant().stacksTo(1)
            .component(DataComponents.TOOL, MaceItem.createToolProperties())
            .attributes(MaceItem.createAttributes())
    )

    fun register() {
        RegistryHelper.registerItem("primal_hammer", PRIMAL_HAMMER)
    }
}