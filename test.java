
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

public class test {

    public static final String nums = "0123456789";
    public static final Random rand = new Random();

    public static void main(String[] arg) throws Exception {

        // BigInt test3 = new BigInt("1740010666492").div(new BigInt("-0684204129249"));
        // BigInt test2 = test1.mult(new BigInt("-12341"));
        // BigInt test3 = new BigInt("4.5974").sub(new BigInt("154.03"));
        // BigInt test2 = new BigInt("4600567").div(new BigInt("1234"));

        // BigInt test2 = new BigInt("11234219");
        // BigInt res = test1.div(test2);
        // System.out.println(test3[0] + " "+test3[1]);
        // System.out.println(test3);

        // runTests();
        randomTests(100, 5, 20);

    }

    public static String getRandomNumber(int length) {
        String ret = "";
        for (int i = 0; i < length; i++) {
            ret += nums.charAt(rand.nextInt(nums.length()));
        }
        // int pos = length - rand.nextInt(2)-1;
        // ret = ret.substring(0, pos) + "." + ret.substring(pos);
        return ret;
    }

    public static void randomTests(int amountTests, int minLength, int maxLength) throws Exception {
        for (int i = 0; i < amountTests; i++) {
            int len = minLength + rand.nextInt(maxLength - minLength);
            String first = getRandomNumber(len);
            String second = getRandomNumber(len);
            int negative1 = rand.nextInt(2);
            int negative2 = rand.nextInt(2);
            if (negative1 == 0) {
                first = "-" + first;
            }
            if (negative2 == 0) {
                second = "-" + second;
            }
            testNums(first, second);
        }
    }

    public static void runTests() throws Exception {
        String[][] nums = { { "0", "0" }, { "-0", "-32" }, { "1234", "4600567" }, { "00045603", "0134743" },
                { "-12341", "-0001234" }, { "8920022", "-123891" },
                { "5732459378245972234587354138", "-008137439738473189407807" } };

        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < 2; j++) {

                String first = nums[i][j];
                String second = nums[i][(j + 1) % 2];

                testNums(first, second);
            }
        }
    }

    public static void testNums(String first, String second) throws Exception {

        System.out.println("Testing: " + first + ", " + second);

        LargeDecimal myNum = new LargeDecimal(first);
        LargeDecimal myNum2 = new LargeDecimal(second);

        String myResultAdd = myNum.add(myNum2).toString();
        String myResultSub = myNum.sub(myNum2).toString();
        String myResultMult = myNum.mult(myNum2).toString();
        String myResultDiv = myNum.div(myNum2).toString();
        String myResultCompareTo = String.valueOf(myNum.compareTo(myNum2));

        BigDecimal theirNum = new BigDecimal(first);
        BigDecimal theirNum2 = new BigDecimal(second);

        MathContext mc = new MathContext(8, RoundingMode.FLOOR);
        BigDecimal theirResultAdd = theirNum.add(theirNum2, mc);
        BigDecimal theirResultSub = theirNum.subtract(theirNum2, mc);
        BigDecimal theirResultMult = theirNum.multiply(theirNum2, mc);
        BigDecimal theirResultDiv = theirNum.divide(theirNum2, mc);
        int theirResultCompareTo = theirNum.compareTo(theirNum2);

        assertTrue(new BigDecimal(myResultAdd), theirResultAdd, "add");
        assertTrue(new BigDecimal(myResultSub), theirResultSub, "sub");
        assertTrue(new BigDecimal(myResultMult), theirResultMult, "mult");
        assertTrue(new BigDecimal(myResultDiv), theirResultDiv, "div");
        assertTrue(new BigDecimal(myResultCompareTo), new BigDecimal(theirResultCompareTo), "compare");
    }

    public static void assertTrue(BigDecimal x, BigDecimal y, String message) throws Exception {
        int compare = x.subtract(y).compareTo(new BigDecimal("0.1"));
        if (compare == -1) {
            System.out.println(x + " != " + y + " - WRONG " + message);
            // throw new Exception("wrong number");
        }
    }
}