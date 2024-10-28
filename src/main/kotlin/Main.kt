import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.Serializable
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Serializable
data class Task(var description: String, var isDone: Boolean = false)

data class Project(val name: String, val tasks: MutableList<Task> = mutableListOf())


@Composable
fun App() {
    val projects = remember {
        mutableStateListOf(
            Project(
                "Today", mutableListOf(
                    Task("Finish quarterly report"),
                    Task("Respond to client emails"),
                    Task("Prepare for team meeting"),
                    Task("Review code for upcoming release"),
                    Task("Organize desk and clean workspace")
                )
            ), Project(
                "Upcoming", mutableListOf(
                    Task("Draft project roadmap for next quarter"),
                    Task("Research tools for process automation"),
                    Task("Update team documentation"),
                    Task("Plan team-building activities"),
                    Task("Follow up on pending invoices"),
                    Task("Schedule performance reviews")
                )
            )
        )
    }
    var selectedTaskIndex by remember { mutableStateOf<Pair<Project, Int>?>(null) }

    IntUiTheme(isDark = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(16.dp)
        ) {
            projects.forEach { project ->
                ProjectSection(
                    project,
                    selectedTaskIndex,
                    onEditClick = { proj, index -> selectedTaskIndex = proj to index },
                    onSaveClick = { proj, index ->
                        selectedTaskIndex = null
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ProjectSection(
    project: Project,
    selectedTaskIndex: Pair<Project, Int>?,
    onEditClick: (Project, Int) -> Unit,
    onSaveClick: (Project, Int) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("<star>")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = project.name, fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        project.tasks.forEachIndexed { index, task ->
            TaskItem(
                task,
                isEditing = selectedTaskIndex == project to index,
                onEditClick = { onEditClick(project, index) },
                onSaveClick = { onSaveClick(project, index) }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { task.isDone = it }
        )
        Spacer(modifier = Modifier.width(8.dp))

        if (isEditing) {
            val state = rememberTextFieldState(task.description)
            TextField(
                state = state,
//                singleLine = true,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Save",
                modifier = Modifier.clickable {
                    task.description = state.text.toString()
                    onSaveClick()
                },
                color = Color.Blue
            )
        } else {
            Text(
                task.description,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Edit",
                modifier = Modifier.clickable { onEditClick() },
                color = Color.Blue
            )
        }
    }
}

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "To-Do App") {
            App()
        }
    }
}
