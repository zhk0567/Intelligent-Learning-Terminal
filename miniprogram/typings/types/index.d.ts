/**
 * 微信小程序 wx 命名空间类型补全（最小集）。
 * 完整版可通过 `npm i miniprogram-api-typings` 引入；这里提供工程内常用 API 的类型，
 * 避免在没有 npm 安装时编辑器仍能给出补全。
 */

/* eslint-disable @typescript-eslint/no-explicit-any */

declare namespace WechatMiniprogram {
  interface IAnyObject {
    [k: string]: any;
  }

  interface ToastOption {
    title: string;
    icon?: "success" | "error" | "loading" | "none";
    image?: string;
    duration?: number;
    mask?: boolean;
  }

  interface ModalOption {
    title?: string;
    content?: string;
    confirmText?: string;
    cancelText?: string;
    showCancel?: boolean;
    success?: (res: { confirm: boolean; cancel: boolean }) => void;
  }

  interface ActionSheetOption {
    itemList: string[];
    itemColor?: string;
    success?: (res: { tapIndex: number }) => void;
    fail?: (err: any) => void;
  }

  interface NavigateOption {
    url: string;
    success?: () => void;
    fail?: (err: any) => void;
  }

  interface PreviewImageOption {
    urls: string[];
    current?: string;
  }
}

declare const wx: {
  showToast: (opt: WechatMiniprogram.ToastOption) => void;
  hideToast: () => void;
  showModal: (opt: WechatMiniprogram.ModalOption) => void;
  showActionSheet: (opt: WechatMiniprogram.ActionSheetOption) => void;
  navigateTo: (opt: WechatMiniprogram.NavigateOption) => void;
  redirectTo: (opt: WechatMiniprogram.NavigateOption) => void;
  switchTab: (opt: WechatMiniprogram.NavigateOption) => void;
  navigateBack: (opt?: { delta?: number }) => void;
  reLaunch: (opt: WechatMiniprogram.NavigateOption) => void;
  previewImage: (opt: WechatMiniprogram.PreviewImageOption) => void;
  setStorageSync: (key: string, value: any) => void;
  getStorageSync: <T = any>(key: string) => T;
  removeStorageSync: (key: string) => void;
  getSystemInfoSync: () => {
    platform: string;
    safeArea: { top: number; bottom: number };
    windowHeight: number;
    windowWidth: number;
    statusBarHeight: number;
    theme?: "light" | "dark";
  };
  onThemeChange: (cb: (res: { theme: "light" | "dark" }) => void) => void;
  setNavigationBarColor: (opt: {
    frontColor: "#ffffff" | "#000000";
    backgroundColor: string;
    animation?: { duration?: number; timingFunc?: string };
  }) => void;
  createSelectorQuery: () => any;
};

declare function App<T = any>(options: T): void;
declare function Page<TData = any, TCustom = any>(
  options: { data?: TData } & TCustom & WechatMiniprogram.IAnyObject,
): void;
declare function Component<TData = any, TProps = any, TMethods = any>(
  options: {
    data?: TData;
    properties?: TProps;
    methods?: TMethods;
    options?: any;
    externalClasses?: string[];
    observers?: WechatMiniprogram.IAnyObject;
    lifetimes?: WechatMiniprogram.IAnyObject;
    pageLifetimes?: WechatMiniprogram.IAnyObject;
  } & WechatMiniprogram.IAnyObject,
): void;
declare function Behavior(options: WechatMiniprogram.IAnyObject): string;
declare function getApp<T = IAppOption>(): T;
declare function getCurrentPages(): WechatMiniprogram.IAnyObject[];
