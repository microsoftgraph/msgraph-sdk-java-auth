# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.

<# 
.Synopsis
    Ensure the maven version is updated in the case that the pull request is 
    to the main/master branch of the repo.
.Description 
    Retrieves the local, and Maven versions of the Java-Auth build.
    Checks that the Maven versions are aligned, trigger warning if not.
    Checks that the current local version is greater than those currently deployed. 
#>

.Parameter packageName
.Parameter propertiesPath

Param(
    [parameter(Mandatory = $true)]
    [string]$packageName,

    [parameter(Mandatory = $true)]
    [string]$propertiesPath
)

#Find the local version from the Gradle.Properties file
$file = get-item $propertiesPath
$findLocalVersions = $file | Select-String -Pattern "mavenMajorVersion" -Context 0,2
$findLocalVersions = $findLocalVersions -split "`r`n"

$localMajor = $findLocalVersions[0].Substring($findLocalVersions[0].Length-1)
$localMinor = $findLocalVersions[1].Substring($findLocalVersions[1].Length-1)
$localPatch = $findLocalVersions[2].Substring($findLocalVersions[2].Length-1)
$localVersion = [version]"$localMajor.$localMinor.$localPatch"

#Set Web Client and retrieve Maven versions from their respective repos.
$web_client = New-Object System.Net.WebClient

$mavenAPIurl = "https://search.maven.org/solrsearch/select?q=$packageName&rows=20&wt=json"
$jsonResult = $web_client.DownloadString($mavenAPIurl) | ConvertFrom-Json
$mavenVersion = [version]$jsonResult.response.docs.latestVersion

#If the api calls return empty then this library cannot be compared to the online versions
#may proceed with the pull request
if($mavenVersion -eq $null)
{
    Write-Information "This package does not exist yet in the online repository, therefore there are no versions to compare."
    return
}

#Inform host of current Maven version
Write-Host 'The current version in the Maven central repository is:' $mavenVersion

#Success if Local version has been updated, Error otherwise. 
if($localVersion -gt $mavenVersion){
    Write-Host "The current pull request is of a greater version"
}   
else{
    Write-Error "The current local version is not updated. Please update the local version in the Gradle.Properties file."
} 