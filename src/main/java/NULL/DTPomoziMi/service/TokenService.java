package NULL.DTPomoziMi.service;

public interface TokenService {

	void updateToken(String token, String email);

	String getTokenByEmail(String email);

}
