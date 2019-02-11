package com.microsoft.graph.auth.confidentialClient;

import java.util.List;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.microsoft.graph.auth.BaseAuthentication;
import com.microsoft.graph.auth.enums.NationalCloud;
import com.microsoft.graph.httpcore.IAuthenticationProvider;

public class ClientCredentialProvider extends BaseAuthentication implements IAuthenticationProvider{
	
	public ClientCredentialProvider(String clientId,
			List<String> scopes,
			String clientSecret,
			String tenant,
			NationalCloud nationalCloud) {
		super(	scopes, 
				clientId,
				GetAuthority(nationalCloud == null? NationalCloud.Global: nationalCloud, tenant),
				null,
				nationalCloud == null? NationalCloud.Global: nationalCloud,
				tenant,
				clientSecret);
	}
	
	@Override
	public String getAccessToken() {
		if(super.response != null) {
			long duration = System.currentTimeMillis() - super.startTime;
			if(duration > 0 && duration < super.response.getExpiresIn()*1000) {
				return super.response.getAccessToken();
			}
		}
		String accessToken = null;
		try {
			OAuthClientRequest request = getTokenRequestMessage();
			accessToken = getAccessTokenNewRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}
	
	protected OAuthClientRequest getTokenRequestMessage() throws OAuthSystemException {
		String tokenUrl = super.authority + "/oauth2/v2.0/token";
		TokenRequestBuilder token = OAuthClientRequest.
				tokenLocation(tokenUrl)
				.setClientId(super.ClientId)
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setScope(getScopesAsString()); 
		if(super.ClientSecret != null) {
			token.setClientSecret(this.ClientSecret);
		}
		return token.buildBodyMessage();
	}
	
	protected String getAccessTokenNewRequest(OAuthClientRequest request) throws OAuthSystemException, OAuthProblemException {
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		super.startTime = System.currentTimeMillis();
		super.response = oAuthClient.accessToken(request);
		return super.response.getAccessToken();
	}
}
