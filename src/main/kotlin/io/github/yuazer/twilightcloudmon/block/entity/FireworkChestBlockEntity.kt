package io.github.yuazer.twilightcloudmon.block.entity

import io.github.yuazer.twilightcloudmon.registry.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.state.BlockState

class FireworkChestBlockEntity(
    pos: BlockPos,
    state: BlockState
) : ChestBlockEntity(ModBlockEntities.FIREWORK_CHEST, pos, state) {

    override fun getDefaultName(): Component {
        return Component.translatable("container.twilightcloudmon.firework_chest")
    }
}

