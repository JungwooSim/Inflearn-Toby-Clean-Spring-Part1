package tobyspring.splearn.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tobyspring.splearn.domain.MemberFixture.createMemberRegisterRequest;
import static tobyspring.splearn.domain.MemberFixture.createPasswordEncoder;

class MemberTest {
  Member member;
  PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    this.passwordEncoder = createPasswordEncoder();

    member = Member.register(createMemberRegisterRequest(), passwordEncoder);
  }

  @Test
  void registerMember() {
    assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
  }

  @Test
  void activate() {
    member.activate();

    assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
  }

  @Test
  void activateFail() {
    member.activate();

    assertThatThrownBy(member::activate).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void deactivate() {
    member.activate();

    member.deactivate();

    assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
  }

  @Test
  void deactivateFail() {
    assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);

    member.activate();
    member.deactivate();

    assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void verifyPassword() {
    assertThat(member.verifyPassword("verysecret", passwordEncoder)).isTrue();
    assertThat(member.verifyPassword("hello", passwordEncoder)).isFalse();
  }

  @Test
  void changeNickname() {
    assertThat(member.getNickName()).isEqualTo("Charlie");

    member.changeNickname("Charlie2");

    assertThat(member.getNickName()).isEqualTo("Charlie2");
  }

  @Test
  void changePassword() {
    member.changePassword("verysecret2", passwordEncoder);

    assertThat(member.verifyPassword("verysecret2", passwordEncoder)).isTrue();
  }

  @Test
  void isActive() {
    assertThat(member.isActive()).isFalse();

    member.activate();
    assertThat(member.isActive()).isTrue();

    member.deactivate();
    assertThat(member.isActive()).isFalse();
  }

  @Test
  void invalidEmail() {
    assertThatThrownBy(() ->
     Member.register(createMemberRegisterRequest("invalid Email"), passwordEncoder)
    ).isInstanceOf(IllegalArgumentException.class);

    Member.register(createMemberRegisterRequest(), passwordEncoder);
  }


}