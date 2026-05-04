package io.github.yuazer.twilightcloudmon.network

import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.registry.RegistryHelper.id
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

object ChiselGuiPacket {

    private val OPEN_GUI_ID = id("open_chisel_gui")
    private val UPDATE_STATUE_ID = id("update_statue")

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
                { buf, p ->
                    buf.writeVarInt(p.statueId)
                    buf.writeUtf(p.pokemonName)
                    buf.writeUtf(p.size)
                    buf.writeUtf(p.animation)
                    buf.writeBoolean(p.isAnimated)
                    buf.writeUtf(p.text)
                    buf.writeUtf(p.material)
                    buf.writeUtf(p.form)
                    buf.writeUtf(p.extraMaterial)
                    buf.writeUtf(p.collisionType)
                    buf.writeBoolean(p.movable)
                    buf.writeUtf(p.gender)
                    buf.writeBoolean(p.isStatic)
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

        fun applyTo(entity: StatueEntity) {
            entity.pokemonName = pokemonName
            entity.size = size
            entity.animation = animation
            entity.isAnimated = isAnimated
            entity.text = text
            entity.material = material
            entity.form = form
            entity.extraMaterial = extraMaterial
            entity.collisionType = collisionType
            entity.movable = movable
            entity.gender = gender
            entity.isStatic = isStatic
        }

        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
    }

    fun register() {
        PayloadTypeRegistry.playS2C().register(OpenGuiPayload.TYPE, OpenGuiPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(UpdateStatuePayload.TYPE, UpdateStatuePayload.CODEC)
        PayloadTypeRegistry.playS2C().register(UpdateStatuePayload.TYPE, UpdateStatuePayload.CODEC)

        ServerPlayNetworking.registerGlobalReceiver(UpdateStatuePayload.TYPE) { payload, context ->
            val player = context.player()
            player.server.execute {
                val entity = player.level().getEntity(payload.statueId)
                if (entity is StatueEntity) payload.applyTo(entity)
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
}
