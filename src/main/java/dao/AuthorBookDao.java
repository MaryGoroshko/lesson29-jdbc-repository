package dao;

import models.AuthorBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class AuthorBookDao{

    private final Connection connection;

    public AuthorBookDao(Connection connection) {
        this.connection = connection;
    }

    public Collection<AuthorBook> getAllAuthorsAndBooks() {
        try (Statement statement = connection.createStatement()) {
            final Collection<AuthorBook> authorsBooks = new ArrayList<>();
            ResultSet cursor = statement.executeQuery("SELECT * FROM authors_books");
            while (cursor.next()) {
                authorsBooks.add(createAuthorBookFromCursorIfPossible(cursor));
            }
            return authorsBooks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch authors", e);
        }
    }

    public int insertAuthorBook(int authorId, int bookId) {
        final String insertTemplate =
                "INSERT INTO authors_books (author_id, book_id) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setInt(1, authorId);
            statement.setInt(2, bookId);
            statement.executeUpdate();
            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert ");
            }
            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert ", e);
        }
    }

    public Optional<AuthorBook> getAuthorBookById(int authorID, int bookID) {
        final String template = "SELECT * FROM authors_books" +
                " WHERE author_id = ? AND book_id = ?" +
                " LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, authorID);
            statement.setInt(2, bookID);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }
            return Optional.of(createAuthorBookFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch", e);
        }
    }

    public void updateAuthorBook(AuthorBook authorBook) {
        final String updateTemplate =
                "UPDATE authors_books SET author_id = ?, book_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setInt(1, authorBook.authorId);
            statement.setInt(2, authorBook.bookId);
            statement.setInt(3, authorBook.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update", e);
        }
    }

    private AuthorBook createAuthorBookFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final AuthorBook authorBook = new AuthorBook();
        authorBook.id = cursor.getInt("id");
        authorBook.authorId = cursor.getInt("author_id");
        authorBook.bookId = cursor.getInt("book_id");
        return authorBook;
    }
}
