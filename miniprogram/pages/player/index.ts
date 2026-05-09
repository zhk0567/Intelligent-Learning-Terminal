import { applyTheme } from "../../utils/theme";
import { assetUrl } from "../../utils/assetUrl";
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
  _unsubAudio: null as null | (() => void),
  innerAudio: null as WechatMiniprogram.InnerAudioContext | null,
  _audioLastIdx: -1,
  _audioLastSrc: "",
  onLoad() {
    applyTheme(this);
    const inner = wx.createInnerAudioContext();
    this.innerAudio = inner;
    inner.obeyMuteSwitch = true;
    inner.onTimeUpdate(() => {
      const s = playerStore.get();
      const t = TRACKS[s.currentIndex];
      if (!t.audioSrc) return;
      playerStore.actions.setPositionSec(Math.floor(inner.currentTime));
    });
    inner.onEnded(() => {
      playerStore.actions.next();
    });

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

    this._unsubAudio = playerStore.subscribe(() => this.syncInnerAudio());
    this.syncInnerAudio();
  },
  onShow() {
    applyTheme(this);
  },
  onUnload() {
    this._unsubPlayer?.();
    this._unsubAudio?.();
    if (this.innerAudio) {
      this.innerAudio.stop();
      this.innerAudio.destroy();
      this.innerAudio = null;
    }
  },
  syncInnerAudio() {
    const inner = this.innerAudio;
    if (!inner) return;
    const s = playerStore.get();
    const t = TRACKS[s.currentIndex];
    if (!t.audioSrc) {
      if (this._audioLastSrc !== "") {
        inner.stop();
        inner.src = "";
        this._audioLastSrc = "";
      }
      this._audioLastIdx = s.currentIndex;
      return;
    }
    const src = t.audioSrc ? assetUrl(t.audioSrc) : "";
    if (src !== this._audioLastSrc || s.currentIndex !== this._audioLastIdx) {
      this._audioLastSrc = src;
      this._audioLastIdx = s.currentIndex;
      inner.stop();
      inner.src = src;
      if (s.isPlaying) inner.play();
      return;
    }
    if (s.isPlaying && !inner.paused) return;
    if (s.isPlaying) inner.play();
    else inner.pause();
  },
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
    const v = e.detail.value;
    playerStore.actions.seek(v);
    const t = TRACKS[playerStore.get().currentIndex];
    if (t.audioSrc && this.innerAudio) {
      this.innerAudio.seek(v);
    }
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
