export interface Address {
  id: string;
  name: string;
  phone: string;
  detail: string;
  isDefault: boolean;
}

export const ADDRESSES: Address[] = [
  {
    id: "a1",
    name: "李子衿",
    phone: "138 8888 8888",
    detail: "上海市 黄浦区 南京东路 100 号 国风大厦 18 层",
    isDefault: true,
  },
  {
    id: "a2",
    name: "周明远",
    phone: "139 6666 6666",
    detail: "北京市 朝阳区 建国门外大街 2 号 银泰中心 22 层",
    isDefault: false,
  },
  {
    id: "a3",
    name: "苏小落",
    phone: "187 9999 1234",
    detail: "杭州市 西湖区 文二西路 525 号 西溪壹号 9 栋 1102",
    isDefault: false,
  },
];
