package com.management.cms.service.implement;

import com.management.cms.model.enitity.SequenceId;
import com.management.cms.service.GeneratorSeqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GeneratorSeqServiceImpl implements GeneratorSeqService {
    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    public GeneratorSeqServiceImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long getNextSequenceId(String key) {
        //get sequence id
        Query query = new Query(Criteria.where("_id").is(key));

        //increase sequence id by 1
        Update update = new Update().inc("seq", 1);

        //return new increased id
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);

        //this is the magic happened.
        SequenceId seqId = mongoOperations.findAndModify(query, update, options, SequenceId.class);

        return !Objects.isNull(seqId) ? seqId.getSeq() : 1;
    }
}
