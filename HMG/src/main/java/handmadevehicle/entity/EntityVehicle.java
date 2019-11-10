package handmadevehicle.entity;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import handmadeguns.entity.IFF;
import handmadeguns.entity.I_SPdamageHandle;
import handmadevehicle.AddNewVehicle;
import handmadevehicle.SlowPathFinder.ModifiedPathNavigater;
import handmadevehicle.SlowPathFinder.WorldForPathfind;
import handmadevehicle.entity.parts.*;
import handmadevehicle.entity.parts.logics.BaseLogic;
import handmadevehicle.entity.parts.turrets.TurretObj;
import handmadevehicle.entity.prefab.Prefab_Vehicle_Base;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

import javax.vecmath.Vector3d;

import static handmadeguns.HandmadeGunsCore.cfg_defgravitycof;
import static handmadeguns.Util.Utils.getmovingobjectPosition_forBlock;
import static handmadevehicle.HMVehicle.HMV_Proxy;
import static handmadevehicle.Utils.*;
import static handmadevehicle.Utils.transformVecByQuat;
import static java.lang.Math.abs;
import static java.lang.Math.sin;

public class EntityVehicle extends Entity implements IFF,IVehicle,IMultiTurretVehicle,IEntityAdditionalSpawnData, I_SPdamageHandle {
	public String typename;
	
	public int deathTicks;
	

	public double movespeed = 0.3d;
	private WorldForPathfind worldForPathfind;
	private ModifiedPathNavigater modifiedPathNavigater;
	public boolean canUseByMob = false;
	public boolean despawn = false;

	BaseLogic baseLogic;
	ModifiedBoundingBox nboundingbox;
	
	public EntityVehicle(World par1World) {
		super(par1World);
		this.modifiedPathNavigater = new ModifiedPathNavigater(this, worldObj,worldForPathfind = new WorldForPathfind(worldObj),64);
		//AI入りはこのmodでは実装しない！良いな！
		
		renderDistanceWeight = 16384;
		if(this.worldObj instanceof WorldServer) {
			EntityTracker entitytracker = ((WorldServer) this.worldObj).getEntityTracker();
			ObfuscationReflectionHelper.setPrivateValue(EntityTracker.class, entitytracker, 1048576, "entityViewDistance", "E", "field_72792_d");
		}
		this.setSize(3f, 3f);
	}
	
	@Override
	protected void entityInit() {
	
	}
	
	public EntityVehicle(World par1World,String typename) {
		this(par1World);
		this.init_2(typename);
	}
	static public EntityVehicle EntityVehicle_spawnByMob(World par1World,String typename) {
		EntityVehicle bespawningEntity = new EntityVehicle(par1World,typename);
		bespawningEntity.canUseByMob = true;
		bespawningEntity.despawn = true;
		return bespawningEntity;
	}
	public void init_2(String typename) {
		this.typename = typename;
		
		ignoreFrustumCheck = true;
		baseLogic = new BaseLogic(worldObj, this);
		Prefab_Vehicle_Base infos = AddNewVehicle.seachInfo(typename);
		baseLogic.setinfo(infos);
		nboundingbox = new ModifiedBoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ,
				0, 1.5, 0,
				3.4, 3, 6.5);
		nboundingbox.rot.set(baseLogic.bodyRot);
		nboundingbox.centerRotX = baseLogic.info.rotcenter[0];
		nboundingbox.centerRotY = baseLogic.info.rotcenter[1];
		nboundingbox.centerRotZ = baseLogic.info.rotcenter[2];
		if(infos.boxes != null){
			nboundingbox.boxes = new OBB[infos.boxes.length];
			int cnt = 0;
			for(OBB aobb:infos.boxes){
				nboundingbox.boxes[cnt] = aobb.getCopy();
//				System.out.println("" + nboundingbox.boxes[cnt].toString());
				cnt++;
			}
		}
		nboundingbox.calculateMax_And_Min();
		HMV_Proxy.replaceBoundingbox(this,nboundingbox);
//		this.applyEntityAttributes2();
	}
	public ModifiedPathNavigater getNavigator()
	{
		return this.modifiedPathNavigater;
	}
	
	public double getMountedYOffset() {
		return 0.0D;
	}
	
	
	public void onUpdate() {
		modifiedPathNavigater.onUpdateNavigation();
		boolean onground = this.onGround;
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		double backupMotionX = this.motionX,backupMotionZ = this.motionY,backupMotionY = this.motionZ;
		super.onUpdate();
		this.motionX = backupMotionX;
		this.motionY = backupMotionZ;
		this.motionZ = backupMotionY;
		this.onGround = onground;
		baseLogic.onUpdate();
		if (!this.worldObj.isRemote)
		{
			this.collideWithNearbyEntities();
		}
		despawnEntity();
	}
	public void setLocationAndAngles(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_)
	{
		this.prevPosX = this.posX = p_70012_1_;
		this.prevPosY = this.posY = p_70012_3_ + (double)this.yOffset;
		this.prevPosZ = this.posZ = p_70012_5_;
		this.rotationYaw = p_70012_7_;
		this.rotationPitch = p_70012_8_;
		this.setPosition(this.posX, this.posY, this.posZ);
	}
	
	public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
