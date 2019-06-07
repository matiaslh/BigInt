public class MutableBigInt {

    public static final String STRING_ZERO = "0";
    public static final String digitOrder = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String decimalNumber;

    public MutableBigInt(String str) {
        this.setNumber(str);
        this.format();
    }

    public void removeLeadingZeros() {
        for (int i = this.integerPlaces() - 1; i >= this.decimalPlaces(); i--) {
            if (this.getLength() == 1 + (this.isNegative() ? 1 : 0) || this.getNumberPlace(i) != 0) {
                break;
            } else {
                this.removePlace(i);
            }
        }
    }

    public void removeTrailingZeros() {
        for (int i = -this.decimalPlaces(); i < 0; i++) {
            if (this.getLength() == 1 || this.getNumberPlace(i) != 0) {
                break;
            } else {
                this.removePlace(i);
            }
        }
    }

    public void removeUnneededDecimal() {
        if (this.getLength() > 1 && this.getChar(this.getLength() - 1) == '.') {
            this.removeFromBack(1);
        }
    }

    public void addNeededZero() {
        int isNegative = this.isNegative() ? 1 : 0;
        if (this.getLength() > 0 && this.getChar(isNegative) == '.') {
            String newString = this.getNumber().substring(isNegative);
            this.setNumber((isNegative == 1 ? "-" : "") + "0" + newString);
        }
    }

    public int getIndexFromPlace(int place) {
        if (this.hasDecimal()) {
            int decimalIndex = this.getNumber().indexOf(".");
            return decimalIndex - place - (place >= 0 ? 1 : 0);
        } else {
            return this.getLength() - place - 1;
        }
    }

    public int getNumberPlace(int place) {
        int index = this.getIndexFromPlace(place);
        if (index < 0 || index >= this.getLength()) {
            return 0;
        }
        return this.getDigit(index);
    }

    public void setNumberPlace(int place, int digit) {
        int index = this.getIndexFromPlace(place);
        if (index >= this.getLength()) {
            this.addToBack(MutableBigInt.repeatString("0", index - this.getLength() + 1));
        }
        this.setDigit(index, digit);
    }

    public void removePlace(int place) {
        int index = this.getIndexFromPlace(place);
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

    public void moveDecimal(int places) {

        boolean isNegative = this.isNegative();
        if (isNegative) {
            this.setNumber(this.getNumber().substring(1));
        }

        int indexDecimal = this.getLength();
        if (this.hasDecimal()) {
            indexDecimal = this.getNumber().indexOf(".");
            String newString = this.getNumber().replaceAll("\\.", "");
            this.setNumber(newString);
        }
        if (indexDecimal + places > this.getLength()) {
            int amountZeros = indexDecimal + places - this.getLength();
            this.addToBack(MutableBigInt.repeatString("0", amountZeros));
            return;
        } else if (indexDecimal + places < 0) {
            int amountZeros = -(indexDecimal + places);
            this.addToFront(MutableBigInt.repeatString("0", amountZeros));
            this.setNumber("0." + this.getNumber());
            return;
        }

        String newString = this.getNumber().substring(0, indexDecimal + places) + "."
                + this.getNumber().substring(indexDecimal + places);
        this.setNumber(newString);
        if (isNegative) {
            this.addToFront("-");
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
            if (currChar != '0' && currChar != '-' && currChar != '.') {
                return false;
            }
        }
        return true;
    }

    public void borrowOne(int index) {
        if (this.getNumberPlace(index) == 0) {
            this.setNumberPlace(index, 9);
            this.borrowOne(index + 1);
        } else {
            int digit = this.getNumberPlace(index) - 1;
            this.setNumberPlace(index, digit);
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
        String newStr = this.getNumber().substring(0, this.getLength() - amount);
        this.setNumber(newStr);
    }

    public void removeFromFront(int amount) {
        String newStr = this.getNumber().substring(amount);
        this.setNumber(newStr);
    }

    public void format() {
        this.removeLeadingZeros();
        this.removeTrailingZeros();
        this.removeUnneededDecimal();
        this.addNeededZero();
        if (this.getLength() > 0 && this.getChar(0) == '.') {
            this.addToFront("0");
        }
        if (this.isZero()) {
            this.setNumber(STRING_ZERO);
        }
    }

    public static String repeatString(String str, int n) {
        String newString = "";
        for (int i = 0; i < n; i++) {
            newString += str;
        }
        return newString;
    }

    public String toString() {
        return this.getNumber();
    }

}