package org.dsahu.langchain.learning.user.respository;


import org.dsahu.langchain.learning.user.entity.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository
        extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByUsername(String username);
}
