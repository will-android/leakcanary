package shark

object AndroidMetadataExtractor : MetadataExtractor {
  override fun extractMetadata(graph: HeapGraph): Map<String, String> {
    val build = AndroidBuildMirror.fromHeapGraph(graph)

    val buildConfigClass = graph.findClassByName("com.squareup.leakcanary.core.BuildConfig")
    val leakCanaryVersion =
      buildConfigClass?.get("LIBRARY_VERSION")?.value?.readAsJavaString() ?: "Unknown"

    val loadedApk = graph.findClassByName("android.app.LoadedApk")
        ?.instances?.firstOrNull()

    val appPackageName =
      loadedApk?.get("android.app.LoadedApk", "mPackageName")?.value?.readAsJavaString()
          ?: "Unknown"
    return mapOf(
        "Build.VERSION.SDK_INT" to build.sdkInt.toString(),
        "Build.MANUFACTURER" to build.manufacturer,
        "LeakCanary version" to leakCanaryVersion,
        "App package name" to appPackageName
    )
  }
}