package io.github.yuazer.twilightcloudmon.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class MovementPlateBlock(properties: Properties) : Block(properties) {

    init {
        @Suppress("LeakingThis")
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: net.minecraft.world.item.context.BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState =
        state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        rotate(state, mirror.getRotation(state.getValue(FACING)))

    override fun getShape(
        state: BlockState,
        level: net.minecraft.world.level.BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (entity.isCrouching) return

        val dir = state.getValue(FACING)
        val pushBase = if (entity is ItemEntity) 0.65 else 0.45
        val directionVec = net.minecraft.world.phys.Vec3(dir.step().x.toDouble(), 0.0, dir.step().z.toDouble())
        if (directionVec.lengthSqr() == 0.0) return

        val push = directionVec.normalize().scale(pushBase)
        val current = entity.deltaMovement
        val blended = current.scale(0.15).add(push)
        val clamped = net.minecraft.world.phys.Vec3(
            Mth.clamp(blended.x, -1.2, 1.2),
            blended.y,
            Mth.clamp(blended.z, -1.2, 1.2)
        )

        entity.deltaMovement = net.minecraft.world.phys.Vec3(clamped.x, current.y, clamped.z)
        entity.hasImpulse = true
    }

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        private val SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0)
    }
}

