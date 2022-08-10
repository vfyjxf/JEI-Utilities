package com.github.vfyjxf.jeiutilities.search;

import com.github.vfyjxf.jeiutilities.search.filter.IIngredientFilter;
import com.github.vfyjxf.jeiutilities.search.filter.IngredientFilters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.vfyjxf.jeiutilities.search.filter.IngredientFilters.NO_PREFIX_FILTER;

public class FilterTextParser {

    private static final Pattern QUOTE_PATTERN = Pattern.compile("\"");
    private static final Pattern FILTER_SPLIT_PATTERN = Pattern.compile("(-?\".*?(?:\"|$)|\\S+)");

    private final IngredientFilters ingredientFilters;

    public FilterTextParser(IngredientFilters ingredientFilters) {
        this.ingredientFilters = ingredientFilters;
    }

    public List<MatchTokens> parseFilterText(String filterText) {
        String[] filters = filterText.split("\\|");
        return Arrays.stream(filters)
                .map(this::parseMatchTokes)
                .filter(token -> !token.toMatch.isEmpty())
                .toList();
    }

    private MatchTokens parseMatchTokes(String filterText) {
        MatchTokens matchTokens = new MatchTokens(new ArrayList<>(), new ArrayList<>());
        if (filterText.isEmpty()) {
            return matchTokens;
        }
        Matcher filterMatcher = FILTER_SPLIT_PATTERN.matcher(filterText);
        while (filterMatcher.find()) {
            String string = filterMatcher.group(1);
            final boolean remove = string.startsWith("-");
            if (remove) {
                string = string.substring(1);
            }
            string = QUOTE_PATTERN.matcher(string).replaceAll("");
            if (string.isEmpty()) {
                continue;
            }
            parseFilterPrefix(string).ifPresent(result -> {
                        if (remove) {
                            matchTokens.toRemove.add(result);
                        } else {
                            matchTokens.toMatch.add(result);
                        }
                    }
            );
        }
        return matchTokens;
    }

    private Optional<TokenData> parseFilterPrefix(String token) {
        if (token.isEmpty()) {
            return Optional.empty();
        }
        char firstChar = token.charAt(0);
        IIngredientFilter ingredientFilter = ingredientFilters.getFilter(firstChar);
        if (ingredientFilter == null) {
            return Optional.of(new TokenData(token, NO_PREFIX_FILTER));
        }
        if (token.length() == 1) {
            return Optional.empty();
        }
        return Optional.of(new TokenData(token.substring(1), ingredientFilter));
    }

    public record MatchTokens(List<TokenData> toMatch, List<TokenData> toRemove) {
    }

    public record TokenData(String token, IIngredientFilter ingredientFilter) {
    }

}
