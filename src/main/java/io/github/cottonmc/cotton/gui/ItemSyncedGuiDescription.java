package io.github.cottonmc.cotton.gui;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * A {@link SyncedGuiDescription} for an {@linkplain ItemStack item stack}
 * in an {@linkplain net.minecraft.world.Container inventory}.
 *
 * <p>The owning item is represented with a {@link SlotAccess}, which can be
 * an item in an entity's inventory or a block's container, or any other reference
 * to an item stack.
 *
 * <p>If the owning item stack changes in any way, the screen closes by default (see {@link #stillValid(Player)}).
 *
 * @since 7.0.0
 */
public class ItemSyncedGuiDescription extends SyncedGuiDescription {
    /**
     * A reference to the owning item stack of this GUI.
     */
    protected final SlotAccess owner;

    /**
     * The initial item stack of this GUI. This stack must <strong>not</strong> be mutated!
     */
    protected final ItemStack ownerStack;

    /**
     * Constructs an {@code ItemSyncedGuiDescription}.
     *
     * @param type            the screen handler type
     * @param syncId          the sync ID
     * @param playerInventory the inventory of the player viewing this GUI description
     * @param owner           a reference to the owning item stack of this GUI description
     */
    public ItemSyncedGuiDescription(MenuType<?> type, int syncId, Inventory playerInventory, SlotAccess owner) {
        super(type, syncId, playerInventory);
        this.owner = Objects.requireNonNull(owner, "Owner cannot be null");
        this.ownerStack = owner.get().copy();
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation for {@code ItemSyncedGuiDescription} returns {@code true} if and only if
     * the {@linkplain #owner current owning item stack} is {@linkplain ItemStack#matches fully equal}
     * to the {@linkplain #ownerStack original owner}.
     *
     * <p>If the item NBT is intended to change, subclasses should override this method to only check
     * the item and the count. Those subclasses should also take care to respond properly
     * to any NBT changes in the owning item stack.
     */
    @Override
    public boolean stillValid(Player entity) {
        return ItemStack.matches(ownerStack, owner.get());
    }
}
