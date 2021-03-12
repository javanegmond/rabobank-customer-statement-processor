package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class SpringBootRabobank {

	private static ConfigurableApplicationContext app;

	public static void main(String[] args) throws InterruptedException {
		System.out.println(Arrays.toString(args));
//		System.out.println("before run");
		app = SpringApplication.run(SpringBootRabobank.class, args);
//		System.out.println("after run");
//		System.out.println(app.isActive());
//		while (!app.isActive()) {
//			System.out.println("starting Spring Boot...");
//			Thread.sleep(1000);
//		}
	}

	public static void startApp() throws InterruptedException {
		main(new String[0]);
		System.out.println(app.isActive());
		System.out.println("*******************************");
//		while (!app.isActive()) {
//			System.out.println("starting Spring Boot...");
//			Thread.sleep(1000);
//		}
	}

	public static void stopApp() {
		app.stop();
	}
}
