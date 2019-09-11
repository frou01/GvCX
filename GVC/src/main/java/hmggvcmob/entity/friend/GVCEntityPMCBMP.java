package hmggvcmob.entity.friend;


import handmadevehicle.entity.parts.logics.TankBaseLogicLogic;
import hmggvcmob.ai.AITankAttack;
import handmadevehicle.entity.ExplodeEffect;
import handmadevehicle.entity.parts.*;
import handmadevehicle.entity.parts.logics.IbaseLogic;
import handmadevehicle.entity.parts.logics.MultiRiderLogics;
import handmadevehicle.entity.parts.turrets.TurretObj;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import static handmadevehicle.Utils.transformVecByQuat;
import static handmadevehicle.Utils.transformVecforMinecraft;
import static handmadevehicle.HMVehicle.HMV_Proxy;

public class GVCEntityPMCBMP extends EntityPMCBase implements ITank,IMultiTurretVehicle {
	// public int type;
	public TileEntity spawnedtile = null;
	int count_for_reset;
	public double angletime;
	public int fireCycle1;
	public int fireCycle2;
	public int cooltime;
	public int magazine;
	public int weaponMode;

	public float subturretrotationYaw;
	public float subturretrotationPitch;

//	public float bodyrotationYaw;
//	public float bodyrotationPitch;
//	public float prevbodyrotationYaw;
//	public float turretrotationYaw;
//	public float turretrotationPitch;
//
//	public float prevturretrotationYaw;
//	public float prevturretrotationPitch;
//	public float subturretrotationYaw;
//	public float subturretrotationPitch;

	public TankBaseLogicLogic baseLogic = new TankBaseLogicLogic(this,0.1f,0.4f,false,"gvcmob:gvcmob.BMPTrack");
	ModifiedBoundingBox nboundingbox;

	Vector3d playerpos = new Vector3d(-0.464f,2.2f,0.2948f);
	Vector3d zoomingplayerpos = new Vector3d(-0,2.2D,-0.3);
	Vector3d subturretpos = new Vector3d(0,2.06F,0);
	Vector3d missilepos = new Vector3d(0,1,0);
	Vector3d cannonpos = new Vector3d(0,2.06F,0);
	Vector3d turretpos = new Vector3d(0,0,0);

	Vector3d[] childposes = {new Vector3d(0.5,1,0.5),new Vector3d(-0.5,1,0.5),new Vector3d(0.5,1,1.5),new Vector3d(-0.5,1,1.5)};

	AITankAttack aiTankAttack;

	public TurretObj mainTurret;
	public TurretObj subTurret;
	public TurretObj missile;
	public TurretObj[] turrets;

