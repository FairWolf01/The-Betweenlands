package thebetweenlands.world.biomes;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.CoarseIslandNoiseFeature;
import thebetweenlands.world.biomes.feature.CragSpiresNoiseFeature;
import thebetweenlands.world.biomes.feature.SiltNoiseFeature;

public class BiomeSwampLands
extends BiomeGenBaseBetweenlands
{
	public BiomeSwampLands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		this.setHeightAndVariation(WorldProviderBetweenlands.LAYER_HEIGHT, 10);
		this.setBiomeName("Swamplands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)5);
		this.waterColorMultiplier = 0x184220;
		
		//// Biome features ////
		this.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature())
		.addFeature(new CragSpiresNoiseFeature());
	}
}