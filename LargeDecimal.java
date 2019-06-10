import java.lang.Math;

public class LargeDecimal extends MutableLargeDecimal {

  public static final LargeDecimal ZERO = new LargeDecimal("0");
  public static final LargeDecimal ONE = new LargeDecimal("1");
  public static final LargeDecimal EMPTY = new LargeDecimal("");
  public static final LargeDecimal UNDEFINED = new LargeDecimal("undefined");
  public static final int PRECISION = 8;

  private int originalBase;

  public LargeDecimal(LargeDecimal num) {
    this(num.getNumber());
  }

  public LargeDecimal(int num) {
    this(String.valueOf(num));
  }

  public LargeDecimal(String numStr) {
    this(numStr, 10);
  }

  public LargeDecimal(int num, int base) {
    this(String.valueOf(num), base);
  }

  public LargeDecimal(String numStr, int baseFrom) {
    super(LargeDecimal.convertToDecimal(numStr, baseFrom));
    this.setOriginalBase(baseFrom);
  }

  public LargeDecimal convertDecimalToBase(int base) {

    if (base == 10) {
      return this.clone();
    }

    LargeDecimal baseTo = new LargeDecimal(base);
    LargeDecimal counter = this.clone();
    LargeDecimal result = new LargeDecimal(LargeDecimal.EMPTY);

    while (counter.compareTo(LargeDecimal.ZERO) > 0) {
      LargeDecimal[] divResult = counter.divRemainder(baseTo);
      counter = divResult[0];
      char toAdd = LargeDecimal.digitOrder.charAt(divResult[1].toInteger());
      result.addToFront(toAdd + "");
    }
    return result;
  }

  public static String convertToDecimal(String str, int baseFrom) {

    if (baseFrom == 10) {
      return str;
    }

    LargeDecimal result = new LargeDecimal(LargeDecimal.ZERO);

    for (int i = str.length() - 1; i >= 0; i--) {
      int exponent = str.length() - i - 1;
      char currChar = str.charAt(i);
      int multValue = digitOrder.indexOf(currChar);
      if (multValue == -1) {
        return null;
      }
      LargeDecimal temp = new LargeDecimal(baseFrom);
      temp = temp.exp(new LargeDecimal(exponent));
      temp = temp.mult(new LargeDecimal(multValue));
      result = result.add(temp);
    }
    return result.getNumber();
  }

  public LargeDecimal roundDownToInteger() {
    LargeDecimal myself = this.clone();
    if (myself.hasDecimal()) {
      String newString = this.getNumber().substring(0, this.getNumber().indexOf("."));
      myself.setNumber(newString);
    }
    return myself;
  }

  public LargeDecimal abs() {
    LargeDecimal myself = this.clone();
    if (this.getChar(0) == '-') {
      myself.removeFromFront(1);
    }
    return myself.convertDecimalToBase(this.getOriginalBase());
  }

  public LargeDecimal negative() {
    LargeDecimal myself = this.clone();
    if (myself.getChar(0) == '-') {
      myself.removeFromFront(1);
    } else {
      myself.addToFront("-");
    }
    myself.format();
    return myself.convertDecimalToBase(this.getOriginalBase());
  }

