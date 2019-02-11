package com.microsoft.graph.auth.confidentialClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

import com.microsoft.graph.auth.enums.NationalCloud;
import com.microsoft.graph.httpcore.IAuthenticationProvider;

public class ClientCredentialProviderTests {
	public static String CLIENT_ID = "CLIENT_ID";
	public static String SCOPE = "https://graph.microsoft.com/.default";
	public static List<String > SCOPES = Arrays.asList(SCOPE);
	public static String CLIENT_SECRET = "CLIENT_SECRET";
	public static String CLIENT_ASSERTION = "CLIENT_ASSERTION";
	public static String TENANT = "TENANT_GUID_OR_DOMAIN_NAME";
	public static NationalCloud NATIONAL_CLOUD = NationalCloud.Global;
	
	@Test
	public void createInstanceClientSecretTest() {
		IAuthenticationProvider authenticationProvider = new ClientCredentialProvider(CLIENT_ID, SCOPES, CLIENT_SECRET, TENANT, NATIONAL_CLOUD);
		assertNotNull(authenticationProvider);
	}
	
	@Test
	public void getTokenRequestMessageTest() throws OAuthSystemException {
		ClientCredentialProvider authenticationProvider = new ClientCredentialProvider(CLIENT_ID, SCOPES, CLIENT_SECRET, TENANT, NATIONAL_CLOUD);
		String expected = "grant_type=client_credentials&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default+&client_secret=CLIENT_SECRET&client_id=CLIENT_ID";
		String actual = authenticationProvider.getTokenRequestMessage().getBody();
		String expectedLocationUri = "https://login.microsoftonline.com/TENANT_GUID_OR_DOMAIN_NAME/oauth2/v2.0/token";
		String actualLocationUri = authenticationProvider.getTokenRequestMessage().getLocationUri();
		assertEquals(expected, actual);
		assertEquals(expectedLocationUri, actualLocationUri);
	}
	
}
