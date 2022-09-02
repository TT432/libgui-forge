package dustw.libgui.event;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DustW
 */
public interface HudRenderCallback {
    List<HudRenderCallback> EVENTS = new ArrayList<>();

    static void register(HudRenderCallback callback) {
        EVENTS.add(callback);
    }

    /**
     * Called after rendering the whole hud, which is displayed in game, in a world.
     *
     * @param matrixStack the matrixStack
     * @param tickDelta Progress for linearly interpolating between the previous and current game state
     */
    void onHudRender(PoseStack matrixStack, float tickDelta);
}
