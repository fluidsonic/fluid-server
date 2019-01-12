import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.withConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet


val SourceSet.kotlin
	get() = withConvention(KotlinSourceSet::class) { kotlin }
