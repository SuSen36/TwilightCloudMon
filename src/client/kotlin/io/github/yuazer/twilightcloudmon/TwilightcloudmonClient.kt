package io.github.yuazer.twilightcloudmon

import io.github.yuazer.twilightcloudmon.client.model.ModModelLayers
import io.github.yuazer.twilightcloudmon.client.model.PrimalArrowModel
import io.github.yuazer.twilightcloudmon.client.renderer.entity.PrimalArrowRenderer
import io.github.yuazer.twilightcloudmon.client.renderer.entity.StatueRenderer
import io.github.yuazer.twilightcloudmon.client.renderer.entity.ModTridentRenderer
import io.github.yuazer.twilightcloudmon.client.screen.ChiselScreen
import io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket
import io.github.yuazer.twilightcloudmon.registry.ModEntities
import io.github.yuazer.twilightcloudmon.registry.ModBlocks
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
 

@Environment(EnvType.CLIENT)
class TwilightcloudmonClient : ClientModInitializer {
    
    override fun onInitializeClient() {
        // 注册模型层
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PRIMAL_ARROW, PrimalArrowModel::createBodyLayer)
        
        
        // 注册实体渲染器
        EntityRendererRegistry.register(ModEntities.PRIMAL_ARROW, ::PrimalArrowRenderer)
        EntityRendererRegistry.register(ModEntities.STATUE, ::StatueRenderer)
        EntityRendererRegistry.register(ModEntities.FIREWORK_TRIDENT, ::ModTridentRenderer)

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOVEMENT_PLATE, RenderType.cutout())
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FIREWORK_CHEST, RenderType.cutout())
        
        // 注册网络包处理器
        ClientPlayNetworking.registerGlobalReceiver(ChiselGuiPacket.OpenGuiPayload.TYPE) { payload, context ->
            Minecraft.getInstance().execute {
                Minecraft.getInstance().setScreen(ChiselScreen(payload.statueId))
            }
        }
    }
}
