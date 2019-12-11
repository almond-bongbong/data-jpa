package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

	@Autowired
	MemberJpaRepository memberJpaRepository;

	@Test
	public void testMember() {
		Member member = new Member("cmlee");
		Member savedMember = memberJpaRepository.save(member);
		Member findMember = memberJpaRepository.find(savedMember.getId());

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");

		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);

		// 단건 조회 검증
		Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
		Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> all = memberJpaRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberJpaRepository.delete(member2);
		Optional<Member> findDeletedMember = memberJpaRepository.findById(member2.getId());
		assertThat(findDeletedMember.isEmpty()).isTrue();
	}

	@Test
	public void findByUsernameAndAgeGreaterThen() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);

		memberJpaRepository.save(m1);
		memberJpaRepository.save(m2);

		List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

		assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void findByPage() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 10);
		Member m3 = new Member("CCC", 10);
		Member m4 = new Member("DDD", 10);
		Member m5 = new Member("EEE", 10);
		memberJpaRepository.save(m1);
		memberJpaRepository.save(m2);
		memberJpaRepository.save(m3);
		memberJpaRepository.save(m4);
		memberJpaRepository.save(m5);

		int age = 10;
		int offset = 0;
		int limit = 3;
		List<Member> result = memberJpaRepository.findByPage(age, offset, limit);
		long totalCount = memberJpaRepository.totalCount(age);

		assertThat(result.size()).isEqualTo(3);
		assertThat(result.get(0).getUsername()).isEqualTo("EEE");
		assertThat(result.get(1).getUsername()).isEqualTo("DDD");
		assertThat(result.get(2).getUsername()).isEqualTo("CCC");
		assertThat(totalCount).isEqualTo(5);
	}
}