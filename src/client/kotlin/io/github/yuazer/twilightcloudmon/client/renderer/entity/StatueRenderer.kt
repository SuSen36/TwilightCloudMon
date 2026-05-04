package io.github.yuazer.twilightcloudmon.client.renderer.entity

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.scheduling.SchedulingTracker
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PosablePokemonEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.VaryingModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import io.github.yuazer.twilightcloudmon.entity.StatueEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

@Environment(EnvType.CLIENT)
class StatueRenderer(context: EntityRendererProvider.Context) : EntityRenderer<StatueEntity>(context) {
    
    private val model = PosablePokemonEntityModel()
    private val renderCache = mutableMapOf<Int, StatueRenderData>()

    private data class StatueSignature(
        val pokemonName: String,
        val form: String,
        val gender: String,
        val material: String,
        val extraMaterial: String,
        val size: String
    )

    private data class StatueRenderData(
        val signature: StatueSignature,
        val pokemon: Pokemon,
        val state: StatuePosableState,
        var poser: PosableModel,
        var texture: ResourceLocation
    )

    override fun getTextureLocation(entity: StatueEntity): ResourceLocation {
        return renderCache[entity.id]?.texture ?: cobblemonResource("textures/pokemon/default.png")
    }

    override fun shouldShowName(entity: StatueEntity): Boolean {
        return entity.text.isNotBlank() || super.shouldShowName(entity)
    }

    override fun render(
        entity: StatueEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {
        val renderData = resolveRenderData(entity, partialTicks) ?: return
        val pokemon = renderData.pokemon
        val state = renderData.state
        val poser = renderData.poser

        val animationName = resolveAnimationName(poser, entity.animation)
        state.prepare(poser, pokemon, animationName, entity, partialTicks)

        val layers = VaryingModelRepository.getLayers(pokemon.species.resourceIdentifier, state)
        val texture = VaryingModelRepository.getTexture(pokemon.species.resourceIdentifier, state)
        renderData.texture = texture

        val sizeScale = computeScale(entity.size)
        prepareRenderContext(entity, pokemon, state, poser, texture, sizeScale)
        poser.setLayerContext(buffer, state, layers)

        poseStack.pushPose()
        val scale = pokemon.form.baseScale * sizeScale
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - entityYaw))
        poseStack.scale(-scale, -scale, scale)

