package handmadevehicle;

import cpw.mods.fml.common.registry.GameRegistry;
import handmadeguns.HMGGunMaker;
import handmadeguns.client.render.HMGGunParts;
import handmadevehicle.Items.ItemVehicle;
import handmadevehicle.entity.prefab.*;
import handmadevehicle.render.HMVVehicleParts;

import javax.vecmath.Vector3d;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static handmadevehicle.AddWeapon.prefab_turretHashMap;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Float.parseFloat;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class AddNewVehicle extends HMGGunMaker {
	private static final HashMap<String,Prefab_Vehicle_Base> prefabBaseHashMap = new HashMap<>();
	public void load( boolean isClient, File file){
		try {
			String dataName = null;
			Prefab_Vehicle_Base data = new Prefab_Vehicle_Base();
			VehicleType vehicleType = null;
			Prefab_AttachedWeapon current = null;
			int turretId_current = 0;
			ArrayList<HMGGunParts> partslist = new ArrayList<HMGGunParts>();
			//File file = new File(configfile,"hmg_handmadeguns.txt");
			if (checkBeforeReadfile(file))
			{
				
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"Shift-JIS"));
				String str = "";
				String str_line = "";
				while((str_line = br.readLine()) != null) {
					str = str.concat(str_line);
					//System.out.println(str);
					if(str.contains(";")) {
						str = str.concat(" ");
						String[] str_temp = str.split(";");
						for(String a_calm:str_temp) {
							str = a_calm;
							String[] type = a_calm.split(",");
							for (int i = 0; i < type.length; i++) {
								type[i] = type[i].trim();
							}
							switch (type[0]) {
								case "Name":
									dataName = type[1];
									if (prefabBaseHashMap.containsKey(dataName)) data = prefabBaseHashMap.get(dataName);
									break;
								case "modelName":
									System.out.println(type[1]);
									data.modelName = type[1];
									break;
								case "modelName_texture":
									data.modelName_texture = type[1];
									break;
								case "health":
									data.maxhealth = parseFloat(type[1]);
									break;
								case "soundname":
									data.soundname = type[1];
									break;
								case "soundpitch":
									data.soundpitch = parseFloat(type[1]);
									break;
								case "throttle_Max":
									data.throttle_Max = parseFloat(type[1]);
									break;
								case "throttle_min":
									data.throttle_min = parseFloat(type[1]);
									break;
								case "throttle_speed":
									data.throttle_speed = parseFloat(type[1]);
									break;
								case "draft":
									data.draft = parseFloat(type[1]);
									break;
								case "thirdDist":
									data.thirdDist = parseFloat(type[1]);
									break;
								case "zoomLevel":
									data.zoomLevel = parseFloat(type[1]);
									break;
								case "splashsound":
									data.splashsound = type[1];
									break;
								case "sightTex":
									data.sightTex = type[1];
									break;
								//LandOnly
								//AirOnly
								case "ParentWeapons_NUM":
									data.prefab_attachedWeapons = new Prefab_AttachedWeapon[parseInt(type[1])];
									break;
								case "AllWeapons_NUM":
									data.prefab_attachedWeapons_all = new Prefab_AttachedWeapon[parseInt(type[1])];
									break;
								case "addParentWeapon":
									data.prefab_attachedWeapons_all[turretId_current] = data.prefab_attachedWeapons[parseInt(type[1])] = new Prefab_AttachedWeapon();
									turretId_current++;
									data.prefab_attachedWeapons[parseInt(type[1])].prefab_turret = prefab_turretHashMap.get(type[2]);
									data.prefab_attachedWeapons[parseInt(type[1])].turretsPos = new Vector3d(parseDouble(type[3]), parseDouble(type[4]), parseDouble(type[5]));
									data.prefab_attachedWeapons[parseInt(type[1])].prefab_Childturrets = new Prefab_AttachedWeapon[parseInt(type[6])];
									current = data.prefab_attachedWeapons[parseInt(type[1])];
									break;
								case "addChildWeapon":
									data.prefab_attachedWeapons_all[turretId_current] = current.prefab_Childturrets[parseInt(type[1])] = new Prefab_AttachedWeapon();
									turretId_current++;
									current.prefab_Childturrets[parseInt(type[1])].prefab_turret = prefab_turretHashMap.get(type[2]);
									current.prefab_Childturrets[parseInt(type[1])].turretsPos = new Vector3d(parseDouble(type[3]), parseDouble(type[4]), parseDouble(type[5]));
									current.prefab_Childturrets[parseInt(type[1])].prefab_Childturrets = new Prefab_AttachedWeapon[parseInt(type[6])];
									current.prefab_Childturrets[parseInt(type[1])].motherTurret = current;
									current = data.prefab_attachedWeapons[parseInt(type[1])];
									break;
								
								case "SetUpSeat1_NUM":
									data.prefab_seats = new Prefab_Seat[parseInt(type[1])];
									data.prefab_seats_zoom = new Prefab_Seat[parseInt(type[1])];
									break;
								case "SetUpSeat2_AddSeat_Normal":
									data.prefab_seats_zoom[parseInt(type[1])] = data.prefab_seats[parseInt(type[1])] = new Prefab_Seat(new double[]{parseDouble(type[2]), parseDouble(type[3]), parseDouble(type[4])}, parseBoolean(type[5]), parseBoolean(type[6]), parseInt(type[7]), parseInt(type[8]));
									break;
								case "SetUpSeat3_AddSeat_Zoom":
									data.prefab_seats_zoom[parseInt(type[1])] = new Prefab_Seat(new double[]{parseDouble(type[2]), parseDouble(type[3]), parseDouble(type[4])}, parseBoolean(type[5]), parseBoolean(type[6]), parseInt(type[7]), parseInt(type[8]));
									break;
								case "back":
									current = current.motherTurret;
									break;
								case "End":
									if (isClient) {
										data.setModel();
										if (!partslist.isEmpty()) data.partslist = partslist;
									}
									prefabBaseHashMap.put(dataName, data);
									GameRegistry.registerItem(new ItemVehicle(dataName).setUnlocalizedName(dataName), dataName);
									break;
							}
							data.readSettings(type);
							if (isClient) readParts_vehicle(type, partslist);
						}
					}
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
	public static Prefab_Vehicle_Base seachInfo(String typeName){
		if(prefabBaseHashMap.containsKey(typeName))
			return prefabBaseHashMap.get(typeName);
		return null;
	}
	
	
	public void readParts_vehicle(String[] type,ArrayList<HMGGunParts> partslist){
		readParts(type,partslist);
		
		switch (type[0]) {
			case "AddPartsRenderAsTrackInf":
				((HMVVehicleParts)currentParts).AddRenderinfTrack(Float.parseFloat(type[1]), Float.parseFloat(type[2]), Float.parseFloat(type[3]), Float.parseFloat(type[4]), Float.parseFloat(type[5]), Float.parseFloat(type[6]));
				break;
			case "AddTrackPos":
				((HMVVehicleParts)currentParts).AddTrackPositions(parseInt(type[1]), Float.parseFloat(type[2]), Float.parseFloat(type[3]), Float.parseFloat(type[4]), Float.parseFloat(type[5]), Float.parseFloat(type[6]), Float.parseFloat(type[7]), parseInt(type[8]), Float.parseFloat(type[9]), Float.parseFloat(type[10]), Float.parseFloat(type[11]), Float.parseFloat(type[12]), Float.parseFloat(type[13]), Float.parseFloat(type[14]));
				break;
			case "IsTrack":
				((HMVVehicleParts)currentParts).setIsTrack(Boolean.parseBoolean(type[1]), parseInt(type[2]));
				break;
			case "IsCloningTrack":
				((HMVVehicleParts)currentParts).setIsTrack_Cloning(Boolean.parseBoolean(type[1]), parseInt(type[2]));
				break;
			case "TurretParts":
				((HMVVehicleParts)currentParts).isTurretParts = true;
				((HMVVehicleParts)currentParts).linkedTurretID = parseInt(type[1]);
				break;
		}
	}
	public HMGGunParts createGunPart(String[] strings){
		return new HMVVehicleParts(strings[1]);
	}
	public HMGGunParts createGunPart(String[] strings,int motherID,HMGGunParts mother){
		return new HMVVehicleParts(strings[1],motherID,mother);
	}
}
