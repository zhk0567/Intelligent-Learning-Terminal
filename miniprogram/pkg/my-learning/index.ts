import { applyTheme } from "../../utils/theme";
import { BASIC_LESSONS } from "../../data/lessons";
import { APP_IMAGES } from "../../data/appImages";
import { toast } from "../../utils/toast";

const COURSES = [
  {
    id: "c1",
    title: BASIC_LESSONS[0].title,
    teacher: BASIC_LESSONS[0].artist,
    progress: 4,
    total: 7,
    coverSrc: BASIC_LESSONS[0].coverSrc,
  },
  {
    id: "c2",
    title: BASIC_LESSONS[1].title,
    teacher: BASIC_LESSONS[1].artist,
    progress: 2,
    total: 6,
    coverSrc: BASIC_LESSONS[1].coverSrc,
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
    recommends: BASIC_LESSONS,
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  onContinue() {
    toast("演示课程已锁定");
  },
});
