package com.ex.androidnavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ex.androidnavigation.ui.theme.AndroidNavigationTheme
import kotlinx.serialization.Serializable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val listStudents = getStudents()

        setContent {

            AndroidNavigationTheme {

                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Profile,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Profile> {
                            ProfileScreen(
                                name = "Advanced Mobile Dev.",
                                modifier = Modifier.padding(innerPadding),
                                onNavigateToStudentList = {
                                    navController.navigate(StudentList)
                                }
                            )
                        }
                        composable<StudentList> {
                            StudentListScreen(
                                onNavigateToProfile = {
                                    navController.navigate(Profile)
                                },
                                onStudentClick = { student, index, showSnackbar ->
                                    if (index == 1) {
                                        navController.navigate("studentInfo/${student.name}/${student.nim}/${student.email}/${student.grade}")
                                    } else {
                                        showSnackbar("Hanya mahasiswa di urutan kedua yang dapat ditampilkan")
                                    }
                                }
                            )
                        }
                        composable("studentInfo/{name}/{nim}/{email}/{grade}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: ""
                            val nim = backStackEntry.arguments?.getString("nim") ?: ""
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            val grade = backStackEntry.arguments?.getString("grade") ?: ""
                            StudentInfoScreen(name, nim, email, grade,
                                onNavigateToStudentList = {navController.navigate(StudentList)}
                            )
                        }
                    }
                }

            }

        }

    }

    @Serializable
    object Profile

    @Serializable
    object StudentList

    data class Student (
        val name: String = "",
        val nim: String = "",
        val email: String = "",
        val grade: Float = 0.0f
    )

    companion object {
        fun getStudents() : List<Student> {
            val list = mutableListOf<Student>()
            list.add(Student(name = "Djokovic", nim = "222301234",
                email = "djokovic@ub.ac.id", grade = 3.14f))
            list.add(Student(name = "Nadal", nim = "222301235",
                email = "nadal@ub.ac.id", grade = 3.24f))
            list.add(Student(name = "Murray", nim = "222301236",
                email = "murray@ub.ac.id", grade = 2.84f))
            return list
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier,
                  name: String,
                  onNavigateToStudentList: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Hej!", modifier = modifier, fontSize = 48.sp)
        Text(text = "This is class of $name.",fontSize = 18.sp)
        Button(onClick = onNavigateToStudentList) {
            Text("See Student List")
        }
    }
}

@Composable
fun StudentListScreen(
    onNavigateToProfile: () -> Unit,
    onStudentClick: (MainActivity.Student, Int, (String) -> Unit) -> Unit
) {
    val students = remember { MainActivity.getStudents() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxHeight()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = onNavigateToProfile) {
                    Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
                    Text("Back to Profile")
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(students.size) { index ->
                    val student = students[index]
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                            onStudentClick(student, index) { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        }
                    ) {
                        Text(text = student.name,
                            fontSize = 18.sp, //mengatur ukuran text menjadi 18 sp
                            color = Color(0xFFFF0000)) //mengatur warna text menjadi merah
                        Text(text = student.nim)
                        Text(text = student.email)
                        Text(text = student.grade.toString())
                    }
                    HorizontalDivider(thickness = 1.dp)
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfilePreview() {
    ProfileScreen(name = "Advanced Mobile Dev.", onNavigateToStudentList = {})
}

@Preview
@Composable
fun StudentListPreview () {
    StudentListScreen(onNavigateToProfile = {}, onStudentClick = {} as (MainActivity.Student, Int, (String) -> Unit) -> Unit)
}

@Composable
fun StudentInfoScreen(name: String, nim: String, email: String, grade: String,
                      onNavigateToStudentList: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Student Information", fontSize = 24.sp)
        Text(text = "Name: $name", fontSize = 18.sp)
        Text(text = "NIM: $nim", fontSize = 18.sp)
        Text(text = "Email: $email", fontSize = 18.sp)
        Text(text = "Grade: $grade", fontSize = 18.sp)
        Text(text = "Oleh: Michael Christopher Harijanto", fontSize = 14.sp)
        Text(text = "NIM: 225150407111041", fontSize = 14.sp)
        Button(onClick = onNavigateToStudentList) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
            Text("Back to StudentList")
        }
    }
}