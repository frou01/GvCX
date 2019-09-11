package hmgww2.entity.planes;


import handmadevehicle.entity.parts.SeatInfo;
import handmadevehicle.entity.parts.turrets.TurretObj;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.vecmath.Vector3d;

public class EntityJPN_Fighter extends EntityJPN_FighterBase
{
	public EntityJPN_Fighter(World par1World)
	{
		super(par1World);
		this.setSize(5f, 3f);
		armor = 4;
		ignoreFrustumCheck = true;
		baseLogic.prefab_vehicle.throttle_Max = 3.0f;
		baseLogic.prefab_vehicle.rollspeed = 0.75f;
		baseLogic.prefab_vehicle.pitchspeed = 0.5f;
		baseLogic.prefab_vehicle.yawspeed = 0.2f;
		baseLogic.prefab_vehicle.dragfactor *= 0.99;
		baseLogic.prefab_vehicle.maxDive = 60;
		baseLogic.prefab_vehicle.startDive = 40;
		baseLogic.prefab_vehicle.maxClimb = -20;
		baseLogic.prefab_vehicle.maxbank = 60;
		baseLogic.prefab_vehicle.soundname = "hmgww2:hmgww2.sound_pera";
		
		baseLogic.prefab_vehicle.camerapos = new double[]{0,2.179,-0.6397};
		baseLogic.prefab_vehicle.rotcenter = new double[]{0,1.1,0};
		baseLogic.riddenByEntities = new Entity[1];
		baseLogic.seatInfos = new SeatInfo[1];
		baseLogic.seatInfos[0] = new SeatInfo();
		baseLogic.seatInfos_zoom[0] = new SeatInfo();
		baseLogic.seatInfos[0].pos = baseLogic.prefab_vehicle.camerapos;
		baseLogic.seatInfos_zoom[0].pos = baseLogic.prefab_vehicle.camerapos;
		
		baseLogic.onground_pitch = -11.53f;
		baseLogic.prefab_vehicle.useMain_withSub = true;
		{
			TurretObj cannon3 = new TurretObj(worldObj);
			{
				cannon3.motherRotCenter = new Vector3d(baseLogic.prefab_vehicle.rotcenter);
				cannon3.cannonPos = new Vector3d(0.4,1.44,-2);
				cannon3.turretspeedY = 5;
				cannon3.turretspeedP = 8;
				cannon3.turretanglelimtPitchMax = 5;
				cannon3.turretanglelimtPitchmin = -80;
				cannon3.traverseSound = null;
				cannon3.currentEntity = this;
				cannon3.powor = 8;
				cannon3.cycle_setting = 1;
				cannon3.firesound = "handmadeguns:handmadeguns.fire";
				cannon3.spread = 1;
				cannon3.speed = 9;
				cannon3.magazineMax = 100;
				cannon3.magazinerem = 100;
				cannon3.reloadTimer = 70;
				cannon3.canex = false;
				cannon3.guntype = 0;
			}
			{
				TurretObj cannon4 = new TurretObj(worldObj);
				cannon4.motherRotCenter = new Vector3d(baseLogic.prefab_vehicle.rotcenter);
				cannon4.cannonPos = new Vector3d(-0.4,1.44,-2);
				cannon4.turretspeedY = 5;
				cannon4.turretspeedP = 8;
				cannon4.turretanglelimtPitchMax = 5;
				cannon4.turretanglelimtPitchmin = -80;
				cannon4.traverseSound = null;
				cannon4.currentEntity = this;
				cannon4.powor = 8;
				cannon4.cycle_setting = 1;
				cannon4.firesound = "handmadeguns:handmadeguns.fire";
				cannon4.spread = 1;
				cannon3.speed = 9;
				cannon4.magazineMax = 100;
				cannon4.magazinerem = 100;
				cannon4.reloadTimer = 70;
				cannon4.canex = false;
				cannon4.guntype = 0;
				cannon3.addchild_triggerLinked(cannon4);
			}
			baseLogic.mainTurret = cannon3;
		}
		{
			TurretObj cannon1 = new TurretObj(worldObj);
			{
				cannon1.motherRotCenter = new Vector3d(baseLogic.prefab_vehicle.rotcenter);
				cannon1.cannonPos = new Vector3d(-1.45, 0.72, -0.65);
				cannon1.flashoffset = 0;
				cannon1.turretspeedY = 5;
				cannon1.turretspeedP = 8;
				cannon1.turretanglelimtPitchMax = 5;
				cannon1.turretanglelimtPitchmin = -80;
				cannon1.traverseSound = null;
				cannon1.currentEntity = this;
				cannon1.powor = 15;
				cannon1.ex = 0.1f;
				cannon1.cycle_setting = 3;
				cannon1.firesound = "handmadeguns:handmadeguns.20mmfire";
				cannon1.magazineMax = 50;
				cannon1.magazinerem = 50;
				cannon1.spread = 3;
				cannon1.speed = 7;
				cannon1.canex = false;
				cannon1.guntype = 2;
			}
			
			{
				TurretObj cannon2 = new TurretObj(worldObj);
				cannon2.motherRotCenter = new Vector3d(baseLogic.prefab_vehicle.rotcenter);
				cannon2.cannonPos = new Vector3d(1.45, 0.72, -0.65);
				cannon2.flashoffset = 0;
				cannon2.turretspeedY = 5;
				cannon2.turretspeedP = 8;
				cannon2.turretanglelimtPitchMax = 5;
				cannon2.turretanglelimtPitchmin = -80;
				cannon2.traverseSound = null;
				cannon2.currentEntity = this;
				cannon2.powor = 15;
				cannon2.ex = 0.1F;
				cannon2.cycle_setting = 3;
				cannon2.firesound = "handmadeguns:handmadeguns.20mmfire";
				cannon2.magazineMax = 50;
				cannon2.magazinerem = 50;
				cannon2.spread = 3;
				cannon1.speed = 7;
				cannon1.canex = false;
				cannon2.guntype = 2;
				cannon1.addchild_triggerLinked(cannon2);
			}
			baseLogic.subTurret = cannon1;
		}
	}
	protected void applyEntityAttributes()
	{
		maxHealth = 70;
		super.applyEntityAttributes();
	}
}
