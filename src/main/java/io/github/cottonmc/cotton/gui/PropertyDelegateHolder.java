package io.github.cottonmc.cotton.gui;

import net.minecraft.world.inventory.ContainerData;

/**
 * This interface can be implemented on block entity classes
 * for providing a property delegate.
 *
 * @see SyncedGuiDescription#getBlockPropertyDelegate(net.minecraft.world.inventory.ContainerLevelAccess)
 */
public interface PropertyDelegateHolder {
    /**
     * Gets this block entity's property delegate.
     *
     * <p>On the client, the returned property delegate <b>must</b> have a working implementation of
     * {@link ContainerData#set(int, int)}.
     *
     * @return the property delegate
     */
    ContainerData getPropertyDelegate();
}
