package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDTO {

	private Long id;
	private String username;
	private String teamName;

	public MemberDTO(Long id, String username) {
		this.id = id;
		this.username = username;
	}
}
