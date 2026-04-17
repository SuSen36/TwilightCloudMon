package io.github.yuazer.twilightcloudmon.item

import io.github.yuazer.twilightcloudmon.registry.ModEntities
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.BowItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level

import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments

class PrimalBowItem(properties: Properties) : BowItem(properties) {

    override fun createProjectile(level: Level, shooter: LivingEntity, weapon: ItemStack, ammo: ItemStack, critical: Boolean): Projectile {
        val primalArrow = ModEntities.PRIMAL_ARROW.create(level)!!
        primalArrow.setOwner(shooter)
        primalArrow.setPos(shooter.x, shooter.eyeY - 0.1, shooter.z)

        primalArrow.setBaseDamage(2.0)
        if (critical) {
            primalArrow.setCritArrow(true)
        }

        // 处理火焰附魔
        val flameLevel = EnchantmentHelper.getItemEnchantmentLevel(
            level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.FLAME),
            weapon
        )
        if (flameLevel > 0) {
            primalArrow.remainingFireTicks = 100
        }

        // 处理力量附魔
        val powerLevel = EnchantmentHelper.getItemEnchantmentLevel(
            level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.POWER),
            weapon
        )
        if (powerLevel > 0 && primalArrow is AbstractArrow) {
            primalArrow.setBaseDamage(primalArrow.baseDamage + powerLevel.toDouble() * 0.5 + 0.5)
        }


        return primalArrow
    }
}
