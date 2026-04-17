package io.github.yuazer.twilightcloudmon.block

import com.mojang.serialization.MapCodec
import io.github.yuazer.twilightcloudmon.block.entity.FireworkChestBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.ChestType
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Supplier

class FireworkChestBlock(properties: BlockBehaviour.Properties) : ChestBlock(
    properties,
    Supplier { io.github.yuazer.twilightcloudmon.registry.ModBlockEntities.FIREWORK_CHEST }
) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return FireworkChestBlockEntity(pos, state)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val state = super.getStateForPlacement(context) ?: return null
        return state.setValue(ChestBlock.TYPE, ChestType.SINGLE)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun isPathfindable(
        state: BlockState,
        type: PathComputationType
    ): Boolean = false

    override fun codec(): MapCodec<out ChestBlock> = CODEC

    companion object {
        private val SHAPE: VoxelShape = Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375)
        private val CODEC: MapCodec<FireworkChestBlock> = simpleCodec(::FireworkChestBlock)
    }
}
