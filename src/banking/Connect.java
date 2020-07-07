package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connect {
    private Connection conn = null;

    public void connect(String fileName){
        String url = "jdbc:sqlite:"+ fileName;
        try {
            conn = DriverManager.getConnection(url);
            //System.out.println("connected to database");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String createTableCard = "CREATE TABLE IF NOT EXISTS card (id INTEGER, number TEXT, pin TEXT, balance INTEGER DEFAULT 0);";
        try {
            conn.createStatement().execute(createTableCard);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String number, String pin){
        String insertStatement = "INSERT INTO card (number, pin) VALUES("+number+","+pin+");";
        try {
            conn.prepareStatement(insertStatement).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkPin (String number, String pin){
        String getpin = "SELECT pin FROM card WHERE (number ='"+number+"');";
        try {
            ResultSet rs = conn.createStatement().executeQuery(getpin);
            while (rs.next()) return rs.getString("pin").equals(pin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getBalance(String number) {
        String getbalance = "SELECT balance FROM card WHERE number ="+number+";";
        try {
            ResultSet rs = conn.prepareStatement(getbalance).executeQuery();
            return rs.getString("balance");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return "00000";
    }

    public void addIncome(String number, int income) {
        String addincome = "UPDATE card SET balance = balance + "+income+" WHERE number = "+number+";";
        try {
            conn.createStatement().executeUpdate(addincome);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void closeAcc(String number) {
        String closeacc = "DELETE FROM card WHERE number = "+number+";";
        try {
            conn.createStatement().executeUpdate(closeacc);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean checkCard(String transferacc) {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT number FROM card WHERE number = " + transferacc + ";");
            while (rs.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void transfer(String cardNumber, String transferacc, int transfermoney) {
        String minusmoney = "UPDATE card SET balance = balance - "+transfermoney+" WHERE number = "+cardNumber+";";
        String plusmoney = "UPDATE card SET balance = balance + "+transfermoney+" WHERE number = "+transferacc+";";
        try {
            conn.createStatement().executeUpdate(minusmoney);
            conn.createStatement().executeUpdate(plusmoney);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
