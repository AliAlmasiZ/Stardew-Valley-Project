package com.ap.stardew.models.enums;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender getGender(String input) {
        Gender gender = null;
        if (input.toLowerCase().equals("male")) {
            gender = Gender.MALE;
        } else if (input.toLowerCase().equals("female")) {
            gender = Gender.FEMALE;
        }

        return gender;
    }
}
