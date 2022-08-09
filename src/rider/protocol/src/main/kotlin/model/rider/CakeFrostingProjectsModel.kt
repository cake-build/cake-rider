package model.rider

import com.jetbrains.rider.model.nova.ide.SolutionModel
import com.jetbrains.rd.generator.nova.*
import com.jetbrains.rd.generator.nova.PredefinedType.*
import com.jetbrains.rd.generator.nova.csharp.CSharp50Generator
import com.jetbrains.rd.generator.nova.kotlin.Kotlin11Generator
import com.jetbrains.rider.model.nova.ide.RunConfigurationModel

@Suppress("unused")
object CakeFrostingProjectsModel : Ext(SolutionModel.Solution) {
    val CakeFrostingProject = classdef {
        property("name", string)
        property("projectFilePath", string)
        list("tasks", string)
    }

    init {
        setting(CSharp50Generator.Namespace, "net.cakebuild.Protocol")
        setting(Kotlin11Generator.Namespace, "net.cakebuild.protocol")

        list("projects", CakeFrostingProject)

        val runConfiguration = RunConfigurationModel::class.java.getDeclaredField("runConfiguration").apply {
            isAccessible = true
        }.get(null) as Struct.Concrete

        map("runConfigurationTaskNames", runConfiguration, string)
    }
}