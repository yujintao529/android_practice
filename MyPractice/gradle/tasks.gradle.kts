//tasks.create("printlnAllConfiguration") {
//    doLast {
//        printlnConfiguration(project)
//    }
//}
//tasks.create("printlnConfiguration"){
//    doLast {
//        val con = project.configurations.findByName(project.ext.properties["configuration"] as String)
//        if (con == null) {
//            println("sorry,no configuration with name ${project.ext.properties["configuration"]}")
//        } else {
//            println(con)
//        }
//    }
//}
//
//fun printlnConfiguration(project: Project) {
//    project.configurations.forEach { configuration ->
//        println(toString(configuration))
//    }
//}
//
//fun toString(configuration:Configuration):String{
//    val stringBuilder = StringBuilder()
//    stringBuilder.append("configuration[")
//    stringBuilder.append(configuration.name)
//    stringBuilder.append("-" + configuration.uploadTaskName).append("]");
//    stringBuilder.append("\n")
//    stringBuilder.append("\tartifacts-")
//    stringBuilder.append("\n")
//    configuration.artifacts.files.forEach { file ->
//        stringBuilder.append("\t\t" + file.getName())
//        stringBuilder.append("\n")
//    }
//    stringBuilder.append("\n")
//    if (configuration.isCanBeResolved) {
//        stringBuilder.append("\tdependencies-")
//        stringBuilder.append("\n")
//        configuration.resolve().forEach { file ->
//            stringBuilder.append("\t\t" + file.getName())
//            stringBuilder.append("\n")
//        }
//    }
//    return stringBuilder.toString()
//}
