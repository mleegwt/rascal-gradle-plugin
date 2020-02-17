package mleegwt.rascal.gradle.plugin

import mleegwt.rascal.gradle.plugin.mleegwt.rascal.gradle.plugin.RascalRunTask
import org.gradle.api.*

class RascalPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // TODO MLE Configure source sets

//        configureTest(project)
        configureRun(project)
    }

    private fun configureRun(project: Project) {
        project.getTasks().named("runRascal", RascalRunTask::class.java)
    }
}
