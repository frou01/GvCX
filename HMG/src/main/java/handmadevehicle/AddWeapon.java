package handmadevehicle;

import handmadevehicle.entity.parts.turrets.FireRist;
import handmadevehicle.entity.prefab.Prefab_Vehicle_Base;
import handmadevehicle.entity.prefab.Prefab_Turret;

import javax.vecmath.Vector3d;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static handmadeguns.HMGGunMaker.readFireInfo;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Float.parseFloat;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class AddWeapon {
	public static final HashMap<String,Prefab_Turret> prefab_turretHashMap = new HashMap<>();
	public static void load( boolean isClient, File file){
		try {
			//File file = new File(configfile,"hmg_handmadeguns.txt");
			if (checkBeforeReadfile(file))
			{
				
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"Shift-JIS"));
				String str;
				Prefab_Turret prefab_turret = new Prefab_Turret();
				String name = null;
				while((str = br.readLine()) != null) {
					
					String[] type = str.split(",");
					//todo 砲塔設定を読み込み保存しておく。車両追加側ではこのオブジェクトから持ってくる。
					switch (type[0]){
						case "WeaponName":
							name = type[1];
							if(prefab_turretHashMap.containsKey(name))prefab_turret = prefab_turretHashMap.get(name);
							break;
						case "turretYawCenterpos":
							prefab_turret.turretYawCenterpos = new Vector3d(parseDouble(type[1]),parseDouble(type[2]),parseDouble(type[3]));
							break;
						case "turretPitchCenterpos":
							prefab_turret.turretPitchCenterpos = new Vector3d(parseDouble(type[1]),parseDouble(type[2]),parseDouble(type[3]));
							break;
						case "cannonPos":
							prefab_turret.cannonPos = new Vector3d(parseDouble(type[1]),parseDouble(type[2]),parseDouble(type[3]));
							break;
						case "multicannonPos":
							prefab_turret.multicannonPos = new Vector3d[(type.length-1)/3];
							for(int id = 0;id < prefab_turret.multicannonPos.length; id++){
								prefab_turret.multicannonPos[id] = new Vector3d(parseDouble(type[id * 3 + 1]),parseDouble(type[id * 3 + 2]),parseDouble(type[id * 3 + 3]));
							}
							break;
						case "linked_MotherTrigger":
							prefab_turret.linked_MotherTrigger = parseBoolean(type[1]);
							break;
						case "fireAll_child":
							prefab_turret.fireAll_child = parseBoolean(type[1]);
							break;
						case "fireAll_cannon":
							prefab_turret.fireAll_cannon = parseBoolean(type[1]);
							break;
						case "syncTurretAngle":
							prefab_turret.syncTurretAngle = parseBoolean(type[1]);
							break;
						case "seekerSize":
							prefab_turret.seekerSize = parseDouble(type[1]);
							break;
						case "elevationType":
							prefab_turret.elevationType = parseInt(type[1]);
							break;
						case "turretanglelimtYawMax":
							prefab_turret.turretanglelimtYawMax = parseFloat(type[1]);
							break;
						case "turretanglelimtYawmin":
							prefab_turret.turretanglelimtYawmin = parseFloat(type[1]);
							break;
						case "turretanglelimtPitchMax":
							prefab_turret.turretanglelimtPitchMax = parseFloat(type[1]);
							break;
						case "turretanglelimtPitchmin":
							prefab_turret.turretanglelimtPitchmin = parseFloat(type[1]);
							break;
						case "turretspeedY":
							prefab_turret.turretspeedY = parseDouble(type[1]);
							break;
						case "turretspeedP":
							prefab_turret.turretspeedP = parseDouble(type[1]);
							break;
						case "traverseSound":
							prefab_turret.traverseSound = type[1];
							break;
						case "traversesoundLV":
							prefab_turret.traversesoundLV = parseFloat(type[1]);
							break;
						case "traversesoundPitch":
							prefab_turret.traversesoundPitch = parseFloat(type[1]);
							break;
						case "hasReflexSight":
							prefab_turret.hasReflexSight = parseBoolean(type[1]);
							break;
						case "fireRists_First":
							prefab_turret.fireRists = new FireRist[parseInt(type[1])];
							break;
						case "fireRists":
							prefab_turret.fireRists[parseInt(type[1])] = new FireRist(parseFloat(type[2]),parseFloat(type[3]),parseFloat(type[4]),parseFloat(type[5]));
							break;
						case "userOnBarrell":
							prefab_turret.userOnBarrell = parseBoolean(type[1]);
							break;
						case "flashoffset":
							prefab_turret.flashoffset = parseDouble(type[1]);
							break;
						case "End":
							prefab_turretHashMap.put(name,prefab_turret);
							break;
					}
					readFireInfo(prefab_turret.gunInfo,type);
					
				}
				br.close();  // ファイルを閉じる
			}
			else
			{
			
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static boolean checkBeforeReadfile(File file){
		if (file.exists()){
			if (file.isFile() && file.canRead()){
				return true;
			}
		}
		
		return false;
	}
}
