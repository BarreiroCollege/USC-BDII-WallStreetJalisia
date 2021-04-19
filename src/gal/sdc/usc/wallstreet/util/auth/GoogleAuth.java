package gal.sdc.usc.wallstreet.util.auth;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GoogleAuth {
    /**
     * Genera una clave secreta para usar como OTP
     *
     * @return clave secreta
     */
    public static String generarClave() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * Genera una URL para ser mostrada como imagen con un codigo QR
     *
     * @param user   usuario
     * @param host   página web
     * @param secret clave secreta
     * @return url de imagen
     */
    public static String obtenerCodigoQR(String user, String host, String secret) {
        try {
            String otpauth = "otpauth://totp/"
                    + URLEncoder.encode(host + " (" + user + ")", "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secret, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(host, "UTF-8").replace("+", "%20");
            return "https://www.google.com/chart?chs=512x512&chld=M%%7C0&cht=qr&chl="
                    + otpauth;
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Comprueba si un codigo dado coincide con la clave secreta especificada
     *
     * @param secret clave secreta
     * @param code   codigo temporal
     * @param t      tiempo
     * @return true cuando es valido, false en caso contrario
     * @throws NoSuchAlgorithmException error
     * @throws InvalidKeyException      error
     */
    public static boolean validarCodigo(String secret, long code)
            throws NoSuchAlgorithmException, InvalidKeyException {
        long t = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);

        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.
        int window = 1;
        for (int i = -window; i <= window; ++i) {
            long hash = verificarCodigo(decodedKey, t + i);

            if (hash == code) {
                return true;
            }
        }


        // The validation code is invalid.
        return false;
    }

    /**
     * Valida una clave con respecto a un tiempo dado
     *
     * @param key clave
     * @param t   tiempo
     * @return true cuando es valido, false en caso contrario
     * @throws NoSuchAlgorithmException error
     * @throws InvalidKeyException      error
     */
    private static int verificarCodigo(byte[] key, long t)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[20 - 1] & 0xF;

        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return (int) truncatedHash;
    }
}
