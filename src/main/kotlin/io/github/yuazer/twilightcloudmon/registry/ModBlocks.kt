package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.block.ElevatorBlock
import io.github.yuazer.twilightcloudmon.block.FireworkChestBlock
import io.github.yuazer.twilightcloudmon.block.MovementPlateBlock
import io.github.yuazer.twilightcloudmon.block.WarpPlateBlock
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import org.slf4j.LoggerFactory
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

object ModBlocks {
    private val LOGGER = LoggerFactory.getLogger("${Twilightcloudmon.MOD_ID}/ModBlocks")

    // 自动注册成功的方块 ID
    private val autoRegisteredIds = mutableListOf<ResourceLocation>()
    private val manualRegisteredIds = mutableListOf<ResourceLocation>()

    lateinit var MOVEMENT_PLATE: Block
        private set

    lateinit var WARP_PLATE: Block
        private set

    lateinit var ELEVATOR: Block
        private set

    lateinit var FIREWORK_CHEST: Block
        private set

    fun register() {
        registerCustomBlocks()

        // 自动注册：扫描 assets/<modid>/blockstates/*.json
        autoRegisterSimpleBlocksByBlockstates()

        // 放进创造模式分组（需要 Fabric API；若不需要可删除本段）
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register { entries ->
            autoRegisteredIds.forEach { id ->
                entries.accept(BuiltInRegistries.ITEM.get(id)) // BlockItem 跟方块同 ID
            }
            manualRegisteredIds.forEach { id ->
                entries.accept(BuiltInRegistries.ITEM.get(id))
            }
        }

        LOGGER.info(
            "Auto block registration done. auto={}, manual={}",
            autoRegisteredIds.size, manualRegisteredIds.size
        )
    }

    private fun registerCustomBlocks() {
        MOVEMENT_PLATE = registerBlockWithItem(
            "movement_plate",
            MovementPlateBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(0.8f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .isRedstoneConductor { _, _, _ -> false }
            )
        )

        WARP_PLATE = registerBlockWithItem(
            "warp_plate",
            WarpPlateBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(1.3f, 0.2f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .isRedstoneConductor { _, _, _ -> false }
            )
        )

        ELEVATOR = registerBlockWithItem(
            "elevator",
            ElevatorBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(0.8f)
                    .sound(SoundType.STONE)
            )
        )

        FIREWORK_CHEST = registerBlockWithItem(
            "firework_chest",
            FireworkChestBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            )
        )
    }

    /** 便捷注册一个“简单方块”，并同时注册同名 BlockItem */
    private fun registerSimpleBlock(
        name: String,
        props: BlockBehaviour.Properties = defaultPropsFor(name)
    ): Block {
        val rid = id(name)

        // 若已存在，直接返回
        BuiltInRegistries.BLOCK.getOptional(rid).ifPresent { return@ifPresent }

        val block = Block(props)
        Registry.register(BuiltInRegistries.BLOCK, rid, block)

        // 同名 BlockItem（如已存在则跳过）
        if (!BuiltInRegistries.ITEM.containsKey(rid)) {
            Registry.register(
                BuiltInRegistries.ITEM,
                rid,
                BlockItem(block, Item.Properties())
            )
        }
        return block
    }

    private fun registerBlockWithItem(name: String, block: Block): Block {
        val rid = id(name)

        Registry.register(BuiltInRegistries.BLOCK, rid, block)
        if (!BuiltInRegistries.ITEM.containsKey(rid)) {
            Registry.register(
                BuiltInRegistries.ITEM,
                rid,
                BlockItem(block, Item.Properties())
            )
        }
        manualRegisteredIds += rid
        return block
    }

    /** 自动扫描 assets/<modid>/blockstates 下所有 .json 文件名并注册为简单方块 */
    private fun autoRegisterSimpleBlocksByBlockstates() {
        val container = FabricLoader.getInstance()
            .getModContainer(Twilightcloudmon.MOD_ID)
            .orElseThrow { IllegalStateException("ModContainer not found for ${Twilightcloudmon.MOD_ID}") }

        val dirOpt = container.findPath("assets/${Twilightcloudmon.MOD_ID}/blockstates")
        if (dirOpt.isEmpty) {
            LOGGER.warn("No blockstates folder found under assets/${Twilightcloudmon.MOD_ID}")
            return
        }

        val dir = dirOpt.get()
        if (!Files.exists(dir)) {
            LOGGER.warn("blockstates path not exists: {}", dir)
            return
        }

        Files.list(dir).use { stream ->
            stream.filter { it.isRegularFile() && it.name.endsWith(".json") }
                .map { it.name.removeSuffix(".json") }
                .forEach { name ->
                    val rid = id(name)

                    // 已注册（手动或其他途径）则跳过
                    if (BuiltInRegistries.BLOCK.containsKey(rid)) {
                        LOGGER.debug("Skip already-registered block: {}", rid)
                        return@forEach
                    }

                    // 如果有少数需要专用类/属性的方块，可在这里按名字特殊处理
                    // when (name) {
                    //     "glass_like" -> Registry.register(... GlassBlock(...))
                    //     else -> ...
                    // }

                    val block = Block(defaultPropsFor(name))
                    Registry.register(BuiltInRegistries.BLOCK, rid, block)

                    if (!BuiltInRegistries.ITEM.containsKey(rid)) {
                        Registry.register(
                            BuiltInRegistries.ITEM,
                            rid,
                            BlockItem(block, Item.Properties())
                        )
                    }

                    autoRegisteredIds += rid
                    LOGGER.info("Auto-registered block: {}", rid)
                }
        }
    }

    /** 默认方块属性；可以根据命名做一点点启发式（可自行扩展） */
    private fun defaultPropsFor(name: String): BlockBehaviour.Properties {
        var props = BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)          // 默认石质
            .strength(1.5f, 6.0f)
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()

        if (name.contains("wood") || name.contains("planks")) {
            props = props.mapColor(MapColor.WOOD).sound(SoundType.WOOD).requiresCorrectToolForDrops()
        }
        if (name.contains("glass")) {
            props = props.sound(SoundType.GLASS).strength(0.3f).requiresCorrectToolForDrops()
        }
        return props
    }

    private fun id(path: String) =
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, path)
}
