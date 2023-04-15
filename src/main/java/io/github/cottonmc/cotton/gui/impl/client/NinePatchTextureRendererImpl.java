package io.github.cottonmc.cotton.gui.impl.client;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import juuxel.libninepatch.ContextualTextureRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * An implementation of LibNinePatch's {@link ContextualTextureRenderer} for identifiers.
 */
public enum NinePatchTextureRendererImpl implements ContextualTextureRenderer<ResourceLocation, PoseStack> {
    INSTANCE;

    @Override
    public void draw(ResourceLocation texture, PoseStack matrices, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
        ScreenDrawing.texturedRect(matrices, x, y, width, height, texture, u1, v1, u2, v2, 0xFF_FFFFFF);
    }

    @Override
    public void drawTiled(ResourceLocation texture, PoseStack matrices, int x, int y, int regionWidth, int regionHeight, int tileWidth, int tileHeight, float u1, float v1, float u2, float v2) {
        RenderSystem.setShader(LibGuiShaders::getTiledRectangle);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Matrix4f positionMatrix = matrices.last().pose();
        onRenderThread(() -> {
            @Nullable ShaderInstance program = RenderSystem.getShader();
            if (program != null) {
                program.safeGetUniform("LibGuiRectanglePos").set((float) x, (float) y);
                program.safeGetUniform("LibGuiTileDimensions").set((float) tileWidth, (float) tileHeight);
                program.safeGetUniform("LibGuiTileUvs").setMat2x2(u1, v1, u2, v2);
                program.safeGetUniform("LibGuiPositionMatrix").set(positionMatrix);
            }
        });

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.enableBlend();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(positionMatrix, x, y, 0).endVertex();
        buffer.vertex(positionMatrix, x, y + regionHeight, 0).endVertex();
        buffer.vertex(positionMatrix, x + regionWidth, y + regionHeight, 0).endVertex();
        buffer.vertex(positionMatrix, x + regionWidth, y, 0).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }

    private static void onRenderThread(RenderCall renderCall) {
        if (RenderSystem.isOnRenderThread()) {
            renderCall.execute();
        } else {
            RenderSystem.recordRenderCall(renderCall);
        }
    }
}
