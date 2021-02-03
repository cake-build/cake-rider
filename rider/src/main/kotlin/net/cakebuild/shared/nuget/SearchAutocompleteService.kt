package net.cakebuild.shared.nuget

@Suppress("unused")
class SearchAutocompleteService : NuGetProvider() {
    override val endpointType: String
        get() = "SearchAutocompleteService"
}
