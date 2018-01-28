/*******************************************************************************
 * Copyright (c) Aug 28, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.shiro;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.iff.infra.util.Assert;
import org.iff.infra.util.BaseCryptHelper;
import org.iff.infra.util.FCS;
import org.iff.infra.util.HttpHelper;
import org.iff.infra.util.Logger;
import org.iff.infra.util.MD5Helper;
import org.iff.infra.util.StringHelper;

import com.foreveross.common.application.AuthorizationApplication;
import com.foreveross.common.application.SystemApplication;

/**
 * Shiro Auth验证过滤器
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 11, 2016
 */
public class ShiroAuthAccessControlFilter extends AdviceFilter {

	@Inject
	@Named("systemApplication")
	SystemApplication systemApplication;
	@Inject
	@Named("authorizationApplication")
	AuthorizationApplication authorizationApplication;

	protected boolean preHandle(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String ip = HttpHelper.getRemoteIpAddr(request);
		/**
		 * value = md5(password)
		 * auth = base64(md5(value+reverse(value)))
		 */
		String token = request.getHeader("auth");
		if (StringUtils.isBlank(token)) {
			token = request.getParameter("auth");
		}
		String loginId = "";
		String password = "";
		if (!StringUtils.isBlank(token)) {
			try {
				String auth = new String(BaseCryptHelper.decodeBase64(token));
				if (!StringUtils.contains(auth, ':')) {
					return false;
				}
				loginId = StringUtils.substringBefore(auth, ":");
				password = StringUtils.substringAfter(auth, ":");
				Assert.notBlank(loginId);
				Assert.notBlank(password);
			} catch (Exception e) {
				return false;
			}
		}

		/**
		 * 登录。
		 */
		ShiroUser user = systemApplication.getShiroUserByLoginId(loginId);
		Assert.notNull(user);
		String loginPasswd = user.getLoginPasswd();
		Assert.notBlank(loginPasswd);

		boolean valid = password.equalsIgnoreCase(MD5Helper.string2MD5(loginPasswd + StringUtils.reverse(loginPasswd)));

		/**
		 * 如果登录成功，那就查看权限。
		 */
		if (valid) {
			//Set<String> roles = authorizationApplication.findAuthRoleByLoginId(loginId);
			Set<String> resources = authorizationApplication.findAuthResourceByLoginId(loginId);
			Permission permission = new UrlWildcardPermissionResolver().resolvePermission(request.getRequestURI());
			for (String r : resources) {
				if (new UrlWildcardPermission(r).implies(permission)) {
					valid = true;
					break;
				}
			}
		}

		/**
		 * 如果验证不通过，但IP是本地IP，可以授权访问。
		 */
		if (!valid) {
			//enable localhost, if you don't want to enable localhost use ShiroIpAccessControlFilter
			String[] ips = new String[] { "0:0:0:0:0:0:0:*", "127.0.0.*" };
			for (String aip : ips) {
				valid = StringHelper.wildCardMatch(ip, aip.trim());
				if (valid) {
					break;
				}
			}
		}
		if (valid) {
			Logger.debug(FCS.get("Accept zuul {0} access!", ip));
			return true;
		} else {
			Logger.debug(FCS.get("Deny zuul {0} access!", ip));
			return false;
		}
	}

	protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
			throws ServletException, IOException {
		super.cleanup(request, response, existing);
	}

}