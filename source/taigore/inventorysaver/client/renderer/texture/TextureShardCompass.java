package taigore.inventorysaver.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import taigore.inventorysaver.world.ShardPositions;

public class TextureShardCompass extends TextureStitched
{
    private boolean isEmerald;
    
    private double currentAngle = 0.0f;
    private double currentHeading = 0.0f;
    
    public TextureShardCompass(String par1, boolean isEmerald) { super(par1); this.isEmerald = isEmerald; }
    
    @Override
    public void updateAnimation()
    {
        int frames = this.textureList.size();
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        
        if(player != null)
        {
            double targetAngle = this.getCompassTargetAngle(player.worldObj, this.isEmerald, player.rotationYaw, player.posX, player.posY, player.posZ);
            double targetDelta = Math.abs(targetAngle - this.currentAngle) > Math.abs(this.currentAngle - targetAngle) ? this.currentAngle - targetAngle : targetAngle - this.currentAngle;
            
            
            this.textureSheet.func_104062_b(this.originX, this.originY, (Texture)this.textureList.get(frameNum));
        }
    }
    
    public double getCompassTargetAngle(World compassWorld, boolean isEmerald, double yaw, double posX, double posY, double posZ)
    {
        int frames = this.textureList.size();
        Vec3 shardPull;
        
        if(compassWorld != null)
            shardPull = ShardPositions.getShardPositions(compassWorld).getPullFrom(posX, posY, posZ, isEmerald);
        else
            shardPull = Vec3.createVectorHelper(0, 0, 0);
        
        shardPull = shardPull.addVector(0, -shardPull.yCoord, 0).normalize();
        
        double pullAngle = Math.atan2(shardPull.zCoord, shardPull.xCoord);
        double referenceAngle = ((yaw / 180 * Math.PI) + Math.PI + (2 * Math.PI / frames)); 
        double targetAngle = pullAngle - referenceAngle;
        
        while(targetAngle < 0) targetAngle += Math.PI * 2;
        
        return targetAngle % 360.0f;
    }
}
