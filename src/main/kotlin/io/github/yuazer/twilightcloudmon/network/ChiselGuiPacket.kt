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
    private const val MAX_TEXT_LENGTH = 128

    data class OpenGuiPayload(
        val statueId: Int,
        val basePosX: Double = 0.0,
        val basePosY: Double = 0.0,
        val basePosZ: Double = 0.0
    ) : CustomPacketPayload {
        companion object {
            val TYPE = CustomPacketPayload.Type<OpenGuiPayload>(OPEN_GUI_ID)
            val CODEC: StreamCodec<FriendlyByteBuf, OpenGuiPayload> = StreamCodec.of(
                { buf, payload ->
                    buf.writeVarInt(payload.statueId)
                    buf.writeDouble(payload.basePosX)
                    buf.writeDouble(payload.basePosY)
                    buf.writeDouble(payload.basePosZ)
                },
                { buf -> OpenGuiPayload(
                    statueId = buf.readVarInt(),
                    basePosX = buf.readDouble(),
                    basePosY = buf.readDouble(),
                    basePosZ = buf.readDouble()
                ) }
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
        val isStatic: Boolean,
        val posX: Double = 0.0,
        val posY: Double = 0.0,
        val posZ: Double = 0.0,
        val rotation: Float = 0f
    ) : CustomPacketPayload {
        companion object {
            val TYPE = CustomPacketPayload.Type<UpdateStatuePayload>(UPDATE_STATUE_ID)
            val CODEC: StreamCodec<FriendlyByteBuf, UpdateStatuePayload> = StreamCodec.of(
                { buf, p ->
                    buf.writeVarInt(p.statueId)
                    buf.writeUtf(p.pokemonName, MAX_TEXT_LENGTH)
                    buf.writeUtf(p.size, MAX_TEXT_LENGTH)
                    buf.writeUtf(p.animation, MAX_TEXT_LENGTH)
                    buf.writeBoolean(p.isAnimated)
                    buf.writeUtf(p.text, MAX_TEXT_LENGTH)
                    buf.writeUtf(p.material, MAX_TEXT_LENGTH)
                    buf.writeUtf(p.form, MAX_TEXT_LENGTH)
                    buf.writeUtf(p.extraMaterial, MAX_TEXT_LENGTH)
                    buf.writeUtf(p.collisionType, MAX_TEXT_LENGTH)
                    buf.writeBoolean(p.movable)
                    buf.writeUtf(p.gender, MAX_TEXT_LENGTH)
                    buf.writeBoolean(p.isStatic)
                    buf.writeDouble(p.posX)
                    buf.writeDouble(p.posY)
                    buf.writeDouble(p.posZ)
                    buf.writeFloat(p.rotation)
                },
                { buf ->
                    UpdateStatuePayload(
                        statueId = buf.readVarInt(),
                        pokemonName = buf.readUtf(MAX_TEXT_LENGTH),
                        size = buf.readUtf(MAX_TEXT_LENGTH),
                        animation = buf.readUtf(MAX_TEXT_LENGTH),
                        isAnimated = buf.readBoolean(),
                        text = buf.readUtf(MAX_TEXT_LENGTH),
                        material = buf.readUtf(MAX_TEXT_LENGTH),
                        form = buf.readUtf(MAX_TEXT_LENGTH),
                        extraMaterial = buf.readUtf(MAX_TEXT_LENGTH),
                        collisionType = buf.readUtf(MAX_TEXT_LENGTH),
                        movable = buf.readBoolean(),
                        gender = buf.readUtf(MAX_TEXT_LENGTH),
                        isStatic = buf.readBoolean(),
                        posX = buf.readDouble(),
                        posY = buf.readDouble(),
                        posZ = buf.readDouble(),
                        rotation = buf.readFloat()
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
            entity.moveTo(posX, posY, posZ, rotation, entity.xRot)
            entity.yRot = rotation
            entity.yRotO = rotation
            entity.yHeadRot = rotation
            entity.yBodyRot = rotation
            entity.setDeltaMovement(0.0, 0.0, 0.0)
            entity.noPhysics = true
            entity.hasImpulse = true
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

    fun sendOpenGui(player: ServerPlayer, statue: StatueEntity) {
        ServerPlayNetworking.send(player, OpenGuiPayload(
            statueId = statue.id,
            basePosX = statue.basePosX,
            basePosY = statue.basePosY,
            basePosZ = statue.basePosZ
        ))
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
                isStatic = statue.isStatic,
                posX = statue.x,
                posY = statue.y,
                posZ = statue.z,
                rotation = statue.yRot
            )
        )
    }
}
