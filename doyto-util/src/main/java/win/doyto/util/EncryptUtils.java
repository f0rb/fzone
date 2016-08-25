package win.doyto.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.lang3.StringUtils;

/**
 * CryptUtils 加密解密工具.
 * <p/>
 * User: f0rb
 * Date: 2010-3-1
 * Time: 23:44:25
 */
public class EncryptUtils {
    /** 如果系统中存在旧版本的数据，则此值不能修改，否则在进行密码解析的时候出错 */
    private static final String DEFAULT_CRYPT_KEY = "_A_1822281_A_";
    private static final String DES = "DES";


    //SecretKey secretKey = SecretKeyFactory.getInstance(DES).generateSecret(new DESKeySpec(DEFAULT_CRYPT_KEY));
    // Cipher对象实际完成加密操作
    //Cipher cipher = Cipher.getInstance(DES);

    /**
     * 使用DEFAULT_CRYPT_KEY进行数据加密
     *
     * @param data 明文
     * @return 密文
     */
    public static String encrypt(String data) {
        return encrypt(data, DEFAULT_CRYPT_KEY);
    }

    /**
     * 使用DEFAULT_CRYPT_KEY进行数据解密密
     *
     * @param data 密文
     * @return 明文
     */
    public static String decrypt(String data) {
        return decrypt(data, DEFAULT_CRYPT_KEY);
    }

    /**
     * 数据加密
     *
     * @param data 明文
     * @param key  密钥
     * @return 密文
     */
    public static String encrypt(String data, String key) {
        key = StringUtils.rightPad(key, 8);// key至少为8位
        return byte2hex(encrypt(data.getBytes(), key.getBytes()));
    }

    /**
     * 数据解密
     *
     * @param data 密文
     * @param key  密钥
     * @return 明文
     */
    public static String decrypt(String data, String key) {
        return new String(decrypt(hex2byte(data.getBytes()), key.getBytes()));
    }

    /**
     * 密码加密
     *
     * @param username key
     * @param password 密码明文
     * @return null 或 password的密文
     */
    public static String encryptPassword(String username, String password) {
        return MD5(MD5(username) + password);
    }

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     */
    private static byte[] encrypt(byte[] src, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError();
        }
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     */
    private static byte[] decrypt(byte[] src, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
            // 现在，获取数据并解密
            // 正式执行解密操作
            return cipher.doFinal(src);
        } catch (Exception e) {
            throw new InternalError();
        }
    }

    /** 对字符串进行MD5加密 */
    private static String MD5(String input) {
        if (input == null) return null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5"); //创建具有指定算法名称的信息摘要
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError(); // this can not happen
        }
        //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
        byte[] results = md.digest(input.getBytes());
        //将得到的字节数组变成字符串返回
        return byte2hex(results);
    }

    /**
     * 二进制转字符串
     *
     * @param b byte array
     * @return string
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        for (int n = 0; b != null && n < b.length; n++) {
            String temp = Integer.toHexString(b[n] & 0XFF);
            //hs = temp.length() == 1 ? hs + "0" + temp : hs + temp;
            if (temp.length() == 1) {
                hs.append("0");//字节对齐
            }
            hs.append(temp);
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
}
