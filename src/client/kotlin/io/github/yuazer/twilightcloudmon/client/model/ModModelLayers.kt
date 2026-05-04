package io.github.yuazer.twilightcloudmon.client.model

import io.github.yuazer.twilightcloudmon.registry.RegistryHelper.id
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.geom.ModelLayerLocation

@Environment(EnvType.CLIENT)
object ModModelLayers {

    private fun layer(name: String) = ModelLayerLocation(id(name), "main")

    val PRIMAL_ARROW = layer("primal_arrow")
    val STATUE = layer("statue")
    val FIREWORK_CHEST = layer("firework_chest")
    val FIREWORK_CHEST_LEFT = layer("firework_chest_left")
    val FIREWORK_CHEST_RIGHT = layer("firework_chest_right")
}
