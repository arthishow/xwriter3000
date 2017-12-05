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

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class CypherUtil {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;


    private PublicKey serverPublicKey;




    private final String algorithm = "RSA";
    private final String symAlgorithm = "AES/CBC/PKCS5Padding";
    private final String signAlgorithm = "SHA512withRSA";
    private final String algoritmoSimetrica = "AES/CBC/PKCS5Padding";
    private final int keyLength = 2048;
    private byte[] iv;
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

    public void writeSalt(String username, String salt){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(username + "Salt"));
            out.write(salt);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String readSalt(String username){
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

            byte[] byteCipherText = aesCipher.doFinal(key.getBytes());
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

    public void decipherPrivate(String password, String salt, String cipheredKey){
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tempKey = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tempKey.getEncoded(), "AES");
            Cipher aesCipher = Cipher.getInstance(symAlgorithm);
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(saltBytes));
            byte[] privateKeyBytes = aesCipher.doFinal(decoder.decode(cipheredKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
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
        return false;
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
            byte[] pubKeyEncoded = serverPublicKey.getEncoded();

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


    /*public String signMessage(String message) {
        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initSign(privateKey);
            sign.update(bookOrMsg.getBytes(), 0, bookOrMsg.getBytes().length);
            byte[] realSig = sign.sign();
            return Base64.getEncoder().encodeToString(realSig);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }*/

/*
    public void readServerPubKey(String pubKeyPath){
        try{
            FileInputStream fis = new FileInputStream(pubKeyPath);
            byte[] encoded = new byte[fis.available()];
            fis.read(encoded);
            fis.close();
            SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, "RSA");

            pubKey = secretKeySpec;


            byte[] pubKeyEncoded = publicKey.getEncoded();

            System.out.println(printHexBinary(pubKeyEncoded));

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }*/


    /*
    private void generateKeys(int bookId) {
        try {
            KeyGenerator hg = KeyGenerator.getInstance(algoritmoSimetricaAES);
            SecretKey key = hg.generateKey();
            bookKeyMap.put(bookId, key);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private String cypherBookKey(int bookId){
        try {
            generateKeys(bookId);
            PublicKey keyPublic = publicKeyMap.get(bookId);
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.ENCRYPT_MODE, keyPublic);
            byte[] chaveCifrada = c.doFinal(bookKeyMap.get(bookId).getEncoded());
            return Base64.getEncoder().encodeToString(chaveCifrada);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return "";
        }        
    }
    
    private Boolean decypherBookKey(String bookCifrado, int bookId){
        try {
            byte[] chaveBytes = Base64.getDecoder().decode(bookCifrado);
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] chaveDecifrada = c.doFinal(chaveBytes);
            SecretKeySpec sks = new SecretKeySpec(chaveDecifrada, algoritmoSimetricaAES);
            bookKeyMap.put(bookId, sks);
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
    
    private String cypherBook(String book, int bookId){
        try {
            String messageString = Base64.getEncoder().encodeToString(book.getBytes("utf-8"));
            byte[] messageBytes = Base64.getDecoder().decode(messageString);
            Cipher c = Cipher.getInstance(algoritmoSimetrica);
            c.init(Cipher.ENCRYPT_MODE, bookKeyMap.get(bookId));
            byte[] mensagemCifrada = c.doFinal(messageBytes);
            iv = c.getIV();
            return Base64.getEncoder().encodeToString(mensagemCifrada);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException 
                | BadPaddingException | UnsupportedEncodingException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }
    
    private String decypherBook(String encrypedBook, int bookId, String ivString){
        try {
            byte[] messageBytes = Base64.getDecoder().decode(encrypedBook);
            byte[] ivBytes = Base64.getDecoder().decode(ivString);
            IvParameterSpec ivPS = new IvParameterSpec(ivBytes);
            Cipher c = Cipher.getInstance(algoritmoSimetrica);
            c.init(Cipher.DECRYPT_MODE, (SecretKeySpec) bookKeyMap.get(bookId), ivPS);
            byte[] bookDecifrada = c.doFinal(messageBytes);
            return new String(bookDecifrada, "utf-8");
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException 
                | UnsupportedEncodingException | InvalidAlgorithmParameterException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }*/
    

    
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
    
    /*
    public void addPublicKey(String user, String publicKey) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PublicKey keyPublic = kf.generatePublic(ks);
            publicKeyMap.put(user, keyPublic);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public boolean havePublicKey(String nome) {
        return publicKeyMap.containsKey(nome);
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    public String getIv() {
        return Base64.getEncoder().encodeToString(iv);
    }



    public boolean verifySign(String book, String sign, String user) {
        try {
            Signature sigV = Signature.getInstance("SHA1withRSA");
            sigV.initVerify(publicKeyMap.get(user));

            sigV.update(book.getBytes(), 0, book.getBytes().length);
            byte[] signByte = Base64.getDecoder().decode(sign);
            return sigV.verify(signByte);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean verifySignPublicKey(String book, String sign, String key) {
        try {
            byte[] base64decodedBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(base64decodedBytes);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PublicKey keyPublic = kf.generatePublic(ks);

            Signature sigV = Signature.getInstance("SHA1withRSA");
            sigV.initVerify(keyPublic);

            sigV.update(book.getBytes(), 0, book.getBytes().length);
            byte[] signByte = Base64.getDecoder().decode(sign);
            return sigV.verify(signByte);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }*/
}
