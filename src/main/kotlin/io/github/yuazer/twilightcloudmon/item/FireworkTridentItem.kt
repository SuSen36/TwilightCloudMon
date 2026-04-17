package io.github.yuazer.twilightcloudmon.item

import io.github.yuazer.twilightcloudmon.entity.FireworkTridentEntity
import io.github.yuazer.twilightcloudmon.registry.ModEntities
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent
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

    override fun releaseUsing(stack: ItemStack, level: Level, user: LivingEntity, timeLeft: Int) {
        if (user !is Player) {
            super.releaseUsing(stack, level, user, timeLeft)
            return
        }

        val charge = getUseDuration(stack, user) - timeLeft
        if (charge < 10) return

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
                user,
                user.xRot,
                user.yRot,
                0.0f,
                2.5f + riptide.toFloat() * 0.5f,
                1.0f
            )

            if (user.abilities.instabuild) {
                tridentEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY
            }

            level.addFreshEntity(tridentEntity)

            val throwSound: SoundEvent = when (val v = SoundEvents.TRIDENT_THROW as Any) {
                is SoundEvent -> v
                is Holder<*> -> v.value() as SoundEvent
                else -> SoundEvents.TRIDENT_THROW as SoundEvent
            }

            level.playSound(null, tridentEntity.x, tridentEntity.y, tridentEntity.z, throwSound, SoundSource.PLAYERS)

            if (!user.abilities.instabuild) {
                user.inventory.removeItem(stack)
            }
        }

        user.awardStat(Stats.ITEM_USED[this])
    }
}
