# Microsoft Graph Auth SDK for Java

[ ![Download](https://api.bintray.com/packages/microsoftgraph/Maven/microsoft-graph/images/download.svg) ](https://bintray.com/microsoftgraph/Maven/microsoft-graph/_latestVersion)


Get started with the Microsoft Graph SDK for Java by integrating the [Microsoft Graph API](https://graph.microsoft.io/en-us/getting-started) into your Java application!

## 1. Installation

### 1.1 Install via Gradle

Add the repository and a compile dependency for `microsoft-graph` to your project's `build.gradle`:

```gradle
repository {
    jcenter()
	jcenter{
        url 'http://oss.jfrog.org/artifactory/oss-snapshot-local'
	}
}

dependency {
    // Include the sdk as a dependency
    compile('com.microsoft.graph:microsoft-graph-auth:0.1.0-SNAPSHOT')
}
```

### 1.2 Install via Maven
Add the dependency in `dependencies` in pom.xml
```dependency
<dependency>
	<groupId>com.microsoft.graph</groupId>
	<artifactId>microsoft-graph-auth</artifactId>
	<version>0.1.0-SNAPSHOT</version>
</dependency>
```

## 2. Getting started

### 2.1 Register your application

Register your application by following the steps at [Register your app with the Azure AD v2.0 endpoint](https://developer.microsoft.com/en-us/graph/docs/concepts/auth_register_app_v2).

### 2.2 Create an IAuthenticationProvider object

#### 2.3.1 Confidential client authentication provider

##### a. Authorization code provider
```java
IAuthenticationProvider authorizationCodeProvider = new AuthorizationCodeProvider(CLIENT_ID, SCOPES_LIST, AUTHORIZATION_CODE, REDIRECT_URL, NATIONAL_CLOUD,	TENANT, SECRET);
```

##### b. Client credential provider
```java
IAuthenticationProvider iAuthenticationProvider = new ClientCredentialProvider(CLIENT_ID, SCOPES_LIST, CLIENT_SECRET, tenantGUID, NationalCloud.Global);
```
#### 2.3.2 Public client authentication provider
##### a. Username password provider
```java
IAuthenticationProvider authenticationProvider = new UsernamePasswordProvider(CLIENT_ID, SCOPES_LIST, USERNAME, PASSWORD, NATIONAL_CLOUD, TENANT, CLIENT_SECRET);
```
### 2.3 Get a HttpClient object and make a call

```java
import com.microsoft.graph.httpcore.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
```

```java
CloseableHttpClient httpclient = HttpClients.createDefault(authenticationProvider);
HttpGet httpget = new HttpGet("https://graph.microsoft.com/v1.0/me/");
HttpClientContext localContext = HttpClientContext.create();
try {
		HttpResponse response = httpclient.execute(httpget, localContext);
		String responseBody = EntityUtils.toString(response.getEntity());
		System.out.println(responseBody); 
} catch (IOException e) {
	e.printStackTrace();
}
```

## 3. Make requests against the service

After you have a GraphServiceClient that is authenticated, you can begin making calls against the service. The requests against the service look like our [REST API](https://developer.microsoft.com/en-us/graph/docs/concepts/overview).

### 3.1 Get the user's drive

To retrieve the user's drive:

```java
CloseableHttpClient httpclient = HttpClients.createDefault(authenticationProvider);
HttpGet httpget = new HttpGet("https://graph.microsoft.com/v1.0/me/drive");
HttpClientContext localContext = HttpClientContext.create();
try {
		HttpResponse response = httpclient.execute(httpget, localContext);
		String responseBody = EntityUtils.toString(response.getEntity());
		System.out.println(responseBody); 
} catch (IOException e) {
	e.printStackTrace();
}
```

## 4. Sample
### 4.1 Authorization code provider

[Steps to get authorizationCode](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow#request-an-authorization-code)
```java
IAuthenticationProvider authenticationProvider = new AuthorizationCodeProvider("6731de76-14a6-49ae-97bc-6eba6914391e", 
				Arrays.asList("https://graph.microsoft.com/user.read"), 
				authorizationCode,
				"http://localhost/myapp/", 
				NationalCloud.Global, 
				"common", 
				"JqQX2PNo9bpM0uEihUPzyrh");
				
CloseableHttpClient httpclient = HttpClients.createDefault(authenticationProvider);
HttpGet httpget = new HttpGet("https://graph.microsoft.com/v1.0/me/messages");
HttpClientContext localContext = HttpClientContext.create();
try {
		HttpResponse response = httpclient.execute(httpget, localContext);
		String responseBody = EntityUtils.toString(response.getEntity());
		System.out.println(responseBody); 
} catch (IOException e) {
	e.printStackTrace();
}
```

## 5. Documentation

For more detailed documentation, see:

[Usage of these providers](https://docs.microsoft.com/en-us/azure/active-directory/develop/active-directory-v2-protocols).

## 6. Issues

For known issues, see [issues](https://github.com/microsoftgraph/msgraph-sdk-java-auth/issues).

## 7. Contributions

The Microsoft Graph SDK is open for contribution. To contribute to this project, see [Contributing](https://github.com/microsoftgraph/msgraph-sdk-java/blob/master/CONTRIBUTING.md).

Thanks to everyone who has already devoted time to improving the library:

<!-- ALL-CONTRIBUTORS-LIST:START  -->
<!-- prettier-ignore -->
| [<img src="https://avatars3.githubusercontent.com/u/16473684?v=4" width="100px;"/><br /><sub><b>Nakul Sabharwal</b></sub>](https://developer.microsoft.com/graph)<br />[](#question-NakulSabharwal "Answering Questions") [](https://github.com/microsoftgraph/msgraph-sdk-java/commits?author=NakulSabharwal "Code") [](https://github.com/microsoftgraph/msgraph-sdk-java/wiki "Documentation") [](#review-NakulSabharwal "Reviewed Pull Requests") [](https://github.com/microsoftgraph/msgraph-sdk-java/commits?author=NakulSabharwal "Tests")| [<img src="https://avatars2.githubusercontent.com/u/3197588?v=4" width="100px;"/><br /><sub><b>Deepak Agrawal</b></sub>](https://github.com/deepak2016)<br /> 
| :---: | :---: | 
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/kentcdodds/all-contributors) specification. Contributions of any kind are welcome!

## 8. Supported Java versions
The Microsoft Graph SDK for Java library is supported at runtime for Java 7+ and [Android API revision 15](http://source.android.com/source/build-numbers.html) and greater.

## 9. License

Copyright (c) Microsoft Corporation. All Rights Reserved. Licensed under the [MIT license](LICENSE).

## 10. Third-party notices

[Third-party notices](THIRD%20PARTY%20NOTICES)
