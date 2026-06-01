package com.gym.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.lang.NonNull;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;
    private final Environment env;

    public CustomErrorController(ErrorAttributes errorAttributes, Environment env) {
        this.errorAttributes = errorAttributes;
        this.env = env;
    }

    @RequestMapping("/error")
    public String handleError(@NonNull HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = 500;
        if (statusObj != null) {
            try {
                statusCode = Integer.parseInt(statusObj.toString());
            } catch (NumberFormatException ignored) {}
        }

        Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(
            new ServletWebRequest(request),
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE,
                                     ErrorAttributeOptions.Include.EXCEPTION)
        );

        String message;
        if (statusCode == 400) {
            
            return "redirect:/";
        } else if (statusCode == 403) {
            message = "No tienes permiso para acceder a esta página.";
        } else if (statusCode == 404) {
            return "redirect:/";
        } else {
            Object raw = errorDetails.get("message");
            message = (raw != null && !raw.toString().isBlank())
                ? raw.toString()
                : "Ocurrió un error inesperado. Revisa los logs del servidor.";
        }

        String title = HttpStatus.resolve(statusCode) != null
            ? HttpStatus.resolve(statusCode).getReasonPhrase()
            : "Error";

        model.addAttribute("status", statusCode);
        model.addAttribute("title", title);
        model.addAttribute("message", message);

        // Mostrar traza y detalles sólo en perfiles de desarrollo/local
        if (env != null && env.acceptsProfiles(Profiles.of("dev", "local"))) {
            Object trace = errorDetails.get("trace");
            model.addAttribute("trace", trace != null ? trace.toString() : null);
            model.addAttribute("errorDetails", errorDetails);
        }
        return "error";
    }
}
