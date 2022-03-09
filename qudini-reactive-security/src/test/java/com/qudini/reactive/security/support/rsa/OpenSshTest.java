package com.qudini.reactive.security.support.rsa;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenSshTest {

    @Test
    void generateKeyPair() {
        var keyPair = OpenSsh.generateKeyPair();
        assertThat(keyPair.getPublicKey()).startsWith("ssh-rsa ");
        assertThat(keyPair.getPublicKey()).endsWith(" qudini");
        assertThat(keyPair.getPrivateKey()).startsWith("-----BEGIN RSA PRIVATE KEY-----\n");
        assertThat(keyPair.getPrivateKey()).endsWith("\n-----END RSA PRIVATE KEY-----\n");
        OpenSsh.decodePublicKey(keyPair.getPublicKey());
    }

}
