package com.example.exchange.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.exchange.R
import com.example.exchange.features.components.Loader
import com.example.exchange.features.components.RefreshButton
import com.example.exchange.ui.theme.LocalCustomColorsPalette
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel(), onBackPressed: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.errorEvent.collectLatest {
            scaffoldState.snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            SearchToolbar(onQueryChanged = { viewModel.searchPair(it) }, onBackPressed = onBackPressed, onDonePressed = {
                scope.launch {
                    viewModel.onAddItemClicked()
                    onBackPressed()
                }
            })
            if (!state.error.isNullOrEmpty()) {
                RefreshButton(onRefresh = { viewModel.loadRemoteData() })
            } else if (!state.isLoading) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalCustomColorsPalette.current.colorBackground)
                        .padding(vertical = 8.dp)
                ) {
                    items(state.searchList ?: state.currencies, key = { it }) { item ->
                        SearchItem(item, onCheckedChange = { check, symbol ->
                            if (check) {
                                viewModel.symbols += symbol
                            } else {
                                viewModel.symbols -= symbol
                            }
                        })
                    }
                }
            } else {
                Loader()
            }
        }
    }
}


@Composable
fun SearchToolbar(
    onQueryChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null
                    )
                }
                Text(
                    text = "Add Asset",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    fontSize = 18.sp,
                    color = LocalCustomColorsPalette.current.textColor
                )
                Button(
                    onClick = { onDonePressed() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = LocalCustomColorsPalette.current.textColor
                    )
                ) {
                    Text(text = "Done")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LocalCustomColorsPalette.current.colorBackground)
            ) {
                OutlinedTextField(
                    value = search,
                    onValueChange = {
                        search = it
                        onQueryChanged(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_search),
                            contentDescription = null
                        )
                    },
                    placeholder = { Text(text = "Search USD pair") }
                )
            }
        }
    }
}

@Composable
fun SearchItem(item: Pair<String, String>, onCheckedChange: (Boolean, String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        var check by remember {
            mutableStateOf(false)
        }
        Row(
            modifier = Modifier
                .background(LocalCustomColorsPalette.current.colorCardBackground)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.first)
                Text(text = item.second)
            }
            Checkbox(checked = check, onCheckedChange = {
                check = it
                onCheckedChange(it, item.first)
            })
        }
    }
}