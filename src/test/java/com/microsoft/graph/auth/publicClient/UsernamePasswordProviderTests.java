package com.microsoft.graph.auth.publicClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
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
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.httpcore.ICoreAuthenticationProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RunWith(MockitoJUnitRunner.class)
public class UsernamePasswordProviderTests {

	private  String CLIENT_ID = "CLIENT_ID";
	private List<String> SCOPES = Arrays.asList("user.read", "openid", "profile", "offline_access");
	private String USERNAME = "USERNAME";
	private String PASSWORD = "PASSWORD";
	private NationalCloud NATIONAL_CLOUD = NationalCloud.Global;
	private String CLIENT_SECRET = "CLIENT_SECRET";
	private String testmeurl = "https://graph.microsoft.com/v1.0/me/"; 

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
		Request httpget = new Request.Builder().url(testmeurl).build();
		usernamePasswordProvider.authenticateRequest(httpget);
		String actualtokenParameter = httpget.header("Authorization");
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
		ICoreAuthenticationProvider authenticationProvider = 
				new UsernamePasswordProvider(CLIENT_ID, 
						SCOPES, 
						USERNAME, 
						PASSWORD, 
						NATIONAL_CLOUD, 
						AuthConstants.Tenants.Organizations, 
						CLIENT_SECRET);
		callGraph(authenticationProvider);
	}

	public void callGraph(ICoreAuthenticationProvider authenticationProvider) {
		OkHttpClient httpclient = HttpClients.createDefault(authenticationProvider);
		Request httpget = new Request.Builder().url(testmeurl).build();
		Response response;
		try {
			response = httpclient.newCall(httpget).execute();
			assertNotNull(response.body().string());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
