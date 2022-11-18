package NULL.DTPomoziMi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import NULL.DTPomoziMi.repository.UserRepo;
import NULL.DTPomoziMi.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private UserRepo userRepo;//TokenDAO tokenDAO;

	@Override
	public void updateToken(String token, String email) {
		email = email == null ? null : email.trim();

		userRepo.updateToken(token, email);
	}

	@Override
	public String getTokenByEmail(String email) {
		email = email == null ? null : email.trim();

		return userRepo.getTokenByEmail(email);
	}
}
