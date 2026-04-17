package io.github.yuazer.twilightcloudmon.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import io.github.yuazer.twilightcloudmon.entity.FireworkTridentEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext

@Environment(EnvType.CLIENT)
class ModTridentRenderer(context: EntityRendererProvider.Context) : EntityRenderer<FireworkTridentEntity>(context) {

    companion object {
        private val FIREWORK_TRIDENT_TEXTURE: ResourceLocation =
            ResourceLocation.withDefaultNamespace("textures/entity/trident.png")
    }

    override fun getTextureLocation(entity: FireworkTridentEntity): ResourceLocation = FIREWORK_TRIDENT_TEXTURE

    override fun render(
        entity: FireworkTridentEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        renderFireworkTrident(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }
    
    private fun renderFireworkTrident(
        entity: FireworkTridentEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()

        val yaw = Mth.lerp(partialTicks, entity.yRotO, entity.yRot)
        val pitch = Mth.lerp(partialTicks, entity.xRotO, entity.xRot)
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw))
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch))
        //poseStack.mulPose(Axis.XP.rotationDegrees(180.0f))

        val shakeTime = entity.shakeTime.toFloat() - partialTicks
        if (shakeTime > 0.0f) {
            val shake = -Mth.sin(shakeTime * 3.0f) * shakeTime
            poseStack.mulPose(Axis.ZP.rotationDegrees(shake))
        }

        poseStack.scale(1.0f, 1.0f, 1.0f)

        val stack = entity.weaponItem

        Minecraft.getInstance().itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.GROUND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            entity.level(),
            entity.id
        )

        poseStack.popPose()
    }
}
