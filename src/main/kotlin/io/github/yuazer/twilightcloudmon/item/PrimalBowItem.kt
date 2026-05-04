package io.github.yuazer.twilightcloudmon.item

import io.github.yuazer.twilightcloudmon.registry.ModEntities
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.BowItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level

class PrimalBowItem(properties: Properties) : BowItem(properties) {

    companion object {
        private const val BASE_DAMAGE = 2.0
        private const val EYE_HEIGHT_OFFSET = 0.1
        private const val FLAME_FIRE_TICKS = 100
        private const val POWER_DAMAGE_PER_LEVEL = 0.5
        private const val POWER_DAMAGE_BONUS = 0.5
    }

    override fun createProjectile(
        level: Level,
        shooter: LivingEntity,
        weapon: ItemStack,
        ammo: ItemStack,
        critical: Boolean
    ): Projectile {
        val enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
        val primalArrow = ModEntities.PRIMAL_ARROW.create(level)!!.apply {
            setOwner(shooter)
            setPos(shooter.x, shooter.eyeY - EYE_HEIGHT_OFFSET, shooter.z)
            setBaseDamage(BASE_DAMAGE)
            if (critical) setCritArrow(true)
        }

        val flameLevel = EnchantmentHelper.getItemEnchantmentLevel(
            enchantmentRegistry.getHolderOrThrow(Enchantments.FLAME), weapon
        )
        if (flameLevel > 0) {
            primalArrow.remainingFireTicks = FLAME_FIRE_TICKS
        }

        val powerLevel = EnchantmentHelper.getItemEnchantmentLevel(
            enchantmentRegistry.getHolderOrThrow(Enchantments.POWER), weapon
        )
        if (powerLevel > 0) {
            primalArrow.setBaseDamage(primalArrow.baseDamage + powerLevel * POWER_DAMAGE_PER_LEVEL + POWER_DAMAGE_BONUS)
        }

        return primalArrow
    }
}
