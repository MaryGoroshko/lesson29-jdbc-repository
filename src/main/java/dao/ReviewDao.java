package dao;

import models.Book;
import models.Review;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ReviewDao {

    private final Connection connection;

    public ReviewDao(Connection connection) {
        this.connection = connection;
    }

    public int insertReview(Review review) {
        final String insertTemplate =
                "INSERT INTO reviews(date,text,user_id,book_id) VALUES(?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setTimestamp(1, review.date);
            statement.setString(2, review.text);
            statement.setInt(3, review.userId);
            statement.setInt(4, review.bookId);
            statement.executeUpdate();
            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert review");
            }
            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert review", e);
        }
    }

    public void updateReview(Review review) {
        final String updateTemplate =
                "UPDATE reviews SET date=?, text=?, user_id=?, book_id=? WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setTimestamp(1, review.date);
            statement.setString(2, review.text);
            statement.setInt(3, review.userId);
            statement.setInt(4, review.bookId);
            statement.setInt(5, review.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update review", e);
        }
    }

    public Collection<Review> getAllReviews() {
        try (Statement statement = connection.createStatement()) {
            final Collection<Review> reviews = new ArrayList<>();
            ResultSet cursor = statement.executeQuery("SELECT * FROM reviews");
            while (cursor.next()) {
                reviews.add(createReviewFromCursorIfPossible(cursor));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch reviews", e);
        }
    }

//  One-to-many connection
    public Collection<Review> getReviewsForBook(Book book) {
        final String template = "SELECT * FROM reviews" +
                " JOIN books ON books.id = reviews.book_id" +
                " WHERE books.id = ? ";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            final Collection<Review> reviews = new ArrayList<>();
            statement.setInt(1, book.id);
            ResultSet cursor = statement.executeQuery();
            while (cursor.next()) {
                reviews.add(createReviewFromCursorIfPossible(cursor));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch reviews", e);
        }
    }

//  One-to-many connection
    public Collection<Review> getReviewsFromUser(User user) {
        final String template = "SELECT * FROM reviews" +
                " JOIN users ON users.id = reviews.user_id" +
                " WHERE users.id = ? ";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            final Collection<Review> reviews = new ArrayList<>();
            statement.setInt(1, user.id);
            ResultSet cursor = statement.executeQuery();
            while (cursor.next()) {
                reviews.add(createReviewFromCursorIfPossible(cursor));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one review", e);
        }
    }

    public Optional<Review> getReviewByUserAndText(int userId, String text) {
        final String template = "SELECT * FROM reviews" +
                " WHERE user_id = ? AND text = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, userId);
            statement.setString(2, text);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }
            return Optional.of(createReviewFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one review", e);
        }
    }

    public void delete(int id) {
        final String template = "DELETE FROM reviews WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete review", e);
        }
    }

    private Review createReviewFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final Review review = new Review();
        review.id = cursor.getInt("id");
        review.date = cursor.getTimestamp("date");
        review.text = cursor.getString("text");
        review.userId = cursor.getInt("user_id");
        review.bookId = cursor.getInt("book_id");
        return review;
    }
}
