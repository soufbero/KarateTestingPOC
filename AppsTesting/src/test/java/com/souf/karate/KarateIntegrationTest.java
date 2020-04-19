package com.souf.karate;

import com.intuit.karate.junit5.Karate;

public class KarateIntegrationTest {

    @Karate.Test
    Karate testFullPath() {
        return Karate.run("classpath:karate");
    }

}
