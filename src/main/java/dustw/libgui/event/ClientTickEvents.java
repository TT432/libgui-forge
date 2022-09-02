package dustw.libgui.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientTickEvents {
    private static final List<EndTick> ENDS = new ArrayList<>();

    public static void registerEnd(EndTick endTick) {
        ENDS.add(endTick);
    }

    @SubscribeEvent
    public static void onEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            ENDS.forEach(e -> e.onEndTick(Minecraft.getInstance()));
    }

    @FunctionalInterface
    public interface EndTick {
        void onEndTick(Minecraft client);
    }
}
