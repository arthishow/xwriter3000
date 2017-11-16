package pt.ulisboa.tecnico.sirs.xwriter3000client;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
    Classe usada para estabelecer uma chave de sessao usando Cifra Hibrida e tambem para troca de mensagem com essa chave de sessao
 */
public class SessaoCifraHibrida {

    private final String algoritmo = "RSA";
    private final String algoritmoSimetrica = "AES/CBC/PKCS5Padding";
    private final String algoritmoSimetricaAES = "AES";
    private Map<String, byte[]> keys = new HashMap<>();
    private Map<String, String> publicKeys = new HashMap<>();
    private byte[] saltMessage;
    private PrivateKey privateKey;
    private Map<String, List<String>> msgs = new HashMap<>();
    private byte[] iv;

    /*
        Função que vai gerar a chave de sessao
    */
    public void createKey(String client) {
        try {
            KeyGenerator hg = KeyGenerator.getInstance(algoritmoSimetricaAES);
            SecretKey secretKey = hg.generateKey();
            keys.put(client, secretKey.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /*
        Função que encripta a key de sessao para ser enviada pelo servidor
    */
    public String encryptKey(String rem) {
        try {
            String keyPublic = publicKeys.get(rem);
            byte[] keyPublicByte = Base64.getDecoder().decode(keyPublic);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(keyPublicByte);
            KeyFactory kf = KeyFactory.getInstance(algoritmo);
            PublicKey publicKey = kf.generatePublic(ks);
            Cipher c = Cipher.getInstance(algoritmo);
            c.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] chaveCifrada = c.doFinal(keys.get(rem));
            return Base64.getEncoder().encodeToString(chaveCifrada);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException 
                | IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }

    /*
        Função que apenas é utilizada quando é criada a chave de sessao, para cada um saber se estao a usar a mesma chave ou nao
    */
    public String createMacKey(String rem, String message) {
        try {
            byte[] keyClient = keys.get(rem);
            SecretKeySpec skey = new SecretKeySpec(keyClient, "HmacMD5");
            Mac mac = Mac.getInstance(skey.getAlgorithm());
            mac.init(skey);
            String messageString = Base64.getEncoder().encodeToString(message.getBytes("utf-8"));
            byte[] b = Base64.getDecoder().decode(messageString);
            byte[] digest = mac.doFinal(b);
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }

    /*
        Função que serve para desencriptar a chave de sessao recebida
    */
    public boolean decryptKey(String rem, String sessaoKey) {
        try {
            byte[] base64decodedBytes1 = Base64.getDecoder().decode(sessaoKey);
            Cipher c = Cipher.getInstance(algoritmo);
            c.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] chaveDecifrada = c.doFinal(base64decodedBytes1);
            SecretKeySpec sks = new SecretKeySpec(chaveDecifrada, algoritmoSimetricaAES);
            keys.put(rem, sks.getEncoded());
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    /*
        Função que vai encriptar a mensagem usando a chave de sessao (usando a troca da chave por cifra hibrida)
    */
    public String encrypt(String message, String client) {
        try {
            byte[] digestedMessage = new byte[16];
            MessageDigest md = MessageDigest.getInstance("SHA");
            createSaltMessage();
            md.update(saltMessage);
            System.arraycopy(md.digest(keys.get(client)), 0, digestedMessage, 0, 16);
            SecretKeySpec skey = new SecretKeySpec(digestedMessage, algoritmoSimetricaAES);
            Cipher cipher = Cipher.getInstance(algoritmoSimetrica);
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            byte[] textoEncriptado = cipher.doFinal(message.getBytes());
            iv = cipher.getIV();
            return Base64.getEncoder().encodeToString(textoEncriptado);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return "";
        }

    }

    /*
        Função que vai desencriptar a mensagem usando a chave de sessao (usando a troca da chave por cifra hibrida)
    */
    public String decrypt(String message, String client, String salt, String ivString) {
        try {
            byte[] digestedMessage = new byte[16];
            byte[] saltByte = Base64.getDecoder().decode(salt);
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(saltByte);
            System.arraycopy(md.digest(keys.get(client)), 0, digestedMessage, 0,16);
            
            byte[] ivByte = Base64.getDecoder().decode(ivString);
            IvParameterSpec ivPS = new IvParameterSpec(ivByte);
            
            SecretKeySpec skey = new SecretKeySpec(digestedMessage, algoritmoSimetricaAES);
            byte[] base64decodedBytes = Base64.getDecoder().decode(message);
            Cipher decifraDES = Cipher.getInstance(algoritmoSimetrica);
            decifraDES.init(Cipher.DECRYPT_MODE, skey, ivPS);
            byte[] textoDesencriptado = decifraDES.doFinal(base64decodedBytes);
            return new String(textoDesencriptado, "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException 
                | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }
    
    /*
        Função para obter a assinatura da mensagem
    */
    public String getSign(String msg) {
        try {
            Signature dsa = Signature.getInstance("SHA1withRSA");
            dsa.initSign(privateKey);
            dsa.update(msg.getBytes(), 0, msg.getBytes().length);
            byte[] realSig = dsa.sign();
            return Base64.getEncoder().encodeToString(realSig);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException ex) {
            System.out.println(ex.getMessage());
            return "";
        }
    }

    /*
        Função para verificar a assinatura recebida
    */
    public boolean verifySign(String msg, String sign, String client) {
        try {
            Signature sigV = Signature.getInstance("SHA1withRSA");

            byte[] base64decodedBytes = Base64.getDecoder().decode(publicKeys.get(client));
            X509EncodedKeySpec ks = new X509EncodedKeySpec(base64decodedBytes);
            KeyFactory kf;
            kf = KeyFactory.getInstance(algoritmo);
            PublicKey keyPublic = kf.generatePublic(ks);
            
            sigV.initVerify(keyPublic);

            sigV.update(msg.getBytes(), 0, msg.getBytes().length);
            byte[] signByte = Base64.getDecoder().decode(sign);
            return sigV.verify(signByte);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
    
    /*
        Função para verificar a assinatura recebida em relação a chave publica
    */
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

    public boolean havePublicKey(String client) {
        return publicKeys.containsKey(client);
    }

    public boolean haveKeys(String client) {
        return keys.containsKey(client);
    }

    public void addPublicKey(String client, String publicKey) {
        publicKeys.put(client, publicKey);
    }

    public String getSalt() {
        return Base64.getEncoder().encodeToString(saltMessage);
    }
    
    public String getIv() {
        return Base64.getEncoder().encodeToString(iv);
    }

    public void createSaltMessage() {
        saltMessage = new byte[16];
        new SecureRandom().nextBytes(saltMessage);
    }

    public void privateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void addMsgs(String client, String msg) {
        if (msgs.containsKey(client)) {
            msgs.get(client).add(msg);
        } else {
            List<String> stack = new ArrayList<>();
            stack.add(msg);
            msgs.put(client, stack);
        }
    }

    public List<String> getMsgs(String client) {
        return msgs.get(client);
    }
}
