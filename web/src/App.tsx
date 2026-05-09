import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import { ReactNode, Suspense, lazy } from "react";
import AppShell from "./layout/AppShell";
import DesktopShell from "./layout/desktop/DesktopShell";
import { useIsDesktop } from "./hooks/useIsDesktop";

const Splash = lazy(() => import("./pages/Splash"));
const Login = lazy(() => import("./pages/Login"));
const PhoneLogin = lazy(() => import("./pages/PhoneLogin"));
const Register = lazy(() => import("./pages/Register"));
const ChangePassword = lazy(() => import("./pages/ChangePassword"));
const WebView = lazy(() => import("./pages/WebView"));

const MusicLib = lazy(() => import("./pages/MusicLib"));
const Story = lazy(() => import("./pages/Story"));
const Create = lazy(() => import("./pages/Create"));
const Shop = lazy(() => import("./pages/Shop"));
const Profile = lazy(() => import("./pages/Profile"));

const Search = lazy(() => import("./pages/Search"));
const SearchResult = lazy(() => import("./pages/SearchResult"));
const Player = lazy(() => import("./pages/Player"));
const Lesson = lazy(() => import("./pages/Lesson"));
const Detail = lazy(() => import("./pages/Detail"));

const StoryList = lazy(() => import("./pages/StoryList"));
const StoryDetail = lazy(() => import("./pages/StoryDetail"));

const PublishWork = lazy(() => import("./pages/PublishWork"));
const Challenge = lazy(() => import("./pages/Challenge"));
const WorkDetail = lazy(() => import("./pages/WorkDetail"));
const CreatorProfile = lazy(() => import("./pages/CreatorProfile"));

const ShopDetail = lazy(() => import("./pages/ShopDetail"));
const Cart = lazy(() => import("./pages/Cart"));
const Order = lazy(() => import("./pages/Order"));
const Address = lazy(() => import("./pages/Address"));

const Setting = lazy(() => import("./pages/Setting"));
const Favorite = lazy(() => import("./pages/Favorite"));
const History = lazy(() => import("./pages/History"));
const MyWorks = lazy(() => import("./pages/MyWorks"));
const MyLearning = lazy(() => import("./pages/MyLearning"));
const EditProfile = lazy(() => import("./pages/EditProfile"));
const About = lazy(() => import("./pages/About"));

function PageFallback() {
  return (
    <div className="flex h-full items-center justify-center text-text-secondary text-sm">
      加载中…
    </div>
  );
}

const CHROMELESS = /^\/(splash|login|register|change-password|webview)/;

function ChromeSwitch({ children }: { children: ReactNode }) {
  const isDesktop = useIsDesktop();
  const { pathname } = useLocation();
  const chromeless = CHROMELESS.test(pathname);
  if (!isDesktop) return <div className="phone-shell">{children}</div>;
  if (chromeless) return <div className="desktop-frame">{children}</div>;
  return <DesktopShell>{children}</DesktopShell>;
}

export default function App() {
  return (
    <ChromeSwitch>
      <Suspense fallback={<PageFallback />}>
        <Routes>
          <Route path="/" element={<Navigate to="/splash" replace />} />
          <Route path="/splash" element={<Splash />} />

          {/* 登录链路 */}
          <Route path="/login" element={<Login />} />
          <Route path="/login/phone" element={<PhoneLogin />} />
          <Route path="/register" element={<Register />} />
          <Route path="/change-password" element={<ChangePassword />} />
          <Route path="/webview" element={<WebView />} />

          {/* 5 个底部 Tab */}
          <Route element={<AppShell />}>
            <Route path="/music" element={<MusicLib />} />
            <Route path="/story" element={<Story />} />
            <Route path="/create" element={<Create />} />
            <Route path="/shop" element={<Shop />} />
            <Route path="/me" element={<Profile />} />
          </Route>

          {/* 音乐相关 */}
          <Route path="/search" element={<Search />} />
          <Route path="/search/result" element={<SearchResult />} />
          <Route path="/player" element={<Player />} />
          <Route path="/lesson/:id" element={<Lesson />} />
          <Route path="/detail/:id" element={<Detail />} />

          {/* 故事 */}
          <Route path="/story/list" element={<StoryList />} />
          <Route path="/story/:id" element={<StoryDetail />} />

          {/* 创作 */}
          <Route path="/create/publish" element={<PublishWork />} />
          <Route path="/create/challenge" element={<Challenge />} />
          <Route path="/create/work/:id" element={<WorkDetail />} />
          <Route path="/create/creator/:id" element={<CreatorProfile />} />

          {/* 商城 */}
          <Route path="/shop/detail/:id" element={<ShopDetail />} />
          <Route path="/shop/cart" element={<Cart />} />
          <Route path="/shop/order" element={<Order />} />
          <Route path="/shop/address" element={<Address />} />

          {/* 我的 */}
          <Route path="/me/setting" element={<Setting />} />
          <Route path="/me/favorite" element={<Favorite />} />
          <Route path="/me/history" element={<History />} />
          <Route path="/me/works" element={<MyWorks />} />
          <Route path="/me/learning" element={<MyLearning />} />
          <Route path="/me/edit" element={<EditProfile />} />
          <Route path="/about" element={<About />} />

          <Route path="*" element={<Navigate to="/splash" replace />} />
        </Routes>
      </Suspense>
    </ChromeSwitch>
  );
}