  public LargeDecimal add(LargeDecimal other) {

    if (other.isNegative()) {
      return this.sub(other.abs());
    }
    if (this.isNegative()) {
      return other.sub(this.abs());
    }

    LargeDecimal result = new LargeDecimal(LargeDecimal.EMPTY);
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

  public LargeDecimal sub(LargeDecimal other) {

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
    LargeDecimal myself = this.clone();

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
    LargeDecimal retNum = new LargeDecimal(returnStr);
    retNum.format();
    return retNum.convertDecimalToBase(this.getOriginalBase());
  }

  public LargeDecimal mult(LargeDecimal other) {

    if (this.isNegative() && other.isNegative()) {
      return this.abs().mult(other.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      return this.abs().mult(other.abs()).negative();
    }

    int carry = 0;

    LargeDecimal result = new LargeDecimal(LargeDecimal.ZERO);

    for (int i = -this.decimalPlaces(); i < this.integerPlaces(); i++) {
      LargeDecimal tempNum = new LargeDecimal(LargeDecimal.EMPTY);

      for (int j = -other.decimalPlaces(); j < other.integerPlaces(); j++) {
        if (j == 0) {
          tempNum.addToFront(".");
        }
        int multDigits = this.getNumberPlace(i) * other.getNumberPlace(j) + carry;
        String multDigitsStr = String.valueOf(multDigits);
        multDigitsStr = multDigitsStr + MutableLargeDecimal.repeatString("0", i + j);
        result = result.add(new LargeDecimal(multDigitsStr));
      }
    }
    result.format();
    return result.convertDecimalToBase(this.getOriginalBase());
  }

  public LargeDecimal div(LargeDecimal other) {

    if (other.isZero()) {
      return LargeDecimal.UNDEFINED.clone();
    }

    LargeDecimal[] result_with_remainder = this.divRemainder(other);
    LargeDecimal remainder = result_with_remainder[1];
    remainder.moveDecimal(LargeDecimal.PRECISION);
    LargeDecimal[] second_result = remainder.divRemainder(other);
    second_result[0].moveDecimal(-LargeDecimal.PRECISION);
    return result_with_remainder[0].add(second_result[0]);
  }

  public LargeDecimal[] divRemainder(LargeDecimal other) {

    if (other.isZero()) {
      LargeDecimal[] result = { new LargeDecimal(LargeDecimal.UNDEFINED), new LargeDecimal(LargeDecimal.UNDEFINED) };
      return result;
    }
    if (this.isNegative() && other.isNegative()) {
      LargeDecimal[] result = this.abs().divRemainder(other.abs());
      result[1] = result[1].negative();
      return result;
    }
    if (this.isNegative() || other.isNegative()) {
      LargeDecimal[] result = this.abs().divRemainder(other.abs());
      result[0] = result[0].negative();
      if (this.isNegative()) {
        result[1] = result[1].negative();
      }
      return result;
    }

    LargeDecimal myself = this.clone();

    if (other.decimalPlaces() > 0) {
      LargeDecimal divisor = other.clone();
      int placesToMove = divisor.decimalPlaces();
      divisor.moveDecimal(placesToMove);
      myself.moveDecimal(placesToMove);
      LargeDecimal[] result = myself.divRemainder(divisor);
      result[1].moveDecimal(-placesToMove);
      return result;
    }

    LargeDecimal result = new LargeDecimal(LargeDecimal.EMPTY);
    LargeDecimal current = new LargeDecimal(LargeDecimal.EMPTY);

    for (int i = myself.integerPlaces() - 1; i >= -myself.decimalPlaces(); i--) {
      current.addToBack(String.valueOf(myself.getNumberPlace(i)));
      LargeDecimal[] tempResult = current.divBruteForce(other);
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
    LargeDecimal[] result_remainder = { result, current };
    result_remainder[0] = result_remainder[0].convertDecimalToBase(this.getOriginalBase());
    result_remainder[1] = result_remainder[1].convertDecimalToBase(this.getOriginalBase());
    return result_remainder;
  }

  public LargeDecimal[] divBruteForce(LargeDecimal other) {

    if (this.isNegative() && other.isNegative()) {
      return this.abs().divBruteForce(other.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      LargeDecimal[] result = this.abs().divBruteForce(other.abs());
      result[0] = result[0].negative();
      result[1] = result[1].negative();
      return result;
    }
    if (other.isZero()) {
      LargeDecimal[] undefined = { LargeDecimal.UNDEFINED, new LargeDecimal(0) };
      return undefined;
    }

    LargeDecimal myself = this.clone();
    LargeDecimal result = new LargeDecimal(LargeDecimal.ZERO);

    while (myself.compareTo(other) != -1) {
      myself = myself.sub(other);
      result = result.add(LargeDecimal.ONE);
    }
    result.format();
    myself.format();
    return new LargeDecimal[] { result, myself };
  }

  public LargeDecimal exp(LargeDecimal other) {

    if (other.equals(LargeDecimal.ZERO)) {
      return LargeDecimal.ONE.clone();
    }
    if (other.compareTo(LargeDecimal.ZERO) < 0) {
      return null;// this.root(other);
    }

    LargeDecimal result = new LargeDecimal(LargeDecimal.ONE);

    while (other.compareTo(LargeDecimal.ZERO) > 0) {
      result = result.mult(this);
      other = other.sub(LargeDecimal.ONE);
    }
    return result.convertDecimalToBase(this.getOriginalBase());
  }

  public int compareTo(LargeDecimal other) {

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

  public LargeDecimal clone() {
    return new LargeDecimal(this.getNumber());
  }

  public boolean equals(Object obj) {
    LargeDecimal other = (LargeDecimal) obj;
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