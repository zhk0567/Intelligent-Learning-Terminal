import { APP_IMAGES } from "../assets/appImages";

export interface Story {
  id: string;
  title: string;
  author: string;
  category: string;
  excerpt: string;
  publishTime: string;
  likeCount: number;
  commentCount: number;
  readCount: number;
  tags: string[];
  isLiked?: boolean;
  isSaved?: boolean;
  /** 与 Android `StoryListActivity` / 壁画专题等 `coverResId`  drawable 对应。 */
  coverSrc: string;
}

export const STORIES: Story[] = [
  {
    id: "story1",
    title: "高山流水遇知音 · 古琴的千年回响",
    author: "古琴传承人 · 王慕之",
    category: "古琴",
    excerpt: "古琴有四千多年历史，伯牙子期的故事被刻进华夏血脉。每一根琴弦，都是一段山水。",
    publishTime: "3 天前",
    likeCount: 1245,
    commentCount: 86,
    readCount: 8210,
    tags: ["古琴", "高山流水"],
    coverSrc: APP_IMAGES.p1,
  },
  {
    id: "story2",
    title: "敦煌壁画里的乐器图谱",
    author: "敦煌研究所 · 林之雪",
    category: "敦煌",
    excerpt: "莫高窟壁画中，琵琶、箜篌、筚篥仍在飞舞。让我们以现代视角，复原那场盛世音律。",
    publishTime: "5 天前",
    likeCount: 962,
    commentCount: 54,
    readCount: 5420,
    tags: ["敦煌", "壁画", "古乐器"],
    coverSrc: APP_IMAGES.banner2,
  },
  {
    id: "story3",
    title: "蜀绣四十年：一针一线的人生",
    author: "蜀绣大师 · 周月明",
    category: "刺绣",
    excerpt: "她从十六岁握起绣绷，用四十年绣出整片山河。每一根丝线背后都是不愿被时代遗忘的坚持。",
    publishTime: "1 周前",
    likeCount: 2024,
    commentCount: 142,
    readCount: 12560,
    tags: ["蜀绣", "传承"],
    coverSrc: APP_IMAGES.p5,
  },
  {
    id: "story4",
    title: "二胡背后的江南雨巷",
    author: "民乐青年 · 沈听雨",
    category: "二胡",
    excerpt: "弓弦摩擦，是江南屋檐雨声。二胡不是悲鸣，是诉说。",
    publishTime: "2 周前",
    likeCount: 712,
    commentCount: 38,
    readCount: 3210,
    tags: ["二胡", "江南"],
    coverSrc: APP_IMAGES.p2,
  },
  {
    id: "story5",
    title: "古法造纸：一张纸的山水修行",
    author: "造纸传承人 · 程小山",
    category: "技艺",
    excerpt: "从砍竹到成纸需 72 道工序。岁月是最好的造纸师。",
    publishTime: "3 周前",
    likeCount: 1330,
    commentCount: 102,
    readCount: 9230,
    tags: ["古法造纸", "技艺"],
    coverSrc: APP_IMAGES.p4,
  },
  {
    id: "story6",
    title: "皮影戏：方寸之间的光影乾坤",
    author: "皮影戏团 · 老李",
    category: "皮影",
    excerpt: "一张幕布，几张兽皮，便是一座戏台。三尺方寸映照人间。",
    publishTime: "1 个月前",
    likeCount: 1804,
    commentCount: 91,
    readCount: 11070,
    tags: ["皮影", "戏曲"],
    coverSrc: APP_IMAGES.p6,
  },
];
