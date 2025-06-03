package com.iafenvoy.pickuptnt;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.component.ComponentType;

public class Constants {
    public static final String FUSE = "fuse";
    public static final int DEFAULT_FUSE = 80;
    public static final TagKey<Item> PRIME_TNT = TagKey.of(RegistryKeys.ITEM, Identifier.of(PickUpTnt.MOD_ID, "prime_tnt"));
    public static ComponentType<Integer> FUSE_TYPE = null;
}
