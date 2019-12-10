package gov.nci.ppe.configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;

/**
 * @author PublicisSapient
 * @version 1.0 
 * @since   2019-07-22 
 */
@Configuration
public class DozerConfig {
	
	@Bean(name = "dozerBean")
	@Scope("singleton")
	public Mapper configDozer(@Value(value = "classpath*:*dozer-mappings.xml") Resource[] resourceArray) throws IOException {
	    List<String> mappingFileUrlList = new ArrayList<>();
	    for (Resource resource : resourceArray) {
	        mappingFileUrlList.add(String.valueOf(resource.getURL()));
	    }
	    DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
	    dozerBeanMapper.setMappingFiles(mappingFileUrlList);
	    return dozerBeanMapper;
	}	

}








