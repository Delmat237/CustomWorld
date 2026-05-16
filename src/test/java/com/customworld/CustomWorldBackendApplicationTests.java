package com.customworld;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomWorldBackendApplicationTests {

    @Test
    void applicationClassCanBeInstantiated() {
        assertThat(new CustomWorldBackendApplication()).isNotNull();
    }
}
