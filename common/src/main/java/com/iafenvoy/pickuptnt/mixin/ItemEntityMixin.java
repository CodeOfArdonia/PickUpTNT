package com.iafenvoy.pickuptnt.mixin;

import com.iafenvoy.pickuptnt.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onItemEntityTick(CallbackInfo ci) {
        ItemStack stack = this.getStack();
        if (!stack.isOf(Items.TNT) || stack.isEmpty()) return;
        if (stack.contains(Constants.FUSE_TYPE)) {
            int fuse = stack.get(Constants.FUSE_TYPE);
            if (fuse == 0) {
                if (!this.getWorld().isClient)
                    this.getWorld().createExplosion(null, this.getX(), this.getBodyY(0.0625), this.getZ(), 4.0F * stack.getCount(), World.ExplosionSourceType.TNT);
                this.discard();
                ci.cancel();
            } else stack.set(Constants.FUSE_TYPE, fuse - 1);
        }
    }
}
