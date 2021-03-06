package thebetweenlands.client.proxy;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thebetweenlands.client.event.handler.AmbienceSoundPlayHandler;
import thebetweenlands.client.event.handler.BrightnessHandler;
import thebetweenlands.client.event.handler.CameraPositionHandler;
import thebetweenlands.client.event.handler.DebugHandlerSharedLocation;
import thebetweenlands.client.event.handler.DecayRenderHandler;
import thebetweenlands.client.event.handler.DruidAltarSoundHandler;
import thebetweenlands.client.event.handler.FogHandler;
import thebetweenlands.client.event.handler.InputHandler;
import thebetweenlands.client.event.handler.ItemTooltipHandler;
import thebetweenlands.client.event.handler.MusicHandler;
import thebetweenlands.client.event.handler.ScreenRenderHandler;
import thebetweenlands.client.event.handler.ShaderHandler;
import thebetweenlands.client.event.handler.TextureStitchHandler;
import thebetweenlands.client.event.handler.TextureStitchHandler.TextureFrameSplitter;
import thebetweenlands.client.event.handler.ThemHandler;
import thebetweenlands.client.event.handler.WorldRenderHandler;
import thebetweenlands.client.event.handler.equipment.RadialMenuHandler;
import thebetweenlands.client.gui.GuiBLMainMenu;
import thebetweenlands.client.gui.GuiLorePage;
import thebetweenlands.client.gui.GuiPouchNaming;
import thebetweenlands.client.gui.inventory.GuiAnimator;
import thebetweenlands.client.gui.inventory.GuiBLDualFurnace;
import thebetweenlands.client.gui.inventory.GuiBLFurnace;
import thebetweenlands.client.gui.inventory.GuiDruidAltar;
import thebetweenlands.client.gui.inventory.GuiMortar;
import thebetweenlands.client.gui.inventory.GuiPouch;
import thebetweenlands.client.gui.inventory.GuiPurifier;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.client.render.entity.RenderAngler;
import thebetweenlands.client.render.entity.RenderAngryPebble;
import thebetweenlands.client.render.entity.RenderBLArrow;
import thebetweenlands.client.render.entity.RenderBlindCaveFish;
import thebetweenlands.client.render.entity.RenderBloodSnail;
import thebetweenlands.client.render.entity.RenderChiromaw;
import thebetweenlands.client.render.entity.RenderDarkDruid;
import thebetweenlands.client.render.entity.RenderDragonFly;
import thebetweenlands.client.render.entity.RenderFirefly;
import thebetweenlands.client.render.entity.RenderFrog;
import thebetweenlands.client.render.entity.RenderGasCloud;
import thebetweenlands.client.render.entity.RenderGecko;
import thebetweenlands.client.render.entity.RenderGiantToad;
import thebetweenlands.client.render.entity.RenderLeech;
import thebetweenlands.client.render.entity.RenderLurker;
import thebetweenlands.client.render.entity.RenderMireSnail;
import thebetweenlands.client.render.entity.RenderMireSnailEgg;
import thebetweenlands.client.render.entity.RenderMummyArm;
import thebetweenlands.client.render.entity.RenderPeatMummy;
import thebetweenlands.client.render.entity.RenderPyrad;
import thebetweenlands.client.render.entity.RenderPyradFlame;
import thebetweenlands.client.render.entity.RenderRopeNode;
import thebetweenlands.client.render.entity.RenderShockwaveBlock;
import thebetweenlands.client.render.entity.RenderShockwaveSwordItem;
import thebetweenlands.client.render.entity.RenderSiltCrab;
import thebetweenlands.client.render.entity.RenderSludge;
import thebetweenlands.client.render.entity.RenderSnailPoisonJet;
import thebetweenlands.client.render.entity.RenderSporeling;
import thebetweenlands.client.render.entity.RenderSwampHag;
import thebetweenlands.client.render.entity.RenderSwordEnergy;
import thebetweenlands.client.render.entity.RenderTarBeast;
import thebetweenlands.client.render.entity.RenderTarminion;
import thebetweenlands.client.render.entity.RenderTermite;
import thebetweenlands.client.render.entity.RenderThrownTarminion;
import thebetweenlands.client.render.entity.RenderVolatileSoul;
import thebetweenlands.client.render.entity.RenderWight;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.entity.ParticleWisp;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.tile.RenderAlembic;
import thebetweenlands.client.render.tile.RenderAnimator;
import thebetweenlands.client.render.tile.RenderChestBetweenlands;
import thebetweenlands.client.render.tile.RenderCompostBin;
import thebetweenlands.client.render.tile.RenderDruidAltar;
import thebetweenlands.client.render.tile.RenderGeckoCage;
import thebetweenlands.client.render.tile.RenderInfuser;
import thebetweenlands.client.render.tile.RenderItemCage;
import thebetweenlands.client.render.tile.RenderItemShelf;
import thebetweenlands.client.render.tile.RenderLootPot;
import thebetweenlands.client.render.tile.RenderMudFlowerPot;
import thebetweenlands.client.render.tile.RenderPestleAndMortar;
import thebetweenlands.client.render.tile.RenderPossessedBlock;
import thebetweenlands.client.render.tile.RenderPurifier;
import thebetweenlands.client.render.tile.RenderSpawnerBetweenlands;
import thebetweenlands.client.render.tile.RenderSpikeTrap;
import thebetweenlands.client.render.tile.RenderWeedwoodSign;
import thebetweenlands.client.render.tile.RenderWeedwoodWorkbench;
import thebetweenlands.client.render.tile.RenderWisp;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.entity.EntityShockwaveSwordItem;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;
import thebetweenlands.common.entity.mobs.EntityMummyArm;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.entity.mobs.EntityPyradFlame;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityTarminion;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.entity.mobs.EntityVolatileSoul;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;
import thebetweenlands.common.herblore.book.GuiManualHerblore;
import thebetweenlands.common.herblore.book.HLEntryRegistry;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerPouch;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.tile.spawner.TileEntityAlembic;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.util.AdvancedStateMap;
import thebetweenlands.util.GLUProjection;
import thebetweenlands.util.config.ConfigHandler;

