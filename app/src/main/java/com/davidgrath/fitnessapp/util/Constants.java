package com.davidgrath.fitnessapp.util;

import com.davidgrath.fitnessapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Constants {
    public static final String MAIN_PREFERENCES_NAME = "FitnessApp";
    public static final String UNIT_HEIGHT_INCHES = "inches";
    public static final String UNIT_HEIGHT_CENTIMETERS = "centimeters";
    public static final String UNIT_WEIGHT_KG = "kilograms";
    public static final String UNIT_WEIGHT_POUNDS = "pounds";
    public static final String UNIT_DISTANCE_MILES = "miles";
    public static final String UNIT_DISTANCE_KILOMETERS = "kilometers";
    public static final String UNIT_TEMPERATURE_CELSIUS = "celsius";
    public static final String UNIT_TEMPERATURE_FAHRENHEIT = "fahrenheit";
    public static final Character BULLET = '\u2022';

    public static final class PreferencesTitles {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String EMAIL = "email";
        public static final String GENDER = "gender";
        /**
         * integer, so I don't forget
         */
        public static final String HEIGHT = "height";
        public static final String HEIGHT_UNIT = "heightUnit";
        public static final String BIRTH_DATE_DAY = "birthDateDay";
        public static final String BIRTH_DATE_MONTH = "birthDateMonth";
        public static final String BIRTH_DATE_YEAR = "birthDateYear";
        /**
         * float, so I don't forget
         */
        public static final String WEIGHT = "weight";
        public static final String WEIGHT_UNIT = "weightUnit";
        public static final String DISTANCE_UNIT = "distanceUnit";
        public static final String TEMPERATURE_UNIT = "temperatureUnit";
        public static final String CHOSEN_LANGUAGE_CODE = "chosenLanguageCode";
        public static final String CHOSEN_COUNTRY_CODE = "chosenCountryCode";
        public static final String SHOULD_SYNC_TO_GOOGLE_FIT = "shouldSyncToGoogleFit";
        public static final String SHOULD_REMIND_TO_WORKOUT = "shouldRemindToWorkout";
        public static final String CURRENT_USER_UUID = "currentUserUuid";
        public static final String MEDIA_STORE_TEMP_IMAGE_URI = "mediaStoreTempImageUri";
        public static final String USER_AVATAR = "userAvatar";
        /**
         * none, default, media
         */
        public static final String USER_AVATAR_TYPE = "userAvatarType";
    }

    public static final String[] supportedLanguages = new String[]{
            "en",
            "fr",
            "it",
            "de",
            "es",
            "pt",
            "nl",
            "pl",
            "zh",
            "ja",
            "tr",
            "ar",
            "ur",
            "id",
            "vi"
    };

    // https://ui8.net/pigment-store-804a8d/products/60-unique-avatars
    // https://ui8.net/pigment-store-804a8d/products?status=7
    public static final Map<String, Integer> avatarMap = new HashMap<>();
    static {
        avatarMap.put("avatar_02", R.drawable.avatar_02);
        avatarMap.put("avatar_05", R.drawable.avatar_05);
        avatarMap.put("avatar_08", R.drawable.avatar_08);
        avatarMap.put("avatar_11", R.drawable.avatar_11);
        avatarMap.put("avatar_29", R.drawable.avatar_29);
        avatarMap.put("avatar_30", R.drawable.avatar_30);
        avatarMap.put("avatar_32", R.drawable.avatar_32);
        avatarMap.put("avatar_35", R.drawable.avatar_35);
        avatarMap.put("avatar_37", R.drawable.avatar_37);
        avatarMap.put("avatar_39", R.drawable.avatar_39);
        avatarMap.put("avatar_41", R.drawable.avatar_41);
        avatarMap.put("avatar_43", R.drawable.avatar_43);
        avatarMap.put("avatar_45", R.drawable.avatar_45);
        avatarMap.put("avatar_47", R.drawable.avatar_47);
        avatarMap.put("avatar_49", R.drawable.avatar_49);
        avatarMap.put("avatar_51", R.drawable.avatar_51);
        avatarMap.put("avatar_53", R.drawable.avatar_53);
        avatarMap.put("avatar_55", R.drawable.avatar_55);
        avatarMap.put("avatar_57", R.drawable.avatar_57);
        avatarMap.put("avatar_59", R.drawable.avatar_59);
    }
}
