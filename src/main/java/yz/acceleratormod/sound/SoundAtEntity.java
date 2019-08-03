package yz.acceleratormod.sound;

import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class SoundAtEntity extends PositionedSound {
    public int entityID;

    public SoundAtEntity(ResourceLocation location, Entity entity, float vol, float pitch) {
        super(location);
        this.entityID = entity.getEntityId();
        this.volume = vol;
        this.repeat = false;
        this.field_147663_c = pitch; //Pitch
        this.xPosF = (float) entity.posX;
        this.yPosF = (float) entity.posY;
        this.zPosF = (float) entity.posZ;
    }
}
