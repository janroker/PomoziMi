package NULL.DTPomoziMi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HTML5Controller {

	/*
	 * all paths that do not contain a period (and are not explicitly mapped
	 * already) are React routes, and should forward to the home page:
	 */

	@RequestMapping("/**/{path:[^\\.]+}")
    public String forward() {
        return "forward:/";
    }

}
