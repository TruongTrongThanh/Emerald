package org.emerald.comicapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.emerald.comicapi.config.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ComicApplicationTests {

	@Autowired
	GlobalVariable globalVariable;

	@Test
	public void contextLoads() throws Exception {
	}
}
