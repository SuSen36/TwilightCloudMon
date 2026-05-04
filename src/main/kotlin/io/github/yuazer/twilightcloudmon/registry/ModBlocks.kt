package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.block.ElevatorBlock
import io.github.yuazer.twilightcloudmon.block.FireworkChestBlock
import io.github.yuazer.twilightcloudmon.block.MovementPlateBlock
import io.github.yuazer.twilightcloudmon.block.WarpPlateBlock
import io.github.yuazer.twilightcloudmon.registry.RegistryHelper.id
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.loader.api.FabricLoader
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
    private val autoRegisteredIds = mutableListOf<ResourceLocation>()
    private val manualRegisteredIds = mutableListOf<ResourceLocation>()

    lateinit var MOVEMENT_PLATE: Block private set
    lateinit var WARP_PLATE: Block private set
    lateinit var ELEVATOR: Block private set
    lateinit var FIREWORK_CHEST: Block private set

    fun register() {
        registerCustomBlocks()
        autoRegisterFromBlockstates()

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register { entries ->
            (autoRegisteredIds + manualRegisteredIds).forEach { entries.accept(BuiltInRegistries.ITEM.get(it)) }
        }

        LOGGER.info("Block registration done. auto={}, manual={}", autoRegisteredIds.size, manualRegisteredIds.size)
    }

    private fun registerCustomBlocks() {
        MOVEMENT_PLATE = registerManual(
            "movement_plate",
            MovementPlateBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(0.8f).sound(SoundType.METAL)
                    .noOcclusion().isRedstoneConductor { _, _, _ -> false }
            )
        )
        WARP_PLATE = registerManual(
            "warp_plate",
            WarpPlateBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).strength(1.3f, 0.2f).sound(SoundType.METAL)
                    .noOcclusion().isRedstoneConductor { _, _, _ -> false }
            )
        )
        ELEVATOR = registerManual(
            "elevator",
            ElevatorBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ).strength(0.8f).sound(SoundType.STONE)
            )
        )
        FIREWORK_CHEST = registerManual(
            "firework_chest",
            FireworkChestBlock(
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.WOOD).noOcclusion()
            )
        )
    }

    private fun registerManual(name: String, block: Block): Block {
        RegistryHelper.registerBlockWithItem(name, block)
        manualRegisteredIds += id(name)
        return block
    }

    private fun autoRegisterFromBlockstates() {
        val container = FabricLoader.getInstance()
            .getModContainer(Twilightcloudmon.MOD_ID)
            .orElseThrow { IllegalStateException("ModContainer not found for ${Twilightcloudmon.MOD_ID}") }

        val dirOpt = container.findPath("assets/${Twilightcloudmon.MOD_ID}/blockstates")
        if (dirOpt.isEmpty) {
            LOGGER.warn("No blockstates folder found")
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
                    if (BuiltInRegistries.BLOCK.containsKey(rid)) return@forEach

                    RegistryHelper.registerBlockWithItem(name, Block(defaultPropsFor(name)))
                    autoRegisteredIds += rid
                    LOGGER.info("Auto-registered block: {}", rid)
                }
        }
    }

    private fun defaultPropsFor(name: String): BlockBehaviour.Properties {
        val props = BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE).strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops()

        return when {
            "wood" in name || "planks" in name ->
                props.mapColor(MapColor.WOOD).sound(SoundType.WOOD)
            "glass" in name ->
                props.sound(SoundType.GLASS).strength(0.3f)
            else -> props
        }
    }
}
