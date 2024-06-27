package com.vishal.myscale;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class MyScaleDataLoader {

    public  void createTable() {

        String dropTableSQL = "DROP TABLE IF EXISTS default.myscale_cookgpt";
        String createTableSQL = """
                CREATE TABLE default.myscale_cookgpt
                (
                    id UInt64,
                    Recipe String,
                    "Total Time" String,    
                    Method String,
                    Category String,       
                    Ingredients String, 
                    method_feature Array(Float32),
                    CONSTRAINT vector_len CHECK length(method_feature) = 384
                )
                ORDER BY id
                """;
        Statement stmt = null;
        Connection connection = MyScaleConnection.getMyScaleConnection().getConnection();
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

            try {
                stmt.execute(dropTableSQL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Table dropped successfully.");

            // Execute the CREATE TABLE statement
            try {
                stmt.execute(createTableSQL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Table created successfully.");

    }
    public  void processRecords() {

        String inputFilePath = "train.csv";
        String outputFilePath = "processed_train.csv";

        try (CSVReader csvReader = new CSVReader(new FileReader(inputFilePath)); BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

            String[] header = csvReader.readNext(); // Read the header line
            if (header != null) {
                // Write the new header with an additional column
                bw.write(String.join(",", header) + ",method_feature");
                bw.newLine();
            }


            String[] columns;
            String insertSQL = """
                INSERT INTO default.myscale_cookgpt
                (id, Recipe, "Total Time", Method, Category, Ingredients, method_feature)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
            long id = 1; // Starting id for records
            Connection connection = MyScaleConnection.getMyScaleConnection().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            while ((columns = csvReader.readNext()) != null) {
                // Process each line

                if (columns.length >= 5) {
                    String recipe = columns[0];
                    String totalTime = columns[1];
                    String ingredients = columns[2];
                    String method = columns[3];
                    String category = columns[4];

                    // Placeholder for method_feature; replace with actual logic
                    float[] methodFeature = extractMethodFeature(method);
                    String methodFeatureStr = Arrays.toString(methodFeature);

                    // Write the processed line to the new file
                    bw.write(String.join(",", recipe, totalTime, ingredients, method, category, methodFeatureStr));
                    pstmt.setLong(1, id++);
                    pstmt.setString(2, recipe);
                    pstmt.setString(3, totalTime);
                    pstmt.setString(4, method);
                    pstmt.setString(5, category);
                    pstmt.setString(6, ingredients);
                    pstmt.setArray(7, connection.createArrayOf("Float32", convertToFloatObjectArray(methodFeature)));
                    pstmt.addBatch();
                    bw.newLine();
                }
            }
            pstmt.executeBatch();
            System.out.println("File processed and saved successfully!");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public void createIndex() {
        Connection connection = MyScaleConnection.getMyScaleConnection().getConnection();
        String alterTableQuery = "ALTER TABLE default.myscale_cookgpt " +
                "ADD VECTOR INDEX method_feature_index method_feature " +
                "TYPE MSTG " +
                "('metric_type=Cosine')";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(alterTableQuery);
            System.out.println("Vector index added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkIndex() {
        Connection connection = MyScaleConnection.getMyScaleConnection().getConnection();

        String getIndexStatusQuery = "SELECT status FROM system.vector_indices WHERE name='method_feature_index'";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(getIndexStatusQuery);

            if (rs.next()) {
                String status = rs.getString("status");
                System.out.println("Index build status: " + status);

                if ("Built".equals(status)) {
                    System.out.println("The vector index is built successfully.");
                } else {
                    System.out.println("The vector index is not built yet. Current status: " + status);
                }
            } else {
                System.out.println("No vector index found with the name 'method_feature_index'.");
            }
        }catch(Exception e) {
            throw new RuntimeException(e);
          }


    }

    private  Float[] convertToFloatObjectArray(float[] floatArray) {
        Float[] floatObjectArray = new Float[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) {
            floatObjectArray[i] = floatArray[i];
        }
        return floatObjectArray;
    }
    private  float[] extractMethodFeature(String method) {
        return CreateEmbedding.embed(method);
    }

    public static void main(String[] args) {
        MyScaleDataLoader loader = new MyScaleDataLoader();
        loader.createTable();
        loader.processRecords();
        loader.createIndex();
    }
}
