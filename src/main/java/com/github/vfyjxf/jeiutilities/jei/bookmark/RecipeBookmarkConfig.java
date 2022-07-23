package com.github.vfyjxf.jeiutilities.jei.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import com.github.vfyjxf.jeiutilities.helper.RecipeHelper;
import com.github.vfyjxf.jeiutilities.jei.recipe.BasedRecipeInfo;
import com.github.vfyjxf.jeiutilities.jei.recipe.IRecipeInfo;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.common.util.ServerConfigPathUtil;
import mezz.jei.config.BookmarkConfig;
import mezz.jei.ingredients.IngredientInfo;
import mezz.jei.ingredients.RegisteredIngredients;
import mezz.jei.ingredients.TypedIngredient;
import mezz.jei.recipes.RecipeTypeData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.focusFactory;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientManager;

public class RecipeBookmarkConfig extends BookmarkConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MARKER_OTHER = "O:";
    public static final String MARKER_STACK = "T:";
    public static final String MARKER_RECIPE = "R:";
    private final File jeiConfigurationDir;

    public static BookmarkConfig create(File jeiConfigurationDir) {
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            return new RecipeBookmarkConfig(jeiConfigurationDir);
        } else {
            return new BookmarkConfig(jeiConfigurationDir);
        }
    }

    @Nullable
    private static File getFile(File jeiConfigurationDir) {
        Path configPath = ServerConfigPathUtil.getWorldPath(jeiConfigurationDir.toPath());
        if (configPath == null) {
            return null;
        }
        File configFolder = configPath.toFile();
        if (!configFolder.exists() && !configFolder.mkdirs()) {
            LOGGER.error("Unable to create bookmark config folder: {}", configFolder);
            return null;
        }
        return configPath.resolve("bookmarks.ini").toFile();
    }

    private static File getOldFile(File jeiConfigurationDir) {
        return Path.of(jeiConfigurationDir.getAbsolutePath(), "bookmarks.ini").toFile();
    }

    public RecipeBookmarkConfig(File jeiConfigurationDir) {
        super(jeiConfigurationDir);
        this.jeiConfigurationDir = jeiConfigurationDir;
    }

    public void saveBookmarks(RegisteredIngredients registeredIngredients, List<ITypedIngredient<?>> ingredientList) {
        File file = getFile(jeiConfigurationDir);
        if (file == null) {
            return;
        }

        List<String> strings = new ArrayList<>();
        for (ITypedIngredient<?> typedIngredient : ingredientList) {
            if (typedIngredient.getIngredient() instanceof ItemStack stack) {
                strings.add(MARKER_STACK + stack.save(new CompoundTag()));
            } else if (typedIngredient.getIngredient() instanceof IRecipeInfo recipeInfo) {
                strings.add(MARKER_RECIPE + recipeInfo.getUniqueId());
            } else {
                strings.add(MARKER_OTHER + getUid(registeredIngredients, typedIngredient));
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            IOUtils.writeLines(strings, "\n", writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save bookmarks list to file {}", file, e);
        }
    }

    public void loadBookmarks(RegisteredIngredients registeredIngredients, BookmarkList bookmarkList) {
        File file = getFile(jeiConfigurationDir);
        if (file == null) {
            return;
        } else if (!file.exists()) {
            File oldFile = getOldFile(jeiConfigurationDir);
            if (!oldFile.exists()) {
                return;
            }
            try {
                Files.copy(oldFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("Failed to copy old bookmarks {} to new location {}", oldFile, file, e);
                return;
            }
        }
        List<String> ingredientJsonStrings;
        try (FileReader reader = new FileReader(file)) {
            ingredientJsonStrings = IOUtils.readLines(reader);
        } catch (IOException e) {
            LOGGER.error("Failed to load bookmarks from file {}", file, e);
            return;
        }

        Collection<IIngredientType<?>> otherIngredientTypes = new ArrayList<>(registeredIngredients.getIngredientTypes());
        otherIngredientTypes.remove(VanillaTypes.ITEM_STACK);

        IIngredientHelper<ItemStack> itemStackHelper = registeredIngredients.getIngredientHelper(VanillaTypes.ITEM_STACK);

        for (String ingredientJsonString : ingredientJsonStrings) {
            if (ingredientJsonString.startsWith(MARKER_STACK)) {
                String itemStackAsJson = ingredientJsonString.substring(MARKER_STACK.length());
                ITypedIngredient<ItemStack> itemStack = loadItemStack(itemStackAsJson, itemStackHelper, registeredIngredients, true);
                if (itemStack != null) {
                    bookmarkList.addToList(itemStack, false);
                }
            } else if (ingredientJsonString.startsWith(MARKER_RECIPE)) {
                String recipeInfoAsJson = ingredientJsonString.substring(MARKER_RECIPE.length());
                Optional<ITypedIngredient<?>> recipeInfo = loadRecipeInfo(recipeInfoAsJson, itemStackHelper, otherIngredientTypes, registeredIngredients);
                if (recipeInfo.isEmpty()) {
                    LOGGER.error("Failed to load recipe info from string {}", recipeInfoAsJson);
                } else {
                    bookmarkList.addToList(recipeInfo.get(), false);
                }

            } else if (ingredientJsonString.startsWith(MARKER_OTHER)) {
                String uid = ingredientJsonString.substring(MARKER_OTHER.length());
                Optional<ITypedIngredient<?>> typedIngredient = getNormalizedIngredientByUid(registeredIngredients, otherIngredientTypes, uid);
                if (typedIngredient.isEmpty()) {
                    LOGGER.error("Failed to load unknown bookmarked ingredient:\n{}", ingredientJsonString);
                } else {
                    bookmarkList.addToList(typedIngredient.get(), false);
                }
            } else {
                LOGGER.error("Failed to load unknown bookmarked ingredient:\n{}", ingredientJsonString);
            }
        }
        bookmarkList.notifyListenersOfChange();
    }

    @SuppressWarnings("rawtypes")
    private static Optional<ITypedIngredient<?>> loadRecipeInfo(String recipeInfoAsJson, IIngredientHelper<ItemStack> itemStackHelper, Collection<IIngredientType<?>> ingredientTypes, RegisteredIngredients registeredIngredients) {
        JsonObject jsonObject;
        try {
            jsonObject = JsonParser.parseString(recipeInfoAsJson).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            LOGGER.error("Failed to parse recipe info json string {}", recipeInfoAsJson, e);
            return Optional.empty();
        }

        RecipeTypeData recipeTypeData = RecipeHelper.getRecipeTypeDataFromUid(new ResourceLocation(jsonObject.get("category").getAsString()));
        Object output = getIngredientByUid(jsonObject.get("output").getAsString(), itemStackHelper, ingredientTypes, registeredIngredients);
        Object focusValue = getIngredientByUid(jsonObject.get("focus").getAsString(), itemStackHelper, ingredientTypes, registeredIngredients);
        boolean isInput = jsonObject.get("isInput").getAsBoolean();
        List<RecipeIngredientRole> roles = isInput ? List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST) : List.of(RecipeIngredientRole.OUTPUT);
        List<? extends IFocus<?>> focuses = getFocuses(focusValue, roles);

        if (jsonObject.has("registerName")) {
            ResourceLocation registerName = new ResourceLocation(jsonObject.get("registerName").getAsString());
            Pair<?, Integer> recipePair = RecipeHelper.getRecipeAndIndexByName(recipeTypeData, registerName, focuses);
            if (recipePair != null) {
                IRecipeInfo recipeInfo = BasedRecipeInfo.create(
                        recipeTypeData.getRecipeCategory(),
                        recipePair.getLeft(),
                        output,
                        focusValue,
                        isInput,
                        recipePair.getRight()
                );
                return IngredientHelper.createTypedIngredient(recipeInfo, registeredIngredients);
            }
        }

        if (jsonObject.has("inputs")) {
            JsonArray inputsArray = jsonObject.get("inputs").getAsJsonArray();
            Map<IIngredientType<?>, List<String>> recipeUidMap = getRecipeUidMap(inputsArray, ingredientTypes, registeredIngredients);
            if (recipeUidMap.isEmpty()) {
                return Optional.empty();
            }
            Pair<?, Integer> recipePair = RecipeHelper.getRecipeAndIndexByInputs(recipeTypeData, focuses, recipeUidMap, output, registeredIngredients);
            if (recipePair != null) {
                IRecipeInfo recipeInfo = BasedRecipeInfo.create(
                        recipeTypeData.getRecipeCategory(),
                        recipePair.getLeft(),
                        output,
                        focusValue,
                        isInput,
                        recipePair.getRight()
                );
                return IngredientHelper.createTypedIngredient(recipeInfo, registeredIngredients);
            }

        }

        return Optional.empty();
    }

    private static ITypedIngredient<ItemStack> loadItemStack(String itemStackAsJson, IIngredientHelper<ItemStack> itemStackHelper, RegisteredIngredients registeredIngredients, boolean normalize) {
        try {
            CompoundTag itemStackAsNbt = TagParser.parseTag(itemStackAsJson);
            ItemStack itemStack = ItemStack.of(itemStackAsNbt);
            if (!itemStack.isEmpty()) {
                Optional<ITypedIngredient<ItemStack>> typedIngredient = TypedIngredient.createTyped(registeredIngredients, VanillaTypes.ITEM_STACK, normalize ? itemStackHelper.normalizeIngredient(itemStack) : itemStack);
                if (typedIngredient.isEmpty()) {
                    LOGGER.warn("Failed to load bookmarked ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
                } else {
                    return typedIngredient.get();
                }
            } else {
                LOGGER.warn("Failed to load bookmarked ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
            }
        } catch (CommandSyntaxException e) {
            LOGGER.error("Failed to load bookmarked ItemStack from json string:\n{}", itemStackAsJson, e);
        }
        return null;
    }

    private static <T> String getUid(RegisteredIngredients registeredIngredients, ITypedIngredient<T> typedIngredient) {
        IIngredientHelper<T> ingredientHelper = registeredIngredients.getIngredientHelper(typedIngredient.getType());
        return ingredientHelper.getUniqueId(typedIngredient.getIngredient(), UidContext.Ingredient);
    }

    private static List<RecipeIngredientRole> getRoles(JsonArray rolesArray) {
        List<RecipeIngredientRole> roles = new ArrayList<>();
        for (JsonElement element : rolesArray) {
            if (element.isJsonPrimitive()) {
                roles.add(RecipeIngredientRole.valueOf(element.getAsString()));
            }
        }
        return roles;
    }

    private static <V> List<? extends IFocus<V>> getFocuses(V focusValue, List<RecipeIngredientRole> roles) {
        IIngredientType<V> ingredientType = ingredientManager.getIngredientType(focusValue);
        return roles.stream()
                .map(role -> focusFactory.createFocus(role, ingredientType, focusValue))
                .collect(Collectors.toList());
    }

    private static Object getIngredientByUid(String ingredientUid, IIngredientHelper<ItemStack> itemStackHelper, Collection<IIngredientType<?>> ingredientTypes, RegisteredIngredients registeredIngredients) {
        if (ingredientUid.startsWith(MARKER_STACK)) {
            String itemStackAsJson = ingredientUid.substring(MARKER_STACK.length());
            ITypedIngredient<ItemStack> itemStack = loadItemStack(itemStackAsJson, itemStackHelper, registeredIngredients, false);
            if (itemStack != null) {
                return itemStack.getIngredient();
            }
        } else if (ingredientUid.startsWith(MARKER_OTHER)) {
            String uid = ingredientUid.substring(MARKER_OTHER.length());
            return getIngredientByUid(registeredIngredients, ingredientTypes, uid);
        }
        return null;
    }

    private static Optional<ITypedIngredient<?>> getNormalizedIngredientByUid(RegisteredIngredients registeredIngredients, Collection<IIngredientType<?>> ingredientTypes, String uid) {
        return ingredientTypes.stream()
                .map(t -> getNormalizedIngredientByUid(registeredIngredients, t, uid))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private static <T> Optional<ITypedIngredient<?>> getNormalizedIngredientByUid(RegisteredIngredients registeredIngredients, IIngredientType<T> ingredientType, String uid) {
        IngredientInfo<T> ingredientInfo = registeredIngredients.getIngredientInfo(ingredientType);
        T ingredient = ingredientInfo.getIngredientByUid(uid);
        return Optional.ofNullable(ingredient)
                .map(i -> {
                    IIngredientHelper<T> ingredientHelper = registeredIngredients.getIngredientHelper(ingredientType);
                    return ingredientHelper.normalizeIngredient(i);
                })
                .flatMap(i -> TypedIngredient.createTyped(registeredIngredients, ingredientType, i));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> Object getIngredientByUid(RegisteredIngredients registeredIngredients, Collection<IIngredientType<?>> ingredientTypes, String uid) {
        for (IIngredientType type : ingredientTypes) {
            IngredientInfo<T> ingredientInfo = registeredIngredients.getIngredientInfo(type);
            T ingredient = ingredientInfo.getIngredientByUid(uid);
            if (ingredient != null) {
                return ingredient;
            }
        }
        return null;
    }

    private static Map<IIngredientType<?>, List<String>> getRecipeUidMap(JsonArray inputsArray, Collection<IIngredientType<?>> ingredientTypes, RegisteredIngredients registeredIngredients) {
        HashMap<IIngredientType<?>, List<String>> recipeUidMap = new HashMap<>(inputsArray.size());
        for (JsonElement element : inputsArray) {
            if (element.isJsonArray()) {
                List<String> inputsUidListInner = new ArrayList<>();
                for (JsonElement elementInner : element.getAsJsonArray()) {
                    if (elementInner.isJsonPrimitive()) {
                        inputsUidListInner.add(elementInner.getAsString());
                    }
                }
                IIngredientType<?> ingredientType = getIngredientType(inputsUidListInner, ingredientTypes, registeredIngredients);
                if (ingredientType == null) {
                    LOGGER.error("Found unknown type of ingredients :\n{}", inputsUidListInner);
                    return Map.of();
                }
                recipeUidMap.put(ingredientType, inputsUidListInner);
            }
        }
        return recipeUidMap;
    }

    private static IIngredientType<?> getIngredientType(List<String> inputsUidListInner, Collection<IIngredientType<?>> ingredientTypes, RegisteredIngredients registeredIngredients) {
        for (String ingredientUid : inputsUidListInner) {
            //First check if the type corresponding to the uid is ItemStack.
            try {
                CompoundTag itemStackAsNbt = TagParser.parseTag(ingredientUid);
                ItemStack itemStack = ItemStack.of(itemStackAsNbt);
                if (!itemStack.isEmpty()) {
                    return VanillaTypes.ITEM_STACK;
                }
            } catch (CommandSyntaxException ignored) {
                // :P
            }

            for (IIngredientType<?> ingredientType : ingredientTypes) {
                IngredientInfo<?> ingredientInfo = registeredIngredients.getIngredientInfo(ingredientType);
                Object ingredient = ingredientInfo.getIngredientByUid(ingredientUid);
                if (ingredient != null) {
                    return ingredientType;
                }
            }
        }

        return null;
    }

}
