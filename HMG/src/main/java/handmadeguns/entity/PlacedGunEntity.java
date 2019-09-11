package handmadeguns.entity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import handmadeguns.HMGPacketHandler;
import handmadeguns.items.guns.HMGItem_Unified_Guns;
import handmadeguns.network.PacketPlacedGunShot;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import static handmadeguns.HandmadeGunsCore.HMG_proxy;
import static java.lang.Math.abs;
import static java.lang.StrictMath.max;
import static java.lang.StrictMath.toRadians;
import static net.minecraft.util.MathHelper.wrapAngleTo180_float;

public class PlacedGunEntity extends Entity implements IEntityAdditionalSpawnData {
    public ItemStack gunStack;
    public boolean issummonbyMob;
    public HMGItem_Unified_Guns gunItem;
    public boolean firing;
    public boolean torideclick;
    public float prevrotationYawGun;
    public float rotationYawGun;
    public float baserotationYaw;
    public float gunyoffset;

    public double prevRiddenByEntityPosX;
    public double prevRiddenByEntityPosY;
    public double prevRiddenByEntityPosZ;
    public float hitpoint;
    public float maxhitpoint = -1;
    
    int triggerFreeze = 10;

    public Entity preRiddenByEntity;
    public PlacedGunEntity(World p_i1582_1_) {
        super(p_i1582_1_);
        ignoreFrustumCheck = true;
    }
    public PlacedGunEntity(World p_i1582_1_,ItemStack stack) {
        this(p_i1582_1_);
        gunStack = stack;
        renderDistanceWeight = 4096;
        ignoreFrustumCheck = true;
        if(gunStack != null) {
            gunItem = (HMGItem_Unified_Guns) gunStack.getItem();
            hitpoint = gunItem.gunInfo.turretMaxHP;
            maxhitpoint = gunItem.gunInfo.turretMaxHP;
            setSize(gunItem.gunInfo.turreboxW, gunItem.gunInfo.turreboxH);
        }else {
            setSize(1,1);
        }
    }
    public void resize(){
        setSize(gunItem.gunInfo.turreboxW, gunItem.gunInfo.turreboxH);
    }
    @Override
    public void updateRiderPosition() {
        if(gunStack != null && gunItem != null) {
            Vec3 seatVec = seatVec();
            if (worldObj.isRemote) {
                if (riddenByEntity == FMLClientHandler.instance().getClientPlayerEntity()) {
                    this.riddenByEntity.setPosition(
                            this.posX + seatVec.xCoord,
                            this.posY + this.gunyoffset + seatVec.yCoord,
                            this.posZ + seatVec.zCoord);
                    prevRiddenByEntityPosX = riddenByEntity.posX;
                    prevRiddenByEntityPosY = riddenByEntity.posY;
                    prevRiddenByEntityPosZ = riddenByEntity.posZ;
                }else if(!(riddenByEntity instanceof EntityPlayer)){
                    this.riddenByEntity.setPosition(
                            this.posX + seatVec.xCoord,
                            this.posY + this.gunyoffset + seatVec.yCoord - riddenByEntity.getEyeHeight(),
                            this.posZ + seatVec.zCoord);
                    prevRiddenByEntityPosX = riddenByEntity.posX;
                    prevRiddenByEntityPosY = riddenByEntity.posY;
                    prevRiddenByEntityPosZ = riddenByEntity.posZ;
                }
            }else {
                this.riddenByEntity.setPosition(
                        this.posX + seatVec.xCoord,
                        this.posY + this.gunyoffset + seatVec.yCoord - riddenByEntity.getEyeHeight(),
                        this.posZ + seatVec.zCoord);
                prevRiddenByEntityPosX = riddenByEntity.posX;
                prevRiddenByEntityPosY = riddenByEntity.posY;
                prevRiddenByEntityPosZ = riddenByEntity.posZ;
            }
            /*if(ridingEntity == null) {
                if (worldObj.isRemote) {
                    if (riddenByEntity == FMLClientHandler.instance().getClientPlayerEntity()) {
                        {
                            this.riddenByEntity.moveEntity(
                                    this.posX + seatVec.xCoord - riddenByEntity.posX,
                                    this.posY + this.gunyoffset + seatVec.yCoord - riddenByEntity.posY,
                                    this.posZ + seatVec.zCoord - riddenByEntity.posZ);
                        }
                        prevRiddenByEntityPosX = riddenByEntity.posX;
                        prevRiddenByEntityPosY = riddenByEntity.posY;
                        prevRiddenByEntityPosZ = riddenByEntity.posZ;
                    }else if(!(riddenByEntity instanceof EntityPlayer)){
                        this.riddenByEntity.setPosition(
                                this.posX + seatVec.xCoord,
                                this.posY + this.gunyoffset + seatVec.yCoord,
                                this.posZ + seatVec.zCoord);
                        prevRiddenByEntityPosX = riddenByEntity.posX;
                        prevRiddenByEntityPosY = riddenByEntity.posY;
                        prevRiddenByEntityPosZ = riddenByEntity.posZ;
                    }
                }else if(!(riddenByEntity instanceof EntityPlayer)){
                    this.riddenByEntity.setPosition(
                            this.posX + seatVec.xCoord,
                            this.posY + this.gunyoffset + seatVec.yCoord,
                            this.posZ + seatVec.zCoord);
                    prevRiddenByEntityPosX = riddenByEntity.posX;
                    prevRiddenByEntityPosY = riddenByEntity.posY;
                    prevRiddenByEntityPosZ = riddenByEntity.posZ;
                }
            }else {
                if (worldObj.isRemote && riddenByEntity == FMLClientHandler.instance().getClientPlayerEntity()) {
                } else {
                    this.riddenByEntity.setPosition(
                            this.posX + seatVec.xCoord,
                            this.posY + this.gunyoffset + seatVec.yCoord - riddenByEntity.getEyeHeight(),
                            this.posZ + seatVec.zCoord);
                    prevRiddenByEntityPosX = riddenByEntity.posX;
                    prevRiddenByEntityPosY = riddenByEntity.posY;
                    prevRiddenByEntityPosZ = riddenByEntity.posZ;
                }
            }*/
        }
    }
    public void onUpdate(){
        prevrotationYawGun = rotationYawGun;

        if(!worldObj.isRemote){
            if(gunStack == null)setDead();
            if(gunStack != null && gunStack.getItem() instanceof HMGItem_Unified_Guns)
                gunItem = (HMGItem_Unified_Guns) gunStack.getItem();
            else gunItem = null;
        }else {
        }

        if(riddenByEntity == null)triggerFreeze = 10;
        else triggerFreeze--;
        if(triggerFreeze>0)firing = false;
        if(gunItem != null){
            this.gunyoffset = gunItem.gunInfo.yoffset;
            maxhitpoint = gunItem.gunInfo.turretMaxHP;
        }
        rotationYawGun = wrapAngleTo180_float(rotationYawGun);
        rotationYaw = wrapAngleTo180_float(rotationYaw);
        if(riddenByEntity != null){
            if(gunItem != null && gunItem.gunInfo.restrictTurretMoveSpeed){
                float angularDif = wrapAngleTo180_float(this.rotationYawGun - riddenByEntity.getRotationYawHead());
                if (angularDif <-gunItem.gunInfo.turretMoveSpeedY) {
                    this.rotationYawGun += gunItem.gunInfo.turretMoveSpeedY;
                } else if (angularDif > gunItem.gunInfo.turretMoveSpeedY){
                    this.rotationYawGun -= gunItem.gunInfo.turretMoveSpeedY;
                }else{
                    this.rotationYawGun = riddenByEntity.getRotationYawHead();
                }
                this.rotationPitch = abs(riddenByEntity.rotationPitch - this.rotationPitch) < gunItem.gunInfo.turretMoveSpeedP ? riddenByEntity.rotationPitch : this.rotationPitch + gunItem.gunInfo.turretMoveSpeedP * ((riddenByEntity.rotationPitch - this.rotationPitch) < 0? -1 : 1);
            }else {
                this.rotationYawGun = riddenByEntity.getRotationYawHead();
                this.rotationPitch = riddenByEntity.rotationPitch;
            }
            if(!worldObj.isRemote && firing && gunStack != null && gunStack.hasTagCompound()){
                gunStack.getTagCompound().setBoolean("IsTriggered",true);
                gunStack.getTagCompound().setBoolean("set_up", true);
                gunStack.getTagCompound().setInteger("set_up_cnt", 10);
                gunStack.getTagCompound().setBoolean("HMGfixed", true);
            }
        }else {
            firing = false;
        }

        if(gunItem != null && gunItem.gunInfo.restrictTurretAngle) {
            float yawamount = wrapAngleTo180_float(this.rotationYawGun - this.rotationYaw);
            if (yawamount > gunItem.gunInfo.turretanglelimtMxY){
                this.rotationYawGun = this.rotationYaw + gunItem.gunInfo.turretanglelimtMxY;
            }else if (yawamount < gunItem.gunInfo.turretanglelimtmnY){
                this.rotationYawGun = this.rotationYaw + gunItem.gunInfo.turretanglelimtmnY;
            }

            if (this.rotationPitch > gunItem.gunInfo.turretanglelimtMxP){
                this.rotationPitch = gunItem.gunInfo.turretanglelimtMxP;
            }else if (this.rotationPitch < gunItem.gunInfo.turretanglelimtmnP){
                this.rotationPitch = gunItem.gunInfo.turretanglelimtmnP;
            }
        }

        baserotationYaw = rotationYaw;
        float backpitch = rotationPitch;
        rotationYaw = rotationYawGun;
        if(riddenByEntity instanceof IMGGunner){
            ((IMGGunner) riddenByEntity).extraprocessInMGFire();
        }

        if(!worldObj.isRemote){
            if(gunItem != null)gunItem.onUpdate(gunStack,worldObj,this,0,true);
            dataWatcher.updateObject(3,gunStack);
        }else {
            if(riddenByEntity == FMLClientHandler.instance().getClientPlayerEntity()){
                firing = false;
                if (HMG_proxy.rightclick()) {
                    if(!torideclick) firing = true;
                }else {
                    torideclick = false;
                }
                HMGPacketHandler.INSTANCE.sendToServer(new PacketPlacedGunShot(this.getEntityId(),firing));
                firing = false;
            }else {
                firing = false;
            }
            if(gunStack != null)gunStack = gunStack.copy();
            if(gunItem != null){
                gunItem.onUpdate(gunStack,worldObj,this,0,true);
            }
            
            
            gunStack = dataWatcher.getWatchableObjectItemStack(3);
            if(gunStack != null && gunStack.getItem() instanceof HMGItem_Unified_Guns)
                gunItem = (HMGItem_Unified_Guns) gunStack.getItem();
            else gunItem = null;
        }
        rotationYaw = baserotationYaw;
        rotationPitch = backpitch;
        motionY -=0.049;
        motionX *= 0.7;
        motionY *= 0.96;
        motionZ *= 0.7;

        if(issummonbyMob && ((ridingEntity == null && riddenByEntity == null) || (ridingEntity == null && riddenByEntity != null && riddenByEntity.isDead))){
            if(!worldObj.isRemote)if(gunStack != null){
                EntityItem entityItem = new EntityItem(worldObj,this.posX,this.posY,this.posZ,gunStack);
                worldObj.spawnEntityInWorld(entityItem);
            }
            this.setDead();
        }
        if(preRiddenByEntity != null && riddenByEntity == null){
            preRiddenByEntity.setPosition(prevRiddenByEntityPosX,prevRiddenByEntityPosY,prevRiddenByEntityPosZ);
        }

        preRiddenByEntity = riddenByEntity;

        while (this.rotationYawGun - this.prevrotationYawGun < -180.0F)
        {
            this.prevrotationYawGun -= 360.0F;
        }

        while (this.rotationYawGun - this.prevrotationYawGun >= 180.0F)
        {
            this.prevrotationYawGun += 360.0F;
        }
        if(!worldObj.isRemote)moveEntity(motionX,motionY,motionZ);
        setPosition(posX,posY,posZ);
        super.onUpdate();
    }

