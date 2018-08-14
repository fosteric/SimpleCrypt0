import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.io.*;
import java.security.*;
import java.util.Base64;

public class ROT13  {

    private int shift;

    ROT13(Character cs, Character cf) {
        this.shift = cf - cs;
    }

    ROT13() {
        this.shift = 13;
    }

    public String readFileToString(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = null;
        while((line = br.readLine()) != null){
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    public String cryptFile(String filename) throws IOException {
        String s = readFileToString(filename);
        return crypt(s);
    }

    public void writeFile(String filename, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(content);
        bw.close();
    }

    public String crypt(String text) throws UnsupportedOperationException {
        return revolve(text, shift);
    }

    public String encrypt(String text) {
        return revolve(text, shift);
    }

    public String decrypt(String text) {
        return revolve(text, 26-shift);
    }

    public String revolve(String s, int revolve) {
        char[] original = s.toCharArray();
        char[] translated = new char[s.length()];
        for (int i=0; i<original.length; i++) {
            if (isLowerCase(original[i])) {
                translated[i] = (char) ((((int) original[i] - (int) 'a' + revolve) % 26) + (int) 'a');
            } else if (isUpperCase(original[i])) {
                translated[i] = (char) ((((int) original[i] - (int) 'A' + revolve) % 26) + (int) 'A');
            } else {
                translated[i] = original[i];
            }
        }
        return new String(translated);
    }

    public String rotate(String s, Character c){
        if (c > 'A') {
            return s.substring((s.length() - (c - 'A')), s.length()) + s.substring(0, s.length() - (c - 'A'));
        } else {
            return s;
        }
    }

    public void AES() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String key = "dAtAbAsE98765432";
        String key2 = "dAtAbAsE98765432dAtAbAsE98765432";
        String expected = "Shall I compare thee to a summer’s day?\n" +
                "Thou art more lovely and more temperate:\n" +
                "Rough winds do shake the darling buds of May,\n" +
                "And summer’s lease hath all too short a date;\n" +
                "Sometime too hot the eye of heaven shines,\n" +
                "And often is his gold complexion dimm'd;\n" +
                "And every fair from fair sometime declines,\n" +
                "By chance or nature’s changing course untrimm'd;\n" +
                "But thy eternal summer shall not fade,\n" +
                "Nor lose possession of that fair thou ow’st;\n" +
                "Nor shall death brag thou wander’st in his shade,\n" +
                "When in eternal lines to time thou grow’st:\n" +
                "   So long as men can breathe or eyes can see,\n" +
                "   So long lives this, and this gives life to thee.\n";
        Key aesKey = new SecretKeySpec(key2.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(expected.getBytes());
        String enc = new String(Base64.getEncoder().encodeToString(encrypted));
        System.err.println("Encrypted: " + enc);
        //During the decryption (Java 8):

        System.out.print("Enter ciphertext: ");
        encrypted = Base64.getDecoder().decode(enc);

        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        String decrypted = new String(cipher.doFinal(encrypted));
        System.err.println("Decrypted: " + decrypted);
    }

    public void publicPrivateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //Create a Key Pair Generator
        KeyPairGenerator keyGenAlice = KeyPairGenerator.getInstance("RSA");
        KeyPairGenerator keyGenBob = KeyPairGenerator.getInstance("RSA");

        //Initialize the Key Pair Generator
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGenAlice.initialize(1024,random);
        keyGenBob.initialize(1024,random);

        //Generate the Pair of Keys
        KeyPair keyPairAlice = keyGenAlice.generateKeyPair();
        PrivateKey privAlice = keyPairAlice.getPrivate();
        PublicKey pubAlice = keyPairAlice.getPublic();
        KeyPair keyPairBob = keyGenBob.generateKeyPair();
        PrivateKey privBob = keyPairBob.getPrivate();
        PublicKey pubBob = keyPairBob.getPublic();

        String expected = "Shall I compare thee to a summer’s day?\n" +
                "Thou art more lovely and more temperate:\n" +
                "Rough winds do shake the darling buds of May,\n" +
                "And summer’s lease hath all too short a date;\n" +
                "Sometime too hot the eye of heaven shines,\n" +
                "And often is his gold complexion dimm'd;\n" +
                "And every fair from fair sometime declines,\n" +
                "By chance or nature’s changing course untrimm'd;\n" +
                "But thy eternal summer shall not fade,\n" +
                "Nor lose possession of that fair thou ow’st;\n" +
                "Nor shall death brag thou wander’st in his shade,\n" +
                "When in eternal lines to time thou grow’st:\n" +
                "   So long as men can breathe or eyes can see,\n" +
                "   So long lives this, and this gives life to thee.\n";

        //encrypt
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.PUBLIC_KEY, pubAlice);
        byte[] encrypted = cipher.doFinal(expected.getBytes());
        String enc = new String(Base64.getEncoder().encodeToString(encrypted));
        System.err.println("Encrypted: " + enc);

        //decrypt
        cipher.init(Cipher.PRIVATE_KEY, privAlice);
        String decrypted = new String(cipher.doFinal(encrypted));
        System.err.println("Decrypted: " + decrypted);
    }

}
