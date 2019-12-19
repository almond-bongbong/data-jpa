package study.datajpa.repository;

public class UsernameOnlyDTO {

	private final String username;

	public UsernameOnlyDTO(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
