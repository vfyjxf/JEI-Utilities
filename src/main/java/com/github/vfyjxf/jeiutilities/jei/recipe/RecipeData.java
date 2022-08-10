package com.github.vfyjxf.jeiutilities.jei.recipe;

import com.github.vfyjxf.jeiutilities.gui.filter.FilterConfig;
import com.google.common.collect.ImmutableSet;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.util.Translator;
import mezz.jei.ingredients.IIngredientSupplier;
import mezz.jei.ingredients.IngredientInformationUtil;
import mezz.jei.ingredients.RegisteredIngredients;
import mezz.jei.recipes.IngredientSupplierHelper;
import mezz.jei.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeData<R> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final R recipe;
    private final RegisteredIngredients registeredIngredients;
    @NotNull
    private final IIngredientSupplier ingredientSupplier;
    private final IModIdHelper modIdHelper;
    private final FilterConfig filterConfig;
    private final Map<RecipeIngredientRole, List<RecipeIngredientInfo<?>>> recipeIngredients = new HashMap<>();
    private final Map<RecipeIngredientRole, Set<String>> allModIds = new HashMap<>();
    private final Map<RecipeIngredientRole, Set<String>> allModNames = new HashMap<>();
    private final Map<RecipeIngredientRole, Set<List<String>>> allTooltipStrings = new HashMap<>();
    private final Map<RecipeIngredientRole, Set<String>> allTagStrings = new HashMap<>();
    private final Map<RecipeIngredientRole, Set<String>> allCreativeTabStrings = new HashMap<>();
    private final Map<RecipeIngredientRole, Set<ResourceLocation>> allResourceLocations = new HashMap<>();

    private Set<String> allIngredientNamesLowercase;
    private Set<String> allOutputNamesLowercase;

    public RecipeData(
            @NotNull R recipe,
            @NotNull IRecipeCategory<R> recipeCategory,
            RegisteredIngredients registeredIngredients,
            IModIdHelper modIdHelper,
            FilterConfig filterConfig
    ) {
        IIngredientSupplier supplier = IngredientSupplierHelper.getIngredientSupplier(recipe, recipeCategory, registeredIngredients);
        ErrorUtil.checkNotNull(supplier, "supplier");
        this.recipe = recipe;
        this.registeredIngredients = registeredIngredients;
        this.ingredientSupplier = supplier;
        this.modIdHelper = modIdHelper;
        this.filterConfig = filterConfig;
    }

    public Set<String> getRecipeIngredientNames() {
        return filterConfig.isSearchInput() ? getAllIngredientNames() : getAllOutputNames();
    }

    private Set<String> getAllIngredientNames() {
        if (this.allIngredientNamesLowercase == null) {
            this.allIngredientNamesLowercase = getIngredientsNames(RecipeIngredientRole.INPUT);
        }
        return this.allIngredientNamesLowercase;
    }

    private Set<String> getAllOutputNames() {
        if (this.allOutputNamesLowercase == null) {
            this.allOutputNamesLowercase = getIngredientsNames(RecipeIngredientRole.OUTPUT);
        }
        return this.allOutputNamesLowercase;
    }

    public <T> Set<String> getAllModIds() {
        RecipeIngredientRole role = getSearchRole();
        Set<String> modIds = this.allModIds.get(role);
        if (modIds == null) {
            modIds = getIngredientInfos(role)
                    .stream()
                    .map(info -> {
                        //noinspection unchecked
                        RecipeIngredientInfo<T> ingredientInfo = (RecipeIngredientInfo<T>) info;
                        return ingredientInfo.ingredients.parallelStream()
                                .map(ingredient -> getModId(ingredient, ingredientInfo.ingredientHelper))
                                .flatMap(Set::stream)
                                .collect(Collectors.toSet());
                    })
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            this.allModIds.put(role, modIds);
        }
        return modIds;
    }

    public Set<String> getAllModNames() {
        RecipeIngredientRole role = getSearchRole();
        Set<String> modNames = this.allModNames.get(role);
        if (modNames == null) {
            modNames = getAllModIds().stream()
                    .map(modIdHelper::getModNameForModId)
                    .collect(Collectors.toSet());
            this.allModNames.put(role, modNames);
        }
        return modNames;
    }

    public <T> Set<List<String>> getAllTooltipStrings() {
        RecipeIngredientRole role = getSearchRole();
        Set<List<String>> tooltipStrings = this.allTooltipStrings.get(role);
        if (tooltipStrings == null) {
            tooltipStrings = getIngredientInfos(role)
                    .stream()
                    .map(info -> {
                        //noinspection unchecked
                        RecipeIngredientInfo<T> ingredientInfo = (RecipeIngredientInfo<T>) info;
                        return ingredientInfo.ingredients.parallelStream()
                                .map(ingredient -> getTooltipString(ingredient, ingredientInfo.ingredientHelper, ingredientInfo.ingredientRenderer))
                                .collect(Collectors.toSet());
                    })
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            this.allTooltipStrings.put(role, tooltipStrings);
        }
        return tooltipStrings;
    }

    public <T> Set<String> getAllTagStrings() {
        RecipeIngredientRole role = getSearchRole();
        Set<String> tagStrings = this.allTagStrings.get(role);
        if (tagStrings == null) {
            tagStrings = getIngredientInfos(role)
                    .stream()
                    .map(info -> {
                        //noinspection unchecked
                        RecipeIngredientInfo<T> ingredientInfo = (RecipeIngredientInfo<T>) info;
                        return ingredientInfo.ingredients.parallelStream()
                                .map(ingredient -> getTagString(ingredient, ingredientInfo.ingredientHelper))
                                .flatMap(Set::stream)
                                .collect(Collectors.toSet());
                    })
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            this.allTagStrings.put(role, tagStrings);
        }
        return tagStrings;
    }

    public <T> Set<String> getAllCreativeTabStrings() {
        RecipeIngredientRole role = getSearchRole();
        Set<String> creativeTabStrings = this.allCreativeTabStrings.get(role);
        if (creativeTabStrings == null) {
            creativeTabStrings = getIngredientInfos(role)
                    .stream()
                    .map(info -> {
                        //noinspection unchecked
                        RecipeIngredientInfo<T> ingredientInfo = (RecipeIngredientInfo<T>) info;
                        return ingredientInfo.ingredients.parallelStream()
                                .map(ingredient -> getCreativeTabString(ingredient, ingredientInfo.ingredientHelper))
                                .flatMap(Set::stream)
                                .collect(Collectors.toSet());
                    })
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            this.allCreativeTabStrings.put(role, creativeTabStrings);
        }
        return creativeTabStrings;
    }

    public <T> Set<ResourceLocation> getAllResourceLocations() {
        RecipeIngredientRole role = getSearchRole();
        Set<ResourceLocation> resourceLocations = this.allResourceLocations.get(role);
        if (resourceLocations == null) {
            resourceLocations = getIngredientInfos(role).stream()
                    .map(info -> {
                        //noinspection unchecked
                        RecipeIngredientInfo<T> ingredientInfo = (RecipeIngredientInfo<T>) info;
                        return ingredientInfo.ingredients.parallelStream()
                                .map(ingredientInfo.ingredientHelper::getResourceLocation)
                                .collect(Collectors.toSet());
                    })
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            this.allResourceLocations.put(role, resourceLocations);
        }
        return resourceLocations;
    }

    private <T> Set<String> getIngredientsNames(RecipeIngredientRole role) {
        return this.getIngredientInfos(role).stream()
                .map(info -> {
                    //noinspection unchecked
                    RecipeIngredientInfo<T> ingredientInfo = (RecipeIngredientInfo<T>) info;
                    return ingredientInfo.ingredients.parallelStream()
                            .map(ingredient -> getIngredientLowercaseName(ingredient, ingredientInfo.ingredientHelper))
                            .collect(Collectors.toSet());
                })
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public <T> List<RecipeIngredientInfo<?>> getIngredientInfos(RecipeIngredientRole role) {
        List<RecipeIngredientInfo<?>> ingredientInfos = this.recipeIngredients.get(role);
        if (ingredientInfos == null) {
            ingredientInfos = this.ingredientSupplier.getIngredientTypes(role)
                    .map(type -> {
                        //noinspection unchecked
                        IIngredientType<T> ingredientType = (IIngredientType<T>) type;
                        IIngredientHelper<T> helper = registeredIngredients.getIngredientHelper(ingredientType);
                        IIngredientRenderer<T> renderer = registeredIngredients.getIngredientRenderer(ingredientType);
                        Set<T> ingredients = ingredientSupplier.getIngredientStream(ingredientType, role)
                                .parallel()
                                .collect(Collectors.toSet());
                        return new RecipeIngredientInfo<>(ingredients, helper, renderer);
                    })
                    .collect(Collectors.toList());
            this.recipeIngredients.put(role, ingredientInfos);
        }
        return ingredientInfos;
    }

    public R getRecipe() {
        return recipe;
    }

    public Set<?> getIngredients(RecipeIngredientRole role) {
        List<RecipeIngredientInfo<?>> ingredients = this.recipeIngredients.get(role);
        if (ingredients == null) {
            ingredients = getIngredientInfos(role);
        }
        return ingredients.stream()
                .map(RecipeIngredientInfo::ingredients)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private <T> String getIngredientLowercaseName(T ingredient, IIngredientHelper<T> ingredientHelper) {
        String displayName = IngredientInformationUtil.getDisplayName(ingredient, ingredientHelper);
        return Translator.toLowercaseWithLocale(displayName);
    }

    private <T> Set<String> getModId(@NotNull T ingredient, @NotNull IIngredientHelper<T> ingredientHelper) {
        String displayModId = ingredientHelper.getDisplayModId(ingredient);
        String modId = ingredientHelper.getResourceLocation(ingredient).getNamespace();
        return Set.of(displayModId, modId);
    }

    private <T> List<String> getTooltipString(@NotNull T ingredient, IIngredientHelper<T> ingredientHelper, @NotNull IIngredientRenderer<T> ingredientRenderer) {
        String modId = ingredientHelper.getDisplayModId(ingredient);
        String modNameLowercase = this.modIdHelper.getModNameForModId(modId).toLowerCase(Locale.ENGLISH);
        String displayNameLowercase = getIngredientLowercaseName(ingredient, ingredientHelper);
        ResourceLocation resourceLocation = ingredientHelper.getResourceLocation(ingredient);
        ImmutableSet<String> toRemove = ImmutableSet.of(modId, modNameLowercase, displayNameLowercase, resourceLocation.getPath());
        TooltipFlag.Default tooltipFlag = filterConfig.isSearchAdvancedTooltips() ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        List<Component> tooltip = ingredientRenderer.getTooltip(ingredient, tooltipFlag);
        return tooltip.stream()
                .map(Component::getString)
                .map(RecipeData::removeChatFormatting)
                .map(Translator::toLowercaseWithLocale)
                .map(line -> {
                    for (String excludeWord : toRemove) {
                        line = line.replace(excludeWord, "");
                    }
                    return line;
                })
                .filter(line -> !StringUtil.isNullOrEmpty(line))
                .toList();
    }

    private <T> Set<String> getTagString(@NotNull T ingredient, IIngredientHelper<T> ingredientHelper) {
        Collection<ResourceLocation> tags = ingredientHelper.getTags(ingredient);
        return tags.stream()
                .map(ResourceLocation::getPath)
                .collect(Collectors.toSet());
    }

    private <T> Set<String> getCreativeTabString(@NotNull T ingredient, IIngredientHelper<T> ingredientHelper) {
        Collection<String> creativeTabsStrings = ingredientHelper.getCreativeTabNames(ingredient);
        return creativeTabsStrings.stream()
                .map(Translator::toLowercaseWithLocale)
                .collect(Collectors.toSet());
    }

    private RecipeIngredientRole getSearchRole() {
        return this.filterConfig.isSearchInput() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
    }

    private static String removeChatFormatting(String string) {
        String withoutFormattingCodes = ChatFormatting.stripFormatting(string);
        return (withoutFormattingCodes == null) ? "" : withoutFormattingCodes;
    }

    public record RecipeIngredientInfo<T>(Set<T> ingredients, IIngredientHelper<T> ingredientHelper, IIngredientRenderer<T> ingredientRenderer) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeData<?> that = (RecipeData<?>) o;

        return recipe.equals(that.recipe);
    }

    @Override
    public int hashCode() {
        return recipe.hashCode();
    }
}
