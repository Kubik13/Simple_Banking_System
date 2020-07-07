package banking;

import java.util.Random;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static Connect connect = new Connect();

    public static void main(String[] args) {
        connect.connect("card.s3db");

        while(true) {
            System.out.println("\n1. Create an account\n2. Log into account\n0. Exit");
            int ans = sc.nextInt();
            if (ans == 0) break;
            else if (ans == 1) createAccount();
            else if (ans == 2) {
                System.out.println("Enter your card number:");
                Long cardNumber = sc.nextLong();
                System.out.println("Enter your PIN:");
                int PIN = sc.nextInt();
                if (connect.checkPin(cardNumber.toString(), String.valueOf(PIN))){
                    if (loggedIn(cardNumber,PIN)) break;
                }
                else System.out.println("Wrong card number or PIN!");
            }

        }

    }

    private static boolean loggedIn(Long cardNumber, int PIN) {
        System.out.println("You have successfully logged in!");
        while (true) {
            System.out.println("\n" +
                    "1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            int ans = sc.nextInt();
            if (ans == 5) return false;
            else if (ans == 0) return true;
            else if (ans == 1) System.out.println("Balance: " + connect.getBalance(cardNumber.toString()));
            else if (ans == 2) {
                System.out.println("Enter income:");
                Integer income = sc.nextInt();
                connect.addIncome(cardNumber.toString(), income);
                System.out.println("Income was added!");
            }
            else if (ans == 3) {
                System.out.println("Transfer\nEnter card number:");
                Long transferacc = sc.nextLong();
                Long checkNum = transferacc % 10;
                if (transferacc == cardNumber) System.out.println("You can't transfer money to the same account!");
                else if (luhnAlgoritm(transferacc/10) != checkNum) System.out.println("Probably you made mistake in the card number. Please try again!");
                else if (!connect.checkCard(transferacc.toString())) System.out.println("Such a card does not exist.");
                else {
                    System.out.println("Enter how much money you want to transfer:");
                    int transfermoney = sc.nextInt();
                    if (transfermoney > Integer.parseInt(connect.getBalance(cardNumber.toString()))) System.out.println("Not enough money!");
                    else {
                        connect.transfer(cardNumber.toString(), transferacc.toString(), transfermoney);
                        System.out.println("Success!");
                    }

                }

            }
            else if (ans == 4) {
                System.out.println("The account has been closed!");
                connect.closeAcc(cardNumber.toString());
                return false;
            }
        }

    }

    private static void createAccount() {
        Long accountIdentifier = 400000_000000000L + Math.abs(new Random().nextInt(999999999));
        int checkNum = luhnAlgoritm(accountIdentifier);
        Long cardNumber = accountIdentifier * 10 + checkNum;
        int PIN = new Random().nextInt(9000) + 1000;

        connect.insert(cardNumber.toString(), String.valueOf(PIN));

        System.out.println("Your card has been created\n" +
                            "Your card number:\n" +
                            cardNumber + "\n" +
                            "Your card PIN:\n" +
                            PIN);
    }

    private static int luhnAlgoritm(long accountIdentifier){
        int sum =0;
        int[] arr = new int[15];
        for (int i = 14; i >= 0; i--){
            arr[i] = (int) (accountIdentifier % 10);
            accountIdentifier = accountIdentifier / 10;
        }
        for (int i = 0; i < 15; i++){
            if (i % 2 == 0) {
                arr[i] = arr[i] * 2;
                if ((arr[i]) > 9) arr[i] = arr[i] - 9;
            }
            sum = sum + arr[i];
        }
        return 10 - (sum % 10);
    }
}
