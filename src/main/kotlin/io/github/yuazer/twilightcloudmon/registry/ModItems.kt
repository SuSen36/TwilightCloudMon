package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.registry.RegistryHelper.id
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import org.slf4j.LoggerFactory
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

object ModItems {
    private val LOGGER = LoggerFactory.getLogger("${Twilightcloudmon.MOD_ID}/ModItems")
    private val autoRegisteredIds = mutableListOf<ResourceLocation>()

    private val BLACKLISTED_ITEMS = setOf(
        "primal_bow", "primal_bow_0", "primal_bow_1", "primal_bow_2",
        "primal_sword", "primal_hammer", "movement_plate", "warp_plate",
        "elevator", "chisel", "pixelmon_item",
        "firework_sword", "firework_greatsword", "firework_hammer",
        "firework_trident", "firework_spear", "firework_scythe",
        "firework_staff", "firework_axe", "firework_pickaxe",
        "firework_shovel", "firework_hoe", "firework_bow",
        "firework_bow_0", "firework_bow_1", "firework_bow_2",
        "firework_crossbow", "firework_crossbow_0", "firework_crossbow_1",
        "firework_crossbow_2", "firework_crossbow_charged", "firework_crossbow_firework",
        "firework_fishing_rod", "firework_fishing_rod_cast",
        "firework_shield", "firework_shield_blocking", "firework_key",
        "firework_chest", "firework_helmet", "firework_chestplate",
        "firework_leggings", "firework_boots", "firework",
        "firework_wings", "firework_wings_cosmetic",
        "firework_wings_cosmetic_normal_2", "firework_wings_cosmetic_self",
        "firework_wings_cosmetic_self_2"
    )

    private val PRIORITY_ITEMS = listOf("dna_fire", "eclipsecharter", "up_paper", "ghost_bottle")
    private val MEGA_STONE_NAMES = listOf("flygonite_x", "flygonite_y", "giratinaite")

    fun register() {
        if (!FabricLoader.getInstance().isModLoaded("mega_showdown")) {
            MEGA_STONE_NAMES.forEach(::registerMegaStone)
        }

        autoRegisterFromModelFiles()

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register { entries ->
            autoRegisteredIds.forEach { entries.accept(BuiltInRegistries.ITEM.get(it)) }
        }
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register { entries ->
            autoRegisteredIds.forEach { entries.accept(BuiltInRegistries.ITEM.get(it)) }
        }

        LOGGER.info("Item registration done. auto={}", autoRegisteredIds.size)
    }

    @JvmStatic
    fun registerMegaStonesAfterMSD() {
        try {
            MEGA_STONE_NAMES.forEach(::registerMegaStone)
            LOGGER.info("Registered mega stones after Mega Showdown init")
        } catch (e: Exception) {
            LOGGER.error("Failed to register mega stones after MSD init", e)
        }
    }

    private fun registerMegaStone(name: String) {
        val rid = id(name)
        if (BuiltInRegistries.ITEM.containsKey(rid)) return

        val isMSD = FabricLoader.getInstance().isModLoaded("mega_showdown")
        val item = if (isMSD) MegaShowdownIntegration.createMegaStoneItem(name)
        else Item(Item.Properties().stacksTo(1))

        val registered = Registry.register(BuiltInRegistries.ITEM, rid, item)
        autoRegisteredIds += rid

        if (isMSD) {
            MegaShowdownIntegration.registerRemaps(registered, name.lowercase().replace("_", ""))
        }
    }

    private fun autoRegisterFromModelFiles() {
        val container = FabricLoader.getInstance()
            .getModContainer(Twilightcloudmon.MOD_ID)
            .orElseThrow { IllegalStateException("ModContainer not found for ${Twilightcloudmon.MOD_ID}") }

        val modelsPathOpt = container.findPath("assets/${Twilightcloudmon.MOD_ID}/models/item")
        if (modelsPathOpt.isEmpty) {
            LOGGER.warn("No models/item folder found")
            return
        }

        val modelsPath = modelsPathOpt.get()
        if (!Files.exists(modelsPath)) {
            LOGGER.warn("models/item path not exists: {}", modelsPath)
            return
        }

        val allItemNames = mutableListOf<String>()
        Files.walk(modelsPath).use { stream ->
            stream.filter { it.isRegularFile() && it.name.endsWith(".json") }
                .map { file ->
                    modelsPath.relativize(file).toString()
                        .replace("\\", "/").removeSuffix(".json")
                        .removePrefix("custom_items2/")
                }
                .forEach { name ->
                    when {
                        "_bow_" in name -> return@forEach
                        name in BLACKLISTED_ITEMS -> return@forEach
                        isMegaStoneName(name) || name.endsWith("stone") -> return@forEach
                        else -> allItemNames.add(name)
                    }
                }
        }

        val sorted = allItemNames.sortedWith(Comparator { a, b ->
            val ap = PRIORITY_ITEMS.indexOf(a)
            val bp = PRIORITY_ITEMS.indexOf(b)
            when {
                ap != -1 && bp != -1 -> ap.compareTo(bp)
                ap != -1 -> -1
                bp != -1 -> 1
                else -> a.compareTo(b)
            }
        })

        sorted.forEach { name ->
            try {
                val rid = id(name)
                if (BuiltInRegistries.ITEM.containsKey(rid)) return@forEach

                Registry.register(BuiltInRegistries.ITEM, rid, Item(Item.Properties()))
                autoRegisteredIds += rid
                LOGGER.info("Auto-registered item: {}", rid)
            } catch (e: Exception) {
                LOGGER.error("Failed to register item: {}", name, e)
            }
        }
    }

    private fun isMegaStoneName(name: String) =
        name.endsWith("ite") || name.endsWith("ite_x") || name.endsWith("ite_y")
}
