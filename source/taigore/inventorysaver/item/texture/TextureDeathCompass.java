package taigore.inventorysaver.item.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import taigore.inventorysaver.world.DeathPositions;

public class TextureDeathCompass extends TextureCompass
{
    public TextureDeathCompass(String par1) { super(par1); }
    
    @Override
    public void updateCompass(World compassWorld, double playerX, double playerZ, double playerYaw, boolean skipCalculations, boolean setAngle)
    {
        EntityPlayer thePlayer = Minecraft.getMinecraft().thePlayer;
        
        if (!this.framesTextureData.isEmpty() && thePlayer != null)
        {
            double targetAngle = 0.0D;

            if (compassWorld != null && !skipCalculations)
            {
                DeathPositions positions = DeathPositions.getDeathPositions(compassWorld);
                Vec3 deathPosition = positions.peekDeathPoint(thePlayer);
                
                if(deathPosition != null)
                {
                    double deltaX = deathPosition.xCoord - playerX;
                    double deltaZ = deathPosition.zCoord - playerZ;
                    playerYaw %= 360.0D;
                    targetAngle = -((playerYaw - 90.0D) * Math.PI / 180.0D - Math.atan2(deltaZ, deltaX));
                }
                else
                    targetAngle = Math.random() * Math.PI * 2.0D;
            }

            if (setAngle)
            {
                this.currentAngle = targetAngle;
            }
            else
            {
                double normalizedTarget = targetAngle - this.currentAngle;

                while(normalizedTarget < -Math.PI)
                    normalizedTarget += (Math.PI * 2D);

                while (normalizedTarget >= Math.PI) 
                    normalizedTarget -= (Math.PI * 2D);

                if (normalizedTarget < -1.0D)
                    normalizedTarget = -1.0D;

                if (normalizedTarget > 1.0D)
                    normalizedTarget = 1.0D;

                this.angleDelta += normalizedTarget * 0.1D;
                this.angleDelta *= 0.8D;
                this.currentAngle += this.angleDelta;
            }

            int i = (int)((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double)this.framesTextureData.size()) % this.framesTextureData.size();

            while(i < 0)
                 i = (i + this.framesTextureData.size()) % this.framesTextureData.size();

            if (i != this.frameCounter)
            {
                this.frameCounter = i;
                TextureUtil.uploadTextureSub((int[])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
    }
}
