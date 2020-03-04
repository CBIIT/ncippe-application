package gov.nci.ppe.configurations;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;

/**
 * @author PublicisSapient
 * @version 1.0 
 * @since   2019-07-22 
 */
@Configuration
public class DozerConfig {
	
	@Bean(name = "dozerBean")
	@Scope("singleton")
	public DozerBeanMapperFactoryBean dozerMapper(ResourcePatternResolver resourcePatternResolver) throws IOException {

		DozerBeanMapperFactoryBean dozerBeanMapper = new DozerBeanMapperFactoryBean();

		dozerBeanMapper.setMappingFiles(resourcePatternResolver.getResources("classpath*:*dozer-mappings.xml"));
	    return dozerBeanMapper;
	}	

}








