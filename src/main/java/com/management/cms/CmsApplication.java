package com.management.cms;

import com.management.cms.constant.Commons;
import com.management.cms.model.enitity.AreaDoc;
import com.management.cms.model.enitity.UserDoc;
import com.management.cms.repository.AreaRepository;
import com.management.cms.repository.PermissionRepository;
import com.management.cms.repository.RoleRepository;
import com.management.cms.repository.UserRepository;
import com.management.cms.service.GeneratorSeqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
@EnableMongoRepositories
public class CmsApplication {
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private GeneratorSeqService generatorSeqService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	public static void main(String[] args) {
		SpringApplication.run(CmsApplication.class, args);
	}

	@PostConstruct
	protected void init(){
//		//tạo permission
//		PermissionDoc permission1 = new PermissionDoc();
//		permission1.setCode(EPermission.BUSINESS_PREMISES_VIEW.getCode());
//		permission1.setName(EPermission.BUSINESS_PREMISES_VIEW.getName());
//		permission1.setStatus(Commons.STATUS_ACTIVE);
//
//		PermissionDoc permission2 = new PermissionDoc();
//		permission2.setCode(EPermission.BUSINESS_PREMISES_CREATE.getCode());
//		permission2.setName(EPermission.BUSINESS_PREMISES_CREATE.getName());
//		permission2.setStatus(Commons.STATUS_ACTIVE);
//
//		PermissionDoc permission3 = new PermissionDoc();
//		permission3.setCode(EPermission.BUSINESS_PREMISES_EDIT.getCode());
//		permission3.setName(EPermission.BUSINESS_PREMISES_EDIT.getName());
//		permission3.setStatus(Commons.STATUS_ACTIVE);
//
//		permission1.setId(generatorSeqService.getNextSequenceId(permission1.SEQUENCE_NAME));
//		permissionRepository.save(permission1);
//		permission2.setId(generatorSeqService.getNextSequenceId(permission2.SEQUENCE_NAME));
//		permissionRepository.save(permission2);
//		permission3.setId(generatorSeqService.getNextSequenceId(permission3.SEQUENCE_NAME));
//		permissionRepository.save(permission3);

//		PermissionDoc permission1 = permissionRepository.findById(1L).get();
//		PermissionDoc permission2 = permissionRepository.findById(2L).get();
//
//
//		//tạo role
//		RoleDoc role = new RoleDoc();
//		role.setCode("SUB_ADMIN");
//		role.setDescription("SUB_ADMIN");
//		role.setStatus(Commons.STATUS_ACTIVE);
//		role.getPermissions().add(permission1);
//		role.getPermissions().add(permission2);
//		role.setId(generatorSeqService.getNextSequenceId(role.SEQUENCE_NAME));
//		roleRepository.save(role);
//
//		//tạo user
//		UserDoc user = new UserDoc();
//		user.setEmail("sub_admin@gmail.com");
//		user.setCreatedAt(LocalDateTime.now());
//		user.setUpdatedAt(LocalDateTime.now());
//		user.setFirstName("Đặng Quang");
//		user.setLastName("Khải");
//		user.setPhoneNumber("0559261020");
//		user.setEnabled(Commons.ENABLED);
//		user.setPassword(passwordEncoder.encode(Commons.DEFAULT_PASSWORD));
//		user.setRole(role);
//		user.setId(generatorSeqService.getNextSequenceId(user.SEQUENCE_NAME));
//		userRepository.save(user);

			UserDoc user = userRepository.findByEmail("dang.quang.khai2610@gmail.com").get();
		user.setPassword(passwordEncoder.encode(Commons.DEFAULT_PASSWORD));
		user.setFailCount(0);
		userRepository.save(user);
	}

}
