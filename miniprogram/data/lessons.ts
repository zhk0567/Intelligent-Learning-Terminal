/**
 * 「基础学习」视频课。
 * - 源视频：`data/视频/基础学习/*.mp4`，由 `tools/sync_basic_lesson_videos.py` 拷到 `web/public/video/basic_lesson/`。
 * - 封面：`data/图片/基础学习/*.png` → `tools/sync_basic_lesson_covers.py` 缩成 `basic_lesson_cover_0n.jpg`。
 * - 文案：依据国家级非遗资料整理（首批国家级非遗名录 2006）。
 *
 * 视频文件较大（约 50–90MB），不内嵌进小程序与 Android 包；
 * 通过 [assetUrl] 与图片同源域名访问 ECS/CDN 上的 `/video/...`。
 */
import { assetUrl } from "../utils/assetUrl";

export interface BasicLesson {
  id: string;
  title: string;
  /** 短副标，主页/Tab 列表展示。 */
  artist: string;
  /** 一句话简介，主页卡片描述展示。 */
  desc: string;
  /** 长简介（视频课页展示，包含历史/流派/项目编号等真实信息）。 */
  longDesc: string;
  durationSec: number;
  coverSrc: string;
  /** 视频路径（经 [lessonVideo] 已为可访问 URL，含 STATIC_ORIGIN）。 */
  videoSrc: string;
}

function lessonCover(slot: 0 | 1): string {
  const n = slot + 1;
  return assetUrl(`/images/basic_lesson/basic_lesson_cover_${n < 10 ? `0${n}` : `${n}`}.jpg`);
}

function lessonVideo(slot: 0 | 1): string {
  const n = slot + 1;
  return assetUrl(`/video/basic_lesson/basic_lesson_${n < 10 ? `0${n}` : `${n}`}.mp4`);
}

export const BASIC_LESSONS: BasicLesson[] = [
  {
    id: "lesson_1",
    title: "乡村振兴 福满人间",
    artist: "河洛大鼓",
    desc: "国家级非遗 · 河洛大鼓新编曲目",
    longDesc:
      "河洛大鼓发源于河南偃师，兴于巩义，流行于洛阳、孟津、登封等地，是以说唱叙事为表演形式的传统曲艺。" +
      "2006 年 5 月经国务院批准，被列入第一批国家级非物质文化遗产名录（编号 Ⅴ-12）。" +
      "本曲目《乡村振兴 福满人间》为河洛大鼓代表性传承人创新创作，以乡音乡韵讲述当代河南乡村振兴故事，" +
      "将百年传统大鼓与时代主题结合，是非遗活态传承的典型范例。",
    durationSec: 285,
    coverSrc: lessonCover(0),
    videoSrc: lessonVideo(0),
  },
  {
    id: "lesson_2",
    title: "小包公",
    artist: "四平调",
    desc: "国家级非遗 · 商丘四平调代表剧目",
    longDesc:
      "四平调由豫东花鼓演变而来，1931 年正式定名，流行于豫、鲁、苏、皖四省交界地带。" +
      "2006 年 5 月与河洛大鼓同批列入第一批国家级非物质文化遗产名录。" +
      "《小包公》与《陈三两爬堂》《哑女告状》并称四平调最具影响力的三大代表剧目，" +
      "由商丘市四平调剧团（被誉为「天下第一团」）创排演出，因唱腔平易近人、剧情风趣劝善而深受观众喜爱，" +
      "也是了解四平调声腔与表演程式的入门佳作。",
    durationSec: 120,
    coverSrc: lessonCover(1),
    videoSrc: lessonVideo(1),
  },
];

export function lessonById(id: string): BasicLesson | undefined {
  return BASIC_LESSONS.find((l) => l.id === id);
}

export function lessonVideoUrl(lesson: BasicLesson): string {
  return assetUrl(lesson.videoSrc);
}
