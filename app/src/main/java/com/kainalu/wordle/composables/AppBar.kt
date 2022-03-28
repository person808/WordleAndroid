package com.kainalu.wordle.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kainalu.wordle.R


@Preview(showBackground = true)
@Composable
fun AppBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        TopAppBar(
            title = { Text("Wordle") },
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painterResource(id = R.drawable.ic_outline_help_outline_24),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painterResource(id = R.drawable.ic_outline_leaderboard_24),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                }
            })
        Divider()
    }
}