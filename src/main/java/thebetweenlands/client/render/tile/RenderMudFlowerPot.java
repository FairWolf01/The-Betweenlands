package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.common.property.IExtendedBlockState;
import thebetweenlands.client.render.block.VertexLighterFlatNoOffsets;
import thebetweenlands.common.block.misc.BlockMudFlowerPot;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;

public class RenderMudFlowerPot extends TileEntitySpecialRenderer<TileEntityMudFlowerPot> {
	private static final VertexLighterFlatNoOffsets FLAT_LIGHTER = new VertexLighterFlatNoOffsets(Minecraft.getMinecraft().getBlockColors());

	@Override
	public final void renderTileEntityAt(TileEntityMudFlowerPot te, double x, double y, double z, float partialTicks, int destroyStage) {
		BlockPos pos = te.getPos();
		IBlockState potBlockState = te.getBlockType().getExtendedState(te.getWorld().getBlockState(pos), te.getWorld(), pos);

		if(potBlockState instanceof IExtendedBlockState) {
			IBlockState flowerBlockState = ((IExtendedBlockState)potBlockState).getValue(BlockMudFlowerPot.FLOWER);

			if(flowerBlockState != null && flowerBlockState.getBlock() != Blocks.AIR) {
				IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(flowerBlockState);
				if(model != null) {
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer vertexBuffer = tessellator.getBuffer();
					this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					RenderHelper.disableStandardItemLighting();
					GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GlStateManager.enableBlend();
					GlStateManager.disableCull();

					if (Minecraft.isAmbientOcclusionEnabled()) {
						GlStateManager.shadeModel(GL11.GL_SMOOTH);
					} else {
						GlStateManager.shadeModel(GL11.GL_FLAT);
					}

					GlStateManager.pushMatrix();
					GlStateManager.translate(x + 0.325F, y + 0.39F, z + 0.325F);
					GlStateManager.scale(0.35F, 0.35F, 0.35F);

					vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

					FLAT_LIGHTER.setParent(new VertexBufferConsumer(vertexBuffer));
					ForgeBlockModelRenderer.render(FLAT_LIGHTER, te.getWorld(), model, flowerBlockState, pos, vertexBuffer, false, MathHelper.getPositionRandom(pos));

					tessellator.draw();

					GlStateManager.popMatrix();

					RenderHelper.enableStandardItemLighting();
				}
			}
		}
	}
}
