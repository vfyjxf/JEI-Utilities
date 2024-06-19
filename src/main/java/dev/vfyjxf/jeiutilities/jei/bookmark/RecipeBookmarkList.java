package dev.vfyjxf.jeiutilities.jei.bookmark;

import dev.vfyjxf.jeiutilities.JeiUtilities;
import dev.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import dev.vfyjxf.jeiutilities.helper.IngredientHelper;
import dev.vfyjxf.jeiutilities.helper.RecipeHelper;
import dev.vfyjxf.jeiutilities.helper.ReflectionUtils;
import dev.vfyjxf.jeiutilities.jei.ingredient.CraftingRecipeInfo;
import dev.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.config.Config;
import mezz.jei.gui.Focus;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.overlay.IIngredientGridSource;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unused"})
public class RecipeBookmarkList extends BookmarkList {

    public static final String MARKER_OTHER = "O:";
    public static final String MARKER_STACK = "T:";
    public static final String MARKER_RECIPE = "R:";

    private final List<Object> list;
    private final List<IIngredientGridSource.Listener> listeners;

    public static BookmarkList create(IngredientRegistry ingredientRegistry) {
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            return new RecipeBookmarkList(ingredientRegistry);
        } else {
            return new BookmarkList(ingredientRegistry);
        }
    }

    public RecipeBookmarkList(IngredientRegistry ingredientRegistry) {
        super(ingredientRegistry);
        list = ReflectionUtils.getFieldValue(BookmarkList.class, this, "list");
        listeners = ReflectionUtils.getFieldValue(BookmarkList.class, this, "listeners");
    }

    @Override
    public void saveBookmarks() {
        List<String> strings = new ArrayList<>();
        for (IIngredientListElement<?> element : this.getIngredientList()) {
            Object object = element.getIngredient();
            if (object instanceof ItemStack) {
                strings.add(MARKER_STACK + ((ItemStack) object).writeToNBT(new NBTTagCompound()));
            } else if (object instanceof RecipeInfo) {
                strings.add(MARKER_RECIPE + object);
            } else {
                strings.add(MARKER_OTHER + getUid(element));
            }
        }
        File file = Config.getBookmarkFile();
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                IOUtils.writeLines(strings, "\n", writer);
            } catch (IOException e) {
                Log.get().error("Failed to save bookmarks list to file {}", file, e);
            }
        }
    }

    @Override
    public void loadBookmarks() {
        File file = Config.getBookmarkFile();
        if (file == null || !file.exists()) {
            return;
        }
        List<String> ingredientJsonStrings;
        try (FileReader reader = new FileReader(file)) {
            ingredientJsonStrings = IOUtils.readLines(reader);
        } catch (IOException e) {
            Log.get().error("Failed to load bookmarks from file {}", file, e);
            return;
        }
        Collection<IIngredientType> otherIngredientTypes = new ArrayList<>(JeiUtilitiesPlugin.ingredientRegistry.getRegisteredIngredientTypes());
        otherIngredientTypes.remove(VanillaTypes.ITEM);
        list.clear();
        getIngredientList().clear();

        for (String ingredientJsonString : ingredientJsonStrings) {
            if (ingredientJsonString.startsWith(MARKER_STACK)) {
                String itemStackAsJson = ingredientJsonString.substring(MARKER_STACK.length());
                try {
                    NBTTagCompound itemStackAsNbt = JsonToNBT.getTagFromJson(itemStackAsJson);
                    ItemStack itemStack = new ItemStack(itemStackAsNbt);
                    if (!itemStack.isEmpty()) {
                        ItemStack normalized = normalize(itemStack);
                        addToLists(normalized);
                    } else {
                        Log.get().warn("Failed to load bookmarked ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
                    }
                } catch (NBTException e) {
                    Log.get().error("Failed to load bookmarked ItemStack from json string:\n{}", itemStackAsJson, e);
                }
            } else if (ingredientJsonString.startsWith(MARKER_RECIPE)) {
                String recipeInfoAsJson = ingredientJsonString.substring(MARKER_RECIPE.length());
                RecipeInfo<?, ?> recipeInfo = loadInfoFromJson(recipeInfoAsJson, otherIngredientTypes);
                if (recipeInfo != null) {
                    addToLists(recipeInfo);
                }
            } else if (ingredientJsonString.startsWith(MARKER_OTHER)) {
                String uid = ingredientJsonString.substring(MARKER_OTHER.length());
                Object ingredient = getUnknownIngredientByUid(otherIngredientTypes, uid);
                if (ingredient != null) {
                    Object normalized = normalize(ingredient);
                    addToLists(normalized);
                }
            } else {
                Log.get().error("Failed to load unknown bookmarked ingredient:\n{}", ingredientJsonString);
            }
        }

        for (IIngredientGridSource.Listener listener : listeners) {
            listener.onChange();
        }
    }

    private <T> void addToLists(T ingredient) {
        IIngredientType<T> ingredientType = JeiUtilitiesPlugin.ingredientRegistry.getIngredientType(ingredient);
        IIngredientListElement<T> element = IngredientListElementFactory.createUnorderedElement(JeiUtilitiesPlugin.ingredientRegistry, ingredientType, ingredient, ForgeModIdHelper.getInstance());
        if (element != null) {
            list.add(ingredient);
            getIngredientList().add(element);
        }
    }

    private static <T> String getUid(IIngredientListElement<T> element) {
        IIngredientHelper<T> ingredientHelper = element.getIngredientHelper();
        return ingredientHelper.getUniqueId(element.getIngredient());
    }

    private RecipeInfo loadInfoFromJson(String recipeInfoString, Collection<IIngredientType> otherIngredientTypes) {
        JsonObject jsonObject;
        try {
            jsonObject = new JsonParser().parse(recipeInfoString).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            JeiUtilities.logger.error("Failed to parse recipe info string {}", recipeInfoString, e);
            return null;
        }
        String ingredientUid = jsonObject.get("ingredient").getAsString();
        String resultUid = jsonObject.get("result").getAsString();
        String recipeCategoryUid = jsonObject.get("recipeCategoryUid").getAsString();

        Object ingredientObject = loadIngredientFromJson(ingredientUid, otherIngredientTypes);
        Object resultObject = loadIngredientFromJson(resultUid, otherIngredientTypes);

        if (ingredientObject == null || resultObject == null) {
            JeiUtilities.logger.error("Failed to load recipe info from json string:\n{}", recipeInfoString);
            return null;
        }

        boolean isInputMode = jsonObject.get("isInputMode").getAsBoolean();
        IFocus.Mode mode = isInputMode ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;

        if (jsonObject.has("registryName")) {
            if (ingredientObject instanceof ItemStack && resultObject instanceof ItemStack) {

                ResourceLocation registryName = new ResourceLocation(jsonObject.get("registryName").getAsString());
                Pair<ICraftingRecipeWrapper, Integer> recipePair = RecipeHelper.getCraftingRecipeWrapperAndIndex(registryName, recipeCategoryUid, resultObject, new Focus<>(mode, ingredientObject));

                if (recipePair == null) {
                    JeiUtilities.logger.error("Failed to find the corresponding recipe, found the invalid recipe record :\n{}", recipeInfoString);
                    return null;
                }

                return new CraftingRecipeInfo((ItemStack) ingredientObject,
                        (ItemStack) resultObject,
                        recipeCategoryUid,
                        recipePair.getRight(),
                        isInputMode,
                        recipePair.getLeft(),
                        registryName
                );

            }
        }

        if (jsonObject.has("inputs")) {
            JsonArray inputsArray = jsonObject.get("inputs").getAsJsonArray();
            Map<IIngredientType, List<String>> recipeUidMap = getRecipeUidMap(inputsArray);

            Pair<IRecipeWrapper, Integer> recipePair = RecipeHelper.getRecipeWrapperAndIndex(recipeUidMap, recipeCategoryUid, resultObject, new Focus<>(mode, ingredientObject));

            if (recipePair == null) {
                JeiUtilities.logger.error("Failed to find the corresponding recipe, found the invalid recipe record :\n{}", recipeInfoString);
                return null;
            }

            return new RecipeInfo<>(ingredientObject,
                    resultObject,
                    recipeCategoryUid,
                    recipePair.getRight(),
                    isInputMode,
                    recipePair.getLeft()
            );
        }

        return null;
    }

    private Object loadIngredientFromJson(String ingredientString, Collection<IIngredientType> otherIngredientTypes) {
        if (ingredientString.startsWith(MARKER_STACK)) {
            String itemStackAsJson = ingredientString.substring(MARKER_STACK.length());
            try {
                NBTTagCompound itemStackAsNbt = JsonToNBT.getTagFromJson(itemStackAsJson);
                ItemStack itemStack = new ItemStack(itemStackAsNbt);
                if (!itemStack.isEmpty()) {
                    return IngredientHelper.getNormalize(itemStack);
                } else {
                    JeiUtilities.logger.warn("Failed to load recipe info ItemStack from json string, the item no longer exists:\n{}", itemStackAsJson);
                }
            } catch (NBTException e) {
                JeiUtilities.logger.error("Failed to load bookmarked ItemStack from json string:\n{}", itemStackAsJson, e);
            }
        } else if (ingredientString.startsWith(MARKER_OTHER)) {
            String uid = ingredientString.substring(MARKER_OTHER.length());
            Object ingredient = getUnknownIngredientByUid(otherIngredientTypes, uid);
            if (ingredient != null) {
                return IngredientHelper.getNormalize(ingredient);
            }
        } else {
            JeiUtilities.logger.error("Failed to load unknown recipe info:\n{}", ingredientString);
        }
        return null;
    }

    private Map<IIngredientType, List<String>> getRecipeUidMap(JsonArray inputsArray) {
        HashMap<IIngredientType, List<String>> recipeUidMap = new HashMap<>(inputsArray.size());
        for (JsonElement element : inputsArray) {
            if (element.isJsonArray()) {
                List<String> inputsUidListInner = new ArrayList<>();
                for (JsonElement elementInner : element.getAsJsonArray()) {
                    if (elementInner.isJsonPrimitive()) {
                        inputsUidListInner.add(elementInner.getAsString());
                    }
                }
                recipeUidMap.put(IngredientHelper.getIngredientType(inputsUidListInner), inputsUidListInner);
            }
        }

        return recipeUidMap;
    }

    @Nullable
    @SuppressWarnings("rawtypes")
    private Object getUnknownIngredientByUid(Collection<IIngredientType> ingredientTypes, String uid) {
        for (IIngredientType<?> ingredientType : ingredientTypes) {
            Object ingredient = JeiUtilitiesPlugin.ingredientRegistry.getIngredientByUid(ingredientType, uid);
            if (ingredient != null) {
                return ingredient;
            }
        }
        return null;
    }

}

