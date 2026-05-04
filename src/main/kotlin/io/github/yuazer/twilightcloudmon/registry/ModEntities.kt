package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.entity.FireworkTridentEntity
import io.github.yuazer.twilightcloudmon.entity.PrimalArrowEntity
import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object ModEntities {

    private const val DEFAULT_UPDATE_RATE = 20

    val PRIMAL_ARROW: EntityType<PrimalArrowEntity> = FabricEntityTypeBuilder
        .create(MobCategory.MISC, ::PrimalArrowEntity)
        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
        .trackRangeBlocks(4).trackedUpdateRate(DEFAULT_UPDATE_RATE)
        .build()

    val STATUE: EntityType<StatueEntity> = FabricEntityTypeBuilder
        .create(MobCategory.MISC, ::StatueEntity)
        .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
        .trackRangeBlocks(10).trackedUpdateRate(DEFAULT_UPDATE_RATE)
        .build()

    val FIREWORK_TRIDENT: EntityType<FireworkTridentEntity> = FabricEntityTypeBuilder
        .create(MobCategory.MISC, ::FireworkTridentEntity)
        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
        .trackRangeBlocks(4).trackedUpdateRate(DEFAULT_UPDATE_RATE)
        .build()

    fun register() {
        RegistryHelper.registerEntityType("primal_arrow", PRIMAL_ARROW)
        RegistryHelper.registerEntityType("statue", STATUE)
        RegistryHelper.registerEntityType("firework_trident", FIREWORK_TRIDENT)
    }
}
