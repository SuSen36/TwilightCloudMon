package io.github.yuazer.twilightcloudmon.block.entity

import io.github.yuazer.twilightcloudmon.registry.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class WarpPlateBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModBlockEntities.WARP_PLATE, pos, state) {

    private var target: BlockPos? = null

    fun getWarpPosition(): BlockPos? = target

    fun setWarpPosition(warpPos: BlockPos) {
        target = warpPos
        setChanged()
    }

    override fun saveAdditional(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        super.saveAdditional(tag, registryLookup)
        target?.let { tag.putLong(TARGET_KEY, it.asLong()) }
    }

    override fun loadAdditional(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        super.loadAdditional(tag, registryLookup)
        target = if (tag.contains(TARGET_KEY)) BlockPos.of(tag.getLong(TARGET_KEY)) else null
    }

    companion object {
        private const val TARGET_KEY = "WarpTarget"
    }
}

