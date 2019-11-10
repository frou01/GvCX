package hmggvcmob.ai;

import handmadevehicle.SlowPathFinder.ModifiedPathNavigater;
import handmadevehicle.SlowPathFinder.PathPoint_slow;
import handmadevehicle.SlowPathFinder.WorldForPathfind;
import handmadevehicle.entity.EntityDummy_rider;
import handmadevehicle.entity.EntityVehicle;
import handmadevehicle.entity.parts.logics.BaseLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import static handmadevehicle.Utils.*;

public class AIDriveTank extends EntityAIBase {
	public EntityVehicle vehicle;
	public boolean Limited_turning_turret = false;//車両設定と同期させる　どうやって検出しよう。手動でいいかな
	public boolean combatSituation = false;
	public EntityLiving driverBody;

	public WorldForPathfind worldForPathfind;


	public AIDriveTank(EntityLiving driverBody,EntityVehicle entityVehicle,WorldForPathfind worldForPathfind){
		this.driverBody = driverBody;
		this.vehicle = entityVehicle;
		this.worldForPathfind = worldForPathfind;
	}

	@Override
	public boolean shouldExecute() {
		if(driverBody.ridingEntity instanceof EntityDummy_rider){
			vehicle = (EntityVehicle) ((EntityDummy_rider) driverBody.ridingEntity).linkedBaseLogic.mc_Entity;
		}
		if(vehicle != null && !vehicle.isDead && vehicle.getBaseLogic().isRidingEntity(driverBody)){
			return true;
		}else {
			vehicle = null;
		}
		return false;
	}
	
	
	@Override
	public void resetTask() {
		if(vehicle != null){
			vehicle.getNavigator().clearPathEntity();
			BaseLogic baseLogic = vehicle.getBaseLogic();
			baseLogic.setControl_throttle_down(false);
			baseLogic.setControl_throttle_up(false);
			baseLogic.setControl_yaw_Right(false);
			baseLogic.setControl_yaw_Left(false);
			baseLogic.setControl_Space(false);
			baseLogic.setControl_brake(false);
		}
		combatSituation = false;
		super.resetTask();
	}
	@Override
	public void updateTask() {

		//todo こっちで戦車を動かす。
		//射撃とかはアタックで
		//パスを設定するときは必ず車両側に
		boolean syncedToDriverPath = false;
		if(!combatSituation && vehicle.getNavigator().getPath() == null && driverBody.getNavigator().getPath() != null){
			vehicle.getNavigator().setPath(worldForPathfind.getEntityPathToXYZ(vehicle,
					MathHelper.floor_double(driverBody.getNavigator().getPath().getFinalPathPoint().xCoord),
					MathHelper.floor_double(driverBody.getNavigator().getPath().getFinalPathPoint().yCoord),
					MathHelper.floor_double(driverBody.getNavigator().getPath().getFinalPathPoint().zCoord),
					60, false, false, false, false),
					driverBody.getNavigator() instanceof ModifiedPathNavigater?((ModifiedPathNavigater) driverBody.getNavigator()).getSpeed():1);
			syncedToDriverPath = true;
		}
		if(vehicle.getBaseLogic().ispilot(driverBody) && !driverBody.worldObj.isRemote) {
			BaseLogic baseLogic = vehicle.getBaseLogic();
			double[] nextPos = nextPosition();
			baseLogic.setControl_throttle_down(false);
			baseLogic.setControl_throttle_up(false);
			baseLogic.setControl_yaw_Right(false);
			baseLogic.setControl_yaw_Left(false);
			baseLogic.setControl_Space(false);
			baseLogic.setControl_brake(false);
			if (nextPos != null) {
				Vector3d vecToNextPos = new Vector3d(nextPos);
//				System.out.println("vecToNextPos global	" + vecToNextPos);
				vecToNextPos.sub(new Vector3d(vehicle.posX, vehicle.posY, vehicle.posZ));//相対位置
				vecToNextPos.scale(vehicle.getNavigator().getSpeed());
				double dist = vecToNextPos.lengthSquared();

				double[] lastPos = lastPosition();
				if(lastPos != null){
					Vector3d vecToLastPos = new Vector3d(lastPos);
					vecToLastPos.sub(new Vector3d(vehicle.posX, vehicle.posY, vehicle.posZ));//相対位置
					dist = vecToLastPos.lengthSquared();
				}

				getVector_local_inRotatedObj(vecToNextPos,vecToNextPos,baseLogic.bodyRot);
				vecToNextPos.normalize();
//				System.out.println("vecToNextPos local	" + vecToNextPos);
//				System.out.println("localMotionVec		" + baseLogic.localMotionVec);
				if(baseLogic.info.forced_rudder_effect > 0){
					baseLogic.setControl_throttle_up(true);

					if (vecToNextPos.x < -0.4) {
						baseLogic.setControl_yaw_Right(true);
					} else if (vecToNextPos.x > 0.4) {
						baseLogic.setControl_yaw_Left(true);
					}

					baseLogic.setControl_brake(true);
					//超信地旋回
				}else if(baseLogic.localMotionVec.z < 0){//前進中
					if (vecToNextPos.x < -0.3) {
						baseLogic.setControl_yaw_Right(true);
					} else if (vecToNextPos.x > 0.3) {
						baseLogic.setControl_yaw_Left(true);
					}
				}else {
					if (vecToNextPos.x < -0.3) {
						baseLogic.setControl_yaw_Right(true);
					} else if (vecToNextPos.x > 0.3) {
						baseLogic.setControl_yaw_Left(true);
					}
				}
				if (vecToNextPos.z < 0 || Limited_turning_turret || (combatSituation && vehicle.getNavigator().getSpeed() >0)) {
					boolean flag_onCourse = false;
					if (vecToNextPos.x < -0.3) {
					} else if (vecToNextPos.x > 0.3) {
					}else {
						if (dist > 16) {
							flag_onCourse = true;
						}
					}

					if (flag_onCourse) {
						baseLogic.setControl_throttle_up(true);
					} else {
						if (baseLogic.throttle > baseLogic.prefab_vehicle.throttle_Max / 2) {
							baseLogic.setControl_throttle_down(true);
						}else {
							baseLogic.setControl_throttle_up(true);
						}
					}
				} else {
					boolean flag_onCourse = false;
					if (vecToNextPos.x < -0.3) {
					} else if (vecToNextPos.x > 0.3) {
					}else {
						if (dist > 16) {
							flag_onCourse = true;
						}
					}
					if (flag_onCourse) {
						baseLogic.setControl_throttle_down(true);
					} else {
						if (-baseLogic.throttle < baseLogic.prefab_vehicle.throttle_min / 2) {
							baseLogic.setControl_throttle_up(true);
						}else {
							baseLogic.setControl_throttle_down(true);
						}
					}
//					if (vecToNextPos.x < -1) {
//						baseLogic.setControl_yaw_Right(true);
//						baseLogic.setControl_brake(true);
//					}
//					else if (vecToNextPos.x > 1) {
//						baseLogic.setControl_yaw_Left(true);
//						baseLogic.setControl_brake(true);
//					}
//					else
//						baseLogic.setControl_throttle_down(true);
				}
				combatSituation = false;
			} else {
				baseLogic.setControl_Space(true);
				baseLogic.setControl_brake(true);
			}
		}
		if(syncedToDriverPath){
			vehicle.getNavigator().clearPathEntity();
		}
	}
	public double[] nextPosition(){//次に移動すべき場所を返す
		PathEntity currentPath = vehicle.getNavigator().getPath();
		if(currentPath != null && !currentPath.isFinished()){
			Vec3 nextPos = currentPath.getPosition(vehicle);
			return new double[]{nextPos.xCoord - (double)((int)(vehicle.width + 1.0F)) * 0.5D,nextPos.yCoord,nextPos.zCoord - (double)((int)(vehicle.width + 1.0F)) * 0.5D};
		}
		return null;
	}
	public double[] lastPosition(){
		PathEntity currentPath = vehicle.getNavigator().getPath();
		if(currentPath != null && !currentPath.isFinished()){
			PathPoint nextPos = currentPath.getFinalPathPoint();
			return new double[]{nextPos.xCoord,nextPos.yCoord,nextPos.zCoord};
		}
		return null;
	}
}
