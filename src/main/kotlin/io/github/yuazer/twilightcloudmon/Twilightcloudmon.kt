package io.github.yuazer.twilightcloudmon

import io.github.yuazer.twilightcloudmon.command.WarpPlateCommand
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
   companion object{
       const val MOD_ID = "twilightcloudmon"
       private val LOGGER = LoggerFactory.getLogger(MOD_ID)
       val SHOWDOWN_FOLDER = FabricLoader.getInstance().gameDir.resolve("showdown/data/mods/cobblemon").toString()
       val SHOWDOWN_FILES: List<String> = listOf(
           "moves.js",
           "abilities.js",
           "items.js"
       )
   }

    override fun onInitialize() {
        LOGGER.info("Initializing Twilightcloudmon")
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

        // 注册实体属性
        FabricDefaultAttributeRegistry.register(ModEntities.STATUE, io.github.yuazer.twilightcloudmon.entity.StatueEntity.createAttributes())

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            WarpPlateCommand.register(dispatcher)
        }
        
        // 注册升降方块事件
        ElevatorEvents.register()
        
        // 注册雕塑工具事件
        ChiselEvents.register()
        
        // 注册网络包
        ChiselGuiPacket.register()
        
        File(SHOWDOWN_FOLDER).mkdirs()

        for (fileName in SHOWDOWN_FILES) {
            val resourceStream = javaClass.getResourceAsStream("/io/github/yuazer/twilightcloudmon/$fileName")
                ?: throw RuntimeException("Could not find $fileName")

            File("$SHOWDOWN_FOLDER/$fileName").outputStream().use { output ->
                resourceStream.use { input ->
                    input.copyTo(output)
                }
            }
        }
        LOGGER.info("Twilightcloudmon initialized")
    }
}
