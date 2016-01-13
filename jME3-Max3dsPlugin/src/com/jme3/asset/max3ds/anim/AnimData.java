package com.jme3.asset.max3ds.anim;
import com.jme3.animation.Animation;
import com.jme3.animation.Skeleton;
import java.util.ArrayList;

public class AnimData {

    public final Skeleton skeleton;
    public final ArrayList<Animation> anims;

    public AnimData(Skeleton skeleton, ArrayList<Animation> anims) {
        this.skeleton = skeleton;
        this.anims = anims;
    }
}
