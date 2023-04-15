package dustw.libgui.event;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author DustW
 */
@FunctionalInterface
public interface CoreShaderRegistrationCallback {
    List<CoreShaderRegistrationCallback> EVENTS = new ArrayList<>();

    /**
     * Registers core shaders using the registration context.
     *
     * @param context the registration context
     */
    void registerShaders(RegistrationContext context) throws IOException;

    /**
     * A context object used to create and register core shader programs.
     *
     * <p>This is not meant for implementation by users of the API.
     */
    @ApiStatus.NonExtendable
    interface RegistrationContext {
        /**
         * Creates and registers a core shader program.
         *
         * <p>The program is loaded from {@code assets/<namespace>/shaders/core/<path>.json}.
         *
         * @param id           the program ID
         * @param vertexFormat the vertex format used by the shader
         * @param loadCallback a callback that is called when the shader program has been successfully loaded
         */
        void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) throws IOException;
    }
}

