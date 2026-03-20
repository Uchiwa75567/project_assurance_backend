package com.ma_sante_assurance.common.util;

public final class NameParser {

    private NameParser() {
    }

    public static NameParts split(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new NameParts("Client", "");
        }

        String[] names = fullName.trim().split("\\s+", 2);
        String prenom = names.length > 0 ? names[0] : fullName.trim();
        String nom = names.length > 1 ? names[1] : "";
        return new NameParts(prenom, nom);
    }
}
