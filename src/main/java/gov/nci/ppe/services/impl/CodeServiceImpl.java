package gov.nci.ppe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.repository.CodeRepository;
import gov.nci.ppe.services.CodeService;

/**
 * Service class to orchestrate all the action related to <table>Code</table>
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-09
 */
@Component
public class CodeServiceImpl implements CodeService {
	
	private CodeRepository codeRepository;

	public CodeServiceImpl() { }

	@Autowired
	public CodeServiceImpl(CodeRepository codeRepo) {
		super();
		this.codeRepository = codeRepo;
	}

	@Override
	public Code getCode(String codeName) {
		return codeRepository.findByCodeName(codeName);
		
	}

}
