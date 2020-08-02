package vietvq.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;
	@Autowired
	private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
	private final String USERS_QUERY = "select email, password, active from user where email=?";
	private final String ROLES_QUERY = "select u.email, r.role_name from user u "
			+ "inner join user_role ur on (u.id = ur.user_id) inner join role r on (ur.role_id=r.role_id) "
			+ "where u.email=?";

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().usersByUsernameQuery(USERS_QUERY).authoritiesByUsernameQuery(ROLES_QUERY)
				.dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Cấu hình remember me, thời gian là 1296000 giây
		http.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(1296000);

		http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/img/**").permitAll().antMatchers("/login")
				.permitAll()
				.antMatchers("/admin/**").hasAuthority("admin")
				.antMatchers("/user/**").hasAuthority("customer")
				.antMatchers("/seller/**").hasAuthority("seller")
				.antMatchers("/signup").permitAll()
				.antMatchers("/home/**").permitAll().anyRequest().authenticated().and()
				.exceptionHandling().accessDeniedPage("/403")
				.and()
				.csrf().disable().formLogin()
				.successHandler(myAuthenticationSuccessHandler).loginPage("/login").failureUrl("/login?error=true")
				.usernameParameter("email").passwordParameter("password").and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").permitAll();
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);

		return db;
	}

}
