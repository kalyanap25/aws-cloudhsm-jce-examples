/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.amazonaws.cloudhsm.examples;

import com.cavium.key.parameter.CaviumECGenParameterSpec;

import java.security.*;
import java.util.Base64;
import java.io.IOException;

/**
 * Demonstrate basic EC sign/verify operations.
 */
public class ECOperationsRunner {

    /**
     * Sign a message using the passed signing algorithm.
     * Supported signature types are documented here: https://docs.aws.amazon.com/cloudhsm/latest/userguide/java-lib-supported.html
     *
     * @param message
     * @param key
     * @param signingAlgorithm
     * @return
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static byte[] sign(byte[] message, PrivateKey key, String signingAlgorithm)
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        Signature sig = Signature.getInstance(signingAlgorithm, "Cavium");
        sig.initSign(key);
        sig.update(message);
        return sig.sign();
    }

    /**
     * Verify the signature of a message.
     * Supported signature types are documented here: https://docs.aws.amazon.com/cloudhsm/latest/userguide/java-lib-supported.html
     *
     * @param message
     * @param signature
     * @param publicKey
     * @param signingAlgorithm
     * @return
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static boolean verify(byte[] message, byte[] signature, PublicKey publicKey, String signingAlgorithm)
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        Signature sig = Signature.getInstance(signingAlgorithm, "Cavium");
        sig.initVerify(publicKey);
        sig.update(message);
        return sig.verify(signature);
    }

    public static void main(final String[] args) throws Exception {
        try {
            Security.addProvider(new com.cavium.provider.CaviumProvider());
        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }

        String plainText = "This is a sample Plain Text Message!";

        // Use the SECP256k1 curve to sign and verify.
        KeyPair kp = new AsymmetricKeys().generateECKeyPair(CaviumECGenParameterSpec.K256, "ectest");
        String signingAlgorithm = "SHA512withECDSA";
        byte[] signature = sign(plainText.getBytes("UTF-8"), kp.getPrivate(), signingAlgorithm);
        System.out.println("Plaintext signature = " + Base64.getEncoder().encodeToString(signature));

        if (verify(plainText.getBytes("UTF-8"), signature, kp.getPublic(), signingAlgorithm)) {
            System.out.println("Signature verified");
        } else {
            System.out.println("Signature is invalid!");
        }
    }
}
