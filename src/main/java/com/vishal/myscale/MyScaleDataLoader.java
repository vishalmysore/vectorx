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

    private Connection connection;

    private void initConnection() {
        String url = "jdbc:clickhouse://msc-8cdd15a4.us-east-1.aws.myscale.com:443/default?ssl=true";
        String user = "vishalmysore_org_default";
        String password = System.getenv("pass");
        try  {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private  void createTable() {

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
        loader.initConnection();
        loader.createTable();
        loader.processRecords();
    }
}
