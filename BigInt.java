import java.lang.Math;

public class BigInt extends MutableBigInt {

  public static final BigInt ZERO = new BigInt("0");
  public static final BigInt ONE = new BigInt("1");
  public static final BigInt EMPTY = new BigInt("");
  public static final BigInt UNDEFINED = new BigInt("undefined");

  private int originalBase;

  public BigInt(BigInt num) {
    this(num.getNumber());
  }

  public BigInt(int num) {
    this(String.valueOf(num));
  }

  public BigInt(String numStr) {
    this(numStr, 10);
  }

  public BigInt(int num, int base) {
    this(String.valueOf(num), base);
  }

  public BigInt(String numStr, int baseFrom) {
    super(BigInt.convertToDecimal(numStr, baseFrom));
    this.setOriginalBase(baseFrom);
  }

  public BigInt convertDecimalToBase(int base) {

    if (base == 10) {
      return this.clone();
    }

    BigInt baseTo = new BigInt(base);
    BigInt counter = this.clone();
    BigInt result = new BigInt(BigInt.EMPTY);

    while (counter.compareTo(BigInt.ZERO) > 0) {
      BigInt[] divResult = counter.divRemainder(baseTo);
      counter = divResult[0];
      char toAdd = BigInt.digitOrder.charAt(divResult[1].toInteger());
      result.addToFront(toAdd + "");
    }
    return result;
  }

  public static String convertToDecimal(String str, int baseFrom) {

    if (baseFrom == 10) {
      return str;
    }

    BigInt result = new BigInt(BigInt.ZERO);

    for (int i = str.length() - 1; i >= 0; i--) {
      int exponent = str.length() - i - 1;
      char currChar = str.charAt(i);
      int multValue = digitOrder.indexOf(currChar);
      if (multValue == -1) {
        return null;
      }
      BigInt temp = new BigInt(baseFrom);
      temp = temp.exp(new BigInt(exponent));
      temp = temp.mult(new BigInt(multValue));
      result = result.add(temp);
    }
    return result.getNumber();
  }

  public BigInt abs() {
    BigInt myself = this.clone();
    if (this.getChar(0) == '-') {
      myself.removeFromFront(1);
    }
    return myself.convertDecimalToBase(this.getOriginalBase());
  }

  public BigInt negative() {
    BigInt myself = this.clone();
    if (myself.getChar(0) == '-') {
      myself.removeFromFront(1);
    } else {
      myself.addToFront("-");
    }
    myself.format();
    return myself.convertDecimalToBase(this.getOriginalBase());
  }

  public BigInt add(BigInt other) {

    if (other.isNegative()) {
      return this.sub(other.abs());
    }
    if (this.isNegative()) {
      return other.sub(this.abs());
    }

    BigInt result = new BigInt(BigInt.EMPTY);
    int carry = 0;
    int startPlace = -Math.max(this.decimalPlaces(), other.decimalPlaces());
    int maxPlace = Math.max(this.integerPlaces(), other.integerPlaces());

    for (int i = startPlace; i < maxPlace; i++) {
      if (i == 0) {
        result.addToFront(".");
      }

      int sumDigits = this.getNumberPlace(i) + other.getNumberPlace(i) + carry;
      String sumDigitsStr = String.valueOf(sumDigits);

      if (sumDigitsStr.length() == 2) {
        result.addToFront(String.valueOf(sumDigitsStr.charAt(1)));
        carry = 1;
      } else {
        result.addToFront(sumDigitsStr);
        carry = 0;
      }
    }
    if (carry > 0) {
      result.addToFront(String.valueOf(carry));
    }
    result.format();
    return result.convertDecimalToBase(this.getOriginalBase());
  }

  public BigInt sub(BigInt other) {

    if (other.isNegative()) {
      return this.add(other.abs());
    }
    if (this.isNegative()) {
      return this.abs().add(other).negative();
    }
    if (this.compareTo(other) == -1) {
      return other.sub(this).negative();
    }

    String returnStr = "";
    BigInt myself = this.clone();

    int startPlace = -Math.max(myself.decimalPlaces(), other.decimalPlaces());
    int maxPlace = Math.max(myself.integerPlaces(), other.integerPlaces());

    for (int i = startPlace; i < maxPlace; i++) {
      if (i == 0) {
        returnStr = "." + returnStr;
      }

      int subDigits = myself.getNumberPlace(i) - other.getNumberPlace(i);

      if (subDigits >= 0) {
        returnStr = subDigits + returnStr;
      } else {
        int nextDigit = subDigits + 10;
        returnStr = nextDigit + returnStr;
        myself.borrowOne(i + 1);
      }
    }
    BigInt retNum = new BigInt(returnStr);
    retNum.format();
    return retNum.convertDecimalToBase(this.getOriginalBase());
  }

