package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.item.PrimalBowItem
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import org.slf4j.LoggerFactory

object ModBows {
    private val registered = mutableListOf<ResourceLocation>()

    // 始源之弓
    val PRIMAL_BOW = PrimalBowItem(
        Item.Properties()
            .rarity(Rarity.EPIC)
            .fireResistant()
            .durability(500)
    )

    fun register() {
        Registry.register(
            BuiltInRegistries.ITEM, 
            ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "primal_bow"), 
            PRIMAL_BOW
        )
        
        registered += ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "primal_bow")
    }
}