    public boolean attackEntityFrom(DamageSource source, float par2)
    {
        if(!worldObj.isRemote) {
            if (maxhitpoint > 0) {
                hitpoint -= par2;
                if (hitpoint < 0) {
                    EntityItem entityItem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, gunStack);
                    worldObj.spawnEntityInWorld(entityItem);
                    this.setDead();
                    return true;
                }
            }
            if (gunStack != null && (source.getDamageType().equals("player"))) {
                EntityItem entityItem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, gunStack);
                worldObj.spawnEntityInWorld(entityItem);
                this.setDead();
                return true;
            }
            if(riddenByEntity != null)riddenByEntity.mountEntity(null);
        }
        return false;
    }
    public float getEyeHeight()
    {
        return gunyoffset;
    }
    public boolean shouldRiderSit(){
        return false;
    }
    public boolean canDespawn(){
        return false;
    }
    public double getMountedYOffset() {
        return 0.0D;
    }
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObjectByDataType(3,5);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        issummonbyMob = p_70037_1_.getBoolean("issummonbyMob");
        rotationYawGun = p_70037_1_.getFloat("rotationYawGun");
        rotationYaw = p_70037_1_.getFloat("rotationYaw");
        hitpoint = p_70037_1_.getFloat("hitpoint");
        NBTBase nbttagcompound = p_70037_1_.getTag("GunStack");
        if(nbttagcompound instanceof NBTTagCompound){
            gunStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) nbttagcompound);
            if(gunStack != null && gunStack.getItem() instanceof HMGItem_Unified_Guns){
                gunItem = (HMGItem_Unified_Guns) gunStack.getItem();
                setSize(gunItem.gunInfo.turreboxW, gunItem.gunInfo.turreboxH);
            }
        }
    }
    public void applyEntityCollision(Entity p_70108_1_)
    {
    }
    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        gunStack.writeToNBT(nbttagcompound);
        p_70014_1_.setTag("GunStack" , nbttagcompound);
        p_70014_1_.setBoolean("issummonbyMob",issummonbyMob);
        p_70014_1_.setFloat("rotationYawGun",rotationYawGun);
        p_70014_1_.setFloat("rotationYaw",rotationYaw);
        p_70014_1_.setFloat("hitpoint",hitpoint);
    }

    public boolean interactFirst(EntityPlayer p_70085_1_) {
        if (riddenByEntity == null && !worldObj.isRemote) {
            p_70085_1_.mountEntity(this);
            prevRiddenByEntityPosX = riddenByEntity.posX;
            prevRiddenByEntityPosY = riddenByEntity.posY;
            prevRiddenByEntityPosZ = riddenByEntity.posZ;
            torideclick = true;
        }
        return true;
    }
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_)
    {
        return true;
    }

    public Vec3 getLook(float p_70676_1_)
    {
        float f1;
        float f2;
        float f3;
        float f4;

        if (p_70676_1_ == 1.0F)
        {
            f1 = MathHelper.cos(-this.rotationYawGun * 0.017453292F - (float)Math.PI);
            f2 = MathHelper.sin(-this.rotationYawGun * 0.017453292F - (float)Math.PI);
            f3 = -MathHelper.cos(-this.rotationPitch * 0.017453292F);
            f4 = MathHelper.sin(-this.rotationPitch * 0.017453292F);
            return Vec3.createVectorHelper((double)(f2 * f3), (double)f4, (double)(f1 * f3));
        }
        else
        {
            f1 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * p_70676_1_;
            f2 = this.prevrotationYawGun + (this.rotationYawGun - this.prevrotationYawGun) * p_70676_1_;
            f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
            f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            return Vec3.createVectorHelper((double)(f4 * f5), (double)f6, (double)(f3 * f5));
        }
    }

    public Vec3 seatVec(){
        double[] sightingpos = gunItem.getSeatpos(gunStack);
        Vec3 vec = Vec3.createVectorHelper(sightingpos[0],sightingpos[1],sightingpos[2]);
        if(gunItem.gunInfo.userOnBarrel) {
            vec = vec.addVector(-gunItem.gunInfo.posGetter.turretRotationPitchPoint[0], -gunItem.gunInfo.posGetter.turretRotationPitchPoint[1], -gunItem.gunInfo.posGetter.turretRotationPitchPoint[2]);
            vec.rotateAroundX(-(float) toRadians(rotationPitch));
            vec = vec.addVector(gunItem.gunInfo.posGetter.turretRotationPitchPoint[0], gunItem.gunInfo.posGetter.turretRotationPitchPoint[1], gunItem.gunInfo.posGetter.turretRotationPitchPoint[2]);
        }
        vec = vec.addVector( - gunItem.gunInfo.posGetter.turretRotationYawPoint[0], - gunItem.gunInfo.posGetter.turretRotationYawPoint[1], - gunItem.gunInfo.posGetter.turretRotationYawPoint[2]);
        vec.rotateAroundY(-(float) toRadians(rotationYawGun - rotationYaw));
        vec = vec.addVector(   gunItem.gunInfo.posGetter.turretRotationYawPoint[0],   gunItem.gunInfo.posGetter.turretRotationYawPoint[1],   gunItem.gunInfo.posGetter.turretRotationYawPoint[2]);
        vec.rotateAroundY(-(float) toRadians(rotationYaw));
        return vec;
    }
    public float getRotationYawHead()
    {
        return rotationYawGun;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(rotationYawGun);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        rotationYawGun = additionalData.readFloat();
    }
}
