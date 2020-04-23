package com.souf.karate;

import com.intuit.karate.junit5.Karate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KarateIntegrationTest {

    @Karate.Test
    Karate testFullPath() {
        List<String> paths = Arrays.stream(
                System.getProperty("apps").split(","))
                .map(c -> "classpath:karate/" + c).collect(Collectors.toList());
        String[] pathsArray = new String[paths.size()];
        paths.toArray(pathsArray);
        return Karate.run(paths.toArray(pathsArray));
    }

}
