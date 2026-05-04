package io.github.yuazer.twilightcloudmon.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*

class StatueEntity(
    entityType: EntityType<out StatueEntity>,
    level: Level
) : LivingEntity(entityType, level) {

    companion object {
        private val DATA_POKEMON_NAME = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_SIZE = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_ANIMATION = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_IS_ANIMATED = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.BOOLEAN)
        private val DATA_TEXT = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_MATERIAL = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_FORM = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_EXTRA_MATERIAL = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_COLLISION_TYPE = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_MOVABLE = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.BOOLEAN)
        private val DATA_GENDER = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.STRING)
        private val DATA_IS_STATIC = SynchedEntityData.defineId(StatueEntity::class.java, EntityDataSerializers.BOOLEAN)

        fun createAttributes(): AttributeSupplier.Builder {
            return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
        }
    }

    var pokemonName: String get() = entityData.get(DATA_POKEMON_NAME); set(v) = entityData.set(DATA_POKEMON_NAME, v)
    var size: String get() = entityData.get(DATA_SIZE); set(v) = entityData.set(DATA_SIZE, v)
    var animation: String get() = entityData.get(DATA_ANIMATION); set(v) = entityData.set(DATA_ANIMATION, v)
    var isAnimated: Boolean get() = entityData.get(DATA_IS_ANIMATED); set(v) = entityData.set(DATA_IS_ANIMATED, v)
    var text: String get() = entityData.get(DATA_TEXT); set(v) = entityData.set(DATA_TEXT, v)
    var material: String get() = entityData.get(DATA_MATERIAL); set(v) = entityData.set(DATA_MATERIAL, v)
    var form: String get() = entityData.get(DATA_FORM); set(v) = entityData.set(DATA_FORM, v)
    var extraMaterial: String get() = entityData.get(DATA_EXTRA_MATERIAL); set(v) = entityData.set(DATA_EXTRA_MATERIAL, v)
    var collisionType: String get() = entityData.get(DATA_COLLISION_TYPE); set(v) = entityData.set(DATA_COLLISION_TYPE, v)
    var movable: Boolean get() = entityData.get(DATA_MOVABLE); set(v) = entityData.set(DATA_MOVABLE, v)
    var gender: String get() = entityData.get(DATA_GENDER); set(v) = entityData.set(DATA_GENDER, v)
    var isStatic: Boolean get() = entityData.get(DATA_IS_STATIC); set(v) = entityData.set(DATA_IS_STATIC, v)

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_POKEMON_NAME, "pikachu")
        builder.define(DATA_SIZE, "normal")
        builder.define(DATA_ANIMATION, "idle")
        builder.define(DATA_IS_ANIMATED, true)
        builder.define(DATA_TEXT, "")
        builder.define(DATA_MATERIAL, "none")
        builder.define(DATA_FORM, "default")
        builder.define(DATA_EXTRA_MATERIAL, "none")
        builder.define(DATA_COLLISION_TYPE, "NONE")
        builder.define(DATA_MOVABLE, false)
        builder.define(DATA_GENDER, "default")
        builder.define(DATA_IS_STATIC, false)
    }

    override fun move(moverType: MoverType, movement: Vec3) {
        if (isStatic) return
        if (!movable && moverType != MoverType.SELF) return

        super.move(moverType, movement)
    }

    override fun travel(travelVector: Vec3) {
        if (isStatic) return
        if (this.isEffectiveAi || this.isControlledByLocalInstance) {
            super.travel(travelVector)
        }
    }

    override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT
    override fun getArmorSlots(): MutableIterable<ItemStack> = Collections.emptyList()
    override fun getItemBySlot(slot: EquipmentSlot): ItemStack = ItemStack.EMPTY
    override fun setItemSlot(slot: EquipmentSlot, stack: ItemStack) {}

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putString("pokemonName", pokemonName)
        tag.putString("size", size)
        tag.putString("animation", animation)
        tag.putBoolean("isAnimated", isAnimated)
        tag.putString("text", text)
        tag.putString("material", material)
        tag.putString("form", form)
        tag.putString("extraMaterial", extraMaterial)
        tag.putString("collisionType", collisionType)
        tag.putBoolean("movable", movable)
        tag.putString("gender", gender)
        tag.putBoolean("isStatic", isStatic)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        pokemonName = tag.getString("pokemonName").ifEmpty { "pikachu" }
        size = tag.getString("size").ifEmpty { "normal" }
        animation = tag.getString("animation").ifEmpty { "idle" }
        isAnimated = if (tag.contains("isAnimated")) tag.getBoolean("isAnimated") else true
        text = tag.getString("text")
        material = tag.getString("material").ifEmpty { "none" }
        form = tag.getString("form").ifEmpty { "default" }
        extraMaterial = tag.getString("extraMaterial").ifEmpty { "none" }
        collisionType = tag.getString("collisionType").ifEmpty { "NONE" }
        movable = tag.getBoolean("movable")
        gender = tag.getString("gender").ifEmpty { "default" }
        isStatic = tag.getBoolean("isStatic")
    }

    override fun isPickable(): Boolean = true
    override fun isPushable(): Boolean = movable && !isStatic

    override fun getDisplayName(): Component {
        return if (text.isNotEmpty()) Component.literal(text) else Component.literal("Statue: $pokemonName")
    }
}