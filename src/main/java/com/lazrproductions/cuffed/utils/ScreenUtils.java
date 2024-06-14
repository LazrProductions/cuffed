package com.lazrproductions.cuffed.utils;

import java.util.List;

import javax.annotation.Nonnull;

import org.joml.Matrix4f;
import org.joml.Vector2i;

import com.lazrproductions.cuffed.CuffedMod;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ScreenUtils {
    static final ResourceLocation GENERIC_PROGRESS_BAR_LOCATION = new ResourceLocation(CuffedMod.MODID, "textures/gui/progress_bar.png");

    
    
    public static void drawTexture(GuiGraphics graphics, BlitCoordinates pos, Texture texture) {
        graphics.blit(texture.getResourceLocation(), pos.getX(), pos.getY(), pos.getWidth(), pos.getHeight(),
                texture.getU(), texture.getV(), texture.getBoundsX(), texture.getBoundsY(), texture.getWidth(),
                texture.getHeight());
    }
    public static void drawTexture(GuiGraphics graphics, BlitCoordinates pos, float rotation, Texture texture) {
        drawTexture(graphics, pos, rotation, (pos.getWidth() / 2), (pos.getHeight() / 2), texture);
    }
    public static void drawTexture(GuiGraphics graphics, BlitCoordinates pos, float rotation, float rotateAroundX, float rotateAroundY, Texture texture) {
        graphics.pose().pushPose();
        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(rotation), pos.getX() + rotateAroundX, pos.getY() + rotateAroundY, 0);

        graphics.blit(texture.getResourceLocation(), pos.getX(), pos.getY(), pos.getWidth(), pos.getHeight(),
                texture.getU(), texture.getV(), texture.getBoundsX(), texture.getBoundsY(), texture.getWidth(),
                texture.getHeight());

        graphics.pose().popPose();
    }


    public static void drawProgressBar(GuiGraphics graphics, BlitCoordinates pos, Texture texture, float progress, int totalFramesHorizontal, int totalFramesVertically) {

        int frames = (totalFramesHorizontal * totalFramesVertically);
        int frameIndex = Mth.floor((float) frames * progress);
        int column = Mth.floor((float) frameIndex % (float) totalFramesHorizontal);
        int row = Mth.floor((float) frameIndex / (float) totalFramesHorizontal);

        float uvXF = texture.getU() + (column * texture.getBoundsX());
        float uvYF = texture.getV() + (row * texture.getBoundsY());

        graphics.blit(texture.getResourceLocation(), pos.getX(), pos.getY(), pos.getWidth(), pos.getHeight(), uvXF, uvYF, texture.getBoundsX(), texture.getBoundsY(), texture.getWidth(), texture.getHeight());
    }
    public static void drawGenericProgressBar(GuiGraphics graphics, BlitCoordinates pos, float progress) {
        graphics.fill(pos.getX(), pos.getY(), pos.getX() + pos.getWidth(), pos.getY() + 2, 1325400064);
        int i = Mth.hsvToRgb(progress / 3.0F, 1.0F, 1.0F);
        graphics.fill(pos.getX(), pos.getY(), pos.getX() + (int) (pos.getWidth() * progress), pos.getY() + 1, i | -16777216);
    }
    public static void drawGenericProgressBarUpright(GuiGraphics graphics, BlitCoordinates pos, float progress, float partialTick) {
        progress = Mth.clamp(progress, 0, 2);

        int shakeX = 0;
        if(progress > 1)
            shakeX = Mth.floor(Mth.sin(partialTick*(100f*(progress - 1)) * 2f)-1);

        graphics.fill(pos.getX() + shakeX, pos.getY(), pos.getX() + 2 + shakeX, pos.getY() + pos.getHeight(), 1325400064);
        int i = Mth.hsvToRgb(progress / 3.0F, 1.0F, 1.0F);
        progress = Mth.clamp(progress, 0, 1f);
        graphics.fill(pos.getX() + shakeX, pos.getY() + pos.getHeight(), pos.getX() + 1 + shakeX, pos.getY() + pos.getHeight() - (int) (pos.getHeight() * progress), i | -16777216);
    }


    public static void renderLabel(Minecraft instance, GuiGraphics graphics, int x, int y, List<Component> list, int color, boolean renderShadow) {
        int space = 15;
        int width = 0;
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            width = Math.max(width, instance.font.width(text) + 10);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            graphics.drawString(instance.font, text,
                    x - instance.font.width(text) / 2,
                    y + ((list.size() / 2) * space + (space * i)),
                    color, renderShadow);
        }
        RenderSystem.enableDepthTest();
    }
    public static void renderLabel(Minecraft instance, GuiGraphics graphics, int x, int y, List<Component> list, int color) {
        renderLabel(instance, graphics, x, y, list, color, true);
    }


    public static void drawParagraph(Minecraft instance, GuiGraphics graphics, int x, int y, List<Component> list, int maxWidth, int color, boolean renderShadow) {        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        
        FormattedText text = FormattedText.composite(list);
        graphics.drawWordWrap(instance.font, text, x, y, maxWidth, color);

        RenderSystem.enableDepthTest();
    }
    public static void drawParagraph(Minecraft instance, GuiGraphics graphics, int x, int y, List<Component> list, int maxWidth, int color) {
        drawParagraph(instance, graphics, x, y, list, maxWidth, color, true);
    }
    public static void drawParagraph(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, List<Component> list, int color) {
        drawParagraph(instance, graphics, pos.getX(), pos.getY(), list, pos.getWidth(), color, true);
    }
    public static void drawParagraph(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, List<Component> list, int color, boolean renderShadow) {
        drawParagraph(instance, graphics, pos.getX(), pos.getY(), list, pos.getWidth(), color, renderShadow);
    }


    public static void drawText(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, Component text, int color) {
        drawText(instance, graphics, pos, text, color, true);
    }
    public static void drawText(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, Component text, int color, boolean renderShadow) {
        pos.width = instance.font.width(text);
        pos.height = instance.font.lineHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    
        graphics.drawString(instance.font, text, pos.getX(), pos.getY(), color, renderShadow);
        
        RenderSystem.enableDepthTest();
    }


    public static void drawItemStack(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull ItemStack stack, int x, int y) {
        drawItemStack(instance, graphics, stack, x, y, 16);
    }
    public static void drawItemStack(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull ItemStack stack, int x, int y, int size) {
        if (!stack.isEmpty()) {
            BakedModel bakedmodel = instance.getItemRenderer().getModel(stack, instance.level, null, 0);

            graphics.pose().pushPose();
            graphics.pose().translate((float) (x + (size / 2)), (float) (y + (size / 2)), (float) (150 + (bakedmodel.isGui3d() ? 0 : 0)));
            
            try {
                graphics.pose().mulPoseMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
                graphics.pose().scale(size, size, size);
                boolean flag = !bakedmodel.usesBlockLight();
                if (flag) {
                    Lighting.setupForFlatItems();
                }

                instance.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, graphics.pose(),
                        graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                graphics.flush();
                if (flag) {
                    Lighting.setupFor3DItems();
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                crashreportcategory.setDetail("Item Type", () -> {
                    return String.valueOf((Object) stack.getItem());
                });
                crashreportcategory.setDetail("Registry Name", () -> String
                        .valueOf(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem())));
                crashreportcategory.setDetail("Item Damage", () -> {
                    return String.valueOf(stack.getDamageValue());
                });
                crashreportcategory.setDetail("Item NBT", () -> {
                    return String.valueOf((Object) stack.getTag());
                });
                crashreportcategory.setDetail("Item Foil", () -> {
                    return String.valueOf(stack.hasFoil());
                });
                throw new ReportedException(crashreport);
            }

            graphics.pose().popPose();
        }
    }


    public static boolean drawLink(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, Component text, int color, int highlightedColor, double mouseX, double mouseY, boolean mouseDown) {
        return drawLink(instance, graphics, pos, text, color, highlightedColor, true, mouseX, mouseY, mouseDown);
    }
    public static boolean drawLink(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, Component text, int color, int highlightedColor, boolean renderShadow, double mouseX, double mouseY, boolean mouseDown) {
        int areaWidth = instance.font.width(text);
        int areaHeight = instance.font.lineHeight;
        boolean highlighted = mouseInArea(mouseX, mouseY, pos.withWidth(areaWidth).withHeight(areaHeight).toRect());
        
        drawText(instance, graphics, pos, text, highlighted ? highlightedColor : color, renderShadow);
        
        if(highlighted && mouseDown)
            return true;
        return false;
    }
    public static boolean drawLinkWrapped(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, Component text, int color, int highlightedColor, double mouseX, double mouseY, boolean mouseDown) {
        return drawLinkWrapped(instance, graphics, pos, text, color, highlightedColor, true, mouseX, mouseY, mouseDown);
    }
    public static boolean drawLinkWrapped(Minecraft instance, GuiGraphics graphics, BlitCoordinates pos, Component text, int color, int highlightedColor, boolean renderShadow, double mouseX, double mouseY, boolean mouseDown) {
        int areaWidth = pos.getWidth();
        int areaHeight = instance.font.wordWrapHeight(text, areaWidth);
        boolean highlighted = mouseInArea(mouseX, mouseY, pos.withWidth(areaWidth).withHeight(areaHeight).toRect());
        
        drawParagraph(instance, graphics, pos, List.of(text), highlighted ? highlightedColor : color, renderShadow);
        
        if(highlighted && mouseDown)
            return true;
        return false;
    }


    public static boolean drawButton(GuiGraphics graphics, BlitCoordinates pos, Texture texture, Texture highlightedTexture, double mouseX, double mouseY, boolean mouseDown) {
        Rect area = pos.toRect();
        if(area.positionEnvlopes(mouseX, mouseY)) {
            drawTexture(graphics, pos, highlightedTexture);
            if(mouseDown)
                return true;
        } else
            drawTexture(graphics, pos, texture);

        return false;
    }


    public static boolean mouseInArea(double mouseX, double mouseY, Rect area) {
        return area.positionEnvlopes(mouseX, mouseY);
    }
    


    public static class BlitCoordinates {
        private int x, y;
        private int width, height;
        private Alignment alignment;

        public BlitCoordinates(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.alignment = Alignment.DEFAULT;
        }

        public int getX() {
            switch (alignment) {
                case CENTER:
                    return x + (width / 2);
                default:
                    return x;
            }
        }

        public int getY() {
            switch (alignment) {
                case CENTER:
                    return y + (height / 2);
                default:
                    return y;
            }
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }


        public BlitCoordinates move(int offsetX, int offsetY) {
            return new BlitCoordinates(getX() + offsetX, getY() + offsetY, getWidth(), getHeight());
        }


        public BlitCoordinates withX(int value) {
            this.x = value;
            return this;
        }
        public BlitCoordinates withY(int value) {
            this.y = value;
            return this;
        }
        public BlitCoordinates withWidth(int value) {
            this.width = value;
            return this;
        }
        public BlitCoordinates withHeight(int value) {
            this.height = value;
            return this;
        }


        public Rect toRect() {
            switch (alignment) {
                case CENTER:
                    return Rect.fromWidthCentered(x, y, width, height);            
                default:
                    return Rect.fromWidth(x, y, width, height);
            }
        }

        static enum Alignment {
            DEFAULT,
            CENTER
        }
    

        public static BlitCoordinates fromRect(Rect rect) {
            return rect.toBlitCoordinates();
        }
        public static final BlitCoordinates DEFAULT = new BlitCoordinates(0, 0, 1, 1);
    }

    public static class Texture {
        private ResourceLocation location;
        private float u, v;
        private int boundsX, boundsY;
        private int width, height;

        public Texture(ResourceLocation location, float u, float v, int boundsX, int boundsY, int width, int height) {
            this.location = location;

            this.u = u;
            this.v = v;

            this.boundsX = boundsX;
            this.boundsY = boundsY;

            this.width = width;
            this.height = height;
        }

        public ResourceLocation getResourceLocation() {
            return location;
        }

        public float getU() {
            return u;
        }
        public float getV() {
            return v;
        }

        public int getBoundsX() {
            return boundsX;
        }
        public int getBoundsY() {
            return boundsY;
        }

        public int getWidth() {
            return width;
        }
        public int getHeight() {
            return height;
        }
    
        /** Get the width to height ratio */
        public float getAspectRatio() {
            return getBoundsX() / (float)getBoundsY();
        }
    }

    public static class Rect {
        int startX, startY, endX, endY;

        public Rect(int fromX, int fromY, int toX, int toY) {
            this.startX = fromX;
            this.startY = fromY;
            this.endX = toX;
            this.endY = toY;

            if(this.startY > this.endY)
            {
                int f = this.endY;
                this.endY = this.startY;
                this.startY = f;
            }

            if(this.startX > this.endX)
            {
                int f = this.endX;
                this.endX = this.startX;
                this.startX = f;
            }
        }

        public static Rect fromWidth(int x, int y, int width, int height) {
            return new Rect(x, y, x + width, y + height);
        }
        public static Rect fromWidthCentered(int x, int y, int width, int height) {
            return new Rect(x - (width / 2), y - (height / 2), x + (width / 2), y + (height / 2));
        }

        public int getFromX() {
            return startX;
        }
        public int getFromY() {
            return startY;
        }
        public int getToX() {
            return endX;
        }
        public int getToY() {
            return endY;
        }
    
        public int getWidth() {
            return getToX() - getFromX();
        }
        public int getHeight() {
            return getToY() - getFromY();
        }

        public Vector2i getTopLeft() {
            return new Vector2i(getFromX(), getFromY());
        }
        public Vector2i getTopRight() {
            return new Vector2i(getToX(), getFromY());
        }
        public Vector2i getBottomLeft() {
            return new Vector2i(getFromX(), getToY());
        }
        public Vector2i getBottomRight() {
            return new Vector2i(getToX(), getToY());
        }
        public Vector2i getCenter() {
            return new Vector2i(Mth.floor(getFromX() + (getWidth() / 2)), Mth.floor(getFromY() + (getHeight() / 2)));
        }

        public boolean positionEnvlopes(double x, double y) {
            return (x >= getFromX() && x <= getToX() &&
                y >= getFromY() && y <= getToY());
        }
        public boolean positionEnvlopes(float x, float y) {
            return (x >= getFromX() && x <= getToX() &&
                y >= getFromY() && y <= getToY());
        }
        public boolean positionEnvlopes(int x, int y) {
            return (x >= getFromX() && x <= getToX() &&
                y >= getFromY() && y <= getToY());
        }

        public BlitCoordinates toBlitCoordinates() {
            return new BlitCoordinates(getFromX(), getFromY(), getWidth(), getHeight());
        }
    }

    public static final class UI {
        public static boolean DRAW_DEBUG_WIDGETS = false;

        public static abstract class Element {

            final int fixedHeight;

            public Element(@Nonnull Minecraft instance, int height) {
                this.fixedHeight = height;
            }

            public int getFixedHeight() {
                return fixedHeight;
            }

            public abstract void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown);
        
            public void drawDebug(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int index) {
                graphics.fill(area.getFromX(), area.getFromY(), area.getToX(), area.getToY(), index % 2 == 0 ? 0xFFFF0000 : 0xFF0009FF);

                graphics.fill(area.getFromX() - 10, area.getFromY(), area.getToX() + 10, area.getFromY() + 1, 0xFF000000);
            }
        }

        public static class BlankElement extends Element {
            public BlankElement(@Nonnull Minecraft instance, int height) {
                super(instance, height);
            }

            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) { }

        }

        public static class BoxElement extends Element {

            final int color;

            public BoxElement(@Nonnull Minecraft instance, int height, int color) {
                super(instance, height);
                this.color = color;
            }

            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {
                graphics.fill(area.getFromX(), area.getFromY(), area.getToX(), area.getToY(), color);
            }

        }

        public static class TextElement extends Element {

            final List<Component> text;
            final int color;

            public TextElement(@Nonnull Minecraft instance, @Nonnull Component text, int height, int color) {
                super(instance, height);
                this.text = List.of(text);
                this.color = color;
            }
            public TextElement(@Nonnull Minecraft instance, int width, @Nonnull Component text, int color) {
                super(instance, instance.font.wordWrapHeight(text, width));
                this.text = List.of(text);
                this.color = color;
            }
            public TextElement(@Nonnull Minecraft instance, int width, @Nonnull List<Component> text, int color) {
                super(instance, ComponentUtils.getTotalHeight(instance, text, width));
                this.text = text;
                this.color = color;
            }


            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {
                ScreenUtils.drawParagraph(instance, graphics, area.toBlitCoordinates(), text, color);
            }
        }
    
        public static class LabelElement extends Element {

            final Component text;
            final Alignment alignment;
            final int width;
            final int color;

            public LabelElement(@Nonnull Minecraft instance, @Nonnull Component text, @Nonnull Alignment alignment, int color) {
                super(instance, instance.font.lineHeight);
                this.text = text;
                this.alignment = alignment;
                this.color = color;
                
                this.width = 0;
            }
            public LabelElement(@Nonnull Minecraft instance, @Nonnull Component text, @Nonnull Alignment alignment, int color, int height) {
                super(instance, height);
                this.text = text;
                this.alignment = alignment;
                this.color = color;

                this.width = 0;
            }
            public LabelElement(@Nonnull Minecraft instance, @Nonnull Component text, @Nonnull Alignment alignment, int color, int width, int height) {
                super(instance, height);
                this.text = text;
                this.alignment = alignment;
                this.color = color;

                this.width = width;
            }

            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {
                int wrappedHeight = instance.font.wordWrapHeight(text, area.getWidth());
                BlitCoordinates pos = alignment.fitToArea(area.toBlitCoordinates(), wrappedHeight <= instance.font.lineHeight ? instance.font.width(text) : area.getWidth(), wrappedHeight);

                if(width > 0)
                    pos = alignment.fitToArea(area.toBlitCoordinates().withWidth(width), wrappedHeight <= instance.font.lineHeight ? instance.font.width(text) : width, wrappedHeight);


                ScreenUtils.drawParagraph(instance, graphics, 
                    pos, 
                    List.of(text), color, false);
            }
        }
    

        public static class LinkElement extends Element {

            final Component text;
            final int color;
            final int highlightedColor;
            final Alignment alignText;
            final OnClickFunction supplier;

            public LinkElement(@Nonnull Minecraft instance, @Nonnull Component text, Alignment textAlignment, OnClickFunction supplier, int height, int color, int highlightedColor) {
                super(instance, height);
                this.text = text;
                this.alignText = textAlignment;
                this.color = color;
                this.highlightedColor = highlightedColor;
                this.supplier = supplier;
            }
            public LinkElement(@Nonnull Minecraft instance, int width, @Nonnull Component text, Alignment textAlignment, OnClickFunction supplier, int color, int highlightedColor) {
                super(instance, instance.font.wordWrapHeight(text, width));
                this.text = text;
                this.alignText = textAlignment;
                this.color = color;
                this.highlightedColor = highlightedColor;
                this.supplier = supplier;
            }

            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {
                int textWidth = instance.font.width(text);
                int textHeight = instance.font.lineHeight;
                if(ScreenUtils.drawLink(instance, graphics, alignText.fitToArea(area.toBlitCoordinates(), textWidth, textHeight), text, color, highlightedColor, false, (double)mouseX, (double)mouseY, mouseDown))
                    supplier.call();
            }
        }

        public static class TextureElement extends Element {

            final Texture texture;
            final int width;
            final Alignment align;

            public TextureElement(@Nonnull Minecraft instance, @Nonnull Texture texture, Alignment align, @Nonnull BlitCoordinates fillToArea) {
                super(instance, Mth.floor(fillToArea.getWidth() / texture.getAspectRatio()));
                this.texture = texture;
                this.width = fillToArea.getWidth();
                this.align = align;
            }
            public TextureElement(@Nonnull Minecraft instance, @Nonnull Texture texture, Alignment align, int height) {
                super(instance, height);
                this.texture = texture;
                this.width = Mth.floor(height * texture.getAspectRatio());
                this.align = align;
            }


            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics,  @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {
                ScreenUtils.drawTexture(graphics, align.fitToArea(area.toBlitCoordinates(), width, fixedHeight), texture);
            }
        }

        public static class ItemStackElement extends Element {

            final ItemStack stack;
            final Alignment alignment;

            public ItemStackElement(Minecraft instance, ItemStack stack, Alignment alignment, int height) {
                super(instance, height);
                this.stack = stack;
                this.alignment = alignment;
            }

            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX,
                    int mouseY, boolean mouseDown) {
                BlitCoordinates pos = alignment.fitToArea(area.toBlitCoordinates(), area.getHeight(), area.getHeight());
                ScreenUtils.drawItemStack(instance, graphics, stack, pos.getX(), pos.getY(), pos.getHeight());
            }

        }

        public static class ScaledTextureElement extends Element {

            final Texture texture;
            final int width;

            public ScaledTextureElement(@Nonnull Minecraft instance, @Nonnull Texture texture, int maxHeight) {
                super(instance, maxHeight);
                this.texture = texture;
                this.width = Mth.floor(maxHeight * texture.getAspectRatio());
            }


            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {
                int width = area.getWidth();
                int height = Mth.floor(width / texture.getAspectRatio());

                ScreenUtils.drawTexture(graphics, area.toBlitCoordinates().withHeight(height), texture);
            }
        }

        public static class BulletListElement extends Element {

            static final int LIST_ICON_SIZE = 16;
            static final int LIST_ITEM_PADDING = 7;

            final int width;
            final Texture bulletTexture;
            final List<Component> itemList;
            final int textColor;

            public BulletListElement(@Nonnull Minecraft instance, int width, @Nonnull Texture bulletTexture, @Nonnull List<Component> list, int textColor) {
                super(instance, getTotalHeight(instance, width, list));
                this.width = width;
                this.bulletTexture = bulletTexture;
                this.itemList = list;
                this.textColor = textColor;
            }

            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {

                int totalHeight = 0;
                for (int i = 0; i < itemList.size(); i++) {
                    ScreenUtils.drawTexture(graphics, 
                        area.toBlitCoordinates().move(-4, totalHeight).withWidth(LIST_ICON_SIZE).withHeight(LIST_ICON_SIZE), 
                        bulletTexture);

                    int localHeight = instance.font.wordWrapHeight(itemList.get(i), width - (LIST_ICON_SIZE - 4));

                    ScreenUtils.drawParagraph(instance, graphics, 
                        area.toBlitCoordinates().move((LIST_ICON_SIZE - 4), totalHeight).withWidth(area.toBlitCoordinates().getWidth() - (LIST_ICON_SIZE - 4)).withHeight(localHeight), 
                        List.of(itemList.get(i)),
                        textColor, false);

                    totalHeight += localHeight + + LIST_ITEM_PADDING;
                }
            }

            static int getTotalHeight(@Nonnull Minecraft instance, int width, @Nonnull List<Component> list) {
                int totalHeight = 0;
                for (int i = 0; i < list.size(); i++)
                    totalHeight += instance.font.wordWrapHeight(list.get(i), width - (LIST_ICON_SIZE - 4)) + LIST_ITEM_PADDING;
                return totalHeight;
            }
        }
        
        public static class BulletLinkListElement extends Element {

            static final int LIST_ICON_SIZE = 16;
            static final int LIST_ITEM_PADDING = 7;

            final int width;
            final Texture bulletTexture;
            final List<Pair<Component, OnClickFunction>> itemList;
            final int textColor;
            final int highlightedColor;

            public BulletLinkListElement(@Nonnull Minecraft instance, int width, @Nonnull Texture bulletTexture, @Nonnull List<Pair<Component, OnClickFunction>> list, int textColor, int highlightedColor) {
                super(instance, getTotalHeight(instance, width, list));
                this.width = width;
                this.bulletTexture = bulletTexture;
                this.itemList = list;
                this.textColor = textColor;
                this.highlightedColor = highlightedColor;
            }

            @Override
            public void draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull Rect area, int mouseX, int mouseY, boolean mouseDown) {
                
                int clicked = -1;

                int totalHeight = 0;
                for (int i = 0; i < itemList.size(); i++) {
                    ScreenUtils.drawTexture(graphics, 
                        area.toBlitCoordinates().move(-4, totalHeight).withWidth(LIST_ICON_SIZE).withHeight(LIST_ICON_SIZE), 
                        bulletTexture);
                    
                    int localHeight = instance.font.wordWrapHeight(itemList.get(i).getFirst(), width - (LIST_ICON_SIZE - 4));

                    if(ScreenUtils.drawLinkWrapped(instance, graphics, 
                            area.toBlitCoordinates().move((LIST_ICON_SIZE - 4), totalHeight).withWidth(area.toBlitCoordinates().getWidth() - (LIST_ICON_SIZE - 4)).withHeight(localHeight), 
                            itemList.get(i).getFirst(),
                            textColor,
                            highlightedColor, false,
                            mouseX, mouseY, mouseDown))
                        clicked = i;
                    
                    totalHeight += localHeight + + LIST_ITEM_PADDING;
                }

                if(clicked > -1)
                    itemList.get(clicked).getSecond().call();
            }
        
            static int getTotalHeight(@Nonnull Minecraft instance, int width, @Nonnull List<Pair<Component, OnClickFunction>> list) {
                int totalHeight = 0;
                for (int i = 0; i < list.size(); i++)
                    totalHeight += instance.font.wordWrapHeight(list.get(i).getFirst(), width - (LIST_ICON_SIZE - 4)) + LIST_ITEM_PADDING;
                return totalHeight;
            }
        }


        public static enum Alignment {
            TOP_LEFT,
            TOP_MIDLE,
            TOP_RIGHT,
            CENTER_LEFT,
            CENTER,
            CENTER_RIGHT,
            BOTTOM_LEFT,
            BOTTOM_MIDDLE,
            BOTTOM_RIGHT;

            BlitCoordinates fitToArea(BlitCoordinates area, int width, int height) {
                Rect r = area.toRect();
                Vector2i p = new Vector2i(0 , 0);
                switch (this) {
                    default:
                        p = r.getTopLeft();
                        return new BlitCoordinates(p.x(), p.y(), width, height);
                    case TOP_MIDLE:
                        p = r.getCenter();
                        return new BlitCoordinates(p.x() - width/2, area.getY(), width, height);
                    case TOP_RIGHT:
                        p = r.getTopRight();
                        return new BlitCoordinates(p.x() - width, p.y(), width, height);
                    case CENTER_LEFT:
                        p = r.getCenter();
                        return new BlitCoordinates(area.getX(), p.y() - (height / 2), width, height);
                    case CENTER:
                        p = r.getCenter();
                        return new BlitCoordinates(p.x() - (width / 2), p.y() - (height / 2), width, height);
                    case CENTER_RIGHT:
                        p = r.getCenter();
                        return new BlitCoordinates(r.getToX() - width, p.y() - (height / 2), width, height);
                    case BOTTOM_LEFT:
                        p = r.getBottomLeft();
                        return new BlitCoordinates(p.x(), p.y() - height, width, height);
                    case BOTTOM_MIDDLE:
                        p = r.getCenter();
                        return new BlitCoordinates(p.x() - (width / 2), r.getToY() - height, width, height);
                    case BOTTOM_RIGHT:
                        p = r.getBottomRight();
                        return new BlitCoordinates(p.x() - width, p.y() - height, width, height);
                }
            }
        }

        public interface OnClickFunction {
            void call();
        }

        public static class HorizontalElement {
            final Element[] elements;
            final int height;

            public HorizontalElement(@Nonnull Element... elements) {
                this.elements = elements;
                this.height = calculateHeight(elements);
            }

            public int draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull BlitCoordinates availableArea, int mouseX, int mouseY, boolean mouseDown) {
                availableArea = availableArea.withHeight(height);
                int widthPerElement = availableArea.getWidth() / elements.length;
                
                for (int i = 0; i < elements.length; i++) {
                    elements[i].draw(instance, graphics, availableArea.move(widthPerElement * i, 0)
                        .withWidth(widthPerElement)
                        .withHeight(height).toRect(),
                        mouseX, mouseY, mouseDown);
                }
    
                return height;
            }

            public int drawDebug(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, @Nonnull BlitCoordinates availableArea, int index) {
                availableArea = availableArea.withHeight(height);
                int widthPerElement = availableArea.getWidth() / elements.length;
                
                for (int i = 0; i < elements.length; i++) {
                    elements[i].drawDebug(instance, graphics, availableArea.move(widthPerElement * i, 0)
                        .withWidth(widthPerElement)
                        .withHeight(height).toRect(),
                        i);
                }
    
                return height;
            }

            public int calculateHeight(Element[] elements) {
                int maxHeight = 0;
                for (Element element : elements) {
                    int height = element.getFixedHeight();
                    if(height > maxHeight)
                        maxHeight = height;
                }
                return maxHeight;
            }
        }

        public static class VerticalElement {
            BlitCoordinates pos;
            final HorizontalElement[] horizontals;
            final int height;
            final int spacing;

            public VerticalElement(@Nonnull BlitCoordinates availableArea, @Nonnull HorizontalElement... horizontals) {
                this.pos = availableArea;
                this.horizontals = horizontals;
                this.height = calculateHeight();
                this.spacing = 0;
            }
            public VerticalElement(@Nonnull BlitCoordinates availableArea, int spacing, @Nonnull HorizontalElement... horizontals) {
                this.pos = availableArea;
                this.horizontals = horizontals;
                this.height = calculateHeight();
                this.spacing = spacing;
            }

            public int draw(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, int mouseX, int mouseY, boolean mouseDown) {
                int spacing = 0;
                for (int i = 0; i < horizontals.length; i++)
                    spacing += horizontals[i].draw(instance, graphics, pos.move(0, spacing), mouseX, mouseY, mouseDown) + this.spacing;
                return spacing;
            }

            public int drawDebug(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics) {
                int spacing = 0;
                for (int i = 0; i < horizontals.length; i++)
                    spacing += horizontals[i].drawDebug(instance, graphics, pos.move(0, spacing), i) + this.spacing;
                return spacing;
            }

            public void setAvailableArea(@Nonnull BlitCoordinates newArea) {
                this.pos = newArea;
            }

            int calculateHeight() {
                int spacing = 0;
                for (int i = 0; i < horizontals.length; i++)
                    spacing += horizontals[i].calculateHeight(horizontals[i].elements);
                return spacing;
            }
        }

        public static void drawPage(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, int mouseX, int mouseY, boolean mouseDown, VerticalElement verticals) {
            if(DRAW_DEBUG_WIDGETS)
                verticals.drawDebug(instance, graphics);
            verticals.draw(instance, graphics, mouseX, mouseY, mouseDown);
        }
    }
}
