import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <P>
 *
 * </p>
 *
 * @author zs
 * @date 2024/09/06
 */
public class TestDemo {
    public static void main(String[] args) throws Exception {
        String apiKey = "03d71b4b6cd638b6767d008a89380450dcd482c5";
        String apiSecret = "d2d1e87c64d1f5e3a2fa2b8c6d24e86e";
        long timestamp = System.currentTimeMillis();

        String md5Hash = generateMD5(apiKey, apiSecret, timestamp);
        System.out.println(timestamp);
        System.out.println("MD5哈希值: " + md5Hash);
    }

    public static String generateMD5(String apiKey, String apiSecret, long timestamp) {
        // 拼接字符串：apiKey + "|" + apiSecret + "|" + timestamp
        String input = apiKey + "|" + "\n" + apiSecret + "|" +"\n" + timestamp;

        try {
            // 获取MD5 MessageDigest实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算MD5哈希值
            byte[] messageDigest = md.digest(input.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }


}
