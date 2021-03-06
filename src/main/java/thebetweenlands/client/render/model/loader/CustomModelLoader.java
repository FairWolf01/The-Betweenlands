package thebetweenlands.client.render.model.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.client.render.model.loader.extension.AdvancedItemLoaderExtension;
import thebetweenlands.client.render.model.loader.extension.CustomDataLoaderExtension;
import thebetweenlands.client.render.model.loader.extension.LoaderExtension;
import thebetweenlands.client.render.model.loader.extension.LoaderExtensionException;
import thebetweenlands.client.render.model.loader.extension.SimpleItemLoaderExtension;

public final class CustomModelLoader implements ICustomModelLoader {
	private static enum LoaderType {
		NORMAL,
		EXTENSION,
		NONE
	}

	private static class LoaderResult {
		private final LoaderType type;
		private final LoaderExtension extension;
		private final String args;
		private final ResourceLocation actualLocation;

		private LoaderResult(LoaderType type, LoaderExtension extension, String args, ResourceLocation location) {
			this.type = type;
			this.extension = extension;
			this.args = args;
			if(location != null && location.getResourcePath().startsWith("models/")) {
				String path = location.getResourcePath();
				path = path.substring("models/".length());
				location = new ResourceLocation(location.getResourceDomain(), path);
			}
			this.actualLocation = location;
		}

		private LoaderResult(ResourceLocation location) {
			this(LoaderType.NORMAL, null, null, location);
		}

		private LoaderResult(ResourceLocation location, LoaderExtension extension, String args) {
			this(LoaderType.EXTENSION, extension, args, location);
		}
	}

	private final CustomModelManager manager;
	private final List<LoaderExtension> loaderExtensions = new ArrayList<LoaderExtension>();

	//Default loader extensions
	public static final LoaderExtension SIMPLE_ITEM_LOADER_EXTENSION = new SimpleItemLoaderExtension();
	public static final LoaderExtension ADVANCED_ITEM_LOADER_EXTENSION = new AdvancedItemLoaderExtension();
	public static final LoaderExtension CUSTOM_DATA_LOADER_EXTENSION = new CustomDataLoaderExtension();

	CustomModelLoader(CustomModelManager manager) {
		this.manager = manager;

		//Item model loader extensions
		this.registerExtension(SIMPLE_ITEM_LOADER_EXTENSION);
		this.registerExtension(ADVANCED_ITEM_LOADER_EXTENSION);
		this.registerExtension(CUSTOM_DATA_LOADER_EXTENSION);
	}

	/**
	 * Registers a loader extension
	 * @param extension
	 * @return
	 */
	public CustomModelLoader registerExtension(@Nonnull LoaderExtension extension) {
		Validate.notNull(extension);
		this.loaderExtensions.add(extension);
		return this;
	}

