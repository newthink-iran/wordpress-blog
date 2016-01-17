package com.entekhab;

import android.content.SearchRecentSuggestionsProvider; 


public class searchProvider extends SearchRecentSuggestionsProvider { 

   public static final String AUTHORITY = searchProvider.class.getName(); 
   public static final int MODE = DATABASE_MODE_QUERIES; 

   public searchProvider() { 
      setupSuggestions(AUTHORITY, MODE); 
   } 
}

