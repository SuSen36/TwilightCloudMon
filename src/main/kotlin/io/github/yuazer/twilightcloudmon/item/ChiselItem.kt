package io.github.yuazer.twilightcloudmon.item

import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.registry.ModEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

class ChiselItem(properties: Properties) : Item(properties) {
    
    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player ?: return InteractionResult.PASS
        val hand = context.hand
        val hitPos = context.clickLocation
        val direction = context.clickedFace
        
        // 只在服务端处理
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        
        // 计算放置位置（在点击的方块上方）
        val placePos = BlockPos.containing(hitPos).relative(direction)
        
        // 检查该位置是否已有雕塑实体
        val existingStatue = level.getEntitiesOfClass(
            StatueEntity::class.java,
            AABB(placePos).inflate(0.5)
        ).firstOrNull()
        
        if (existingStatue != null) {
            // 如果已有雕塑，打开GUI
            openChiselGui(player as ServerPlayer, existingStatue)
            return InteractionResult.SUCCESS
        }
        
        // 创建新的雕塑实体
        val statue = ModEntities.STATUE.create(level) ?: return InteractionResult.FAIL
        statue.moveTo(
            placePos.x + 0.5,
            placePos.y.toDouble(),
            placePos.z + 0.5,
            player.yRot,
            0f
        )
        
        if (level.addFreshEntity(statue)) {
            // 打开GUI
            openChiselGui(player as ServerPlayer, statue)
            return InteractionResult.SUCCESS
        }
        
        return InteractionResult.FAIL
    }
    
    private fun openChiselGui(player: ServerPlayer, statue: StatueEntity) {
        // 通过网络包打开客户端GUI
        io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket.sendOpenGui(player, statue.id)
    }
}

