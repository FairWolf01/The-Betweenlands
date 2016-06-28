package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.corrosion.CorrosionHelper;
import thebetweenlands.common.gem.CircleGem;
import thebetweenlands.common.item.ICorrodible;

public class ItemBLSword extends ItemSword implements ICorrodible {
	public ItemBLSword(ToolMaterial material) {
		super(material);

		CorrosionHelper.addCorrosionPropertyOverrides(this);
		CircleGem.addGemPropertyOverrides(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation[] getCorrodibleVariants() {
		//Add all corrodible sword variants
		return new ResourceLocation[] {
				new ResourceLocation("thebetweenlands", "weedwood_sword"),
				new ResourceLocation("thebetweenlands", "bone_sword"),
				new ResourceLocation("thebetweenlands", "octine_sword"),
				new ResourceLocation("thebetweenlands", "valonite_sword")
		};
	}
}
