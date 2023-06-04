package dustw.libgui;

import dustw.libgui.network.LibGuiMessages;
import io.github.cottonmc.cotton.gui.impl.client.LibGuiConfig;
import io.github.cottonmc.cotton.gui.impl.client.LibGuiShaders;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LibGui.MOD_ID)
public class LibGui {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "libgui";

    public LibGui() {
        LibGuiShaders.register();
        LibGuiConfig.register();
        LibGuiMessages.register();
    }
}
