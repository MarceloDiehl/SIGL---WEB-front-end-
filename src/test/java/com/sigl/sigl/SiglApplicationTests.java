package com.sigl.sigl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Sobe a aplicação inteira com o banco H2 do perfil "test" (src/test/resources/application-test.properties)
@SpringBootTest
@ActiveProfiles("test")
class SiglApplicationTests {

	@Test
	void contextLoads() {
	}

}
