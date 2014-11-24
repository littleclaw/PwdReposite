package com.lttclaw.pwdrepository;

import android.content.SearchRecentSuggestionsProvider;

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {

	public final static String AUTHORITY = "com.lttclaw.pwdrepository.MySuggestionProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;

	public MySuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
