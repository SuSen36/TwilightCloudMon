package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.item.FireworkBowItem
import io.github.yuazer.twilightcloudmon.item.FireworkTridentItem
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.Ingredient

object ModFireworkWeapons {
    private val registered = mutableListOf<ResourceLocation>()

    // 武器
    val FIREWORK_SWORD = SwordItem(
        Tiers.NETHERITE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4F))
    )

    val FIREWORK_GREATSWORD = SwordItem(
        Tiers.NETHERITE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 5, -3.0F))
    )

    val FIREWORK_HAMMER = MaceItem(
        Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1).durability(2031) // 下界合金耐久度.component(net.minecraft.core.component.DataComponents.TOOL, MaceItem.createToolProperties()).attributes(MaceItem.createAttributes())
    )

    val FIREWORK_TRIDENT = FireworkTridentItem(
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度.attributes(TridentItem.createAttributes())
    )

    val FIREWORK_SCYTHE = SwordItem(
        Tiers.NETHERITE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 4, -2.6F))
    )

    // 工具
    val FIREWORK_AXE = AxeItem(
        Tiers.NETHERITE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(AxeItem.createAttributes(Tiers.NETHERITE, 5.0F, -3.0F))
    )

    val FIREWORK_PICKAXE = PickaxeItem(
        Tiers.NETHERITE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(PickaxeItem.createAttributes(Tiers.NETHERITE, 1.0F, -2.8F))
    )

    val FIREWORK_SHOVEL = ShovelItem(
        Tiers.NETHERITE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(ShovelItem.createAttributes(Tiers.NETHERITE, 1.5F, -3.0F))
    )

    val FIREWORK_HOE = HoeItem(
        Tiers.NETHERITE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(HoeItem.createAttributes(Tiers.NETHERITE, -3.0F, 0.0F))
    )

    // 远程武器
    val FIREWORK_BOW = FireworkBowItem(
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度
    )

    val FIREWORK_CROSSBOW = CrossbowItem(
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度
    )

    // 防御
    val FIREWORK_SHIELD = ShieldItem(
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度
    )

    // 盔甲材质
    private val FIREWORK_ARMOR_MATERIAL = ArmorMaterial(
        mapOf(ArmorItem.Type.BOOTS to 3,ArmorItem.Type.LEGGINGS to 6,ArmorItem.Type.CHESTPLATE to 8,ArmorItem.Type.HELMET to 3
        ),
        20, // 附魔值
        SoundEvents.ARMOR_EQUIP_NETHERITE,
        { Ingredient.EMPTY }, // 修复材料
        listOf(ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "firework_armor"))),
        3.0f, // 韧性
        0.1f  // 击退抗性
    )

    // 盔甲套装
    val FIREWORK_HELMET = ArmorItem(
        Holder.direct(FIREWORK_ARMOR_MATERIAL),
        ArmorItem.Type.HELMET,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度
    )

    val FIREWORK_CHESTPLATE = ArmorItem(
        Holder.direct(FIREWORK_ARMOR_MATERIAL),
        ArmorItem.Type.CHESTPLATE,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度
    )

    val FIREWORK_LEGGINGS = ArmorItem(
        Holder.direct(FIREWORK_ARMOR_MATERIAL),
        ArmorItem.Type.LEGGINGS,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度
    )

    val FIREWORK_BOOTS = ArmorItem(
        Holder.direct(FIREWORK_ARMOR_MATERIAL),
        ArmorItem.Type.BOOTS,
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(2031) // 下界合金耐久度
    )

    val FIREWORK_WINGS = ElytraItem(
        Item.Properties().rarity(Rarity.EPIC).fireResistant().durability(432)
    )

    fun register() {
        // 注册武器
        registerItem("firework_sword", FIREWORK_SWORD)
        registerItem("firework_greatsword", FIREWORK_GREATSWORD)
        registerItem("firework_hammer", FIREWORK_HAMMER)
        registerItem("firework_trident", FIREWORK_TRIDENT)
        registerItem("firework_scythe", FIREWORK_SCYTHE)

        // 注册工具
        registerItem("firework_axe", FIREWORK_AXE)
        registerItem("firework_pickaxe", FIREWORK_PICKAXE)
        registerItem("firework_shovel", FIREWORK_SHOVEL)
        registerItem("firework_hoe", FIREWORK_HOE)

        // 注册远程武器
        registerItem("firework_bow", FIREWORK_BOW)
        registerItem("firework_crossbow", FIREWORK_CROSSBOW)

        // 注册防御
        registerItem("firework_shield", FIREWORK_SHIELD)

        // 注册盔甲
        registerItem("firework_helmet", FIREWORK_HELMET)
        registerItem("firework_chestplate", FIREWORK_CHESTPLATE)
        registerItem("firework_leggings", FIREWORK_LEGGINGS)
        registerItem("firework_boots", FIREWORK_BOOTS)
        registerItem("firework_wings", FIREWORK_WINGS)
    }

    private fun registerItem(name: String, item: Item) {
        val id = ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, name)
        Registry.register(BuiltInRegistries.ITEM, id, item)
        registered += id
    }
}

