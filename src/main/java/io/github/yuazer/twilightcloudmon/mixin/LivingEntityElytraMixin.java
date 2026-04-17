package io.github.yuazer.twilightcloudmon.mixin;

import io.github.yuazer.twilightcloudmon.registry.ModFireworkWeapons;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityElytraMixin {

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    private void twilightcloudmon$enableCustomElytra(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        var stack = self.getItemBySlot(EquipmentSlot.CHEST);
        if (stack.getItem().asItem() == ModFireworkWeapons.INSTANCE.getFIREWORK_WINGS()  && ElytraItem.isFlyEnabled(stack)) {
            cir.setReturnValue(true);
        }
    }
}

