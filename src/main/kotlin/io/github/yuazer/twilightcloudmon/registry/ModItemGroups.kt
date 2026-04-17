package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.*
import java.util.function.Supplier

object ModItemGroups {
    lateinit var CUSTOM_WEAPONS: CreativeModeTab
    lateinit var CUSTOM_ITEMS: CreativeModeTab
    lateinit var CUSTOM_ITEMS1: CreativeModeTab
    lateinit var CUSTOM_ITEMS2: CreativeModeTab
    lateinit var CUSTOM_ITEMS3: CreativeModeTab
    lateinit var CUSTOM_BLOCKS: CreativeModeTab

    // 如果你在 data/<modid>/tags/items/creative/ 下放了对应 json，这些标签会被自动加入
    private val TAG_WEAPONS = TagKey.create(Registries.ITEM, rl("creative/custom_weapons"))
    private val TAG_ITEMS   = TagKey.create(Registries.ITEM, rl("creative/custom_items"))
    private val TAG_ITEMS1  = TagKey.create(Registries.ITEM, rl("creative/custom_items1"))
    private val TAG_ITEMS2  = TagKey.create(Registries.ITEM, rl("creative/custom_items2"))

    // 不应进入武器栏的黑名单（如：骨之剑只是携带道具，极致之刃/盾是进化道具）
    private val WEAPON_BLACKLIST = setOf(
        "bonesword",
        "zenith_edge",
        "zenith_ward",
        "bug_stone",
        "dragon_stone",
        "fairy_stone",
        "fighting_stone",
        "fly_stone",
        "ghost_stone",
        "ground_stone",
        "poison_stone",
        "psychic_stone",
        "rock_stone",
        "star_stone",
        "giratinaite",
        "flygonite_x",
        "flygonite_y",
        "magearnite"
    )

    private val CUSTOM_ITEMS1_LIST = setOf(
        "bug_stone",
        "dragon_stone",
        "fairy_stone",
        "fighting_stone",
        "fly_stone",
        "ghost_stone",
        "ground_stone",
        "poison_stone",
        "psychic_stone",
        "rock_stone",
        "star_stone",
        "zenith_edge",
        "zenith_ward",
        "giratinaite",
        "flygonite_x",
        "flygonite_y",
        "magearnite"
    )

