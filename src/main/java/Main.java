import models.Author;
import models.Book;
import models.Review;
import models.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (SQLException e) {
            System.out.println("Failed to do something: " + e.getLocalizedMessage());
        }
    }

    private void run() throws SQLException {
        try (Connection connection =
                     DriverManager.getConnection("jdbc:sqlite:books.db");) {
            doWork(connection);
        }
    }

    private void doWork(Connection connection) throws SQLException {
        // TODO work with connection

        createTables(connection);
        IBookRepository repository = new JdbcBookRepository(connection);

        final Author author1 = new Author();
        author1.name = "J.R.R.Tolkien";
        author1.birthYear = 1901;

        final Book book1 = new Book();
        book1.title = "The Lord of the Rings";
        book1.publishYear = 1940;
        book1.price = new BigDecimal("3000.33");

        final User user1 = new User();
        user1.name = "Max";
        user1.email = "mx@mail.com";

        final Review review1 = new Review();
        review1.date = new Timestamp(System.currentTimeMillis());
        review1.text = "A great book.";

        final Author author2 = new Author();
        author2.name = "Harvey Deitel";
        author2.birthYear = 1960;

        final Author author3 = new Author();
        author3.name = "Paul Deitel";
        author3.birthYear = 1950;

        final Book book2 = new Book();
        book2.title = "Java for beginners";
        book2.publishYear = 2005;
        book2.price = new BigDecimal("100500700.255123");

        final Review review2 = new Review();
        review2.date = new Timestamp(System.currentTimeMillis());
        review2.text = "Great book for beginners.";

        final Book book3 = new Book();
        book3.title = "C++ How to Program";
        book3.publishYear = 2020;
        book3.price = new BigDecimal("720.0002");

        final User user2 = new User();
        user2.name = "Leo";
        user2.email = "leo@mail.com";

        final Review review3 = new Review();
        review3.date = new Timestamp(System.currentTimeMillis());
        review3.text = "It is a good book to learn programming";

        repository.saveBookAndAuthor(book1, author1);
        repository.saveBookAndAuthor(book2, author2);
        repository.saveBookAndAuthor(book3, author2);
        repository.saveBookAndAuthor(book3, author3);

        repository.saveUserAndReview(book1, user1, review1);
        repository.saveUserAndReview(book2, user1, review2);
        repository.saveUserAndReview(book3, user2, review3);

        Collection<Book> books = repository.getAll();
        System.out.println("Books count:" + books.size());

        Collection<Review> reviews = repository.getAllReviews();
        System.out.println("Reviews: \n" + reviews);
    }

    public final String CreateBooksTableQuery = "CREATE TABLE IF NOT EXISTS books (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " title VARCHAR(100)," +
            " publish_year INTEGER," +
            " price DECIMAL(10,2)," +
            " author_id INTEGER" +
            ")";

    public final String CreateAuthorsTableQuery = "CREATE TABLE IF NOT EXISTS authors (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name VARCHAR(100)," +
            " birth_year INTEGER" +
            ")";

    public final String CreateReviewsTableQuery = "CREATE TABLE IF NOT EXISTS reviews (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " date DATE,"+
            " text VARCHAR(500)," +
            " user_id INTEGER,"+
            " book_id INTEGER"+
            ")";

    public final String CreateUsersTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name VARCHAR(100)," +
            " email VARCHAR(50)" +
            ")";

    public final String CreateAuthorsBooksTableQuery = "CREATE TABLE IF NOT EXISTS authors_books (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " author_id INTEGER," +
            " book_id INTEGER," +
            " FOREIGN KEY (author_id) REFERENCES authors(id)," +
            " FOREIGN KEY (book_id) REFERENCES books(id)" +
            ")";

    private void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CreateAuthorsTableQuery);
            statement.executeUpdate(CreateBooksTableQuery);
            statement.executeUpdate(CreateReviewsTableQuery);
            statement.executeUpdate(CreateUsersTableQuery);
            statement.executeUpdate(CreateAuthorsBooksTableQuery);
        }
    }
}
