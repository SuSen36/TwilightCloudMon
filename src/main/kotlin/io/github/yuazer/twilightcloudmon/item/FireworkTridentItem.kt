package io.github.yuazer.twilightcloudmon.item

import io.github.yuazer.twilightcloudmon.entity.FireworkTridentEntity
import io.github.yuazer.twilightcloudmon.registry.ModEntities
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TridentItem
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level

class FireworkTridentItem(properties: Properties) : TridentItem(properties) {

    companion object {
        private const val MIN_CHARGE_TICKS = 10
        private const val BASE_VELOCITY = 2.5f
        private const val RIPTIDE_VELOCITY_BONUS = 0.5f
        private const val INACCURACY = 1.0f
    }

    override fun releaseUsing(stack: ItemStack, level: Level, user: LivingEntity, timeLeft: Int) {
        if (user !is Player) {
            super.releaseUsing(stack, level, user, timeLeft)
            return
        }

        val charge = getUseDuration(stack, user) - timeLeft
        if (charge < MIN_CHARGE_TICKS) return

        val riptide = EnchantmentHelper.getItemEnchantmentLevel(
            level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.RIPTIDE),
            stack
        )
        if (riptide > 0 && user.isInWaterOrRain) {
            super.releaseUsing(stack, level, user, timeLeft)
            return
        }

        if (!level.isClientSide) {
            stack.hurtAndBreak(1, user, LivingEntity.getSlotForHand(user.usedItemHand))

            val tridentEntity = FireworkTridentEntity(ModEntities.FIREWORK_TRIDENT, level, user, stack)
            tridentEntity.shootFromRotation(
                user, user.xRot, user.yRot, 0.0f,
                BASE_VELOCITY + riptide.toFloat() * RIPTIDE_VELOCITY_BONUS,
                INACCURACY
            )

            if (user.abilities.instabuild) {
                tridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY
            }

            level.addFreshEntity(tridentEntity)
            level.playSound(
                null, tridentEntity.x, tridentEntity.y, tridentEntity.z,
                SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS
            )

            if (!user.abilities.instabuild) {
                user.inventory.removeItem(stack)
            }
        }

        user.awardStat(Stats.ITEM_USED[this])
    }
}
