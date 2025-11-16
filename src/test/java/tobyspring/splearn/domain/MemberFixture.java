package tobyspring.splearn.domain;

public class MemberFixture {

  public static MemberRegisterRequest createMemberRegisterRequest(String mail) {
    return new MemberRegisterRequest(mail, "Charlie", "verysecret");
  }

  public static MemberRegisterRequest createMemberRegisterRequest() {
    return createMemberRegisterRequest("toby@splearn.app");
  }

  public static PasswordEncoder createPasswordEncoder() {
    return new PasswordEncoder() {
      @Override
      public String encode(String password) {
        return password.toUpperCase();
      }

      @Override
      public boolean matches(String password, String passwordHash) {
        return encode(password).equals(passwordHash);
      }
    };
  }
}
