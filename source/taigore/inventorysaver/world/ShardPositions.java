package taigore.inventorysaver.world;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import taigore.inventorysaver.network.packet.Packet250ShardUpdate;

public class ShardPositions extends WorldSavedData
{
    public static final String shardPositionsName = "Taigore_InventorySaver_ShardPositions";
    
    private ShardPositions(World world)
    {
        super(shardPositionsName);
        this.ownWorld = world;
    }
    
    public final World ownWorld; 
    public final Set<Tracked> emeraldShards = new HashSet();
    public final Set<Tracked> diamondShards = new HashSet();
    
    public static ShardPositions getShardPositions(World world)
    {
        WorldSavedData returnValue = null;
        
        if(world != null)
        {
            returnValue = world.loadItemData(ShardPositions.class, shardPositionsName);
        
            if(returnValue == null || !ShardPositions.class.isInstance(returnValue))
            {
                returnValue = new ShardPositions(world);
                world.setItemData(shardPositionsName, returnValue);
            }
        }
        
        return (ShardPositions)returnValue;
    }
    
    public void changeShardStrength(Entity toTrack, int strength, boolean isEmerald)
    {
        if(toTrack != null && !toTrack.worldObj.isRemote)
        {
            Set<Tracked> toUpdate = isEmerald ? this.emeraldShards : this.diamondShards;
            Tracked tempTracked = new Tracked(toTrack);
            tempTracked.strength = strength;
            
            for(Tracked inSet : toUpdate)
            {
                if(tempTracked.equals(inSet))
                {
                    tempTracked.strength += inSet.strength;
                    toUpdate.remove(inSet);
                    break;
                }
            }
            
            //If strength is greater than 0, the tracked entity is readded and will be synchronized
            //automatically at a later time
            if(tempTracked.strength > 0)
                toUpdate.add(tempTracked);
            //Otherwise, the tracked entity is not in list to be synchronized, so a manual synchronization
            //is needed
            else
            {
                Packet250CustomPayload shardRemovalPacket = Packet250ShardUpdate.makeForSomeTracked(isEmerald, tempTracked);
                PacketDispatcher.sendPacketToAllInDimension(shardRemovalPacket, this.ownWorld.getWorldInfo().getDimension());
            }
        }
    }
    
    public Vec3 getPullFrom(double compassX, double compassY, double compassZ, boolean isEmerald)
    {
        Vec3 returnValue = Vec3.createVectorHelper(0, 0, 0);
        
        Set<Tracked> toRead = isEmerald ? this.emeraldShards : this.diamondShards;
        
        for(Tracked toAdd : toRead)
        {
            //Adding attraction
            Vec3 attraction = toAdd.getPull(compassX, compassY, compassZ);
            
            returnValue.xCoord += attraction.xCoord;
            returnValue.yCoord += attraction.yCoord;
            returnValue.zCoord += attraction.zCoord;
        }
        
        return returnValue;
    }
    
    static final String diamondShardsTag = "DiamondList";
    static final String emeraldShardsTag = "EmeraldList";
    
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        NBTTagList diamondShards = new NBTTagList();
        NBTTagList emeraldShards = new NBTTagList();
        
        for(int i = 0; i < 2; ++i)
        {
            boolean isEmerald = i != 0;
            
            Set<Tracked> saving = isEmerald ? this.diamondShards : this.emeraldShards;
            NBTTagList saveList = isEmerald ? diamondShards : emeraldShards;
            
            for(Tracked toSave : saving)
            {
                NBTTagCompound entry = toSave.writeToNBT(new NBTTagCompound());
                saveList.appendTag(entry);
            }
        }
        
        nbttagcompound.setTag(diamondShardsTag, diamondShards);
        nbttagcompound.setTag(emeraldShardsTag, emeraldShards);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        //Pre-clearing any saved shards
        this.diamondShards.clear();
        this.emeraldShards.clear();
        
        NBTTagList diamondShards = nbttagcompound.getTagList("DiamondList");
        NBTTagList emeraldShards = nbttagcompound.getTagList("EmeraldList");
        
