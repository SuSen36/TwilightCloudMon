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
        /** 升降方块搜索范围（默认10格） */
        private const val SEARCH_RANGE = 10

        /**
         * 使用升降方块传送玩家
         * @param world 世界
         * @param pos 当前升降方块位置
         * @param player 玩家
         * @param upwards 是否向上（true=向上，false=向下）
         */
        fun takeElevator(world: Level, pos: BlockPos, player: ServerPlayer, upwards: Boolean) {
            val elevatorPos = findNearestElevator(world, pos, upwards) ?: return
            val destination = Vec3(
                player.x,
                elevatorPos.y + 1.5,
                player.z
            )
            
            // 传送玩家
            player.teleportTo(
                (world as? ServerLevel) ?: return,
                destination.x,
                destination.y,
                destination.z,
                player.yRot,
                player.xRot
            )
        }

        /**
         * 查找最近的升降方块
         * @param world 世界
         * @param pos 起始位置
         * @param upwards 是否向上搜索
         * @return 找到的升降方块位置，如果没找到返回 null
         */
        private fun findNearestElevator(world: Level, pos: BlockPos, upwards: Boolean): BlockPos? {
            val posY = pos.y
            val mutablePos = BlockPos.MutableBlockPos()
            
            val searchRangeY = if (upwards) SEARCH_RANGE else -SEARCH_RANGE
            var currentY = posY
            
            // 循环搜索，跳过当前位置和当前位置上方
            while (if (upwards) currentY < posY + searchRangeY else currentY > posY + searchRangeY) {
                mutablePos.set(pos.x, currentY, pos.z)
                
                // 跳过当前位置和当前位置上方
                if (!mutablePos.equals(pos) && !mutablePos.above().equals(pos)) {
                    val state = world.getBlockState(mutablePos)
                    if (state.block is ElevatorBlock) {
                        return mutablePos.immutable()
                    }
                }
                
                currentY += if (upwards) 1 else -1
            }
            
            return null
        }
    }
}

