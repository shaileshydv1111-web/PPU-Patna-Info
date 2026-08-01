package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onGuestLogin: () -> Unit,
    onStudentLogin: (email: String, name: String, rollNo: String) -> Unit,
    onAdminLogin: (adminKey: String) -> Unit
) {
    var isAdminMode by remember { mutableStateOf(false) }

    // Student fields
    var studentName by remember { mutableStateOf("") }
    var studentEmail by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }

    // Admin field
    var adminKey by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(90.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "PPU Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isAdminMode) "PPU Admin Portal" else "Student Login",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = if (isAdminMode) "Manage Notices, Results, PYQs & Broadcast Notifications"
                    else "Access Patliputra University Notices, Results & Services",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Mode Selector Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = !isAdminMode,
                    onClick = { isAdminMode = false },
                    label = { Text("Student Mode") },
                    leadingIcon = { Icon(Icons.Filled.School, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("mode_student_chip")
                )
                Spacer(modifier = Modifier.width(12.dp))
                FilterChip(
                    selected = isAdminMode,
                    onClick = { isAdminMode = true },
                    label = { Text("Admin Panel") },
                    leadingIcon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("mode_admin_chip")
                )
            }

            // Form Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isAdminMode) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_name_input")
                    )

                    OutlinedTextField(
                        value = studentEmail,
                        onValueChange = { studentEmail = it },
                        label = { Text("Email Address") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text("Roll Number / Registration No (Optional)") },
                        leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_roll_input")
                    )

                    Button(
                        onClick = {
                            if (studentEmail.isNotBlank()) {
                                onStudentLogin(
                                    studentEmail,
                                    studentName.ifBlank { "Student" },
                                    rollNumber.ifBlank { "2026PPU1001" }
                                )
                            } else {
                                onGuestLogin()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_login_button")
                    ) {
                        Text(
                            text = if (studentEmail.isNotBlank()) "Login / Register" else "Continue as Guest",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onGuestLogin,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_guest_button")
                    ) {
                        Icon(Icons.Outlined.PermIdentity, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Explore Portal as Guest")
                    }
                } else {
                    OutlinedTextField(
                        value = adminKey,
                        onValueChange = { adminKey = it },
                        label = { Text("Admin Passcode Key") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_adminkey_input")
                    )

                    Text(
                        text = "Hint: Passcode is 'admin123' or 'ppu2026'",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Button(
                        onClick = { onAdminLogin(adminKey) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_admin_login_button")
                    ) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Login to Admin Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
