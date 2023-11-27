package com.lazrproductions.cuffed.init;

import java.util.UUID;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ModAttributes {
    public static final AttributeModifier HANDCUFFED_ATTIRBUTE = new AttributeModifier(UUID.fromString("3b44d328-0746-45c9-85e3-c2df6c70d4a3"), "handcuffed", -1,
        AttributeModifier.Operation.MULTIPLY_BASE);
}
