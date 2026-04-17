package io.github.yuazer.twilightcloudmon.entity

import io.github.yuazer.twilightcloudmon.registry.ModEntities
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class PrimalArrowEntity : Arrow {
    
    constructor(entityType: EntityType<out Arrow>, level: Level) : super(entityType, level)

    override fun getDefaultPickupItem(): ItemStack {
        return ItemStack(net.minecraft.world.item.Items.ARROW)
    }

    override fun onHitEntity(result: EntityHitResult) {
        super.onHitEntity(result)
        val entity = result.entity
        if (entity is LivingEntity) {
            // 移除手动设置火焰效果，让 setEnchantmentEffectsFromEntity 处理火焰附魔
            // entity.setRemainingFireTicks(100)
        }
    }
}
