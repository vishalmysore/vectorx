package com.vishal.myscale;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Log
public class MyScaleDataQuery {
    @Autowired
    MyScaleConnection myScaleConnection;
    public List queryResult(String queryStr) {
        Connection connection = myScaleConnection.getConnection();
        String query = "SELECT Recipe, Method, distance(method_feature, ?) as dist " +
                "FROM default.myscale_cookgpt " +
                "ORDER BY dist LIMIT ?";
        int topK = 2;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            Float[] embQuery = CreateEmbedding.embedAsObject(queryStr);

            Array array = connection.createArrayOf("Float32", embQuery);
            pstmt.setArray(1, array);
            pstmt.setInt(2, topK);

            try (ResultSet rs = pstmt.executeQuery()) {
                List<String[]> summaries = new ArrayList<>();
                while (rs.next()) {
                    String recipe = rs.getString("Recipe");
                    String method = rs.getString("Method");
                    summaries.add(new String[]{recipe, method});
                }

                // Print the summaries
                for (String[] summary : summaries) {
                    log.info("Recipe: " + summary[0] + ", Method: " + summary[1]);
                }
                return summaries;
            }
        } catch (SQLException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List queryData(String recipeName) {
        Connection connection = myScaleConnection.getConnection();
        String query = "SELECT *  " +
                "FROM default.myscale_cookgpt " +
                "where Recipe = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {




            pstmt.setString(1, recipeName);


            try (ResultSet rs = pstmt.executeQuery()) {
                List<String[]> summaries = new ArrayList<>();
                while (rs.next()) {
                    String recipe = rs.getString("Recipe");
                    String method = rs.getString("Method");
                    String method_feature = rs.getString("method_feature");
                    summaries.add(new String[]{recipe, method,method_feature});
                }

                // Print the summaries
                for (String[] summary : summaries) {
                    log.info("Recipe: " + summary[0] + ", Method: " + summary[1]);
                }
                return summaries;
            }
        } catch (SQLException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public long addRecipe(Recipe recipe) {
        String insertSQL = """
                INSERT INTO default.myscale_cookgpt
                (id, Recipe, "Total Time", Method, Category, Ingredients, method_feature)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        Random random = new Random();
        long id = random.nextInt() ; // Starting id for records
        Connection connection = myScaleConnection.getConnection();
        try {
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setLong(1, id);
            pstmt.setString(2, recipe.getReceipeName());
            pstmt.setString(3, recipe.getTotalTime());
            pstmt.setString(4, recipe.getMethod());
            pstmt.setString(5, recipe.getCategory());
            pstmt.setString(6, recipe.getIngridents());
            Float[] methodFeature = CreateEmbedding.embedAsObject(recipe.getMethod());
            pstmt.setArray(7, connection.createArrayOf("Float32",methodFeature));
            pstmt.executeUpdate();
            log.info("Recipe inserted successfully!");
        } catch (SQLException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        }
        return id;
    }
}
