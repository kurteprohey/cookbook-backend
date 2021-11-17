package com.testprojects.portfolio.errors

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        e: AccessDeniedException
    ) {
        throw e
    }
}
