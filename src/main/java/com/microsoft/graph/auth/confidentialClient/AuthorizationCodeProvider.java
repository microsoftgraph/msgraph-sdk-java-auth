package com.microsoft.graph.auth.confidentialClient;

import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.microsoft.graph.auth.AuthConstants;
import com.microsoft.graph.auth.BaseAuthentication;
import com.microsoft.graph.auth.enums.NationalCloud;
import com.microsoft.graph.httpcore.IAuthenticationProvider;


public class AuthorizationCodeProvider extends BaseAuthentication implements IAuthenticationProvider{

	/*
	 * Authorization code provider initialization
	 * 
	 * @param clientId Client ID of application
	 * @param scopes Scopes of application to access protected resources
	 * @param authorizationCode Authorization code
	 * @param redirectUri Redirect uri provided while getting authorization code
	 * @param clientSecret Client secret of application
	 */
	public AuthorizationCodeProvider(
			String clientId,
			List<String> scopes,
			String authorizationCode,
			String redirectUri,
			String clientSecret){
		this(clientId, scopes, authorizationCode, redirectUri, null, null, clientSecret);
	}

	/*
	 * Authorization code provider initialization
	 * 
	 * @param clientId Client ID of application
	 * @param scopes Scopes of application to access protected resources
	 * @param authorizationCode Authorization code
	 * @param redirectUri Redirect uri provided while getting authorization code
	 * @param nationalCloud National cloud to access
	 * @param clientSecret Client secret of application
	 */
	public AuthorizationCodeProvider(
			String clientId,
			List<String> scopes,
			String authorizationCode,
			String redirectUri,
			NationalCloud nationalCloud,
			String tenant,
			String clientSecret){
		
		super(	scopes,
				clientId,
				GetAuthority(nationalCloud == null? NationalCloud.Global: nationalCloud, tenant == null? AuthConstants.Tenants.Common: tenant),
				redirectUri,
				nationalCloud == null? NationalCloud.Global: nationalCloud,
				tenant == null? AuthConstants.Tenants.Common: tenant,
				clientSecret);
		
		getAccessToken(authorizationCode);
	}
	
	@Override
	public void authenticateRequest(HttpRequest request) {
		String tokenParameter =  AuthConstants.BEARER + getAccessTokenSilent();
		request.addHeader(AuthConstants.AUTHORIZATION_HEADER, tokenParameter);
	}

	void getAccessToken(String authorizationCode) {
		try {
			OAuthClientRequest request = getTokenRequestMessage(authorizationCode);
			getAccessTokenNewRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OAuthClientRequest getTokenRequestMessage(String authorizationCode) throws OAuthSystemException {
		String tokenUrl = getAuthority() + AuthConstants.TOKEN_ENDPOINT;
		TokenRequestBuilder token = OAuthClientRequest.
				tokenLocation(tokenUrl)
				.setClientId(getClientId())
				.setCode(authorizationCode)
				.setRedirectURI(getRedirectUri())
				.setGrantType(GrantType.AUTHORIZATION_CODE)
				.setScope(getScopesAsString()); 
		if(getClientSecret() != null) {
			token.setClientSecret(getClientSecret());
		}
		return token.buildBodyMessage();
	}
	
	void getAccessTokenNewRequest(OAuthClientRequest request) throws OAuthSystemException, OAuthProblemException {
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		setStartTime(System.currentTimeMillis());  
		setResponse(oAuthClient.accessToken(request));
	}
}
