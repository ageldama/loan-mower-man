package jhyun.loanmowerman.services;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashingService {

    @Autowired
    public PasswordHashingService() {
    }

    public String hash(final String input) {
        final HashFunction hf = Hashing.md5();
        final HashCode hc = hf.newHasher()
                .putString(input, Charsets.UTF_8)
                .hash();
        return hc.toString();
    }
}
