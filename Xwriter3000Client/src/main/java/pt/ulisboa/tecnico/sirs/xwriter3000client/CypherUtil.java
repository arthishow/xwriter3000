package pt.ulisboa.tecnico.sirs.xwriter3000client;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CypherUtil {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final String algoritmo = "RSA";
    private final String algoritmoSimetrica = "AES/CBC/PKCS5Padding";
    private final String algoritmoSimetricaAES = "AES";
    private final int comprimentoChave = 2048;
    private Map<Integer, SecretKey> bookKeyMap;
    private Map<String, PublicKey> publicKeyMap;
    private byte[] iv;
    
    public CypherUtil() {
        bookKeyMap = new HashMap<>();
        publicKeyMap = new HashMap<>();
        generateKeyPair();
    }

    /* gera um par de chaves assimetrica */
    private void generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algoritmo);
            kpg.initialize(comprimentoChave);
            KeyPair kp = kpg.generateKeyPair();
            privateKey = kp.getPrivate();
            publicKey = kp.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /* gera uma chave necessaria para a cifra simetrica */
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
            Cipher c = Cipher.getInstance(algoritmo);
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
            Cipher c = Cipher.getInstance(algoritmo);
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
    }
    
    private String cypherMessage(String msg, String user){
        try {
            PublicKey keyPublic = publicKeyMap.get(user);
            Cipher c = Cipher.getInstance(algoritmo);
            c.init(Cipher.ENCRYPT_MODE, keyPublic);
            byte[] msgCifrada = c.doFinal(msg.getBytes());
            return Base64.getEncoder().encodeToString(msgCifrada);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return "";
        }        
    }
    
    private String decypherMsg(String msgCifrada, String user){
        try {
            byte[] msgBytes = Base64.getDecoder().decode(msgCifrada);
            Cipher c = Cipher.getInstance(algoritmo);
            c.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] msgDecifrada = c.doFinal(msgBytes);
            return Base64.getEncoder().encodeToString(msgDecifrada);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }
    
    /* adiciona a public key de um user */
    public void addPublicKey(String user, String publicKey) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance(algoritmo);
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
    
    /* criação da assinatura para o book */
    public String getSign(String book) {
        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initSign(privateKey);
            sign.update(book.getBytes(), 0, book.getBytes().length);
            byte[] realSig = sign.sign();
            return Base64.getEncoder().encodeToString(realSig);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }
    
    /* verifica a assinatura recebida de um book*/
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
    
    /* verifica a assinatura da public key recebida */
    public boolean verifySignPublicKey(String book, String sign, String key) {
        try {
            byte[] base64decodedBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(base64decodedBytes);
            KeyFactory kf = KeyFactory.getInstance(algoritmo);
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
    }
}
