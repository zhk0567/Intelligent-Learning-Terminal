module.exports = {
  title: '非遗交互体验',
  subtitle: '包含智能编曲与 AI 导师点评两条演示链路，支持从输入到结果的完整闭环。',
  modules: [
    {
      id: 'compose',
      title: '智能编曲（MVP）',
      description: '输入风格、情绪、节奏参数，生成演示片段并提供试听/收藏按钮。',
      actionLabel: '进入编曲',
      hint: '当前为模板生成版本，后续可替换为真实模型服务。',
    },
    {
      id: 'review',
      title: 'AI 导师点评（MVP）',
      description: '输入练习音频与关注维度，输出多维评分与改进建议。',
      actionLabel: '进入点评',
      hint: '当前为规则引擎点评版本，结果用于演示流程。',
    },
  ],
};
