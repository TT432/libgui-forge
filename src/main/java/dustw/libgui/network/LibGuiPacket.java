package dustw.libgui.network;

import io.github.cottonmc.cotton.gui.impl.ScreenNetworkingImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author DustW
 */
public class LibGuiPacket {
    // Packet structure:
    //   syncId: int
    //   message: identifier
    //   rest: buf

    public int syncId;
    public ResourceLocation message;
    public FriendlyByteBuf rest;

    public LibGuiPacket(int syncId, ResourceLocation message, FriendlyByteBuf rest) {
        this.syncId = syncId;
        this.message = message;
        this.rest = rest;
    }

    public LibGuiPacket(FriendlyByteBuf buf) {
        syncId = buf.readVarInt();
        message = buf.readResourceLocation();
        rest = buf;
    }

    public static void toBytes(LibGuiPacket libGuiPacket, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(libGuiPacket.syncId);
        friendlyByteBuf.writeResourceLocation(libGuiPacket.message);
        friendlyByteBuf.writeBytes(libGuiPacket.rest);
    }

    public static boolean handler(LibGuiPacket libGuiPacket, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() ->
                ScreenNetworkingImpl.handle(ServerLifecycleHooks.getCurrentServer(),
                        Objects.requireNonNull(context.getSender()), libGuiPacket.rest));
        return true;
    }
}
