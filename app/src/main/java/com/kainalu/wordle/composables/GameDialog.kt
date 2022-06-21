package com.kainalu.wordle.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun GameDialog(
    title: String = "",
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest, properties) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            contentColor = contentColorFor(MaterialTheme.colors.surface),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp),
                ) {
                    val (titleRef, closeButtonRef) = createRefs()

                    Text(title, modifier = Modifier.constrainAs(titleRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }, fontWeight = FontWeight.Bold)
                    IconButton(
                        modifier = Modifier.constrainAs(closeButtonRef) {
                            end.linkTo(parent.end)
                        },
                        onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }

                Box(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
                    content()
                }
            }
        }
    }
}