  public BigInt mult(BigInt other) {

    if (this.isNegative() && other.isNegative()) {
      return this.abs().mult(other.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      return this.abs().mult(other.abs()).negative();
    }

    int carry = 0;

    BigInt result = new BigInt(BigInt.ZERO);

    for (int i = -this.decimalPlaces(); i < this.integerPlaces(); i++) {
      BigInt tempNum = new BigInt(BigInt.EMPTY);

      for (int j = -other.decimalPlaces(); j < other.integerPlaces(); j++) {
        if (j == 0) {
          tempNum.addToFront(".");
        }
        int multDigits = this.getNumberPlace(i) * other.getNumberPlace(j) + carry;
        String multDigitsStr = String.valueOf(multDigits);

        String toAdd = String.valueOf(multDigitsStr.charAt(multDigitsStr.length() - 1));
        tempNum.addToFront(toAdd);
        tempNum.moveDecimal(i + j);

        if (multDigitsStr.length() == 2) {
          carry = multDigitsStr.charAt(0) - '0';
        } else {
          carry = 0;
        }
      }
      if (carry > 0) {
        tempNum.addToFront(String.valueOf(carry));
        carry = 0;
      }

      result = result.add(tempNum);
    }
    result.format();
    return result.convertDecimalToBase(this.getOriginalBase());
  }

  public BigInt[] divRemainder(BigInt other) {

    if (other.isZero()) {
      BigInt[] result = { new BigInt(BigInt.UNDEFINED), new BigInt(BigInt.UNDEFINED) };
      return result;
    }
    if (this.isNegative() && other.isNegative()) {
      return this.abs().divRemainder(other.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      BigInt[] result = this.abs().divRemainder(other.abs());
      result[0].negative();
      return result;
    }

    BigInt myself = this.clone();

    if (other.compareTo(BigInt.ONE) == -1) {
      BigInt divisor = other.clone();
      int placesToMove = divisor.decimalPlaces();
      divisor.moveDecimal(placesToMove);
      myself.moveDecimal(placesToMove);
      return myself.divRemainder(divisor);
    }

    BigInt result = new BigInt(BigInt.EMPTY);
    BigInt current = new BigInt(BigInt.EMPTY);

    for (int i = myself.integerPlaces() - 1; i >= -myself.decimalPlaces(); i--) {
      current.addToBack(String.valueOf(myself.getNumberPlace(i)));
      BigInt[] tempResult = current.divBruteForce(other);
      result.addToBack("0");
      if (result.hasDecimal()) {
        tempResult[0].moveDecimal(i);
      }
      result = result.add(tempResult[0]);
      current = tempResult[1];
      if (i == 0) {
        result.addToBack(".");
      }
    }
    result.format();
    current.format();
    BigInt[] result_remainder = {result, current};
    result_remainder[0] = result_remainder[0].convertDecimalToBase(this.getOriginalBase());
    result_remainder[1] = result_remainder[1].convertDecimalToBase(this.getOriginalBase());
    return result_remainder;
  }

  public BigInt[] divBruteForce(BigInt other) {

    if (this.isNegative() && other.isNegative()) {
      return this.abs().divBruteForce(other.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      BigInt[] result = this.abs().divBruteForce(other.abs());
      result[0] = result[0].negative();
      result[1] = result[1].negative();
      return result;
    }
    if (other.isZero()) {
      BigInt[] undefined = { BigInt.UNDEFINED, new BigInt(0) };
      return undefined;
    }

    BigInt myself = this.clone();
    BigInt result = new BigInt(BigInt.ZERO);

    while (myself.compareTo(other) != -1) {
      myself = myself.sub(other);
      result = result.add(BigInt.ONE);
    }
    result.format();
    myself.format();
    return new BigInt[] { result, myself };
  }

  public BigInt exp(BigInt other) {

    if (other.equals(BigInt.ZERO)) {
      return BigInt.ONE.clone();
    }
    if (other.compareTo(BigInt.ZERO) < 0) {
      return null;// this.root(other);
    }

    BigInt result = new BigInt(BigInt.ONE);

    while (other.compareTo(BigInt.ZERO) > 0) {
      result = result.mult(this);
      other = other.sub(BigInt.ONE);
    }
    return result.convertDecimalToBase(this.getOriginalBase());
  }

  public int compareTo(BigInt other) {

    if (this.equals(other)) {
      return 0;
    }
    if (this.isNegative() && other.isNegative()) {
      return other.abs().compareTo(this.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      return this.isNegative() ? -1 : 1;
    }

    this.format();
    other.format();

    int startPlace = Math.max(this.integerPlaces(), other.integerPlaces());
    int endPlace = -Math.max(this.decimalPlaces(), other.decimalPlaces());

    for (int i = startPlace - 1; i >= endPlace; i--) {
      int thisCurrDigit = this.getNumberPlace(i);
      int otherCurrDigit = other.getNumberPlace(i);
      if (thisCurrDigit > otherCurrDigit) {
        return 1;
      } else if (thisCurrDigit < otherCurrDigit) {
        return -1;
      }
    }
    return 0;
  }

  public int getOriginalBase() {
    return this.originalBase;
  }

  public void setOriginalBase(int originalBase) {
    this.originalBase = originalBase;
  }

  public BigInt clone() {
    return new BigInt(this.getNumber());
  }

  public boolean equals(Object obj) {
    BigInt other = (BigInt) obj;
    this.format();
    other.format();
    return this.getNumber().equals(other.getNumber());
  }

  public String toString() {
    return this.convertDecimalToBase(this.getOriginalBase()).getNumber();
  }

  public int toInteger() {
    return Integer.parseInt(this.getNumber());
  }

}