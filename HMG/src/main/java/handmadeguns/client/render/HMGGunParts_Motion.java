package handmadeguns.client.render;

import static handmadeguns.HMGGunMaker.*;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static net.minecraft.util.MathHelper.wrapAngleTo180_float;

public class HMGGunParts_Motion {
    public float startflame;
    public float endflame;
    public float startrotationX;
    public float startrotationY;
    public float startrotationZ;
    public float startposX;
    public float startposY;
    public float startposZ;
    public float size_flame;
    public float size_rotationX;
    public float size_rotationY;
    public float size_rotationZ;
    public float size_posX;
    public float size_posY;
    public float size_posZ;
    public float endrotationX;
    public float endrotationY;
    public float endrotationZ;
    public float endposX;
    public float endposY;
    public float endposZ;
    public boolean isrendering = true;

    public HMGGunParts_Motion(){

    }
    public HMGGunParts_Motion(String[] type){
        if(type.length>6) {
            set(parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]), parseFloat(type[readerCnt++]));
        }else {
            startflame = parseInt(type[readerCnt++]);
            isrendering = parseBoolean(type[readerCnt++]);
            endflame = parseInt(type[readerCnt++]);
            setup();
        }
    }

    public void set(float  startflame,
                              float startoffsetX,
                              float startoffsetY,
                              float startoffsetZ,
                              float startrotationX,
                              float startrotationY,
                              float startrotationZ,
                    float   endflame,
                              float endoffsetX,
                              float endoffsetY,
                              float endoffsetZ,
                              float endrotationX,
                              float endrotationY,
                              float endrotationZ){
        this.startflame =         startflame;
        this.startposX =          startoffsetX;
        this.startposY =          startoffsetY;
        this.startposZ =          startoffsetZ;
        this.startrotationX =     startrotationX;
        this.startrotationY =     startrotationY;
        this.startrotationZ =     startrotationZ;
        this.endflame =         endflame;
        this.endposX =          endoffsetX;
        this.endposY =          endoffsetY;
        this.endposZ =          endoffsetZ;
        this.endrotationX =     endrotationX;
        this.endrotationY =     endrotationY;
        this.endrotationZ =     endrotationZ;
        this.setup();
    }
    public void setup(){
        size_rotationX = endrotationX -startrotationX;
        size_rotationY = endrotationY -startrotationY;
        size_rotationZ = endrotationZ -startrotationZ;
        size_posX = endposX -startposX;
        size_posY = endposY -startposY;
        size_posZ = endposZ -startposZ;
        size_flame = endflame - startflame;
        if(size_flame == 0)size_flame = 1;
    }
    public void setup2(){
        size_rotationX = wrapAngleTo180_float(endrotationX -startrotationX);
        size_rotationY = wrapAngleTo180_float(endrotationY -startrotationY);
        size_rotationZ = wrapAngleTo180_float(endrotationZ -startrotationZ);
        size_posX = endposX -startposX;
        size_posY = endposY -startposY;
        size_posZ = endposZ -startposZ;
        size_flame = endflame - startflame;
        if(size_flame == 0)size_flame = 1;
    }
    public final static HMGGunParts_Motion_PosAndRotation temp = new HMGGunParts_Motion_PosAndRotation();
    public HMGGunParts_Motion_PosAndRotation posAndRotation(float flame){
        float flameforCompletion = flame-startflame;
        temp.set(startposX + size_posX * (flameforCompletion/size_flame),
                                                                                                        startposY + size_posY * (flameforCompletion/size_flame),
                                                                                                        startposZ + size_posZ * (flameforCompletion/size_flame),
                                                                                                        startrotationX + size_rotationX * (flameforCompletion/size_flame),
                                                                                                        startrotationY + size_rotationY * (flameforCompletion/size_flame),
                                                                                                        startrotationZ + size_rotationZ * (flameforCompletion/size_flame));
        temp.renderOnOff = isrendering;
        return temp;
    }
}
