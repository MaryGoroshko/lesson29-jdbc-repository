package dao;

import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class UserDao {

    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public int insertUser(User user) {
        final String insertTemplate =
                "INSERT INTO users(name,email) VALUES(?,?)";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, user.name);
            statement.setString(2, user.email);
            statement.executeUpdate();

            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert user");
            }
            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    public void updateUser(User user) {
        final String updateTemplate =
                "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setString(1, user.name);
            statement.setString(2, user.email);
            statement.setInt(3, user.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public Optional<User> getUserById(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM users WHERE id = " + id);
            if (!cursor.next()) {
                return Optional.empty();
            }
            return Optional.of(createUserFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    public Optional<User> getUserByName(String name) {
        final String template = "SELECT * FROM users" +
                " WHERE name = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setString(1, name);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }
            return Optional.of(createUserFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one user", e);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        final String template = "SELECT * FROM users" +
                " WHERE email = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setString(1, email);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }
            return Optional.of(createUserFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one user", e);
        }
    }

    public Collection<User> getAllUsers() {
        try (Statement statement = connection.createStatement()) {
            final Collection<User> users = new ArrayList<>();
            ResultSet cursor = statement.executeQuery("SELECT * FROM users");
            while (cursor.next()) {
                users.add(createUserFromCursorIfPossible(cursor));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch books", e);
        }
    }

    public void delete(int id) {
        final String template = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    private User createUserFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final User user = new User();
        user.id = cursor.getInt("id");
        user.name = cursor.getString("name");
        user.email = cursor.getString("email");
        return user;
    }
}
