package com.kainalu.wordle.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kainalu.wordle.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    TopAppBar(
      title = { Text("Wordle") },
      actions = {
        IconButton(onClick = {}) { Icon(Icons.Outlined.Help, contentDescription = null) }
        IconButton(onClick = { navController.navigate(Screen.Statistics.route) }) {
          Icon(Icons.Outlined.Leaderboard, contentDescription = null)
        }
        IconButton(onClick = { /*TODO*/}) { Icon(Icons.Filled.Settings, contentDescription = null) }
      }
    )
  }
}

@Preview
@Composable
private fun AppBarPreview() {
  MaterialTheme { AppBar(rememberNavController()) }
}
