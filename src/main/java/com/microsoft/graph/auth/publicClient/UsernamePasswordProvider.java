package com.microsoft.graph.auth.publicClient;

import java.util.List;

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
		this(clientId, scopes, username, password, NationalCloud.Global, AuthConstants.Tenants.Organizations);
	}

	public UsernamePasswordProvider(
			String clientId,
			List<String> scopes,
			String username,
			String password,
			NationalCloud nationalCloud,
			String tenant) {
		super(  scopes, 
				clientId, 
				GetAuthority(nationalCloud == null? NationalCloud.Global: nationalCloud, tenant == null? AuthConstants.Tenants.Organizations: tenant), 
				null, 
				nationalCloud == null? NationalCloud.Global: nationalCloud, 
				tenant,
				null);
		this.Username = username;
		this.Password = password;
	}

	@Override
	public String getAccessToken(){
		String accessToken = getAccessTokenSilent();
		if(accessToken == null) {
			try {
				OAuthClientRequest request = getTokenRequestMessage();
				accessToken = getAccessTokenNewRequest(request);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return accessToken;
	}
	
	protected OAuthClientRequest getTokenRequestMessage() throws OAuthSystemException {
		String tokenUrl = this.authority + "/oauth2/v2.0/token";
		TokenRequestBuilder token = OAuthClientRequest.
				tokenLocation(tokenUrl)
				.setClientId(this.ClientId)
				.setUsername(this.Username)
				.setPassword(this.Password)
				.setGrantType(GrantType.PASSWORD)
				.setScope(getScopesAsString());
		OAuthClientRequest request = token.buildBodyMessage();
		return request;
	}
	
	protected String getAccessTokenNewRequest(OAuthClientRequest request) throws OAuthSystemException, OAuthProblemException {
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		super.startTime = System.currentTimeMillis();
		super.response = oAuthClient.accessToken(request);
		return super.response.getAccessToken();
	}
}
