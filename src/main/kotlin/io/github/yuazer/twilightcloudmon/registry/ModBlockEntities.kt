package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.block.entity.FireworkChestBlockEntity
import io.github.yuazer.twilightcloudmon.block.entity.WarpPlateBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.Builder

object ModBlockEntities {

    lateinit var WARP_PLATE: BlockEntityType<WarpPlateBlockEntity> private set
    lateinit var FIREWORK_CHEST: BlockEntityType<FireworkChestBlockEntity> private set

    fun register() {
        WARP_PLATE = RegistryHelper.registerBlockEntity(
            "warp_plate",
            Builder.of(::WarpPlateBlockEntity, ModBlocks.WARP_PLATE).build(null)
        )
        FIREWORK_CHEST = RegistryHelper.registerBlockEntity(
            "firework_chest",
            Builder.of(::FireworkChestBlockEntity, ModBlocks.FIREWORK_CHEST).build(null)
        )
    }
}
