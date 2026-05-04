package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.item.PrimalBowItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

object ModBows {

    val PRIMAL_BOW: PrimalBowItem = PrimalBowItem(
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(500)
    )

    fun register() {
        RegistryHelper.registerItem("primal_bow", PRIMAL_BOW)
    }
}
