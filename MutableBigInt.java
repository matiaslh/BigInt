public class MutableBigInt {

    public static final String STRING_ZERO = "0";
    public static final String digitOrder = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String decimalNumber;

    public MutableBigInt(String str) {
        this.setNumber(str);
        this.format();
    }

    public void removeLeadingZeros() {
        int currIndex = 0;
        if (this.isNegative()) {
            currIndex = 1;
        }
        while (this.integerPlaces() > 1 && this.getDigit(currIndex) == 0) {
            this.removeIndex(currIndex);
        }
    }

    public int integerPlaces() {
        if (this.hasDecimal()) {
            return this.getNumber().indexOf(".");
        } else {
            return this.getLength() - (this.isNegative() ? 1 : 0);
        }
    }

    public int decimalPlaces() {
        if (!this.hasDecimal()) {
            return 0;
        }
        int index = this.getNumber().indexOf(".");
        return this.getLength() - index - 1;
    }

    public boolean hasDecimal() {
        return this.getNumber().indexOf(".") != -1;
    }

    public boolean isPositive() {
        return this.getChar(0) != '-';
    }

    public boolean isNegative() {
        return this.getLength() != 0 && this.getChar(0) == '-';
    }

    public boolean isEmpty() {
        return this.getNumber().equals("");
    }

    public boolean isZero() {
        if (this.isEmpty()) {
            return false;
        }
        for (int i = 0; i < this.getLength(); i++) {
            char currChar = this.getChar(i);
            if (currChar != '0' && currChar != '-') {
                return false;
            }
        }
        return true;
    }

    public void borrowOne(int index) {
        if (this.getDigit(index) == 0) {
            this.setDigit(index, 9);
            this.borrowOne(index - 1);
        } else {
            int digit = this.getDigit(index) - 1;
            this.setDigit(index, digit);
        }
    }

    public int getLength() {
        return this.getNumber().length();
    }

    public String getNumber() {
        return this.decimalNumber;
    }

    public void setNumber(String str) {
        this.decimalNumber = str;
    }

    public char getChar(int index) {
        return this.getNumber().charAt(index);
    }

    public void setChar(int index, char character) {
        String temp = this.getNumber();
        temp = temp.substring(0, index) + character + temp.substring(index + 1);
        this.setNumber(temp);
    }

    public int getDigit(int index) {
        if (index < 0) {
            return 0;
        }
        char digitChar = this.getChar(index);
        return digitChar - '0';
    }

    public void setDigit(int index, int digit) {
        char character = (char) (digit + '0');
        this.setChar(index, character);
    }

    public void addToFront(String str) {
        this.setNumber(str + this.getNumber());
    }

    public void addToBack(String str) {
        this.setNumber(this.getNumber() + str);
    }

    public void removeFromBack(int amount) {
        String newStr = this.getNumber().substring(0, this.getLength() - 1 - amount);
        this.setNumber(newStr);
    }

    public void removeFromFront(int amount) {
        String newStr = this.getNumber().substring(amount);
        this.setNumber(newStr);
    }

    public void removeIndex(int index) {
        if (index == 0) {
            this.removeFromFront(1);
            return;
        } else if (index == this.getLength() - 1) {
            this.removeFromBack(1);
            return;
        }
        String newStr = this.getNumber().substring(0, index) + this.getNumber().substring(index + 1);
        this.setNumber(newStr);
    }

    public void format() {
        this.removeLeadingZeros();
        if (this.isZero()) {
            this.setNumber(STRING_ZERO);
        }
    }

    public BigInt clone() {
        return new BigInt(this.getNumber());
    }

    public boolean equals(Object obj) {
        BigInt other = (BigInt) obj;
        this.removeLeadingZeros();
        other.removeLeadingZeros();
        return this.getNumber().equals(other.getNumber());
    }

    public String toString() {
        return this.getNumber();
    }

}