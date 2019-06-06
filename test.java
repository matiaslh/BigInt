
import java.math.BigInteger;

public class test {
    public static void main(String[] arg) {

        // BigInt test1 = new BigInt("12");
        // BigInt test2 = test1.mult(new BigInt("-12341"));
        BigInt test3 = new BigInt("-0001234").div(new BigInt("-12341"));
        // BigInt test2 = new BigInt("4600567").div(new BigInt("1234"));


        // BigInt test2 = new BigInt("11234219");
        // BigInt res = test1.div(test2);
        // System.out.println(test3[0] + " "+test3[1]);
        System.out.println(test3);

        // runTests();

    }

    public static void runTests() {
        String[][] nums = { { "0", "0" }, { "-0", "-32" }, { "1234", "4600567" }, { "00045603", "0134743" },
                { "-12341", "-0001234" }, { "8920022", "-123891" },
                { "5732459378245972234587354138", "-008137439738473189407807" } };

        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < 2; j++) {

                String first = nums[i][j];
                String second = nums[i][(j + 1) % 2];

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
        }
    }

    public static void assertTrue(String x, String y, String message) {
        if (!x.equals(y)) {
            System.out.println(x + " != " + y + " - WRONG " + message);
        }
    }
}