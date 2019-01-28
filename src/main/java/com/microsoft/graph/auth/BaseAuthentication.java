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

	protected List<String> Scopes;
	protected String ClientId;
	protected String authority;
	protected String ClientSecret;
	protected long startTime;
	protected NationalCloud nationalCloud;
	protected String tenant;
	protected String redirectUri = "https://localhost:8080";
	protected OAuthJSONAccessTokenResponse response;

	public BaseAuthentication(
			List<String> scopes,
			String clientId,
			String authority,
			String redirectUri,
			NationalCloud nationalCloud,
			String tenant,
			String ClientSecret)
	{
		this.Scopes = scopes;
		this.ClientId = clientId;
		this.authority = authority;
		this.redirectUri = redirectUri;
		this.nationalCloud = nationalCloud;
		this.tenant = tenant;
		this.ClientSecret = ClientSecret;
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
		String scopeString = new String();
		for(String s : this.Scopes) {
			scopeString+=(s+" ");
		}
		return scopeString;
	}

	protected String getAccessTokenSilent()
	{
		long durationPassed = System.currentTimeMillis() - startTime;
		if(this.response == null || durationPassed < 0) return null;
		try {
			if(durationPassed >= response.getExpiresIn()*1000) {
				TokenRequestBuilder token = OAuthClientRequest.
						tokenLocation(this.authority + "/oauth2/v2.0/token")
						.setClientId(this.ClientId)
						.setScope(getScopesAsString())
						.setRefreshToken(response.getRefreshToken())
						.setGrantType(GrantType.REFRESH_TOKEN)
						.setScope(getScopesAsString())
						.setRedirectURI(redirectUri);
				if(this.ClientSecret != null) {
					token.setClientSecret(this.ClientSecret);
				}

				OAuthClientRequest r = token.buildBodyMessage();
				OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
				this.startTime = System.currentTimeMillis(); 
				this.response = oAuthClient.accessToken(r);
				return response.getAccessToken();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
