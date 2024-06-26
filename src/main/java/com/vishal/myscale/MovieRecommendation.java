package com.vishal.myscale;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import org.json.JSONObject;

public class MovieRecommendation {

    public static void main(String[] args) {
        String url = "jdbc:clickhouse://msc-8cdd15a4.us-east-1.aws.myscale.com:443/default?ssl=true";
        String user = "vishalmysore_org_default";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();

            // Display the number of rows in the table
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM myscale_movies");
            rs.next();
            System.out.println("movies count: " + rs.getInt(1));

            rs = stmt.executeQuery("SELECT count(*) FROM myscale_users");
            rs.next();
            System.out.println("users count: " + rs.getInt(1));

            rs = stmt.executeQuery("SELECT count(*) FROM myscale_ratings");
            rs.next();
            System.out.println("ratings count: " + rs.getInt(1));

            // Select a random user
            rs = stmt.executeQuery("SELECT * FROM myscale_users ORDER BY rand() LIMIT 1");
            rs.next();
            int targetUserId = rs.getInt("userId");
            String targetUserVector = rs.getString("user_rating_vector");

            System.out.println("currently selected user id=" + targetUserId + " for movie recommendation\n");

            // User rating plot data
            rs = stmt.executeQuery("SELECT rating, count(movieId) FROM myscale_ratings WHERE userId = " + targetUserId + " GROUP BY rating");
            while (rs.next()) {
                int rating = rs.getInt(1);
                int count = rs.getInt(2);
                System.out.println("Rating: " + rating + ", Count: " + count);
            }

            // Query the database to find the top K recommended movies for target user
            int topK = 10;
            rs = stmt.executeQuery(
                    "SELECT movieId, title, genres, tmdbId, distance(movie_rating_vector, " + targetUserVector + ") AS dist "
                            + "FROM myscale_movies "
                            + "WHERE movieId NOT IN (SELECT movieId FROM myscale_ratings WHERE userId = " + targetUserId + ") "
                            + "ORDER BY dist DESC LIMIT " + topK);

            System.out.println("Top 10 movie recommendations with estimated ratings for user " + targetUserId);
            while (rs.next()) {
                int movieId = rs.getInt("movieId");
                String title = rs.getString("title");
                String genres = rs.getString("genres");
                double dist = rs.getDouble("dist");
                System.out.println("MovieId: " + movieId + ", Title: " + title + ", Genres: " + genres + ", Distance: " + dist);
            }

            // Count rated movies
            rs = stmt.executeQuery("SELECT count(movieId) FROM myscale_ratings WHERE userId = " + targetUserId);
            rs.next();
            int ratedCount = rs.getInt(1);

            // Query the database to find the top K highest-rated watched movies for user
            rs = stmt.executeQuery(
                    "SELECT movieId, genres, tmdbId, dist, rating "
                            + "FROM (SELECT * FROM myscale_ratings WHERE userId = " + targetUserId + ") AS ratings "
                            + "INNER JOIN ("
                            + "    SELECT movieId, genres, tmdbId, distance(movie_rating_vector, " + targetUserVector + ") AS dist "
                            + "    FROM myscale_movies "
                            + "    WHERE movieId IN (SELECT movieId FROM myscale_ratings WHERE userId = " + targetUserId + ") "
                            + "    ORDER BY dist DESC LIMIT " + ratedCount
                            + ") AS movie_info ON ratings.movieId = movie_info.movieId "
                            + "WHERE rating >= ("
                            + "    SELECT MIN(rating) FROM ("
                            + "        SELECT LEAST(rating) AS rating FROM myscale_ratings WHERE userId = " + targetUserId + " ORDER BY rating DESC LIMIT " + topK
                            + "    )"
                            + ") ORDER BY dist DESC LIMIT " + topK);

            System.out.println("Top 10 highest-rated movies along with their respective user scores and predicted ratings for the user " + targetUserId);
            while (rs.next()) {
                int movieId = rs.getInt("movieId");
                String genres = rs.getString("genres");
                double dist = rs.getDouble("dist");
                double rating = rs.getDouble("rating");
                System.out.println("MovieId: " + movieId + ", Genres: " + genres + ", Distance: " + dist + ", Rating: " + rating);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

