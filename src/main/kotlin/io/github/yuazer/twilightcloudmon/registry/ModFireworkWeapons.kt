package io.github.yuazer.twilightcloudmon.registry

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.item.FireworkBowItem
import io.github.yuazer.twilightcloudmon.item.FireworkTridentItem
import io.github.yuazer.twilightcloudmon.registry.RegistryHelper.id
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.Ingredient

object ModFireworkWeapons {

    private const val NETHERITE_DURABILITY = 2031
    private const val ELYTRA_DURABILITY = 432
    private const val ENCHANTMENT_VALUE = 20
    private const val TOUGHNESS = 3.0f
    private const val KNOCKBACK_RESISTANCE = 0.1f

    private fun epicFireResistant(): Item.Properties =
        Item.Properties().rarity(Rarity.EPIC).fireResistant()

    private fun epicDurable(): Item.Properties =
        epicFireResistant().durability(NETHERITE_DURABILITY)

    val FIREWORK_SWORD: SwordItem = SwordItem(
        Tiers.NETHERITE,
        epicFireResistant().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4F))
    )
    val FIREWORK_GREATSWORD: SwordItem = SwordItem(
        Tiers.NETHERITE,
        epicFireResistant().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 5, -3.0F))
    )
    val FIREWORK_HAMMER: MaceItem = MaceItem(
        epicFireResistant().stacksTo(1).durability(NETHERITE_DURABILITY)
    )
    val FIREWORK_TRIDENT: FireworkTridentItem = FireworkTridentItem(
        epicDurable()
    )
    val FIREWORK_SCYTHE: SwordItem = SwordItem(
        Tiers.NETHERITE,
        epicFireResistant().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 4, -2.6F))
    )

    val FIREWORK_AXE: AxeItem = AxeItem(
        Tiers.NETHERITE,
        epicFireResistant().attributes(AxeItem.createAttributes(Tiers.NETHERITE, 5.0F, -3.0F))
    )
    val FIREWORK_PICKAXE: PickaxeItem = PickaxeItem(
        Tiers.NETHERITE,
        epicFireResistant().attributes(PickaxeItem.createAttributes(Tiers.NETHERITE, 1.0F, -2.8F))
    )
    val FIREWORK_SHOVEL: ShovelItem = ShovelItem(
        Tiers.NETHERITE,
        epicFireResistant().attributes(ShovelItem.createAttributes(Tiers.NETHERITE, 1.5F, -3.0F))
    )
    val FIREWORK_HOE: HoeItem = HoeItem(
        Tiers.NETHERITE,
        epicFireResistant().attributes(HoeItem.createAttributes(Tiers.NETHERITE, -3.0F, 0.0F))
    )

    val FIREWORK_BOW: FireworkBowItem = FireworkBowItem(epicDurable())
    val FIREWORK_CROSSBOW: CrossbowItem = CrossbowItem(epicDurable())
    val FIREWORK_SHIELD: ShieldItem = ShieldItem(epicDurable())

    private val FIREWORK_ARMOR_MATERIAL = ArmorMaterial(
        mapOf(
            ArmorItem.Type.BOOTS to 3,
            ArmorItem.Type.LEGGINGS to 6,
            ArmorItem.Type.CHESTPLATE to 8,
            ArmorItem.Type.HELMET to 3
        ),
        ENCHANTMENT_VALUE,
        SoundEvents.ARMOR_EQUIP_NETHERITE,
        { Ingredient.EMPTY },
        listOf(ArmorMaterial.Layer(id("firework_armor"))),
        TOUGHNESS,
        KNOCKBACK_RESISTANCE
    )

    private val armorHolder = Holder.direct(FIREWORK_ARMOR_MATERIAL)

    val FIREWORK_HELMET: ArmorItem = ArmorItem(armorHolder, ArmorItem.Type.HELMET, epicDurable())
    val FIREWORK_CHESTPLATE: ArmorItem = ArmorItem(armorHolder, ArmorItem.Type.CHESTPLATE, epicDurable())
    val FIREWORK_LEGGINGS: ArmorItem = ArmorItem(armorHolder, ArmorItem.Type.LEGGINGS, epicDurable())
    val FIREWORK_BOOTS: ArmorItem = ArmorItem(armorHolder, ArmorItem.Type.BOOTS, epicDurable())

    val FIREWORK_WINGS: ElytraItem = ElytraItem(
        epicFireResistant().durability(ELYTRA_DURABILITY)
    )

    private val ALL_ITEMS = mapOf(
        "firework_sword" to FIREWORK_SWORD,
        "firework_greatsword" to FIREWORK_GREATSWORD,
        "firework_hammer" to FIREWORK_HAMMER,
        "firework_trident" to FIREWORK_TRIDENT,
        "firework_scythe" to FIREWORK_SCYTHE,
        "firework_axe" to FIREWORK_AXE,
        "firework_pickaxe" to FIREWORK_PICKAXE,
        "firework_shovel" to FIREWORK_SHOVEL,
        "firework_hoe" to FIREWORK_HOE,
        "firework_bow" to FIREWORK_BOW,
        "firework_crossbow" to FIREWORK_CROSSBOW,
        "firework_shield" to FIREWORK_SHIELD,
        "firework_helmet" to FIREWORK_HELMET,
        "firework_chestplate" to FIREWORK_CHESTPLATE,
        "firework_leggings" to FIREWORK_LEGGINGS,
        "firework_boots" to FIREWORK_BOOTS,
        "firework_wings" to FIREWORK_WINGS
    )

    fun register() {
        ALL_ITEMS.forEach { (name, item) -> RegistryHelper.registerItem(name, item) }
    }
}
