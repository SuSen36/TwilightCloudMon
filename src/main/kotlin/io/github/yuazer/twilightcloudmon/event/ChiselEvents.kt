package io.github.yuazer.twilightcloudmon.event

import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.item.ChiselItem
import io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player

object ChiselEvents {
    
    fun register() {
        UseEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
            if (player is ServerPlayer && entity is StatueEntity) {
                val stack = player.getItemInHand(hand)
                if (stack.item is ChiselItem) {
                    // 下蹲时右键移除雕塑，否则打开GUI
                    if (player.isShiftKeyDown) {
                        entity.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED)
                        return@register InteractionResult.SUCCESS
                    } else {
                        // 右键打开GUI
                        ChiselGuiPacket.sendOpenGui(player, entity.id)
                        return@register InteractionResult.SUCCESS
                    }
                }
            }
            InteractionResult.PASS
        }
    }
}

