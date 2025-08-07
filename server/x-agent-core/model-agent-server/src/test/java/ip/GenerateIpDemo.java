package ip;

public class GenerateIpDemo {
    public static void main(String[] args) {
        String ipRange1 = "103.79.202.150-103.79.202.220";
        String ipRange2 = "27.0.167.150-27.0.167.220";

        String result1 = generateIpRangeString(ipRange1);
        String result2 = generateIpRangeString(ipRange2);

        System.out.println("result1:\n" + result1);
        System.out.println("result2:\n" + result2);
    }


    private static String generateIpRangeString(String ipRange) {
        String[] parts = ipRange.split("-");
        String startIp = parts[0];
        String endIp = parts[1];

        String[] startParts = startIp.split("\\.");
        String[] endParts = endIp.split("\\.");

        int start = Integer.parseInt(startParts[3]);
        int end = Integer.parseInt(endParts[3]);

        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= end; i++) {
            sb.append(startParts[0]).append(".").append(startParts[1]).append(".").append(startParts[2]).append(".").append(i);
            if (i < end) {
                sb.append(";");
            }
        }

        return sb.toString();
    }

}
