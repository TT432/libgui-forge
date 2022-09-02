package dustw.libgui;

import dustw.libgui.network.LibGuiMessages;
import io.github.cottonmc.cotton.gui.impl.client.LibGuiConfig;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LibGui.MODID)
public class LibGui {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "libgui";

    public LibGui() {
        LibGuiConfig.register();
        LibGuiMessages.register();
    }
}
