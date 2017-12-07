package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class CypherUtil {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final String algorithm = "RSA";
    private final String signAlgorithm = "SHA512withRSA";
    private final int keyLength = 2048;
    private Random random;


    public CypherUtil() {
        random = new Random();
        readServerPrivateKey();
    }

    public void readServerPrivateKey() {

        try {
            InputStream in = new FileInputStream("Priv");
            ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            privateKey = fact.generatePrivate(keySpec);
            oin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String cypherMessage(String msg, PublicKey publicKey){
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
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

    public String decypherMessage(String cipheredMsg){
        try {
            byte[] msgBytes = Base64.getDecoder().decode(cipheredMsg);
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decipheredBytes = c.doFinal(msgBytes);
            String message = new String(decipheredBytes);
            return message;
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

    public String generateSalt(){
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hashPass(String password, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeySpecException e){
            e.printStackTrace();
        }
        return null;
    }

    public Boolean checkHmac(String message, String messageMac, SecretKey macKey){
        try {
            byte[] messageBytes = message.getBytes();
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(macKey);
            byte[] realMac = mac.doFinal(messageBytes);
            byte[] messageMacBytes = Base64.getDecoder().decode(messageMac);
            if (Arrays.equals(realMac, messageMacBytes)){
                return true;
            }
            else{
                return false;
            }
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        }
        return false;
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

    public void generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(keyLength);
            KeyPair kp = kpg.generateKeyPair();
            privateKey = kp.getPrivate();
            publicKey = kp.getPublic();
            byte[] privKeyEncoded = privateKey.getEncoded();
            byte[] pubKeyEncoded = publicKey.getEncoded();

            FileOutputStream privFos = new FileOutputStream("Priv");
            privFos.write(privKeyEncoded);
            privFos.close();
            FileOutputStream pubFos = new FileOutputStream("Pub");
            pubFos.write(pubKeyEncoded);
            pubFos.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSiganture(String message){
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

    public Boolean verifySignature(String message, String sign, PublicKey publicKey){
        try{
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(message.getBytes());
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        } catch (SignatureException e){
            e.printStackTrace();
        }
        return false;
    }

}