public class ClientProxy extends CommonProxy {

	//Please turn this off again after using
	private static final boolean createJSONFile = false;

	public static Render<EntityDragonFly> dragonFlyRenderer;

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		switch (id) {
		case GUI_DRUID_ALTAR:
			if (tile instanceof TileEntityDruidAltar) {
				return new GuiDruidAltar(player.inventory, (TileEntityDruidAltar) tile);
			}
			break;

		case GUI_PURIFIER:
			if (tile instanceof TileEntityPurifier) {
				return new GuiPurifier(player.inventory, (TileEntityPurifier) tile);
			}
			break;

		case GUI_WEEDWOOD_CRAFT:
			if (tile instanceof TileEntityWeedwoodWorkbench) {
				return new GuiWeedwoodWorkbench(player.inventory, (TileEntityWeedwoodWorkbench) tile);
			}
			break;

		case GUI_HL:
			return new GuiManualHerblore(player);

		case GUI_BL_FURNACE:
			if (tile instanceof TileEntityBLFurnace) {
				return new GuiBLFurnace(player.inventory, (TileEntityBLFurnace) tile);
			}
			break;

		case GUI_BL_DUAL_FURNACE:
			if (tile instanceof TileEntityBLDualFurnace) {
				return new GuiBLDualFurnace(player.inventory, (TileEntityBLDualFurnace) tile);
			}
			break;

		case GUI_PESTLE_AND_MORTAR:
			if (tile instanceof TileEntityMortar) {
				return new GuiMortar(player.inventory, (TileEntityMortar) tile);
			}
			break;

		case GUI_ANIMATOR:
			if (tile instanceof TileEntityAnimator) {
				return new GuiAnimator(player, (TileEntityAnimator) tile);
			}
			break;

		case GUI_LURKER_POUCH: {
			ItemStack item = player.getHeldItemMainhand();
			if(item == null || item.getItem() instanceof ItemLurkerSkinPouch == false) {
				item = player.getHeldItemOffhand();
			}
			if(item != null && item.getItem() instanceof ItemLurkerSkinPouch) {
				return new GuiPouch((ContainerPouch) new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (x * 9), I18n.format("container.lurkerSkinPouch"))));
			}
			break;
		}

		case GUI_LURKER_POUCH_KEYBIND: {
			ItemStack item = ItemLurkerSkinPouch.getFirstPouch(player);
			if(item != null) {
				return new GuiPouch((ContainerPouch) new ContainerPouch(player, player.inventory, new InventoryItem(item, 9 + (x * 9), I18n.format("container.lurkerSkinPouch"))));
			}
		}

		case GUI_LURKER_POUCH_NAMING:
			if(player.getHeldItemMainhand() != null) {
				return new GuiPouchNaming(player, x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
			}
			break;

		case GUI_LORE:
			return new GuiLorePage(player.getHeldItem(x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
		}

		return null;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public void registerItemAndBlockRenderers() {
		CustomModelManager.INSTANCE.registerLoader();
		//TODO ItemRegistry.registerRenderers();
		registerBlockRenderers();
	}

	private static void registerBlockRenderers() {
		for (Block block : BlockRegistry.BLOCKS) {
			if (block instanceof IStateMappedBlock) {
				AdvancedStateMap.Builder builder = new AdvancedStateMap.Builder();
				((IStateMappedBlock) block).setStateMapper(builder);
				ModelLoader.setCustomStateMapper(block, builder.build());
			}
			if (block instanceof ICustomItemBlock) {
				ICustomItemBlock customItemBlock = (ICustomItemBlock) block;
				if (customItemBlock.getRenderedItem() != null) {
					ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(customItemBlock.getRenderedItem().getRegistryName(), "inventory"));
					continue;
				}
			}
			ResourceLocation name = block.getRegistryName();
			if (block instanceof ISubtypeBlock) {
				ISubtypeBlock subtypeBlock = (ISubtypeBlock) block;
				for (int i = 0; i < subtypeBlock.getSubtypeNumber(); i++) {
					int meta = subtypeBlock.getSubtypeMeta(i);
					ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(name.getResourceDomain() + ":" + String.format(subtypeBlock.getSubtypeName(meta), name.getResourcePath()), "inventory"));
				}
			} else {
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(name, "inventory"));
			}
		}
	}

	@Override
	public void setCustomStateMap(Block block, StateMap stateMap) {
		ModelLoader.setCustomStateMapper(block, stateMap);
	}

	/*
        @Override
        public void registerDefaultBlockItemRenderer(Block block) {
            if (block instanceof BlockRegistry.ISubBlocksBlock) {
                List<String> models = ((BlockRegistry.ISubBlocksBlock) block).getModels();
                if (block instanceof BlockDruidStone) {
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(0), "inventory"));
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 4, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(1), "inventory"));
                } else
                    for (int i = 0; i < models.size(); i++) {
                        if (ConfigHandler.debug && createJSONFile)
                            JsonRenderGenerator.createJSONForBlock(block, models.get(i));
                        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(i), "inventory"));
                    }
            } else {
                String name = block.getRegistryName().toString().replace("thebetweenlands:", "");
                if (ConfigHandler.debug && createJSONFile)
                    JsonRenderGenerator.createJSONForBlock(block, name);
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + name, "inventory"));
            }
        }
	 */
	@Override
	public void registerDefaultItemRenderer(Item item) {
		if (item instanceof ItemRegistry.ISubItemsItem) {
			Set<Entry<Integer, ResourceLocation>> models = ((ItemRegistry.ISubItemsItem) item).getModels().entrySet();
			Iterator<Entry<Integer, ResourceLocation>> modelsIT = ((ItemRegistry.ISubItemsItem) item).getModels().entrySet().iterator();
			while (modelsIT.hasNext()) {
				Entry<Integer, ResourceLocation> model = modelsIT.next();
				if (ConfigHandler.debug && createJSONFile)
					JsonRenderGenerator.createJSONForItem(item, model.getValue().getResourcePath());
				ModelLoader.setCustomModelResourceLocation(item, model.getKey(), new ModelResourceLocation(model.getValue(), "inventory"));
			}
		} else if (item instanceof ItemRegistry.ISingleJsonSubItems) {
			List<String> types = ((ItemRegistry.ISingleJsonSubItems) item).getTypes();
			for (int i = 0; i < types.size(); i++) {
				//if (ConfigHandler.debug && createJSONFile)
				//JsonRenderGenerator.createJSONForItem(item, types.get(i)); //TODO: Make this work. Tomorrow, (hopefully), so don't panic
				ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + item.getRegistryName().getResourcePath(), types.get(i)));
			}
		} else {
			String itemName = item.getRegistryName().toString().replace("thebetweenlands:", "");
			if (ConfigHandler.debug && createJSONFile)
				JsonRenderGenerator.createJSONForItem(item, itemName);
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
		}
	}


	//Probably will only be used while updating
	@Override
	public void changeFileNames() {
		File textures = new File(TheBetweenlands.sourceFile, "assets/thebetweenlands/sounds");
		if (textures.listFiles() != null)
			for (File file : textures.listFiles()) {
				if (file.getName().contains(".ogg")) {
					CharSequence sequence = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

					String text = file.getName();
					for (int i = 0; i < sequence.length(); i++) {
						text = text.replace("" + sequence.charAt(i), "_" + ("" + sequence.charAt(i)).toLowerCase());
					}
					File newFile = new File(file.getPath().replace(file.getName(), "") + text);
					System.out.println(file.renameTo(newFile));
				} else
					for (File file2 : file.listFiles()) {
						if (file2.getName().contains(".ogg")) {
							CharSequence sequence = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

							String text = file2.getName();
							for (int i = 0; i < sequence.length(); i++) {
								text = text.replace("" + sequence.charAt(i), "_" + ("" + sequence.charAt(i)).toLowerCase());
							}
							File newFile = new File(file2.getPath().replace(file2.getName(), "") + text);
							System.out.println(file2.renameTo(newFile));
						}
					}
			}
	}

	@Override
	public void preInit() {
		RenderingRegistry.registerEntityRenderingHandler(EntityAngler.class, RenderAngler::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBlindCaveFish.class, RenderBlindCaveFish::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMireSnail.class, RenderMireSnail::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMireSnailEgg.class, RenderMireSnailEgg::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBloodSnail.class, RenderBloodSnail::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySnailPoisonJet.class, RenderSnailPoisonJet::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySwampHag.class, RenderSwampHag::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityChiromaw.class, RenderChiromaw::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonFly.class, RenderDragonFly::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLurker.class, RenderLurker::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFrog.class, RenderFrog::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGiantToad.class, RenderGiantToad::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySporeling.class, RenderSporeling::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTermite.class, RenderTermite::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLeech.class, RenderLeech::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySwordEnergy.class, RenderSwordEnergy::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShockwaveBlock.class, RenderShockwaveBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGecko.class, RenderGecko::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWight.class, RenderWight::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShockwaveSwordItem.class, manager -> new RenderShockwaveSwordItem(manager, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFirefly.class, RenderFirefly::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGasCloud.class, RenderGasCloud::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySludge.class, RenderSludge::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBLArrow.class, RenderBLArrow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, RenderDarkDruid::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityVolatileSoul.class, RenderVolatileSoul::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTarBeast.class, RenderTarBeast::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySiltCrab.class, RenderSiltCrab::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPyrad.class, RenderPyrad::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPyradFlame.class, RenderPyradFlame::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityPeatMummy.class, RenderPeatMummy::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTarminion.class, RenderTarminion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownTarminion.class, RenderThrownTarminion::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRopeNode.class, RenderRopeNode::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMummyArm.class, RenderMummyArm::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAngryPebble.class, manager -> new RenderAngryPebble(manager, Minecraft.getMinecraft().getRenderItem()));

		IReloadableResourceManager resourceManager = ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager());
		resourceManager.registerReloadListener(ShaderHelper.INSTANCE);
		resourceManager.registerReloadListener(new FoodSickness.ResourceReloadListener());

		//Register particle stitchers
		BLParticles[] particles = BLParticles.values();
		for(BLParticles particle : particles) {
			ParticleTextureStitcher<?> stitcher = particle.getFactory().getStitcher();
			if(stitcher != null) {
				TextureStitchHandler.INSTANCE.registerTextureFrameSplitter(new TextureFrameSplitter((splitter) -> {
					stitcher.setFrames(splitter.getFrames());
				}, stitcher.getTextures()));
			}
		}
	}

	@Override
	public void init() {
		KeyBindRegistry.init();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void postInit() {
		dragonFlyRenderer = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityDragonFly.class);

		//Tile entities
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPurifier.class, new RenderPurifier());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new RenderDruidAltar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodWorkbench.class, new RenderWeedwoodWorkbench());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLootPot.class, new RenderLootPot());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMobSpawnerBetweenlands.class, new RenderSpawnerBetweenlands());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChestBetweenlands.class, new RenderChestBetweenlands());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpikeTrap.class, new RenderSpikeTrap());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPossessedBlock.class, new RenderPossessedBlock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemCage.class, new RenderItemCage());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodSign.class, new RenderWeedwoodSign());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMudFlowerPot.class, new RenderMudFlowerPot());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGeckoCage.class, new RenderGeckoCage());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWisp.class, new RenderWisp());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfuser.class, new RenderInfuser());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMortar.class, new RenderPestleAndMortar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAnimator.class, new RenderAnimator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlembic.class, new RenderAlembic());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCompostBin.class, new RenderCompostBin());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemShelf.class, new RenderItemShelf());
		
		//item models
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.DRUID_ALTAR), 0, TileEntityDruidAltar.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.COMPOST_BIN), 0, TileEntityCompostBin.class);
		//ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.DRUID_SPAWNER), 0, TileEntityDruidSpawner.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.PURIFIER), 0, TileEntityPurifier.class);
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_1.getMetadata(facing), TileEntityLootPot.class);
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_2.getMetadata(facing), TileEntityLootPot.class);
			ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.LOOT_POT), EnumLootPot.POT_3.getMetadata(facing), TileEntityLootPot.class);
		}
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.MOB_SPAWNER), 0, TileEntityMobSpawnerBetweenlands.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.SPIKE_TRAP), 0, TileEntitySpikeTrap.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.POSSESSED_BLOCK), 0, TileEntityPossessedBlock.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ITEM_CAGE), 0, TileEntityItemCage.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.GECKO_CAGE), 0, TileEntityGeckoCage.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.INFUSER), 0, TileEntityInfuser.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.MORTAR), 0, TileEntityMortar.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ANIMATOR), 0, TileEntityAnimator.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ALEMBIC), 0, TileEntityAlembic.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(BlockRegistry.ITEM_SHELF), 0, TileEntityItemShelf.class);

		//Block colors
		for (Block block : BlockRegistry.BLOCKS) {
			if (block instanceof ITintedBlock) {
				final ITintedBlock tintedBlock = (ITintedBlock) block;
				Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
					@Override
					public int getColorFromItemstack(ItemStack stack, int tintIndex) {
						IBlockState blockState = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
						return tintedBlock.getColorMultiplier(blockState, null, null, tintIndex);
					}
				}, block);
				Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
					@Override
					public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
						return tintedBlock.getColorMultiplier(state, worldIn, pos, tintIndex);
					}
				}, block);
			}
		}

		//Item colors
		for (Item item : ItemRegistry.ITEMS) {
			if (item instanceof ITintedItem) {
				final ITintedItem tintedItem = (ITintedItem) item;
				Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
					@Override
					public int getColorFromItemstack(ItemStack stack, int tintIndex) {
						return tintedItem.getColorMultiplier(stack, tintIndex);
					}
				}, item);
			}
		}

		pixelLove = new FontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("thebetweenlands:textures/gui/manual/font_atlas.png"), Minecraft.getMinecraft().renderEngine, false);
		if (Minecraft.getMinecraft().gameSettings.language != null) {
			pixelLove.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
		}
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(pixelLove);
		HLEntryRegistry.init();
	}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(TextureStitchHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ShaderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FogHandler.class);
		MinecraftForge.EVENT_BUS.register(AmbienceSoundPlayHandler.class);
		MinecraftForge.EVENT_BUS.register(GLUProjection.getInstance());
		MinecraftForge.EVENT_BUS.register(WorldRenderHandler.class);
		MinecraftForge.EVENT_BUS.register(ScreenRenderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(DecayRenderHandler.class);
		MinecraftForge.EVENT_BUS.register(CameraPositionHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(MusicHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ThemHandler.class);
		MinecraftForge.EVENT_BUS.register(RadialMenuHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ItemAmulet.class);
		MinecraftForge.EVENT_BUS.register(InputHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemLurkerSkinPouch.class);
		MinecraftForge.EVENT_BUS.register(BrightnessHandler.class);
		MinecraftForge.EVENT_BUS.register(DebugHandlerSharedLocation.class);
		MinecraftForge.EVENT_BUS.register(DruidAltarSoundHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemTooltipHandler.class);
		MinecraftForge.EVENT_BUS.register(GuiBLMainMenu.class);
	}

	@Override
	public void updateWispParticles(TileEntityWisp te) {
		Iterator<Object> it = te.particleList.iterator();
		while (it.hasNext()) {
			ParticleWisp wisp = (ParticleWisp) it.next();
			if (!wisp.isAlive()) {
				it.remove();
			} else {
				wisp.onUpdate();
			}
		}
	}

	private static FontRenderer pixelLove;

	@Override
	public FontRenderer getCustomFontRenderer() {
		return pixelLove;
	}
}
