package com.example.squarehotbar.mixin;

import com.example.squarehotbar.HotbarConfigScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    private boolean squarehotbar$canOpen(long window) {
        return window == this.client.getWindow().getHandle()
                && this.client.currentScreen == null
                && this.client.world != null;
    }

    // нажатие клавиши H
    @Inject(method = "onKey", at = @At("HEAD"))
    private void squarehotbar$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key == GLFW.GLFW_KEY_H && action == GLFW.GLFW_PRESS && squarehotbar$canOpen(window)) {
            this.client.openScreen(new HotbarConfigScreen());
        }
    }

    // запасной вариант: набранная буква "h" (экранная клавиатура)
    @Inject(method = "onChar", at = @At("HEAD"))
    private void squarehotbar$onChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if ((codePoint == 'h' || codePoint == 'H') && squarehotbar$canOpen(window)) {
            this.client.openScreen(new HotbarConfigScreen());
        }
    }
}
