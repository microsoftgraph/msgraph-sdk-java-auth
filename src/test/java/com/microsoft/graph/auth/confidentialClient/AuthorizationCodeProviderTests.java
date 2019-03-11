package com.microsoft.graph.auth.confidentialClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.microsoft.graph.auth.AuthConstants;
import com.microsoft.graph.auth.enums.NationalCloud;

import okhttp3.Request;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationCodeProviderTests {

	private  String CLIENT_ID = "CLIENT_ID";
	private  String REDIRECT_URL = "http://localhost";
	private  String SECRET = "CLIENT_SECRET";
	private List<String> SCOPES = Arrays.asList("user.read", "openid", "profile", "offline_access");
	private String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
	private NationalCloud NATIONAL_CLOUD = NationalCloud.Global;
	private String TENANT = AuthConstants.Tenants.Common;
	private String testmeurl = "https://graph.microsoft.com/v1.0/me/"; 

	@Test
	public void getAuthorizationCodeProviderTest() {
		AuthorizationCodeProvider authorizationCodeProvider = new AuthorizationCodeProvider(CLIENT_ID, SCOPES, AUTHORIZATION_CODE, REDIRECT_URL, SECRET);
		assertNotNull(authorizationCodeProvider);
	}

	@Test
	public void getAuthorizationCodeProviderWithNationalCloudTenantTest() {
		AuthorizationCodeProvider authorizationCodeProvider = new AuthorizationCodeProvider(CLIENT_ID, SCOPES, AUTHORIZATION_CODE, REDIRECT_URL, NATIONAL_CLOUD, TENANT, SECRET);
		assertNotNull(authorizationCodeProvider);
	}

	@Test
	public void getTokenRequestMessageTest() throws OAuthSystemException {
		String expected = "code=AUTHORIZATION_CODE&grant_type=authorization_code&scope=user.read+openid+profile+offline_access+&redirect_uri=http%3A%2F%2Flocalhost&client_secret=CLIENT_SECRET&client_id=CLIENT_ID";
		AuthorizationCodeProvider authorizationCodeProvider = new AuthorizationCodeProvider(CLIENT_ID, SCOPES, AUTHORIZATION_CODE, REDIRECT_URL, SECRET);
		OAuthClientRequest request = authorizationCodeProvider.getTokenRequestMessage(AUTHORIZATION_CODE);
		assertEquals(expected, request.getBody().toString());
	}

	@Test
	public void authenticateRequestTest() throws OAuthSystemException, OAuthProblemException {
		AuthorizationCodeProvider authorizationCodeProvider = Mockito.mock(AuthorizationCodeProvider.class);
		Mockito.when(authorizationCodeProvider.getTokenRequestMessage(AUTHORIZATION_CODE)).thenReturn(Mockito.mock(OAuthClientRequest.class));
		Request httpget = new Request.Builder().url(testmeurl).build();
		authorizationCodeProvider.authenticateRequest(httpget);
		String actual = httpget.header("Authorization");
		assertEquals("Bearer test_accessToken"	, actual);
	}

	@Test
	public void getAccessTokenNewRequestTest() throws OAuthSystemException, OAuthProblemException {
		AuthorizationCodeProvider authorizationCodeProvider = new AuthorizationCodeProvider(CLIENT_ID, SCOPES, AUTHORIZATION_CODE, REDIRECT_URL, SECRET);
		Request httpget = new Request.Builder().url(testmeurl).build();
		authorizationCodeProvider.authenticateRequest(httpget);
		String actualTokenparameter = httpget.header("Authorization");
		assertNotNull(actualTokenparameter);
	}

	@Test
	public void getAccessTokenNewRequestWithNationalCloudTenantTest() throws OAuthSystemException, OAuthProblemException {
		AuthorizationCodeProvider authorizationCodeProvider = new AuthorizationCodeProvider(CLIENT_ID, SCOPES, AUTHORIZATION_CODE, REDIRECT_URL, NATIONAL_CLOUD, TENANT, SECRET);
		Request httpget = new Request.Builder().url(testmeurl).build();
		authorizationCodeProvider.authenticateRequest(httpget);
		String actualTokenparameter = httpget.header("Authorization");
		assertNotNull(actualTokenparameter);
	}

}
