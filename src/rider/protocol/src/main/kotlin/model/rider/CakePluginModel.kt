package model.rider

import com.jetbrains.rider.model.nova.ide.SolutionModel
import com.jetbrains.rd.generator.nova.*
import com.jetbrains.rd.generator.nova.PredefinedType.*

/*
This is only the demo code and currently unused!
Change this to something useful, if needed
and let `kotlinCompile` and `buildDotNet`
dependon("rdgen") again...
 */

@Suppress("unused")
object CakePluginModel : Ext(SolutionModel.Solution) {

    val MyEnum = enum {
        +"FirstValue"
        +"SecondValue"
    }

    val MyStructure = structdef {
        field("projectFile", string)
        field("target", string)
    }

    init {
        //setting(CSharp50Generator.Namespace, "ReSharperPlugin.MyAwesomePlugin.Rider.Model")
        //setting(Kotlin11Generator.Namespace, "com.jetbrains.rider.myawesomeplugin.model")

        property("myString", string)
        property("myBool", bool)
        property("myEnum", MyEnum.nullable)

        map("data", string, string)

        signal("myStructure", MyStructure)
    }
}