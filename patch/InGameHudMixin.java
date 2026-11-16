package com.example.squarehotbar.mixin;

import com.example.squarehotbar.HotbarConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    private static final int CELL = 22; // размер ванильной ячейки с рамкой
    private static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");

    @Shadow @Final private MinecraftClient client;

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack);

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void squarehotbar$render(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) this.client.getCameraEntity();

        double gs = this.client.getWindow().getScaleFactor();
        float scale = (float) (HotbarConfig.cellSize / (CELL * gs));
        float x = (float) (HotbarConfig.offsetX / gs);
        float totalGui = (float) (3.0 * HotbarConfig.cellSize / gs);
        float y = (this.client.getWindow().getScaledHeight() - totalGui) / 2.0F + (float) (HotbarConfig.offsetY / gs);

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0F);
        RenderSystem.scalef(scale, scale, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        this.client.getTextureManager().bindTexture(WIDGETS);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        // 1) ванильные полупрозрачные слоты
        for (int i = 0; i < 9; i++) {
            this.drawTexture(matrices, (i % 3) * CELL, (i / 3) * CELL, 0, 0, 22, 22);
        }

        // 2) обводка выбранного слота
        int selected = player.inventory.selectedSlot;
        if (selected >= 0 && selected < 9) {
            this.drawTexture(matrices, (selected % 3) * CELL - 1, (selected / 3) * CELL - 1, 0, 22, 24, 24);
        }

        // 3) предметы, количество, прочность
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (int i = 0; i < 9; i++) {
            int cx = (i % 3) * CELL + 3;
            int cy = (i / 3) * CELL + 3;
            this.renderHotbarItem(cx, cy, tickDelta, player, player.inventory.getStack(i));
        }

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
        RenderSystem.popMatr
            ix();
    }
}
