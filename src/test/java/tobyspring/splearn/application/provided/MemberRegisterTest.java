package tobyspring.splearn.application.provided;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
public record MemberRegisterTest(
  MemberRegister memberRegister,
  EntityManager entityManager
) {

  @Test
  void register() {
    Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

    assertThat(member.getId()).isNotNull();
    assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
  }

  @Test
  void duplicationEmailFail() {
    Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

    assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
        .isInstanceOf(DuplicationEmailException.class);
  }

  @Test
  void activate() {
    Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
    entityManager.flush();
    entityManager.clear();

    member = memberRegister.activate(member.getId());

    entityManager.flush();

    assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
  }

  @Test
  void memberRegisterRequestFail() {
    extracted(new MemberRegisterRequest("toby@splearn.app", "Toby", "secret"));
    extracted(new MemberRegisterRequest("toby@splearn.app", "Charlie", "secret"));
    extracted(new MemberRegisterRequest("toby@splearn.app", "Charlie__________________________", "secret"));
    extracted(new MemberRegisterRequest("tobysplearn.app", "Charlie", "secret"));
  }

  private void extracted(MemberRegisterRequest invalid) {
    assertThatThrownBy(() -> memberRegister.register(invalid))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
