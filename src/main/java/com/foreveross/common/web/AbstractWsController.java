/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.ActionHelper;
import org.iff.infra.util.FCS;
import org.iff.infra.util.GsonHelper;
import org.iff.infra.util.JsonHelper;
import org.iff.infra.util.TypeConvertHelper;
import org.iff.infra.util.TypeConvertHelper.TypeConvert;
import org.iff.infra.util.XStreamHelper;
import org.iff.infra.util.spring.SpringContextHelper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foreveross.common.ResultBean;
import com.foreveross.common.application.Authorization;

/**
 * CommonWsController
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
public abstract class AbstractWsController extends BaseController {

	/**
	 * <pre>
	 * map:
	 *     key   = beanName@@methodName
	 *     value = List<Method>
	 * </pre>
	 */
	private static final Map<String, List<Method>> beanMehtodMap = new HashMap<String, List<Method>>();

	/**
	 * invoke bean method and return xml result, only accept "params" parameter name and json value or xstream xml value.
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Jul 19, 2016
	 */
	@RequestMapping(value = "/json/{beanName}/{methodName}/**", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String toJson(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			ActionHelper actionHelper = ActionHelper.create(request, response);
			String requestURI = request.getRequestURI();
			String reportPath = StringUtils.substringAfter(requestURI, "/json/");
			String[] pathSplit = StringUtils.split(reportPath, "/");
			String beanName = pathSplit[0];
			String methodName = pathSplit[1];
			Object bean = null;
			{
				try {
					bean = SpringContextHelper.getBean(beanName);
				} catch (Exception e) {
				}
				if (bean == null) {
					return GsonHelper.toJsonString(
							ResultBean.error().setBody(FCS.get("can't find bean[{0}]!", beanName).toString()));
				}
			}
			Map<String, Object> conditionParams = new LinkedHashMap<String, Object>();
			{
				for (int i = 2; i < pathSplit.length; i++) {
					String tmp = pathSplit[i];
					String[] tmpSplit = StringUtils.split(tmp, "=");
					if (tmpSplit.length == 1) {
						conditionParams.put("arg" + (i - 2), actionHelper.urlDecode(tmpSplit[0]));
					} else if (tmpSplit.length != 2) {
						continue;
					} else {
						conditionParams.put(tmpSplit[0], actionHelper.urlDecode(tmpSplit[1]));
					}
				}
			}
			{
				Enumeration<String> names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String name = names.nextElement();
					conditionParams.put(name, request.getParameter(name));
				}
				conditionParams.putAll(modelMap);
			}
			String value = invokeToString(beanName, methodName, bean, conditionParams, GsonHelper.class);
			return value;
		} catch (Exception ex) {
			return GsonHelper.toJsonString(
					ResultBean.error().setBody(FCS.get("get json error: {0}", ex.getStackTrace()).toString()));
		}
	}

	/**
	 * invoke bean method and return xml result, only accept "params" parameter name and json value or xstream xml value.
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
	 * @since Jul 19, 2016
	 */
	@RequestMapping(value = "/xml/{beanName}/{methodName}/**", produces = "application/xml;charset=UTF-8")
	@ResponseBody
	public String toXml(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			ActionHelper actionHelper = ActionHelper.create(request, response);
			String requestURI = request.getRequestURI();
			String reportPath = StringUtils.substringAfter(requestURI, "/xml/");
			String[] pathSplit = StringUtils.split(reportPath, "/");
			String beanName = pathSplit[0];
			String methodName = pathSplit[1];
			Object bean = null;
			{
				try {
					bean = SpringContextHelper.getBean(beanName);
				} catch (Exception e) {
				}
				if (bean == null) {
					return XStreamHelper
							.toXml(ResultBean.error().setBody(FCS.get("can't find bean[{0}]!", beanName).toString()));
				}
			}
			Map<String, Object> conditionParams = new LinkedHashMap<String, Object>();
			{
				for (int i = 2; i < pathSplit.length; i++) {
					String tmp = pathSplit[i];
					String[] tmpSplit = StringUtils.split(tmp, "=");
					if (tmpSplit.length == 1) {
						conditionParams.put("arg" + (i - 2), actionHelper.urlDecode(tmpSplit[0]));
					} else if (tmpSplit.length != 2) {
						continue;
					} else {
						conditionParams.put(tmpSplit[0], actionHelper.urlDecode(tmpSplit[1]));
					}
				}
			}
			{
				conditionParams.putAll(modelMap);
			}
			return invokeToString(beanName, methodName, bean, conditionParams, XStreamHelper.class);
		} catch (Exception ex) {
			return GsonHelper.toJsonString(
					ResultBean.error().setBody(FCS.get("get xml error: {0}", ex.getStackTrace()).toString()));
		}
	}

	private String invokeToString(String beanName, String methodName, Object bean, Map<String, Object> conditionParams,
			Class<?> xmlOrJsonHelper) {
		List<Method> methods = findMethod(beanName, methodName, bean);
		{
			if (methods.isEmpty()) {
				return toXmlOrJson(xmlOrJsonHelper, ResultBean.error()
						.setBody(FCS.get("can't find bean[{0}] method[{1}]!", beanName, methodName).toString()));
			} else if (methods.size() > 1) {
				return toXmlOrJson(xmlOrJsonHelper,
						ResultBean.error().setBody(
								FCS.get("can't find bean[{0}] contains multiple method[{1}], method name must be unique!",
										beanName, methodName).toString()));
			}
		}
		Method method = methods.get(0);
		Class<?>[] parameterTypes = method.getParameterTypes();
		int paramCount = parameterTypes.length;
		List<Object> listArgs = new ArrayList<Object>();
		{
			for (int i = 0; i < paramCount; i++) {
				listArgs.add(conditionParams.get("arg" + i));
			}
		}
		{
			try {
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> clazz = parameterTypes[i];
					Object arg = listArgs.get(i);
					if (arg == null) {
						continue;
					}
					if (arg instanceof String) {
						String paramStr = ((String) arg).trim();
						if (paramStr.startsWith("<")) {
							arg = XStreamHelper.fromXml(paramStr);
						} else {
							TypeConvert convert = TypeConvertHelper.me().get(clazz.getName());
							if (convert == null || "null".equalsIgnoreCase(convert.getName())) {
								arg = JsonHelper.toObject(clazz, paramStr);
							} else {
								arg = convert.convert(clazz.getName(), paramStr, String.class, null);
							}
						}
						listArgs.set(i, arg);
					} else if (clazz.isInstance(arg)) {
						continue;
					} else {
						listArgs.set(i, TypeConvertHelper.me().get(clazz.getName()).convert(clazz.getName(), arg,
								arg.getClass(), null));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return toXmlOrJson(xmlOrJsonHelper, ResultBean.error().setBody(e.toString()));
			}
		}
		{
			try {
				Object invoke = method.invoke(bean, listArgs.toArray());
				return toXmlOrJson(xmlOrJsonHelper, ResultBean.success().setBody(invoke));
			} catch (Throwable e) {
				e.printStackTrace();
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				return toXmlOrJson(xmlOrJsonHelper, ResultBean.error().setBody(sw.toString()));
			}
		}
	}

	private String toXmlOrJson(Class<?> xmlJsonHelper, ResultBean result) {
		if (XStreamHelper.class.isAssignableFrom(xmlJsonHelper)) {
			return XStreamHelper.toXml(result);
		} else if (GsonHelper.class.isAssignableFrom(xmlJsonHelper)) {
			return GsonHelper.toJsonString(result);
		}
		return null;
	}

	private String beanMethodKey(String beanName, String methodName) {
		return beanName + "@@" + methodName;
	}

	private List<Method> findMethod(String beanName, String methodName, Object bean) {
		String beanMethodKey = beanMethodKey(beanName, methodName);
		beanMehtodMap.clear();
		List<Method> list = beanMehtodMap.get(beanMethodKey);
		if (list != null) {
			return list;
		}
		{//授权判断 //TODO 没有做会话级【角色】的判断，如果要做会话级的判断，这一段前置几行代码。
			Authorization authorization = SpringContextHelper.getApplicationContext().findAnnotationOnBean(beanName,
					Authorization.class);
			if (!canAccessBean(authorization, beanName, getRoles())) {
				return list;
			}
		}
		list = new ArrayList<Method>();
		List<Class<?>> clazzList = new ArrayList<Class<?>>();
		try {
			ApplicationContext applicationContext = SpringContextHelper.getApplicationContext();
			ConfigurableApplicationContext xml = (ConfigurableApplicationContext) applicationContext;
			DefaultListableBeanFactory bf = (DefaultListableBeanFactory) xml.getBeanFactory();
			String className = bf.getBeanDefinition(beanName).getBeanClassName();
			Class<?> loadClass = xml.getClassLoader().loadClass(className);
			Class<?>[] interfaces = loadClass.getInterfaces();
			clazzList.addAll(Arrays.asList(interfaces));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		{
			for (Class<?> cls : clazzList) {
				if (cls.getName().endsWith("Application")) {
					Method[] methods = cls.getDeclaredMethods();
					for (Method m : methods) {
						map.put(m.getName(), Boolean.TRUE);
						if (methodName.equals(m.getName()) && Modifier.isPublic(m.getModifiers())) {
							list.add(m);
						}
					}
				}
			}
		}
		synchronized (beanMehtodMap) {
			beanMehtodMap.put(beanMethodKey, list);
		}
		return list;
	}

	List<String> getRoles() {
		return Collections.EMPTY_LIST;
	}

	abstract boolean canAccessBean(Authorization authorization, String beanName, List<String> roles);
}
