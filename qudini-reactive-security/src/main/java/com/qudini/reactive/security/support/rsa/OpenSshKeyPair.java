package com.qudini.reactive.security.support.rsa;

import lombok.Value;

@Value
public class OpenSshKeyPair {

    String publicKey;
    String privateKey;

}
