package org.gauravagrwl.financeData;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories("org.gauravagrwl.financeData.model")
@EnableAsync
@OpenAPIDefinition(info = @Info(title = "Personal Finance Data API"))
public class FinanceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceAppApplication.class, args);
    }

    //TODO: 1. Add validation for ENUMS in Rest controller REQUEST.
    //TODO: 2. Decide where the duplicate indicator should be. (In transaction statement or in Statement Model.
    //TODO: 3. NEed to store the amount as negative from transactions. But at time of inserting into Statement make it to ABS. NEed to this to handle the transactions.

}

//import java.io.*;
//        import java.net.URL;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FileService {
//
//    public InputStream downloadZip(String urlString) throws IOException {
//        URL url = new URL(urlString);
//        return url.openStream();
//    }
//
//    public InputStream extractCsvFromZip(InputStream zipStream, String csvFileName) throws IOException {
//        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
//            ZipEntry entry;
//            while ((entry = zis.getNextEntry()) != null) {
//                if (entry.getName().equalsIgnoreCase(csvFileName)) {
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = zis.read(buffer)) > 0) {
//                        out.write(buffer, 0, length);
//                    }
//                    return new ByteArrayInputStream(out.toByteArray());
//                }
//            }
//        }
//        throw new FileNotFoundException("CSV file not found in ZIP");
//    }
//}
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//public interface CsvRecordRepository extends MongoRepository<CsvRecord, String> {
//}
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//@Document
//public class CsvRecord {
//    @Id
//    private String id;
//    private String field1;
//    private String field2;
//    // Add more fields as per your CSV structure
//}
//
//
//import com.opencsv.CSVReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class CsvService {
//
//    @Autowired
//    private CsvRecordRepository csvRecordRepository;
//
//    public void loadCsvToMongo(InputStream csvStream) throws IOException {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream));
//             CSVReader csvReader = new CSVReader(reader)) {
//
//            List<CsvRecord> records = new ArrayList<>();
//            String[] nextLine;
//            while ((nextLine = csvReader.readNext()) != null) {
//                CsvRecord record = new CsvRecord();
//                record.setField1(nextLine[0]);
//                record.setField2(nextLine[1]);
//                // Map remaining fields
//                records.add(record);
//            }
//            csvRecordRepository.saveAll(records);
//        }
//    }
//}
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.InputStream;
//
//@RestController
//public class FileController {
//
//    @Autowired
//    private FileService fileService;
//
//    @Autowired
//    private CsvService csvService;
//
//    @GetMapping("/load-csv")
//    public String loadCsv(@RequestParam String url, @RequestParam String csvFileName) {
//        try {
//            InputStream zipStream = fileService.downloadZip(url);
//            InputStream csvStream = fileService.extractCsvFromZip(zipStream, csvFileName);
//            csvService.loadCsvToMongo(csvStream);
//            csvStream.close(); // Clean up
//            zipStream.close();
//            return "CSV loaded into MongoDB successfully!";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error loading CSV: " + e.getMessage();
//        }
//    }
//}
//
//# Use an official OpenJDK image as a base
//FROM openjdk:17-jdk-slim
//
//# Add a volume to store data permanently
//VOLUME /tmp
//
//# Copy the application JAR
//COPY target/myapp.jar app.jar
//
//# Expose the application port
//EXPOSE 8080
//
//        # Run the application
//ENTRYPOINT ["java","-jar","/app.jar"]
//
//version: '3.8'
//
//services:
//mongodb:
//image: mongo
//container_name: mongodb
//ports:
//        - "27017:27017"
//volumes:
//        - mongo_data:/data/db
//
//spring_app:
//build: .
//container_name: spring_app
//ports:
//        - "8080:8080"
//depends_on:
//        - mongodb
//environment:
//        - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/yourdbname
//
//volumes:
//mongo_data:
//
//mvn clean package
//
//docker-compose up --build
