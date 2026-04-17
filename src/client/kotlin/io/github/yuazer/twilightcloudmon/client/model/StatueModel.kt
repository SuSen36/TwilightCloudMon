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
import net.minecraft.world.entity.Entity

@Environment(EnvType.CLIENT)
class StatueModel(root: ModelPart) : EntityModel<Entity>() {
    private val body: ModelPart = root.getChild("body")

    companion object {
        fun createBodyLayer(): LayerDefinition {
            val mesh = MeshDefinition()
            val root = mesh.root

            val body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 12.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                    .texOffs(0, 16)
                    .addBox(-3.0f, -8.0f, -3.0f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(mesh, 64, 64)
        }
    }

    override fun setupAnim(entity: Entity, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float) {
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        i: Int,
        j: Int,
        k: Int
    ) {
        body.render(poseStack, vertexConsumer, i, j, k)
    }
}

