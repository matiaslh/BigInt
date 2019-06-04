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

  public BigInt convertToOriginalBase() {

    if (this.getOriginalBase() == 10) {
      return this.clone();
    }

    BigInt baseTo = new BigInt(this.getOriginalBase());
    BigInt counter = this.clone();
    BigInt result = new BigInt(BigInt.EMPTY);

    while (counter.compareTo(BigInt.ZERO) > 0) {
      BigInt[] divResult = counter.divRemainder(baseTo);
      counter = divResult[0];
      result.addToFront(divResult[1].getNumber());
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
//qet
    return result.getNumber();
  }

  public BigInt removeDecimal() {
    if (this.hasDecimal()) {
      String newNumber = this.getNumber().substring(0, this.getNumber().indexOf("."));
      return new BigInt(newNumber);
    }
    return this.clone();
  }

  public BigInt abs() {
    BigInt temp = this.clone();
    if (this.getChar(0) == '-') {
      temp.removeFromFront(1);
    }
    return temp;
  }

  public BigInt negative() {
    BigInt temp = this.clone();
    if (this.getChar(0) == '-') {
      temp.removeFromFront(1);
    } else {
      temp.addToFront("-");
    }
    temp.format();
    return temp;
  }

  public BigInt add(BigInt other) {

    if (other.isNegative()) {
      return this.sub(other.abs());
    }
    if (this.isNegative()) {
      return other.sub(this.abs());
    }
    if (this.compareTo(other) == -1) {
      return other.add(this);
    }

    BigInt result = new BigInt(BigInt.EMPTY);
    int carry = 0;

    for (int i = this.getLength() - 1; i >= 0; i--) {
      int otherIndex = i - this.getLength() + other.getLength();
      int sumDigits = this.getDigit(i) + other.getDigit(otherIndex) + carry;
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
    return result;
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
    BigInt temp = this.clone();

    for (int i = temp.getLength() - 1; i >= 0; i--) {
      int otherIndex = i - this.getLength() + other.getLength();
      int result = temp.getDigit(i) - other.getDigit(otherIndex);

      if (result >= 0) {
        returnStr = result + returnStr;
      } else {
        int nextDigit = result + 10;
        returnStr = nextDigit + returnStr;
        temp.borrowOne(i - 1);
      }
    }
    BigInt retNum = new BigInt(returnStr);
    retNum.format();
    return retNum;
  }

  public BigInt mult(BigInt other) {

    if (this.isNegative() && other.isNegative()) {
      return this.abs().mult(other.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      return this.abs().mult(other.abs()).negative();
    }

    String zeros = "";
    int carry = 0;

    BigInt result = new BigInt(BigInt.ZERO);

    for (int i = this.getLength() - 1; i >= 0; i--) {

      BigInt tempNum = new BigInt(BigInt.EMPTY);

      for (int j = other.getLength() - 1; j >= 0; j--) {
        int res = this.getDigit(i) * other.getDigit(j) + carry;
        String resStr = String.valueOf(res);

        String toAdd = String.valueOf(resStr.charAt(resStr.length() - 1));
        tempNum.addToFront(toAdd);

        if (resStr.length() == 2) {
          carry = resStr.charAt(0) - '0';
        } else {
          carry = 0;
        }
      }
      if (carry > 0) {
        tempNum.addToFront(String.valueOf(carry));
        carry = 0;
      }

      tempNum.addToBack(zeros);
      result = result.add(tempNum);
      zeros = zeros + "0";
    }
    result.format();
    return result;
  }

  public BigInt div(BigInt other) {

    if (other.isZero()) {
      return new BigInt(BigInt.UNDEFINED);
    }
    if (this.isNegative() && other.isNegative()) {
      return this.abs().div(other.abs());
    }
    if (this.isNegative() || other.isNegative()) {
      return this.abs().div(other.abs()).negative();
    }

    BigInt temp = this.clone();
    BigInt result = new BigInt(BigInt.EMPTY);
    BigInt current = new BigInt(BigInt.EMPTY);

    for (int i = 0; i < temp.getLength(); i++) {
      current.addToBack(String.valueOf(temp.getChar(i)));
      BigInt[] tempResult = current.divBruteForce(other);
      result.addToBack(tempResult[0].getNumber());
      current = tempResult[1];
      if (i == temp.getLength() - 1 && !current.isZero()) {
        if (!result.hasDecimal()) {
          result.addToBack(".");
        } else if (result.decimalPlaces() == 8) {
          break;
        }
        temp.addToBack("0");
      }
    }
    result.format();
    return result;
  }

  private BigInt[] divRemainder(BigInt other) {

    if (other.isZero()) {
      BigInt[] result = { BigInt.ZERO.clone(), other.clone() };
      return result;
    }
    if (this.isNegative() && other.isNegative()) {
      return this.abs().divRemainder(other.abs());
    }

    BigInt temp = this.clone();
    BigInt result = new BigInt(BigInt.EMPTY);
    BigInt current = new BigInt(BigInt.EMPTY);

    for (int i = 0; i < temp.getLength(); i++) {
      current.addToBack(String.valueOf(temp.getChar(i)));
      BigInt[] tempResult = current.divBruteForce(other);
      result.addToBack(tempResult[0].getNumber());
      current = tempResult[1];
    }
    result.format();
    current.format();
    BigInt[] quotient_and_rem = { result, current };
    return quotient_and_rem;
  }

  private BigInt[] divBruteForce(BigInt other) {

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

    BigInt temp = this.clone();
    BigInt result = new BigInt(BigInt.ZERO);

    while (temp.compareTo(other) != -1) {
      temp = temp.sub(other);
      result = result.add(BigInt.ONE);
    }
    return new BigInt[] { result, temp };
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
    return result;
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

    if (this.getLength() > other.getLength()) {
      return 1;
    } else if (this.getLength() < other.getLength()) {
      return -1;
    }

    for (int i = 0; i < this.getLength(); i++) {
      int thisCurrDigit = this.getDigit(i);
      int otherCurrDigit = other.getDigit(i);
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

}