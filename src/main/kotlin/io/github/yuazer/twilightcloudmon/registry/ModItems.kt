package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.ShieldItem
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import org.slf4j.LoggerFactory
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

object ModItems {
    private val LOGGER = LoggerFactory.getLogger("${Twilightcloudmon.MOD_ID}/ModItems")

    // 自动注册成功的 id 列表（用于加入创造模式分组等）
    private val autoRegisteredIds = mutableListOf<ResourceLocation>()
    
    // 自动注册黑名单 - 这些物品将被跳过而不自动注册
    private val blacklistedItems = setOf(
        "primal_bow",
        "primal_bow_0",
        "primal_bow_1",
        "primal_bow_2",
        "primal_sword",
        "primal_hammer",
        "movement_plate",
        "warp_plate",
        "elevator",
        "chisel",
        // 模型基类，不应注册成物品
        "pixelmon_item",
        // Firework 武器和物品 - 需要手动注册
        "firework_sword",
        "firework_greatsword",
        "firework_hammer",
        "firework_trident",
        "firework_spear",
        "firework_scythe",
        "firework_staff",
        "firework_axe",
        "firework_pickaxe",
        "firework_shovel",
        "firework_hoe",
        "firework_bow",
        "firework_bow_0",
        "firework_bow_1",
        "firework_bow_2",
        "firework_crossbow",
        "firework_crossbow_0",
        "firework_crossbow_1",
        "firework_crossbow_2",
        "firework_crossbow_charged",
        "firework_crossbow_firework",
        "firework_fishing_rod",
        "firework_fishing_rod_cast",
        "firework_shield",
        "firework_shield_blocking",
        "firework_key",
        "firework_chest",
        "firework_helmet",
        "firework_chestplate",
        "firework_leggings",
        "firework_boots",
        "firework",
        "firework_wings",
        "firework_wings_cosmetic",
        "firework_wings_cosmetic_normal_2",
        "firework_wings_cosmetic_self",
        "firework_wings_cosmetic_self_2"
    )

    fun register() {
        val isMegaShowdownLoaded = FabricLoader.getInstance().isModLoaded("mega_showdown")
        // 如果未装 mega_showdown，则立即以占位实现注册；若已装，则延迟到 MSD init 尾部注册
        if (!isMegaShowdownLoaded) {
            registerMegaStone("flygonite_x")
            registerMegaStone("flygonite_y")
            registerMegaStone("giratinaite")
        }

        // 自动注册：扫描 assets/<modid>/models/item/*.json 的文件名
        autoRegisterSimpleItemsByModelFiles()
        
        // 进化石也可以根据需要注册
        // registerMegaStone("bug_stone")
        // ... 其他进化石

        // 放进创造模式分组
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register { entries ->
            autoRegisteredIds.forEach { id ->
                entries.accept(BuiltInRegistries.ITEM.get(id))
            }
        }
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register { entries ->
            autoRegisteredIds.forEach { id ->
                entries.accept(BuiltInRegistries.ITEM.get(id))
            }
        }

        LOGGER.info("Auto item registration done. total auto = {}, manual = {}",
            autoRegisteredIds.size, 0)
    }

    /** 便捷注册“简单物品” */
    private fun registerSimple(name: String): Item {
        val id = id(name)
        // 已存在则直接返回
        BuiltInRegistries.ITEM.getOptional(id).ifPresent { return@ifPresent }
        val item = Item(Item.Properties().stacksTo(64))
        return Registry.register(BuiltInRegistries.ITEM, id, item)
    }

