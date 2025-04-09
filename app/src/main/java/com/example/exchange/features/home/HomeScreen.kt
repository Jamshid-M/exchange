package com.example.exchange.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.exchange.data.model.ExchangeRate
import com.example.exchange.features.components.Loader
import com.example.exchange.features.components.RefreshButton
import com.example.exchange.ui.theme.LocalCustomColorsPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), onAddClick: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is HomeEvent.Error -> scaffoldState.snackbarHostState.showSnackbar(
                    it.message,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            HomeToolbar(onAddClick = onAddClick)
            if (!state.error.isNullOrEmpty()) {
                RefreshButton(onRefresh = { viewModel.refreshData() })
            }
            if (state.isLoading) {
                Loader()
            } else if (state.rates.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalCustomColorsPalette.current.colorBackground)
                        .padding(vertical = 8.dp)
                ) {
                    items(state.rates, key = { it.to }) { item ->
                        RateItem(item, onDelete = { viewModel.removeSymbol(item) })
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalCustomColorsPalette.current.colorBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Add currency rate pairs")
                }
            }
        }
    }
}

@Composable
fun HomeToolbar(onAddClick: () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Exchange Rates",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = LocalCustomColorsPalette.current.textColor
            )
            IconButton(onClick = onAddClick) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_add),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun RateItem(item: ExchangeRate, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SwipeDeleteContainer(item = item, onDelete = onDelete) {
            Row(
                modifier = Modifier
                    .background(LocalCustomColorsPalette.current.colorCardBackground)
                    .padding(16.dp)
            ) {
                Text(text = "${item.from}/${item.to}", modifier = Modifier.weight(1f))
                Text(text = item.rate.toString())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeDeleteContainer(
    item: ExchangeRate,
    onDelete: () -> Unit,
    animationDuration: Int = 200,
    content: @Composable (ExchangeRate) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
        confirmValueChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete()
        }
    }
    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(state = state, background = {
            SwipeItemBackground(swipeDismissState = state)
        }, dismissContent = { content(item) }, directions = setOf(DismissDirection.EndToStart))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeItemBackground(
    swipeDismissState: DismissState
) {
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        Color.Red
    } else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_delete),
            contentDescription = null,
            tint = Color.White
        )
    }
}