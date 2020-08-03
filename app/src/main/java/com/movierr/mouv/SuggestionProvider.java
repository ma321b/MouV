package com.movierr.mouv;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Provides search suggestions from the previously-typed (recent) strings.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.movierr.com.movierr.mouv.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
