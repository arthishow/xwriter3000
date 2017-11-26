package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

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
import javax.crypto.NoSuchPaddingException;

public class CypherUtil {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final String algoritmo = "RSA";
    private final int comprimentoChave = 2048;
    private Map<String, PublicKey> publicKeyMap;
    
    public CypherUtil() {
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
    
    /* verifica a assinatura recebida de uma mensagem*/
    public boolean verifySign(String msg, String sign, String user) {
        try {
            Signature sigV = Signature.getInstance("SHA1withRSA");
            sigV.initVerify(publicKeyMap.get(user));

            sigV.update(msg.getBytes(), 0, msg.getBytes().length);
            byte[] signByte = Base64.getDecoder().decode(sign);
            return sigV.verify(signByte);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
    
    /* verifica a assinatura da public key recebida */
    public boolean verifySignPublicKey(String msg, String sign, String key) {
        try {
            byte[] base64decodedBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(base64decodedBytes);
            KeyFactory kf = KeyFactory.getInstance(algoritmo);
            PublicKey keyPublic = kf.generatePublic(ks);

            Signature sigV = Signature.getInstance("SHA1withRSA");
            sigV.initVerify(keyPublic);

            sigV.update(msg.getBytes(), 0, msg.getBytes().length);
            byte[] signByte = Base64.getDecoder().decode(sign);
            return sigV.verify(signByte);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
