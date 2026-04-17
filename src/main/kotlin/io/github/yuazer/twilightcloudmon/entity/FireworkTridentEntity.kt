package io.github.yuazer.twilightcloudmon.entity

import io.github.yuazer.twilightcloudmon.registry.ModFireworkWeapons
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrownTrident
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class FireworkTridentEntity(
    entityType: EntityType<out FireworkTridentEntity>,
    level: Level
) : ThrownTrident(entityType, level) {

    private var weaponItemStack: ItemStack = ItemStack(ModFireworkWeapons.FIREWORK_TRIDENT)

    constructor(
        entityType: EntityType<out FireworkTridentEntity>,
        level: Level,
        owner: LivingEntity,
        weapon: ItemStack
    ) : this(entityType, level) {
        setOwner(owner)
        setPos(owner.x, owner.eyeY - 0.1, owner.z)
        weaponItemStack = weapon.copy()
    }

    override fun getWeaponItem(): ItemStack = weaponItemStack

    override fun getPickupItem(): ItemStack = weaponItemStack.copy()
}

