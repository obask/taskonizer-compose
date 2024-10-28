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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
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
    var editingTaskIndex by remember { mutableStateOf<Pair<Project, Int>?>(projects[0] to 0) }

    IntUiTheme(isDark = true) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(16.dp).onKeyEvent { event ->
                    when (event.key) {
                        Key.DirectionDown -> {
                            selectedTaskIndex = getNextTask(selectedTaskIndex, projects)
                            true
                        }

                        Key.DirectionUp -> {
                            selectedTaskIndex = getPreviousTask(selectedTaskIndex, projects)
                            true
                        }

                        Key.Enter -> {
                            if (selectedTaskIndex != null) {
                                editingTaskIndex = selectedTaskIndex
                            }
                            true
                        }

                        else -> false
                    }
                }) {
            projects.forEach { project ->
                ProjectSection(
                    project,
                    selectedTaskIndex,
                    editingTaskIndex,
                    onEditClick = { proj, index -> editingTaskIndex = proj to index },
                    onSaveClick = { editingTaskIndex = null })
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

fun getNextTask(current: Pair<Project, Int>?, projects: List<Project>): Pair<Project, Int>? {
    if (current == null) return projects.firstOrNull()?.let { it to 0 }
    val (project, index) = current
    val projectIndex = projects.indexOf(project)
    return when {
        index + 1 < project.tasks.size -> project to index + 1
        projectIndex + 1 < projects.size -> projects[projectIndex + 1] to 0
        else -> null
    }
}

fun getPreviousTask(current: Pair<Project, Int>?, projects: List<Project>): Pair<Project, Int>? {
    if (current == null) return projects.lastOrNull()?.let { it to it.tasks.lastIndex }
    val (project, index) = current
    val projectIndex = projects.indexOf(project)
    return when {
        index - 1 >= 0 -> project to index - 1
        projectIndex - 1 >= 0 -> projects[projectIndex - 1] to projects[projectIndex - 1].tasks.lastIndex
        else -> null
    }
}

@Composable
fun ProjectSection(
    project: Project,
    selectedTaskIndex: Pair<Project, Int>?,
    editingTaskIndex: Pair<Project, Int>?,
    onEditClick: (Project, Int) -> Unit,
    onSaveClick: () -> Unit
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
                isEditing = editingTaskIndex == project to index,
                isSelected = selectedTaskIndex == project to index,
                onEditClick = { onEditClick(project, index) },
                onSaveClick = onSaveClick
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task, isEditing: Boolean, isSelected: Boolean, onEditClick: () -> Unit, onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
            .background(if (isSelected) Color.DarkGray else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone, onCheckedChange = { task.isDone = it })
        Spacer(modifier = Modifier.width(8.dp))

        if (isEditing) {
            val state = rememberTextFieldState(task.description)
            TextField(
                state = state, modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Save", modifier = Modifier.clickable {
                    task.description = state.text.toString()
                    onSaveClick()
                }, color = Color.Blue
            )
        } else {
            Text(
                task.description, fontSize = 16.sp, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Edit", modifier = Modifier.clickable { onEditClick() }, color = Color.Blue
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
