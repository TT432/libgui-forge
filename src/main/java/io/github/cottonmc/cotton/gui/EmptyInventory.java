package io.github.cottonmc.cotton.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * An empty inventory that cannot hold any items.
 */
public class EmptyInventory implements Container {
	public static final EmptyInventory INSTANCE = new EmptyInventory();
	
	private EmptyInventory() {}
	
	@Override
	public void clearContent() {}
	
	@Override
	public int getContainerSize() {
		return 0;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public ItemStack getItem(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeItem(int slot, int count) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItem(int slot, ItemStack stack) {
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

}
