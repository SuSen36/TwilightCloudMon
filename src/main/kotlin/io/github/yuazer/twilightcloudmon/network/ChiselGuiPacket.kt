package io.github.yuazer.twilightcloudmon.network

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

object ChiselGuiPacket {
    val OPEN_GUI_ID = ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "open_chisel_gui")
    val UPDATE_STATUE_ID = ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "update_statue")
    
    data class OpenGuiPayload(val statueId: Int) : CustomPacketPayload {
        companion object {
            val TYPE = CustomPacketPayload.Type<OpenGuiPayload>(OPEN_GUI_ID)
            val CODEC: StreamCodec<FriendlyByteBuf, OpenGuiPayload> = StreamCodec.of(
                { buf, payload -> buf.writeVarInt(payload.statueId) },
                { buf -> OpenGuiPayload(buf.readVarInt()) }
            )
        }
        
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
    }
    
    data class UpdateStatuePayload(
        val statueId: Int,
        val pokemonName: String,
        val size: String,
        val animation: String,
        val isAnimated: Boolean,
        val text: String,
        val material: String,
        val form: String,
        val extraMaterial: String,
        val collisionType: String,
        val movable: Boolean,
        val gender: String,
        val isStatic: Boolean
    ) : CustomPacketPayload {
        companion object {
            val TYPE = CustomPacketPayload.Type<UpdateStatuePayload>(UPDATE_STATUE_ID)
            val CODEC: StreamCodec<FriendlyByteBuf, UpdateStatuePayload> = StreamCodec.of(
                { buf, payload ->
                    buf.writeVarInt(payload.statueId)
                    buf.writeUtf(payload.pokemonName)
                    buf.writeUtf(payload.size)
                    buf.writeUtf(payload.animation)
                    buf.writeBoolean(payload.isAnimated)
                    buf.writeUtf(payload.text)
                    buf.writeUtf(payload.material)
                    buf.writeUtf(payload.form)
                    buf.writeUtf(payload.extraMaterial)
                    buf.writeUtf(payload.collisionType)
                    buf.writeBoolean(payload.movable)
                    buf.writeUtf(payload.gender)
                    buf.writeBoolean(payload.isStatic)
                },
                { buf ->
                    UpdateStatuePayload(
                        statueId = buf.readVarInt(),
                        pokemonName = buf.readUtf(),
                        size = buf.readUtf(),
                        animation = buf.readUtf(),
                        isAnimated = buf.readBoolean(),
                        text = buf.readUtf(),
                        material = buf.readUtf(),
                        form = buf.readUtf(),
                        extraMaterial = buf.readUtf(),
                        collisionType = buf.readUtf(),
                        movable = buf.readBoolean(),
                        gender = buf.readUtf(),
                        isStatic = buf.readBoolean()
                    )
                }
            )
        }
        
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
    }
    
    fun register() {
        // 客户端打开界面使用的包（S2C）
        PayloadTypeRegistry.playS2C().register(OpenGuiPayload.TYPE, OpenGuiPayload.CODEC)
        // 雕塑属性更新包（双向注册以便复用）
        PayloadTypeRegistry.playC2S().register(UpdateStatuePayload.TYPE, UpdateStatuePayload.CODEC)
        PayloadTypeRegistry.playS2C().register(UpdateStatuePayload.TYPE, UpdateStatuePayload.CODEC)

        // 服务器处理客户端提交的修改
        ServerPlayNetworking.registerGlobalReceiver(UpdateStatuePayload.TYPE) { payload, context ->
            val player = context.player()
            player.server.execute {
                val level = player.level()
                val entity = level.getEntity(payload.statueId)
                if (entity is StatueEntity) {
                    entity.pokemonName = payload.pokemonName
                    entity.size = payload.size
                    entity.animation = payload.animation
                    entity.isAnimated = payload.isAnimated
                    entity.text = payload.text
                    entity.material = payload.material
                    entity.form = payload.form
                    entity.extraMaterial = payload.extraMaterial
                    entity.collisionType = payload.collisionType
                    entity.movable = payload.movable
                    entity.gender = payload.gender
                    entity.isStatic = payload.isStatic
                }
            }
        }
    }
    
    fun sendOpenGui(player: ServerPlayer, statueId: Int) {
        ServerPlayNetworking.send(player, OpenGuiPayload(statueId))
    }
    
    fun sendUpdateStatue(player: ServerPlayer, statue: StatueEntity) {
        ServerPlayNetworking.send(
            player,
            UpdateStatuePayload(
                statueId = statue.id,
                pokemonName = statue.pokemonName,
                size = statue.size,
                animation = statue.animation,
                isAnimated = statue.isAnimated,
                text = statue.text,
                material = statue.material,
                form = statue.form,
                extraMaterial = statue.extraMaterial,
                collisionType = statue.collisionType,
                movable = statue.movable,
                gender = statue.gender,
                isStatic = statue.isStatic
            )
        )
    }
    
    fun readStatueData(buf: FriendlyByteBuf, level: net.minecraft.world.level.Level): StatueData? {
        val statueId = buf.readVarInt()
        val entity = level.getEntity(statueId) as? StatueEntity ?: return null
        
        return StatueData(
            statue = entity,
            pokemonName = buf.readUtf(),
            size = buf.readUtf(),
            animation = buf.readUtf(),
            isAnimated = buf.readBoolean(),
            text = buf.readUtf(),
            material = buf.readUtf(),
            form = buf.readUtf(),
            extraMaterial = buf.readUtf(),
            collisionType = buf.readUtf(),
            movable = buf.readBoolean(),
            gender = buf.readUtf(),
            isStatic = buf.readBoolean()
        )
    }
    
    data class StatueData(
        val statue: StatueEntity,
        val pokemonName: String,
        val size: String,
        val animation: String,
        val isAnimated: Boolean,
        val text: String,
        val material: String,
        val form: String,
        val extraMaterial: String,
        val collisionType: String,
        val movable: Boolean,
        val gender: String,
        val isStatic: Boolean
    ) {
        fun applyToStatue() {
            statue.pokemonName = pokemonName
            statue.size = size
            statue.animation = animation
            statue.isAnimated = isAnimated
            statue.text = text
            statue.material = material
            statue.form = form
            statue.extraMaterial = extraMaterial
            statue.collisionType = collisionType
            statue.movable = movable
            statue.gender = gender
            statue.isStatic = isStatic
        }
    }
}

