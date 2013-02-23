package taigore.inventorysaver.client;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelBox;
import net.minecraft.src.ModelRenderer;

public class ModelBag extends ModelBase
{
	private int ModelsNumber = 4;
	
	public ModelRenderer[] bagModel = new ModelRenderer[ModelsNumber];
	
	public ModelBag()
	{
		int[] ModelDimensions = {
								  6,  1,  6, //Cube #1: bag faces
								  2,  1,  2, //Cube #2: closing string
								  3,  1,  3, //Cube #3: bottom leather puff
								  4,  1,  4  //Cube #4: top leather puff
								};
		//
		
		float[] OriginOffsets = {0.0f, 0.0f, 0.0f}; //X, Y, Z
		
		float[] OriginPoints = {
							   -3.0f,  -3.0f, -3.0f,
							   -1.0f,   5.0f, -1.0f,
							   -1.5f,   6.0f, -1.5f,
							   -2.0f,   7.0f, -2.0f
							   };
		
		int[] TextureOffsets = {
								4, 0,
								0, 7,
								7, 3,
								2, 2
							   };
		
		float ScaleFactor = 0.0f;
		
		for(int i = 0; i < ModelsNumber; i++)
		{
			int ModelWidth =  ModelDimensions[i * 3];
			int ModelHeight = ModelDimensions[i * 3 + 1];
			int ModelDepth =  ModelDimensions[i * 3 + 2];
			
			float ModelOriginX =  OriginPoints[i * 3] + OriginOffsets[0];
			float ModelOriginY = -OriginPoints[i * 3 + 1] + OriginOffsets[1];
			float ModelOriginZ =  OriginPoints[i * 3 + 2] + OriginOffsets[2];
			
			int TextureOffsetX = TextureOffsets[i * 2];
			int TextureOffsetY = TextureOffsets[i * 2 + 1];
			
			bagModel[i] = new ModelRenderer(this, TextureOffsets[i * 2], TextureOffsets[i * 2 + 1]);
			bagModel[i].setTextureSize(32, 32);
			
			bagModel[i].addBox(ModelOriginX, ModelOriginY, ModelOriginZ,
							   ModelWidth, ModelHeight, ModelDepth, ScaleFactor);
			
			bagModel[i].setRotationPoint(0.0f, 1.0f, 0.0f);
		}
	}
	
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{	
        float PI = (float)Math.PI;
		
		float[][] RenderPassRotations = {
        									{//Model #1
        										0.0f, 0.0f, 0.0f, //Render Pass #1
        										PI/2, 0.0f, 0.0f, //Render Pass #2
        									    PI  , 0.0f, 0.0f, //Etc.
        									   -PI/2, 0.0f, 0.0f,
        									   	PI/2, PI/2, 0.0f,
        									   	PI/2,-PI/2, 0.0f
        									},
        									{//Model #2
        										0.0f, 0.0f, 0.0f
        									},
        									{//Model #3
        										0.0f, 0.0f, 0.0f
        									},
        									{//Model #4
        										0.0f, 0.0f, 0.0f
        									}
        								};
		
        for(int i = 0; i < this.ModelsNumber; i++)
        {
        	int RenderPasses = RenderPassRotations[i].length / 3;
        	
        	for(int j = 0; j < RenderPasses; j++)
        	{
        		bagModel[i].rotateAngleX = RenderPassRotations[i][j * 3];
        		bagModel[i].rotateAngleY = RenderPassRotations[i][j * 3 + 1];
        		bagModel[i].rotateAngleZ = RenderPassRotations[i][j * 3 + 2];

        		bagModel[i].render(par7);
        	}
        }
    }
}
