package io.github.yuazer.twilightcloudmon.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import io.github.yuazer.twilightcloudmon.block.entity.FireworkChestBlockEntity
import io.github.yuazer.twilightcloudmon.client.model.ModModelLayers
import it.unimi.dsi.fastutil.ints.Int2IntFunction
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BrightnessCombiner
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.AbstractChestBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.DoubleBlockCombiner
import net.minecraft.world.level.block.entity.LidBlockEntity
import net.minecraft.world.level.block.state.properties.ChestType

@Environment(EnvType.CLIENT)
class FireworkChestBlockEntityRenderer(context: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<FireworkChestBlockEntity> {

    private val lid: ModelPart
    private val bottom: ModelPart
    private val lock: ModelPart
    private val doubleLeftLid: ModelPart
    private val doubleLeftBottom: ModelPart
    private val doubleLeftLock: ModelPart
    private val doubleRightLid: ModelPart
    private val doubleRightBottom: ModelPart
    private val doubleRightLock: ModelPart


    init {
        // 使用自定义的烟花箱子模型层，而不是原版箱子模型层
        val modelPart = context.bakeLayer(ModModelLayers.FIREWORK_CHEST)
        bottom = modelPart.getChild("bottom")
        lid = modelPart.getChild("lid")
        lock = modelPart.getChild("lock")
        
        val modelPart2 = context.bakeLayer(ModModelLayers.FIREWORK_CHEST_LEFT)
        doubleLeftBottom = modelPart2.getChild("bottom")
        doubleLeftLid = modelPart2.getChild("lid")
        doubleLeftLock = modelPart2.getChild("lock")
        
        val modelPart3 = context.bakeLayer(ModModelLayers.FIREWORK_CHEST_RIGHT)
        doubleRightBottom = modelPart3.getChild("bottom")
        doubleRightLid = modelPart3.getChild("lid")
        doubleRightLock = modelPart3.getChild("lock")
    }

    override fun render(
        blockEntity: FireworkChestBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val level = blockEntity.level
        val hasLevel = level != null
        val blockState = if (hasLevel) {
            blockEntity.blockState
        } else {
            Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH)
        }
        
        val chestType = if (blockState.hasProperty(ChestBlock.TYPE)) {
            blockState.getValue(ChestBlock.TYPE)
        } else {
            ChestType.SINGLE
        }
        
        val block = blockState.block
        if (block is AbstractChestBlock<*>) {
            val isDouble = chestType != ChestType.SINGLE
            poseStack.pushPose()
            
            val facing = blockState.getValue(ChestBlock.FACING)
            val rotation = facing.toYRot()
            poseStack.translate(0.5f, 0.5f, 0.5f)
            poseStack.mulPose(Axis.YP.rotationDegrees(-rotation))
            poseStack.translate(-0.5f, -0.5f, -0.5f)
            
            val neighborCombineResult: DoubleBlockCombiner.NeighborCombineResult<net.minecraft.world.level.block.entity.ChestBlockEntity> =
                if (hasLevel) {
                    @Suppress("UNCHECKED_CAST")
                block.combine(blockState, level, blockEntity.blockPos, true)
                        as DoubleBlockCombiner.NeighborCombineResult<net.minecraft.world.level.block.entity.ChestBlockEntity>
            } else {
                     DoubleBlockCombiner.NeighborCombineResult.Single(blockEntity)
            }

            val openness = neighborCombineResult.apply(ChestBlock.opennessCombiner(blockEntity)).get(partialTick)
            var lidAngle = 1.0f - openness
            lidAngle = 1.0f - lidAngle * lidAngle * lidAngle

            val brightness = neighborCombineResult.apply(BrightnessCombiner()).applyAsInt(packedLight)
            
            // 不使用 Sheets.CHEST_SHEET 图集（会导致自定义贴图未被 stitch 时变紫黑）
            val texture = when (chestType) {
                ChestType.LEFT -> LEFT_CHEST_TEXTURE
                ChestType.RIGHT -> RIGHT_CHEST_TEXTURE
                else -> SINGLE_CHEST_TEXTURE
            }
            val vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture))
            
            if (isDouble) {
                if (chestType == ChestType.LEFT) {
                    render(poseStack, vertexConsumer, doubleLeftLid, doubleLeftLock, doubleLeftBottom, lidAngle, brightness, packedOverlay)
                } else {
                    render(poseStack, vertexConsumer, doubleRightLid, doubleRightLock, doubleRightBottom, lidAngle, brightness, packedOverlay)
                }
            } else {
                render(poseStack, vertexConsumer, lid, lock, bottom, lidAngle, brightness, packedOverlay)
            }

            poseStack.popPose()
        }
    }

    private fun render(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        lidPart: ModelPart,
        lockPart: ModelPart,
        bottomPart: ModelPart,
        lidAngle: Float,
        packedLight: Int,
        packedOverlay: Int
    ) {
        lidPart.xRot = -(lidAngle * 1.5707964f)
        lockPart.xRot = lidPart.xRot
        lidPart.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        lockPart.render(poseStack, vertexConsumer, packedLight, packedOverlay)
        bottomPart.render(poseStack, vertexConsumer, packedLight, packedOverlay)
    }

    companion object {
        private val SINGLE_CHEST_TEXTURE = ResourceLocation.fromNamespaceAndPath("twilightcloudmon", "textures/entity/chest/firework_chest.png")
        private val LEFT_CHEST_TEXTURE = ResourceLocation.fromNamespaceAndPath("twilightcloudmon", "textures/entity/chest/firework_chest_left.png")
        private val RIGHT_CHEST_TEXTURE = ResourceLocation.fromNamespaceAndPath("twilightcloudmon", "textures/entity/chest/firework_chest_right.png")
        
        // 将 Blockbench 导出的 firework_chest 模型转换为 LayerDefinition。
        // 注意：这里的几何在 bottom 部分，lid/lock 为空，盖子开合动画将不会影响模型。
        private fun createModelLayer(): LayerDefinition {
            val mesh = MeshDefinition()
            val root = mesh.root

            val bottom = root.addOrReplaceChild(
                "bottom",
                CubeListBuilder.create()
                    .texOffs(9, 0).addBox(-13.5f, -7.0f, 2.5f, 11.0f, 1.0f, 11.0f)
                    .texOffs(0, 5).addBox(-12.0f, -10.5f, 3.5f, 8.0f, 3.5f, 9.0f)
                    .texOffs(13, 9).mirror().addBox(-13.0f, -1.5f, 3.0f, 10.0f, 1.0f, 10.0f).mirror(false)
                    .texOffs(18, 0).addBox(-13.0f, -6.0f, 3.0f, 10.0f, 1.0f, 10.0f)
                    .texOffs(4, 0).mirror().addBox(-12.5f, -5.0f, 3.5f, 9.0f, 3.5f, 9.0f).mirror(false)
                    .texOffs(0, 19).addBox(-4.0f, -1.0f, 2.5f, 1.5f, 1.0f, 1.5f)
                    .texOffs(1, 0).mirror().addBox(-4.5f, -5.0f, 3.0f, 1.5f, 3.5f, 1.5f).mirror(false)
                    .texOffs(1, 19).addBox(-5.0f, -2.0f, 2.5f, 1.0f, 2.0f, 1.5f)
                    .texOffs(4, 19).addBox(-4.0f, -2.0f, 4.0f, 1.5f, 2.0f, 1.0f)
                    .texOffs(1, 19).mirror().addBox(-12.0f, -2.0f, 2.5f, 1.0f, 2.0f, 1.5f).mirror(false)
                    .texOffs(0, 19).mirror().addBox(-13.5f, -1.0f, 2.5f, 1.5f, 1.0f, 1.5f).mirror(false)
                    .texOffs(4, 19).mirror().addBox(-13.5f, -2.0f, 4.0f, 1.5f, 2.0f, 1.0f).mirror(false)
                    .texOffs(1, 0).addBox(-13.0f, -5.0f, 3.0f, 1.5f, 3.5f, 1.5f)
                    .texOffs(2, 20).mirror().addBox(-12.0f, -2.0f, 12.0f, 1.0f, 2.0f, 1.5f).mirror(false)
                    .texOffs(0, 20).mirror().addBox(-13.5f, -1.0f, 12.0f, 1.5f, 1.0f, 1.5f).mirror(false)
                    .texOffs(5, 20).mirror().addBox(-13.5f, -2.0f, 11.0f, 1.5f, 2.0f, 1.0f).mirror(false)
                    .texOffs(0, 0).addBox(-13.0f, -5.0f, 11.5f, 1.5f, 3.5f, 1.5f)
                    .texOffs(2, 20).addBox(-5.0f, -2.0f, 12.0f, 1.0f, 2.0f, 1.5f)
                    .texOffs(0, 20).addBox(-4.0f, -1.0f, 12.0f, 1.5f, 1.0f, 1.5f)
                    .texOffs(5, 20).addBox(-4.0f, -2.0f, 11.0f, 1.5f, 2.0f, 1.0f)
                    .texOffs(0, 0).mirror().addBox(-4.5f, -5.0f, 11.5f, 1.5f, 3.5f, 1.5f).mirror(false)
                    .texOffs(0, 0).addBox(-14.75f, -10.25f, 2.0f, 13.5f, 5.0f, 0.0f)
                    .texOffs(14, 0).addBox(-13.765f, -9.5f, 3.5f, 11.53f, 9.0f, 9.0f)
                    .texOffs(0, 0).addBox(-12.5f, -9.5f, 2.235f, 9.0f, 9.0f, 11.53f),
                PartPose.offset(16.0f, 8.0f, 0.0f)
            )

            bottom.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create()
                    .texOffs(18, 18).addBox(-1.5f, -2.0f, -1.0f, 3.0f, 3.0f, 2.5f),
                PartPose.offsetAndRotation(-8.0f, -3.5f, 3.0f, -0.3927f, 0.0f, 0.0f)
            )
            bottom.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create()
                    .texOffs(0, 14).mirror().addBox(-1.5f, -2.0f, -5.25f, 3.0f, 5.0f, 10.5f).mirror(false),
                PartPose.offsetAndRotation(-12.5f, -9.0f, 8.0f, 0.0f, 0.0f, -0.3927f)
            )
            bottom.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create()
                    .texOffs(16, 13).addBox(-14.0f, -4.0f, 0.0f, 14.0f, 4.0f, 4.0f),
                PartPose.offsetAndRotation(-1.0f, -6.0f, 8.0f, 0.7854f, 0.0f, 0.0f)
            )
            bottom.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create()
                    .texOffs(0, 14).addBox(-1.5f, -2.0f, -5.25f, 3.0f, 5.0f, 10.5f),
                PartPose.offsetAndRotation(-3.5f, -9.0f, 8.0f, 0.0f, 0.0f, 0.3927f)
            )

            // lid/lock 保留空壳，避免渲染器崩溃；动画不会影响模型。
            root.addOrReplaceChild("lid", CubeListBuilder.create(), PartPose.ZERO)
            root.addOrReplaceChild("lock", CubeListBuilder.create(), PartPose.ZERO)

            return LayerDefinition.create(mesh, 64, 64)
        }

        fun createSingleBodyLayer(): LayerDefinition = createModelLayer()
        fun createDoubleBodyLeftLayer(): LayerDefinition = createModelLayer()
        fun createDoubleBodyRightLayer(): LayerDefinition = createModelLayer()
    }
}
