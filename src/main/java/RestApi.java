import com.company.competitions.entity.Document;
import com.company.competitions.entity.DocumentVersion;
import com.company.competitions.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.ReferenceToEntitySupport;
import io.jmix.core.security.CurrentAuthentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/service")
public class RestApi {

}

