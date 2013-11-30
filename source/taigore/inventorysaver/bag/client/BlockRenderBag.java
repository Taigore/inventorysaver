package taigore.inventorysaver.bag.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import taigore.inventorysaver.main.InventorySaver;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.FMLLog;

public class BlockRenderBag implements ISimpleBlockRenderingHandler
{
    static public BlockRenderBag instance;
    private Vertex[][] vertices;
    /*
    private Vec3[][] allVertices;
    private int[] uInterval;
    private int[] vInterval;
    */
    //TODO Block model utility
    {
        instance = this;
        this.vertices = new Vertex[9][4];
        
        // 1/16th high base side 
        vertices[0][0] = new Vertex(-3.0 / 16, 0.0, -3.0 / 16, 6, 0);
        vertices[0][1] = vertices[0][0].addVector(0, 1.0 / 16, 0, 0, 1);
        vertices[0][2] = vertices[0][1].addVector(6.0 / 16, 0, 0, 6, 0);
        vertices[0][3] = vertices[0][0].addVector(6.0 / 16, 0, 0, 6, 0);
        
        // Side bump bottom
        vertices[1][0] = new Vertex(vertices[0][1], 6, 0);
        vertices[1][1] = vertices[1][0].addVector(0, 0, -1.0 / 16, 0, 1);
        vertices[1][3] = new Vertex(vertices[0][2], 12, 0);
        vertices[1][2] = vertices[1][3].addVector(0, 0, -1.0 / 16, 0, 1);
        
        // Side bump left
        vertices[2][0] = new Vertex(vertices[1][0], 6, 0);
        vertices[2][3] = new Vertex(vertices[1][1], 6, 1);
        vertices[2][1] = vertices[2][0].addVector(0, 6.0 / 16, 0, 6, 0);
        vertices[2][2] = vertices[2][3].addVector(0, 6.0 / 16, 0, 6, 0);
        
        // Side bump right
        vertices[3][0] = new Vertex(vertices[1][2], 6, 0);
        vertices[3][3] = new Vertex(vertices[1][3], 6, 1);
        vertices[3][1] = vertices[3][0].addVector(0, 6.0 / 16, 0, 6, 0);
        vertices[3][2] = vertices[3][3].addVector(0, 6.0 / 16, 0, 6, 0);
        
        // Side bump top
        vertices[5][0] = new Vertex(vertices[2][2], 6, 0);
        vertices[5][1] = new Vertex(vertices[2][1], 6, 1);
        vertices[5][2] = new Vertex(vertices[3][2], 12, 1);
        vertices[5][3] = new Vertex(vertices[3][1], 12, 0);
        
        // Side bump wide face
        vertices[4][0] = new Vertex(vertices[2][3], 0, 6);
        vertices[4][1] = new Vertex(vertices[2][2], 0, 12);
        vertices[4][2] = new Vertex(vertices[3][1], 6, 12);
        vertices[4][3] = new Vertex(vertices[3][0], 6, 6);
        
        // Flat top side
        vertices[8][0] = new Vertex(vertices[5][1], 6, 0);
        vertices[8][1] = vertices[8][0].addVector(0, 1.0 / 16, 0, 0, 1);
        vertices[8][3] = new Vertex(vertices[5][2], 12, 0);
        vertices[8][2] = vertices[8][3].addVector(0, 1.0 / 16, 0, 0, 1);
        
        // Top puff string
        vertices[6][0] = new Vertex(-1.0 / 16, 8.0 / 16, -1.0 / 16, 4, 12);
        vertices[6][1] = vertices[6][0].addVector(0, 1.0 / 16, 0, 0, 1);
        vertices[6][2] = vertices[6][0].addVector(2.0 / 16, 1.0 / 16, 0, 2, 1);
        vertices[6][3] = vertices[6][0].addVector(2.0 / 16, 0, 0, 2, 0);
        
        // Top puff side
        vertices[7][0] = new Vertex(vertices[6][1], 6, 0).addVector(-1.0 / 16, 0, -1.0 /16, 0, 0);
        vertices[7][1] = vertices[7][0].addVector(0, 1.0 / 16, 0, 0, 1);
        vertices[7][2] = vertices[7][0].addVector(4.0 / 16, 1.0 / 16, 0, 4, 1);
        vertices[7][3] = vertices[7][0].addVector(4.0 / 16, 0, 0, 4, 0);
        
        /*
        this.allVertices = new Vec3[9][4];
        this.uInterval = new int[allVertices.length * 2];
        this.vInterval = new int[uInterval.length];
        
        // 1/16th high base side 
        allVertices[0][0] = Vec3.createVectorHelper(-4.0 / 16, 0.0, -4.0 / 16);
        allVertices[0][1] = allVertices[0][0].addVector(0, 1.0 / 16, 0);
        allVertices[0][2] = allVertices[0][1].addVector(8.0 / 16, 0, 0);
        allVertices[0][3] = allVertices[0][0].addVector(8.0 / 16, 0, 0);
        uInterval[0] = 0;
        uInterval[1] = 8;
        vInterval[0] = 0;
        vInterval[1] = 1;
        
        // Side bump bottom
        allVertices[1][0] = allVertices[0][1];
        allVertices[1][1] = allVertices[0][1].addVector(0, 0, -1.0 / 16);
        allVertices[1][2] = allVertices[0][2].addVector(0, 0, -1.0 / 16);
        allVertices[1][3] = allVertices[0][2];
        uInterval[2] = 0;
        uInterval[3] = 8;
        vInterval[2] = 1;
        vInterval[3] = 2;
        
        // Side bump left
        allVertices[2][0] = allVertices[1][0];
        allVertices[2][1] = allVertices[1][0].addVector(0, 8.0 / 16, 0);
        allVertices[2][2] = allVertices[1][1].addVector(0, 8.0 / 16, 0);
        allVertices[2][3] = allVertices[1][1];
        uInterval[4] = 0;
        uInterval[5] = 8;
        vInterval[4] = 2;
        vInterval[5] = 3;
        
        // Side bump right
        allVertices[3][0] = allVertices[1][2];
        allVertices[3][1] = allVertices[1][2].addVector(0, 8.0 / 16, 0);
        allVertices[3][2] = allVertices[1][3].addVector(0, 8.0 / 16, 0);
        allVertices[3][3] = allVertices[1][3];
        uInterval[6] = 0;
        uInterval[7] = 8;
        vInterval[6] = 3;
        vInterval[7] = 4;
        
        // Side bump wide face
        allVertices[4][0] = allVertices[2][3];
        allVertices[4][1] = allVertices[2][2];
        allVertices[4][2] = allVertices[3][1];
        allVertices[4][3] = allVertices[3][0];
        uInterval[8] = 0;
        uInterval[9] = 8;
        vInterval[8] = 0;
        vInterval[9] = 8;
        
        // Side bump top
        allVertices[5][0] = allVertices[2][2];
        allVertices[5][1] = allVertices[2][1];
        allVertices[5][2] = allVertices[3][2];
        allVertices[5][3] = allVertices[3][1];
        uInterval[10] = 0;
        uInterval[11] = 8;
        vInterval[10] = 4;
        vInterval[11] = 5;
        
        //Flat top side
        allVertices[8][0] = allVertices[5][1];
        allVertices[8][1] = allVertices[8][0].addVector(0, 1.0 / 16, 0);
        allVertices[8][3] = allVertices[5][2];
        allVertices[8][2] = allVertices[8][3].addVector(0, 1.0 / 16, 0);
        uInterval[16] = 0;
        uInterval[17] = 8;
        vInterval[16] = 5;
        vInterval[17] = 6;
        
        // Top puff string
        allVertices[6][0] = Vec3.createVectorHelper(-1.0 / 16, 10.0 / 16, -1.0 / 16);
        allVertices[6][1] = allVertices[6][0].addVector(0, 1.0 / 16, 0);
        allVertices[6][2] = allVertices[6][0].addVector(2.0 / 16, 1.0 / 16, 0);
        allVertices[6][3] = allVertices[6][0].addVector(2.0 / 16, 0, 0);
        uInterval[12] = 4;
        uInterval[13] = 6;
        vInterval[12] = 8;
        vInterval[13] = 9;
        
        // Top puff side
        allVertices[7][0] = allVertices[6][1].addVector(-1.0 / 16, 0, -1.0 /16);
        allVertices[7][1] = allVertices[7][0].addVector(0, 1.0 / 16, 0);
        allVertices[7][2] = allVertices[7][0].addVector(4.0 / 16, 1.0 / 16, 0);
        allVertices[7][3] = allVertices[7][0].addVector(4.0 / 16, 0, 0);
        uInterval[14] = 0;
        uInterval[15] = 8;
        vInterval[14] = 6;
        vInterval[15] = 7;
        */
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        try
        {
            Tessellator tsr = Tessellator.instance;
            
            int color = block.colorMultiplier(world, x, y, z);
            
            tsr.setTextureUV(block.getIcon(0, 0).getMinU(), block.getIcon(0, 0).getMinV());
            tsr.setBrightness((int) (0.8f * block.getMixedBrightnessForBlock(world, x, y, z)));
            tsr.setColorOpaque_I(color);
            
            float minU, maxU;
            float minV, maxV;
            
            minU = block.getIcon(0, 0).getInterpolatedU(0.0f);
            minV = block.getIcon(0, 0).getInterpolatedV(0.0f);
            maxU = block.getIcon(0, 0).getInterpolatedU(6.0f);
            maxV = block.getIcon(0, 0).getInterpolatedV(6.0f);
            
            //Bag base
            tsr.addVertexWithUV( 5.0f / 16 + x, y,  5.0f / 16 + z, minU, minV);
            tsr.addVertexWithUV(11.0f / 16 + x, y,  5.0f / 16 + z, maxU, minV);
            tsr.addVertexWithUV(11.0f / 16 + x, y, 11.0f / 16 + z, maxU, maxV);
            tsr.addVertexWithUV( 5.0f / 16 + x, y, 11.0f / 16 + z, minU, maxV);
            
            //Bag flat top
            tsr.addVertexWithUV( 5.0f / 16 + x, 8.0 / 16 + y,  5.0f / 16 + z, minU, minV);
            tsr.addVertexWithUV( 5.0f / 16 + x, 8.0 / 16 + y, 11.0f / 16 + z, maxU, minV);
            tsr.addVertexWithUV(11.0f / 16 + x, 8.0 / 16 + y, 11.0f / 16 + z, maxU, maxV);
            tsr.addVertexWithUV(11.0f / 16 + x, 8.0 / 16 + y,  5.0f / 16 + z, minU, maxV);
            
            maxU = block.getIcon(0, 0).getInterpolatedU(4.0f);
            maxV = block.getIcon(0, 0).getInterpolatedV(4.0f);
            
            //Bag puff bottom
            tsr.addVertexWithUV(6.0 / 16 + x, 9.0 / 16 + y, 10.0 / 16 + z, minU, maxV);
            tsr.addVertexWithUV(6.0 / 16 + x, 9.0 / 16 + y, 6.0 / 16 + z, minU, minV);
            tsr.addVertexWithUV(10.0 / 16 + x, 9.0 / 16 + y, 6.0 / 16 + z, maxU, minV);
            tsr.addVertexWithUV(10.0 / 16 + x, 9.0 / 16 + y, 10.0 / 16 + z, maxU, maxV);
            
            minU = block.getIcon(0, 0).getInterpolatedU(0.0f);
            maxU = block.getIcon(0, 0).getInterpolatedU(4.0f);
            minV = block.getIcon(0, 0).getInterpolatedV(12.0f);
            maxV = block.getIcon(0, 0).getInterpolatedV(16.0f);
            
            //Bag puff top
            tsr.addVertexWithUV(6.0 / 16 + x, 10.0 / 16 + y, 6.0 / 16 + z, minU, minV);
            tsr.addVertexWithUV(6.0 / 16 + x, 10.0 / 16 + y, 10.0 / 16 + z, minU, maxV);
            tsr.addVertexWithUV(10.0 / 16 + x, 10.0 / 16 + y, 10.0 / 16 + z, maxU, maxV);
            tsr.addVertexWithUV(10.0 / 16 + x, 10.0 / 16 + y, 6.0 / 16 + z, maxU, minV);
            
            for(int poly = 0; poly < vertices.length; ++poly)
            {
                Vertex[] polygon = vertices[poly];
                
                for(int facing = 0; facing < 4; ++facing)
                {
                    for(int vert = 0; vert < 4; ++vert)
                    {
                        Vertex vertex = polygon[vert];
                        vertex.rotate((float)(Math.PI / 2 * facing));
                        
                        float vertU = block.getIcon(0, 0).getInterpolatedU(vertex.u);
                        float vertV = block.getIcon(0, 0).getInterpolatedV(vertex.v);
                        
                        tsr.addVertexWithUV(vertex.rX + x + 0.5f, vertex.rY + y, vertex.rZ + z + 0.5f,
                                             vertU,
                                             vertV);
                    }
                }
            }
        }
        catch (Exception graphicException)
        {
            FMLLog.info("Taigore InventorySaver: failed rendering of block at %d, %d, %d", x, y, z);
            graphicException.printStackTrace();
            return false;
        }
        
        return true;
    }
    @Override
    public boolean shouldRender3DInInventory() { return true; }
    @Override
    public int getRenderId() { return InventorySaver.instance.configuration.bag.getBlock().blockID; }
    //This block should never end up in an inventory
    //I'll add a placeholder another time
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) { }
    
    private class Vertex
    {
        double x, y, z;
        double u, v;
        
        double rX, rY, rZ;
        
        Vertex(double x, double y, double z, double u, double v)
        {
            this.x = x; 
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
        }
        Vertex(Vertex pos, double u, double v) { this(pos.x, pos.y, pos.z, u, v); }
        
        Vertex addVector(double x, double y, double z, double u, double v) { return new Vertex(this.x + x, this.y + y, this.z + z, this.u + u, this.v + v); }
        
        void rotate(float angle)
        {
            Vec3 vec = Vec3.createVectorHelper(this.x, this.y, this.z);
            vec.rotateAroundY(angle);
            
            this.rX = vec.xCoord;
            this.rY = vec.yCoord;
            this.rZ = vec.zCoord;
        }
    }
}
