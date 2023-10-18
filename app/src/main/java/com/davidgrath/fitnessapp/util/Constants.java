package com.davidgrath.fitnessapp.util;

public class Constants {
    public static final String MAIN_PREFERENCES_NAME = "FitnessApp";
    public static final String UNIT_HEIGHT_INCHES = "inches";
    public static final String UNIT_HEIGHT_CENTIMETERS = "centimeters";
    public static final String UNIT_WEIGHT_KG = "kilograms";
    public static final String UNIT_WEIGHT_POUNDS = "pounds";
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
    }
}
