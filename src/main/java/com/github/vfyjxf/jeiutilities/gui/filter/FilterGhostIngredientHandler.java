package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FilterGhostIngredientHandler implements IGhostIngredientHandler<RecipesFilterScreen> {
    @Override
    public <I> @NotNull List<Target<I>> getTargets(@NotNull RecipesFilterScreen gui, @NotNull I ingredient, boolean doStart) {
        return List.of(new FocusValueTarget<>(gui.getFocusValueSlot()));
    }

    @Override
    public void onComplete() {

    }

    private record FocusValueTarget<I>(@NotNull FocusValueSlot slot) implements Target<I> {

            private FocusValueTarget(@NotNull FocusValueSlot slot) {
                this.slot = slot;
            }

            @Override
            public @NotNull Rect2i getArea() {
                ImmutableRect2i contentArea = slot.getContentArea();
                return new Rect2i(contentArea.getX(), contentArea.getY(), contentArea.getWidth(), contentArea.getHeight());
            }

            @Override
            public void accept(@NotNull Object ingredient) {
                ITypedIngredient<?> typedIngredient = IngredientHelper.createTypedIngredient(ingredient);
                if (typedIngredient != null) {
                    slot.setFocusValue(typedIngredient);
                }
            }
        }


}
