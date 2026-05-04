package io.github.yuazer.twilightcloudmon.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level

class PrimalArrowEntity(
    entityType: EntityType<out Arrow>,
    level: Level
) : Arrow(entityType, level) {

    override fun getDefaultPickupItem(): ItemStack = ItemStack(Items.ARROW)
}
