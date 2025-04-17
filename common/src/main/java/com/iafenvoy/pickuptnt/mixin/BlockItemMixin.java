package com.iafenvoy.pickuptnt.mixin;

import com.iafenvoy.pickuptnt.Constants;
import net.minecraft.block.Blocks;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onPlaceTnt(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = context.getStack();
        if (!stack.isOf(Items.TNT) || stack.isEmpty() || stack.getNbt() == null || !stack.getNbt().contains(Constants.FUSE))
            return;
        World world = context.getWorld();
        if (!Blocks.TNT.isEnabled(world.getEnabledFeatures())) return;
        BlockPos pos = context.getBlockPos().add(context.getSide().getVector());
        if (!world.getBlockState(pos).isReplaceable()) return;
        stack.decrement(1);
        TntEntity tnt = new TntEntity(world, pos.getX(), pos.getY(), pos.getZ(), null);
        tnt.setFuse(stack.getNbt().getInt(Constants.FUSE));
        world.spawnEntity(tnt);
        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
