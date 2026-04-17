package io.github.yuazer.twilightcloudmon.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.client.model.ModModelLayers
import io.github.yuazer.twilightcloudmon.client.model.PrimalArrowModel
import io.github.yuazer.twilightcloudmon.entity.PrimalArrowEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

@Environment(EnvType.CLIENT)
class PrimalArrowRenderer(context: EntityRendererProvider.Context) : EntityRenderer<PrimalArrowEntity>(context) {
    
    private val model: PrimalArrowModel = PrimalArrowModel(context.bakeLayer(ModModelLayers.PRIMAL_ARROW))
    
    companion object {
        val PRIMAL_ARROW_LOCATION = ResourceLocation.fromNamespaceAndPath(
            Twilightcloudmon.MOD_ID, 
            "textures/item/primal_arrow.png"
        )
    }

    override fun getTextureLocation(primalArrow: PrimalArrowEntity): ResourceLocation {
        return PRIMAL_ARROW_LOCATION
    }

    override fun render(
        entity: PrimalArrowEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.yRot)))
        poseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.xRot)))
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f))

        val shakeTime = entity.shakeTime.toFloat() - partialTicks
        if (shakeTime > 0.0f) {
            val shake = -Mth.sin(shakeTime * 3.0f) * shakeTime
            poseStack.mulPose(Axis.ZP.rotationDegrees(shake))
        }

        val vertexConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutout(getTextureLocation(entity)))
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY)
        
        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }
}