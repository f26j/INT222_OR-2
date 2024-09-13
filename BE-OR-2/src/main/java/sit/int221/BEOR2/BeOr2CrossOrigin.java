package sit.int221.BEOR2;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://ip23or2.sit.kmutt.ac.th",
    "http://intproj23.sit.kmutt.ac.th"
})
public @interface BeOr2CrossOrigin {
}
