@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.intangibleheritage.music

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.theme.HeritageTechBackdrop
import com.intangibleheritage.music.feature.community.CommunityScreen
import com.intangibleheritage.music.feature.community.ComposePostScreen
import com.intangibleheritage.music.feature.community.PostDetailScreen
import com.intangibleheritage.music.core.data.model.MallSection
import com.intangibleheritage.music.feature.mall.MallScreen
import com.intangibleheritage.music.feature.mall.MallSectionListScreen
import com.intangibleheritage.music.feature.mall.ProductDetailScreen
import com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen
import com.intangibleheritage.music.feature.musichall.MusicHallScreen
import com.intangibleheritage.music.feature.musichall.MusicHallMoreScreen
import com.intangibleheritage.music.feature.musichall.MusicHallTagResultScreen
import com.intangibleheritage.music.feature.musichall.HeritageArchiveScreen
import com.intangibleheritage.music.feature.musichall.HeritageArchiveDetailScreen
import com.intangibleheritage.music.feature.musichall.HeritageCourseScreen
import com.intangibleheritage.music.feature.musichall.HeritageCourseDetailScreen
import com.intangibleheritage.music.feature.musichall.CourseOutlineScreen
import com.intangibleheritage.music.feature.musichall.CourseMaterialsScreen
import com.intangibleheritage.music.feature.musichall.CourseTutorScreen
import com.intangibleheritage.music.feature.musichall.HeritageCompositionScreen
import com.intangibleheritage.music.feature.musichall.HeritageInteractiveHubScreen
import com.intangibleheritage.music.feature.musichall.HeritageMentorReviewScreen
import com.intangibleheritage.music.feature.player.PlayerScreen
import com.intangibleheritage.music.feature.profile.ProfileScreen
import com.intangibleheritage.music.feature.profile.SettingsNotificationScreen
import com.intangibleheritage.music.feature.profile.SettingsPrivacyScreen
import com.intangibleheritage.music.feature.profile.SettingsScreen
import com.intangibleheritage.music.feature.profile.SettingsThemeScreen
import com.intangibleheritage.music.feature.stories.StoriesScreen
import com.intangibleheritage.music.feature.stories.StoryDetailScreen
import kotlinx.coroutines.launch

/**
 * 根级 Compose 导航：底栏五 Tab（单路由 + HorizontalPager）+ 商品详情 + 播放器。
 * 路由前缀与底栏策略见 [HeritageNavigation]。
 */
private enum class MainTab(
    val labelRes: Int,
    val icon: ImageVector
) {
    MUSIC_HALL(R.string.nav_music_hall, Icons.Outlined.MusicNote),
    STORIES(R.string.nav_stories, Icons.AutoMirrored.Outlined.MenuBook),
    COMMUNITY(R.string.nav_community, Icons.Outlined.Edit),
    MALL(R.string.nav_mall, Icons.Outlined.ShoppingCart),
    PROFILE(R.string.nav_profile, Icons.Outlined.Person)
}

