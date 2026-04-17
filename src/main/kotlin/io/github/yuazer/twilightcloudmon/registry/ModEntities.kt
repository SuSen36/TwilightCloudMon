package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.entity.FireworkTridentEntity
import io.github.yuazer.twilightcloudmon.entity.PrimalArrowEntity
import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object ModEntities {
    private val registered = mutableListOf<ResourceLocation>()

    // 始源之箭实体
    val PRIMAL_ARROW: EntityType<PrimalArrowEntity> = FabricEntityTypeBuilder.create(MobCategory.MISC) { entityType, level ->
        PrimalArrowEntity(entityType, level)
    }
        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
        .trackRangeBlocks(4)
        .trackedUpdateRate(20)
        .build()

    // 雕塑实体
    val STATUE: EntityType<StatueEntity> = FabricEntityTypeBuilder.create(MobCategory.MISC) { entityType, level ->
        StatueEntity(entityType, level)
    }
        .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
        .trackRangeBlocks(10)
        .trackedUpdateRate(20)
        .build()

    val FIREWORK_TRIDENT: EntityType<FireworkTridentEntity> = FabricEntityTypeBuilder.create(MobCategory.MISC) { entityType, level ->
        FireworkTridentEntity(entityType, level)
    }
        .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
        .trackRangeBlocks(4)
        .trackedUpdateRate(20)
        .build()

    fun register() {
        Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "primal_arrow"),
            PRIMAL_ARROW
        )
        
        Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "statue"),
            STATUE
        )

        Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "firework_trident"),
            FIREWORK_TRIDENT
        )
        
        registered += ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "primal_arrow")
        registered += ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "statue")
        registered += ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "firework_trident")
    }
}
