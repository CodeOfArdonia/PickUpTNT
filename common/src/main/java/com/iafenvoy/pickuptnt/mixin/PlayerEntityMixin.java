package com.iafenvoy.pickuptnt.mixin;

import com.iafenvoy.pickuptnt.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    public abstract boolean giveItemStack(ItemStack stack);

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void handleTntAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof TntEntity tnt)) return;
        if (tnt.getFuse() >= 0) {
            ItemStack stack = new ItemStack(Items.TNT);
            stack.set(Constants.FUSE_TYPE, tnt.getFuse());
            this.giveItemStack(stack);
            target.discard();
            ci.cancel();
        }
    }
}
