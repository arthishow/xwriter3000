package pt.ulisboa.tecnico.sirs.xwriter3000client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CypherUtil {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final String algoritmo = "RSA";
    private final String algoritmoSimetrica = "AES/CBC/PKCS5Padding";
    private final String algoritmoSimetricaAES = "AES";
    private final int comprimentoChave = 2048;
    private Map<String, SecretKey> bookKeyMap;
    private Map<String, PublicKey> publicKeyMap;
    
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
    private void generateKeys(String client) {
        try {
            KeyGenerator hg = KeyGenerator.getInstance(algoritmoSimetricaAES);
            SecretKey key = hg.generateKey();
            bookKeyMap.put(client, key);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void cypherBookKey(String bookId, int userId){
        
    }
    
    private void decypherBookKey(String book){
        
    }
    
    private void cypherBook(String book, int bookId){
        
    }
    
    private void decypherBook(String encrypedBook, int bookId){
    
    }   
}
