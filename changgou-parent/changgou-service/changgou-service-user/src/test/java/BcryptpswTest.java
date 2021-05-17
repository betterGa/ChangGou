import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptpswTest {

    @Test
    public void encoder(){

        // 编码
        System.out.println(new BCryptPasswordEncoder().encode("2232"));
    }
}
