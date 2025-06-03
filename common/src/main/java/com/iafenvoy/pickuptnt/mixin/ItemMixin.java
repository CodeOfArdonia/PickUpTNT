package com.iafenvoy.pickuptnt.mixin;

import com.iafenvoy.pickuptnt.Constants;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void onTickTnt(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!stack.isOf(Items.TNT) || stack.isEmpty()) return;
        if (stack.contains(Constants.FUSE_TYPE)) {
            int fuse = stack.get(Constants.FUSE_TYPE);
            if (fuse == 0) {
                if (!world.isClient)
                    world.createExplosion(null, entity.getX(), entity.getBodyY(0.0625), entity.getZ(), 4.0F * stack.getCount(), World.ExplosionSourceType.TNT);
                stack.setCount(0);
            } else stack.set(Constants.FUSE_TYPE, fuse - 1);
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void handleTntBehaviour(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (!stack.isOf(Items.TNT) || stack.isEmpty()) return;
        if (stack.contains(Constants.FUSE_TYPE) && !world.isClient) {//Ender Pearl Logic
            TntEntity tnt = new TntEntity(world, user.getX(), user.getBodyY(0.0625), user.getZ(), user);
            float pitch = user.getPitch();
            float yaw = user.getYaw();
            float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
            float g = -MathHelper.sin((pitch + 0.0F) * 0.017453292F);
            float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
            Random random = ((EntityAccessor) tnt).getRandom();
            Vec3d vec = new Vec3d(f, g, h).normalize().add(random.nextTriangular(0.0, 0.0172275), random.nextTriangular(0.0, 0.0172275), random.nextTriangular(0.0, 0.0172275)).multiply(1.5F);
            tnt.setYaw((float) (MathHelper.atan2(vec.x, vec.z) * 57.2957763671875));
            tnt.setPitch((float) (MathHelper.atan2(vec.y, vec.horizontalLength()) * 57.2957763671875));
            tnt.prevYaw = tnt.getYaw();
            tnt.prevPitch = tnt.getPitch();
            Vec3d vec3d = user.getVelocity();
            tnt.setVelocity(vec.add(vec3d.x, user.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
            tnt.setFuse(stack.get(Constants.FUSE_TYPE));
            world.spawnEntity(tnt);
            stack.decrement(1);
            cir.setReturnValue(ActionResult.SUCCESS.withNewHandStack(stack));
        } else if (hand == Hand.MAIN_HAND) {
            ItemStack offhand = user.getOffHandStack();
            Optional<RegistryEntry<Enchantment>> optional = Optional.ofNullable(world.getRegistryManager()).map(x -> x.get(RegistryKeys.ENCHANTMENT)).map(x -> x.getEntry(Enchantments.FIRE_ASPECT)).flatMap(x -> x);
            if (offhand.isOf(Items.FLINT_AND_STEEL)) offhand.damage(1, user, EquipmentSlot.OFFHAND);
            else if (!offhand.isIn(Constants.PRIME_TNT) && optional.isPresent() && EnchantmentHelper.getLevel(optional.get(), offhand) == 0)
                return;
            if (stack.getCount() == 1 || user.isSneaking())
                stack.set(Constants.FUSE_TYPE, Constants.DEFAULT_FUSE);
            else {
                ItemStack newStack = stack.split(1);
                newStack.set(Constants.FUSE_TYPE, Constants.DEFAULT_FUSE);
                user.giveItemStack(newStack);
            }
            user.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1, 1);
            world.emitGameEvent(user, GameEvent.PRIME_FUSE, user.getPos());
            user.incrementStat(Stats.USED.getOrCreateStat(offhand.getItem()));
        }
    }
}
