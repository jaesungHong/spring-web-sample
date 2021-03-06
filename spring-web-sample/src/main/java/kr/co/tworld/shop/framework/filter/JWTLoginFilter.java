package kr.co.tworld.shop.framework.filter;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.tworld.shop.framework.model.AccountCredentials;
import kr.co.tworld.shop.framework.security.model.User;
import kr.co.tworld.shop.framework.security.service.TokenAuthenticationService;

/**
 * JWT Login filter
 * @author Sangjun, Park
 *
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	@Autowired
	private TokenAuthenticationService authenticationService;
	
	@Autowired
	private ObjectMapper objectMapper;

	public JWTLoginFilter(final RequestMatcher requestMatcher, final AuthenticationManager authenticationManager) {
		super(requestMatcher);
		this.setAuthenticationManager(authenticationManager);
		this.setAuthenticationFailureHandler((req, resp, e) -> {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.flushBuffer();
		});
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		final AccountCredentials account = this.objectMapper.readValue(request.getInputStream(), AccountCredentials.class);
		return this.getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword(), new ArrayList<>()));
	}

	@Override
	protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain, Authentication authResult) throws IOException, ServletException {
		this.authenticationService.addAuthentication(response, (User) authResult.getPrincipal());
	}

}