//		baseLogic.setVelocity(p_70016_1_,p_70016_3_,p_70016_5_);
	}
	
	@Override
	public BaseLogic getBaseLogic() {
		return baseLogic;
	}
	
	public boolean isConverting() {
		return false;
	}
	
	public boolean canBePushed() {
		return false;
	}
	
	public void moveFlying(float p_70060_1_, float p_70060_2_, float p_70060_3_) {
	}
	
	public void jump() {
	
	}
	
	
	
	
	
	
	
	
	public boolean canAttackClass(Class par1Class) {
		return EntityCreature.class != par1Class;
	}

//	public void setAttackTarget(EntityLivingBase p_70624_1_)
//	{
//		if(mode == 3 || mode == 4){
//			super.setAttackTarget(null);
//		}else super.setAttackTarget(p_70624_1_);
//	}
	
	public int getVerticalFaceSpeed()
	{
		return 90;
	}
	
	public boolean isAIEnabled() {
		return true;
	}
	
	protected boolean canDespawn()
	{
		return despawn && rider_canDespawn();
	}
	protected boolean rider_canDespawn(){
		for(Entity entity:baseLogic.riddenByEntities){
			if(entity != null)return false;
		}
		return true;
	}
	
	
	public boolean attackEntityFrom_with_Info(MovingObjectPosition movingObjectPosition, DamageSource source, float level){
		BaseLogic temp;
		float temparomor = 0;
		if (source.getEntity() != null) {
			temp = ((HasBaseLogic) this).getBaseLogic();
			Vector3d TankFrontVec = new Vector3d(0, 0, -1);
			TankFrontVec = transformVecByQuat(TankFrontVec, temp.bodyRot);
			TankFrontVec.z *= -1;
//			double angle_position = abs(toDegrees(TankFrontVec.angle(shooterPositionVec)));
			Vector3d TankRighttVec = new Vector3d(-1, 0, 0);
			TankRighttVec = transformVecByQuat(TankRighttVec, temp.bodyRot);
			TankRighttVec.z *= -1;
			int boxId = movingObjectPosition.sideHit / 6;
			OBB box = nboundingbox.boxes[boxId];
			int hitside = movingObjectPosition.sideHit % 6;
			double angle_sin = 0;
			Vector3d Incident_vector = new Vector3d();
			Incident_vector.normalize((Vector3d) movingObjectPosition.hitInfo);
			switch (hitside) {
				case 2: //正面
					System.out.println("front");
					angle_sin = abs(Incident_vector.z);
					temparomor = box.armor_Front;
					break;
				case 4://ヒダリ
					System.out.println("left");
					angle_sin = abs(Incident_vector.x);
					temparomor = box.armor_SideLeft;
					break;
				case 5://ミギ
					System.out.println("right");
					angle_sin = abs(Incident_vector.x);
					temparomor = box.armor_SideRight;
					break;
				case 0://上
					System.out.println("top");
					angle_sin = abs(Incident_vector.y);
					temparomor = box.armor_Top;
					break;
				case 1://下
					System.out.println("bottom");
					angle_sin = abs(Incident_vector.y);
					temparomor = box.armor_Bottom;
					break;
				case 3: //背面
					System.out.println("back");
					angle_sin = abs(Incident_vector.z);
					temparomor = box.armor_Back;
					break;
			}
//			System.out.println("angle_sin" + angle_sin);
			temparomor /=angle_sin;
//			System.out.println("temparomor" + temparomor);
			if (level <= temparomor) {
				if (!source.getDamageType().equals("mob"))
					this.playSound("gvcmob:gvcmob.ArmorBounce", 0.5F, 1/(level / temparomor));
			}else this.playSound("gvcmob:gvcmob.armorhit",5, 1F);
			level -= temparomor;
			if(level<0)level = 0;
		}
		return this.attackEntityFrom(source,level);
	}

	protected void despawnEntity()
	{

		if (worldObj instanceof WorldServer)
		{
			EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0D);
			if(entityplayer != null) {
				double d0 = entityplayer.posX - this.posX;
				double d1 = entityplayer.posY - this.posY;
				double d2 = entityplayer.posZ - this.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;

				if (this.canDespawn() && d3 > ((WorldServer)worldObj).func_73046_m().getConfigurationManager().getViewDistance()*16 *
						((WorldServer)worldObj).func_73046_m().getConfigurationManager().getViewDistance()*16) {
//					System.out.println("debug");
					this.setDead();
				}
			}
		}
	}

	public boolean attackEntityFrom(DamageSource source, float par2) {
		if(baseLogic!= null){
			if(baseLogic.riddenByEntities[baseLogic.getpilotseatid()] != null){
				baseLogic.riddenByEntities[baseLogic.getpilotseatid()].attackEntityFrom(source,par2);
			}
		}
		if(source.getDamageType().equals(DamageSource.fall.damageType) ||
				   source.getDamageType().equals(DamageSource.outOfWorld.damageType) ||
				   source.getDamageType().equals(DamageSource.inWall.damageType))return attackEntityFrom_exceptArmor(source, par2);

		if(source.isExplosion()){
			par2 *= baseLogic.info.antiExplosionCof;
		}

		if(par2 < 0)par2 = 0;
		if (this.riddenByEntity == source.getEntity()) {
			return false;
		} else if (this == source.getEntity()) {
			return false;
		} else if(this.isRidingEntity(source.getEntity())) {
			return false;
		}else {
			baseLogic.health -= par2;
			return true;
		}
	}
	public boolean attackEntityFrom_exceptArmor(DamageSource source, float par2){
		baseLogic.health -= par2;
		return super.attackEntityFrom(source, par2);
	}
	
	
	
	
	
	
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1)
	{
		return super.getBrightnessForRender(par1);
	}
	
	/**
	 * Gets how bright this entity is.
	 */
    /*public float getBrightness(float par1)
    {
        return 1.0F;
    }*/
	
	public boolean canEntityBeSeen(Entity p_70685_1_)
	{
		Vec3 startpos = Vec3.createVectorHelper(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ);
		Vec3 targetpos = Vec3.createVectorHelper(p_70685_1_.posX, p_70685_1_.posY + (double) p_70685_1_.getEyeHeight(), p_70685_1_.posZ);
		MovingObjectPosition movingobjectposition = getmovingobjectPosition_forBlock(worldObj,startpos, targetpos, false, true, false);
		if(movingobjectposition!=null) {
			return false;
		}
		return !((this.isInWater() || p_70685_1_.isInWater()) && getDistanceSqToEntity(p_70685_1_) > 256);
	}
	/**
	 * returns a (normalized) vector of where this entity is looking
	 */
