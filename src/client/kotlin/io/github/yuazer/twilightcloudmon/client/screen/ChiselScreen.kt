package io.github.yuazer.twilightcloudmon.client.screen

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import io.github.yuazer.twilightcloudmon.client.renderer.entity.StatueRenderer
import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import io.github.yuazer.twilightcloudmon.network.ChiselGuiPacket
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.LightTexture
import net.minecraft.network.chat.Component
import org.joml.Quaternionf
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
class ChiselScreen(
    private val statueId: Int,
    private val initBasePosX: Double = 0.0,
    private val initBasePosY: Double = 0.0,
    private val initBasePosZ: Double = 0.0
) : Screen(Component.translatable("gui.twilightcloudmon.chisel")) {

    companion object {
        private const val PREVIEW_SIZE = 140
        private const val PAD = 5
        private const val ROW = 17
        private const val CTRL_W = 160
        private const val CTRL_H = 14
        private const val BTN_H = 16
        private const val NUDGE_BTN = 14
        private const val NUDGE_STEP = 0.0625

        private val SIZES = listOf("tiny", "small", "normal", "large", "huge")
        private val MATERIALS = listOf("none", "shiny", "special")
        private val EXTRA_MATERIALS = listOf("none", "stone", "gold", "bronze", "iron", "diamond")
        private val COLLISION_TYPES = listOf("NONE", "SOLID", "PUSHOUT")
        private val GENDERS = listOf("default", "male", "female")

        private const val WHITE_BORDER = 0xFFFFFFFF.toInt()
        private const val BLACK_BG = 0xFF000000.toInt()
        private const val WHITE = 0xFFFFFFFF.toInt()
    }

    private var statue: StatueEntity? = null

    private lateinit var pokemonNameField: EditBox
    private lateinit var textField: EditBox
    private lateinit var animationField: EditBox
    private lateinit var formField: EditBox

    private var currentSize = "normal"
    private var currentMaterial = "none"
    private var currentExtraMaterial = "none"
    private var currentCollision = "NONE"
    private var currentGender = "default"
    private var isMovable = false
    private var isStatic = false
    private var isAnimated = true

    private var previewYaw = 0f
    private var previewScale = 50f
    private var saving = false

    private var offsetX = 0.0
    private var offsetY = 0.0
    private var offsetZ = 0.0
    private var rotOffset = 0f

    private var savedPokemonName = ""
    private var savedSize = ""
    private var savedMaterial = ""
    private var savedForm = ""
    private var savedExtraMaterial = ""
    private var savedGender = ""
    private var savedAnimation = ""
    private var savedAnimated = true
    private var basePosX = 0.0
    private var basePosY = 0.0
    private var basePosZ = 0.0
    private var savedOffsetX = 0.0
    private var savedOffsetY = 0.0
    private var savedOffsetZ = 0.0
    private var savedRotOffset = 0f

    private val rightW get() = CTRL_W
    private val guiW get() = PREVIEW_SIZE + PAD * 3 + rightW
    private val guiH get() = PREVIEW_SIZE + PAD * 3 + ROW * 4 + BTN_H

    override fun init() {
        super.init()

        val level = minecraft?.level
        if (level != null) {
            val entity = level.getEntity(statueId)
            if (entity is StatueEntity) {
                statue = entity
                currentSize = entity.size
                currentMaterial = entity.material
                currentExtraMaterial = entity.extraMaterial
                currentCollision = entity.collisionType
                currentGender = entity.gender
                isMovable = entity.movable
                isStatic = entity.isStatic
                isAnimated = entity.isAnimated

                basePosX = initBasePosX
                basePosY = initBasePosY
                basePosZ = initBasePosZ
                offsetX = entity.x - basePosX
                offsetY = entity.y - basePosY
                offsetZ = entity.z - basePosZ
                rotOffset = entity.yRot
                savedOffsetX = offsetX
                savedOffsetY = offsetY
                savedOffsetZ = offsetZ
                savedRotOffset = rotOffset

                savedPokemonName = entity.pokemonName
                savedSize = entity.size
                savedMaterial = entity.material
                savedForm = entity.form
                savedExtraMaterial = entity.extraMaterial
                savedGender = entity.gender
                savedAnimation = entity.animation
                savedAnimated = entity.isAnimated

                entity.noPhysics = true
                entity.setDeltaMovement(0.0, 0.0, 0.0)
            }
        }

        buildControls()
    }

    private fun buildControls() {
        clearWidgets()

        val left = (width - guiW) / 2
        val top = (height - guiH) / 2
        val cx = left + PREVIEW_SIZE + PAD * 2
        var y = top + PAD

        pokemonNameField = field(cx, y, statue?.pokemonName ?: "pikachu", "pokemon_name")
        y += ROW

        formField = field(cx, y, statue?.form ?: "default", "form")
        y += ROW

        animationField = field(cx, y, statue?.animation ?: "idle", "animation")
        y += ROW

        textField = field(cx, y, statue?.text ?: "", "display_text")
        y += ROW + 1

        fun cycleBtn(yy: Int, key: String, valStr: String, action: Runnable): Button {
            val label = Component.translatable("gui.twilightcloudmon.chisel.$key").append(": $valStr")
            val b = Button.builder(label) { action.run() }.bounds(cx, yy, CTRL_W, BTN_H).build()
            addRenderableWidget(b)
            return b
        }

        cycleBtn(y, "size", optStr("size", currentSize)) { cycleIn(SIZES, currentSize) { currentSize = it; rebuild() } }
        y += ROW
        cycleBtn(y, "material", optStr("material", currentMaterial)) { cycleIn(MATERIALS, currentMaterial) { currentMaterial = it; rebuild() } }
        y += ROW
        cycleBtn(y, "extra_material", optStr("extra_material", currentExtraMaterial)) { cycleIn(EXTRA_MATERIALS, currentExtraMaterial) { currentExtraMaterial = it; rebuild() } }
        y += ROW
        cycleBtn(y, "gender", optStr("gender", currentGender)) { cycleIn(GENDERS, currentGender) { currentGender = it; rebuild() } }
        y += ROW
        cycleBtn(y, "collision", optStr("collision", currentCollision.lowercase())) { cycleIn(COLLISION_TYPES, currentCollision) { currentCollision = it; rebuild() } }
        y += ROW
        cycleBtn(y, "animated", boolStr(isAnimated)) { isAnimated = !isAnimated; rebuild() }
        y += ROW
        cycleBtn(y, "movable", boolStr(isMovable)) { isMovable = !isMovable; rebuild() }
        y += ROW
        cycleBtn(y, "static", boolStr(isStatic)) { isStatic = !isStatic; rebuild() }

        val bottomAreaTop = top + PAD + PREVIEW_SIZE + PAD
        val bx = left + PAD
        val nudgeW = NUDGE_BTN
        val labelSpace = PREVIEW_SIZE - nudgeW * 2

        fun nudgeRow(ny: Int, labelKey: String, onMinus: Runnable, onPlus: Runnable) {
            addRenderableWidget(Button.builder(Component.literal("-")) { onMinus.run() }.bounds(bx, ny, nudgeW, NUDGE_BTN).build())
            addRenderableWidget(Button.builder(Component.literal("+")) { onPlus.run() }.bounds(bx + nudgeW + labelSpace, ny, nudgeW, NUDGE_BTN).build())
        }

        var ny = bottomAreaTop
        nudgeRow(ny, "pos_x", { offsetX -= NUDGE_STEP; syncEntityTransform(); rebuild() }, { offsetX += NUDGE_STEP; syncEntityTransform(); rebuild() })
        ny += ROW
        nudgeRow(ny, "pos_y", { offsetY -= NUDGE_STEP; syncEntityTransform(); rebuild() }, { offsetY += NUDGE_STEP; syncEntityTransform(); rebuild() })
        ny += ROW
        nudgeRow(ny, "pos_z", { offsetZ -= NUDGE_STEP; syncEntityTransform(); rebuild() }, { offsetZ += NUDGE_STEP; syncEntityTransform(); rebuild() })
        ny += ROW
        nudgeRow(ny, "rot", { rotOffset -= 15f; syncEntityTransform(); rebuild() }, { rotOffset += 15f; syncEntityTransform(); rebuild() })

        val saveY = ny + ROW + PAD
        val thirdW = (CTRL_W - 4) / 3
        addRenderableWidget(
            Button.builder(Component.translatable("gui.twilightcloudmon.chisel.save")) { saveAndClose() }
                .bounds(cx, saveY, thirdW, BTN_H).build()
        )
        addRenderableWidget(
            Button.builder(Component.translatable("gui.twilightcloudmon.chisel.reset")) {
                previewYaw = 0f
                previewScale = 50f
            }.bounds(cx + thirdW + 2, saveY, thirdW, BTN_H).build()
        )
        addRenderableWidget(
            Button.builder(Component.translatable("gui.twilightcloudmon.chisel.reset_pos")) {
                offsetX = 0.0
                offsetY = 0.0
                offsetZ = 0.0
                rotOffset = 0f
                syncEntityTransform()
                rebuild()
            }.bounds(cx + (thirdW + 2) * 2, saveY, thirdW, BTN_H).build()
        )
    }

    private fun field(x: Int, y: Int, value: String, key: String): EditBox {
        val box = EditBox(font, x, y, CTRL_W, CTRL_H, Component.translatable("gui.twilightcloudmon.chisel.$key"))
        box.setMaxLength(128)
        box.value = value
        box.setHint(Component.translatable("gui.twilightcloudmon.chisel.$key"))
        addRenderableWidget(box)
        return box
    }

    private fun boolStr(v: Boolean) =
        Component.translatable(if (v) "gui.twilightcloudmon.chisel.yes" else "gui.twilightcloudmon.chisel.no").string

    private fun optStr(category: String, value: String) =
        Component.translatable("gui.twilightcloudmon.chisel.$category.${value.lowercase()}").string

    private inline fun cycleIn(list: List<String>, cur: String, apply: (String) -> Unit) =
        apply(list[(list.indexOf(cur) + 1) % list.size])

    private fun rebuild() = buildControls()

    private fun syncEntityTransform() {
        statue?.let { s ->
            val wx = basePosX + offsetX
            val wy = basePosY + offsetY
            val wz = basePosZ + offsetZ
            val wr = rotOffset
            s.lerpTo(wx, wy, wz, wr, s.xRot, 0)
            s.setPos(wx, wy, wz)
            s.xo = wx
            s.yo = wy
            s.zo = wz
            s.yRot = wr
            s.yRotO = wr
            s.yHeadRot = wr
            s.yBodyRot = wr
            s.setDeltaMovement(0.0, 0.0, 0.0)
            s.noPhysics = true
        }
    }

    private fun applyPreviewToEntity() {
        statue?.let { s ->
            s.pokemonName = pokemonNameField.value
            s.form = formField.value
            s.animation = animationField.value
            s.size = currentSize
            s.material = currentMaterial
            s.extraMaterial = currentExtraMaterial
            s.gender = currentGender
            s.isAnimated = isAnimated
        }
    }

    private fun restoreEntity() {
        statue?.let { s ->
            s.pokemonName = savedPokemonName
            s.form = savedForm
            s.animation = savedAnimation
            s.size = savedSize
            s.material = savedMaterial
            s.extraMaterial = savedExtraMaterial
            s.gender = savedGender
            s.isAnimated = savedAnimated
            val origX = basePosX + savedOffsetX
            val origY = basePosY + savedOffsetY
            val origZ = basePosZ + savedOffsetZ
            s.moveTo(origX, origY, origZ, savedRotOffset, s.xRot)
            s.yRot = savedRotOffset
            s.yRotO = savedRotOffset
            s.noPhysics = true
            s.setDeltaMovement(0.0, 0.0, 0.0)
        }
    }

    private fun saveAndClose() {
        saving = true

        val wx = basePosX + offsetX
        val wy = basePosY + offsetY
        val wz = basePosZ + offsetZ
        val wr = rotOffset

        ClientPlayNetworking.send(
            ChiselGuiPacket.UpdateStatuePayload(
                statueId = statueId,
                pokemonName = pokemonNameField.value,
                size = currentSize,
                animation = animationField.value,
                isAnimated = isAnimated,
                text = textField.value,
                material = currentMaterial,
                form = formField.value,
                extraMaterial = currentExtraMaterial,
                collisionType = currentCollision,
                movable = isMovable,
                gender = currentGender,
                isStatic = isStatic,
                posX = wx,
                posY = wy,
                posZ = wz,
                rotation = wr
            )
        )

        statue?.let { s ->
            s.pokemonName = pokemonNameField.value
            s.text = textField.value
            s.animation = animationField.value
            s.isAnimated = isAnimated
            s.size = currentSize
            s.material = currentMaterial
            s.form = formField.value
            s.extraMaterial = currentExtraMaterial
            s.collisionType = currentCollision
            s.movable = isMovable
            s.gender = currentGender
            s.isStatic = isStatic
            s.lerpTo(wx, wy, wz, wr, s.xRot, 0)
            s.setPos(wx, wy, wz)
            s.xo = wx
            s.yo = wy
            s.zo = wz
            s.yRot = wr
            s.yRotO = wr
            s.noPhysics = true
            s.setDeltaMovement(0.0, 0.0, 0.0)
        }

        minecraft?.setScreen(null)
    }

    override fun removed() {
        if (!saving) {
            restoreEntity()
        }
        super.removed()
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        syncEntityTransform()

        val left = (width - guiW) / 2
        val top = (height - guiH) / 2
        val pLeft = left + PAD
        val pTop = top + PAD
        val pRight = pLeft + PREVIEW_SIZE
        val pBottom = pTop + PREVIEW_SIZE

        graphics.fill(pLeft - 1, pTop - 1, pRight + 1, pBottom + 1, WHITE_BORDER)
        graphics.fill(pLeft, pTop, pRight, pBottom, BLACK_BG)

        graphics.enableScissor(pLeft, pTop, pRight, pBottom)
        renderPreview(graphics, pLeft, pTop, pRight, pBottom)
        graphics.disableScissor()

        val bottomAreaTop = pBottom + PAD
        val bx = left + PAD
        val nudgeW = NUDGE_BTN
        val labelSpace = PREVIEW_SIZE - nudgeW * 2
        val labelX = bx + nudgeW + labelSpace / 2

        fun drawNudgeLabel(ny: Int, key: String, valueStr: String) {
            val label = Component.translatable("gui.twilightcloudmon.chisel.$key").append(": $valueStr")
            graphics.drawCenteredString(font, label, labelX, ny + (NUDGE_BTN - 8) / 2, WHITE)
        }

        var ny = bottomAreaTop
        drawNudgeLabel(ny, "pos_x", String.format("%.2f", offsetX))
        ny += ROW
        drawNudgeLabel(ny, "pos_y", String.format("%.2f", offsetY))
        ny += ROW
        drawNudgeLabel(ny, "pos_z", String.format("%.2f", offsetZ))
        ny += ROW
        drawNudgeLabel(ny, "rot", "${(rotOffset % 360).toInt()}\u00B0")

        super.render(graphics, mouseX, mouseY, delta)
    }

    private fun renderPreview(graphics: GuiGraphics, x1: Int, y1: Int, x2: Int, y2: Int) {
        val s = statue ?: return
        val mc = minecraft ?: return

        applyPreviewToEntity()

        // Zero entity rotation so animation system doesn't double-rotate
        val prevYaw = s.yRot
        val prevYawO = s.yRotO
        val prevHead = s.yHeadRot
        val prevHeadO = s.yHeadRotO
        val prevBody = s.yBodyRot
        s.yRot = 0f
        s.yRotO = 0f
        s.yHeadRot = 0f
        s.yHeadRotO = 0f
        s.yBodyRot = 0f

        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f
        val w = maxOf(s.scale, 0.1f)
        val entityScale = previewScale / w

        val pose = graphics.pose()
        pose.pushPose()
        pose.translate(centerX.toDouble(), centerY.toDouble(), 50.0)
        pose.scale(entityScale, entityScale, -entityScale)
        pose.translate(0f, s.bbHeight / 2f, 0f)
        val quat = Quaternionf().rotateZ(Math.PI.toFloat())
        quat.rotateY(Math.PI.toFloat() + Math.toRadians(previewYaw.toDouble()).toFloat())
        pose.mulPose(quat)

        Lighting.setupForEntityInInventory()
        StatueRenderer.previewMode = true
        val dispatcher = mc.entityRenderDispatcher
        dispatcher.setRenderShadow(false)
        RenderSystem.runAsFancy {
            dispatcher.render(s, 0.0, 0.0, 0.0, 0f, 1f, pose, graphics.bufferSource(), LightTexture.FULL_BRIGHT)
        }
        graphics.flush()
        dispatcher.setRenderShadow(true)
        StatueRenderer.previewMode = false
        pose.popPose()
        Lighting.setupFor3DItems()

        s.yRot = prevYaw
        s.yRotO = prevYawO
        s.yHeadRot = prevHead
        s.yHeadRotO = prevHeadO
        s.yBodyRot = prevBody

        val displayName = s.displayName.string
        if (displayName.isNotEmpty()) {
            graphics.drawCenteredString(font, displayName, centerX.toInt(), y1 + 4, WHITE)
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button == 0 && isInPreview(mouseX, mouseY)) {
            previewYaw += deltaX.toFloat() * 1.5f
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        if (isInPreview(mouseX, mouseY)) {
            previewScale = (previewScale + verticalAmount.toFloat() * 5f).coerceIn(5f, 200f)
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    private fun isInPreview(mx: Double, my: Double): Boolean {
        val left = (width - guiW) / 2
        val pLeft = left + PAD
        val pRight = pLeft + PREVIEW_SIZE
        val pTop = (height - guiH) / 2 + PAD
        val pBottom = pTop + PREVIEW_SIZE
        return mx >= pLeft && mx <= pRight && my >= pTop && my <= pBottom
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
