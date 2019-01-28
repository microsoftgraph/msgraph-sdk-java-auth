package com.microsoft.graph.auth;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.microsoft.graph.auth.enums.NationalCloud;

public class BaseAuthenticationTests {
	
	private  String CLIENT_ID = "CLIENT_ID";
	private  String REDIRECT_URL = "http://localhost";
	private  String SECRET = "CLIENT_SECRET";
	private List<String> SCOPES = Arrays.asList("user.read", "openid", "profile", "offline_access");
	private String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
	private NationalCloud NATIONAL_CLOUD = NationalCloud.Global;
	private String TENANT = AuthConstants.Tenants.Common;
	
	@Test
	public void testCloudListMap() {
		assertEquals(BaseAuthentication.CloudList.get("Global"), "https://login.microsoftonline.com/");
		assertEquals(BaseAuthentication.CloudList.get("China"), "https://login.chinacloudapi.cn/");
	}

	@Test
	public void getAuthorityTest() {
		String actual = BaseAuthentication.GetAuthority(NationalCloud.Global, AuthConstants.Tenants.Common);
		String expected = "https://login.microsoftonline.com/common";
		assertEquals(expected, actual);
	}
	
	@Test
	public void getScopesAsStringTest() {
		BaseAuthentication baseAuthentication = new BaseAuthentication(SCOPES, CLIENT_ID, BaseAuthentication.GetAuthority(NATIONAL_CLOUD, TENANT), REDIRECT_URL, NATIONAL_CLOUD, TENANT, SECRET);
		String actual = baseAuthentication.getScopesAsString();
		String expected = "user.read openid profile offline_access ";
		assertEquals(expected, actual);
	}
	
}
