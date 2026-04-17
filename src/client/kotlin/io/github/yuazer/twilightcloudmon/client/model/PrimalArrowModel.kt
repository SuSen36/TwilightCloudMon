package io.github.yuazer.twilightcloudmon.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.model.geom.builders.PartDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.entity.Entity

@Environment(EnvType.CLIENT)
class PrimalArrowModel(root: ModelPart) : EntityModel<Entity>() {
    private val jian: ModelPart = root.getChild("jian")

    companion object {
        fun createBodyLayer(): LayerDefinition {
            val modelData = MeshDefinition()
            val modelPartData = modelData.root

            val jian = modelPartData.addOrReplaceChild(
                "jian",
                CubeListBuilder.create()
                    .texOffs(0, -26).addBox(0.0f, -1.75f, -15.0f, 0.0f, 3.5f, 30.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            jian.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create()
                    .texOffs(0, -26).addBox(0.0f, -1.75f, -15.0f, 0.0f, 3.5f, 30.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.5708f)
            )

            return LayerDefinition.create(modelData, 32, 32)
        }
    }

    override fun setupAnim(
        entity: Entity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        i: Int,
        j: Int,
        k: Int
    ) {
        jian.render(poseStack, vertexConsumer, i, j, k)

    }
}
