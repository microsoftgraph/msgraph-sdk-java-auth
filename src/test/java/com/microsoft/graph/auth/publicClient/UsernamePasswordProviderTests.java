package com.microsoft.graph.auth.publicClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
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
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.httpcore.IAuthenticationProvider;


@RunWith(MockitoJUnitRunner.class)
public class UsernamePasswordProviderTests {
	
	private  String CLIENT_ID = "CLIENT_ID";
	private List<String> SCOPES = Arrays.asList("user.read", "openid", "profile", "offline_access");
	private String USERNAME = "USERNAME";
	private String PASSWORD = "PASSWORD";
	private NationalCloud NATIONAL_CLOUD = NationalCloud.Global;
	private String CLIENT_SECRET = "CLIENT_SECRET";

	@Test
	public void getTokenRequestMessageTest() throws OAuthSystemException {
		String expected = "password=PASSWORD&grant_type=password&scope=user.read+openid+profile+offline_access+&client_id=CLIENT_ID&username=USERNAME";
		UsernamePasswordProvider usernamePasswordProvider = new UsernamePasswordProvider(CLIENT_ID, SCOPES, USERNAME, PASSWORD);
		OAuthClientRequest request = usernamePasswordProvider.getTokenRequestMessage();
		assertEquals(expected, request.getBody().toString());
	}
	
	@Ignore
	@Test
	public void authenticateRequestTest() throws OAuthSystemException, OAuthProblemException {
		UsernamePasswordProvider usernamePasswordProvider = Mockito.mock(UsernamePasswordProvider.class);
		Mockito.when(usernamePasswordProvider.getTokenRequestMessage()).thenReturn(Mockito.mock(OAuthClientRequest.class));
		Mockito.when(usernamePasswordProvider.getAccessTokenNewRequest(Mockito.any())).thenReturn("test_accessToken");
		HttpGet httpget = new HttpGet("https://graph.microsoft.com/v1.0/me/");
		usernamePasswordProvider.authenticateRequest(httpget);
		String actualtokenParameter = httpget.getFirstHeader("Authorization").getValue();
		assertEquals("Bearer test_accessToken"	, actualtokenParameter);
	}
	
	@Ignore
	@Test
	public void getAccessTokenNewRequestTest() throws OAuthSystemException, OAuthProblemException {
		UsernamePasswordProvider usernamePasswordProvider = new UsernamePasswordProvider(CLIENT_ID, SCOPES, USERNAME, PASSWORD);
		String actualAccessToken = usernamePasswordProvider.getAccessTokenNewRequest(usernamePasswordProvider.getTokenRequestMessage());
		assertNotNull(actualAccessToken);
	}
	
	@Ignore
	@Test
	public void publicClientWithClientSecretTest() throws OAuthSystemException, OAuthProblemException {
		java.util.List<String> s = Arrays.asList("user.read", "openid", "profile", "offline_access");
		IAuthenticationProvider authenticationProvider = 
				new UsernamePasswordProvider(CLIENT_ID, 
											SCOPES, 
											USERNAME, 
											PASSWORD, 
											NATIONAL_CLOUD, 
											AuthConstants.Tenants.Organizations, 
											CLIENT_SECRET);
		callGraph(authenticationProvider);
	}
	
	public static void callGraph(IAuthenticationProvider authenticationProvider) {
		CloseableHttpClient httpclient = HttpClients.createDefault(authenticationProvider);
		HttpGet httpget = new HttpGet("https://graph.microsoft.com/v1.0/me/");
		HttpClientContext localContext = HttpClientContext.create();
		HttpResponse response;
		try {
			response = httpclient.execute(httpget, localContext);
			String responseBody = EntityUtils.toString(response.getEntity());
			System.out.println(responseBody);
			assertNotNull(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