        for(int switcher = 0; switcher < 2; ++switcher)
        {
            boolean isEmerald = switcher != 0;
            NBTTagList loadList =  isEmerald ? diamondShards : emeraldShards;
            Set<Tracked> toUpdate = isEmerald ? this.diamondShards : this.emeraldShards;
            
            for(int i = 0; i < loadList.tagCount(); ++i)
            {
                NBTTagCompound shardData = (NBTTagCompound)loadList.tagAt(i);
                Tracked loaded = new Tracked();
                loaded.readFromNBT(shardData);
                
                toUpdate.add(loaded);
            }
        }
    }
    
    public class Tracked
    {
        public Entity tracked;
        public UUID entityUUID;
        
        public Vec3 lastPosition;
        public int strength;
        
        public Tracked() {}
        public Tracked(Entity toTrack)
        {
            this.tracked = toTrack;
            
            if(this.tracked != null)
            {
                this.entityUUID = this.tracked.getPersistentID();
                this.strength = 0;
            }
        }
        
        public void updatePosition()
        {
            if(this.tracked != null && !this.tracked.worldObj.isRemote && this.tracked.worldObj.getEntityByID(this.tracked.entityId) != null)
            {
                boolean hasUpdated = false;
                
                if(this.lastPosition == null)
                {
                    this.lastPosition = Vec3.createVectorHelper(this.tracked.posX, this.tracked.posY, this.tracked.posZ);
                    hasUpdated = true;
                }
                else
                {
                    hasUpdated = this.lastPosition.xCoord == this.tracked.posX || this.lastPosition.yCoord == this.tracked.posY || this.lastPosition.zCoord == this.tracked.posZ;;
                
                    this.lastPosition.xCoord = this.tracked.posX;
                    this.lastPosition.yCoord = this.tracked.posY;
                    this.lastPosition.zCoord = this.tracked.posZ;
                }
                
                if(hasUpdated)
                {
                    boolean diamondIsSmaller = ShardPositions.this.diamondShards.size() < ShardPositions.this.diamondShards.size();
                    boolean isEmerald;
                    
                    if(diamondIsSmaller)
                        isEmerald = ShardPositions.this.diamondShards.contains(this) || ShardPositions.this.emeraldShards.contains(this);
                    else
                        isEmerald = ShardPositions.this.emeraldShards.contains(this) || ShardPositions.this.diamondShards.contains(this);
                    
                    Packet250CustomPayload toSend = Packet250ShardUpdate.makeForSomeTracked(isEmerald, this);
                    PacketDispatcher.sendPacketToAllInDimension(toSend, ShardPositions.this.ownWorld.getWorldInfo().getDimension());
                }
            }
        }
        
        Vec3 getPull(double posX, double posY, double posZ)
        {
            Vec3 pull = this.lastPosition.subtract(Vec3.createVectorHelper(posX, posY, posZ)).normalize();
            pull.xCoord *= this.strength;
            pull.yCoord *= this.strength;
            pull.zCoord *= this.strength;
            
            return pull;
        }
        
        static final String lastPositionTag = "Position";
            static final String lastXTag = "posX";
            static final String lastYTag = "posY";
            static final String lastZTag = "posZ";
        static final String UUIDFirstHalfTag = "EntityUUIDMost";
        static final String UUIDSecondHalfTag = "EntityUUIDLeast";
        static final String strengthTag = "Strength";
        
        NBTTagCompound writeToNBT(NBTTagCompound toWriteOn)
        {
            if(toWriteOn == null) toWriteOn = new NBTTagCompound();
            
            if(this.lastPosition != null)
            {
                NBTTagCompound vector = new NBTTagCompound();
                
                vector.setDouble(lastXTag, this.lastPosition.xCoord);
                vector.setDouble(lastYTag, this.lastPosition.yCoord);
                vector.setDouble(lastZTag, this.lastPosition.zCoord);
                
                toWriteOn.setCompoundTag(lastPositionTag, vector);
            }
            
            toWriteOn.setInteger(strengthTag, this.strength);
            
            if(this.entityUUID != null)
            {
                toWriteOn.setLong(UUIDFirstHalfTag, this.entityUUID.getMostSignificantBits());
                toWriteOn.setLong(UUIDSecondHalfTag, this.entityUUID.getLeastSignificantBits());
            }
            
            return toWriteOn;
        }
        
        Tracked readFromNBT(NBTTagCompound toRead)
        {
            if(toRead != null)
            {
                if(toRead.hasKey(lastPositionTag))
                {
                    NBTTagCompound vector = toRead.getCompoundTag(lastPositionTag);
                    
                    if(this.lastPosition == null) this.lastPosition = Vec3.createVectorHelper(0, 0, 0);
                    
                    this.lastPosition.xCoord = vector.getDouble(lastXTag);
                    this.lastPosition.yCoord = vector.getDouble(lastYTag);
                    this.lastPosition.zCoord = vector.getDouble(lastZTag);
                }
                
                if(toRead.hasKey(UUIDFirstHalfTag) && toRead.hasKey(UUIDSecondHalfTag))
                    this.entityUUID = new UUID(toRead.getLong(UUIDFirstHalfTag), toRead.getLong(UUIDSecondHalfTag));
                
                this.strength = toRead.getInteger(strengthTag);
            }
            
            return this;
        }
        
        @Override
        public boolean equals(Object toCompare)
        {
            if(toCompare == this) return true;
            
            if(this.getClass().isInstance(toCompare))
                return this.entityUUID.compareTo(((Tracked)toCompare).entityUUID) == 0;
            else
                return false;
        }
        
        @Override
        public int hashCode() { return this.entityUUID.hashCode(); }
    }
}
