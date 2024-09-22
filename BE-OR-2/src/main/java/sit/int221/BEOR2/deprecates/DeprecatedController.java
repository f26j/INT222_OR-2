package sit.int221.BEOR2.deprecates;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;

@Deprecated
@RestController
@RequestMapping("")
@BeOr2CrossOrigin
public class DeprecatedController {
    @RequestMapping(value = "/v2/tasks/**", method = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE
    })
    @ResponseStatus(HttpStatus.OK)
    public String[] deprecatedTasks() {
        return new String[]{};
    }

    @RequestMapping(value = "/v2/statuses/**", method = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE
    })
    @ResponseStatus(HttpStatus.OK)
    public String[] deprecatedStatuses() {
        return new String[]{};
    }
}
