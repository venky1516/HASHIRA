import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.json.*;

public class C {
    public static void main(String[] args) throws Exception {
        // Process both files
        solve("testcase1.json");
        solve("testcase2.json");
    }

    private static void solve(String filename) throws Exception {
        System.out.println("\nProcessing: " + filename);

        String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filename)));
        JSONObject obj = new JSONObject(content);

        JSONObject keys = obj.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<int[]> smallPoints = new ArrayList<>();
        List<BigInteger[]> bigPoints = new ArrayList<>();

        // Decode points
        for (String key : obj.keySet()) {
            if (key.equals("keys")) continue;
            int x = Integer.parseInt(key);
            JSONObject entry = obj.getJSONObject(key);
            int base = Integer.parseInt(entry.getString("base"));
            String val = entry.getString("value");
            BigInteger y = new BigInteger(val, base);
            bigPoints.add(new BigInteger[]{BigInteger.valueOf(x), y});
        }

        // Sort by x
        bigPoints.sort(Comparator.comparing(a -> a[0]));

        // Take first k points
        List<BigInteger[]> chosen = bigPoints.subList(0, k);

        // Lagrange interpolation at x=0
        BigInteger secret = BigInteger.ZERO;
        for (int i = 0; i < k; i++) {
            BigInteger xi = chosen.get(i)[0];
            BigInteger yi = chosen.get(i)[1];

            
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                BigInteger xj = chosen.get(j)[0];
                num = num.multiply(xj.negate()); // (0 - xj)
                den = den.multiply(xi.subtract(xj));
            }

            // yi * num/den
            BigInteger term = yi.multiply(num).divide(den);
            secret = secret.add(term);
        }

        System.out.println("C = " + secret);
    }
}
