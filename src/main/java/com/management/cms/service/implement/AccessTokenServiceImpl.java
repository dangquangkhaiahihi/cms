package com.management.cms.service.implement;

import com.management.cms.model.enitity.AccessTokenMgo;
import com.management.cms.repository.AccessTokenMgoRepository;
import com.management.cms.service.AccessTokenService;
import com.management.cms.service.GeneratorSeqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AccessTokenServiceImpl implements AccessTokenService {
    @Autowired
    private AccessTokenMgoRepository accessTokenMgoRepository;

    @Autowired
    private GeneratorSeqService generatorSeqService;

    @Override
    public void save(AccessTokenMgo accessTokenMgo) throws Exception {
        accessTokenMgo.setCreatedAt(LocalDateTime.now());
        accessTokenMgo.setId(generatorSeqService.getNextSequenceId(accessTokenMgo.SEQUENCE_NAME));
        accessTokenMgoRepository.save(accessTokenMgo);
    }
}
