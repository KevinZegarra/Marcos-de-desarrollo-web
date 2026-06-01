package com.gym.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Normaliza URLs con doble slash (//) redirigiéndolas a la versión limpia.
 * Corre ANTES de Spring Security para que "/" quede permitida.
 */
@Component
@Order(1)
public class UrlNormalizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpRequest)
                || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        String uri = httpRequest.getRequestURI();

        // Solo actuar si hay doble slash real (no la raíz sola)
        if (uri.contains("//")) {
            String clean = uri.replaceAll("/{2,}", "/");
            // Si quedó igual o es la raíz, dejar pasar sin redirigir
            if (!clean.equals(uri)) {
                String qs = httpRequest.getQueryString();
                String location = httpRequest.getContextPath() + clean
                        + (qs != null ? "?" + qs : "");
                httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                httpResponse.setHeader("Location", location);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
