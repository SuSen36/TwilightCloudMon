package io.github.yuazer.twilightcloudmon.block

import com.mojang.serialization.MapCodec
import io.github.yuazer.twilightcloudmon.block.entity.WarpPlateBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class WarpPlateBlock(properties: BlockBehaviour.Properties) : BaseEntityBlock(properties) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        WarpPlateBlockEntity(pos, state)

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (level.isClientSide) return
        val blockEntity = level.getBlockEntity(pos) as? WarpPlateBlockEntity ?: return
        val target = blockEntity.getWarpPosition() ?: return

        if (target == pos || entity.blockPosition() != pos) return

        val destinationX = target.x + 0.5
        val destinationY = target.y.toDouble()
        val destinationZ = target.z + 0.5

        if (entity is ServerPlayer) {
            val serverLevel = level as? ServerLevel ?: return
            entity.teleportTo(
                serverLevel,
                destinationX,
                destinationY,
                destinationZ,
                entity.yRot,
                entity.xRot
            )
        } else {
            entity.teleportTo(destinationX, destinationY, destinationZ)
        }

        entity.fallDistance = 0f
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    companion object {
        private val SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)
        private val CODEC: MapCodec<WarpPlateBlock> = simpleCodec(::WarpPlateBlock)
    }
}

