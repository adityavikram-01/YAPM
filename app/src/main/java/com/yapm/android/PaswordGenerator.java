import java.security.SecureRandom;
import java.util.Scanner;

public class PaswordGenerator {
    private static final char[] SMALL = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] LARGE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] DIGIT = "0123456789".toCharArray();
    private static final char[] SYMBOL = "!@#$%^&*()-_=+[]{};:,.<>?".toCharArray();

    public static final int MIN_LENGTH = 12;
    public static final int MAX_LENGTH = 20; 

    private static final SecureRandom random = new SecureRandom();
    
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int length = readLength(sc);    //Takes length andd verify it


        System.out.println("Do you want Lowercase? Enter true or false:");     
        boolean useSmall = sc.nextBoolean();
        System.out.println("Do you want Uppercase? Enter true or false:");
        boolean useLarge = sc.nextBoolean();
        System.out.println("Do you want digits? Enter true or false:");
        boolean useDigit = sc.nextBoolean();
        System.out.println("Do you want Symbol? Enter true or false:");
        boolean useSymbol = sc.nextBoolean();

        StringBuilder allowedCharacters = new StringBuilder();    //To build selected character string

        if (useSmall==true){
        allowedCharacters.append(SMALL);
        }
        if (useLarge==true){
        allowedCharacters.append(LARGE);
        }
        if (useDigit==true){
        allowedCharacters.append(DIGIT);
        }
        if (useSymbol==true){
        allowedCharacters.append(SYMBOL);
        }
        if(allowedCharacters.length()==0){
            System.out.println("Select at least one character cateogery");
            return;
        }

        char[] Password = new char[length];
        int filledCount = 0;

        if (useSmall) {                                             //using if statements to fix the characters from cateogery selected
            int fixedPosition = random.nextInt(SMALL.length);
            char selectedChar = SMALL[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }
        if (useLarge) {
            int fixedPosition = random.nextInt(LARGE.length);
            char selectedChar = LARGE[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }
        if (useDigit) {
            int fixedPosition = random.nextInt(DIGIT.length);
            char selectedChar = DIGIT[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }
        if (useSymbol) {
            int fixedPosition = random.nextInt(SYMBOL.length);
            char selectedChar = SYMBOL[fixedPosition];
            Password[filledCount] = selectedChar;
            filledCount++;
        }

        while (filledCount < length) {                          //randomly filling the remaining position
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

        System.out.println("Generated password: " + new String(Password));         //Password printing

        sc.close();
    }

    public static int readLength(Scanner sc){
        while(true){
            System.out.println("Enter password length (12-20):");
            int length = sc.nextInt();
            if(length < MIN_LENGTH || length > MAX_LENGTH){
            System.out.println("Invalid");
            }
            else{
            return length;
            }
        }
    }
    
}