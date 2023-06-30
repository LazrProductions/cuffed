package com.lazrproductions.cuffed.events;

import org.jetbrains.annotations.ApiStatus;

import com.lazrproductions.cuffed.client.render.entity.ChainKnotEntityRenderer;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.fml.LogicalSide;

public class RenderChainKnotEntityEvent extends GenericEvent<RenderChainKnotEntityEvent> {

    private final ChainKnotEntity entity;
    private final ChainKnotEntityRenderer renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    @ApiStatus.Internal
    protected RenderChainKnotEntityEvent(ChainKnotEntity entity, ChainKnotEntityRenderer renderer, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    /**
     * {@return the chain knot entity}
     */
    public ChainKnotEntity getEntity() {
        return entity;
    }


    /**
     * {@return the chain entity renderer}
     */
    public ChainKnotEntityRenderer getRenderer() {
        return renderer;
    }

    /**
     * {@return the partial tick}
     */
    public float getPartialTick() {
        return partialTick;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack() {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight() {
        return packedLight;
    }

    /**
     * Fired <b>before</b> the chain knot entity is rendered.
     * This can be used for rendering additional effects or suppressing rendering.
     *
     * <p>
     * This event is {@linkplain Cancelable cancellable}, and does not
     * {@linkplain HasResult have a result}.
     * If this event is cancelled, then the chain knot entity will not be rendered and the
     * corresponding
     * {@link RenderChainKnotEntityEvent.Post} will not be fired.
     * </p>
     *
     * <p>
     * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge
     * event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.
     * </p>
     */
    @Cancelable
    public static class Pre extends RenderChainKnotEntityEvent {
        @ApiStatus.Internal
        public Pre(ChainKnotEntity player, ChainKnotEntityRenderer renderer, float partialTick, PoseStack poseStack,
                MultiBufferSource multiBufferSource, int packedLight) {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }

    /**
     * Fired <b>after</b> the chain knot entity is rendered, if the corresponding {@link RenderChainKnotEntityEvent.Pre} is not cancelled.
     *
     * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
     */
    public static class Post extends RenderChainKnotEntityEvent {
        @ApiStatus.Internal
        public Post(ChainKnotEntity player, ChainKnotEntityRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight)
        {
            super(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }
}
