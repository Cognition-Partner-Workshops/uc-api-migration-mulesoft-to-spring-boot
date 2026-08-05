package com.workshop.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TokenStoreTest {
    @Test
    void issuedTokenIsValidThenExpires() throws InterruptedException {
        TokenStore store = new TokenStore(1);
        String token = store.issue("client");
        assertThat(store.isValid(token)).isTrue();
        Thread.sleep(1100);
        assertThat(store.isValid(token)).isFalse();
    }
}
