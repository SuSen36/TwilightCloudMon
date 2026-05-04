package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.item.ChiselItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

object ModTools {

    val CHISEL: ChiselItem = ChiselItem(
        Item.Properties().rarity(Rarity.RARE).stacksTo(1)
    )

    fun register() {
        RegistryHelper.registerItem("chisel", CHISEL)
    }
}
