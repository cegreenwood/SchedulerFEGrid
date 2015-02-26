/**
 *
 * Copyright 2015 Blueshire Services Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.blueshireservices.schedulergrid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

public class SchedEncryp {

    public final static SecretKey generateKey() throws NoSuchAlgorithmException{
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        kg.init(random);
        return kg.generateKey();
    }

    public static final String encrypt(final String message,
                                       final Key key,
                                       final IvParameterSpec iv)
            throws IllegalBlockSizeException,BadPaddingException, NoSuchAlgorithmException,
                   NoSuchPaddingException, InvalidKeyException,
                   UnsupportedEncodingException, InvalidAlgorithmParameterException
    {

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE,key,iv);

      byte[] stringBytes = message.getBytes();
      byte[] raw = cipher.doFinal(stringBytes);

      return Base64.encodeBase64String(raw);
    }

    public static final String decrypt(final String encrypted,
                                       final Key key,
                                       final IvParameterSpec iv)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
                   IllegalBlockSizeException, BadPaddingException, IOException,
                   InvalidAlgorithmParameterException 
    {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key,iv);

        byte[] raw = Base64.decodeBase64(encrypted);

        byte[] stringBytes = cipher.doFinal(raw);

        String clearText = new String(stringBytes, "UTF8");
        return clearText;
    }

}

