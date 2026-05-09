import { applyTheme } from "../../utils/theme";
import { lessonById, lessonVideoUrl, BasicLesson } from "../../data/lessons";
import { fmtTime } from "../../utils/format";

Page({
  data: {
    __themeClass: "theme-dark",
    lesson: null as BasicLesson | null,
    videoUrl: "",
    durationStr: "00:00",
  },
  onLoad(query: any) {
    applyTheme(this);
    const id = query?.id || "lesson_1";
    const lesson = lessonById(id);
    if (!lesson) {
      wx.showToast({ title: "课程不存在", icon: "none" });
      return;
    }
    this.setData({
      lesson,
      videoUrl: lessonVideoUrl(lesson),
      durationStr: fmtTime(lesson.durationSec),
    });
  },
  onShow() {
    applyTheme(this);
  },
  onShareAppMessage() {
    const l = this.data.lesson;
    return { title: l ? `${l.title} · ${l.artist}` : "古韵薪传 · 基础学习" };
  },
});
