package test;

import static org.junit.Assert.*;
import static play.test.Helpers.testServer;

import java.io.IOException;
import java.util.List;

import models.User;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.avaje.ebean.Ebean;

import org.mindrot.jbcrypt.BCrypt;

import play.libs.Yaml;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;

public class BaseTest {
	
	public static FakeApplication app;
	public static String createDdl = "";
	public static String dropDdl = "";
	public final static String TEST_FILE_FOLDER = "test/files/";
	public String userId;

	@BeforeClass
	public static void startApp() throws IOException {
		
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		
		Helpers.start(app); 
		
		String evolutions = FileUtils.readFileToString(app.getWrappedApplication().getFile("conf/evolutions/default/1.sql"));
		String[] splittedEvolutionContent = evolutions.split("# --- !Ups");
	    String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
	    createDdl = upsDowns[0];
	    dropDdl = upsDowns[1];
	}

	@AfterClass
	public static void stopApp() {
		Helpers.stop(app);
	}
	
	@Before
	public void createCleanDb() {
		Ebean.execute(Ebean.createCallableSql(dropDdl));
		Ebean.execute(Ebean.createCallableSql(createDdl));
		
		Ebean.save((List) Yaml.load("test-data.yml"));
		
		
		// TODO: Write the hashed password directly in yaml file?
		// hash passwords
		List<User> users = User.find.all();
		for(User user : users) {
			user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
			user.update();
		}
		
		// Get id from database (User id changes between tests)
				userId = String.valueOf(User.find.where().eq
					("username", "robindough").findUnique().id); 
		
	}

}
