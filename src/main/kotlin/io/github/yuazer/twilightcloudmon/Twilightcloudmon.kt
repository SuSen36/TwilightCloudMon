package io.github.yuazer.twilightcloudmon

import io.github.yuazer.twilightcloudmon.command.WarpPlateCommand
import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.event.ChiselEvents
import io.github.yuazer.twilightcloudmon.event.ElevatorEvents
import io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket
import io.github.yuazer.twilightcloudmon.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.io.File

class Twilightcloudmon : ModInitializer {

    companion object {
        const val MOD_ID = "twilightcloudmon"
        private val LOGGER = LoggerFactory.getLogger(MOD_ID)

        private const val SHOWDOWN_RELATIVE_PATH = "showdown/data/mods/cobblemon"
        private const val RESOURCE_BASE = "/io/github/yuazer/twilightcloudmon/"

        val SHOWDOWN_FOLDER: String =
            FabricLoader.getInstance().gameDir.resolve(SHOWDOWN_RELATIVE_PATH).toString()

        private val SHOWDOWN_FILES = listOf("moves.js", "abilities.js", "items.js")
    }

    override fun onInitialize() {
        LOGGER.info("Initializing $MOD_ID")

        ModBlocks.register()
        ModBlockEntities.register()
        ModSwords.register()
        ModBows.register()
        ModHammers.register()
        ModTools.register()
        ModFireworkWeapons.register()
        ModItems.register()
        ModEntities.register()
        ModItemGroups.register()

        FabricDefaultAttributeRegistry.register(ModEntities.STATUE, StatueEntity.createAttributes())

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            WarpPlateCommand.register(dispatcher)
        }

        ElevatorEvents.register()
        ChiselEvents.register()
        ChiselGuiPacket.register()

        deployShowdownFiles()

        LOGGER.info("$MOD_ID initialized")
    }

    private fun deployShowdownFiles() {
        File(SHOWDOWN_FOLDER).mkdirs()
        for (fileName in SHOWDOWN_FILES) {
            val resourceStream = javaClass.getResourceAsStream("$RESOURCE_BASE$fileName")
                ?: throw RuntimeException("Could not find $fileName")
            File("$SHOWDOWN_FOLDER/$fileName").outputStream().use { output ->
                resourceStream.use { it.copyTo(output) }
            }
        }
    }
}
