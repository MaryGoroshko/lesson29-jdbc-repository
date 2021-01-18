package models;

import java.sql.Timestamp;

public class Review {
    public int id;
    public Timestamp date;
    public String text;
    public int userId;
    public int bookId;

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", userId=" + userId +
                ", bookId=" + bookId +
                '}' + "\n";
    }
}
