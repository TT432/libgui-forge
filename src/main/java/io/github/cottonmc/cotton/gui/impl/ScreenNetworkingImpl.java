package io.github.cottonmc.cotton.gui.impl;

import dustw.libgui.LibGui;
import dustw.libgui.network.LibGuiMessages;
import dustw.libgui.network.LibGuiPacket;
import dustw.libgui.network.PacketByteBufs;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ScreenNetworkingImpl implements ScreenNetworking {
    // Packet structure:
    //   syncId: int
    //   message: identifier
    //   rest: buf

    public static final ResourceLocation SCREEN_MESSAGE_S2C = new ResourceLocation(LibGui.MOD_ID, "screen_message_s2c");
    public static final ResourceLocation SCREEN_MESSAGE_C2S = new ResourceLocation(LibGui.MOD_ID, "screen_message_c2s");

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<SyncedGuiDescription, ScreenNetworkingImpl> instanceCache = new WeakHashMap<>();

    private final Map<ResourceLocation, MessageReceiver> messages = new HashMap<>();
    private final SyncedGuiDescription description;
    private final NetworkSide side;

    private ScreenNetworkingImpl(SyncedGuiDescription description, NetworkSide side) {
        this.description = description;
        this.side = side;
    }

    public void receive(ResourceLocation message, MessageReceiver receiver) {
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(receiver, "receiver");

        if (!messages.containsKey(message)) {
            messages.put(message, receiver);
        } else {
            throw new IllegalStateException("Message " + message + " on side " + side + " already registered");
        }
    }

    @Override
    public void send(ResourceLocation message, Consumer<FriendlyByteBuf> writer) {
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(writer, "writer");

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(description.containerId);
        buf.writeResourceLocation(message);
        writer.accept(buf);

        if (side == NetworkSide.SERVER)
            LibGuiMessages.sendToPlayer(new LibGuiPacket(description.containerId, message, buf), (ServerPlayer) description.player());
        else LibGuiMessages.sendToServer(new LibGuiPacket(description.containerId, message, buf));
    }

    public static void handle(Executor executor, Player player, FriendlyByteBuf buf) {
        AbstractContainerMenu screenHandler = player.containerMenu;

        // Packet data
        int syncId = buf.readVarInt();
        ResourceLocation messageId = buf.readResourceLocation();

        if (!(screenHandler instanceof SyncedGuiDescription)) {
            LOGGER.error("Received message packet for screen handler {} which is not a SyncedGuiDescription", screenHandler);
            return;
        } else if (syncId != screenHandler.containerId) {
            LOGGER.error("Received message for sync ID {}, current sync ID: {}", syncId, screenHandler.containerId);
            return;
        }

        ScreenNetworkingImpl networking = instanceCache.get(screenHandler);

        if (networking != null) {
            MessageReceiver receiver = networking.messages.get(messageId);

            if (receiver != null) {
                buf.retain();
                executor.execute(() -> {
                    try {
                        receiver.onMessage(buf);
                    } catch (Exception e) {
                        LOGGER.error("Error handling screen message {} for {} on side {}", messageId, screenHandler, networking.side, e);
                    } finally {
                        buf.release();
                    }
                });
            } else {
                LOGGER.warn("Message {} not registered for {} on side {}", messageId, screenHandler, networking.side);
            }
        } else {
            LOGGER.warn("GUI description {} does not use networking", screenHandler);
        }
    }

    public static ScreenNetworking of(SyncedGuiDescription description, NetworkSide networkSide) {
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(networkSide, "networkSide");

        if (description.getNetworkSide() == networkSide) {
            return instanceCache.computeIfAbsent(description, it -> new ScreenNetworkingImpl(description, networkSide));
        } else {
            return DummyNetworking.INSTANCE;
        }
    }

    private static final class DummyNetworking extends ScreenNetworkingImpl {
        static final DummyNetworking INSTANCE = new DummyNetworking();

        private DummyNetworking() {
            super(null, null);
        }

        @Override
        public void receive(ResourceLocation message, MessageReceiver receiver) {
            // NO-OP
        }

        @Override
        public void send(ResourceLocation message, Consumer<FriendlyByteBuf> writer) {
            // NO-OP
        }
    }
}
