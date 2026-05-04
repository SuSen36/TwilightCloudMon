package io.github.yuazer.twilightcloudmon.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.phys.Vec3

class ElevatorBlock(properties: BlockBehaviour.Properties) : Block(properties) {

    companion object {
        private const val SEARCH_RANGE = 10
        private const val TELEPORT_Y_OFFSET = 1.5

        fun takeElevator(world: Level, pos: BlockPos, player: ServerPlayer, upwards: Boolean) {
            val elevatorPos = findNearestElevator(world, pos, upwards) ?: return
            val serverLevel = world as? ServerLevel ?: return
            player.teleportTo(
                serverLevel,
                player.x,
                elevatorPos.y + TELEPORT_Y_OFFSET,
                player.z,
                player.yRot,
                player.xRot
            )
        }

        private fun findNearestElevator(world: Level, pos: BlockPos, upwards: Boolean): BlockPos? {
            val step = if (upwards) 1 else -1
            val limit = pos.y + (if (upwards) SEARCH_RANGE else -SEARCH_RANGE)
            val mutablePos = BlockPos.MutableBlockPos()
            var currentY = pos.y

            while (if (upwards) currentY < limit else currentY > limit) {
                mutablePos.set(pos.x, currentY, pos.z)
                if (mutablePos != pos && mutablePos.above() != pos) {
                    if (world.getBlockState(mutablePos).block is ElevatorBlock) {
                        return mutablePos.immutable()
                    }
                }
                currentY += step
            }
            return null
        }
    }
}

