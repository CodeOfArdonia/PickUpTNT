package com.iafenvoy.pickuptnt.mixin;

import com.iafenvoy.pickuptnt.Constants;
import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.codec.PacketCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.UnaryOperator;

@Mixin(DataComponentTypes.class)
public abstract class DataComponentTypesMixin {
    @Shadow
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        throw new AssertionError("This method should be replaced by mixin.");
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void registerComponentType(CallbackInfo ci) {
        Constants.FUSE_TYPE = register(Constants.FUSE, builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER));
    }
}