    private fun registerWeapon(name: String): Item {
        val id = id(name)
        // 已存在则直接返回
        BuiltInRegistries.ITEM.getOptional(id).ifPresent { return@ifPresent }
        val item = SwordItem(Tiers.NETHERITE, Item.Properties().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4f)))
        return Registry.register(BuiltInRegistries.ITEM, id, item)
    }

    private fun registerShield(name: String): Item {
        val id = id(name)
        // 已存在则直接返回
        BuiltInRegistries.ITEM.getOptional(id).ifPresent { return@ifPresent }
        val item = ShieldItem(Item.Properties().durability(2031))
        return Registry.register(BuiltInRegistries.ITEM, id, item)
    }

    private fun registerMegaStone(name: String): Item {
        val id = id(name)
        BuiltInRegistries.ITEM.getOptional(id).ifPresent { return@ifPresent }
        
        val isMegaShowdownLoaded = FabricLoader.getInstance().isModLoaded("mega_showdown")

        val item = if (isMegaShowdownLoaded) {
            MegaShowdownIntegration.createMegaStoneItem(name)
        } else {
            Item(Item.Properties().stacksTo(1))
        }
        
        val registered = Registry.register(BuiltInRegistries.ITEM, id, item)
        autoRegisteredIds += id

        if (isMegaShowdownLoaded) {
            // 将物品名转换为 Showdown ID (例如 flygonite_x -> flygonitex)
            val showdownId = name.lowercase().replace("_", "")
            MegaShowdownIntegration.registerRemaps(registered, showdownId)
        }

        return registered
    }

    /** 在 MegaShowdown.init 尾部调用，确保组件注册后再注册 Mega 石 */
    @JvmStatic
    fun registerMegaStonesAfterMSD() {
        try {
            registerMegaStone("flygonite_x")
            registerMegaStone("flygonite_y")
            registerMegaStone("giratinaite")
            LOGGER.info("Registered mega stones after Mega Showdown init")
        } catch (e: Exception) {
            LOGGER.error("Failed to register mega stones after MSD init", e)
        }
    }

    /**
     * 自动扫描并注册：
     * 读取 assets/<modid>/models/item 下所有 .json 文件名作为注册名
     * 注意：只适用于"无特殊行为"的简单物品
     */
    private fun autoRegisterSimpleItemsByModelFiles() {
        val container = FabricLoader.getInstance()
            .getModContainer(Twilightcloudmon.MOD_ID)
            .orElseThrow { IllegalStateException("ModContainer not found for ${Twilightcloudmon.MOD_ID}") }

        val modelsPathOpt = container.findPath("assets/${Twilightcloudmon.MOD_ID}/models/item")
        if (modelsPathOpt.isEmpty) {
            LOGGER.warn("No models/item folder found under assets/${Twilightcloudmon.MOD_ID}")
            return
        }

        val modelsPath = modelsPathOpt.get()
        if (!Files.exists(modelsPath)) {
            LOGGER.warn("models/item path not exists: {}", modelsPath)
            return
        }

        // 定义注册顺序：优先注册的物品列表
        val priorityItems = listOf("dna_fire", "eclipsecharter", "up_paper", "ghost_bottle")
        
        // 收集所有需要注册的物品名称
        val allItemNames = mutableListOf<String>()
        Files.walk(modelsPath).use { stream ->
            stream.filter { it.isRegularFile() && it.name.endsWith(".json") }
                .map { file ->
                    val relativePath = modelsPath.relativize(file)
                    var name = relativePath.toString().replace("\\", "/").removeSuffix(".json")
                    
                    // 移除 custom_items2/ 前缀
                    if (name.startsWith("custom_items2/")) {
                        name = name.removePrefix("custom_items2/")
                    }
                    
                    name
                }
                .forEach { name ->
                    // 跳过弓的变体，这些由 ModBows 处理
                    if (name.contains("_bow_")) {
                        LOGGER.debug("Skip bow variant: {}", name)
                        return@forEach
                    }
                    
                    // 检查黑名单：跳过黑名单中的物品
                    if (blacklistedItems.contains(name)) {
                        LOGGER.debug("Skip blacklisted item: {}", name)
                        return@forEach
                    }

                    // 跳过进化石和 Mega 石的自动注册
                    if (isMegaStoneName(name) || name.endsWith("stone")) {
                        LOGGER.debug("Skip evolution/mega stone: {}", name)
                        return@forEach
                    }
                    
                    allItemNames.add(name)
                }
        }

        // 按优先级排序：优先物品在前，其他物品按字母顺序
        val sortedItemNames = try {
            allItemNames.sortedWith { a, b ->
                val aPriority = priorityItems.indexOf(a)
                val bPriority = priorityItems.indexOf(b)
                
                when {
                    aPriority != -1 && bPriority != -1 -> aPriority.compareTo(bPriority) // 都在优先级列表中
                    aPriority != -1 -> -1 // a 在优先级列表中，b 不在
                    bPriority != -1 -> 1  // b 在优先级列表中，a 不在
                    else -> a.compareTo(b) // 都不在优先级列表中，按字母顺序
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to sort items, using original order", e)
            allItemNames // 如果排序失败，使用原始顺序
        }

        // 按排序后的顺序注册物品
        sortedItemNames.forEach { name ->
            try {
                val id = id(name)

                // 已经注册过（手动或其它途径），则跳过
                if (BuiltInRegistries.ITEM.containsKey(id)) {
                    LOGGER.debug("Skip already-registered item: {}", id)
                    return@forEach
                }

                // 若某些名字需要特殊类/属性，可在这里加白名单映射
                val item = when {
                    name in listOf("zenith_edge", "zenith_ward") -> {
                        Item(Item.Properties())
                    }
                    else -> Item(Item.Properties())
                }
                Registry.register(BuiltInRegistries.ITEM, id, item)
                autoRegisteredIds += id
                
                val priority = priorityItems.indexOf(name)
                if (priority != -1) {
                    LOGGER.info("Auto-registered item: {} (priority: {})", id, priority)
                } else {
                    LOGGER.info("Auto-registered item: {}", id)
                }
            } catch (e: Exception) {
                LOGGER.error("Failed to register item: {}", name, e)
            }
        }
    }

    private fun isMegaStoneName(name: String) =
        name.endsWith("ite") || name.endsWith("ite_x") || name.endsWith("ite_y")

    private fun id(path: String) =
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, path)
}