//	public Vec3 getLookVec()
//	{
//		return this.getLook(1.0F);
//	}
	
	public boolean interactFirst(EntityPlayer p_70085_1_) {
		if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity != p_70085_1_)) {
			if(!p_70085_1_.isSneaking()&&!p_70085_1_.isRiding()){
				pickupEntity(p_70085_1_,0);
			}
			return true;
		} else {
			return false;
		}
	}
	public boolean pickupEntity(Entity p_70085_1_, int StartSeachSeatNum){
		return this.getBaseLogic().pickupEntity(p_70085_1_,StartSeachSeatNum);
	}

//	public Vec3 getLook(float p_70676_1_)
//	{
//		float f1;
//		float f2;
//		float f3;
//		float f4;
//
//		if (p_70676_1_ == 1.0F)
//		{
//			f1 = MathHelper.cos(-this.rotationYawHead * 0.017453292F - (float)Math.PI);
//			f2 = MathHelper.sin(-this.rotationYawHead * 0.017453292F - (float)Math.PI);
//			f3 = -MathHelper.cos(-this.rotationPitch * 0.017453292F);
//			f4 = MathHelper.sin(-this.rotationPitch * 0.017453292F);
//			return Vec3.createVectorHelper((double)(f2 * f3), (double)f4, (double)(f1 * f3));
//		}
//		else
//		{
//			f1 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * p_70676_1_;
//			f2 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * p_70676_1_;
//			f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
//			f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
//			float f5 = -MathHelper.cos(-f1 * 0.017453292F);
//			float f6 = MathHelper.sin(-f1 * 0.017453292F);
//			return Vec3.createVectorHelper((double)(f4 * f5), (double)f6, (double)(f3 * f5));
//		}
//	}
//	public void moveFlying(float p_70060_1_, float p_70060_2_, float p_70060_3_)
//	{
//		float f3 = p_70060_1_ * p_70060_1_ + p_70060_2_ * p_70060_2_;
//
//		if (f3 >= 1.0E-4F)
//		{
//			f3 = MathHelper.sqrt_float(f3);
//
//			if (f3 < 1.0F)
//			{
//				f3 = 1.0F;
//			}
//			f3 = p_70060_3_ / f3;
//			f3 = abs(f3);
//			p_70060_1_ *= f3;
//			p_70060_2_ *= f3;
//			float f4 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
//			float f5 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
//			this.motionX += (double)(-p_70060_2_ * f4 + p_70060_1_ * f5);
//			this.motionZ += (double)( p_70060_2_ * f5 + p_70060_1_ * f4);
//		}
//	}
	
	
//	public void onLivingUpdate()
//	{
//		this.updateArmSwingProgress();
//		float f = this.getBrightness(1.0F);
//
//		if (f > 0.5F)
//		{
//			this.entityAge += 2;
//		}
//
//		super.onLivingUpdate();
//	}
	
	public void readEntityFromNBT(NBTTagCompound p_70037_1_)
	{
		typename = p_70037_1_.getString("typename");
		canUseByMob = p_70037_1_.getBoolean("canUseByMob");
		despawn = p_70037_1_.getBoolean("despawn");
		init_2(typename);
		this.setSize(3f, 3f);
//		super.readEntityFromNBT(p_70037_1_);
		baseLogic.readFromTag(p_70037_1_);
	}
	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound p_70014_1_)
	{
//		super.writeEntityToNBT(p_70014_1_);
		p_70014_1_.setString("typename",typename);
		p_70014_1_.setBoolean("canUseByMob",canUseByMob);
		p_70014_1_.setBoolean("despawn",despawn);
		baseLogic.saveToTag(p_70014_1_);
	}
	public boolean writeMountToNBT(NBTTagCompound p_98035_1_)
	{
		return false;
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer){
		ByteBufUtils.writeUTF8String(buffer,typename);
	}
	
	@Override
	public void readSpawnData(ByteBuf additionalData){
		typename = ByteBufUtils.readUTF8String(additionalData);
		
		System.out.println("" + typename);
		init_2(typename);
	}
	public boolean shouldDismountInWater(Entity entity){
		return false;
	}
	@Override
	public boolean is_this_entity_friend(Entity entity){
		return false;
	}

	public boolean standalone(){
		return false;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double p_70112_1_)
	{
		return true;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_)
	{
		return true;
	}
	
