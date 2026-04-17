package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers

object ModSwords {
    private val registered = mutableListOf<ResourceLocation>()

    // 始源之剑 - 添加攻击属性
    val PRIMAL_SWORD = SwordItem(
        Tiers.NETHERITE,
        Item.Properties()
        .fireResistant()
        .stacksTo(1)
        .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4F))
    )

    // 骨之剑
    val BONESWORD = SwordItem(
        Tiers.NETHERITE,
        Item.Properties()
            .stacksTo(1)
            .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 2, -2.4F))
    )

    fun register() {
        registerItem("primal_sword", PRIMAL_SWORD)
        registerItem("bonesword", BONESWORD)
    }

    private fun registerItem(name: String, item: Item) {
        val id = ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, name)
        Registry.register(BuiltInRegistries.ITEM, id, item)
        registered += id
    }
}
                                                                                                                                    