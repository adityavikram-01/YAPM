import java.security.SecureRandom;

public class PaswordGenerator {
    private static final char[] SMALL = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] LARGE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] DIGIT = "0123456789".toCharArray();
    private static final char[] SYMBOL = "!@#$%^&*()-_=+[]{};:,.<>?".toCharArray();

    public static final int MIN_LENGTH = 12;
    public static final int MAX_LENGTH = 20; 

    private static final SecureRandom random = new SecureRandom();
    
    public static String PasswordGenerator(int len, boolean needLower, boolean needUpper, boolean needNum, boolean needSym) {

        if (len >= MIN_LENGTH && len <= MAX_LENGTH){
        StringBuilder allowedCharacters = new StringBuilder();    //To build selected character string

        if (needLower){
        allowedCharacters.append(SMALL);
        }
        if (needUpper){
        allowedCharacters.append(LARGE);
        }
        if (needNum){
        allowedCharacters.append(DIGIT);
        }
        if (needSym){
        allowedCharacters.append(SYMBOL);
        }
        

        char[] Password = new char[len];
        int filledCount = 0;

        if (needLower) {                                             //using if statements to fix the characters from cateogery selected
            int fixedPosition = random.nextInt(SMALL.length);
            char selectedChar = SMALL[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }
        if (needUpper) {
            int fixedPosition = random.nextInt(LARGE.length);
            char selectedChar = LARGE[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }
        if (needNum) {
            int fixedPosition = random.nextInt(DIGIT.length);
            char selectedChar = DIGIT[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }
        if (needSym) {
            int fixedPosition = random.nextInt(SYMBOL.length);
            char selectedChar = SYMBOL[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }

        while (filledCount < len) {                          //randomly filling the remaining position
            int randomPosition = random.nextInt(allowedCharacters.length());
            char selectedChar = allowedCharacters.charAt(randomPosition);
            Password[filledCount] = selectedChar;
            filledCount++;          
        }

        for (int i = Password.length - 1; i > 0; i--) {        //Fisher–Yates shuffle algorithm
        int j = random.nextInt(i + 1);
        char temp = Password[i];
        Password[i] = Password[j];
        Password[j] = temp;
        }

        return (new String(Password));         //Password printing

    }
    else {return "";}
    }

    
}