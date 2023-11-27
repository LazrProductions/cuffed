package com.lazrproductions.cuffed.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScreenUtils {
    public static void drawTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
            float uvX, float uvY, int uvBoundsX, int uvBoundsY, int textureWidth, int textureHeight) {
        graphics.blit(texture, x, y, width, height, uvX, uvY, uvBoundsX, uvBoundsY, textureWidth, textureHeight);
    }

    /**
     * Renders a progress bar from a sprite-sheet, frames are picked from left to right top to bottom.
     * @param texture The sprite-sheet to render.
     * @param progress The progress of the progress bar, from 0-1
     * @param framesH The amount of frames stacked horizontally in the sprite-sheet
     * @param framesV The amount of frames stacked vertically in the sprite-sheet
     * @param uvStartX Where the uv of this texture starts on the x
     * @param uvStartY Where the uv of this texture start on the y
     * @param uvBoundsX The size of each frame in the sheet, on the x.
     * @param uvBoundsY The size of each frame in the sheet, on the y.
     * @param textureWidth The width of the sprite-sheet 
     * @param textureHeight The height of the sprite-sheet
     */
    public static void drawProgressBar(GuiGraphics graphics, ResourceLocation texture, float progress, int framesH, int framesV, int x, int y, int width, int height,
        float uvStartX, float uvStartY, int uvBoundsX, int uvBoundsY, int textureWidth, int textureHeight) {
        
        int frames = (framesH*framesV);
        int frameIndex = Mth.floor((float)frames*progress);
        int column = Mth.floor((float)frameIndex%(float)framesH);
        int row = Mth.floor((float)frameIndex/(float)framesH);
        
        float uvXF = uvStartX+(column*uvBoundsX);
        float uvYF = uvStartY+(row*uvBoundsY);

        graphics.blit(texture, x, y, width, height, uvXF, uvYF, uvBoundsX, uvBoundsY, textureWidth, textureHeight);
    }

    public static boolean mouseInArea(double mouseX, double mouseY, int x1, int y1, int x2, int y2) {
        return (mouseX >= x1 && mouseX <= x2 &&
                mouseY >= y1 && mouseY <= y2);
    }


}

