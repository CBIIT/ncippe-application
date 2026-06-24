package gov.nci.ppe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 *
 * @author debsarka0
 *
 */
@SpringBootApplication
@Configuration
public class NcippeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NcippeApplication.class, args);
	}

	@Bean
	public OpenAPI ncippeOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("NCI PPE API Documentation")
				.description("Documentation automatically generated")
				.version("1.0"));
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("messages/errors/error", "messages/emails/email");
		return messageSource;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