@Composable
fun HeritageApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route.orEmpty()
    val showBottomBar = route == HeritageNavigation.ROUTE_MAIN_TABS
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var lastBackPressMs by remember { mutableLongStateOf(0L) }

    var savedTab by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = savedTab,
        pageCount = { MainTab.entries.size }
    )
    LaunchedEffect(pagerState.settledPage) {
        savedTab = pagerState.settledPage
    }

    BackHandler {
        if (navController.popBackStack()) return@BackHandler
        if (route != HeritageNavigation.ROUTE_MAIN_TABS) {
            navController.navigate(HeritageNavigation.ROUTE_MAIN_TABS) {
                launchSingleTop = true
            }
            return@BackHandler
        }
        val now = System.currentTimeMillis()
        if (now - lastBackPressMs < 2000L) {
            (context as? ComponentActivity)?.finish()
        } else {
            lastBackPressMs = now
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.press_back_again_to_exit))
            }
        }
    }
    val onMusicHallFeedback = remember(snackbarHostState, scope) {
        { message: String ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            Unit
        }
    }
    val navigateSingleTop = remember(navController) {
        { targetRoute: String ->
            navController.navigate(targetRoute) {
                launchSingleTop = true
            }
        }
    }
    // 使用 scrollToPage：瞬时切页，避免 animateScrollToPage 导致底栏「选中态/涟漪」要等动画结束才更新，体感像按钮延迟
    val jumpToTab = remember(pagerState, scope) {
        { index: Int ->
            scope.launch { pagerState.scrollToPage(index) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showBottomBar) {
            HeritageTechBackdrop(simplified = true)
        }
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    ) {
                        MainTab.entries.forEachIndexed { index, tab ->
                            val selected = pagerState.currentPage == index
                            NavigationBarItem(
                                selected = selected,
                                onClick = { jumpToTab(index) },
                                icon = {
                                    Icon(
                                        tab.icon,
                                        contentDescription = stringResource(tab.labelRes)
                                    )
                                },
                                label = { Text(stringResource(tab.labelRes)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HeritageNavigation.ROUTE_MAIN_TABS,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                composable(HeritageNavigation.ROUTE_MAIN_TABS) {
                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 0,
                        userScrollEnabled = false,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (MainTab.entries[page]) {
                            MainTab.MUSIC_HALL -> MusicHallScreen(
                                onPlayTrack = { trackId ->
                                    navigateSingleTop(HeritageNavigation.player(trackId))
                                },
                                onFeedback = onMusicHallFeedback,
                                onOpenNotifications = {
                                    navigateSingleTop(HeritageNavigation.ROUTE_NOTIFICATIONS)
                                },
                                onOpenMore = { sectionTitle ->
                                    navigateSingleTop(HeritageNavigation.musicHallMore(sectionTitle))
                                },
                                onOpenTagResult = { tagName ->
                                    navigateSingleTop(HeritageNavigation.musicHallTag(tagName))
                                },
                                onOpenArchive = {
                                    navigateSingleTop(HeritageNavigation.ROUTE_ARCHIVE)
                                },
                                onOpenCourses = {
                                    navigateSingleTop(HeritageNavigation.ROUTE_COURSES)
                                },
                                onOpenInteractive = {
                                    navigateSingleTop(HeritageNavigation.ROUTE_INTERACTIVE)
                                }
                            )
                            MainTab.STORIES -> StoriesScreen(
                                onOpenStory = { id -> navigateSingleTop(HeritageNavigation.story(id)) },
                                onOpenNotifications = {
                                    navigateSingleTop(HeritageNavigation.ROUTE_NOTIFICATIONS)
                                }
                            )
                            MainTab.COMMUNITY -> CommunityScreen(
                                onOpenPost = { id ->
                                    navigateSingleTop(HeritageNavigation.communityPost(id))
                                },
                                onComposeClick = { navigateSingleTop(HeritageNavigation.ROUTE_COMPOSE) }
                            )
                            MainTab.MALL -> MallScreen(
                                onProductClick = { id ->
                                    navigateSingleTop(HeritageNavigation.product(id))
                                },
                                onOpenSection = { sec ->
                                    navigateSingleTop(HeritageNavigation.mallSection(sec.routeKey))
                                }
                            )
                            MainTab.PROFILE -> ProfileScreen(
                                onGridItemClick = { id ->
                                    navigateSingleTop(ProfileContentNav.routeFor(id))
                                },
                                onOpenSettings = { navigateSingleTop(HeritageNavigation.ROUTE_SETTINGS) },
                                onExploreMusicHall = { jumpToTab(MainTab.MUSIC_HALL.ordinal) },
                                onExploreStories = { jumpToTab(MainTab.STORIES.ordinal) }
                            )
                        }
                    }
                }
                composable(HeritageNavigation.ROUTE_NOTIFICATIONS) {
                    NotificationCenterScreen(onBack = { navController.popBackStack() })
                }
                composable(HeritageNavigation.ROUTE_ARCHIVE) {
                    HeritageArchiveScreen(
                        onBack = { navController.popBackStack() },
                        onOpenDetail = { assetId ->
                            navigateSingleTop(HeritageNavigation.archiveDetail(assetId))
                        }
                    )
                }
                composable(HeritageNavigation.ROUTE_COURSES) {
                    HeritageCourseScreen(
                        onBack = { navController.popBackStack() },
                        onOpenDetail = { courseId ->
                            navigateSingleTop(HeritageNavigation.courseDetail(courseId))
                        }
                    )
                }
                composable(HeritageNavigation.ROUTE_INTERACTIVE) {
                    HeritageInteractiveHubScreen(
                        onBack = { navController.popBackStack() },
                        onOpenComposition = { navigateSingleTop(HeritageNavigation.ROUTE_COMPOSITION) },
                        onOpenReview = { navigateSingleTop(HeritageNavigation.ROUTE_MENTOR_REVIEW) }
                    )
                }
                composable(HeritageNavigation.ROUTE_COMPOSITION) {
                    HeritageCompositionScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(HeritageNavigation.ROUTE_MENTOR_REVIEW) {
                    HeritageMentorReviewScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_COURSE_DETAIL_PREFIX}{courseId}",
                    arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                ) { entry ->
                    val courseId = entry.arguments?.getString("courseId") ?: return@composable
                    HeritageCourseDetailScreen(
                        courseId = courseId,
                        onBack = { navController.popBackStack() },
                        onOpenStory = { sid -> navigateSingleTop(HeritageNavigation.story(sid)) },
                        onOpenTrack = { tid -> navigateSingleTop(HeritageNavigation.player(tid)) },
                        onOpenProduct = { pid -> navigateSingleTop(HeritageNavigation.product(pid)) },
                        onOpenOutline = { cid -> navigateSingleTop(HeritageNavigation.courseOutline(cid)) },
                        onOpenMaterials = { cid -> navigateSingleTop(HeritageNavigation.courseMaterials(cid)) },
                        onOpenTutor = { cid -> navigateSingleTop(HeritageNavigation.courseTutor(cid)) }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_COURSE_OUTLINE_PREFIX}{courseId}",
                    arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                ) { entry ->
                    val courseId = entry.arguments?.getString("courseId") ?: return@composable
                    CourseOutlineScreen(
                        courseId = courseId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_COURSE_MATERIALS_PREFIX}{courseId}",
                    arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                ) { entry ->
                    val courseId = entry.arguments?.getString("courseId") ?: return@composable
                    CourseMaterialsScreen(
                        courseId = courseId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_COURSE_TUTOR_PREFIX}{courseId}",
                    arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                ) { entry ->
                    val courseId = entry.arguments?.getString("courseId") ?: return@composable
                    CourseTutorScreen(
                        courseId = courseId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_ARCHIVE_DETAIL_PREFIX}{assetId}",
                    arguments = listOf(navArgument("assetId") { type = NavType.StringType })
                ) { entry ->
                    val assetId = entry.arguments?.getString("assetId") ?: return@composable
                    HeritageArchiveDetailScreen(
                        assetId = assetId,
                        onBack = { navController.popBackStack() },
                        onOpenStory = { sid -> navigateSingleTop(HeritageNavigation.story(sid)) },
                        onOpenTrack = { trackId -> navigateSingleTop(HeritageNavigation.player(trackId)) }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_MUSIC_HALL_MORE_PREFIX}{sectionTitle}",
                    arguments = listOf(navArgument("sectionTitle") { type = NavType.StringType })
                ) { entry ->
                    val raw = entry.arguments?.getString("sectionTitle") ?: return@composable
                    MusicHallMoreScreen(
                        sectionTitle = Uri.decode(raw),
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_MUSIC_HALL_TAG_PREFIX}{tagName}",
                    arguments = listOf(navArgument("tagName") { type = NavType.StringType })
                ) { entry ->
                    val raw = entry.arguments?.getString("tagName") ?: return@composable
                    MusicHallTagResultScreen(
                        tagName = Uri.decode(raw),
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_STORY_PREFIX}{storyId}",
                    arguments = listOf(navArgument("storyId") { type = NavType.StringType })
                ) { entry ->
                    val sid = entry.arguments?.getString("storyId") ?: return@composable
                    StoryDetailScreen(
                        storyId = sid,
                        onBack = { navController.popBackStack() },
                        onOpenProduct = { pid -> navigateSingleTop(HeritageNavigation.product(pid)) }
                    )
                }
                composable(HeritageNavigation.ROUTE_COMPOSE) {
                    ComposePostScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_COMMUNITY_POST_PREFIX}{postId}",
                    arguments = listOf(navArgument("postId") { type = NavType.StringType })
                ) { entry ->
                    val pid = entry.arguments?.getString("postId") ?: return@composable
                    PostDetailScreen(
                        postId = pid,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(HeritageNavigation.ROUTE_SETTINGS) {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onOpenAbout = { navigateSingleTop(HeritageNavigation.ROUTE_ABOUT) },
                        onOpenLicenses = { navigateSingleTop(HeritageNavigation.ROUTE_LICENSES) },
                        onOpenNotifications = {
                            navigateSingleTop(HeritageNavigation.ROUTE_SETTINGS_NOTIFICATIONS)
                        },
                        onOpenPrivacy = {
                            navigateSingleTop(HeritageNavigation.ROUTE_SETTINGS_PRIVACY)
                        },
                        onOpenTheme = {
                            navigateSingleTop(HeritageNavigation.ROUTE_SETTINGS_THEME)
                        }
                    )
                }
                composable(HeritageNavigation.ROUTE_SETTINGS_NOTIFICATIONS) {
                    SettingsNotificationScreen(onBack = { navController.popBackStack() })
                }
                composable(HeritageNavigation.ROUTE_SETTINGS_PRIVACY) {
                    SettingsPrivacyScreen(onBack = { navController.popBackStack() })
                }
                composable(HeritageNavigation.ROUTE_SETTINGS_THEME) {
                    SettingsThemeScreen(onBack = { navController.popBackStack() })
                }
                composable(HeritageNavigation.ROUTE_ABOUT) {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
                composable(HeritageNavigation.ROUTE_LICENSES) {
                    ImageLicensesScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = "${HeritageNavigation.ROUTE_MALL_SECTION_PREFIX}{sectionKey}",
                    arguments = listOf(navArgument("sectionKey") { type = NavType.StringType })
                ) { entry ->
                    val key = entry.arguments?.getString("sectionKey") ?: return@composable
                    when (val section = MallSection.fromRouteKey(key)) {
                        null -> InvalidDeepLinkScreen(
                            title = stringResource(R.string.nav_invalid_mall_section_title),
                            message = stringResource(R.string.nav_invalid_mall_section_message),
                            onBack = { navController.popBackStack() }
                        )
                        else -> MallSectionListScreen(
                            section = section,
                            onBack = { navController.popBackStack() },
                            onOpenProduct = { pid -> navigateSingleTop(HeritageNavigation.product(pid)) }
                        )
                    }
                }
                composable(
                    route = "product/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("productId") ?: return@composable
                    ProductDetailScreen(
                        productId = id,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "player/{trackId}",
                    arguments = listOf(navArgument("trackId") { type = NavType.StringType })
                ) { entry ->
                    val trackId = entry.arguments?.getString("trackId") ?: return@composable
                    PlayerScreen(
                        trackId = trackId,
                        onBack = { navController.popBackStack() },
                        onPlayTrackId = { newId ->
                            navController.navigate(HeritageNavigation.player(newId)) {
                                popUpTo(HeritageNavigation.player(trackId)) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
