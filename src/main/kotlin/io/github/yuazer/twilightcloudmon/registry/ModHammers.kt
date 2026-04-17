package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.MaceItem
import net.minecraft.world.item.Rarity

object ModHammers {
    private val registered = mutableListOf<ResourceLocation>()

    // 始源之锤 - 使用MaceItem作为基础
    val PRIMAL_HAMMER = MaceItem(
        Item.Properties()
            .rarity(Rarity.EPIC)
            .fireResistant()
            .stacksTo(1)
            .component(DataComponents.TOOL, MaceItem.createToolProperties())
            .attributes(MaceItem.createAttributes())
    )

    fun register() {
        Registry.register(
            BuiltInRegistries.ITEM, 
            ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "primal_hammer"), 
            PRIMAL_HAMMER
        )
        
        registered += ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "primal_hammer")
    }
} 