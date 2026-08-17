package org.springframework.samples.petclinic.visit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/search")
    public String initFindForm(Model model) {
        model.addAttribute("visitFilterCriteria", new VisitFilterCriteria());
        model.addAttribute("visits", visitService.findAllVisits());
        return "visits/visitsList";
    }

    @GetMapping("/search/results")
    public String processFindForm(@ModelAttribute VisitFilterCriteria visitFilterCriteria, Model model) {
        List<Visit> visits = visitService.findVisitsByCriteria(visitFilterCriteria);
        model.addAttribute("visits", visits);
        model.addAttribute("visitFilterCriteria", visitFilterCriteria);
        return "visits/visitsList";
    }
}