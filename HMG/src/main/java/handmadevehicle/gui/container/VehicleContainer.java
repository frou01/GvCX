package handmadevehicle.gui.container;

import handmadevehicle.entity.EntityVehicle;
import handmadevehicle.inventory.InventoryVehicle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class VehicleContainer extends Container {
	public InventoryVehicle inventoryVehicle;
	public IInventory userInventory;
	public int numRows;
	public int numRowsWeapon;
	public int numRowsCargo;
	public VehicleContainer(EntityPlayer player){
		if(player.ridingEntity instanceof EntityVehicle) {
			userInventory = player.inventory;
			inventoryVehicle = ((EntityVehicle) player.ridingEntity).getBaseLogic().inventoryVehicle;

			numRowsWeapon = inventoryVehicle.baseLogic.info.weaponSlotNum / 9;
			int raw;
			int line;
			for (raw = 0; raw < numRowsWeapon; ++raw)
			{
				for (line = 0; line < 9; ++line)
				{
					this.addSlotToContainer(new SlotModded(inventoryVehicle, line + raw * 9, 8 + line * 18, 18 + raw * 18));
				}
			}
			for (line = 0; line < inventoryVehicle.baseLogic.info.weaponSlotNum%9; ++line)
			{
				this.addSlotToContainer(new SlotModded(inventoryVehicle, line + (numRowsWeapon) * 9, 8 + line * 18, 18 + (numRowsWeapon) * 18));
			}
			numRowsWeapon = inventoryVehicle.baseLogic.info.weaponSlotNum / 9;

			int yoffset = numRowsWeapon * 18;
			numRowsCargo = inventoryVehicle.baseLogic.info.cargoSlotNum / 9;
			for (raw = 0; raw < numRowsCargo; ++raw)
			{
				for (line = 0; line < 9; ++line)
				{
					this.addSlotToContainer(new SlotModded(inventoryVehicle, line + raw * 9, 8 + line * 18, 18 + raw * 18 + yoffset));
				}
			}
			for (line = 0; line < inventoryVehicle.baseLogic.info.cargoSlotNum%9; ++line)
			{
				this.addSlotToContainer(new SlotModded(inventoryVehicle, line + (numRowsCargo) * 9, 8 + line * 18, 18 + (numRowsCargo) * 18 + yoffset));
			}
			numRowsCargo = inventoryVehicle.baseLogic.info.cargoSlotNum / 9 + (inventoryVehicle.baseLogic.info.cargoSlotNum%9 > 0 ? 1:0);

			numRows = numRowsCargo+numRowsWeapon;


			yoffset = (numRows - 3) * 18;
			for (raw = 0; raw < 3; ++raw)
			{
				for (line = 0; line < 9; ++line)
				{
					this.addSlotToContainer(new SlotModded(userInventory, line + raw * 9 + 9, 8 + line * 18, 103 + raw * 18 + yoffset));
				}
			}

			for (line = 0; line < 9; ++line)
			{
				this.addSlotToContainer(new SlotModded(userInventory, line, 8 + line * 18, 161 + yoffset));
			}
		}else {
			System.out.println("failed:open vehicle Inventory by " + player);
		}
	}
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotID)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(slotID);
		
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (slot.inventory == userInventory)
			{
				if (!this.mergeItemStack(itemstack1, 0, inventoryVehicle.getSizeInventory(), false))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, inventoryVehicle.getSizeInventory(), inventoryVehicle.getSizeInventory() + userInventory.getSizeInventory(), false))
			{
				return null;
			}
			
			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}
		
		return itemstack;
	}

	public ItemStack slotClick(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer p_75144_4_){
		try {
			return super.slotClick(p_75144_1_, p_75144_2_, p_75144_3_, p_75144_4_);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
