import { applyTheme } from "../../utils/theme";
import { ALBUMS } from "../../data/music";
import { APP_IMAGES } from "../../data/appImages";
import { toast } from "../../utils/toast";

const COURSES = [
  {
    id: "c1",
    title: "古琴入门 7 课",
    teacher: "王慕之",
    progress: 4,
    total: 7,
    coverSrc: ALBUMS.find((a) => a.id === "a7")!.coverSrc,
  },
  {
    id: "c2",
    title: "古筝乐理速成",
    teacher: "周月明",
    progress: 2,
    total: 6,
    coverSrc: ALBUMS.find((a) => a.id === "a8")!.coverSrc,
  },
  {
    id: "c3",
    title: "敦煌乐器图谱解读",
    teacher: "林之雪",
    progress: 1,
    total: 8,
    coverSrc: APP_IMAGES.banner2,
  },
].map((c) => ({ ...c, percent: Math.round((c.progress / c.total) * 100) }));

Page({
  data: {
    __themeClass: "theme-dark",
    courses: COURSES,
    recommends: ALBUMS.filter((a) => a.category === "basic"),
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  onContinue() {
    toast("演示课程已锁定");
  },
});
