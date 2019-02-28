package com.microsoft.graph.auth;

import java.util.HashMap;
import java.util.List;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.microsoft.graph.auth.enums.NationalCloud;

public class BaseAuthentication {

	private List<String> scopes;
	private String clientId;
	private String authority;
	private String clientSecret;
	private long startTime;
	private NationalCloud nationalCloud;
	private String tenant;
	private String redirectUri = "https://localhost:8080";
	private OAuthJSONAccessTokenResponse response;

	public BaseAuthentication(
			List<String> scopes,
			String clientId,
			String authority,
			String redirectUri,
			NationalCloud nationalCloud,
			String tenant,
			String clientSecret)
	{
		this.scopes = scopes;
		this.clientId = clientId;
		this.authority = authority;
		this.redirectUri = redirectUri;
		this.nationalCloud = nationalCloud;
		this.tenant = tenant;
		this.clientSecret = clientSecret;
	}

	protected static HashMap<String, String> CloudList = new HashMap<String, String>()
	{{
		put( "Global", "https://login.microsoftonline.com/" );
		put( "China", "https://login.chinacloudapi.cn/" );
		put( "Germany", "https://login.microsoftonline.de/" );
		put( "UsGovernment", "https://login.microsoftonline.us/" );
	}};

	protected static String GetAuthority(NationalCloud authorityEndpoint, String tenant)
	{
		return CloudList.get(authorityEndpoint.toString()) + tenant;
	}

	protected String getScopesAsString() {
		StringBuilder scopeString = new StringBuilder();
		for(String s : this.scopes) {
			scopeString.append(s);
			scopeString.append(" ");
		}
		return scopeString.toString();
	}

	protected String getAccessTokenSilent()
	{
		long durationPassed = System.currentTimeMillis() - startTime;
		if(this.response == null || durationPassed < 0) return null;
		try {
			if(durationPassed >= response.getExpiresIn()*1000) {
				TokenRequestBuilder token = OAuthClientRequest.
						tokenLocation(this.authority + AuthConstants.TOKEN_ENDPOINT)
						.setClientId(this.clientId)
						.setScope(getScopesAsString())
						.setRefreshToken(response.getRefreshToken())
						.setGrantType(GrantType.REFRESH_TOKEN)
						.setScope(getScopesAsString())
						.setRedirectURI(redirectUri);
				if(this.clientSecret != null) {
					token.setClientSecret(this.clientSecret);
				}

				OAuthClientRequest request = token.buildBodyMessage();
				OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
				this.startTime = System.currentTimeMillis(); 
				this.response = oAuthClient.accessToken(request);
				return response.getAccessToken();
			}
			else return response.getAccessToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String getAuthority() {
		return this.authority;
	}
	
	protected String getClientId() {
		return this.clientId;
	}
	
	protected String getClientSecret() {
		return this.clientSecret;
	}
	
	protected String getRedirectUri() {
		return this.redirectUri;
	}
	
	protected void setResponse(OAuthJSONAccessTokenResponse response) {
		this.response = response;
	}
	
	protected OAuthJSONAccessTokenResponse getResponse() {
		return this.response;
	}
	
	protected long getStartTime() {
		return this.startTime;
	}
	
	protected void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	protected NationalCloud getNationalCloud() {
		return this.nationalCloud;
	}
	
	protected String getTenant() {
		return this.tenant;
	}

}
