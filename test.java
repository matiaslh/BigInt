
import java.math.BigInteger;
import java.util.Random;

public class test {

    public static final String nums = "0123456789";
    public static final Random rand = new Random();

    public static void main(String[] arg) {

        // BigInt test1 = new BigInt("-0");//.add(new BigInt("12"));
        // test1.format();
        // BigInt test2 = test1.mult(new BigInt("-12341"));
        // BigInt test3 = new BigInt("4.5974").sub(new BigInt("154.03"));
        // BigInt test2 = new BigInt("4600567").div(new BigInt("1234"));

        // BigInt test2 = new BigInt("11234219");
        // BigInt res = test1.div(test2);
        // System.out.println(test3[0] + " "+test3[1]);
        // System.out.println(test1);

        runTests();
        randomTests(10, 5, 20);

    }

    public static String getRandomNumber(int length) {
        String ret = "";
        for (int i = 0; i < length; i++) {
            ret += nums.charAt(rand.nextInt(nums.length()));
        }
        int pos = length - rand.nextInt(2) - 1;
        ret = ret.substring(0, pos) + "." + ret.substring(pos);
        return ret;
    }

    public static void randomTests(int amountTests, int minLength, int maxLength) {
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

    public static void runTests() {
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

    public static void testNums(String first, String second) {

        System.out.println("Testing: " + first + ", " + second);

        BigInt myNum = new BigInt(first);
        String myResultAdd = myNum.add(new BigInt(second)).toString();
        String myResultSub = myNum.sub(new BigInt(second)).toString();
        String myResultMult = myNum.mult(new BigInt(second)).toString();
        BigInt divResult = myNum.div(new BigInt(second)).roundDownToInteger();
        String myResultDiv = divResult.toString();
        String myResultCompareTo = String.valueOf(myNum.compareTo(new BigInt(second)));

        BigInteger theirNum = new BigInteger(first);
        String theirResultAdd = theirNum.add(new BigInteger(second)).toString();
        String theirResultSub = theirNum.subtract(new BigInteger(second)).toString();
        String theirResultMult = theirNum.multiply(new BigInteger(second)).toString();
        String theirResultDiv = null;
        try {
            theirResultDiv = theirNum.divide(new BigInteger(second)).toString();
        } catch (Exception e) {
            theirResultDiv = "undefined";
        }
        String theirResultCompareTo = String.valueOf(theirNum.compareTo(new BigInteger(second)));

        assertTrue(myResultAdd, theirResultAdd, "add");
        assertTrue(myResultSub, theirResultSub, "sub");
        assertTrue(myResultMult, theirResultMult, "mult");
        assertTrue(myResultDiv, theirResultDiv, "div");
        assertTrue(myResultCompareTo, theirResultCompareTo, "compare");
    }

    public static void assertTrue(String x, String y, String message) {
        if (!x.equals(y)) {
            System.out.println(x + " != " + y + " - WRONG " + message);
        }
    }
}