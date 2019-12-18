package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDTO;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TeamRepository teamRepository;

	@PersistenceContext
	EntityManager em;

	@Test
	public void memberTest() {
		Member member = new Member("maxx");
		Member savedMember = memberRepository.save(member);

		Member findMember = memberRepository.findById(savedMember.getId()).get();

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");

		memberRepository.save(member1);
		memberRepository.save(member2);

		// 단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();

		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> all = memberRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberRepository.delete(member2);
		Optional<Member> findDeletedMember = memberRepository.findById(member2.getId());
		assertThat(findDeletedMember.isEmpty()).isTrue();
	}

	@Test
	public void findByUsernameAndAgeGreaterThen() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

		assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void testQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findUser("AAA", 10);
		assertThat(result.get(0)).isEqualTo(m1);
	}

	@Test
	public void findUsernameList() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<String> result = memberRepository.findUsernameList();
		assertThat(result.get(0)).isEqualTo("AAA");
		assertThat(result.get(1)).isEqualTo("BBB");
	}

	@Test
	public void findMemberDTO() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamB);

		memberRepository.save(member1);
		memberRepository.save(member2);

		List<MemberDTO> result = memberRepository.findMemberDTO();
		assertThat(result.get(0).getTeamName()).isEqualTo(member1.getTeam().getName());
	}

	@Test
	public void findMemberByNames() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		Member m3 = new Member("CCC", 30);

		memberRepository.save(m1);
		memberRepository.save(m2);
		memberRepository.save(m3);

		List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	public void returnType() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result1 = memberRepository.findListByUsername("AAA");
		assertThat(result1.size()).isEqualTo(1);

		Member result2 = memberRepository.findMemberByUsername("AAA");
		assertThat(result2).isEqualTo(m1);

		Optional<Member> result3 = memberRepository.findOptionalByUsername("BBB");
		assertThat(result3.get()).isEqualTo(m2);
	}

	@Test
	public void findByPage() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 10);
		Member m3 = new Member("CCC", 10);
		Member m4 = new Member("DDD", 10);
		Member m5 = new Member("EEE", 10);
		memberRepository.save(m1);
		memberRepository.save(m2);
		memberRepository.save(m3);
		memberRepository.save(m4);
		memberRepository.save(m5);

		int age = 10;
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

		Page<Member> result = memberRepository.findByAge(age, pageRequest);
		Page<MemberDTO> page = result.map(member -> new MemberDTO(member.getId(), member.getUsername()));

		assertThat(page.getContent().size()).isEqualTo(3);
		assertThat(page.getContent().get(0).getUsername()).isEqualTo("EEE");
		assertThat(page.getContent().get(1).getUsername()).isEqualTo("DDD");
		assertThat(page.getContent().get(2).getUsername()).isEqualTo("CCC");

//		assertThat(result.getContent().get(0).getUsername()).isEqualTo("BBB");
//		assertThat(result.getContent().get(1).getUsername()).isEqualTo("AAA");
//		assertThat(result.getTotalElements()).isEqualTo(5);
	}

	@Test
	public void bulkUpdate() {
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));

		int resultCount = memberRepository.bulkAgePlus(20);
		assertThat(resultCount).isEqualTo(3);

		Optional<Member> findMember = memberRepository.findOptionalByUsername("member5");
		assertThat(findMember.get().getAge()).isEqualTo(41);
	}

	@Test
	public void findMemberLazy() {
		// Given
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		teamRepository.save(teamA);
		teamRepository.save(teamB);
		memberRepository.save(new Member("member1", 10, teamA));
		memberRepository.save(new Member("member2", 20, teamB));

		em.flush();
		em.clear();

		// When
		List<Member> members = memberRepository.findAll();

		// Then
		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member's team = " + member.getTeam().getName());
		}
	}

	@Test
	public void queryHint() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		Member findMember = memberRepository.findReadOnlyByUsername("member1");
		findMember.setUsername("member2");

		em.flush();
	}

	@Test
	public void lock() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		List<Member> members = memberRepository.findLockByUsername("member1");
	}

	@Test
	public void callCustom() {
		List<Member> members = memberRepository.findMemberCustom();

		assertThat(members).isEmpty();
	}

	@Test
	public void specBasic() {
		Team teamA = new Team("teamA");
		em.persist(teamA);

		Member m1 = new Member("m1", 10, teamA);
		Member m2 = new Member("m2", 20, teamA);

		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamMember("teamA"));
		List<Member> result = memberRepository.findAll(spec);

		assertThat(result.size()).isEqualTo(1);
	}
}