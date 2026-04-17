package io.github.yuazer.twilightcloudmon.event

import io.github.yuazer.twilightcloudmon.block.ElevatorBlock
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer

object ElevatorEvents {
    private val jumpCooldown = mutableMapOf<ServerPlayer, Int>()
    private val sneakCooldown = mutableMapOf<ServerPlayer, Int>()
    private val lastOnGround = mutableMapOf<ServerPlayer, Boolean>()
    private val lastCrouching = mutableMapOf<ServerPlayer, Boolean>()
    
    fun register() {
        // 处理跳跃事件（向上传送）和潜行事件（向下传送）
        ServerTickEvents.END_SERVER_TICK.register { server ->
            server.playerList.players.forEach { player ->
                if (player !is ServerPlayer) return@forEach
                
                val wasOnGround = lastOnGround[player] ?: false
                val isOnGround = player.onGround()
                val wasCrouching = lastCrouching[player] ?: false
                val isCrouching = player.isCrouching
                
                val pos = player.blockPosition().below()
                val block = player.level().getBlockState(pos).block
                
                // 检查跳跃（向上传送）
                // 检测从地面到空中的瞬间（玩家刚跳起）
                if (wasOnGround && !isOnGround && block is ElevatorBlock) {
                    val cooldown = jumpCooldown[player] ?: 0
                    if (cooldown <= 0) {
                        // 立即执行传送（向上）
                        ElevatorBlock.takeElevator(player.level(), pos, player, true)
                        jumpCooldown[player] = 10
                        sneakCooldown[player] = 10
                    }
                }
                
                // 检查潜行（向下传送）
                // 检测下蹲且在地面上（允许持续下蹲时触发）
                if (isCrouching && isOnGround && block is ElevatorBlock) {
                    val cooldown = sneakCooldown[player] ?: 0
                    if (cooldown <= 0) {
                        // 立即执行传送（向下）
                        ElevatorBlock.takeElevator(player.level(), pos, player, false)
                        sneakCooldown[player] = 10
                        jumpCooldown[player] = 10
                    }
                }
                
                // 更新状态
                lastOnGround[player] = isOnGround
                lastCrouching[player] = isCrouching
                
                // 更新冷却时间
                val currentJumpCooldown = (jumpCooldown[player] ?: 0) - 1
                if (currentJumpCooldown <= 0) {
                    jumpCooldown.remove(player)
                } else {
                    jumpCooldown[player] = currentJumpCooldown
                }
                
                val currentSneakCooldown = (sneakCooldown[player] ?: 0) - 1
                if (currentSneakCooldown <= 0) {
                    sneakCooldown.remove(player)
                } else {
                    sneakCooldown[player] = currentSneakCooldown
                }
            }
        }
    }
}

