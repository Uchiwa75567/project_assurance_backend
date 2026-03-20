package com.ma_sante_assurance.common.validation;

public final class PhoneNumberValidator {

    private PhoneNumberValidator() {
    }

    public static boolean isValid(String phone) {
        if (phone == null || phone.isBlank()) {
            return true;
        }
        return phone.matches("[+0-9][0-9\\s-]{6,20}");
    }
}
