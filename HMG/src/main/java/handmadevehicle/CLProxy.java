package handmadevehicle;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import handmadeguns.client.render.ModelSetAndData;
import handmadevehicle.audio.TurretSound;
import handmadevehicle.audio.VehicleSound;
import handmadevehicle.audio.Vehicle_OptionalSound;
import handmadevehicle.entity.EntityVehicle;
import handmadevehicle.entity.parts.HasLoopSound;
import handmadevehicle.entity.parts.ModifiedBoundingBox;
import handmadevehicle.entity.parts.OBB;
import handmadevehicle.entity.parts.turrets.TurretObj;
import handmadevehicle.render.RenderVehicle_Simple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.input.Keyboard.isKeyDown;

public class CLProxy extends CMProxy {
	public static final KeyBinding Zoom = new KeyBinding("CannonCamera", Keyboard.KEY_Z, "HMVehicle");
	public static final KeyBinding RButton = new KeyBinding("Fire1", -99 , "HMVehicle");
	public static final KeyBinding LButton = new KeyBinding("Fire2", -100, "HMVehicle");
	public static final KeyBinding W = new KeyBinding("throttle UP", Keyboard.KEY_W, "HMVehicle");
	public static final KeyBinding S = new KeyBinding("throttle Down", Keyboard.KEY_S, "HMVehicle");
	public static final KeyBinding A = new KeyBinding("Yaw Left", Keyboard.KEY_A, "HMVehicle");
	public static final KeyBinding D = new KeyBinding("Yaw Right", Keyboard.KEY_D, "HMVehicle");
	
	public static final KeyBinding F = new KeyBinding("EXControl1 (Flap)(weaponMode)", Keyboard.KEY_F, "HMVehicle");
	public static final KeyBinding X = new KeyBinding("EXControl2 (Brake)", Keyboard.KEY_X, "HMVehicle");
	
	public static final KeyBinding Next_Seat = new KeyBinding("Change to Next Seat", Keyboard.KEY_Y, "HMVehicle");
	public static final KeyBinding Previous_Seat = new KeyBinding("Change to Previous Seat", Keyboard.KEY_H, "HMVehicle");
	public static boolean Next_Seat_stopper = false;
	public static boolean Previous_Seat_stopper = false;
	public static final KeyBinding ChangeControl = new KeyBinding("Change Control", Keyboard.KEY_N, "HMVehicle");
	public static boolean ChangeControl_stopper = false;
	public static final KeyBinding resetCamrot = new KeyBinding("Reset camera Rotation", Keyboard.KEY_V, "HMVehicle");
	public static boolean resetCamrot_stopper = false;
	
	public static final KeyBinding pitchUp = new KeyBinding("pitchUp", Keyboard.KEY_I, "HMVehicle");
	public static final KeyBinding pitchDown = new KeyBinding("pitchDown", Keyboard.KEY_K, "HMVehicle");
	public static final KeyBinding RollRight = new KeyBinding("rollRight", Keyboard.KEY_L, "HMVehicle");
	public static final KeyBinding RollLeft = new KeyBinding("rollLeft", Keyboard.KEY_J, "HMVehicle");
	public static boolean zooming;
	static boolean zoomkey_stopper;
	static boolean fkey_stopper;
	static boolean reload_stopper;
	