//	@Override
//	protected String getLivingSound()
//	{
//		return null;
//	}
	protected String getHurtSound()
	{
		return null;
	}
	
	@Override
	public void yourSoundIsremain() {
		getBaseLogic().needStartSound = false;
	}
	
	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound()
	{
		return null;
	}
	
	protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
	{
//		this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
	}
	
	public void setPosition(double x, double y, double z)
	{
		super.setPosition(x,y,z);
		if(getBaseLogic() != null)getBaseLogic().setPosition(x,y,z);
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
			this.playSound("handmadeguns.handmadeguns.fireee", 1.20F, 0.8F);
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

	public void updateRiderPosition()
	{
	}
	@Override
	public TurretObj[] getmainTurrets() {
		return null;
	}
	
	@Override
	public TurretObj[] getsubTurrets() {
		return null;
	}
	
	@Override
	public TurretObj[] getTurrets() {
		return getBaseLogic().turrets;
	}
	
	@Override
	public void applyEntityCollision(Entity p_70108_1_)
	{
		getBaseLogic().applyEntityCollision(p_70108_1_);
	}
	
//	@Override
//	public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_)
//	{
//		getBaseLogic().moveEntityWithHeading(p_70612_1_,p_70612_2_);
//	}
	
	@Override
	public boolean handleWaterMovement(){
		return false;
	}
	
	@Override
	public void moveEntity(double x, double y, double z){
		getBaseLogic().moveEntity(x,y,z);
	}
	
	@Override
	public void updateFallState_public(double stepHeight, boolean onground){
		this.updateFallState(stepHeight,onground);
	}
	
	@Override
	public void func_145775_I_public() {
		this.func_145775_I();
	}
	
	@Override
	public boolean getinWater() {
		return inWater;
	}
	
	@Override
	public void setinWater(boolean value) {
		inWater = value;
	}

	protected void collideWithNearbyEntities(){
		getBaseLogic().collideWithNearbyEntities();
	}
	@Override
	public void public_collideWithEntity(Entity entity) {
		entity.applyEntityCollision(this);
	}
	
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 0 || pass == 1;
	}
	public boolean canBeCollidedWith()
	{
		return true;
	}
	public boolean checkObstacle()
	{
		return this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
	}
}
