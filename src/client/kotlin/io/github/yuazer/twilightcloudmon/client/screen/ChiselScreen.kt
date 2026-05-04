package io.github.yuazer.twilightcloudmon.client.screen

import io.github.yuazer.twilightcloudmon.Twilightcloudmon
import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
class ChiselScreen(
    private val statueId: Int
) : Screen(Component.translatable("gui.twilightcloudmon.chisel")) {

    private var statue: StatueEntity? = null
    private lateinit var pokemonNameField: EditBox
    private lateinit var textField: EditBox
    private lateinit var animationField: EditBox
    private lateinit var animatedButton: Button
    private lateinit var sizeButton: Button
    private lateinit var materialButton: Button
    private lateinit var formButton: Button
    private lateinit var extraMaterialButton: Button
    private lateinit var collisionButton: Button
    private lateinit var movableButton: Button
    private lateinit var genderButton: Button
    private lateinit var staticButton: Button
    private lateinit var saveButton: Button

    private var currentSize = "normal"
    private var currentMaterial = "none"
    private var currentForm = "default"
    private var currentExtraMaterial = "none"
    private var currentCollision = "NONE"
    private var currentGender = "default"
    private var isMovable = false
    private var isStatic = false
    private var isAnimated = true

    companion object {
        private val TEXTURE = ResourceLocation.fromNamespaceAndPath(Twilightcloudmon.MOD_ID, "textures/gui/chisel.png")

        private val SIZES = listOf("tiny", "small", "normal", "large", "huge")
        private val MATERIALS = listOf("none", "shiny", "special")
        private val EXTRA_MATERIALS = listOf("none", "stone", "gold", "bronze", "iron", "diamond")
        private val COLLISION_TYPES = listOf("NONE", "SOLID", "PUSHOUT")
        private val GENDERS = listOf("default", "male", "female")
    }

    override fun init() {
        super.init()

        val level = minecraft?.level
        if (level != null) {
            val entity = level.getEntity(statueId)
            if (entity is StatueEntity) {
                statue = entity
                currentSize = entity.size
                currentMaterial = entity.material
                currentForm = entity.form
                currentExtraMaterial = entity.extraMaterial
                currentCollision = entity.collisionType
                currentGender = entity.gender
                isMovable = entity.movable
                isStatic = entity.isStatic
                isAnimated = entity.isAnimated
            }
        }

        val left = width / 2 - 200
        val top = height / 2 - 100

        pokemonNameField = EditBox(font, left + 10, top + 20, 180, 20, Component.literal("宝可梦名称"))
        pokemonNameField.setMaxLength(64)
        pokemonNameField.value = statue?.pokemonName ?: "pikachu"
        addRenderableWidget(pokemonNameField)

        textField = EditBox(font, left + 10, top + 50, 180, 20, Component.literal("显示文本"))
        textField.setMaxLength(64)
        textField.value = statue?.text ?: ""
        addRenderableWidget(textField)

        animationField = EditBox(font, left + 10, top + 80, 180, 20, Component.literal("动画"))
        animationField.setMaxLength(64)
        animationField.value = statue?.animation ?: "idle"
        addRenderableWidget(animationField)

        animatedButton = Button.builder(
            Component.literal("启用动画: ${if (isAnimated) "是" else "否"}"),
            {
                isAnimated = !isAnimated
                animatedButton.message = Component.literal("启用动画: ${if (isAnimated) "是" else "否"}")
            }
        ).bounds(left + 10, top + 110, 180, 20).build()
        addRenderableWidget(animatedButton)

        sizeButton = Button.builder(
            Component.literal("大小: $currentSize"),
            { cycleSize() }
        ).bounds(left + 10, top + 140, 180, 20).build()
        addRenderableWidget(sizeButton)

        materialButton = Button.builder(
            Component.literal("材质: $currentMaterial"),
            { cycleMaterial() }
        ).bounds(left + 10, top + 170, 180, 20).build()
        addRenderableWidget(materialButton)

        formButton = Button.builder(
            Component.literal("形态: $currentForm"),
            { cycleForm() }
        ).bounds(left + 10, top + 200, 180, 20).build()
        addRenderableWidget(formButton)

        extraMaterialButton = Button.builder(
            Component.literal("附加材质: $currentExtraMaterial"),
            { cycleExtraMaterial() }
        ).bounds(left + 10, top + 230, 180, 20).build()
        addRenderableWidget(extraMaterialButton)

        collisionButton = Button.builder(
            Component.literal("碰撞: $currentCollision"),
            { cycleCollision() }
        ).bounds(left + 10, top + 260, 180, 20).build()
        addRenderableWidget(collisionButton)

        movableButton = Button.builder(
            Component.literal("可移动: ${if (isMovable) "是" else "否"}"),
            {
                isMovable = !isMovable
                movableButton.message = Component.literal("可移动: ${if (isMovable) "是" else "否"}")
            }
        ).bounds(left + 10, top + 290, 180, 20).build()
        addRenderableWidget(movableButton)

        genderButton = Button.builder(
            Component.literal("性别: $currentGender"),
            { cycleGender() }
        ).bounds(left + 10, top + 320, 180, 20).build()
        addRenderableWidget(genderButton)

        staticButton = Button.builder(
            Component.literal("状态: ${if (isStatic) "静止" else "运动"}"),
            {
                isStatic = !isStatic
                staticButton.message = Component.literal("状态: ${if (isStatic) "静止" else "运动"}")
            }
        ).bounds(left + 10, top + 350, 180, 20).build()
        addRenderableWidget(staticButton)

        saveButton = Button.builder(
            Component.translatable("gui.twilightcloudmon.chisel.save"),
            { saveAndClose() }
        ).bounds(left + 10, top + 380, 180, 20).build()
        addRenderableWidget(saveButton)
    }

    private fun cycleSize() {
        val index = (SIZES.indexOf(currentSize) + 1) % SIZES.size
        currentSize = SIZES[index]
        sizeButton.message = Component.literal("大小: $currentSize")
    }

    private fun cycleMaterial() {
        val index = (MATERIALS.indexOf(currentMaterial) + 1) % MATERIALS.size
        currentMaterial = MATERIALS[index]
        materialButton.message = Component.literal("材质: $currentMaterial")
    }

    private fun cycleForm() {
        currentForm = if (currentForm == "default") "mega" else "default"
        formButton.message = Component.literal("形态: $currentForm")
    }

    private fun cycleExtraMaterial() {
        val index = (EXTRA_MATERIALS.indexOf(currentExtraMaterial) + 1) % EXTRA_MATERIALS.size
        currentExtraMaterial = EXTRA_MATERIALS[index]
        extraMaterialButton.message = Component.literal("附加材质: $currentExtraMaterial")
    }

    private fun cycleCollision() {
        val index = (COLLISION_TYPES.indexOf(currentCollision) + 1) % COLLISION_TYPES.size
        currentCollision = COLLISION_TYPES[index]
        collisionButton.message = Component.literal("碰撞: $currentCollision")
    }

    private fun cycleGender() {
        val index = (GENDERS.indexOf(currentGender) + 1) % GENDERS.size
        currentGender = GENDERS[index]
        genderButton.message = Component.literal("性别: $currentGender")
    }

    private fun saveAndClose() {
        ClientPlayNetworking.send(
            ChiselGuiPacket.UpdateStatuePayload(
                statueId = statueId,
                pokemonName = pokemonNameField.value,
                size = currentSize,
                animation = animationField.value,
                isAnimated = isAnimated,
                text = textField.value,
                material = currentMaterial,
                form = currentForm,
                extraMaterial = currentExtraMaterial,
                collisionType = currentCollision,
                movable = isMovable,
                gender = currentGender,
                isStatic = isStatic
            )
        )

        statue?.let { s ->
            s.pokemonName = pokemonNameField.value
            s.text = textField.value
            s.animation = animationField.value
            s.isAnimated = isAnimated
            s.size = currentSize
            s.material = currentMaterial
            s.form = currentForm
            s.extraMaterial = currentExtraMaterial
            s.collisionType = currentCollision
            s.movable = isMovable
            s.gender = currentGender
            s.isStatic = isStatic
        }

        minecraft?.setScreen(null)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(graphics, mouseX, mouseY, delta)

        val left = width / 2 - 200
        val top = height / 2 - 100
        val guiWidth = 400
        val guiHeight = 420

        graphics.blit(
            TEXTURE,
            left, top,
            0f, 0f,
            guiWidth, guiHeight,
            256, 256
        )

        super.render(graphics, mouseX, mouseY, delta)

        graphics.drawString(font, title, left + 10, top + 5, 0xFFFFFF, false)
    }

    override fun isPauseScreen(): Boolean = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            saveAndClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}


