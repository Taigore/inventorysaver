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

public class DeathPositions extends WorldSavedData
{
    public static final String deathPositionsName = "Taigore_InventorySaver_DeathPositions";
    
    public DeathPositions(String name) { super(name); }
    
    private final Map<String, Stack<Vec3>> deathPoints = new HashMap();
    
    public static DeathPositions getDeathPositions(World world)
    {
        WorldSavedData returnValue = null;
        
        if(world != null)
        {
            returnValue = world.loadItemData(DeathPositions.class, deathPositionsName);
        
            if(returnValue == null || !DeathPositions.class.isInstance(returnValue))
            {
                returnValue = world.loadItemData(DeathPositions.class, deathPositionsName);
                
                if(returnValue == null || !DeathPositions.class.isInstance(returnValue))
                {
                    returnValue = new DeathPositions(deathPositionsName);
                    world.setItemData(deathPositionsName, returnValue);
                }
            }
        }
        
        return (DeathPositions)returnValue;
    }
    
    public Stack<Vec3> getDeathPointsForPlayer(EntityPlayer player)
    {
        if(player != null)
            return this.getDeathPointsForPlayer(player.username);
        else
            return null;
    }
    public Stack<Vec3> getDeathPointsForPlayer(String username)
    {
        Stack<Vec3> returnValue = null;
        
        if(username != null && !username.isEmpty())
        {
            returnValue = this.deathPoints.get(username);
            
            if(returnValue == null)
            {
                returnValue = new Stack();
                this.deathPoints.put(username, returnValue);
            }
        }
        
        return returnValue;
    }
    
    public Vec3 getLastDeathPointForPlayer(EntityPlayer player)
    {
        if(player != null)
            return this.getLastDeathPointForPlayer(player.username);
        else
            return null;
    }
    public Vec3 getLastDeathPointForPlayer(String username)
    {
        Stack<Vec3> playerDeaths = this.getDeathPointsForPlayer(username);
        Vec3 returnValue = null;
        
        if(playerDeaths != null && !playerDeaths.isEmpty())
            returnValue = playerDeaths.peek();
        
        return returnValue;
    }
    
    public void addNewDeathPointForPlayer(EntityPlayer dead)
    {
        if(dead != null)
        {
            String username = dead.username;
            Vec3 position = Vec3.createVectorHelper(dead.posX, dead.posY, dead.posZ);
            
            Stack<Vec3> previousDeaths = this.getDeathPointsForPlayer(username);
            previousDeaths.push(position);
            
            this.markDirty();
        }
    }
    
    public Vec3 removeLastPointForPlayer(EntityPlayer player)
    {
        return player != null ? this.removeLastPointForPlayer(player.username) : null;
    }
    public Vec3 removeLastPointForPlayer(String username)
    {
        Vec3 returnValue = null;
        
        if(username != null && !username.isEmpty())
        {
            Stack<Vec3> deathStack = this.getDeathPointsForPlayer(username);
            
            if(deathStack != null)
            {
                if(!deathStack.isEmpty())
                    returnValue = deathStack.pop();
                else
                    this.deathPoints.remove(username);
                
                this.markDirty();
            }
        }
        
        return returnValue;
    }
    
    private static final String playersListTag = "SavedPlayerDeathsList";
    private static final String usernameTag = "PlayerUsername";
    private static final String deathsTag = "PlayerDeaths";
    
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        NBTTagList players = new NBTTagList();
        
        for(Map.Entry<String, Stack<Vec3>> toSave : this.deathPoints.entrySet())
        {
            NBTTagCompound playerCompound = new NBTTagCompound();
            
            playerCompound.setString(usernameTag, toSave.getKey());
            
            NBTTagList deathPoints = new NBTTagList();
            
            for(int i = 0; i < toSave.getValue().size(); ++i)
            {
                Vec3 position = toSave.getValue().get(i);
                
                if(position != null)
                {
                    NBTTagCompound positionData = new NBTTagCompound();
                    
                    positionData.setDouble("X", position.xCoord);
                    positionData.setDouble("Y", position.yCoord);
                    positionData.setDouble("Z", position.zCoord);
                    
                    deathPoints.appendTag(positionData);
                }
            }
            
            playerCompound.setTag(deathsTag, deathPoints);
            
            players.appendTag(playerCompound);
        }
        
        nbttagcompound.setTag(playersListTag, players);
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
                NBTTagList savedDeathPoints = playerData.getTagList(deathsTag);
                
                Stack<Vec3> deathPoints = savedDeathPoints.tagCount() > 0 ? new Stack() : null;
                
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
