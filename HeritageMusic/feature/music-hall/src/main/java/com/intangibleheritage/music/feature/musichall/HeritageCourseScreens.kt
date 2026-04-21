package com.intangibleheritage.music.feature.musichall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.CourseLevel
import com.intangibleheritage.music.core.data.model.HeritageCourse
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen
import com.intangibleheritage.music.core.ui.theme.ScreenLayout

@Composable
fun HeritageCourseScreen(
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val courses = remember { AppRepositories.courses.allCourses() }
    var selectedLevel by remember { mutableStateOf<CourseLevel?>(null) }
    val filtered = remember(courses, selectedLevel) {
        courses.filter { selectedLevel == null || it.level == selectedLevel }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.course_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Text(
                    text = stringResource(R.string.course_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = ScreenLayout.TopSpacing)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CourseLevelChip(
                        label = stringResource(R.string.course_level_all),
                        selected = selectedLevel == null,
                        onClick = { selectedLevel = null }
                    )
                    CourseLevelChip(
                        label = stringResource(R.string.course_level_basic),
                        selected = selectedLevel == CourseLevel.Basic,
                        onClick = { selectedLevel = CourseLevel.Basic }
                    )
                    CourseLevelChip(
                        label = stringResource(R.string.course_level_advanced),
                        selected = selectedLevel == CourseLevel.Advanced,
                        onClick = { selectedLevel = CourseLevel.Advanced }
                    )
                    CourseLevelChip(
                        label = stringResource(R.string.course_level_research),
                        selected = selectedLevel == CourseLevel.Research,
                        onClick = { selectedLevel = CourseLevel.Research }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.course_result_count, filtered.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(filtered, key = { it.id }) { course ->
                CourseListCard(course = course, onClick = { onOpenDetail(course.id) })
            }
        }
    }
}

@Composable
fun HeritageCourseDetailScreen(
    courseId: String,
    onBack: () -> Unit,
    onOpenStory: (String) -> Unit,
    onOpenTrack: (String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onOpenOutline: (String) -> Unit,
    onOpenMaterials: (String) -> Unit,
    onOpenTutor: (String) -> Unit
) {
    val course = remember(courseId) { AppRepositories.courses.courseById(courseId) }
    if (course == null) {
        InvalidDeepLinkScreen(
            title = stringResource(R.string.nav_invalid_title),
            message = stringResource(R.string.course_invalid_message),
            onBack = onBack
        )
        return
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.course_detail_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
                        AsyncImage(
                            model = course.coverRes,
                            contentDescription = course.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${stringResource(R.string.course_detail_tutor)}：${course.tutorName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${stringResource(R.string.course_detail_level)}：${courseLevelLabel(course.level)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${stringResource(R.string.course_detail_lessons)}：${course.lessons}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                CourseInfoSection(
                    title = stringResource(R.string.course_detail_summary),
                    body = course.summary
                )
            }
            item {
                CourseInfoSection(
                    title = stringResource(R.string.course_detail_goals),
                    body = course.goals.joinToString(separator = "\n") { "- $it" }
                )
            }
            item {
                CourseInfoSection(
                    title = stringResource(R.string.course_detail_copyright),
                    body = course.copyrightNote
                )
            }
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
                        Text(
                            text = stringResource(R.string.course_detail_modules),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = { onOpenOutline(course.id) }) {
                                Text(stringResource(R.string.course_open_outline))
                            }
                            Button(onClick = { onOpenMaterials(course.id) }) {
                                Text(stringResource(R.string.course_open_materials))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { onOpenTutor(course.id) }) {
                            Text(stringResource(R.string.course_open_tutor))
                        }
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
                        Text(
                            text = stringResource(R.string.course_detail_related),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            val sid = course.relatedStoryId
                            val tid = course.relatedTrackId
                            if (sid != null) {
                                Button(onClick = { onOpenStory(sid) }) {
                                    Text(stringResource(R.string.course_open_story))
                                }
                            }
                            if (tid != null) {
                                Button(onClick = { onOpenTrack(tid) }) {
                                    Text(stringResource(R.string.course_open_track))
                                }
                            }
                            Button(onClick = { onOpenProduct(courseToProductId(course.id)) }) {
                                Text(stringResource(R.string.course_open_product))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseOutlineScreen(
    courseId: String,
    onBack: () -> Unit
) {
    val course = remember(courseId) { AppRepositories.courses.courseById(courseId) }
    if (course == null) {
        InvalidDeepLinkScreen(
            title = stringResource(R.string.nav_invalid_title),
            message = stringResource(R.string.course_invalid_message),
            onBack = onBack
        )
        return
    }
    val lessons = remember(course.id) { buildCourseLessons(course) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.course_outline_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing))
                Text(
                    text = stringResource(R.string.course_outline_subtitle, course.title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(lessons) { lesson ->
                CourseInfoSection(
                    title = lesson.title,
                    body = lesson.summary
                )
            }
        }
    }
}

@Composable
fun CourseMaterialsScreen(
    courseId: String,
    onBack: () -> Unit
) {
    val course = remember(courseId) { AppRepositories.courses.courseById(courseId) }
    if (course == null) {
        InvalidDeepLinkScreen(
            title = stringResource(R.string.nav_invalid_title),
            message = stringResource(R.string.course_invalid_message),
            onBack = onBack
        )
        return
    }
    val materials = remember(course.id) { buildCourseMaterials(course) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.course_materials_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing))
                Text(
                    text = stringResource(R.string.course_materials_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(materials) { material ->
                CourseInfoSection(
                    title = material.title,
                    body = material.summary
                )
            }
        }
    }
}

@Composable
fun CourseTutorScreen(
    courseId: String,
    onBack: () -> Unit
) {
    val course = remember(courseId) { AppRepositories.courses.courseById(courseId) }
    if (course == null) {
        InvalidDeepLinkScreen(
            title = stringResource(R.string.nav_invalid_title),
            message = stringResource(R.string.course_invalid_message),
            onBack = onBack
        )
        return
    }
    val tutor = remember(course.id) { buildTutorProfile(course) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(R.string.course_tutor_title),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Spacer(modifier = Modifier.height(ScreenLayout.TopSpacing))
                CourseInfoSection(
                    title = tutor.name,
                    body = tutor.bio
                )
            }
            item {
                CourseInfoSection(
                    title = stringResource(R.string.course_tutor_focus),
                    body = tutor.focus
                )
            }
            item {
                CourseInfoSection(
                    title = stringResource(R.string.course_tutor_works),
                    body = tutor.works.joinToString(separator = "\n") { "- $it" }
                )
            }
        }
    }
}

@Composable
private fun CourseLevelChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (selected) 0.9f else 0.5f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CourseListCard(
    course: HeritageCourse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Row(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
            AsyncImage(
                model = course.coverRes,
                contentDescription = course.title,
                modifier = Modifier
                    .height(88.dp)
                    .fillMaxWidth(0.34f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${course.tutorName} · ${courseLevelLabel(course.level)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.course_lessons_count, course.lessons),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CourseInfoSection(title: String, body: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(ScreenLayout.CardContentPadding)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun courseLevelLabel(level: CourseLevel): String = when (level) {
    CourseLevel.Basic -> stringResource(R.string.course_level_basic)
    CourseLevel.Advanced -> stringResource(R.string.course_level_advanced)
    CourseLevel.Research -> stringResource(R.string.course_level_research)
}

private data class CourseLesson(val title: String, val summary: String)
private data class CourseMaterial(val title: String, val summary: String)
private data class CourseTutorProfile(
    val name: String,
    val bio: String,
    val focus: String,
    val works: List<String>
)

private fun buildCourseLessons(course: HeritageCourse): List<CourseLesson> = listOf(
    CourseLesson("第 1 单元：课程导论", "目标、学习路径与评估方式说明。"),
    CourseLesson("第 2 单元：核心技法", "分解关键技法并配套示范。"),
    CourseLesson("第 3 单元：曲目实践", "结合典型片段完成实操训练。"),
    CourseLesson("第 4 单元：阶段复盘", "复盘常见问题并给出改进建议。")
).mapIndexed { idx, item ->
    item.copy(title = "课时 ${idx + 1} · ${item.title}")
}

private fun buildCourseMaterials(course: HeritageCourse): List<CourseMaterial> = listOf(
    CourseMaterial("讲义 PDF", "${course.title} 对应讲义（演示版）。"),
    CourseMaterial("示范音频", "课内示范音频切片与练习伴奏（演示版）。"),
    CourseMaterial("谱例/案例", "课程核心谱例与典型案例合集（演示版）。")
)

private fun buildTutorProfile(course: HeritageCourse): CourseTutorProfile = CourseTutorProfile(
    name = course.tutorName,
    bio = "${course.tutorName}，长期从事${courseLevelText(course.level)}教学与非遗音乐传播。",
    focus = "研究方向：${course.summary}",
    works = listOf("《${course.title}》", "非遗音乐公开课（演示）", "地方音乐工作坊（演示）")
)

private fun courseLevelText(level: CourseLevel): String = when (level) {
    CourseLevel.Basic -> "入门"
    CourseLevel.Advanced -> "进阶"
    CourseLevel.Research -> "研究"
}

private fun courseToProductId(courseId: String): String = when (courseId) {
    "course_basic_guqin" -> "dunhuang_magnet"
    "course_advanced_dizi" -> "silk_scarf"
    "course_research_bells" -> "bronze_bells"
    else -> "pipa_bookmark"
}
