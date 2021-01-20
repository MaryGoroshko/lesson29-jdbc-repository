import models.*;

import java.util.Collection;
import java.util.Optional;

public interface IBookRepository {
    /**
     * @throws RuntimeException Если что-то пошло не так во время извлечения книг из хранилища.
     */
    Collection<Book> getAll();

    Optional<Book> getById(int id);

    Collection<Author> getAllAuthors();

    Optional<Author> getAuthorById(int id);

    Collection<Review> getAllReviews();

    Optional<User> getUserById(int id);

    /**
     * Сохраняет книгу с написавшим ее автором.
     * Если книга или автор не сохранены, то они добавляются в хранилище.
     * Если книга или автор сохранены, то они обновляются в хранилище.
     */
    void saveBookAndAuthor(Book book, Author author);

    void saveUser(User user);

    /**
     * Сохраняет отзыв с написавшим его пользователем к книге которая есть в базе.
     * Если отзыв или пользователь не сохранены, то они добавляются в хранилище.
     * Если отзыв или пользователь сохранены, то они обновляются в хранилище.
     */
    void saveUserAndReview(Book book, User user, Review review);

    void deleteBook(int id); //gjhgb,lgu
    void deleteBook(Book book);
    void deleteAuthor(int id);
    void deleteAuthor(Author author);
    void deleteReview(int id);
    void deleteUser(int id);
}
