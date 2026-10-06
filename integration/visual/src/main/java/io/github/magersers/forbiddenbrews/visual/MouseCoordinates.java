package io.github.magersers.forbiddenbrews.visual;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Test-only cursor coordinates; does not move the operating-system pointer. */
@Mixin(MouseHandler.class)
public interface MouseCoordinates {
    @Accessor("xpos") void brews$setX(double value);
    @Accessor("ypos") void brews$setY(double value);
}