    fun register() {
        // 行列随你排：TOP/BOTTOM + 列索引（从 0 起）
        CUSTOM_WEAPONS = registerTab(
            "custom_weapons",
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(Component.translatable("itemgroup.${Twilightcloudmon.MOD_ID}.custom_weapons"))
                .icon(iconSupplier(listOf("primal_sword", "primal_bow", "primal_hammer"), Items.IRON_SWORD))
                .displayItems { _, output ->
                    val added = hashSetOf<Item>()
                    fun add(item: Item) {
                        if (added.add(item)) output.accept(item)
                    }

                    // 先放核心武器
                    add(ModSwords.PRIMAL_SWORD)
                    add(ModBows.PRIMAL_BOW)
                    add(ModHammers.PRIMAL_HAMMER)

                    // 再放烟花系列（你提到的“烟花武器”）
                    add(ModFireworkWeapons.FIREWORK_SWORD)
                    add(ModFireworkWeapons.FIREWORK_GREATSWORD)
                    add(ModFireworkWeapons.FIREWORK_HAMMER)
                    add(ModFireworkWeapons.FIREWORK_TRIDENT)
                    add(ModFireworkWeapons.FIREWORK_SCYTHE)
                    add(ModFireworkWeapons.FIREWORK_AXE)
                    add(ModFireworkWeapons.FIREWORK_PICKAXE)
                    add(ModFireworkWeapons.FIREWORK_SHOVEL)
                    add(ModFireworkWeapons.FIREWORK_HOE)
                    add(ModFireworkWeapons.FIREWORK_BOW)
                    add(ModFireworkWeapons.FIREWORK_CROSSBOW)
                    add(ModFireworkWeapons.FIREWORK_SHIELD)
                    // 烟花护甲也归入武器栏
                    add(ModFireworkWeapons.FIREWORK_HELMET)
                    add(ModFireworkWeapons.FIREWORK_CHESTPLATE)
                    add(ModFireworkWeapons.FIREWORK_LEGGINGS)
                    add(ModFireworkWeapons.FIREWORK_BOOTS)
                    add(ModFireworkWeapons.FIREWORK_WINGS)

                    // 支持通过 tag 扩展
                    addByTag(TAG_WEAPONS, output)

                    // 自动收集本模组所有“武器/工具/防具(可按规则调整)”类物品，确保不会漏掉
                    forEachModItem { name, item ->
                        if (name in WEAPON_BLACKLIST) return@forEachModItem
                        if (isWeaponName(name)) add(item)
                    }
                }
        )

        CUSTOM_ITEMS = registerTab(
            "custom_items",
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                .title(Component.translatable("itemgroup.${Twilightcloudmon.MOD_ID}.custom_items"))
                .icon(iconSupplier(listOf("anti_constant_energy"), Items.CHEST))
                .displayItems { _, output ->
                    val added = hashSetOf<Item>()
                    fun add(item: Item) {
                        if (added.add(item)) output.accept(item)
                    }

                    BuiltInRegistries.ITEM.getTag(TAG_ITEMS).ifPresent { named ->
                        named.forEach { holder -> add(holder.value()) }
                    }
                    forEachModItem { name, item ->
                        if (name == "anti_constant_energy" || name == "bonesword" || name == "flygonite_x" || name == "flygonite_y" || name == "giratinaite") {
                            add(item)
                        }
                    }
                }
        )

        CUSTOM_ITEMS1 = registerTab(
            "custom_items1",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
                .title(Component.translatable("itemgroup.${Twilightcloudmon.MOD_ID}.custom_items1"))
                .icon(iconSupplier(listOf("star_stone", "ghost_bottle", "up_paper"), Items.AMETHYST_SHARD))
                .displayItems { _, output ->
                    val added = hashSetOf<Item>()
                    fun add(item: Item) {
                        if (added.add(item)) output.accept(item)
                    }

                    BuiltInRegistries.ITEM.getTag(TAG_ITEMS1).ifPresent { named ->
                        named.forEach { holder -> add(holder.value()) }
                    }

                    forEachModItem { name, item ->
                        if (name == "movement_plate" || name == "warp_plate" || name == "elevator" || name == "firework_chest") return@forEachModItem
                        // 排除 flygon 进化石，它们在携带道具物品栏中
                        if (name == "flygonite_x" || name == "flygonite_y" || name == "passkey" || name == "giratinaite" || name == "archaic_libram") return@forEachModItem
                        if ((isEvolutionItem(name) || name == "zenith_edge" || name == "zenith_ward") && name != "anti_constant_energy") {
                            add(item)
                        }
                        // 添加其他携带道具（除了 anti_constant_energy 和 chisel）
                        else if (!isArchaeItem(name)&&!isWeaponName(name) && name != "anti_constant_energy" && name != "chisel")
                            add(item)
                    }
                }
        )

        CUSTOM_ITEMS2 = registerTab(
            "custom_items2",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 1)
                .title(Component.translatable("itemgroup.${Twilightcloudmon.MOD_ID}.custom_items2"))
                .icon(iconSupplier(listOf("dna_fire"), Items.BRUSH))
                .displayItems { _, output ->
                    val added = hashSetOf<Item>()
                    fun add(item: Item) {
                        if (added.add(item)) output.accept(item)
                    }

                    // 先将 dna_fire 放在第一个位置
                    BuiltInRegistries.ITEM.getOptional(rl("dna_fire")).ifPresent { item ->
                        add(item)
                    }
                    BuiltInRegistries.ITEM.getTag(TAG_ITEMS2).ifPresent { named ->
                        named.forEach { holder -> add(holder.value()) }
                    }
                    forEachModItem { name, item ->
                        if (isArchaeItem(name) && name != "anti_constant_energy" && name != "dna_fire") {
                            add(item)
                        }
                    }
                }
        )

        CUSTOM_ITEMS3 = registerTab(
            "custom_items3",
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
                .title(Component.translatable("itemgroup.${Twilightcloudmon.MOD_ID}.custom_items3"))
                .icon(iconSupplier(listOf("archaic_libram"), Items.ENCHANTED_BOOK))
                .displayItems { _, output ->
                    val added = hashSetOf<Item>()
                    fun add(item: Item) {
                        if (added.add(item)) output.accept(item)
                    }

                    forEachModItem { name, item ->
                        if (name == "archaic_libram" || name == "passkey") {
                            add(item)
                        }
                    }
                }
        )

