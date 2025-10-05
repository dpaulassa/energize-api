package br.com.fiap.energizeapi.repository;

import br.com.fiap.energizeapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}