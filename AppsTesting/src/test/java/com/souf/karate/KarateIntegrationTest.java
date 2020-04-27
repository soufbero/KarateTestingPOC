package com.souf.karate;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;

public class KarateIntegrationTest {

    @BeforeAll
    static void initiateIntegrationPoints(){
        List<String> paths = new ArrayList<>();
        paths.add("classpath:karate/PreTests");
        String karateOutputPath = "target/surefire-reports";
        Results results = Runner.parallel(null,paths,1,karateOutputPath);
        generateReport(karateOutputPath);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    @Test
    void testFullPathParallel() {

        List<String> paths = Arrays.stream(
                System.getProperty("apps").split(","))
                .map(c -> "classpath:karate/" + c).collect(Collectors.toList());
        String karateOutputPath = "target/surefire-reports";
        Results results = Runner.parallel(null,paths,5,karateOutputPath);
        generateReport(karateOutputPath);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    private static void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);
        List<String> jsonPaths = new ArrayList(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File("target"), "demo");
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

}
