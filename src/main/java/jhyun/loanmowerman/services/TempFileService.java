package jhyun.loanmowerman.services;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class TempFileService {
    public File createTempFile(String suffix) throws IOException {
        return File.createTempFile("temp-", suffix);
    }

    public void removeTempFile(File file) {
        if (file != null) {
            FileUtils.deleteQuietly(file);
        }
    }
}
