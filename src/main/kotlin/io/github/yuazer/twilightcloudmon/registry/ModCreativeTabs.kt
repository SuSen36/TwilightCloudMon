package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem

import net.minecraft.world.item.CreativeModeTabs

object ModCreativeTabs {
    fun registerToTabs() {

        // 方块类：全部 BlockItem 放到"建筑方块"分组
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register { entries ->
            for (item in BuiltInRegistries.ITEM) {
                val key = BuiltInRegistries.ITEM.getKey(item) ?: continue
                if (key.namespace == Twilightcloudmon.MOD_ID && item is BlockItem) {
                    entries.accept(item)
                }
            }
        }

        // 普通物品：非 BlockItem 的本模组物品放到"材料/原料"分组
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register { entries ->
            for (item in BuiltInRegistries.ITEM) {
                val key = BuiltInRegistries.ITEM.getKey(item) ?: continue
                if (key.namespace == Twilightcloudmon.MOD_ID && item !is BlockItem && !isWeaponItem(item, key.path)) {
                    entries.accept(item)
                }
            }
        }

        // 工具类物品放到 TOOLS_AND_UTILITIES
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register { entries ->
            for (item in BuiltInRegistries.ITEM) {
                val key = BuiltInRegistries.ITEM.getKey(item) ?: continue
                if (key.namespace == Twilightcloudmon.MOD_ID && (isToolLike(item, key.path) || key.path == "chisel")) {
                    entries.accept(item)
                }
            }
        }
    }

    // 判断是否为武器物品
    private fun isWeaponItem(item: net.minecraft.world.item.Item, name: String): Boolean {
        return name == "primal_sword" || name == "primal_bow" || name == "primal_hammer" ||
               name.endsWith("_sword") || name.endsWith("_bow") || name.endsWith("_hammer") ||
               name.endsWith("_blade") || "sword" in name || "bow" in name ||
               "hammer" in name || "blade" in name
    }

    // 判断是否为工具类物品
    private fun isToolLike(item: net.minecraft.world.item.Item, name: String): Boolean {
        return name.endsWith("_tool") || name.endsWith("_chisel") || 
               "tool" in name || "chisel" in name
    }
}
