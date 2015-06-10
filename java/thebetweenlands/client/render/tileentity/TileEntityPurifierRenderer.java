package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.model.block.ModelPurifier;
import thebetweenlands.tileentities.TileEntityPurifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityPurifierRenderer extends TileEntitySpecialRenderer {
	private final RenderBlocks blockRenderer = new RenderBlocks();
	private final ModelPurifier model = new ModelPurifier();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/purifier.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityPurifier purifier = (TileEntityPurifier) tile;
		int meta = purifier.getBlockMetadata();
		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		switch (meta) {
			case 2:
				GL11.glRotatef(180F, 0.0F, 1F, 0F);
				break;
			case 3:
				GL11.glRotatef(0F, 0.0F, 1F, 0F);
				break;
			case 4:
				GL11.glRotatef(90F, 0.0F, 1F, 0F);
				break;
			case 5:
				GL11.glRotatef(-90F, 0.0F, 1F, 0F);
				break;
		}
		model.renderAll();
		if(purifier.isPurifying() && purifier.lightOn)
			model.renderFirePlate();
		GL11.glPopMatrix();
	
// TODO make this a water block and make it sync for rendering
// TODO give it bubbles whilst purifying
		int amount = purifier.waterTank.getFluidAmount();
		int capacity = purifier.waterTank.getCapacity();
		float size = 0.70F / capacity * amount;
		if (amount >= 100) {
			Tessellator tess = Tessellator.instance;
			IIcon waterIcon = ((BlockSwampWater)BLBlockRegistry.swampWater).getWaterIcon(1);
			
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(770, 771);
			bindTexture(TextureMap.locationBlocksTexture);
			float tx = (float) x + 0.0F;
			float ty = (float) (y + 0.26F + size * 0.5F);
			float tz = (float) z + 0.0F;
			tess.addTranslation(tx, ty, tz);
			tess.startDrawingQuads();
			tess.setColorRGBA_F(0.2F, 0.6F, 0.4F, 1.0F);
			tess.addVertexWithUV(0.1, 0, 0.1, waterIcon.getMinU(), waterIcon.getMinV());
			tess.addVertexWithUV(0.1, 0, 0.9, waterIcon.getMinU(), waterIcon.getMaxV());
			tess.addVertexWithUV(0.9, 0, 0.9, waterIcon.getMaxU(), waterIcon.getMaxV());
			tess.addVertexWithUV(0.9, 0, 0.1, waterIcon.getMaxU(), waterIcon.getMinV());
			tess.draw();
			tess.addTranslation(-tx, -ty, -tz);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
	}
}
