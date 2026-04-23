
(function() {
  var _musicCache = null;
  var _musicSearchTimer = null;
  var _commKwTimer = null;
  var communityKw = '';
  var LS_FAV = 'heritageWebFav';
  var LS_HIST = 'heritageWebHist';
  var LS_SET = 'heritageWebSettings';
  var THEME_META = { tech: '#050814', paper: '#f7f2e8', neon: '#0d0218', forest: '#101c16' };
  function applyTheme(name) {
    var h = document.documentElement;
    h.classList.remove('theme-paper', 'theme-neon', 'theme-forest');
    if (name === 'paper') h.classList.add('theme-paper');
    else if (name === 'neon') h.classList.add('theme-neon');
    else if (name === 'forest') h.classList.add('theme-forest');
    var mc = document.getElementById('meta-theme-color');
    if (mc) mc.setAttribute('content', THEME_META[name] || THEME_META.tech);
    document.querySelectorAll('.theme-chip').forEach(function(b) {
      b.classList.toggle('on', b.getAttribute('data-theme') === (name || 'tech'));
    });
    try { localStorage.setItem('heritageWebTheme', name || 'tech'); } catch (e1) {}
  }
  try {
    var st = localStorage.getItem('heritageWebTheme');
    if (st === 'paper' || st === 'neon' || st === 'forest' || st === 'tech') applyTheme(st);
  } catch (e2) {}
  document.addEventListener('click', function(ev) {
    var chip = ev.target.closest('.theme-chip');
    if (!chip || chip.tagName !== 'BUTTON') return;
    var dt = chip.getAttribute('data-theme');
    if (!dt) return;
    ev.preventDefault();
    applyTheme(dt);
    toast('已切换主题（演示，已写入 localStorage）');
  });
  document.addEventListener('keydown', function(ev) {
    if (ev.key !== 'Escape') return;
    var sp = document.getElementById('app-subpage');
    var back = document.getElementById('subpage-back');
    if (sp && sp.classList.contains('show') && back) {
      ev.preventDefault();
      back.click();
    }
  });
  function toast(msg) {
    var el = document.getElementById('snackbar');
    el.textContent = msg;
    el.classList.add('show');
    clearTimeout(el._t);
    el._t = setTimeout(function() { el.classList.remove('show'); }, 2600);
  }
  function escapeHtml(s) {
    if (!s) return '';
    return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
  }
  function fmtDay(ts) {
    if (!ts) return '';
    var d = new Date(ts);
    function z(n) { return n < 10 ? '0' + n : '' + n; }
    return d.getFullYear() + '-' + z(d.getMonth() + 1) + '-' + z(d.getDate());
  }
  function readJsonLs(key, def) {
    try {
      var s = localStorage.getItem(key);
      if (s) return JSON.parse(s);
    } catch (eLs) {}
    return def;
  }
  function writeJsonLs(key, obj) {
    try { localStorage.setItem(key, JSON.stringify(obj)); } catch (eW) {}
  }
  function playTrack(trackId, title) {
    var url = AUDIO_DEMO[trackId];
    var a = document.getElementById('demo-audio');
    if (!url) {
      toast('暂无试听');
      return;
    }
    a.src = url;
    a.play().catch(function() {
      toast('播放失败（请检查网络或浏览器格式支持）');
    });
    toast(title ? ('播放：' + title) : '开始播放');
    try {
      var lh = readJsonLs(LS_HIST, []);
      lh.unshift({ id: 'h' + Date.now(), title: title || '未命名曲目', sub: '音乐馆 · 试听', ts: Date.now() });
      if (lh.length > 30) lh.length = 30;
      writeJsonLs(LS_HIST, lh);
    } catch (eHist) {}
  }
  var _archiveItems = [];
  var _courseItems = [];
  var _mallCatalog = null;
  var _mallCtx = { fromSection: false, sectionId: '', sectionTitle: '', sectionDesc: '', sort: 'default' };
  var _ixHubData = null;
  var _loadedStories = false;
  var _loadedCommunity = false;
  var _loadedMall = false;
  var subState = { kind: 'none', screen: 'main' };
  function subpageShow(title) {
    document.getElementById('subpage-title').textContent = title;
    var sp = document.getElementById('app-subpage');
    sp.classList.add('show');
    sp.setAttribute('aria-hidden', 'false');
  }
  function subpageHide() {
    var sp = document.getElementById('app-subpage');
    sp.classList.remove('show');
    sp.setAttribute('aria-hidden', 'true');
    document.getElementById('subpage-body').innerHTML = '';
    subState = { kind: 'none', screen: 'main' };
  }
  function renderArchiveList() {
    var body = document.getElementById('subpage-body');
    body.innerHTML = '<p class="sub-lead">支持按素材类型筛选，展示资料溯源路径与版权状态（MVP）。共 ' + _archiveItems.length + ' 条资料（演示）。</p><div id="archive-list-root"></div>';
    var root = document.getElementById('archive-list-root');
    _archiveItems.forEach(function(it) {
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'archive-card';
      btn.innerHTML = '<img src="' + escapeHtml(it.coverImageUrl) + '" alt="" loading="lazy"/>' +
        '<div class="bd"><div class="badge">' + escapeHtml(it.typeLabel || '') + '</div>' +
        '<div class="t">' + escapeHtml(it.title) + '</div>' +
        '<div class="m">' + escapeHtml(it.region) + ' · ' + escapeHtml(it.genre) + '</div>' +
        '<div class="sc">可信度 ' + escapeHtml(String(it.credibilityScore)) + '</div></div>';
      btn.addEventListener('click', function() {
        subState.screen = 'detail';
        document.getElementById('subpage-title').textContent = '资料详情';
        renderArchiveDetail(it);
      });
      root.appendChild(btn);
    });
  }
  function renderArchiveDetail(it) {
    var body = document.getElementById('subpage-body');
    var tl = (it.sourceTimeline || []).map(function(x) { return '<li>' + escapeHtml(x) + '</li>'; }).join('');
    var goalsHtml = '<div class="detail-block"><h3>' + escapeHtml(it.title) + '</h3>' +
      '<p style="margin:0;font-size:0.82rem;color:var(--on-surface-variant);line-height:1.5">' +
      '<strong style="color:var(--primary)">' + escapeHtml(it.typeLabel || '') + '</strong> · ' +
      escapeHtml(it.region) + ' · ' + escapeHtml(it.genre) + ' · ' + escapeHtml(it.era) + '</p>' +
      '<p style="margin:10px 0 0;font-size:0.82rem;color:var(--on-surface-variant)">传承人：' + escapeHtml(it.inheritor) + '</p>' +
      '<p style="margin:8px 0 0;font-size:0.82rem;color:var(--on-surface-variant)">来源链路：' + escapeHtml(it.sourcePath || '') + '</p>' +
      '<p style="margin:8px 0 0;font-size:0.82rem;color:var(--on-surface-variant)">版权：' + escapeHtml(it.copyrightStatus || '') + '</p>' +
      '<p style="margin:8px 0 0;font-size:0.82rem;color:var(--on-surface-variant)">可信度：<strong style="color:var(--secondary)">' + escapeHtml(String(it.credibilityScore)) + '</strong></p>' +
      '<p style="margin:12px 0 4px;font-size:0.75rem;color:var(--primary)">来源时间轴</p><ul>' + tl + '</ul>' +
      '<div class="detail-actions">' +
      '<button type="button" id="ar-play">播放关联曲目</button>' +
      '<button type="button" id="ar-story">查看关联故事</button></div></div>';
    body.innerHTML = goalsHtml;
    var pb = document.getElementById('ar-play');
    if (pb) pb.addEventListener('click', function() {
      if (it.relatedTrackId) playTrack(it.relatedTrackId, it.title);
      else toast('暂无关联曲目');
    });
    var sb = document.getElementById('ar-story');
    if (sb) sb.addEventListener('click', function() { toast('关联故事：网页演示中'); });
  }
  function renderCourseList() {
    var body = document.getElementById('subpage-body');
    body.innerHTML = '<p class="sub-lead">按层级筛选课程，查看课程目标、版权说明与关联内容（演示）。共 ' + _courseItems.length + ' 门课程。</p><div id="course-list-root"></div>';
    var root = document.getElementById('course-list-root');
    _courseItems.forEach(function(c) {
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'course-card';
      btn.innerHTML = '<img src="' + escapeHtml(c.coverImageUrl) + '" alt="" loading="lazy"/>' +
        '<div class="bd"><div class="badge">' + escapeHtml(c.levelLabel || '') + '</div>' +
        '<div class="t">' + escapeHtml(c.title) + '</div>' +
        '<div class="m">导师 ' + escapeHtml(c.tutorName) + ' · ' + escapeHtml(String(c.lessons)) + ' 课时</div></div>';
      btn.addEventListener('click', function() {
        subState.screen = 'detail';
        document.getElementById('subpage-title').textContent = '课程详情';
        renderCourseDetail(c);
      });
      root.appendChild(btn);
    });
  }
  function renderCourseDetail(c) {
    var body = document.getElementById('subpage-body');
    var gl = (c.goals || []).map(function(g) { return '<li>' + escapeHtml(g) + '</li>'; }).join('');
    body.innerHTML = '<div class="detail-block"><h3>' + escapeHtml(c.title) + '</h3>' +
      '<p style="margin:0;font-size:0.82rem;color:var(--on-surface-variant)">' + escapeHtml(c.summary || '') + '</p>' +
      '<p style="margin:10px 0 0;font-size:0.82rem">导师：<strong style="color:var(--on-surface)">' + escapeHtml(c.tutorName) + '</strong> · 层级 ' + escapeHtml(c.levelLabel || '') + ' · 课时 ' + escapeHtml(String(c.lessons)) + '</p>' +
      '<p style="margin:8px 0 0;font-size:0.78rem;color:var(--on-surface-variant)">版权：' + escapeHtml(c.copyrightNote || '') + '</p>' +
      '<p style="margin:12px 0 4px;font-size:0.75rem;color:var(--primary)">课程目标</p><ul>' + gl + '</ul>' +
      '<div class="detail-actions"><button type="button" id="co-play">试听关联曲目</button></div></div>';
    var pb = document.getElementById('co-play');
    if (pb) pb.addEventListener('click', function() {
      if (c.relatedTrackId) playTrack(c.relatedTrackId, c.title);
      else toast('暂无关联曲目');
    });
  }
  function openArchiveFlow() {
    subState = { kind: 'archive', screen: 'list' };
    subpageShow('非遗音乐资料库');
    fetch('/archive/assets').then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(function(d) {
      _archiveItems = d.items || [];
      renderArchiveList();
    }).catch(function() {
      toast('资料库加载失败');
      subpageHide();
    });
  }
  function openCoursesFlow() {
    subState = { kind: 'course', screen: 'list' };
    subpageShow('传承人亲授课程');
    fetch('/courses').then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(function(d) {
      _courseItems = d.courses || [];
      renderCourseList();
    }).catch(function() {
      toast('课程加载失败');
      subpageHide();
    });
  }
  function renderInteractiveHub(d) {
    var body = document.getElementById('subpage-body');
    var data = d || _ixHubData || { title: '', subtitle: '', modules: [] };
    var html = '<div class="ix-hero"><h2>' + escapeHtml(data.title || '') + '</h2><p>' + escapeHtml(data.subtitle || '') + '</p></div>';
    (data.modules || []).forEach(function(m) {
      html += '<div class="ix-card"><div class="ix-t">' + escapeHtml(m.title) + '</div>' +
        '<div class="ix-d">' + escapeHtml(m.description || '') + '</div>' +
        '<button type="button" class="detail-actions ix-go" data-ix="' + escapeHtml(m.id) + '">' + escapeHtml(m.actionLabel || '进入') + '</button></div>';
    });
    body.innerHTML = html;
    body.querySelectorAll('[data-ix]').forEach(function(btn) {
      btn.addEventListener('click', function() {
        var id = btn.getAttribute('data-ix');
        if (id === 'compose') openIxCompose();
        else if (id === 'review') openIxReview();
        else toast('演示');
      });
    });
  }
  function openIxCompose() {
    subState = { kind: 'interactive', screen: 'compose' };
    document.getElementById('subpage-title').textContent = '智能编曲（MVP）';
    var body = document.getElementById('subpage-body');
    body.innerHTML = '<p class="sub-lead">输入风格、情绪、节奏参数，生成演示片段并提供试听/收藏按钮。</p>' +
      '<div class="ix-form">' +
      '<div class="ix-field"><label for="ixc-style">风格</label><input id="ixc-style" type="text" placeholder="如：古韵、电子"/></div>' +
      '<div class="ix-field"><label for="ixc-mood">情绪</label><input id="ixc-mood" type="text" placeholder="如：空灵、热烈"/></div>' +
      '<div class="ix-field"><label for="ixc-bpm">节奏 BPM</label><input id="ixc-bpm" type="number" min="40" max="220" placeholder="120"/></div>' +
      '<button type="button" class="detail-actions" id="ixc-gen">生成片段</button>' +
      '<p style="margin:12px 0 0;font-size:0.72rem;color:var(--outline)">当前为模板生成版本，后续可替换为真实模型服务。</p></div>';
    document.getElementById('ixc-gen').addEventListener('click', function() {
      toast('当前为模板生成版本，后续可替换为真实模型服务。');
    });
  }
  function openIxReview() {
    subState = { kind: 'interactive', screen: 'review' };
    document.getElementById('subpage-title').textContent = 'AI 导师点评（MVP）';
    var body = document.getElementById('subpage-body');
    body.innerHTML = '<p class="sub-lead">输入练习音频与关注维度，输出多维评分与改进建议。</p>' +
      '<div class="ix-form">' +
      '<div class="ix-field"><label for="ixr-file">练习音频文件名</label><input id="ixr-file" type="text" placeholder="如：practice_take_01.wav"/></div>' +
      '<div class="ix-field"><label for="ixr-focus">关注维度</label><select id="ixr-focus">' +
      '<option>节奏</option><option>音准</option><option>表现力</option></select></div>' +
      '<button type="button" class="detail-actions" id="ixr-gen">生成点评</button>' +
      '<p style="margin:12px 0 0;font-size:0.72rem;color:var(--outline)">当前为规则引擎点评版本，结果用于演示流程。</p></div>';
    document.getElementById('ixr-gen').addEventListener('click', function() {
      toast('当前为规则引擎点评版本，结果用于演示流程。');
    });
  }
  function openInteractiveFlow() {
    subState = { kind: 'interactive', screen: 'hub' };
    subpageShow('非遗交互体验');
    fetch('/interactive/hub').then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(function(d) {
      _ixHubData = d;
      renderInteractiveHub(d);
    }).catch(function() {
      toast('互动模块加载失败');
      subpageHide();
    });
  }
  function renderMallSectionProducts() {
    var sid = _mallCtx.sectionId;
    var desc = _mallCtx.sectionDesc || '';
    if (!_mallCtx.sort) _mallCtx.sort = 'default';
    var prods = (_mallCatalog && _mallCatalog.products) ? _mallCatalog.products.filter(function(x) { return x.section === sid; }) : [];
    var sorted = prods.slice();
    if (_mallCtx.sort === 'asc') sorted.sort(function(a, b) { return (a.priceYuan || 0) - (b.priceYuan || 0); });
    else if (_mallCtx.sort === 'desc') sorted.sort(function(a, b) { return (b.priceYuan || 0) - (a.priceYuan || 0); });
    var sortHtml = '<div class="mall-sort-row"><span class="mall-sort-label">排序</span>' +
      '<button type="button" data-ms="default"' + (_mallCtx.sort === 'default' ? ' class="on"' : '') + '>综合</button>' +
      '<button type="button" data-ms="asc"' + (_mallCtx.sort === 'asc' ? ' class="on"' : '') + '>价格升序</button>' +
      '<button type="button" data-ms="desc"' + (_mallCtx.sort === 'desc' ? ' class="on"' : '') + '>价格降序</button></div>';
    var body = document.getElementById('subpage-body');
    body.innerHTML = '<p class="sub-lead">' + escapeHtml(desc) + '</p>' + sortHtml +
      '<p style="margin:0 0 8px;font-size:0.8rem;color:var(--on-surface-variant)">以下为该分区商品（演示）。共 ' + sorted.length + ' 件。</p>' +
      '<div class="product-grid" id="mall-sec-grid"></div>';
    body.querySelectorAll('.mall-sort-row button').forEach(function(b) {
      b.addEventListener('click', function() {
        _mallCtx.sort = b.getAttribute('data-ms') || 'default';
        renderMallSectionProducts();
      });
    });
    var grid = document.getElementById('mall-sec-grid');
    sorted.forEach(function(p) {
      var card = document.createElement('div');
      card.className = 'product-card';
      var btn = document.createElement('button');
      btn.type = 'button';
      var rv = (p.rating != null && p.rating !== '') ? p.rating : '—';
      var rc = p.reviewCount != null ? p.reviewCount : 0;
      btn.innerHTML = '<img src="' + escapeHtml(p.imageUrl) + '" alt="" loading="lazy"/>' +
        '<div class="pd"><div class="pt">' + escapeHtml(p.title) + '</div>' +
        '<div class="pp">¥' + escapeHtml(String(p.priceYuan)) + '</div>' +
        '<div class="pr">' + escapeHtml(String(rv)) + ' 分 · ' + escapeHtml(String(rc)) + ' 条评价</div></div>';
      btn.addEventListener('click', function() { openProductDetail(p, true); });
      card.appendChild(btn);
      grid.appendChild(card);
    });
  }
  function renderProductDetailBody(p) {
    var body = document.getElementById('subpage-body');
    var rv = (p.rating != null && p.rating !== '') ? p.rating : '—';
    var rc = p.reviewCount != null ? p.reviewCount : 0;
    body.innerHTML = '<div class="product-detail-hero"><img src="' + escapeHtml(p.imageUrl) + '" alt="" loading="lazy"/></div>' +
      '<div class="detail-block"><h3>' + escapeHtml(p.title) + '</h3>' +
      '<p style="margin:0;font-size:1.1rem;color:var(--primary);font-weight:600">¥' + escapeHtml(String(p.priceYuan)) + '</p>' +
      '<p style="margin:8px 0 0;font-size:0.82rem;color:var(--on-surface-variant)">' + escapeHtml(String(rv)) + ' 分 · ' + escapeHtml(String(rc)) + ' 条评价</p>' +
      '<p style="margin:12px 0 0;font-size:0.88rem;color:var(--on-surface-variant);line-height:1.55">' + escapeHtml(p.description || '') + '</p>' +
      '<p style="margin:12px 0 0;font-size:0.78rem;color:var(--on-surface-variant);line-height:1.5">现货 48 小时内发货（演示文案）。合作快递：顺丰 / 中通，偏远地区可能延时。</p>' +
      '<p style="margin:8px 0 0;font-size:0.78rem;color:var(--on-surface-variant);line-height:1.5">签收 7 日内可申请无理由退换（演示环境不执行真实物流）。</p>' +
      '<div class="detail-actions" style="margin-top:14px">' +
      '<button type="button" id="pd-cart">加入购物车</button>' +
      '<button type="button" id="pd-buy">立即购买</button></div>' +
      '<p style="margin:10px 0 0;font-size:0.72rem;color:var(--outline)">演示环境：未接入支付与订单</p></div>';
    var cart = document.getElementById('pd-cart');
    if (cart) cart.addEventListener('click', function() { toast('演示环境：未接入支付与订单'); });
    var buy = document.getElementById('pd-buy');
    if (buy) buy.addEventListener('click', function() { toast('演示环境：未接入支付与订单'); });
  }
  function openMallSectionFlow(sectionId, title, desc) {
    if (!_mallCatalog) {
      toast('商城数据未就绪，请稍后重试');
      return;
    }
    subState = { kind: 'mall', screen: 'section' };
    _mallCtx.sectionId = sectionId || '';
    _mallCtx.sectionTitle = title || '';
    _mallCtx.sectionDesc = desc || '';
    _mallCtx.sort = 'default';
    subpageShow(title || '商城分区');
    renderMallSectionProducts();
  }
  function openProductDetail(p, fromSection) {
    subpageShow('商品详情');
    subState.kind = 'mall';
    subState.screen = 'product';
    _mallCtx.fromSection = !!fromSection;
    renderProductDetailBody(p);
  }
  function openCommunityPostDetail(p) {
    subpageShow('帖子详情');
    subState = { kind: 'community', screen: 'detail' };
    var cat = p.category ? (CAT[p.category] || p.category) : '';
    var body = document.getElementById('subpage-body');
    body.innerHTML = '<div class="detail-block">' +
      '<div class="cat" style="margin-bottom:8px">' + escapeHtml(cat) + '</div>' +
      '<h3 style="margin:0 0 8px">' + escapeHtml(p.title) + '</h3>' +
      '<div class="sub" style="margin-bottom:12px;color:var(--secondary)">' + escapeHtml(p.subtitle || '') + '</div>' +
      '<p style="margin:0;font-size:0.9rem;color:var(--on-surface-variant);line-height:1.65">' + escapeHtml(p.body || '') + '</p>' +
      '<p style="margin:14px 0 0;font-size:0.78rem;color:var(--outline)">正文、作者信息与多媒体资源待接入接口后展示。</p>' +
      '<div class="post-actions">' +
      '<button type="button" id="post-like">点赞</button>' +
      '<button type="button" id="post-share">分享</button></div>' +
      '<div class="post-comment-box">' +
      '<label for="post-cmt" style="display:block;font-size:0.78rem;color:var(--primary);margin-bottom:6px">写下你的感受...</label>' +
      '<textarea id="post-cmt" placeholder="写下你的感受..."></textarea>' +
      '<button type="button" class="detail-actions" style="margin-top:10px" id="post-send">发送</button></div></div>';
    var send = document.getElementById('post-send');
    if (send) send.addEventListener('click', function() {
      var ta = document.getElementById('post-cmt');
      var t = ta ? String(ta.value || '').trim() : '';
      if (!t) toast('请先输入评论内容');
      else toast('评论已发送');
    });
    var lk = document.getElementById('post-like');
    if (lk) lk.addEventListener('click', function() { toast('点赞（演示）'); });
    var sh = document.getElementById('post-share');
    if (sh) sh.addEventListener('click', function() { toast('分享（演示）'); });
  }
  function openMusicTagDetail(label) {
    var lab = String(label || '');
    subpageShow('话题「' + lab + '」');
    subState = { kind: 'musicTag', screen: 'main' };
    var body = document.getElementById('subpage-body');
    body.innerHTML = '<p class="sub-lead">聚合与该标签相关的曲目、故事与商城周边；正式版将按权重与版权信息排序展示。</p>' +
      '<div class="detail-block"><h3>' + escapeHtml(lab) + '</h3>' +
      '<p style="margin:0;font-size:0.88rem;color:var(--on-surface-variant);line-height:1.65">当前为离线演示。你可继续浏览音乐馆其它模块，或前往商城、创作社区发现相关内容。</p>' +
      '<div class="detail-actions"><button type="button" id="mt-mall">打开商城</button><button type="button" id="mt-comm">打开社区</button></div></div>';
    document.getElementById('mt-mall').addEventListener('click', function() { subpageHide(); window.location.href = '/mall'; });
    document.getElementById('mt-comm').addEventListener('click', function() { subpageHide(); window.location.href = '/community'; });
  }
  function openStoryDetail(it) {
    subpageShow('故事详情');
    subState = { kind: 'story', screen: 'detail' };
    var body = document.getElementById('subpage-body');
    var ov = it.overlay ? '<p style="color:var(--secondary);font-size:0.88rem;margin:0 0 10px">' + escapeHtml(it.overlay) + '</p>' : '';
    body.innerHTML = '<div class="product-detail-hero"><img src="' + escapeHtml(it.imageUrl) + '" alt="" style="max-height:320px"/></div>' +
      '<div class="detail-block"><h3>故事「' + escapeHtml(it.id || '') + '」</h3>' + ov +
      '<p style="margin:0;font-size:0.9rem;color:var(--on-surface-variant);line-height:1.65">非遗音乐与地方风物相结合的灵感记录。正文、作者信息与多媒体资源待接入接口后展示。</p>' +
      '<p style="margin:12px 0 0;font-size:0.82rem;color:var(--on-surface-variant)">更多精彩内容即将上线</p>' +
      '<div class="detail-actions" style="margin-top:14px"><button type="button" id="st-rel">相关推荐</button></div></div>';
    var br = document.getElementById('st-rel');
    if (br) br.addEventListener('click', function() { toast('更多精彩内容即将上线（演示）'); });
  }
  function openMusicHighlightDetail(title, cardId) {
    var t = String(title || '');
    subpageShow(t || '详情');
    subState = { kind: 'musicCard', screen: 'main' };
    var body = document.getElementById('subpage-body');
    var extra = '更多非遗音乐专题内容与策展信息将在后续版本接入。';
    if (t.indexOf('跨界') >= 0) {
      extra = '非遗跨界聚合演出策展、品牌联名与城市音乐周等活动入口。可在此查看活动日程、合作创作者与购票说明（演示占位，非真实票务）。';
    } else if (t.indexOf('基础') >= 0 || t.indexOf('学习') >= 0) {
      extra = '基础学习提供节拍习惯、谱例缩略预览与练耳小题等轻量训练；完整课程体系将在接入「传承人亲授课程」后联动开放（演示占位）。';
    }
    body.innerHTML = '<div class="detail-block"><h3>' + escapeHtml(t) + '</h3>' +
      '<p style="margin:0;font-size:0.88rem;color:var(--on-surface-variant);line-height:1.65">' + escapeHtml(extra) + '</p>' +
      '<p style="margin:12px 0 0;font-size:0.75rem;color:var(--outline)">卡片 ID：' + escapeHtml(String(cardId || '')) + ' · 演示数据</p>' +
      '<div class="detail-actions" style="margin-top:14px"><button type="button" id="mh-more">去看商品</button></div></div>';
    var bm = document.getElementById('mh-more');
    if (bm) bm.addEventListener('click', function() { toast('请前往「商城」选购文创与教程周边（演示）'); });
  }
  function openProfileScreen(key) {
    subState = { kind: 'profile', screen: key };
    var ttl = { fav: '我的收藏', hist: '观看历史', notif: '消息与通知', theme: '主题设置', settings: '设置', about: '关于本站', licenses: '图片致谢与许可', edit: '编辑资料' };
    subpageShow(ttl[key] || '我的');
    var body = document.getElementById('subpage-body');
    if (key === 'edit') renderPfEdit(body);
    else if (key === 'fav') renderPfFavorites(body);
    else if (key === 'hist') renderPfHistory(body);
    else if (key === 'notif') renderPfNotifications(body);
    else if (key === 'theme') renderPfTheme(body);
    else if (key === 'settings') renderPfSettings(body);
    else if (key === 'about') renderPfAbout(body);
    else if (key === 'licenses') renderPfLicenses(body);
    else body.innerHTML = '<p class="sub-lead">功能筹备中</p>';
  }
  function renderPfEdit(body) {
    var nick = '非遗旅人';
    var bio = '感受音乐，感受世界';
    try {
      var o = readJsonLs('heritageWebProfile', {});
      if (o.nick) nick = o.nick;
      if (o.bio) bio = o.bio;
    } catch (eP) {}
    body.innerHTML = '<p class="sub-lead">访客模式下可修改本机展示的昵称与签名（演示，写入 localStorage）。</p>' +
      '<div class="detail-block"><div class="ix-field"><label for="pf-nick">昵称</label><input id="pf-nick" type="text" value="' + escapeHtml(nick) + '"/></div>' +
      '<div class="ix-field"><label for="pf-bio">签名</label><textarea id="pf-bio" rows="3">' + escapeHtml(bio) + '</textarea></div>' +
      '<div class="detail-actions"><button type="button" id="pf-save">保存</button></div></div>';
    document.getElementById('pf-save').addEventListener('click', function() {
      var n = document.getElementById('pf-nick');
      var b = document.getElementById('pf-bio');
      writeJsonLs('heritageWebProfile', { nick: n ? n.value : '', bio: b ? b.value : '' });
      var nb = document.querySelector('.profile-banner .nick');
      var bb = document.querySelector('.profile-banner .bio');
      if (nb) nb.textContent = n ? n.value : '';
      if (bb) bb.textContent = b ? b.value : '';
      toast('已保存（演示）');
      subpageHide();
    });
  }
  function renderPfFavorites(body) {
    function paint() {
      var list = readJsonLs(LS_FAV, []);
      var html = '<p class="sub-lead">收藏的故事、专题与商品摘录（演示：保存在本机 localStorage）。</p>';
      if (!list.length) {
        html += '<div class="detail-block"><p style="margin:0;font-size:0.88rem;color:var(--on-surface-variant);line-height:1.55">暂无收藏。可在故事、音乐专题或商品详情中通过「收藏」类操作积累条目（演示）。</p>' +
          '<div class="detail-actions" style="margin-top:12px"><button type="button" id="pf-fav-demo">写入演示收藏</button></div></div>';
      } else {
        html += '<div id="pf-fav-list" class="pf-card-list"></div>';
      }
      body.innerHTML = html;
      var db = document.getElementById('pf-fav-demo');
      if (db) db.addEventListener('click', function() {
        writeJsonLs(LS_FAV, [
          { id: 'fv1', title: '钟鼓和鸣', sub: '音乐馆 · 轮播专题', ts: Date.now() - 86400000 },
          { id: 'fv2', title: '烟火弦音', sub: '故事 · 推荐卡片', ts: Date.now() - 172800000 },
          { id: 'fv3', title: '敦煌月影磁吸灯', sub: '商城 · 文创演示', ts: Date.now() - 3600000 }
        ]);
        toast('已写入演示收藏');
        paint();
      });
      var root = document.getElementById('pf-fav-list');
      if (root) {
        list.forEach(function(it, idx) {
          var blk = document.createElement('div');
          blk.className = 'detail-block';
          blk.innerHTML = '<h3 style="margin:0 0 6px;font-size:0.95rem;color:var(--on-surface)">' + escapeHtml(it.title) + '</h3>' +
            '<p style="margin:0;font-size:0.78rem;color:var(--on-surface-variant)">' + escapeHtml(it.sub || '') + '</p>' +
            '<p style="margin:8px 0 0;font-size:0.7rem;color:var(--outline)">' + escapeHtml(fmtDay(it.ts)) + '</p>' +
            '<div class="detail-actions" style="margin-top:10px">' +
            '<button type="button" class="pf-fav-open" data-idx="' + idx + '">查看</button>' +
            '<button type="button" class="pf-fav-rm" data-idx="' + idx + '">移除</button></div>';
          root.appendChild(blk);
        });
        root.querySelectorAll('.pf-fav-open').forEach(function(b) {
          b.addEventListener('click', function() {
            var i = parseInt(b.getAttribute('data-idx'), 10);
            var it = readJsonLs(LS_FAV, [])[i];
            if (it) toast('演示打开：' + (it.title || ''));
          });
        });
        root.querySelectorAll('.pf-fav-rm').forEach(function(b) {
          b.addEventListener('click', function() {
            var i = parseInt(b.getAttribute('data-idx'), 10);
            var arr = readJsonLs(LS_FAV, []);
            arr.splice(i, 1);
            writeJsonLs(LS_FAV, arr);
            toast('已移除');
            paint();
          });
        });
      }
    }
    paint();
  }
  function renderPfHistory(body) {
    function paint() {
      var list = readJsonLs(LS_HIST, []);
      var html = '<p class="sub-lead">最近播放与浏览摘录（演示：保存在本机 localStorage；在音乐馆试听会自动记录）。</p>';
      if (!list.length) {
        html += '<div class="detail-block"><p style="margin:0;font-size:0.88rem;color:var(--on-surface-variant);line-height:1.55">暂无记录。请在音乐馆点击带试听标识的卡片。</p>' +
          '<div class="detail-actions" style="margin-top:12px"><button type="button" id="pf-hist-demo">写入演示历史</button></div></div>';
      } else {
        html += '<div class="detail-actions" style="margin-bottom:10px"><button type="button" id="pf-hist-clear">清空历史</button></div><div id="pf-hist-list" class="pf-card-list"></div>';
      }
      body.innerHTML = html;
      var hd = document.getElementById('pf-hist-demo');
      if (hd) hd.addEventListener('click', function() {
        writeJsonLs(LS_HIST, [
          { id: 'h1', title: '古韵新声', sub: '音乐馆 · 热门', ts: Date.now() - 7200000 },
          { id: 'h2', title: '丝路弦歌', sub: '音乐馆 · 热门', ts: Date.now() - 86400000 },
          { id: 'h3', title: '东方回响', sub: '音乐馆 · 精选', ts: Date.now() - 172800000 }
        ]);
        toast('已写入演示历史');
        paint();
      });
      var hc = document.getElementById('pf-hist-clear');
      if (hc) hc.addEventListener('click', function() {
        writeJsonLs(LS_HIST, []);
        toast('已清空');
        paint();
      });
      var root = document.getElementById('pf-hist-list');
      if (root) {
        list.forEach(function(it) {
          var blk = document.createElement('div');
          blk.className = 'detail-block';
          blk.innerHTML = '<h3 style="margin:0 0 6px;font-size:0.95rem;color:var(--on-surface)">' + escapeHtml(it.title) + '</h3>' +
            '<p style="margin:0;font-size:0.78rem;color:var(--on-surface-variant)">' + escapeHtml(it.sub || '') + '</p>' +
            '<p style="margin:8px 0 0;font-size:0.7rem;color:var(--outline)">' + escapeHtml(fmtDay(it.ts)) + '</p>';
          root.appendChild(blk);
        });
      }
    }
    paint();
  }
  function renderPfNotifications(body) {
    body.innerHTML = '<p class="sub-lead">系统公告、活动与互动提醒（离线演示数据）。</p>' +
      '<button type="button" class="pf-notif unread" id="pf-n1"><div class="nt">今天 · 10:20</div><h3 class="nh">非遗音乐网页版更新</h3><p class="nb">新增「我的」二级页面与主题记忆，欢迎体验演示流程。</p></button>' +
      '<button type="button" class="pf-notif unread" id="pf-n2"><div class="nt">昨天</div><h3 class="nh">商城分区排序说明</h3><p class="nb">四分区支持价格排序与关键词筛选，正式环境将同步订单与库存。</p></button>' +
      '<button type="button" class="pf-notif" id="pf-n3"><div class="nt">更早</div><h3 class="nh">创作社区守则</h3><p class="nb">请尊重版权与署名；违规内容将被下架（演示文案）。</p></button>' +
      '<div class="detail-actions" style="margin-top:12px"><button type="button" id="pf-n-all">全部标为已读</button></div>';
    ['pf-n1', 'pf-n2', 'pf-n3'].forEach(function(id) {
      var el = document.getElementById(id);
      if (el) el.addEventListener('click', function() { el.classList.remove('unread'); toast('已打开通知（演示）'); });
    });
    document.getElementById('pf-n-all').addEventListener('click', function() {
      ['pf-n1', 'pf-n2', 'pf-n3'].forEach(function(id) {
        var el = document.getElementById(id);
        if (el) el.classList.remove('unread');
      });
      toast('已全部标为已读（演示）');
    });
  }
  function renderPfTheme(body) {
    body.innerHTML = '<p class="sub-lead">四套界面主题与色板说明如下；选择会写入本机 localStorage，下次打开本站时恢复。</p>' +
      '<div class="theme-strip" style="margin-bottom:14px"><p class="theme-strip-title">选择主题</p><div class="theme-chips">' +
      '<button type="button" class="theme-chip" data-theme="tech">科技深色</button>' +
      '<button type="button" class="theme-chip" data-theme="paper">纸墨浅色</button>' +
      '<button type="button" class="theme-chip" data-theme="neon">霓虹紫蓝</button>' +
      '<button type="button" class="theme-chip" data-theme="forest">森林绿金</button></div></div>' +
      '<div class="detail-block"><h3>无障碍与对比度</h3><p style="margin:0;font-size:0.82rem;color:var(--on-surface-variant);line-height:1.55">浅色纸墨主题适合强光环境阅读；霓虹与森林主题为高对比氛围色，正式版可继续微调 WCAG 对比度。</p></div>';
    try {
      var cur = localStorage.getItem('heritageWebTheme') || 'tech';
      applyTheme(cur === 'paper' || cur === 'neon' || cur === 'forest' || cur === 'tech' ? cur : 'tech');
    } catch (eTh) { applyTheme('tech'); }
  }
  function renderPfSettings(body) {
    var def = { wifiOnly: false, hdCache: true, autoNext: false, pushDemo: true };
    var st = readJsonLs(LS_SET, def);
    if (typeof st.wifiOnly !== 'boolean') st.wifiOnly = def.wifiOnly;
    if (typeof st.hdCache !== 'boolean') st.hdCache = def.hdCache;
    if (typeof st.autoNext !== 'boolean') st.autoNext = def.autoNext;
    if (typeof st.pushDemo !== 'boolean') st.pushDemo = def.pushDemo;
    function row(id, label, on) {
      return '<button type="button" class="pf-set-row" data-set="' + id + '"><span class="sl">' + label + '</span><span class="sr">' + (on ? '开' : '关') + '</span></button>';
    }
    function paint() {
      st = readJsonLs(LS_SET, def);
      body.innerHTML = '<p class="sub-lead">本站常用开关（演示）。账号、支付与订单请在正式业务系统中完成。</p>' +
        row('wifiOnly', '仅 Wi‑Fi 下加载高清封面', !!st.wifiOnly) +
        row('hdCache', '允许浏览器缓存试听片段', !!st.hdCache) +
        row('autoNext', '试听结束后自动下一首（演示）', !!st.autoNext) +
        row('pushDemo', '接收演示版活动通知（本地 Toast）', !!st.pushDemo) +
        '<div class="detail-block" style="margin-top:14px"><h3>数据与隐私</h3><p style="margin:0;font-size:0.82rem;color:var(--on-surface-variant);line-height:1.55">本页不向服务器上报个人数据；收藏与历史仅存于浏览器 localStorage，清除站点数据会一并删除。</p></div>';
      body.querySelectorAll('.pf-set-row').forEach(function(btn) {
        btn.addEventListener('click', function() {
          var k = btn.getAttribute('data-set');
          if (!k) return;
          st[k] = !st[k];
          writeJsonLs(LS_SET, st);
          toast('已更新「' + (btn.querySelector('.sl').textContent || k) + '」');
          paint();
        });
      });
    }
    paint();
  }
  function renderPfAbout(body) {
    body.innerHTML = '<div class="detail-block"><h3>非遗音乐学习终端 · 网页演示</h3>' +
      '<p style="margin:0;font-size:0.88rem;color:var(--on-surface-variant);line-height:1.6">版本 <strong style="color:var(--primary)">0.3.0-web</strong>（FastAPI 服务 + 独立静态前端）。本站为完整网页演示，可单独部署与扩展接口。</p>' +
      '<dl style="margin:14px 0 0;font-size:0.82rem;color:var(--on-surface-variant)"><dt style="color:var(--primary);font-size:0.72rem">包含模块</dt><dd style="margin:4px 0 0">音乐馆、故事、创作社区、商城、资料库、课程、互动编曲/点评、我的。</dd>' +
      '<dt style="color:var(--primary);font-size:0.72rem;margin-top:10px">技术栈</dt><dd style="margin:4px 0 0">FastAPI · Uvicorn · 单页 HTML/CSS/JS（演示数据内嵌）。</dd></dl>' +
      '<div class="detail-actions" style="margin-top:14px"><button type="button" id="pf-ab-lic">图片致谢与许可</button><button type="button" id="pf-ab-check">检查更新（演示）</button></div></div>';
    document.getElementById('pf-ab-lic').addEventListener('click', function() { openProfileScreen('licenses'); });
    document.getElementById('pf-ab-check').addEventListener('click', function() { toast('当前已是最新演示版'); });
  }
  function renderPfLicenses(body) {
    body.innerHTML = '<p class="sub-lead">演示环境所用图片与音频的来源说明；正式上架请替换为自有或签约素材。</p>' +
      '<div class="detail-block"><h3>图片</h3><p style="margin:0;font-size:0.82rem;color:var(--on-surface-variant);line-height:1.65">瀑布流与轮播等位置使用 <strong style="color:var(--on-surface)">Lorem Picsum</strong>（picsum.photos）随机占位图，遵循其服务条款，仅用于开发演示。</p></div>' +
      '<div class="detail-block"><h3>试听音频</h3><p style="margin:0;font-size:0.82rem;color:var(--on-surface-variant);line-height:1.65">示范曲目来自 <strong style="color:var(--on-surface)">Wikimedia Commons</strong> 的公有领域或开放许可录音（OGG），仅作流媒体格式与播放流程演示。</p></div>' +
      '<div class="detail-block"><h3>字体与界面</h3><p style="margin:0;font-size:0.82rem;color:var(--on-surface-variant);line-height:1.65">界面使用系统字体栈，不额外加载网络字体文件。</p></div>';
  }
  document.getElementById('subpage-back').addEventListener('click', function() {
    if (subState.kind === 'archive' && subState.screen === 'detail') {
      subState.screen = 'list';
      document.getElementById('subpage-title').textContent = '非遗音乐资料库';
      renderArchiveList();
      return;
    }
    if (subState.kind === 'course' && subState.screen === 'detail') {
      subState.screen = 'list';
      document.getElementById('subpage-title').textContent = '传承人亲授课程';
      renderCourseList();
      return;
    }
    if (subState.kind === 'mall' && subState.screen === 'product') {
      if (_mallCtx.fromSection) {
        subState.screen = 'section';
        document.getElementById('subpage-title').textContent = _mallCtx.sectionTitle;
        renderMallSectionProducts();
      } else {
        subpageHide();
      }
      return;
    }
    if (subState.kind === 'mall' && subState.screen === 'section') {
      subpageHide();
      return;
    }
    if (subState.kind === 'interactive' && subState.screen === 'compose') {
      subState = { kind: 'interactive', screen: 'hub' };
      document.getElementById('subpage-title').textContent = '非遗交互体验';
      renderInteractiveHub(_ixHubData);
      return;
    }
    if (subState.kind === 'interactive' && subState.screen === 'review') {
      subState = { kind: 'interactive', screen: 'hub' };
      document.getElementById('subpage-title').textContent = '非遗交互体验';
      renderInteractiveHub(_ixHubData);
      return;
    }
    if (subState.kind === 'interactive' && subState.screen === 'hub') {
      subpageHide();
      return;
    }
    if (subState.kind === 'story' && subState.screen === 'detail') {
      subpageHide();
      return;
    }
    if (subState.kind === 'musicCard') {
      subpageHide();
      return;
    }
    if (subState.kind === 'community' && subState.screen === 'detail') {
      subpageHide();
      return;
    }
    if (subState.kind === 'musicTag') {
      subpageHide();
      return;
    }
    if (subState.kind === 'profile') {
      subpageHide();
      return;
    }
    subpageHide();
  });
  function ensureStoriesLoaded() {
    if (_loadedStories) return;
    _loadedStories = true;
    var active = document.querySelector('.inner-tab.active');
    var tab = active ? active.getAttribute('data-st') : 'recommend';
    loadStories(tab || 'recommend');
  }
  function ensureCommunityLoaded() {
    if (_loadedCommunity) return;
    _loadedCommunity = true;
    fetch('/community/posts').then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(renderCommunity).catch(function(err) {
      document.getElementById('community-loading').style.display = 'none';
      var e = document.getElementById('community-error');
      e.style.display = 'block';
      e.textContent = '社区数据加载失败：' + (err && err.message ? err.message : '');
    });
  }
  function ensureMallLoaded() {
    if (_loadedMall) return;
    _loadedMall = true;
    loadMall();
  }
  document.querySelectorAll('.entry-chip').forEach(function(c) {
    c.addEventListener('click', function() {
      var k = c.getAttribute('data-entry');
      if (k === 'archive') openArchiveFlow();
      else if (k === 'course') openCoursesFlow();
      else if (k === 'interactive') openInteractiveFlow();
    });
  });
  var profileBanner = document.querySelector('.profile-banner');
  if (profileBanner) {
    profileBanner.addEventListener('click', function() { openProfileScreen('edit'); });
    profileBanner.style.cursor = 'pointer';
  }
  try {
    var prof = readJsonLs('heritageWebProfile', {});
    if (prof.nick) { var n0 = document.querySelector('.profile-banner .nick'); if (n0) n0.textContent = prof.nick; }
    if (prof.bio) { var b0 = document.querySelector('.profile-banner .bio'); if (b0) b0.textContent = prof.bio; }
  } catch (ePr) {}
  document.querySelectorAll('.profile-row').forEach(function(b) {
    b.addEventListener('click', function() {
      var k = b.getAttribute('data-pf');
      if (k) openProfileScreen(k);
    });
  });
  var _storyKwTimer = null;
  var storyKw = '';
  var _lastStoryItems = [];
  function renderStoryMasonry(items) {
    _lastStoryItems = items || [];
    var qv = String(storyKw || '').trim().toLowerCase();
    var filtered = _lastStoryItems;
    if (qv) {
      filtered = _lastStoryItems.filter(function(it) {
        var blob = (it.id || '') + ' ' + (it.overlay || '');
        return blob.toLowerCase().indexOf(qv) >= 0;
      });
    }
    var nh = document.getElementById('story-no-hit');
    var grid = document.getElementById('story-grid');
    if (nh) nh.style.display = (qv && !filtered.length) ? 'block' : 'none';
    grid.innerHTML = '';
    filtered.forEach(function(it) {
      var card = document.createElement('div');
      card.className = 'story-card';
      var btn = document.createElement('button');
      btn.type = 'button';
      var h = it.minHeight ? (parseInt(String(it.minHeight), 10) || 160) : 160;
      var inner = '<img src="' + escapeHtml(it.imageUrl) + '" alt="" style="min-height:' + h + 'px" loading="lazy" decoding="async"/>';
      if (it.overlay) inner += '<div class="ov">' + escapeHtml(it.overlay) + '</div>';
      btn.innerHTML = inner;
      btn.addEventListener('click', function() { openStoryDetail(it); });
      card.appendChild(btn);
      grid.appendChild(card);
    });
  }
  function loadStories(tab) {
    var tabKey = tab || 'recommend';
    document.getElementById('stories-loading').style.display = 'block';
    document.getElementById('story-grid').style.display = 'none';
    document.getElementById('stories-error').style.display = 'none';
    fetch('/stories/feed?tab=' + encodeURIComponent(tabKey)).then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(function(data) {
      document.getElementById('stories-loading').style.display = 'none';
      var grid = document.getElementById('story-grid');
      grid.style.display = 'block';
      var skr = document.getElementById('story-kw-row');
      if (skr) skr.classList.add('visible');
      var skw = document.getElementById('story-kw');
      if (skw && !skw._bound) {
        skw._bound = true;
        skw.addEventListener('input', function() {
          clearTimeout(_storyKwTimer);
          _storyKwTimer = setTimeout(function() {
            storyKw = String(skw.value || '').trim().toLowerCase();
            renderStoryMasonry(_lastStoryItems);
          }, 200);
        });
      }
      renderStoryMasonry(data.items || []);
    }).catch(function(err) {
      document.getElementById('stories-loading').style.display = 'none';
      var e = document.getElementById('stories-error');
      e.style.display = 'block';
      e.textContent = '故事加载失败：' + (err && err.message ? err.message : '');
    });
  }
  document.querySelectorAll('.inner-tab').forEach(function(b) {
    b.addEventListener('click', function() {
      document.querySelectorAll('.inner-tab').forEach(function(x) {
        x.classList.remove('active');
        x.setAttribute('aria-selected', 'false');
      });
      b.classList.add('active');
      b.setAttribute('aria-selected', 'true');
      loadStories(b.getAttribute('data-st'));
    });
  });
  var _mallKwTimer = null;
  function renderMallProductStrip(keyword) {
    if (!_mallCatalog) return;
    var sc = document.getElementById('mall-products');
    if (!sc) return;
    var kw = (keyword || '').trim().toLowerCase();
    sc.innerHTML = '';
    var list = (_mallCatalog.products || []).slice();
    if (kw) {
      list = list.filter(function(p) { return String(p.title || '').toLowerCase().indexOf(kw) >= 0; });
    }
    if (!list.length) {
      sc.innerHTML = '<p class="loading" style="flex:0 0 100%;padding:12px 8px">无匹配商品（演示）</p>';
      return;
    }
    list.forEach(function(p) {
      var card = document.createElement('div');
      card.className = 'product-card';
      var btn = document.createElement('button');
      btn.type = 'button';
      var rv = (p.rating != null && p.rating !== '') ? p.rating : '—';
      var rc = p.reviewCount != null ? p.reviewCount : 0;
      btn.innerHTML = '<img src="' + escapeHtml(p.imageUrl) + '" alt="" loading="lazy" decoding="async"/>' +
        '<div class="pd"><div class="pt">' + escapeHtml(p.title) + '</div>' +
        '<div class="pp">¥' + escapeHtml(String(p.priceYuan)) + '</div>' +
        '<div class="pr">' + escapeHtml(String(rv)) + ' 分 · ' + escapeHtml(String(rc)) + ' 条评价</div></div>';
      btn.addEventListener('click', function() {
        openProductDetail(p, false);
      });
      card.appendChild(btn);
      sc.appendChild(card);
    });
  }
  function loadMall() {
    fetch('/mall/catalog').then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(function(d) {
      _mallCatalog = d;
      document.getElementById('mall-loading').style.display = 'none';
      document.getElementById('mall-error').style.display = 'none';
      document.getElementById('mall-products').style.display = 'flex';
      var kwRow = document.getElementById('mall-kw-row');
      if (kwRow) kwRow.style.display = 'block';
      var zones = document.getElementById('mall-zones');
      zones.innerHTML = '';
      var wrap = document.createElement('div');
      wrap.className = 'mall-zone';
      (d.sections || []).forEach(function(z) {
        var row = document.createElement('div');
        row.className = 'mall-zone-row';
        var left = document.createElement('div');
        left.innerHTML = '<h3>' + escapeHtml(z.title) + '</h3><p>' + escapeHtml(z.description || '') + '</p>';
        var go = document.createElement('button');
        go.type = 'button';
        go.className = 'go';
        go.textContent = '进入分区';
        go.addEventListener('click', function() {
          openMallSectionFlow(z.id, z.title, z.description);
        });
        row.appendChild(left);
        row.appendChild(go);
        wrap.appendChild(row);
      });
      zones.appendChild(wrap);
      renderMallProductStrip('');
      var inp = document.getElementById('mall-kw-filter');
      if (inp && !inp._bound) {
        inp._bound = true;
        inp.addEventListener('input', function() {
          clearTimeout(_mallKwTimer);
          var v = inp.value;
          _mallKwTimer = setTimeout(function() { renderMallProductStrip(v); }, 200);
        });
      }
    }).catch(function(err) {
      document.getElementById('mall-loading').style.display = 'none';
      var e = document.getElementById('mall-error');
      e.style.display = 'block';
      e.textContent = '商城数据加载失败：' + (err && err.message ? err.message : '');
    });
  }
  function renderMusic(d) {
    _musicCache = d;
    document.getElementById('music-loading').style.display = 'none';
    document.getElementById('music-content').style.display = 'block';
    var searchEl = document.getElementById('music-search');
    var q = searchEl ? String(searchEl.value || '').trim().toLowerCase() : '';
    function filt(list, getText) {
      if (!q) return list || [];
      return (list || []).filter(function(x) { return String(getText(x) || '').toLowerCase().indexOf(q) >= 0; });
    }
    var banners = filt(d.banners, function(b) { return b.title; });
    var hots = filt(d.hotTiles, function(h) { return h.title; });
    var picks = filt(d.dailyPicks, function(p) { return p.title; });
    var tags = filt(d.guessTags, function(t) { return t.label; });
    var bottoms = filt(d.bottomCards, function(c) { return c.title; });
    var hasAny = banners.length || hots.length || picks.length || tags.length || bottoms.length;
    var nhit = document.getElementById('music-no-hit');
    if (nhit) nhit.style.display = (q && !hasAny) ? 'block' : 'none';
    var mctx = document.getElementById('music-content');
    if (mctx && !mctx._musicDelegated) {
      mctx._musicDelegated = true;
      mctx.addEventListener('click', function(ev) {
        var mor = ev.target.closest && ev.target.closest('.section-title .more');
        if (mor) {
          ev.preventDefault();
          toast('查看更多（内容筹备中）');
        }
      });
    }
    if (searchEl && !searchEl._bound) {
      searchEl._bound = true;
      searchEl.addEventListener('input', function() {
        clearTimeout(_musicSearchTimer);
        _musicSearchTimer = setTimeout(function() {
          if (_musicCache) renderMusic(_musicCache);
        }, 220);
      });
    }
    var bn = document.getElementById('banners');
    bn.innerHTML = '';
    banners.forEach(function(b) {
      var wrap = document.createElement('div');
      wrap.className = 'banner-card';
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.style.cssText = 'padding:0;border:0;background:none;width:100%;cursor:pointer;color:inherit;text-align:left;';
      btn.innerHTML = '<img src="' + escapeHtml(b.imageUrl) + '" alt="" loading="lazy" decoding="async"/>' +
        '<div class="cap">' + escapeHtml(b.title) + '</div>';
      btn.addEventListener('click', function() {
        if (b.audioTrackId) {
          playTrack(b.audioTrackId, b.title);
        } else {
          openMusicHighlightDetail(b.title, b.id || 'banner');
        }
      });
      wrap.appendChild(btn);
      bn.appendChild(wrap);
    });
    var ht = document.getElementById('hots');
    ht.innerHTML = '';
    hots.forEach(function(h) {
      var col = document.createElement('div');
      col.className = 'tile';
      var inner = document.createElement('div');
      inner.className = 'tile-inner';
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.innerHTML = '<img src="' + escapeHtml(h.imageUrl) + '" alt=""/>' +
        '<span>' + escapeHtml(h.title) + '</span>';
      btn.addEventListener('click', function() {
        if (h.audioTrackId) playTrack(h.audioTrackId, h.title);
        else openMusicHighlightDetail(h.title, h.id || 'hot');
      });
      inner.appendChild(btn);
      col.appendChild(inner);
      ht.appendChild(col);
    });
    var pk = document.getElementById('picks');
    pk.innerHTML = '';
    picks.forEach(function(p) {
      var card = document.createElement('div');
      card.className = 'pick-card';
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.innerHTML = '<img src="' + escapeHtml(p.imageUrl) + '" alt=""/>' +
        '<div class="t">' + escapeHtml(p.title) + '</div>';
      btn.addEventListener('click', function() {
        if (p.audioTrackId) playTrack(p.audioTrackId, p.title);
        else openMusicHighlightDetail(p.title, p.id || 'pick');
      });
      card.appendChild(btn);
      pk.appendChild(card);
    });
    var tg = document.getElementById('tags');
    tg.innerHTML = '';
    tags.forEach(function(t, i) {
      var el = document.createElement('button');
      el.type = 'button';
      el.className = 'tag' + (i === 0 ? ' selected' : '');
      el.textContent = t.label || '';
      el.addEventListener('click', function() {
        tg.querySelectorAll('.tag').forEach(function(x) { x.classList.remove('selected'); });
        el.classList.add('selected');
        openMusicTagDetail(t.label || '');
      });
      tg.appendChild(el);
    });
    var bt = document.getElementById('bottoms');
    bt.innerHTML = '';
    bottoms.forEach(function(c) {
      var row = document.createElement('div');
      row.className = 'bottom-highlight';
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.innerHTML = '<img src="' + escapeHtml(c.imageUrl) + '" alt=""/>' +
        '<div class="meta"><div class="t1">' + escapeHtml(c.title) + '</div>' +
        '<div class="t2">点击查看详情（演示）</div></div>';
      btn.addEventListener('click', function() {
        openMusicHighlightDetail(c.title, c.id);
      });
      row.appendChild(btn);
      bt.appendChild(row);
    });
    function musicSecVis(wrapId, count) {
      var w = document.getElementById(wrapId);
      if (!w) return;
      w.style.display = (count > 0 || !q) ? '' : 'none';
    }
    musicSecVis('music-wrap-banners', banners.length);
    musicSecVis('music-wrap-hot', hots.length);
    musicSecVis('music-wrap-picks', picks.length);
    musicSecVis('music-wrap-tags', tags.length);
    musicSecVis('music-wrap-bottoms', bottoms.length);
  }
  var CAT = { FolkInstrument: '民族乐器', Electronic: '电子音乐', Ai: 'AI 创作' };
  var communityAllPosts = [];
  var communityFilter = 'all';
  function applyCommunityFilter() {
    var list = document.getElementById('posts');
    list.innerHTML = '';
    var arr = communityAllPosts;
    if (communityFilter !== 'all') {
      arr = arr.filter(function(p) { return p.category === communityFilter; });
    }
    if (communityKw) {
      arr = arr.filter(function(p) {
        var blob = (p.title || '') + ' ' + (p.subtitle || '') + ' ' + (p.body || '');
        return blob.toLowerCase().indexOf(communityKw) >= 0;
      });
    }
    if (arr.length === 0) {
      var emptyMsg = communityKw ? '无匹配帖子（演示）' : '该分类下暂无帖子（演示）';
      list.innerHTML = '<p class="loading" style="width:100%">' + emptyMsg + '</p>';
      return;
    }
    arr.forEach(function(p) {
      var div = document.createElement('button');
      div.type = 'button';
      div.className = 'post-card';
      var cat = p.category ? (CAT[p.category] || p.category) : '';
      div.innerHTML = '<div class="cat">' + escapeHtml(cat) + '</div>' +
        '<h3>' + escapeHtml(p.title) + '</h3>' +
        '<div class="sub">' + escapeHtml(p.subtitle || '') + '</div>' +
        '<p>' + escapeHtml(p.body || '') + '</p>' +
        '<div class="hint-tap">点击查看全文 · 演示</div>';
      div.addEventListener('click', function() { openCommunityPostDetail(p); });
      list.appendChild(div);
    });
  }
  function renderCommunity(d) {
    document.getElementById('community-loading').style.display = 'none';
    communityAllPosts = d.posts || [];
    var ckr = document.getElementById('comm-kw-row');
    if (ckr) ckr.classList.add('visible');
    var ckw = document.getElementById('community-kw');
    if (ckw && !ckw._bound) {
      ckw._bound = true;
      ckw.addEventListener('input', function() {
        clearTimeout(_commKwTimer);
        _commKwTimer = setTimeout(function() {
          communityKw = String(ckw.value || '').trim().toLowerCase();
          applyCommunityFilter();
        }, 200);
      });
    }
    var filt = document.getElementById('community-filters');
    filt.style.display = 'flex';
    filt.innerHTML = '';
    var chips = [
      { id: 'all', label: '全部' },
      { id: 'FolkInstrument', label: '民族乐器' },
      { id: 'Electronic', label: '电子音乐' },
      { id: 'Ai', label: 'AI 创作' }
    ];
    chips.forEach(function(c) {
      var b = document.createElement('button');
      b.type = 'button';
      b.textContent = c.label;
      b.setAttribute('data-cat', c.id);
      b.className = c.id === 'all' ? 'on' : '';
      b.addEventListener('click', function() {
        communityFilter = c.id;
        filt.querySelectorAll('button').forEach(function(x) {
          x.classList.toggle('on', x.getAttribute('data-cat') === c.id);
        });
        applyCommunityFilter();
      });
      filt.appendChild(b);
    });
    var list = document.getElementById('posts');
    list.style.display = 'flex';
    communityFilter = 'all';
    applyCommunityFilter();
  }
  var bootPage = document.body.getAttribute('data-page') || 'music';
  if (bootPage === 'music') {
    fetch('/music/home').then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(renderMusic).catch(function(err) {
      document.getElementById('music-loading').style.display = 'none';
      var e = document.getElementById('music-error');
      e.style.display = 'block';
      e.textContent = '音乐馆数据加载失败：' + (err && err.message ? err.message : '');
    });
  } else if (bootPage === 'stories') {
    _loadedStories = true;
    loadStories('recommend');
  } else if (bootPage === 'community') {
    _loadedCommunity = true;
    fetch('/community/posts').then(function(r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    }).then(renderCommunity).catch(function(err) {
      document.getElementById('community-loading').style.display = 'none';
      var e = document.getElementById('community-error');
      e.style.display = 'block';
      e.textContent = '社区数据加载失败：' + (err && err.message ? err.message : '');
    });
  } else if (bootPage === 'mall') {
    _loadedMall = true;
    loadMall();
  }
})();