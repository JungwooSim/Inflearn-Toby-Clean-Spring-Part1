package tobyspring.splearn.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

@Entity
@Getter
@ToString
@NoArgsConstructor
@NaturalIdCache
public class Member {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  @NaturalId
  private Email email;

  private String nickName;

  private String passwordHash;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  public static Member register(MemberRegisterRequest createRequest, PasswordEncoder passwordEncoder) {
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
