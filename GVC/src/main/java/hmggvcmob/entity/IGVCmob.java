package hmggvcmob.entity;

import hmggvcmob.ai.AIAttackGun;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import javax.vecmath.Vector3d;

public interface IGVCmob {
    //今度初期処理をまとめて呼べるようにしよう
    float getviewWide();
    boolean canSeeTarget(Entity target);
    boolean canhearsound(Entity target);
    default Vector3d getSeeingPosition(){
        if(getAttackGun() != null)
        return getAttackGun().getSeeingPosition();
        return null;
    }
    AIAttackGun getAttackGun();

    void setCanDespawn(boolean canDespawn);
}