        CUSTOM_BLOCKS = registerTab(
            "custom_blocks",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 2)
                .title(Component.translatable("itemgroup.${Twilightcloudmon.MOD_ID}.custom_blocks"))
                .icon(iconSupplier(listOf("movement_plate", "warp_plate"), Items.STONE))
                .displayItems { _, _ -> }
        )

        // 使用 ItemGroupEvents 添加传送方块到新物品栏
        val customBlocksResourceLocation = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(CUSTOM_BLOCKS)
            ?: throw IllegalStateException("Custom blocks tab not registered")
        val customBlocksKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, customBlocksResourceLocation)
        ItemGroupEvents.modifyEntriesEvent(customBlocksKey).register { entries ->
            entries.accept(ModBlocks.MOVEMENT_PLATE.asItem())
            entries.accept(ModBlocks.WARP_PLATE.asItem())
            entries.accept(ModBlocks.ELEVATOR.asItem())
            entries.accept(ModBlocks.FIREWORK_CHEST.asItem())
            // 工具类放这里以便雕塑相关方块与工具归类
            BuiltInRegistries.ITEM.getOptional(rl("chisel")).ifPresent { entries.accept(it) }
        }
    }

    // ---------- helpers ----------

    private fun registerTab(name: String, builder: CreativeModeTab.Builder): CreativeModeTab {
        val id = rl(name)
        val tab = builder.build()
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab) // ✅ 用真实注册表实例
    }

    private fun rl(path: String) =
        ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, path)

    /** 遍历本 mod 的所有物品（通过注册表） */
    private inline fun forEachModItem(crossinline block: (name: String, item: Item) -> Unit) {
        for (item in BuiltInRegistries.ITEM) {
            val key = BuiltInRegistries.ITEM.getKey(item)
            if (key.namespace != Twilightcloudmon.MOD_ID) continue
            block(key.path, item)
        }
    }

    /** 生成图标 Supplier：优先用候选物品，找不到就用 fallback */
    private fun iconSupplier(candidates: List<String>, fallback: Item): Supplier<ItemStack> = Supplier {
        for (p in candidates) {
            val opt = BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, p)
            )
            if (opt.isPresent) return@Supplier ItemStack(opt.get())
        }
        ItemStack(fallback)
    }

    /** 把某个 item 标签里的内容加入分组（如果提供了 tags JSON） */
    private fun addByTag(tag: TagKey<Item>, output: CreativeModeTab.Output) {
        BuiltInRegistries.ITEM.getTag(tag).ifPresent { named ->
            named.forEach { holder -> output.accept(holder.value()) }
        }
    }

    // 名称规则（可按需调整/改成白名单）
    private fun isWeaponName(name: String) =
        name.endsWith("_sword") ||
                name.endsWith("_greatsword") ||
                name.endsWith("_bow") ||
                name.endsWith("_crossbow") ||
                name.endsWith("_hammer") ||
                name.endsWith("_trident") ||
                name.endsWith("_scythe") ||
                name.endsWith("_staff") ||
                name.endsWith("_spear") ||
                name.endsWith("_shield") ||
                name.endsWith("_axe") ||
                name.endsWith("_pickaxe") ||
                name.endsWith("_shovel") ||
                name.endsWith("_hoe") ||
                name.endsWith("_helmet") ||
                name.endsWith("_chestplate") ||
                name.endsWith("_leggings") ||
                name.endsWith("_boots") ||
                name.endsWith("_wings") ||
                name.endsWith("_blade") ||
                name.endsWith("_edge") ||
                name.endsWith("_ward") ||
                "sword" in name ||
                "greatsword" in name ||
                "bow" in name ||
                "crossbow" in name ||
                "hammer" in name ||
                "trident" in name ||
                "scythe" in name ||
                "staff" in name ||
                "spear" in name ||
                "shield" in name ||
                "pickaxe" in name ||
                "shovel" in name ||
                "hoe" in name ||
                "axe" in name ||
                "helmet" in name ||
                "chestplate" in name ||
                "leggings" in name ||
                "boots" in name ||
                "wings" in name ||
                "blade" in name ||
                "edge" in name ||
                "ward" in name

    private fun isEvolutionItem(name: String) =
        name.endsWith("_stone") || name.endsWith("stone") || name.endsWith("ite") || name.endsWith("ite_x") || name.endsWith("ite_y")

    private fun isArchaeItem(name: String) =
        name.startsWith("dna_") || "fossil" in name || "relic" in name ||
                "archae" in name || "ruin" in name || "chip" in name || "hydro_from" in name || "mal_data" in name

}
