package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object RegistryHelper {

    fun id(path: String): ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, path)

    fun registerItem(name: String, item: Item): Item =
        Registry.register(BuiltInRegistries.ITEM, id(name), item)

    fun registerBlock(name: String, block: Block): Block =
        Registry.register(BuiltInRegistries.BLOCK, id(name), block)

    fun registerBlockWithItem(
        name: String,
        block: Block,
        itemProps: Item.Properties = Item.Properties()
    ): Block {
        val rid = id(name)
        Registry.register(BuiltInRegistries.BLOCK, rid, block)
        if (!BuiltInRegistries.ITEM.containsKey(rid)) {
            Registry.register(BuiltInRegistries.ITEM, rid, BlockItem(block, itemProps))
        }
        return block
    }

    fun <T : BlockEntity> registerBlockEntity(
        name: String,
        type: BlockEntityType<T>
    ): BlockEntityType<T> =
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(name), type)

    fun <T : Entity> registerEntityType(
        name: String,
        type: EntityType<T>
    ): EntityType<T> =
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id(name), type)

    fun isModItem(key: ResourceLocation): Boolean =
        key.namespace == Twilightcloudmon.MOD_ID

    inline fun forEachModItem(block: (name: String, item: Item) -> Unit) {
        for (item in BuiltInRegistries.ITEM) {
            val key = BuiltInRegistries.ITEM.getKey(item)
            if (isModItem(key)) block(key.path, item)
        }
    }
}
