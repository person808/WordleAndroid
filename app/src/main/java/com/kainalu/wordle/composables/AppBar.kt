package com.kainalu.wordle.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kainalu.wordle.navigation.Screen

@Composable
fun AppBar(navController: NavController, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    TopAppBar(
      title = { Text("Wordle") },
      backgroundColor = Color.Transparent,
      elevation = 0.dp,
      actions = {
        IconButton(onClick = {}) { Icon(Icons.Outlined.Help, contentDescription = null) }
        IconButton(onClick = { navController.navigate(Screen.Statistics.route) }) {
          Icon(Icons.Outlined.Leaderboard, contentDescription = null)
        }
        IconButton(onClick = { /*TODO*/}) { Icon(Icons.Filled.Settings, contentDescription = null) }
      }
    )
    Divider()
  }
}

@Preview
@Composable
private fun AppBarPreview() {
  AppBar(rememberNavController())
}
