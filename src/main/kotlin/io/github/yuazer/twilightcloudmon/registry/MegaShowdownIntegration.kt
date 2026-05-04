package io.github.yuazer.twilightcloudmon.registry

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager
import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.item.MegaStoneItem
import io.github.yuazer.twilightcloudmon.registry.RegistryHelper.id
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import org.slf4j.LoggerFactory
import java.util.function.Supplier

object MegaShowdownIntegration {

    private val LOGGER = LoggerFactory.getLogger("${Twilightcloudmon.MOD_ID}/MegaShowdownIntegration")
    private const val MSD_COMPONENTS_CLASS = "com.github.yajatkaul.mega_showdown.components.MegaShowdownDataComponents"

    private val msdComponentsClass by lazy {
        runCatching { Class.forName(MSD_COMPONENTS_CLASS) }
            .onFailure { LOGGER.warn("MegaShowdownDataComponents class not found", it) }
            .getOrNull()
    }

    @Suppress("UNCHECKED_CAST")
    private val registryTypeComponent: DataComponentType<String>? by lazy {
        getComponentField<String>("REGISTRY_TYPE_COMPONENT")
    }

    @Suppress("UNCHECKED_CAST")
    private val resourceLocationComponent: DataComponentType<ResourceLocation>? by lazy {
        getComponentField<ResourceLocation>("RESOURCE_LOCATION_COMPONENT")
    }

    fun createMegaStoneItem(name: String): Item {
        val properties = Item.Properties()
        registryTypeComponent?.let { properties.component(it, "mega") }
        resourceLocationComponent?.let { properties.component(it, id(name)) }
        return MegaStoneItem(properties.stacksTo(1))
    }

    fun registerRemaps(item: Item, showdownId: String) {
        CobblemonHeldItemManager.registerRemap(item, showdownId)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getComponentField(fieldName: String): DataComponentType<T>? = try {
        val field = msdComponentsClass?.getDeclaredField(fieldName)
        field?.isAccessible = true
        (field?.get(null) as? Supplier<DataComponentType<T>>)?.get()
    } catch (e: Exception) {
        LOGGER.warn("Failed to get $fieldName via reflection", e)
        null
    }
}