	static boolean inited = false;
	public CLProxy() {
		if(!inited) {
			ClientRegistry.registerKeyBinding(Zoom);
			ClientRegistry.registerKeyBinding(W);
			ClientRegistry.registerKeyBinding(S);
			ClientRegistry.registerKeyBinding(A);
			ClientRegistry.registerKeyBinding(D);
			FMLClientHandler.instance().getClient().gameSettings.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
			FMLClientHandler.instance().getClient().gameSettings.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
			FMLClientHandler.instance().getClient().gameSettings.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
			FMLClientHandler.instance().getClient().gameSettings.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
			FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
			FMLClientHandler.instance().getClient().gameSettings.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
			ClientRegistry.registerKeyBinding(RButton);
			ClientRegistry.registerKeyBinding(LButton);
			ClientRegistry.registerKeyBinding(F);
			ClientRegistry.registerKeyBinding(X);
			ClientRegistry.registerKeyBinding(Next_Seat);
			ClientRegistry.registerKeyBinding(Previous_Seat);
			ClientRegistry.registerKeyBinding(pitchUp);
			ClientRegistry.registerKeyBinding(pitchDown);
			ClientRegistry.registerKeyBinding(RollRight);
			ClientRegistry.registerKeyBinding(RollLeft);
			ClientRegistry.registerKeyBinding(ChangeControl);
			ClientRegistry.registerKeyBinding(resetCamrot);
			inited = true;
			
			RenderingRegistry.registerEntityRenderingHandler(EntityVehicle.class,new RenderVehicle_Simple());
		}
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ModelSetAndData loadResource_model(String resourceName_model,String resourceName_Texture,float scale){
		return new ModelSetAndData(AdvancedModelLoader.loadModel(new ResourceLocation("handmadevehicle:textures/model/" + resourceName_model)),new ResourceLocation("handmadevehicle:textures/model/" + resourceName_Texture),scale);
	}
	
	@Override
	public void playsoundasVehicle(float maxdist, Entity attached){
		Minecraft.getMinecraft().getSoundHandler().playSound(new VehicleSound(attached,maxdist));
	}
	@Override
	public void playsoundasTurret(float maxdist, TurretObj attached){
		Minecraft.getMinecraft().getSoundHandler().playSound(new TurretSound(attached,maxdist));
	}
	@Override
	public void playsoundasVehicle_noRepeat(String name , float maxdist, Entity attached, HasLoopSound hasLoopSound,int time){
		Minecraft.getMinecraft().getSoundHandler().playSound(new Vehicle_OptionalSound(name,attached,hasLoopSound,maxdist,time));
	}
	public boolean hasStick(){
		return Controllers.getControllerCount() > 0;
	}
	
	public float getXaxis(){
		if(Controllers.getControllerCount() > 0){
			Controller stick = Controllers.getController(0);
			if(stick != null)return stick.getXAxisValue();
		}
		return 0;
	}
	public float getYaxis(){
		if(Controllers.getControllerCount() > 0){
			Controller stick = Controllers.getController(0);
			if(stick != null)return stick.getYAxisValue();
		}
		return 0;
	}
	public float getZaxis(){
		if(Controllers.getControllerCount() > 0){
			Controller stick = Controllers.getController(0);
			if(stick != null)return stick.getZAxisValue();
		}
		return 0;
	}
	public boolean pitchUp(){
		return pitchUp.getIsKeyPressed();
	}
	public boolean pitchDown() {
		return pitchDown.getIsKeyPressed();
	}
	public boolean rollRight(){
		return RollRight.getIsKeyPressed();
	}
	public boolean rollLeft(){
		return RollLeft.getIsKeyPressed();
	}
	
	@Override
	public boolean reload(){
		//return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
		return isKeyDown(Keyboard.KEY_R);
		//return false;
	}
	
	@Override
	public boolean reload_Semi(){
		
		boolean flag = isKeyDown(Keyboard.KEY_R);
		if(flag){
			if(!reload_stopper) {
				reload_stopper = true;
			}else {
				flag = false;
			}
		}else {
			reload_stopper = false;
		}
		return flag;
	}
	@Override
	public boolean spaceKeyDown(){
		return Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed();
		//return false;
	}
	
	@Override
	public boolean leftclick(){
		boolean flag = FMLClientHandler.instance().getClient().gameSettings.keyBindAttack.getKeyCode() == LButton.getKeyCode();
		return (flag ? FMLClientHandler.instance().getClient().gameSettings.keyBindAttack.getIsKeyPressed():LButton.getIsKeyPressed());
		//return false;
	}
	@Override
	public boolean rightclick(){
		boolean flag = FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem.getKeyCode() == RButton.getKeyCode();
		return (flag ? FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem.getIsKeyPressed():RButton.getIsKeyPressed());
		//return false;
	}
	@Override
	public boolean wclick(){
		return isKeyDown(W.getKeyCode());
		//return false;
	}
	@Override
	public boolean aclick(){
		return isKeyDown(A.getKeyCode());
		//return false;
	}
	@Override
	public boolean sclick(){
		return isKeyDown(S.getKeyCode());
		//return false;
	}
	@Override
	public boolean dclick(){
		return isKeyDown(D.getKeyCode());
		//return false;
	}
	@Override
	public boolean zoomclick(){
		boolean flag = isKeyDown(Zoom.getKeyCode());
		if(flag){
			if(!zoomkey_stopper) {
				zoomkey_stopper = true;
			}else {
				flag = false;
			}
		}else {
			zoomkey_stopper = false;
		}
		return flag;
		//return false;
	}
	@Override
	public boolean fclick(){
		return isKeyDown(F.getKeyCode());
		//return false;
	}
	@Override
	public boolean xclick(){
		return isKeyDown(X.getKeyCode());
		//return false;
	}
	@Override
	public boolean next_Seatclick(){
		boolean flag = isKeyDown(Next_Seat.getKeyCode());
		if(!flag)Next_Seat_stopper = false;
		if(Next_Seat_stopper){
			return false;
		}else if(flag){
			Next_Seat_stopper = true;
			return true;
		}else return false;
		//return false;
	}
	@Override
	public boolean previous_Seatclick(){
		boolean flag = isKeyDown(Previous_Seat.getKeyCode());
		if(!flag)Previous_Seat_stopper = false;
		if(Previous_Seat_stopper){
			return false;
		}else if(flag){
			Previous_Seat_stopper = true;
			return true;
		}else return false;
		//return false;
	}
	@Override
	public boolean changeControlclick(){
		boolean flag = isKeyDown(ChangeControl.getKeyCode());
		if(!flag)ChangeControl_stopper = false;
		if(ChangeControl_stopper){
			return false;
		}else if(flag){
			ChangeControl_stopper = true;
			return true;
		}else return false;
		//return false;
	}
	@Override
	public boolean resetCamrotclick(){
		boolean flag = isKeyDown(resetCamrot.getKeyCode());
		if(!flag)resetCamrot_stopper = false;
		if(resetCamrot_stopper){
			return false;
		}else if(flag){
			resetCamrot_stopper = true;
			return true;
		}else return false;
		//return false;
	}
	
	public boolean iszooming(){
		return zooming;
	}
	
	
	public EntityPlayer getEntityPlayerInstance() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	
	public static void drawOutlinedBoundingBox(ModifiedBoundingBox p_147590_0_, int p_147590_1_)
	{
		
		GL11.glPushMatrix();
		for(OBB aobb : p_147590_0_.boxes){
			drawOutlinedOBB(aobb,p_147590_1_);
		}
		GL11.glPopMatrix();
	}
	
	public static void drawOutlinedOBB(OBB p_147590_0_, int p_147590_1_)
	{
		GL11.glPushMatrix();
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawing(3);
		
		if (p_147590_1_ != -1)
		{
			tessellator.setColorOpaque_I(p_147590_1_);
		}
		
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.minvertex.z);
		tessellator.draw();
		tessellator.startDrawing(3);
		
		if (p_147590_1_ != -1)
		{
			tessellator.setColorOpaque_I(p_147590_1_);
		}
		
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.minvertex.z);
		tessellator.draw();
		tessellator.startDrawing(1);
		
		if (p_147590_1_ != -1)
		{
			tessellator.setColorOpaque_I(p_147590_1_);
		}
		
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.minvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.addVertex(p_147590_0_.maxvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.minvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.addVertex(p_147590_0_.minvertex.x, p_147590_0_.maxvertex.y, -p_147590_0_.maxvertex.z);
		tessellator.draw();
		GL11.glPopMatrix();
	}
}