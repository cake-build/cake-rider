package net.cakebuild.shared.nuget

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.HttpRequests

class SearchQueryService : NuGetProvider() {
    private val log = Logger.getInstance(SearchQueryService::class.java)

    override val endpointType: String
        get() = "SearchQueryService"

    fun getVersion(packageName: String, includePrerelease: Boolean): String? {
        if (endpointUrl == null) {
            log.warn("No endpoint url found.")
            return null
        }
        val url = "$endpointUrl?q=$packageName&prerelease=$includePrerelease"
        val response = HttpRequests.request(url).readString()
        val searchResult = jacksonObjectMapper().readValue(response, SearchResult::class.java)
        val pkg = searchResult.data.firstOrNull { packageName.equals(it.id, true) }
        if (pkg == null) {
            log.warn("Unable to find the package named $packageName")
            return null
        }
        return pkg.version
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class SearchResult {
        val data: List<NuGetPackages> = listOf()
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    class NuGetPackages {
        val id: String = ""
        val version: String = ""
    }
}
