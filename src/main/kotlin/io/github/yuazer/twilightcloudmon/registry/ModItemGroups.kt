package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.registry.RegistryHelper.id
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.*
import java.util.function.Supplier

object ModItemGroups {

    lateinit var CUSTOM_WEAPONS: CreativeModeTab private set
    lateinit var CUSTOM_ITEMS: CreativeModeTab private set
    lateinit var CUSTOM_ITEMS1: CreativeModeTab private set
    lateinit var CUSTOM_ITEMS2: CreativeModeTab private set
    lateinit var CUSTOM_ITEMS3: CreativeModeTab private set
    lateinit var CUSTOM_BLOCKS: CreativeModeTab private set

    private val TAG_WEAPONS = TagKey.create(Registries.ITEM, id("creative/custom_weapons"))
    private val TAG_ITEMS = TagKey.create(Registries.ITEM, id("creative/custom_items"))
    private val TAG_ITEMS1 = TagKey.create(Registries.ITEM, id("creative/custom_items1"))
    private val TAG_ITEMS2 = TagKey.create(Registries.ITEM, id("creative/custom_items2"))

    private val WEAPON_BLACKLIST = setOf(
        "bonesword", "zenith_edge", "zenith_ward",
        "bug_stone", "dragon_stone", "fairy_stone", "fighting_stone",
        "fly_stone", "ghost_stone", "ground_stone", "poison_stone",
        "psychic_stone", "rock_stone", "star_stone",
        "giratinaite", "flygonite_x", "flygonite_y", "magearnite"
    )

    private val BLOCK_NAMES = setOf("movement_plate", "warp_plate", "elevator", "firework_chest")

    private val CUSTOM_ITEMS_WHITELIST = setOf(
        "anti_constant_energy", "bonesword", "flygonite_x", "flygonite_y", "giratinaite"
    )

    private val CUSTOM_ITEMS1_EXCLUDE = BLOCK_NAMES + setOf(
        "flygonite_x", "flygonite_y", "passkey", "giratinaite", "archaic_libram"
    )

    private val WEAPON_SUFFIXES = listOf(
        "_sword", "_greatsword", "_bow", "_crossbow", "_hammer", "_trident",
        "_scythe", "_staff", "_spear", "_shield", "_axe", "_pickaxe",
        "_shovel", "_hoe", "_helmet", "_chestplate", "_leggings", "_boots",
        "_wings", "_blade", "_edge", "_ward"
    )

    private val WEAPON_KEYWORDS = listOf(
        "sword", "greatsword", "bow", "crossbow", "hammer", "trident",
        "scythe", "staff", "spear", "shield", "pickaxe", "shovel",
        "hoe", "axe", "helmet", "chestplate", "leggings", "boots",
        "wings", "blade", "edge", "ward"
    )

    fun register() {
        CUSTOM_WEAPONS = registerTab(
            "custom_weapons",
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(tabTitle("custom_weapons"))
                .icon(iconSupplier(listOf("primal_sword", "primal_bow", "primal_hammer"), Items.IRON_SWORD))
                .displayItems { _, output ->
                    val added = DedupOutput(output)

                    added += ModWeapons.PRIMAL_SWORD
                    added += ModWeapons.PRIMAL_BOW
                    added += ModWeapons.PRIMAL_HAMMER

                    added += ModWeapons.FIREWORK_SWORD
                    added += ModWeapons.FIREWORK_GREATSWORD
                    added += ModWeapons.FIREWORK_HAMMER
                    added += ModWeapons.FIREWORK_TRIDENT
                    added += ModWeapons.FIREWORK_SCYTHE
                    added += ModWeapons.FIREWORK_AXE
                    added += ModWeapons.FIREWORK_PICKAXE
                    added += ModWeapons.FIREWORK_SHOVEL
                    added += ModWeapons.FIREWORK_HOE
                    added += ModWeapons.FIREWORK_BOW
                    added += ModWeapons.FIREWORK_CROSSBOW
                    added += ModWeapons.FIREWORK_SHIELD
                    added += ModWeapons.FIREWORK_HELMET
                    added += ModWeapons.FIREWORK_CHESTPLATE
                    added += ModWeapons.FIREWORK_LEGGINGS
                    added += ModWeapons.FIREWORK_BOOTS
                    added += ModWeapons.FIREWORK_WINGS

                    addByTag(TAG_WEAPONS, output)

                    RegistryHelper.forEachModItem { name, item ->
                        if (name !in WEAPON_BLACKLIST && isWeaponName(name)) added += item
                    }
                }
        )

        CUSTOM_ITEMS = registerTab(
            "custom_items",
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                .title(tabTitle("custom_items"))
                .icon(iconSupplier(listOf("anti_constant_energy"), Items.CHEST))
                .displayItems { _, output ->
                    val added = DedupOutput(output)
                    addByTag(TAG_ITEMS, output)
                    RegistryHelper.forEachModItem { name, item ->
                        if (name in CUSTOM_ITEMS_WHITELIST) added += item
                    }
                }
        )

        CUSTOM_ITEMS1 = registerTab(
            "custom_items1",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
                .title(tabTitle("custom_items1"))
                .icon(iconSupplier(listOf("star_stone", "ghost_bottle", "up_paper"), Items.AMETHYST_SHARD))
                .displayItems { _, output ->
                    val added = DedupOutput(output)
                    addByTag(TAG_ITEMS1, output)

                    RegistryHelper.forEachModItem { name, item ->
                        if (name in CUSTOM_ITEMS1_EXCLUDE) return@forEachModItem
                        if ((isEvolutionItem(name) || name == "zenith_edge" || name == "zenith_ward") && name != "anti_constant_energy") {
                            added += item
                        } else if (!isArchaeItem(name) && !isWeaponName(name) && name != "anti_constant_energy" && name != "chisel") {
                            added += item
                        }
                    }
                }
        )

        CUSTOM_ITEMS2 = registerTab(
            "custom_items2",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 1)
                .title(tabTitle("custom_items2"))
                .icon(iconSupplier(listOf("dna_fire"), Items.BRUSH))
                .displayItems { _, output ->
                    val added = DedupOutput(output)
                    BuiltInRegistries.ITEM.getOptional(id("dna_fire")).ifPresent { added += it }
                    addByTag(TAG_ITEMS2, output)
                    RegistryHelper.forEachModItem { name, item ->
                        if (isArchaeItem(name) && name != "anti_constant_energy" && name != "dna_fire") {
                            added += item
                        }
                    }
                }
        )

