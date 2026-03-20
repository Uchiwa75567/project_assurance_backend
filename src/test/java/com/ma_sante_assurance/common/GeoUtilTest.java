package com.ma_sante_assurance.common;

import com.ma_sante_assurance.common.util.GeoUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GeoUtilTest {

    @Test
    void distanceShouldBeZeroForSamePoint() {
        double d = GeoUtil.distanceKm(14.67, -17.43, 14.67, -17.43);
        Assertions.assertTrue(d < 0.001);
    }

    @Test
    void distanceShouldBePositiveForDifferentPoints() {
        double d = GeoUtil.distanceKm(14.67, -17.43, 14.69, -17.45);
        Assertions.assertTrue(d > 0.1);
    }
}
