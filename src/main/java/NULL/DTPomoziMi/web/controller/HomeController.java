package NULL.DTPomoziMi.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@GetMapping("/")
	public String getHome() { return "index"; }

	@ResponseBody
	@GetMapping("/api/getCsrf")
	public ResponseEntity<?> get() { return ResponseEntity.ok(""); }

}