	public GVCEntityPMCBMP(World par1World)
	{
		super(par1World);
		this.tasks.removeTask(aiSwimming);
		this.setSize(3F, 3F);
		baseLogic.tankinfo.canControlonWater = true;
		baseLogic.riddenByEntities = new Entity[5];
		baseLogic.seatInfos = new SeatInfo[]{new SeatInfo(new double[]{-0.5,2,0}),new SeatInfo(childposes[0]),new SeatInfo(childposes[1]),
				new SeatInfo(childposes[2]),new SeatInfo(childposes[3])};
		
		nboundingbox = new ModifiedBoundingBox(boundingBox.minX,boundingBox.minY,boundingBox.minZ,boundingBox.maxX,boundingBox.maxY,boundingBox.maxZ,
				0,1,0,3.4,2,6.5);
		nboundingbox.rot.set(baseLogic.bodyRot);
		HMV_Proxy.replaceBoundingbox(this,nboundingbox);
		nboundingbox.centerRotX = 0;
		nboundingbox.centerRotY = 0;
		nboundingbox.centerRotZ = 0;
		this.tasks.removeTask(aiSwimming);
		aiTankAttack = new AITankAttack(this,1600,400);
		this.tasks.addTask(1,aiTankAttack);
		viewWide = 2.09f;
		yOffset = 0;
		mainTurret = new TurretObj(worldObj);
		{
			mainTurret.onMotherPos = turretpos;
			mainTurret.prefab_turret.cannonPos = cannonpos;
			mainTurret.currentEntity = this;
			mainTurret.prefab_turret.gunInfo.power = 60;
			mainTurret.prefab_turret.gunInfo.ex = 5.0F;
			mainTurret.prefab_turret.gunInfo.destroyBlock = false;
			mainTurret.prefab_turret.gunInfo.guntype = 2;
			mainTurret.prefab_turret.gunInfo.soundbase = "gvcmob:gvcmob.73mmLowPresureGunFire";
		}
		missile = new TurretObj(worldObj);
		{
			missile.currentEntity = this;
			missile.prefab_turret.turretanglelimtPitchmin = -70;
			missile.prefab_turret.turretanglelimtPitchMax = 20;
			missile.prefab_turret.turretanglelimtYawmin = -10;
			missile.prefab_turret.turretanglelimtYawMax = 10;
			missile.prefab_turret.turretspeedY = 8;
			missile.prefab_turret.turretspeedP = 10;
			missile.prefab_turret.traverseSound = null;

			missile.prefab_turret.turretYawCenterpos = missilepos;
			missile.prefab_turret.cannonPos = missilepos;
			missile.prefab_turret.gunInfo.rates.set(0,200);
			missile.prefab_turret.gunInfo.spread_setting = 5;
			missile.prefab_turret.gunInfo.speed = 8;
			missile.prefab_turret.gunInfo.canlock = true;
			missile.prefab_turret.gunInfo.acceleration = 1;
			missile.prefab_turret.gunInfo.soundbase = "gvcmob:gvcmob.missile1";
			missile.prefab_turret.gunInfo.flashname = null;


			missile.prefab_turret.gunInfo.power =140;
			missile.prefab_turret.gunInfo.ex = 2.5f;
			missile.prefab_turret.gunInfo.destroyBlock = false;
			missile.prefab_turret.gunInfo.guntype = 3;
			missile.prefab_turret.linked_MotherTrigger = false;
		}
		mainTurret.addbrother(missile);
		subTurret = new TurretObj(worldObj);
		{
			subTurret.currentEntity = this;
			subTurret.turretanglelimtPitchmin = -5;
			subTurret.turretanglelimtPitchMax = 5;
			subTurret.turretanglelimtYawmin = -5;
			subTurret.turretanglelimtYawMax = 5;
			subTurret.turretspeedY = 8;
			subTurret.turretspeedP = 10;
			subTurret.traverseSound = null;

			subTurret.onMotherPos = subturretpos;
			subTurret.cycle_setting = 1;
			subTurret.spread = 5;
			subTurret.speed = 8;
			subTurret.firesound = "handmadeguns:handmadeguns.fire";
			subTurret.flashName = "arrow";
			subTurret.flashfuse = 1;
			subTurret.flashscale = 1.5f;

			subTurret.powor = 8;
			subTurret.ex = 0;
			subTurret.canex = false;
			subTurret.guntype = 0;

			subTurret.magazineMax = 250;
			subTurret.reloadSetting = 200;
			subTurret.flashoffset = 0.5f;
		}
		baseLogic.seatInfos[0].maingun = mainTurret;
		baseLogic.seatInfos[0].hasGun = true;
		baseLogic.seatInfos[0].hasParentGun = false;
		mainTurret.addchild_NOTtriggerLinked(subTurret);

		turrets = new TurretObj[]{mainTurret,missile};
	}
	public boolean interact(EntityPlayer p_70085_1_) {
		if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == p_70085_1_)) {
			if(p_70085_1_.isSneaking()){
				if(p_70085_1_.getEquipmentInSlot(0) != null) {
					ItemStack itemstack = p_70085_1_.getEquipmentInSlot(0);
					if (itemstack.getItem() == Items.iron_ingot) {
						if (!p_70085_1_.capabilities.isCreativeMode) itemstack.stackSize--;
						if (itemstack.stackSize <= 0 && !p_70085_1_.capabilities.isCreativeMode) {
							p_70085_1_.destroyCurrentEquippedItem();
						}
						this.setHealth(this.getHealth() + 30);
					}
					if (itemstack.getItem() == Item.getItemFromBlock(Blocks.iron_block)) {
						if (!p_70085_1_.capabilities.isCreativeMode) itemstack.stackSize--;
						if (itemstack.stackSize <= 0 && !p_70085_1_.capabilities.isCreativeMode) {
							p_70085_1_.destroyCurrentEquippedItem();
						}
						this.setHealth(this.getHealth() + 300);
					}
				}else
				if(this.getMobMode() == 0){
					mode = 1;
					this.setMobMode(1);
					homeposX = (int) p_70085_1_.posX;
					homeposY = (int) p_70085_1_.posY;
					homeposZ = (int) p_70085_1_.posZ;
					master = p_70085_1_;
					p_70085_1_.addChatComponentMessage(new ChatComponentTranslation(
							"Cover mode"));
				}else if(this.getMobMode() == 1) {
					mode = 2;
					this.setMobMode(2);

					p_70085_1_.addChatComponentMessage(new ChatComponentTranslation(
							"Defense  " + (int)posX + "," + (int)posZ));
					homeposX = (int) posX;
					homeposY = (int) posY;
					homeposZ = (int) posZ;
				}else if(this.getMobMode() == 2){
					mode = 0;
					this.setMobMode(0);
				}
			}else if(!p_70085_1_.isRiding()){
				mode = 0;
				this.setMobMode(0);
				pickupEntity(p_70085_1_,0);
			}
			return true;
		} else {
			return false;
		}
	}
	public void updateRiderPosition() {
		if (this.riddenByEntity != null) {
			mainTurret.calculatePos(new Vector3d(this.posX,this.posY,-this.posZ),baseLogic.bodyRot);
			Vector3d temp = new Vector3d(mainTurret.pos);
			Vector3d tempplayerPos = new Vector3d(HMV_Proxy.iszooming() ? zoomingplayerpos:playerpos);
			Vector3d playeroffsetter = new Vector3d(0,((worldObj.isRemote && this.riddenByEntity == HMV_Proxy.getEntityPlayerInstance()) ? 0:(this.riddenByEntity.getEyeHeight() + this.riddenByEntity.yOffset)),0);
			tempplayerPos.sub(playeroffsetter);
			Vector3d temp2 = mainTurret.getGlobalVector_fromLocalVector_onTurretPoint(tempplayerPos);
			temp.add(temp2);
			transformVecforMinecraft(temp);
			temp.add(playeroffsetter);
//			System.out.println(temp);
			this.riddenByEntity.setPosition(temp.x,
					temp.y,
					temp.z);
			this.riddenByEntity.posX = temp.x;
			this.riddenByEntity.posY = temp.y;
			this.riddenByEntity.posZ = temp.z;
		}
	}

	public void jump(){

	}

	public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_)
	{

		if(!worldObj.isRemote) {
			if (this.isInWater()) {
				this.moveFlying(p_70612_1_, p_70612_2_, this.isAIEnabled() ? 0.04F : 0.02F);
				this.motionY += 0.02D;
				double prevmotionX = motionX;
				double prevmotionZ = motionZ;
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				handleWaterMovement();
				if(!this.isInWater() && isCollidedHorizontally){
					this.moveEntity(0, 1, 0);
					this.moveEntity(prevmotionX/10, 0, prevmotionZ/10);
				}
				this.motionX *= 0.800000011920929D;
				this.motionY *= 0.800000011920929D;
				this.motionZ *= 0.800000011920929D;
				setAir(0);
			} else if (this.handleLavaMovement()) {
				this.moveFlying(p_70612_1_, p_70612_2_, 0.02F);
				this.motionY -= 0.02D;
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			} else {
				float f2 = 0.91F;

				if (this.onGround) {
					f2 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
				}

				float f3 = 0.16277136F / (f2 * f2 * f2);
				float f4;

				if (this.onGround) {
					f4 = this.getAIMoveSpeed() * f3;
				} else {
					f4 = this.jumpMovementFactor;
				}

				this.moveFlying(p_70612_1_, p_70612_2_, f4);
				f2 = 0.91F;

				if (this.onGround) {
					f2 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
				}

				this.motionY -= 0.08D;

				this.moveEntity(this.motionX, this.motionY, this.motionZ);

				this.motionY *= 0.9800000190734863D;
				this.motionX *= (double) f2;
				this.motionZ *= (double) f2;
			}
		}
	}
	public boolean handleWaterMovement()
	{
		if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D).offset(0,1.0,0), Material.water, this))
		{
			if (!this.inWater)
			{
				float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;

				if (f > 1.0F)
				{
					f = 1.0F;
				}

				this.playSound(this.getSplashSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
				float f1 = (float)MathHelper.floor_double(this.boundingBox.minY + 1.0);
				int i;
				float f2;
				float f3;

				for (i = 0; (float)i < 1.0F + this.width * 20.0F; ++i)
				{
					f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					this.worldObj.spawnParticle("bubble", this.posX + (double)f2, (double)(f1 + 1.0F), this.posZ + (double)f3, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
				}

				for (i = 0; (float)i < 1.0F + this.width * 20.0F; ++i)
				{
					f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
					this.worldObj.spawnParticle("splash", this.posX + (double)f2, (double)(f1 + 1.0F), this.posZ + (double)f3, this.motionX, this.motionY, this.motionZ);
				}
			}

			this.fallDistance = 0.0F;
			this.inWater = true;
			this.extinguish();
		}
		else
		{
			this.inWater = false;
		}

		return this.inWater;
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(30.0D);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(28, Float.valueOf(0));
		this.dataWatcher.addObject(29, Float.valueOf(0));
		this.dataWatcher.addObject(30, Float.valueOf(0));
	}


	public void setRotationYaw(float floats) {
		this.dataWatcher.updateObject(28, Float.valueOf(floats));
	}
	public float getRotationYaw() {
		return this.dataWatcher.getWatchableObjectFloat(28);
	}
	public void setRotationPitch(float floats) {
		this.dataWatcher.updateObject(29, Float.valueOf(floats));
	}
	public float getRotationPitch() {
		return this.dataWatcher.getWatchableObjectFloat(29);
	}
	public void setRotationRoll(float floats) {
		this.dataWatcher.updateObject(30, Float.valueOf(floats));
	}
	public float getRotationRoll() {
		return this.dataWatcher.getWatchableObjectFloat(30);
	}


	public boolean attackEntityFrom(DamageSource source, float par2) {
		if (this.riddenByEntity == source.getEntity()) {
			return false;
		} else {
			if (par2 <= 25) {
				if (!source.getDamageType().equals("mob")) this.playSound("gvcmob:gvcmob.ArmorBounce", 0.5F, 2F);
				return false;
			}
			this.playSound("gvcmob:gvcmob.armorhit", 0.5F, 1F);
			return super.attackEntityFrom(source, par2);
		}

	}

	public void addRandomArmor()
	{
		super.addRandomArmor();
	}


	public void onUpdate()
	{
		if(mainTurret.isreloading()){
			weaponMode = 1;
		}else {
			weaponMode = 0;
		}
		nboundingbox.update(this.posX,this.posY,this.posZ);
		super.onUpdate();
		tankUpdate();
		this.rotationYaw = this.renderYawOffset = baseLogic.bodyrotationYaw;
		this.rotationPitch = baseLogic.bodyrotationPitch;
		baseLogic.turretrotationYaw = (float) mainTurret.turretrotationYaw;
		baseLogic.turretrotationPitch = (float) mainTurret.turretrotationPitch;
	}
	
	public void tankUpdate(){
		this.stepHeight = 1.5f;
		if(!this.worldObj.isRemote){
			baseLogic.updateServer();
		}else{
			baseLogic.updateClient();
			mode = getMobMode();
		}
		baseLogic.updateCommon();
	}
	
	public boolean pickupEntity(Entity p_70085_1_, int StartSeachSeatNum){
		return ((MultiRiderLogics)((HasBaseLogic)this).getBaseLogic()).pickupEntity(p_70085_1_,StartSeachSeatNum);
	}
	public void mainFireToTarget(Entity target){
		mainTurret.currentEntity = this;
		mainTurret.fire();
//        Vector3d Vec_transformedbybody = baseLogic.getTransformedVector_onturret(cannonpos,turretYawCenterpos);
//
//        Utils.transformVecforMinecraft(Vec_transformedbybody);
//        if(fireCycle1 <0){
//            fireCycle1 = 100;
//            if (!this.worldObj.isRemote) {
//                Vector3d lookVec = baseLogic.getCannonDir();
//                Utils.transformVecforMinecraft(lookVec);
//                HMGPacketHandler.INSTANCE.sendToAll(new PacketPlaysound(this, "gvcmob:gvcmob.120mmFire", 1, 5));
//                if (this.getEntityData().getFloat("GunshotLevel") < 0.1)
//                    soundedentity.add(this);
//                this.getEntityData().setFloat("GunshotLevel", 5);
//                HMGEntityBulletExprode var3 = new HMGEntityBulletExprode(this.worldObj, this, 600, 6.5f, 3, 5.0F, false);
//                var3.gra = (float) (0.029f / cfg_defgravitycof);
//                var3.setLocationAndAngles(
//                        this.posX + Vec_transformedbybody.x,
//                        this.posX + Vec_transformedbybody.y,
//                        this.posX + Vec_transformedbybody.z,
//                        baseLogic.turretrotationYaw, baseLogic.turretrotationPitch);
//                var3.setThrowableHeading(lookVec.x,lookVec.y,lookVec.z,8,2);
//                var3.canbounce = false;
//                var3.fuse = 0;
//                this.worldObj.spawnEntityInWorld(var3);
//                {
//                    PacketSpawnParticle flash = new PacketSpawnParticle(
//                            this.posX + Vec_transformedbybody.x + lookVec.x * 6,
//                            this.posY + Vec_transformedbybody.y + lookVec.y * 6,
//                            this.posZ + Vec_transformedbybody.z + lookVec.z * 6,
//                            toDegrees(-atan2(lookVec.x, lookVec.z)),
//                            toDegrees(-asin(lookVec.y)), 100, "CannonMuzzleFlash", true);
//                    flash.fuse = 3;
//                    flash.scale = 5;
//                    flash.id = 100;
//                    HMGPacketHandler.INSTANCE.sendToAll(flash);
//                }
//            }
////			if (!this.worldObj.isRemote) {
////				this.worldObj.createExplosion(this, this.posX,
////						this.posY + 2.2D,this.posZ, 0.0F, false);
////			}
//        }
	}

	@Override
	public TurretObj getMainTurret() {
		return mainTurret;
	}
	
	@Override
	public TurretObj[] getmainTurrets() {
		return new TurretObj[]{mainTurret,missile};
	}
	
	@Override
	public TurretObj[] getsubTurrets() {
		return new TurretObj[]{subTurret};
	}
	
	@Override
	public TurretObj[] getTurrets() {
		return turrets;
	}

	@Override
	public IbaseLogic getBaseLogic() {
		return baseLogic;
	}

	public void mainFire(){
		switch (weaponMode){
			case 0:
				mainTurret.currentEntity = this.riddenByEntity;
				mainTurret.fire();
				missile.freezTrigger(5);
				break;
			case 1:
				missile.currentEntity = this.riddenByEntity;
				missile.fire();
				break;
		}
//        if(fireCycle1 <0){
//            fireCycle1 = 100;
//
//
//            if (!this.worldObj.isRemote) {
//
//                Vector3d lookVec = baseLogic.getCannonDir();
//                Utils.transformVecforMinecraft(lookVec);
//
//
//
//                Vector3d Vec_transformedbybody = baseLogic.getTransformedVector_onturret(cannonpos,turretYawCenterpos);
//
//                Utils.transformVecforMinecraft(Vec_transformedbybody);
//
//                HMGPacketHandler.INSTANCE.sendToAll(new PacketPlaysound(this,"gvcmob:gvcmob.120mmFire",1,10));
//                this.getEntityData().setFloat("GunshotLevel",10);
//                soundedentity.add(this);
//                if(this.riddenByEntity != null){
//
//                    soundedentity.add(this.riddenByEntity);
//                    HMGEntityBulletExprode var3 = new HMGEntityBulletExprode(this.worldObj, riddenByEntity, 600, 6.5f, 3, 5.0F, false);
//                    var3.gra = (float) (0.029f / cfg_defgravitycof);
//                    var3.setLocationAndAngles(this.posX + Vec_transformedbybody.x, this.posY + Vec_transformedbybody.y, this.posZ + Vec_transformedbybody.z,
//                            baseLogic.turretrotationYaw, baseLogic.turretrotationPitch);
//                    var3.setThrowableHeading(lookVec.x, lookVec.y, lookVec.z, 8, 0.5F);
//                    var3.canbounce = false;
//                    var3.fuse = 0;
//                    this.worldObj.spawnEntityInWorld(var3);
//                }
//
//
//
//                {
//                    PacketSpawnParticle flash = new PacketSpawnParticle(
//                            this.posX + Vec_transformedbybody.x + lookVec.x*6,
//                            this.posY + Vec_transformedbybody.y + lookVec.y*6,
//                            this.posZ + Vec_transformedbybody.z + lookVec.z*6,
//                            toDegrees(-atan2(lookVec.x,lookVec.z)),
//                            toDegrees(-asin(lookVec.y)),100,"CannonMuzzleFlash",true);
//                    flash.fuse = 3;
//                    flash.scale = 5;
//                    flash.id = 100;
//                    HMGPacketHandler.INSTANCE.sendToAll(flash);
//                }
//            }
////			if (!this.worldObj.isRemote) {
////				this.worldObj.createExplosion(this, this.posX,
////						this.posY + 2.2D,this.posZ, 0.0F, false);
////			}
//        }
	}
	public void subFireToTarget(Entity target){
		subTurret.currentEntity = this;
		if(subTurret.aimToEntity(target)){
			subTurret.target = target;
			subTurret.fire();
		}
		missile.currentEntity = this;
		if(missile.aimToEntity(target)){
			missile.target = target;
			missile.fire();
		}
		subturretrotationYaw = (float) subTurret.turretrotationYaw;
		subturretrotationPitch = (float) subTurret.turretrotationPitch;
	}
	public void subFire(){
		subTurret.currentEntity = riddenByEntity;
		subTurret.fire();
//        Quat4d turretyawrot = new Quat4d(0,0,0,1);
//
//        Vector3d axisy = Utils.transformVecByQuat(new Vector3d(0,1,0), turretyawrot);
//        AxisAngle4d axisyangled = new AxisAngle4d(axisy, toRadians(baseLogic.turretrotationYaw)/2);
//        turretyawrot = Utils.quatRotateAxis(turretyawrot,axisyangled);
//
//
//
//
//        {
//            if (fireCycle2 < 0) {
//
//
//                Vector3d lookVec = new Vector3d(0,0,1);
//
//
//                Quat4d gun = new Quat4d(0, 0, 0, 1);
//                Vector3d axisY = transformVecByQuat(baseLogic.unitY, gun);
//                AxisAngle4d axisxangledY = new AxisAngle4d(axisY, toRadians(subturretrotationYaw) / 2);
//                gun = quatRotateAxis(gun, axisxangledY);
//
//                Vector3d axisX = transformVecByQuat(baseLogic.unitX, gun);
//                AxisAngle4d axisxangledX = new AxisAngle4d(axisX, toRadians(-subturretrotationPitch) / 2);
//                gun = quatRotateAxis(gun, axisxangledX);
//
//                lookVec = transformVecByQuat(lookVec,gun);
//                lookVec = transformVecByQuat(lookVec,turretyawrot);
//                lookVec = transformVecByQuat(lookVec,baseLogic.bodyRot);
//
//                Utils.transformVecforMinecraft(lookVec);
//
//
//
//
//                Vector3d Vec_transformed = baseLogic.getTransformedVector_onturret(subturretpos,turretYawCenterpos);
//
//                Utils.transformVecforMinecraft(Vec_transformed);
//
//
//                fireCycle2 = 1;
//                HMGEntityBullet var3 = new HMGEntityBullet(worldObj, this, 20, 8F, 3.0F);
//                var3.gra = 0.05f;
//                var3.setLocationAndAngles(this.posX + Vec_transformed.x, this.posY + Vec_transformed.y, this.posZ + Vec_transformed.z,
//                        riddenByEntity.getRotationYawHead(), riddenByEntity.rotationPitch);
//                var3.setHeadingFromThrower(riddenByEntity.rotationPitch, riddenByEntity.getRotationYawHead(), 0, 5.8f, 3.0F);
//                var3.setThrowableHeading(lookVec.x,lookVec.y,lookVec.z,5.8f,3.0F);
//                if (!this.worldObj.isRemote) {
//                    this.worldObj.spawnEntityInWorld(var3);
//                    HMGPacketHandler.INSTANCE.sendToAll(new PacketSpawnParticle(this.posX + Vec_transformed.x, this.posY + Vec_transformed.y, this.posZ + Vec_transformed.z, 100));
//                    this.playSound("handmadeguns:handmadeguns.HeavyMachineGun", 5.0F, 1.0F);
//                    if(this.getEntityData().getFloat("GunshotLevel")<0.1)
//                        soundedentity.add(this);
//                    this.getEntityData().setFloat("GunshotLevel",5);
//                }
//            }
//        }
	}
	
	@Override
	public boolean standalone() {
		return mode != 0;
	}

	protected void onDeathUpdate() {
		++this.deathTicks;
		if(this.deathTicks == 3){
			//this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 0F, false);
			ExplodeEffect ex = new ExplodeEffect(this, 3F);
			ex.offset[0] = (float) (rand.nextInt(30) - 15)/10;
			ex.offset[1] = (float) (rand.nextInt(30) - 15)/10 + 1.5f;
			ex.offset[2] = (float) (rand.nextInt(30) - 15)/10;
			ex.Ex();
		}
		if(this.deathTicks > 40) {
			if (worldObj.isRemote) {
				for (int i = 0; i < 5; i++) {
					worldObj.spawnParticle("flame",
							this.posX + (float) (rand.nextInt(20) - 10) / 10,
							this.posY + (float) (rand.nextInt(20) - 10) / 10 + 1.5f,
							this.posZ + (float) (rand.nextInt(20) - 10) / 10,
							0.0D, 0.5D, 0.0D);
					worldObj.spawnParticle("smoke",
							this.posX + (float) (rand.nextInt(30) - 15) / 10,
							this.posY + (float) (rand.nextInt(30) - 15) / 10 + 1.5f,
							this.posZ + (float) (rand.nextInt(30) - 15) / 10,
							0.0D, 0.2D, 0.0D);
					worldObj.spawnParticle("cloud",
							this.posX + (float) (rand.nextInt(30) - 15) / 10,
							this.posY + (float) (rand.nextInt(30) - 15) / 10 + 1.5f,
							this.posZ + (float) (rand.nextInt(30) - 15) / 10,
							0.0D, 0.3D, 0.0D);
				}
			}
			this.playSound("gvcguns:gvcguns.fireee", 1.20F, 0.8F);
		}else
		if (rand.nextInt(3) == 0) {
			ExplodeEffect ex = new ExplodeEffect(this, 1F);
			ex.offset[0] = (float) (rand.nextInt(30) - 15) / 10;
			ex.offset[1] = (float) (rand.nextInt(30) - 15) / 10;
			ex.offset[2] = (float) (rand.nextInt(30) - 15) / 10;
			ex.Ex();
		}
		if (this.deathTicks >= 140) {
			ExplodeEffect ex = new ExplodeEffect(this, 8F);
			ex.Ex();
			for (int i = 0; i < 15; i++) {
				worldObj.spawnParticle("flame",
						this.posX + (float) (rand.nextInt(20) - 10) / 10,
						this.posY + (float) (rand.nextInt(20) - 10) / 10,
						this.posZ + (float) (rand.nextInt(20) - 10) / 10,
						(rand.nextInt(20) - 10) / 100,
						(rand.nextInt(20) - 10) / 100,
						(rand.nextInt(20) - 10) / 100 );
				worldObj.spawnParticle("smoke",
						this.posX + (float) (rand.nextInt(30) - 15) / 10,
						this.posY + (float) (rand.nextInt(30) - 15) / 10,
						this.posZ + (float) (rand.nextInt(30) - 15) / 10,
						(rand.nextInt(20) - 10) / 100,
						(rand.nextInt(20) - 10) / 100,
						(rand.nextInt(20) - 10) / 100 );
				worldObj.spawnParticle("cloud",
						this.posX + (float) (rand.nextInt(30) - 15) / 10,
						this.posY + (float) (rand.nextInt(30) - 15) / 10,
						this.posZ + (float) (rand.nextInt(30) - 15) / 10,
						(rand.nextInt(20) - 10) / 100,
						(rand.nextInt(20) - 10) / 100,
						(rand.nextInt(20) - 10) / 100 );
			}
			if(this.deathTicks == 150)
				this.setDead();
		}
	}
	public boolean isConverting() {
		return false;
	}
	public void readEntityFromNBT(NBTTagCompound p_70037_1_)
	{
		super.readEntityFromNBT(p_70037_1_);
		baseLogic.turretrotationYaw 			= p_70037_1_.getFloat("turretrotationYaw");
		baseLogic.turretrotationPitch 		= p_70037_1_.getFloat("turretrotationPitch");
		baseLogic.bodyrotationYaw 			= p_70037_1_.getFloat("bodyrotationYaw");
		baseLogic.bodyrotationPitch 			= p_70037_1_.getFloat("bodyrotationPitch");

		this.subturretrotationYaw 		= p_70037_1_.getFloat("subturretrotationYaw");
		this.subturretrotationPitch 	= p_70037_1_.getFloat("subturretrotationPitch");
	}
	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound p_70014_1_)
	{
		super.writeEntityToNBT(p_70014_1_);
		p_70014_1_.setFloat("turretrotationYaw",baseLogic.turretrotationYaw);
		p_70014_1_.setFloat("turretrotationPitch",baseLogic.turretrotationPitch);
		p_70014_1_.setFloat("bodyrotationYaw",	baseLogic.bodyrotationYaw);
		p_70014_1_.setFloat("bodyrotationPitch",baseLogic.bodyrotationPitch);
		p_70014_1_.setFloat("subturretrotationYaw",this.subturretrotationYaw);
		p_70014_1_.setFloat("subturretrotationPitch",this.subturretrotationPitch);
	}




	@Override
	public float getviewWide() {
		return viewWide;
	}


	@Override
	public void setspawnedtile(TileEntity flag) {
		spawnedtile = flag;
	}


	@Override
	public int getfirecyclesettings1() {
		return 100;
	}

	@Override
	public int getfirecycleprogress1() {
		return fireCycle1;
	}

	@Override
	public int getfirecyclesettings2() {
		return 0;
	}

	@Override
	public int getfirecycleprogress2() {
		return 0;
	}



	@Override
	public float getturretrotationYaw() {
		return baseLogic.turretrotationYaw;
	}

	@Override
	public float getbodyrotationYaw() {
		return baseLogic.bodyrotationYaw;
	}
	
	@Override
	public void setrotationYawmotion(float value) {
		baseLogic.rotationmotion = value;
	}

	@Override
	public void setBodyRot(Quat4d rot) {
		baseLogic.bodyRot.set(rot);
	}

	@Override
	public float getthrottle() {
		return baseLogic.throttle;
	}

	@Override
	public void setthrottle(float value) {
		baseLogic.throttle = value;
	}
	

	public void moveFlying(float p_70060_1_, float p_70060_2_, float p_70060_3_){
		baseLogic.moveFlying(p_70060_1_,p_70060_2_,p_70060_3_);
	}


	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch){
		super.setLocationAndAngles(x,y,z,yaw,pitch);
		baseLogic.setLocationAndAngles(yaw,pitch);
	}

	public boolean canBePushed()
	{
		return false;
	}

	protected void collideWithEntity(Entity p_82167_1_){
		if(p_82167_1_.ridingEntity == null)p_82167_1_.applyEntityCollision(this);
	}
	public void applyEntityCollision(Entity p_70108_1_)
	{
		if (p_70108_1_ != this && p_70108_1_.riddenByEntity != this && p_70108_1_.ridingEntity != this && p_70108_1_.ridingEntity != null)
		{
			double d0 = p_70108_1_.posX - this.posX;
			double d1 = p_70108_1_.posZ - this.posZ;
			double d2 = MathHelper.abs_max(d0, d1);

			if (d2 >= 0.009999999776482582D)
			{
				d2 = (double)MathHelper.sqrt_double(d2);
				d0 /= d2;
				d1 /= d2;
				double d3 = 1.0D / d2;

				if (d3 > 1.0D)
				{
					d3 = 1.0D;
				}

				d0 *= d3;
				d1 *= d3;
				d0 *= 0.05000000074505806D;
				d1 *= 0.05000000074505806D;
				d0 *= (double)(1.0F - this.entityCollisionReduction);
				d1 *= (double)(1.0F - this.entityCollisionReduction);
				p_70108_1_.addVelocity(d0, 0.0D, d1);
			}
		}
	}
	
	@Override
	public void moveEntity(double x, double y, double z){
		baseLogic.moveEntity(x,y,z);
	}
	
	@Override
	public void updateFallState_public(double stepHeight, boolean onground){
		this.updateFallState(stepHeight,onground);
	}
	
	@Override
	public void func_145775_I_public() {
		this.func_145775_I();
	}
}
