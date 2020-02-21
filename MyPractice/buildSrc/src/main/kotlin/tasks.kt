import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get

class ConfigurationInfoTask : DefaultTask() {

    @TaskAction
    fun printlnAllConfiguration() {
        printlnConfiguration(project)
    }

    @TaskAction
    fun printlnConfiguration() {
        val con = project.configurations.findByName(project.extensions["configuration"] as String)
        if (con == null) {
            println("sorry,no configuration with name ${project.extensions["configuration"]}")
        } else {
            println(con)
        }
    }
}


fun printlnConfiguration(project: Project) {
    project.configurations.forEach { configuration ->
        println(toString(configuration))
    }
}

fun toString(configuration: Configuration): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append("configuration[")
    stringBuilder.append(configuration.name)
    stringBuilder.append("-" + configuration.uploadTaskName).append("]");
    stringBuilder.append("\n")
    stringBuilder.append("\tartifacts-")
    stringBuilder.append("\n")
    configuration.artifacts.files.forEach { file ->
        stringBuilder.append("\t\t" + file.getName())
        stringBuilder.append("\n")
    }
    stringBuilder.append("\n")
    if (configuration.isCanBeResolved) {
        stringBuilder.append("\tdependencies-")
        stringBuilder.append("\n")
        configuration.resolve().forEach { file ->
            stringBuilder.append("\t\t" + file.getName())
            stringBuilder.append("\n")
        }
    }
    return stringBuilder.toString()
}