	/**
	 * Returns an unmodifiable list of all registered loader extensions
	 * @return
	 */
	public List<LoaderExtension> getExtensions() {
		return Collections.unmodifiableList(this.loaderExtensions);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		for(LoaderExtension extension : this.loaderExtensions) {
			if(extension instanceof IResourceManagerReloadListener)
				((IResourceManagerReloadListener)extension).onResourceManagerReload(resourceManager);
		}
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.manager.getRegisteredModelProviders().entrySet()) {
			if(this.getLoaderResult(entry.getKey(), modelLocation).type != LoaderType.NONE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		for(Entry<ResourceLocation, Function<ResourceLocation, IModel>> entry : this.manager.getRegisteredModelProviders().entrySet()) {
			LoaderResult match = this.getLoaderResult(entry.getKey(), modelLocation);
			if(match.type == LoaderType.NORMAL) {
				return entry.getValue().apply(match.actualLocation);
			} else if(match.type == LoaderType.EXTENSION) {
				LoaderExtension loaderExtension = match.extension;
				String loaderArgs = match.args;
				try {
					return loaderExtension.loadModel(entry.getValue().apply(match.actualLocation), match.actualLocation, loaderArgs);
				} catch(Exception ex) {
					if(ex instanceof LoaderExtensionException == false)
						this.throwLoaderException(loaderExtension, ex);
					else
						throw ex;
				}
			}
		}
		return null;
	}

	/**
	 * Compares two model locations and returns how they should be handled if they match
	 * @param registeredModel
	 * @param modelLocation
	 * @return
	 */
	private LoaderResult getLoaderResult(ResourceLocation registeredModel, ResourceLocation modelLocation) {
		//Not in the same domain, can't match
		if(!registeredModel.getResourceDomain().equals(modelLocation.getResourceDomain()))
			return new LoaderResult(LoaderType.NONE, null, null, null);

		String registeredPath = registeredModel.getResourcePath();
		String modelPath = modelLocation.getResourcePath();

		if(modelPath.startsWith(registeredPath)) {
			String suffix = modelPath.substring(registeredPath.length());

			//Find loader extension in suffix
			LoaderExtension loaderExtension = null;
			for(LoaderExtension arg : this.loaderExtensions) {
				String argPrefix = "$" + arg.getName() + "(";
				if(suffix.startsWith(argPrefix)) {
					loaderExtension = arg;
					break;
				}
			}

			//Find loader args in suffix
			String loaderArgs = null;
			if(loaderExtension != null) {
				suffix = suffix.substring(loaderExtension.getName().length() + 2);
				loaderArgs = suffix.substring(0, suffix.indexOf(")"));
				suffix = suffix.substring(loaderArgs.length() + 1);
				if(loaderArgs.length() == 0)
					loaderArgs = null;
			}

			//Only accept if path fully matches or is a variant
			if(suffix.length() == 0 || suffix.startsWith("#")) {
				ResourceLocation actualLocation = new ResourceLocation(modelLocation.getResourceDomain(), registeredPath + suffix);

				//Extension loader
				if(loaderExtension != null)
					return new LoaderResult(actualLocation, loaderExtension, loaderArgs);

				//Normal loader
				return new LoaderResult(actualLocation); 
			}
		}

		//No match
		return new LoaderResult(LoaderType.NONE, null, null, null);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		List<Pair<ModelResourceLocation, IBakedModel>> loadedModels = new ArrayList<Pair<ModelResourceLocation, IBakedModel>>();

		for(ModelResourceLocation modelLocation : modelRegistry.getKeys()) {
			IBakedModel model = modelRegistry.getObject(modelLocation);

			//Model depends on other baked models
			if(model instanceof IBakedModelDependant) {
				IBakedModelDependant dependant = (IBakedModelDependant) model;
				Collection<ModelResourceLocation> dependencies = dependant.getDependencies(modelLocation);
				Map<ModelResourceLocation, IBakedModel> loadedDependencies = new HashMap<ModelResourceLocation, IBakedModel>();

				for(ModelResourceLocation dependencyLocation : dependencies) {
					IBakedModel bakedModel = modelRegistry.getObject(dependencyLocation);

					if(bakedModel == null) {
						ResourceLocation dependencyLocationNoVariants = new ResourceLocation(dependencyLocation.getResourceDomain(), dependencyLocation.getResourcePath());
						try {
							IModel externalModel = ModelLoaderRegistry.getModel(dependencyLocationNoVariants);
							bakedModel = externalModel.bake(dependant.getModelState(externalModel), dependant.getVertexFormat(externalModel), dependant.getTextureGetter(externalModel));
							loadedModels.add(Pair.of(dependencyLocation, bakedModel));
						} catch (Exception ex) {
							throw new RuntimeException("Failed to load model dependency " + dependencyLocationNoVariants + " for model " + modelLocation, ex);
						}
					}

					loadedDependencies.put(dependencyLocation, bakedModel);
				}

				dependant.setDependencies(modelLocation, loadedDependencies);
			}
		}
		for(Pair<ModelResourceLocation, IBakedModel> loadedModel : loadedModels) {
			modelRegistry.putObject(loadedModel.getKey(), loadedModel.getValue());
		}

		//Replace loader extensions models
		Set<ModelResourceLocation> keys = modelRegistry.getKeys();
		Map<ModelResourceLocation, IBakedModel> replacementMap = new HashMap<>();

		//Get model replacements from extensions
		for(LoaderExtension extension : this.loaderExtensions) {
			for(ModelResourceLocation loc : keys) {
				try {
					IBakedModel replacement = extension.getModelReplacement(loc, modelRegistry.getObject(loc));
					if(replacement != null)
						replacementMap.put(loc, replacement);
				} catch(Exception ex) {
					if(ex instanceof LoaderExtensionException == false)
						this.throwLoaderException(extension, ex);
					else
						throw ex;
				}
			}
		}

		this.replaceRegistryObjects(modelRegistry, replacementMap);
	}

	/**
	 * Throws a {@link LoaderExtensionException}
	 * @param reason
	 * @param cause
	 */
	private void throwLoaderException(LoaderExtension extension, Throwable cause) {
		throw new LoaderExtensionException(String.format("Model loader extension %s failed loading a model", extension.getName()), cause);
	}

	/**
	 * Replaces the specified objects in the specified registry
	 * @param registry
	 * @param map
	 */
	private <K, T> void replaceRegistryObjects(IRegistry<K, T> registry, Map<K, T> map) {
		List<T> objectsToRemove = new ArrayList<T>(map.size());
		Set<K> replacementKeys = map.keySet();

		//Gather registered objects
		for(K replacementKey : replacementKeys) {
			T obj = registry.getObject(replacementKey);
			if(obj != null)
				objectsToRemove.add(obj);
		}

		//Remove registered objects
		Iterator<T> it = registry.iterator();
		T obj = null;
		while(it.hasNext()) {
			obj = it.next();
			if(objectsToRemove.contains(obj))
				it.remove();
		}

		//Add replacement objects
		for(Entry<K, T> replacement : map.entrySet()) {
			registry.putObject(replacement.getKey(), replacement.getValue());
		}
	}
}