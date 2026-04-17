package io.github.yuazer.twilightcloudmon.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.yuazer.twilightcloudmon.block.entity.WarpPlateBlockEntity
import io.github.yuazer.twilightcloudmon.registry.ModBlocks
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component

object WarpPlateCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("warpplate")
                .requires { it.hasPermission(2) }
                .then(setLiteral())
        )
    }

    private fun setLiteral(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("set")
            .then(
                Commands.argument("x", IntegerArgumentType.integer())
                    .then(
                        Commands.argument("y", IntegerArgumentType.integer())
                            .then(
                                Commands.argument("z", IntegerArgumentType.integer())
                                    .executes { ctx ->
                                        val source = ctx.source
                                        val player = source.playerOrException
                                        val level = player.serverLevel()
                                        val platePos = player.blockPosition()
                                        val state = level.getBlockState(platePos)

                                        if (!state.`is`(ModBlocks.WARP_PLATE)) {
                                            source.sendFailure(
                                                Component.translatable("command.twilightcloudmon.warpplate.not_on_plate")
                                            )
                                            return@executes 0
                                        }

                                        val blockEntity = level.getBlockEntity(platePos) as? WarpPlateBlockEntity
                                        if (blockEntity == null) {
                                            source.sendFailure(
                                                Component.translatable("command.twilightcloudmon.warpplate.error")
                                            )
                                            return@executes 0
                                        }

                                        val x = IntegerArgumentType.getInteger(ctx, "x")
                                        val y = IntegerArgumentType.getInteger(ctx, "y")
                                        val z = IntegerArgumentType.getInteger(ctx, "z")

                                        val targetPos = BlockPos(x, y, z)
                                        blockEntity.setWarpPosition(targetPos)

                                        source.sendSuccess(
                                            { Component.translatable("command.twilightcloudmon.warpplate.set", x, y, z) },
                                            true
                                        )
                                        1
                                    }
                            )
                    )
            )
    }
}

