package io.github.yuazer.twilightcloudmon.event

import io.github.yuazer.twilightcloudmon.block.ElevatorBlock
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.level.ServerPlayer

object ElevatorEvents {

    private const val COOLDOWN_TICKS = 10

    private val jumpCooldown = mutableMapOf<ServerPlayer, Int>()
    private val sneakCooldown = mutableMapOf<ServerPlayer, Int>()
    private val lastOnGround = mutableMapOf<ServerPlayer, Boolean>()
    private val lastCrouching = mutableMapOf<ServerPlayer, Boolean>()

    fun register() {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            server.playerList.players.forEach { player ->
                if (player !is ServerPlayer) return@forEach

                val wasOnGround = lastOnGround[player] ?: false
                val isOnGround = player.onGround()
                val isCrouching = player.isCrouching

                val pos = player.blockPosition().below()
                val block = player.level().getBlockState(pos).block

                if (wasOnGround && !isOnGround && block is ElevatorBlock) {
                    if ((jumpCooldown[player] ?: 0) <= 0) {
                        ElevatorBlock.takeElevator(player.level(), pos, player, true)
                        jumpCooldown[player] = COOLDOWN_TICKS
                        sneakCooldown[player] = COOLDOWN_TICKS
                    }
                }

                if (isCrouching && isOnGround && block is ElevatorBlock) {
                    if ((sneakCooldown[player] ?: 0) <= 0) {
                        ElevatorBlock.takeElevator(player.level(), pos, player, false)
                        sneakCooldown[player] = COOLDOWN_TICKS
                        jumpCooldown[player] = COOLDOWN_TICKS
                    }
                }

                lastOnGround[player] = isOnGround
                lastCrouching[player] = isCrouching

                decrementCooldown(jumpCooldown, player)
                decrementCooldown(sneakCooldown, player)
            }
        }
    }

    private fun decrementCooldown(map: MutableMap<ServerPlayer, Int>, player: ServerPlayer) {
        val remaining = (map[player] ?: 0) - 1
        if (remaining <= 0) map.remove(player) else map[player] = remaining
    }
}
