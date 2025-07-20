package unibo.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@EnableScheduling
public class ServiceAGUIController {
	@Value("${spring.application.name}")
    String appName;

	@GetMapping("/")
    public String homePage(Model model) {
        // Valori iniziali (verranno sovrascritti via WebSocket)
        model.addAttribute("currentWeight", 0);
        model.addAttribute("MAX_WEIGHT", 60);
        return "serviceaccessGUI";
    }

}