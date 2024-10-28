import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
@Composable
fun App() {
    val taskList = remember { mutableStateListOf<Task>().apply { addAll(loadTasks()) } }
    var taskText by remember { mutableStateOf("") }

    // Call saveTasks whenever taskList changes
    DisposableEffect(taskList) {
        onDispose { saveTasks(taskList) }
    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("To-Do List", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(16.dp))

            // Input field
            OutlinedTextField(
                value = taskText,
                onValueChange = { taskText = it },
                label = { Text("Enter a task") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (taskText.isNotBlank()) {
                        taskList.add(Task(taskText))
                        taskText = ""
                        saveTasks(taskList)
                    }
                })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display task list
            Column(modifier = Modifier.fillMaxWidth()) {
                taskList.forEachIndexed { index, task ->
                    TaskItem(task = task, onRemove = {
                        taskList.removeAt(index)
                        saveTasks(taskList)
                    })
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(task.description, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onRemove) {
            Text("Remove")
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "To-Do App") {
        App()
    }
}
