package tk.zwander.fabricateoverlaysample.ui.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import tk.zwander.fabricateoverlaysample.data.LoadedApplicationInfo
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.elements.AppItem
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppListPage(
    navController: NavController
) {
    var apps by remember { mutableStateOf(listOf<LoadedApplicationInfo>()) }
    var filter by remember { mutableStateOf("") }
    val context = LocalContext.current

    val transition = updateTransition(
        targetState = apps.isEmpty(),
        label = "progress"
    )

    LaunchedEffect("app_launch") {
        async(Dispatchers.IO) {
            apps = TreeSet(
                context.packageManager.getInstalledApplications(0)
                    .filterNot { it.isResourceOverlay }
                    .map {
                        LoadedApplicationInfo(
                            it.loadLabel(context.packageManager).toString(),
                            it.loadIcon(context.packageManager),
                            it
                        )
                    }).toList()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            value = filter,
            onValueChange = { filter = it },
            label = {
                Text(stringResource(id = R.string.search))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(Modifier.size(8.dp))

        Box(
            modifier = Modifier.weight(1f)
                .fillMaxWidth()
        ) {
            transition.AnimatedContent {
                if (it) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(apps.size) {
                            val item = apps[it]

                            if (item.label.contains(filter, true) || item.info.packageName.contains(filter, true)) {
                                AppItem(item) { info ->
                                    navController.currentBackStackEntry?.arguments?.putParcelable("appInfo", info.info)

                                    navController.navigate(
                                        route = "list_overlays"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}