package com.microsoft.graph.auth.confidentialClient;

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


public class AuthorizationCodeProvider extends BaseAuthentication implements IAuthenticationProvider{

	public AuthorizationCodeProvider(
			String clientId,
			List<String> scopes,
			String authorizationCode,
			String redirectUri,
			String clientSecret){
		this(clientId, scopes, authorizationCode, redirectUri, null,null, clientSecret);
	}

	public AuthorizationCodeProvider(
			String clientId,
			List<String> scopes,
			String authorizationCode,
			String redirectUri,
			NationalCloud nationalCloud,
			String tenant,
			String clientSecret){
		
		super(scopes,
				clientId,
				GetAuthority(nationalCloud==null?NationalCloud.Global:nationalCloud, tenant == null ? AuthConstants.Tenants.Common : tenant),
				redirectUri,
				nationalCloud==null?NationalCloud.Global:nationalCloud,
				tenant == null ? AuthConstants.Tenants.Common : tenant,
						clientSecret);
		
		getAccessToken(authorizationCode);
	}
	
	@Override
	public String getAccessToken(){
		return getAccessTokenSilent();
	}

	private void getAccessToken(String authorizationCode) {
		try {
			OAuthClientRequest req = getTokenRequestMessage(authorizationCode);
			getAccessTokenNewRequest(req);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected OAuthClientRequest getTokenRequestMessage(String authorizationCode) throws OAuthSystemException {
		String tokenUrl = super.authority + "/oauth2/v2.0/token";
		TokenRequestBuilder token = OAuthClientRequest.
				tokenLocation(tokenUrl)
				.setClientId(this.ClientId)
				.setCode(authorizationCode)
				.setRedirectURI(this.redirectUri)
				.setGrantType(GrantType.AUTHORIZATION_CODE)
				.setScope(getScopesAsString()); 
		if(this.ClientSecret != null) {
			token.setClientSecret(this.ClientSecret);
		}
		return token.buildBodyMessage();
	}
	
	protected void getAccessTokenNewRequest(OAuthClientRequest req) throws OAuthSystemException, OAuthProblemException {
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		super.startTime = System.currentTimeMillis(); 
		super.response = oAuthClient.accessToken(req);
	}
}
