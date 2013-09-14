package taigore.inventorysaver.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import com.google.common.base.Strings;

public class DeathPositions extends WorldSavedData
{
    public static final String deathPositionsName = "Taigore_InventorySaver_DeathPositions";
    
    public DeathPositions(String name) { super(name); }
    
    private final Map<String, Stack<Vec3>> deathPoints = new HashMap();
    
    public static DeathPositions getDeathPositions(World world)
    {
        DeathPositions returnValue = null;
        
        if(world != null)
        {
            returnValue = (DeathPositions) world.loadItemData(DeathPositions.class, deathPositionsName);
                
            if(returnValue == null || !DeathPositions.class.isInstance(returnValue))
            {
                returnValue = new DeathPositions(deathPositionsName);
                world.setItemData(deathPositionsName, returnValue);
            }
        }
        
        return returnValue;
    }
    
    public Vec3 peekDeathPoint(String username)
    {
        Stack<Vec3> playerDeaths = this.deathPoints.get(username);
        Vec3 returnValue = null;
        
        if(playerDeaths != null && !playerDeaths.isEmpty())
            returnValue = playerDeaths.peek();
        
        return returnValue;
    }
    public Vec3 peekDeathPoint(EntityPlayer player)
        { return this.peekDeathPoint(player.username); }
    
    public void addDeathPoint(EntityPlayer dead)
    {
        if(dead != null)
        {
            String username = dead.username;
            Vec3 position = Vec3.createVectorHelper(dead.posX, dead.posY, dead.posZ);
            
            this.addDeathPoint(username, position);
        }
    }
    public void addDeathPoint(String username, Vec3 position)
    {
        if(!Strings.isNullOrEmpty(username) && position != null)
        {
            Stack<Vec3> deathPoints = this.deathPoints.get(username);
            
            if(deathPoints == null)
            {
                deathPoints = new Stack();
                this.deathPoints.put(username, deathPoints);
            }
            
            deathPoints.add(position);
            
            this.markDirty();
        }
    }
    
    public Vec3 popDeathPoint(String username)
    {
        Vec3 returnValue = null;
        
        if(!Strings.isNullOrEmpty(username))
        {
            Stack<Vec3> deathStack = this.deathPoints.get(username);
            
            if(deathStack != null && !deathStack.isEmpty())
            {
                returnValue = deathStack.pop();
                
                this.markDirty();
            }
        }
        
        return returnValue;
    }
    public Vec3 popDeathPoint(EntityPlayer player)
        { return player != null ? this.popDeathPoint(player.username) : null; }
    
    ///////////////////
    // WorldSavedData
    ///////////////////
    private static final String playersListTag = "SavedPlayerDeathsList";
    private static final String usernameTag = "PlayerUsername";
    private static final String deathPointsTag = "PlayerDeaths";
    
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        NBTTagList playersList = new NBTTagList();
        
        for(Map.Entry<String, Stack<Vec3>> toSave : this.deathPoints.entrySet())
        {
            //Skip empty stacks
            if(toSave.getValue().isEmpty())
                continue;
            
            NBTTagCompound playerData = new NBTTagCompound();
            NBTTagList deathPoints = new NBTTagList();
            
            for(Vec3 position : toSave.getValue())
            {
                NBTTagCompound positionNBT = new NBTTagCompound();
                
                positionNBT.setDouble("X", position.xCoord);
                positionNBT.setDouble("Y", position.yCoord);
                positionNBT.setDouble("Z", position.zCoord);
                
                deathPoints.appendTag(positionNBT);
            }
            
            playerData.setString(usernameTag, toSave.getKey());
            playerData.setTag(deathPointsTag, deathPoints);
            
            playersList.appendTag(playerData);
        }
        
        nbttagcompound.setTag(playersListTag, playersList);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        this.deathPoints.clear();
        
        NBTTagList players = nbttagcompound.getTagList(playersListTag);
        
        for(int i = 0; i < players.tagCount(); ++i)
        {
            NBTTagCompound playerData = (NBTTagCompound)players.tagAt(i);
            
            String username = playerData.getString(usernameTag);
            
            if(!username.isEmpty())
            {
                NBTTagList savedDeathPoints = playerData.getTagList(deathPointsTag);
                
                Stack<Vec3> deathPoints = savedDeathPoints.tagCount() > 0 ? new Stack()
                                                                          : null;
                
                for(int j = 0; j < savedDeathPoints.tagCount(); ++j)
                {
                    NBTTagCompound savedPosition = (NBTTagCompound)savedDeathPoints.tagAt(j);
                    double posX = savedPosition.getDouble("X");
                    double posY = savedPosition.getDouble("Y");
                    double posZ = savedPosition.getDouble("Z");
                    
                    deathPoints.add(Vec3.createVectorHelper(posX, posY, posZ));
                }
                
                if(deathPoints != null)
                    this.deathPoints.put(username, deathPoints);
            }
        }
    }
}
