package io.github.yuazer.twilightcloudmon.client.model

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.resources.ResourceLocation

@Environment(EnvType.CLIENT)
object ModModelLayers {
    val PRIMAL_ARROW = ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "primal_arrow"),
        "main"
    )
    val STATUE = ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "statue"),
        "main"
    )

    val FIREWORK_CHEST = ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "firework_chest"),
        "main"
    )
    val FIREWORK_CHEST_LEFT = ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "firework_chest_left"),
        "main"
    )
    val FIREWORK_CHEST_RIGHT = ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "firework_chest_right"),
        "main"
    )
}
