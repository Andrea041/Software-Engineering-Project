package com.example.codiceprogetto.logic.dao;

import com.example.codiceprogetto.logic.entities.User;
import com.example.codiceprogetto.logic.enumeration.UserType;
import com.example.codiceprogetto.logic.exception.DAOException;
import com.example.codiceprogetto.logic.utils.DBConnectionFactory;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends AbsUserDAO {
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";

    private User newUser(ResultSet rs) throws SQLException {
        User user;
        UserType userType;

        if (rs.getString("userType").equals("customer"))
            userType = UserType.CUSTOMER;
        else
            userType = UserType.SELLER;

        user = new User(rs.getString(EMAIL),
                        rs.getString(PASSWORD),
                        userType,
                        rs.getString(NAME),
                        rs.getString(SURNAME));

        return user;
    }

    public int insertUser(String email, String password, String userType, String name, String surname) throws DAOException {
        int result;
        String query = "INSERT INTO User (email, password, userType, name, surname) VALUES (?, ?, ?, ?, ?)";

        result = registerUser(email, password, userType, name, surname, query);

        if (result > 0) {
            Logger.getAnonymousLogger().log(Level.INFO, "New row in DB");
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Insertion failed");
        }

        return result;
    }

    public User findUser(String email) {
        Connection conn = DBConnectionFactory.getConn();
        ResultSet rs = null;
        User user = null;
        String sql = "SELECT * FROM User WHERE " + EMAIL + " = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (!rs.first()) {
                return null;
            }

            rs.first();

            user = newUser(rs);
        } catch(SQLException e) {
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
            }
        }

        return user;
    }
}
