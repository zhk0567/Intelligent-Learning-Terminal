Component({
  options: {
    multipleSlots: true,
    addGlobalClass: true,
  },
  properties: {
    title: { type: String, value: "" },
    eyebrow: { type: String, value: "" },
    framed: { type: Boolean, value: true },
    hasAction: { type: Boolean, value: false },
    extraClass: { type: String, value: "" },
  },
});
