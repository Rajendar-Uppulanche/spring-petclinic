package org.springframework.samples.petclinic.spec;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple controller to hold the specification version.
 *
 * @author Dave Syer
 */
@Controller
@RequestMapping("/spec")
public class SpecController {

    public static final String SPECIFICATION_VERSION = "0.0.52";

}
