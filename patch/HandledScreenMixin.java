package com.example.squarehotbar.mixin;

import com.example.squarehotbar.HotbarConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    private static final int CELL = 22;
    private static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void squarehotbar$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.client == null || this.client.player == null) {
            return;
        }
        PlayerEntity player = this.client.player;

        double gs = this.client.getWindow().getScaleFactor();
        float scale = (float) (HotbarConfig.cellSize / (CELL * gs));
        float x = (float) (HotbarConfig.offsetX / gs);
        float totalGui = (float) (3.0 * HotbarConfig.cellSize / gs);
        float y = (this.client.getWindow().getScaledHeight() - totalGui) / 2.0F + (float) (HotbarConfig.offsetY / gs);

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 250.0F);
        RenderSystem.scalef(scale, scale, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        this.client.getTextureManager().bindTexture(WIDGETS);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 9; i++) {
            this.drawTexture(matrices, (i % 3) * CELL, (i / 3) * CELL, 0, 0, 22, 22);
        }

        int selected = player.inventory.selectedSlot;
        if (selected >= 0 && selected < 9) {
            this.drawTexture(matrices, (selected % 3) * CELL - 1, (selected / 3) * CELL - 1, 0, 22, 24, 24);
        }

        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (int i = 0; i < 9; i++) {
            int cx = (i % 3) * CELL + 3;
            int cy = (i / 3) * CELL + 3;
            ItemStack stack = player.inventory.getStack(i);
            if (!stack.isEmpty()) {
                this.client.getItemRenderer().renderInGuiWithOverrides(player, stack, cx, cy);
                this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, stack, cx, cy);
            }
        }

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
}
