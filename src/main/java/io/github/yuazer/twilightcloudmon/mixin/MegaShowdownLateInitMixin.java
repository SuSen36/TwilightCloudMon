package io.github.yuazer.twilightcloudmon.mixin;

import com.github.yajatkaul.mega_showdown.MegaShowdown;
import io.github.yuazer.twilightcloudmon.registry.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MegaShowdown.class, remap = false)
public class MegaShowdownLateInitMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private static void twilight$afterMSDInit(CallbackInfo ci) {
        ModItems.registerMegaStonesAfterMSD();
    }
}

