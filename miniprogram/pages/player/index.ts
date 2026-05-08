import { applyTheme } from "../../utils/theme";
import { TRACKS } from "../../data/tracks";
import { playerStore } from "../../stores/playerStore";
import { fmtTime } from "../../utils/format";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    tracks: TRACKS.map((t) => ({ ...t, durationStr: fmtTime(t.durationSec) })),
    currentIndex: 0,
    isPlaying: false,
    positionSec: 0,
    track: TRACKS[0],
    positionStr: "00:00",
    durationStr: fmtTime(TRACKS[0].durationSec),
    fav: false,
    favList: [] as string[],
    listOpen: false,
  },
  _unsubPlayer: null as any,
  onLoad() {
    applyTheme(this);
    this._unsubPlayer = playerStore.bind(this, (s) => {
      const track = TRACKS[s.currentIndex];
      return {
        currentIndex: s.currentIndex,
        isPlaying: s.isPlaying,
        positionSec: s.positionSec,
        track,
        positionStr: fmtTime(s.positionSec),
        durationStr: fmtTime(track.durationSec),
        fav: this.data.favList.includes(track.id),
      };
    });
  },
  onShow() { applyTheme(this); },
  onUnload() { this._unsubPlayer?.(); },
  goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) wx.navigateBack({ delta: 1 });
    else wx.switchTab({ url: "/pages/music/index" });
  },
  toggle() {
    playerStore.actions.toggle();
  },
  prev() {
    playerStore.actions.prev();
  },
  next() {
    playerStore.actions.next();
  },
  onSeek(e: any) {
    playerStore.actions.seek(e.detail.value);
  },
  toggleFav() {
    const id = this.data.track.id;
    const cur = this.data.favList;
    const next = cur.indexOf(id) >= 0 ? cur.filter((x) => x !== id) : [...cur, id];
    const fav = next.indexOf(id) >= 0;
    this.setData({ favList: next, fav });
    toast(fav ? "已加入我喜欢的音乐" : "已取消收藏");
  },
  onDownload() {
    toast("已加入下载队列");
  },
  onShare() {
    toast("分享链接已复制");
  },
  openList() {
    this.setData({ listOpen: true });
  },
  closeList() {
    this.setData({ listOpen: false });
  },
  pickTrack(e: any) {
    const i = e.currentTarget.dataset.i;
    playerStore.actions.setIndex(i);
    this.setData({ listOpen: false });
  },
});
