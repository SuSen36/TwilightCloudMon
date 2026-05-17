package io.github.yuazer.twilightcloudmon.registry

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager
import com.github.yajatkaul.mega_showdown.components.MegaShowdownDataComponents
import com.github.yajatkaul.mega_showdown.utils.RegistryLocator
import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.item.MegaStoneItem
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

object MegaShowdownIntegration {

    fun createMegaStoneItem(name: String): Item {
        val properties = Item.Properties()
            .component(MegaShowdownDataComponents.REGISTRY_TYPE_COMPONENT.get(), RegistryLocator.MEGA)
            .component(MegaShowdownDataComponents.RESOURCE_LOCATION_COMPONENT.get(), megaShowdownId(name))
            .stacksTo(1)
        return MegaStoneItem(properties)
    }

    fun registerRemaps(item: Item, showdownId: String) {
        CobblemonHeldItemManager.registerRemap(item, showdownId)
        CobblemonHeldItemManager.registerStackRemap { stack ->
            if (stack.item === item) showdownId else null
        }
    }

    private fun megaShowdownId(path: String): ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, path)
}
