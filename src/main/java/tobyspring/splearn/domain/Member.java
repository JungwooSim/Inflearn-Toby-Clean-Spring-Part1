package tobyspring.splearn.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

@Getter
@ToString
public class Member {
  private Email email;

  private String nickName;

  private String passwordHash;

  private MemberStatus status;

  private Member() {

  }

  public static Member create(MemberCreateRequest createRequest, PasswordEncoder passwordEncoder) {
    Member member = new Member();

    member.email = new Email(createRequest.email());
    member.nickName = requireNonNull(createRequest.nickName());
    member.passwordHash = requireNonNull(passwordEncoder.encode(createRequest.password()));

    member.status = MemberStatus.PENDING;

    return member;
  }

  public void activate() {
    state(status == MemberStatus.PENDING, "PNEDING 상태가 아님");

    this.status = MemberStatus.ACTIVE;
  }

  public void deactivate() {
    state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아님");

    this.status = MemberStatus.DEACTIVATED;
  }

  public boolean verifyPassword(String secret, PasswordEncoder passwordEncoder) {
    return passwordEncoder.matches(secret, this.passwordHash);
  }

  public void changeNickname(String nickName) {
    this.nickName = requireNonNull(nickName);
  }

  public void changePassword(String password, PasswordEncoder passwordEncoder) {
    this.passwordHash = passwordEncoder.encode(requireNonNull(password));
  }

  public boolean isActive() {
    return this.status == MemberStatus.ACTIVE;
  }
}
