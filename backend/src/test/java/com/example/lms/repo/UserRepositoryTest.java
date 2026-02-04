package com.example.lms.repo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.lms.domain.User;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void saveAndFindByEmail_andExistsByEmail() {
    User u = new User();
    u.setName("John");
    u.setEmail("john@example.com");
    u.setPasswordHash("hash");

    userRepository.save(u);

    assertThat(userRepository.findByEmail("john@example.com")).isPresent();
    assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
  }
}
