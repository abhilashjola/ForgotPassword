package com.dakr.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.time.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dakr.model.User;
import com.dakr.repository.UserRepository;

//import ch.qos.logback.core.util.Duration;

@Service
public class UserServiceImp {

	

	public User saveDetails(User u1) {
		User u2=repo.save(u1);
		return u2;
	}
  private static final long EXPIRE_TOKEN_AFTER_MINUTES = 10;

  @Autowired
  public UserRepository repo;

  public String forgotPassword(String email) {
    Optional<User> userOptional = Optional.ofNullable(repo.findByEmail(email));

    if (!userOptional.isPresent()) {
      return "Invalid email id.";
    }

    User user = userOptional.get();
    
    user.setToken(generateToken());
    user.setTokenCreationDate(LocalDateTime.now());

    user = repo.save(user);

    return user.getToken();
  }

  public String resetPassword(String token, String password) {
    Optional<User> userOptional = Optional.ofNullable(repo.findByToken(token));

    if (!userOptional.isPresent()) {
      return "Invalid token.";
    }

    LocalDateTime tokenCreationDate = userOptional.get().getTokenCreationDate();

    if (isTokenExpired(tokenCreationDate)) {
      return "Token expired.";

    }

    User user = userOptional.get();

    user.setPassword(password);
    user.setToken(token);
    user.setTokenCreationDate(tokenCreationDate);

    repo.save(user);

    return "Your password successfully updated.";
  }

   /* Generate unique token. You may add multiple parameters to create a strong
   * token.
   * 
   * @return unique token
   */
  private String generateToken() {
    StringBuilder token = new StringBuilder();

    return token.append(UUID.randomUUID().toString()).append(UUID.randomUUID().toString()).toString();
  }

  
  /* Check whether the created token expired or not.
   * 
   * @param tokenCreationDate
   * @return true or false
   */
  private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

    LocalDateTime now = LocalDateTime.now();
    Duration diff = Duration.between(tokenCreationDate, now);

    return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
  }
}