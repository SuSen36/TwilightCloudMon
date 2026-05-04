package io.github.yuazer.twilightcloudmon.item

import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket
import io.github.yuazer.twilightcloudmon.registry.ModEntities
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.AABB

class ChiselItem(properties: Properties) : Item(properties) {

    companion object {
        private const val STATUE_SEARCH_INFLATE = 0.5
        private const val BLOCK_CENTER_OFFSET = 0.5
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player ?: return InteractionResult.PASS
        if (level.isClientSide) return InteractionResult.SUCCESS

        val placePos = BlockPos.containing(context.clickLocation).relative(context.clickedFace)

        val existingStatue = level.getEntitiesOfClass(
            StatueEntity::class.java,
            AABB(placePos).inflate(STATUE_SEARCH_INFLATE)
        ).firstOrNull()

        if (existingStatue != null) {
            ChiselGuiPacket.sendOpenGui(player as ServerPlayer, existingStatue.id)
            return InteractionResult.SUCCESS
        }

        val statue = ModEntities.STATUE.create(level) ?: return InteractionResult.FAIL
        statue.moveTo(
            placePos.x + BLOCK_CENTER_OFFSET,
            placePos.y.toDouble(),
            placePos.z + BLOCK_CENTER_OFFSET,
            player.yRot,
            0f
        )

        if (level.addFreshEntity(statue)) {
            ChiselGuiPacket.sendOpenGui(player as ServerPlayer, statue.id)
            return InteractionResult.SUCCESS
        }

        return InteractionResult.FAIL
    }
}
