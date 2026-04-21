package com.intangibleheritage.music.core.data

import android.app.Application
import com.intangibleheritage.music.core.data.repository.AudioRepository
import com.intangibleheritage.music.core.data.repository.ArchiveRepository
import com.intangibleheritage.music.core.data.repository.CommunityRepository
import com.intangibleheritage.music.core.data.repository.CourseRepository
import com.intangibleheritage.music.core.data.repository.DataStoreProfileRepository
import com.intangibleheritage.music.core.data.repository.FakeAudioRepository
import com.intangibleheritage.music.core.data.repository.FakeArchiveRepository
import com.intangibleheritage.music.core.data.repository.FakeCommunityRepository
import com.intangibleheritage.music.core.data.repository.FakeCompositionService
import com.intangibleheritage.music.core.data.repository.FakeCourseRepository
import com.intangibleheritage.music.core.data.repository.RemoteCommunityRepository
import com.intangibleheritage.music.core.data.repository.FakeMallRepository
import com.intangibleheritage.music.core.data.repository.FakeMentorReviewService
import com.intangibleheritage.music.core.data.repository.FakeMusicHallRepository
import com.intangibleheritage.music.core.data.repository.FakeInteractionAnalyticsRepository
import com.intangibleheritage.music.core.data.repository.FakeStoriesRepository
import com.intangibleheritage.music.core.data.repository.InteractionAnalyticsRepository
import com.intangibleheritage.music.core.data.repository.MallRepository
import com.intangibleheritage.music.core.data.repository.CompositionService
import com.intangibleheritage.music.core.data.repository.MentorReviewService
import com.intangibleheritage.music.core.data.repository.MusicHallRepository
import com.intangibleheritage.music.core.data.repository.ProfileRepository
import com.intangibleheritage.music.core.data.repository.RemoteMusicHallRepository
import com.intangibleheritage.music.core.data.repository.StoriesRepository
import com.intangibleheritage.music.core.network.HeritageApi

object AppRepositories {

    lateinit var musicHall: MusicHallRepository
        private set
    lateinit var stories: StoriesRepository
        private set
    lateinit var community: CommunityRepository
        private set
    lateinit var mall: MallRepository
        private set
    lateinit var profile: ProfileRepository
        private set
    lateinit var audio: AudioRepository
        private set
    lateinit var archive: ArchiveRepository
        private set
    lateinit var courses: CourseRepository
        private set
    lateinit var composition: CompositionService
        private set
    lateinit var mentorReview: MentorReviewService
        private set
    lateinit var interactionAnalytics: InteractionAnalyticsRepository
        private set

    /**
     * 在 [android.app.Application.onCreate] 中调用一次。
     * @param heritageApi 由 [com.intangibleheritage.music.core.network.NetworkModule] 创建；即使 [useRemoteApi] 为 false 也可传入占位实例。
     */
    fun initialize(application: Application, useRemoteApi: Boolean, heritageApi: HeritageApi) {
        profile = DataStoreProfileRepository(application)
        audio = FakeAudioRepository()
        stories = FakeStoriesRepository()
        community = if (useRemoteApi) {
            RemoteCommunityRepository(heritageApi)
        } else {
            FakeCommunityRepository()
        }
        mall = FakeMallRepository()
        archive = FakeArchiveRepository()
        courses = FakeCourseRepository()
        composition = FakeCompositionService()
        mentorReview = FakeMentorReviewService()
        interactionAnalytics = FakeInteractionAnalyticsRepository()
        musicHall = if (useRemoteApi) {
            RemoteMusicHallRepository(heritageApi)
        } else {
            FakeMusicHallRepository()
        }
    }
}
