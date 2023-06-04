package io.github.cottonmc.cotton.gui.impl.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dustw.libgui.LibGui;
import dustw.libgui.event.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class LibGuiShaders {
    private static @Nullable ShaderInstance tiledRectangle;

    public static void register() {
        CoreShaderRegistrationCallback.EVENTS.add(context -> {
            // Register our core shaders.
            // The tiled rectangle shader is used for performant tiled texture rendering.
            context.register(new ResourceLocation(LibGui.MOD_ID, "tiled_rectangle"),
                    DefaultVertexFormat.POSITION, program -> tiledRectangle = program);
        });
    }

    private static ShaderInstance assertPresent(ShaderInstance program, String name) {
        if (program == null) {
            throw new NullPointerException("Shader libgui:" + name + " not initialised!");
        }

        return program;
    }

    public static ShaderInstance getTiledRectangle() {
        return assertPresent(tiledRectangle, "tiled_rectangle");
    }
}
