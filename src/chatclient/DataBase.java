/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package chatclient;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
 *
 * @author DANIEL
 */
public class DataBase {
    
    //db config
    protected static final String URL = "jdbc:mysql://localhost:3306/";
    protected static final String DATABASE = URL + "java_chat";
    protected static final String USERNAME = "root";
    protected static final String PASSWORD = "";
    private static DataBase DB = null;
    
    protected Connection con;
    
    //session variables
    private static int Id;
    
    private DataBase(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
//            
//            Statement stmt = con.createStatement();
//            ResultSet rs = stmt.executeQuery("select * from emp");
//            
//            while(rs.next())
//                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Cannot connect to database (" + DATABASE +")", "Server Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static DataBase getDataBase(){
        if(DB == null)
            DB = new DataBase();
        
        return DB;
    }
    
    public boolean addUser(String username){
        try {
            String sql = "INSERT INTO users(name) VALUES(?);";
            
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            //Statement.RETURN_GENERATED_KEYS
            
            if(ps.executeUpdate() > 0){
                //get inserted id and set session variables
                try{
                    ResultSet rs = ps.getGeneratedKeys();
                    
                    if(rs.next()){
                        this.Id = rs.getInt(1);
                        return true;
                    } else {
                        return false;
                    }
                    
                } catch(SQLException ex){
                    JOptionPane.showMessageDialog(null, "SQL error: cannot retrieve session ID", "Server Error", JOptionPane.ERROR_MESSAGE);;
                    return false;
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "SQL error: no rows inserted", "Server Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "SQL error: " + ex, "Server Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public void logMessage(int userId, String message){
        String sql = "INSERT INTO chat_log (user_id, message, date) VALUES(?,?,?);";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Id);
            ps.setString(2, message);
            ps.setTimestamp(3, new Timestamp(new Date().getTime()));

            if(ps.executeUpdate() == 0){
                System.out.println("Message not recorded");
            }
        } catch (SQLException ex) {
            System.out.println("Error in logging chat message: " + ex);
        }
    }
    
    public ArrayList<String> getChatHistory(){
        String sql = "SELECT users.name, chat_log.message FROM chat_log INNER JOIN users ON chat_log.user_id = users.id ORDER BY date";
        
        try {
            ArrayList<String> messages = new ArrayList<String>();
            CallableStatement cs = con.prepareCall(sql);
            ResultSet result = cs.executeQuery();
            
            while(result.next()){
                String message = result.getString(1) + ": " + result.getString(2);
                messages.add(message);
            }
            
            return messages;
        } catch (SQLException ex) {
            System.out.println("Cannot fetch chat history: " + ex);
            return null;
        }
    }
    
    public int getSessionId(){
        return this.Id;
    }
    
    public void closeConnection() throws SQLException{
        con.close();
    }
}
