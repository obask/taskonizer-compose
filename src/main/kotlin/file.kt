import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


private val file = File("tasks.json")

// Save tasks to a file
fun saveTasks(taskList: List<Task>) {
    val jsonString = Json.encodeToString(taskList)
    file.writeText(jsonString)
}

// Load tasks from a file
fun loadTasks(): List<Task> {
    return if (file.exists()) {
        val jsonString = file.readText()
        Json.decodeFromString(jsonString)
    } else {
        emptyList()
    }
}
