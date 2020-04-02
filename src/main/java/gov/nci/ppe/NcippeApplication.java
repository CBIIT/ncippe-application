package gov.nci.ppe;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author debsarka0
 *
 */
@SpringBootApplication
@Configuration
@EnableSwagger2
public class NcippeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NcippeApplication.class, args);
	}

	@Bean
	public Docket api() throws FileNotFoundException, IOException, XmlPullParserException {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("gov.nci.ppe.controller")).paths(PathSelectors.any())
				.build().apiInfo(new ApiInfo("NCI PPE Api Documentation", "Documentation automatically generated", null,
						null, new Contact("PublicisSapient", null, null), null, null));

	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("messages/errors/error");
		return messageSource;
	}

}
