import { APP_IMAGES } from "./appImages";

export interface Post {
  id: number;
  title: string;
  content: string;
  category: string;
  author: string;
  timeText: string;
  likeCount: number;
  commentCount: number;
  coverSrc: string;
}

export const POSTS: Post[] = [
  { id: 1, title: "我把奶奶的旗袍改成了演出服", content: "用了五十年的真丝旗袍，被我改成了首演的演出服。她说这件衣服终于回到舞台。", category: "传承", author: "锦书",   timeText: "2 小时前", likeCount: 824,  commentCount: 96,  coverSrc: APP_IMAGES.p1 },
  { id: 2, title: "古筝×电子音乐：第一次尝试",   content: "在《渔舟唱晚》的旋律里加入了电子鼓点，发现古风也可以很 Cyberpunk！",       category: "改编", author: "晚风过山", timeText: "5 小时前", likeCount: 612,  commentCount: 48,  coverSrc: APP_IMAGES.p2 },
  { id: 3, title: "学习剪纸三个月，第一次拿到夸奖", content: "妈妈说我比她进步更快。原来非遗也能这么治愈。",                          category: "学习", author: "纸鸢",   timeText: "昨天",     likeCount: 1246, commentCount: 153, coverSrc: APP_IMAGES.p3 },
  { id: 4, title: "二胡 vs 小提琴：一场跨界对话",  content: "今晚和我的乐队伙伴用二胡和小提琴合奏《茉莉花》，感动到自己。",          category: "演出", author: "孤舟",   timeText: "2 天前",   likeCount: 988,  commentCount: 74,  coverSrc: APP_IMAGES.p4 },
  { id: 5, title: "自制竹笛全过程记录",            content: "从砍竹、晾干、烫膜，到调音，整整一个月。送给爸爸做生日礼物。",          category: "手作", author: "听雪",   timeText: "3 天前",   likeCount: 1402, commentCount: 121, coverSrc: APP_IMAGES.p5 },
];

export const CHALLENGES = [
  { id: "c1", title: "30 天经典曲目挑战",    desc: "每天打卡一首中国民乐，连签 30 天可领取古风周边。",          progress: 12, total: 30, rewardLabel: "限定徽章", coverSrc: APP_IMAGES.banner1 },
  { id: "c2", title: "原创非遗短视频大赛",   desc: "用 60 秒讲述你身边的非遗故事，最高奖金 ¥3000。",            progress: 0,  total: 60, rewardLabel: "现金奖励", coverSrc: APP_IMAGES.banner2 },
  { id: "c3", title: "古乐器跨界改编",       desc: "把任意流行歌曲用民乐改编上传，与传承人云合奏。",            progress: 6,  total: 20, rewardLabel: "联名乐谱", coverSrc: APP_IMAGES.banner3 },
];
