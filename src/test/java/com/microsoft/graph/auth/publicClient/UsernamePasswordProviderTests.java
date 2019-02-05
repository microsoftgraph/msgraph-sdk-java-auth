package com.microsoft.graph.auth.publicClient;

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
import org.mockito.internal.matchers.Any;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UsernamePasswordProviderTests {
	
	private  String CLIENT_ID = "CLIENT_ID";
	private List<String> SCOPES = Arrays.asList("user.read", "openid", "profile", "offline_access");
	private String USERNAME = "USERNAME";
	private String PASSWORD = "PASSWORD";

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
		String actual = usernamePasswordProvider.getAccessToken();
		assertEquals("test_accessToken"	, actual);
	}
	
	@Ignore
	@Test
	public void getAccessTokenNewRequestTest() throws OAuthSystemException, OAuthProblemException {
		UsernamePasswordProvider usernamePasswordProvider = new UsernamePasswordProvider(CLIENT_ID, SCOPES, USERNAME, PASSWORD);
		String actualAccessToken = usernamePasswordProvider.getAccessTokenNewRequest(usernamePasswordProvider.getTokenRequestMessage());
		assertNotNull(actualAccessToken);
	}
	
}
