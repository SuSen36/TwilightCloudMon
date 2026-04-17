package io.github.yuazer.twilightcloudmon.registry

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager
import io.github.yuazer.twilightcloudmon.item.MegaStoneItem
import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import org.slf4j.LoggerFactory
import java.util.function.Supplier

private val LOGGER = LoggerFactory.getLogger("${Twilightcloudmon.MOD_ID}/MegaShowdownIntegration")

object MegaShowdownIntegration {
    
    private val msdComponentsClass by lazy {
        try {
            Class.forName("com.github.yajatkaul.mega_showdown.components.MegaShowdownDataComponents")
        } catch (e: ClassNotFoundException) {
            LOGGER.warn("MegaShowdownDataComponents class not found", e)
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val registryTypeComponent: DataComponentType<String>? by lazy {
        try {
            val field = msdComponentsClass?.getDeclaredField("REGISTRY_TYPE_COMPONENT")
            field?.isAccessible = true
            val supplier = field?.get(null) as? Supplier<DataComponentType<String>>
            supplier?.get()
        } catch (e: Exception) {
            LOGGER.warn("Failed to get REGISTRY_TYPE_COMPONENT via reflection", e)
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val resourceLocationComponent: DataComponentType<ResourceLocation>? by lazy {
        try {
            val field = msdComponentsClass?.getDeclaredField("RESOURCE_LOCATION_COMPONENT")
            field?.isAccessible = true
            val supplier = field?.get(null) as? Supplier<DataComponentType<ResourceLocation>>
            supplier?.get()
        } catch (e: Exception) {
            LOGGER.warn("Failed to get RESOURCE_LOCATION_COMPONENT via reflection", e)
            null
        }
    }

    fun createMegaStoneItem(name: String): Item {
        val properties = Item.Properties()

        registryTypeComponent?.let { properties.component(it, "mega") }
        
        resourceLocationComponent?.let { component ->
            properties.component(component, ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, name))
        }

        return MegaStoneItem(properties.stacksTo(1))
    }

    fun registerRemaps(item: Item, showdownId: String) {
        CobblemonHeldItemManager.registerRemap(item, showdownId)
    }
}
