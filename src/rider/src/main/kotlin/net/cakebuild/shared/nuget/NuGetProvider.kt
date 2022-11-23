package net.cakebuild.shared.nuget

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.HttpRequests
import net.cakebuild.installers.CakeNetToolInstaller

abstract class NuGetProvider {

    private val log = Logger.getInstance(CakeNetToolInstaller::class.java)
    abstract val endpointType: String
    protected val endpointUrl: String? by lazy {
        val response = HttpRequests.request("https://api.nuget.org/v3/index.json").readString()
        val index = jacksonObjectMapper().readValue(response, NuGetServiceIndex::class.java)
        if (index.resources == null) {
            log.error("nuget returned no resources for the v3 index.")
            return@lazy null
        }
        val targets = index.resources.filter { endpointType.equals(it.type, true) }
        if (!targets.any()) {
            log.error("nuget returned no endpoints with type $endpointType")
            return@lazy null
        }
        log.trace("nuget returned ${targets.count()} endpoints for type $endpointType")
        var target = targets.firstOrNull { it.comment != null && it.comment.contains("primary", true) }
        if (target == null) {
            target = targets.first()
        }

        target.url
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class NuGetServiceIndex {
        val resources: List<NuGetServiceResource>? = null
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class NuGetServiceResource {
        @get:JsonProperty("@type")
        val type: String? = null

        @get:JsonProperty("@id")
        val url: String? = null
        val comment: String? = null
    }
}
