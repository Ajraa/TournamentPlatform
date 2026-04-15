package cz.ajraa.tournament;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "jwt.secret=test-tajny-klic-pro-testy-minimalne-32-znaku")
class SpringApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
