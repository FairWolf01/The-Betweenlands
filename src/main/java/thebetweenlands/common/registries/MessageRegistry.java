package thebetweenlands.common.registries;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.network.clientbound.MessageDruidAltarProgress;
import thebetweenlands.common.network.clientbound.MessageDruidTeleportParticles;
import thebetweenlands.common.network.clientbound.MessageGemProc;
import thebetweenlands.common.network.clientbound.MessageMireSnailEggHatching;
import thebetweenlands.common.network.clientbound.MessageRemoveSharedStorage;
import thebetweenlands.common.network.clientbound.MessageSyncChunkData;
import thebetweenlands.common.network.clientbound.MessageSyncEntityCapabilities;
import thebetweenlands.common.network.clientbound.MessageSyncEnvironmentEvent;
import thebetweenlands.common.network.clientbound.MessageSyncSharedStorage;
import thebetweenlands.common.network.clientbound.MessageSyncStaticAspects;
import thebetweenlands.common.network.clientbound.MessageWeedwoodBushRustle;
import thebetweenlands.common.network.clientbound.MessageWightVolatileParticles;
import thebetweenlands.common.network.serverbound.MessageEquipItem;
import thebetweenlands.common.network.serverbound.MessageFlightState;
import thebetweenlands.common.network.serverbound.MessageOpenPouch;
import thebetweenlands.common.network.serverbound.MessagePouchNaming;
import thebetweenlands.common.network.serverbound.MessageUpdatePuppeteerState;
import thebetweenlands.common.network.serverbound.MessageUpdateSummoningState;

public class MessageRegistry {
	private MessageRegistry() { }

	private static byte nextMessageId = 0;

	public static void preInit() {
		registerMessage(MessageDruidAltarProgress.class, Side.CLIENT);
		registerMessage(MessageSyncChunkData.class, Side.CLIENT);
		registerMessage(MessageSyncEnvironmentEvent.class, Side.CLIENT);
		registerMessage(MessageWeedwoodBushRustle.class, Side.CLIENT);
		registerMessage(MessageSyncEntityCapabilities.class, Side.CLIENT);
		registerMessage(MessageSyncStaticAspects.class, Side.CLIENT);
		registerMessage(MessageSyncSharedStorage.class, Side.CLIENT);
		registerMessage(MessageRemoveSharedStorage.class, Side.CLIENT);
		registerMessage(MessageDruidTeleportParticles.class, Side.CLIENT);
		registerMessage(MessageWightVolatileParticles.class, Side.CLIENT);
		registerMessage(MessageGemProc.class, Side.CLIENT);
		registerMessage(MessageMireSnailEggHatching.class, Side.CLIENT);

		registerMessage(MessageEquipItem.class, Side.SERVER);
		registerMessage(MessageOpenPouch.class, Side.SERVER);
		registerMessage(MessagePouchNaming.class, Side.SERVER);
		registerMessage(MessageFlightState.class, Side.SERVER);
		registerMessage(MessageUpdatePuppeteerState.class, Side.SERVER);
		registerMessage(MessageUpdateSummoningState.class, Side.SERVER);
	}

	private static void registerMessage(Class<? extends MessageBase> messageType, Side toSide) {
		TheBetweenlands.networkWrapper.registerMessage(getHandler(messageType, toSide), messageType, MessageRegistry.nextMessageId++, toSide);
	}

	private static IMessageHandler<MessageBase, IMessage> getHandler(Class<? extends MessageBase> messageType, Side toSide) {
		if (toSide == Side.CLIENT) {
			return new ClientboundHandler();
		}
		return new ServerboundHandler();
	}

	private static class ServerboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			try {
				server.callFromMainThread(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}

	private static class ClientboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			Minecraft mc = FMLClientHandler.instance().getClient();
			try {
				mc.addScheduledTask(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}
}
