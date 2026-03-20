package com.ma_sante_assurance.common.util;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
