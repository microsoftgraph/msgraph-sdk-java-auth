package com.microsoft.graph.auth.publicClient;

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

public class UsernamePasswordProvider extends BaseAuthentication implements IAuthenticationProvider{

	private String Username;
	private String Password;

	public UsernamePasswordProvider(
			String clientId,
			List<String> scopes,
			String username,
			String password){
		this(clientId, scopes, username, password, NationalCloud.Global, AuthConstants.Tenants.Organizations, null);
	}

	public UsernamePasswordProvider(
			String clientId,
			List<String> scopes,
			String username,
			String password,
			NationalCloud nationalCloud,
			String tenant,
			String clientSecret) {
		super(  scopes, 
				clientId, 
				GetAuthority(nationalCloud == null? NationalCloud.Global: nationalCloud, tenant == null? AuthConstants.Tenants.Organizations: tenant), 
				null, 
				nationalCloud == null? NationalCloud.Global: nationalCloud, 
				tenant,
				clientSecret);
		this.Username = username;
		this.Password = password;
	}

	@Override
	public void authenticateRequest(HttpRequest request) {
		String accessToken = getAccessTokenSilent();
		if(accessToken == null) {
			try {
				OAuthClientRequest authRequest = getTokenRequestMessage();
				accessToken = getAccessTokenNewRequest(authRequest);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		request.addHeader("Authorization", AuthConstants.BEARER + accessToken);
	}
	
	OAuthClientRequest getTokenRequestMessage() throws OAuthSystemException {
		String tokenUrl = getAuthority() + AuthConstants.TOKEN_ENDPOINT;
		TokenRequestBuilder token = OAuthClientRequest.
				tokenLocation(tokenUrl)
				.setClientId(getClientId())
				.setUsername(this.Username)
				.setPassword(this.Password)
				.setGrantType(GrantType.PASSWORD)
				.setScope(getScopesAsString());
		if(getClientSecret() != null) {
			token.setClientSecret(getClientSecret());
		}
		OAuthClientRequest request = token.buildBodyMessage();
		return request;
	}
	
	String getAccessTokenNewRequest(OAuthClientRequest request) throws OAuthSystemException, OAuthProblemException {
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		setStartTime(System.currentTimeMillis());
		setResponse(oAuthClient.accessToken(request)); 
		return getResponse().getAccessToken();
	}
}
