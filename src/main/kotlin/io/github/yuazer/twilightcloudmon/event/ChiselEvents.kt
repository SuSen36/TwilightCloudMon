package io.github.yuazer.twilightcloudmon.event

import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.item.ChiselItem
import io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity

object ChiselEvents {

    fun register() {
        UseEntityCallback.EVENT.register { player, _, hand, entity, _ ->
            if (player is ServerPlayer && entity is StatueEntity && player.getItemInHand(hand).item is ChiselItem) {
                if (player.isShiftKeyDown) {
                    entity.remove(Entity.RemovalReason.DISCARDED)
                } else {
                    ChiselGuiPacket.sendOpenGui(player, entity)
                }
                return@register InteractionResult.SUCCESS
            }
            InteractionResult.PASS
        }
    }
}
