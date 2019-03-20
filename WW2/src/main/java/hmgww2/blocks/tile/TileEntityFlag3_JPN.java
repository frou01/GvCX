package hmgww2.blocks.tile;
 
import hmgww2.mod_GVCWW2;
import hmgww2.entity.EntityJPNBase;
import hmgww2.entity.EntityJPN_Fighter;
import hmgww2.entity.EntityJPN_FighterA;
import hmgww2.entity.EntityJPN_S;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
 
/*
 * TileEntityのクラスです。
 * TileEntityは、Tick毎に特殊な動作をしたり、複雑なモデルを持ったり、
 * NBTを使ってデータを格納したり、色々な用途に使えます。
 * 
 * ただしこのクラス内で行われた処理やデータは基本的にサーバ側にしかないので、
 * 同期処理についてよく考えて実装する必要があります。
 */
public class TileEntityFlag3_JPN extends TileEntityBase
{
		
	public void updateEntity() {
		this.maxs = mod_GVCWW2.cfg_spawnblock_limit_s;
		this.maxv = mod_GVCWW2.cfg_spawnblock_limit_air;
		this.friend = new EntityJPNBase(this.worldObj);
		this.fre = 1;
		this.sorv = false;

		this.spawntime = 600;
		this.spawntimev = 900;
		this.range = 80;
		this.blocklevel = 3;
		this.helmet = mod_GVCWW2.armor_jpn;

		super.updateEntity();
	}

	protected void SpawnEntity(World par1World, int par1, int par2, int par3, int par4, int par5, int par6) {

		if (!par1World.isRemote && this.spawn) {

			for (int ii = 0; ii < 5; ++ii) {
				int ix = par1World.rand.nextInt(10);
				int iz = par1World.rand.nextInt(10);

				EntityJPN_S entityskeleton = new EntityJPN_S(par1World);
				entityskeleton.setLocationAndAngles(par1 + 0.5 + ix - 5, par2 + 2, par3 + 0.5 + iz - 5,
						entityskeleton.renderYawOffset, 0.0F);
				entityskeleton.setFlagMode(1);
				int iii = par1World.rand.nextInt(10);
				if (iii == 0) {
					entityskeleton.setCurrentItemOrArmor(0, new ItemStack(mod_GVCWW2.gun_type4Auto));
				} else if (iii == 1) {
					entityskeleton.setCurrentItemOrArmor(0, new ItemStack(mod_GVCWW2.gun_type4Auto));
				} else if (iii == 2) {
					entityskeleton.setCurrentItemOrArmor(0, new ItemStack(mod_GVCWW2.gun_type99lmg));
				} else if (iii == 3) {
					entityskeleton.setCurrentItemOrArmor(0, new ItemStack(mod_GVCWW2.gun_type99lmg));
				} else if (iii == 4) {
					entityskeleton.setCurrentItemOrArmor(0, new ItemStack(mod_GVCWW2.gun_rota_cannon));
				} else if (iii == 5) {
					entityskeleton.setCurrentItemOrArmor(0, new ItemStack(mod_GVCWW2.gun_rota_cannon));
				} else {
					entityskeleton.setCurrentItemOrArmor(0, new ItemStack(mod_GVCWW2.gun_type38));
				}
				entityskeleton.setCurrentItemOrArmor(4, new ItemStack(mod_GVCWW2.armor_jpn));
				if (!this.worldObj.isRemote) {
					par1World.spawnEntityInWorld(entityskeleton);
				}

			}
		}
	}
		
        protected void SpawnEntity2(World par1World, int par1, int par2, int par3, int par4, int par5, int par6) {
			
			if(!par1World.isRemote && this.spawn){
				int i = par1World.rand.nextInt(2);
				if(i == 0)
				{
					int ix = par1World.rand.nextInt(10);
					int iz = par1World.rand.nextInt(10);
					EntityJPN_FighterA entityskeleton = new EntityJPN_FighterA(par1World);
			            entityskeleton.setLocationAndAngles(par1+0.5 + ix - 5, par2+12, par3+0.5 + iz - 5, 90F, 0.0F);
			            entityskeleton.setFlagMode(1);
			        if(!this.worldObj.isRemote){
			            par1World.spawnEntityInWorld(entityskeleton);
			        }
				}else {
					int ix = par1World.rand.nextInt(10);
					int iz = par1World.rand.nextInt(10);
					EntityJPN_Fighter entityskeleton = new EntityJPN_Fighter(par1World);
			            entityskeleton.setLocationAndAngles(par1+0.5 + ix - 5, par2+12, par3+0.5 + iz - 5, 90F, 0.0F);
			            entityskeleton.setFlagMode(1);
			        if(!this.worldObj.isRemote){
			            par1World.spawnEntityInWorld(entityskeleton);
			        }
				}
			}
			if(!par1World.isRemote && this.spawn){
				int i = par1World.rand.nextInt(4);
				if(i == 0)
				{
					int ix = par1World.rand.nextInt(10);
					int iz = par1World.rand.nextInt(10);
					EntityJPN_FighterA entityskeleton = new EntityJPN_FighterA(par1World);
			            entityskeleton.setLocationAndAngles(par1+0.5 + ix - 5, par2+2, par3+0.5 + iz - 5, 90F, 0.0F);
			            entityskeleton.setMobMode(1);
			        if(!this.worldObj.isRemote){
			            par1World.spawnEntityInWorld(entityskeleton);
			        }
				}else {
					int ix = par1World.rand.nextInt(10);
					int iz = par1World.rand.nextInt(10);
					EntityJPN_Fighter entityskeleton = new EntityJPN_Fighter(par1World);
			            entityskeleton.setLocationAndAngles(par1+0.5 + ix - 5, par2+2, par3+0.5 + iz - 5, 90F, 0.0F);
			            entityskeleton.setMobMode(1);
			        if(!this.worldObj.isRemote){
			            par1World.spawnEntityInWorld(entityskeleton);
			        }
				}
			}
		}
}