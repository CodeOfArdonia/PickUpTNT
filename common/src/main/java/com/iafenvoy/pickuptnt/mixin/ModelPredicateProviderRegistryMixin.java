package com.iafenvoy.pickuptnt.mixin;

import com.iafenvoy.pickuptnt.Constants;
import com.iafenvoy.pickuptnt.PickUpTnt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ModelPredicateProviderRegistry.class)
public abstract class ModelPredicateProviderRegistryMixin {
    @Shadow
    private static void register(Item item, Identifier id, ClampedModelPredicateProvider provider) {
        throw new AssertionError("This method should be replaced by mixin.");
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void afterClientInit(CallbackInfo ci) {
        register(Items.TNT, Identifier.of(PickUpTnt.MOD_ID, "burning"), (stack, world, entity, seed) -> stack.contains(Constants.FUSE_TYPE) ? 1 : 0);
    }
}
