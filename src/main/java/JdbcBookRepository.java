import dao.*;
import models.*;

import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;

public class JdbcBookRepository implements IBookRepository {

    private static final int INVALID_ID = 0;

    private final AuthorDao authorDao;
    private final BookDao bookDao;
    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final AuthorBookDao authorBookDao;

    public JdbcBookRepository(Connection connection) {
        bookDao = new BookDao(connection);
        authorDao = new AuthorDao(connection);
        reviewDao = new ReviewDao(connection);
        userDao = new UserDao(connection);
        authorBookDao = new AuthorBookDao(connection);
    }

    @Override
    public Collection<Book> getAll() {
        return bookDao.getAllBooks();
    }

    @Override
    public Optional<Book> getById(int id) {
        return bookDao.getBookById(id);
    }

    @Override
    public Collection<Author> getAllAuthors() {
        return authorDao.getAllAuthors();
    }

    @Override
    public Optional<Author> getAuthorById(int id) {
        return authorDao.getAuthorById(id);
    }

    @Override
    public Collection<Review> getAllReviews() {
        return reviewDao.getAllReviews();
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public void saveBookAndAuthor(Book book, Author author) {

        if (author.id == INVALID_ID) {
            Optional<Author> matchingAuthor = authorDao.getAuthorByName(author.name);
            if (matchingAuthor.isPresent()) {
                author.id = matchingAuthor.get().id;
                authorDao.updateAuthor(author);
            } else {
                author.id = authorDao.insertAuthor(author);
            }
        } else {
            authorDao.updateAuthor(author);
        }

        book.authorId = author.id;

        if (book.id == INVALID_ID) {
            Optional<Book> matchingBook =
                    bookDao.getBookByTitleAndAuthor(book.title, author.name);
            if (matchingBook.isPresent()) {
                book.id = matchingBook.get().id;
                bookDao.updateBook(book);
            } else {
                book.id = bookDao.insertBook(book);
            }
        } else {
            bookDao.updateBook(book);
        }

        AuthorBook authorBook = new AuthorBook();
        if (authorBook.id == INVALID_ID) {
            Optional<AuthorBook> matchingID =
                    authorBookDao.getAuthorBookById(authorBook.authorId, authorBook.bookId);
            if (matchingID.isPresent()) {
                authorBook.id = matchingID.get().id;
                authorBookDao.updateAuthorBook(authorBook);
            } else {
                authorBook.id = authorBookDao.insertAuthorBook(author.id, book.id);
            }
        } else {
            authorBookDao.updateAuthorBook(authorBook);
        }
    }

    @Override
    public void saveUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public void saveUserAndReview(Book book, User user, Review review) {
        review.bookId = book.id;

        if (user.id == INVALID_ID) {
            Optional<User> matchingUser = userDao.getUserByEmail(user.email);
            if (matchingUser.isPresent()) {
                user.id = matchingUser.get().id;
                userDao.updateUser(user);
            } else {
                user.id = userDao.insertUser(user);
            }
        } else {
            userDao.updateUser(user);
        }

        review.userId = user.id;

        if (review.text.length() != INVALID_ID) {
            if (review.id == INVALID_ID) {
                Optional<Review> matchingReview =
                        reviewDao.getReviewByUserAndText(review.userId, review.text);
                if (matchingReview.isPresent()) {
                    review.id = matchingReview.get().id;
                    reviewDao.updateReview(review);
                } else {
                    reviewDao.insertReview(review);
                }
            } else {
                reviewDao.updateReview(review);
            }
        }
    }

    @Override
    public void deleteBook(int id) {
        bookDao.delete(id);
    }

    @Override
    public void deleteBook(Book book) {
        bookDao.delete(book.id);
    }

    @Override
    public void deleteAuthor(int id) {
        authorDao.delete(id);
    }

    @Override
    public void deleteAuthor(Author author) {
        authorDao.delete(author.id);
    }

    @Override
    public void deleteReview(int id) {
        reviewDao.delete(id);
    }

    @Override
    public void deleteUser(int id) {
        userDao.delete(id);
    }
}