        CUSTOM_ITEMS3 = registerTab(
            "custom_items3",
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
                .title(tabTitle("custom_items3"))
                .icon(iconSupplier(listOf("archaic_libram"), Items.ENCHANTED_BOOK))
                .displayItems { _, output ->
                    val added = DedupOutput(output)
                    RegistryHelper.forEachModItem { name, item ->
                        if (name == "archaic_libram" || name == "passkey") added += item
                    }
                }
        )

        CUSTOM_BLOCKS = registerTab(
            "custom_blocks",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 2)
                .title(tabTitle("custom_blocks"))
                .icon(iconSupplier(listOf("movement_plate", "warp_plate"), Items.STONE))
                .displayItems { _, _ -> }
        )

        val blocksTabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(CUSTOM_BLOCKS)
            ?: throw IllegalStateException("Custom blocks tab not registered")
        val blocksTabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, blocksTabId)
        ItemGroupEvents.modifyEntriesEvent(blocksTabKey).register { entries ->
            entries.accept(ModBlocks.MOVEMENT_PLATE.asItem())
            entries.accept(ModBlocks.WARP_PLATE.asItem())
            entries.accept(ModBlocks.ELEVATOR.asItem())
            entries.accept(ModBlocks.FIREWORK_CHEST.asItem())
            BuiltInRegistries.ITEM.getOptional(id("chisel")).ifPresent { entries.accept(it) }
        }
    }

    private class DedupOutput(private val output: CreativeModeTab.Output) {
        private val seen = hashSetOf<Item>()
        operator fun plusAssign(item: Item) {
            if (seen.add(item)) output.accept(item)
        }
    }

    private fun registerTab(name: String, builder: CreativeModeTab.Builder): CreativeModeTab =
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id(name), builder.build())

    private fun tabTitle(name: String): Component =
        Component.translatable("itemgroup.${Twilightcloudmon.MOD_ID}.$name")

    private fun iconSupplier(candidates: List<String>, fallback: Item): Supplier<ItemStack> = Supplier {
        candidates.firstNotNullOfOrNull { name ->
            BuiltInRegistries.ITEM.getOptional(id(name)).orElse(null)
        }?.let { ItemStack(it) } ?: ItemStack(fallback)
    }

    private fun addByTag(tag: TagKey<Item>, output: CreativeModeTab.Output) {
        BuiltInRegistries.ITEM.getTag(tag).ifPresent { named ->
            named.forEach { holder -> output.accept(holder.value()) }
        }
    }

    private fun isWeaponName(name: String) =
        WEAPON_SUFFIXES.any { name.endsWith(it) } || WEAPON_KEYWORDS.any { it in name }

    private fun isEvolutionItem(name: String) =
        name.endsWith("_stone") || name.endsWith("stone") ||
                name.endsWith("ite") || name.endsWith("ite_x") || name.endsWith("ite_y")

    private fun isArchaeItem(name: String) =
        name.startsWith("dna_") || "fossil" in name || "relic" in name ||
                "archae" in name || "ruin" in name || "chip" in name ||
                "hydro_from" in name || "mal_data" in name
}
