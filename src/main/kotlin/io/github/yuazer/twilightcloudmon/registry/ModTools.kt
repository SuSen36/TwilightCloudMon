package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.item.ChiselItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

object ModTools {
    private val registered = mutableListOf<ResourceLocation>()

    // 宝可梦雕塑工具
    val CHISEL = ChiselItem(
        Item.Properties()
            .rarity(Rarity.RARE)
            .stacksTo(1)
    )

    fun register() {
        Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "chisel"),
            CHISEL
        )
        
        registered += ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "chisel")
    }
}

