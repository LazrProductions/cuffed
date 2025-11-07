package com.lazrproductions.cuffed.client.gui.screen;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;

import org.joml.Vector2i;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.lazrslib.client.font.FontUtilities;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenRect;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.lazrproductions.lazrslib.client.ui.Alignment;
import com.lazrproductions.lazrslib.client.ui.UIUtilities;
import com.lazrproductions.lazrslib.client.ui.element.HorizontalElement;
import com.lazrproductions.lazrslib.client.ui.element.ItemIconElement;
import com.lazrproductions.lazrslib.client.ui.element.LinkElement;
import com.lazrproductions.lazrslib.client.ui.element.ScaledTextureElement;
import com.lazrproductions.lazrslib.client.ui.element.TextElement;
import com.lazrproductions.lazrslib.client.ui.element.TextureElement;
import com.lazrproductions.lazrslib.client.ui.element.VerticalElement;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.Mth;

@OnlyIn(Dist.CLIENT)
public class InformationBookletScreen extends GenericScreen {

    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CuffedMod.MODID,
            "textures/gui/information_booklet.png");

    private static final int FIXED_PAGE_HEIGHT = 202;

    private static final ScreenTexture FRONT_COVER_PAGE = new ScreenTexture(TEXTURE_LOCATION, 146, 0, 146, 180, 512, 512);
    private static final ScreenTexture BACK_COVER_PAGE = new ScreenTexture(TEXTURE_LOCATION, 0, 0, 146, 180, 512, 512);

    private static final ScreenTexture BASIC_LEFT_PAGE = new ScreenTexture(TEXTURE_LOCATION, 0, 180, 146, 180, 512, 512);
    private static final ScreenTexture BASIC_RIGHT_PAGE = new ScreenTexture(TEXTURE_LOCATION, 146, 180, 146, 180, 512, 512);


    private static final ScreenTexture RIGHT_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 0, 360, 18, 10, 512, 512);
    private static final ScreenTexture RIGHT_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 18, 360, 18, 10, 512, 512);

    private static final ScreenTexture LEFT_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 0, 370, 18, 10, 512, 512);
    private static final ScreenTexture LEFT_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 18, 370, 18, 10, 512, 512);
    
    private static final ScreenTexture BACK_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 35, 399, 18, 10, 512, 512);
    private static final ScreenTexture BACK_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 53, 399, 18, 10, 512, 512);

    private static final ScreenTexture HOME_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 0, 381, 11, 11, 512, 512);
    private static final ScreenTexture HOME_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 11, 381, 11, 11, 512, 512);
    
    static final int WRITING_COLOR = ARGB32.color(255, 148, 116, 90);
    static final int HIGHLIGHTED_COLOR = 0xFF009FFF;

    public ArrayList<GenericPage> pages;
    HashMap<String, Integer> encyclopediaMap = new HashMap<String, Integer>();
    int currentPage = 0;
    int previousPage = -1;

    int pageHeight;
    int pageWidth;

    public InformationBookletScreen(Minecraft instance) {
        super(instance);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        pageHeight = FIXED_PAGE_HEIGHT;
        pageWidth = Mth.floor(pageHeight * (146f / 180f));

        if (minecraft != null)
            drawContent(graphics, partialTick);

        super.render(graphics, mouseX, mouseY, partialTick);
        
    }

    @SuppressWarnings("null")
    public void drawContent(@Nonnull GuiGraphics graphics, float partialTick) {
        assemblePages(minecraft);
        
        var page = pages.get(currentPage);

        this.renderBackground(graphics);

        pages.get(currentPage).renderPage(minecraft, graphics, partialTick, mouseX, mouseY, (lastMouseInput.getAction() == 1 && lastMouseInput.getInput() == 0));

        // List<Component> c = List.of(Component.literal(currentPage + "/" +
        // (pages.size() - 1)), Component.literal("btn: " + lastMouseInput.input + ",
        // actn: " + lastMouseInput.action));
        // ScreenUtils.renderLabel(minecraft, graphics, mouseX, mouseY, c);

        // DRAW PAGE NUMBERS ON DOUBLE PAGE
        if(pages.get(currentPage) instanceof DoublePage) {
            int l_pN = (currentPage * 2) - 1;
            int r_pN = l_pN + 1;

            //LEFT PAGE NUMBER
            Component l_pageNumber = Component.literal("" + l_pN);
            int l_h = Mth.floor(minecraft.font.lineHeight), l_w = Mth.floor(minecraft.font.width(l_pageNumber));
            int l_x = Mth.floor(page.getPageRect().getFromX() + 26);
            int l_y = Mth.floor(page.getPageRect().getToY() - l_h - 12);
            FontUtilities.drawText(minecraft, graphics, new BlitCoordinates(l_x, l_y, l_w, l_h), l_pageNumber, WRITING_COLOR, false);

            //RIGHT PAGE NUMBER
            Component r_pageNumber = Component.literal("" + r_pN);
            int r_h = Mth.floor(minecraft.font.lineHeight), r_w = Mth.floor(minecraft.font.width(l_pageNumber));
            int r_x = Mth.floor(page.getPageRect().getToX() - r_w - 26);
            int r_y = Mth.floor(page.getPageRect().getToY() - r_h - 12);
            FontUtilities.drawText(minecraft, graphics, new BlitCoordinates(r_x, r_y, r_w, r_h), r_pageNumber, WRITING_COLOR, false);
        }

        
        boolean pageWasChanged = false;
        if(currentPage > 1) { // HOME BUTTON
            float aspectRatioOfTexture = 11 / (float) 11;
            int h = Mth.floor(10), w = Mth.floor(h * aspectRatioOfTexture);
            int x = Mth.floor(page.getPageRect().getFromX() + (pageWidth/2) - (Mth.floor(10 * (18 / (float) 10))) / 2) - 4 - w;
            int y = Mth.floor(page.getPageRect().getToY() - h - 12);

            if (drawButton(graphics, new BlitCoordinates(x, y, w, h), HOME_BUTTON, HOME_BUTTON_HIGHLIGHTED,
                    mouseX, mouseY, (lastMouseInput.getAction() == 1 && lastMouseInput.getInput() == 0))) {
                setPage(minecraft, 1);
                pageWasChanged = true;
            }
        }

        if(previousPage > -1) {
            float aspectRatioOfTexture = 18 / (float) 10;
            int h = Mth.floor(10), w = Mth.floor(h * aspectRatioOfTexture);
            int x = Mth.floor(page.getPageRect().getFromX() + 18);
            int y = Mth.floor(page.getPageRect().getFromY() + 14);

            if (drawButton(graphics, new BlitCoordinates(x, y, w, h), BACK_BUTTON, BACK_BUTTON_HIGHLIGHTED,
                    mouseX, mouseY, (lastMouseInput.getAction() == 1 && lastMouseInput.getInput() == 0))) {
                setPage(minecraft, previousPage);
                previousPage = -1;
                pageWasChanged = true;
            }
        }

        if (currentPage > 0) { // PREVIOUS PAGE ARROW
            float aspectRatioOfTexture = 18 / (float) 10;
            int h = Mth.floor(10), w = Mth.floor(h * aspectRatioOfTexture);
            int x = Mth.floor(page.getPageRect().getFromX() + (pageWidth/2) - (w/2));
            int y = Mth.floor(page.getPageRect().getToY() - h - 12);

            if (drawButton(graphics, new BlitCoordinates(x, y, w, h), LEFT_BUTTON, LEFT_BUTTON_HIGHLIGHTED,
                    mouseX, mouseY, (lastMouseInput.getAction() == 1 && lastMouseInput.getInput() == 0))) {
                currentPage--;
                previousPage = -1;
                pageWasChanged = true;
                minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
            }
        }
        
        if (!pageWasChanged && currentPage + 1 < pages.size()) { // NEXT PAGE ARROW
            float aspectRatioOfTexture = 18 / (float) 10;
            int h = Mth.floor(10), w = Mth.floor(h * aspectRatioOfTexture);
            int x = Mth.floor(page.getPageRect().getToX() - (pageWidth/2) - (w/2));
            int y = Mth.floor(page.getPageRect().getToY() - h - 12);

            if (drawButton(graphics, new BlitCoordinates(x, y, w, h), RIGHT_BUTTON,
                    RIGHT_BUTTON_HIGHLIGHTED, mouseX, mouseY,
                    (lastMouseInput.getAction() == 1 && lastMouseInput.getInput() == 0))) {
                currentPage++;
                previousPage = -1;
                minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
            }
        }
    }

    @SuppressWarnings("null")
    public void setPage(@Nonnull Minecraft insance, int value) {
        previousPage = currentPage;
        currentPage = value;
        insance.player.playSound(SoundEvents.BOOK_PAGE_TURN);
    }


    public void assemblePages(@Nonnull Minecraft instance) {
        pageHeight = FIXED_PAGE_HEIGHT;
        pageWidth = Mth.floor(pageHeight * (146f / 180f));

        int contentWidth = pageWidth -GenericPage.CONTENT_PADDING_LEFT - GenericPage.CONTENT_PADDING_RIGHT;

        pages = new ArrayList<>();

        // [0] -> front cover 
        pages.add(new CoverPage(FRONT_COVER_PAGE));

        // [1] -> intro / contents p1
        pages.add(new DoublePage(
                new ComplexPage(BASIC_LEFT_PAGE, new VerticalElement(BlitCoordinates.DEFAULT, 2,
                    new HorizontalElement(
                        new TextureElement(instance, 
                            new ScreenTexture(TEXTURE_LOCATION, 438, 253, 65, 23, 512, 512), 
                            Alignment.CENTER, Mth.floor(GenericPage.HEADER_HEIGHT * 0.771f))),
                    new HorizontalElement(
                        new ScaledTextureElement(instance, 
                            GenericPage.SEPARATOR_TEXTURE, 
                            Mth.floor(contentWidth * (10f / 104f)))),
                    new HorizontalElement(
                        new TextElement(instance, contentWidth, 
                            Component.translatable("guide.cuffed.title_page.info"), WRITING_COLOR, false)))),
                new ComplexPage(BASIC_RIGHT_PAGE, new VerticalElement(BlitCoordinates.DEFAULT, 2, 
                    new HorizontalElement(
                        new TextElement(instance, contentWidth, Component.literal("Warden's guide has been discontinued, use the"), WRITING_COLOR, false)),
                    new HorizontalElement(
                        new LinkElement(instance, contentWidth, Component.literal("Official Cuffed Wiki"), Alignment.CENTER, () -> {
                        
                        }, WRITING_COLOR, HIGHLIGHTED_COLOR)),
                    new HorizontalElement(
                        new TextElement(instance, contentWidth, Component.literal("to find information about the mod."), WRITING_COLOR, false))))));

        pages.add(new CoverPage(BACK_COVER_PAGE));
    }


    public int getItemEncyclopediaPage(Item item) {
        return getItemEncyclopediaPage(new ItemStack(item));
    }
    public int getItemEncyclopediaPage(ItemStack stack) {
        return getItemEncyclopediaPage(stack.getDescriptionId());
    }
    public int getItemEncyclopediaPage(String path) {
        return encyclopediaMap.containsKey(path) ? encyclopediaMap.get(path) : 0;
    }
    public DoublePage createItemEncyclopediaPage(int wouldBeIndex, @Nonnull Minecraft instance, Item item, int contentWidth) {
        return createItemEncyclopediaPage(wouldBeIndex, instance, new ItemStack(item), contentWidth);
    }
    public DoublePage createItemEncyclopediaPage(int wouldBeIndex, @Nonnull Minecraft instance, ItemStack stack, int contentWidth) {
        return createItemEncyclopediaPage(wouldBeIndex, instance, stack.getItem().getDescriptionId(), stack, contentWidth);
    }
    public DoublePage createItemEncyclopediaPage(int wouldBeIndex, @Nonnull Minecraft instance, String path, ItemStack stack, int contentWidth) {
        if(!encyclopediaMap.containsKey(path))
            encyclopediaMap.put(path, wouldBeIndex);
        else
            encyclopediaMap.replace(path, wouldBeIndex);

        return new DoublePage(
            new ComplexPage(BASIC_LEFT_PAGE, new VerticalElement(BlitCoordinates.DEFAULT,
                new HorizontalElement(
                    new TextElement(instance, contentWidth - GenericPage.HEADER_HEIGHT,
                        Component.translatable(path).withStyle(ChatFormatting.BOLD), WRITING_COLOR, false),
                    new ItemIconElement(instance, stack, 
                        Alignment.CENTER_RIGHT, GenericPage.HEADER_HEIGHT)),
                new HorizontalElement(
                    new TextElement(instance, contentWidth,
                        Component.translatable(path+".desc"), WRITING_COLOR, false)))),
            new BlankPage(BASIC_RIGHT_PAGE));
    
    }

    public static boolean drawButton(GuiGraphics graphics, BlitCoordinates pos, ScreenTexture texture, ScreenTexture highlightedTexture, double mouseX, double mouseY, boolean mouseDown) {
        ScreenRect area = pos.toRect();
        if(area.positionEnvlopes(mouseX, mouseY)) {
            ScreenUtilities.drawTexture(graphics, pos, highlightedTexture);
            if(mouseDown)
                return true;
        } else
            ScreenUtilities.drawTexture(graphics, pos, texture);

        return false;
    }


    static abstract class GenericPage {
        static final float PAGE_VERTICAL_PERCENTAGE = 0.8f;
        static final int CONTENT_PADDING_LEFT = 24;
        static final int CONTENT_PADDING_RIGHT = 24;
        static final int CONTENT_PADDING_TOP = 18;
        static final int CONTENT_PADDING_BOTTOM = 28;

        static final int HEADER_HEIGHT = 36;

        static final int ICON_HEIGHT = 36;
        static final int ICON_WIDTH = 36;

        static int SEPARATOR_HEIGHT = 0;
        static final ScreenTexture SEPARATOR_TEXTURE = new ScreenTexture(TEXTURE_LOCATION, 0, 411, 104, 9, 512, 512);


        ResourceLocation PAGE_LOCATION;

        private int screenWidth, screenHeight;
        private ScreenRect pageRect;
        private ScreenRect contentRect;
        private ScreenTexture backgroundtexture;
        public int pageHorizontalOffset;

        public GenericPage(ScreenTexture background) {
            this.backgroundtexture = background;

            calibratePage(Minecraft.getInstance());
        }

        public void renderPage(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick,
                int mouseX, int mouseY, boolean mouseDown) {
            calibratePage(instance);
            renderBackground(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
            renderContent(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
        }

        protected void renderBackground(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick,
                int mouseX, int mouseY, boolean mouseDown) {
            ScreenUtilities.drawTexture(graphics, getPageBlitCoords(), backgroundtexture);

        }

        protected abstract void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics,
                float partialTick, int mouseX, int mouseY, boolean mouseDown);

        
        private final void calibratePage(@Nonnull Minecraft instance) {
            Window window = Minecraft.getInstance().getWindow();
            screenWidth = window.getGuiScaledWidth();
            screenHeight = window.getGuiScaledHeight();

            int centerScreenX = screenWidth / 2, centerScreenY = screenHeight / 2;

            float aspectRatioOfTexture = backgroundtexture.getBoundsX() / (float) backgroundtexture.getBoundsY();
            int h = FIXED_PAGE_HEIGHT, w = Mth.floor(h * aspectRatioOfTexture);
            int x = Mth.floor(centerScreenX - (w / 2)) + pageHorizontalOffset, y = Mth.floor(centerScreenY - (h / 2));

            pageRect = new BlitCoordinates(x, y, w, h).toRect();
            contentRect = new ScreenRect(pageRect.getFromX() + CONTENT_PADDING_LEFT,
                    pageRect.getFromY() + CONTENT_PADDING_TOP,
                    pageRect.getToX() - CONTENT_PADDING_RIGHT, pageRect.getToY() - CONTENT_PADDING_BOTTOM);
            SEPARATOR_HEIGHT = Mth.floor(contentRect.getWidth() / (104f/ 9f));
        }


        public Vector2i getTopLeft() {
            return pageRect.getTopLeft();
        }

        public Vector2i getTopRight() {
            return pageRect.getTopRight();
        }

        public Vector2i getBottomLeft() {
            return pageRect.getBottomLeft();
        }

        public Vector2i getBottomRight() {
            return pageRect.getBottomRight();
        }

        public Vector2i getCenter() {
            return pageRect.getCenter();
        }

        public ScreenRect getContentRect() {
            return contentRect;
        }
        public BlitCoordinates getContentBlitCoords() {
            return getContentRect().toBlitCoordinates();
        }

        public ScreenRect getPageRect() {
            return pageRect;
        }
        public BlitCoordinates getPageBlitCoords() {
            return getPageRect().toBlitCoordinates();
        }
    }

    static class BlankPage extends GenericPage {
        public BlankPage(ScreenTexture background) {
            super(background);
        }

        @Override
        protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick,
                int mouseX, int mouseY, boolean mouseDown) {
            BlitCoordinates pos = new BlitCoordinates(
                getContentBlitCoords().toRect().getCenter().x() - (ICON_WIDTH/2), 
                getContentBlitCoords().toRect().getCenter().y() - (ICON_HEIGHT/2), 
                ICON_WIDTH, ICON_HEIGHT);
            ScreenUtilities.drawTexture(graphics, pos, new ScreenTexture(TEXTURE_LOCATION, 292, 0, 35, 35, 512, 512));
        }

    }

    static class CoverPage extends GenericPage {

        public CoverPage(ScreenTexture background) {
            super(background);
        }

        protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
        }
    }

    static class ComplexPage extends GenericPage {
        final VerticalElement element;

        public ComplexPage(ScreenTexture background, VerticalElement element) {
            super(background);
            this.element = element;
        }

        @Override
        protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick,
                int mouseX, int mouseY, boolean mouseDown) {
            element.setAvailableArea(getContentBlitCoords());
            UIUtilities.drawPage(instance, graphics, mouseX, mouseY, mouseDown, element);
        }
    }

    static class DoublePage extends GenericPage {
        final GenericPage leftPage;
        final GenericPage rightPage;

        public DoublePage(GenericPage leftPage, GenericPage rightPage) {
            super(leftPage.backgroundtexture);
            this.leftPage = leftPage;
            this.leftPage.pageHorizontalOffset = -leftPage.getPageRect().getWidth()/2;
            this.rightPage = rightPage;
            this.rightPage.pageHorizontalOffset = rightPage.getPageRect().getWidth()/2;
        }


        public void renderPage(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick,
                int mouseX, int mouseY, boolean mouseDown) {
            super.renderPage(instance, graphics, partialTick, mouseX, mouseY, mouseDown);

            leftPage.renderPage(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
            rightPage.renderPage(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
        }

        protected void renderBackground(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) { }
        protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) { }

        @Override
        public ScreenRect getPageRect() {
            return new ScreenRect(leftPage.getPageRect().getFromX(), leftPage.getPageRect().getFromY(), rightPage.getPageRect().getToX(), rightPage.getPageRect().getToY());
        }
    }
}