        val vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, RenderType.entityCutout(texture), false, false)
        poser.applyAnimations(entity, state, 0f, 0f, state.animationTicks(entity), 0f, 0f)
        poser.render(model.context, poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -0x1)

        poser.red = 1f
        poser.green = 1f
        poser.blue = 1f
        poser.resetLayerContext()

        poseStack.popPose()

        if (entity.isRemoved) {
            renderCache.remove(entity.id)
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight)
    }

    private fun resolveRenderData(entity: StatueEntity, partialTicks: Float): StatueRenderData? {
        val signature = StatueSignature(
            pokemonName = entity.pokemonName,
            form = entity.form,
            gender = entity.gender,
            material = entity.material,
            extraMaterial = entity.extraMaterial,
            size = entity.size
        )
        val cached = renderCache[entity.id]
        if (cached != null && cached.signature == signature) {
            if (entity.isAnimated) {
                cached.state.updateAge(entity.tickCount)
            }
            cached.state.updatePartialTicks(if (entity.isStatic) 0f else partialTicks)
            return cached
        }

        val pokemon = buildPokemon(signature) ?: return null
        val state = StatuePosableState(entity, partialTicks, SchedulingTracker())
        applyMaterialAspects(pokemon, signature)
        state.currentAspects = pokemon.aspects
        val poser = VaryingModelRepository.getPoser(pokemon.species.resourceIdentifier, state)
        val texture = VaryingModelRepository.getTexture(pokemon.species.resourceIdentifier, state)
        val data = StatueRenderData(signature, pokemon, state, poser, texture)
        renderCache[entity.id] = data
        return data
    }

    private fun buildPokemon(signature: StatueSignature): Pokemon? {
        val species = resolveSpecies(signature.pokemonName) ?: return null
        val props = PokemonProperties()
        props.species = if (species.resourceIdentifier.namespace == Cobblemon.MODID) {
            species.resourceIdentifier.path
        } else {
            species.resourceIdentifier.toString()
        }
        props.form = species.forms.firstOrNull { it.name.equals(signature.form, true) }?.name
        props.gender = parseGender(signature.gender)
        props.shiny = signature.material.equals("shiny", true)
        return props.create()
    }
    
    private fun applyMaterialAspects(pokemon: Pokemon, signature: StatueSignature) {
        val aspects = mutableSetOf<String>()
        if (!signature.material.equals("none", true)) {
            aspects.add(signature.material.lowercase())
        }
        if (signature.extraMaterial.isNotBlank() && !signature.extraMaterial.equals("none", true)) {
            aspects.add(signature.extraMaterial.lowercase())
        }
        if (aspects.isNotEmpty()) {
            pokemon.forcedAspects = pokemon.forcedAspects + aspects
            pokemon.updateAspects()
        }
    }

    private fun resolveSpecies(rawName: String): com.cobblemon.mod.common.pokemon.Species? {
        val trimmed = rawName.trim()
        if (trimmed.isEmpty()) return null
        
        val speciesRegistry = PokemonSpecies
        
        speciesRegistry.getByName(trimmed)?.let { return it }

        return try {
            speciesRegistry.getByIdentifier(ResourceLocation.parse(trimmed))
        } catch (_: Exception) {
            null
        }
    }

    private fun parseGender(value: String): Gender? = when (value.lowercase()) {
        "male" -> Gender.MALE
        "female" -> Gender.FEMALE
        "genderless" -> Gender.GENDERLESS
        "default" -> null
        else -> null
    }

    private fun computeScale(size: String): Float {
        return when (size.lowercase()) {
            "tiny" -> 0.5f
            "small" -> 0.75f
            "normal" -> 1.0f
            "large" -> 1.5f
            "huge" -> 2.0f
            else -> 1.0f
        }
    }

    private fun prepareRenderContext(
        entity: StatueEntity,
        pokemon: Pokemon,
        poserState: StatuePosableState,
        poser: PosableModel,
        texture: ResourceLocation,
        sizeScale: Float
    ) {
        model.context.put(RenderContext.RENDER_STATE, RenderContext.RenderState.WORLD)
        model.context.put(RenderContext.ENTITY, entity)
        model.context.put(RenderContext.POSABLE_STATE, poserState)
        model.context.put(RenderContext.ASPECTS, pokemon.aspects)
        model.context.put(RenderContext.SPECIES, pokemon.species.resourceIdentifier)
        model.context.put(RenderContext.TEXTURE, texture)
        model.context.put(RenderContext.SCALE, pokemon.form.baseScale * sizeScale)
        poser.context = model.context
    }

    private fun resolveAnimationName(poser: PosableModel, requested: String): String {
        if (requested.isNotBlank() && poser.poses.containsKey(requested)) {
            return requested
        }
        return poser.poses.keys.firstOrNull { it == "idle" } ?: poser.poses.keys.firstOrNull() ?: "idle"
    }

    private class StatuePosableState(
        private val statue: StatueEntity,
        partialTicks: Float,
        override val schedulingTracker: SchedulingTracker
    ) : PosableState() {
        init {
            updateAge(if (statue.isAnimated) statue.tickCount else 0)
            updatePartialTicks(if (statue.isStatic) 0f else partialTicks)
        }

        fun prepare(
            model: PosableModel,
            pokemon: Pokemon,
            animation: String,
            entity: StatueEntity,
            partialTicks: Float
        ) {
            updateAge(if (entity.isAnimated) entity.tickCount else 0)
            updatePartialTicks(if (entity.isStatic) 0f else partialTicks)
            currentModel = model
            currentAspects = pokemon.aspects
            currentPose = animation
            setPose(animation)
            updateLocatorPosition(entity.position())
        }

        fun animationTicks(entity: StatueEntity): Float {
            if (!entity.isAnimated) return 0f
            val baseAge = if (entity.isStatic) 0f else entity.tickCount.toFloat()
            return baseAge + currentPartialTicks
        }

        override fun getEntity(): Entity = statue

        override fun updatePartialTicks(partialTicks: Float) {
            currentPartialTicks = partialTicks
            schedulingTracker.update(0f)
        }
    }
}