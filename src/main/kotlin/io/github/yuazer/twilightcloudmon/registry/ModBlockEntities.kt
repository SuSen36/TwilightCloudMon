package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.block.entity.FireworkChestBlockEntity
import io.github.yuazer.twilightcloudmon.block.entity.WarpPlateBlockEntity
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType.Builder

object ModBlockEntities {

    lateinit var WARP_PLATE: BlockEntityType<WarpPlateBlockEntity>
        private set

    lateinit var FIREWORK_CHEST: BlockEntityType<FireworkChestBlockEntity>
        private set

    fun register() {
        WARP_PLATE = register(
            "warp_plate",
            Builder.of(::WarpPlateBlockEntity, ModBlocks.WARP_PLATE).build(null)
        )
        
        FIREWORK_CHEST = register(
            "firework_chest",
            Builder.of(::FireworkChestBlockEntity, ModBlocks.FIREWORK_CHEST).build(null)
        )
    }

    private fun <T : BlockEntity> register(
        name: String,
        type: BlockEntityType<T>
    ): BlockEntityType<T> {
        val id = ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, name)
        return net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type)
    }
}

