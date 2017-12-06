package pt.ulisboa.tecnico.sirs.xwriter3000client;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class CypherUtil {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;


    private PublicKey serverPublicKey;




    private final String algorithm = "RSA";
    private final String symAlgorithm = "AES/CBC/PKCS5Padding";
    private final String signAlgorithm = "SHA512withRSA";
    private final int keyLength = 2048;
    private Random random;

    private Base64.Decoder decoder;
    private Base64.Encoder encoder;

    
    public CypherUtil() {
        decoder = Base64.getDecoder();
        encoder = Base64.getEncoder();
        random = new Random();
    }

    public SecretKey generateMac() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey;
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    public String MAC(String message, SecretKey secretKey){
        try{
            byte[] messageBytes = message.getBytes();
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);
            byte[] messageMac = mac.doFinal(messageBytes);
            return Base64.getEncoder().encodeToString(messageMac);
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch(InvalidKeyException e){
            e.printStackTrace();
        }
        return null;
    }

    public String generateSalt(String username){
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(username + "Salt"));
            System.out.println(encoder.encodeToString(salt));
            out.write(encoder.encodeToString(salt));
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return encoder.encodeToString(salt);
    }

    public static void writeSalt(String username, String salt){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(username + "Salt"));
            out.write(salt);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String readSalt(String username){
        try{
            BufferedReader in = new BufferedReader(new FileReader(username + "Salt"));
            String salt = "";
            String line = in.readLine();
            while (line != null) {
                salt += line;
                line = in.readLine();
            }
            in.close();
            return salt;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    //fixme:random IV
    public String cipherPrivate(String password, String salt, String key) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tempKey = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tempKey.getEncoded(), "AES");
            Cipher aesCipher = Cipher.getInstance(symAlgorithm);
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(saltBytes));

            byte[] byteCipherText = aesCipher.doFinal(decoder.decode(key));
            return encoder.encodeToString(byteCipherText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
        return null;
    }

    public void decipherPrivate(String username, String password, String salt, String cipheredKey){
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tempKey = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tempKey.getEncoded(), "AES");
            Cipher aesCipher = Cipher.getInstance(symAlgorithm);
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(saltBytes));
            byte[] privateKeyBytes = aesCipher.doFinal(decoder.decode(cipheredKey));
            KeyFactory fact = KeyFactory.getInstance("RSA");
            privateKey = fact.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            RSAPrivateKeySpec priv = fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            saveToFile(username+ "Priv", priv.getModulus(), priv.getPrivateExponent());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
    }

    public List<String> generateKeyPair(String username) {
        try {
            List<String> keys = new ArrayList<>();
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(keyLength);
            KeyPair keyPair = kpg.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            KeyFactory fact = KeyFactory.getInstance("RSA");

            RSAPublicKeySpec pub = fact.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
            saveToFile(username + "Pub", pub.getModulus(), pub.getPublicExponent());

            RSAPrivateKeySpec priv = fact.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);
            saveToFile(username+ "Priv", priv.getModulus(), priv.getPrivateExponent());

            keys.add(encoder.encodeToString(publicKey.getEncoded()));

            keys.add(encoder.encodeToString(privateKey.getEncoded()));

            return keys;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeySpecException ex){
            System.out.println(ex.getMessage());
        }

        return null;
    }


    //Fixme: catch each exception
    public void saveToFile(String fileName, BigInteger mod, BigInteger exp) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            out.writeObject(mod);
            out.writeObject(exp);
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Boolean readFromFile(String username){
        try {
            InputStream in = new FileInputStream(username + "Priv");
            ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            privateKey = fact.generatePrivate(keySpec);
            oin.close();
        } catch (FileNotFoundException e){
            return false;
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        }
        return true;
    }


    public void readServerPublicKey() {

        try {
            InputStream in = new FileInputStream("Pub");
            ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            serverPublicKey = fact.generatePublic(keySpec);
            oin.close();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public String cypherMessage(String msg){
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] cipheredMsg = cipher.doFinal(msg.getBytes());
            return Base64.getEncoder().encodeToString(cipheredMsg);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        }
        return null;
    }

    public String cipherBookKey(SecretKey simKey, String publicKey){
        try {
            PublicKey authorKey = getPublicKeyFromString(publicKey);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, authorKey);
            byte[] cipheredKey = cipher.doFinal(simKey.getEncoded());
            return encoder.encodeToString(cipheredKey);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        }
        return null;
    }

    public SecretKey stringToKey(String keyString){
        byte[] secretKetBytes = decoder.decode(keyString);
        return new SecretKeySpec(secretKetBytes, 0, secretKetBytes.length, "AES");
    }


    public PublicKey getPublicKeyFromString(String publicKeyString){
        try{
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(X509publicKey);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getPrivateKeyFromString(String privateKeyString){
        try{
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        }
        return null;
    }

    public String decypherBookKey(String cypheredMessage){
        try{
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] cypheredBytes = Base64.getDecoder().decode(cypheredMessage);
            byte[] decypheredBytes = cipher.doFinal(cypheredBytes);
            return encoder.encodeToString(decypheredBytes);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        }
        return null;
    }

    public String decypherMessage(String cypheredMessage){
        try{
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] cypheredBytes = Base64.getDecoder().decode(cypheredMessage);
            byte[] decypheredBytes = cipher.doFinal(cypheredBytes);
            return new String(decypheredBytes);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getSignature(String message){
        try{
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            byte[] messageBytes = message.getBytes();
            signature.update(messageBytes);
            byte[] signatureResult = signature.sign();
            return Base64.getEncoder().encodeToString(signatureResult);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Boolean verifySignature(String message, String sign){
        try{
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(serverPublicKey);
            signature.update(message.getBytes());
            return signature.verify(decoder.decode(sign));
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (SignatureException e){
            e.printStackTrace();
        }
        return false;
    }

    public SecretKey generateSecretKey(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey;
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    public String cypherSecretKey(String password, String salt, SecretKey simKey){
        try {
            byte[] saltBytes = decoder.decode(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tempKey = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tempKey.getEncoded(), "AES");
            Cipher aesCipher = Cipher.getInstance(symAlgorithm);
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(saltBytes));
            System.out.println("originalKey");
            System.out.println(new String(simKey.getEncoded()));
            byte[] byteCipherText = aesCipher.doFinal(simKey.getEncoded());
            System.out.println(encoder.encodeToString(byteCipherText));
            return encoder.encodeToString(byteCipherText);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        }
        return null;
    }

    public SecretKey decypherSecretKey(String cipheredKey, String password, String salt){
        try {
            System.out.println(cipheredKey);
            byte[] saltBytes = decoder.decode(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tempKey = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tempKey.getEncoded(), "AES");
            Cipher aesCipher = Cipher.getInstance(symAlgorithm);
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(saltBytes));
            byte[] secretKetBytes = aesCipher.doFinal(decoder.decode(cipheredKey));
            System.out.println("decipheredSecretKeyBytes");
            System.out.println(new String(secretKetBytes));
            return new SecretKeySpec(secretKetBytes, 0, secretKetBytes.length, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        } catch (NoSuchPaddingException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
        return null;
    }

    public String cypherBook(String bookContent, SecretKey secretKey){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cypheredBook = cipher.doFinal(bookContent.getBytes());
            return encoder.encodeToString(cypheredBook);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e){
            e.printStackTrace();
        }
        return null;
    }

    public String decypherBook(String bookContent, SecretKey secretKey){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decypheredBook = cipher.doFinal(decoder.decode(bookContent));
            return new String(decypheredBook);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e){
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    private String decypherMsg(String msgCifrada, String user){
        try {
            byte[] msgBytes = Base64.getDecoder().decode(msgCifrada);
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] msgDecifrada = c.doFinal(msgBytes);
            return Base64.getEncoder().encodeToString(msgDecifrada);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }

}
